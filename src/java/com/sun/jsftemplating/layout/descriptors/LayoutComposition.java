/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://jsftemplating.dev.java.net/cddl1.html or
 * jsftemplating/cddl1.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at jsftemplating/cddl1.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.layout.descriptors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.LayoutDefinitionException;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.event.EncodeEvent;


/**
 * <p>	This concept is borrowed from <a href="https://facelets.dev.java.net">
 *	Facelets</a>.  A composition delegates to a "template" which performs
 *	the layout for the content which exists in the body of this
 *	composition component.  The composition may have {@link LayoutDefine}s
 *	to provide named blocks which the template may "insert" at the
 *	appropriate place.</p>
 *
 *  <p>	This {@link LayoutElement} implements the behavior not only for
 *	compositions, but also decorate, and include.</p>
 *
 * @author Jason Lee
 * @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class LayoutComposition extends LayoutElementBase {
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
	boolean result = false;
	if (required != null) {
	    Object answer = resolveValue(FacesContext.getCurrentInstance(), null, required);
	    if (answer != null) {
		result = Boolean.parseBoolean(answer.toString());
	    }
	}
	return result;
    }

    /**
     *	<p> Setter for the template filename.</p>
     */
    public void setRequired(String required) {
        this.required = required;
    }

    /**
     *	<p> Accessor for the template filename.</p>
     */
    public String getTemplate() {
	Object result = resolveValue(FacesContext.getCurrentInstance(), null, template);
        return (result == null) ? null : result.toString();
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
     *	    <code>LayoutComposition</code> <code>Stack</code>.  In addition
     *	    it puts any parameters that are defined into the global parameter
     *	    <code>Map</code> so EL expressions can test to see if they may
     *	    reference one a composition parameter.  However, this
     *	    <code>Map</code> should not be used to determine the value --
     *	    instead the value should be obtained by looking through the
     *	    Stack of compositions.</p>
     */
    public static void push(FacesContext context, LayoutElement comp) {
	if (comp instanceof LayoutComposition) {
	    // This should be the case...
	    Map<String, Object> params =
		    ((LayoutComposition) comp).getParameters();
	    if (params != null) {
		// Get the request-scoped global param map...
		Map<String, Object> globalParamMap =
			LayoutComposition.getGlobalParamMap(context);

		// Iterate over the params in this LayoutComposition and add
		// them to the global parameters that we're tracking.  This
		// will flatten the hierarchy for the parameter values... but
		// that's ok, b/c we don't use this for obtaining the values
		// (normally), we use it to quickly detect if there are values.
		// We'll search the composition stack to actually obtain the
		// values.
		Iterator<Map.Entry<String, Object>> it =
			params.entrySet().iterator();
		Map.Entry<String, Object> entry = null;
		while (it.hasNext()) {
		    entry = it.next();
		    globalParamMap.put(entry.getKey(), entry.getValue());
		}
	    }
	}
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
     *	<p> This method retrieves a Map from the request scope for storing
     *	    ui:param NVPs.  If the <code>Map</code> doesn't exist, it will be
     *	    created.</p>
     */
    public static Map<String, Object> getGlobalParamMap(FacesContext context) {
	// First get the requestMap
	Map<String, Object> requestMap =
		context.getExternalContext().getRequestMap();
	Map<String, Object> paramMap = (Map<String, Object>)
		requestMap.get(GLOBAL_PARAM_MAP_KEY);
	if (paramMap == null) {
	    // Hasn't been created yet, create it
	    paramMap = new HashMap<String, Object>();
	    requestMap.put(GLOBAL_PARAM_MAP_KEY, paramMap);
	}
	return paramMap;
    }

    /**
     *	<p> This method returns a <code>Map</code> that may be used to test
     *	    this code outside JSF.</p>
     */
    private static Map<String, Object> getTestMap() {
// FIXME: Shouldn't we mock up the test environment instead of changing the code here?
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
     *	<p> This method searches the given the entire <code>stack</code> for a
     *	    template param with the given <code>name</code>.</p>
     *
     *	<p> A "template param" is a name-value-pair associated with a
     *	    {@link LayoutComposition}.  This enables overridable values to be
     *	    set on a LayoutComposition and consumed by the templates.  This is
     *	    similar to a ui:define, except for values instead of
     *	    <code>UIComponents</code>.</p>
     *
     *	@param	eltList	The <code>List</code> of LayoutCompositions in which
     *			to search (must be non-null, NPE will be thrown).
     *	@param	name	The name of the parameter to look for.
     */
    public static Object findTemplateParam(List<LayoutElement> eltList, String name) {
// FIME: Can I make this return a String?  If this is at create time I should still have #{} or maybe ${}
	Iterator<LayoutElement> stackIt = eltList.iterator();
	Object val = null;
	LayoutElement elt = null;
	LayoutComposition comp = null;
	while (stackIt.hasNext()) {
	    elt = stackIt.next();
	    if (elt instanceof LayoutComposition) {
		// It should always be a LayoutComposition, however, I want
		// to be safe in case things change in the future.
		comp = (LayoutComposition) elt;
		if ((val = comp.getParameter(name)) != null) {
		    break;
		}
	    }
	}

	// Return the value (if found)
	return val;
    }

    /**
     *	<p> This method returns the <code>Map</code> of parameter values, or
     *	    <code>null</code> if there are no parameter values for this
     *	    <code>LayoutComposition</code>.</p>
     */
    protected Map<String, Object> getParameters() {
	return _params;
    }

    /**
     *	<p> This method returns the parameter value for the requested
     *	    parameter, or <code>null</code> if the requested parameter does
     *	    not exist.</p>
     */
    public Object getParameter(String name) {
	Object value = null;
	if (_params != null) {
	    value = _params.get(name);
	}
	return value;
    }

    /**
     *	<p> This method sets the given parameter name with the given parameter
     *	    value.</p>
     */
    public void setParameter(String name, Object value) {
	if (_params == null) {
	    _params = new HashMap<String, Object>();
	}
	_params.put(name, value);
    }


    private static final long serialVersionUID = 2L;

    /**
     *	<p> This is the key used to store the <code>LayoutComposition</code>
     *	    stack.</p>
     */
    private static final String COMPOSITION_STACK_KEY	= "_composition";

    /**
     *	<p> This is the key used to store the ui:param NVPs to assist in
     *	    determining if an EL is referencing one.  It also may be used in
     *	    somone attempts to locate a ui:param after the composition stack
     *	    is no longer available (don't do that!).</p>
     */
    private static final String GLOBAL_PARAM_MAP_KEY	= "_uiparamCacheMap";

    /**
     *	<p> This Map exists to allow test cases to run w/o an ExternalContext
     *	    "request map."</p>
     */
    private static Map _testMap = null;

    /**
     *	<p> This is a <code>Map</code> of parameters that may be passed from
     *	    this <code>LayoutComposition</code> to the template.</p>
     */
    private Map<String, Object> _params = null;

    /**
     *	<p> Flag to indicate that whether an exception should be thrown if the
     *	    template is not found.</p>
     */
    private String required = null;

    /**
     *	<p> The filename of the template.</p>
     */
    private String template = null;

    /**
     *	<p> True if trimming should occur.</p>
     */
// FIXME: This info is only important at read-time, this probably should NOT exist
    private boolean trimming = true;
}
