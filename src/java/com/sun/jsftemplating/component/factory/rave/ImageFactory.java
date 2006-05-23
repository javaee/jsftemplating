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
package com.sun.jsftemplating.component.factory.rave;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.factory.ComponentFactoryBase;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *  <p>	This factory is responsible for creating a <code>Image</code>
 *	UIComponent.</p>
 *
 *  @author Rick Ratta
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("rave:image")
public class ImageFactory extends ComponentFactoryBase {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>Image</code> UIComponent.</p>
     *
     *	@param	context	    The FacesContext
     *
     *	@param	descriptor  The {@link LayoutComponent} descriptor that is
     *			    associated with the requested
     *			    <code>Image</code>.
     *
     *	@param	parent	    The parent UIComponent
     *
     *	@return	The newly created <code>Image</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	// Create the UIComponent
	UIComponent comp = context.getApplication().createComponent(COMPONENT_TYPE);

	// This needs to be done here (before setOptions) so that $...{...}
	// expressions can be resolved...
	if (parent != null) {
	    addChild(context, descriptor, parent, comp);
	}

	// Set all the attributes / properties (allow these to override theme)
	setOptions(context, descriptor, comp);

	// Return the value
	return comp;
    }

    /**
     *	<p> The <code>UIComponent</code> type that must be registered in the
     *	    <code>faces-config.xml</code> file mapping to the UIComponent class
     *	    to use for this <code>UIComponent</code>.</p>
     */
    public static final String COMPONENT_TYPE	= "com.sun.rave.web.ui.Image";
}
