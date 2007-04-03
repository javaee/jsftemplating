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
package com.sun.jsftemplating;

import com.sun.jsftemplating.layout.LayoutDefinitionManagerTest;
import com.sun.jsftemplating.layout.facelets.FaceletsLayoutDefinitionReaderTest;
import com.sun.jsftemplating.layout.template.TemplateParserTest;
import com.sun.jsftemplating.layout.template.TemplateReaderTest;
import com.sun.jsftemplating.layout.template.TemplateWriterTest;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 *  <p>	Unit tests.</p>
 */
public class UnitTests {

    /**
     *
     */
    public static Test suite() {
	// Dynamic way
	TestSuite tests = new TestSuite();
	tests.addTestSuite(TemplateParserTest.class);
	tests.addTestSuite(TemplateReaderTest.class);
	tests.addTestSuite(TemplateWriterTest.class);
	tests.addTestSuite(FaceletsLayoutDefinitionReaderTest.class);
	tests.addTestSuite(LayoutDefinitionManagerTest.class);
	return tests;
    }

    /**
     *
     */
    public static void main (String[] args) {
	TestRunner.run(suite());
    }
}
