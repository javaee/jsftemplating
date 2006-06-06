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
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * NavigationHandlers.java
 *
 * Created on December 6, 2004, 11:06 PM
 */
package com.sun.jsftemplating.handlers;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;

import java.io.IOException;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;


/**
 *  <p>	This class contains
 *	{@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
 *	methods that perform navigation oriented actions.</p>
 *
 *  @author  Ken Paulsen (ken.paulsen@sun.com)
 */
public class NavigationHandlers {

    /**
     *	<p> Default Constructor.</p>
     */
    public NavigationHandlers() {
    }

    /**
     *	<p> This handler navigates to the given page.</p>
     *
     *	<p> Input value: "page" -- Type: <code>String</code></p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="navigate",
	input={
	    @HandlerInput(name="page", type=String.class, required=true)
	})
    public static void navigate(HandlerContext context) {
	String page = (String) context.getInputValue("page");
	FacesContext ctx = context.getFacesContext();
	UIViewRoot newRoot = ctx.getApplication().getViewHandler().
	    createView(ctx, page);
	ctx.setViewRoot(newRoot);
    }

    /**
     *	<p> This handler redirects to the given page.</p>
     *
     *	<p> Input value: "page" -- Type: <code>String</code></p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="redirect",
	input={
	    @HandlerInput(name="page", type=String.class, required=true)
	})
    public static void redirect(HandlerContext context) {
	String page = (String) context.getInputValue("page");
	FacesContext ctx = context.getFacesContext();
	try {
	    ctx.getExternalContext().redirect(page);
	    ctx.responseComplete();
	} catch (IOException ex) {
	    throw new RuntimeException(
		"Unable to navigate to page '" + page + "'!", ex);
	}
    }
}
