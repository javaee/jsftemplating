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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;



/**
 *  <p>	This class is for general purpose utility methods.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class Util {


    /**
     *	<p> This method returns the ContextClassLoader unless it is null, in
     *	    which case it returns the ClassLoader that loaded "obj".  Unless it
     *	    is null, in which it will return the system ClassLoader.</p>
     *
     * @param	obj May be null, if non-null when the Context ClassLoader is
     *		    null, then the Classloader used to load this Object will be
     *		    returned.
     */
    public static ClassLoader getClassLoader(Object obj) {
	// Get the ClassLoader
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	if (loader == null) {
	    if (obj != null) {
		loader = obj.getClass().getClassLoader();
	    }
	}

	// Wrap with custom ClassLoader if specified
	loader = getCustomClassLoader(loader);

	return loader;
    }
// NOTE: Maybe in addition to getClassLoader, we should have Iterator<ClassLoader> getClassLoaders() for cases where we want to attempt multiple ClassLoaders

    /**
     *	<p> Method to get the custom <code>ClassLoader</code> if one exists.
     *	    If one does not exist, it will return the
     *	    <code>ClassLoader</code> that is passed in.  If (null) is passed
     *	    in for the parent <code>ClassLoader</code>, it will get the
     *	    <b>System</b> <code>ClassLoader</code>, not the context
     *	    <code>ClassLoader</code> or any other one.</p>
     */
    private static ClassLoader getCustomClassLoader(ClassLoader parent) {
	// Figure out the parent ClassLoader
	parent = (parent == null) ? ClassLoader.getSystemClassLoader() : parent;

	// Check to see if we've calculated the ClassLoader for this parent
	ClassLoader loader = classLoaderCache.get(parent);
	if (loader != null) {
//System.out.println("CACHED: " + loader);
	    return loader;
	}
	loader = parent;

	// Look to see if a custom ClassLoader was specified via an initParam
	String clsName = (String) FacesContext.getCurrentInstance().
	    getExternalContext().getInitParameterMap().get(CUSTOM_CLASS_LOADER);
//System.out.println("Looking for new CL for parent: " + loader.getClass().getName());
	if (clsName != null) {
	    if (clsName.equals(loader.getClass().getName())) {
		// It has already been wrapped
		return loader;
	    }
	    try {
		// Intantiate the custom classloader w/ "loader" as its parent
		Class cls = Class.forName(clsName, true, parent);
		loader = (ClassLoader) cls.getConstructor(
			new Class[] {ClassLoader.class}).newInstance(parent);

		// Set custom classloader as the context-classloader... This
		// didn't work, JSF blew up... revisit this if necessary
//		Thread.currentThread().setContextClassLoader(loader);
	    } catch (ClassNotFoundException ex) {
		throw new IllegalArgumentException("Unable to load class ("
		    + clsName + ").  Make sure your context-param is "
		    + "specified correctly and that your custom ClassLoader "
		    + "is included in your application.", ex);
	    } catch (NoSuchMethodException ex) {
		throw new IllegalArgumentException("Unable to load class ("
		    + clsName + ").  You must have a constructor that "
		    + "allows the parent ClassLoader to be provided on your "
		    + "custom ClassLoader.", ex);
	    } catch (InstantiationException ex) {
		throw new RuntimeException("Unable to instantiate class ("
		    + clsName + ")!", ex);
	    } catch (IllegalAccessException ex) {
		throw new RuntimeException("Unable to access class ("
		    + clsName + ")!", ex);
	    } catch (java.lang.reflect.InvocationTargetException ex) {
		throw new RuntimeException("Unable to instantiate class ("
		    + clsName + ")!", ex);
	    }
	}

	// Cache for next time
	classLoaderCache.put(parent, loader);

	// Return the ClassLoader (may be the same one passed in)
	return loader;
    }

    /**
     *	<p> This method will attempt to load a Class from the context
     *	    ClassLoader.  If it fails, it will try from the ClassLoader used
     *	    to load the given object.  If that's null, or fails, it will try
     *	    using the System ClassLoader.</p>
     *
     *	@param	className   The full name of the class to load.
     *	@param	obj	    An optional Object used to help find the
     *			    ClassLoader to use.
     */
    public static Class loadClass(String className, Object obj) throws ClassNotFoundException {
	// Get the context ClassLoader
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	Class cls = null;
	if (loader != null) {
	    try {
		cls = Class.forName(className, false, loader);
	    } catch (ClassNotFoundException ex) {
		// Ignore
		if (LogUtil.finestEnabled()) {
		    LogUtil.finest("Unable to find class (" + className
			+ ") using the context ClassLoader: '" + loader
			+ "'.  I will keep looking.", ex);
		}
	    }
	}
	if (cls == null) {
	    // Still haven't found it... look for it somewhere else.
	    if (obj != null) {
		loader = obj.getClass().getClassLoader();
		if (loader != null) {
		    try {
			cls = Class.forName(className, false, loader);
		    } catch (ClassNotFoundException ex) {
			// Ignore
			if (LogUtil.finestEnabled()) {
			    LogUtil.finest("Unable to find class (" + className
				+ ") using ClassLoader: '" + loader
				+ "'.  I will try the System ClassLoader.", ex);
			}
		    }
		}
	    }
	    if (cls == null) {
		// Still haven't found it, use System ClassLoader
		loader = ClassLoader.getSystemClassLoader();

		// Allow this one to throw the Exception if not found
		cls = Class.forName(className, false, loader);
	    }
	}

	// Return the Class
	return cls;
    }

    /**
     *	<p> This method attempts load the requested Class.  If obj is a
     *	    String, it will use this value as the fully qualified class name.
     *	    If it is a Class, it will return it.  If it is anything else, it
     *	    will return the Class for the given Object.</p>
     *
     *	@param	obj The Object describing the requested Class
     */
    public static Class getClass(Object obj) throws ClassNotFoundException {
	if ((obj == null) || (obj instanceof Class)) {
	    return (Class) obj;
	}
	Class cls = null;
	if (obj instanceof String) {
	    cls = loadClass((String) obj, obj);
	} else {
	    cls = obj.getClass();
	}
	return cls;
    }

    /**
     *	<p> This method locates the requested <code>Method</code> on the
     *	    given <code>Class</code>, with the given <code>params</code>.  This
     *	    method does not throw any exceptions.  Instead it will return
     *	    <code>null</code> if unable to locate the method.</p>
     */
    public static Method getMethod(Class cls, String name, Class ... prms) {
	Method method = null;
	try {
	    method = cls.getMethod(name, prms);
	} catch (NoSuchMethodException ex) {
	    // Do nothing, we're eating the exception
	} catch (SecurityException ex) {
	    // Do nothing, we're eating the exception
	}
	return method;
    }

    /**
     *	<p> This method converts the given Map into a Properties Map (if it is
     *	    already one, then it simply returns the given Map).</p>
     */
    public static Properties mapToProperties(Map map) {
	if ((map == null) || (map instanceof Properties)) {
	    return (Properties) map;
	}

	// Create Properties and add all the values
	Properties props = new Properties();
	props.putAll(map);

	// Return the result
	return props;
    }

    /**
     *	<p> Help obtain the current <code>Locale</code>.</p>
     */
    public static Locale getLocale(FacesContext context) {
	Locale locale = null;
	if (context != null) {
	    // Attempt to obtain the locale from the UIViewRoot
	    UIViewRoot root = context.getViewRoot();
	    if (root != null) {
		locale = root.getLocale();
	    }
	}

	// Return the locale; if not found, return the system default Locale
	return (locale == null) ? Locale.getDefault() : locale;
    }

    /**
     *	<p> This method escapes text so that HTML tags and escape characters
     *	    can be shown in an HTML page without seeming to be parsed.</p>
     */
    public static String htmlEscape(String str) {
	if (str == null) {
	    return null;
	}
	StringBuffer buf = new StringBuffer("");
	for (char ch : str.toCharArray()) {
	    switch (ch) {
		case '&':
		    buf.append("&amp;");
		    break;
		case '<':
		    buf.append("&lt;");
		    break;
		case '>':
		    buf.append("&gt;");
		    break;
		default:
		    buf.append(ch);
		    break;
	    }
	}
	return buf.toString();
    }
    /**
     *	<p> This method strips leading delimeter. </p>
     *
     */
    protected static String stripLeadingDelimeter(String str, char ch) {
	if(str == null || str.equals("")) {
	    return str;
	}
	int j = 0;
	char[] strArr = str.toCharArray();
	for(int i=0; i < strArr.length; i++) {
	    j=i;
	    if(strArr[i] != ch) {
		break;
	    }
	}
	return str.substring(j);

    }

    /**
     * Closes an InputStream if it is non-null, throwing away any Exception
     * that may occur
     * @param is
     */
    public static void closeStream(InputStream is) {
	if (is != null) {
	    try {
		is.close();
	    } catch (Exception e) {
		// ignore
	    }
	}
    }

    /**
     *	<p> This stores <code>CustomClassLoader</code>s keyed by the parent
     *	    <code>ClassLoader</code>.</p>
     */
    private static Map<ClassLoader, ClassLoader> classLoaderCache =
	new HashMap<ClassLoader, ClassLoader>(5);

    /**
     *	<p> This is the context-param that specifies the JSFTemplating
     *	    custom <code>ClassLoader</code> to use.</p>
     */
    public static final String	CUSTOM_CLASS_LOADER =
	"com.sun.jsftemplating.CLASSLOADER";
}
