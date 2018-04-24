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
     *	<p> This method returns the path of the resource that was
     *	    requested.</p>
     */
    public String getResourcePath(Context ctx);

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
