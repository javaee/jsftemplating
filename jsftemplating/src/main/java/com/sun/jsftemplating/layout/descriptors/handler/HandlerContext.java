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

import java.util.EventObject;

import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.descriptors.LayoutElement;


/**
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public interface HandlerContext {

    /**
     *	<p> Accessor for the FacesContext.</p>
     *
     *	@return FacesContext
     */
    public FacesContext getFacesContext();

    /**
     *	<p> Accessor for the LayoutElement associated with this Handler.  The
     *	    LayoutElement associated with this Handler is the LayoutElement
     *	    which declared the handler.  This provides a way for the handler
     *	    to obtain access to the LayoutElement which is responsible for it
     *	    being invoked.</p>
     */
    public LayoutElement getLayoutElement();

    /**
     *	<p> Accessor for the EventObject associated with this Handler.  This
     *	    may be null if an EventObject was not created for this handler.
     *	    An EventObject, if it does exist, may provide additional details
     *	    describing the context in which this Event is invoked.</p>
     */
    public EventObject getEventObject();

    /**
     *	<p> This method provides access to the EventType.  This is mostly
     *	    helpful for diagnostics, but may be used in a handler to determine
     *	    more information about the context in which the code is
     *	    executing.</p>
     */
    public String getEventType();

    /**
     *	<p> Accessor for the Handler descriptor for this Handler.  The Handler
     *	    descriptor object contains specific meta information describing
     *	    the invocation of this handler.  This includes details such as
     *	    input values, and where output values are to be set.</p>
     */
    public Handler getHandler();

    /**
     *	<p> Setter for the Handler descriptor for this Handler.</p>
     *
     *	@param	handler	    The Handler
     */
    public void setHandler(Handler handler);

    /**
     *	<p> Accessor for the Handler descriptor for this Handler.  The
     *	    HandlerDefinition descriptor contains meta information about the
     *	    actual Java handler that will handle the processing.  This
     *	    includes the inputs required, outputs produces, and the types for
     *	    both.</p>
     */
    public HandlerDefinition getHandlerDefinition();

    /**
     *	<p> This method returns the value for the named input.  Input values
     *	    are not stored in this Context itself, but in the Handler.  If
     *	    you are trying to set input values for a handler, you must create
     *	    a new Handler object and set its input values.</p>
     *
     *	@param	name	    The input name
     *
     *	@return	The value of the input (null if not found)
     */
    public Object getInputValue(String name);

    /**
     *	<p> This method retrieves an Output value. Output values must not be
     *	    stored in this Context itself (remember HandlerContext objects
     *	    are shared).  Output values are stored according to what is
     *	    specified in the HandlerDefintion.</p>
     *
     *	@param	name	    The output name
     *
     *	@return	The value of the output (null if not found)
     */
    public Object getOutputValue(String name);

    /**
     *	<p> This method sets an Output value. Output values must not be
     *	    stored in this Context itself (remember HandlerContext objects
     *	    are shared).  Output values are stored according to what is
     *	    specified in the HandlerDefintion.</p>
     */
    public void setOutputValue(String name, Object value);
}
