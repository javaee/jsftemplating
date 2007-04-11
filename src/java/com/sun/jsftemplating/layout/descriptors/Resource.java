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
package com.sun.jsftemplating.layout.descriptors;

import com.sun.jsftemplating.resource.ResourceFactory;


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
	    Class cls = Class.forName(_factoryClass);
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
