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
package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.component.factory.basic.StaticTextFactory;import com.sun.jsftemplating.layout.descriptors.ComponentType;import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;import junit.framework.TestCase;


/**
 *  <p>	Tests for the {@link LayoutDefinitionManager}.</p>
 */
public class LayoutDefinitionManagerTest extends TestCase {

    /**
     *
     */
    protected void setUp() {
    }

    /**
     *	<p> Test to ensure we can read global {@link ComponentType}s.</p>
     */
    public void testReadGlobalComponentTypes() {
	try {
	    ComponentType staticText = LayoutDefinitionManager.getGlobalComponentType("staticText");
	    assertTrue("staticTextNull", staticText != null);
	    ComponentType event = LayoutDefinitionManager.getGlobalComponentType("event");
	    assertTrue("eventNull", event != null);
	    assertTrue("eventNotEqualStaticText", staticText != event);
	    assertTrue("staticTextType", staticText.getFactory() instanceof StaticTextFactory);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }        public void testReadFaceletsTagLibXml() {	//assertNotNull(LayoutDefinitionManager.getGlobalComponentTypes().get("http://java.sun.com/jsf/extensions/dynafaces:scripts"));    }

    /**
     *	<p> Test to ensure we can read global {@link HandlerDefinition}s.</p>
     */
    public void testReadGlobalHandlers() {
	try {
	    // Try to get a HandlerDefinition that doesn't exist
	    HandlerDefinition handler = LayoutDefinitionManager.getGlobalHandlerDefinition("nullHandler");
	    assertTrue("nullHandler", handler == null);

	    // Test the "println" handler
	    handler = LayoutDefinitionManager.getGlobalHandlerDefinition("println");
	    assertTrue("notNullHandler", handler != null);
	    // Test id
	    assertEquals("id", "println", handler.getId());
	    // Test 'value' input def.
	    assertTrue("valueParamNotNull", handler.getInputDef("value") != null);
	    assertEquals("valueParamName", "value", handler.getInputDef("value").getName());
	    assertEquals("valueParamType", String.class, handler.getInputDef("value").getType());
	    assertEquals("valueParamRequired", true, handler.getInputDef("value").isRequired());

	    // Test the "setUIComponentProperty" handler
	    handler = LayoutDefinitionManager.getGlobalHandlerDefinition("setUIComponentProperty");
	    assertTrue("notNullHandler2", handler != null);
	    // Test 'value' input def.
	    assertTrue("valueParamNotNull2", handler.getInputDef("value") != null);
	    assertEquals("valueParamName2", "value", handler.getInputDef("value").getName());
	    assertEquals("valueParamType2", Object.class, handler.getInputDef("value").getType());
	    assertEquals("valueParamRequired2", false, handler.getInputDef("value").isRequired());

	    // Test the "getUIComponentChildren" handler
	    handler = LayoutDefinitionManager.getGlobalHandlerDefinition("getUIComponentChildren");
	    assertTrue("notNullHandler3", handler != null);
	    // Test method name
	    assertEquals("methodName", "getChildren", handler.getHandlerMethod().getName());
	    // Test 'size' output def
	    assertTrue("sizeParamNotNull", handler.getOutputDef("size") != null);
	    assertEquals("sizeParamName", "size", handler.getOutputDef("size").getName());
	    assertEquals("sizeParamType", Integer.class, handler.getOutputDef("size").getType());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }
}
