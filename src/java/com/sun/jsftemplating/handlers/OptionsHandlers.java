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

/*
 * OptionsHandlers.java
 *
 * Created on June 8, 2006, 5:01 PM
 */
package com.sun.jsftemplating.handlers;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;

import java.util.Collection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import javax.faces.model.SelectItem;


/**
 *
 *  @author Jennifer Chou
 */
public class OptionsHandlers {

    /**
     * Creates a new instance of OptionsHandlers
     */
    public OptionsHandlers() {
    }

    /**
     *	<p> This handler returns the "rave" version of Options
     *	    of the given <code>labels</code> and <code>values</code>.
     *	    <code>labels</code> and <code>values</code> arrays must be equal in
     *	    size and in matching sequence.</p>
     *
     *	<p> Input value: <code>labels</code> -- Type:
     *	    <code>java.util.Collection</code></p>
     *
     *	<p> Input value: <code>values</code> -- Type:
     *	    <code>java.util.Collection</code></p>
     *
     *  <p> Output value: <code>options</code> -- Type:
     *	    <code>SelectItem[] (castable to Option[])</code></p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="getRaveOptions",
	input={
	    @HandlerInput(name="labels", type=Collection.class, required=true),
	    @HandlerInput(name="values", type=Collection.class, required=true)},
	output={
	    @HandlerOutput(name="options", type=SelectItem[].class)})
    public static void getRaveOptions(HandlerContext context) throws Exception {
	Collection<String> labels = (Collection) context.getInputValue("labels");
	Collection<String> values = (Collection) context.getInputValue("values");
	if (labels.size() != values.size()) {
	    throw new Exception("getRaveOptions Handler input "
		+ "incorrect: Input 'labels' and 'values' size must be equal. "
		+ "'labels' size: " + labels.size() + " 'values' size: "
		+ values.size());
	}

	//SelectItem[] options = new SelectItem[labels.size()];
	SelectItem[] options =
	    (SelectItem []) Array.newInstance(RAVE_OPTION_CLASS, labels.size());
	String[] labelsArray = (String[])labels.toArray(new String[labels.size()]);
	String[] valuesArray = (String[])values.toArray(new String[values.size()]);
	for (int i =0; i < labels.size(); i++) {
	    SelectItem option = getRaveOption(valuesArray[i], labelsArray[i]);
	    options[i] = option;
	}
	context.setOutputValue("options", options);
    }

    private static SelectItem getRaveOption(String value, String label) {
	try {
	    return (SelectItem) RAVE_OPTION_CONSTRUCTOR.newInstance(value, label);
	} catch (Exception ex) {
	    return null;
	}
    }
    
    /**
     *	<p> This handler returns the Lockhart version of Options of the drop-down
     *	    of the given <code>labels</code> and <code>values</code>.
     *	    <code>labels</code> and <code>values</code> arrays must be equal in
     *	    size and in matching sequence.</p>
     *
     *	<p> Input value: <code>labels</code> -- Type:
     *	    <code>java.util.Collection</code></p>
     *
     *	<p> Input value: <code>values</code> -- Type:
     *	    <code>java.util.Collection</code></p>
     *
     *  <p> Output value: <code>options</code> -- Type:
     *	    <code>SelectItem[] (castable to Option[])</code></p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="getSunOptions",
	input={
	    @HandlerInput(name="labels", type=Collection.class, required=true),
	    @HandlerInput(name="values", type=Collection.class, required=true)},
	output={
	    @HandlerOutput(name="options", type=SelectItem[].class)})
    public static void getSunOptions(HandlerContext context) throws Exception {
	Collection<String> labels = (Collection) context.getInputValue("labels");
	Collection<String> values = (Collection) context.getInputValue("values");
	if (labels.size() != values.size()) {
	    throw new Exception("getSunOptions Handler input "
		+ "incorrect: Input 'labels' and 'values' size must be equal. "
		+ "'labels' size: " + labels.size() + " 'values' size: "
		+ values.size());
	}

	SelectItem[] options =
	    (SelectItem []) Array.newInstance(SUN_OPTION_CLASS, labels.size());
	String[] labelsArray = (String[])labels.toArray(new String[labels.size()]);
	String[] valuesArray = (String[])values.toArray(new String[values.size()]);
	for (int i =0; i < labels.size(); i++) {
	    SelectItem option = getSunOption(valuesArray[i], labelsArray[i]);
	    options[i] = option;
	}
	context.setOutputValue("options", options);
    }

    private static SelectItem getSunOption(String value, String label) {
	try {
	    return (SelectItem) SUN_OPTION_CONSTRUCTOR.newInstance(value, label);
	} catch (Exception ex) {
	    return null;
	}
    }

    private static Class	     RAVE_OPTION_CLASS = null;
    private static Constructor RAVE_OPTION_CONSTRUCTOR = null;
    private static Class	     SUN_OPTION_CLASS = null;
    private static Constructor SUN_OPTION_CONSTRUCTOR = null;

    static {
	try {
	    RAVE_OPTION_CLASS =
	    	Class.forName("com.sun.rave.web.ui.model.Option");
	    RAVE_OPTION_CONSTRUCTOR = RAVE_OPTION_CLASS.
	    	getConstructor(new Class[] {Object.class, String.class});
	} catch (Exception ex) {
	    // Ignore exception here, NPE will be thrown when attempting to
	    // use RAVE_OPTION_CONSTRUCTOR.
	}
	try {
            SUN_OPTION_CLASS =
		Class.forName("com.sun.webui.jsf.model.Option");
	    SUN_OPTION_CONSTRUCTOR = SUN_OPTION_CLASS.
                getConstructor(new Class[] {Object.class, String.class});
	} catch (Exception ex) {
	    // Ignore exception here, NPE will be thrown when attempting to
	    // use SUN_OPTION_CONSTRUCTOR.
	}
    }
}
