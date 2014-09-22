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

import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.descriptors.Resource;


/**
 *  <p>	This file defines the ResourceFactory interface.  Resources are added
 *	to the Request scope so that they may be accessed easily using JSF EL
 *	value-binding, or by other convient means.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public interface ResourceFactory {

    /**
     *	<p> This is the method responsible for getting the resource using the
     *	    given {@link Resource} descriptor.</p>
     *
     *	@param	context	    The FacesContext
     *	@param	descriptor  The Resource descriptor that is associated
     *			    with the requested Resource.
     *
     *	@return	The newly created (or found) resource.
     */
    public Object getResource(FacesContext context, Resource descriptor);
}
