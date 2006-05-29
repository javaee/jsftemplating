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

import com.sun.jsftemplating.layout.descriptors.LayoutElement;

import java.io.IOException;


/**
 *  <p> This interface defines the operations that may be acted upon while
 *      a "template" document is traversed.  The intent is that this
 *      interface may be implemented by the different "processing contexts"
 *      which occur throught the template file.  This provides the
 *      opportunity for context sensitive syntax in an easy to provide
 *      way.</p>
 *
 *  <p> While the standard <code>ProcessingContext</code> instances are
 *      likely to be sufficient, there may be cases where a custom
 *      <code>ProcessingContext</code> may be needed to process the
 *      children of a {@link CustomParserCommand}.  This may be done by
 *      implementing this interface, or extending one of the existing
 *      implementations.  Typically this object is used by passing it to
 *      the {@link TemplateReader#process(ProcessingContext, LayoutElement, boolean)}
 *      method -- this method will delegate actions back to the given
 *      <code>ProcessingContext</code>.</p>
 */
public interface ProcessingContext {

    /**
     *  <p> This is called when a component tag is found (&lt;tagname ...).</p>
     */
    void beginComponent(ProcessingContextEnvironment env, String content) throws IOException;

    /**
     *  <p> This is called when an end component tag is found (&lt;/tagname
     *	    ... or &lt;tagname ... /&gt;).</p>
     */
    void endComponent(ProcessingContextEnvironment env, String content) throws IOException;

    /**
     *  <p> This is called when a special tag is found (&lt;!tagname ...).</p>
     */
    void beginSpecial(ProcessingContextEnvironment env, String content) throws IOException;

    /**
     *  <p> This is called when a special end tag is found (&lt;/tagname ...
     *	    or &lt;!tagname ... /&gt;).</p>
     */
    void endSpecial(ProcessingContextEnvironment env, String content) throws IOException;

    /**
     *  <p> This is called when static text is found (").</p>
     */
    void staticText(ProcessingContextEnvironment env, String content) throws IOException;

    /**
     *  <p> This is called when escaped static text is found (').  The
     *	    difference between this and staticText is that HTML is expected to
     *	    be escaped so the browser does not parse it.</p>
     */
    void escapedStaticText(ProcessingContextEnvironment env, String content) throws IOException;

    /**
     *  <p> This method is invoked when nothing else matches.</p>
     */
    void handleDefault(ProcessingContextEnvironment env, String content) throws IOException;
}
