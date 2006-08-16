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
import com.sun.jsftemplating.component.ChildManager;
import com.sun.jsftemplating.el.VariableResolver;
import com.sun.jsftemplating.layout.event.AfterCreateEvent;
import com.sun.jsftemplating.layout.event.AfterEncodeEvent;
import com.sun.jsftemplating.layout.event.BeforeCreateEvent;
import com.sun.jsftemplating.layout.event.BeforeEncodeEvent;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;


/**
 *  <p>	This class defines a <code>LayoutComponent</code>.  A
 *	<code>LayoutComponent</code> describes a <code>UIComponent</code> to be
 *	instantiated.  The method {@link #getType()} provides a
 *	{@link ComponentType} descriptor that is capable of providing a
 *	{@link com.sun.jsftemplating.component.factory.ComponentFactory}
 *	to perform the actual instantiation.  This class also stores properties
 *	and facets (children) to be set on a newly instantiated instance.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutComponent extends LayoutElementBase implements LayoutElement {

    /**
     *	<p> Constructor.</p>
     */
    public LayoutComponent(LayoutElement parent, String id, ComponentType type) {
	super(parent, id);
	_type = type;
    }

    /**
     *	<p> Accessor for type.</p>
     */
    public ComponentType getType() {
	return _type;
    }

    /**
     *	<p> Determines if this component should be created even if there is
     *	    already an existing <code>UIComponent</code>.  It will "overwrite"
     *	    the existing component if this property is true.</p>
     */
    public void setOverwrite(boolean value) {
	_overwrite = value;
    }

    /**
     *	<p> Determines if this component should be created even if there is
     *	    already an existing <code>UIComponent</code>.  It will "overwrite"
     *	    the existing component if this property is true.</p>
     */
    public boolean isOverwrite() {
	return _overwrite;
    }

    /**
     *	<p> This method adds an option to the LayoutComponent.  Options may be
     *	    useful in constructing the LayoutComponent.</p>
     *
     *	@param	name	The name of the option
     *	@param	value	The value of the option (may be List or String)
     */
    public void addOption(String name, Object value) {
	_options.put(name, value);
    }

    /**
     *	<p> This method adds all the options in the given Map to the
     *	    {@link LayoutComponent}.  Options may be useful in constructing the
     *	    {@link LayoutComponent}.</p>
     *
     *	@param	map	The map of options to add.
     */
    public void addOptions(Map<String, Object> map) {
	_options.putAll(map);
    }

    /**
     *	<p> Accessor method for an option.  This method does not evaluate
     *	    expressions.</p>
     *
     *	@param	name	The option name to retrieve.
     *
     *	@return	The option value (List or String), or null if not found.
     *
     *	@see #getEvaluatedOption(FacesContext, String, UIComponent)
     */
    public Object getOption(String name) {
	return _options.get(name);
    }

    /**
     *	<p> Accessor method for an option.  This method evaluates our own
     *	    expressions (not JSF expressions).</p>
     *
     *	@param	ctx	    The <code>FacesContext</code>.
     *	@param	name	    The option name to retrieve.
     *	@param	component   The <code>UIComponent</code> (may be null).
     *
     *	@return	The option value (List or String), or null if not found.
     *
     *	@see #getOption(String)
     */
    public Object getEvaluatedOption(FacesContext ctx, String name, UIComponent component) {
	// Get the option value
	Object value = getOption(name);

	// Invoke our own EL.  This is needed b/c JSF's EL is designed for
	// Bean getters only.  It does not get CONSTANTS or pull data from
	// other sources (such as session, request attributes, etc., etc.)
	// Resolve our variables now because we cannot depend on the
	// individual components to do this.  We may want to find a way to
	// make this work as a regular ValueExpression... but for
	// now, we'll just resolve it here.
	return VariableResolver.resolveVariables(ctx, this, component, value);
    }

    /**
     *	<p> This method returns true/false based on whether the given option
     *	    name has been set.</p>
     *
     *	@param	name	The option name to look for.
     *
     *	@return	true/false depending on whether the options exists.
     */
    public boolean containsOption(String name) {
	return _options.containsKey(name);
    }

    /**
     *	<p> This method sets the Map of options.</p>
     *
     *	@param	options	    <code>Map</code> of options.
     */
    public void setOptions(Map<String, Object> options) {
	_options = options;
    }

    /**
     *	<p> This method returns the options as a Map.  This method does not
     *	    evaluate expressions.</p>
     *
     *	@return Map of options.
     */
    public Map<String, Object> getOptions() {
	return _options;
    }

    /**
     *	<p> This method is overriden so that the correct UIComponent can be
     *	    passed into the events.  This is important so that correct
     *	    component is searched for "instance" handlers.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	parent	    The <code>UIComponent</code>.
     */
    public void encode(FacesContext context, UIComponent parent) throws IOException {
	if (!this.getClass().getName().equals(CLASS_NAME)) {
	    // The sub-classes of this component shouldn't use this method,
	    // this is a hack to allow them to use LayoutElementBase.encode
	    super.encode(context, parent);
	    return;
	}

	// If overwrite...
	if (isOverwrite()) {
	    String id = getId(context, parent);
	    if (parent.getFacets().remove(id) == null) {
		UIComponent child = ComponentUtil.findChild(parent, id, null);
		if (child != null) {
		    // Not a facet, try child...
		    parent.getChildren().remove(child);
		}
	    }
	}

	// Display this UIComponent
	// First find the UIComponent
	UIComponent childComponent = null;
	if (parent instanceof ChildManager) {
	    // If we have a ChildManager, take advantage of it...
	    childComponent = ((ChildManager) parent).getChild(context, this);
	} else {
	    // Use local util method for finding / creating child component...
	    childComponent = getChild(context, parent);
	}

	Object result = dispatchHandlers(context, BEFORE_ENCODE,
	    new BeforeEncodeEvent(childComponent));

	// Render the child UIComponent
	encodeChild(context, childComponent);

	// Invoke "after" handlers
	result = dispatchHandlers(context, AFTER_ENCODE,
	    new AfterEncodeEvent(childComponent));
    }

    /**
     *	<p> Although this method is part of the interface, it is not used b/c
     *	    I overrode the encode() method which calls this method.  This
     *	    method does nothing except satisfy the compiler.</p>
     */
    public boolean encodeThis(FacesContext context, UIComponent parent) throws IOException {
	return false;
    }

    /**
     *	<p> This method will find or create a <code>UIComponent</code> as
     *	    described by this <code>LayoutComponent</code> descriptor.  If the
     *	    component already exists as a child or facet, it will be returned.
     *	    If it creates a new <code>UIComponent</code>, it will typically be
     *	    added to the given parent <code>UIComponent</code> as a facet (this
     *	    actually depends on the factory that instantiates the
     *	    <code>UIComponent</code>).</p>
     *
     *	@param	context	The <code>FacesContext</code>
     *	@param	parent	The <code>UIComponent</code> to serve as the parent to
     *			search and to store the new <code>UIComponent</code>.
     *
     *	@return	The <code>UIComponent</code> requested (found or newly created)
     */
    public UIComponent getChild(FacesContext context, UIComponent parent) {
	UIComponent childComponent = null;

	// First pull off the id from the descriptor
	String id = this.getId(context, parent);

	// We have an id, use it to search for an already-created child
	childComponent = ComponentUtil.findChild(parent, id, id);
	if (childComponent != null) {
	    return childComponent;
	}

	// Invoke "beforeCreate" handlers
	this.beforeCreate(context, parent);

	// Create UIComponent
	childComponent =
	    ComponentUtil.createChildComponent(context, this, parent);

	// Invoke "afterCreate" handlers
	this.afterCreate(context, childComponent);

	// Return the newly created UIComponent
	return childComponent;
    }

    /**
     *	<p> This method retrieves the Handlers for the requested type.  But
     *	    also includes any handlers that are associated with the instance
     *	    (i.e. the UIComponent).</p>
     *
     *	@param	type	The type of <code>Handler</code>s to retrieve.
     *	@param	comp	The associated <code>UIComponent</code> (or null).
     *
     *	@return	A List of Handlers.
     */
    public List<Handler> getHandlers(String type, UIComponent comp) {
	// 1st get list of handlers for definition of this LayoutElement
	List<Handler> handlers = null;

	// Now check to see if there are any on the UIComponent
	if (comp != null) {
	    List<Handler> instHandlers =
		    (List<Handler>) comp.getAttributes().get(type);
	    if ((instHandlers != null) && (instHandlers.size() > 0)) {
		// NOTE: Copy b/c this is <i>instance</i> + static
		// Add the UIComponent instance handlers
		handlers = new ArrayList<Handler>(instHandlers);

		List<Handler> defHandlers = getHandlers(type);
		if (defHandlers != null) {
		    // Add the LayoutElement "definition" handlers, if any
		    handlers.addAll(getHandlers(type));
		}
	    }
	}
	if (handlers == null) {
	    handlers = getHandlers(type);
	}

	return handlers;
    }

    /**
     *	<p> This method is invoked before the Component described by this
     *	    LayoutComponent is created.  This allows handlers registered for
     *	    "beforeCreate" functionality to be invoked.</p>
     *
     *	@param	context	The FacesContext
     *
     *	@return	The result of invoking the handlers (null by default)
     */
    public Object beforeCreate(FacesContext context, UIComponent parent) {
	// Invoke "beforeCreate" handlers
	return dispatchHandlers(
		context, BEFORE_CREATE, new BeforeCreateEvent(parent));
    }

    /**
     *	<p> This method is invoked after the Component described by this
     *	    LayoutComponent is created.  This allows handlers registered for
     *	    "afterCreate" functionality to be invoked.</p>
     *
     *	@param	context	The FacesContext
     *
     *	@return	The result of invoking the handlers (null by default)
     */
    public Object afterCreate(FacesContext context, UIComponent component) {
	// Invoke "afterCreate" handlers
	return dispatchHandlers(
		context, AFTER_CREATE, new AfterCreateEvent(component));
    }

    /**
     *	<p> This method returns true if the child should be added to the parent
     *	    component as a facet.  Otherwise, it returns false indicating that
     *	    it should exist as a real child.  The default is true.</p>
     *
     *	@return	True if the child UIComponent should be added as a facet.
     */
    public boolean isFacetChild() {
	return _isFacetChild;
    }

    /**
     *	<p> This method sets whether the child <code>UIComponent</code> should
     *	    be set as a facet or a real child.</p>
     *
     *	@param	facetChild  True if the child <code>UIComponent</code> should
     *			    be added as a facet.
     */
    public void setFacetChild(boolean facetChild) {
	_isFacetChild = facetChild;
    }

    /**
     *	<p> This method returns a flag that indicates if this
     *	    <code>LayoutComponent</code> is nested (directly or indirectly)
     *	    inside another <code>LayoutComponent</code>.  This flag is used
     *	    for such purposes as deciding if "instance" handlers are
     *	    appropriate.</p>
     *
     *	@return    <code>true</code> if component is nested.
     */
    public boolean isNested() {
	return _nested;
    }

    /**
     *	<p> This method sets the nested flag for this
     *	    <code>LayoutComponent</code>.  This method is commonly only called
     *	    from code that constructs the tree of {@link LayoutElement}
     *	    components.</p>
     *
     *	@param	value	The boolean value.
     */
    public void setNested(boolean value) {
	_nested = value;
    }


    /**
     *	<p> Component type</p>
     */
    private ComponentType _type	= null;

    /**
     *	<p> Determines if this component should be created even if there is
     *	    already an existing <code>UIComponent</code>.  It will "overwrite"
     *	    the existing component if this property is true.  Usually only
     *	    applies when this is used within the context of a
     *	    <code>Renderer</code>.</p>
     */
    private boolean _overwrite	= false;

    /**
     *	<p> Map of options.</p>
     */
    private Map<String, Object>	_options    = new HashMap<String, Object>();

    /**
     *
     */
    private boolean _isFacetChild = true;

    /**
     *	<p> This is the "type" for handlers to be invoked to handle
     *	    "afterCreate" functionality for this element.</p>
     */
    public static final String AFTER_CREATE =	"afterCreate";

    /**
     *	<p> This is the "type" for handlers to be invoked to handle
     *	    "beforeCreate" functionality for this element.</p>
     */
    public static final String BEFORE_CREATE =	"beforeCreate";

    /**
     *	<p> This is the "type" for handlers to be invoked to handle
     *	    "command" functionality for this element.</p>
     */
    public static final String COMMAND =	"command";

    /**
     *	<p> This defines the property key for specifying the facet name in
     *	    which the component should be stored under in its parent
     *	    UIComponent.</p>
     */
    public static final String FACET_NAME   = "_facetName";

    public static final String CLASS_NAME  = LayoutComponent.class.getName();

    /**
     *	<p> The value of the nested property.</p>
     */
    private boolean _nested = false;
}
