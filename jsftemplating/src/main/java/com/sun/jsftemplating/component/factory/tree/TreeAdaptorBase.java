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

package com.sun.jsftemplating.component.factory.tree;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;


/**
 *  <p>	This class provides some of the implemenation for the methods required
 *	by the TreeAdaptor interface.  This class may be extended to assist in
 *	implementing a TreeAdaptor implementation.</p>
 *
 *  <p> The <code>TreeAdaptor</code> implementation must have a <code>public
 *	static TreeAdaptor getInstance(FacesContext, LayoutComponent,
 *	UIComponent)</code> method in order to get access to an instance of the
 *	<code>TreeAdaptor</code> instance.</p>
 *
 *  @see    TreeAdaptor
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public abstract class TreeAdaptorBase implements TreeAdaptor {

    /**
     *	<p> This Constructor does nothing.  If you need to store a reference
     *	    to the <code>LayoutComponent</code> or <code>UIComponent</code>
     *	    associated with this <code>TreeAdaptor</code>, it may be more
     *	    convenient to use a different constructor.</p>
     */
    protected TreeAdaptorBase() {
    }

    /**
     *	<p> This Constructor save the <code>LayoutComponent</code> and the
     *	    <code>UIComponent</code> for easy use later.</p>
     */
    protected TreeAdaptorBase(LayoutComponent desc, UIComponent parent) {
	setLayoutComponent(desc);
	setParentUIComponent(parent);
    }

    /**
     *	<p> This method retrieves the <code>LayoutComponent</code> associated
     *	    with this <code>TreeAdaptor</code>.</p>
     */
    public LayoutComponent getLayoutComponent() {
	return _layoutComponent;
    }

    /**
     *	<p> This method sets the <code>LayoutComponent</code> associated
     *	    with this <code>TreeAdaptor</code>.</p>
     */
    public void setLayoutComponent(LayoutComponent comp) {
	_layoutComponent = comp;
    }

    /**
     *	<p> This method retrieves the <code>UIComponent</code> associated
     *	    with this <code>TreeAdaptor</code>.</p>
     */
    public UIComponent getParentUIComponent() {
	return _parent;
    }

    /**
     *	<p> This method sets the <code>UIComponent</code> associated with this
     *	    <code>TreeAdaptor</code>.</p>
     */
    public void setParentUIComponent(UIComponent comp) {
	_parent = comp;
    }

    /**
     *	<p> This method is called shortly after
     *	    getInstance(FacesContext, LayoutComponent, UIComponent).  It
     *	    provides a place for post-creation initialization to take occur.</p>
     *
     *	<p> This implemenation does nothing.</p>
     */
    public void init() {
    }

    /**
     *	<p> Returns the model object for the top <code>TreeNode</code>, this
     *	    may contain sub <code>TreeNode</code>s.</p>
     *
     *	<p> This implementation returns the value that was supplied by
     *	    {@link #setTreeNodeObject(Object)}.  If that method is not explicitly
     *	    called, then this implementation will return null.</p>
     */
    public Object getTreeNodeObject() {
	return _topNodeObject;
    }

    /**
     *	<p> This method stores the top tree node model object.</p>
     */
    public void setTreeNodeObject(Object nodeObject) {
	_topNodeObject = nodeObject;
    }

    /**
     *	<p> This method returns the <code>UIComponent</code> factory class
     *	    implementation that should be used to create a
     *	    <code>TreeNode</code> for the given tree node model object.</p>
     */
    public String getFactoryClass(Object nodeObject) {
	return "com.sun.jsftemplating.component.factory.sun.TreeNodeFactory";
    }

    /**
     *	<p> This method returns any facets that should be applied to the
     *	    <code>TreeNode (comp)</code>.  Useful facets for the sun
     *	    <code>TreeNode</code> component are: "content" and "image".</p>
     *
     *	<p> Facets that already exist on <code>comp</code>, or facets that
     *	    are directly added to <code>comp</code> do not need to be returned
     *	    from this method.</p>
     *
     *	@param	nodeObject  The (model) object representing the tree node.
     */
    public Map<String, UIComponent> getFacets(Object nodeObject) {
	return null;
    }

    /**
     *	<p> Advanced framework feature which provides better handling for
     *	    things such as expanding TreeNodes, beforeEncode, and other
     *	    events.</p>
     *
     *	<p> This method should return a <code>Map</code> of <code>List</code>
     *	    of <code>Handler</code> objects.  Each <code>List</code> in the
     *	    <code>Map</code> should be registered under a key that cooresponds
     *	    to to the "event" in which the <code>Handler</code>s should be
     *	    invoked.</p>
     *
     *	<p> This implementation returns null.  This method must be overriden
     *	    to take advantage of this feature.</p>
     */
    public Map<String, List<Handler>> getHandlersByType(UIComponent comp, Object nodeObject) {
	return null;
    }


    private Object	    _topNodeObject	= null;
    private LayoutComponent _layoutComponent	= null;
    private UIComponent	    _parent		= null;
}
