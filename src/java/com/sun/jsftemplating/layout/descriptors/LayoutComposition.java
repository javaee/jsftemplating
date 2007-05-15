/**
 * 
 */
package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

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

	// Add this to the stack
	LayoutComposition.push(context, this);

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

	// Pop this from the stack
	LayoutComposition.pop(context);

        return false;
    }

    /**
     *	<p> This handler pushes a value onto the
     *	    <code>LayoutComposition</code> <code>Stack</code>.</p>
     */
    public static void push(FacesContext context, LayoutElement comp) {
	getCompositionStack(context).push(comp);
    }

    /**
     *	<p> This handler pops a value off the
     *	    <code>LayoutComposition</code> <code>Stack</code>.</p>
     */
    public static LayoutElement pop(FacesContext context) {
	return getCompositionStack(context).pop();
    }

    /**
     *	<p> This method returns the <code>Stack</code> used to keep track of
     *	    the {@link LayoutComposition}s that are used.</p>
     */
    public static Stack<LayoutElement> getCompositionStack(FacesContext context) {
	Map requestMap = context.getExternalContext().getRequestMap();
	Stack<LayoutElement> stack = (Stack<LayoutElement>)
	    requestMap.get(COMPOSITION_STACK_KEY);
	if (stack == null) {
	    stack = new Stack<LayoutElement>();
	    requestMap.put(COMPOSITION_STACK_KEY, stack);
	}
	return stack;
    }

    /**
     *	<p> This is the key used to store the <code>LayoutComposition</code>
     *	    stack.</p>
     */
    private static final String COMPOSITION_STACK_KEY	= "_composition";
}
