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
package com.sun.jsftemplating.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.LayoutDefinitionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.faces.context.ExternalContext;


/**
 *  <p>	This class is for general purpose utility methods.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class FileUtil {

    /**
     *	<p> This method calculates the system path to the given filename that
     *	    is relative to the docroot.  It takes the
     *	    <code>ServletContext</code> or <code>PortletContext</code> (which
     *	    is why this method takes an <code>Object</code> for this parameter)
     *	    and the relative path to find.  It then invokes the
     *	    <code>getRealPath(String)</code> method of the
     *	    <code>ServletContext</code> / <code>PortletContext</code> and
     *	    returns the result.  This method uses reflection.</p>
     */
    public static String getRealPath(Object ctx, String relativePath) {
	String path = null;

	// The following should work w/ a ServletContext or PortletContext
	Method method = null;
	try {
	    method = ctx.getClass().getMethod("getRealPath", REALPATH_ARGS);
	} catch (NoSuchMethodException ex) {
	    throw new RuntimeException(ex);
	}
	try {
	    path = (String) method.invoke(ctx, new Object [] {relativePath});
	} catch (IllegalAccessException ex) {
	    throw new RuntimeException(ex);
	} catch (InvocationTargetException ex) {
	    throw new RuntimeException(ex);
	}

	// Return Result
	return path;
    }

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
		    "getResource", GET_RES_ARGS);
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
     *	    first looks relative the context root of the application, it then
     *	    looks in the classpath, including relative to the
     *	    <code>META-INF</code> folder.  If found a <code>URL</code> to the
     *	    file will be returned.</p>
     *
     *	@param	relPath	The Path.
     *
     *	@param	defSuff	The suffix to use if the file specified in path is not
     *			found, it is sometimes useful to translate the path
     *			using a default suffix.
     */
    public static URL searchForFile(String relPath, String defSuff) {
	// Remove leading '/' characters if needed
	while (relPath.startsWith("/")) {
	    relPath = relPath.substring(1);
	}

	// Check for file in docroot.
	URL url = getResource(relPath);
	if (url == null) {
	    // Check the classpath for the file
	    ClassLoader loader = Util.getClassLoader(relPath);
	    url = loader.getResource(relPath);
	    if (url == null) {
		// Check w/ a leading '/'
		url = loader.getResource("/" + relPath);
		if (url == null) {
		    // Check in "META-INF/"
		    url = loader.getResource("META-INF/" + relPath);
		    if ((url == null) && (defSuff != null)) {
			// Check to see if the extension is not .jsf, if not
			// then try finding w/ the extension of .jsf
			// This allows developers to write .jsf files and
			// share them even if the FacesServlet is mapped
			// differently
			int idx = relPath.lastIndexOf('.');
			if (idx != -1) {
			    String ext = relPath.substring(idx);
			    if (!ext.equalsIgnoreCase(defSuff)) {
				return searchForFile(
				    relPath.substring(0, idx) + defSuff, null);
			    }
			} else {
			    return searchForFile(relPath + defSuff, null);
			}
		    }
		}
	    }
	}

	// Return a url to the file (hopefully)...
	return url;
    }
    
    public static List<Tuple> getJarResources(FacesContext facesContext, String resourcePath, String... searchPaths) throws IOException  {
        List<Tuple> entries = new ArrayList<Tuple>();
        ExternalContext ec = facesContext.getExternalContext();
        for (String searchPath : searchPaths) {
            Set<String> paths = ec.getResourcePaths(searchPath);
            for (String path : paths) {
                if ("jar".equalsIgnoreCase(path.substring(path.length() - 3))) {
                    JarFile jarFile = new JarFile(new File(ec.getResource(path).getFile()));
                    JarEntry jarEntry = jarFile.getJarEntry(resourcePath);
                    if (jarEntry != null) {
                        entries.add(new Tuple(jarFile, jarEntry));
                    }
                }
            }
        }
        return entries;
    }
    
    public static List<Tuple> getJarResources(FacesContext facesContext, String resourcePath) throws IOException  {
        return getJarResources(facesContext, resourcePath, "/WEB-INF/lib/");
    }

    private static final Class [] REALPATH_ARGS	= new Class[] {String.class};
    private static final Class [] GET_RES_ARGS	= new Class[] {String.class};
}
