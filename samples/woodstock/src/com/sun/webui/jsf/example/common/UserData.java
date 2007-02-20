/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://woodstock.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://woodstock.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
 
package com.sun.webui.jsf.example.common;

import com.sun.data.provider.TableDataProvider;
import com.sun.data.provider.impl.ObjectArrayDataProvider;

import java.util.List;

/**
 * This class contains data provider.
 */
public class UserData {
    
    /** Data provider. */
    private TableDataProvider provider = null; 
    
    /** Default constructor. */
    public UserData() {
    }
    
    /** Construct an instance using given Object array. */
    public UserData(Object[] array) {        
        provider = new ObjectArrayDataProvider(array);
    }    
    
    /** Get data provider. */
    public TableDataProvider getDataProvider() {
        return provider;
    }
}
