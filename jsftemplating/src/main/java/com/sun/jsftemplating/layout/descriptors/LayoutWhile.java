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

package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.el.PermissionChecker;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.event.AfterLoopEvent;
import com.sun.jsftemplating.layout.event.BeforeLoopEvent;


/**
 *  <p>	This class defines a LayoutWhile {@link LayoutElement}.  The
 *	LayoutWhile provides the functionality necessary to iteratively
 *	display a portion of the layout tree.  The condition is a boolean
 *	equation and may use "$...{...}" type expressions to substitute
 *	values.</p>
 *
 *  @see com.sun.jsftemplating.el.VariableResolver
 *  @see com.sun.jsftemplating.el.PermissionChecker
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutWhile extends LayoutIf {
    private static final long serialVersionUID = 1L;

    /**
     *	Constructor
     */
    public LayoutWhile(LayoutElement parent, String condition) {
	super(parent, condition,
	    LayoutDefinitionManager.getGlobalComponentType(null, "while"));
    }


    /**
     *	<p> This method always returns true.  The condition is checked in
     *	    {@link #shouldContinue(UIComponent)} instead of here because
     *	    the {@link #encode(FacesContext, UIComponent)} method
     *	    evaluates the condition and calls the super.  Performing the check
     *	    here would cause the condition to be evaluated twice.</p>
     *
     *	@param	context	    The FacesContext
     *	@param	component   The UIComponent
     *
     *	@return	true
     */
    public boolean encodeThis(FacesContext context, UIComponent component) {
	return true;
    }

    /**
     *	<p> This method returns true if the condition of this LayoutWhile is
     *	    met, false otherwise.  This provides the functionality for
     *	    iteratively displaying a portion of the layout tree.</p>
     *
     *	@param	component   The UIComponent
     *
     *	@return	true if children are to be rendered, false otherwise.
     */
    protected boolean shouldContinue(UIComponent component) {
	PermissionChecker checker =
	    new PermissionChecker(this, component,
		(String) getOption("condition"));
	return checker.hasPermission();
    }

    /**
     *	<p> This implementation overrides the parent <code>encode</code>
     *	    method.  It does this to cause the encode process to loop while
     *	    {@link #shouldContinue(UIComponent)} returns
     *	    true.  Currently there is no infinite loop checking, so be
     *	    careful.</p>
     *
     *	@param	context	    The FacesContext
     *	@param	component   The UIComponent
     */
    public void encode(FacesContext context, UIComponent component) throws IOException {
	dispatchHandlers(context, BEFORE_LOOP,
	    new BeforeLoopEvent((UIComponent) component));
	while (shouldContinue(component)) {
	    super.encode(context, component);
	}
	dispatchHandlers(context, AFTER_LOOP,
	    new AfterLoopEvent((UIComponent) component));
    }

    /**
     *	<p> This is the event "type" for
     *	    {@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
     *	    elements to be invoked after this LayoutWhile is processed
     *	    (outside loop).</p>
     */
     public static final String AFTER_LOOP =	"afterLoop";

    /**
     *	<p> This is the event "type" for
     *	    {@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
     *	    elements to be invoked before this LayoutWhile is processed
     *	    (outside loop).</p>
     */
     public static final String BEFORE_LOOP =	"beforeLoop";
}
