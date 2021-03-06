/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008-2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;

import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;


/**
 *  <p>	This class provides utility methods used by JSFT for working with
 *	<code>UIViewRoot</code> instances.  JSFTemplating no longer provides
 *	its own <code>UIViewRoot</code>, in an effort to make integration
 *	with other frameworks more simple.  The methods in this class perform
 *	operations such as setting the {@link LayoutDefinition} key on the
 *	<code>UIViewRoot</code> as an attribute.  It also allows you go obtain
 *	the {@link LayoutDefinition} used by a specific instance of the
 *	<code>UIViewRoot</code>, or the current instance set in the
 *	<code>FacesContext</code>.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class ViewRootUtil {

    /**
     *	<p> Constructor.  All methods are static, no need to instantiate this
     *	    class.</p>
     */
    private ViewRootUtil() {
    }

    /**
// FIXME: This method was originally from the LayoutViewRoot class (which
// FIXME: extended UIViewRoot).  This method allowed "decode" events to work
// FIXME: on pages.  I still need to replace this functionality w/o extending
// FIXME: the UIViewRoot (if possible).

     *	<p> This method enables the decode event to work for pages.</p>
     *
     *	<p> This method checks for Ajax requests and treats them differently
     *	    than normal requests.  It only decodes the targeted UIComponent
     *	    (and its children), then invokes processApplication(), and finally
     *	    renders a partial response (rendering change is actually handled
     *	    by LayoutViewHandler).</p>
     *
     *	<p> When decoding template-based components, this is handled by the
     *	    TemplateRenderer.  However, when dealing with pages, this is done
     *	    here (TemplateRenderer is not involved to fire handlers).</p>
     *
     *	<p> This method continues to delegate to the superclass after invoking
     *	    any registered handlers.</p>
    public void processDecodes(FacesContext context) {
	// PartialTraversalViewRootHelper may call us in an attempt to call
	// super.processDecodes(), detect this...
	if (!(new RuntimeException().getStackTrace()[1].getClassName().equals(HELPER_NAME)) &&
		!helper.processDecodes(context)) {
	    // Request already handled...
	    return;
	}

// BEGIN EXPERIMENTAL CODE...
	ExternalContext extCtx = context.getExternalContext();
	String targetId = extCtx.getRequestParameterMap().get(LayoutViewHandler.AJAX_REQ_KEY);
	if ((targetId != null) && !targetId.equals("")) {
	    // Detected Ajax Request
	    // This request will only process a sub-tree of the UIComponent
	    // tree and return the cooresponding partial HTML

	    // First find the Ajax target
	    UIComponent target = findComponent(":"+targetId);
	    if (target == null) {
		// FIXME: Log a warning message!
		// FIXME: Rework this so that the following 6 lines are duplicated
		LayoutDefinition def = getLayoutDefinition(context);
		if (def != null) {
		    def.decode(context, this);
		}
		super.processDecodes(context);
		return;
	    }
	    extCtx.getRequestMap().put(LayoutViewHandler.AJAX_REQ_TARGET_KEY, target);

	    // Process sub-tree (similar to immedate, no validation/update)
	    target.processDecodes(context);
	    processApplication(context);

	    // Mark the context that the next phase should be RenderResponse
	    context.renderResponse();
	} else {
    // END EXPERIMENTAL CODE...

	    LayoutDefinition def = getLayoutDefinition(context);
	    if (def != null) {
		def.decode(context, this);
	    }
	    super.processDecodes(context);
	}
    }
     */

    /**
     *	<p> This method provides the ability to obtain a "child"
     *	    <code>UIComponent</code> from this <code>UIViewRoot</code>.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *	@param	id	The <code>id</code> of <code>UIComponent</code> child.
     *
     *	@return	The requested <code>UIComponent</code> or null if not found.
    public UIComponent getChild(FacesContext context, String id) {
	if ((id == null) || (id.trim().equals("")))  {
	    // No id, no LayoutComponent, nothing we can do.
	    return null;
	}

	// We have an id, use it to search for an already-created child
	UIComponent childComponent = ComponentUtil.getInstance(context).findChild(this, id, id);
	if (childComponent != null) {
	    return childComponent;
	}

	// If we're still here, then we need to create it... hopefully we have
	// a LayoutComponent to tell us how to do this!
	LayoutDefinition ld = getLayoutDefinition(context);
	if (ld == null) {
	    // No LayoutDefinition to tell us how to create it... return null
	    return null;
	}

	// Attempt to find a LayoutComponent matching the id
	LayoutElement elt =
	    LayoutDefinition.getChildLayoutElementById(context, id, ld, this);

	// Create the child from the LayoutComponent
	return getChild(context, (LayoutComponent) elt);
    }
     */

    /**
     *	<p> This method provides the ability to obtain a "child"
     *	    <code>UIComponent</code> from this <code>UIViewRoot</code>.  If
     *	    the child does not already exist, it will be created using the
     *	    given {@link LayoutComponent} descriptor.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	descriptor  The {@link LayoutComponent} for the
     *			    <code>UIComponent</code> child.
     *
     *	@return	The requested <code>UIComponent</code>.
     *
     *	@throws IllegalArgumentException if descriptor is null.
    public UIComponent getChild(FacesContext context, LayoutComponent descriptor) {
	UIComponent childComponent = null;

	// Sanity check
	if (descriptor == null) {
	    throw new IllegalArgumentException("The LayoutComponent is null!");
	}

	// First pull off the id from the descriptor
	String id = descriptor.getId(context, this);
	if ((id != null) && !(id.trim().equals(""))) {
	    // We have an id, use it to search for an already-created child
	    childComponent = ComponentUtil.getInstance(context).findChild(this, id, id);
	    if (childComponent != null) {
		return childComponent;
	    }
	}

	// No id, or the component hasn't been created.  In either case, we
	// create a new component (moral: always have an id)

	// Invoke "beforeCreate" handlers
	descriptor.beforeCreate(context, this);

	// Create UIComponent
	childComponent =
	    ComponentUtil.getInstance(context).createChildComponent(context, descriptor, this);

	// Invoke "afterCreate" handlers
	descriptor.afterCreate(context, childComponent);

	// Return the newly created UIComponent
	return childComponent;
    }
     */

    /**
     *	<p> Returns the {@link LayoutDefinition} used by the given
     *	    <code>UIViewRoot</code>.  This method retrieves the
     *	    {@link LayoutDefinition} key from the given <code>UIViewRoot</code>
     *	    (or gets the current <code>UIViewRoot</code> from the
     *	    <code>FacesContext</code> if the value passed in is
     *	    <code>null</code>.  It then invokes the overloaded method
     *	    ({@link #getLayoutDefinition(String)}) with this key.</p>
     *
     *	@param	root	    The <code>UIViewRoot</code> to use.
     *
     *	@return	The {@link LayoutDefinition} for this <code>UIViewRoot</code>.
     */
    public static LayoutDefinition getLayoutDefinition(UIViewRoot root) throws LayoutDefinitionException {
	if (root == null) {
	    // Default to the current UIViewRoot
	    root = FacesContext.getCurrentInstance().getViewRoot();
	}
	return (root == null) ? null : getLayoutDefinition((String)
		getLayoutDefinitionKey(root));
    }

    /**
     *	<p> This method returns the {@link LayoutDefinition} for the given
     *	    <code>key</code>.  If the {@link LayoutDefinition} has already be
     *	    retrieved during this request, it will be returned.  Otherwise, it
     *	    will ask the {@link LayoutDefinitionManager}.</p>
     */
    public static LayoutDefinition getLayoutDefinition(String key) throws LayoutDefinitionException {
	// Make sure the key is not null
	if (key == null) {
	    return null;
	}

	// Get the FacesContext
	FacesContext context = FacesContext.getCurrentInstance();

	// Make sure we don't already have it...
	Map<String, Object> requestMap =
	    context.getExternalContext().getRequestMap();
	LayoutDefinition ld = (LayoutDefinition)
	    requestMap.get(LAYOUT_DEFINITION_KEY + key);
	if (ld != null) {
	    return ld;
	}

	// Find it...
	ld = LayoutDefinitionManager.getLayoutDefinition(context, key);

	// Save the LayoutDefinition for future calls to this method
	requestMap.put(LAYOUT_DEFINITION_KEY + key, ld);

	// Return the LayoutDefinition (if found)
	return ld;
    }

    /**
     *	<p> This method gets the {@link LayoutDefinition} key from the given
     *	    <code>UIViewRoot</code>.</p>
     */
    public static String getLayoutDefinitionKey(UIViewRoot root) {
	return (String) root.getAttributes().get(LAYOUT_DEFINITION_KEY);
    }

    /**
     *	<p> This method sets the {@link LayoutDefinition} key on the given
     *	    <code>UIViewRoot</code>.</p>
     */
    public static void setLayoutDefinitionKey(UIViewRoot root, String key) {
	root.getAttributes().put(LAYOUT_DEFINITION_KEY, key);
    }


    /**
     *	<p> This is the key to be used to store the {@link LayoutDefinition}
     *	    on the <code>ViewRoot</code> in its attribute map.</p>
     */
    public static final String LAYOUT_DEFINITION_KEY	= "_ldKey";

}
