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
 *  UtilHandlers.java
 *
 *  Created on December 2, 2004, 3:06 AM
 */
package com.sun.jsftemplating.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.el.PageSessionResolver;
import com.sun.jsftemplating.layout.LayoutViewHandler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;


/**
 *  <p>	This class contains
 *	{@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
 *	methods that perform common utility-type functions.</p>
 *
 *  @author  Ken Paulsen (ken.paulsen@sun.com)
 */
public class UtilHandlers {

    /**
     *	<p> Default Constructor.</p>
     */
    public UtilHandlers() {
    }

    @Handler(id="if",
	input={@HandlerInput(name="condition", type=String.class)})
    public static void ifHandler(HandlerContext context) {
	// Do nothing, the purpose of this handler is to provide condition
	// support which is handled by the parser / runtime.
    }

    /**
     *	<p> This handler writes using <code>System.out.println</code>.  It
     *	    requires that <code>value</code> be supplied as a String input
     *	    parameter.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="println",
	input={@HandlerInput(name="value", type=String.class, required=true)})
    public static void println(HandlerContext context) {
	String value = (String) context.getInputValue("value");
	System.out.println(value);
    }

    /**
     *	<p> This handler writes using
     *	    <code>FacesContext.getResponseWriter()</code>.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="write",
	input={@HandlerInput(name="value", type=String.class, required=true)})
    public static void write(HandlerContext context) {
	String text = (String) context.getInputValue("value");
	if (text == null) {
	    // Even though this is required, an expression can evaluate to null
	    text = "";
	}
	try {
	    context.getFacesContext().getResponseWriter().write(text);
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}
    }


    /**
     *	<p> This handler decrements a number by 1.  This handler requires
     *	    "number" to be supplied as an Integer input value.  It sets an
     *	    output value "value" to number-1.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="dec",
	input={
	    @HandlerInput(name="number", type=Integer.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Integer.class)})
    public static void dec(HandlerContext context) {
	Integer value = (Integer) context.getInputValue("number");
	context.setOutputValue("value", new Integer(value.intValue() - 1));
    }

    /**
     *	<p> This handler increments a number by 1.  This handler requires
     *	    "number" to be supplied as an Integer input value.  It sets an
     *	    output value "value" to number+1.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="inc",
	input={
	    @HandlerInput(name="number", type=Integer.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Integer.class)})
    public static void inc(HandlerContext context) {
	Integer value = (Integer) context.getInputValue("number");
	context.setOutputValue("value", new Integer(value.intValue() + 1));
    }

    /**
     *	<p> This method returns an <code>Iterator</code> for the given
     *	    <code>List</code>.  The <code>List</code> input value key is:
     *	    "list".  The output value key for the <code>Iterator</code> is:
     *	    "iterator".</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="getIterator",
	input={
	    @HandlerInput(name="list", type=List.class, required=true)},
	output={
	    @HandlerOutput(name="iterator", type=Iterator.class)})
    public static void getIterator(HandlerContext context) {
	List<Object> list = (List<Object>) context.getInputValue("list");
	context.setOutputValue("iterator", list.iterator());
    }

    /**
     *	<p> This method returns a <code>Boolean</code> value representing
     *	    whether another value exists for the given <code>Iterator</code>.
     *	    The <code>Iterator</code> input value key is: "iterator".  The
     *	    output value key is "hasNext".</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="iteratorHasNext",
	input={
	    @HandlerInput(name="iterator", type=Iterator.class, required=true)},
	output={
	    @HandlerOutput(name="hasNext", type=Boolean.class)})
    public static void iteratorHasNext(HandlerContext context) {
	Iterator<Object> it = (Iterator<Object>) context.getInputValue("iterator");
	context.setOutputValue("hasNext", Boolean.valueOf(it.hasNext()));
    }

    /**
     *	<p> This method returns the next object in the <code>List</code> that
     *	    the given <code>Iterator</code> is iterating over.  The
     *	    <code>Iterator</code> input value key is: "iterator".  The
     *	    output value key is "next".</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="iteratorNext",
	input={
	    @HandlerInput(name="iterator", type=Iterator.class, required=true)},
	output={
	    @HandlerOutput(name="next")})
    public static void iteratorNext(HandlerContext context) {
	Iterator<Object> it =
		(Iterator<Object>) context.getInputValue("iterator");
	context.setOutputValue("next", it.next());
    }

    /**
     *	<p> This method creates a List.  Optionally you may supply "size" to
     *	    create a List of blank "" values of the specified size.  The
     *	    output value from this handler is "result".</p>
     *
     *	@param	context	The HandlerContext
     */
    @Handler(id="createList",
	input={
	    @HandlerInput(name="size", type=Integer.class, required=true)},
	output={
	    @HandlerOutput(name="result", type=List.class)})
    public static void createList(HandlerContext context) {
	int size = ((Integer) context.getInputValue("size")).intValue();
	List<Object> list = new ArrayList<Object>(size);
	for (int count = 0; count < size; count++) {
	    list.add("");
	}
	context.setOutputValue("result", list);
    }

