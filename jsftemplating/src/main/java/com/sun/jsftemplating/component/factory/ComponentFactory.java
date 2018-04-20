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

package com.sun.jsftemplating.component.factory;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;


/**
 *  <p>	This interface must be implemented by all UIComponent factories.
 *	This enabled UIComponents to be created via a consistent interface.
 *	This is critical to classes such as
 *	{@link com.sun.jsftemplating.component.TemplateComponentBase}
 *	and {@link LayoutComponent}.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public interface ComponentFactory {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>UIComponent</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent);

    /**
     *	<p> This method returns the extraInfo that was set for this
     *	    <code>ComponentFactory</code> from the
     *	    {@link com.sun.jsftemplating.layout.descriptors.ComponentType}.</p>
     */
    public Serializable getExtraInfo();

    /**
     *	<p> This method is invoked from the
     *	    {@link com.sun.jsftemplating.layout.descriptors.ComponentType} to
     *	    provide more information to the factory.  For example, if the JSF
     *	    component type was passed in, a single factory class could
     *	    instatiate multiple components the extra info that is passed in.</p>
     *
     *	<p> Some factory implementations may want to use this method to
     *	    execute intialization code for the factory based in the value
     *	    passed in.</p>
     */
    public void setExtraInfo(Serializable extraInfo);
}
