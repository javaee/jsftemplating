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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


/**
 *  <p> This class encapsulates servlet specific objects so that the
 *	{@link FileStreamer} class is not servlet-specific.</p>
 *
 *  <p>	This implementation will look for the following attributes
 *	({@link BaseContext#setAttribute(String, Object)}):</p>
 *
 *  <ul><li>{@link #CONTENT_TYPE} -- The "Content-type:" of the response.</li>
 *	<li>{@link #EXTENSION} -- The file extension of the response.</li>
 *	</ul>
 */
public class ServletStreamerContext extends BaseContext {

    /**
     *	<p> Constructor.</p>
     */
    public ServletStreamerContext(ServletRequest request, ServletResponse resp, ServletConfig config) {
	setServletRequest(request);
	setServletResponse(resp);
	setServletConfig(config);
    }

    /**
     *	<p> This method locates the appropriate {@link ContentSource} for this
     *	    {@link Context}.  It uses the <code>ServletRequest</code> to look
     *	    for a <b>ServletRequest Parameter</b> named
     *	    {@link #CONTENT_SOURCE_ID}.  This value is used as the key when
     *	    looking up registered {@link ContentSource} implementations.</p>
     */
    public ContentSource getContentSource() {
	// Get the ContentSource id
	String id = getServletRequest().getParameter(CONTENT_SOURCE_ID);
	if (id == null) {
	    id = getServletConfig().getInitParameter(CONTENT_SOURCE_ID);
	    if (id == null) {
		throw new RuntimeException("You must provide the '"
		    + CONTENT_SOURCE_ID + "' request parameter!");
	    }
	}

	// Get the ContentSource
	ContentSource src = FileStreamer.getFileStreamer().getContentSource(id);
	if (src == null) {
	    throw new RuntimeException("The ContentSource with id '" + id
		    + "' is not registered!");
	}

	// Return the ContentSource
	return src;
    }

    /**
     *	<p> This method is responsible for setting the response header
     *	    information.</p>
     */
    public void writeHeader(ContentSource source) {
	ServletResponse resp = getServletResponse();
	if (!(resp instanceof HttpServletResponse)) {
	    // This implementation is only valid for HttpServletResponse
	    return;
	}

	// Set the "Last-Modified" Header
	// First check context
	long longTime = source.getLastModified(this);
	if (longTime != -1) {
	    ((HttpServletResponse) resp).
		setDateHeader("Last-Modified", longTime);
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
	((HttpServletResponse) resp).setHeader("Content-type", contentType);
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
     *  <p> This returns the <code>ServletRequest</code>.  This is the same
     *	    as calling:</p>
     *
     *  <p> <code>getAttribute(SERVLET_REQUEST)</code></p>
     */
    public ServletRequest getServletRequest() {
	return (ServletRequest) getAttribute(SERVLET_REQUEST);
    }

    /**
     *  <p> This sets the <code>ServletRequest</code>.  This is the same as
     *	    calling:</p>
     *
     *  <p> <code>setAttribute(SERVLET_REQUEST, request)</code></p>
     */
    protected void setServletRequest(ServletRequest request) {
	setAttribute(SERVLET_REQUEST, request);
    }

    /**
     *  <p> This returns the <code>ServletResponse</code>.  This is the same
     *	    as calling:</p>
     *
     *  <p> <code>getAttribute(SERVLET_RESPONSE)</code></p>
     */
    public ServletResponse getServletResponse() {
	return (ServletResponse) getAttribute(SERVLET_RESPONSE);
    }

    /**
     *  <p> This sets the <code>ServletResponse</code>.  This is the same as
     *	    calling:</p>
     *
     *  <p> <code>setAttribute(SERVLET_RESPONSE, response)</code></p>
     */
    protected void setServletResponse(ServletResponse response) {
	setAttribute(SERVLET_RESPONSE, response);
    }

    /**
     *	<p> The attribute value to access the ServletConfig.  See
     *	    {@link #getServletConfig()}.</p>
     */
    public static final String SERVLET_CONFIG    = "contentSourceId";

    /**
     *	<p> The attribute value to access the ServletRequest.  See
     *	    {@link #getServletRequest()}.</p>
     */
    public static final String SERVLET_REQUEST    = "contentSourceId";

    /**
     *	<p> The attribute value to access the ServletResponse.  See
     *	    {@link #getServletResponse()}.</p>
     */
    public static final String SERVLET_RESPONSE    = "contentSourceId";

    /**
     *	<p> This is the <b>ServletRequest Parameter</b> that should be provided
     *	    to identify the <code>ContentSource</code>
     *	    implementation that should be used.  This value must match the
     *	    value returned by the <code>ContentSource</code>
     *	    implementation's <code>getId()</code> method.</p>
     */
    public static final String CONTENT_SOURCE_ID    = "contentSourceId";
}
