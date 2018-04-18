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

package com.sun.jsftemplating.layout.facelets;

import com.sun.jsftemplating.ContextMocker;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * <p>
 * Tests for the {@link FaceletsLayoutDefinitionReader}.
 * </p>
 * 
 */

public class FaceletsLayoutDefinitionReaderTest {

  private final ClassLoader cl = FaceletsLayoutDefinitionReaderTest.class.getClassLoader();

  @Before
  public void init(){
    ContextMocker.init();
  }
  
    /**
     * 
     * <p>
     * Simple test to ensure we can read a facelets file.
     * </p>
     * 
     */
    @Test
    public void testRead1() {
        try {
            FaceletsLayoutDefinitionReader reader =
                new FaceletsLayoutDefinitionReader("foo", cl.getResource("./simple.xhtml"));
            LayoutDefinition ld = reader.read();
            Assert.assertEquals("LayoutDefinition.unevaluatedId", "foo", ld
                    .getUnevaluatedId());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    public void timeTest(String fileName, int iterations) {
        try {
	    java.util.Date start = new java.util.Date();
	    for (int x=0; x<iterations; x++) {
		FaceletsLayoutDefinitionReader reader =
		    new FaceletsLayoutDefinitionReader("foo", cl.getResource(fileName));
		LayoutDefinition ld = reader.read();
	    }
	    java.util.Date end = new java.util.Date();
System.out.println("Faclets performance " + fileName + " (" + iterations + "), lower is better: " + (end.getTime() - start.getTime()));
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSpeed() {
	timeTest("./speed-s.xhtml", 500);
//	timeTest(new URL("file:"),".speed-m.xhtml", 500);
//	timeTest(new URL("file:"),".speed-l.xhtml", 500);
//	timeTest(new URL("file:"),".speed-xl.xhtml", 500);
    }
    
    /**
     * 
     * <p>
     * This tests the accuracy of what was read.
     * </p>
     * 
     * public void testReadAccuracy() {
     * 
     * try {
     * 
     * FaceletsLayoutDefinitionReader reader =
     * 
     * new FaceletsLayoutDefinitionReader("bar", new
     * URL(new URL("file:"),".readTest1.jsf"));
     * 
     * LayoutDefinition ld = reader.read();
     * 
     * List<LayoutElement> children = ld.getChildLayoutElements();
     * 
     * if (children.size() < 5) {
     * 
     * throw new RuntimeException("Not enough children!");
     *  }
     * 
     * assertEquals("testReadAccuracy.id.y",
     * 
     * "y", children.get(2).getUnevaluatedId());
     * 
     * assertEquals("testReadAccuracy.value.abcd",
     * 
     * "abcd", ((LayoutComponent) children.get(2)).getOption("value"));
     * 
     * 
     * 
     * assertEquals("testReadAccuracy.id.hhh",
     * 
     * "hhh", children.get(3).getUnevaluatedId());
     * 
     * assertEquals("testReadAccuracy.text.some tree",
     * 
     * "some tree",
     * 
     * ((LayoutComponent) children.get(3)).getOption("text"));
     * 
     * assertEquals("testReadAccuracy.hhh:treeNode1.id",
     * 
     * "treeNode1",
     * 
     * children.get(3).getChildLayoutElements().get(0).getUnevaluatedId());
     * 
     * assertEquals("testReadAccuracy.hhh:treeNode1.text",
     * 
     * "abc",
     * 
     * ((LayoutComponent)
     * children.get(3).getChildLayoutElements().get(0)).getOption("text"));
     * 
     * 
     * 
     * assertEquals("testReadAccuracy.id.theform",
     * 
     * "theform", children.get(4).getUnevaluatedId());
     * 
     * assertEquals("testReadAccuracy.style1",
     * 
     * "border: 1px solid red;",
     * 
     * ((LayoutComponent) children.get(4)).getOption("style"));
     * 
     * 
     * 
     * for (LayoutElement elt : children.get(4).getChildLayoutElements()) {
     * 
     * System.out.println(elt.getUnevaluatedId());
     *  }
     *  } catch (Exception ex) {
     * 
     * ex.printStackTrace();
     * 
     * fail(ex.getMessage());
     *  }
     *  }
     * 
     */
}
