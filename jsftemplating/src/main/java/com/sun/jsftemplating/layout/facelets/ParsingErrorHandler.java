/**
 * 
 */
package com.sun.jsftemplating.layout.facelets;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Jason Lee
 *
 */
public class ParsingErrorHandler implements ErrorHandler {
	//Log logger = LogFactory.getLog(this.getClass());

	public ParsingErrorHandler() {
	    super();
	}

	public void warning(SAXParseException arg0) throws SAXException {
//	    logger.warn(arg0.getMessage());
	}

	public void error(SAXParseException arg0) throws SAXException {
	    //logger.error(arg0.getMessage());
	    fatalError(arg0);
	}

	public void fatalError(SAXParseException arg0) throws SAXException {
//	    logger.error(arg0.getMessage());
	    System.err.println (arg0.getMessage());
//	    System.exit(-1);
	}

}