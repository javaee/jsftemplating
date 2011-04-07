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
import com.sun.jsft.util.Util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.faces.component.UIComponent;
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
	if (condition) {
	    Command command = (Command) FacesContext.getCurrentInstance().
		    getExternalContext().getRequestMap().
		    get(Command.COMMAND_KEY);
	    List<Command> childCommands = command.getChildCommands();
	    if (childCommands != null) {
		for (Command childCommand : childCommands) {
		    childCommand.invoke();
		}
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
}
