/**
 * 
 */
package com.sun.jsftemplating.layout.facelets;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
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
import com.sun.jsftemplating.layout.descriptors.LayoutDefine;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutInsert;
import com.sun.jsftemplating.layout.descriptors.LayoutStaticText;
import com.sun.jsftemplating.layout.template.TemplateWriter;
import com.sun.jsftemplating.util.LayoutElementUtil;

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
	    this.key = key;
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
            process(ld, nodeList.item(i), false);
        }
        FileOutputStream os = new FileOutputStream("c:\\temp\\template.out.txt");
        TemplateWriter writer = new TemplateWriter(os);
        writer.write(ld);
        os.close();
        dumpLayoutElementTree(ld, "");
        return ld;
    }
    
    private void dumpLayoutElementTree(LayoutElement element, String padding) {
        String value = element.toString();
        if (element instanceof LayoutStaticText) {
            value = ((LayoutStaticText)element).getValue();
        } else if (element instanceof LayoutComponent) {
            value = ((LayoutComponent)element).getType().toString();
        }
        System.out.println (padding + element.getUnevaluatedId() + ":  " + value);
        for (LayoutElement child : element.getChildLayoutElements()) {
            dumpLayoutElementTree (child, padding+"    ");
        }
    }

    public void process(LayoutElement parent, Node node, boolean nested) throws IOException {
        LayoutElement element = null;
        LayoutElement newParent = parent;
	boolean endElement = false;

        String value = node.getNodeValue();
//        System.out.println(node.getNodeName() + ":  '" + node.getNodeType() + "' = '" + value + "'");
        // TODO:  find out what "name" should be in the ctors
        switch (node.getNodeType()) {
        case Node.TEXT_NODE :
	    if (!value.trim().equals("")) {
		element = new LayoutStaticText(parent, 
                    LayoutElementUtil.getGeneratedId(node.getNodeName()), 
                    value);
	    }
            break;
        case Node.ELEMENT_NODE:
            nested = true;
            element = createComponent(parent, node, nested);
	    if (element instanceof LayoutStaticText) {
		// We have a element node that needs to be static text
		endElement = true;
	    } else {
		newParent = element;
	    }
            break;
        default:
            // just because... :P
        }

        if (element != null) {
            parent.addChildLayoutElement(element);

            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                process(newParent, nodeList.item(i), nested);
            }

	    if (endElement) {
		String nodeName = node.getNodeName();
		element = new LayoutStaticText(parent, LayoutElementUtil.getGeneratedId(nodeName), "</" + nodeName + ">");
		parent.addChildLayoutElement(element);
	    }
        }
    }

    private LayoutElement createComponent(LayoutElement parent, Node node, boolean nested) {
        LayoutElement element = null;
        String nodeName = node.getNodeName();
        String id = LayoutElementUtil.getGeneratedId(nodeName);

        if ("ui:composition".equals(nodeName)) {
            LayoutComposition lc = new LayoutComposition(parent, id);
            NamedNodeMap attrs = node.getAttributes();
            String template = ((Node)attrs.getNamedItem("template")).getNodeValue();
            lc.setTemplate(template);
            element = lc;
        } else if ("ui:define".equals(nodeName)) {
            LayoutDefine ld = new LayoutDefine(parent, id);
            NamedNodeMap attrs = node.getAttributes();
            String name = ((Node)attrs.getNamedItem("name")).getNodeValue();
            ld.setName(name);
            element = ld;
        } else if ("ui:insert".equals(nodeName)) {
            LayoutInsert li = new LayoutInsert(parent, id);
            NamedNodeMap attrs = node.getAttributes();
            String name = ((Node)attrs.getNamedItem("name")).getNodeValue();
            li.setName(name);
            element = li;
        } else if ("ui:component".equals(nodeName)) {
        } else if ("ui:debug".equals(nodeName)) {
        } else if ("ui:decorate".equals(nodeName)) {
            LayoutComposition lc = new LayoutComposition(parent, id, true);
            NamedNodeMap attrs = node.getAttributes();
            String template = ((Node)attrs.getNamedItem("template")).getNodeValue();
            lc.setTemplate(template);
            element = lc;
        } else if ("ui:fragment".equals(nodeName)) {
        } else if ("ui:include".equals(nodeName)) {
        } else if ("ui:param".equals(nodeName)) {
        } else if ("ui:remove".equals(nodeName)) {
            // Let the element remain null
        } else if ("ui:repeat".equals(nodeName)) {
        } else {
            ComponentType componentType = LayoutDefinitionManager.getGlobalComponentType(nodeName);
            if (componentType == null) {
                String value = node.getNodeValue();
                if (value == null) {
                    value = "";
                }
// FIXME: This needs to account for beginning and ending tags....
                element = new LayoutStaticText(parent, id, 
                        "<" + nodeName + buildAttributeList(node) + ">");
            } else {
                LayoutComponent lc = new LayoutComponent(parent, id, componentType);
                addAttributesToComponent(lc, node);
                lc.setNested(nested);
                LayoutElementUtil.checkForFacetChild(parent, lc);
                element = lc;
            }
        }

        return element;
    }
    
    private void addAttributesToComponent (LayoutComponent lc, Node node) {
        NamedNodeMap map = node.getAttributes();
        for (int i = 0; i < map.getLength(); i++) {
            Node attr = map.item(i);
            lc.addOption(attr.getNodeName(), attr.getNodeValue());
        }
    }
    
    private String buildAttributeList(Node node) {
        StringBuilder attrs = new StringBuilder();
        
        NamedNodeMap map = node.getAttributes();
        for (int i = 0; i < map.getLength(); i++) {
            Node attr = map.item(i);
            attrs.append(" ")
                .append(attr.getNodeName())
                .append("=\"")
                .append(attr.getNodeValue())
                .append("\"");
        }
        
        return attrs.toString();
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
