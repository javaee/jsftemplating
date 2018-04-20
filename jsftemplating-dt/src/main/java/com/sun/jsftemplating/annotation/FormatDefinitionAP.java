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
 * Copyright 2007-2014 Sun Microsystems, Inc. All rights reserved.
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
