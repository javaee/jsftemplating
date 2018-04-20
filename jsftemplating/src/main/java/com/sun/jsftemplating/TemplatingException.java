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

package com.sun.jsftemplating;

import javax.faces.component.UIComponent;

import com.sun.jsftemplating.layout.descriptors.LayoutElement;


/**
 *  <p>	This is the base exception class for other exception types that may be
 *	used in this project.  It provides a means for setting / obtaining the
 *	responsible {@link LayoutElement} and / or <code>UIComponent</code>
 *	associated with the <code>Exception</code>.  This information is
 *	optional and may be null.</p>
 */
public class TemplatingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

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
