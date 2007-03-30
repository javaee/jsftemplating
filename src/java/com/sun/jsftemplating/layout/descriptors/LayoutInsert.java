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
public class LayoutInsert extends LayoutElementBase {
    private static final long serialVersionUID = 1L;
    private String name;

    /**
     * @param parent
     * @param id
     */
    public LayoutInsert(LayoutElement parent, String id) {
        super(parent, id);
        // TODO Auto-generated constructor stub
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see com.sun.jsftemplating.layout.descriptors.LayoutElementBase#encodeThis(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    protected boolean encodeThis(FacesContext context, UIComponent component)
            throws IOException {
        // TODO Auto-generated method stub
        return false;
    }
}
