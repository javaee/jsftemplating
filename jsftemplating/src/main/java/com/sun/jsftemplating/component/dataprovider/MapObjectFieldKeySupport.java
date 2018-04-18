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

package com.sun.jsftemplating.component.dataprovider;

import java.util.Map;

import com.sun.data.provider.DataProviderException;
import com.sun.data.provider.FieldKey;
import com.sun.data.provider.impl.ObjectFieldKeySupport;


/**
 *  <p>	Support class for <code>DataProvider</code> implementations that need
 *	to instrospect Java classes to discover properties, or Map values and
 *	return <code>FieldKey</code> instances for them.</p>
 */
public class MapObjectFieldKeySupport extends ObjectFieldKeySupport {

    /**
     *	<p> Construct a new support instance wrapping the specified Map class,
     *	    with the specified flag for including public fields.</p>
     *
     *	@param	cls	Class whose properties should be exposed.
     *	@param	inst	Optional instance of <code>cls</code> used for
     *			resolving Map values.
     */
    public MapObjectFieldKeySupport(Class cls, Map inst) {
	super(cls, false);
	if (!Map.class.isAssignableFrom(cls)) {
	    throw new IllegalArgumentException(this.getClass().getName()
		    + " is only valid for java.util.Map classes!");
	}
	_inst = inst;
    }

    /**
     *	<p> Return the <code>FieldKey</code> associated with the specified
     *	    canonical identifier, if any; otherwise, return
     *	    <code>null</code>.</p>
     *
     *	@param	fieldId	Canonical identifier of the required field.
     */
    public FieldKey getFieldKey(String fieldId) throws DataProviderException {
	FieldKey key = super.getFieldKey(fieldId);
	if ((key == null) && (_inst != null)) {
	    if (_inst.get(fieldId) != null) {
		key = new FieldKey(fieldId);
	    }
	}
	return key;
    }

    /**
     *	<p> Return an array of all supported <code>FieldKey</code>s.</p>
     */
    public FieldKey[] getFieldKeys() throws DataProviderException {
	FieldKey keys[] = super.getFieldKeys();
	if (_inst != null) {
	    FieldKey tmp[] = new FieldKey[keys.length + _inst.size()];
	    int cnt = 0;
	    // Add all other keys
	    for (FieldKey key : keys) {
		tmp[cnt++] = key;
	    }

	    // Add all Map keys
	    for (Object key : _inst.keySet()) {
		tmp[cnt++] = new FieldKey(key.toString());
	    }
	    keys = tmp;
	}
	return keys;
    }

    /**
     *	<p> Return the type of the field associated with the specified
     *	    <code>FieldKey</code>, if it can be determined; otherwise, return
     *	    <code>null</code>.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> to return the type for.
     */
    public Class getType(FieldKey fieldKey) throws DataProviderException {
	Class type = super.getType(fieldKey);
	if (type == null) {
	    Object obj = _inst.get(fieldKey.getFieldId());
	    if (obj != null) {
		type = obj.getClass();
	    }
	}
	return type;
    }


    /**
     *	<p> Return the value for the specified <code>FieldKey</code>, from the
     *	    specified base object.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> for the requested field.
     *	@param	base	    Base object to be used.
     */
    public Object getValue(FieldKey fieldKey, Object base) throws DataProviderException {
	// Make sure we have an instance
	if (base != null) {
	    _inst = (Map) base;
	}
	if (_inst == null) {
	    return null;
	}

	// Check the properties (super behavior)
	Object val = super.getValue(fieldKey, _inst);

	// If not found, check the Map
	if (val == null) {
	    val = _inst.get(fieldKey.getFieldId());
	}

	// Return the result (if found, null otherwise)
	return val;
    }


    /**
     *	<p> Return <code>true</code> if the specified value may be successfully
     *	    assigned to the specified field.  The only time this is false is if
     *	    a property of the Map implementation is being set.  Map values can
     *	    be any Object and therefor will always be true.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> to check assignability for.
     *	@param	value	    Proposed value.
     */
    public boolean isAssignable(FieldKey fieldKey, Object value) throws DataProviderException {
	Class type = super.getType(fieldKey);
	if (type != null) {
	    // We only do this if we have a property...
	    return super.isAssignable(fieldKey, value);
	}

	// Return true in all other cases
	return true;
    }

    /**
     *	<p> Return the read only state of the field associated with the
     *	    specified <code>FieldKey</code>, if it can be determined,
     *	    otherwise, return <code>false</code>.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> to return read only state for
     */
    public boolean isReadOnly(FieldKey fieldKey) throws DataProviderException {
	Class type = super.getType(fieldKey);
	if (type != null) {
	    // We only do this if we have a property...
	    return super.isReadOnly(fieldKey);
	}
	return false;
    }


    /**
     *	<p> Set the <code>value</code> for the specified <code>fieldKey</code>,
     *	    on the specified <code>base</code> object.  If the instance
     *	    (<code>base</code>) is already set on this support object, then
     *	    <code>base</code> may be <code>null</code>.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> for the requested field.
     *	@param	base	    Base object to be used (null ok if already set).
     *	@param	value	    Value to set.
     *
     *	@exception  NullPointerException	If base and _inst are null.
     *	@exception  IllegalArgumentException	If a type mismatch occurs.
     *	@exception  IllegalStateException	If setting a read only field.
     */
    public void setValue(FieldKey fieldKey, Object base, Object value) throws DataProviderException {
	if (base != null) {
	    _inst = (Map) base;
	}
	Class type = super.getType(fieldKey);
	if (type != null) {
	    // We only do this if we have a property...
	    super.setValue(fieldKey, _inst, value);
	} else {
	    _inst.put(fieldKey.getFieldId(), value);
	}
    }

    private Map	_inst	= null;
}
