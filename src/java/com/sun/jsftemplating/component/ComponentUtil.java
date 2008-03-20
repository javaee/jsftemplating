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
 * ComponentUtil.java
 *
 * Created on November 24, 2004, 3:06 PM
 */
package com.sun.jsftemplating.component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;

import com.sun.jsftemplating.el.VariableResolver;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.util.LogUtil;
import com.sun.jsftemplating.util.TypeConverter;


/**
 *  <p>	Utility class that contains helper methods for components.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class ComponentUtil {

    /**
     *	<p> This constructor is here to prevent this class from being
     *	    instantiated.  It only contains static methods.</p>
     */
    private ComponentUtil() {
    }

    /**
     *	<p> Return a child with the specified component id from the specified
     *	    component. If not found, return <code>null</code>.</p>
     *
     *	<p> This method will NOT create a new <code>UIComponent</code>.</p>
     *
     *	@param	parent	<code>UIComponent</code> to be searched
     *	@param	id	Component id (or facet name) to search for
     *
     *	@return	The child <code>UIComponent</code> if it exists, null otherwise.
     */
    public static UIComponent getChild(UIComponent parent, String id) {
	return findChild(parent, id, id);
    }

    /**
     *	<p> Return a child with the specified component id (or facetName) from
     *	    the specified component. If not found, return <code>null</code>.
     *	    <code>facetName</code> or <code>id</code> may be null to avoid
     *	    searching the facet Map or the <code>parent</code>'s children.</p>
     *
     *	<p> This method will NOT create a new <code>UIComponent</code>.</p>
     *
     *	@param	parent	    <code>UIComponent</code> to be searched
     *	@param	id	    id to search for
     *	@param	facetName   Facet name to search for
     *
     *	@return	The child <code>UIComponent</code> if it exists, null otherwise.
     */
    public static UIComponent findChild(UIComponent parent, String id, String facetName) {
	// Sanity Check
	if (parent == null) {
	    return null;
	}

	// First search for facet
	UIComponent child = null;
	if (facetName != null) {
	    child = parent.getFacets().get(facetName);
	    if (child != null) {
		return child;
	    }
	}

	// Search for component by id
	if (id != null) {
	    Iterator<UIComponent> it = parent.getChildren().iterator();
	    while (it.hasNext()) {
		child = it.next();
		if (id.equals(child.getId())) {
		    return (child);
		}
	    }
	}

	// Not found, return null
	return null;
    }

    /**
     *	<p> This method finds or creates a child <code>UIComponent</code>
     *	    identified by the given id.  If the child is not found, it will
     *	    attempt to create it using the provided
     *	    {@link com.sun.jsftemplating.component.factory.ComponentFactory}
     *	    (<code>factoryClass</code>).</p>
     *
     *	<p> If there are <code>Properties</code> to be set on the UIComponent,
     *	    this method should generally be avoided.  It is preferable to use
     *	    the {@link #getChild(UIComponent, String, String, Properties)}
     *	    form of <code>getChild</code>.</p>
     *
     *	<p> <code>
     *		// Example (no properties):<br>
     *		UIComponent child = Util.getChild(component, "jklLabel", "com.sun.jsftemplating.component.factory.basic.LabelFactory");<br>
     *		((Label)child).setText("JKL Label:");<br>
     *		((Label)child).setFor("jkl");<br>
     *		<br>
     *		{@link LayoutComponent#encodeChild(FacesContext, UIComponent) LayoutComponent.encodeChild}(context, child);
     *	    </code></p>
     *
     *	@param	parent		Parent <code>UIComponent</code>
     *	@param	id		Identifier for child <code>UIComponent</code>
     *	@param	factoryClass	Full {@link com.sun.jsftemplating.component.factory.ComponentFactory} class name
     *
     *	@return	The child UIComponent that was found or created.
     *
     *	@see	#getChild(UIComponent, String, String, Properties)
     */
    public static UIComponent getChild(UIComponent parent, String id, String factoryClass) {
	return getChild(parent, id, factoryClass, id);
    }

    /**
     *	<p> Same as {@link #getChild(UIComponent, String, String)} except that
     *	    it allows you to specify a facetName different than the id.  If
     *	    null is supplied, it won't save the component as a facet.</p>
     *
     *	@param	parent		Parent <code>UIComponent</code>
     *	@param	id		Identifier for the child <code>UIComponent</code>
     *	@param	factoryClass	Full {@link com.sun.jsftemplating.component.factory.ComponentFactory} class name
     *	@param	facetName	The facet name (null means don't store it)
     *
     *	@return	The child UIComponent that was found or created.
     *
     *	@see	#getChild(UIComponent, String, String)
     */
    public static UIComponent getChild(UIComponent parent, String id, String factoryClass, String facetName) {
	return getChild(parent, id, getComponentType(factoryClass),
		null, facetName);
    }

    /**
     *	<p> This method finds or creates a child <code>UIComponent</code>
     *	    identified by the given id.  If the child is not found, it will
     *	    attempt to create it using the provided
     *	    {@link com.sun.jsftemplating.component.factory.ComponentFactory}
     *	    (<code>factoryClass</code>).  It will also initialize the
     *	    <code>UIComponent</code> using the provided set of
     *	    <code>Properties</code>.</p>
     *
     *	<p> <code>
     *		// Example (with properties):<br>
     *		Properties props = new Properties();<br>
     *		props.setProperty("text", "ABC Label:");<br>
     *		props.setProperty("for", "abc");<br>
     *		UIComponent child = Util.getChild(component, "abcLabel", "com.sun.jsftemplating.component.factory.basic.LabelFactory", props);<br>
     *		<br>
     *		{@link LayoutComponent#encodeChild(FacesContext, UIComponent) LayoutComponent.encodeChild}(context, child);
     *	    </code></p>
     *
     *	@param	parent		Parent <code>UIComponent</code>
     *	@param	id		Identifier for the child <code>UIComponent</code>
     *	@param	factoryClass	Full {@link com.sun.jsftemplating.component.factory.ComponentFactory} class name
     *	@param	properties	<code>java.util.Properties</code> needed to
     *				create and/or initialize the
     *				<code>UIComponent</code>
     *
     *	@return	The child UIComponent that was found or created.
     */
    public static UIComponent getChild(UIComponent parent, String id, String factoryClass, Properties properties) {
	return getChild(parent, id, factoryClass, properties, id);
    }

    /**
     *	<p> Same as {@link #getChild(UIComponent, String, String, Properties)}
     *	    except that it allows you to specify a facetName different than the
     *	    id.  If null is supplied, it won't save the component as a
     *	    facet.</p>
     *
     *	@param	parent		Parent <code>UIComponent</code>
     *	@param	id		Identifier for the child <code>UIComponent</code>
     *	@param	factoryClass	Full {@link com.sun.jsftemplating.component.factory.ComponentFactory} class name
     *	@param	properties	<code>java.util.Properties</code> needed to
     *				create and/or initialize the
     *				<code>UIComponent</code>
     *	@param	facetName	The facet name (null means don't store it)
     *
     *	@return	The child UIComponent that was found or created.
     */
    public static UIComponent getChild(UIComponent parent, String id, String factoryClass, Properties properties, String facetName) {
	return getChild(parent, id, getComponentType(factoryClass),
		properties, facetName);
    }

    /**
     *	<p> This method finds or creates a child <code>UIComponent</code>
     *	    identified by the given id.  If the child is not found, it will
     *	    attempt to create it using the provided {@link ComponentType}
     *	    (<code>type</code>).  It will also initialize the
     *	    <code>UIComponent</code> using the provided set of
     *	    <code>properties</code>.</p>
     *
     *	@param	parent		Parent <code>UIComponent</code>
     *	@param	id		Identifier for the child
     *				<code>UIComponent</code>
     *	@param	type		The <code>ComponentType</code> class name
     *	@param	properties	Properties needed to create and/or initialize
     *				the <code>UIComponent</code>
     *	@param	facetName	The facet name (null means don't store it)
     *
     *	@return	The child <code>UIComponent</code> that was found or created.
     */
    private static UIComponent getChild(UIComponent parent, String id, ComponentType type, Properties properties, String facetName) {
	LayoutComponent desc = new LayoutComponent(null, id, type);
	if (properties != null) {
		//Remove Generics for now. Check with Ken to change thisinto HashMap.
	    desc.setOptions((Map) properties);
	}
	if (facetName != null) {
	    // Add the facetName to use
// FIXME: Decide if this should have its own method
	    desc.addOption(LayoutComponent.FACET_NAME, facetName);
	}

	return getChild(parent, desc);
    }

    /**
     *	<p> This method creates a {@link ComponentType} instance from the given
     *	    <code>factoryClass</code>.  It will first check its cache to see if
     *	    one has already been created.  If not, it will create one and add
     *	    to the cache for future use.</p>
     *
     *	<p> This method sets <code>factoryClass</code> for the
     *	    {@link ComponentType} id.</p>
     *
     *	@param	facatoryClass	The full classname of the
     *	    {@link com.sun.jsftemplating.component.factory.ComponentFactory}.
     *
     *	@return	A ComponentType instance for <code>factoryClass</code>.
     */
    private static ComponentType getComponentType(String factoryClass) {
	// Check the cache
	ComponentType type = (ComponentType) _types.get(factoryClass);
	if (type == null) {
	    // Not in the cache... add it...
	    type = new ComponentType(factoryClass, factoryClass);
	    Map<String, ComponentType> newMap =
		    new HashMap<String, ComponentType>(_types);
	    newMap.put(factoryClass, type);
	    _types = newMap;
	}

	// Return the ComponentType
	return type;
    }

    /**
     *	<p> This method finds or creates a child <code>UIComponent</code>
     *	    identified by the given id.  If the child is not found, it will
     *	    attempt to create it using the provided {@link LayoutComponent}
     *	    (<code>descriptor</code>).  It will also initialize the
     *	    <code>UIComponent</code> using the options set on the
     *	    {@link LayoutComponent}.</p>
     *
     *	<p> If <code>parent</code> implements {@link ChildManager}, then the
     *	    responsibility of finding and creating the child will be delegated
     *	    to the {@link ChildManager} <code>UIComponent</code>.</p>
     *
     *	<p> If you are constructing and populating a LayoutComponent before
     *	    calling this method, there are a few features that should be noted.
     *	    Besides <code>id</code> and <code>type</code> which can be set in
     *	    the LayoutComponent constructor, you can also set
     *	    <code>options</code>, and
     *	    {@link com.sun.jsftemplating.layout.descriptors.handler.Handler}'s.</p>
     *
     *	<p> <code>Options</code> may be set via
     *	    {@link LayoutComponent#setOptions(Map)}.  These options will be
     *	    applied to the <code>UIComponent</code> and may also be used by the
     *	    {@link com.sun.jsftemplating.component.factory.ComponentFactory}
     *	    while instantiating the <code>UIComponent</code>.</p>
     *
     *	<p> {@link com.sun.jsftemplating.layout.descriptors.handler.Handler}'s
     *	    can be supplied by calling
     *	    {@link LayoutComponent#setHandlers(String, List)}.  The
     *	    <code>type</code> must match the event name which invokes the
     *	    <code>List</code> of handlers you provide.  The
     *	    <code>Renderer</code> for this <code>UIComponent</code> is
     *	    responsible for declaring and dispatching events.
     *	    {@link com.sun.jsftemplating.renderer.TemplateRenderer}
     *	    will invoke <code>beforeCreate</code> and <code>afterCreate</code>
     *	    events for each child it creates (such as the one being requested
     *	    here).</p>
     *
     *	<p> <code>
     *		// Example (with LayoutComponent):<br>
     *		{@link ComponentType} type = new {@link ComponentType#ComponentType(String, String) ComponentType}("LabelFactory", "com.sun.jsftemplating.component.factory.basic.LabelFactory");<br>
     *		{@link LayoutComponent} descriptor = new {@link LayoutComponent#LayoutComponent(LayoutElement, String, ComponentType) LayoutComponent}(null, "abcLabel", type);<br>
     *		{@link LayoutComponent#addOption(String, Object) descriptor.addOption}("text", "ABC Label:");<br>
     *		{@link LayoutComponent#addOption(String, Object) descriptor.addOption}("for", "abc");<br>
     *		UIComponent child = Util.getChild(component, descriptor);<br>
     *		<br>
     *		{@link LayoutComponent#encodeChild(FacesContext, UIComponent) LayoutComponent.encodeChild}(context, child);
     *	    </code></p>
     *
     *	@param	parent		Parent <code>UIComponent</code>
     *	@param	descriptor	The {@link LayoutComponent} describing the
     *				<code>UIComponent</code>
     *
     *	@return	The child <code>UIComponent</code> that was found or created.
     */
    public static UIComponent getChild(UIComponent parent, LayoutComponent descriptor) {
	FacesContext context = FacesContext.getCurrentInstance();
	// First check to see if the UIComponent can create its own children
	if (parent instanceof ChildManager) {
	    return ((ChildManager) parent).getChild(
		context, descriptor);
	}

	// Make sure it doesn't already exist
	String childId = descriptor.getId(context, parent);
	UIComponent childComponent = findChild(parent, childId,
	    (String) descriptor.getEvaluatedOption(
		context, LayoutComponent.FACET_NAME, null));
	if (childComponent != null) {
	    return childComponent;
	}

	// Not found, create a new UIComponent
	return createChildComponent(context, descriptor, parent);
    }

    /**
     *	<p> This method creates a child <code>UIComponent</code> by using the
     *	    provided {@link LayoutComponent} (<code>descriptor</code>).  It
     *	    will associate the parent and the newly created
     *	    <code>UIComponent</code>.</p>
     *
     *	<p> It is recommended that this method NOT be called from a Renderer.
     *	    It should not be called if you have not yet checked to see if a
     *	    child UIComponent with the requested ID already exists.</p>
     *
     *	@param	context	    The <code>FacesContext</code> object.
     *	@param	descriptor  The {@link LayoutComponent} describing the
     *			    <code>UIComponent</code> to be created.
     *	@param	parent	    Parent <code>UIComponent</code>.
     *
     *	@return	A new <code>UIComponent</code> based on the provided
     *		{@link LayoutComponent}.
     *
     *	@throws	IllegalArgumentException    Thrown if descriptor equals null.
     *
     *	@see	#getChild(UIComponent, LayoutComponent)
     *	@see	#getChild(UIComponent, String, String, Properties)
     *	@see	LayoutComponent#getType()
     *	@see	ComponentType#getFactory()
     *	@see	com.sun.jsftemplating.component.factory.ComponentFactory#create(FacesContext, LayoutComponent, UIComponent)
     */
    public static UIComponent createChildComponent(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	//  Make sure a LayoutComponent was provided.
	if (descriptor == null) {
	    throw new IllegalArgumentException("'descriptor' cannot be null!");
	}

	// Create & return the child UIComponent
	return descriptor.getType().getFactory().create(
		    context, descriptor, parent);
    }

    /**
     *	<p> This util method will set the given key/value on the
     *	    <code>UIComponent</code>.  It will resolve all $...{...}
     *	    expressions, and convert the String into a
     *	    <code>ValueExpression</code> if a valid expression is detected.
     *	    The return value will be a <code>ValueExpression</code> or the
     *	    value.</p>
     *
     *	@param	context	    <code>FacesContext</code>
     *	@param	key	    The Property name to set
     *	@param	value	    The Property value to set
     *	@param	desc	    The {@link LayoutElement} associated with the
     *			    <code>UIComponent</code>
     *	@param	component   The <code>UIComponent</code>
     *
     *	@return A <code>ValueExpression</code>, or the "$...{...}" evaulated
     *		value (if no <code>ValueExpression</code> is present).
     */
    public static Object setOption(FacesContext context, String key, Object value, LayoutElement desc, UIComponent comp) {
	// Invoke our own EL.  This is needed b/c JSF's EL is designed for
	// context-insensitive EL.  Resolve our variables now because we
	// cannot depend on the individual components to do this later.  We
	// need to find a way to make this work as a regular ValueExpression...
	// for now, we'll continue to use it...
	value = VariableResolver.resolveVariables(context, desc, comp, value);

	// Next check to see if the value contains a JSF ValueExpression
	if ((value instanceof String)
		&& ComponentUtil.isValueReference((String) value)) {
	    ValueExpression ve =
		context.getApplication().getExpressionFactory().
		    createValueExpression(
			    context.getELContext(), (String) value, Object.class);
	    if (comp != null) {
		comp.setValueExpression(key, ve);
	    }
	    value = ve;
	} else if (comp != null) {
	    // In JSF, you must directly modify the attribute Map
	    Map<String, Object> attributes = comp.getAttributes();
	    if (value == null) {
		// Setting null, assume they want to remove the value
		try {
		    attributes.remove(key);
		} catch (Exception ex) { // Switched from IAE to E b/c of MyFaces incompatibility
		    // JSF is mesed up... it throws an exception if it has a
		    // property descriptor and you call remove(...).  It also
		    // throws an exception if you attempt to call put w/ null
		    // and there is no property descriptor.  Either way you
		    // MUST catch something and then handle the other case.
		    try {
			attributes.put(key, (Object) null);
		    } catch (Exception iae) { // Switched from IAE to E b/c of MyFaces incompatibility
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
		} catch (Exception ex) { // Switched from IAE to E b/c of MyFaces incompatibility
		    // Ok, try a little harder...
		    Class type = findPropertyType(comp, key);
		    if (type != null) {
			try {
			    attributes.put(key, TypeConverter.asType(type, value));
			} catch (Exception ex2) { // Switched from IAE to E b/c of MyFaces incompatibility
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

	// Return the value (which may be a ValueExpression)
	return value;
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
    public static String getGetterName(String name) {
	return "get" + ((char) (name.charAt(0) & 0xFFDF)) + name.substring(1);
    }


    /**
     *	<p> This method will attempt to resolve the given EL string.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	elt	    The LayoutElement associated w/ the expression.
     *	@param	parent	    The parent <code>UIComponent</code>.  This is used
     *			    because the current UIComponent is typically
     *			    unknown (or not even created yet).
     *	@param	value	    The String (or List / Array) to resolve.
     *
     *	@return The evaluated value (may be null).
     */
    public static Object resolveValue(FacesContext context, LayoutElement elt, UIComponent parent, Object value) {
	// Invoke our own EL.  This is needed b/c JSF's EL is designed for
	// Bean getters only.  It does not get CONSTANTS or pull data from
	// other sources without adding a custom VariableResolver and/or
	// PropertyResolver.  Eventually we may want to find a good way to
	// make this work as a regular ValueExpression expression... but for
	// now, we'll just resolve it this way.
	Object result = VariableResolver.resolveVariables(
	    context, elt, parent, value);

	// Next check to see if the result contains a JSF ValueExpression
	if ((result != null) && (result instanceof String)
		&& ComponentUtil.isValueReference((String) result)) {
	    ELContext elctx = context.getELContext();
	    ValueExpression ve =
		context.getApplication().getExpressionFactory().
		    createValueExpression(elctx, (String) result, Object.class);
	    result = ve.getValue(elctx);
/*
1.1+
		// JSF 1.1 VB:
		try {
		    ValueBinding vb =
			context.getApplication().createValueBinding((String) result);
		    result = vb.getValue(context);
		} catch (EvaluationException ex) {
		    if (LogUtil.infoEnabled()) {
			LogUtil.info("JSFT0007", new Object[] {value});
		    }
		    throw ex;
		}
*/
	}

	// Return the result
	return result;
    }

    /**
     *	<p> Returns true if this expression looks like an EL expression.</p>
     */
    public static boolean isValueReference(String value) {
	if (value == null) {
	    return false;
	}
// FIXME: Consider adding logic to look for "matching" {}'s
	int start = value.indexOf("#{");
	if ((start != -1) && (start < value.indexOf('}', start))) {
            return true;
        }
        return false;
    }


    private static Map<String, Class> _typeCache =
	    new HashMap<String, Class>();

    /**
     *	<p> This Map caches ComponentTypes by their factoryClass name.</p>
     */
    private static Map<String, ComponentType> _types =
	    new HashMap<String, ComponentType>();
}
