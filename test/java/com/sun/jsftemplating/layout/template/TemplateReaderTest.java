package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
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
		new TemplateReader("foo", new URL("file:src/java/com/sun/jsftemplating/layout/template/TemplateFormat.txt"));
	    LayoutDefinition ld = reader.read();
	    assertEquals("LayoutDefinition.unevaluatedId", "id1", ld.getUnevaluatedId());
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
		new TemplateReader("bar", new URL("file:exampleapp/index.jsf"));
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
