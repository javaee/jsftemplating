/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jsftemplating.layout.facelets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;


/**
 *  <p>	This class provides namespace support for facelets taglib.xml
 *	files.</p>
 *
 * @author Ken Paulsen
 */
public class NSContext implements javax.xml.namespace.NamespaceContext {

    /**
     *	<p> Creates a default NSContext.</p>
     */
    public NSContext() {
	addNamespace("xml", XMLConstants.XML_NS_URI);
	addNamespace("xmlns", XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }

    /**
     *	<p> Returns the namespace for the given prefix.</p>
     *
     *	@throws IllegalArgumentException If <code>null</code> prefix is given.
     */
    public String getNamespaceURI(String prefix) {
	if (prefix == null) {
	    throw new IllegalArgumentException("null is not allowed.");
	}
	String result = _uris.get(prefix);
	if (result == null) {
	    if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
		result = _defaultNSURI;
	    }
	    if (result == null) {
		result = XMLConstants.NULL_NS_URI;
	    }
	}
	return result;
    }

    /**
     *	<p> Returns the prefix for the given namespace.  If not mapped,
     *	    <code>null</code> is returned.</p>
     *
     *	@throws IllegalArgumentException If <code>null</code> prefix is given.
     */
    public String getPrefix(String namespaceURI) {
	if (namespaceURI == null) {
	    throw new IllegalArgumentException("null is not allowed.");
	}
	String result = _prefixes.get(namespaceURI);
	if (result == null) {
	    if (namespaceURI.equals(_defaultNSURI)) {
		result = XMLConstants.DEFAULT_NS_PREFIX;
	    }
	}
	return result;
    }

    /**
     *	<p> This implementation doesn't support this functionality. Instead
     *	    returns the same result as {@link #getPrefix(String)} via an
     *	    <code>Iterator</code>.</p>
     */
    public Iterator<String> getPrefixes(String namespaceURI) {
	ArrayList list = new ArrayList<String>();
	list.add(getPrefix(namespaceURI));
	return list.iterator();
    }

    /**
     *	<p> This method sets the default NS URI to be used when the default
     *	    prefix (<code>XMLConstants.DEFAULT_NS_PREFIX</code>) is
     *	    supplied.</p>
     */
    public void setDefaultNSURI(String defaultNSURI) {
	_defaultNSURI = defaultNSURI;
    }

    /**
     *	<p> This method returns the default NS URI (null if not set).</p>
     */
    public String getDefaultNSURI() {
	return _defaultNSURI;
    }

    /**
     *	<p> This method registers a Namespace mapping.</p>
     */
    public void addNamespace(String prefix, String uri) {
	_uris.put(prefix, uri);
	_prefixes.put(uri, prefix);
    }

    private String _defaultNSURI = null;

    private Map<String, String> _uris   = new HashMap<String, String>();
    private Map<String, String> _prefixes   = new HashMap<String, String>();
}
