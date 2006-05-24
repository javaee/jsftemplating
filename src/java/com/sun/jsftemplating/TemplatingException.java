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
package com.sun.jsftemplating;

import com.sun.jsftemplating.layout.descriptors.LayoutElement;

import javax.faces.component.UIComponent;


/**
 *  <p>	This is the base exception class for other exception types that may be
 *	used in this project.  It provides a means for setting / obtaining the
 *	responsible {@link LayoutElement} and / or <code>UIComponent</code>
 *	associated with the <code>Exception</code>.  This information is
 *	optional and may be null.</p>
 */
public class TemplatingException extends RuntimeException {

    /**
     *	<p> This is the preferred constructor.</p>
     */
    public TemplatingException(String msg, Throwable ex, LayoutElement elt, UIComponent comp) {
	super(msg, ex);

	// Setup the rest
	setResponsibleLayoutElement(elt);
	setResponsibleUIComponent(comp);
    }

    /**
     *
     */
    public TemplatingException() {
	super();
    }

    /**
     *
     */
    public TemplatingException(LayoutElement elt, UIComponent comp) {
	super();

	// Setup the rest
	setResponsibleLayoutElement(elt);
	setResponsibleUIComponent(comp);
    }

    /**
     *
     */
    public TemplatingException(Throwable ex) {
	super(ex);
    }

    /**
     *
     */
    public TemplatingException(Throwable ex, LayoutElement elt, UIComponent comp) {
	super(ex);

	// Setup the rest
	setResponsibleLayoutElement(elt);
	setResponsibleUIComponent(comp);
    }

    /**
     *
     */
    public TemplatingException(String msg) {
	super(msg);
    }

    /**
     *	This is the preferred constructor if there is no root cause.
     */
    public TemplatingException(String msg, LayoutElement elt, UIComponent comp) {
	super(msg);

	// Setup the rest
	setResponsibleLayoutElement(elt);
	setResponsibleUIComponent(comp);
    }

    /**
     *
     */
    public TemplatingException(String msg, Throwable ex) {
	super(msg, ex);
    }

    /**
     *	Allow the Exception to hold the responsible UIComponent
     */
    public void setResponsibleUIComponent(UIComponent comp) {
	_comp = comp;
    }

    /**
     *	Allow the Exception to hold the responsible UIComponent
     */
    public UIComponent getResponsibleUIComponent() {
	return _comp;
    }

    /**
     *	Allow the Exception to hold the responsible LayoutElement
     */
    public void setResponsibleLayoutElement(LayoutElement elt) {
	_elt = elt;
    }

    /**
     *	Allow the responsible LayoutElement to be obtained.
     *
     *	@return The responsible LayoutElement (null if not specified)
     */
    public LayoutElement getResponsibleLayoutElement() {
	return _elt;
    }


    private UIComponent		_comp	    = null;
    private LayoutElement	_elt	    = null;
}
