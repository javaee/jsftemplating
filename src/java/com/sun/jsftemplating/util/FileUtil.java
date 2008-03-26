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

import com.sun.jsftemplating.layout.LayoutDefinitionException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


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
     *	@param	path	The Path.
     *
     *	@param	defSuff	The suffix to use if the file specified in path is not
     *			found, it is sometimes useful to translate the path
     *			using a default suffix.
     */
    public static URL searchForFile(String path, String defSuff) {
	// Remove leading '/' characters if needed
	boolean absolutePath = false;
	while (path.startsWith("/")) {
	    path = path.substring(1);
	    absolutePath = true;
	}

	// Check to see if we have already found this before (on this request)
	URL url = null;
	FacesContext ctx = FacesContext.getCurrentInstance();
	Map<String, URL> filesFound = null;
	if (ctx != null) {
	    filesFound = (Map<String, URL>)
		ctx.getExternalContext().getRequestMap().get(FILES_FOUND);
	    if (filesFound != null) {
		url = filesFound.get(path);
		if (url != null) {
		    // We've already figured this out, abort before we start
		    return url;
		}
	    }
	}

	// Next check relative path (i.e. determine the directory w/i the app
	// they are in and prepend it to the path)
	if (!absolutePath) {
	    String absPath = getAbsolutePath(ctx, path);
	    url = searchForFile(absPath, defSuff);

	    // We're done, don't search anymore even if not found
	    return url;
	}

	// Check for file in docroot.
	url = getResource(path);
	if (url == null) {
	    // Check the classpath for the file
	    ClassLoader loader = Util.getClassLoader(path);
	    url = loader.getResource(path);
	    if (url == null) {
		// Check w/ a leading '/'
		url = loader.getResource("/" + path);
		if (url == null) {
		    // Check in "META-INF/"
		    url = loader.getResource("META-INF/" + path);
		    if ((url == null) && (defSuff != null)) {
			// Check to see if the extension is not .jsf, if
			// not then try finding w/ the extension of .jsf
			// This allows developers to write .jsf files and
			// share them even if the FacesServlet is mapped
			// differently
			int idx = path.lastIndexOf('.');
			if (idx != -1) {
			    String ext = path.substring(idx);
			    if (!ext.equalsIgnoreCase(defSuff)) {
				return searchForFile(path.substring(0,
					idx) + defSuff, null);
			    }
			} else {
			    return searchForFile(path + defSuff, null);
			}
		    }
		}
	    }
	}

	if ((url != null) && (ctx != null)) {
	    // Cache what we found -- each LDM calls this method, help them...
	    if (filesFound == null) {
		filesFound = new HashMap<String, URL>(8);
		ctx.getExternalContext().getRequestMap().
			put(FILES_FOUND, filesFound);
	    }
	    filesFound.put(path, url);
	}

	// Return a url to the file (hopefully)...
	return url;
    }

    /**
     *	<p> This method converts a path relative to the current viewId into an
     *	    absolute path from the context-root.  It does this by prepending
     *	    the current viewId to it.  It is expected that relPath does not
     *	    contain a leading '/'.</p>
     *
     *	@param	ctx	The <code>FacesContext</code>.
     *	@param	relPath	The relative path to convert.
     *
     *	@return	The absolute path (relative to the context-root).
     */
    public static String getAbsolutePath(FacesContext ctx, String relPath) {
	// Sanity check
	String absPath = null;
	if (ctx != null) {
	    // Make sure we have a ViewRoot
	    UIViewRoot viewRoot = ctx.getViewRoot();
	    if (viewRoot != null) {
		// Get the viewId
		String viewId = viewRoot.getViewId();
		if (viewId == null) {
		    viewId = "/";
		} else if (!viewId.startsWith("/")) {
		    // Ensure our viewId starts with a '/'
		    viewId = "/" + viewId;
		}
		int slash = viewId.lastIndexOf('/');

		// This will give our our base directory...
		absPath = viewId.substring(0, ++slash);

		// Append on the relative path
		absPath += relPath; 
	    }
	}
	return (absPath == null) ? ("/" + relPath) : absPath;
    }

    /**
     *	<p> This method looks for "/./" or "/../" elements in an absolute path
     *	    and removes them.  If a "/./" is found, it simply removes it.  If a
     *	    "/../" is found, it removes it and the preceeding path element.
     *	    This method also removes duplate '/' characters (i.e. "//" becomes
     *	    "/").</p>
     */
    public static String cleanUpPath(String absPath) {
	// First lets remove any "/./" elements
	int idx;
	while ((idx = absPath.indexOf("/./")) != -1) {
	    absPath = absPath.substring(0, idx) + absPath.substring(idx + 2);
	}

	// Next remove any "/../" elements
	while ((idx = absPath.indexOf("/../")) != -1) {
	    int prevElement = 0;
	    if (idx > 0) {
		// Find previous element
		prevElement = absPath.lastIndexOf('/', idx - 1);
		if (prevElement == -1) {
		    prevElement = 0;
		}
	    }
	    absPath = absPath.substring(0, prevElement) + absPath.substring(idx + 3);
	}

	// Remove "//"
	while ((idx = absPath.indexOf("//")) != -1) {
	    absPath = absPath.substring(0, idx) + absPath.substring(idx + 1);
	}

	// Return the fixed-up path
	return absPath;
    }
    
    /**
     *	<p> This method looks for resources in jar files without using the
     *	    ClassLoader.  It accepts directories in which it should scan for
     *	    jar files.</p>
     *
     *	@param	facesContext	The <code>FacesContext</code>.
     *	@param	resourcePath	The resource name to search in all jar files.
     *	@param	searchPaths	The array of paths to search for jar files.
     */
    public static List<Tuple> getJarResources(FacesContext facesContext, String resourcePath, String... searchPaths) throws IOException  {
	if (searchPaths == null) {
	    // Use default jar search path...
	    searchPaths = DEFAULT_SEARCH_PATH;
	}
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
    
    private static final String	    FILES_FOUND		= "_filesFoundThisRequest";
    private static final Class []   REALPATH_ARGS	= new Class[] {String.class};
    private static final Class []   GET_RES_ARGS	= new Class[] {String.class};
    private static final String []  DEFAULT_SEARCH_PATH	= new String[] {"/WEB-INF/lib/"};
}
