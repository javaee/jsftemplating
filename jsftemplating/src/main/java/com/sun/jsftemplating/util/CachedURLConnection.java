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
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;


/**
 *  <p>	This class enables a URL connection to a cached object of type
 *	<code>&lt;T&gt;</code>.  It is required that a
 *	<code>WeakReference</code> to this object by used, allowing GC to
 *	occur if no other Strong references are present.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class CachedURLConnection<T> extends URLConnection {

    /**
     *	<p> This constructor requires the <code>WeakReference&lt;T&gt;</code>
     *	    containing the object to be supplied, in addition to the URL.</p>
     */
    public CachedURLConnection(URL url, WeakReference<T> weakRef) {
	super(url);
	if ((weakRef == null) || (weakRef.get() == null)) {
	    throw new IllegalArgumentException("The weakRef is required in "
		+ "order to create a CachedURLConnection!");
	}
	this.weakRef = weakRef;
    }

    /**
     *	<p> This method is overriden to provide access to an InputStream based
     *	    on the <code>T</code> object.</p>
     */
    @Override
    public InputStream getInputStream() throws IOException {
	try {
	    // If the value is (null) a NPE will be thrown here... only
	    // should occur if the object has been GC'd.
	    byte bytes[] = null;
	    T obj = weakRef.get();
	    if (obj instanceof byte[]) {
		bytes = (byte[]) obj;
	    } else {
		bytes = obj.toString().getBytes();
	    }
	    return new ByteArrayInputStream(bytes);
	} catch (NullPointerException ex) {
	    throw new IOException("Cached object was null!");
	}
    }

    /**
     *	<p> This method is required to be overriden, however, no "connection"
     *	    is needed, so this method does nothing.</p>
     */
    @Override
    public void connect() throws IOException {
	// Do nothing.
    }

    private WeakReference<T> weakRef = null;
}
