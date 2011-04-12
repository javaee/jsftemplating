/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2011 Ken Paulsen
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
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

package com.sun.jsft.facelets;

import com.sun.jsft.event.Command;
import com.sun.jsft.event.ELCommand;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 *  <p>	This class is responsible for reading in all the commands for the
 *	given String.  The String typically is passed in from the body
 *	content of event.</p>
 *
 *  @author  Ken Paulsen (kenapaulsen@gmail.com)
 */
public class CommandReader {

    /**
     *	<p> Constructor.</p>
     */
    public CommandReader(String str) {
	this(new ByteArrayInputStream(
		("{" + CommandReader.unwrap(str) + "}").getBytes()));
    }

    /**
     *	<p> Constructor.</p>
     *
     *	@param	stream	The <code>InputStream</code> for the {@link Command}.
     */
    protected CommandReader(InputStream stream) {
	_parser = new CommandParser(stream);
    }

    /**
     *	<p> The read method uses the {@link CommandParser} to parses the
     *	    template.  It populates a {@link LayoutDefinition} structure, which
     *	    is returned.</p>
     *
     *	@return	The {@link LayoutDefinition}
     *
     *	@throws	IOException
     */
    public List<Command> read() throws IOException {
	// Start...
	_parser.open();

	try {
	    // Populate the LayoutDefinition from the Document
	    return readCommandList();
	} finally {
	    _parser.close();
	}
    }

    /**
     *
     */
    private Command readCommand() throws IOException {
	// Skip White Space...
	_parser.skipCommentsAndWhiteSpace(CommandParser.SIMPLE_WHITE_SPACE);

	// Read the next Command
	String commandLine = _parser.readUntil(new int[] {';', '{', '}'}, true);

	// Read the children
	int ch = _parser.nextChar();
	List<Command> commandChildren = null;
	if (ch == '{') {
	    // Read the Command Children 
	    commandChildren = readCommandList();
	} else if (ch == '}') {
	    _parser.unread(ch);
	}

	// Check to see if there is a variable to store the result...
	String variable = null;
	int idx = indexOf((byte) '=', commandLine);
	if (idx != -1) {
	    // We have a result variable, store it separately...
	    variable = commandLine.substring(0, idx).trim();
	    commandLine = commandLine.substring(++idx).trim();
	}

	// If "if" handle "else" if present
	Command elseCommand = null;
	if (commandLine.startsWith("if")) {
	    // First convert "if" to the real if handler...
	    commandLine = "jsft._if" + commandLine.substring(2);

	    // Check the next few characters to see if they are "else"...
	    _parser.skipCommentsAndWhiteSpace(CommandParser.SIMPLE_WHITE_SPACE);
	    int next[] = new int[] {
		_parser.nextChar(),
		_parser.nextChar(),
		_parser.nextChar(),
		_parser.nextChar(),
		_parser.nextChar()
	    };
	    if ((next[0] == 'e')
		    && (next[1] == 'l')
		    && (next[2] == 's')
		    && (next[3] == 'e')
		    && (Character.isWhitespace((char) next[4]))) {
		// This is an else case, parse it...
		elseCommand = readCommand();
	    } else {
		// Not an else, restore the parser state
		for (idx=4; idx > -1; idx--) {
		    if (next[idx] != -1) {
			_parser.unread(next[idx]);
		    }
		}
	    }
	}

	// Create the Command
	Command command = null;
	if ((commandLine.length() > 0) || (commandChildren != null)) {
	    command = new ELCommand(
		    variable,
		    convertKeywords(commandLine),
		    commandChildren,
		    elseCommand);
	}

	// Return the LayoutElement
	return command;
    }

    /**
     *	<p> This method replaces keywords with the "real" syntax for
     *	    developer convenience.</p>
     */
    private String convertKeywords(String exp) {
	if (exp != null) {
	    // "if" keyword is processed different to also process "else"
	    if (exp.startsWith("foreach")) {
		exp = "jsft.foreach" + exp.substring(7);
	    } else if (exp.startsWith("for")) {
		exp = "jsft._for" + exp.substring(3);
	    }
	}
	return exp;
    }

    /**
     *	<p> This method looks for the given <code>char</code> in the given
     *	    <code>String</code>.  It will not match any values that are
     *	    found within parenthesis or quotes.</p>
     */
    private int indexOf(byte ch, String str) {
	byte[] bytes = str.getBytes();
	
	int idx = 0;
	int insideChar = -1;
	for (byte curr : bytes) {
	    if (insideChar == -1) {
		// Not inside anything...
		if (ch == curr) {
		    break;
		} else if (('\'' == curr) || ('"' == curr)) {
		    insideChar = curr;
		} else if ('(' == curr) {
		    insideChar = ')';
		} else if ('[' == curr) {
		    insideChar = ']';
		}
	    } else if (insideChar == curr) {
		// Was inside something, ending now...
		insideChar = -1;
	    }
	    idx++;
	}

	// If we found it return it, otherwise return -1
	if (idx >= bytes.length) {
	    idx = -1;
	}
	return idx;
    }

    /**
     *	<p> This method reads Commands until a closing '}' is encountered.</p>
     */
    private List<Command> readCommandList() throws IOException {
	int ch = _parser.nextChar();
	List<Command> commands = new ArrayList<Command>();
	Command command = null;
	while (ch != '}') {
	    // Make sure readCommand gets the full command line...
	    if (ch != '{') {
		// We want to throw this char away...
		_parser.unread(ch);
	    }

	    // Read a Command
	    command = readCommand();
	    if (command != null) {
		commands.add(command);
	    }

	    // Skip White Space...
	    _parser.skipCommentsAndWhiteSpace(CommandParser.SIMPLE_WHITE_SPACE);

	    // Get the next char...
	    ch = _parser.nextChar();
	    if (ch == -1)  {
		throw new IOException(
		    "Unexpected end of stream! Expected to find '}'.");
	    }
	}

	// Return the Commands
	return commands;
    }

    /**
     *	<p> This function removes the containing CDATA tags, if found.</p>
     */
    private static String unwrap(String str) {
	str = str.trim();
	if (str.startsWith(OPEN_CDATA)) {
	    int endingIdx = str.lastIndexOf(CLOSE_CDATA);
	    if (endingIdx != -1) {
		// Remove the CDATA wrapper
		str = str.substring(OPEN_CDATA.length(), endingIdx);
	    }
	}
	return str;
    }

    private static final String	    OPEN_CDATA	= "<![CDATA[";
    private static final String	    CLOSE_CDATA	= "]]>";

    private CommandParser  _parser    = null;
}
