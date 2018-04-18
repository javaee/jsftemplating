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

package com.sun.jsftemplating.util.fileStreamer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *  <p> This class encapsulates servlet specific objects so that the
 *	{@link FileStreamer} class is not servlet-specific.</p>
 *
 *  <p>	This implementation will look for the following attributes
 *	({@link BaseContext#setAttribute(String, Object)}):</p>
 *
 *  <ul><li>{@link Context#CONTENT_TYPE} -- The "Content-type:" of the response.</li>
 *	<li>{@link Context#CONTENT_DISPOSITION} -- Disposition of the streamed content.</li>
 *	<li>{@link Context#CONTENT_FILENAME} -- Filename of the streamed content.</li>
 *	<li>{@link Context#EXTENSION} -- The file extension of the response.</li>
 *	</ul>
 */
public class ServletStreamerContext extends BaseContext {

    /**
     *	<p> Constructor.</p>
     */
    public ServletStreamerContext(HttpServletRequest request, HttpServletResponse resp, ServletConfig config) {
	super();
	setServletRequest(request);
	setServletResponse(resp);
	setServletConfig(config);
	setAttribute(Context.FILE_PATH, request.getPathInfo());

// FIXME: initialize allowed / denied paths...
	List<String> paths = new ArrayList<String>();
	paths.add("");  // Add default value
	ServletContext ctx = config.getServletContext();
	setAllowedPaths(ctx, paths);
	paths = new ArrayList<String>();
	paths.add("META-INF/");
	paths.add("WEB-INF/");
	setDeniedPaths(ctx, paths);
    }

    /**
     *	<p> Accessor to get the {@link FileStreamer} instance.</p>
     */
    public FileStreamer getFileStreamer() {
	return FileStreamer.getFileStreamer(getServletConfig().getServletContext());
    }

    /**
     *	<p> This method locates the appropriate {@link ContentSource} for this
     *	    {@link Context}.  It uses the <code>HttpServletRequest</code> to
     *	    look for a <b>HttpServletRequest</b> parameter named
     *	    {@link #CONTENT_SOURCE_ID}.  This value is used as the key when
     *	    looking up registered {@link ContentSource} implementations.</p>
     */
    public ContentSource getContentSource() {
	// Check cache...
	ContentSource src = (ContentSource) getAttribute("_contentSource");
	if (src != null) {
	    return src;
	}

	// Get the ContentSource id
	String id = getServletRequest().getParameter(CONTENT_SOURCE_ID);
	if (id == null) {
	    id = getServletConfig().getInitParameter(CONTENT_SOURCE_ID);
	    if (id == null) {
		id = Context.DEFAULT_CONTENT_SOURCE_ID;
	    }
	}

	// Get the ContentSource
	src = getFileStreamer().getContentSource(id);
	if (src == null) {
	    throw new RuntimeException("The ContentSource with id '" + id
		    + "' is not registered!");
	}

	// Return the ContentSource
	setAttribute("_contentSource", src);  // cache result
	return src;
    }

    /**
     *	<p> This method allows the Context to restrict access to resources.
     *	    It returns <code>true</code> if the user is allowed to view the
     *	    resource.  It returns <code>false</code> if the user should not
     *	    be allowed access to the resource.</p>
     */
    public boolean hasPermission(ContentSource src) {
	String filename = src.getResourcePath(this);
	ServletContext srvCtx = getServletConfig().getServletContext();
	List<String> paths = getAllowedPaths(srvCtx);
	boolean ok = false;

	// Ensure it is in our list of OK paths...
	for (String path : paths) {
	    if (filename.startsWith(path)) {
		ok = true;
		break;
	    }
	}

	// ...and ensure it is not in our blacklisted paths...
	if (ok) {
	    // Only check if ok so far...
	    paths = getDeniedPaths(srvCtx);
	    for (String path : paths) {
		if (path.startsWith("*") && filename.endsWith(path.substring(1))) {
		    // Matched suffix pattern
		    ok = false;
		    break;
		} else if (filename.startsWith(path)) {
		    // Matched prefix pattern
		    ok = false;
		    break;
		}
	    }
	}

	return ok;
    }

    /**
     *	<p> This methods sets the allowed paths for resources.   Paths may be
     *	    further restricted using the {@link #setDeniedPaths} method.</p>
     */
    public List<String> getAllowedPaths(ServletContext ctx) {
	return (List<String>) ctx.getAttribute(ALLOWED_PATHS_KEY);
    }

    /**
     *	<p> This methods sets the allowed paths for resources.   Paths may be
     *	    further restricted using the {@link #setDeniedPaths} method.</p>
     */
    public void setAllowedPaths(ServletContext ctx, List<String> paths) {
	ctx.setAttribute(ALLOWED_PATHS_KEY, paths);
    }

    /**
     *	<p> This methods sets the list of paths in which resources should not
     *	    be served.</p>
     */
    public void setDeniedPaths(ServletContext ctx, List<String> paths) {
	ctx.setAttribute(DENIED_PATHS_KEY, paths);
    }

    /**
     *	<p> This methods sets the list of paths for resources.</p>
     */
    public List<String> getDeniedPaths(ServletContext ctx) {
	return (List<String>) ctx.getAttribute(DENIED_PATHS_KEY);
    }

    /**
     *	<p> This method is responsible for setting the response header
     *	    information.</p>
     */
    public void writeHeader(ContentSource source) {
	HttpServletResponse resp = getServletResponse();

	// Set the "Last-Modified" Header
	// First check context
	long longTime = source.getLastModified(this);
	if (longTime != -1) {
	    resp.setDateHeader("Last-Modified", longTime);
	    resp.setDateHeader("Expires",
		new java.util.Date().getTime() + Context.EXPIRY_TIME);
	}

	// First check CONTENT_TYPE
	String contentType = (String) getAttribute(CONTENT_TYPE);
	if (contentType == null) {
	    // Not found yet, check EXTENSION
	    String ext = (String) getAttribute(EXTENSION);
	    if (ext != null) {
		contentType = FileStreamer.getMimeType(ext);
	    }
	    if (contentType == null) {
		// Default Content-type is: application/octet-stream
		contentType = FileStreamer.getDefaultMimeType();
	    }
	}
	resp.setHeader("Content-type", contentType);

	// Check disposition/filename to associate a name with the stream
	String disposition = (String) getAttribute(CONTENT_DISPOSITION);
	String filename = (String) getAttribute(CONTENT_FILENAME);
	if (disposition == null) {
	    // No disposition set, see if we have a filename
	    if (filename != null) {
		resp.setHeader("Content-Disposition", DEFAULT_DISPOSITION
			+ ";filename=\"" + filename + "\"");
	    }
	} else {
	    // Disposition set, see if we also have a filename
	    if (filename != null) {
		disposition += ";filename=\"" + filename + "\"";
	    }
	    resp.setHeader("Content-Disposition", disposition);
	}
    }

    /**
     *	<p> This method is responsible for sending an error.</p>
     */
    public void sendError(int code, String msg) throws IOException {
	HttpServletResponse resp = getServletResponse();
	if (msg == null) {
	    resp.sendError(code);
	} else {
	    resp.sendError(code, msg);
	}
    }

    /**
     *	<p> This method is returns a <code>ServletOutputStream</code> for the
     *	    <code>Servlet</code> that is represented by this class.</p>
     */
    public OutputStream getOutputStream() throws IOException {
	return getServletResponse().getOutputStream();
    }

    /**
     *  <p> This returns the <code>ServletConfig</code>.  This is the same
     *	    as calling:</p>
     *
     *  <p> <code>getAttribute(SERVLET_CONFIG)</code></p>
     */
    public ServletConfig getServletConfig() {
	return (ServletConfig) getAttribute(SERVLET_CONFIG);
    }

    /**
     *  <p> This sets the <code>ServletConfig</code>.  This is the same as
     *	    calling:</p>
     *
     *  <p> <code>setAttribute(SERVLET_CONFIG, config)</code></p>
     */
    protected void setServletConfig(ServletConfig config) {
	setAttribute(SERVLET_CONFIG, config);
    }

    /**
     *  <p> This returns the <code>HttpServletRequest</code>.  This is the same
     *	    as calling:</p>
     *
     *  <p> <code>getAttribute(SERVLET_REQUEST)</code></p>
     */
    public HttpServletRequest getServletRequest() {
	return (HttpServletRequest) getAttribute(SERVLET_REQUEST);
    }

    /**
     *  <p> This sets the <code>HttpServletRequest</code>.  This is the same as
     *	    calling:</p>
     *
     *  <p> <code>setAttribute(SERVLET_REQUEST, request)</code></p>
     */
    protected void setServletRequest(HttpServletRequest request) {
	setAttribute(SERVLET_REQUEST, request);
    }

    /**
     *  <p> This returns the <code>HttpServletResponse</code>.  This is the same
     *	    as calling:</p>
     *
     *  <p> <code>getAttribute(SERVLET_RESPONSE)</code></p>
     */
    public HttpServletResponse getServletResponse() {
	return (HttpServletResponse) getAttribute(SERVLET_RESPONSE);
    }

    /**
     *  <p> This sets the <code>HttpServletResponse</code>.  This is the same as
     *	    calling:</p>
     *
     *  <p> <code>setAttribute(SERVLET_RESPONSE, response)</code></p>
     */
    protected void setServletResponse(HttpServletResponse response) {
	setAttribute(SERVLET_RESPONSE, response);
    }

    /**
     *	<p> The attribute value to access the ServletConfig.  See
     *	    {@link #getServletConfig()}.</p>
     */
    public static final String SERVLET_CONFIG    = "servletConfig";

    /**
     *	<p> The attribute value to access the HttpServletRequest.  See
     *	    {@link #getServletRequest()}.</p>
     */
    public static final String SERVLET_REQUEST    = "servletRequest";

    /**
     *	<p> The attribute value to access the
     *	    <code>HttpServletResponse</code>.  See
     *	    {@link #getServletResponse()}.</p>
     */
    public static final String SERVLET_RESPONSE    = "servletResponse";

    /**
     *	<p> The default Content-Disposition.  It is only used when a filename
     *	    is provided, but a disposition is not.  The default is
     *	    "attachment".  This will normally cause a browser to prompt the
     *	    user to save the file.  This is the default since setting a
     *	    filename implies that the user may want to save this file.  You
     *	    must explicitly set the disposition for "inline" behavior with a
     *	    filename.</p>
     */
    public static final String DEFAULT_DISPOSITION =	"attachment";
}
