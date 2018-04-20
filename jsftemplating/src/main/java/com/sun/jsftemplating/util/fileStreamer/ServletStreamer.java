/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2018 Oracle and/or its affiliates. All rights reserved.
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

import com.sun.jsftemplating.util.LogUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *  <p>	This <code>Servlet</code> provides the ability to stream information
 *	from the server to the client.  It provides the ability to set the
 *	Content-type of the streamed content, if not specified, it will attempt
 *	to guess based on the extension (if possible).  It requires the
 *	{@link ContentSource} of the data to stream to be specified by
 *	passing in a <code>ServletRequest</code> parameter named
 *	{@link ServletStreamerContext#CONTENT_SOURCE_ID}.  The
 *	{@link ContentSource} provides a plugable way of obtaining data from
 *	any source (i.e. the filesystem, generated data, from the network, a
 *	database, etc.).  The available {@link ContentSource} implemenatations
 *	must be specified via a <code>Servlet</code> init parameter named
 *	{@link Context#CONTENT_SOURCES}.</p>
 */
public class ServletStreamer extends HttpServlet {

    /**
     *	<p> Default Constructor.</p>
     */
    public ServletStreamer() {
	super();
    }

    /**
     *	<p> <code>Servlet</code> initialization method.</p>
     */
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	// Register ContentSources
	String sources = config.getInitParameter(Context.CONTENT_SOURCES);
	if ((sources != null) && (sources.trim().length() != 0)) {
	    FileStreamer fs = FileStreamer.getFileStreamer(config.getServletContext());
	    StringTokenizer tokens = new StringTokenizer(sources, " \t\n\r\f,;:");
	    while (tokens.hasMoreTokens()) {
		fs.registerContentSource(tokens.nextToken());
	    }
	}
    }

    /**
     *	<p> This method delegates to the {@link #doPost(
     *	    HttpServletRequest, HttpServletResponse)} method.</p>
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	doPost(request, response);
    }

    /**
     *	<p> This method is the main method for this class when used in an
     *	    <code>HttpServlet</code> environment.  It starts the process, by
     *	    creating a {@link ServletStreamerContext} and invoking
     *	    {@link FileStreamer#streamContent(Context)}.</p>
     *
     *	<p> The {@link Context#FILE_PATH} will be set to the PATH_INFO of the
     *	    <code>HttpServletRequest</code>.</p>
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	// Get the ServletStreamerContext
	Context context = getServletStreamerContext(request, response);

	// Stream Content
	try {
	    FileStreamer.getFileStreamer(getServletContext()).streamContent(context);
	} catch (FileNotFoundException ex) {
	    if (LogUtil.infoEnabled()) {
		LogUtil.info("JSFT0004", (Object) request.getPathInfo());
	    }
	    response.sendError(HttpServletResponse.SC_NOT_FOUND);
	} catch (IOException ex) {
	    if (LogUtil.infoEnabled()) {
		String path = request.getPathInfo();
		LogUtil.info("JSFT0004", (Object) path);
		if (LogUtil.fineEnabled()) {
		    LogUtil.fine(
			"Resource (" + path + ") not available!", ex);
		}
	    }
// FIXME: send 404?
	}
    }

    /**
     *	<p> This method instantiates a {@link ServletStreamerContext} and
     *	    initializes it with the <code>ServletConfig</code>,
     *	    <code>HttpServletRequest</code>, and
     *	    <code>HttpServletResponse</code>.</p>
     *
     *	@param	request	    The <code>HttpServletRequest</code>.
     *	@param	response    The <code>HttpServletResponse</code>.
     */
    protected ServletStreamerContext getServletStreamerContext(HttpServletRequest request, HttpServletResponse response) {
	ServletStreamerContext ctx = (ServletStreamerContext)
	    request.getAttribute(SERVLET_STREAMER_CONTEXT);
	if (ctx == null) {
	    ctx = new ServletStreamerContext(
		    request, response, getServletConfig());
	    request.setAttribute(SERVLET_STREAMER_CONTEXT, ctx);
	}

	// This is every time b/c the response may initially be null,
	// subsequent calls may provide this a non-null value.
	ctx.setServletResponse(response);

	return ctx;
    }

    /**
     *	<p> <code>HttpServlet</code> defines this method.  This method gets
     *	    called before the doGet / doPost methods.  This requires us to
     *	    create the {@link Context} here.  However, we do not have the
     *	    <code>HttpServletResponse</code> yet, so we will pass in
     *	    <code>null</code>.</p>
     */
    @Override
    protected long getLastModified(HttpServletRequest request) {
	// Get the ServletStreamerContext
	Context context = getServletStreamerContext(request, null);

	// Get the ContentSource
	ContentSource source = context.getContentSource();
	
	// Calculate the last modified date
	return source.getLastModified(context);
    }


    /**
     *	<p> This String ("servletStreamerContext") is the name if the
     *	    <code>ServletRequest</code> attribute used to store the
     *	    {@link Context} object for this request.</p>
     */
    public static final String SERVLET_STREAMER_CONTEXT	    = "servletStreamerContext";

    /**
     *	<p> The Default Content-type ("application/octet-stream").</p>
     */
    public static final String DEFAULT_CONTENT_TYPE =
	    "application/octet-stream";
}
