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
