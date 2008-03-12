/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.jsftemplating.util.fileStreamer;

import com.sun.jsftemplating.util.LogUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jason CTR Lee
 */
public class FileStreamerPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1;
    private static final String INVOCATION_PATH = "com.sun.jsftemplating.INVOCATION_PATH";
    public static final String STATIC_RESOURCE_IDENTIFIER = "/jsft_resource";

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void beforePhase(PhaseEvent event) {
        if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
            FacesContext context = event.getFacesContext();
            ExternalContext extContext = context.getExternalContext();
            Context fsContext = new FacesStreamerContext(context);
            String path = null;

            HttpServletRequest req = (HttpServletRequest) extContext.getRequest();
            if (req.getRequestURI().indexOf(STATIC_RESOURCE_IDENTIFIER) != -1) {
                context.responseComplete();
                // Get the HttpServletResponse
                Object obj = extContext.getResponse();
                HttpServletResponse resp = null;
                if (obj instanceof HttpServletResponse) {
                    resp = (HttpServletResponse) obj;
                    path = extContext.getRequestParameterMap().get(Context.CONTENT_FILENAME);

                    fsContext.setAttribute(Context.FILE_PATH, path);

                    // We have an HttpServlet response, do some extra stuff...
                    // Check the last modified time to see if we need to serve the resource
                    long mod = fsContext.getContentSource().getLastModified(fsContext);
                    if (mod != -1) {
                        long ifModifiedSince = req.getDateHeader("If-Modified-Since");
                        // Round down to the nearest second for a proper compare
                        if (ifModifiedSince < (mod / 1000 * 1000)) {
                            // A ifModifiedSince of -1 will always be less
                            resp.setDateHeader("Last-Modified", mod);
                        } else {
                            // Set not modified header and complete response
                            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        }
                    }
                }

                // Stream the content
                try {
                    FileStreamer.getFileStreamer().streamContent(fsContext);
                } catch (FileNotFoundException ex) {
                    if (LogUtil.infoEnabled()) {
                        LogUtil.info("JSFT0004", (Object) path);
                    }
                    if (resp != null) {
                        try {
                            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                        } catch (IOException ioEx) {
                            // Ignore
                        }
                    }
                } catch (IOException ex) {
                    if (LogUtil.infoEnabled()) {
                        LogUtil.info("JSFT0004", (Object) path);
                        if (LogUtil.fineEnabled()) {
                            LogUtil.fine("Resource (" + path + ") not available!", ex);
                        }
                    }
                }
            }
        }
    }

    public static String createResourceUrl(FacesContext context,
            String contentSourceId,
            String path) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(context.getExternalContext().getRequestContextPath());
        String mapping = getFacesMapping(context);
        if ((mapping.charAt(0) == '/')) { // prefix mapping
            sb.append("/")
                    .append(mapping)
                    .append(STATIC_RESOURCE_IDENTIFIER);
        } else {
            sb.append(STATIC_RESOURCE_IDENTIFIER)
                    .append(mapping);
        }

        sb.append("?")
                .append(Context.CONTENT_SOURCE_ID)
                .append("=")
                .append(contentSourceId == null ? Context.DEFAULT_CONTENT_SOURCE_ID : contentSourceId)
                .append("&")
                .append(Context.CONTENT_FILENAME)
                .append("=")
                .append(path);

        return sb.toString();
    }

    /**
     * <p>Returns the URL pattern of the
     * {@link javax.faces.webapp.FacesServlet} that
     * is executing the current request.  If there are multiple
     * URL patterns, the value returned by
     * <code>HttpServletRequest.getServletPath()</code> and
     * <code>HttpServletRequest.getPathInfo()</code> is
     * used to determine which mapping to return.</p>
     * If no mapping can be determined, it most likely means
     * that this particular request wasn't dispatched through
     * the {@link javax.faces.webapp.FacesServlet}.
     *
     * @param context the {@link FacesContext} of the current request
     *
     * @return the URL pattern of the {@link javax.faces.webapp.FacesServlet}
     *         or <code>null</code> if no mapping can be determined
     *
     * @throws NullPointerException if <code>context</code> is null
     */
    private static String getFacesMapping(FacesContext context) {

        if (context == null) {
            throw new NullPointerException("The FacesContext was null.");
        }

        // Check for a previously stored mapping   
        ExternalContext extContext = context.getExternalContext();
        String mapping = (String) extContext.getRequestMap().get(INVOCATION_PATH);

        if (mapping == null) {

            Object request = extContext.getRequest();
            String servletPath = null;
            String pathInfo = null;

            // first check for javax.servlet.forward.servlet_path
            // and javax.servlet.forward.path_info for non-null
            // values.  if either is non-null, use this
            // information to generate determine the mapping.

            if (request instanceof HttpServletRequest) {
                servletPath = extContext.getRequestServletPath();
                pathInfo = extContext.getRequestPathInfo();
            }


            mapping = getMappingForRequest(servletPath, pathInfo);
        }

        // if the FacesServlet is mapped to /* throw an 
        // Exception in order to prevent an endless 
        // RequestDispatcher loop
        if ("/*".equals(mapping)) {
            throw new FacesException("The FacesServlet was configured incorrectly");
        }

        if (mapping != null) {
            extContext.getRequestMap().put(INVOCATION_PATH, mapping);
        }
        return mapping;
    }

    /**
     * <p>Return the appropriate {@link javax.faces.webapp.FacesServlet} mapping
     * based on the servlet path of the current request.</p>
     *
     * @param servletPath the servlet path of the request
     * @param pathInfo    the path info of the request
     *
     * @return the appropriate mapping based on the current request
     *
     * @see HttpServletRequest#getServletPath()
     */
    private static String getMappingForRequest(String servletPath, String pathInfo) {

        if (servletPath == null) {
            return null;
        }

        // If the path returned by HttpServletRequest.getServletPath()
        // returns a zero-length String, then the FacesServlet has
        // been mapped to '/*'.
        if (servletPath.length() == 0) {
            return "/*";
        }

        // presence of path info means we were invoked
        // using a prefix path mapping
        if (pathInfo != null) {
            return servletPath;
        } else if (servletPath.indexOf('.') < 0) {
            // if pathInfo is null and no '.' is present, assume the
            // FacesServlet was invoked using prefix path but without
            // any pathInfo - i.e. GET /contextroot/faces or
            // GET /contextroot/faces/
            return servletPath;
        } else {
            // Servlet invoked using extension mapping
            return servletPath.substring(servletPath.lastIndexOf('.'));
        }
    }

    public void afterPhase(PhaseEvent arg0) {
        // no op
    }
}