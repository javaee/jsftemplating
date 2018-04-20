/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jsftemplating.component;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

import com.sun.jsftemplating.layout.LayoutDefinitionException;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;


/**
 *  <p>	This class provides base functionality for components that
 *	work in conjunction with the
 *	{@link com.sun.jsftemplating.renderer.TemplateRenderer}.  It
 *	provides the bulk of the default implementation of the
 *	{@link TemplateComponent} interface.</p>
 *
 *  <p>	This class is meant to be used inside a <code>UIComponent</code> class
 *	that implements <code>TemplateComponent</code> to help provide the
 *	behavior of a <code>TemplateComponent</code>.  It is <em>NOT</em> an
 *	implementation by itself.  A <code>TemplateComonent</code>
 *	implementation class may use this to help define its functionality and
 *	must also be an instance of <code>UIComponent</code>.</p>
 *
 *  @see    com.sun.jsftemplating.renderer.TemplateRenderer
 *  @see    TemplateComponent
 *  @see    TemplateComponentBase
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class TemplateComponentHelper {

    /**
     *	<p> This class should only be used by <code>TemplateComponent</code>
     *	    implementations to help them provide their
     *	    <code>TemplateComponent</code> funtionality.</p>
     */
    public TemplateComponentHelper() {
    }

    /**
     *	<p> This method will find the request child UIComponent by id.  If it
     *	    is not found, it will attempt to create it if it can find a
     *	    {@link LayoutElement} describing it.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	id	    The <code>UIComponent</code> id to find.
     *
     *	@return	The requested UIComponent
     */
    public UIComponent getChild(UIComponent comp, FacesContext context, String id) {
	if ((id == null) || (id.trim().equals("")))  {
	    // No id, no LayoutComponent, nothing we can do.
	    return null;
	}

	// We have an id, use it to search for an already-created child
// FIXME: I am doing this 2x if it falls through to create the child...
// FIXME: think about optimizing this
	UIComponent childComponent = ComponentUtil.getInstance(context).findChild(comp, id, id);
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
	    LayoutDefinition.getChildLayoutElementById(context, id, ld, comp);

	// Create the child from the LayoutComponent
	return getChild(comp, context, (LayoutComponent) elt);
    }


    /**
     *	<p> This method will find the request child <code>UIComponent</code>
     *	    by id (the id is obtained from the given {@link LayoutComponent}).
     *	    If it is not found, it will attempt to create it from the supplied
     *	    {@link LayoutElement}.</p>
     *
     *	@param	descriptor  The {@link LayoutElement} describing the <code>UIComponent</code>.
     *
     *	@return	The requested UIComponent
     */
    public UIComponent getChild(UIComponent comp, FacesContext context, LayoutComponent descriptor) {
	UIComponent childComponent = null;

	// Sanity check
	if (descriptor == null) {
	    throw new IllegalArgumentException("The LayoutComponent is null!");
	}

	// First pull off the id from the descriptor
	String id = descriptor.getId(context, comp);
	ComponentUtil compUtil = ComponentUtil.getInstance(context);
	if ((id != null) && !(id.trim().equals(""))) {
	    // We have an id, use it to search for an already-created child
	    childComponent = compUtil.findChild(comp, id, id);
	    if (childComponent != null) {
		return childComponent;
	    }
	}

	// No id, or the component hasn't been created.  In either case, we
	// create a new component (moral: always have an id)

	// Invoke "beforeCreate" handlers
	descriptor.beforeCreate(context, comp);

	// Create UIComponent
	childComponent =
	    compUtil.createChildComponent(context, descriptor, comp);

	// Invoke "afterCreate" handlers
	descriptor.afterCreate(context, childComponent);

	// Return the newly created UIComponent
	return childComponent;
    }

    /**
     *	<p> This method returns the {@link LayoutDefinition} associated with
     *	    the <code>UIComponent</code>.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *
     *	@return	{@link LayoutDefinition} associated with the <code>UIComponent</code>.
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

	// Save the LayoutDefinition for future calls to this method
	try {
	    _layoutDefinition = LayoutDefinitionManager.
		getLayoutDefinition(context, key);
	} catch (LayoutDefinitionException ex) {
	    throw new IllegalArgumentException(
		    "A LayoutDefinition was not provided for '" + key
		    + "'!  This is required.", ex);
	}

	// Return the LayoutDefinition (if found)
	return _layoutDefinition;
    }

    /**
     *	<p> This method saves the state for the <code>UIComponent</code>.  It
     *	    relies on the <code>UIComponent</code>'s superclass to save its own
     *	    sate, and to pass in that <code>Object</code> to this method.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	superState  The <code>UIComponent</code>'s superclass state.
     *
     *	@return The serialized State.
     */
    public Object saveState(FacesContext context, Object superState) {
	Object[] values = new Object[2];
	values[0] = superState;
	values[1] = _ldmKey;
	return values;
    }

    /**
     *	<p> This method restores the state for the <code>UIComponent</code>.
     *	    It will return an <code>Object</code> that must be passed to the
     *	    superclass's <code>restoreState</code> method.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *	@param	state	The serialized state.
     *
     *	@return The State for the superclass to deserialize.
     */
    public Object restoreState(FacesContext context, Object state) {
	Object[] values = (Object[]) state;
	_ldmKey = (java.lang.String) values[1];
	return values[0];
    }

    /**
     *	<p> This method returns the {@link LayoutDefinition} key for the
     *	    <code>UIComponent</code>.</p>
     *
     *	@return	key	The key to use in the {@link LayoutDefinitionManager}.
     */
    public String getLayoutDefinitionKey() {
	return _ldmKey;
    }


    /**
     *	<p> This method sets the LayoutDefinition key for the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	key The key to use in the {@link LayoutDefinitionManager}.
     */
    public void setLayoutDefinitionKey(String key) {
	_ldmKey = key;
    }

    public <V> V getAttributeValue(UIComponent comp, V field, String attributeName, V defaultValue) {
        if (field != null) {
            return field;
        }
        ValueExpression ve = comp.getValueExpression(attributeName);
        return (ve != null) ? (V) ve.getValue(FacesContext.getCurrentInstance().getELContext()) :
                defaultValue;
    }

    /**
     *	<p> This is the LayoutDefinition key for the <code>UIComponent</code>.
     *	    This is typically set by the Tag.  The Component may also provide
     *	    a default by setting it in its constructor.</p>
     */
    private String _ldmKey = null;


    /**
     *	<p> This is a cached reference to the {@link LayoutDefinition} used by
     *	    the <code>UIComponent</code>.</p>
     */
    private transient LayoutDefinition _layoutDefinition = null;
}
