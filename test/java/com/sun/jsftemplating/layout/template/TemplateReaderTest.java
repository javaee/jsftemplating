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

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutComposition;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;

import java.net.URL;
import java.util.List;

import junit.framework.*;


/**
 *  <p>	Tests for the {@link TemplateReader}.</p>
 */
public class TemplateReaderTest extends TestCase {

    /**
     *
     */
    protected void setUp() {
    }

    /**
     *	<p> Simple test to ensure we can read a template.</p>
     */
    public void testRead1() {
	try {
	    TemplateReader reader =
		new TemplateReader("foo", new URL("file:test/files/TemplateFormat.jsf"));
	    LayoutDefinition ld = reader.read();
//	    assertEquals("LayoutDefinition.unevaluatedId", "id1", ld.getUnevaluatedId());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    /**
     *	<p> This test reads <code>test/files/TemplateFormat2.jsf</code>.  This
     *	    file contains a bunch of {@link LayoutComposition} tags to
     *	    ensure they are read correctly.</p>
     */
    public void testReadComposition() {
	try {
	    // Start reading...
	    TemplateReader reader =
		new TemplateReader("foo", new URL("file:test/files/TemplateFormat2.jsf"));
	    LayoutDefinition ld = reader.read();

	    // Find the body LayoutComponent
	    LayoutElement body = ld.findLayoutElement("page").getChildLayoutElement("html").
		    getChildLayoutElement("body");
	    assertEquals("testReadComposition.body",
		"body", body.getUnevaluatedId());

	    // Find each composition component and check the results...
	    LayoutElement child = body.getChildLayoutElement("form");
	    assertEquals("testReadComposition.formId",
		"form", child.getUnevaluatedId());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    public void testDecorate() {
	try {
	    // Start reading...
	    TemplateReader reader =
		new TemplateReader("foo", new URL("file:test/files/TemplateFormat3.jsf"));
	    LayoutDefinition ld = reader.read();

	    // Find the body LayoutComponent
	    LayoutElement body = ld.getChildLayoutElement("page");
	    body = body.getChildLayoutElement("html").getChildLayoutElement("body");
	    assertEquals("testReadComposition.body",
		"body", body.getUnevaluatedId());

	    // Find each composition component and check the results...
	    List<LayoutElement> children = body.getChildLayoutElements();
	    String[] results = {
		    "single1", "single2", "single3",
		    "single4", "single5", "single6",
		    "\"single7\"", "single11", "single12",
		    "single13", null, null, "mult2"
		};
	    int[] resultChildSizes = {
		    0, 0, 0, 0,
		    0, 0, 0, 0, 0,
		    0, 0, 1, 2
		};
	    int idx = 0;
	    for (LayoutElement child : children) {
		assertTrue("testReadComposition.instanceof" + idx,
		    child instanceof LayoutComposition);
		assertEquals("testReadComposition.val" + idx,
		    results[idx], ((LayoutComposition) child).getTemplate());
		assertEquals("testReadComposition.childSize" + idx,
		    resultChildSizes[idx++],
		    child.getChildLayoutElements().size());
	    }
	    assertEquals("testReadComposition.childrenSize",
		13, children.size());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    /**
     *	<p> This tests the accuracy of what was read.</p>
     */
    public void testReadAccuracy() {
	try {
	    TemplateReader reader =
		new TemplateReader("bar", new URL("file:test/files/readTest1.jsf"));
	    LayoutDefinition ld = reader.read();
	    List<LayoutElement> children = ld.getChildLayoutElements();
	    if (children.size() < 5) {
		throw new RuntimeException("Not enough children!");
	    }
	    assertEquals("testReadAccuracy.id.y",
		"y", children.get(2).getUnevaluatedId());
	    assertEquals("testReadAccuracy.value.abcd",
		"abcd", ((LayoutComponent) children.get(2)).getOption("value"));

	    assertEquals("testReadAccuracy.id.hhh",
		"hhh", children.get(3).getUnevaluatedId());
	    assertEquals("testReadAccuracy.text.some tree",
		"some tree",
		((LayoutComponent) children.get(3)).getOption("text"));
	    assertEquals("testReadAccuracy.hhh:treeNode1.id",
		"treeNode1",
		children.get(3).getChildLayoutElements().get(0).getUnevaluatedId());
	    assertEquals("testReadAccuracy.hhh:treeNode1.text",
		"abc",
		((LayoutComponent) children.get(3).getChildLayoutElements().get(0)).getOption("text"));

	    assertEquals("testReadAccuracy.id.theform",
		"theform", children.get(4).getUnevaluatedId());
	    assertEquals("testReadAccuracy.style1",
		"border: 1px solid red;",
		((LayoutComponent) children.get(4)).getOption("style"));

	    for (LayoutElement elt : children.get(4).getChildLayoutElements()) {
		System.out.println(elt.getUnevaluatedId());
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }
}
