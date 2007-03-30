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
     *	<p> The value for the "Content-Disposition" ("Filename"). This is the
     *	    {@link Context} attribute used to specify an explicit "Filename".
     *	    It may be set by the {@link ContentSource}.  If not specified,
     *	    nothing will be set.</p>
     */
    public static final String CONTENT_FILENAME = "Filename";
}
