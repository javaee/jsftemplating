/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006-2018 Oracle and/or its affiliates. All rights reserved.
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
