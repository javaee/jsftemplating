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

/**
 *  UtilCommands.java
 *
 *  Created  April 2, 2011
 *  @author  Ken Paulsen (kenapaulsen@gmail.com)
 */
package com.sun.jsft.commands;

import com.sun.jsft.event.Command;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.faces.context.FacesContext;


/**
 *  <p>	This class contains methods that perform common utility-type
 *	functionality.</p>
 *
 *  @author  Ken Paulsen (kenapaulsen@gmail.com)
 */
@ApplicationScoped
@ManagedBean(name="jsft")
public class JSFTCommands {

    /**
     *	<p> Default Constructor.</p>
     */
    public JSFTCommands() {
    }

    /**
     *	<p> This command conditionally executes its child commands.</p>
     */
    public void _if(boolean condition) {
	Command command = (Command) FacesContext.getCurrentInstance().
		getExternalContext().getRequestMap().get(Command.COMMAND_KEY);
	if (condition) {
	    command.invokeChildCommands();
	} else {
	    command = command.getElseCommand();
	    if (command != null) {
		command.invoke();
	    }
	}
    }

    /**
     *	<p> This command iterates over the given List and sets given
     */
    public void foreach(String var, List list) {
	// Get the Request Map
	Map<String, Object> reqMap = FacesContext.getCurrentInstance().
		getExternalContext().getRequestMap();

	// Get the Current Command...
	Command command = (Command) reqMap.get(Command.COMMAND_KEY);

	// Iterate over each item in the List
	List<Command> childCommands = null;
	for (Object item : list) {
	    // Set the item in the request scope under the given key
	    reqMap.put(var, item);

	    // Invoke all the child commands
	    childCommands = command.getChildCommands();
	    if (childCommands != null) {
		for (Command childCommand : childCommands) {
		    childCommand.invoke();
		}
	    }
	}
    }


    /**
     *	<p> This command sets a requestScope attribute with the given
     *	    <code>key</code> and <code>value</code>.</p>
     */
    public void setAttribute(String key, Object value) {
	FacesContext.getCurrentInstance().getExternalContext().
		getRequestMap().put(key, value);
    }

    /**
     *	<p> This command writes output using <code>System.out.println</code>.
     *	    It requires <code>value</code> to be supplied.</p>
     */
    public void println(String value) {
	System.out.println(value);
    }

    /**
     *	<p> This command writes using
     *	    <code>FacesContext.getResponseWriter()</code>.</p>
     *
     *	@param	context	The HandlerContext.
     */
    public static void write(String text) {
	if (text == null) {
	    text = "";
	}
	try {
	    FacesContext.getCurrentInstance().getResponseWriter().write(text);
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}
    }

    /**
     *	<p> This command marks the response complete.  This means that no
     *	    additional response will be sent.  This is useful if you've
     *	    provided a response already and you don't want JSF to do it again
     *	    (it may cause problems to do it 2x).</p>
     *
     *	@param	context	The HandlerContext.
     */
    public static void responseComplete() {
	FacesContext.getCurrentInstance().responseComplete();
    }

    /**
     *	<p> This command indicates to JSF that the request should proceed
     *	    immediately to the render response phase.  It will be ignored if
     *	    rendering has already begun.  This is useful if you want to stop
     *	    processing and jump to the response.  This is often the case when
     *	    an error ocurrs or validation fails.  Typically the page the user
     *	    is on will be reshown (although if navigation has already
     *	    occurred, the new page will be shown.</p>
     *
     *	@param	context	The HandlerContext.
     */
    public void renderResponse() {
	FacesContext.getCurrentInstance().renderResponse();
    }

    /**
     *	<p> This command provides a way to see the call stack by printing a
     *	    stack trace.  The output will go to stderr and will also be
     *	    returned in the output value "stackTrace".  An optional message
     *	    may be provided to be included in the trace.</p>
     */
    public void printStackTrace(String msg) {
	// See if we have a message to print w/ it
	if (msg == null) {
	    msg = "";
	}

	// Get the StackTrace
	StringWriter strWriter = new StringWriter();
	new RuntimeException(msg).printStackTrace(new PrintWriter(strWriter));
	String trace = strWriter.toString();

	// Print it to stderr and return it
	System.err.println(trace);
    }
    
    /**
     *	<p> Returns the nano seconds since some point in time.  This is only
     *	    useful for relative measurments.</p>
     */
    public long getNanoTime() {
	return nanoStartTime - System.nanoTime();
    }

    /**
     *	<p> This is application scoped, so it is not safe to change.  Use
     *	    caution.</p>
     */
    private long nanoStartTime = System.nanoTime();
}
