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
package com.sun.jsftemplating.component.factory.tree;

import com.sun.jsftemplating.layout.descriptors.handler.Handler;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;


/**
 *  <p>	This interface defines the methods required by
 *	{@link DynamicTreeNodeFactory}.  By providing these methods, you are
 *	able to interface some tree structure with the
 *	{@link DynamicTreeNodeFactory} so that whole or partial trees can be
 *	created without having to do any tree conversion work (the work is done
 *	by the <code>TreeAdaptor</code> implementation in conjunction with the
 *	{@link DynamicTreeNodeFactory}).</p>
 *
 *  <p> The <code>TreeAdaptor</code> implementation must have a <code>public
 *	static TreeAdaptor getInstance(FacesContext, LayoutComponent,
 *	UIComponent)</code> method in order to get access to an instance of the
 *	<code>TreeAdaptor</code> instance.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public interface TreeAdaptor {

    /**
     *	<p> This method is called shortly after
     *	    getInstance(FacesContext, LayoutComponent, UIComponent).  It
     *	    provides a place for post-creation initialization to take occur.</p>
     */
    public void init();

    /**
     *	<p> Returns the model object for the top <code>TreeNode</code>, this
     *	    may contain sub <code>TreeNode</code>s.</p>
     */
    public Object getTreeNodeObject();

    /**
     *	<p> Returns child <code>TreeNode</code>s for the given
     *	    <code>TreeNode</code> model Object.</p>
     */
    public List<Object> getChildTreeNodeObjects(Object nodeObject);

    /**
     *	<p> This method returns the <code>UIComponent</code> factory class
     *	    implementation that should be used to create a
     *	    <code>TreeNode</code> for the given tree node model object.</p>
     */
    public String getFactoryClass(Object nodeObject);

    /**
     *	<p> This method returns the "options" that should be supplied to the
     *	    factory that creates the <code>TreeNode</code> for the given tree
     *	    node model object.</p>
     *
     *	<p> Some useful options for the standard <code>TreeNode</code>
     *	    component include:<p>
     *
     *	<ul><li>text</li>
     *	    <li>url</li>
     *	    <li>imageURL</li>
     *	    <li>target</li>
     *	    <li>action<li>
     *	    <li>actionListener</li>
     *	    <li>expanded</li></ul>
     *
     *	<p> See Tree / TreeNode component documentation for more details.</p>
     */
    public Map<String, Object> getFactoryOptions(Object nodeObject);

    /**
     *	<p> This method returns the <code>id</code> for the given tree node
     *	    model object.</p>
     */
    public String getId(Object nodeObject);

    /**
     *	<p> This method returns any facets that should be applied to the
     *	    <code>TreeNode (comp)</code>.  Useful facets for the sun
     *	    <code>TreeNode</code> component are: "content" and "image".</p>
     *
     *	<p> Facets that already exist on <code>comp</code>, or facets that
     *	    are directly added to <code>comp</code> do not need to be returned
     *	    from this method.</p>
     *
     *	@param	comp	    The tree node <code>UIComponent</code>.
     *	@param	nodeObject  The (model) object representing the tree node.
     */
    public Map<String, UIComponent> getFacets(UIComponent comp, Object nodeObject);

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
     */
    public Map<String, List<Handler>> getHandlersByType(UIComponent comp, Object nodeObject);
}
