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
package com.sun.jsftemplating.layout.descriptors;

import com.sun.jsftemplating.el.VariableResolver;
import com.sun.jsftemplating.layout.event.AfterLoopEvent;
import com.sun.jsftemplating.layout.event.BeforeLoopEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentTag;


/**
 *  <p>	This class defines a LayoutForEach {@link LayoutElement}.  The
 *	LayoutForEach provides the functionality necessary to iteratively
 *	display a portion of the layout tree.  The list property contains
 *	the <code>List</code> of items to iterate over.</p>
 *
 *  @see VariableResolver
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutForEach extends LayoutElementBase implements LayoutElement {

    /**
     *	<p> Constructor.</p>
     *
     *	@param	parent		The parent {@link LayoutElement}
     *	@param	listBinding	The <code>List</code> to iterate over
     *	@param	key		The <code>ServletRequest</code> attribute key
     *				used to store the object being processed
     */
    public LayoutForEach(LayoutElement parent, String listBinding, String key) {
	super(parent, null);
	if ((listBinding == null) || listBinding.equals("")) {
	    throw new IllegalArgumentException("'listBinding' is required!");
	}
	if ((key == null) || key.equals("")) {
	    throw new IllegalArgumentException("'key' is required!");
	}
	_listBinding = listBinding;
	_key = key;
    }


    /**
     *	<p> This method always returns true.  The condition is based on an
     *	    <code>Iterator.hasNext()</code> call instead of here because
     *	    the {@link #encode(FacesContext, UIComponent)} method
     *	    evaluates this and then calls the super.  Performing the check
     *	    here would cause the condition to be evaluated twice.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	component   The <code>UIComponent</code>.
     *
     *	@return	true
     */
    protected boolean encodeThis(FacesContext context, UIComponent component) {
	return true;
    }

    /**
     *	<p> This method evaluates the list binding for this
     *	    <code>LayoutForEach</code>.  This is expected to evaulate to a
     *	    <code>List</code> object.  If it doesn't, this method will throw a
     *	    <code>NullPointerException</code> (if it evaulates to
     *	    <code>null</code>), or an <code>IllegalArgumentException</code> if
     *	    it doesn't evaluate to a <code>List</code>.</p>
     *
     *	@param	context	The <code>FacesContext</code>
     *
     *	return	The <code>List</code> of objects to iterate over
     */
    protected List getList(FacesContext context) {
	// Invoke our own EL.  This is needed b/c JSF's EL is designed for
	// Bean getters only.  It does not get CONSTANTS or pull data from
	// other sources (such as session, request attributes, etc., etc.)
	// Resolve our variables now because we cannot depend on the
	// individual components to do this.  We may want to find a way to
	// make this work as a regular ValueExpression... but for
	// now, we'll just resolve it this way.
	Object value = VariableResolver.resolveVariables(
	    context, this, null /* FIXME: component */, _listBinding);

	// Next check to see if the value contains a ValueExpression
	if (value != null) {
	    String strVal = value.toString();
	    if (UIComponentTag.isValueReference(strVal)) {
		ELContext elctx = context.getELContext();
		ValueExpression ve =
		    context.getApplication().getExpressionFactory().
			createValueExpression(elctx, strVal, List.class);
		value = ve.getValue(elctx);
	    }
	}

	// Make sure we found something...
	if (value == null) {
	    throw new NullPointerException("List not found via expression: '"
		    + _listBinding + "'.");
	}

	// Make sure we have a List...
	if (!(value instanceof List)) {
	    throw new IllegalArgumentException("Expression '" + _listBinding
		    + "' did not resolve to a List! Found: '" + value + "'");
	}

	// Return the List
	return (List) value;
    }

    /**
     *	<p> This method sets the <code>Object</code> that is currently being
     *	    processed by this <code>LayoutForEach</code>.  This implementation
     *	    stores this value in the request attribute map undert the key
     *	    provided to this <code>LayoutForEach</code>.</p>
     *
     *	<p> As an added convenience, this method will also set an attribute
     *	    that contains the current index number.  The attribute key will be
     *	    the same key the <code>Object</code> is stored under plus "-index".
     *	    The index is stored as a String.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	value	    The <code>Object</code> to store
     *	@param	index	    The current index number of the <code>Object</code>
     */
    protected void setCurrentForEachValue(FacesContext context, Object value, int index, String key) {
	Map map = context.getExternalContext().getRequestMap();
	map.put(key, value);
	map.put(key + "-index", "" + index);
    }

    /**
     *	<p> This implementation overrides the parent <code>encode</code>
     *	    method.  It does this to cause the encode process to loop as long
     *	    as there are more <code>List</code> entries to process.</p>
     *
     *	@param	context	    The FacesContext
     *	@param	component   The UIComponent
     */
    public void encode(FacesContext context, UIComponent component) throws IOException {
	// Before events..
	Object result = dispatchHandlers(context, BEFORE_LOOP,
	    new BeforeLoopEvent(component));

	String key = resolveValue(
		context, component, _key).toString();

	// Iterate over the values in the list and perform the requested
	// action(s) per the body of the LayoutForEach
	Iterator it = getList(context).iterator();
	for (int index = 1; it.hasNext(); index++) {
	    setCurrentForEachValue(context, it.next(), index, key);
	    super.encode(context, component);
	}

	// Invoke any "after" handlers
	result = dispatchHandlers(context, AFTER_LOOP,
	    new AfterLoopEvent(component));
    }


    private String  _listBinding    = null;
    private String  _key	    = null;


    /**
     *	<p> This is the event "type" for
     *	    {@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
     *	    elements to be invoked after this LayoutForEach is processed
     *	    (outside loop).</p>
     */
     public static final String AFTER_LOOP =	"afterLoop";

    /**
     *	<p> This is the event "type" for
     *	    {@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
     *	    elements to be invoked before this LayoutForEach is processed
     *	    (outside loop).</p>
     */
     public static final String BEFORE_LOOP =	"beforeLoop";
}
