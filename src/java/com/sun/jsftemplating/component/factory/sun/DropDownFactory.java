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
package com.sun.jsftemplating.component.factory.sun;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.factory.ComponentFactoryBase;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.util.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


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
	UIComponent comp = context.getApplication().createComponent(COMPONENT_TYPE);

	// This needs to be done here (before setOptions) so that $...{...}
	// expressions can be resolved... may want to defer these?
	if (parent != null) {
	    addChild(context, descriptor, parent, comp);
	}

	// Set all the attributes / properties
	setOptions(context, descriptor, comp);

	// Check to see if the user is passing in Lists to be converted to a
	// List of Option objects for the "items" property.
	Object labels = descriptor.getEvaluatedOption(context, "labels", comp);
	if (labels != null) {
	    List optionList = new ArrayList();
	    Object values = descriptor.getEvaluatedOption(context, "values", comp);
	    if (values == null) {
		values = labels;
	    }

	    try {
		// Use reflection (for now) to avoid a build dependency
		// Find the Option constuctor...
		Constructor optConst = Util.getClassLoader(this).
		    loadClass("com.sun.webui.jsf.model.Option").
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
     *	<p> The <code>UIComponent</code> type that must be registered in the
     *	    <code>faces-config.xml</code> file mapping to the UIComponent class
     *	    to use for this <code>UIComponent</code>.</p>
     */
    public static final String COMPONENT_TYPE	= "com.sun.webui.jsf.DropDown";
}
