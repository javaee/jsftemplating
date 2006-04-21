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
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.AnnotationMirror;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 *  <p>	This is an <code>AnnotationProcessor</code> for
 *	{@link UIComponentFactory} annotations.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class UIComponentFactoryAP implements AnnotationProcessor {
    public UIComponentFactoryAP(Set<AnnotationTypeDeclaration> types, AnnotationProcessorEnvironment env, PrintWriter writer) {
	_writer = writer;
	_env = env;
	_types = types;
    }

    public void process() {
	if (_types == null) {
	    // Nothing to do
	    return;
	}
	for (AnnotationTypeDeclaration decl : _types) {
	    for (Declaration dec : _env.getDeclarationsAnnotatedWith(decl)) {
		for (AnnotationMirror mirror : dec.getAnnotationMirrors()) {
		    for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : mirror.getElementValues().entrySet()) {
			if (entry.getKey().getSimpleName().equals(UIComponentFactory.FACTORY_ID)) {
			    System.out.println(dec.toString() + ">> " + entry.getValue());
			}
		    }
		}
	    }
	}
    }

    private PrintWriter _writer = null;
    private AnnotationProcessorEnvironment _env = null;
    private Set<AnnotationTypeDeclaration> _types = null;
}
