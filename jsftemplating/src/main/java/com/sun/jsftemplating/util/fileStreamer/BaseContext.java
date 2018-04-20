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
package com.sun.jsftemplating.util.fileStreamer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 *  <p> This class provides a base implemention of {@link Context} to
 *	implement code that is likely to be needed by most {@link Context}
 *	implementations.</p>
 */
public abstract class BaseContext implements Context {

    /**
     *	Constructor.
     */
    protected BaseContext() {
    }

    /**
     *	<p> This method may be used to manage arbitrary information between the
     *	    code invoking the {@link FileStreamer} and the
     *	    <code>ContentSource</code>.  This method retrieves an attribute.</p>
     *
     *	<p> See individual {@link ContentSource} implementations for more
     *	    details on supported / required attributes.</p>
     */
    public Object getAttribute(String name) {
	if (name == null) {
	    return null;
	}

	// Return the value (if any)
	return _att.get(name);
    }

    /**
     *	<p> This provides access to all attributes in this Context.</p>
     *
     *	<p> See individual {@link ContentSource} implementations for more
     *	    details on supported / required attributes.</p>
     */
    public Set<String> getAttributeKeys() {
	return _att.keySet();
    }

    /**
     *	<p> This method may be used to manage arbitrary information between the
     *	    code invoking the {@link FileStreamer} and the
     *	    <code>ContentSource</code>.  This method sets an attribute.</p>
     *
     *	<p> See individual {@link ContentSource} implementations for more
     *	    details on supported / required attributes.</p>
     */
    public void setAttribute(String name, Object value) {
	if (name != null) {
	    _att.put(name, value);
	}
    }

    /**
     *	<p> This method may be used to manage arbitrary information between the
     *	    coding invoking the {@link FileStreamer} and the
     *	    <code>ContentSource</code>.  This method removes an attribute.</p>
     *
     *	<p> See individual {@link ContentSource} implementations for more
     *	    details on supported / required attributes.</p>
     */
    public void removeAttribute(String name) {
	_att.remove(name);
    }

    /**
     *	<p> Application scope key for allowed paths.</p>
     */
    protected static final String   ALLOWED_PATHS_KEY   =   "__jsft_AllowPath";

    /**
     *	<p> Application scope key for denied paths.</p>
     */
    protected static final String   DENIED_PATHS_KEY   =   "__jsft_DenyPath";

    private Map<String, Object> _att	    = new HashMap<String, Object>();
}
