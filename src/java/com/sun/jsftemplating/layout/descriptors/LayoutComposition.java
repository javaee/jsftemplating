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
    private boolean trimming = true;

    /**
     * @param parent
     * @param id
     */
    public LayoutComposition(LayoutElement parent, String id) {
        super(parent, id);
    }

    public LayoutComposition(LayoutElement parent, String id, boolean trimming) {
        super(parent, id);
        this.trimming = trimming;
    }

    public String getTemplate() {
        return template;
    }

    public boolean isTrimming() {
        return trimming;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setTrimming(boolean trimming) {
        this.trimming = trimming;
    }

    @Override
    protected boolean encodeThis(FacesContext context, UIComponent component)
            throws IOException {
        // TODO Auto-generated method stub
        return false;
    }
}
