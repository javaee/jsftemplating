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
import java.util.Set;


/**
 *  <p> This interface provides API's to encapsulate environment specific
 *	objects so that the FileStreamer class is not specific to a specific
 *	environment (like a Servlet environment).</p>
 */
public interface Context {

    /**
     *	<p> This method locates the appropriate {@link ContentSource} for this
     *	    <code>Context</code>.</p>
     */
    public ContentSource getContentSource();

    /**
     *	<p> This method is responsible for setting the response header
     *	    information.</p>
     */
    public void writeHeader(ContentSource source) throws IOException;

    /**
     *	<p> This method is responsible for returning an
     *	    <code>OutputStream</code> suitable for writing content.</p>
     */
    public OutputStream getOutputStream() throws IOException;

    /**
     *	<p> This method may be used to manage arbitrary information between the
     *	    code invoking the {@link FileStreamer} and the
     *	    <code>ContentSource</code>.  This method retrieves an attribute.</p>
     *
     *	<p> See individual {@link ContentSource} implementations for more
     *	    details on supported / required attributes.</p>
     */
    public Object getAttribute(String name);

    /**
     *	<p> This provides access to all attributes in this Context.</p>
     *
     *	<p> See individual {@link ContentSource} implementations for more
     *	    details on supported / required attributes.</p>
     */
    public Set<String> getAttributeKeys();

    /**
     *	<p> This method may be used to manage arbitrary information between the
     *	    code invoking the {@link FileStreamer} and the
     *	    <code>ContentSource</code>.  This method sets an attribute.</p>
     *
     *	<p> See individual {@link ContentSource} implementations for more
     *	    details on supported / required attributes.</p>
     */
    public void setAttribute(String name, Object value);

    /**
     *	<p> This method may be used to manage arbitrary information between the
     *	    coding invoking the {@link FileStreamer} and the
     *	    <code>ContentSource</code>.  This method removes an attribute.</p>
     *
     *	<p> See individual {@link ContentSource} implementations for more
     *	    details on supported / required attributes.</p>
     */
    public void removeAttribute(String name);

    /**
     *	<p> This is the {@link Context} attribute name used to
     *	    specify the filename extension of the content.  It is the
     *	    responsibility of the {@link ContentSource} to set
     *	    this value.  The value should represent the filename extension of
     *	    the content if it were saved to a filesystem.</p>
     */
    public static final String EXTENSION = "extension";

    /**
     *	<p> The Content-type ("ContentType").  This is the {@link Context}
     *	    attribute used to specify an explicit "Content-type".  It may be
     *	    set by the {@link ContentSource}.  If not specified, the
     *	    {@link #EXTENSION} will typically be used (if possible).  If that
     *	    fails, the {@link FileStreamer#getDefaultMimeType} is used.</p>
     */
    public static final String CONTENT_TYPE = "ContentType";

    /**
     *	<p> The value for the "Content-Disposition" ("disposition"). This is
     *	    the {@link Context} attribute used to specify the content
     *	    disposition.  The content disposition tells the browser how to
     *	    handle the content.  The two standard values for this are:</p>
     *
     *	<ul><li>inline</li>
     *	    <li>attachment</li></ul>
     *
     *	<p> See RFC 2183 for more information.  This value may be set by the
     *	    {@link ContentSource}.  If not specified, nothing will be set.
     *	    This value may be used in conjunction with the
     *	    {@link #CONTENT_FILENAME} attribute, or the entire content
     *	    disposition may be specified with this attribute.</p>
     */
    public static final String CONTENT_DISPOSITION = "disposition";

    /**
     *	<p> The value for the "Content-Disposition" ("filename"). This is the
     *	    {@link Context} attribute used to specify an explicit "filename".
     *	    It may be set by the {@link ContentSource}.  If not specified,
     *	    nothing will be set.  If {@link #CONTENT_DISPOSITION} is also set,
     *	    this method will append the file name.  If not set, it will set
     *	    the contentDisposition to "attachment".</p>
     */
    public static final String CONTENT_FILENAME = "filename";

    /**
     *	<p> This is the path of the requested file ("filename").  It is the
     *	    responsibility of the {@link Context} implementation to provide
     *	    this information as an attribute.</p>
     */
    public static final String FILE_PATH = "filePath";

    /**
     *	<p> This is the parameter that may be provided to identify the
     *	    {@link ContentSource} implementation to be used.  This value must
     *	    match the value returned by the <code>ContentSource</code>
     *	    implementation's <code>getId()</code> method.  It is typical for
     *	    {@link Context} implementations to allow this to be specified by a
     *	    <code>HttpServletRequest</code> parameter.</p>
     */
    public static final String CONTENT_SOURCE_ID    = "contentSourceId";

    /**
     *	<p> This String ("ContentSources") is the name if the
     *	    <code>Servlet</code> init parameter or context param (depending
     *	    on environment implementation) that should be used to register
     *	    all available {@link ContentSource} implementations.  This should
     *	    be a list of full classnames of the {@link ContentSource}s.</p>
     */
    public static final String CONTENT_SOURCES	    = "ContentSources";

    /**
     *	<p> This is the id of the default {@link ContentSource}.  This is set
     *	    to the id of the {@link ResourceContentSource}.</p>
     */
    public static final String DEFAULT_CONTENT_SOURCE_ID =
	    ResourceContentSource.ID;
}
