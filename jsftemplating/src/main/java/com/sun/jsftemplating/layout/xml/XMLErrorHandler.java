/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

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
