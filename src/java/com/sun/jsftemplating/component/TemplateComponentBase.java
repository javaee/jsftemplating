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
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.LayoutDefinitionException;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;


/**
 *  <p>	This abstract class provides base functionality for components that
 *	work in conjunction with the
 *	{@link com.sun.jsftemplating.renderer.TemplateRenderer}.  It
 *	provides a default implementation of the
 *	{@link com.sun.jsftemplating.component.TemplateComponent}
 *	interface.</p>
 *
 *  @see    com.sun.jsftemplating.renderer.TemplateRenderer
 *  @see    com.sun.jsftemplating.component.TemplateComponent
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public abstract class TemplateComponentBase extends UIComponentBase implements TemplateComponent {

    /**
     *	This method will find the request child UIComponent by id.  If it is
     *	not found, it will attempt to create it if it can find a LayoutElement
     *	describing it.
     *
     *	@param	context	    The FacesContext
     *	@param	id	    The UIComponent id to search for
     *
     *	@return	The requested UIComponent
     */
    public UIComponent getChild(FacesContext context, String id) {
	return getHelper().getChild(this, context, id);
    }


    /**
     *	This method will find the request child UIComponent by id (the id is
     *	obtained from the given LayoutComponent).  If it is not found, it will
     *	attempt to create it from the supplied LayoutElement.
     *
     *	@param	descriptor  The LayoutElement describing the UIComponent
     *
     *	@return	The requested UIComponent
     */
    public UIComponent getChild(FacesContext context, LayoutComponent descriptor) {
	return getHelper().getChild(this, context, descriptor);
    }

    /**
     *	This method returns the LayoutDefinition associated with this component.
     *
     *	@param	context	The FacesContext
     *
     *	@return	LayoutDefinition associated with this component.
     */
    public LayoutDefinition getLayoutDefinition(FacesContext context) {
	return getHelper().getLayoutDefinition(context);
    }

    /**
     *	This method saves the state for this component.  It relies on the
     *	super class to save its own sate, this method will invoke
     *	super.saveState().
     *
     *	@param	context	The FacesContext
     *
     *	@return The serialized State
     */
    public Object saveState(FacesContext context) {
	return getHelper().saveState(context, super.saveState(context));
    }

    /**
     *	This method restores the state for this component.  It will invoke the
     *	super class to restore its state.
     *
     *	@param	context	The FacesContext
     *	@param	state	The serialized State
     *
     */
    public void restoreState(FacesContext context, Object state) {
	super.restoreState(context, getHelper().restoreState(context, state));
    }

    /**
     *	This method returns the LayoutDefinitionKey for this component.
     *
     *	@return	key	The key to use in the {@link LayoutDefinitionManager}.
     */
    public String getLayoutDefinitionKey() {
	return getHelper().getLayoutDefinitionKey();
    }


    /**
     *	This method sets the LayoutDefinition key for this component.
     *
     *	@param	key The key to use in the {@link LayoutDefinitionManager}.
     */
    public void setLayoutDefinitionKey(String key) {
	getHelper().setLayoutDefinitionKey(key);
    }

    /**
     *	<p> This method retrieves the {@link TemplateComponentHelper} used by
     *	    this class to help implement the {@link TemplateComponent}
     *	    interface.</p>
     */
    protected TemplateComponentHelper getHelper() {
	if (_helper == null) {
	    _helper = new TemplateComponentHelper();
	}
	return _helper;
    }

    /**
     *	<p> Our <code>TemplateComponentHelper</code>.  We initialize it on
     *	    access b/c we want to ensure it exists, if it is Serialized it
     *	    won't exist if we init it here or in the constructor.</p>
     */
    private transient TemplateComponentHelper _helper = null;
}
