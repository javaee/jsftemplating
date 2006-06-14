/*
 * PageHandlers.java
 *
 * Created on June 9, 2006, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.jsftemplating.samples.editor.handlers;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;

import java.util.*;

/**
 *
 * @author Administrator
 */
public class PageHandlers {
    
    /**
     * Creates a new instance of PageHandlers
     */
    public PageHandlers() {
    }
    
    /**
     *	<p> This handler returns a list of display names and the corresponding 
     *      list of qualified names of all the LayoutComponents in a given page.</p>
     *
     *	<p> Input value: "pageName" -- Type: <code>java.lang.String</code></p>
     *
     *	<p> Output value: "displayNames" -- Type: <code>java.util.List</code></p>
     *
     *  <p> Output value: "qualifiedNames" -- Type: <code>java.util.List</code></p>
     *	@param	context	The HandlerContext.
     */
    @Handler(id="getPageComponentNames",
	input={
	    @HandlerInput(name="pageName", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="displayNames", type=List.class),
            @HandlerOutput(name="qualifiedNames", type=List.class)})
    public static void getPageComponentNames(HandlerContext context) {
	String pageName = (String) context.getInputValue("pageName");
        LayoutDefinition ld = LayoutDefinitionManager.getLayoutDefinition(null, "template.jsf");
        List<String> displayNames = getDisplayList(ld, new ArrayList(), "");
        List<String> qualifiedNames = getQualifiedList(ld, new ArrayList(), "");
	context.setOutputValue("displayNames", displayNames);
        context.setOutputValue("qualifiedNames", qualifiedNames);
    }
    
    private static List<String> getDisplayList(LayoutElement le, List displayList, String indent) {
	List list = le.getChildLayoutElements();
	
	for (int i=0; i < list.size(); i++) {
		LayoutElement lel = (LayoutElement)list.get(i);
		if (lel instanceof LayoutComponent) {
			displayList.add(indent + lel.getUnevaluatedId());
			getDisplayList(lel, displayList, indent + " ");
			
		}	
	}

	return displayList;
    }
    
    private static List<String> getQualifiedList(LayoutElement le, List qualifiedList, String longName) {
	List list = le.getChildLayoutElements();
	
	for (int i=0; i < list.size(); i++) {
		LayoutElement lel = (LayoutElement)list.get(i);
		if (lel instanceof LayoutComponent) {
			qualifiedList.add(longName + lel.getUnevaluatedId());
			getQualifiedList(lel, qualifiedList, longName + lel.getUnevaluatedId() + ":");
			
		}	
	}
        
	return qualifiedList;
    }

    
}
