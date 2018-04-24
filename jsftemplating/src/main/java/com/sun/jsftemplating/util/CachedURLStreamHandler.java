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
     *	    will be used by the {@link CachedURLConnection} which is created
     *	    when openConnection is called.  It stores this as a
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