    /**
     *	<p> This method creates a <code>Map</code> (<code>HashMap</code>).
     *	    The output value from this handler is "result".</p>
     *
     *	@param	context	The <code>HandlerContext<code>
     */
    @Handler(id="createMap",
	output={
	    @HandlerOutput(name="result", type=Map.class)})
    public static void createMap(HandlerContext context) {
	Map<Object, Object> map = new HashMap<Object, Object>();
	context.setOutputValue("result", map);
    }

    /**
     *	<p> This method adds a value to a </code>Map</code>.  You must supply
     *	    <code>map</code> to use as well as the <code>key</code> and
     *	    <code>value</code> to add.</p>
     *
     *	@param	context	The <code>HandlerContext<code>
     */
    @Handler(id="mapPut",
	input={
	    @HandlerInput(name="map", type=Map.class, required=true),
	    @HandlerInput(name="key", type=Object.class, required=true),
	    @HandlerInput(name="value", type=Object.class, required=true)}
	)
    public static void mapPut(HandlerContext context) {
	Map map = (Map) context.getInputValue("map");
	Object key = context.getInputValue("key");
	Object value = context.getInputValue("value");
	map.put(key, value);
    }

    /**
     *	<p> This method returns true.  It does not take any input or provide
     *	    any output values.</p>
     *
     *	@param context	The {@link HandlerContext}
     */
    @Handler(id="returnTrue")
    public static boolean returnTrue(HandlerContext context) {
	return true;
    }

    /**
     *	<p> This method returns false.  It does not take any input or provide
     *	    any output values.</p>
     *
     *	@param context	The {@link HandlerContext}
     */
    @Handler(id="returnFalse")
    public static boolean returnFalse(HandlerContext context) {
	return false;
    }

    /**
     *	<p> This method enables you to retrieve the clientId for the given
     *	    <code>UIComponent</code>.</p>
     *
     *	@param context	The {@link HandlerContext}
     */
    @Handler(id="getClientId",
	input={
	    @HandlerInput(name="component", type=UIComponent.class, required=true)},
	output={
	    @HandlerOutput(name="clientId", type=String.class)})
    public static void getClientId(HandlerContext context) {
	UIComponent comp = (UIComponent) context.getInputValue("component");
	context.setOutputValue("clientId",
		comp.getClientId(context.getFacesContext()));
    }

    /**
     *	<p> This method enables you to retrieve the id or clientId for the given
     *	    <code>Object</code> which is expected to be a
     *	    <code>UIComponent</code> or a <code>String</code> that already
     *	    represents the clientId.</p>
     *
     *	@param context	The {@link HandlerContext}
     */
    @Handler(id="getId",
	input={
	    @HandlerInput(name="object", required=true)},
	output={
	    @HandlerOutput(name="id", type=String.class),
	    @HandlerOutput(name="clientId", type=String.class)})
    public static void getId(HandlerContext context) {
	Object obj = context.getInputValue("object");
	if (obj == null) {
	    return;
	}

	String clientId = null;
	String id = null;
	if (obj instanceof UIComponent) {
	    clientId =
		((UIComponent) obj).getClientId(context.getFacesContext());
	    id = ((UIComponent) obj).getId();
	} else {
	    clientId = obj.toString();
	    id = clientId.substring(clientId.lastIndexOf(':') + 1);
	}
	context.setOutputValue("id", id);
	context.setOutputValue("clientId", clientId);
    }

