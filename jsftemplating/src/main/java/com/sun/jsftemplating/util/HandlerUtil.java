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

package com.sun.jsftemplating.util;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContextImpl;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;


/**
 *  <p>	This class is for {@link Handler} utility methods.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class HandlerUtil {

    /**
     *	<p> This method invokes the {@link Handler} identified by the given
     *	    id.</p>
     *
     *	@param	handlerId   The id of the globally defined {@link Handler}.
     *	@param	elt	    The {@link LayoutElement} to associate with the
     *			    {@link Handler}, it does not need to reference
     *			    the {@link Handler}.
     *	@param	args	    <code>Object[]</code> that represent the arguments
     *			    to pass into the {@link Handler}.
     *
     *	@return	The value returned from the {@link Handler} if any.
     */
    public static Object dispatchHandler(String handlerId, LayoutElement elt, Object ... args) {
	// Get the Handler
	HandlerDefinition def = elt.getLayoutDefinition().getHandlerDefinition(handlerId);
	if (def == null) {
	    def = LayoutDefinitionManager.getGlobalHandlerDefinition(handlerId);
	}
	if (def == null) {
	    throw new IllegalArgumentException(
		"Unable to locate handler definition for '" + handlerId + "'!");
	}
	Handler handler = new Handler(def);
	if (args != null) {
	    // Basic check to make sure we have valid arguments
	    int size = args.length;
	    if ((size % 2) == 1) {
		throw new IllegalArgumentException("Arguments to "
			+ "dispatchHandler must be paired: name1, value1, "
			+ "name2, value2.  An odd number was received which "
			+ "is invalid.");
	    }

	    // Set all the input values
	    String name = null;
	    Object value = null;
	    for (int count=0; count<size; count += 2) {
		name = (String) args[count];
		value = args[count+1];
		handler.setInputValue(name, value);
	    }
	}

	// Put it in a List
	List<Handler> handlers = new ArrayList<Handler>();
	handlers.add(handler);

	// Create a HandlerContext...
	HandlerContext handlerCtx = new HandlerContextImpl(
		FacesContext.getCurrentInstance(), elt, new EventObject(elt), "none");

	return elt.dispatchHandlers(handlerCtx, handlers);
    }
}
