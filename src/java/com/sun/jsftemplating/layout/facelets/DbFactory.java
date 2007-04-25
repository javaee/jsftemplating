/**
 * 
 */
package com.sun.jsftemplating.layout.facelets;

import com.sun.jsftemplating.layout.xml.XMLErrorHandler;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class provides a convenient way to create a DocumentBuilder using the
 * JSFTemplating entity resolver.
 * @author Jason Lee
 *
 */
public class DbFactory {
    public static DocumentBuilder getInstance() throws ParserConfigurationException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setValidating(true);
	factory.setNamespaceAware(true);
	factory.setValidating(false);
	factory.setIgnoringComments(true);
	factory.setIgnoringElementContentWhitespace(false);
	factory.setCoalescing(false);
	factory.setExpandEntityReferences(true);
	DocumentBuilder builder = factory.newDocumentBuilder();
	try {
	    builder.setErrorHandler(new XMLErrorHandler(new PrintWriter(
		    new OutputStreamWriter(System.err, "UTF-8"), true)));
	} catch (UnsupportedEncodingException ex) {
	    throw new RuntimeException(ex);
	}

	builder.setEntityResolver(new FaceletsClasspathEntityResolver());

	return builder;
    }
}
