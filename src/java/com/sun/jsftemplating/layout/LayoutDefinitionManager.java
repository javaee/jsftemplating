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
package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.Resource;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;
import com.sun.jsftemplating.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.faces.context.FacesContext;


/**
 *  <p>	This abstract class provides the base functionality for all
 *	<code>LayoutDefinitionManager</code> implementations.  It provides an
 *	static method used to obtain an instance of a concrete
 *	<code>LayoutDefinitionManager</code>: {@link #getManager(FacesContext)}.
 *	It also provides another version of this method which allows a specific
 *	instance to be specified by classname:
 *	{@link #getManager(String className)} (typically not used, the
 *	environment should be setup to provide the correct
 *	<code>LayoutDefinitionManager</code>).  Once an instance is obtained,
 *	the {@link #getLayoutDefinition(String key)} method can be invoked to
 *	obtain a {@link LayoutDefinition}.
 *  </p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public abstract class LayoutDefinitionManager {


    /**
     *	<p> Constructor.</p>
     */
    protected LayoutDefinitionManager() {
	super();
    }


    /**
     *	<p> This method is responsible for finding/creating the requested
     *	    {@link LayoutDefinition}.</p>
     *
     *	@param	key	The key used to identify the requested
     *			{@link LayoutDefinition}.
     */
    public abstract LayoutDefinition getLayoutDefinition(String key) throws IOException;


    /**
     *	<p> This is a factory method for obtaining the
     *	    {@link LayoutDefinitionManager}.  This implementation uses the
     *	    external context's initParams to look for the
     *	    {@link LayoutDefinitionManager} class.  If it exists, the specified
     *	    concrete {@link LayoutDefinitionManager} class will be used.
     *	    Otherwise, the default {@link LayoutDefinitionManager} will be
     *	    used.  The initParam key is:
     *	    {@link #LAYOUT_DEFINITION_MANAGER_KEY}.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *
     *	@see #LAYOUT_DEFINITION_MANAGER_KEY
     */
    public static LayoutDefinitionManager getManager(FacesContext context) {
// FIXME: Decide how to define the LAYOUT_DEFINITION_MANAGER
// FIXME: Properties should be settable on the LDM, such as entity resolvers...
	Map initParams = context.getExternalContext().getInitParameterMap();
	String className = DEFAULT_LAYOUT_DEFINITION_MANAGER_IMPL;
	if (initParams.containsKey(LAYOUT_DEFINITION_MANAGER_KEY)) {
	    className = (String) initParams.get(LAYOUT_DEFINITION_MANAGER_KEY);
	}
	return getManager(className);
    }


    /**
     *	<p> This method is a singleton factory method for obtaining an instance
     *	    of a <code>LayoutDefintionManager</code>.  It is possible that
     *	    multiple different implementations of
     *	    <code>LayoutDefinitionManager</code>s will be used within the same
     *	    JVM.  This is OK, the purpose of the
     *	    <code>LayoutDefinitionManager</code> is primarily performance.
     *	    Someone may provide a different <code>LayoutDefinitionManager</code>
     *	    to locate {@link LayoutDefinition}'s in a different way (XML,
     *	    database, file, java code, etc.).</p>
     */
    public static LayoutDefinitionManager getManager(String className) {
	LayoutDefinitionManager ldm =
	    (LayoutDefinitionManager) _instances.get(className);
	if (ldm == null) {
	    try {
		ClassLoader loader = Util.getClassLoader(className);
		ldm = (LayoutDefinitionManager) loader.loadClass(className).
		    getMethod("getInstance", (Class []) null).
			invoke((Object) null, (Object []) null);
	    } catch (ClassNotFoundException ex) {
		throw new RuntimeException(ex);
	    } catch (NoSuchMethodException ex) {
		throw new RuntimeException(ex);
	    } catch (IllegalAccessException ex) {
		throw new RuntimeException(ex);
	    } catch (InvocationTargetException ex) {
		throw new RuntimeException(ex);
	    } catch (NullPointerException ex) {
		throw new RuntimeException(ex);
	    } catch (ClassCastException ex) {
		throw new RuntimeException(ex);
	    }
	    _instances.put(className, ldm);
	}
	return ldm;
    }


    /**
     *	<p> Retrieve an attribute by key.</p>
     *
     *	@param	key	The key used to retrieve the attribute
     *
     *	@return	The requested attribute or null
     */
    public Object getAttribute(String key) {
	return _attributes.get(key);
    }


    /**
     *	<p> Associate the given key with the given Object as an attribute.</p>
     *
     *	@param	key	The key associated with the given object (if this key
     *	    is already in use, it will replace the previously set attribute
     *	    object).
     *
     *	@param	value	The Object to store.
     */
    public void setAttribute(String key, Object value) {
	_attributes.put(key, value);
    }

    /**
     *	<p> This method returns the <code>Map</code> of global
     *	    {@link ComponentType}s (the {@link ComponentType}s available across
     *	    the application).</p>
     *
     *	<p> It is recommended that this method not be used directly.  The map
     *	    returned by this method is shared across the application and is not
     *	    thread safe.  Instead access this Map via
     *	    {@link LayoutDefinitionManager#getGlobalComponentType(String)}.</p>
     *
     *	<p> This method will initialize the global {@link ComponentType}s if
     *	    they are not initialized.  It does this by finding all files in the
     *	    classpath named:
     *	    {@link LayoutDefinitionManager.UICOMPONENT_FACTORY_FILE}.  It then
     *	    reads each of these files (which must be <code>Properties</code>
     *	    files) and stores each identifier / fully qualified classname as
     *	    an entry in the
     *	    <code>Map&lt;String, {@link ComponentType}&gt;</code>.</p>
     */
    public static Map<String, ComponentType> getGlobalComponentTypes() {
	if (_globalComponentTypes == null) {
	    // We haven't initialized the global ComponentTypes yet
	    _globalComponentTypes = new HashMap<String, ComponentType>();

	    try {
		Properties props = null;
		URL url = null;
		String id = null;
		// Get all the properties files that define them
		Enumeration<URL> urls =
		    Util.getClassLoader(_globalComponentTypes).
			getResources(UICOMPONENT_FACTORY_FILE);
		while (urls.hasMoreElements()) {
		    url = urls.nextElement();
		    props = new Properties();
		    // Load each Properties file
		    props.load(url.openStream());
		    for (Map.Entry<Object, Object> entry : props.entrySet()) {
			// Add each property entry (key, ComponentType)
			id = (String) entry.getKey();
			_globalComponentTypes.put(id,
			    new ComponentType(id, (String) entry.getValue()));
		    }
		}
	    } catch (IOException ex) {
		throw new RuntimeException(ex);
	    }
	}
	return _globalComponentTypes;
    }

    /**
     *	<p> This method retrieves a globally defined {@link ComponentType} (a
     *	    {@link ComponentType} available across the application).</p>
     */
    public static ComponentType getGlobalComponentType(String typeID) {
	return getGlobalComponentTypes().get(typeID);
    }

    /**
     *	<p> This method allows a global {@link ComponentType} to be added.
     *	    This way of adding a global {@link ComponentType} is discouraged.
     *	    Instead, you should use a <code>UIComponentFactory</code>
     *	    annotation in each <code>ComponentFactory</code> and compile
     *	    using "<code>apt</code>".</p>
     */
    public static void addGlobalComponentType(ComponentType type) {
	synchronized (LayoutDefinitionManager.class) {
	    getGlobalComponentTypes().put(type.getId(), type);
	}
    }

    /**
     *	<p> This method clears the cached global {@link ComponentType}s.</p>
     */
    public static void clearGlobalComponentTypes() {
	_globalComponentTypes = null;
    }

    /**
     *	<p> This method returns the <code>Map</code> of global
     *	    {@link HandlerDefinition}s (the {@link HandlerDefinition}s
     *	    available across the application).</p>
     *
     *	<p> It is recommended that this method not be used.  The map returned
     *	    by this method is shared across the application and is not thread
     *	    safe.  Instead access this Map via
     *	    {@link LayoutDefinitionManager#getGlobalHandlerDefinition(String)} and
     *	    {@link LayoutDefinitionManager#addGlobalHandlerDefinition(HandlerDefinition)}.</p>
     *
     *	<p> This method will initialize the global {@link HandlerDefinition}s if
     *	    they are not initialized.  It does this by... FIXME: TBD...</p>
     */
    public static Map<String, HandlerDefinition> getGlobalHandlerDefinitions() {
	if (_globalHandlerDefs == null) {
	    _globalHandlerDefs = new HashMap<String, HandlerDefinition>();
// FIXME: Find / Initialize component types...
	}
	return _globalHandlerDefs;
    }

    /**
     *	<p> This method retrieves a globally defined {@link HandlerDefinition} (a
     *	    {@link HandlerDefinition} available across the application).</p>
     */
    public static HandlerDefinition getGlobalHandlerDefinition(String id) {
	return getGlobalHandlerDefinitions().get(id);
    }

    /**
     *	<p> This method allows a global {@link HandlerDefinition} to be added.
     *	    This way of adding a global {@link HandlerDefinition} is discouraged,
     *	    it should be done by... FIXME: TBD...</p>
     */
    public static void addGlobalHandlerDefinition(HandlerDefinition def) {
	synchronized (LayoutDefinitionManager.class) {
	    getGlobalHandlerDefinitions().put(def.getId(), def);
	}
    }

    /**
     *	<p> This method clears cached global {@link HandlerDefinition}s.</p>
     */
    public static void clearGlobalHandlerDefinitions() {
	_globalHandlerDefs = null;
    }

    /**
     *	<p> This method provides a means to add an additional global
     *	    {@link Resource} (a {@link Resource} that is available across the
     *	    application).  It is recommended that this not be done using this
     *	    method, but instead by registering the global {@link Resource}.
     *	    This can be done by... FIXME: TBD...</p>
     */
    public static void addGlobalResource(Resource res) {
	synchronized (LayoutDefinitionManager.class) {
	    getGlobalResources().add(res);
	}
    }

    /**
     *	<p> This method returns a <code>List</code> of global
     *	    {@link Resource}s.  The <code>List</code> returned should not be
     *	    changed, it is the actual internal <code>List</code> that is shared
     *	    across the application and it is not thread safe.</p>
     *
     *	<p> This method will find global resources by... FIXME: TBD...</p>
     */
    public static List getGlobalResources() {
	if (_globalResources == null) {
	    _globalResources = new ArrayList<Resource>();
// FIXME: Find / Initialize resources...
	}
	return _globalResources;
    }

    /**
     *	<p> This method clears the cached global {@link Resource}s.</p>
     */
    public static void clearGlobalResources() {
	_globalResources = null;
    }


    /**
     *	<p> This map contains sub-class specific attributes that may be needed
     *	    by specific implementations of
     *	    <code>LayoutDefinitionManager</code>s.  For example, setting an
     *	    <code>EntityResolver</code> on a
     *	    <code>LayoutDefinitionManager</code> that creates
     *	    <code>LayoutDefinitions</code> from XML files.</p>
     */
    private Map<String, Object> _attributes = new HashMap<String, Object>();

    /**
     *	<p> Static map of <code>LayoutDefinitionManager</code s.  Normally
     *	    this will only contain the default <code>LayoutManager</code>.</p>
     */
    private static Map<String, LayoutDefinitionManager> _instances =
	new HashMap<String, LayoutDefinitionManager>(2);

    /**
     *	<p> This <code>Map</code> holds global {@link ComponentType}s so they
     *	    can be defined once and shared across the application.</p>
     */
    private static Map<String, ComponentType> _globalComponentTypes	= null;

    /**
     *	<p> This <code>Map</code> holds global {@link HandlerDefinition}s so
     *	    they can be defined once and shared across the application.</p>
     */
    private static Map<String, HandlerDefinition> _globalHandlerDefs	= null;

    /**
     *	<p> This <code>List</code> holds global {@link Resource}s so
     *	    they can be defined once and shared across the application.</p>
     */
    private static List<Resource> _globalResources	= null;

    /**
     *	<p> This constant defines the default
     *	    <code>LayoutDefinitionManager</code> implementation class name.</p>
     */
    public static final String DEFAULT_LAYOUT_DEFINITION_MANAGER_IMPL =
	"com.sun.jsftemplating.layout.xml.XMLLayoutDefinitionManager";

    /**
     *	<p> This constant defines the <code>LayoutDefinitionManager</code>
     *	    implementation key for initParams. ("layoutManagerImpl")</p>
     */
    public static final String LAYOUT_DEFINITION_MANAGER_KEY =
	"layoutManagerImpl";

    /**
     *	<p> This is a <code>Properties</code> file that contains a list of
     *	    ids and class names corresponding to
     *	    <code>ComponentFactory</code>'s.</p>
     */
    public static final String UICOMPONENT_FACTORY_FILE =
	"META-INF/jsftemplating/UIComponentFactories.map";
}
