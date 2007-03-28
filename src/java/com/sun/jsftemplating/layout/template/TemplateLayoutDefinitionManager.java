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

import com.sun.jsftemplating.annotation.FormatDefinition;
import com.sun.jsftemplating.layout.LayoutDefinitionException;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.util.FileUtil;

import java.io.IOException;
import java.net.URL;

import javax.faces.context.FacesContext;


/**
 *  <p>	This class is a concrete implmentation of the abstract class
 *	{@link LayoutDefinitionManager}.  It obtains {@link LayoutDefinition}
 *	objects by interpreting the <code>key</code> passed to
 *	{@link #getLayoutDefinition(String)} as a path to a template file
 *	describing the {@link LayoutDefinition}.  It will first attempt to
 *	resolve this path from the document root of the ServletContext or
 *	PortletCotnext.  If that fails, it will attempt to use the Classloader
 *	to resolve it.</p>
 *
 *  <p>	This class is a singleton.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@FormatDefinition
public class TemplateLayoutDefinitionManager extends LayoutDefinitionManager {

    /**
     *	<p> Constructor.</p>
     */
    protected TemplateLayoutDefinitionManager() {
	super();
    }

    /**
     *	<p> This method returns an instance of this LayoutDefinitionManager.
     *	    The object returned is a singleton (only 1 instance will be
     *	    created per JVM).</p>
     *
     *	@return	<code>TemplateLayoutDefinitionManager</code> instance
     */
    public static LayoutDefinitionManager getInstance() {
	if (instance == null) {
	    instance = new TemplateLayoutDefinitionManager();
	}
	return instance;
    }

    /**
     *	<p> This method uses the key to determine if this
     *	    {@link LayoutDefinitionManager} is responsible for handling the
     *	    key.</p>
     *
     *	<p> The template format is very flexible which makes it difficult to
     *	    detect this vs. another format.  For this reason, it is suggested
     *	    that this format be attempted last (or at least after more
     *	    detectable formats).</p>
     *
     *	<p> This method checks the first character that is not a comment or
     *	    whitespace (according to the TemplateParser).  If this first
     *	    character is a single quote or double quote it return
     *	    <code>true</code>.  If it is a "&lt;" character, it looks to see
     *	    if it starts with "&lt;?" or "&lt;!DOCTYPE".  If it does start
     *	    that way, it returns <code>false</code>; otherwise it returns
     *	    <code>true</code>.  If any other character is found or an
     *	    exception is thrown, it will return <code>false</code>.</p>
     */
    public boolean accepts(String key) {
	URL url = FileUtil.searchForFile(key, ".jsf");
	if (url == null) {
	    return false;
	}

	// Use the TemplateParser to help us read the file to see if it is a
	// valid XML-format file
	TemplateParser parser = new TemplateParser(url);
	try {
	    parser.open();
	    parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);
	    int ch = parser.nextChar();
	    switch (ch) {
		case '<':
		    ch = parser.nextChar();
		    if (ch == '?') {
			// XML Documents often start with "<?xml...>", '?' is
			// not valid after '<' in this format
			return false;
		    }
		    if (ch == '!') {
			String token = parser.readToken();
			if (token.equalsIgnoreCase("doctype")) {
			    // <!DOCTYPE ... is also indicates an XML syntax
			    // and should be ignored for this format
			    return false;
			}
		    } else if (ch == '%') {
			// "<%@page ..."-type JSP stuff not valid
			return false;
		    }
		    return true;
		case '\"':
		case '\'':
		    return true;
		default:
		    return false;
	    }
	} catch (Exception ex) {
	    // Didn't work...
	    return false;
	} finally {
	    parser.close();
	}
    }

    /**
     *	<p> This method is responsible for finding the requested
     *	    {@link LayoutDefinition} for the given <code>key</code>.</p>
     *
     *	@param	key	Key identifying the desired {@link LayoutDefinition}.
     *
     *	@return		The requested {@link LayoutDefinition}.
     */
    public LayoutDefinition getLayoutDefinition(String key) throws LayoutDefinitionException {
	// Remove leading "/" (we'll add it back if needed)
	while (key.startsWith("/")) {
	    key = key.substring(1);
	}

	// See if we already have this one.
	LayoutDefinition ld = getCachedLayoutDefinition(key);
	if (ld == null) {
	    URL url = FileUtil.searchForFile(key, ".jsf");

	    // Make sure we found the url
	    if (url == null) {
		throw new LayoutDefinitionException(
			"Unable to locate '" + key + "'");
	    }

	    // Read the template file
	    try {
		ld  = new TemplateReader(key, url).read();
	    } catch (IOException ex) {
		throw new LayoutDefinitionException(
		    "Unable to process '" + url.toString() + "'.", ex);
	    }

	    // Cache the LayoutDefinition
	    putCachedLayoutDefinition(key, ld);
	}

	// Dispatch "initPage" handlers
	ld.dispatchInitPageHandlers(FacesContext.getCurrentInstance(), ld);

	// Return the LayoutDefinition
	return ld;
    }


    /**
     *	<p> This is used to ensure that only 1 instance of this class is
     *	    created (per JVM).</p>
     */
    private static LayoutDefinitionManager instance = null;
}
