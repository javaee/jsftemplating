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

import java.util.EventObject;

import javax.faces.component.UIComponent;


/**
 *  <p>	This class serves as the base class for <code>EventObject</code>s in
 *	this package.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class EventObjectBase extends EventObject implements UIComponentHolder {

    /**
     *	<p> This constructor should not be used.</p>
     */
    private EventObjectBase() {
	super(null);
    }

    /**
     *	<p> This constructor is protected to avoid direct instantiation, one
     *	    of the sub-classes of this class should be used instead.</p>
     *
     *	@param	component   The <code>UIComponent</code> associated with this
     *			    <code>EventObject</code>.
     */
    protected EventObjectBase(UIComponent component) {
	super(component);
    }

    /**
     *	<P> This method returns the <code>UIComponent</code> held by the
     *	    <code>Object</code> implementing this interface.</p>
     *
     *	@return The <code>UIComponent</code>.
     */
    public UIComponent getUIComponent() {
	return (UIComponent) getSource();
    }
}
