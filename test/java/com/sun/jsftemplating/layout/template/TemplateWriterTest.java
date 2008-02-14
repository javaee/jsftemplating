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

import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;

import java.io.ByteArrayOutputStream;
import java.net.URL;

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
		new TemplateReader("foo", new URL("file:test/files/TemplateFormat.jsf"));
	    LayoutDefinition ld = reader.read();
//	    assertEquals("LayoutDefinition.unevaluatedId", "id2", ld.getUnevaluatedId());
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    TemplateWriter writer =
		new TemplateWriter(stream);
	    writer.write(ld);
// FIXME: Add some sort of check here
//	    System.err.println(stream.toString());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }
}
