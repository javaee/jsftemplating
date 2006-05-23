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
package com.sun.jsftemplating.component.factory;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.util.LogUtil;

import java.util.Iterator;
import java.util.Map;

// JSF 1.2 specific... don't do this yet...
//import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding; // JSF 1.1
import javax.faces.webapp.UIComponentTag;


/**
 *  <p>	This abstract class provides common functionality for UIComponent
 *	factories.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public abstract class ComponentFactoryBase implements ComponentFactory {

    /**
     *	<p> This method iterates through the Map of options.  It looks at each
     *	    one, if it contians an EL expression, it sets a value binding.
     *	    Otherwise, it calls setAttribute() on the component (which in turn
     *	    will invoke the bean setter if there is one).</p>
     *
     *	<p> This method also interates through the child
     *	    <code>LayoutElement</code>s of the given {@link LayoutComponent}
     *	    descriptor and adds Facets or children as appropriate.</p>
     *
     *	@param	context	The <code>FacesContext</code>
     *	@param	desc    The {@link LayoutComponent} descriptor associated with
     *			the requested <code>UIComponent</code>.
     *	@param	comp	The <code>UIComponent</code>
     */
    protected void setOptions(FacesContext context, LayoutComponent desc, UIComponent comp) {
	if (desc == null) {
	    // Nothing to do
	    return;
	}

	// First set the id if supplied, treated special b/c the component
	// used for ${} expressions is the parent and this must be set first
	// so other ${} expressions can use $this{id} and $this{clientId}.
	String compId = (String) desc.getId(context, comp.getParent());
	if ((compId != null) && (!compId.equals(""))) {
	    comp.setId(compId);
	}

	// Loop through all the options and set the values
	Map attributes = comp.getAttributes();
// FIXME: Figure a way to skip options that should not be set on the Component
	Iterator it = desc.getOptions().keySet().iterator();
	Object value = null;
	String strVal = null;
	String key = null;
	while (it.hasNext()) {
	    // Get next property
	    key = (String) it.next();
	    value = desc.getEvaluatedOption(context, key, comp);

	    // Next check to see if the value contains a ValueExpression
	    strVal = "" + value;
	    if (UIComponentTag.isValueReference(strVal)) {
		/*
		1.2+
		ValueExpression ve =
		    context.getApplication().getExpressionFactory().
			createValueExpression(
				context.getELContext(), strVal, Object.class);
		comp.setValueExpression((String) key, ve);
		*/
		// JSF 1.1 VB:
		ValueBinding vb =
		    context.getApplication().createValueBinding(strVal);
		comp.setValueBinding((String) key, vb);
	    } else {
		// In JSF, you must directly modify the attribute Map
		try {
		    attributes.put(key, value);
		} catch (NullPointerException ex) {
		    // Setting null, assume they want to remove the value
		    attributes.remove(key);
		}
	    }
	}

	// Set the events on the new component
	storeInstanceHandlers(desc, comp);
    }

    /**
     *	<p> This method is responsible for interating over the "instance"
     *	    handlers and applying them to the UIComponent.  An "instance"
     *	    handler is one that is defined <b>outside a renderer</b>, or <b>a
     *	    nested component within a renderer</b>.  In other words, a handler
     *	    that would not get fired by the TemplateRenderer.  By passing this
     *	    in via the UIComponent, code that is aware of events (see
     *	    {@link com.sun.jsftemplating.layout.descriptors.LayoutElementBase})
     *	    may find these events and fire them.  These may vary per "instance"
     *	    of a particular component (i.e. <code>TreeNode</code>) unlike the
     *	    handlers defined in a TemplateRender's XML (which are shared and
     *	    therefor should not change dynamically).</p>
     *
     *	<p> This method is invoked from setOptions(), however, if setOptions
     *	    is not used in by a factory, this method may be invoked directly.
     *	    Calling this method multiple times will not cause any harm,
     *	    besides making an extra unnecessary call.</p>
     *
     *	@param	desc	The descriptor potentially containing handlers to copy.
     *	@param	comp	The UIComponent instance to store the handlers.
     */
    protected void storeInstanceHandlers(LayoutComponent desc, UIComponent comp) {
	if (!desc.isNested()) {
	    // This is not a nested LayoutComponent, it does should not store
	    // instance handlers
	    return;
	}

	// Iterate over the instance handlers
	Iterator it = desc.getHandlersByTypeMap().keySet().iterator();
	if (it.hasNext()) {
	    String eventType = null;
	    Map compAttrs = comp.getAttributes();
	    while (it.hasNext()) {
		// Assign instance handlers to attribute for retrieval later
		//   (NOTE: retrieval must be explicit, see LayoutElementBase)
		eventType = (String) it.next();
		if (eventType.equals(LayoutComponent.BEFORE_CREATE)) {
		    // This is handled directly, no need for instance handler
		    continue;
		} else if (eventType.equals(LayoutComponent.AFTER_CREATE)) {
		    // This is handled directly, no need for instance handler
		    continue;
		}
		compAttrs.put(eventType, desc.getHandlers(eventType));
	    }
	}
    }

    /**
     *	<p> This method associates the given child with the given parent.  By
     *	    using this method we centralize the code so that if we decide
     *	    later to add it as a real child it can be done in one place.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *	@param	child	    The child <code>UIComponent</code>
     */
    protected void addChild(FacesContext context, LayoutComponent descriptor, UIComponent parent, UIComponent child) {
	if (descriptor.isFacetChild()) {
	    String name = (String) descriptor.getEvaluatedOption(
		    context, LayoutComponent.FACET_NAME, child);
	    if (name != null) {
		parent.getFacets().put(name, child);
	    } else {
		// Warn the developer that they may have a problem
		if (LogUtil.configEnabled()) {
		    LogUtil.config("Warning: no facet name was supplied for '"
			    + descriptor.getId(context, child) + "'!");
		}

		// Set the parent
		child.setParent(parent);
	    }
	} else {
	    // Add this as an actual child
	    parent.getChildren().add(child);
	}
    }
}
