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
