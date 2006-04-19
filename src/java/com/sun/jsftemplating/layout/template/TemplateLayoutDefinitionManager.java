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
package com.sun.jsftemplating.layout.template;

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


/**
 *  <p>	This class is a concrete implmentation of the abstract class
 *	{@link LayoutDefinitionManager}.  It obtains {@link LayoutDefinition}
 *	objects by interpreting the <code>key</code> passed to
 *	{@link #getLayoutDefinition(String)} as a path to a template file
 *	describing the {@link LayoutDefinition}.  It will first attempt to
 *	resolve this path from the document root of the ServletContext or
 *	PortletCotnext.  If that fails, it will attempt to use the Classloader
 *	to resolve it.</p>
 *
 *  <p>	This class is a singleton.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class TemplateLayoutDefinitionManager extends LayoutDefinitionManager {

    /**
     *	<p> Constructor.</p>
     */
    protected TemplateLayoutDefinitionManager() {
	super();
    }

    /**
     *	<p> This method returns an instance of this LayoutDefinitionManager.
     *	    The object returned is a singleton (only 1 instance will be
     *	    created per JVM).</p>
     *
     *	@return	<code>TemplateLayoutDefinitionManager</code> instance
     */
    public static LayoutDefinitionManager getInstance() {
	if (instance == null) {
	    instance = new TemplateLayoutDefinitionManager();
	}
	return instance;
    }

    /**
     *	<p> This method is responsible for finding the requested
     *	    {@link LayoutDefinition} for the given <code>key</code>.</p>
     *
     *	@param	key	Key identifying the desired {@link LayoutDefinition}.
     *
     *	@return		The requested {@link LayoutDefinition}.
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
	    // Check for template file in docroot.
	    Object ctx = FacesContext.getCurrentInstance().
		getExternalContext().getContext();
	    URL url = null;

	    // The following should work w/ a ServletContext or PortletContext
	    Method method = null;
	    try {
		method = ctx.getClass().getMethod(
			"getResource", GET_RESOURCE_ARGS);
	    } catch (NoSuchMethodException ex) {
		throw new RuntimeException(ex);
	    }
	    try {
		url = (URL) method.invoke(ctx, new Object [] {"/" + key});
	    } catch (IllegalAccessException ex) {
		throw new RuntimeException(ex);
	    } catch (InvocationTargetException ex) {
		throw new RuntimeException(ex);
	    }

	    if (url == null) {
		// Check the classpath for the template file
		ClassLoader loader = Util.getClassLoader(key);
		url = loader.getResource(key);
		if (url == null) {
		    url = loader.getResource("/"+key);
		    if (url == null) {
			url = loader.getResource("META-INF/"+key);
		    }
		}
	    }

	    // Make sure we found the url
	    if (url == null) {
		throw new java.io.FileNotFoundException(
			"Unable to locate '" + key + "'");
	    }

	    // Read the template file
	    try {
		ld  = new TemplateReader(url).read();
	    } catch (IOException ex) {
		throw new RuntimeException(
		    "Unable to process '" + url.toString() + "'.", ex);
	    }

	    // Cache the LayoutDefinition
	    synchronized (layouts) {
		layouts.put(key, ld);
	    }
	}
	return ld;
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
    private static final Class [] GET_RESOURCE_ARGS =
	    new Class[] {String.class};

    private static boolean DEBUG =
	Boolean.getBoolean("com.sun.jsftemplating.DEBUG");
}
