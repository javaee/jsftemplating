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

package com.sun.jsftemplating.component.factory.sun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.component.dataprovider.MultipleListDataProvider;
import com.sun.jsftemplating.component.factory.ComponentFactoryBase;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.util.LogUtil;


/**
 *  <p>	This factory is responsible for instantiating a <code>TableRowGroup
 *	UIComponent</code>.</p>
 *
 *  <p>	The {@link com.sun.jsftemplating.layout.descriptors.ComponentType}
 *	id for this factory is: "sun:tableRowGroup".</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("sun:tableRowGroup")
public class TableRowGroupFactory extends ComponentFactoryBase {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>TableRowGroup</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	// Create the UIComponent
	UIComponent comp = createComponent(context, COMPONENT_TYPE, descriptor, parent);

	// Set all the attributes / properties
	setOptions(context, descriptor, comp);

	// Handle "data" option specially...
	ComponentUtil compUtil = ComponentUtil.getInstance(context);
	Object data = compUtil.resolveValue(
		context, descriptor, comp, descriptor.getOption("data"));
	if (data != null) {
	    // Create a DataProvider
	    if (!(data instanceof List)) {
		throw new IllegalArgumentException("TableRowGroupFactory "
			+ "only supports values of type \"java.util.List\" "
			+ "for the 'data' attribute.");
	    }
	    if ((((List) data).size() > 0) &&
		    !(((List) data).get(0) instanceof List)) {
		Object obj = ((List) data).get(0);
		if (obj == null) {
		    // In this case, treat this as an empty List and log a
		    // warning... this prevents some cases from blowing up
		    // just b/c there is no data.
		    if (LogUtil.configEnabled()) {
			LogUtil.config("JSFT0008");
		    }
		    ((List) data).set(0, new ArrayList());
		} else {
		    obj = obj.getClass().getName();
		    // We don't have a List of List of Object!
		    throw new IllegalArgumentException("TableRowGroupFactory "
			    + "expects a List<List<Object>>.  Where the outer "
			    + "List should be a List of sources, and the inner"
			    + " List should be a List of rows.  However, the "
			    + "following was passed in: List<" + obj + ">.");
		}
	    }
	    List<List<Object>> lists = (List<List<Object>>) data;
	    Object dataProvider = new MultipleListDataProvider(lists);

	    // Remove the data object from the UIComponent, not needed
	    Map<String, Object> atts = comp.getAttributes();
	    atts.remove("data");
// FIXME: This stores the *data* in the UIComponent... change to use a #{} value binding to push the data somewhere else. Session?? Configurable?
	    atts.put("sourceData", dataProvider);
	}

	// Return the component
	return comp;
    }

    /**
     *	<p> The <code>UIComponent</code> type that must be registered in the
     *	    <code>faces-config.xml</code> file mapping to the UIComponent class
     *	    to use for this <code>UIComponent</code>.</p>
     */
    public static final String COMPONENT_TYPE	= "com.sun.webui.jsf.TableRowGroup";
}
