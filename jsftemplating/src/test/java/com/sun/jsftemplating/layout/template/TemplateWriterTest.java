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

import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 *  <p>	Tests for the {@link TemplateWriter}.</p>
 */
public class TemplateWriterTest {
    /**
     *	<p> </p>
     */
  private final ClassLoader cl = TemplateWriterTest.class.getClassLoader();
  
    @Test
    public void testWrite1() {
	try {
	    // First read some data
	    TemplateReader reader =
		new TemplateReader("foo", cl.getResource("./TemplateFormat.jsf"));
	    LayoutDefinition ld = reader.read();
//	    assertEquals("LayoutDefinition.unevaluatedId", "id2", ld.getUnevaluatedId());
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    TemplateWriter writer =
		new TemplateWriter(stream);
	    writer.write(ld);
// FIXME: Add some sort of check here
//	    System.err.println(stream.toString());
	} catch (IOException ex) {
	    ex.printStackTrace();
	    Assert.fail(ex.getMessage());
	}
    }
}
