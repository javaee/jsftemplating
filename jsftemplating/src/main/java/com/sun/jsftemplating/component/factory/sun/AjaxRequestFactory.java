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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.factory.ComponentFactoryBase;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;


/**
 *  <p>	This factory is responsible for instantiating a <code>Hyperlink
 *	UIComponent</code> that is configured to submit an Ajax
 *	request.</p>
 *
 *  <p>	All properties are passed through to the underlying
 *	<code>Hyperlink</code> UIComponent.</p>
 *
 *  <p>	The {@link com.sun.jsftemplating.layout.descriptors.ComponentType}
 *	id for this factory is: "ajaxRequest".</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("ajaxRequest")
public class AjaxRequestFactory extends ComponentFactoryBase {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.  You should specify the
     *	    <code>ajaxTarget</code> for this component.  See
     *	    {@link #AJAX_TARGET}.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>AjaxRequest</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	// Create the UIComponent
	UIComponent comp = createComponent(context, COMPONENT_TYPE, descriptor, parent);

	// Set all the attributes / properties
	setOptions(context, descriptor, comp);

	// Setup the AjaxRequest Request
// FIXME: support javascript function to handle return value
// FIXME: support extra NVPs??  Maybe not needed?  UIParameter instead?  May be easier as a &n=v&... string.
	String clientId = comp.getClientId(context);
// FIXME: XXX DEAL WITH THIS!!!
// FIXME: BUG: JSF automatically converts the '&' characters in attributes to &amp; this causes a problem... talk to LH and JSF about this.
//	String extraNVPs = "'&" + clientId + "_submittedField=" + clientId + "'";
	String extraNVPs = "'" + clientId + "_submittedField=" + clientId + "'";
	String jsHandlerFunction = "null";
// FIXME: Support multiple targets?  If I render multiple sections of the UIComponent tree and return a document describing the results...
	String target = (String) descriptor.getEvaluatedOption(context, AJAX_TARGET, comp);
	if ((target == null) || target.equals("")) {
	    target = clientId;
	}
	comp.getAttributes().put("onClick", "submitAjaxRequest('" + target + "', " +extraNVPs + ", " + jsHandlerFunction + "); return false;");

	// Return the component
	return comp;
    }

    /**
     *	<p> This is the property name that specifies the target for the
     *	    Ajax request.  If not specified, the link itself will be the target
     *	    (which is likely not the desired target of the XMLHttpRequest).
     *	    The target should be the clientId (i.e. the "id" you see in the
     *	    HTML source) of the <code>UIComponent</code>.</p>
     *
     *	<p> The property name is: <code>ajaxTarget</code>.</p>
     */
    public static final String AJAX_TARGET	=   "ajaxTarget";

    /**
     *	<p> The <code>UIComponent</code> type that must be registered in the
     *	    <code>faces-config.xml</code> file mapping to the UIComponent class
     *	    to use for this <code>UIComponent</code>.</p>
     */
    public static final String COMPONENT_TYPE	=   "com.sun.webui.jsf.Hyperlink";
}
