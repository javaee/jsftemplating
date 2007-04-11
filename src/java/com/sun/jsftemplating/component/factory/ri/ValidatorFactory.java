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
package com.sun.jsftemplating.component.factory.ri;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.component.factory.ComponentFactoryBase;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.util.LogUtil;


/**
 *  <p>	This factory is responsible for instantiating a
 *	<code>Validator</code> and adding it to the parent.  This factory
 *	does <b>not</b> create a <code>UIComponent</code>.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("f:validator")
public class ValidatorFactory extends ComponentFactoryBase {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The <b>parent</b> <code>UIComponent</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	Object value = null;
	Validator validator = null;

	// Check for the "binding" property
	String binding = (String) descriptor.getOption("binding");
	if (binding != null) {
	    value = ComponentUtil.resolveValue(context, descriptor, parent, binding);
	    if ((value != null) && !(value instanceof Validator)) {
		// Warn developer that attempted to set a Validator that was
		// not a Validator
		if (LogUtil.warningEnabled()) {
		    LogUtil.warning("JSFT0009", (Object) parent.getId());
		}
	    }
	}

	// Check to see if we still need to create one...
	if (validator == null) {
	    // Check for the "validatorId" property
	    String id = (String) descriptor.getOption("validatorId");
	    if (id != null) {
		id = (String) ComponentUtil.resolveValue(context, descriptor, parent, id);
		if (id != null) {
		    // Create a new Validator
		    Application app = context.getApplication();
		    validator = app.createValidator(id);
		    if ((validator != null) && (binding != null)) {
			// Set the validator on the binding, if bound
			ELContext elctx = context.getELContext();
			ValueExpression ve = app.getExpressionFactory().createValueExpression(elctx, binding, Object.class);
			ve.setValue(elctx, validator);
		    }
		}
	    }
	}

	// Set the validator on the parent...
	if (validator != null) {
	    if (!(parent instanceof EditableValueHolder)) {
		throw new IllegalArgumentException("You may only add "
			+ "f:validator tags to components which are "
			+ "EditableValueHolders.  Component (" + parent.getId()
			+ ") is not an EditableValueHolder.");
	    }
	    ((EditableValueHolder) parent).addValidator(validator);
	}

	// Return the validator
	return parent;
    }
}
