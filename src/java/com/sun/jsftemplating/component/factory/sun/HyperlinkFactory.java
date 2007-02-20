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
import com.sun.jsftemplating.el.VariableResolver;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import java.util.Map;


/**
 *  <p>	This factory is responsible for instantiating a <code>Hyperlink
 *	UIComponent</code>.</p>
 *
 *  <p>	The {@link com.sun.jsftemplating.layout.descriptors.ComponentType}
 *	id for this factory is: "sun:hyperlink".</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("sun:hyperlink")
public class HyperlinkFactory extends ComponentFactoryBase {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>Hyperlink</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	// Create the UIComponent
	UIComponent comp = createComponent(context, COMPONENT_TYPE, descriptor, parent);

	// Set all the attributes / properties
	setOptions(context, descriptor, comp);

	// FIXME: Hack!  This is a hack to fix the Hyperlink component.  This
	//	  component adds a hidden field to the form after it submits.
	//	  This hack is here to remove this hidden field so that it does
	//	  not mess things up in the case of a frames environment (or
	//	  any environment where the result is not itself).  Hopefully
	//	  this will be fixed soon making this hack unnecessary.
	Map<String, Object> options = descriptor.getOptions();
	if ((options.get("target") != null) && (options.get("url") == null)) {
	    // This is a submit hyperlink that may target a different window
	    Object onClick = options.get("onClick");
	    onClick = (onClick == null) ? HACK_JS :
		(onClick.toString() + "; " + HACK_JS);
	    comp.getAttributes().put("onClick",
		VariableResolver.resolveVariables(
		    context, descriptor, comp, onClick));
	}

	// Return the component
	return comp;
    }

    /**
     *	<p> JavaScript to Hack Hyperlink to do the right thing when submitting
     *	    a form.  Remove when Hyperlink component is fixed.</p>
     */
    private static final String HACK_JS	=
	"setTimeout('var child = document.getElementById("
	+ "\"$this{clientid}_submittedField\"); "
	+ "child.parentNode.removeChild(child);', 500);";

    /**
     *	<p> The <code>UIComponent</code> type that must be registered in the
     *	    <code>faces-config.xml</code> file mapping to the UIComponent class
     *	    to use for this <code>UIComponent</code>.</p>
     */
    public static final String COMPONENT_TYPE	= "com.sun.webui.jsf.Hyperlink";
}
