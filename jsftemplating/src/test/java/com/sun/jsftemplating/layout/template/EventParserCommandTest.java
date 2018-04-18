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

package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.ContextMocker;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *  <p>	Tests for the {@link EventParserCommand}.</p>
 */
public class EventParserCommandTest {
  @Before
  public void init(){
    ContextMocker.init();
  }
    /**
     *	<p> Simple test to ensure we can read a template.</p>
     */
    @Test
    public void testHandlerParsing() {
	try {
	    // Create element to contain handlers...
	    LayoutDefinition elt = new LayoutDefinition("top");

	    // Setup the parser...
	    TemplateParser parser = new TemplateParser(new ByteArrayInputStream(HANDLERS1));
	    parser.open();  // Needed to initialize things.

	    // Setup the reader...
	    TemplateReader reader = new TemplateReader("foo", parser);
	    reader.pushTag("event"); // The tag will be popped at the end

	    // Read the handlers...
	    command.process(bpc, new ProcessingContextEnvironment(reader, elt, true), "beforeEncode");

	    // Clean up
	    parser.close();

	    // Test to see if things worked...
	    Assert.assertEquals("component.id", "top", elt.getUnevaluatedId());
	    List<Handler> handlers = elt.getHandlers("beforeEncode", null);
	    Assert.assertEquals("handler.size", 3, handlers.size());
	    Assert.assertEquals("handler.name1", "println", handlers.get(0).getHandlerDefinition().getId());
	    Assert.assertEquals("handler.name2", "setAttribute", handlers.get(1).getHandlerDefinition().getId());
	    Assert.assertEquals("handler.name3", "printStackTrace", handlers.get(2).getHandlerDefinition().getId());
	    Assert.assertEquals("handler.valueInput1", "This is a test!", handlers.get(0).getInputValue("value"));
	    Assert.assertEquals("handler.keyInput2", "foo", handlers.get(1).getInputValue("key"));
	    Assert.assertEquals("handler.valueInput2", "bar", handlers.get(1).getInputValue("value"));
	    Assert.assertEquals("handler.msgInput3", "test", handlers.get(2).getInputValue("msg"));
	    Assert.assertEquals("handler.stOutput3.name", "stackTrace", handlers.get(2).getOutputValue("stackTrace").getOutputName());
	    Assert.assertEquals("handler.stOutput3.type",
		"com.sun.jsftemplating.layout.descriptors.handler.RequestAttributeOutputType",
		handlers.get(2).getOutputValue("stackTrace").getOutputType().getClass().getName());
	    Assert.assertEquals("handler.stOutput3.mappedName", "st", handlers.get(2).getOutputValue("stackTrace").getOutputKey());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testHandlerParsing2() {
	try {
	    // Create element to contain handlers...
	    LayoutDefinition elt = new LayoutDefinition("newTop");

	    // Setup the parser...
	    TemplateParser parser = new TemplateParser(new ByteArrayInputStream(HANDLERS2));
	    parser.open();  // Needed to initialize things.

	    // Setup the reader...
	    TemplateReader reader = new TemplateReader("foo", parser);
	    reader.pushTag("event"); // The tag will be popped at the end

	    // Read the handlers...
	    command.process(bpc, new ProcessingContextEnvironment(reader, elt, true), "beforeEncode");

	    // Clean up
	    parser.close();

	    // Test to see if things worked...
	    Assert.assertEquals("component.id", "newTop", elt.getUnevaluatedId());

	    List<Handler> handlers = elt.getHandlers("beforeEncode", null);
	    Assert.assertEquals("handler.size", 1, handlers.size());
	    Assert.assertEquals("handler.valueInput1", "test", handlers.get(0).getInputValue("value"));
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    private static final byte[] HANDLERS1   = (
	"\nprintln(\"This is a test!\");\n"
	+ "setAttribute(key='foo' value='bar');"
	+ "printStackTrace('test', stackTrace=>$attribute{st});/>").getBytes();

    private static final byte[] HANDLERS2   = "println('test');/>".getBytes();

    /**
     *	<p> This class should be re-usable and thread-safe.</p>
     */
    EventParserCommand command = new EventParserCommand();
    BaseProcessingContext bpc = new BaseProcessingContext(); // Not used
}
