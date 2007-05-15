/**
 * 
 */
package com.sun.jsftemplating.layout.descriptors;

import com.sun.jsftemplating.layout.event.EncodeEvent;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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
    protected boolean encodeThis(FacesContext context, UIComponent component) throws IOException {
	Stack<LayoutElement> stack =
	    LayoutComposition.getCompositionStack(context);
	if (stack.empty()) {
	    // Render whatever is inside the insert
	    return true;
	}

	// Get assoicated UIComposition
	String name = getName();
	if (name == null) {
	    encodeChildren(context, component, stack.get(0));
	} else {
	    // First resolve any EL in the insertName
	    name = "" + resolveValue(context, component, name);

	    // Search for specific LayoutDefine
	    LayoutElement def = LayoutInsert.findLayoutDefine(
		    context, component, stack, name);
	    if (def == null) {
		// Render whatever is inside the insert
		return true;
	    } else {
		// Found ui:define, render it
		encodeChildren(context, component, def);
	    }
	}
	return false; // Already rendered it
    }

    /**
     *	<p> Encode the appropriate children...</p>
     */
    private void encodeChildren(FacesContext context, UIComponent component, LayoutElement parentElt) throws IOException {
	// Fire an encode event
	dispatchHandlers(context, ENCODE, new EncodeEvent(component));

	// Iterate over children
	LayoutElement childElt = null;
	Iterator<LayoutElement> it = parentElt.getChildLayoutElements().iterator();
	while (it.hasNext()) {
	    childElt = it.next();
	    childElt.encode(context, component);
	}
    }

    /**
     *	<p> This method searches the given the entire <code>stack</code> for a
     *	    {@link LayoutDefine} with the given <code>name</code>.</p>
     */
    public static LayoutDefine findLayoutDefine(FacesContext context, UIComponent parent, List<LayoutElement> eltList, String name) {
	Iterator<LayoutElement> stackIt = eltList.iterator();
	LayoutDefine define = null;
	while (stackIt.hasNext()) {
	    define = findLayoutDefine(context, parent, stackIt.next(), name);
	    if (define != null) {
		return define;
	    }
	}

	// Not found!
	return null;
    }

    /**
     *	<p> This method searches the given {@link LayoutElement} for a
     *	    {@link LayoutDefine} with the given <code>name</code>.</p>
     */
    private static LayoutDefine findLayoutDefine(FacesContext context, UIComponent parent, LayoutElement elt, String name) {
	Iterator<LayoutElement> it = elt.getChildLayoutElements().iterator();
	LayoutElement def = null;
	while (it.hasNext()) {
	    def = it.next();
	    if ((def instanceof LayoutDefine) && def.
		    getId(context, parent).equals(name)) {
		// We found what we're looking for...
		return (LayoutDefine) def;
	    }
	}

	// We still haven't found it, search the child LayoutElements
	it = elt.getChildLayoutElements().iterator();
	while (it.hasNext()) {
	    def = findLayoutDefine(context, parent, it.next(), name);
	    if (def != null) {
		return (LayoutDefine) def;
	    }
	}

	// Not found!
	return null;
    }

}
