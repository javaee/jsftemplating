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
package com.sun.jsftemplating.util.fileStreamer;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
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
	src = FileStreamer.getFileStreamer(null).getContentSource(id);
	if (src == null) {
	    throw new RuntimeException("The ContentSource with id '" + id
		    + "' is not registered!");
	}

	// Return the ContentSource
	setAttribute("_contentSource", src);  // cache result
	return src;
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
