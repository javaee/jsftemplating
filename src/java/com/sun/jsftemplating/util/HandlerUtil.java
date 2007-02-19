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
package com.sun.jsftemplating.util;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContextImpl;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.faces.context.FacesContext;


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
	HandlerDefinition def =
		LayoutDefinitionManager.getGlobalHandlerDefinition(handlerId);
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
