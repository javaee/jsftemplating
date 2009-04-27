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
package com.sun.jsftemplating.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import com.sun.jsftemplating.resource.ResourceBundleManager;


/**
 *  <p>	This class gets ResourceBundle messages and formats them.</p>
 *
 *  @author Ken Paulsen
 */
public class MessageUtil extends Object {

    /**
     *	<p> This class should not be instantiated directly.</p>
     */
    private MessageUtil() {
    }

    /**
     *	<p> Use this to get an instance of this class.</p>
     */
    public static MessageUtil getInstance() {
	return _instance;
    }

    /**
     *	<p> This method returns a formatted String from the requested
     *	    <code>ResourceBundle</code>.</p>
     *
     *	@param	baseName    The <code>ResourceBundle</code> name.
     *	@param	key	    The  <code>ResourceBundle</code> key.
     */
    public String getMessage(String baseName, String key) {
	return getMessage(baseName, key, null);
    }

    /**
     *	<p> This method returns a formatted String from the requested
     *	    <code>ResourceBundle</code>.</p>
     *
     *	@param	baseName    The <code>ResourceBundle</code> name.
     *	@param	key	    The  <code>ResourceBundle</code> key.
     *	@param	args	    The substitution values (may be null).
     */
    public String getMessage(String baseName, String key, Object args[]) {
	return getMessage(null, baseName, key, args);
    }

    /**
     *	<p> This method returns a formatted String from the requested
     *	    <code>ResourceBundle</code>.</p>
     *
     *	@param	locale	    The desired <code>Locale</code> (may be null).
     *	@param	baseName    The <code>ResourceBundle</code> name.
     *	@param	key	    The  <code>ResourceBundle</code> key.
     *	@param	args	    The substitution values (may be null).
     */
    public String getMessage(Locale locale, String baseName, String key, Object args[]) {
	if (key == null) {
	    return null;
	}
	if (baseName == null) {
	    throw new RuntimeException(
		    "'baseName' is null for key '" + key + "'!");
	}
	if (locale == null) {
	    locale = Util.getLocale(FacesContext.getCurrentInstance());
	}

	// Get the ResourceBundle
	ResourceBundle bundle =
	    ResourceBundleManager.getInstance().getBundle(baseName, locale);
	if (bundle == null) {
	    // FIXME: Log a warning
	    return key;
	}

	String message = null;
	try {
	    message = bundle.getString(key);
	} catch (MissingResourceException ex) {
	    // Key not found!
	    // FIXME: Log a warning
	}
	if (message == null) {
	    // No message found?
	    return key;
	}

	return getFormattedMessage(message, args);
    }

    /**
     * Format message using given arguments.
     *
     * @param message The string used as a pattern for inserting arguments.
     * @param args The arguments to be inserted into the string.
     */
    public static String getFormattedMessage(String message, Object args[]) {
	// Sanity Check
	if ((message == null) || (args == null) || (args.length == 0)) {
	    return message;
	}

	String result = null;

	MessageFormat mf = new MessageFormat(message);
	result = mf.format(args);

	return (result != null) ? result : message;
    }

    /**
     *	<p> Singleton.  This one is OK to share across VMs (no state).</p>
     */
    private static final MessageUtil _instance = new MessageUtil();
}