    /**
     *	<p> This handler provides a way to see the call stack by printing a
     *	    stack trace.  The output will go to stderr and will also be
     *	    returned in the output value "stackTrace".  An optional message
     *	    may be provided to be included in the trace.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="printStackTrace",
	input={
	    @HandlerInput(name="msg", type=String.class)},
	output={
	    @HandlerOutput(name="stackTrace", type=String.class)})
    public static void printStackTrace(HandlerContext context) {
	// See if we have a message to print w/ it
	String msg = (String) context.getInputValue("msg");
	if (msg == null) {
	    msg = "";
	}

	// Get the StackTrace
	StringWriter strWriter = new StringWriter();
	new RuntimeException(msg).printStackTrace(new PrintWriter(strWriter));
	String trace = strWriter.toString();

	// Print it to stderr and return it
	System.err.println(trace);
	context.setOutputValue("stackTrace", trace);
    }
    
    /**
     *	<p> This handler prints out the contents of the given UIComponent's
     *	    attribute map.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="dumpAttributeMap",
	input={
	    @HandlerInput(name="component", type=UIComponent.class)})
    public static void dumpAttributeMap(HandlerContext context) {
	UIComponent comp = (UIComponent) context.getInputValue("component");
	if (comp != null) {
            Map<String,Object> map = comp.getAttributes();
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry me = (Map.Entry)iter.next();
                System.out.println("key="+ me.getKey()+"'"+"value="+ me.getValue());
            }
        } else {
            System.out.println("UIComponent is null");
            
        }
    }

    /**
     *	<p> This handler sets the encoding type of the given UIViewRoot's
     *	    attribute map.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="setEncoding",
	input={
	    @HandlerInput(name="value", type=String.class)
	})
    public static void setEncoding(HandlerContext context) {
	String value = (String) context.getInputValue("value");
	FacesContext fctxt = context.getFacesContext();
	if(fctxt != null) {
		UIViewRoot root = fctxt.getViewRoot();
		// Get the Page Session Map
		Map<String, Serializable> map =
	    PageSessionResolver.getPageSession(fctxt, root);
		if (map == null) {
	    	map = PageSessionResolver.createPageSession(fctxt, root);
		}

		// Set the page session value
		map.put(LayoutViewHandler.ENCODING_TYPE , value);
	}
    }

    /**
     *	<p> This handler prints out the contents of the given UIComponent's
     *	    attribute map.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="urlencode",
	input={
	    @HandlerInput(name="value", type=String.class, required=true),
	    @HandlerInput(name="encoding", type=String.class)
	},
	output={
	    @HandlerOutput(name="value", type=String.class)})
    public static void urlencode(HandlerContext context) {
	String value = (String) context.getInputValue("value");
	String encoding = (String) context.getInputValue("encoding");
	try {
	    value = (encoding == null) ?
		URLEncoder.encode(value) : URLEncoder.encode(value, encoding);
	} catch (UnsupportedEncodingException ex) {
	    throw new IllegalArgumentException(ex);
	}
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler marks the response complete.  This means that no
     *	    additional response will be sent.  This is useful if you've
     *	    provided a response already and you don't want JSF to do it again
     *	    (it may cause problems to do it 2x).</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="responseComplete")
    public static void responseComplete(HandlerContext context) {
	context.getFacesContext().responseComplete();
    }

    /**
     *	<p> This handler indicates to JSF that the request should proceed
     *	    immediately to the render response phase.  It will be ignored if
     *	    rendering has already begun.  This is useful if you want to stop
     *	    processing and jump to the response.  This is often the case when
     *	    an error ocurrs or validation fails.  Typically the page the user
     *	    is on will be reshown (although if navigation has already
     *	    occurred, the new page will be shown.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="renderResponse")
    public static void renderResponse(HandlerContext context) {
	context.getFacesContext().renderResponse();
    }

    /**
     *	<p> This handler gets the current system time in milliseconds.  It may
     *	    be used to time things.</p>
     */
    @Handler(id="getDate",
	output={
	    @HandlerOutput(name="time", type=Long.class)
	})
    public static void getDate(HandlerContext context) {
	context.setOutputValue("time", new java.util.Date().getTime());
    }
}
