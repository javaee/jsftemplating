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

package com.sun.jsftemplating.layout.template;


/**
 *  <p> This class is used to represent NVP information.  This information
 *	consists of 2 or 3 parts.  If this is a simple Name Value Pair, it will
 *	contain a <code>name</code> and a <code>value</code>.  If it is an NVP
 *	that is used to map a return value, then it also contains a
 *	<code>target</code> as well (which should be set to "session" or
 *	"attribute").  <code>name</code> and <code>target</code> contain
 *	<code>String</code> values.  The <code>value</code> property contains
 *	an <code>Object</code> value because it may be a <code>String</code>,
 *	<code>java.util.List</code>, or an <code>array[]</code>. </p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class NameValuePair {
    public NameValuePair(String name, Object value, String target) {
	_name = name;
	_value = value;
	_target = target;
    }

    /**
     *	<p> Name accessor.</p>
     */
    public String getName() {
	return _name;
    }

    /**
     *	<p> Value accessor.</p>
     */
    public Object getValue() {
	return _value;
    }

    /**
     *	<p> Target accessor.  If this value is non-null it can be assumed that
     *	    this is an output mapping.  However, if it is null it cannot be
     *	    assumed to be an input mapping -- it may still be an output mapping
     *	    or an in-out mapping which uses EL.  Valid values for this are
     *	    currently: (null), "pageSession", "attribute", or "session" (this
     *	    list may be expanded in the future).</p>
     */
    public String getTarget() {
	return _target;
    }

    /**
     *	<p> Customized to reconstruct the NVP.</p>
     */
    public String toString() {
	if (getTarget() == null) {
	    return getName() + "=\"" + getValue() + '"';
	} else {
	    return getName() + "=>$" + getTarget() + '{' + getValue() + '}';
	}
    }

    private String _name = null;
    private Object _value = null;
    private String _target = null;
}
