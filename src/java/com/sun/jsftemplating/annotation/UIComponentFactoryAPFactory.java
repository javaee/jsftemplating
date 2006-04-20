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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;


/**
 *  <p>	This is an <code>AnnotationProcessorFactory</code> for
 *	{@link UIComponentFactory} annotations.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class UIComponentFactoryAPFactory implements AnnotationProcessorFactory {

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
	return new UIComponentFactoryAP(types, env);
    }

    private static final Collection<String> _supportedAnnotationTypes =
	Arrays.asList("com.sun.jsftemplating.annotation.UIComponentFactory");

    private static final Collection<String> _supportedOptions =
	Collections.emptySet();
}
