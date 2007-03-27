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

import com.sun.data.provider.FieldKey;


/**
 *  <p>	This implementation of <code>FieldKey</code> provides a way to
 *	associate an index along with the fieldId.  One use case for this is
 *	when a DataProvider acts as a facade for multiple data sources, the
 *	index can be used to indicate to which underlying source the key
 *	pertains.</p>
 *
 *  <p>	Keey in mind that a single <code>FieldKey</code> is meant to represent
 *	all rows, so it would not be useful to store row information in a
 *	FieldKey.  Therefor the index in this <code>IndexFieldKey</code> is
 *	<b>not</b> intended to specify a row!</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class IndexFieldKey extends FieldKey {

    /**
     *	<p> Constructs a new <code>IndexFieldKey</code> with the specified
     *	    <code>fieldId</code> and <code>index</code>.</p>
     *
     *	@param	fieldId	The desired cannonical ID String.
     *	@param	index	    The index for this <code>IndexFieldKey</code>.
     */
    public IndexFieldKey(String fieldId, int index) {
	super(fieldId);
	setIndex(index);
    }

    /**
     *	<p> Constructs a new <code>IndexFieldKey</code> with the specified
     *	    <code>fieldId</code>, <code>displayName</code>, and
     *	    <code>index</code>.</p>
     *
     *	@param	fieldId	    The desired cannonical ID String for this field.
     *	@param	displayName The desired display name String.
     *	@param	index	    The index for this <code>IndexFieldKey</code>.
     */
    public IndexFieldKey(String fieldId, String displayName, int index) {
	super(fieldId, displayName);
	setIndex(index);
    }

    /**
     *	<p> Constructs a new <code>IndexFieldKey</code> with the specified
     *	    <code>fieldId</code>, <code>displayName</code>, and
     *	    <code>index</code>.</p>
     *
     *	@param	fieldId	    The desired cannonical ID String for this field.
     *	@param	displayName The desired display name String.
     *	@param	index	    The index for this <code>IndexFieldKey</code>.
     */
    public IndexFieldKey(FieldKey fk, int index) {
	super(fk.getFieldId(), fk.getDisplayName());
	setIndex(index);
    }

    /**
     *	<p> This method retreives the index associated with this object.</p>
     */
    public int getIndex() {
	return _index;
    }

    /**
     *	<p> This method retreives the index associated with this object.</p>
     */
    public void setIndex(int idx) {
	_index = idx;
    }

    /**
     *	<p> Standard equals implementation.  This method compares the
     *	    <code>IndexFieldKey</code> <code>fieldId</code> and
     *	    <code>index</code> values for equality.</p>
     *
     *	@param	obj	The Object to check equality.
     *
     *	@return	<code>true</code> if equal, <code>false</code> if not.
     */
    public boolean equals(Object obj) {
	boolean val = super.equals(obj);
        if (val && (obj instanceof IndexFieldKey)) {
            val = ((IndexFieldKey) obj).getIndex() == getIndex();
        }
        return val;
    }

    /**
     *	<p> This provides a hash for instances of this class.</p>
     */
    public int hashCode() {
	if (_hash == -1) {
	    // Use the hashCode() of the String (id + index)
	    _hash = (getFieldId() + getIndex()).hashCode();
	}
	return _hash;
    }

    /**
     *	<p> The toString() implementation.  This implementation prints out
     *	    the index and fieldId:</p>
     *
     *	<p> IndexFieldKey[<code>&lt;index&gt;</code>][<code>&lt;id&gt;</code>]
     *	    </p>
     */
    public String toString() {
        return "IndexFieldKey[" + getIndex() + "][" + getFieldId() + "]"; // NOI18N
    }

    /**
     *	<p> Storate for the index.</p>
     */
    private int		    _index	= -1;
    private transient int   _hash	= -1;
}
