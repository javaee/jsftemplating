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

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;


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
	if ((id == null) || (id.trim().equals("")))  {
	    // No id, no LayoutComponent, nothing we can do.
	    return null;
	}

	// We have an id, use it to search for an already-created child
// FIXME: I am doing this 2x if it falls through to create the child...
// FIXME: think about optimizing this
	UIComponent childComponent = ComponentUtil.findChild(this, id, id);
	if (childComponent != null) {
	    return childComponent;
	}

	// If we're still here, then we need to create it... hopefully we have
	// a LayoutComponent to tell us how to do this!
	LayoutDefinition ld = getLayoutDefinition(context);
	if (ld == null) {
	    // No LayoutDefinition to tell us how to create it... return null
	    return null;
	}

	// Attempt to find a LayoutComponent matching the id
	LayoutElement elt =
	    LayoutDefinition.getChildLayoutElementById(context, id, ld, this);

	// Create the child from the LayoutComponent
	return getChild(context, (LayoutComponent) elt);
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
	UIComponent childComponent = null;

	// Sanity check
	if (descriptor == null) {
	    throw new IllegalArgumentException("The LayoutComponent is null!");
	}

	// First pull off the id from the descriptor
	String id = descriptor.getId(context, this);
	if ((id != null) && !(id.trim().equals(""))) {
	    // We have an id, use it to search for an already-created child
	    childComponent = ComponentUtil.findChild(this, id, id);
	    if (childComponent != null) {
		return childComponent;
	    }
	}

	// No id, or the component hasn't been created.  In either case, we
	// create a new component (moral: always have an id)

	// Invoke "beforeCreate" handlers
	descriptor.beforeCreate(context, this);

	// Create UIComponent
	childComponent =
	    ComponentUtil.createChildComponent(context, descriptor, this);

	// Invoke "afterCreate" handlers
	descriptor.afterCreate(context, childComponent);

	// Return the newly created UIComponent
	return childComponent;
    }

    /**
     *	This method returns the LayoutDefinition associated with this component.
     *
     *	@param	context	The FacesContext
     *
     *	@return	LayoutDefinition associated with this component.
     */
    public LayoutDefinition getLayoutDefinition(FacesContext context) {
	// Make sure we don't already have it...
	if (_layoutDefinition != null) {
	    return _layoutDefinition;
	}

	// Get the LayoutDefinitionManager key
	String key = getLayoutDefinitionKey();
	if (key == null) {
	    throw new NullPointerException("LayoutDefinition key is null!");
	}

	// Get the LayoutDefinitionManager
	LayoutDefinitionManager ldm =
	    LayoutDefinitionManager.getManager(context);

	// Save the LayoutDefinition for future calls to this method
	try {
	    _layoutDefinition = ldm.getLayoutDefinition(key);
	} catch (IOException ex) {
	    throw new IllegalArgumentException(
		    "A LayoutDefinition was not provided for '" + key
		    + "'!  This is required.", ex);
	}

	// Return the LayoutDefinition (if found)
	return _layoutDefinition;
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
	Object[] values = new Object[2];
	values[0] = super.saveState(context);
	values[1] = _ldmKey;
	return values;
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
	Object[] values = (Object[]) state;
	super.restoreState(context, values[0]);
	_ldmKey = (java.lang.String) values[1];
    }

    /**
     *	This method returns the LayoutDefinitionKey for this component.
     *
     *	@return	key	The key to use in the LayoutDefinitionManager
     */
    public String getLayoutDefinitionKey() {
	return _ldmKey;
    }


    /**
     *	This method sets the LayoutDefinition key for this component.
     *
     *	@param	key The key to use in the LayoutDefinitionManager
     */
    public void setLayoutDefinitionKey(String key) {
	_ldmKey = key;
    }

    /**
     *	This is the LayoutDefinition key for this component.  This is
     *	typically set by the Tag.  The Component may also provide a default
     *	by setting it in its constructor.
     */
    private String _ldmKey = null;


    /**
     *	This is a cached reference to the LayoutDefinition used by this
     *	UIComponent.
     */
    private transient LayoutDefinition _layoutDefinition = null;
}
