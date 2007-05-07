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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.util.FileUtil;


/**
 *  <p>	This class implements {@link ContentSource}.  It retreives it content
 *	by looking in the "docroot" of the application, and if not found, it
 *	will check the classpath.</p>
 */
public class ResourceContentSource implements ContentSource {

    /**
     *	<p> This method returns a unique string used to identify this
     *	    {@link ContentSource}.  This string must be specified in order to
     *	    select the appropriate {@link ContentSource} when using the
     *	    {@link FileStreamer}.</p>
     */
    public String getId() {
	return ID;
    }

    /**
     *  <p> This method is responsible for generating the content and returning
     *	    an <code>InputStream</code> for that content.  It is also
     *	    responsible for setting any attribute values in the
     *	    {@link Context}, such as {@link Context#EXTENSION} or
     *	    {@link Context#CONTENT_TYPE}.</p>
     */
    public InputStream getInputStream(Context ctx) throws IOException {
	// See if we already have it.
	InputStream in = (InputStream) ctx.getAttribute("inputStream");
	if (in != null) {
	    return in;
	}

	// Get the path...
	String path = getResourcePath(ctx);

	// Find the file...
	URL url = FileUtil.searchForFile(path, null);
	if (url == null) {
	    return null;
	}

	// Set the extension so it can be mapped to a MIME type
	int index = path.lastIndexOf('.');
	if (index > 0) {
	    ctx.setAttribute(Context.EXTENSION, path.substring(index + 1));
	}

	// Open the InputStream
	in = url.openStream();
	ctx.setAttribute("inputStream", in);

	// Return an InputStream to the file
	return in;
    }

    /**
     *	<p> This method returns the path of the resource that was
     *	    requested.</p>
     */
    public String getResourcePath(Context ctx) {
	// Check the ctx for the path...
	return (String) ctx.getAttribute(Context.FILE_PATH);
    }

    /**
     *	<p> This method may be used to clean up any temporary resources.  It
     *	    will be invoked after the <code>InputStream</code> has been
     *	    completely read.</p>
     */
    public void cleanUp(Context ctx) {
	// Get the File information
	InputStream is = (InputStream) ctx.getAttribute("inputStream");

	// Close the InputStream
	if (is != null) {
	    try {
		is.close();
	    } catch (Exception ex) {
		// Ignore...
	    }
	}
	ctx.removeAttribute("inputStream");
    }

    /**
     *	<p> This method is responsible for returning the last modified date of
     *	    the content, or -1 if not applicable.  This information will be
     *	    used for caching.</p>
     */
    public long getLastModified(Context context) {
	if (LayoutDefinitionManager.isDebug()) {
	    // When in debug mode, don't cache resources... otherwise always
	    // allow browser to always cache them
	    FacesContext fc = FacesContext.getCurrentInstance();
	    if (fc != null) {
		// Check to see if this resource exists in the FileSystem, if
		// it doesn't we will let the browser cache it b/c it can't be
		// changed.
		String path = getResourcePath(context);
		path = FileUtil.getRealPath(
		    fc.getExternalContext().getContext(), path);
		if (path != null) {
		    long time = new File(path).lastModified();
		    if (time > 0) {
			// Send the timestamp so it will only be cached by
			// the browser if it hasn't changed
			return time;
		    }
		}
	    }
	}

	// This will enable caching on all files served through this code path
	return DEFAULT_MODIFIED_DATE;
    }

    /**
     *	This is the default "Last-Modified" Date.  Its value is the
     *	<code>Long</code> representing the initialization time of this class.
     */
    protected static final long DEFAULT_MODIFIED_DATE = (new Date()).getTime();

    /**
     *	<p>This is the ID for this {@link ContentSource}.</p>
     */
    public static final String ID = "resourceCS";
}
