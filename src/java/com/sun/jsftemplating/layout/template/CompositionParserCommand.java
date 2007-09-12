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
import com.sun.jsftemplating.layout.descriptors.LayoutComposition;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.util.LayoutElementUtil;

import java.io.IOException;


/**
 *  <p> This {@link CustomParserCommand} handles "if" statements.  To obtain
 *	the condition, it simply reads until it finds '&gt;'.  This means
 *	'&gt;' must be escaped if it appears in the condition.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class CompositionParserCommand implements CustomParserCommand {

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
	String tpl = (String) parser.getNVP(TEMPLATE_ATTRIBUTE, false).getValue();

	// We have the template...

	// First remove the current children on the parent (trimming == true)
	LayoutElement parent = env.getParent().getLayoutDefinition();
	parent.getChildLayoutElements().clear();

	// Create new LayoutComposition
// FIXME: Consider supporting an id for the LayoutComposition
// FIXME: reuse this code for trimming 'false' case as well...
	LayoutComposition compElt = new LayoutComposition(parent, null, true);
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
	    // End processing... (trimming == true)
	    throw new ProcessingCompleteException((LayoutDefinition) parent);
	}
    }


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
     *	<p> The {@link ProcessingContext} to be used when processing children
     *	    of a {@link LayoutComposition}.  This {@link ProcessingContext} may
     *	    have special meaning for {@link LayoutDefine}s and other tags.</p>
     */
    public static final ProcessingContext LAYOUT_COMPOSITION_CONTEXT	=
	new LayoutCompositionContext();
}
