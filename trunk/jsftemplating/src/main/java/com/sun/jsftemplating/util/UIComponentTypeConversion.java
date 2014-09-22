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
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *  <p>	This {@link TypeConversion} makes an attempt to convert a String
 *	clientId to a UIComponent.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class UIComponentTypeConversion implements TypeConversion {

    /**
     *
     */
    public Object convertValue(Object value) {
	if (value == null) {
	    return null;
	}

	if (!(value instanceof UIComponent)) {
	    String strVal = value.toString();
	    if (strVal.trim().length() == 0) {
		value = null;
	    } else {
		// Treat String as clientId
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (!strVal.startsWith(":")) {
		    strVal = ":" + strVal;
		}
		value = ctx.getViewRoot().findComponent(strVal);
	    }
	}

	return value;
    }
}
