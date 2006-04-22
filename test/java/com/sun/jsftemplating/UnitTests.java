package com.sun.jsftemplating;

import com.sun.jsftemplating.layout.LayoutDefinitionManagerTest;
import com.sun.jsftemplating.layout.template.TemplateParserTest;
import com.sun.jsftemplating.layout.template.TemplateReaderTest;

import java.net.URL;

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
