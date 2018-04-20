/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006-2018 Oracle and/or its affiliates. All rights reserved.
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
