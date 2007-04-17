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

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.LayoutViewHandler;
import com.sun.jsftemplating.layout.LayoutViewRoot;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;
import com.sun.jsftemplating.util.LayoutElementUtil;


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
    @Handler(id="getUIComponentChildren",
	input={
	    @HandlerInput(name="parent", type=UIComponent.class, required=true)},
	output={
	    @HandlerOutput(name="children", type=List.class),
	    @HandlerOutput(name="size", type=Integer.class)})
    public static void getChildren(HandlerContext context) {
	UIComponent parent = (UIComponent) context.getInputValue("parent");
	List<UIComponent> list = parent.getChildren();
	context.setOutputValue("children", list);
	context.setOutputValue("size", new Integer(list.size()));
    }

    /**
     *	<p> This handler replaces the given <code>old UIComponent</code> in the
     *	    <code>UIComponent</code> tree with the given <code>new
     *	    UIComponent</code>.  If the new <code>UIComponent</code> is not
     *	    specified or is <code>null</code>, the old UIComponent will simply
     *	    be removed.</p>
     *
     *	<p> Input value: "old" -- Type: <code>UIComponent</code></p>
     *	<p> Input value: "new" -- Type: <code>UIComponent</code></p>
     *
     *	@param	context	The <code>HandlerContext</code>.
     */
    @Handler(id="replaceUIComponent",
	input={
	    @HandlerInput(name="old", type=UIComponent.class, required=true),
	    @HandlerInput(name="new", type=UIComponent.class, required=false)}
	)
    public static void replaceUIComponent(HandlerContext context) {
	// Get the old component which is to be replaced
	UIComponent oldComp = (UIComponent) context.getInputValue("old");
	if (oldComp == null) {
	    throw new IllegalArgumentException(
		    "You must provide a non-null value for 'component'.");
	}

	// Check for a replacement UIComponent
	UIComponent newComp = (UIComponent) context.getInputValue("new");

	// Get the child UIComponent list...
	List<UIComponent> list = oldComp.getParent().getChildren();
	if (newComp == null) {
	    // Nothing to replace it with, just do a remove...
	    list.remove(oldComp);
	} else {
	    // Find the index to put the new UIComponent in the right place
	    int index = list.indexOf(oldComp);
	    list.set(index, newComp);
	}
    }

    /**
     *	<p> This will build a <code>UIComponent</code> tree from a
     *	    {@link LayoutElement}.  You must pass in the {@link LayoutElement}
     *	    that will be used to create the <code>UIComponent</code> tree.
     *	    You may optionally pass in the <code>parent UIComponent</code>
     *	    which will serve as the parent for the newly created
     *	    <code>UIComponent</code> tree.  The resulting
     *	    <code>UIComponent</code> tree will be returned via the
     *	    <code>result</code> output value.  If more than 1 root node exists
     *	    for the given <code>LayoutElement</code>, the last added to the
     *	    <code>parent</code> will be returned.  Typically, you will pass in
     *	    a {@link LayoutComponent} as the <code>layoutElement</code> so
     *	    there will only be 1.</p>
     *
     *	<p> It is recommended that you *do* supply the parent since EL
     *	    expressions may depend on this when creating the
     *	    <code>UIComponent</code> tree.</p>
     *
     *	<p> One possible use case for calling this method would be to have a
     *	    dynamic "id" property of a {@link LayoutComponent}, call this
     *	    method multiple times with different values set in the "id"
     *	    property.  Remember, that you should not change a
     *	    {@link LayoutComponent} (or any {@link LayoutElement}) directly.
     *	    It is only safe to have dynamic values through EL bindings #{}.</p>
     *
     *	<p> Another reason to use this handler is to cause a portion of a
     *	    <code>UIComponent</code> tree to be recreated.  This of often
     *	    desirable during Ajax requests so that factory options can be
     *	    reevaluated.</p>
     *
     *	<p> Input value: "layoutElement" -- Type: <code>LayoutElement</code></p>
     *	<p> Input value: "parent"	 -- Type: <code>UIComponent</code></p>
     *
     *	<p> Output value: "result"     -- Type: <code>UIComponent</code></p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="buildUIComponentTree",
	input={
	    @HandlerInput(name="layoutElement", type=LayoutElement.class, required=true),
	    @HandlerInput(name="parent", type=UIComponent.class, required=false)},
	output={
	    @HandlerOutput(name="result", type=UIComponent.class)}
	)
    public static void buildUIComponentTree(HandlerContext context) {
	LayoutElement desc = (LayoutElement) context.getInputValue("layoutElement");
	UIComponent parent = (UIComponent) context.getInputValue("parent");

	// If they didn't give us a parent, make one one up...
	if (parent == null) {
	    parent = new LayoutViewRoot();
	    ((LayoutViewRoot) parent).setViewId("fake");
	}

	// Build it...
	FacesContext facesCtx = context.getFacesContext();
	if (desc instanceof LayoutComponent) {
	    // Special case if LayoutComponent is the root node element
	    // The LayoutViewHandler assumes that the root node is not used
	    // because it was written assuming that the LayoutDefinition was
	    // always going to be passed in.  The result is that it ignores
	    // the LayoutElement that is passed in and goes striaight to the
	    // children.  This isn't what we want.  Process the child here,
	    // then continue as normal.
	    UIComponent tmpParent =
		((LayoutComponent) desc).getChild(facesCtx, parent);
	    LayoutViewHandler.buildUIComponentTree(facesCtx, tmpParent, desc);
	} else {
	    // Process normally (which in this path is probably not normal)
	    LayoutViewHandler.buildUIComponentTree(facesCtx, parent, desc);
	}

	// Get the result to return...
	String id = desc.getId(facesCtx, parent);
	UIComponent result = parent.findComponent(id);
	if (result == null) {
	    // First see if we can find it in the facet Map
	    if (desc instanceof LayoutComponent) {
		result = parent.getFacets().get(id);
	    } else {
// FIXME: find a way to find a "facet child" if the root LayoutElement is not a LayoutComponent
	    }

	    if (result == null) {
		// Still not found, check children...
		List<UIComponent> children = parent.getChildren();
		if (children.size() > 0) {
		    // Return the last child added b/c we want to make sure
		    // we're returning something that we added, not something
		    // that already existed.  While not perfect, this is
		    // reasonable.
		    result = children.get(children.size() -1);
		}
	    }
	}

	// Set the output...
	context.setOutputValue("result", result);
    }

    /**
     *	<p> This handler creates a <code>UIComponent</code>.  It requires you
     *	    to pass in the componentType (<code>type</code>) and returns the
     *	    new component via the output parameter <code>component</code>.</p>
     *
     *	<p> Input value: "type"	      -- Type: <code>String</code></p>
     *	<p> Input value: "parent"     -- Type: <code>UIComponent</code></p>
     *
     *	<p> Output value: "component" -- Type: <code>UIComponent</code></p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="createComponent",
	input={
	    @HandlerInput(name="type", type=String.class, required=true),
	    @HandlerInput(name="id", type=String.class, required=false),
	    @HandlerInput(name="parent", type=UIComponent.class, required=false)},
	output={
	    @HandlerOutput(name="component", type=UIComponent.class)}
	)
    public static void createComponent(HandlerContext context) {
	// Get the input...
	String type = (String) context.getInputValue("type");
	UIComponent parent = (UIComponent) context.getInputValue("parent");
	String id = (String) context.getInputValue("id");
	if (id == null) {
	    id = LayoutElementUtil.getGeneratedId(type);
	}

	// Create a LayoutComponent...
	LayoutComponent desc = new LayoutComponent((LayoutComponent) null, id,
		LayoutDefinitionManager.getGlobalComponentType(type));
	desc.setFacetChild(false);

	// Create the component...
	UIComponent component = ComponentUtil.createChildComponent(
		context.getFacesContext(), desc, parent);

	// Return the result...
	context.setOutputValue("component", component);
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
    @Handler(id="setUIComponentProperty",
	input={
	    @HandlerInput(name="component", type=UIComponent.class, required=true),
	    @HandlerInput(name="property", type=String.class, required=true),
	    @HandlerInput(name="value")})
    public static void setComponentProperty(HandlerContext context) {
	UIComponent component =
	    (UIComponent) context.getInputValue("component");
	String propName = (String) context.getInputValue("property");
	Object value = context.getInputValue("value");

	// Set the attribute or property value
	component.getAttributes().put(propName, value);
    }

    /**
     *	<p> This handler finds the requested <code>UIComponent</code> by
     *	    <code>clientId</code>.  It takes <code>clientId</code> as an input
     *	    parameter, and returns <code>component</code> as an output
     *	    parameter.</p>
     */
    @Handler(id="getUIComponent",
	input={
	    @HandlerInput(name="clientId", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="component", type=UIComponent.class)})
    public static void getUIComponent(HandlerContext context) {
	UIComponent viewRoot = context.getFacesContext().getViewRoot();
	String clientId = (String) context.getInputValue("clientId");
	context.setOutputValue("component", viewRoot.findComponent(clientId));
    }

    /**
     *	<p> This handler retrieves a property from the given
     *	    <code>UIComponent</code>.  It expects <code>component</code> and
     *	    <code>name</code> as an input parameters, and returns
     *	    <code>value</code> as an output parameter containing the value of
     *	    the property.</p>
     */
    @Handler(id="getUIComponentProperty",
	input={
	    @HandlerInput(name="component", type=UIComponent.class, required=true),
	    @HandlerInput(name="name", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Object.class)})
    public static void getUIComponentProperty(HandlerContext context) {
	UIComponent comp = (UIComponent) context.getInputValue("component");
	String name = (String) context.getInputValue("name");
	if ((comp == null) || (name == null)) {
	    throw new IllegalArgumentException("This Handler requires non-null"
		    + " values for 'component' and 'name'.  'component' was"
		    + " specified as '"
		    + context.getHandler().getInputValue("component")
		    + "' and evaluated to '" + comp + "'. 'name' was"
		    + " specified as '"
		    + context.getHandler().getInputValue("name")
		    + "' and evaluated to '" + name + "'.");
	}
	Object value = comp.getAttributes().get(name);
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler will print out the structure of a
     *	    <code>UIComponent</code> tree from the given UIComponent.</p>
     */
    @Handler(id="dumpUIComponentTree",
	input={
	    @HandlerInput(name="component", type=UIComponent.class, required=false)},
	output={
	    @HandlerOutput(name="value", type=String.class)})
    public static void dumpUIComponentTree(HandlerContext context) {
// FIXME: Add flag to dump attributes also, perhaps facets should be optional as well?
	// Find the root UIComponent to use...
	UIComponent comp = (UIComponent) context.getInputValue("component");
	if (comp == null) {
	    Object eventObject = context.getEventObject();
	    if (eventObject instanceof UIComponent) {
		comp = (UIComponent) eventObject;
	    } else {
		comp = context.getFacesContext().getViewRoot();
		if (comp == null) {
		    throw new IllegalArgumentException(
			    "Unable to determine UIComponent to dump!");
		}
	    }
	}

	// Create the buffer and populate it...
	StringBuffer buf = new StringBuffer("UIComponent Tree:\n");
	dumpTree(comp, buf, "    ");

	context.setOutputValue("value", buf.toString());
    }

    /**
     *	<p> This method recurses through the <code>UIComponent</code> tree to
     *	    generate a String representation of its structure.</p>
     */
    private static void dumpTree(UIComponent comp, StringBuffer buf, String indent) {
	// First add the current UIComponent
	buf.append(indent + comp.getId() + " (" + comp.getClass().getName() + ")\n");

	// Children...
	Iterator<UIComponent> it = comp.getChildren().iterator();
	if (it.hasNext()) {
	    buf.append(indent + "  Children:\n");
	    while (it.hasNext()) {
		dumpTree(it.next(), buf, indent + "    ");
	    }
	}

	// Facets...
	it = comp.getFacets().values().iterator();
	if (it.hasNext()) {
	    buf.append(indent + "  Facets:\n");
	    while (it.hasNext()) {
		dumpTree(it.next(), buf, indent + "    ");
	    }
	}
    }
}
