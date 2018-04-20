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
package com.sun.jsftemplating.component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;


/**
 *  <p>	This interface defines a method to find or create a child
 *	<code>UIComponent</code>.  It is designed to be used in conjunction
 *	with <code>UIComponent</code> implementations.</p>
 *
 *  @see    TemplateComponent
 *  @see    com.sun.jsftemplating.component.ComponentUtil
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public interface ChildManager {

    /**
     *	<p> This method will find the request child UIComponent by id (the id
     *	    is obtained from the given {@link LayoutComponent}).  If it is not
     *	    found, it will attempt to create it from the supplied
     *	    {@link LayoutComponent}.</p>
     *
     *	@param	context	    FacesContext
     *	@param	descriptor  {@link LayoutComponent} describing the UIComponent
     *
     *	@return	Requested UIComponent
     */
    public UIComponent getChild(FacesContext context, LayoutComponent descriptor);
}
