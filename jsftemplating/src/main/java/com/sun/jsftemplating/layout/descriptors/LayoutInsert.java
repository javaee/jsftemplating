/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2018 Oracle and/or its affiliates. All rights reserved.
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

import com.sun.jsftemplating.layout.event.EncodeEvent;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *  <p>	This class represents a <code>ui:insert</code>.</p>
 *
 *  @author Jason Lee
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutInsert extends LayoutElementBase {
    private static final long serialVersionUID = 1L;
    private String name;

    /**
     * @param parent
     * @param id
     */
    public LayoutInsert(LayoutElement parent, String id) {
	super(parent, id);
    }

    /**
     *	<p> Returns the name of the {@link LayoutDefine} to look for when
     *	    including content for this <code>LayoutInsert</code>.  This value
     *	    may be <code>null</code> to indicate that it should use its body
     *	    content.</p>
     */
    public String getName() {
	return name;
    }

    /**
     *	<p> Sets the name of the {@link LayoutDefine} to look for when including
     *	    content for this <code>LayoutInsert</code>.  This value may be
     *	    <code>null</code> to indicate that it should use its body content.</p>
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * <p>  This method is override to enable searching of its content via the
     *	    name of this insert (if supplied), or the rendering of its body if
     *	    not supplied, or not found.  If this is encountered outside the
     *	    context of a composition, it will render its body content also.</p>
     *
     * @see LayoutElementBase#encodeThis(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    protected boolean encodeThis(FacesContext context, UIComponent component) throws IOException {
	Stack<LayoutElement> stack =
	    LayoutComposition.getCompositionStack(context);
	if (stack.empty()) {
	    // Render whatever is inside the insert
	    return true;
	}

	// Get assoicated UIComposition
	String name = getName();
	if (name == null) {
	    encodeChildren(context, component, stack.get(0));
	} else {
	    // First resolve any EL in the insertName
	    name = "" + resolveValue(context, component, name);

	    // Search for specific LayoutDefine
	    LayoutElement def = LayoutInsert.findLayoutDefine(
		    context, component, stack, name);
	    if (def == null) {
		// Render whatever is inside the insert
		return true;
	    } else {
		// Found ui:define, render it
		encodeChildren(context, component, def);
	    }
	}
	return false; // Already rendered it
    }

    /**
     *	<p> Encode the appropriate children...</p>
     */
    private void encodeChildren(FacesContext context, UIComponent component, LayoutElement parentElt) throws IOException {
	// Fire an encode event
	dispatchHandlers(context, ENCODE, new EncodeEvent(component));

	// Iterate over children
	LayoutElement childElt = null;
	Iterator<LayoutElement> it = parentElt.getChildLayoutElements().iterator();
	while (it.hasNext()) {
	    childElt = it.next();
	    childElt.encode(context, component);
	}
    }

    /**
     *	<p> This method searches the given the entire <code>stack</code> for a
     *	    {@link LayoutDefine} with the given <code>name</code>.</p>
     */
    public static LayoutDefine findLayoutDefine(FacesContext context, UIComponent parent, List<LayoutElement> eltList, String name) {
	Iterator<LayoutElement> stackIt = eltList.iterator();
	LayoutDefine define = null;
	while (stackIt.hasNext()) {
	    define = findLayoutDefine(context, parent, stackIt.next(), name);
	    if (define != null) {
		return define;
	    }
	}

	// Not found!
	return null;
    }

    /**
     *	<p> This method searches the given {@link LayoutElement} for a
     *	    {@link LayoutDefine} with the given <code>name</code>.</p>
     */
    private static LayoutDefine findLayoutDefine(FacesContext context, UIComponent parent, LayoutElement elt, String name) {
	Iterator<LayoutElement> it = elt.getChildLayoutElements().iterator();
	LayoutElement def = null;
	while (it.hasNext()) {
	    def = it.next();
	    if ((def instanceof LayoutDefine) && def.
		    getId(context, parent).equals(name)) {
		// We found what we're looking for...
		return (LayoutDefine) def;
	    }
	}

	// We still haven't found it, search the child LayoutElements
	it = elt.getChildLayoutElements().iterator();
	while (it.hasNext()) {
	    def = findLayoutDefine(context, parent, it.next(), name);
	    if (def != null) {
		return (LayoutDefine) def;
	    }
	}

	// Not found!
	return null;
    }
}
