/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.component.dataprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.sun.data.provider.DataProviderException;
import com.sun.data.provider.FieldKey;
import com.sun.data.provider.impl.ObjectFieldKeySupport;


/**
 *  <p>	Support class for <code>DataProvider</code> implementations that need
 *	to instrospect Java classes to discover properties, Map values, or
 *	public fields (optional) and return <code>FieldKey</code> instances for
 *	them.  This implementation provides support for multiple Objects.</p>
 */
public class MultipleObjectFieldKeySupport {

    /**
     *	<p> Construct a new support instance wrapping the specified classes,
     *	    with the specified flag for including public fields.</p>
     *
     *	@param	classes		Class whose properties should be exposed.
     *	@param	includeFields	Flag indicating whether public fields should
     *				also be included.
    public MultipleObjectFieldKeySupport(Class classes[], boolean includeFields) {
	for (Class cls : classes) {
	}
    }
     */

    /**
     *	<p> Construct a new support instance wrapping the specified objects,
     *	    with the specified flag for including public fields.</p>
     *
     *	@param	objs		Class whose properties should be exposed.
     *	@param	includeFields	Flag indicating whether public fields should
     *				also be included.
     */
    public MultipleObjectFieldKeySupport(Object objs[], boolean includeFields) {
	_children = new ArrayList();
	for (Object obj : objs) {
	    if (obj instanceof Map) {
		_children.add(
		    new MapObjectFieldKeySupport(obj.getClass(), (Map) obj));
	    } else {
		_children.add(
		    new ObjectFieldKeySupport(obj.getClass(), includeFields));
	    }
	}
    }

    /**
     *	<p> Return the <code>FieldKey</code> associated with the specified
     *	    canonical identifier, if any; otherwise, return
     *	    <code>null</code>.</p>
     *
     *	@param	fieldId	Canonical identifier of the required field.
     */
    public FieldKey getFieldKey(String fieldId) throws DataProviderException {
	FieldKey key = null;
	int idx = 0;
	for (ObjectFieldKeySupport support : _children) {
	    key = support.getFieldKey(fieldId);
	    if (key != null) {
		key = new IndexFieldKey(key, idx);
		break;
	    }
	    idx++;
	}
	return key;
    }


    /**
     *	<p> Return an array of all supported <code>FieldKey</code>s.</p>
     */
    public FieldKey[] getFieldKeys() throws DataProviderException {
	Set<FieldKey> keys = new TreeSet<FieldKey>();
	FieldKey keyArr[] = null;
	int idx = 0;
	for (ObjectFieldKeySupport support : _children) {
	    keyArr = support.getFieldKeys();
	    for (int cnt = 0; cnt < keyArr.length; cnt++) {
		keyArr[cnt] = new IndexFieldKey(keyArr[cnt], idx++);
	    }
	    keys.addAll(Arrays.asList(keyArr));
	}
	return keys.toArray(new FieldKey[keys.size()]);
    }


    /**
     *	<p> Return the type of the field associated with the specified
     *	    <code>FieldKey</code>, if it can be determined; otherwise, return
     *	    <code>null</code>.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> for which to return the type.
     */
    public Class getType(FieldKey fieldKey) throws DataProviderException {
	Class type = null;
	if (fieldKey instanceof IndexFieldKey) {
	    type = _children.get(((IndexFieldKey) fieldKey).getIndex()).
		    getType(fieldKey);
	} else {
	    for (ObjectFieldKeySupport support : _children) {
		type = support.getType(fieldKey);
		if (type != null) {
		    break;
		}
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
	Object value = null;
	if (fieldKey instanceof IndexFieldKey) {
	    value = _children.get(((IndexFieldKey) fieldKey).getIndex()).
		getValue(fieldKey, base);
	} else {
	    for (ObjectFieldKeySupport support : _children) {
		value = support.getValue(fieldKey, base);
		if (value != null) {
		    break;
		}
	    }
	}
	return value;
    }

    /**
     *	<p> Return <code>true</code> if the specified value may be successfully
     *	    assigned to the specified field.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> to check assignability.
     *	@param	value	    Proposed value.
     */
    public boolean isAssignable(FieldKey fieldKey, Object value) throws DataProviderException {
	boolean assignable = false;
	if (fieldKey instanceof IndexFieldKey) {
	    assignable = _children.get(((IndexFieldKey) fieldKey).getIndex()).
		isAssignable(fieldKey, value);
	} else {
	    for (ObjectFieldKeySupport support : _children) {
		assignable = support.isAssignable(fieldKey, value);
		if (assignable) {
		    break;
		}
	    }
	}
	return assignable;
    }

    /**
     *	<p> Return the read only state of the field associated with the
     *	    specified <code>FieldKey</code>, if it can be determined,
     *	    otherwise, return <code>true</code>.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> to check.
     */
    public boolean isReadOnly(FieldKey fieldKey) throws DataProviderException {
	boolean readOnly = true;
	if (fieldKey instanceof IndexFieldKey) {
	    readOnly = _children.get(((IndexFieldKey) fieldKey).getIndex()).
		isReadOnly(fieldKey);
	} else {
	    FieldKey fk = null;
	    for (ObjectFieldKeySupport support : _children) {
		fk = support.getFieldKey(fieldKey.getFieldId());
		if (fk != null) {
		    // Only call support for the one that claims it has a FieldKey!
		    readOnly = support.isReadOnly(fieldKey);
		    break;
		}
	    }
	}
	return readOnly;
    }


    /**
     *	<p> Set the value for the specified <code>FieldKey</code>, on the
     *	    specified base object.</p>
     *
     *	@param	fieldKey    <code>FieldKey</code> for the requested field.
     *	@param	base	    Base object to be used.
     *	@param	value	    Value to be set.
     *
     *	@exception  IllegalArgumentException	If a type mismatch occurs
     *	@exception  IllegalStateException	If setting a read only field
     *						is attempted.
     */
    public void setValue(FieldKey fieldKey, Object base, Object value) throws DataProviderException {
	if (fieldKey instanceof IndexFieldKey) {
	    _children.get(((IndexFieldKey) fieldKey).getIndex()).
		setValue(fieldKey, base, value);
	} else {
	    FieldKey fk = null;
	    for (ObjectFieldKeySupport support : _children) {
		fk = support.getFieldKey(fieldKey.getFieldId());
		if (fk != null) {
		    // Only call support for the one that claims it has a FieldKey!
		    support.setValue(fieldKey, base, value);
		    break;
		}
	    }
	}
    }

    /**
     *	<p> This list hold the child ObjectFieldKeySupport implementations.</p>
     */
    private List<ObjectFieldKeySupport> _children  = null;
}
