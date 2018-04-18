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

package com.sun.jsftemplating.resource;

import java.util.Map;

import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.descriptors.Resource;
import com.sun.jsftemplating.util.Util;


/**
 *  <p>	This factory class provides a means to instantiate a
 *	java.util.ResouceBundle.  It implements the {@link ResourceFactory}
 *	which the
 *	{@link com.sun.jsftemplating.renderer.TemplateRenderer} knows
 *	how to use to create arbitrary {@link Resource} objects.  This
 *	factory utilizes the ResourceBundleManager for efficiency.</p>
 *
 *  @see ResourceFactory
 *  @see Resource
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class ResourceBundleFactory implements ResourceFactory {

    /**
     *	<p> This is the factory method responsible for obtaining a
     *	    ResourceBundle.  This method uses the ResourceBundleManager to
     *	    manage instances of ResourceBundles per key/locale.</p>
     *
     *	<p> It should be noted that this method does not do anything if there
     *	    is already a request attribute with the given id.</p>
     *
     *	@param	context	    The FacesContext
     *	@param	descriptor  The Resource descriptor that is associated
     *			    with the requested Resource.
     *
     *	@return	The newly created Resource
     */
    public Object getResource(FacesContext context, Resource descriptor) {
	// Get the id from the descriptor, this is the id that should be used
	// to store it in the RequestScope
	String id = descriptor.getId();
	Map<String, Object> map = context.getExternalContext().getRequestMap();
	if (map.containsKey(id)) {
	    // It is already set
	    return map.get(id);
	}

	// Obtain the ResourceBundle
	Object resource = ResourceBundleManager.getInstance(context).getBundle(
	    descriptor.getExtraInfo(), Util.getLocale(context));

	// The id does not exist in the request scope yet.
	map.put(id, resource);

	return resource;
    }
}
