/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006-2018 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;

import com.sun.jsftemplating.layout.SyntaxException;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutStaticText;
import com.sun.jsftemplating.util.LayoutElementUtil;
import com.sun.jsftemplating.util.Util;


/**
 *  <p> Since many contexts share common functionality (i.e. processing static
 *	text elements), it makes sense to have a base {@link ProcessingContext}
 *	which may be specialized as needed.</p>
 */
public class BaseProcessingContext implements ProcessingContext {

    /**
     *  <p> This is called when a component tag is found
     *	    (&lt;tagname ...).</p>
     */
    public void beginComponent(ProcessingContextEnvironment env, String content) throws IOException {
	// We have a UIComponent tag... first get the parser
	// Skip white Space
	TemplateReader reader = env.getReader();
	TemplateParser parser = reader.getTemplateParser();
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);

// tagStack.push(content);
	// Create the LayoutComponent
	LayoutElement parent = env.getParent();
	LayoutComponent child = reader.createLayoutComponent(
		parent, env.isNested(), content);
	parent.addChildLayoutElement(child);

	// See if this is a single tag or if there is a closing tag
	boolean single = false;
	int ch = parser.nextChar();
	if (ch == '/') {
	    // Single Tag
	    ch = parser.nextChar();  // Throw away '>'
	    single = true;
	    reader.popTag();	    // Don't look for ending tag
	}
	if (ch != '>') {
	    throw new SyntaxException(
		"Expected '>' found '" + (char) ch + "'.");
	}

	if (single) {
	    // This is also the end of the component in this case...
	    endComponent(env, content);
	} else {
	    // Process child LayoutElements (recurse)
	    reader.process(
		TemplateReader.LAYOUT_COMPONENT_CONTEXT, child, true);
	}
    }

    /**
     *  <p> This is called when an end component tag is found (&lt;/tagname&gt;
     *	    or &lt;tagname ... /&gt;).  Because it may be called for either of
     *	    the above syntaxes, the caller of this method is responsible for
     *	    maintaining the parser position, this method (or its subclasses)
     *	    should not effect the parser position.</p>
     */
    public void endComponent(ProcessingContextEnvironment env, String content) throws IOException {
    }

    /**
     *  <p> This is called when a special tag is found (&lt;!tagname ...).</p>
     */
    public void beginSpecial(ProcessingContextEnvironment env, String content) throws IOException {
	CustomParserCommand command =
	    env.getReader().getCustomParserCommand(content);
	if (command == null) {
	    // If there is no Custom command for this, use the default...
	    command = TemplateReader.EVENT_PARSER_COMMAND;
	}
	command.process(this, env, content);
    }

    /**
     *  <p> This is called when a special end tag is found (&lt;/tagname ... or
     *	    &lt;!tagname ... /&gt;).</p>
     */
    public void endSpecial(ProcessingContextEnvironment env, String content) throws IOException {
    }

    /**
     *  <p> This is called when static text is found (").</p>
     */
    public void staticText(ProcessingContextEnvironment env, String content) throws IOException {
	LayoutElement parent = env.getParent();

	// Create a LayoutStaticText
	LayoutComponent child = new LayoutStaticText(
	    parent,
	    LayoutElementUtil.getGeneratedId(
		"txt", env.getReader().getNextIdNumber()),
	    content);
	child.addOption("value", content);
	child.setNested(env.isNested());

	parent.addChildLayoutElement(child);
    }

    /**
     *  <p> This is called when escaped static text is found (').  The
     *	    difference between this and staticText is that HTML is expected to
     *	    be escaped so the browser does not parse it.</p>
     */
    public void escapedStaticText(ProcessingContextEnvironment env, String content) throws IOException {
	staticText(env, Util.htmlEscape(content));
    }

    /**
     *  <p> This method is invoked when nothing else matches.</p>
     *
     *  <p> This implementation reads a character and does nothing with it.</p>
     */
    public void handleDefault(ProcessingContextEnvironment env, String content) throws IOException {
	env.getReader().getTemplateParser().nextChar();
    }

    /**
     *	<p>  This is a static reference to the "staticText"
     *	    {@link ComponentType}.</p>
    public static final ComponentType STATIC_TEXT = 
	LayoutDefinitionManager.getGlobalComponentType(null, "staticText");
     */
}
