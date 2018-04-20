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
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutComposition;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *  <p>	Tests for the {@link TemplateReader}.</p>
 */
public class TemplateReaderTest {

  private final ClassLoader cl = TemplateReaderTest.class.getClassLoader();

  @Before
  public void init(){
    ContextMocker.init();
  }
  
    /**
     *	<p> Simple test to ensure we can read a template.</p>
     */
    @Test
    public void testRead1() {
	try {
	    TemplateReader reader =
		new TemplateReader("foo", cl.getResource("./TemplateFormat.jsf"));
	    LayoutDefinition ld = reader.read();
//	    assertEquals("LayoutDefinition.unevaluatedId", "id1", ld.getUnevaluatedId());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    public void timeTest(String fileName, int iterations) {
        try {
	    java.util.Date start = new java.util.Date();
	    for (int x=0; x<iterations; x++) {
		TemplateReader reader =
		    new TemplateReader("foo", cl.getResource(fileName));
		LayoutDefinition ld = reader.read();
	    }
	    java.util.Date end = new java.util.Date();
System.out.println("Template performance " + fileName + " (" + iterations + "), lower is better: " + (end.getTime() - start.getTime()));
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSpeed() {
	timeTest("./speed-s.jsf", 500);
//	timeTest(new URL("file:"),".speed-m.jsf", 500);
//	timeTest(new URL("file:"),".speed-l.jsf", 500);
//	timeTest(new URL("file:"),".speed-xl.jsf", 500);
    }

    /**
     *	<p> This test reads <code>TemplateFormat2.jsf</code>.  This
     *	    file contains a bunch of {@link LayoutComposition} tags to
     *	    ensure they are read correctly.</p>
     */
    @Test
    public void testReadComposition() {
	try {
	    // Start reading...
	    TemplateReader reader =
		new TemplateReader("foo", cl.getResource("./TemplateFormat2.jsf"));
	    LayoutDefinition ld = reader.read();

	    // Find the body LayoutComponent
	    LayoutElement body = ld.findLayoutElement("page").getChildLayoutElement("html").
		    getChildLayoutElement("body");
	    Assert.assertEquals("testReadComposition.body",
		"body", body.getUnevaluatedId());

	    // Find each composition component and check the results...
	    LayoutElement child = body.getChildLayoutElement("form");
	    Assert.assertEquals("testReadComposition.formId",
		"form", child.getUnevaluatedId());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    @Test
    public void testDecorate() {
	try {
	    // Start reading...
	    TemplateReader reader =
		new TemplateReader("foo", cl.getResource("./TemplateFormat3.jsf"));
	    LayoutDefinition ld = reader.read();

	    // Find the body LayoutComponent
	    LayoutElement body = ld.getChildLayoutElement("page");
	    body = body.getChildLayoutElement("html").getChildLayoutElement("body");
	    Assert.assertEquals("testReadDecorate.body",
		"body", body.getUnevaluatedId());

	    // Find each composition component and check the results...
	    List<LayoutElement> children = body.getChildLayoutElements();
	    String[] results = {
		    "single1", "single2", "single3",
		    "single4", "single5", "Template.jsf",
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
		Assert.assertTrue("testReadDecorate.instanceof" + idx,
		    child instanceof LayoutComposition);
		Assert.assertEquals("testReadDecorate.val" + idx,
		    results[idx], ((LayoutComposition) child).getTemplate());
		Assert.assertEquals("testReadDecorate.childSize" + idx,
		    resultChildSizes[idx++],
		    child.getChildLayoutElements().size());
	    }
	    Assert.assertEquals("testReadDecorate.childrenSize",
		13, children.size());
	    LayoutElement htmlElt = children.get(5).findLayoutElement("html");
	    Assert.assertEquals("testReadDecorate.decorate.page.size",
		2,
		(htmlElt == null) ? 0 : htmlElt.getChildLayoutElements().size());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }

    /**
     *	<p> This tests the accuracy of what was read.</p>
     */
    @Test
    public void testReadAccuracy() {
	try {
	    TemplateReader reader =
		new TemplateReader("bar", cl.getResource("./readTest1.jsf"));
	    LayoutDefinition ld = reader.read();
	    List<LayoutElement> children = ld.getChildLayoutElements();
	    if (children.size() < 5) {
		throw new RuntimeException("Not enough children!");
	    }
	    Assert.assertEquals("testReadAccuracy.id.y",
		"y", children.get(2).getUnevaluatedId());
	    Assert.assertEquals("testReadAccuracy.value.abcd",
		"abcd", ((LayoutComponent) children.get(2)).getOption("value"));

	    Assert.assertEquals("testReadAccuracy.id.hhh",
		"hhh", children.get(3).getUnevaluatedId());
	    Assert.assertEquals("testReadAccuracy.text.some tree",
		"some tree",
		((LayoutComponent) children.get(3)).getOption("text"));
	    Assert.assertEquals("testReadAccuracy.hhh:treeNode1.id",
		"treeNode1",
		children.get(3).getChildLayoutElements().get(0).getUnevaluatedId());
	    Assert.assertEquals("testReadAccuracy.hhh:treeNode1.text",
		"abc",
		((LayoutComponent) children.get(3).getChildLayoutElements().get(0)).getOption("text"));

	    Assert.assertEquals("testReadAccuracy.id.theform",
		"theform", children.get(4).getUnevaluatedId());
	    Assert.assertEquals("testReadAccuracy.style1",
		"border: 1px solid red;",
		((LayoutComponent) children.get(4)).getOption("style"));

	    for (LayoutElement elt : children.get(4).getChildLayoutElements()) {
		System.out.println(elt.getUnevaluatedId());
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }
}
