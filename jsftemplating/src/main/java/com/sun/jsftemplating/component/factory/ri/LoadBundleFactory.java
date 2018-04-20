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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.EventComponent;
import com.sun.jsftemplating.component.factory.ComponentFactoryBase;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;


/**
 *  <p>	When using JSP as the view technology for JSF, you not only have
 *	components but also JSP tags that interact with JSF.  In JSFTemplating
 *	the recommended approach to doing this is to use handlers.  This
 *	offers a clean way to execute arbitrary Java code.  While that is still
 *	the recommendation, this class is provided for added flexibility.  The
 *	purpose of this class is to read a ResourceBundle and make it available
 *	to the page.  The better approach would be to use the
 *	{@link com.sun.jsftemplating.handlers.ScopeHandlers#setResourceBundle}
 *	handler.</p>
 *
 *  <p>	The &gt;f:loadBundle&lt; tag does not represent a component, so this
 *	handler does not create a component, it returns the <code>parent</code>
 *	(which is passed in) after configuring the
 *	<code>ResourceBundle</code>.</p>
 *
 *  <p>	The {@link com.sun.jsftemplating.layout.descriptors.ComponentType}
 *	id for this factory is: "f:loadBundle".  It requires a
 *	<code>basename</code> and a <code>var</code> property to be passed
 *	in.  Optionally the <code>locale</code> can be passed in (this is not
 *	a feature of the JSF version, but an extra feature supported by the
 *	handler in JSFTemplating.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("f:loadBundle")
public class LoadBundleFactory extends ComponentFactoryBase {

    /**
     *	<p> This is the factory method loads a <code>ResourceBundle</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor.
     *	@param	parent	    The parent <code>UIComponent</code> (not used)
     *
     *	@return	<code>parent</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	// Create an Event component for this
	EventComponent event = new EventComponent();
	if (parent != null) {
	    addChild(context, descriptor, parent, event);
	}

	// Get the inputs
	String baseName = (String) descriptor.getEvaluatedOption(context, "basename", parent);
	String var = (String) descriptor.getEvaluatedOption(context, "var", parent);
	Locale locale = (Locale) descriptor.getEvaluatedOption(context, "locale", parent);

	// Create a handler (needed to execute code each time displayed)...
	HandlerDefinition def = LayoutDefinitionManager.
		getGlobalHandlerDefinition("setResourceBundle");
	Handler handler = new Handler(def);
	handler.setInputValue("bundle", baseName);
	handler.setInputValue("key", var);
	handler.setInputValue("locale", locale);
	List<Handler> handlers = new ArrayList<Handler>();
	handlers.add(handler);
	event.getAttributes().put("beforeEncode", handlers);

	// Return (parent)
	return event;
    }
}
