/**
 * 
 */
package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.LayoutDefinitionException;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.event.EncodeEvent;


/**
 * @author Jason Lee
 *
 */
public class LayoutComposition extends LayoutElementBase {
    private static final long serialVersionUID = 1L;
    private boolean required = true;
    private String template;
    private boolean trimming = true;

    /**
     * @param parent
     * @param id
     */
    public LayoutComposition(LayoutElement parent, String id) {
        super(parent, id);
    }

    /**
     *	<p> Constructor.</p>
     */
    public LayoutComposition(LayoutElement parent, String id, boolean trimming) {
        super(parent, id);
        this.trimming = trimming;
    }

    /**
     *	<p> <code>true</code> if a template filename is required.
     *	    <code>false</code> if it should be ignored when the template
     *	    filename is not specified or does not exist on the
     *	    filesystem.</p>
     */
    public boolean isRequired() {
	return required;
    }

    /**
     *	<p> Setter for the template filename.</p>
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     *	<p> Accessor for the template filename.</p>
     */
    public String getTemplate() {
        return template;
    }

    /**
     *	<p> Setter for the template filename.</p>
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     *	<p> <code>true</code> if all content outside of this LayoutComposition
     *	    should be thrown away.</p>
     */
    public boolean isTrimming() {
        return trimming;
    }

    /**
     *	<p> Setter for the trimming property.</p>
     */
    public void setTrimming(boolean trimming) {
        this.trimming = trimming;
    }

    @Override
    protected boolean encodeThis(FacesContext context, UIComponent component)
            throws IOException {
	// The child LayoutElements for a LayoutComposition are consumed by
	// the template.  The LayoutElements consumed here is the template.
	String templateName = getTemplate();
	boolean result = true;
	if (templateName == null) {
	    return result;
	}

	// Add this to the stack
	LayoutComposition.push(context, this);

	// Fire an encode event
	dispatchHandlers(context, ENCODE, new EncodeEvent(component));

	LayoutElement template = null;
	try {
	    template = LayoutDefinitionManager.
		getLayoutDefinition(context, templateName);
	} catch (LayoutDefinitionException ex) {
	    if (isRequired()) {
		throw ex;
	    }

	    // If the template is optional ignore this error...
	}

	// Iterate over children
	if (template != null) {
	    LayoutElement childElt = null;
	    Iterator<LayoutElement> it = template.getChildLayoutElements().iterator();
	    while (it.hasNext()) {
		childElt = it.next();
		childElt.encode(context, component);
	    }
	    result = false;
	}

	// Pop this from the stack
	LayoutComposition.pop(context);

        return result;
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
	Map<String, Object> requestMap = (context == null) ?
		getTestMap() : context.getExternalContext().getRequestMap();
	Stack<LayoutElement> stack = (Stack<LayoutElement>)
	    requestMap.get(COMPOSITION_STACK_KEY);
	if (stack == null) {
	    stack = new Stack<LayoutElement>();
	    requestMap.put(COMPOSITION_STACK_KEY, stack);
	}
	return stack;
    }

    /**
     *	<p> This method returns a <code>Map</code> that may be used to test
     *	    this code outside JSF.</p>
     */
    private static Map<String, Object> getTestMap() {
	if (_testMap == null) {
	    _testMap = new HashMap<String, Object>();
	}
	return _testMap;
    }

    /**
     *	<p> This method allows the composition stack to be set directly.
     *	    Normally this isn't needed, but if a seperate walk of the tree
     *	    must be done in the middle of an existing walk, this may be
     *	    necessary to reset and restore the Stack.</p>
     */
    public static Stack<LayoutElement> setCompositionStack(FacesContext context, Stack<LayoutElement> stack) {
	Map requestMap = context.getExternalContext().getRequestMap();
	requestMap.put(COMPOSITION_STACK_KEY, stack);
	return stack;
    }

    /**
     *	<p> This is the key used to store the <code>LayoutComposition</code>
     *	    stack.</p>
     */
    private static final String COMPOSITION_STACK_KEY	= "_composition";

    /**
     *	<p> This Map exists to allow test cases to run w/o an ExternalContext
     *	    "request map."</p>
     */
    private static Map _testMap = null;
}
