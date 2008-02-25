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

import java.io.PrintWriter;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;


/**
 *  <p>	This is an <code>AnnotationProcessor</code> for
 *	{@link FormatDefinition} annotations.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class FormatDefinitionAP implements AnnotationProcessor {

    /**
     *	<p> This is the constructor for the {@link FormatDefinition}
     *	    <code>AnnotationProcessor</code>.</p>
     *
     *	@param	env	The <code>AnnotationProcessorEnvironment</code>.
     *	@param	writer	The <code>PrintWriter</code> used for output.
     */
    public FormatDefinitionAP(AnnotationProcessorEnvironment env, PrintWriter writer) {
	_writer = writer;
	_env = env;
    }

    /**
     *	<p> This method will process the annotation.  It produces a line using
     *	    the <code>PrintWriter</code> that contains the identifer and the
     *	    class name which holds the annotation:</p>
     *
     *	<p> <code>[identifer]=[class name]</code></p>
     */
    public void process() {
	// Get the supported annotation type (for @FormatDefinition)
	AnnotationTypeDeclaration decl = (AnnotationTypeDeclaration)
		_env.getTypeDeclaration(FormatDefinition.class.getName());

	// Loop through the declarations that are annotated
	for (Declaration dec : _env.getDeclarationsAnnotatedWith(decl)) {
	    // Loop through the annotations on the current declartion
	    for (AnnotationMirror mirror : dec.getAnnotationMirrors()) {
// FIXME: Add a check that ensures it's a LayoutDefinitionManager
		// Write classname using the PrintWriter
		_writer.println(dec.toString());
	    }
	}
    }

    private PrintWriter _writer = null;
    private AnnotationProcessorEnvironment _env = null;
}
