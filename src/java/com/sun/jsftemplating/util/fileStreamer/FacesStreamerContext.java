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
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
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
public class FacesStreamerContext extends BaseContext {

    /**
     *	<p> Constructor.</p>
     */
    public FacesStreamerContext(FacesContext ctx) {
	setFacesContext(ctx);
	init(ctx);
    }

    /**
     *	<p> This method initializes {@link FileStreamer}
     *	    {@link ContentSources}s.  It looks for the
     *	    {@link Context#CONTENT_SOURCES} context parameter (JSF doesn't
     *	    provide a way to get to the ServletConfig init parameters, as of
     *	    JSF 1.2).</p>
     */
    protected synchronized void init(FacesContext ctx) {
	if (initDone) {
	    return;
	}

	// Register ContentSources
	String sources = ctx.getExternalContext().getInitParameter(CONTENT_SOURCES);
	if ((sources != null) && (sources.trim().length() != 0)) {
	    FileStreamer fs = FileStreamer.getFileStreamer();
	    StringTokenizer tokens = new StringTokenizer(sources, " \t\n\r\f,;:");
	    while (tokens.hasMoreTokens()) {
		fs.registerContentSource(tokens.nextToken());
	    }
	}

	initDone = true;
    }

    /**
     *	<p> This method locates the appropriate {@link ContentSource} for this
     *	    {@link Context}.  It uses the <code>FacesContext</code>'s
     *	    <code>ExternalContext</code> to look for a <b>request parameter</b>
     *	    named {@link Context#CONTENT_SOURCE_ID}.  This value is used as the
     *	    key when looking up registered {@link ContentSource}
     *	    implementations.</p>
     */
    public ContentSource getContentSource() {
	ContentSource src = (ContentSource) getAttribute("_contentSource");
	if (src != null) {
	    return src;
	}

	// Get the ContentSource id
	String id = getFacesContext().getExternalContext().
		getRequestParameterMap().get(Context.CONTENT_SOURCE_ID);
	if (id == null) {
	    // Use the default ContentSource
	    id = Context.DEFAULT_CONTENT_SOURCE_ID;
	}

	// Get the ContentSource
	src = FileStreamer.getFileStreamer().getContentSource(id);
	if (src == null) {
	    throw new RuntimeException("The ContentSource with id '" + id
		    + "' is not registered!");
	}

	// Return the ContentSource
	setAttribute("_contentSource", src);
	return src;
    }

    /**
     *	<p> This method is responsible for setting the response header
     *	    information.</p>
     */
    public void writeHeader(ContentSource source) {
// FIXME: Portlet
	ServletResponse resp = (ServletResponse)
	    getFacesContext().getExternalContext().getResponse();

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

	// Check disposition/filename to associate a name with the stream
	String disposition = (String) getAttribute(CONTENT_DISPOSITION);
	String filename = (String) getAttribute(CONTENT_FILENAME);
	if (disposition == null) {
	    // No disposition set, see if we have a filename
	    if (filename != null) {
		((HttpServletResponse) resp).setHeader("Content-Disposition",
		       DEFAULT_DISPOSITION + ";filename=\"" + filename + "\"");
	    }
	} else {
	    // Disposition set, see if we also have a filename
	    if (filename != null) {
		disposition += ";filename=\"" + filename + "\"";
	    }
	    ((HttpServletResponse) resp).setHeader("Content-Disposition",
		       disposition);
	}
    }

    /**
     *	<p> This method is returns the <code>ServletOutputStream</code>.</p>
     */
    public OutputStream getOutputStream() throws IOException {
// FIXME: Portlet?
	ServletResponse resp = (ServletResponse)
	    getFacesContext().getExternalContext().getResponse();
	return resp.getOutputStream();
    }

    /**
     *  <p> This returns the <code>FacesContext</code>.  This is the same
     *	    as calling:</p>
     *
     *  <p> <code>getAttribute({@link #FACES_CONTEXT})</code></p>
     */
    public FacesContext getFacesContext() {
	return (FacesContext) getAttribute(FACES_CONTEXT);
    }

    /**
     *  <p> This sets the <code>FacesContext</code>.  This is the same as
     *	    calling:</p>
     *
     *  <p> <code>setAttribute({@link #FACES_CONTEXT}, ctx)</code></p>
     */
    protected void setFacesContext(FacesContext ctx) {
	setAttribute(FACES_CONTEXT, ctx);
    }

    /**
     *	<p> The attribute value to access the FacesContext.  See
     *	    {@link #getFacesContext()}.</p>
     */
    public static final String FACES_CONTEXT    = "facesContext";

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

    /**
     *	<p> This is a flag to indicate if initialization has been
     *	    completed.</p>
     */
    private static boolean initDone = false;
}
