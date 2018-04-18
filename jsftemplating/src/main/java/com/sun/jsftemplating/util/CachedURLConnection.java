/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
