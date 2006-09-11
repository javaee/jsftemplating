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

import com.sun.jsftemplating.el.PermissionChecker;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *  <p>	This class defines a LayoutIf {@link LayoutElement}.  The LayoutIf
 *	provides the functionality necessary to conditionally display a portion
 *	of the layout tree.  The condition is a boolean equation and may use
 *	"$...{...}" type expressions to substitute in values.</p>
 *
 *  <p>	Depending on its environment, this {@link LayoutElement} can represent
 *	an {@link If} <code>UIComponent</code> or simply exist as a
 *	{@link LayoutElement}.  When its {@link #encode} method is called, the
 *	if functionality will act as a {@link LayoutElement}.  When the
 *	{@link LayoutComponent#getChild(FacesContext, UIComponent)} method is
 *	called, it will create an {@link If} <code>UIComponent</code>.</p>
 *
 *  @see com.sun.jsftemplating.el.VariableResolver
 *  @see com.sun.jsftemplating.el.PermissionChecker
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutIf extends LayoutComponent {

    /**
     *	Constructor.
     */
    public LayoutIf(LayoutElement parent, String condition) {
	this(parent, condition,
	    LayoutDefinitionManager.getGlobalComponentType("if"));
    }

    /**
     *	<p> This constructor may be used by subclasses which wish to provide
     *	    an alternate {@link ComponentType}.  The {@link ComponentType} is
     *	    used to instantiate an {@link com.sun.jsftemplating.component.If}
     *	    <code>UIComponent</code> (or whatever the given
     *	    {@link ComponentType} specifies).  This occurs when this
     *	    {@link LayoutElement} is nested inside a {@link LayoutComponent}.
     *	    It must create a <code>UIComponent</code> in order to ensure it
     *	    is executed because during rendering there is no other way to get
     *	    control to perform the functionality provided by this
     *	    {@link LayoutElement}.</p>
     */
    protected LayoutIf(LayoutElement parent, String condition, ComponentType type) {
	super(parent, (String) null, type);
	setFacetChild(false);
	addOption("condition", condition);
	if (condition.equals("$property{condition}")) {
	    _doubleEval = true;
	}
    }

    /**
     *	This method returns true if the condition of this LayoutIf is met,
     *	false otherwise.  This provides the functionality for conditionally
     *	displaying a portion of the layout tree.
     *
     *	@param	context	    The FacesContext
     *	@param	component   The UIComponent
     *
     *	@return	true if children are to be rendered, false otherwise.
     */
    public boolean encodeThis(FacesContext ctx, UIComponent comp) {
	PermissionChecker checker = new PermissionChecker(
	    this, comp, (_doubleEval) ?
		(getEvaluatedOption(ctx, "condition", comp).toString()) :
		((String) getOption("condition")));
	return checker.hasPermission();
    }

    /**
     *	<p> This flag is set to true when the condition equals
     *	    "$property{condition}".  This is a special case where the value to
     *	    be evaluated is not $property{condition}, but rather the value of
     *	    this expression.  This requires double evaluation to correct
     *	    interpret the expression.  For now this is a hack for this case
     *	    only.  In the future we may want to support an $eval{} or something
     *	    more general syntax for doing this declaratively.</p>
     */
    private boolean _doubleEval = false;
}
