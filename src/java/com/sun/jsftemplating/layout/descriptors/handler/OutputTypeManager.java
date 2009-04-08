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
package com.sun.jsftemplating.layout.descriptors.handler;

import com.sun.jsftemplating.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;


/**
 *  <p>	<code>OutputTypeManager</code> manages the various {@link OutputType}s
 *	that can be used.  The {@link OutputType}s are managed statically.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class OutputTypeManager {

    /**
     *	Constructor.
     */
    protected OutputTypeManager() {
    }

    /**
     *
     */
    public static OutputTypeManager getInstance() {
	return _defaultInstance;
    }

    /**
     *	<p> This is a factory method for obtaining an OutputTypeManager
     *	    instance. This implementation uses the external context's
     *	    initParams to look for the OutputTypeManager class.  If it
     *	    exists, the specified concrete OutputTypeManager class will
     *	    be used.  Otherwise, the default will be used -- which is an
     *	    instance of this class.  The initParam key is:
     *	    {@link #OUTPUT_TYPE_MANAGER_KEY}.</p>
     *
     *	@param	context	    The FacesContext
     *
     *	@see #OUTPUT_TYPE_MANAGER_KEY
     */
    public static OutputTypeManager getManager(FacesContext context) {
	if (context == null) {
	    return _defaultInstance;
	}

	// If the context is non-null, check for init parameter specifying
	// the Manager
	String className = null;
	Map initParams = context.getExternalContext().getInitParameterMap();
	if (initParams.containsKey(OUTPUT_TYPE_MANAGER_KEY)) {
	    className = (String) initParams.get(OUTPUT_TYPE_MANAGER_KEY);
	}
	return getManager(className);
    }


    /**
     *	This method is a singleton factory method for obtaining an instance of
     *	a OutputTypeManager.  It is possible that multiple different
     *	implementations of OutputTypeManagers will be used within the
     *	same JVM.  This is OK, the purpose of the OutputTypeManager is
     *	primarily performance.  Someone may provide a different
     *	OutputTypeManager to locate OutputTypeManager's in a different way
     *	(XML, database, file, java code, etc.).
     */
    public static OutputTypeManager getManager(String className) {
	if (className == null) {
	    // Default case...
	    return _defaultInstance;
	}

	OutputTypeManager ldm = _instances.get(className);
	if (ldm == null) {
	    try {
		ldm = (OutputTypeManager) Util.loadClass(className, className).
		    getMethod("getInstance", (Class []) null).
		    invoke((Object) null, (Object []) null);
	    } catch (ClassNotFoundException ex) {
		throw new RuntimeException(ex);
	    } catch (NoSuchMethodException ex) {
		throw new RuntimeException(ex);
	    } catch (IllegalAccessException ex) {
		throw new RuntimeException(ex);
	    } catch (InvocationTargetException ex) {
		throw new RuntimeException(ex);
	    } catch (NullPointerException ex) {
		throw new RuntimeException(ex);
	    } catch (ClassCastException ex) {
		throw new RuntimeException(ex);
	    }
	    _instances.put(className, ldm);
	}
	return ldm;
    }

    /**
     *	<p> This method retrieves a <code>List</code> of {@link OutputType}.
     *	    Changes to this <code>List</code> have no effect.</p>
     *
     *	@return	The {@link OutputType}s.
     */
    public List<OutputType> getOutputTypes() {
	return new ArrayList<OutputType>(_outputTypes.values());
    }

    /**
     *	<p> This method retrieves an OutputType.</p>
     *
     *	@param	name	The name of the OutputType.
     *
     *	@return	The requested OutputType.
     */
    public OutputType getOutputType(String name) {
	return _outputTypes.get(name);
    }

    /**
     *	<p> This method sets an OutputType.</p>
     *
     *	@param	name	    The name of the OutputType.
     *	@param	outputType  The OutputType.
     */
    public void setOutputType(String name, OutputType outputType) {
	_outputTypes.put(name, outputType);
    }

    /**
     *	<p> Cache different subclasses. </p>
     */
    private static Map<String, OutputType> _outputTypes = new HashMap<String, OutputType>(8);

    /**
     *	<p> Cache different subclasses. </p>
     */
    private static Map<String, OutputTypeManager>_instances = new HashMap<String, OutputTypeManager>(2);

    /**
     *	<p> This is the default implementation of the OutputTypeManager, which
     *	    happens to be an instance of this class (because I'm too lazy to
     *	    do this right).</p>
     */
    private static OutputTypeManager _defaultInstance =
	new OutputTypeManager();


    /**
     *	<p> This constant defines the layout definition manager implementation
     *	    key for initParams. The value for this initParam should be the
     *	    full class name of an {@link OutputTypeManager}.
     *	    ("outputTypeManagerImpl")</p>
     */
    public static final String OUTPUT_TYPE_MANAGER_KEY =
	"outputTypeManagerImpl";

    public static final String  REQUEST_ATTRIBUTE_TYPE	=   "attribute";
    public static final String  PAGE_ATTRIBUTE_TYPE	=   "page";
    public static final String  PAGE_ATTRIBUTE_TYPE2	=   "pageSession";
    public static final String  SESSION_ATTRIBUTE_TYPE	=   "session";
    public static final String	APP_ATTRIBUTE_TYPE	=   "application";
    public static final String	EL_TYPE			=   "el";

    static {
	_outputTypes.put(REQUEST_ATTRIBUTE_TYPE,
		new RequestAttributeOutputType());
	PageAttributeOutputType pageType = new PageAttributeOutputType();
	_outputTypes.put(PAGE_ATTRIBUTE_TYPE, pageType);
	_outputTypes.put(PAGE_ATTRIBUTE_TYPE2, pageType);
	_outputTypes.put(SESSION_ATTRIBUTE_TYPE,
		new SessionAttributeOutputType());
	_outputTypes.put(APP_ATTRIBUTE_TYPE,
		new ApplicationAttributeOutputType());
	_outputTypes.put(EL_TYPE,
		new ELOutputType());
    }
}
