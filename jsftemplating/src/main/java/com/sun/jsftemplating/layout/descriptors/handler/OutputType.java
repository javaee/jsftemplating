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
 *  <p>	This interface provides an abstraction for different locations for
 *	storing output from a handler.  Implementations may store values in
 *	Session, request attributes, databases, etc.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public interface OutputType {

    /**
     *	<p> This method is responsible for retrieving the value of the Output
     *	    from the destination that was specified by handler.  'key' may be
     *	    null.  In cases where it is not needed, it can be ignored.  If it
     *	    is needed, the implementation may either provide a default or
     *	    throw an exception.</p>
     *
     *	@param	context	    The HandlerContext
     *
     *	@param	outDesc	    The IODescriptor for this Output value in
     *			    which to obtain the value
     *
     *	@param	key	    The optional 'key' to use when retrieving the
     *			    value from the OutputType
     *
     *	@return The requested value.
     */
    public Object getValue(HandlerContext context, IODescriptor outDesc, String key);

    /**
     *	<p> This method is responsible for setting the value of the Output
     *	    to the destination that was specified by handler.  'key' may be
     *	    null.  In cases where it is not needed, it can be ignored.  If it
     *	    is needed, the implementation may either provide a default or
     *	    throw an exception.</p>
     *
     *	@param	context	    The HandlerContext
     *
     *	@param	outDesc	    The IODescriptor for this Output value in
     *			    which to obtain the value
     *
     *	@param	key	    The optional 'key' to use when setting the
     *			    value from the OutputType
     *
     *	@param	value	    The value to set
     */
    public void setValue(HandlerContext context, IODescriptor outDesc, String key, Object value);
}
