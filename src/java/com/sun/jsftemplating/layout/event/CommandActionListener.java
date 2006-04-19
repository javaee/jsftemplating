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
package com.sun.jsftemplating.layout.event;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;

import java.io.IOException;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


/**
 *  <p>	The purpose of this class is to provide an <code>ActionListener</code>
 *	that can delegate to handlers (that are likely defined via XML).  It is
 *	safe to register this class as a managed bean at the Application
 *	scope.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class CommandActionListener implements java.io.Serializable {

    /**
     *	<p> Constructor.</p>
     */
    public CommandActionListener() {
	super();
    }

    /**
     *	<p> This is an ActionListener that delegates to handlers to process
     *	    the action.</p>
     */
    public void invokeCommandHandlers(ActionEvent event) {
	// Get the UIComponent source associated w/ this command
	UIComponent command = (UIComponent) event.getSource();
	if (command == null) {
	    throw new IllegalArgumentException(
		    "Action invoked, however, no source was given!");
	}

	// Get the FacesContext
	FacesContext context = FacesContext.getCurrentInstance();

	// Look on the UIComponent for the CommandHandlers
	LayoutElement desc = null;
	List handlers = (List) command.getAttributes().get(COMMAND_HANDLERS);
	if ((handlers != null) && (handlers.size() > 0)) {
	    // This is needed for components that don't have corresponding
	    // LayoutElements, it is also useful for dynamically defining
	    // Handlers (advanced and not recommended unless you have  a good
	    // reason).  May also happen if "id" for any component in
	    // hierarchy is not a simple String.

	    // No parent (null) or ComponentType, just pass (null)
	    desc = new LayoutComponent(
		(LayoutElement) null, command.getId(), (ComponentType) null);
	    desc.setHandlers(CommandEvent.EVENT_TYPE, handlers);
	} else {
	    // Attempt to find LayoutElement based on command's client id
	    // "desc" may be null
	    String viewId = getViewId(command);
	    desc = findLayoutElementByClientId(
		    context, viewId, command.getClientId(context));
	    if (desc == null) {
		// Do a brute force search for the LE
		desc = findLayoutElementById(context, viewId, command.getId());
	    }
	}

	// If We still don't have a desc, we're stuck
	if (desc == null) {
	    throw new IllegalArgumentException(
		"Unable to locate handlers for '"
		+ command.getClientId(context) + "'.");
	}

	// Dispatch the Handlers from the LayoutElement
	desc.dispatchHandlers(context, CommandEvent.EVENT_TYPE, event);
    }

    /**
     *	<p> This method returns the "viewId" of the <code>ViewRoot</code> given
     *	    a <code>UIComponent</code> that is part of that
     *	    <code>ViewRoot</code>.</p>
     */
    public static String getViewId(UIComponent comp) {
	String result = null;
	while ((comp != null) && !(comp instanceof UIViewRoot)) {
	    // Searching for the UIViewRoot...
	    comp = comp.getParent();
	}
	if (comp != null) {
	    // Found the UIViewRoot, get its "ViewId"
	    result = ((UIViewRoot) comp).getViewId();
	}
	// Return the result (or null)
	return result;
    }

    /**
     *	<p> This method searches for the LayoutComponent that matches the given
     *	    client ID.  Although this is often possible, it won't work all the
     *	    time.  This is because there is no way to ensure a 1-to-1 mapping
     *	    between the UIComponent and the LayoutComponent tree.  A given
     *	    LayoutComponent may create multiple UIComponent, the
     *	    LayoutComponent tree may itself be dynamic, and the UIComponent
     *	    tree may change after it is initially created from the
     *	    LayoutComponent tree.  For these reasons, this method may fail.  In
     *	    these circumstances, it is critical to store the necessary
     *	    information with the UIComponent itself.</p>
     */
    public static LayoutElement findLayoutElementByClientId(FacesContext ctx, String layoutDefKey, String clientId) {
	LayoutElement result = null;
	try {
	    result =
		findLayoutElementByClientId(
		    LayoutDefinitionManager.getManager(ctx).
		    getLayoutDefinition(layoutDefKey), clientId);
	} catch (IOException ex) {
	    // FIXME: Report a low priority warning, returning null is fine
	}
	return result;
    }

    public static LayoutElement findLayoutElementByClientId(LayoutDefinition def, String clientId) {
// FIXME: TBD...
// FIXME: Walk LE tree, ignore non-LayoutComponent entries (this may cause a problem itself b/c of conditional statements & loops)
	return null;
    }

    /**
     *	<p> This method simply searches the LayoutElement tree using the given
     *	    id.  As soon as it matches any LayoutElement in the tree w/ the
     *	    given id, it returns it.  This method does *not* respect
     *	    NamingContainers as the only information given to this method is a
     *	    simple "id".</p>
     */
    public static LayoutElement findLayoutElementById(FacesContext ctx, String layoutDefKey, String id) {
	// Sanity check
	if (id == null) {
	    return null;
	}

	LayoutElement result = null;
	try {
	    result = findLayoutElementById(
		    LayoutDefinitionManager.getManager(ctx).
		    getLayoutDefinition(layoutDefKey), id);
	} catch (IOException ex) {
	    // FIXME: Report a low priority warning, returning null is fine
	}
	return result;
    }

    /**
     *	<p> This method does not evaluate the id field of the LayoutElement
     *	    when checking for a match, this means it will not find values where
     *	    the LayoutComponent's id must be evaulated.  Store the handlers on
     *	    the UIComponent in this case.</p>
     */
    public static LayoutElement findLayoutElementById(LayoutElement elt, String id) {
	// First check to see if the given LayoutElement is it
	if (elt.getUnevaluatedId().equals(id)) {
	    return elt;
	}

	// Iterate over children and recurse (depth first)
	LayoutElement child = null;
	Iterator it = elt.getChildLayoutElements().iterator();
	while (it.hasNext()) {
	    child = (LayoutElement) it.next();
	    if (child instanceof LayoutComponent) {
		child = findLayoutElementById(child, id);
		if (child != null) {
		    return child;
		}
	    }
	}
	return null;
    }

    /**
     *	<p> Attribute name on a UIComponent which may store handlers for a
     *	    CommandEvent. ("commandHandlers")</p>
     */
    public static final String	COMMAND_HANDLERS    = "command";
}
