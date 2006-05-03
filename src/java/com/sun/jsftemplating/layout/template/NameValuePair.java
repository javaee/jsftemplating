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
package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.util.IncludeInputStream;
import com.sun.jsftemplating.util.LogUtil;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;


/**
 *  <p> This class is used to represent NVP information.  This
 *      information consists of 2 or 3 parts.  If this is a simple Name
 *      Value Pair, it will contain a Name and a Value.  If it is an NVP
 *      that is used to map a return value, then it also contains a
 *      <code>target</code> as well (which should be set to "session" or
 *      "attribute").  All of these values are Strings.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class NameValuePair {
    public NameValuePair(String name, String value, String target) {
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
    public String getValue() {
	return _value;
    }

    /**
     *	<p> Target accessor.  If this value is non-null it can be assumed
     *	that this is an output mapping.  Valid values for this are
     *	currently attribute or session (this could be expanded in the
     *	future).</p>
     */
    public String getTarget() {
	return _target;
    }

    private String _name = null;
    private String _value = null;
    private String _target = null;
}
