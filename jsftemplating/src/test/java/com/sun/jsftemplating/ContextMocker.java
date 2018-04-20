/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jsftemplating;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

/**
 * Provide a dummy FacesContext for unit tests to pass.
 * 
 * @author Romain Grecourt
 */
public class ContextMocker extends FacesContext {
  public ExternalContext _extCtx = new ExternalContextMocker();
  public UIViewRoot _viewRoot = new UIViewRoot();
  
  public ContextMocker() {
  }

  static ContextMocker _ctx = new ContextMocker();
  public static void init(){
    setCurrentInstance(_ctx);
  }

  @Override
  public ExternalContext getExternalContext() {
    return _extCtx;
  }

  @Override
  public Application getApplication() {
    throw new UnsupportedOperationException("Not supported."); 
  }

  @Override
  public Iterator<String> getClientIdsWithMessages() {
    throw new UnsupportedOperationException("Not supported."); 
  }

  @Override
  public FacesMessage.Severity getMaximumSeverity() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public Iterator<FacesMessage> getMessages() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public Iterator<FacesMessage> getMessages(String clientId) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public RenderKit getRenderKit() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public boolean getRenderResponse() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public boolean getResponseComplete() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public ResponseStream getResponseStream() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void setResponseStream(ResponseStream responseStream) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public ResponseWriter getResponseWriter() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void setResponseWriter(ResponseWriter responseWriter) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public UIViewRoot getViewRoot() {
    return _viewRoot;
  }

  @Override
  public void setViewRoot(UIViewRoot root) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void addMessage(String clientId, FacesMessage message) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void release() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void renderResponse() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void responseComplete() {
    throw new UnsupportedOperationException("Not supported.");
  }
  
  public static class ExternalContextMocker extends ExternalContext {

    public ExternalContextMocker() {
    }
    
    public Map<String,Object> _appMap = new HashMap<String,Object>();
    public Map _initParamMap = new HashMap();
    public Map<String, Object> _requestMap = new HashMap<String,Object>();
    
    @Override
    public Map<String, Object> getApplicationMap() {
      return _appMap;
    }
    @Override
    public void dispatch(String path) throws IOException {
      throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String encodeActionURL(String url) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String encodeNamespace(String name) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String encodeResourceURL(String url) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String getAuthType() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Object getContext() {
      return this;
    }

    @Override
    public String getInitParameter(String name) {
      return (String) _initParamMap.get(name);
    }

    @Override
    public Map getInitParameterMap() {
      return _initParamMap;
    }

    @Override
    public String getRemoteUser() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Object getRequest() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String getRequestContextPath() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, Object> getRequestCookieMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, String> getRequestHeaderMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, String[]> getRequestHeaderValuesMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Locale getRequestLocale() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Iterator<Locale> getRequestLocales() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, Object> getRequestMap() {
      return _requestMap;
    }

    @Override
    public Map<String, String> getRequestParameterMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Iterator<String> getRequestParameterNames() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, String[]> getRequestParameterValuesMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String getRequestPathInfo() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String getRequestServletPath() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public URL getResource(String path) {
      return this.getClass().getClassLoader().getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Set<String> getResourcePaths(String path) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Object getResponse() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Object getSession(boolean create) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, Object> getSessionMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Principal getUserPrincipal() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public boolean isUserInRole(String role) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public void log(String message) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public void log(String message, Throwable exception) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public void redirect(String url) throws IOException {
      throw new UnsupportedOperationException("Not supported."); 
    }
    
  }
}
