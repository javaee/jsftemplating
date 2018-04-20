/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006-2018 Oracle and/or its affiliates. All rights reserved.
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

import com.sun.jsftemplating.layout.descriptors.LayoutElement;


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
