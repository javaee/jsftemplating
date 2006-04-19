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

import com.sun.jsftemplating.component.factory.ComponentFactory;


/**
 *  <p>	This class holds information that describes a {@link LayoutComponent}
 *	type.  It provides access to a {@link ComponentFactory} for
 *	instantiating an instance of a the <code>UIComponent</code> described
 *	by this descriptor.  See the layout.dtd file for more information on
 *	how to declare types via XML.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class ComponentType implements java.io.Serializable {

    /**
     *	<p> Constructor.</p>
     */
    public ComponentType(String id, String factoryClass) {
	if (id == null) {
	    throw new NullPointerException("'id' cannot be null!");
	}
	if (factoryClass == null) {
	    throw new NullPointerException("'factoryClass' cannot be null!");
	}
	_id = id;
	_factoryClass = factoryClass;
    }


    public String getId() {
	return _id;
    }


    /**
     *	<p> This method provides access to the {@link ComponentFactory}.</p>
     *
     *	@return The {@link ComponentFactory}.
     */
    public ComponentFactory getFactory() {
	if (_factory == null) {
	    _factory = createFactory();
	}
	return _factory;
    }


    /**
     *	<p> This method creates a new factory.</p>
     *
     *	@return The new {@link ComponentFactory}.
     */
    protected ComponentFactory createFactory() {
	try {
	    Class cls = Class.forName(_factoryClass);
	    return (ComponentFactory) cls.newInstance();
	} catch (ClassNotFoundException ex) {
	    throw new RuntimeException(ex);
	} catch (InstantiationException ex) {
	    throw new RuntimeException(ex);
	} catch (IllegalAccessException ex) {
	    throw new RuntimeException(ex);
	}
    }


    /**
     *	<p> This is the id for the ComponentType.</p>
     */
    private String _id				= null;


    /**
     *	<p> This is a String className for the Factory.</p>
     */
    private String _factoryClass		= null;


    /**
     *	<p> The {@link ComponentFactory} that produces the desired
     *	    <code>UIComponent</code>.</p>
     */
    private transient ComponentFactory _factory	= null;
}
