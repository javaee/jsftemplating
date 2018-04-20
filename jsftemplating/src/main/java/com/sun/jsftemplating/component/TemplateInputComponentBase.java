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

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;


/**
 *  <p>	This abstract class provides base functionality for components that
 *	work in conjunction with the
 *	{@link com.sun.jsftemplating.renderer.TemplateRenderer} and would like
 *	to provide <code>UIInput</code> functionality.  It provides a default
 *	implementation of the {@link TemplateComponent} interface.</p>
 *
 *  @see    com.sun.jsftemplating.renderer.TemplateRenderer
 *  @see    com.sun.jsftemplating.component.TemplateComponent
 *  @see    com.sun.jsftemplating.component.TemplateComponentHelper
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public abstract class TemplateInputComponentBase extends UIInput implements TemplateComponent {

    /**
     *	<p> This method will find the request child <code>UIComponent</code>
     *	    by id.  If it is not found, it will attempt to create it if it can
     *	    find a {@link LayoutElement} describing it.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	id	    The <code>UIComponent</code> id to find.
     *
     *	@return	The requested <code>UIComponent</code>.
     */
    public UIComponent getChild(FacesContext context, String id) {
	return getHelper().getChild(this, context, id);
    }


    /**
     *	<p> This method will find the request child <code>UIComponent</code> by
     *	    id (the id is obtained from the given {@link LayoutComponent}).  If
     *	    it is not found, it will attempt to create it from the supplied
     *	    {@link LayoutElement}.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	descriptor  The {@link LayoutElement} describing the <code>UIComponent</code>
     *
     *	@return	The requested <code>UIComponent</code>.
     */
    public UIComponent getChild(FacesContext context, LayoutComponent descriptor) {
	return getHelper().getChild(this, context, descriptor);
    }

    /**
     *	<p> This method returns the {@link LayoutDefinition} associated with
     *	    this component.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *
     *	@return	{@link LayoutDefinition} associated with this component.
     */
    public LayoutDefinition getLayoutDefinition(FacesContext context) {
	return getHelper().getLayoutDefinition(context);
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
	return getHelper().saveState(context, super.saveState(context));
    }

    /**
     *	<p> This method restores the state for this component.  It will invoke
     *	    the superclass to restore its state.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *	@param	state	The serialized state.
     */
    public void restoreState(FacesContext context, Object state) {
	super.restoreState(context, getHelper().restoreState(context, state));
    }

    /**
     *	<p> This method returns the {@link LayoutDefinition} key for this
     *	    component.</p>
     *
     *	@return	The key to use in the {@link LayoutDefinitionManager}.
     */
    public String getLayoutDefinitionKey() {
	return getHelper().getLayoutDefinitionKey();
    }


    /**
     *	<p> This method sets the {@link LayoutDefinition} key for this
     *	    component.</p>
     *
     *	@param	key The key to use in the {@link LayoutDefinitionManager}.
     */
    public void setLayoutDefinitionKey(String key) {
	getHelper().setLayoutDefinitionKey(key);
    }

    public <V> V getPropertyValue(V field, String attributeName, V defaultValue) {
        return getHelper().getAttributeValue(this, field, attributeName, defaultValue);
    }

    /**
     *	<p> This method retrieves the {@link TemplateComponentHelper} used by
     *	    this class to help implement the {@link TemplateComponent}
     *	    interface.</p>
     *
     *	@return	The {@link TemplateComponentHelper} for this component.
     */
    protected TemplateComponentHelper getHelper() {
	if (_helper == null) {
	    _helper = new TemplateComponentHelper();
	}
	return _helper;
    }

    /**
     *	<p> Our <code>TemplateComponentHelper</code>.  We initialize it on
     *	    access b/c we want to ensure it exists, if it is serialized it
     *	    won't exist if we init it here or in the constructor.</p>
     */
    private transient TemplateComponentHelper _helper = null;
}
