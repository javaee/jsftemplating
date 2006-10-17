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
package com.sun.jsftemplating.util.fileStreamer;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

//import javax.servlet.http.HttpServletResponse;

import com.sun.jsftemplating.util.Util;


/**
 *  <p>	This class provides the ability to retrieve information from an
 *	abritrary source.  It provides the ability to set the content type
 *	of the file, if the request is Servlet based.  If the request is not
 *	explicitly specified, it will attempt to guess based on the extension
 *	(if possible).  It requires the {@link ContentSource} for the data to
 *	be retrieved to be specified by calling
 *	{@link #registerContentSource(String)}.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class FileStreamer {

    /**
     *	<p> Only instantiate via factory method.</p>
     */
    private FileStreamer() {
	super();
    }

    /**
     *	<p> Use this method to obtain the instance of this class.</p>
     */
    public static FileStreamer getFileStreamer() {
	if (_streamer == null) {
	    _streamer = new FileStreamer();
	}
	return _streamer;
    }

    /**
     *	<p> This method registers the given <code>className</code> as a
     *	    {@link ContentSource}.  This method will attempt to resolve and
     *	    instantiate the given <code>className</code>.  If it is unable to
     *	    do so, it will throw an <code>IllegalArgumentException</code>.</p>
     *
     *	@param	className   The full classname of the {@link ContentSource}
     *			    implementation.
     */
    public void registerContentSource(String className) {
	// Sanity Check
	if ((className == null) || className.trim().equals("")) {
	    return;
	}

	Class cls = null;
	try {
	    cls = Util.getClassLoader(className).loadClass(className);
	} catch (Exception ex) {
	    throw new IllegalArgumentException(ex);
	}
	registerContentSource(cls);
    }

    /**
     *	<p> This method registers the given <code>Class</code> as a
     *	    {@link ContentSource}.  This method will attempt to instantiate the
     *	    given <code>Class</code> (<code>cls</code>).  If it is unable to do
     *	    so, it will throw an <code>IllegalArgumentException</code>.</p>
     *
     *	@param	cls The <code>Class</code> for the {@link ContentSource}
     *		    implementation.
     */
    public void registerContentSource(Class cls) {
	// Create a new instance
	ContentSource source = null;
	try {
	    source = (ContentSource) cls.newInstance();
	} catch (Exception ex) {
	    throw new IllegalArgumentException(ex);
	}
	registerContentSource(source);
    }

    /**
     *	<p> This method registers the given {@link ContentSource}.</p>
     *
     *	@param	source	 The {@link ContentSource} implementation to register.
     */
    public void registerContentSource(ContentSource source) {
	// Add the new instance to the registered ContentSources
	_contentSources.put(source.getId(), source);
    }

    /**
     *	<p> This method looks up a ContentSource given its id.  The
     *	    {@link ContentSource} must be previously registered.</p>
     */
    public ContentSource getContentSource(String id) {
	return _contentSources.get(id);
    }

    /**
     *	<p> This method is the main method for this class.  It drives the
     *	    process, which includes obtaining the appropriate
     *	    {@link ContentSource}, and copying the output of the
     *	    {@link ContentSource} to the {@link Context}'s
     *	    <code>OutputStream</code>, and telling the {@link ContentSource}
     *	    to {@link ContentSource#cleanUp(Context)}.</p>
     *
     *	@param	ctx The {@link Context} in which to execute this method.
     */
    public void streamContent(Context ctx) throws IOException {
	// Get the ContentSource
	ContentSource source = ctx.getContentSource();

	// Write Content
	writeContent(source, ctx);

	// Clean Up
	source.cleanUp(ctx);
    }

    /**
     *	<p> This method is responsible for copying the data from the given
     *	    <code>InputStream</code> to the <code>OutputStream</code>.  The
     *	    <code>InputStream</code> comes from the {@link ContentSource}.
     *	    The <code>OutputStream</code> is obtained from the {@link Context}.
     *	    </p>
     */
    protected void writeContent(ContentSource source, Context context) throws IOException {
	// Get the InputStream
	InputStream in = source.getInputStream(context);

	// Get the OutputStream
	if (in == null) {
// FIXME: Provide generic "response complete" flag & check this before throwing an exception...
	    throw new FileNotFoundException();
//	    String jspPage = (String) context.getAttribute("JSP_PAGE_SERVED"); 
//	    if (jspPage != null && (jspPage.equals("false"))) {
//		try {
		    //Mainly to take care of javahelp2, bcz javahelp2 code needs an Exception to be thrown for FileNotFound.
		    //We may have to localize this message.
//		    ((HttpServletResponse)resp).sendError(404, "File Not Found");
//		} catch (IOException ex) {
		    //squelch it, just return.
//		}
//	    }

	    // nothing to write, already done
//	    return;
	}

	OutputStream out = context.getOutputStream();

	// Get the InputStream
	InputStream stream = new BufferedInputStream(in);

	// Write the header
	context.writeHeader(source);

	// Copy the data to the ServletOutputStream
	byte [] buf = new byte[512]; // Set our buffer at 512 bytes
	int read = stream.read(buf, 0, 512);
	while (read != -1) {
	    // Write data from the OutputStream to the InputStream
	    out.write(buf, 0, read);

	    // Read more...
	    read = stream.read(buf, 0, 512);
	}

	// Close the Stream
	stream.close();
    }

    /**
     *	<p> This method attempts to return the mime type for the given
     *	    <code>extension</code>.  If the type is not found,
     *	    <code>null</code> will be returned.</p>
     */
    public static String getMimeType(String extension) {
	return mimeTypes.get(extension.toLowerCase());
    }

    /**
     *	<p> Get the default mime type {@link #DEFAULT_CONTENT_TYPE}.</p>
     */
    public static String getDefaultMimeType() {
	return DEFAULT_CONTENT_TYPE;
    }

    /**
     *	HashMap to hold mimetypes by extension.
     */
    private static Map<String, String> mimeTypes =
	    new HashMap<String, String>(120);
    static {
	mimeTypes.put("aif", "audio/x-aiff");
	mimeTypes.put("aifc", "audio/x-aiff");
	mimeTypes.put("aiff", "audio/x-aiff");
	mimeTypes.put("asc", "text/plain");
	mimeTypes.put("asf", "application/x-ms-asf");
	mimeTypes.put("asx", "application/x-ms-asf");
	mimeTypes.put("au", "audio/basic");
	mimeTypes.put("avi", "video/x-msvideo");
	mimeTypes.put("bin", "application/octet-stream");
	mimeTypes.put("bmp", "image/bmp");
	mimeTypes.put("bwf", "audio/wav");
	mimeTypes.put("bz2", "application/x-bzip2");
	mimeTypes.put("c", "text/plain");
	mimeTypes.put("cc", "text/plain");
	mimeTypes.put("cdda", "audio/x-aiff");
	mimeTypes.put("class", "application/octet-stream");
	mimeTypes.put("com", "application/octet-stream");
	mimeTypes.put("cpp", "text/plain");
	mimeTypes.put("cpr", "image/cpr");
	mimeTypes.put("css", "text/css");
	mimeTypes.put("doc", "application/msword");
	mimeTypes.put("dot", "application/msword");
	mimeTypes.put("dtd", "text/xml");
	mimeTypes.put("ear", "application/zip");
	mimeTypes.put("exe", "application/octet-stream");
	mimeTypes.put("flc", "video/flc");
	mimeTypes.put("fm", "application/x-maker");
	mimeTypes.put("frame", "application/x-maker");
	mimeTypes.put("frm", "application/x-maker");
	mimeTypes.put("h", "text/plain");
	mimeTypes.put("hh", "text/plain");
	mimeTypes.put("hpp", "text/plain");
	mimeTypes.put("hqx", "application/mac-binhex40");
	mimeTypes.put("htm", "text/html");
	mimeTypes.put("html", "text/html");
	mimeTypes.put("gif", "image/gif");
	mimeTypes.put("gz", "application/x-gunzip");
	mimeTypes.put("ico", "image/x-icon");
	mimeTypes.put("iso", "application/octet-stream");
	mimeTypes.put("jar", "application/zip");
	mimeTypes.put("java", "text/plain");
	mimeTypes.put("jnlp", "application/x-java-jnlp-file");
	mimeTypes.put("jpeg", "image/jpeg");
	mimeTypes.put("jpe", "image/jpeg");
	mimeTypes.put("jpg", "image/jpeg");
	mimeTypes.put("js", "text/x-javascript");
	mimeTypes.put("jsf", "text/plain");
	mimeTypes.put("m3u", "audio/x-mpegurl");
	mimeTypes.put("maker", "application/x-maker");
	mimeTypes.put("mid", "audio/midi");
	mimeTypes.put("midi", "audio/midi");
	mimeTypes.put("mim", "application/mime");
	mimeTypes.put("mime", "application/mime");
	mimeTypes.put("mov", "video/quicktime");
	mimeTypes.put("mp2", "audio/mpeg");
	mimeTypes.put("mp3", "audio/mpeg");
	mimeTypes.put("mp4", "video/mpeg4");
	mimeTypes.put("mpa", "video/mpeg");
	mimeTypes.put("mpe", "video/mpeg");
	mimeTypes.put("mpeg", "video/mpeg");
	mimeTypes.put("mpg", "video/mpeg");
	mimeTypes.put("mpga", "audio/mpeg");
	mimeTypes.put("mpm", "video/mpeg");
	mimeTypes.put("mpv", "video/mpeg");
	mimeTypes.put("pdf", "application/pdf");
	mimeTypes.put("pic", "image/x-pict");
	mimeTypes.put("pict", "image/x-pict");
	mimeTypes.put("pct", "image/x-pict");
	mimeTypes.put("pl", "application/x-perl");
	mimeTypes.put("png", "image/png");
	mimeTypes.put("pnm", "image/x-portable-anymap");
	mimeTypes.put("pbm", "image/x-portable-bitmap");
	mimeTypes.put("ppm", "image/x-portable-pixmap");
	mimeTypes.put("ps", "application/postscript");
	mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
	mimeTypes.put("qt", "video/quicktime");
	mimeTypes.put("ra", "application/vnd.rn-realaudio");
	mimeTypes.put("rar", "application/zip");
	mimeTypes.put("rf", "application/vnd.rn-realflash");
	mimeTypes.put("ra", "audio/vnd.rn-realaudio");
	mimeTypes.put("ram", "audio/x-pn-realaudio");
	mimeTypes.put("rm", "application/vnd.rn-realmedia");
	mimeTypes.put("rmm", "audio/x-pn-realaudio");
	mimeTypes.put("rsml", "application/vnd.rn-rsml");
	mimeTypes.put("rtf", "text/rtf");
	mimeTypes.put("rv", "video/vnd.rn-realvideo");
	mimeTypes.put("spl", "application/futuresplash");
	mimeTypes.put("snd", "audio/basic");
	mimeTypes.put("ssm", "application/smil");
	mimeTypes.put("swf", "application/x-shockwave-flash");
	mimeTypes.put("tar", "application/x-tar");
	mimeTypes.put("tgz", "application/x-gtar");
	mimeTypes.put("tif", "image/tiff");
	mimeTypes.put("tiff", "image/tiff");
	mimeTypes.put("txt", "text/plain");
	mimeTypes.put("ulw", "audio/basic");
	mimeTypes.put("war", "application/zip");
	mimeTypes.put("wav", "audio/x-wav");
	mimeTypes.put("wax", "application/x-ms-wax");
	mimeTypes.put("wm", "application/x-ms-wm");
	mimeTypes.put("wma", "application/x-ms-wma");
	mimeTypes.put("wml", "text/wml");
	mimeTypes.put("wmw", "application/x-ms-wmw");
	mimeTypes.put("wrd", "application/msword");
	mimeTypes.put("wvx", "application/x-ms-wvx");
	mimeTypes.put("xbm", "image/x-xbitmap");
	mimeTypes.put("xpm", "image/image/x-xpixmap");
	mimeTypes.put("xml", "text/xml");
	mimeTypes.put("xsl", "text/xml");
	mimeTypes.put("xls", "application/vnd.ms-excel");
	mimeTypes.put("zip", "application/zip");
	mimeTypes.put("z", "application/x-compress");
	mimeTypes.put("Z", "application/x-compress");
    }


    private static Map<String, ContentSource> _contentSources =
	    new HashMap<String, ContentSource>();

    static {
	_contentSources.put(
	    ResourceContentSource.ID, new ResourceContentSource());
    }

    /**
     *	<p> The Default Content-type ("application/octet-stream").</p>
     */
    public static final String DEFAULT_CONTENT_TYPE =
	    "application/octet-stream";

    private static FileStreamer    _streamer	= null;
}
