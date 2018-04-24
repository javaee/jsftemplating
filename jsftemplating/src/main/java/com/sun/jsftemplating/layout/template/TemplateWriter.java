/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jsftemplating.layout.template;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutStaticText;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;
import com.sun.jsftemplating.layout.descriptors.handler.OutputMapping;


/**
 *  <p>	This class is responsible for writing templates.  It processes a
 *	{@link LayoutElement} tree with a {@link LayoutDefinition} object at
 *	the root of the tree and writes it to a file.</p>
 *
 *  <p>	This class is intended to "write" one template, however, it does not
 *	close the given <code>OutputStream</code> or prevent the
 *	{@link #write(LayoutDefinition)} method from being called multiple
 *	times.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class TemplateWriter {

    /**
     *	<p> Constructor.</p>
     *
     *	@param	stream	<code>OutputStream</code> to the
     *			{@link LayoutDefinition} file.
     */
    public TemplateWriter(OutputStream stream) {
	_writer = new PrintWriter(stream);
    }

    /**
     *	<p> This method should be invoked to write the given
     *	    {@link LayoutDefinition} to the <code>OutputStream</code>.  This
     *	    is the primary method of this Class.</p>
     *
     *	@param	def	The {@link LayoutDefinition}.
     *
     *	@throws	IOException
     */
    public void write(LayoutDefinition def) throws IOException {
	// Write out the LayoutDefinition (nothing to do except body for LDs)
	writeBody("", def);

	// Flush @ the end...
	_writer.flush();
    }

    /**
     *	<p> This method writes {@link Handler}s and child
     *	    {@link LayoutElement}s.</p>
     */
    protected void writeBody(String prefix, LayoutElement elt) throws IOException {
	writeHandlers(prefix, elt.getHandlersByTypeMap());
	for (LayoutElement child : elt.getChildLayoutElements()) {
	    if (child instanceof LayoutStaticText) {
		writeLayoutStaticText(prefix, (LayoutStaticText) child);
	    } else if (child instanceof LayoutComponent) {
		writeLayoutComponent(prefix, (LayoutComponent) child);
	    }
	}
    }

    /**
     *	<p> This method simply writes the given <code>str</code> to the
     *	    <code>OutputStream</code>.</p>
     */
    protected void write(String str) throws IOException {
	_writer.print(str);
    }

    /**
     *	<p> This method is responsible for producing the output for
     *	    {@link Handler}s.</p>
     */
    protected void writeHandlers(String prefix, Map<String, List<Handler>> handlerMap) throws IOException {
	for (String eventType : handlerMap.keySet()) {
	    write(prefix + "<!" + eventType + "\n");
	    for (Handler handler : handlerMap.get(eventType)) {
		writeHandler(INDENT + prefix, handler);
	    }
	    write(prefix + "/>\n");
	}
    }

    /**
     *	<p> This method writes output for the given {@link Handler}.</p>
     */
    protected void writeHandler(String prefix, Handler handler) throws IOException {
	// Start the handler...
	HandlerDefinition def = handler.getHandlerDefinition();
	write(prefix + def.getId() + "(");

	// Add inputs
	String seperator = "";
	for (String inputKey : def.getInputDefs().keySet()) {
	    write(seperator + inputKey + "=\""
		+ handler.getInputValue(inputKey) + "\"");
	    seperator = ", ";
	}

	// Add outputs
	OutputMapping output = null;
// FIXME: Support EL output mappings, e.g.: output1="#{requestScope.bar}"
	for (String outputKey : def.getOutputDefs().keySet()) {
	    output = handler.getOutputValue(outputKey);
	    write(seperator + outputKey + "=>$" + output.getStringOutputType()
		+ "{" + output.getOutputKey() + "}");
	    seperator = ", ";
	}
	
	// Close the Handler
	write(");\n");
    }

    /**
     *	<p> Write a {@link LayoutStaticText} to the
     *	    <code>OutputStream</code>.</p>
     */
    protected void writeLayoutStaticText(String prefix, LayoutStaticText text) throws IOException {
	String value = "" + text.getOptions().get("value");
	if (value.contains("\n")) {
	    // Check for multi-line comment
	    write(prefix + "<f:verbatim>" + value + "</f:verbatim>\n");
	} else {
	    write(prefix + "\"" + text.getOptions().get("value") + "\n");
	}
    }

    /**
     *	<p> Write a {@link LayoutComponent} to the
     *	    <code>OutputStream</code>.</p>
     */
    protected void writeLayoutComponent(String prefix, LayoutComponent comp) throws IOException {
	String type = comp.getType().getId();
	write(prefix + "<" + type);
	write(" id=\"" + comp.getUnevaluatedId() + "\"");
	if (comp.isOverwrite()) {
	    write(" overwrite=\"true\"");
	}
	Map<String, Object> options = comp.getOptions();
	for (String key : options.keySet()) {
	    write(" " + key + "=\"" + options.get(key) + "\"");
	}
	write(">\n");
	writeBody(INDENT + prefix, comp);
	write(prefix + "</" + type + ">\n");
    }

    /**
     *	<p> The <code>PrintWriter</code> used to send output the the
     *	    <code>OutputStream</code>.</p>
     */
    private PrintWriter _writer = null;

    /**
     *	<p> This is the value used when indenting is needed.</p>
     */
    private static final String INDENT =    "  ";
}
