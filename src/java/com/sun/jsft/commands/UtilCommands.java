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
 *  Created March 29, 2011
 *  @author Ken Paulsen kenapaulsen@gmail.com
 */
package com.sun.jsft.commands;

import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;


/**
 *  <p>	This class contains methods that perform common utility-type
 *	functionality.</p>
 *
 *  @author  Ken Paulsen (kenapaulsen@gmail.com)
 */
@ApplicationScoped
@ManagedBean(name="util")
public class UtilCommands {

    /**
     *	<p> Default Constructor.</p>
     */
    public UtilCommands() {
    }

    /**
     *	<p> This command prints out the contents of the given
     *	    <code>UIComponent</code>'s attribute map.</p>
     */
    public void dumpAttributeMap(UIComponent comp) {
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
     *	<p> This method returns an <code>Iterator</code> for the given
     *	    <code>List</code>.  The <code>List</code> input value key is:
     *	    "list".  The output value key for the <code>Iterator</code> is:
     *	    "iterator".</p>
     *
     *	@param	context	The HandlerContext.
    @Handler(id="getIterator",
	input={
	    @HandlerInput(name="list", type=List.class, required=true)},
	output={
	    @HandlerOutput(name="iterator", type=Iterator.class)})
    public static void getIterator(HandlerContext context) {
	List<Object> list = (List<Object>) context.getInputValue("list");
	context.setOutputValue("iterator", list.iterator());
    }
     */

    /**
     *	<p> This method returns a <code>Boolean</code> value representing
     *	    whether another value exists for the given <code>Iterator</code>.
     *	    The <code>Iterator</code> input value key is: "iterator".  The
     *	    output value key is "hasNext".</p>
     *
     *	@param	context	The HandlerContext.
    @Handler(id="iteratorHasNext",
	input={
	    @HandlerInput(name="iterator", type=Iterator.class, required=true)},
	output={
	    @HandlerOutput(name="hasNext", type=Boolean.class)})
    public static void iteratorHasNext(HandlerContext context) {
	Iterator<Object> it = (Iterator<Object>) context.getInputValue("iterator");
	context.setOutputValue("hasNext", Boolean.valueOf(it.hasNext()));
    }
     */

    /**
     *	<p> This method returns the next object in the <code>List</code> that
     *	    the given <code>Iterator</code> is iterating over.  The
     *	    <code>Iterator</code> input value key is: "iterator".  The
     *	    output value key is "next".</p>
     *
     *	@param	context	The HandlerContext.
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
     */

    /**
     *	<p> This method creates a List.  Optionally you may supply "size" to
     *	    create a List of blank "" values of the specified size.  The
     *	    output value from this command is "result".</p>
     *
     *	@param	context	The HandlerContext
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
     */

    /**
     *	<p> This method creates a <code>Map</code> (<code>HashMap</code>).
     *	    The output value from this command is "result".</p>
     *
     *	@param	context	The <code>HandlerContext<code>
    @Handler(id="createMap",
	output={
	    @HandlerOutput(name="result", type=Map.class)})
    public static void createMap(HandlerContext context) {
	Map<Object, Object> map = new HashMap<Object, Object>();
	context.setOutputValue("result", map);
    }
     */

    /**
     *	<p> This method adds a value to a </code>Map</code>.  You must supply
     *	    <code>map</code> to use as well as the <code>key</code> and
     *	    <code>value</code> to add.</p>
     *
     *	@param	context	The <code>HandlerContext<code>
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
     */

    /**
     *	<p> This command url-encodes the given String.  It will return null if
     *	    null is given and it will use a default encoding of "UTF-8" if no
     *	    encoding is specified.</p>
     *
     *	@param	context	The HandlerContext.
    @Handler(id="urlencode",
	input={
	    @HandlerInput(name="value", type=String.class, required=true),
	    @HandlerInput(name="encoding", type=String.class)
	},
	output={
	    @HandlerOutput(name="result", type=String.class)})
    public static void urlencode(HandlerContext context) {
	String value = (String) context.getInputValue("value");
	String encoding = (String) context.getInputValue("encoding");
	if (encoding == null) {
	    encoding = "UTF-8";
	}
	// The value could be null if an EL expression maps to null
	if (value != null) {
	    try {
		value = java.net.URLEncoder.encode(value, encoding);
	    } catch (java.io.UnsupportedEncodingException ex) {
		throw new IllegalArgumentException(ex);
	    }
	}
	context.setOutputValue("result", value);
    }
     */

    /**
     *	<p> This command gets the current system time in milliseconds.  It may
     *	    be used to time things.</p>
    @Handler(id="getDate",
	output={
	    @HandlerOutput(name="time", type=Long.class)
	})
    public static void getDate(HandlerContext context) {
	context.setOutputValue("time", new java.util.Date().getTime());
    }
     */

    /**
     *	<p> This method converts '&lt;' and '&gt;' characters into "&amp;lt;"
     *	    and "&amp;gt;" in an effort to avoid HTML from being processed.
     *	    This can be used to avoid &lt;script&gt; tags, or to show code
     *	    examples which might include HTML characters.  '&amp;' characters
     *	    will also be converted to "&amp;amp;".</p>
    @Handler(id="htmlEscape",
	input={
	    @HandlerInput(name="value", type=String.class, required=true)
	},
	output={
	    @HandlerOutput(name="result", type=String.class)})
    public static void htmlEscape(HandlerContext context) {
	String value = (String) context.getInputValue("value");
	value = com.sun.jsft.util.Util.htmlEscape(value);
	context.setOutputValue("result", value);
    }
     */

    /**
     *	<p> A utility command that resembles the for() method in Java. Commands
     *	    inside the for loop will be executed in a loop.  The starting index
     *	    is specified by <code>start</code>.  The index will increase
     *	    sequentially untill it is equal to <code>end</code>.
     *	    <code>var</code> will be a request attribute that is set to the
     *	    current index value as the loop iterates.</p>
     *	<p> For example:</p>
     *
     *	<code>forLoop(start="1"  end="3" var="foo") {...}</code>
     *
     *	<p>The commands inside the {...} will be executed 2 times
     *	   (with foo=1 and foo=2).</p>
     *
     *	<ul><li><code>start</code> -- type: <code>Integer</code> Starting
     *		index, defaults to zero if not specified.</li>
     *	    <li><code>end</code> -- type: <code>Integer</code>; Ending index.
     *		Required.</li>
     *	    <li><code>var</code> -- type: <code>String</code>; Request
     *		attribute to be set in the for loop to the value of the
     *		index.</li></ul>
    public static boolean forLoop(int start, int end, String var) {
	List<> commands =
	    handlerCtx.getHandler().getChildHandlers();
	if (commands.size() > 0) {
	    // We have child commands in the loop... execute while we iterate
	    Map<String, Object> requestMap = FacesContext.getCurrentInstance().
		    getExternalContext().getRequestMap();
	    for (int idx=start; idx < end; idx++) {
		requestMap.put(var, idx);
		// Ignore what is returned by the commands... we need to return
		// false anyway to prevent children from being executed again
		elt.dispatchHandlers(commands);
	    }
	}

	// This will prevent the child commands from executing again
	return false;
    }
     */
}
