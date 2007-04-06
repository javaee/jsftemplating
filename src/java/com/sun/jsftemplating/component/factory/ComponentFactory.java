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
package com.sun.jsftemplating.component.factory;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *  <p>	This interface must be implemented by all UIComponent factories.
 *	This enabled UIComponents to be created via a consistent interface.
 *	This is critical to classes such as
 *	{@link com.sun.jsftemplating.component.TemplateComponentBase}
 *	and {@link LayoutComponent}.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public interface ComponentFactory {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>UIComponent</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent);

    /**
     *	<p> This method returns the extraInfo that was set for this
     *	    <code>ComponentFactory</code> from the
     *	    {@link com.sun.jsftemplating.layout.descriptors.ComponentType}.</p>
     */
    public Serializable getExtraInfo();

    /**
     *	<p> This method is invoked from the
     *	    {@link com.sun.jsftemplating.layout.descriptors.ComponentType} to
     *	    provide more information to the factory.  For example, if the JSF
     *	    component type was passed in, a single factory class could
     *	    instatiate multiple components the extra info that is passed in.</p>
     *
     *	<p> Some factory implementations may want to use this method to
     *	    execute intialization code for the factory based in the value
     *	    passed in.</p>
     */
    public void setExtraInfo(Serializable extraInfo);
}
