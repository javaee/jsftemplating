/**
 * 
 */
package com.sun.jsftemplating.layout.facelets;

import java.io.IOException;
import java.net.URL;

import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.FormatDefinition;
import com.sun.jsftemplating.layout.LayoutDefinitionException;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.template.TemplateParser;
import com.sun.jsftemplating.util.FileUtil;

/**
 * @author Jason Lee
 *
 */
@FormatDefinition
public class FaceletsLayoutDefinitionManager extends LayoutDefinitionManager {
    private static String defaultSuffix;

    public FaceletsLayoutDefinitionManager() {
        super();
//        defaultSuffix = 
//            FacesContext.getCurrentInstance().getExternalContext().getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
        if (defaultSuffix == null) {
            defaultSuffix = ".xhtml";
        }
    }

    @Override
    public boolean accepts(String key) {
        boolean accept = false;
        URL url = FileUtil.searchForFile(key, defaultSuffix);
        if (url == null) {
            return false;
        }

        // Eventually, we may want this check to be configurable via a
	// context-param...
	if (url.getPath().contains(".xhtml")) {
	    accept = true;
        } else {
            // Use the TemplateParser to help us read the file to see if it is a
            // valid XML-format file
            TemplateParser parser = new TemplateParser(url);
            try {
                parser.open();
                parser.readUntil("=\"http://java.sun.com/jsf/facelets\"", true);
            } catch (Exception ex) {
                // Didn't work...
            } finally {
                parser.close();
            }
            accept = true;
        }

        return accept;
    }

    /**
     *  <p> This method is responsible for finding the requested
     *      {@link LayoutDefinition} for the given <code>key</code>.</p>
     *
     *  @param  key     Key identifying the desired {@link LayoutDefinition}.
     *
     *  @return         The requested {@link LayoutDefinition}.
     */
    public LayoutDefinition getLayoutDefinition(String key) throws LayoutDefinitionException {
	// Make sure we found the url
	URL url = FileUtil.searchForFile(key, defaultSuffix);
	if (url == null) {
	    throw new LayoutDefinitionException(
		    "Unable to locate '" + key + "'");
	}

	// Read the template file
	LayoutDefinition ld = null;
	try {
	    ld  = new FaceletsLayoutDefinitionReader(key, url).read();
	} catch (IOException ex) {
	    throw new LayoutDefinitionException(
		"Unable to process '" + url.toString() + "'.", ex);
	}

        // Dispatch "initPage" handlers
        ld.dispatchInitPageHandlers(FacesContext.getCurrentInstance(), ld);

        // Return the LayoutDefinition
        return ld;
    }

    /**
     *  <p> This method returns an instance of this LayoutDefinitionManager.
     *      The object returned is a singleton (only 1 instance will be
     *      created per JVM).</p>
     *
     *  @return <code>XMLLayoutDefinitionManager</code> instance
     */
    public static LayoutDefinitionManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     *  <p> This is used to ensure that only 1 instance of this class is
     *      created (per JVM).</p>
     */
    static class SingletonHolder {
        static FaceletsLayoutDefinitionManager instance = new FaceletsLayoutDefinitionManager();
    }
}
