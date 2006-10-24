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
package com.sun.jsftemplating.annotation;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.apt.RoundCompleteEvent;
import com.sun.mirror.apt.RoundCompleteListener;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;


/**
 *  <p>	This is an <code>AnnotationProcessorFactory</code> for
 *	{@link Handler} annotations.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class HandlerAPFactory implements AnnotationProcessorFactory, RoundCompleteListener {
    /*
<handlerDefinition id="getCFStackMaps" className="com.sun.enterprise.tools.admingui.handlers.CallFlowHandler" methodName="getCallFlowStackMaps">
    <inputDef name="requestId" type="String" required="true" />
    <inputDef name="instanceName" type="String" required="true" />
    <outputDef name="callStackMap" type="java.util.List" />
</handlerDefinition>
    */

    /**
     *
     */
    public Collection<String> supportedAnnotationTypes() {
	return _supportedAnnotationTypes;
    }

    /**
     *
     */
    public Collection<String> supportedOptions() {
	return _supportedOptions;
    }

    /**
     *
     */
    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> types, AnnotationProcessorEnvironment env) {
	AnnotationProcessor processor = AnnotationProcessors.NO_OP;
	if ((types != null) && (types.size() > 0)) {
	    if (setup(env)) {
		// We have stuff to do, and we're setup...
		processor = new HandlerAP(types, env, _writer);
	    }
	}
	return processor;
    }

    /**
     *
     */
    private boolean setup(AnnotationProcessorEnvironment env) {
	if (_setup) {
	    // Don't do setup more than once
	    return true;
	}

	// Register our listener so we can do something at the end
	env.addListener(this);

	try {
	    // Create handler mapping file
	    _writer = getHandlerMapWriter(env);
	} catch (IOException ex) {
	    StringWriter buf = new StringWriter();
	    ex.printStackTrace(new PrintWriter(buf));
	    env.getMessager().printError("Unable to write "
		+ "'" + HANDLER_FILE + "' file while processing "
		+ "@Handler annotation: " + buf.toString());
	    return false;
	}

	_setup = true;
	return true;
    }

    /**
     *	<p> This method is responsible for creating (or finding) the
     *	    "Handler.map" file.</p>
     */
    private PrintWriter getHandlerMapWriter(AnnotationProcessorEnvironment env) throws IOException {
	PrintWriter writer = null;
	ClassLoader cl = this.getClass().getClassLoader();
	URL url = cl.getResource(HANDLER_FILE);
	if (url != null) {
	    // Append to the existing file...
	    writer = new PrintWriter(new FileOutputStream(url.getFile(), true));
	} else {
	    // Create a new file...
	    writer = env.getFiler().createTextFile(Filer.Location.CLASS_TREE,
		"", new File(HANDLER_FILE), (String) null);
	}
	return writer;
    }

    /**
     *
     */
    public void roundComplete(RoundCompleteEvent event) {
	if (event.getRoundState().finalRound()) {
	    // Close file
	    if (_setup) {
		_writer.close();
	    }
	}
    }

    // Flag to indicate setup has occurred
    private boolean _setup = false;

    private PrintWriter _writer = null;

    private static final Collection<String> _supportedAnnotationTypes =
	Arrays.asList(
	    "com.sun.jsftemplating.annotation.Handler",
	    "com.sun.jsftemplating.annotation.HandlerInput",
	    "com.sun.jsftemplating.annotation.HandlerOutput");

    private static final Collection<String> _supportedOptions =
	Collections.emptySet();

    /**
     *	<p> This is the file name of the file that is created based on the
     *	    annotations. ("META-INF/jsftemplating/Handler.map")</p>
     */
    public static final String HANDLER_FILE =
	"META-INF/jsftemplating/Handler.map";
}
