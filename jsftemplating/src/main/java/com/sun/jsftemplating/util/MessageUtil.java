/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006-2018 Oracle and/or its affiliates. All rights reserved.
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
	FacesContext ctx = FacesContext.getCurrentInstance();
	if (locale == null) {
	    locale = Util.getLocale(ctx);
	}

	// Get the ResourceBundle
	ResourceBundle bundle =
	    ResourceBundleManager.getInstance(ctx).getBundle(baseName, locale);
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
