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
 *  ScopeHandlers.java
 *
 *  Created on December 2, 2004, 3:06 AM
 */
package com.sun.jsftemplating.handlers;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;

import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;


/**
 *  <p>	This class contains
 *	{@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
 *	methods that perform common utility-type functions.</p>
 *
 *  @author  Ken Paulsen (ken.paulsen@sun.com)
 */
public class ScopeHandlers {

    /**
     *	<p> Default Constructor.</p>
     */
    public ScopeHandlers() {
    }

    /**
     *	<p> This handler gets a request attribute.  It requires "key" as an
     *	    input value.  It returns "value" as an output value.  Note this
     *	    can also be done via #{requestScope["attributeName"]}.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="getAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Object.class)})
    public static void getAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getFacesContext().getExternalContext().
	    getRequestMap().get(key);
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler sets a request attribute.  It requires "key" and
     *	    "value" input values to be passed in.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="setAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="value", required=true)})
    public static void setAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getInputValue("value");
	context.getFacesContext().getExternalContext().
	    getRequestMap().put(key, value);
    }

    /**
     *	<p> This handler produces a String consisting of all the request
     *	    attributes.  It outputs this via the "value" output value.</p>
     */
    @Handler(id="dumpAttributes",
	output={
	    @HandlerOutput(name="value", type=String.class)
	})
    public static void dumpAttributes(HandlerContext context) {
	Map<String, Object> map =
	    context.getFacesContext().getExternalContext().getRequestMap();
	context.setOutputValue("value", formatAttributes(map));
    }

    /**
     *	<p> This handler gets a session attribute.  It requires "key" as an
     *	    input value.  It returns "value" as an output value.  Note this
     *	    can also be done via #{sessionScope["attributeName"]}.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="getSessionAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Object.class)})
    public static void getSessionAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getFacesContext().getExternalContext().
	    getSessionMap().get(key);
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler sets a session attribute.  It requires "key" and
     *	    "value" input values to be passed in.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="setSessionAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="value", required=true)})
    public static void setSessionAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getInputValue("value");
	context.getFacesContext().getExternalContext().
	    getSessionMap().put(key, value);
    }

    /**
     *	<p> This handler produces a String consisting of all the request
     *	    attributes.  It outputs this via the "value" output value.</p>
     */
    @Handler(id="dumpSessionAttributes",
	output={
	    @HandlerOutput(name="value", type=String.class)
	})
    public static void dumpSessionAttributes(HandlerContext context) {
	Map<String, Object> map =
	    context.getFacesContext().getExternalContext().getSessionMap();
	context.setOutputValue("value", formatAttributes(map));
    }

    /**
     *	<p> This handler gets a application attribute.  It requires "key" as an
     *	    input value.  It returns "value" as an output value.  Note this
     *	    can also be done via #{applicationScope["attributeName"]}.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="getApplicationAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Object.class)})
    public static void getApplicationAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getFacesContext().getExternalContext().
	    getApplicationMap().get(key);
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler sets a application attribute.  It requires "key" and
     *	    "value" input values to be passed in.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="setApplicationAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="value", required=true)})
    public static void setApplicationAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getInputValue("value");
	context.getFacesContext().getExternalContext().
	    getApplicationMap().put(key, value);
    }

    /**
     *	<p> This handler produces a String consisting of all the request
     *	    attributes.  It outputs this via the "value" output value.</p>
     */
    @Handler(id="dumpApplicationAttributes",
	output={
	    @HandlerOutput(name="value", type=String.class)
	})
    public static void dumpApplicationAttributes(HandlerContext context) {
	Map<String, Object> map =
	    context.getFacesContext().getExternalContext().getApplicationMap();
	context.setOutputValue("value", formatAttributes(map));
    }

    /**
     *	<p> This method formats attributes from a <code>Map</code>.  This is
     *	    used with the dump handlers.</p>
     */
    private static String formatAttributes(Map<String, Object> map) {
	Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
	Formatter printf = new Formatter();
	Map.Entry<String, Object> entry = null;
	while (it.hasNext()) {
	    entry = it.next();
	    printf.format("%-20s = %s\n", entry.getKey(), entry.getValue());
	}
	return printf.toString();
    }
}
