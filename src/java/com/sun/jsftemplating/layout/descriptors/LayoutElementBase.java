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

import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.layout.event.AfterEncodeEvent;
import com.sun.jsftemplating.layout.event.BeforeEncodeEvent;
import com.sun.jsftemplating.layout.event.EncodeEvent;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContextImpl;
import com.sun.jsftemplating.util.LayoutElementUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *  <p>	This class provides some common functionality between the various types
 *	of {@link LayoutElement}s.  It is the base class of most
 *	implementations (perhaps all).</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public abstract class LayoutElementBase implements LayoutElement {

    /**
     *	<p> Constructor.</p>
     *
     *	@param	parent	The parent LayoutElement
     *	@param	id	Identifier for this LayoutElement
     */
    protected LayoutElementBase(LayoutElement parent, String id) {
	setParent(parent);
	_id = id;
    }


    /**
     *	<p> This method is used to add a {@link LayoutElement}.
     *	    {@link LayoutElement}s should be added sequentially in the order
     *	    in which they are to be rendered.</p>
     *
     *	@param	element	The {@link LayoutElement} to add as a child.
     */
    public void addChildLayoutElement(LayoutElement element) {
	_layoutElements.add(element);
    }


    /**
     *	<p> This method returns the {@link LayoutElement}s as a
     *	    <code>List</code> of {@link LayoutElement}.</p>
     *
     *	@return List of {@link LayoutElement}s.
     */
    public List<LayoutElement> getChildLayoutElements() {
	return _layoutElements;
    }

    /**
     *	<p> This method returns the requested child {@link LayoutElement} by
     *	    <code>id</code>.</p>
     *
     *	@param	id  The <code>id</code> of the child to find and return.
     *
     *	@return	The requested {@link LayoutElement}; <code>null</code> if not
     *		found.
     */
    public LayoutElement getChildLayoutElement(String id) {
	Iterator<LayoutElement> it = getChildLayoutElements().iterator();
	LayoutElement elt = null;
	while (it.hasNext()) {
	    elt = it.next();
	    if (id.equals(elt.getUnevaluatedId())) {
		return elt;
	    }
	}
	return null;
    }

    /**
     *	<p> This method walks to the top-most {@link LayoutElement}, which
     *	    should be a {@link LayoutDefinition}.  If not, it will throw an
     *	    exception.</p>
     *
     *	@return	The {@link LayoutDefinition}.
     */
    public LayoutDefinition getLayoutDefinition() {
	// Find the top-most LayoutElement
	LayoutElement cur = this;
	while (cur.getParent() != null) {
	    cur = cur.getParent();
	}

	// This should be the LayoutDefinition, return it
	return (LayoutDefinition) cur;
    }


    /**
     *	<p> This method returns the parent {@link LayoutElement}.</p>
     *
     *	@return	parent LayoutElement
     */
    public LayoutElement getParent() {
	return _parent;
    }


    /**
     *	<p> This method sets the parent {@link LayoutElement}.</p>
     *
     *	@param	parent	Parent {@link LayoutElement}.
     */
    protected void setParent(LayoutElement parent) {
	_parent = parent;
    }


    /**
     *	<p> Accessor method for id.  This returns a non-null value, it may
     *	    return "" if id is not set or does not apply.</p>
     *
     *	<p> This method will also NOT resolve EL strings.</p>
     *
     *	@return a non-null id
     */
    private String getId() {
	if ((_id == null) || (_id == "")) {
	    // Ensure we ALWAYS have an id, however, generating ids is
	    // potentially dangerous because the results may not be the same
	    // always.  Effort has been taken to avoid most problems, though.
	    _id = LayoutElementUtil.getGeneratedId((String) null);
	}
	return _id;
    }

    /**
     *	<p> This method generally should not be used.  It does not resolve
     *	    expressions.  Instead use
     *	    {@link #getId(FacesContext, UIComponent)}.</p>
     *
     *	@return	The unevaluated id.
     */
    public String getUnevaluatedId() {
	return getId();
    }

    /**
     *	<p> Accessor method for id.  This returns a non-null value, it may
     *	    return "" if id is not set or does not apply.</p>
     *
     *	<p> This method will also attempt to resolve EL strings.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	parent	    The parent <code>UIComponent</code>.  This is used
     *			    because the current UIComponent is typically
     *			    unknown (or not even created yet).
     *
     *	@return A non-null id.
     */
    public String getId(FacesContext context, UIComponent parent) {
	// Evaluate the id...
	Object value = resolveValue(context, parent, getId());

	// Return the result
	return (value == null) ? "" : value.toString();
    }

    /**
     *	<p> This method will attempt to resolve EL strings in the given
     *	    value.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	parent	    The parent <code>UIComponent</code>.  This is used
     *			    because the current UIComponent is typically
     *			    unknown (or not even created yet).
     *	@param	value	    The String to resolve
     *
     *	@return The evaluated value (may be null).
     */
    public Object resolveValue(FacesContext context, UIComponent parent, Object value) {
	return ComponentUtil.resolveValue(context, this, parent, value);
    }

    /**
     *	<p> This method allows each LayoutElement to provide it's own encode
     *	    functionality.  If the {@link LayoutElement} should render its
     *	    children, this method should return true.  Otherwise, this method
     *	    should return false.</p>
     *
     *	@param	context	    The FacesContext
     *	@param	component   The UIComponent
     *
     *	@return	true if children are to be rendered, false otherwise.
     */
    protected abstract boolean encodeThis(FacesContext context, UIComponent component) throws IOException;

    /**
     *	<p> This is the base implementation for encode.  Typically each type of
     *	    LayoutElement wants to do something specific then conditionally have
     *	    its children rendered.  This method invokes the abstract method
     *	    "encodeThis" to do specific functionality, it the walks the children
     *	    and renders them, if encodeThis returns true.  It skips the children
     *	    if encodeThis returns false.</p>
     *
     *	<p> NOTE: Some subclasses override this method, be careful when
     *		  changing/adding to this code.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	component   The <code>UIComponent</code>
     */
    public void encode(FacesContext context, UIComponent component) throws IOException {
	// Invoke "before" handlers
	Object result = dispatchHandlers(context, BEFORE_ENCODE,
	    new BeforeEncodeEvent(component));

	if ((result != null) && (result.toString().equals("false"))) {
	    // Skip...
	    return;
	}

	// Do LayoutElement specific stuff...
	boolean renderChildren = encodeThis(context, component);

// FIXME: Consider buffering HTML and passing to "endDisplay" handlers...
// FIXME: Storing in the EventObject may be useful if we go this route.

	// Perhaps we want our own Response writer to buffer children?
	//ResponseWriter out = context.getResponseWriter();

	// Conditionally render children...
	if (renderChildren) {
	    result = dispatchHandlers(context, ENCODE,
		new EncodeEvent(component));

	    // Iterate over children
	    LayoutElement childElt = null;
	    Iterator<LayoutElement> it = getChildLayoutElements().iterator();
	    while (it.hasNext()) {
		childElt = it.next();
		childElt.encode(context, component);
	    }
	}

	// Invoke "after" handlers
	result = dispatchHandlers(context, AFTER_ENCODE,
	    new AfterEncodeEvent(component));
    }


    /**
     *	<p> This method iterates over the {@link Handler}s and executes each
     *	    one.  A {@link HandlerContext} will be created to pass to each
     *	    {@link Handler}.  The {@link HandlerContext} object is reused
     *	    across all {@link Handler}s that are invoked; the
     *	    {@link #setHandler(Handler)} method is invoked with the correct
     *	    {@link Handler} descriptor before the handler is executed.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	eventType   The event type which is being fired
     *	@param	event	    An optional <code>EventObject</code>
     *
     *	@return	By default, (null) is returned.  However, if any of the
     *		{@link Handler}s produce a non-null return value, the value
     *		from the last {@link Handler} to produces a non-null return
     *		value is returned.
     */
    public Object dispatchHandlers(FacesContext context, String eventType, EventObject event) {
	// Get the handlers for this eventType
	Object eventObj = event.getSource();
	if (!(eventObj instanceof UIComponent)) {
	    eventObj = null;
	}
	List<Handler> handlers = getHandlers(eventType, (UIComponent) eventObj);

	// Make sure we have something to do...
	if (handlers == null) {
	    return null;
	}

	// Create a HandlerContext
	HandlerContext handlerContext =
	    createHandlerContext(context, event, eventType);

	// This method is broken down so that recursion is easier
	return dispatchHandlers(handlerContext, handlers);
    }

    /**
     *	<p> As currently implemented, this method is essentially a utility
     *	    method.  It dispatches the given List of {@link Handler}s.
     *	    This may be available as a static method in the future.</p>
     */
    public Object dispatchHandlers(HandlerContext handlerCtx, List<Handler> handlers) {
	Object retVal = null;
	Object result = null;
	Handler handler = null;
	Iterator<Handler> it = handlers.iterator();
	while (it.hasNext()) {
	    try {
		// Get the Handler
		handler = it.next();
		handlerCtx.setHandler(handler);

		// Delegate to the Handler to perform invocation
		retVal = handler.invoke(handlerCtx);

		// Check for return value
		if (retVal != null) {
		    result = retVal;
		}
	    } catch (Exception ex) {
		throw new RuntimeException(
		    ex.getClass().getName() + " while attempting to "
		    + "process a '" + handlerCtx.getEventType()
		    + "' event for '" + getId() + "'.", ex);
	    }
	}

	// Return the return value (null by default)
	return result;
    }

    /**
     *	<p> This method is responsible for creating a new HandlerContext.  It
     *	    does not set the Handler descriptor.  This is done right before a
     *	    Handler is invoked.  This allows the HandlerContext object to be
     *	    reused.</p>
     *
     *	@param	context	    The FacesContext
     */
    protected HandlerContext createHandlerContext(FacesContext context, EventObject event, String eventType) {
	return new HandlerContextImpl(context, this, event, eventType);
    }

    /**
     *	<p> This method retrieves the {@link Handler}s for the requested type.</p>
     *
     *	@param	type	The type of {@link Handler}s to retrieve.
     *
     *	@return	A List of {@link Handler}s.
     */
    public List<Handler> getHandlers(String type) {
	return _handlersByType.get(type);
    }

    /**
     *	<p> This method provides access to the "handlersByType"
     *	    <code>Map</code>.</p>
     */
    public Map<String, List<Handler>> getHandlersByTypeMap() {
	return _handlersByType;
    }

    /**
     *	<p> This method provides a means to set the "handlersByType" Map.
     *	    Normally this is done for each type individually via
     *	    {@link #setHandlers(String, List)}.  This Map may not be null (null
     *	    will be ignored) and should contain entries that map to
     *	    <code>List</code>s of {@link Handler}s.
     */
    public void setHandlersByTypeMap(Map<String, List<Handler>> map) {
	if (map != null) {
	    _handlersByType = map;
	}
    }

    /**
     *	<p> This method retrieves the {@link Handler}s for the requested
     *	    type.</p>
     *
     *	@param	type	The type of <code>Handler</code>s to retrieve.
     *	@param	comp	The associated <code>UIComponent</code> (or null).
     *
     *	@return	A <code>List</code> of {@link Handler}s.
     */
    public List<Handler> getHandlers(String type, UIComponent comp) {
	// 1st get list of handlers for definition of this LayoutElement
	List<Handler> handlers = getHandlers(type);

	// NOTE: At this point, very few types should support "instance"
	// NOTE: handlers (LayoutComponent, LayoutDefinition, more??).  To
	// NOTE: support them, the future, the specific LayoutElement subclass
	// NOTE: will have to deal with this.  For example, LayoutComponent
	// NOTE: "instance" handlers are dealt with in LayoutComponent (it
	// NOTE: overrides this method).

	return handlers;
    }

    /**
     *	<p> This method associates 'type' with the given list of
     *	    {@link Handler}s.</p>
     *
     *	@param	type	    The String type for the List of {@link Handler}s
     *	@param	handlers    The List of {@link Handler}s
     */
    public void setHandlers(String type, List<Handler> handlers) {
	_handlersByType.put(type, handlers);
    }

    /**
     *	<p> This method is a convenience method for encoding the given
     *	    <code>UIComponent</code>.  It calls the appropriate encoding
     *	    methods on the component and calls itself recursively for all
     *	    <code>UIComponent</code> children that do not render their own
     *	    children.</p>
     *
     *	@param	context	    <code>FacesContext</code>
     *	@param	component   <code>UIComponent</code> to encode
     */
    public static void encodeChild(FacesContext context, UIComponent component) throws IOException {
	if (!component.isRendered()) {
	    return;
	}

	/******* REMOVE THIS IF TABLE IS EVER FIXED TO WORK RIGHT *******/
/*
 *	This code is removed b/c of the way the Table code is designed.  It
 *	needs to recalculate the clientId all the time.  Rather than deal with
 *	this in the table code, the design requires that every component
 *	regenerate its clientId every time it is rendered.  Hopefully the Table
 *	code will be rewritten to not require this, or to do this task itself.
 *
 *	For now, I will avoid doing the "right" thing and reset the id blindly.
 *	This causes the clientId to be erased and regenerated.
 */
	String id = component.getId();
	if (id != null) {
	    component.setId(id);
	}
	/******* REMOVE THIS IF TABLE IS EVER FIXED TO WORK RIGHT *******/

	component.encodeBegin(context);
	if (component.getRendersChildren()) {
	    component.encodeChildren(context);
	} else {
	    Iterator<UIComponent> it = component.getChildren().iterator();
	    while (it.hasNext()) {
		encodeChild(context, it.next());
	    }
	}
	component.encodeEnd(context);
    }


    /**
     *	List of child LayoutElements (if, facet, UIComponents, etc.)
     */
    private List<LayoutElement> _layoutElements = new ArrayList<LayoutElement>();

    /**
     *	The parent LayoutElement.  This will be null for the LayoutDefinition.
     */
    private LayoutElement _parent = null;

    /**
     *	<p> <code>Map</code> containing <code>List</code>s of
     *	    {@link Handler}s.</p>
     */
    private Map<String, List<Handler>> _handlersByType = new HashMap<String, List<Handler>>();

    /**
     *	This stores the id for the LayoutElement
     */
    private String  _id	    = null;

    /**
     *	<p> This is the "type" for handlers to be invoked after the encoding
     *	    of this element.</p>
     */
     public static final String AFTER_ENCODE =	"afterEncode";

    /**
     *	<p> This is the "type" for handlers to be invoked before the encoding
     *	    of this element.</p>
     */
     public static final String BEFORE_ENCODE =	"beforeEncode";

    /**
     *	<p> This is the "type" for handlers to be invoked during the encoding
     *	    of this element.  This occurs before any child LayoutElements are
     *	    invoked and only if child Elements are to be invoked.</p>
     */
     public static final String ENCODE =	"encode";
}
