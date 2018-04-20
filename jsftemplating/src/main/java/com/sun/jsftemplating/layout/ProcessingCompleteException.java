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
package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;

/**
 *  <p>	This exception is thrown to signal the parser to stop processing.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class ProcessingCompleteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     *	<p> The constructor.</p>
     */
    public ProcessingCompleteException(LayoutDefinition ld) {
	super("Processing completed early.");
	_layoutDef = ld;
    }

    /**
     *	<p> This method is here to prevent the superclass method from eating
     *	    up time.  This implementation does not require a stack trace.
     *	    This method simply returns <code>this</code>.</p>
     */
    @Override
    public Throwable fillInStackTrace() {
	return this;
    }

    /**
     *	<p> Accessor for the {@link LayoutDefinition} to be used.</p>
     */
    public LayoutDefinition getLayoutDefinition() {
	return _layoutDef;
    }

    /**
     *	<p> This hold the {@link LayoutDefinition} which should be used as
     *	    the result of processing the file.</p>
     */
    private LayoutDefinition _layoutDef = null;
}
