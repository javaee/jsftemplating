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
package com.sun.jsftemplating.layout.template;

import java.net.URL;

import junit.framework.*;


/**
 *  <p>	Tests for the "Template" parser.</p>
 */
public class TemplateParserTest extends TestCase {
    protected void setUp() {
    }

    public void testURL() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
	    assertEquals("URL", "file:test/files/TemplateFormat.jsf", parser.getURL().toString());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    public void testOpenClose() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
	    parser.open();
	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

/*
    public void testInclude() {
    }
*/

    public void testNextChar1() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
	    parser.open();
	    assertEquals("testNextChar1-1", '#', parser.nextChar());
	    assertEquals("testNextChar1-2", ' ', parser.nextChar());
	    assertEquals("testNextChar1-3", 'R', parser.nextChar());
	    assertEquals("testNextChar1-4", 'e', parser.nextChar());
	    assertEquals("testNextChar1-5", 'a', parser.nextChar());
	    assertEquals("testNextChar1-6", 'd', parser.nextChar());
	    assertEquals("testNextChar1-7", 'e', parser.nextChar());
	    assertEquals("testNextChar1-8", 'r', parser.nextChar());
	    assertEquals("testNextChar1-9", ' ', parser.nextChar());
	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    public void testUnread() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
	    parser.open();
	    assertEquals("testNextChar1-1", '#', parser.nextChar());
	    assertEquals("testNextChar1-2", ' ', parser.nextChar());
	    parser.unread(' ');
	    assertEquals("testNextChar1-3", ' ', parser.nextChar());
	    assertEquals("testNextChar1-4", 'R', parser.nextChar());
	    parser.unread('R');
	    assertEquals("testNextChar1-5", 'R', parser.nextChar());
	    parser.unread('X');
	    assertEquals("testNextChar1-6", 'X', parser.nextChar());
	    assertEquals("testNextChar1-7", 'e', parser.nextChar());
	    parser.unread('1');
	    parser.unread('2');
	    parser.unread('3');
	    assertEquals("testNextChar1-8", '3', (char) parser.nextChar());
	    assertEquals("testNextChar1-9", '2', (char) parser.nextChar());
	    assertEquals("testNextChar1-10", '1', (char) parser.nextChar());
	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    public void testNVP() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
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

	    assertEquals("testNVP1", "ile", nvp.getName());
	    assertEquals("testNVP2", "jsftemplating/js/jsftemplating.js", nvp.getValue());
	    assertEquals("testNVP3", null, nvp.getTarget());

	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    public void testNVP2() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
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

	    assertEquals("testNVP1", "value", nvp.getName());
	    assertEquals("testNVP2", "val", nvp.getValue());
	    assertEquals("testNVP3", "attribute", nvp.getTarget());

	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    public void testReadLine() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
	    parser.open();
	    // Read some lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Read some characters
	    parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar();
	    parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar(); parser.nextChar();

	    // Make sure we're at the right spot
	    assertEquals("testReadLine1", ' ', parser.nextChar());
	    assertEquals("testReadLine2", 'm', parser.nextChar());

	    // Make sure we can read the rest of the line
	    assertEquals("testReadLine3", "ulti line comment is hit, skip until end is found", parser.readLine());

	    // Read more lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Make sure we're at the right spot
	    assertEquals("testReadLine4", "\t    <sun:script file=\"jsftemplating/js/jsftemplating.js\" />", parser.readLine());
	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    public void testReadToken() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
	    parser.open();
	    // Read some lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Move to the Script Tag
	    parser.readUntil('<', false);

	    // Test readToken()
	    assertEquals("testReadToken", "sun:script", parser.readToken());

	    // Test skipWhiteSpace();
	    parser.skipWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);

	    // Test NVP
	    NameValuePair nvp = parser.getNVP(null);

	    // NVP should be setup correctly
	    assertEquals("testNVP1", "file", nvp.getName());
	    assertEquals("testNVP2", "jsftemplating/js/jsftemplating.js", nvp.getValue());
	    assertEquals("testNVP3", null, nvp.getTarget());

	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    /**
     *	This test tests the String version of Read Until.
     */
    public void testReadUntilStr() {
	try {
	    TemplateParser parser = new TemplateParser(
		new URL("file:test/files/TemplateFormat.jsf"));
	    parser.open();
	    // Read some lines
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();
	    parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine(); parser.readLine();

	    // Test readUntil.  On line before, read until openning comment.
	    assertEquals("testReadUntilStr", "    // This text should be commented out.  <tags> should not be parsed.\n    <!--", parser.readUntil("<!--", false));
	    assertEquals("testReadUntilStr2", "\n	This text should be commented out.  <tags> should not be parsed.\n    -->", parser.readUntil("-->", false));
	    assertEquals("testReadUntilStr3", "\n    /*\n     *	This text should be commented out.  <tags> should not be parsed.\n     */", parser.readUntil("*/", false));

	    parser.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
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
