/**
 * 
 */
package com.sun.jsftemplating.layout.facelets;

import com.sun.jsftemplating.util.ClasspathEntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Jason Lee
 *
 */
public class FaceletsClasspathEntityResolver extends ClasspathEntityResolver {
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) {
	String grammarName = systemId.substring(systemId.lastIndexOf('/') + 1);
	return super.resolveEntity(name, publicId, baseURI, grammarName);
    }
}
