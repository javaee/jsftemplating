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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.component.factory.ComponentFactoryBase;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.util.Util;


// FIXME: Document
/**
 *  <p>	This factory is responsible for instantiating a <code>DropDrown
 *	UIComponent</code>.</p>
 *
 *  <p>	The {@link com.sun.jsftemplating.layout.descriptors.ComponentType}
 *	id for this factory is: "sun:dropDown".</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("sun:dropDown")
public class DropDownFactory extends ComponentFactoryBase {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>DropDown</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	// Create the UIComponent
	UIComponent comp = createComponent(context, getComponentType(), descriptor, parent);

	// Set all the attributes / properties
	setOptions(context, descriptor, comp);

	// Check to see if the user is passing in Lists to be converted to a
	// List of Option objects for the "items" property.
	ComponentUtil compUtil = ComponentUtil.getInstance(context);
	Object labels = compUtil.resolveValue(
		context, descriptor, comp, descriptor.getOption("labels"));
	if (labels != null) {
	    List optionList = new ArrayList();
	    Object values = compUtil.resolveValue(
		    context, descriptor, comp, descriptor.getOption("values"));
	    if (values == null) {
		values = labels;
	    }

	    try {
		// Use reflection (for now) to avoid a build dependency
		// Find the Option constuctor...
		Constructor optConst = Util.loadClass(
		    "com.sun.webui.jsf.model.Option", this).
		    getConstructor(Object.class, String.class);

		if (values instanceof List) {
		    // We have a List, we need to convert to Option objects.
		    Iterator<Object> it = ((List<Object>) labels).iterator();
		    for (Object obj : (List<Object>) values) {
			optionList.add(
			    optConst.newInstance(obj, it.next().toString()));
		    }
		} else if (values instanceof Object[]) {
		    Object [] valArr = (Object []) values;
		    Object [] labArr = (Object []) labels;
		    int len = valArr.length;
		    // Convert the array to Option objects
		    for (int count = 0; count < len; count++) {
			optionList.add(
			    optConst.newInstance(
				valArr[count], labArr[count].toString()));
		    }
		}
	    } catch (ClassNotFoundException ex) {
		ex.printStackTrace();
	    } catch (NoSuchMethodException ex) {
		ex.printStackTrace();
	    } catch (InstantiationException ex) {
		ex.printStackTrace();
	    } catch (IllegalAccessException ex) {
		ex.printStackTrace();
	    } catch (InvocationTargetException ex) {
		ex.printStackTrace();
	    }

	    // Set the options
	    comp.getAttributes().put("items", optionList);
	}

	// Return the component
	return comp;
    }

    /**
     *	<p> This method returns the ComponentType of this component.  It is
     *	    implemented this way to allow subclasses to easily provide a
     *	    different ComponentType.</p>
     */
    protected String getComponentType() {
	return COMPONENT_TYPE;
    }

    /**
     *	<p> The <code>UIComponent</code> type that must be registered in the
     *	    <code>faces-config.xml</code> file mapping to the UIComponent class
     *	    to use for this <code>UIComponent</code>.</p>
     */
    public static final String COMPONENT_TYPE	= "com.sun.webui.jsf.DropDown";
}
