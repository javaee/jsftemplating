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

package com.sun.jsftemplating.layout.descriptors.handler;


/**
 *  <p>	This class implements the OutputType interface to provide a way to
 *	get/set Output values from the Application Attribute Map.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class ApplicationAttributeOutputType implements OutputType {

    /**
     *	<p> This method is responsible for retrieving the value of the Output
     *	    from a Application attribute.  'key' may be null, if this occurs, a
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
     *			    value from the Application attribute Map.
     *
     *	@return The requested value.
     */
    public Object getValue(HandlerContext context, IODescriptor outDesc, String key) {
	if (key == null) {
	    // Provide a reasonably unique default
	    key = context.getHandlerDefinition().getId()
		+ ':' + outDesc.getName();
	}

	// Get it from the Application attribute map
	return context.getFacesContext().getExternalContext().
	    getApplicationMap().get(key);
    }

    /**
     *	<p> This method is responsible for setting the value of the Output to
     *	    a Application attribute.  'key' may be null, in this case, a
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
     *			    value into the Application attribute Map
     *
     *	@param	value	    The value to set
     */
    public void setValue(HandlerContext context, IODescriptor outDesc, String key, Object value) {
	if (key == null) {
	    // Provide a reasonably unique default
	    key = context.getHandlerDefinition().getId()
		+ ':' + outDesc.getName();
	}

	// Get it from the Application attribute map
	context.getFacesContext().getExternalContext().
	    getApplicationMap().put(key, value);
    }
}
