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
import com.sun.jsftemplating.annotation.HandlerOutput;
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
     *	<p> This handler returns a <code>UIViewRoot</code>.  If the
     *	    <code>id</code> parameter is supplied it will return the requested
     *	    <code>UIViewRoot</code> (this may fail and cause an exception). If
     *	    the <code>id</code> is <em>not</em> supplied, it will return the
     *	    current <code>UIViewRoot</code>.  The result will be returned in
     *	    an output parameter named <code>viewRoot</code>.</p>
     */
    @Handler(id="getUIViewRoot",
	input={
	    @HandlerInput(name="id", type=String.class)
	},
	output={
	    @HandlerOutput(name="viewRoot", type=UIViewRoot.class)
	})
    public void getUIViewRoot(HandlerContext context) {
	String pageName = (String) context.getInputValue("id");
	FacesContext ctx = context.getFacesContext();
	UIViewRoot root = null;
	if (pageName == null) {
	    root = ctx.getViewRoot();
	} else {
	    if (pageName.charAt(0) != '/') {
		// Ensure we start w/ a '/'
		pageName = "/" + pageName;
	    }
	    root = ctx.getApplication().getViewHandler().
		createView(ctx, pageName);
	}
	context.setOutputValue("viewRoot", root);
    }

    /**
     *	<p> This handler navigates to the given page.  <code>page</code> may
     *	    either be a <code>UIViewRoot</code> or a <code>String</code>
     *	    representing a <code>UIViewRoot</code>.  Passing in a
     *	    <code>String</code> name of a <code>UIViewRoot</code> will always
     *	    create a new <code>UIViewRoot</code>.  Passing in the
     *	    <code>UIViewRoot</code> provides an opportunity to customize the
     *	    <code>UIComponent</code> tree that will be displayed.</p>
     *
     *	<p> The {@link #getUIViewRoot(HandlerContext)} handler provides a way
     *	    to obtain a <code>UIViewRoot</code>.</p>
     *
     *	<p> Input value: "page" -- Type: <code>Object</code> (should be a
     *	    <code>String</code> or a <code>UIViewRoot</code>).</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="navigate",
	input={
	    @HandlerInput(name="page", type=Object.class, required=true)
	})
    public static void navigate(HandlerContext context) {
	Object page = context.getInputValue("page");
	UIViewRoot root = null;
	FacesContext ctx = context.getFacesContext();
	if (page instanceof String) {
	    // Create a new UIViewRoot with the given id
	    String strPage = (String) page;
	    if (strPage.charAt(0) != '/') {
		// Ensure we start w/ a '/'
		strPage = "/" + strPage;
	    }
	    root = ctx.getApplication().getViewHandler().
		createView(ctx, strPage);
	} else if (page instanceof UIViewRoot) {
	    // We recieved a UIViewRoot, use it...
	    root = (UIViewRoot) page;
	} else {
	    throw new IllegalArgumentException("Type '"
		+ page.getClass().getName()
		+ "' is not valid.  It must be a String or UIViewRoot.");
	}

	// Set the UIViewRoot so that it will be displayed
	ctx.setViewRoot(root);
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
