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
package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.el.ValueExpression;

import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;


/**
 *  <p>	This class defines a LayoutStaticText.  A LayoutStaticText describes a
 *	text to be output to the screen.  This element is NOT a
 *	<code>UIComponent</code>.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutStaticText extends LayoutComponent {
    private static final long serialVersionUID = 1L;

    /**
     *	<p> Constructor.</p>
     */
    public LayoutStaticText(LayoutElement parent, String id, String value) {
	super(parent, id,
	    LayoutDefinitionManager.getGlobalComponentType(null, "staticText"));
	addOption("value", value);
	_value = value;
    }

    /**
     *
     */
    public String getValue() {
	return _value;
    }

    /**
     *	<p> This method displays the text described by this component.  If the
     *	    text includes an EL expression, it will be evaluated.  It returns
     *	    false to avoid attempting to render children.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	component   The <code>UIComponent</code>
     *
     *	@return	false
     */
    public boolean encodeThis(FacesContext context, UIComponent component) throws IOException {
	// Get the ResponseWriter
	ResponseWriter writer = context.getResponseWriter();

	// Render the child UIComponent
//	if (staticText.isEscape()) {
//	    writer.writeText(getValue(), "value");
//	} else {
	    // This code depends on the side-effect of Util.setOption
	    // converting the string to a ValueExpression if needed.  The
	    // "__value" is arbitrary.
	    Object value = ComponentUtil.getInstance(context).setOption(
		context, "__value", getValue(),
		getLayoutDefinition(), component);

	    // JSF 1.2 VB:
	    if (value instanceof ValueExpression) {
		value =
		    ((ValueExpression) value).getValue(context.getELContext());
	    }

	    if (value != null) {
		writer.write(value.toString());
	    }
//	}

	// No children
	return false;
    }

    private String _value   = null;
}
