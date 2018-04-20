/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2018 Oracle and/or its affiliates. All rights reserved.
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

	// Make sure it is an EL expression...
	if (!key.startsWith("#{")) {
	    // If the key is not an EL expression, make it one... while this
	    // may cover some user-errors, I think it adds a nice ease-of-use
	    // feature that people may like...
	    key = "#{requestScope['" + key + "']}";
	}

	// See if we can find the UIComp...
	UIComponent uicomp = null;
	Object eventObj = context.getEventObject();
	if (eventObj instanceof UIComponent) {
	    uicomp = (UIComponent) eventObj;
	}

	// Get it from the EL expression
	FacesContext ctx = context.getFacesContext();
	return ComponentUtil.getInstance(ctx).resolveValue(
	    ctx, context.getLayoutElement(), uicomp, key);
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
