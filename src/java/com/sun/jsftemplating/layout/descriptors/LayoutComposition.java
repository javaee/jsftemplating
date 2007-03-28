/**
 * 
 */
package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * @author Jason Lee
 *
 */
public class LayoutComposition extends LayoutElementBase {
    private static final long serialVersionUID = 1L;
    private String template;

    /**
     * @param parent
     * @param id
     */
    public LayoutComposition(LayoutElement parent, String id) {
        super(parent, id);
        // TODO Auto-generated constructor stub
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    protected boolean encodeThis(FacesContext context, UIComponent component)
            throws IOException {
        // TODO Auto-generated method stub
        return false;
    }
}
