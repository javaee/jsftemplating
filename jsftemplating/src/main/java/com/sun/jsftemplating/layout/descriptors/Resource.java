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

package com.sun.jsftemplating.layout.descriptors;

import com.sun.jsftemplating.resource.ResourceFactory;
import com.sun.jsftemplating.util.Util;


/**
 *  <p>	This class holds information that describes a Resource.  It
 *	provides access to a {@link ResourceFactory} for obtaining the
 *	actual Resource object described by this descriptor.  See the
 *	layout.dtd file for more information on how to define a Resource
 *	via XML.  The LayoutDefinition will add all defined Resources to
 *	the request scope for easy access (including via JSF EL).</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class Resource implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *	<p> This is the id for the Resource.</p>
     */
    private String _id				= null;

    /**
     *	<p> This holds "extraInfo" for the Resource, such as a ResourceBundle
     *	    baseName.</p>
     */
    private String _extraInfo			= null;


    /**
     *	<p> This is a String className for the Factory.</p>
     */
    private String _factoryClass		= null;


    /**
     *	<p> The Factory that produces the desired <code>UIComponent</code>.</p>
     */
    private transient ResourceFactory _factory	= null;


    /**
     *	<p> Constructor.</p>
     */
    public Resource(String id, String extraInfo, String factoryClass) {
	if (id == null) {
	    throw new NullPointerException("'id' cannot be null!");
	}
	if (factoryClass == null) {
	    throw new NullPointerException("'factoryClass' cannot be null!");
	}
	_id = id;
	_extraInfo = extraInfo;
	_factoryClass = factoryClass;
	_factory = createFactory();
    }


    /**
     *	<p> Accessor method for ID.  This is the key the resource will be stored
     *	    under in the Request scope.</p>
     */
    public String getId() {
	return _id;
    }

    /**
     *	<p> This holds "extraInfo" for the Resource, such as a
     *	    <code>ResourceBundle</code> baseName.</p>
     */
    public String getExtraInfo() {
	return _extraInfo;
    }


    /**
     *	<p> This method provides access to the {@link ResourceFactory}.</p>
     *
     *	@return The {@link ResourceFactory}.
     */
    public ResourceFactory getFactory() {
	if (_factory == null) {
	    _factory = createFactory();
	}
	return _factory;
    }

    /**
     *	<p> This method creates a new {@link ResourceFactory}.</p>
     *
     *	@return The new {@link ResourceFactory}.
     */
    protected ResourceFactory createFactory() {
	try {
	    Class cls = Util.loadClass(_factoryClass, _factoryClass);
	    return (ResourceFactory) cls.newInstance();
	} catch (ClassNotFoundException ex) {
	    throw new RuntimeException(ex);
	} catch (InstantiationException ex) {
	    throw new RuntimeException(ex);
	} catch (IllegalAccessException ex) {
	    throw new RuntimeException(ex);
	}
    }
}
