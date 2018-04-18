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

package com.sun.jsftemplating.annotation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * A JSR 169 compliant annotation processor for FormatDefinition annotation
 * This is required for JDK8+, since APT has been deprecated.
 * @author Romain Grecourt
 */
@SupportedAnnotationTypes(value = {"com.sun.jsftemplating.annotation.FormatDefinition"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class FormatDefinitionAP extends AbstractProcessor {

  public static final String FACTORY_FILE = "META-INF/jsftemplating/FormatDefinition.map";
  private PrintWriter writer = null;
  private boolean _setup = false;

  private boolean setup() {
    if (_setup) {
      // Don't do setup more than once
      return true;
    }
    try {
      // Create factory mapping file
      writer = getMapWriter();
    } catch (IOException ex) {
      StringWriter buf = new StringWriter();
      ex.printStackTrace(new PrintWriter(buf));
      processingEnv.getMessager().printMessage(
          Diagnostic.Kind.ERROR,
          String.format("Unable to write %s file while processing @FormatDefinition annotation %s",
              FACTORY_FILE,
              buf.toString()));
      return false;
    }
    _setup = true;
    return _setup;
  }

  private PrintWriter getMapWriter() throws IOException {
    PrintWriter _writer = null;
    ClassLoader cl = this.getClass().getClassLoader();
    URL url;
    for (Enumeration<URL> urls = cl.getResources(FACTORY_FILE);
        urls.hasMoreElements() && (_writer == null);) {
      url = urls.nextElement();
      if ((url != null) && new File(url.getFile()).canRead()) {
        // Append to the existing file...
        _writer = new PrintWriter(new FileOutputStream(url.getFile(), true));
      }
    }
    if (_writer == null) {
      // File not found, create a new one...
      FileObject  fo = processingEnv.getFiler().createResource(
          StandardLocation.CLASS_OUTPUT,
          "",
          FACTORY_FILE);
      _writer = new PrintWriter(fo.openWriter());
      return _writer;
    }
    return _writer;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    if (SourceVersion.latest().compareTo(SourceVersion.RELEASE_6) > 0) {
      return SourceVersion.valueOf("RELEASE_7");
    } else {
      return SourceVersion.RELEASE_6;
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    setup();
 
    for (Element decl : roundEnv.getElementsAnnotatedWith(FormatDefinition.class)) {
      for (AnnotationMirror an : decl.getAnnotationMirrors()) {
        writer.println(decl.toString());
      }
    }

    if(_setup){
      writer.close();
    }
    return roundEnv.processingOver();
  }
}
