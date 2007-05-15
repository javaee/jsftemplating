/**
 * 
 */
package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 *  <p>	This {@link LayoutElement} provides a means to identify a portion of
 *	the LayoutDefinition tree by name (id).  This is used by
 *	{@link LayoutInsert} to include portions of the tree defined elsewhere
 *	at the location of the {@link LayoutInsert}.</p>
 *
 *  @author Jason Lee
 */
public class LayoutDefine extends LayoutElementBase {
    private static final long serialVersionUID = 1L;

    /**
     * @param parent
     * @param id
     */
    public LayoutDefine(LayoutElement parent, String id) {
        super(parent, id);
    }

    @Override
    protected boolean encodeThis(FacesContext context, UIComponent component) throws IOException {
        return true;
    }
}
