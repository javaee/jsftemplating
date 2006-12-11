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
package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.util.LogUtil;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

// Dynamic Faces Classes
import com.sun.faces.extensions.avatar.components.PartialTraversalViewRoot;


/**
 *  <p>	This is a <code>UIViewRoot</code> implemenation that allows the
 *	<code>UIComponent</code> tree to be defined by a
 *	{@link LayoutDefinition}.  This implementation is used by the
 *	{@link LayoutViewHandler} implementation in this same package.  It is
 *	expected that {@link #setLayoutDefinitionKey(String)} will be invoked
 *	soon after creation.  This key will be resolved by the configured
 *	{@link LayoutDefinitionManager} to locate the {@link LayoutDefinition}
 *	which defines the <code>UIComponent</code> tree.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutViewRoot extends PartialTraversalViewRoot {

    /**
     *	<p> Constructor.</p>
     */
    public LayoutViewRoot() {
	super();
    }

    /**
     *	<p> This method enables the decode event to work for pages.</p>
     *
     *	<p> This method checks for Ajax requests and treats them differently
     *	    than normal requests.  It only decodes the targeted UIComponent
     *	    (and its children), then invokes processApplication(), and finally
     *	    renders a partial response (rendering change is actually handled
     *	    by LayoutViewHandler).</p>
     *
     *	<p> When decoding template-based components, this is handled by the
     *	    TemplateRenderer.  However, when dealing with pages, this is done
     *	    here (TemplateRenderer is not involved to fire handlers).</p>
     *
     *	<p> This method continues to delegate to the superclass after invoking
     *	    any registered handlers.</p>
     */
    public void processDecodes(FacesContext context) {

// BEGIN EXPERIMENTAL CODE...
	ExternalContext extCtx = context.getExternalContext();
	String targetId = extCtx.getRequestParameterMap().get(LayoutViewHandler.AJAX_REQ_KEY);
	if ((targetId != null) && !targetId.equals("")) {
	    // Detected Ajax Request
	    // This request will only process a sub-tree of the UIComponent
	    // tree and return the cooresponding partial HTML

	    // First find the Ajax target
	    UIComponent target = findComponent(":"+targetId);
	    if (target == null) {
		// FIXME: Log a warning message!
		// FIXME: Rework this so that the following 6 lines are duplicated
		LayoutDefinition def = getLayoutDefinition(context);
		if (def != null) {
		    def.decode(context, this);
		}
		super.processDecodes(context);
		return;
	    }
	    extCtx.getRequestMap().put(LayoutViewHandler.AJAX_REQ_TARGET_KEY, target);

	    // Process sub-tree (similar to immedate, no validation/update)
	    target.processDecodes(context);
	    processApplication(context);

	    // Mark the context that the next phase should be RenderResponse
	    context.renderResponse();
	} else {
    // END EXPERIMENTAL CODE...

	    LayoutDefinition def = getLayoutDefinition(context);
	    if (def != null) {
		def.decode(context, this);
	    }
	    super.processDecodes(context);
	}
    }

    /**
     *	<p> This method provides the ability to obtain a "child"
     *	    <code>UIComponent</code> from this <code>UIViewRoot</code>.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *	@param	id	The <code>id</code> of <code>UIComponent</code> child.
     *
     *	@return	The requested <code>UIComponent</code> or null if not found.
     */
    public UIComponent getChild(FacesContext context, String id) {
	if ((id == null) || (id.trim().equals("")))  {
	    // No id, no LayoutComponent, nothing we can do.
	    return null;
	}

	// We have an id, use it to search for an already-created child
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
     *	<p> This method provides the ability to obtain a "child"
     *	    <code>UIComponent</code> from this <code>UIViewRoot</code>.  If
     *	    the child does not already exist, it will be created using the
     *	    given {@link LayoutComponent} descriptor.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	descriptor  The {@link LayoutComponent} for the
     *			    <code>UIComponent</code> child.
     *
     *	@return	The requested <code>UIComponent</code>.
     *
     *	@throws IllegalArgumentException if descriptor is null.
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
     *	<p> Returns the {@link LayoutDefinition}.  If the
     *	    {@link LayoutDefinition} has not already be retrieved, it will be
     *	    found using the set {@link LayoutDefinition} "key".  If the key is
     *	    not yet set, this method will return null.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *
     *	@return	The {@link LayoutDefinition} for this <code>UIViewRoot</code>.
     */
    public LayoutDefinition getLayoutDefinition(FacesContext context) throws LayoutDefinitionException {
	// Make sure we don't already have it...
	if (_layoutDefinition != null) {
	    return _layoutDefinition;
	}

	// Get the LayoutDefinitionManager key
	String key = getLayoutDefinitionKey();
	if (key == null) {
	    return null;
	}

	// Save the LayoutDefinition for future calls to this method
	_layoutDefinition =
	    LayoutDefinitionManager.getLayoutDefinition(context, key);

	// Return the LayoutDefinition (if found)
	return _layoutDefinition;
    }

    /**
     *	<p> Accessor method for the <code>LayoutDefintionKey</code>.</p>
     *
     *	@return The {@link LayoutDefinition} key.
     */
    public String getLayoutDefinitionKey() {
	return _ldmKey;
    }

    /**
     *	<p> Setter for the <code>LayoutDefintionKey</code>.</p>
     *
     *	@param key  The {@link LayoutDefinition} key.
     */
    public void setLayoutDefinitionKey(String key) {
	_ldmKey = key;
    }

    /**
     *	<p> This method saves the state for this component.  It relies on the
     *	    superclass to save its own sate, this method will invoke
     *	    super.saveState().</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *
     *	@return The serialized state.
     */
    public Object saveState(FacesContext context) {
	Object [] values = new Object[2];
	values[0] = super.saveState(context);
	values[1] = _ldmKey;
	return values;
    }

    /**
     *	<p> This method restores the state for this component.  It will invoke
     *	    the superclass to restore its state.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *	@param	state	The serialized state.
     */
    public void restoreState(FacesContext context, Object state) {
	Object [] values = (Object[]) state;
	super.restoreState(context, values[0]);
	_ldmKey = (java.lang.String) values[1];
    }


    private String _ldmKey = null;
    private transient LayoutDefinition _layoutDefinition = null;
}
