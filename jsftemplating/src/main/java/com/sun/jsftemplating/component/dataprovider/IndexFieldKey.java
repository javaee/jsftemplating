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
    private static final long serialVersionUID = 1L;

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
     *	@param	fk	    The <code>FieldKey</code>.
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
