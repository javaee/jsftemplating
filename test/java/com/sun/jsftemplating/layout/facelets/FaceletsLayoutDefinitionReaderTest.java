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
package com.sun.jsftemplating.layout.facelets;
import java.net.URL;
import junit.framework.TestCase;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
/**
 * 
 * <p>
 * Tests for the {@link FaceletsLayoutDefinitionReader}.
 * </p>
 * 
 */
public class FaceletsLayoutDefinitionReaderTest extends TestCase {
    /**
     * 
     * 
     * 
     */
    protected void setUp() {
    }
    /**
     * 
     * <p>
     * Simple test to ensure we can read a facelets file.
     * </p>
     * 
     */
    public void testRead1() {
        try {
            FaceletsLayoutDefinitionReader reader =
                new FaceletsLayoutDefinitionReader("foo", new URL(
                "file:test/files/simple.xhtml"));
            LayoutDefinition ld = reader.read();
            assertEquals("LayoutDefinition.unevaluatedId", "foo", ld
                    .getUnevaluatedId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
    
    /**
     * Test a more complex file
     *
     */
    public void testNestedRead() {
        try {
            FaceletsLayoutDefinitionReader reader =
                new FaceletsLayoutDefinitionReader("foo", new URL(
                "file:test/files/facelets.xhtml"));
            LayoutDefinition ld = reader.read();
            assertEquals("LayoutDefinition.unevaluatedId", "foo", ld
                    .getUnevaluatedId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
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
     * URL("file:test/files/readTest1.jsf"));
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