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

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.SyntaxException;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;
import com.sun.jsftemplating.layout.descriptors.handler.OutputTypeManager;
import com.sun.jsftemplating.util.LayoutElementUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 *  <p> This {@link CustomParserCommand} implementation processes handlers for
 *	an event.</p>
 */
public class EventParserCommand implements CustomParserCommand {
    /**
     *	<p> This method processes a "custom" command.  These are commands that
     *	    start with a !.  When this method receives control, the
     *	    <code>name</code> (i.e. the token after the '!' character) has
     *	    already been read.  It is passed via the <code>name</code>
     *	    parameter.</p>
     *
     *	<p> This implementation processes events and their handlers.  2
     *	    syntaxes are supported:</p>
     *
     *	<ul><li>&lt;event type="beforeCreate"&gt;handler1(input="foo" output="bar"); ... &lt;/event&gt;<li>
     *	    <li>&lt;!beforeCreate handler1(input="foo" output="bar"); ... /&gt;</li></ul>
     *
     *	<p> The first format should be preferred.</p>
     *
     *	<p> The {@link ProcessingContext} and
     *	    {@link ProcessingContextEnvironment} are both available.</p>
     */
    public void process(ProcessingContext ctx, ProcessingContextEnvironment env, String eventName) throws IOException {
	Handler handler = null;
	List<Handler> handlers = new ArrayList<Handler>();
	TemplateReader reader = env.getReader();
	TemplateParser parser = reader.getTemplateParser();
	Handler parentHandler = null;
	Stack<Handler> handlerStack = new Stack<Handler>();
	LayoutElement parent = env.getParent();

	// Skip whitespace...
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	int ch = -1;

	// We now support 2 syntaxes:
	//   <!event type="beforeCreate">[handlers]</event>
	//   <!beforeCreate [handlers] />
	// If "eventName" is event, look for type and the closing '>' before
	// trying to parse the handlers.
	boolean useBodyContent = false;
	boolean createHandlerDefinitionOnLayoutDefinition = false;
	if (eventName.equals("handler")) {
	    // We have a <handler id="foo"> tag...
	    createHandlerDefinitionOnLayoutDefinition = true;
	    useBodyContent = true;

	    // Read type="...", no other options are supported at this time
	    NameValuePair nvp = parser.getNVP(null);
	    if (!nvp.getName().equals("id")) {
		throw new SyntaxException(
		    "When defining and event, you must supply the event type! "
		    + "Found \"...event " + nvp.getName() + "\" instead.");
	    }
	    eventName = nvp.getValue().toString();

	    // Ensure the next character is '>'
	    parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	    ch = parser.nextChar();
	    if (ch != '>') {
		throw new SyntaxException(
		    "Syntax error in event definition, found: '...handler id=\""
		    + eventName + "\" " + ((char) ch)
		    + "\'.  Expected closing '>' for opening handler element.");
	    }

	    // Get ready to read the handlers now...
	    parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	    ch = parser.nextChar();
	} else if (eventName.equals("event")) {
	    // We have the new syntax...
	    useBodyContent = true;

	    // Read type="...", no other options are supported at this time
	    NameValuePair nvp = parser.getNVP(null);
	    if (!nvp.getName().equals("type")) {
		throw new SyntaxException(
		    "When defining and event, you must supply the event type! "
		    + "Found \"...event " + nvp.getName() + "\" instead.");
	    }
	    eventName = nvp.getValue().toString();

	    // Ensure the next character is '>'
	    parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	    ch = parser.nextChar();
	    if (ch != '>') {
		throw new SyntaxException(
		    "Syntax error in event definition, found: '...event type=\""
		    + eventName + "\" " + ((char) ch)
		    + "\'.  Expected closing '>' for opening event element.");
	    }

	    // Get ready to read the handlers now...
	    parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	    ch = parser.nextChar();
	} else {
	    // Make sure to read the first char for the old syntax...
	    ch = parser.nextChar();
	}

	// Read the Handler(s)...
	while (ch != -1) {
	    if (useBodyContent) {
		// If we're using the new format.... check for "</event>"
		if (ch == '<') {
		    // Just unread the '<', framework will validate the rest
		    parser.unread('<');
		    break;
		}
	    } else {
		if ((ch == '/') || (ch == '>')) {
		    // We found the end in the case where the handlers are
		    // inside the tag (old syntax).
		    break;
		}
	    }
	    // Check for {}'s
	    if ((ch == LEFT_CURLY) || (ch == RIGHT_CURLY)) {
		if (ch == LEFT_CURLY) {
		    // We are defining child handlers
		    handlerStack.push(parentHandler);
		    parentHandler = handler;
		} else {
		    // We are DONE defining child handlers
		    if (handlerStack.empty()) {
			throw new SyntaxException("Encountered unmatched '"
			    + RIGHT_CURLY + "' when parsing handlers for '"
			    + eventName + "' event.");
		    }
		    parentHandler = handlerStack.pop();
		}

		// ';' or ',' characters may appear between handlers
		parser.skipCommentsAndWhiteSpace(
			TemplateParser.SIMPLE_WHITE_SPACE + ",;");

		// We need to "continue" b/c we need to check next ch again
		ch = parser.nextChar();
		continue;
	    }

	    // Get Handler ID / Definition
	    parser.unread(ch);

	    // Read a Handler
	    handler = readHandler(parser, eventName, parent);

	    // Add the handler to the appropriate place
	    if (parentHandler == null) {
		handlers.add(handler);
	    } else {
		parentHandler.addChildHandler(handler);
	    }

	    // Look at the next character...
	    ch = parser.nextChar();
	}
	if (ch == -1) {
	    // Make sure we didn't get to the end of the file
	    throw new SyntaxException("Unexpected EOF encountered while "
		+ "parsing handlers for event '" + eventName + "'!");
	}

	// Do some checks to make sure everything is good...
	if (!handlerStack.empty()) {
	    throw new SyntaxException("Unmatched '" + LEFT_CURLY
		    + "' when parsing handlers for '" + eventName
		    + "' event.");
	}
	if (!useBodyContent) {
	    // Additional checks for old syntax...
	    if (ch == '>') {
		throw new SyntaxException("Handlers for event '" + eventName
		    + "' did not end with '/&gt;' but instead ended with '&gt;'!");
	    }
	    if (ch == '/') {
		// Make sure we have a "/>"...
		parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
		ch = parser.nextChar();
		if (ch != '>') {
		    throw new SyntaxException("Expected '/&gt;' a end of '"
			+ eventName + "' event.  But found '/"
			+ (char) ch + "'.");
		}
		reader.popTag();   // Get rid of this event tag from the Stack
		ctx.endSpecial(env, eventName);
	    }
	} else {
	    // We need to recurse in order for the end-tag code to properly
	    // close out the context and make everything run correctly...
	    // Process child LayoutElements (should be none)
	    reader.process(EVENT_PROCESSING_CONTEXT, parent,
		LayoutElementUtil.isLayoutComponentChild(parent));
	}

	// Set the Handlers on the parent...
	if (createHandlerDefinitionOnLayoutDefinition) {
	    HandlerDefinition def = new HandlerDefinition(eventName);
	    def.setChildHandlers(handlers);
	    parent.getLayoutDefinition().setHandlerDefinition(eventName, def);
	} else {
	    parent.setHandlers(eventName, handlers);
	}
    }

