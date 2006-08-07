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
package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutFacet;
import com.sun.jsftemplating.layout.descriptors.Resource;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.application.StateManager.SerializedView;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

// FIXME: Things to consider:
// FIXME:   - What is necessary to support Portlets...
// FIXME:   - Should I attempt to clean up old unused UIComponents?
// FIXME:   - f:view supported setting locale, I should too...


/**
 *  <p>	This class provides a custom <code>ViewHandler</code> that is able to
 *	create and populate a <code>UIViewRoot</code> from a
 *	{@link LayoutDefinition}.  This is often defined by an XML document,
 *	the default implementation's DTD is defined in
 *	<code>layout.dtd</code>.</p>
 *
 *  <p>	Besides the default <code>ViewHandler</code> behavior, this class is
 *	responsible for instantiating a {@link LayoutViewRoot} and using the
 *	given <code>viewId</code> as the {@link LayoutDefinition} key.  It
 *	will obtain the {@link LayoutDefinition}, initialize the declared
 *	{@link Resource}s, and instantiate <code>UIComponent</code> tree using
 *	the {@link LayoutDefinition}'s declared {@link LayoutComponent}
 *	structure.  During rendering, it delegates to the
 *	{@link LayoutDefinition}.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutViewHandler extends ViewHandler {

    /**
     *	<p> Constructor.</p>
     *
     *	@param	oldViewHandler	The old <code>ViewHandler</code>.
     */
    public LayoutViewHandler(ViewHandler oldViewHandler) {
	_oldViewHandler = oldViewHandler;
    }

    /**
     *	<p> This method is invoked when restoreView does not yield a UIViewRoot
     *	    (initial requests and new pages).</p>
     *
     *	<p> This implementation should work with both
     *	    <code>LayoutDefinition<code>-based pages as well as traditional
     *	    JSP pages.</p>
     */
    public UIViewRoot createView(FacesContext context, String viewId) {
	Locale locale = null;
	String renderKitId = null;

	// use the locale from the previous view if is was one which will be
	// the case if this is called from NavigationHandler. There wouldn't be
	// one for the initial case.
	if (context.getViewRoot() != null) {
	    locale = context.getViewRoot().getLocale();
	    renderKitId = context.getViewRoot().getRenderKitId();
	}

	// Create the LayoutViewRoot
	LayoutViewRoot viewRoot = new LayoutViewRoot();
	viewRoot.setViewId(viewId);
	viewRoot.setLayoutDefinitionKey(viewId);

	// if there was no locale from the previous view, calculate the locale
	// for this view.
	if (locale == null) {
	    locale = calculateLocale(context);
	}
	viewRoot.setLocale(locale);

	// set the renderkit
	if (renderKitId == null) {
	    renderKitId = calculateRenderKitId(context);
	}
	viewRoot.setRenderKitId(renderKitId);

	// Initialize Resources / Create Tree
	LayoutDefinition def = null;
	try {
	    def = viewRoot.getLayoutDefinition(context);
	} catch (LayoutDefinitionException ex) {
// FIXME: Provide better feedback when no .jsf & no .jsp
ex.printStackTrace();

	    // Not found, delegate to old ViewHandler
	    return _oldViewHandler.createView(context, viewId);
	}

	// FIXME: I should not set the view root here!  But some components
	//	  may require this during creation of the UIComponent tree.
	// NOTE: This must happen after return _oldViewHandler.createView(...)
	if (context.getViewRoot() == null) {
	    context.setViewRoot(viewRoot);
	}

	if (def != null) {
	    // Ensure that our Resources are available
	    Iterator it = def.getResources().iterator();
	    Resource resource = null;
	    while (it.hasNext()) {
		resource = (Resource) it.next();
		// Just calling getResource() puts it in the Request scope
		resource.getFactory().getResource(context, resource);
	    }

	    // Get the Tree and pre-walk it
	    buildUIComponentTree(context, viewRoot, def);
	}

	// Return the populated UIViewRoot
	return viewRoot;
    }

    /**
     *
     */
    protected void buildUIComponentTree(FacesContext context, UIComponent parent, LayoutElement elt) {
// FIXME: Consider processing *ALL* LayoutElements so that <if> and others
// FIXME: have meaning when inside other components.
	Iterator<LayoutElement> it = elt.getChildLayoutElements().iterator();
	LayoutElement childElt;
	UIComponent child = null;
	while (it.hasNext()) {
	    childElt = it.next();
	    if (childElt instanceof LayoutFacet) {
		if (!((LayoutFacet) childElt).isRendered()) {
		    // The contents of this should be a single UIComponent
		    buildUIComponentTree(context, parent, childElt);
		}
		// NOTE: LayoutFacets that aren't JSF facets aren't
		// NOTE: meaningful in this context
	    } if (childElt instanceof LayoutComponent) {
		// Calling getChild will add the child UIComponent to tree
		child = ((LayoutComponent) childElt).
			getChild(context, parent);

		// Check for events
		// NOTE: For now I am only supporting "action" and
		// NOTE: "actionListener" event types.  In the future it
		// NOTE: may be desirable to support beforeEncode /
		// NOTE: afterEncode as well.  At this time, those events
		// NOTE: are supported by the "Event" UIComponent.  That
		// NOTE: component can wrap non-layout-based components to
		// NOTE: achieve this functionality (supporting that
		// NOTE: functionality here will simply do the same thing
		// NOTE: automatically).

		// Recurse
		buildUIComponentTree(context, child, childElt);
	    } else {
		buildUIComponentTree(context, parent, childElt);
	    }
	}
    }

    /**
     *	<p> This implementation relies on the default behavior to reconstruct
     *	    the UIViewRoot.</p>
     *
     *	<p> ...</p>
     */
    public UIViewRoot restoreView(FacesContext context, String viewId) {
	Map<String, Object> map = context.getExternalContext().getRequestMap();
	if (map.get(RESTORE_VIEW_ID) == null) {
	    map.put(RESTORE_VIEW_ID, viewId);
	} else {
	    // This request has already been processed, it must be a forward()
	    return createView(context, viewId);
	}

	// Peform default behavior...
	UIViewRoot root = _oldViewHandler.restoreView(context, viewId);

	// Return the UIViewRoot (LayoutViewRoot most likely)
	return root;
    }

    /**
     *
     */
    public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException {
	// Make sure we have a def
	LayoutDefinition def = null;
	if (viewToRender instanceof LayoutViewRoot) {
	    def = ((LayoutViewRoot) viewToRender).getLayoutDefinition(context);
	}

	if (def == null) {
	    // No def, fall back to default behavior
	    _oldViewHandler.renderView(context, viewToRender);
	} else {
	    // Start document
	    ResponseWriter writer = setupResponseWriter(context);
	    writer.startDocument();

// BEGIN EXPERIMENTAL CODE...
	    UIComponent target = (UIComponent) context.getExternalContext().
		    getRequestMap().get(AJAX_REQ_TARGET_KEY);
	    if (target != null) {
		renderComponent(context, target);
	    } else {
// END EXPERIMENTAL CODE...
		// Render content
		def.encode(context, viewToRender);
	    }

	    // End document
	    writer.endDocument();
	}
    }

    private static void renderComponent(FacesContext context, UIComponent comp) throws IOException {
	if (!comp.isRendered()) {
	    return;
	}

	comp.encodeBegin(context);
	if (comp.getRendersChildren()) {
	    comp.encodeChildren(context);
	} else {
	    UIComponent child = null;
	    Iterator it = comp.getChildren().iterator();
	    while (it.hasNext()) {
		child = (UIComponent) it.next();
		renderComponent(context, child);
	    }
	}
	comp.encodeEnd(context);
    }

    /**
     *
     */
    private ResponseWriter setupResponseWriter(FacesContext context) throws IOException {
	ResponseWriter writer = context.getResponseWriter();
	if (writer != null) {
	    // It is already setup
	    return writer;
	}

	ExternalContext extCtx = context.getExternalContext();
	ServletResponse response = (ServletResponse) extCtx.getResponse();

	RenderKitFactory renderFactory = (RenderKitFactory)
	    FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
	RenderKit renderKit =
	    renderFactory.getRenderKit(context,
		 context.getViewRoot().getRenderKitId());
	String contentTypeList = (String) extCtx.getRequestHeaderMap().get("Accept");
	if (contentTypeList == null) {
	    contentTypeList = "text/html;q=1.0";
	} else if (!contentTypeList.toLowerCase().contains("text/html")) {
	    contentTypeList += ",text/html;q=1.0";
	}
	writer =
	    renderKit.createResponseWriter(
		new OutputStreamWriter(response.getOutputStream()),
		contentTypeList,
		((ServletRequest) extCtx.getRequest()).getCharacterEncoding());
	context.setResponseWriter(writer);
// Not setting the contentType here results in XHTML which formats differently
// than text/html in Mozilla.. even though the documentation claims this
// works, it doesn't (try viewing the Tree)
        response.setContentType("text/html");

	// As far as I can tell JSF doesn't ever set the Content-type that it
	// works so hard to calculate...  This is the code we should be
	// calling, however we can't do this yet
//	response.setContentType(writer.getContentType());

	return writer;
    }

    /**
     *	<p> Take any appropriate action to either immediately write out the
     *	    current state information (by calling
     *	    <code>StateManager.writeState</code>, or noting where state
     *	    information should later be written.</p>
     *
     *	@param context <code>FacesContext</code> for the current request
     *
     *	@exception IOException if an input/output error occurs
     */
    public void writeState(FacesContext context) throws IOException {
	// Check to see if we should delegate back to the legacy ViewHandler
	UIViewRoot root = context.getViewRoot();
	if ((root == null) || !(root instanceof LayoutViewRoot)
		|| (((LayoutViewRoot) root).
			getLayoutDefinition(context) == null)) {
	    // Use old behavior...
	    _oldViewHandler.writeState(context);
	} else {
	    // b/c we pre-processed the ViewTree, we can just add it...
	    StateManager stateManager =
		context.getApplication().getStateManager();
	    SerializedView view = stateManager.saveSerializedView(context);

	    // New versions of JSF 1.2 changed the contract so that state is
	    // always written (client and server state saving)
	    stateManager.writeState(context, view);
	}
    }

    /**
     *	<p> Return a URL suitable for rendering (after optional encoding
     *	    perfomed by the <code>encodeResourceURL()</code> method of
     *	    <code>ExternalContext<code> that selects the specifed web
     *	    application resource.  If the specified path starts with a slash,
     *	    it must be treated as context relative; otherwise, it must be
     *	    treated as relative to the action URL of the current view.</p>
     *
     *	@param context	<code>FacesContext</code> for the current request
     *	@param path	Resource path to convert to a URL
     *
     *	@exception  IllegalArgumentException	If <code>viewId</code> is not
     *	    valid for this <code>ViewHandler</code>.
     */
    public String getResourceURL(FacesContext context, String path) {
	return _oldViewHandler.getResourceURL(context, path);
    }

    /**
     *	<p> Return a URL suitable for rendering (after optional encoding
     *	    performed by the <code>encodeActionURL()</code> method of
     *	    <code>ExternalContext</code> that selects the specified view
     *	    identifier.</p>
     *
     *	@param	context	<code>FacesContext</code> for this request
     *	@param	viewId	View identifier of the desired view
     *
     *	@exception  IllegalArgumentException	If <code>viewId</code> is not
     *		valid for this <code>ViewHandler</code>.
     */
    public String getActionURL(FacesContext context, String viewId) {
	return _oldViewHandler.getActionURL(context, viewId);
    }

    /**
     *	<p> Returns an appropriate <code>Locale</code> to use for this and
     * subsequent requests for the current client.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     *
     * @exception NullPointerException if <code>context</code> is
     *  <code>null</code>
     */
     public Locale calculateLocale(FacesContext context) {
	 return _oldViewHandler.calculateLocale(context);
     }

    /**
     * <p>Return an appropriate <code>renderKitId</code> for this
     * and subsequent requests from the current client.</p>
     *
     * <p>The default return value is
     * <code>javax.faces.render.RenderKitFactory.HTML_BASIC_RENDER_KIT</code>.
     * </p>
     *
     * @param	context	<code>FacesContext</code> for the current request.
     */
    public String calculateRenderKitId(FacesContext context) {
	return _oldViewHandler.calculateRenderKitId(context);
    }

    /**
     *	<p> This is the key that may be used to identify the clientId of the
     *	    UIComponent that is to be updated via an Ajax request.</p>
     */
    public static final String AJAX_REQ_KEY		= "ajaxReq";

    public static final String RESTORE_VIEW_ID		= "_resViewID";

    private ViewHandler _oldViewHandler			= null;
    static final String AJAX_REQ_TARGET_KEY		= "_ajaxReqTarget";
}
