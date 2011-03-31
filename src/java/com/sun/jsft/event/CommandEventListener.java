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

package com.sun.jsft.event;

import com.sun.jsft.util.Util;

import java.io.Serializable;
import java.util.List;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;


/**
 *  <p>	This class handles the dispatching of events to commands.  Commands
 *	are simply EL functions.</p>
 *
 *  Created March 29, 2011
 *  @author Ken Paulsen kenapaulsen@gmail.com
 */
public class CommandEventListener implements ComponentSystemEventListener, Serializable {

    /**
     *	<p> Default constructor needed for serialization.</p>
     */
    public CommandEventListener() {
    }

    /**
     *	<p> Primary constructor used.  It is neeeded in order to supply a list
     *	    of commands.</p>
     */
    public CommandEventListener(List<String> commands) {
        this.commands = commands;
    }

    /**
     *	<p> This method is responsible for dispatching the event to the various
     *	    EL expressions that are listening to this event.  It also stores
     *	    the Event object in request scope under the key "theEvent" so that
     *	    it can be accessed easiliy via EL.  For example:
     *	    <code>util.println(theEvent);</code></p>
     */
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
	FacesContext ctx = FacesContext.getCurrentInstance();
	// Store the event under the key "theEvent" in case we want to access
	// it for some reason.
	ctx.getExternalContext().getRequestMap().put("theEvent", event);

	ExpressionFactory elFactory = ctx.getApplication().getExpressionFactory();
	ELContext elCtx = ctx.getELContext();
	for (String command : commands) {
	    // Create expression
	    ValueExpression ve = elFactory.createValueExpression(
		    elCtx, "#{" + command + "}", Object.class);

	    // Execute expression
	    ve.getValue(elCtx);
	}
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        CommandEventListener that = (CommandEventListener) obj;

	if (hashCode() != that.hashCode()) {
	    return false;
	}

        return true;
    }

    @Override
    public int hashCode() {
	if (hash == -1) {
	    StringBuilder builder = new StringBuilder();
	    for (String command : commands) {
		builder.append(command);
	    }
	    hash = builder.toString().hashCode();
	}
	return hash;
    }

    private int hash = -1;
    private static final long serialVersionUID = 8945415935164238908L;

    private List<String> commands = null;
}