    /**
     *	<p> This method parses and creates an individual
     *	    <code>Handler</code>.</p>
     */
    private Handler readHandler(TemplateParser parser, String eventName, LayoutElement parent) throws IOException {
	String target = null;
	String defVal = null;
	NameValuePair nvp = null;
	HandlerDefinition def = null;

	String handlerId = parser.readToken();
	// Check locally defined Handler
	def = parent.getLayoutDefinition().getHandlerDefinition(handlerId);
	if (def == null) {
	    // Check globally defined Handler
	    def = LayoutDefinitionManager.getGlobalHandlerDefinition(handlerId);
	    if (def == null) {
		throw new SyntaxException("Handler '" + handlerId
		    + "' in event '" + eventName + "' is not declared!  "
		    + "Ensure the '@Handler' annotation has been defined "
		    + "on the handler Java method, that it has been "
		    + "compiled with the annotation processing tool, and "
		    + "that the resulting"
		    + " 'META-INF/jsftemplating/Handler.map' is located "
		    + "in your classpath (you may need to do a clean "
		    + "build).");
	    }
	}

	// Create a Handler
	Handler handler = new Handler(def);

	// Get the default name
	Map inputs = def.getInputDefs();
// FIXME: Allow for HandlerDefs to declare their default input
	if (inputs.size() == 1) {
	    defVal = inputs.keySet().toArray()[0].toString();
	}

	// Get the outputs so we can see what outputs have been declared
	Map outputs = def.getOutputDefs();

	// Ensure we have an opening parenthesis
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	int ch = parser.nextChar();
	if (ch != '(') {
	    throw new SyntaxException("While processing '&lt;!" + eventName
		+ "...' the handler '" + handlerId
		+ "' was missing the '(' character!");
	}

	// Move to the first char inside the parenthesis
	parser.skipWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	ch = parser.nextChar();

	// We should not ignore '#' characters for 'if' (Issue #5)
	if ((ch != '#') || !handlerId.equals(IF_HANDLER)) {
	    parser.unread(ch);
	    parser.skipCommentsAndWhiteSpace(""); // Already skipped white
	    ch = parser.nextChar();
	}

	// Allow if() handlers to be more flexible...
	if (handlerId.equals(IF_HANDLER)
		&& (ch != '\'') && (ch != '"') && (ch != 'c')) {
// FIXME: check for "condition", otherwise expressions starting with 'c' will
// FIXME: not parse correctly
	    // We have an if() w/o a condition="" && w/o quotes...
	    // Take the entire value inside the ()'s to be the expression
	    parser.unread(ch);
	    handler.setCondition(parser.readUntil(')', false).trim());
	    ch = ')';
	}

	// Read NVP(s)...
	while ((ch != -1) && (ch != ')')) {
	    // Read NVP
	    parser.unread(ch);
	    try {
		nvp = parser.getNVP(defVal);
	    } catch (SyntaxException ex) {
		throw new SyntaxException("Unable to process handler '"
			+ handlerId + "'!", ex);
	    }
	    parser.skipCommentsAndWhiteSpace(
		TemplateParser.SIMPLE_WHITE_SPACE + ",;");
	    ch = parser.nextChar();

	    // Store the NVP..
	    target = nvp.getTarget();
	    if (target != null) {
		// "old-style" OutputMapping (key=>$attribute{value})
		// NOTE: 'value' must be a String for an OutputMapping
		handler.setOutputMapping(
		    nvp.getName(), nvp.getValue().toString(), target);
	    } else {
		// First check for special input value (condition)
		String name = nvp.getName();
		if (name.equals(CONDITION_ATTRIBUTE)
		    && ((inputs.get(CONDITION_ATTRIBUTE) == null)
			|| (handlerId.equals(IF_HANDLER)))) {
		    // We have a Handler condition, set it
		    handler.setCondition(nvp.getValue().toString());
		} else {
		    // We still don't know if this is an input, output, or both
		    // (EL is now supported as an output mapping: out="#{el}")
		    boolean validIO = false;
		    // We also check to see if the "old" output mapping was
		    // used and DO NOT override it if it was.  This is useful
		    // if there are cases where an input and output share a
		    // name and the user does not fix this... the old syntax
		    // can reliably declare an output without a namespace
		    // problem.
		    if (outputs.containsKey(name) && (handler.getOutputValue(name) == null)) {
			// We have an Output... use 2 arg method for this
			// syntax (expects EL, or uses simple String for a
			// request attribute).
			handler.setOutputMapping(
				name, nvp.getValue().toString(),
				OutputTypeManager.EL_TYPE);
			validIO = true;
		    }
		    // Don't do "else" b/c it may be BOTH an input AND output
		    if (inputs.containsKey(name)) {
			// We have an Input
			handler.setInputValue(name, nvp.getValue());
			validIO = true;
		    }
		    if (!validIO) {
			throw new IllegalArgumentException(
			    "Input or output named \"" + name
			    + "\" was declared for handler \""
			    + handlerId + "\" in event \"" + eventName
			    + "\", however, no such input or output exists!");
		    }
		}
	    }
	}

	// ';' or ',' characters may appear between handlers
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE + ",;");

	// Return the Handler
	return handler;
    }

    /**
     *	<p> This is the {@link ProcessingContext} for events.  Currently does
     *	    nothing.</p>
     */
    protected static class EventProcessingContext extends BaseProcessingContext {
    }

    public static final String IF_HANDLER		    =	"if";
    public static final String CONDITION_ATTRIBUTE	    =	"condition";
    public static final ProcessingContext EVENT_PROCESSING_CONTEXT  = 
	new EventProcessingContext();

    public static final char LEFT_CURLY			    =	'{';
    public static final char RIGHT_CURLY		    =	'}';
}
