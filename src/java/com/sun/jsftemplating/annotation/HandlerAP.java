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
import com.sun.mirror.declaration.MemberDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.AnnotationMirror;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *  <p>	This is an <code>AnnotationProcessor</code> for
 *	{@link Handler} annotations.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class HandlerAP implements AnnotationProcessor {

// FIXME: Add additional checks to make sure Handler methods are valid (i.e.
// FIXME: correct method signature)

    /**
     *	<p> This is the constructor for the {@link Handler}
     *	    <code>AnnotationProcessor</code>.  It expects the annotation to
     *	    have a single value (called 'value', or have 'value' omitted) which
     *	    specifies the identifier used to locate the class containing the
     *	    annotation (the
     *	    {@link com.sun.jsftemplating.component.factory.ComponentFactory}
     *	    class).</p>
     *
     *	@param	types	The <code>AnnotationTypeDeclaration</code>s.
     *	@param	env	The <code>AnnotationProcessorEnvironment</code>.
     *	@param	writer	The <code>PrintWriter</code> used for output.
     */
    public HandlerAP(Set<AnnotationTypeDeclaration> types, AnnotationProcessorEnvironment env, PrintWriter writer) {
	_writer = writer;
	_env = env;
	_types = types;
    }

    /**
     *	<p> This method will process the annotation.  It produces a line using
     *	    the <code>PrintWriter</code> that contains the identifer and the
     *	    class name which holds the annotation:</p>
     *
     *	<p> <code>[identifer]=[class name]</code></p>
     */
    public void process() {
	if (_types == null) {
	    // Nothing to do
	    return;
	}

	// Temporary Variables
	String key;
	Object value;
	int cnt;
	String id;
	List<AnnotationValue> input;
	List<AnnotationValue> output;

	// Loop through the supported annotation types (only 1)
	for (AnnotationTypeDeclaration decl : _types) {
	    // Loop through the declarations that are annotated
	    for (Declaration dec : _env.getDeclarationsAnnotatedWith(decl)) {
		// Loop through the annotations on the current declartion
		for (AnnotationMirror mirror : dec.getAnnotationMirrors()) {
		    // Loop through the NVPs contained in the annotation
		    id = null;
		    input = null;
		    output = null;
		    for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : mirror.getElementValues().entrySet()) {
			// At this point I'm processing a "Handler" annotation
			//   it may contain "id", "input", "output"
			key = entry.getKey().getSimpleName();
			value = entry.getValue().getValue();
			if (key.equals(Handler.ID)) {
			    // Found 'id', save it
			    id = value.toString();
			} else if (key.equals(Handler.INPUT)) {
			    // Found inputs
			    input = (List<AnnotationValue>) value;
			} else if (key.equals(Handler.OUTPUT)) {
			    // Found outputs
			    output = (List<AnnotationValue>) value;
			}
		    }

		    // Make sure we have an id... if so record this Handler
		    if (id != null) {
			// Record class / method names (and javadoc comment)
			_writer.println(formatComment(dec.getDocComment()));
			_writer.println(id + ".class="
			    + ((MemberDeclaration) dec).getDeclaringType().
				getQualifiedName());
			_writer.println(id + ".method=" + dec.getSimpleName());

			// Now record inputs for this handler...
			if (input != null) {
			    writeIOProperties(id, "input", input);
			}

			// Now record outputs for this handler...
			if (output != null) {
			    writeIOProperties(id, "output", output);
			}
		    }
		}
	    }
	}
    }

    /**
     *	<p> This is a helper method that writes out property lines to represent
     *	    either a HandlerInput or a HandlerOutput.  The <code>type</code>
     *	    that is passed in is expected to be either <code>input</code> or
     *	    <code>output</code>.</p>
     */
    private void writeIOProperties(String id, String type, List<AnnotationValue> ioList) {
	int cnt = 0;
	for (AnnotationValue ioVal : ioList) {
	    // Process each @HandlerInput annotation...
	    for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> prop :
		    ((AnnotationMirror) ioVal.getValue()).getElementValues().entrySet()) {
		// Look at each "param": @Handler<I/O>put(param=)
		_writer.println(id + "." + type + "[" + cnt + "]."
		    + prop.getKey().getSimpleName() + "="
		    + convertClassName(prop.getValue().getValue().toString()));
	    }
	    cnt++;
	}
    }

    /**
     *	<p> This method attempts to convert the given <code>clsName</code> to
     *	    a valid class name.  The issue is that arrays appear something like
     *	    "java.lang.String[]" where they should appear
     *	    "[Ljava.lang.String;".</p>
     */
    private String convertClassName(String str) {
	int idx = str.indexOf("[]");
	if (idx == -1) {
	    // For not only worry about Strings that contain array brackets
	    return str;
	}

	// Count []'s
	int count = 0;
	while (idx != -1) {
	    str = str.replaceFirst("\\[]", "");
	    idx = str.indexOf("[]");
	    count++;
	}

	// Generate new String
	String brackets = "";
	for (idx = 0; idx<count; idx++) {
	    brackets += "[";
	}
	// Return something of the format: [Ljava.lang.String;
	return brackets + "L" + str + ";";
    }

    /**
     *	<p> This method strips off HTML tags, converts "&lt;" and "&gt;",
     *	    inserts '#' characters in front of each line, and ensures there
     *	    are no trailing returns.</p>
     */
    private String formatComment(String javadoc) {
	// First trim off extra stuff
	int idx = javadoc.indexOf("@param");
	if (idx > -1) {
	    // Ignore @param stuff
	    javadoc = javadoc.substring(0, idx);
	}
	javadoc = javadoc.trim();

	// Now process the String
	StringBuffer buf = new StringBuffer("\n# ");
	int len = javadoc.length();
	char ch;
	idx = 0;
	while (idx < len) {
	    ch = javadoc.charAt(idx);
	    switch (ch) {
		case '&':
		    if ((idx + 3) < len) {
			if ((javadoc.charAt(idx + 2) == 't') &&
				(javadoc.charAt(idx + 3) == ';')) {
			    if (javadoc.charAt(idx + 1) == 'g') {
				buf.append('>');
				idx += 3;
			    } else if (javadoc.charAt(idx + 1) == 'l') {
				buf.append('<');
				idx += 3;
			    }
			}
		    }
		    break;
		case '<' :
		    idx++;
		    while ((idx < len) && (javadoc.charAt(idx) != '>')) {
			idx++;
		    }
		    break;
		case '>' :
		    idx++;
		    while ((idx < len) && (javadoc.charAt(idx) != '<')) {
			idx++;
		    }
		    break;
		case '\n':
		case '\r':
		    if (((idx + 1) > len)
			&& ((javadoc.charAt(idx + 1) == '\n')
			    || (javadoc.charAt(idx + 1) == '\r'))) {
			idx++;
		    }
		    buf.append("\n# ");
		    break;
		default:
		    buf.append(ch);
	    }
	    idx++;
	}

	// Return the stripped javadoc
	return buf.toString();
    }


    private PrintWriter _writer = null;
    private AnnotationProcessorEnvironment _env = null;
    private Set<AnnotationTypeDeclaration> _types = null;
}
