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
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 *  <p>	This {@link LayoutElement} provides a means to identify a portion of
 *	the LayoutDefinition tree by name (id).  This is used by
 *	{@link LayoutInsert} to include portions of the tree defined elsewhere
 *	at the location of the {@link LayoutInsert}.</p>
 *
 *  @author Jason Lee
 */
public class LayoutDefine extends LayoutElementBase {
    private static final long serialVersionUID = 1L;

    /**
     * @param parent
     * @param id
     */
    public LayoutDefine(LayoutElement parent, String id) {
        super(parent, id);
    }

    @Override
    protected boolean encodeThis(FacesContext context, UIComponent component) throws IOException {
        return true;
    }
}
