package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

import junit.framework.*;


/**
 *  <p>	Tests for the {@link TemplateWriter}.</p>
 */
public class TemplateWriterTest extends TestCase {

    /**
     *
     */
    protected void setUp() {
    }

    /**
     *	<p> </p>
     */
    public void testWrite1() {
	try {
	    // First read some data
	    TemplateReader reader =
		new TemplateReader("foo", new URL("file:src/java/com/sun/jsftemplating/layout/template/TemplateFormat.txt"));
	    LayoutDefinition ld = reader.read();
	    assertEquals("LayoutDefinition.unevaluatedId", "id2", ld.getUnevaluatedId());
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    TemplateWriter writer =
		new TemplateWriter(stream);
	    writer.write(ld);
// FIXME: Add some sort of check here
	    System.err.println(stream.toString());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }
}
