/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.layout.ProcessingCompleteException;
import com.sun.jsftemplating.layout.SyntaxException;
import com.sun.jsftemplating.layout.descriptors.LayoutDefine;
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
public class DefineParserCommand implements CustomParserCommand {

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

	// Get the parent and define name
	LayoutElement parent = env.getParent();
	String id = (String) parser.getNVP(NAME_ATTRIBUTE, true).getValue();

	// Create new LayoutDefine
	LayoutDefine compElt = new LayoutDefine(parent, id);
	parent.addChildLayoutElement(compElt);

	// Skip any white space or extra junk...
	String theRest = parser.readUntil('>', true).trim();
	if (theRest.endsWith("/")) {
	    reader.popTag();  // Don't look for end tag
	} else {
	    // Process child LayoutElements (recurse)
	    reader.process(
		LAYOUT_DEFINE_CONTEXT, compElt,
		LayoutElementUtil.isLayoutComponentChild(compElt));
	}
    }


    /**
     *	<p> This is the {@link ProcessingContext} for
     *	    {@link LayoutDefine}s.</p>
     */
    protected static class LayoutDefineContext extends BaseProcessingContext {
    }

    /**
     *	<p> A String containing "template".  This is the attribute name of the
     *	    template file to use in the {@link LayoutDefine}.</p>
     */
    public static final String NAME_ATTRIBUTE	= "name";

    /**
     *	<p> The {@link ProcessingContext} to be used when processing children
     *	    of a {@link LayoutDefine}.  This {@link ProcessingContext} may
     *	    have special meaning for {@link LayoutDefine}s and other tags.</p>
     */
    public static final ProcessingContext LAYOUT_DEFINE_CONTEXT	=
	new LayoutDefineContext();
}
