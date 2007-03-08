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
import com.sun.jsftemplating.util.Util;
import com.sun.jsftemplating.util.LogUtil;
import com.sun.jsftemplating.util.fileStreamer.Context;
import com.sun.jsftemplating.util.fileStreamer.FacesStreamerContext;
import com.sun.jsftemplating.util.fileStreamer.FileStreamer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
//_time = new java.util.Date();
	// Check to see if this is a resource request
	String path = getResourcePath(viewId);
	if (path != null) {
	    // Serve Resource
	    return serveResource(context, path);
	}

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

	// Save the current viewRoot, temporarily set the new UIViewRoot so
	// beforeCreate, afterCreate will function correctly
	UIViewRoot currentViewRoot = context.getViewRoot(); 

	// Set the View Root to the new viewRoot
	// NOTE: This must happen after return _oldViewHandler.createView(...)
	// NOTE2: However, we really want the UIViewRoot available during
	//	  initPage events which are fired during
	//	  getLayoutDefinition()... so we need to set this, then unset
	//	  it if we go through _oldViewHandler.createView(...)
	context.setViewRoot(viewRoot);

	// Initialize Resources / Create Tree
	LayoutDefinition def = null;
	try {
	    def = viewRoot.getLayoutDefinition(context);
	} catch (LayoutDefinitionException ex) {
	    if (LogUtil.configEnabled()) {
		LogUtil.config("JSFT0005", (Object) viewId);
		if (LogUtil.finestEnabled()) {
		    LogUtil.finest(
			"File (" + viewId + ") not found!", ex);
		}
	    }

	    // Restore original ViewRoot, we set it prematurely
	    if (currentViewRoot != null) {
// FIXME: Talk to Ryan about restoring the ViewRoot to null!!
		context.setViewRoot(currentViewRoot);
	    }

// FIXME: Provide better feedback when no .jsf & no .jsp
// FIXME: Difficult to tell at this stage if no .jsp is present

	    // Not found, delegate to old ViewHandler
	    return _oldViewHandler.createView(context, viewId);
	} catch (RuntimeException ex) {
	    // Restore original ViewRoot, we set it prematurely
	    if (currentViewRoot != null) {
// FIXME: Talk to Ryan about restoring the ViewRoot to null!!
		context.setViewRoot(currentViewRoot);
	    }

	    // Allow error to be thrown (this isn't the normal code path)
	    throw ex;
	}

	if (def != null) {
	    // Ensure that our Resources are available
	    Iterator<Resource> it = def.getResources().iterator();
	    Resource resource = null;
	    while (it.hasNext()) {
		resource = it.next();
		// Just calling getResource() puts it in the Request scope
		resource.getFactory().getResource(context, resource);
	    }

	    // Get the Tree and pre-walk it
	    buildUIComponentTree(context, viewRoot, def);
	}

	// Restore the current UIViewRoot
	if (currentViewRoot != null) {
	    context.setViewRoot(currentViewRoot);
	}

	// Return the populated UIViewRoot
	return viewRoot;
    }

    /**
     *	<p> If this is a resource request, this method will handle the
     *	    request.</p>
     */
    public UIViewRoot serveResource(FacesContext context, String path) {
	// Mark the response complete so no more processing occurs
	context.responseComplete();

	// Create dummy UIViewRoot
	UIViewRoot root = new LayoutViewRoot();
	root.setRenderKitId("dummy");

	// Setup the FacesStreamerContext
	Context fsContext = new FacesStreamerContext(context);
	fsContext.setAttribute("filePath", path);

	// Get the HttpServletResponse
	Object obj = context.getExternalContext().getResponse();
	HttpServletResponse resp = null;
	if (obj instanceof HttpServletResponse) {
	    resp = (HttpServletResponse) obj;

	    // We have an HttpServlet response, do some extra stuff...
	    // Check the last modified time to see if we need to serve the resource
	    long mod = fsContext.getContentSource().getLastModified(fsContext);
	    if (mod != -1) {
		long ifModifiedSince = ((HttpServletRequest)
			context.getExternalContext().getRequest()).
			getDateHeader("If-Modified-Since");
		// Round down to the nearest second for a proper compare
		if (ifModifiedSince < (mod / 1000 * 1000)) {
		    // A ifModifiedSince of -1 will always be less
		    resp.setDateHeader("Last-Modified", mod);
		} else {
		    // Set not modified header and complete response
		    resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		    return root;
		}
	    }
	}

	// Stream the content
	try {
	    FileStreamer.getFileStreamer().streamContent(fsContext);
	} catch (FileNotFoundException ex) {
	    if (LogUtil.infoEnabled()) {
		LogUtil.info("JSFT0004", (Object) path);
	    }
	    if (resp != null) {
		try {
		    ((HttpServletResponse) resp).sendError(
			   HttpServletResponse.SC_NOT_FOUND);
		} catch (IOException ioEx) {
		    // Ignore
		}
	    }
	} catch (IOException ex) {
	    if (LogUtil.infoEnabled()) {
		LogUtil.info("JSFT0004", (Object) path);
		if (LogUtil.fineEnabled()) {
		    LogUtil.fine(
			"Resource (" + path + ") not available!", ex);
		}
	    }
// FIXME: send 404?
	}

	// Return dummy UIViewRoot to avoid NPE
	return root;
    }

    /**
     *	<p> This method checks the given viewId and returns a the path to the
     *	    requested resource if it refers to a resource.  Resources are
     *	    things like JavaScript files, images, etc.  Basically anything that
     *	    is not a JSF page that you'd like to serve up via the FacesServlet.
     *	    Serving resources this way allows you to bundle the resources in a
     *	    jar file, this is useful if you want to package up part of an app
     *	    (or a JSF component) in a single file.</p>
     *
     *	<p> A request for a resource must be prefixed by the resource prefix,
     *	    see @{link #getResourcePrefixes}.  This prefix must also be mapped to
     *	    the <code>FacesServlet</code> in order for this class to handle the
     *	    request.</p>
     */
    public String getResourcePath(String viewId) {
	ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
// FIXME: Portlet!
	String servletPath = extCtx.getRequestServletPath();
	Iterator<String> it = getResourcePrefixes().iterator();
	while (it.hasNext()) {
	    if (servletPath.equals(it.next())) {
		return extCtx.getRequestPathInfo();
	    }
	}
	return null;
    }

    /**
     *	<p> This method returns the prefix that a URL must contain in order to
     *	    retrieve a "resource" through this <code>ViewHandler</code>.</p>
     *
     *	<p> The prefix itself does not manifest itself in the file system /
     *	    classpath.</p>
     *
     *	<p> If the prefix is not set, then an init parameter (see
     *	    {@link #RESOURCE_PREFIX}) will be checked.  If that is still not
     *	    specified, then the {@link #DEFAULT_RESOURCE_PREFIX} will be
     *	    used.</p>
     */
    public List<String> getResourcePrefixes() {
	if (_resourcePrefix == null) {
	    ArrayList<String> list = new ArrayList<String>();

	    // Check to see if it's specified by a context param
	    // Get context parameter map (initParams in JSF are context params)
	    String initParam = (String) FacesContext.getCurrentInstance().
		getExternalContext().getInitParameterMap().get(RESOURCE_PREFIX);
	    if (initParam != null) {
		list.add(initParam);
	    }
// FIXME: Support more...

	    // Add default...
	    list.add(DEFAULT_RESOURCE_PREFIX);
	    _resourcePrefix = list;
	}
	return _resourcePrefix;
    }

    /**
     *	<p> This method allows a user to set the resource prefix which will be
     *	    checked to obtain a resource via this <code>Viewhandler</code>.
     *	    Currently, only 1 prefix is supported.  The prefix itself does not
     *	    manifest itself in the file system / classpath.</p>
     */
    public void setResourcePrefixes(List<String> prefix) {
	_resourcePrefix = prefix;
    }

    /**
     *	<p> This method iterates over the child {@link LayoutElement}s of the
     *	    given <code>elt</code> to create <code>UIComponent</code>s for each
     *	    {@link LayoutComponent}.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *	@param	parent	The parent <code>UIComponent</code> of the
     *			<code>UIComponent</code> to be found or created.
     *	@param	elt	The <code>LayoutElement</code> driving everything.
     */
    public static void buildUIComponentTree(FacesContext context, UIComponent parent, LayoutElement elt) {
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
//_time = new java.util.Date();
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
//System.out.println("PROCESSING TIME: " + (new java.util.Date().getTime() - _time.getTime()));
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
	    Iterator<UIComponent> it = comp.getChildren().iterator();
	    while (it.hasNext()) {
		child = it.next();
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
// FIXME: Portlet?
	ServletResponse response = (ServletResponse) extCtx.getResponse();
	ServletRequest request = (ServletRequest) extCtx.getRequest();

	RenderKitFactory renderFactory = (RenderKitFactory)
	    FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
	RenderKit renderKit =
	    renderFactory.getRenderKit(context,
		 context.getViewRoot().getRenderKitId());

	// See if the user (page author) specified a ContentType...
	String contentTypeList = null;
// FIXME: Provide a way for the user to specify this...
// FIXME: Test multiple browsers against this code!!
	String userContentType = "text/html";
	boolean responseCTSet = false;
	if((userContentType != null) && (userContentType.length() > 0)) {
		// User picked this, use it...
		response.setContentType(userContentType);
		responseCTSet = true;
	}
	else {
		// No explicit Content-type, find best match...
		contentTypeList = (String) extCtx.getRequestHeaderMap().get("Accept");
		if (contentTypeList == null) {
			contentTypeList = "text/html;q=1.0";
		}
	}
	String encType = Util.getEncoding(context);
	if(encType == null)  {
		encType = request.getCharacterEncoding();
		if(encType == null) {
			//default encoding type
			encType="UTF-8";
		}	
	}
	response.setCharacterEncoding(encType);

// FIXME: Portlet?
	writer =
	    renderKit.createResponseWriter(
		new OutputStreamWriter(response.getOutputStream(), encType),
		contentTypeList, encType);
	context.setResponseWriter(writer);
// Not setting the contentType here results in XHTML which formats differently
// than text/html in Mozilla.. even though the documentation claims this
// works, it doesn't (try viewing the Tree)
//        response.setContentType("text/html");

	// As far as I can tell JSF doesn't ever set the Content-type that it
	// works so hard to calculate...  This is the code we should be
	// calling, however we can't do this yet
	response.setContentType(writer.getContentType());

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

    /**
     *	<p> This is the default prefix that must be included on all requests
     *	    for resources.</p>
     */
    public static final String DEFAULT_RESOURCE_PREFIX	= "/resource";

    /**
     *	<p> The name of the <code>context-param</code> to set the resource
     *	    prefix.</p>
     */
    public static final String RESOURCE_PREFIX		=
	"com.sun.jsftemplating.RESOURCE_PREFIX";

    private List<String> _resourcePrefix = null;
//private transient java.util.Date _time = null;

    private ViewHandler _oldViewHandler			= null;
    static final String AJAX_REQ_TARGET_KEY		= "_ajaxReqTarget";
}
