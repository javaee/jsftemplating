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
 * ComponentHandlers.java
 *
 * Created on December 6, 2004, 11:06 PM
 */
package com.sun.jsftemplating.handlers;

import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;

import java.util.List;

import javax.faces.component.UIComponent;


/**
 *  <p>	This class contains
 *	{@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
 *	methods that perform component functions.</p>
 *
 *  @author  Ken Paulsen (ken.paulsen@sun.com)
 */
public class ComponentHandlers {

    /**
     *	<p> Default Constructor.</p>
     */
    public ComponentHandlers() {
    }

    /**
     *	<p> This handler returns the children of the given
     *	    <code>UIComponent</code>.</p>
     *
     *	<p> Input value: "parent" -- Type: <code>UIComponent</code></p>
     *
     *	<p> Output value: "children" -- Type: <code>java.util.List</code></p>
     *	<p> Output value: "size"     -- Type: <code>java.lang.Integer</code></p>
     *
     *	@param	context	The HandlerContext.
     */
    public void getChildren(HandlerContext context) {
	UIComponent parent = (UIComponent) context.getInputValue("parent");
	List list = parent.getChildren();
	context.setOutputValue("children", list);
	context.setOutputValue("size", new Integer(list.size()));
    }

    /**
     *	<p> This handler sets a <code>UIComponent</code> attribute /
     *	    property.</p>
     *
     *	<p> Input value: "component" -- Type: <code>UIComponent</code></p>
     *	<p> Input value: "property" -- Type: <code>String</code></p>
     *	<p> Input value: "value" -- Type: <code>Object</code></p>
     *
     *	@param	context	The HandlerContext.
     */
    public void setComponentProperty(HandlerContext context) {
	UIComponent component =
	    (UIComponent) context.getInputValue("component");
	String propName = (String) context.getInputValue("property");
	Object value = context.getInputValue("value");

	// Set the attribute or property value
	component.getAttributes().put(propName, value);
    }
}
