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

/*
 * OptionsHandlers.java
 *
 * Created on June 8, 2006, 5:01 PM
 */
package com.sun.jsftemplating.handlers;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.faces.model.SelectItem;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;
import com.sun.jsftemplating.util.Util;


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

    /**
     *	Creates a Woodstock Option instance.
     */
    private static SelectItem getSunOption(String value, String label) {
	try {
	    return (SelectItem) SUN_OPTION_CONSTRUCTOR.newInstance(value, label);
	} catch (InstantiationException ex) {
	    throw new RuntimeException("Unable to instantiate '"
		    + SUN_OPTION_CLASS + "'!", ex);
	} catch (IllegalAccessException ex) {
	    throw new RuntimeException("Unable to instantiate '"
		    + SUN_OPTION_CLASS + "'!", ex);
	} catch (InvocationTargetException ex) {
	    throw new RuntimeException("Unable to instantiate '"
		    + SUN_OPTION_CLASS + "'!", ex);
	}
    }

    /**
     *	<p> Method wich returns the constructor on the class with the given
     *	    arguments.  It will return null if any exceptions occur, no
     *	    exceptions will be thrown from this method.</p>
     */
    private static Constructor noExceptionFindConstructor(Class cls, Class args[]) {
	Constructor constructor = null;
	try {
	    constructor = cls.getConstructor(args);
	} catch (Exception ex) {
	    // Ignore...
	}
	return constructor;
    }

    private static final Class	     SUN_OPTION_CLASS =
	    Util.noExceptionLoadClass("com.sun.webui.jsf.model.Option");
    private static final Constructor SUN_OPTION_CONSTRUCTOR =
	    noExceptionFindConstructor(
		SUN_OPTION_CLASS, new Class[] {Object.class, String.class});
}
