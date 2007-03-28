/**
 * 
 */
package com.sun.jsftemplating.layout.facelets;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.jsftemplating.layout.LayoutDefinitionException;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutComposition;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutStaticText;

/**
 * @author Jason Lee
 *
 */
public class FaceletsLayoutDefinitionReader {
    private URL url;
    private String key;
    private Document document;

    public FaceletsLayoutDefinitionReader(String key, URL url) {
        try{
            this.url = url;

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(false);
            dbf.setCoalescing(false);
            // The opposite of creating entity ref nodes is expanding inline
            dbf.setExpandEntityReferences(true);

            DocumentBuilder builder = dbf.newDocumentBuilder();
            builder.setErrorHandler(new ParsingErrorHandler());
            InputStream is = new BufferedInputStream(this.url.openStream());
            document = builder.parse(is);
            is.close();
        } catch (Exception e) {
            throw new LayoutDefinitionException(e);
        }
    }

    public LayoutDefinition read() throws IOException {
        LayoutDefinition ld = new LayoutDefinition(key);
        NodeList nodeList = document.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            process(ld, nodeList.item(i));
        }
        return ld;
    }

    public LayoutElement process(LayoutElement parent, Node node) throws IOException {
        LayoutElement element = null;

        String value = node.getNodeValue();
        if (value != null) {
            value = value.trim();
        } else {
//            value = "";
        }
        System.out.println(node.getNodeName() + ":  '" + node.getNodeType() + "' = '" + value + "'");
        // TODO:  find out what "name" should be in the ctors
        switch (node.getNodeType()) {
        case Node.TEXT_NODE :
            element = new LayoutStaticText(parent, "", node.getNodeValue());
            break;
        case Node.ELEMENT_NODE:
            element = createComponent(parent, node);
            break;
        default:
            // just because... :P
        }


        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            process(element, nodeList.item(i));
        }

        if (parent != null) {
            parent.addChildLayoutElement(element);
        }
        return element;
    }
    
    private LayoutElement createComponent(LayoutElement parent, Node node) {
        LayoutElement element = null;
        String nodeName = node.getNodeName();
        
        if ("ui:composition".equals(nodeName)) {
            LayoutComposition lc = new LayoutComposition(parent, "");
            NamedNodeMap attrs = node.getAttributes();
            String template = ((Node)attrs.getNamedItem("template")).getNodeValue();
            lc.setTemplate(template);
            element = lc;
        } else {
            ComponentType componentType = LayoutDefinitionManager.  getGlobalComponentType(nodeName);
            element = new LayoutComponent(parent, "", componentType);
        }
        
        return element;
    }
}


class ParsingErrorHandler implements org.xml.sax.ErrorHandler {
    //Log logger = LogFactory.getLog(this.getClass());

    public ParsingErrorHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void warning(SAXParseException arg0) throws SAXException {
//      logger.warn(arg0.getMessage());
    }

    public void error(SAXParseException arg0) throws SAXException {
        //logger.error(arg0.getMessage());
        fatalError(arg0);
    }

    public void fatalError(SAXParseException arg0) throws SAXException {
//      logger.error(arg0.getMessage());
        System.err.println (arg0.getMessage());
        System.exit(-1);
    }

}