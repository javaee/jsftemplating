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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  <p>	This is the <code>HandlerInput Annotation</code>.  It is
 *	expected to exist on each
 *	{@link com.sun.jsftemplating.component.factory.ComponentFactory}.
 *	It must specify an identifier for the <code>ComponentFactory</code>
 *	so that it may be referenced when defining <code>UIComponents</code>
 *	in your template or code.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface HandlerInput {

    public String name();
    public Class type() default Object.class;
    public boolean required() default DEFAULT_REQUIRED;
    // Annotations don't allow Object, use String
    // Annotations don't allow null, use special String
    public String defaultValue() default DEFAULT_DEFAULT_VALUE;

    public static final String  NAME =	    "name";
    public static final String  DEFAULT =   "defaultValue";
    public static final String  TYPE =	    "type";
    public static final String  REQUIRED =  "required";

    public static final boolean DEFAULT_REQUIRED =	false;
    public static final String	DEFAULT_DEFAULT_VALUE =	"jsfTempNULLString";
}
