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
import java.io.InputStream;


/**
 *  <p>	Implement this interface to provide an Object that is capable of
 *	providing data to <code>FileStreamer</code>.
 *	<code>ContentSource</code> implementations must be thread safe.  The
 *	<code>FileStreamer</code> will reuse the same instance when 2 requests
 *	are made to the same ContentSource type.  Instance variables,
 *	therefore, should not be used; you may use the context to store local
 *	information.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public interface ContentSource {

    /**
     *	<p> This method should return a unique string used to identify this
     *	    <code>ContentSource</code>.  This string must be specified in order
     *	    to select the appropriate <code>ContentSource</code> when using the
     *	    <code>FileStreamer</code>.</p>
     */
    public String getId();

    /**
     *  <p> This method is responsible for generating the content and returning
     *	    an InputStream to that content.  It is also responsible for setting
     *	    any attribute values in the {@link Context}, such as
     *	    {@link Context#EXTENSION} or {@link Context#CONTENT_TYPE}.</p>
     */
    public InputStream getInputStream(Context ctx) throws IOException;

    /**
     *	<p> This method may be used to clean up any temporary resources.  It
     *	    will be invoked after the <code>InputStream</code> has been
     *	    completely read.</p>
     */
    public void cleanUp(Context ctx);

    /**
     *	<p> This method is responsible for returning the last modified date of
     *	    the content, or -1 if not applicable.  This information will be
     *	    used for caching.</p>
     */
    public long getLastModified(Context context);
}
