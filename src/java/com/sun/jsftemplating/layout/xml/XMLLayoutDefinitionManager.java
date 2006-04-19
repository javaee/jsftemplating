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
package com.sun.jsftemplating.layout.xml;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.util.Util;
import com.sun.jsftemplating.util.ClasspathEntityResolver;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 *  <p>	This class is a concrete implmentation of the abstract class
 *	{@link LayoutDefinitionManager}.  It obtains {@link LayoutDefinition}
 *	objects by interpreting the <code>key</code> passed to
 *	{@link #getLayoutDefinition(String)} as a path to an XML file
 *	describing the {@link LayoutDefinition}.  It will first attempt to
 *	resolve this path from the document root of the ServletContext or
 *	PortletCotnext.  If that fails, it will attempt to use the Classloader
 *	to resolve it.</p>
 *
 *  <p>	Locating the dtd for the XML file is done in a similar manner.  It
 *	will first attempt to locate the dtd relative to the ServletContext
 *	(or PortletContext) root.  If that fails it will attempt to use the
 *	ClassLoader to resolve it.  Optionally a different EntityResolver may
 *	be supplied to provide a custom way of locating the dtd, this is done
 *	via {@link #setEntityResolver}.</p>
 *
 *  <p>	This class is a singleton.  This means modifications to this class
 *	effect all threads using this class.  This includes setting
 *	EntityResolvers and ErrorHandlers.  These values only need to be set
 *	once to remain in effect as long as the JVM is running.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class XMLLayoutDefinitionManager extends LayoutDefinitionManager {

    /**
     *	<p> Constructor.</p>
     */
    protected XMLLayoutDefinitionManager() {
	super();

	// Set the default XMLError Handler
	try {
	    setErrorHandler(new XMLErrorHandler(new PrintWriter(
		    new OutputStreamWriter(System.err, "UTF-8"), true)));
	} catch (UnsupportedEncodingException ex) {
	    throw new RuntimeException(ex);
	}

	// Set the default EntityResolver
	setEntityResolver(new ClasspathEntityResolver());
    }

    /**
     *	<p> This method returns an instance of this LayoutDefinitionManager.
     *	    The object returned is a singleton (only 1 instance will be
     *	    created per JVM).</p>
     *
     *	@return	<code>XMLLayoutDefinitionManager</code> instance
     */
    public static LayoutDefinitionManager getInstance() {
	if (instance == null) {
	    instance = new XMLLayoutDefinitionManager();
	}
	return instance;
    }

    /**
     *	<p> This method is responsible for finding the requested
     *	    {@link LayoutDefinition} for the given <code>key</code>.</p>
     *
     *	@param	key Key identifying the desired {@link LayoutDefinition}
     *
     *	@return	The requested {@link LayoutDefinition}.
     */
    public LayoutDefinition getLayoutDefinition(String key) throws IOException {
	// Remove leading "/" (we'll add it back if needed)
	while (key.startsWith("/")) {
	    key = key.substring(1);
	}

	// See if we already have this one.
	LayoutDefinition ld = (LayoutDefinition) layouts.get(key);
	if (DEBUG) {
	    // Disable caching
	    ld = null;
	}
	if (ld == null) {
	    String baseURI = getBaseURI();

	    // Check for XML file in docroot.  Use docroot for the baseURI,
	    // and get the full path to the xml file
	    URL ldURL = null;
	    Object ctx = FacesContext.getCurrentInstance().
		getExternalContext().getContext();
	    String url;

	    // The following should work w/ a ServletContext or PortletContext
	    Method method = null;
	    try {
		method = ctx.getClass().getMethod(
			"getRealPath", GET_REAL_PATH_ARGS);
	    } catch (NoSuchMethodException ex) {
		throw new RuntimeException(ex);
	    }
	    try {
		if (baseURI == null) {
		    baseURI = "file:///"
			+ method.invoke(ctx, new Object [] {"/"});
		}
		url = (String) method.invoke(ctx, new Object [] {key});
		// Don't add the leading '/' back on because it looks a the
		// root if we do
	    } catch (IllegalAccessException ex) {
		throw new RuntimeException(ex);
	    } catch (InvocationTargetException ex) {
		throw new RuntimeException(ex);
	    }

	    // Create a URL to the xml file, if file exists
	    if ((url != null) && new File(url).canRead()) {
		try {
		    ldURL = new URL("file:///" + url);
		} catch (Exception ex) {
		    throw new RuntimeException(
			"Unable to create URL: 'file:///" + url
			+ "' while attempting to locate '" + key + "'", ex);
		}
	    }

	    if (ldURL == null) {
		// Check the classpath for the xml file
		ClassLoader loader = Util.getClassLoader(key);
		ldURL = loader.getResource(key);
		if (ldURL == null) {
		    ldURL = loader.getResource("/"+key);
		    if (ldURL == null) {
			ldURL = loader.getResource("META-INF/"+key);
		    }
		}
	    }

	    // Make sure we found the url
	    if (ldURL == null) {
		throw new java.io.FileNotFoundException(
			"Unable to locate '" + key + "'");
	    }

	    // Read the XML file
	    try {
		ld  = new XMLLayoutDefinitionReader(
		    ldURL, getEntityResolver(), getErrorHandler(), baseURI).
			read();
	    } catch (IOException ex) {
		throw new RuntimeException("Unable to process '" + ldURL
			+ "'.  EntityResolver: '" + getEntityResolver()
			+ "'.  ErrorHandler: '" + getErrorHandler()
			+ "'.  baseURI: '" + baseURI + "'.", ex);
	    }

	    // Cache the LayoutDefinition
	    synchronized (layouts) {
		layouts.put(key, ld);
	    }
	}
	return ld;
    }

    /**
     *	<p> This returns the LDM's entity resolver, null if not set.</p>
     *
     *	@return EntityResolver
     */
    public EntityResolver getEntityResolver() {
	return (EntityResolver) getAttribute(ENTITY_RESOLVER);
    }

    /**
     *	<p> This method sets the LDM's entity resolver.</p>
     *
     *	@param	entityResolver	The EntityResolver to use.
     */
    public void setEntityResolver(EntityResolver entityResolver) {
	setAttribute(ENTITY_RESOLVER, entityResolver);
    }

    /**
     *	<p> This returns the LDM's XML parser ErrorHandler, null if not set.</p>
     *
     *	@return ErrorHandler
     */
    public ErrorHandler getErrorHandler() {
	return (ErrorHandler) getAttribute(ERROR_HANDLER);
    }

    /**
     *	<p> This method sets the LDM's ErrorHandler.</p>
     *
     *	@param	errorHandler	The ErrorHandler to use.
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
	setAttribute(ERROR_HANDLER, errorHandler);
    }

    /**
     *	<p> This returns the LDM's XML parser baseURI which will be used to
     *	    resolve relative URI's, null if not set.</p>
     *
     *	@return The base URI as a String
     */
    public String getBaseURI() {
	return (String) getAttribute(BASE_URI);
    }

    /**
     *	<p> This method sets the LDM's BaseURI.</p>
     *
     *	@param	baseURI		The BaseURI to use.
     */
    public void setBaseURI(String baseURI) {
	setAttribute(BASE_URI, baseURI);
    }

    /**
     *	<p> This class handles XML parser errors.</p>
     */
    private static class XMLErrorHandler implements ErrorHandler {
	/** Error handler output goes here */
	private PrintWriter out;

	XMLErrorHandler(PrintWriter outWriter) {
	    this.out = outWriter;
	}

	/**
	 *  <p>	Returns a string describing parse exception details.</p>
	 */
	private String getParseExceptionInfo(SAXParseException spe) {
	    String systemId = spe.getSystemId();
	    if (systemId == null) {
		systemId = "null";
	    }
	    String info = "URI=" + systemId + " Line=" + spe.getLineNumber()
		+ ": " + spe.getMessage();
	    return info;
	}

	// The following methods are standard SAX ErrorHandler methods.
	// See SAX documentation for more info.

	public void warning(SAXParseException spe) throws SAXException {
	    out.println("Warning: " + getParseExceptionInfo(spe));
	}

	public void error(SAXParseException spe) throws SAXException {
	    String message = "Error: " + getParseExceptionInfo(spe);
	    throw new SAXException(message, spe);
	}

	public void fatalError(SAXParseException spe) throws SAXException {
	    String message = "Fatal Error: " + getParseExceptionInfo(spe);
	    throw new SAXException(message, spe);
	}
    }

    /**
     *	<p> Static map of LayoutDefinitionManagers.  Normally this will only
     *	    contain the default LayoutManager.</p>
     */
    private static Map layouts = new HashMap();

    /**
     *	<p> This is used to ensure that only 1 instance of this class is
     *	    created (per JVM).</p>
     */
    private static LayoutDefinitionManager instance = null;

    /**
     *
     */
    private static final Class [] GET_REAL_PATH_ARGS =
	    new Class[] {String.class};

    /**
     *	<p> This is an attribute key which can be used to provide an
     *	    EntityResolver to the XML parser.</p>
     */
    public static final String ENTITY_RESOLVER	= "entityResolver";

    /**
     *
     */
    public static final String ERROR_HANDLER	= "errorHandler";

    /**
     *
     */
    public static final String BASE_URI		= "baseURI";

    private static boolean DEBUG =
	Boolean.getBoolean("com.sun.jsftemplating.DEBUG");
}
