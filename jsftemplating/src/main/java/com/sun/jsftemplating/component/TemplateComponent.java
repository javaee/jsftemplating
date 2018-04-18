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

package com.sun.jsftemplating.component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;


/**
 *  <p>	This interface defines additional methods in addition to those defined
 *	by UIComponent that are needed to work with a TemplateRenderer.</p>
 *
 *  <p>	JSF did not define an interface for UIComponent, so I cannot extend an
 *	interface here.  This means that casting is needed to use UIComponent
 *	features from a TemplateComponent.</p>
 *
 *  <p>	If you need to have a <code>NamingContainer</code>, do not forget to
 *	implement that interface in addition to this interface.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public interface TemplateComponent extends ChildManager {

    /**
     *	This method will find the request child UIComponent by id.  If it is
     *	not found, it will attempt to create it if it can find a LayoutElement
     *	describing it.
     *
     *	@param	context	    The FacesContext
     *	@param	id	    The UIComponent id to search for
     *
     *	@return	The requested UIComponent
     */
    public UIComponent getChild(FacesContext context, String id);

    /**
     *	This method returns the LayoutDefinition associated with this component.
     *
     *	@param	context	The FacesContext
     *
     *	@return	LayoutDefinition associated with this component.
     */
    public LayoutDefinition getLayoutDefinition(FacesContext context);

    /**
     *	This method returns the LayoutDefinitionKey for this component.
     *
     *	@return	key	The key to use in the LayoutDefinitionManager
     */
    public String getLayoutDefinitionKey();

    /**
     *	This method sets the LayoutDefinition key for this component.
     *
     *	@param	key The key to use in the LayoutDefinitionManager
     */
    public void setLayoutDefinitionKey(String key);

    /**
     *	<p> This method returns the value of the requested field.  It should
     *	    first check the value of <code>field</code> passed in, it should
     *	    return that value if set.  Next, it should check to see if there
     *	    is a <code>ValueExpression</code> matching
     *	    <code>attributeName</code> and return that value, if it exists.
     *	    If neither of the first 2 cases yielded a result,
     *	    <code>defaultValue</code> is returned.</p>
     *
     *	@param	field		The field which may contain the value.
     *	@param	attributeName	The <code>ValueExpression</code> name.
     *	@param	defaultValue	The default value.
     *
     *	@return	The value of the property.
     */
    public <V> V getPropertyValue(V field, String attributeName, V defaultValue);
}
