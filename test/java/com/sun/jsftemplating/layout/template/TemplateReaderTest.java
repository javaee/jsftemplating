package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;

import java.net.URL;

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
     *	<p> Simple test to ensure we can read a simple template.</p>
     */
    public void testRead1() {
	try {
	    TemplateReader reader =
		new TemplateReader(new URL("file:src/java/com/sun/jsftemplating/layout/template/TemplateFormat.txt"));
	    reader.read();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }

    /**
     *	<p> This tests the accuracy of what was read.</p>
     */
    public void testRead2() {
	try {
	    TemplateReader reader =
		new TemplateReader(new URL("file:src/java/com/sun/jsftemplating/layout/template/TemplateFormat.txt"));
	    LayoutDefinition ld = reader.read();
	    assertEquals("LayoutDefinition.unevaluatedId", "", ld.getUnevaluatedId());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }
}
