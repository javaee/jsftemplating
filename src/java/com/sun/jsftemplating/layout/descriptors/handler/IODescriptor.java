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
package com.sun.jsftemplating.layout.descriptors.handler;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;


/**
 *  <p>	This class describes an input or output parameter.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class IODescriptor implements java.io.Serializable {

    /**
     *	<p> Constructor.</p>
     *
     *	@param	name	The name of the input/output field.
     *	@param	type	The type of the input/output field.
     */
    public IODescriptor(String name, String type) {
	setName(name);
	setType(type);
    }


    /**
     *	<p> This method returns the name for this handler definition.</p>
     */
    public String getName() {
	if (_name == null) {
	    throw new NullPointerException("Name cannot be null!");
	}
	return _name;
    }


    /**
     *	<p> This method sets the handler definitions name (used by the
     *	    contsrutor).</p>
     */
    protected void setName(String name) {
	_name = name;
    }


    /**
     *	<p> For future tool support.</p>
     */
    public String getDescription() {
	return _description;
    }


    /**
     *	<p> For future tool support.</p>
     */
    public void setDescription(String desc) {
	_description = desc;
    }


    /**
     *	<p> This method returns the type for this parameter.</p>
     */
    public Class getType() {
	return _type;
    }


    /**
     *	<p> This method sets the type for this parameter.</p>
     */
    public void setType(Class type) {
	_type = type;
    }


    /**
     *	<p> This method sets the type for this parameter.</p>
     */
    public void setType(String type) {
	if ((type == null) || (type.trim().length() == 0)) {
	    return;
	}
	Class cls = (Class) _typeMap.get(type);
	if (cls == null) {
	    try {
		cls = Class.forName(type);
	    } catch (Exception ex) {
		throw new RuntimeException(
		    "Unable to determine parameter type '" + type +
		    "' for parameter named '" + getName() + "'.", ex);
	    }
	}
	_type = cls;
    }


    /**
     *	<p> This method returns the default for this parameter (valid for input
     *	    only).</p>
     */
    public Object getDefault() {
	return _default;
    }


    /**
     *	<p> This method sets the default for this parameter (valid for input
     *	    only).</p>
     */
    public void setDefault(Object def) {
	_default = def;
    }

    /**
     *	<p> This method inidicates if the input is required (valid for input
     *	    only).</p>
     */
    public boolean isRequired() {
	return _required;
    }

    /**
     *	<p> This method specifies whether this input field is required.</p>
     */
    public void setRequired(boolean required) {
	_required = required;
    }

    /**
     *	<p> This <code>toString()</code> method provides detailed information
     *	    about this <code>IODescriptor</code>.</p>
     */
    public String toString() {
	// Print the info...
	Formatter printf = new Formatter();
	printf.format("%-28s  %-40s  %s",
	    _name + (_required ? "(required)" : ""),
	    _type,
	    (_default == null) ? "" : ("DEFAULT: " + _default.toString()));

	// Print description if available
	if (_description != null) {
	    printf.format("\n\t%s", _description);
	}

	// Return the result...
	return printf.toString();
    }

    //	The following provides some basic pre-defined types
    private static Map		_typeMap		= new HashMap();
    static {
	_typeMap.put("boolean", Boolean.class);
	_typeMap.put("Boolean", Boolean.class);
	_typeMap.put("byte", Byte.class);
	_typeMap.put("Byte", Byte.class);
	_typeMap.put("char", Character.class);
	_typeMap.put("Character", Character.class);
	_typeMap.put("double", Double.class);
	_typeMap.put("Double", Double.class);
	_typeMap.put("float", Float.class);
	_typeMap.put("Float", Float.class);
	_typeMap.put("int", Integer.class);
	_typeMap.put("Integer", Integer.class);
	_typeMap.put("long", Long.class);
	_typeMap.put("Long", Long.class);
	_typeMap.put("short", Short.class);
	_typeMap.put("Short", Short.class);
	_typeMap.put("char[]", String.class);
	_typeMap.put("String", String.class);
	_typeMap.put("Object", Object.class);
    }

    private String  _name	    = null;
    private String  _description    = null;
    private Object  _default	    = null; // Input only
    private Class   _type	    = Object.class;
    private boolean _required	    = false; // Input only

    private static final long serialVersionUID = 0xA9B8C7D6E5F40312L;
}
