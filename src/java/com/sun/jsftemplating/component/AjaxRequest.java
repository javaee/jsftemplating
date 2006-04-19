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
package com.sun.jsftemplating.component;


/**
 *  <p>	This <code>UIComponent</code> exists to facilitate Ajax requests.
 *	Since these requests send information via the XMLHttpRequest Object, a
 *	standard href or form submit will not work.  Further it is cumbersome
 *	to override the JavaScript in the available set of components to
 *	expect the developer to do this for each request.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class AjaxRequest extends TemplateComponentBase {
    /**
     *	<p> Constructor for <code>AjaxRequest</code>.</p>
     */
    public AjaxRequest() {
	super();
	setRendererType("com.sun.jsftemplating.AjaxRequest");
	setLayoutDefinitionKey(LAYOUT_KEY);
    }

    /**
     *	<p> Return the family for this component.</p>
     */
    public String getFamily() {
	return "com.sun.jsftemplating.AjaxRequest";
    }

    /**
     *	<p> This is the location of the XML file that declares the layout for
     *	    the AjaxRequest. (jsftemplating/ajaxRequest.xml)</p>
     */
    public static final String	LAYOUT_KEY  =	"jsftemplating/ajaxRequest.xml";
}
