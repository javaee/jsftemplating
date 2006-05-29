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
package com.sun.jsftemplating.util;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutFacet;


/**
 *  <p>	This class is a utility class for misc {@link LayoutElement} related
 *	tasks.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class LayoutElementUtil {

    /**
     *	<p> This method determines if the given {@link LayoutElement} is
     *	    inside a {@link LayoutComponent}.  It will look at all the
     *	    parents, if any of them are {@link LayoutComponent} instances,
     *	    this method will return <code>true</code>.</p>
     *
     *	@param	elt	The {@link LayoutElement} to check.
     *
     *	@return	true	If a {@link LayoutComponent} exists as an ancestor.
     */
    public static boolean isNestedLayoutComponent(LayoutElement elt) {
	return (getParentLayoutComponent(elt) != null);
    }

    /**
     *	<p> This method returns the nearest {@link LayoutComponent} that
     *	    contains the given {@link LayoutElement}, null if none.</p>
     *
     *	@param	elt	The {@link LayoutElement} to start with.
     *
     *	@return	The containing {@link LayoutComponent} if one exists.
     */
    public static LayoutComponent getParentLayoutComponent(LayoutElement elt) {
	elt = elt.getParent();
	while (elt != null) {
	    if (elt instanceof LayoutComponent) {
		return (LayoutComponent) elt;
	    }
	    elt = elt.getParent();
	}
	return null;
    }

    /**
     *	<p> This method returns true if any of the parent
     *	    {@link LayoutElement}s are {@link LayoutComponent}s.  If a
     *	    {@link LayoutFacet} is encountered first, false is automatically
     *	    returned.  This method is specific to processing needed for
     *	    creating a {@link LayoutComponent}.  Do not use this for general
     *	    cases when you need to find if a {@link LayoutElement} is embedded
     *	    within a {@link LayoutComponent} (which should ignore
     *	    {@link LayoutFacet} elements).</p>
     *
     *	@param	elt	The {@link LayoutElement} to check.
     *
     *	@return true if it has a {@link LayoutComponent} ancestor.
     */
    public static boolean isLayoutComponentChild(LayoutElement elt) {
	elt = elt.getParent();
	while (elt != null) {
	    if (elt instanceof LayoutComponent) {
		return true;
	    } else if (elt instanceof LayoutFacet) {
		// Don't consider it a child if it is a facet
		return false;
	    }
	    elt = elt.getParent();
	}

	// Not found
	return false;
    }

    /**
     *	<p> This method produces a generated ID.  It optionally uses the given
     *	    base as a prefix to the generated ID ({@link #DEFAULT_ID_BASE} is
     *	    used otherwise).  This implementation will generate an id that
     *	    contains a number between 1 and {@link #MAX_ID}.  Do not depend on
     *	    this implementation, it may change in the future.</p>
     */
    public static String getGeneratedId(String base) {
	if (base == null) {
	    base = DEFAULT_ID_BASE;
	} else {
	    base = base.trim();
	    if (base.equals("")) {
		base = DEFAULT_ID_BASE;
	    } else {
		StringBuffer buf = new StringBuffer();
		int lowch;
		for (int ch : base.toCharArray()) {
		    lowch = ch | 0x20;
		    if ((lowch >= 'a') && (lowch <= 'z')) {
			buf.append((char) ch);
		    } else {
			buf.append('_');
		    }
		}
		base = buf.toString();
	    }
	}
	return base + (_idNum++ % MAX_ID);
    }


    /**
     *	<p> This value represents the maximum number that is contained in an
     *	    auto generated id.  I intentionally did not make this final so that
     *	    if needed it can be tweaked at runtime.  However, I do not think
     *	    this will ever be necessary (id's can be specified, and this many
     *	    unspecified ids is unlikely to be needed on a single page!).</p>
     */
    public  static int	MAX_ID			= 0x00010000;
    private static int	_idNum			= 1;

    public static final String DEFAULT_ID_BASE	= "id";
}
