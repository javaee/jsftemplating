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
package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;
import com.sun.jsftemplating.layout.descriptors.handler.OutputMapping;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutFacet;
import com.sun.jsftemplating.layout.descriptors.LayoutForEach;
import com.sun.jsftemplating.layout.descriptors.LayoutIf;
import com.sun.jsftemplating.layout.descriptors.LayoutMarkup;
import com.sun.jsftemplating.layout.descriptors.LayoutStaticText;
import com.sun.jsftemplating.layout.descriptors.LayoutWhile;
import com.sun.jsftemplating.layout.descriptors.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


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
     *	@param	url	<code>URL</code> to the {@link LayoutDefinition} file.
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
	write(prefix + "\"" + text.getOptions().get("value") + "\n");
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
