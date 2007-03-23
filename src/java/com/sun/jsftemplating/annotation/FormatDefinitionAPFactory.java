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
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
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
import java.util.Enumeration;
import java.util.Set;


/**
 *  <p>	This is an <code>AnnotationProcessorFactory</code> for
 *	{@link FormatDefinition} annotation.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class FormatDefinitionAPFactory implements AnnotationProcessorFactory, RoundCompleteListener {

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
		processor = new FormatDefinitionAP(types, env, _writer);
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
	    // Create factory mapping file
	    _writer = getFormatDefinitionMapWriter(env);
	} catch (IOException ex) {
	    StringWriter buf = new StringWriter();
	    ex.printStackTrace(new PrintWriter(buf));
	    env.getMessager().printError("Unable to write "
		+ "'" + FACTORY_FILE + "' file while processing "
		+ "@FormatDefinition annotation: " + buf.toString());
	    return false;
	}

	_setup = true;
	return true;
    }

    /**
     *	<p> This method is responsible for creating (or finding) the
     *	    "FormatDefinition.map" file.</p>
     */
    private PrintWriter getFormatDefinitionMapWriter(AnnotationProcessorEnvironment env) throws IOException {
	PrintWriter writer = null;
	ClassLoader cl = this.getClass().getClassLoader();
	URL url = null;
	for (Enumeration<URL> urls = cl.getResources(FACTORY_FILE);
		urls.hasMoreElements() && (writer == null); ) {
	    url = urls.nextElement();
	    if ((url != null) && new File(url.getFile()).canRead()) {
		// Append to the existing file...
		writer = new PrintWriter(new FileOutputStream(url.getFile(), true));
	    }
	}
	if (writer == null) {
	    // File not found, create a new one...
	    writer = env.getFiler().createTextFile(Filer.Location.CLASS_TREE,
		"", new File(FACTORY_FILE), (String) null);
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
	Arrays.asList("com.sun.jsftemplating.annotation.FormatDefinition");

    private static final Collection<String> _supportedOptions =
	Collections.emptySet();

    /**
     *	<p> This is the file name of the file that is created based on the
     *	    annotations. ("META-INF/jsftemplating/FormatDefinition.map")</p>
     *
     *	<p> The file is a <code>Properties</code> file that contains a list of
     *	    ids and class names corresponding to
     *	    <code>ComponentFactory</code>'s.</p>
     */
    public static final String FACTORY_FILE =
	"META-INF/jsftemplating/FormatDefinition.map";
}
