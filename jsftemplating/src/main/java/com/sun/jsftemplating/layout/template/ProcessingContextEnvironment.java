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

import com.sun.jsftemplating.layout.descriptors.LayoutElement;


/**
 *  <p> This class hold environmental information needed while a parsing.  This
 *	information is specific to the nested level that is currently being
 *	processed.  This is unlike the {@link ProcessingContext}
 *	which is related to the "type" of element that is being processed.  Or
 *	said another way, the {@link ProcessingContext}
 *	specifies how / what sub-elements are to be processed based on the
 *	context; this class provides the "where" information for that
 *	processing.  Another difference is this class is stateful, the
 *	{@link ProcessingContext} has stateless methods that
 *	parse specific portions of the document.</p>
 *
 *  @see    TemplateReader
 *  @see    ProcessingContext
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class ProcessingContextEnvironment {

    /**
     *	<p> Constructor.</p>
     */
    public ProcessingContextEnvironment(TemplateReader reader, LayoutElement parent, boolean nested) {
	_reader = reader;
	_parent = parent;
	_nested = nested;
    }

    /**
     *  @return The <code>TemplateReader</code> instance.
     */
    public TemplateReader getReader() {
	return _reader;
    }

    /**
     *  @return <code>true</code> if nested in a LayoutComponent.
     */
    public boolean isNested() {
	return _nested;
    }

    /**
     *  @return The parent {@link LayoutElement}.
     */
    public LayoutElement getParent() {
	return _parent;
    }

    /**
     *  <p> This method marks the current {@link ProcessingContext} as
     *	    complete.</p>
     */
    public void setFinished(boolean finished) {
	_finished = finished;
    }

    /**
     *  <p> This method indicates if the current {@link ProcessingContext}
     *	    is still valid.</p>
     */
    public boolean isFinished() {
	return _finished;
    }

    boolean		_finished   = false;
    boolean		_special    = false;
    boolean		_nested	    = false;
    LayoutElement	_parent	    = null;
    TemplateReader	_reader	    = null;
}
