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
package com.sun.jsftemplating.resource;

import com.sun.jsftemplating.util.Util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


/**
 *  <p>	This class caches <code>ResourceBundle</code> objects per locale.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class ResourceBundleManager {


    /**
     *	<p> Use {@link #getInstance()} to obtain an instance.</p>
     */
    protected ResourceBundleManager() {
    }


    /**
     *	<p> Use this method to get the instance of this class.</p>
     */
    public static ResourceBundleManager getInstance() {
	if (_instance == null) {
	    _instance = new ResourceBundleManager();
	}
	return _instance;
    }

    /**
     *	<p> This method checks the cache for the requested
     *	    <code>ResourceBundle</code>.</p>
     *
     *	@param	baseName    Name of the bundle.
     *	@param	locale	    The <code>Locale</code>.
     *
     *	@return	The requested <code>ResourceBundle</code> in the most
     *		appropriate <code>Locale</code>.
     */
    protected ResourceBundle getCachedBundle(String baseName, Locale locale) {
	return (ResourceBundle) _cache.get(getCacheKey(baseName, locale));
    }

    /**
     *	<p> This method generates a unique key for setting / getting
     *	    <code>ResourceBundle</code>s from the cache.  It is important to
     *	    have different keys per locale (obviously).</p>
     */
    protected String getCacheKey(String baseName, Locale locale) {
	return baseName + "__" + locale.toString();
    }

    /**
     *	<p> This method adds a <code>ResourceBundle</code> to the cache.</p>
     */
    protected void addCachedBundle(String baseName, Locale locale, ResourceBundle bundle) {
	// Copy the old Map to prevent changing a Map while someone is
	// accessing it.
	Map map = new HashMap(_cache);

	// Add the new bundle
	map.put(getCacheKey(baseName, locale), bundle);

	// Set this new Map as the shared cache Map
	_cache = map;
    }

    /**
     *	<p> This method obtains the requested <code>ResourceBundle</code> as
     *	    specified by the given <code>basename</code> and
     *	    <code>locale</code>.</p>
     *
     *	@param	baseName    The base name for the <code>ResourceBundle</code>.
     *	@param	locale	    The desired <code>Locale</code>.
     */
    public ResourceBundle getBundle(String baseName, Locale locale) {
	ResourceBundle bundle = getCachedBundle(baseName, locale);
	if (bundle == null) {
	    bundle = ResourceBundle.getBundle(baseName, locale,
		    Util.getClassLoader(baseName));
	    if (bundle != null) {
		addCachedBundle(baseName, locale, bundle);
	    }
	}
	return bundle;
    }

    /**
     *	<p> This method obtains the requested <code>ResourceBundle</ocde> as
     *	    specified by the given basename, locale, and classloader.</p>
     *
     *	@param	baseName    The base name for the <code>ResourceBundle</code>.
     *	@param	locale	    The desired <code>Locale</code>.
     *	@param	loader	    The <code>ClassLoader</code> that should be used.
     */
    public ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
	ResourceBundle bundle = getCachedBundle(baseName, locale);
	if (bundle == null) {
	    bundle = ResourceBundle.getBundle(baseName, locale, loader);
	    if (bundle != null) {
		addCachedBundle(baseName, locale, bundle);
	    }
	}
	return bundle;
    }

    /**
     *	<p> Singleton.</p>
     */
    private static ResourceBundleManager _instance = null;

    /**
     *	<p> The cache of <code>ResourceBundle</code>s.</p>
     */
    private Map	_cache = new HashMap();
}
