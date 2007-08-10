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
package com.sun.jsftemplating.component.factory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.event.CommandActionListener;
import com.sun.jsftemplating.util.LogUtil;
import com.sun.jsftemplating.util.TypeConverter;


/**
 *  <p>	This abstract class provides common functionality for UIComponent
 *	factories.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public abstract class ComponentFactoryBase implements ComponentFactory {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>UIComponent</code>.
     */
    public abstract UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent);

    /**
     *	<p> This method iterates through the Map of options.  It looks at each
     *	    one, if it contians an EL expression, it sets a value binding.
     *	    Otherwise, it calls setAttribute() on the component (which in turn
     *	    will invoke the bean setter if there is one).</p>
     *
     *	<p> This method also interates through the child
     *	    <code>LayoutElement</code>s of the given {@link LayoutComponent}
     *	    descriptor and adds Facets or children as appropriate.</p>
     *
     *	@param	context	The <code>FacesContext</code>
     *	@param	desc    The {@link LayoutComponent} descriptor associated with
     *			the requested <code>UIComponent</code>.
     *	@param	comp	The <code>UIComponent</code>
     */
    protected void setOptions(FacesContext context, LayoutComponent desc, UIComponent comp) {
	if (desc == null) {
	    // Nothing to do
	    return;
	}

	// First set the id if supplied, treated special b/c the component
	// used for ${} expressions is the parent and this must be set first
	// so other ${} expressions can use $this{id} and $this{clientId}.
	String compId = (String) desc.getId(context, comp.getParent());
	if ((compId != null) && (!compId.equals(""))) {
	    comp.setId(compId);
	}

	// Loop through all the options and set the values
// FIXME: Figure a way to skip options that should not be set on the Component
	Iterator<String> it = desc.getOptions().keySet().iterator();
	String key = null;
	while (it.hasNext()) {
	    // Get next property
	    key = it.next();

	    setOption(context, comp, key,
		desc.getEvaluatedOption(context, key, comp));
	}

	// Check for "Command" handlers...
	List<Handler> handlers = desc.getHandlers(LayoutComponent.COMMAND);
	if ((handlers != null) && (comp instanceof ActionSource)) {
	    ((ActionSource) comp).addActionListener(
		CommandActionListener.getInstance());
	}

	// Set the events on the new component
	storeInstanceHandlers(desc, comp);
    }

    /**
     *	<p> This method sets an individual option on the
     *	    <code>UIComponent</code>.  It will check to see if it is a
     *	    <code>ValueExpression</code>, if it is it will store it as
     *	    such.</p>
     */
    protected void setOption(FacesContext context, UIComponent comp, String key, Object value) {
	// Next check to see if the value contains a ValueExpression
	if ((value instanceof String)
		&& (ComponentUtil.isValueReference((String) value))) {
	    ValueExpression ve =
		context.getApplication().getExpressionFactory().
		    createValueExpression(
			    context.getELContext(), (String) value, Object.class);
	    comp.setValueExpression((String) key, ve);
	} else {
	    // In JSF, you must directly modify the attribute Map
	    Map<String, Object> attributes = comp.getAttributes();
	    if (value == null) {
		// Setting null, assume they want to remove the value
		try {
		    attributes.remove(key);
		} catch (IllegalArgumentException ex) {
		    // JSF is mesed up... it throws an exception if it has a
		    // property descriptor and you call remove(...).  It also
		    // throws an exception if you attempt to call put w/ null
		    // and there is no property descriptor.  Either way you
		    // MUST catch something and then handle the other case.
		    try {
			attributes.put(key, (Object) null);
		    } catch (IllegalArgumentException iae) {
			// We'll make this non-fatal, but log a message
			if (LogUtil.infoEnabled()) {
			    LogUtil.info("JSFT0006", new Object[] {
				key, comp.getId(), comp.getClass().getName()});
			    if (LogUtil.fineEnabled()) {
				LogUtil.fine("Unable to set (" + key + ").", iae);
			    }
			}
		    }
		}
	    } else {
		try {
		    // Attempt to set the value as given...
		    attributes.put(key, value);
		} catch (IllegalArgumentException ex) {
		    // Ok, try a little harder...
		    Class type = findPropertyType(comp, key);
		    if (type != null) {
			try {
			    attributes.put(key, TypeConverter.asType(type, value));
			} catch (IllegalArgumentException ex2) {
			    throw new IllegalArgumentException(
				"Failed to set property (" + key + ") with "
				+ "value (" + value + "), which is of type ("
				+ value.getClass().getName() + ").  Expected "
				+ "type (" + type.getName() + ").  This "
				+ "occured on the component named ("
				+ comp.getId() + ") of type ("
				+ comp.getClass().getName() + ").", ex2);
			}
		    } else {
			throw new IllegalArgumentException(
			    "Failed to set property (" + key + ") with value ("
			    + value + "), which is of type ("
			    + value.getClass().getName() + ").  This occured "
			    + "on the component named (" + comp.getId()
			    + ") of type (" + comp.getClass().getName()
			    + ").", ex);
		    }
		}
	    }
	}
    }

    /**
     *	<p> This method attempts to resolve the expected type for the given
     *	    property <code>key</code> and <code>UIComponent</code>.</p>
     */
    private static Class findPropertyType(UIComponent comp, String key) {
	// First check to see if we've done this before...
	Class compClass = comp.getClass();
	String cacheKey = compClass.getName() + ';' + key;
	if (_typeCache.containsKey(cacheKey)) {
	    // May return null if method previously executed unsuccessfully
	    return _typeCache.get(cacheKey);
	}

	// Search a little...
	Class val = null;
	Method meth = null;
	String methodName = getGetterName(key);
	try {
	    meth = compClass.getMethod(methodName);
	} catch (NoSuchMethodException ex) {
	    // May fail if we have a boolean property that has an "is" getter.
	    try { 
		// Try again, replace "get" with "is"
		meth = compClass.getMethod(
		    "is" + methodName.substring(3));
	    } catch (NoSuchMethodException ex2) {
		// Still not found, must not have getter / setter
		ex2.printStackTrace();
	    }
	}
	if (meth != null) {
	    val = meth.getReturnType();
	} else {
	    Object obj = comp.getAttributes().get("key");
	    if (val != null) {
		val = obj.getClass();
	    }
	}

	// Save the value for future calls for the same information
	// NOTE: We do it this way to avoid modifying a shared Map
	Map<String, Class> newTypeCache =
		new HashMap<String, Class>(_typeCache);
	newTypeCache.put(cacheKey, val);
	_typeCache = newTypeCache;

	// Return the result
	return val;
    }

    /**
     *	<p> This method converts the given <code>name</code> to a bean getter
     *	    method name.  In other words, it capitalizes the first letter and
     *	    prepends "get".</p>
     */
    private static String getGetterName(String name) {
	return "get" + ((char) (name.charAt(0) & 0xFFDF)) + name.substring(1);
    }

    /**
     *	<p> This method is responsible for interating over the "instance"
     *	    handlers and applying them to the UIComponent.  An "instance"
     *	    handler is one that is defined <b>outside a renderer</b>, or <b>a
     *	    nested component within a renderer</b>.  In other words, a handler
     *	    that would not get fired by the TemplateRenderer.  By passing this
     *	    in via the UIComponent, code that is aware of events (see
     *	    {@link com.sun.jsftemplating.layout.descriptors.LayoutElementBase})
     *	    may find these events and fire them.  These may vary per "instance"
     *	    of a particular component (i.e. <code>TreeNode</code>) unlike the
     *	    handlers defined in a TemplateRender's XML (which are shared and
     *	    therefor should not change dynamically).</p>
     *
     *	<p> This method is invoked from setOptions(), however, if setOptions
     *	    is not used in by a factory, this method may be invoked directly.
     *	    Calling this method multiple times will not cause any harm,
     *	    besides making an extra unnecessary call.</p>
     *
     *	@param	desc	The descriptor potentially containing handlers to copy.
     *	@param	comp	The UIComponent instance to store the handlers.
     */
    protected void storeInstanceHandlers(LayoutComponent desc, UIComponent comp) {
	if (!desc.isNested()) {
	    // This is not a nested LayoutComponent, it does should not store
	    // instance handlers
	    return;
	}

	// Iterate over the instance handlers
	Iterator<String> it = desc.getHandlersByTypeMap().keySet().iterator();
	if (it.hasNext()) {
	    String eventType = null;
	    Map<String, Object> compAttrs = comp.getAttributes();
	    while (it.hasNext()) {
		// Assign instance handlers to attribute for retrieval later
		//   (NOTE: retrieval must be explicit, see LayoutElementBase)
		eventType = it.next();
		if (eventType.equals(LayoutComponent.BEFORE_CREATE)) {
		    // This is handled directly, no need for instance handler
		    continue;
		} else if (eventType.equals(LayoutComponent.AFTER_CREATE)) {
		    // This is handled directly, no need for instance handler
		    continue;
		}
		compAttrs.put(eventType, desc.getHandlers(eventType));
	    }
	}
    }

    /**
     *	<p> This method associates the given child with the given parent.  By
     *	    using this method we centralize the code so that if we decide
     *	    later to add it as a real child it can be done in one place.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *	@param	child	    The child <code>UIComponent</code>
     */
    protected void addChild(FacesContext context, LayoutComponent descriptor, UIComponent parent, UIComponent child) {
	// Check to see if we should add this as a facet.  NOTE: We add
	// UIViewRoot children as facets b/c we render them via the
	// LayoutElement tree.
	String facetName = descriptor.getFacetName(parent);
	if (facetName != null) {
	    // Add child as a facet...
	    if (LogUtil.configEnabled() && facetName.equals("_noname")) {
		// Warn the developer that they may have a problem
		LogUtil.config("Warning: no id was supplied for "
			+ "component '" + child + "'!");
	    }
	    // Resolve the id if its dynamic
	    facetName = (String) ComponentUtil.resolveValue(
		    context, descriptor, child, facetName);
	    parent.getFacets().put(facetName, child);
	} else {
	    // Add this as an actual child
	    parent.getChildren().add(child);
	}
    }

    /**
     *	<p> This method instantiates the <code>UIComponent</code> given its
     *	    <code>ComponentType</code>.  It will respect the
     *	    <code>binding</code> property so that a <code>UIComponent</code>
     *	    can be created via the <code>binding</code> property.  While a
     *	    custom {@link ComponentFactory} can do a better job, at times it
     *	    may be desirable to use <code>binding</code> instead.</p>
     */
    protected UIComponent createComponent(FacesContext ctx, String componentType, LayoutComponent desc, UIComponent parent) {
	UIComponent comp = null;

	// Check for the "binding" property
	String binding = null;
	if (desc != null) {
	    binding =
		(String) desc.getEvaluatedOption(ctx, "binding", parent);
	}
	if ((binding != null) && ComponentUtil.isValueReference(binding)) {
	    // Create a ValueExpression
	    ValueExpression ve =
		ctx.getApplication().getExpressionFactory().
		    createValueExpression(
			    ctx.getELContext(), binding, UIComponent.class);
	    // Create / get the UIComponent
	    comp = ctx.getApplication().createComponent(
		    ve, ctx, componentType);
	} else {
	    // No binding, do the normal way...
	    comp = ctx.getApplication().createComponent(componentType);
	}

	// Parent the new component
	if (parent != null) {
	    addChild(ctx, desc, parent, comp);
	}

	// Return it...
	return comp;
    }

    /**
     *	<p> This method returns the extraInfo that was set for this
     *	    <code>ComponentFactory</code> from the
     *	    {@link com.sun.jsftemplating.layout.descriptors.ComponentType}.</p>
     */
    public Serializable getExtraInfo() {
	return _extraInfo;
    }

    /**
     *	<p> This method is invoked from the
     *	    {@link com.sun.jsftemplating.layout.descriptors.ComponentType} to
     *	    provide more information to the factory.  For example, if the JSF
     *	    component type was passed in, a single factory class could
     *	    instatiate multiple components the extra info that is passed in.</p>
     *
     *	<p> Some factory implementations may want to override this method to
     *	    execute intialization code for the factory based in the value
     *	    passed in.</p>
     */
    public void setExtraInfo(Serializable extraInfo) {
	_extraInfo = extraInfo;
    }

    /**
     *	<p> Extra information associated with this ComponentFactory.</p>
     */
    private Serializable _extraInfo = null;

    private static Map<String, Class> _typeCache =
	    new HashMap<String, Class>();
}
