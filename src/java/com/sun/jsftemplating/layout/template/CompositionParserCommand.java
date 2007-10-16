/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://jsftemplating.dev.java.net/cddl1.html or
 * jsftemplating/cddl1.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at jsftemplating/cddl1.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.layout.ProcessingCompleteException;
import com.sun.jsftemplating.layout.SyntaxException;
import com.sun.jsftemplating.layout.descriptors.LayoutComposition;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.util.LayoutElementUtil;

import java.io.IOException;


/**
 *  <p> This {@link CustomParserCommand} handles "composition" statements.
 *	TBD...
 *  </p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class CompositionParserCommand implements CustomParserCommand {

    /**
     *	<p> Constructor.  This constructor requires a flag to be passed in
     *	    indicating wether content outside this component should be ignored
     *	    (trimmed) or left alone.</p>
     *
     *	@param	trim	<code>true</code> if content outside this component
     *			should be thrown away.
     */
    public CompositionParserCommand(boolean trim) {
	this.trimming = trim;
    }

    /**
     *	<p> This method processes a "custom" command.  These are commands that
     *	    start with a !.  When this method receives control, the
     *	    <code>name</code> (i.e. the token after the '!' character) has
     *	    already been read.  It is passed via the <code>name</code>
     *	    parameter.</p>
     *
     *	<p> The {@link ProcessingContext} and
     *	    {@link ProcessingContextEnvironment} are both available.</p>
     */
    public void process(ProcessingContext ctx, ProcessingContextEnvironment env, String name) throws IOException {
	// Get the reader and parser
	TemplateReader reader = env.getReader();
	TemplateParser parser = reader.getTemplateParser();

	// Skip any white space...
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);

	// Read the attribute...

	// Get the parent and template filename
	LayoutElement parent = env.getParent();
	String tpl;
	NameValuePair nvp;
	if (trimming) {
	    // First remove the current children on the LD (trimming == true)
	    parent = parent.getLayoutDefinition();
	    parent.getChildLayoutElements().clear();

	    // Next get the template name
	    nvp = parser.getNVP(TEMPLATE_ATTRIBUTE, true);
	    if (!nvp.getName().equals(TEMPLATE_ATTRIBUTE)) {
		throw new SyntaxException("!composition must provide a "
			+ "'" + TEMPLATE_ATTRIBUTE + "' attribute!  Found '"
			+ nvp.getName() + "' with value '"
			+ nvp.getValue() + "' instead.");
	    }
	} else {
	    // Get the src filename
	    nvp = parser.getNVP(SRC_ATTRIBUTE, true);
	    if (!nvp.getName().equals(SRC_ATTRIBUTE)) {
		throw new SyntaxException("!include must provide a "
			+ "'" + SRC_ATTRIBUTE + "' attribute!  Found '"
			+ nvp.getName() + "' with value '"
			+ nvp.getValue() + "' instead.");
	    }
	}
	tpl = (String) nvp.getValue();
	if (tpl == null) {
	    // FIXME: Log a non-fatal CONFIG message
	}

	// Create new LayoutComposition
// FIXME: Consider supporting an id for the LayoutComposition
	LayoutComposition compElt = new LayoutComposition(parent, null, trimming);
	compElt.setTemplate(tpl);
	parent.addChildLayoutElement(compElt);

	// Skip any white space or extra junk...
	String theRest = parser.readUntil('>', true).trim();
	if (theRest.endsWith("/")) {
	    reader.popTag();  // Don't look for end tag
	} else {
	    // Process child LayoutElements (recurse)
	    reader.process(
		LAYOUT_COMPOSITION_CONTEXT, compElt,
		LayoutElementUtil.isLayoutComponentChild(compElt));

	    if (trimming) {
		// End processing... (trimming == true)
		throw new ProcessingCompleteException((LayoutDefinition) parent);
	    }
	}
    }


    /**
     *	<p> This indicates whether content outside of this tag should be left
     *	    alone or used.</p>
     */
    private boolean trimming = true;

    /**
     *	<p> This is the {@link ProcessingContext} for
     *	    {@link LayoutComposition}s.</p>
     */
    protected static class LayoutCompositionContext extends BaseProcessingContext {
// FIXME: This is where we can put logic to look for ui:defines and other similar tags that have special meaning within the context of a LayoutComposition
    }

    /**
     *	<p> A String containing "template".  This is the attribute name of the
     *	    template file to use in the {@link LayoutComposition}.</p>
     */
    public static final String TEMPLATE_ATTRIBUTE	= "template";

    /**
     *	<p> A String containing "template".  This is the attribute name of the
     *	    template file to use in the {@link LayoutComposition}.</p>
     */
    public static final String SRC_ATTRIBUTE	= "src";

    /**
     *	<p> The {@link ProcessingContext} to be used when processing children
     *	    of a {@link LayoutComposition}.  This {@link ProcessingContext} may
     *	    have special meaning for {@link LayoutDefine}s and other tags.</p>
     */
    public static final ProcessingContext LAYOUT_COMPOSITION_CONTEXT	=
	new LayoutCompositionContext();
}
