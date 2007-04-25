/**
 * 
 */
package com.sun.jsftemplating.layout.xml;

import java.io.PrintWriter;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p> This class handles XML parser errors.</p>
 * @author Ken Paulsen
 *
 */
public class XMLErrorHandler implements ErrorHandler {
    /** Error handler output goes here */
    private PrintWriter out;

    public XMLErrorHandler(PrintWriter outWriter) {
	this.out = outWriter;
    }

    /**
     *  <p>	Returns a string describing parse exception details.</p>
     */
    private String getParseExceptionInfo(SAXParseException spe) {
	String systemId = spe.getSystemId();
	if (systemId == null) {
	    systemId = "null";
	}
	String info = "URI=" + systemId + " Line=" + spe.getLineNumber()
	+ ": " + spe.getMessage();
	return info;
    }

    // The following methods are standard SAX ErrorHandler methods.
    // See SAX documentation for more info.

    public void warning(SAXParseException spe) throws SAXException {
	out.println("Warning: " + getParseExceptionInfo(spe));
    }

    public void error(SAXParseException spe) throws SAXException {
	String message = "Error: " + getParseExceptionInfo(spe);
	throw new SAXException(message, spe);
    }

    public void fatalError(SAXParseException spe) throws SAXException {
	String message = "Fatal Error: " + getParseExceptionInfo(spe);
	throw new SAXException(message, spe);
    }
}