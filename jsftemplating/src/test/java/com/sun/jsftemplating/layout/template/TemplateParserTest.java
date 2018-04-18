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

import java.net.URL;
import org.junit.Assert;
import org.junit.Test;


/**
 *  <p>	Tests for the "Template" parser.</p>
 */
public class TemplateParserTest {

  private final ClassLoader cl = TemplateParserTest.class.getClassLoader();
  
    @Test
  public void testURL() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("./TemplateFormat.jsf"));
	    Assert.assertEquals(cl.getResource("./TemplateFormat.jsf").toString(), parser.getURL().toString());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testOpenClose() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("./TemplateFormat.jsf"));
	    parser.open();
	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testNextChar1() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("./TemplateFormat.jsf"));
	    parser.open();
	    Assert.assertEquals("testNextChar1-1", '#', parser.nextChar());
	    Assert.assertEquals("testNextChar1-2", ' ', parser.nextChar());
	    Assert.assertEquals("testNextChar1-3", 'R', parser.nextChar());
	    Assert.assertEquals("testNextChar1-4", 'e', parser.nextChar());
	    Assert.assertEquals("testNextChar1-5", 'a', parser.nextChar());
	    Assert.assertEquals("testNextChar1-6", 'd', parser.nextChar());
	    Assert.assertEquals("testNextChar1-7", 'e', parser.nextChar());
	    Assert.assertEquals("testNextChar1-8", 'r', parser.nextChar());
	    Assert.assertEquals("testNextChar1-9", ' ', parser.nextChar());
	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testUnread() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("./TemplateFormat.jsf"));
	    parser.open();
	    Assert.assertEquals("testNextChar1-1", '#', parser.nextChar());
	    Assert.assertEquals("testNextChar1-2", ' ', parser.nextChar());
	    parser.unread(' ');
	    Assert.assertEquals("testNextChar1-3", ' ', parser.nextChar());
	    Assert.assertEquals("testNextChar1-4", 'R', parser.nextChar());
	    parser.unread('R');
	    Assert.assertEquals("testNextChar1-5", 'R', parser.nextChar());
	    parser.unread('X');
	    Assert.assertEquals("testNextChar1-6", 'X', parser.nextChar());
	    Assert.assertEquals("testNextChar1-7", 'e', parser.nextChar());
	    parser.unread('1');
	    parser.unread('2');
	    parser.unread('3');
	    Assert.assertEquals("testNextChar1-8", '3', (char) parser.nextChar());
	    Assert.assertEquals("testNextChar1-9", '2', (char) parser.nextChar());
	    Assert.assertEquals("testNextChar1-10", '1', (char) parser.nextChar());
	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testNVP() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("./TemplateFormat.jsf"));
	    parser.open();
	    // Read some lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Move in to the NVP (actually 1 past just to see that that works)
	    parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar();
	    parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar();
	    parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar();
	    parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar();

	    NameValuePair nvp = parser.getNVP(null);

	    Assert.assertEquals("testNVP1", "ile", nvp.getName());
	    Assert.assertEquals("testNVP2", "jsftemplating/js/jsftemplating.js", nvp.getValue());
	    Assert.assertEquals("testNVP3", null, nvp.getTarget());

	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testNVP2() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("TemplateFormat.jsf"));
	    parser.open();

	    // Read 49 lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Move to the output mapping on line 50
	    parser.readUntil('v', false);
	    parser.unread('v');

	    NameValuePair nvp = parser.getNVP(null);

	    Assert.assertEquals("testNVP1", "value", nvp.getName());
	    Assert.assertEquals("testNVP2", "val", nvp.getValue());
	    Assert.assertEquals("testNVP3", "attribute", nvp.getTarget());

	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testReadLine() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("TemplateFormat.jsf"));
	    parser.open();
	    // Read some lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Read some characters
	    parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar();
	    parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar();

	    // Make sure we're at the right spot
	    Assert.assertEquals("testReadLine1", ' ', parser.nextChar());
	    Assert.assertEquals("testReadLine2", 'm', parser.nextChar());

	    // Make sure we can read the rest of the line
	    Assert.assertEquals("testReadLine3", "ulti line comment is hit, skip until end is found", parser.readLine());

	    // Read more lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Make sure we're at the right spot
	    Assert.assertEquals("testReadLine4", "\t    <sun:script file=\"jsftemplating/js/jsftemplating.js\" />", parser.readLine());
	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testReadToken() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("TemplateFormat.jsf"));
	    parser.open();
	    // Read some lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Move to the Script Tag
	    parser.readUntil('<', false);

	    // Test readToken()
	    Assert.assertEquals("testReadToken", "sun:script", parser.readToken());

	    // Test skipWhiteSpace();
	    parser.skipWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);

	    // Test NVP
	    NameValuePair nvp = parser.getNVP(null);

	    // NVP should be setup correctly
	    Assert.assertEquals("testNVP1", "file", nvp.getName());
	    Assert.assertEquals("testNVP2", "jsftemplating/js/jsftemplating.js", nvp.getValue());
	    Assert.assertEquals("testNVP3", null, nvp.getTarget());

	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    /**
     *	This test tests the String version of Read Until.
     */
    @Test
    public void testReadUntilStr() {
	try {
	    TemplateParser parser = new TemplateParser(cl.getResource("./TemplateFormat.jsf"));
	    parser.open();
	    // Read some lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Test readUntil.  On line before, read until openning comment.
	    Assert.assertEquals("testReadUntilStr", "    // This text should be commented out.  <tags> should not be parsed.\n    <!--", parser.readUntil("<!--", false));
	    Assert.assertEquals("testReadUntilStr2", "\n	This text should be commented out.  <tags> should not be parsed.\n    -->", parser.readUntil("-->", false));
	    Assert.assertEquals("testReadUntilStr3", "\n    /*\n     *	This text should be commented out.  <tags> should not be parsed.\n     */", parser.readUntil("*/", false));

	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

/*
    public void testAdd() {
	assertTrue(5 == 6);
    }

    public void testEquals() {
	assertEquals(12, 12);
	assertEquals(12L, 12L);
	assertEquals(new Long(12), new Long(12));

	assertEquals("Size", 12, 13);
	assertEquals("Capacity", 12.0, 11.99, 0.0);
    }

    public static void main (String[] args) {
	    junit.textui.TestRunner.run(suite());
    }
*/
}
