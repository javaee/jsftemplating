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

package com.sun.webui.jsf.example.index;

import com.sun.webui.jsf.example.index.AppAction;

/**
 * A convenient class to wrap data about a given example.
 */
public class AppData {    
    
    /** The example app name */
    private String name;

    /** The concepts illustrated by this example */
    private String concepts;
    
    /** The example app action */
    private String appAction;

    /** 
     * Array of file names that will have source code
     * links for this example
     */
    private String[] files;               
    
    /**
     * Accepts info necessary to describe the given
     * example.
     *
     * @param name The name of the example
     * @param concepts The concepts illustrated by this example
     * @param appAction The example app action
     * @param files Array of file names for this example
     */
    public AppData(String name, String concepts, String appAction, String[] files) {
        this.name = name;
	this.concepts = concepts;
        this.appAction = appAction;
	this.files = files;
    }    
    
    /**
     * Get the name of the example
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the concepts illustrated by this example
     */
    public String getConcepts() {
        return concepts;
    }         
    
    /**
     * Get AppAction.
     */
    public AppAction getAppAction() {               
        return new AppAction(appAction);                
    }
    
    /**
     * Get array of files for this example
     */
    public String[] getFiles() {
        return files;
    }      
}
