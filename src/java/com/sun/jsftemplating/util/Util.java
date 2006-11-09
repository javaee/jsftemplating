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
	    } else {
		loader = ClassLoader.getSystemClassLoader();
	    }
	}
	return loader;
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
	    ClassLoader loader = getClassLoader(obj);
	    cls = loader.loadClass((String) obj);
	} else {
	    cls = obj.getClass();
	}
	return cls;
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
	 public static String stripLeadingDelimeter(String str, char ch) {
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
}
