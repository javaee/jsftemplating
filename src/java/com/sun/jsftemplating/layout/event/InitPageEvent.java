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
package com.sun.jsftemplating.layout.event;

import javax.faces.component.UIComponent;


/**
 *  <p>	This event is fired for each page accessed during each request.  This
 *	may be fired during the UIComponent tree creation time, while restoring
 *	the View, when forwarding to a new page, or when simply accessing a
 *	page via Java code.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class InitPageEvent extends EventObjectBase implements UIComponentHolder {

    /**
     *	<p> Constructor.</p>
     *
     *	@param	component   The <code>UIComponent</code> associated with this
     *			    <code>EventObject</code> (likely null).
     */
    public InitPageEvent(Object component) {
	super(component);
    }
}
