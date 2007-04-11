/**
 * 
 */
package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.event.EncodeEvent;


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
	// The child LayoutElements for a LayoutComposition are consumed by
	// the template.  The LayoutElements consumed here is the template.
	String templateName = getTemplate();
	if (templateName == null) {
	    throw new IllegalArgumentException("You must specify a template!");
	}

	// Fire an encode event
	dispatchHandlers(context, ENCODE, new EncodeEvent(component));

	LayoutElement template = LayoutDefinitionManager.
	    getLayoutDefinition(context, templateName);

	// Iterate over children
	LayoutElement childElt = null;
	Iterator<LayoutElement> it = template.getChildLayoutElements().iterator();
	while (it.hasNext()) {
	    childElt = it.next();
	    childElt.encode(context, component);
	}
        return false;
    }
}
