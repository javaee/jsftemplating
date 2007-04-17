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

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.SyntaxException;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;

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
    public void process(ProcessingContext ctx, ProcessingContextEnvironment env, String eventName) throws IOException {
	Handler handler = null;
	List<Handler> handlers = new ArrayList<Handler>();
	TemplateReader reader = env.getReader();
	TemplateParser parser = reader.getTemplateParser();
	Handler parentHandler = null;
	Stack<Handler> handlerStack = new Stack<Handler>();

	// Read the Handler(s)...
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	int ch = parser.nextChar();
	while ((ch != -1) && (ch != '/') && (ch != '>')) {
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
	    handler = readHandler(parser, eventName);

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

	// Set the Handlers on the parent...
	env.getParent().setHandlers(eventName, handlers);
    }

    /**
     *  <p>	This method parses and creates an individual
     *	<code>Handler</code>.</p>
     */
    private Handler readHandler(TemplateParser parser, String eventName) throws IOException {
	String target = null;
	String defVal = null;
	NameValuePair nvp = null;
	HandlerDefinition def = null;

	String handlerId = parser.readToken();
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

	// Create a Handler
	Handler handler = new Handler(def);

	// Get the default name
	Map inputs = def.getInputDefs();
// FIXME: Allow for HandlerDefs to declare their default input
	if (inputs.size() == 1) {
	    defVal = inputs.keySet().toArray()[0].toString();
	}

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
		// We have an OutputMapping
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
		    // We have an Input
		    handler.setInputValue(nvp.getName(), nvp.getValue());
		}
	    }
	}

	// ';' or ',' characters may appear between handlers
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE + ",;");

	// Return the Handler
	return handler;
    }

    public static final String IF_HANDLER		    =	"if";
    public static final String CONDITION_ATTRIBUTE	    =	"condition";

    public static final char LEFT_CURLY			    =	'{';
    public static final char RIGHT_CURLY		    =	'}';
}
