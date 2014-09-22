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
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *  <p> This {@link CustomParserCommand} handles "namespace" declarations. The
 *	format of this command should be:</p>
 *
 *  <ul><li>
 *	    &lt;!namespace "longname"="shortname" /&gt;
 *  </li></ul>
 *
 *  <p>	For example: </p>
 *
 *  <ul><li>
 *	    &lt;!namespace "http://java.sun.com/mojarra/scales"="sc" /&gt;
 *  </li></ul>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class NamespaceParserCommand implements CustomParserCommand {

    /**
     *	<p> Constructor.</p>
     */
    public NamespaceParserCommand() {
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
	TemplateParser parser = reader.getTemplateParser();
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	int ch = parser.nextChar();
	if ((ch == '>') || (ch == '/')) {
	    // Nothing specified, throw exception!
	    throw new IllegalArgumentException(
		"Found an empty \"<!namespace />\" delcaration!  The long and"
		+ " short namespace names must be provided.");
	}
	parser.unread(ch);

	// Next get the next NVP...
        NameValuePair nvp = parser.getNVP(null, true);

	// Make sure we read the WS after the data...
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);

	// Now make sure this is an end tag...
	ch = parser.nextChar();
	int ch2 = parser.nextChar();
	if ((ch != '/') || (ch2 != '>')) {
	    throw new IllegalArgumentException(
		"[<!namespace " + nvp.getName() + "=" + nvp.getValue()
		+ ((char) ch) + ((char) ch2) + "] does not end with \"/>\"!");
	}
	reader.popTag();  // Don't look for end tag

	// Save the mapping...
	reader.setNamespace(nvp.getName(), nvp.getValue().toString());
    }
}
