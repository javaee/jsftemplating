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

package com.sun.jsftemplating.el;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;


/**
 *  <p>	This <code>VariableResolver</code> exists to resolve "page session"
 *	attributes.  This concept, borrowed from NetDynamics / JATO, stores
 *	data w/ the page so that it is available throughout the life of the
 *	page.  This is longer than request scope, but usually shorter than
 *	session.  This implementation stores the attributes on the
 *	<code>UIViewRoot</code>.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class PageSessionResolver extends VariableResolver {

    /**
     *	<p> Constructor.</p>
     */
    public PageSessionResolver(VariableResolver orig) {
	super();
	_origVariableResolver = orig;
    }

    /**
     *	<p> This first delegates to the original <code>VariableResolver</code>,
     *	    it then checks "page session" to see if the value exists.</p>
     */
    public Object resolveVariable(FacesContext context, String name) throws EvaluationException {
	Object result = null;
	// Check to see if expression explicitly asks for PAGE_SESSION
	if (name.equals(PAGE_SESSION)) {
	    // It does, return the Map
	    UIViewRoot root = context.getViewRoot();
	    result = getPageSession(context, root);
	    if (result == null) {
		// No Map!  That's ok, create one...
		result = createPageSession(context, root);
	    }
	} else {
	    if (_origVariableResolver != null) {
		// Not explicit, let original resolver do its thing first...
		result = _origVariableResolver.resolveVariable(context, name);
	    }

	    if (result == null) {
		// Original resolver couldn't find anything, check page session
		Map<String, Serializable> map =
		    getPageSession(context, (UIViewRoot) null);
		if (map != null) {
		    result = map.get(name);
		}
	    }
	}
	return result;
    }

    /**
     *	<p> This method provides access to the "page session"
     *	    <code>Map</code>.  If it doesn't exist, it returns
     *	    <code>null</code>.  If the given <code>UIViewRoot</code> is null,
     *	    then the current <code>UIViewRoot</code> will be used.</p>
     */
    public static Map<String, Serializable> getPageSession(FacesContext ctx, UIViewRoot root) {
	if (root == null) {
	    root = ctx.getViewRoot();
	}
	return (Map<String, Serializable>)
	    root.getAttributes().get(PAGE_SESSION_KEY);
    }

    /**
     *	<p> This method will create a new "page session" <code>Map</code>.  It
     *	    will overwrite any existing "page session" <code>Map</code>, so be
     *	    careful.</p>
     */
    public static Map<String, Serializable> createPageSession(FacesContext ctx, UIViewRoot root) {
	if (root == null) {
	    root = ctx.getViewRoot();
	}
	// Create it...
	Map<String, Serializable> map = new HashMap<String, Serializable>(4);

	// Store it...
	root.getAttributes().put(PAGE_SESSION_KEY, map);

	// Return it...
	return map;
    }

    /**
     *	<p> The original <code>VariableResolver</code>.</p>
     */
    private VariableResolver _origVariableResolver = null;

    /**
     *	<p> The attribute key in which to store the "page" session Map.</p>
     */
    private static final String PAGE_SESSION_KEY	= "_ps";

    /**
     *	<p> The name an expression must use when it explicitly specifies page
     *	    session. ("pageSession")</p>
     */
    public static final String PAGE_SESSION		= "pageSession";
}
