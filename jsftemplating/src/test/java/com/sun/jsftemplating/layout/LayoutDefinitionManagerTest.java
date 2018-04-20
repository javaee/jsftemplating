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

package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.ContextMocker;
import com.sun.jsftemplating.component.factory.basic.StaticTextFactory;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *  <p>	Tests for the {@link LayoutDefinitionManager}.</p>
 */
public class LayoutDefinitionManagerTest {
  @Before
  public void init(){
    ContextMocker.init();
  }
    /**
     *	<p> Test to ensure we can read global {@link ComponentType}s.</p>
     */
  @Test
    public void testReadGlobalComponentTypes() {
	try {
	    ComponentType staticText = LayoutDefinitionManager.getGlobalComponentType(null,"staticText");
	    Assert.assertTrue("staticTextNull", staticText != null);
	    ComponentType event = LayoutDefinitionManager.getGlobalComponentType(null,"event");
	    Assert.assertTrue("eventNull", event != null);
	    Assert.assertTrue("eventNotEqualStaticText", staticText != event);
	    Assert.assertTrue("staticTextType", staticText.getFactory() instanceof StaticTextFactory);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }
    
    public void testReadFaceletsTagLibXml() {
	//assertNotNull(LayoutDefinitionManager.getGlobalComponentTypes().get("http://java.sun.com/jsf/extensions/dynafaces:scripts"));
    }

    /**
     *	<p> Test to ensure we can read global {@link HandlerDefinition}s.</p>
     */
    @Test
    public void testReadGlobalHandlers() {
	try {
	    // Try to get a HandlerDefinition that doesn't exist
	    HandlerDefinition handler = LayoutDefinitionManager.getGlobalHandlerDefinition("nullHandler");
	    Assert.assertTrue("nullHandler", handler == null);

	    // Test the "println" handler
	    handler = LayoutDefinitionManager.getGlobalHandlerDefinition("println");
	    Assert.assertTrue("notNullHandler", handler != null);
	    // Test id
	    Assert.assertEquals("id", "println", handler.getId());
	    // Test 'value' input def.
	    Assert.assertTrue("valueParamNotNull", handler.getInputDef("value") != null);
	    Assert.assertEquals("valueParamName", "value", handler.getInputDef("value").getName());
	    Assert.assertEquals("valueParamType", String.class, handler.getInputDef("value").getType());
	    Assert.assertEquals("valueParamRequired", true, handler.getInputDef("value").isRequired());

	    // Test the "setUIComponentProperty" handler
	    handler = LayoutDefinitionManager.getGlobalHandlerDefinition("setUIComponentProperty");
	    Assert.assertTrue("notNullHandler2", handler != null);
	    // Test 'value' input def.
	    Assert.assertTrue("valueParamNotNull2", handler.getInputDef("value") != null);
	    Assert.assertEquals("valueParamName2", "value", handler.getInputDef("value").getName());
	    Assert.assertEquals("valueParamType2", Object.class, handler.getInputDef("value").getType());
	    Assert.assertEquals("valueParamRequired2", false, handler.getInputDef("value").isRequired());

	    // Test the "getUIComponentChildren" handler
	    handler = LayoutDefinitionManager.getGlobalHandlerDefinition("getUIComponentChildren");
	    Assert.assertTrue("notNullHandler3", handler != null);
	    // Test method name
	    Assert.assertEquals("methodName", "getChildren", handler.getHandlerMethod().getName());
	    // Test 'size' output def
	    Assert.assertTrue("sizeParamNotNull", handler.getOutputDef("size") != null);
	    Assert.assertEquals("sizeParamName", "size", handler.getOutputDef("size").getName());
	    Assert.assertEquals("sizeParamType", Integer.class, handler.getOutputDef("size").getType());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }
}
