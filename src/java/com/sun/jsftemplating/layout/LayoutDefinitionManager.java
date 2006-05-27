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

import com.sun.jsftemplating.annotation.UIComponentFactoryAPFactory;
import com.sun.jsftemplating.annotation.HandlerAPFactory;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.Resource;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;
import com.sun.jsftemplating.layout.descriptors.handler.IODescriptor;
import com.sun.jsftemplating.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 *	<code>LayoutDefinitionManager</code> implementations.  It provides a
 *	static method used to obtain an instance of a concrete
 *	<code>LayoutDefinitionManager</code>:
 *	{@link #getLayoutDefinitionManager(FacesContext, String)}.  However, in
 *	most cases is makes the most sense to call the static method:
 *	{@link #getLayoutDefinition(FacesContext, String)}.  This method
 *	ensures that the cache is checked first before going through the effort
 *	of finding a <code>LayoutDefinitionManager</code> instance.</p>
 *
 *  <p>	This class also provides access to global {@link HandlerDefinition}s,
 *	{@link Resource}s, and {@link ComponentType}s.</p>
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
    public abstract LayoutDefinition getLayoutDefinition(String key) throws LayoutDefinitionException;

    /**
     *	<p> This method is used to determine if this
     *	    <code>LayoutDefinitionManager</code> should process the given key.
     *	    It does not necessarily mean that the
     *	    <code>LayoutDefinitionManager</code> <em>can</em> process it.
     *	    Parser errors do not necessarily mean that it should not process
     *	    the file.  In order to provide meaningful error messages, this
     *	    method should return true if the format of the template matches the
     *	    type that this <code>LayoutDefinitionManager</code> processes.  It
     *	    is understood that at times it may not be recognizable; in the case
     *	    where no <code>LayoutDefinitionManager</code>s return
     *	    <code>true</code> from this method, the parent
     *	    <code>ViewHandler</code> will be used, which likely means that it
     *	    will look for a .jsp and give error messages accordingly.  Also,
     *	    the existance of a file should not be used as a meassure of success
     *	    as other <code>LayoutDefinitionManager</code>s may be more
     *	    appropriate.</p>
     */
    public abstract boolean accepts(String key);

    /**
     *	<p> This method checks for the <code>relPath</code> in the docroot of
     *	    the application.  This should work in both Portlet and Servlet
     *	    environments.  If <code>FacesContext</code> is null, null will be
     *	    returned.</p>
     */
    public static URL getResource(String relPath) {
	FacesContext facesContext = FacesContext.getCurrentInstance();
	if (facesContext == null) {
	    return null;
	}
	Object ctx = facesContext.getExternalContext().getContext();
	URL url = null;

	// The following should work w/ a ServletContext or PortletContext
	Method method = null;
	try {
	    method = ctx.getClass().getMethod(
		    "getResource", GET_RESOURCE_ARGS);
	} catch (NoSuchMethodException ex) {
	    throw new LayoutDefinitionException("Unable to find "
		+ "'getResource' method in this environment!", ex);
	}
	try {
	    url = (URL) method.invoke(ctx, new Object [] {"/" + relPath});
	} catch (IllegalAccessException ex) {
	    throw new LayoutDefinitionException(ex);
	} catch (InvocationTargetException ex) {
	    throw new LayoutDefinitionException(ex);
	}

	return url;
    }

    /**
     *	<p> This method searches for the given relative path filename.  It
     *	    first looks relative the context root of the application, it
     *	    then looks in the classpath, including relative to the
     *	    <code>META-INF</code> folder.  If found a <code>URL</code> to the
     *	    file will be returned.</p>
     */
    public static URL searchForFile(String relPath) {
	// Check for file in docroot.
	URL url = getResource(relPath);

	if (url == null) {
	    // Check the classpath for the xml file
	    ClassLoader loader = Util.getClassLoader(relPath);
	    url = loader.getResource(relPath);
	    if (url == null) {
		url = loader.getResource("/" + relPath);
		if (url == null) {
		    url = loader.getResource("META-INF/" + relPath);
		}
	    }
	}
	return url;
    }

    /**
     *	<p> This method should be used to obtain a {@link LayoutDefinition}.
     *	    It first checks to see if a cached {@link LayoutDefinition}
     *	    already exists, if so it returns it.  If one does not already
     *	    exist, it will obtain the appropriate
     *	    <code>LayoutDefinitionManager</code> instance and call
     *	    {@link #getLayoutDefinition} and return the result.</p>
     */
    public static LayoutDefinition getLayoutDefinition(FacesContext ctx, String key) throws LayoutDefinitionException {
	LayoutDefinition def = getCachedLayoutDefinition(key);
	if (def != null) {
	    return def;
	}

	// Obtain the correct LDM, and get the LD
	return getLayoutDefinitionManager(ctx, key).getLayoutDefinition(key);
    }


    /**
     *	<p> This method obtains the <code>LayoutDefinitionManager</code> that
     *	    is able to process the given <code>key</code>.</p>
     *
     *	<p> This implementation uses the <code>ExternalContext</code>'s
     *	    initParams to look for the <code>LayoutDefinitionManager</code>
     *	    class.  If it exists, the specified concrete
     *	    <code>LayoutDefinitionManager</code> class will be used as the
     *	    "default" (i.e. the first <code>LayoutDefinitionManager</code>
     *	    checked).  Otherwise,
     *	    {@link #DEFAULT_LAYOUT_DEFINITION_MANAGER_IMPL} will be used.
     *	    "{@link #LAYOUT_DEFINITION_MANAGER_KEY}" is the initParam key.</p>
     *
     *	<p> The <code>key</code> is used to test if desired
     *	    <code>LayoutDefinitionManager</code> is able to read the requested
     *	    {@link LayoutDefinition}.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *	@param	key	The desired {@link LayoutDefinition}.
     *
     *	@see #LAYOUT_DEFINITION_MANAGER_KEY
     */
    public static LayoutDefinitionManager getLayoutDefinitionManager(FacesContext ctx, String key) throws LayoutDefinitionException {
	List<String> ldms = getLayoutDefinitionManagers(ctx);
	LayoutDefinitionManager mgr = null;
	for (String className : ldms) {
	    mgr = getLayoutDefinitionManager(className);
	    if (mgr.accepts(key)) {
		return mgr;
	    }
	}
	throw new LayoutDefinitionException("No LayoutDefinitionManager "
	    + "available for '" + key + "'.  This may mean the file cannot "
	    + "be found, or is unrecognizable.");
    }

    /**
     *	<p> This method is responsible for returning a <code>List</code> of
     *	    known <code>LayoutDefinitionManager</code> instances.  Each value
     *	    of the list is a <code>String</code> representing the classname of
     *	    a <code>LayoutDefinitionManager</code> implementation.</p>
     */
    public static List<String> getLayoutDefinitionManagers(FacesContext ctx) {
	if (_ldmKeys == null) {
	    List<String> keys = new ArrayList<String>();

	    // Check to see what the default should be...
	    Map initParams = ctx.getExternalContext().getInitParameterMap();
	    String def = DEFAULT_LAYOUT_DEFINITION_MANAGER_IMPL;
	    if (initParams.containsKey(LAYOUT_DEFINITION_MANAGER_KEY)) {
		def = (String) initParams.get(LAYOUT_DEFINITION_MANAGER_KEY);
	    }
	    keys.add(def);
// FIXME: Populate this from an external source!!
// while (...) {
//  if (!key.equals(def)) {
//	keys.add(key);
//  }
// }
	    keys.add("com.sun.jsftemplating.layout.xml.XMLLayoutDefinitionManager");
	    keys.add("com.sun.jsftemplating.layout.template.TemplateLayoutDefinitionManager");

	    _ldmKeys = keys;
	}
	return _ldmKeys;
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
    public static LayoutDefinitionManager getLayoutDefinitionManager(String className) {
	LayoutDefinitionManager ldm =
	    (LayoutDefinitionManager) _instances.get(className);
	if (ldm == null) {
	    try {
		ClassLoader loader = Util.getClassLoader(className);
		ldm = (LayoutDefinitionManager) loader.loadClass(className).
		    getMethod("getInstance", (Class []) null).
			invoke((Object) null, (Object []) null);
	    } catch (ClassNotFoundException ex) {
		throw new LayoutDefinitionException(
		    "Unable to find LDM: '" + className + "'.", ex);
	    } catch (NoSuchMethodException ex) {
		throw new LayoutDefinitionException("LDM '" + className
		    + "' does not have a 'getInstance()' method!", ex);
	    } catch (IllegalAccessException ex) {
		throw new LayoutDefinitionException("Unable to access LDM: '"
		    + className + "'!", ex);
	    } catch (InvocationTargetException ex) {
		throw new LayoutDefinitionException("Error while attempting "
		    + "to get LDM: '" + className + "'!", ex);
	    } catch (ClassCastException ex) {
		throw new LayoutDefinitionException("LDM '" + className
		    + "' must extend from '"
		    + LayoutDefinitionManager.class.getName() + " and must "
		    + "be loaded from the same ClassLoader!", ex);
	    } catch (NullPointerException ex) {
		throw new LayoutDefinitionException(ex);
	    }
	    _instances.put(className, ldm);
	}
	return ldm;
    }

    /**
     *	<p> This method may be used to obtain a cached
     *	    {@link LayoutDefinition}.  If it has not been cached, this method
     *	    returns <code>null</code>.</p>
     *
     *	@param	key The key for the cached {@link LayoutDefinition} to obtain.
     *
     *	@return	The {@link LayoutDefinition} or <code>null</code>.
     */
    public static LayoutDefinition getCachedLayoutDefinition(String key) {
	if (DEBUG) {
	    // Disable caching for debug mode
	    return null;
	}

	// Remove leading '/' characters if needed
	while (key.startsWith("/")) {
	    key = key.substring(1);
	}
	return _layoutDefinitions.get(key);
    }

    /**
     *	<p> This method should be used by sub-classes to store a cached
     *	    {@link LayoutDefinition}.</p>
     *
     *	@param	key	The {@link LayoutDefinition} key to cache.
     *	@param	value	The {@link LayoutDefinition} to cache.
     */
    protected static void putCachedLayoutDefinition(String key, LayoutDefinition value) {
	// Remove leading '/' characters if needed
	while (key.startsWith("/")) {
	    key = key.substring(1);
	}
	synchronized (_layoutDefinitions) {
	    _layoutDefinitions.put(key, value);
	}
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
     *	    {@link UIComponentFactoryAPFactory#FACTORY_FILE}.  It then reads
     *	    each of these files (which must be <code>Properties</code> files)
     *	    and stores each identifier / fully qualified classname as an entry
     *	    in the <code>Map&lt;String, {@link ComponentType}&gt;</code>.</p>
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
			getResources(UIComponentFactoryAPFactory.FACTORY_FILE);
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
     *	    safe.  Instead get values from the Map via:
     *	    {@link LayoutDefinitionManager#getGlobalHandlerDefinition(String)}.
     *	    </p>
     *
     *	<p> This method will initialize the global {@link HandlerDefinition}s if
     *	    they are not initialized.  It does this by finding all files in the
     *	    classpath named: {@link HandlerAPFactory#HANDLER_FILE}.  It then
     *	    reads each file (which must be a valid <code>Properties</code>
     *	    file) and stores the information for later retrieval.</p>
     */
    public static Map<String, HandlerDefinition> getGlobalHandlerDefinitions() {
	if (_globalHandlerDefs != null) {
	    // We've already done this, return the answer
	    return _globalHandlerDefs;
	}

	// Create a new Map to hold the defs
	_globalHandlerDefs = new HashMap<String, HandlerDefinition>();
	Properties props = null;
	URL url = null;
	try {
	    // Get all the properties files that define them
	    Enumeration<URL> urls =
		Util.getClassLoader(_globalHandlerDefs).
		    getResources(HandlerAPFactory.HANDLER_FILE);
	    while (urls.hasMoreElements()) {
		url = urls.nextElement();
		props = new Properties();
		// Load each Properties file
		props.load(url.openStream());
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
		    if (((String) entry.getKey()).endsWith(".class")) {
			// We will only process .class entries
			readGlobalHandlerDefinition((Map<String, String>) props, entry);
		    }
		}
	    }
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}
	return _globalHandlerDefs;
    }

    /**
     *	<p> This method processes a single {@link HandlerDefinition}'s
     *	    meta-data.</p>
     */
    private static void readGlobalHandlerDefinition(Map<String, String> map, Map.Entry<Object, Object> entry) {
	// Get the key.class value...
	String key = (String) entry.getKey();
	// Strip off .class
	key = key.substring(0, key.lastIndexOf('.'));

	// Create a new HandlerDefinition
	HandlerDefinition def = new HandlerDefinition(key);

	// Set the class / method
	String value = map.get(key + '.' + "method");
	def.setHandlerMethod((String) entry.getValue(), value);

	// Read the input defs
	def.setInputDefs(readIODefs(map, key, true));

	// Read the output defs
	def.setOutputDefs(readIODefs(map, key, false));

	// Add the Handler...
	_globalHandlerDefs.put(key, def);
    }

    /**
     *	<p> This method reads and creates IODescriptors for the given key.</p>
     */
    private static Map<String, IODescriptor> readIODefs(Map<String, String> map, String key, boolean input) {
	String type;
	String inOrOut = input ? "input" : "output";
	int count = 0;
	IODescriptor desc = null;
	Map<String, IODescriptor> defs = new HashMap<String, IODescriptor>(5);
	String value = map.get(key + "." + inOrOut + "[" + count + "].name");
	while (value != null) {
	    // Get the type
	    type = map.get(key + "." + inOrOut + "[" + count + "].type");
	    if (type == null) {
		type = DEFAULT_TYPE;
	    }

	    // Create an IODescriptor
	    desc = new IODescriptor(value, type);
	    defs.put(value, desc);

	    // If this is an output, we're done... for input we need to do more
	    if (input) {
		// required?
		value = map.get(key + "." + inOrOut + "[" + count + "].required");
		if ((value != null) && Boolean.valueOf(value).booleanValue()) {
		    desc.setRequired(true);
		}

		// default?
		value = map.get(key + "." + inOrOut + " [ " + count + "].defaultValue");
		if ((value != null) && !value.equals(HandlerInput.DEFAULT_DEFAULT_VALUE)) {
		    desc.setDefault(value);
		}
	    }

	    // Look for next IO declaration
	    value = map.get(key + "." + inOrOut + "[" + (++count) + "].name");
	}

	return defs;
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
     *	    This way of adding a global {@link HandlerDefinition} is
     *	    discouraged.  It should be done implicitly through annotations,
     *	    placement of a properties file in the correct location, or
     *	    explicitly by declaring it the page (some template formats may not
     *	    support this).</p>
     *
     *	@see LayoutDefinitionManager#getGlobalHandlerDefinitions()
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
// FIXME: TBD...
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
     *	<p> Static <code>Map</code> of <code>LayoutDefinitionManager</code s.
     *	    Normally this will only contain the default
     *	    {@link LayoutDefinitionManager}.</p>
     */
    private static Map<String, LayoutDefinitionManager> _instances =
	new HashMap<String, LayoutDefinitionManager>(4);

    /**
     *	<p> Static <code>Map</code> of cached {@link LayoutDefinition}s.</p>
     */
    private static Map<String, LayoutDefinition> _layoutDefinitions =
	new HashMap<String, LayoutDefinition>();

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
     *	<p> This <code>List</code> contains the classnames of known
     *	    {@link LayoutDefinitionManager} instances.</p>
     */
    private static List<String> _ldmKeys = null;

    /**
     *	<p> This is the default input and output type.</p>
     */
    public static final String	DEFAULT_TYPE	= "Object";

    /**
     *	<p> This constant defines the default
     *	    <code>LayoutDefinitionManager</code> implementation class name.</p>
     */
    public static final String DEFAULT_LAYOUT_DEFINITION_MANAGER_IMPL =
	"com.sun.jsftemplating.layout.xml.XMLLayoutDefinitionManager";

    /**
     *	<p> This constant defines the <code>LayoutDefinitionManager</code>
     *	    implementation key for initParams.
     *	    ("LayoutDefinitionManagerImpl")</p>
     */
    public static final String LAYOUT_DEFINITION_MANAGER_KEY =
	"LayoutDefinitionManagerImpl";

    public static final boolean DEBUG =
	Boolean.getBoolean("com.sun.jsftemplating.DEBUG");

    /**
     *
     */
    private static final Class [] GET_RESOURCE_ARGS =
	    new Class[] {String.class};
}
