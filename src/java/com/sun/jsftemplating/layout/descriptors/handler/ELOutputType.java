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
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.layout.descriptors.handler;

import com.sun.jsftemplating.component.ComponentUtil;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *  <p>	This class implements the OutputType interface to provide a way to
 *	get/set Output values via standard EL.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class ELOutputType implements OutputType {

    /**
     *	<p> This method is responsible for retrieving the value of the output
     *	    from EL.  The "key" is expected to be the EL expression, including
     *	    the opening and closing delimiters: #{some.el}</p>
     *
     *	@param	context	    The HandlerContext.
     *
     *	@param	outDesc	    The {@link IODescriptor} for this output in which
     *			    we are obtaining a value (not used).
     *
     *	@param	key	    The EL expression to evaluate.
     *
     *	@return The requested value.
     */
    public Object getValue(HandlerContext context, IODescriptor outDesc, String key) {
	if (key == null) {
	    throw new IllegalArgumentException(
		"ELOutputType's key may not be null!");
	}

	// See if we can find the UIComp...
	UIComponent uicomp = null;
	Object eventObj = context.getEventObject();
	if (eventObj instanceof UIComponent) {
	    uicomp = (UIComponent) eventObj;
	}

	// Get it from the EL expression
	return ComponentUtil.resolveValue(
	    context.getFacesContext(), context.getLayoutElement(), uicomp, key);
    }

    /**
     *	<p> This method is responsible for setting the value of the output via
     *	    EL.  The "key" is expected to be the EL expression, including
     *	    the opening and closing delimiters: #{some.el}</p>
     *
     *	@param	context	    The HandlerContext.
     *
     *	@param	outDesc	    The IODescriptor for this Output value in which to
     *			    obtain the value.  Used to pull EL expression from
     *			    the Handler.
     *
     *	@param	key	    The EL expression to evaluate.
     *
     *	@param	value	    The value to set.
     */
    public void setValue(HandlerContext context, IODescriptor outDesc, String key, Object value) {
	// It should never be null...
	if (key == null) {
	    throw new IllegalArgumentException(
		"ELOutputType's key may not be null!");
	}

	// Make sure it is an EL expression...
	if (!key.startsWith("#{")) {
	    // If the key is not an EL expression, make it one... while this
	    // may cover some user-errors, I think it adds a nice ease-of-use
	    // feature that people may like...
	    key = "#{requestScope['" + key + "']}";
	}

	// Set it in EL
	FacesContext facesContext = context.getFacesContext();
	ELContext elctx = facesContext.getELContext();
	ValueExpression ve =
		facesContext.getApplication().getExpressionFactory().
		    createValueExpression(elctx, key, Object.class);
	try {
	    ve.setValue(elctx, value);
	} catch (ELException ex) {
	    throw new RuntimeException(
		"Unable to set handler output value named \""
		+ outDesc.getName() + "\" mapped to EL expression \""
		+ key + "\" on the \""
		+ context.getLayoutElement().getUnevaluatedId()
		+ "\" element.", ex);
	}
    }
}
