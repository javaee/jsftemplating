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
import java.util.List;


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
     *
     *	@param	templateAttName	The name of the attribute for the template name.
     */
    public CompositionParserCommand(boolean trim, String templateAttName) {
	this.trimming = trim;
	this.templateAttName = templateAttName;
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
	// Get the reader
	TemplateReader reader = env.getReader();

	LayoutElement parent = env.getParent();
	if (trimming) {
	    // First remove the current children on the LD (trimming == true)
	    parent = parent.getLayoutDefinition();
	    parent.getChildLayoutElements().clear();
	}

	// Next get the attributes
	List<NameValuePair> nvps =
	    reader.readNameValuePairs(name, templateAttName, true);

	// Look for required attribute
	// Find the template name
	String tpl = null;
	boolean required = true;
	for (NameValuePair nvp : nvps) {
	    if (nvp.getName().equals(templateAttName)) {
		tpl = (String) nvp.getValue();
	    } else if (nvp.getName().equals(REQUIRED_ATTRIBUTE)) {
		required = Boolean.parseBoolean(nvp.getValue().toString());
	    } else {
		throw new SyntaxException("'" + name
			+ "' tag contained unkown attribute '" + nvp.getName()
			+ "' with value '" + nvp.getValue() + "'.");
	    }
	}

	// Create new LayoutComposition
	LayoutComposition compElt = new LayoutComposition(
	    parent,
	    LayoutElementUtil.getGeneratedId(name, reader.getNextIdNumber()),
	    trimming);
	compElt.setTemplate(tpl);
	compElt.setRequired(required);
	parent.addChildLayoutElement(compElt);

	// See if this is a single tag or not...
	TemplateParser parser = reader.getTemplateParser();
	int ch = parser.nextChar();
	if (ch == '/') {
	    reader.popTag();  // Don't look for end tag
	} else {
	    // Unread the ch we just read
	    parser.unread(ch);

	    // Process child LayoutElements (recurse)
	    reader.process(
		LAYOUT_COMPOSITION_CONTEXT, compElt,
		LayoutElementUtil.isLayoutComponentChild(compElt));
	}

	if (trimming) {
	    // End processing... (trimming == true)
	    throw new ProcessingCompleteException((LayoutDefinition) parent);
	}
    }


    /**
     *	<p> This is the {@link ProcessingContext} for
     *	    {@link LayoutComposition}s.</p>
     */
    protected static class LayoutCompositionContext extends BaseProcessingContext {
	/**
	 *  <p> This is called when a special tag is found (&lt;!tagname ...).</p>
	 *
	 *  <p>	This implementation looks for "define" tags and handles them
	 *	specially.  These tags are only valid in this context.</p>
	 */
	public void beginSpecial(ProcessingContextEnvironment env, String content) throws IOException {
	    if (content.equals("define")) {
		DEFINE_PARSER_COMMAND.process(this, env, content);
	    } else {
		super.beginSpecial(env, content);
	    }
	}
    }


    /**
     *	<p> This indicates whether content outside of this tag should be left
     *	    alone or used.</p>
     */
    private boolean trimming = true;
    private String templateAttName = null;

    public static final String REQUIRED_ATTRIBUTE   =	"required";
    /**
     *	<p> The {@link ProcessingContext} to be used when processing children
     *	    of a {@link LayoutComposition}.  This {@link ProcessingContext} may
     *	    have special meaning for {@link LayoutDefine}s and other tags.</p>
     */
    public static final ProcessingContext LAYOUT_COMPOSITION_CONTEXT	=
	new LayoutCompositionContext();

    public static final CustomParserCommand DEFINE_PARSER_COMMAND =
	new DefineParserCommand();
}
