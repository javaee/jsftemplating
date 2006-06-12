/*
 * DropDownHandlers.java
 *
 * Created on June 8, 2006, 5:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.jsftemplating.samples.editor.handlers;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;

import com.sun.rave.web.ui.model.Option;

import java.util.*;


/**
 *
 * @author Administrator
 */
public class DropDownHandlers {
    
    /**
     * Creates a new instance of DropDownHandlers
     */
    public DropDownHandlers() {
    }
    
    /**
     *	<p> This handler returns the options of the drop-down of the given
     *	    labels and values. labels and values must be equal in size and
     *      in the proper sequence.</p>
     *
     *	<p> Input value: "labels" -- Type: <code>java.util.List</code></p>
     *
     *	<p> Input value: "values" -- Type: <code>java.util.List</code></p>
     *
     *  <p> Output value: "options" -- Type: <code>Option[]</code></p>
     *	@param	context	The HandlerContext.
     */
    @Handler(id="getDropDownOptions",
	input={
	    @HandlerInput(name="labels", type=List.class, required=true),
	    @HandlerInput(name="values", type=List.class, required=true)},
	output={
	    @HandlerOutput(name="options", type=Option[].class)})
    public static void getDropDownOptions(HandlerContext context) throws Exception {
	List<String> labels = (List) context.getInputValue("labels");
        List<String> values = (List) context.getInputValue("values");
        if (labels.size() != values.size()) {
            throw new Exception("getDropDownOptions Handler input incorrect: Input 'labels' and 'values'"
                +  " size must be equal. labels size: " + labels.size() 
                + " values size: " + values.size());
        }
    	Option[] options = new Option[labels.size()];
    	String[] labelsArray = (String[])labels.toArray(new String[labels.size()]);
        String[] valuesArray = (String[])values.toArray(new String[values.size()]);
    	for (int i =0; i < labels.size(); i++) {
		Option option = new Option(valuesArray[i], labelsArray[i]);
		options[i] = option;
        }
	context.setOutputValue("options", options);
    }

    
}
