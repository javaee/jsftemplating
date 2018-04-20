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
package com.sun.jsftemplating.layout.descriptors.handler;


/**
 *  <p>	This class implements the OutputType interface to provide a way to
 *	get/set Output values from a ServletRequest attribute Map.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class RequestAttributeOutputType implements OutputType {

    /**
     *	<p> This method is responsible for retrieving the value of the Output
     *	    from a Request attribute.  'key' may be null, if this occurs, a
     *	    default name will be provided.  That name will follow the
     *	    following format:</p>
     *
     *	<p> [handler-id]:[output-name]</p>
     *
     *	@param	context	    The HandlerContext
     *
     *	@param	outDesc	    The IODescriptor for this Output value in
     *			    which to obtain the value
     *
     *	@param	key	    The optional 'key' to use when retrieving the
     *			    value from the ServletRequest attribute Map.
     *
     *	@return The requested value.
     */
    public Object getValue(HandlerContext context, IODescriptor outDesc, String key) {
	if (key == null) {
	    // Provide a reasonably unique default
	    key = context.getHandlerDefinition().getId()
		+ ':' + outDesc.getName();
	}

	// Get it from the Request attribute map
	return context.getFacesContext().getExternalContext().
	    getRequestMap().get(key);
    }

    /**
     *	<p> This method is responsible for setting the value of the Output to
     *	    a ServletRequest attribute.  'key' may be null, in this case, a
     *	    default name will be provided.  That name will follow the
     *	    following format:</p>
     *
     *	<p> [handler-id]:[output-name]</p>
     *
     *	@param	context	    The HandlerContext
     *
     *	@param	outDesc	    The IODescriptor for this Output value in
     *			    which to obtain the value
     *
     *	@param	key	    The optional 'key' to use when setting the
     *			    value into the ServletRequest attribute Map
     *
     *	@param	value	    The value to set
     */
    public void setValue(HandlerContext context, IODescriptor outDesc, String key, Object value) {
	if (key == null) {
	    // Provide a reasonably unique default
	    key = context.getHandlerDefinition().getId()
		+ ':' + outDesc.getName();
	}

	// Get it from the Request attribute map
	context.getFacesContext().getExternalContext().
	    getRequestMap().put(key, value);
    }
}
