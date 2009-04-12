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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 *  <p>	This class enables a <code>URLConnection</code> to be returned when
 *	<code>openConnection(URL)</code> is called which is capable of reading
 *	from a <code>T</code> object.</p>
 */
public class CachedURLStreamHandler<T> extends URLStreamHandler {

    /**
     *	<p> This constructor stores the given <code>T</code> object, which
     *	    will be used by the {@link MapURLConnection} which is created when
     *	    openConnection is called.  It stores this as a
     *	    <em>WeakReference</em>, so it will be garbage collected if no
     *	    other "strong" references refer to it!</p>
     */
    public CachedURLStreamHandler(T obj) {
	this.weakRef = new WeakReference<T>(obj);
    }

    /**
     *	<p> This method creates a new {@link CachedURLConnection} associated with
     *	    the <code>T</code> object given when the constructor was called.</p>
     */
    protected URLConnection openConnection(URL url) throws IOException {
	return new CachedURLConnection<T>(url, weakRef);
    }

    private WeakReference<T> weakRef = null;
}
