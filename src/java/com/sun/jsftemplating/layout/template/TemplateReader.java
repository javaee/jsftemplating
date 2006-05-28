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
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.SyntaxException;
import com.sun.jsftemplating.layout.descriptors.handler.Handler;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;
import com.sun.jsftemplating.layout.descriptors.handler.IODescriptor;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutAttribute;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutFacet;
import com.sun.jsftemplating.layout.descriptors.LayoutForEach;
import com.sun.jsftemplating.layout.descriptors.LayoutIf;
import com.sun.jsftemplating.layout.descriptors.LayoutMarkup;
import com.sun.jsftemplating.layout.descriptors.LayoutStaticText;
import com.sun.jsftemplating.layout.descriptors.LayoutWhile;
import com.sun.jsftemplating.layout.descriptors.Resource;
import com.sun.jsftemplating.util.LayoutElementUtil;
import com.sun.jsftemplating.util.Util;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *  <p>	This class is responsible for parsing templates.  It produces a
 *	{@link LayoutElement} tree with a {@link LayoutDefinition} object at
 *	the root of the tree.</p>
 *
 *  <p>	This class is intended to "read" the template one time.  Often it may
 *	be useful to cache the result as it would be inefficient to call
 *	{@link TemplateReader#read()} multiple time (and therefor parse the
 *	template multiple times).  Templates that are generated from this class
 *	are intended to be static and safe to share (but not alter).</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class TemplateReader {

    /**
     *	<p> Constructor.</p>
     *
     *	@param	url	<code>URL</code> to the {@link LayoutDefinition} file.
     */
    public TemplateReader(URL url) {
	_tpl = new TemplateParser(url);
    }

    /**
     *	<p> Constructor.</p>
     *
     *	@param	parser	{@link TemplateParser} ready to read the
     *			{@link LayoutDefinition}.
     */
    public TemplateReader(TemplateParser parser) {
	_tpl = parser;
    }

    /**
     *	<p> Accessor for the {@link TemplateParser}.</p>
     *
     *	@return	The {@link TemplateParser}.
     */
    public TemplateParser getTemplateParser() {
	return _tpl;
    }

    /**
     *	<p> The read method uses the {@link TemplateParser} to parses the
     *	    template.  It populates a {@link LayoutDefinition} structure, which
     *	    is returned.</p>
     *
     *	@return	The {@link LayoutDefinition}
     *
     *	@throws	IOException
     */
    public LayoutDefinition read() throws IOException {
	// Open the Template
	TemplateParser parser = getTemplateParser();
	parser.open();

	try {
	    // Populate the LayoutDefinition from the Document
	    return readLayoutDefinition();
	} finally {
	    parser.close();
	}
    }

    /**
     *	<p> This method is responsible for creating and populating the
     *	    {@link LayoutDefinition}.</p>
     *
     *	@return	The new {@link LayoutDefinition} Object.
     */
    private LayoutDefinition readLayoutDefinition() throws IOException {
	// Create a new LayoutDefinition (the id is not propagated here)
	LayoutDefinition ld = new LayoutDefinition("");

	// For now we will only support global resources.  In the future, we
	// may want to allow resources to be overriden at the page level and /
	// or additional page-specific resources to be added.
	//
	// NOTE: Resources may be locale specific, so we can't easily share
	//	 this at the application scope.  Here, "global" means across
	//	 pages, not across sessions.
// FIXME: This isn't implemented yet...
	ld.setResources(LayoutDefinitionManager.getGlobalResources());

/*
	// Look to see if there is an EVENT_ELEMENT defined
	childElements = getChildElements(node, EVENT_ELEMENT);
	it = childElements.iterator();
	if (it.hasNext()) {
	    // Found the EVENT_ELEMENT, there is at most 1
	    // Get the event type
	    Node eventNode = (Node) it.next();
	    String type = (String) getAttributes(eventNode).
		get(TYPE_ATTRIBUTE);

	    // Set the Handlers for the given event type (name)
	    List handlers = ld.getHandlers(type);
	    ld.setHandlers(type, getHandlers(eventNode, handlers));
	}
*/
	return (LayoutDefinition) process(LAYOUT_DEFINITION_CONTEXT, ld, false);
    }

    /**
     *	<p> This method does the walking through the file.  The file has
     *	    different "contexts" in which it may be processing.  For example,
     *	    when processing at the top-level, certain syntax is valid.
     *	    However, when processing content inside a component, the valid
     *	    syntax may be different.  This method delegates handling of various
     *	    syntaxes to the {@link ProcessingContext}.  This enables the
     *	    walking and the processing to be separated.</p>
     *
     *	<p> The <code>nested</code> flag is used to indicate whether
     *	    processing is nested inside a {@link LayoutComponent}.  This is
     *	    important to know because the rules change when
     *	    <code>UIComponent</code>s become nested.  The
     *	    <code>ViewHandler</code> has control at the top level
     *	    (non-nested), but does not have control inside
     *	    <code>UIComponent</code>s.  When nested often a
     *	    <code>UIComponent</code> must be utilized instead of a
     *	    {@link LayoutElement}.  In a page situation, most tags will be
     *	    nested; when defining a <code>Renderer</code> most tags are not
     *	    likely to be nested.</p>
     *
     *	@param	ctx	The {@link ProcessingContext}
     *	@param	parent	The parent {@link LayoutElement}
     *	@param	nested	<code>true</code> if nested in a {@link LayoutComponent}
     */
    protected LayoutElement process(ProcessingContext ctx, LayoutElement parent, boolean nested) throws IOException {
	// Get the parser...
	TemplateParser parser = getTemplateParser();

	// Skip White Space...
	parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);

	ProcessingContextEnvironment env = new ProcessingContextEnvironment(
		this, parent, nested);
	int ch = parser.nextChar();
	String tmpstr = null;
	boolean finished = false;
	while (ch != -1) {
	    switch (ch) {
		case '<' :
		    parser.skipCommentsAndWhiteSpace(   // Skip white space
			parser.SIMPLE_WHITE_SPACE);
		    ch = parser.nextChar();
		    if (ch == '/') {
			// Closing tag
			parser.skipCommentsAndWhiteSpace(   // Skip white space
			    parser.SIMPLE_WHITE_SPACE);
			if (env.isSpecial()) {
			    // Check for special flag might have a '!'
			    ch = parser.nextChar();
			    // Ignore the '!' if there, if not push it back
			    if (ch == '!') {
				// Ignore '!', but skip white space after it
				parser.skipCommentsAndWhiteSpace(
				    parser.SIMPLE_WHITE_SPACE);
			    } else {
				// No optional '!', push back extra read char
				parser.unread(ch);
			    }
			}
			tmpstr = parser.readToken();
			ctx.endComponent(env, tmpstr);
			finished = true; // Indicate done with this context
			parser.skipCommentsAndWhiteSpace(   // Skip white space
			    parser.SIMPLE_WHITE_SPACE);
			ch = parser.nextChar(); // Throw away '>' character
			if (ch != '>') {
			    throw new SyntaxException(
				"While processing closing tag '</" + tmpstr
				+ "...' expected to encounter closing '>' but"
				+ " found '" + (char) ch + "' instead!");
			}
		    } else if (ch == '!') {
			// We have a reserved tag...
			env.setSpecial(true);
			ctx.beginSpecial(env, parser.readToken());
		    } else {
			// Open tag
			parser.unread(ch);
			ctx.beginComponent(env, parser.readToken());
		    }
		    break;
		case '\'' :
		    // Escape HTML
		    ctx.escapedStaticText(env, parser.readLine());
		    break;
		case '"' :
		    // Write output directly to stdout
		    ctx.staticText(env, parser.readLine());
		    break;
		default:
		    parser.unread(ch);
		    ctx.handleDefault(env, null);
	    }
	    if (finished) {
		// Done w/ this context...
		return parent;
	    }
	    parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);
	    ch = parser.nextChar();
	}

	// Return the LayoutDefinition
	return parent;
    }

    /**
     *	<p> This method is responsible for creating a
     *	    {@link LayoutComponent}.</p>
     *
     *	@param	type	The type of component to create.
     */
    private LayoutComponent createLayoutComponent(LayoutElement parent, boolean nested, String type) throws IOException {
	// Ensure type is defined
	ComponentType componentType = LayoutDefinitionManager.
	    getGlobalComponentType(type);
	if (componentType == null) {
	    throw new IllegalArgumentException("ComponentType '" + type
		    + "' not defined!");
	}

	// Get the rest of the properties (look for id)
	// We're processing "<type" continue until we find "[/]>".
	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	NameValuePair nvp = null;
	String id = null;
	String overwrite = null;
	int ch = 0;
	boolean single = false;
	TemplateParser parser = getTemplateParser();
	while (ch != -1) {
	    parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);
	    ch = parser.nextChar();
	    if (ch == '>') {
		// We're at the end of the parameters.
		break;
	    }
	    if (ch == '/') {
		parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);
		ch = parser.nextChar();
		if (ch != '>') {
		    throw new SyntaxException("'" + type + "' tag contained "
			+ "'/' that was not followed by a '>' character!");
		}

		// We're at the end of the parameters.
		single = true;
		break;
	    }
	    parser.unread(ch);
	    nvp = parser.getNVP();
	    if (nvp.getName().equals(ID_ATTRIBUTE)) {
		// Found id...
		id = nvp.getValue();
	    } else if (nvp.getName().equals(OVERWRITE_ATTRIBUTE)) {
		overwrite = nvp.getValue();
	    } else {
		// Found other parameter...
		nvps.add(nvp);
	    }
	}

	// Create the LayoutComponent
	if (id == null) {
	    id = getGeneratedId(type);
	}
	LayoutComponent component =
	    new LayoutComponent(parent, id, componentType);

	// Set Overwrite flag if needed
	if (overwrite != null) {
	    component.setOverwrite(Boolean.valueOf(overwrite).booleanValue());
	}

	// Set options...
	for (NameValuePair np : nvps) {
	    component.addOption(np.getName(), np.getValue());
	}

	// Set flag to indicate if this LayoutComponent is nested in another
	// LayoutComponent.  This is significant b/c during rendering, events
	// will need to be fired differently (the TemplateRenderer /
	// LayoutElements will not have any control).  The strategy used will
	// rely on "instance" handlers, this flag indicates that "instance"
	// handlers should be used.
	// NOTE: While this could be implemented on the LayoutComponent
	//	 itself, I decided not to for performance reasons and to
	//	 allow this value to be overruled if desired.
	component.setNested(nested);

	// Set facet id if needed
	checkForFacetChild(parent, component);

	// Let calling method see if this is a single tag, or if there should
	// be a closing tag as well
	parser.unread('>');
	if (single) {
	    parser.unread('/');
	}

	return component;
    }

    /**
     *	<p> This method checks to see if the given <code>component</code> is
     *	    sitting inside a facet or not.  If it is, it will use the facet
     *	    name for its id so that it will be found correctly.  However, if
     *	    the facet tag exists outside a component, then it is not a facet
     *	    -- its a place holder for a facet.  In this case it will not use
     *	    the id of the place holder.</p>
     */
    private static void checkForFacetChild(LayoutElement parent, LayoutComponent component) {
	// Figure out if this should be stored as a facet, if so under what id
	if (LayoutElementUtil.isLayoutComponentChild(component)) {
	    component.setFacetChild(false);
	} else {
	    // Need to add this so that it has the correct facet name
	    // Check to see if this LayoutComponent is inside a LayoutFacet
	    String id = component.getUnevaluatedId();
	    while (parent != null) {
		if (parent instanceof LayoutFacet) {
		    // Inside a LayoutFacet, use its id... only if this facet
		    // is a child of a LayoutComponent (otherwise, it is a
		    // layout facet used for layout, not for defining a facet
		    // of a UIComponent)
		    if (LayoutElementUtil.isLayoutComponentChild(parent)) {
			id = parent.getUnevaluatedId();
		    }
		    break;
		}
		parent = parent.getParent();
	    }

	    // Set the facet name
	    component.addOption(LayoutComponent.FACET_NAME, id);
	}
    }


    /**
     *	<p> This method adds child <code>LayoutElement</code>s.</p>
     *
     *	@param	layElt	The parent <code>LayoutElment</code>.
     */
    private void addChildLayoutElements(LayoutElement layElt, boolean nested) {
	/*
	// Get the child nodes
	Iterator it = getChildElements(node).iterator();

	// Walk children (we care about IF_ELEMENT, ATTRIBUTE_ELEMENT,
	// MARKUP_ELEMENT, FACET_ELEMENT, STATIC_TEXT_ELEMENT,
	// COMPONENT_ELEMENT, EVENT_ELEMENT, FOREACH_ELEMENT, EDIT_ELEMENT, and
	// WHILE_ELEMENT)
	Node childNode = null;
	String name = null;
	while (it.hasNext()) {
	    childNode = (Node) it.next();
	    name = childNode.getNodeName();
	    if (name.equalsIgnoreCase(IF_ELEMENT)) {
		// Found a IF_ELEMENT
		layElt.addChildLayoutElement(
		    createLayoutIf(layElt, childNode));
	    } else if (name.equalsIgnoreCase(ATTRIBUTE_ELEMENT)) {
		// Found a ATTRIBUTE_ELEMENT
		LayoutElement childElt =
		    createLayoutAttribute(layElt, childNode);
		if (childElt != null) {
		    layElt.addChildLayoutElement(childElt);
		}
	    } else if (name.equalsIgnoreCase(MARKUP_ELEMENT)) {
		// Found a MARKUP_ELEMENT
		layElt.addChildLayoutElement(
		    createLayoutMarkup(layElt, childNode));
	    } else if (name.equalsIgnoreCase(FACET_ELEMENT)) {
		// Found a FACET_ELEMENT
		layElt.addChildLayoutElement(
		    createLayoutFacet(layElt, childNode));
	    } else if (name.equalsIgnoreCase(STATIC_TEXT_ELEMENT)) {
		// Found a STATIC_TEXT_ELEMENT
		layElt.addChildLayoutElement(
		    createLayoutStaticText(layElt, childNode));
	    } else if (name.equalsIgnoreCase(COMPONENT_ELEMENT)) {
		// Found a COMPONENT_ELEMENT
		layElt.addChildLayoutElement(
		    createLayoutComponent(layElt, childNode));
	    } else if (name.equalsIgnoreCase(EVENT_ELEMENT)) {
		// Found a EVENT_ELEMENT
		// Get the event type
		name = (String) getAttributes(childNode).
		    get(TYPE_ATTRIBUTE);
		// Set the Handlers for the given event type (name)
		List handlers = layElt.getHandlers(name);
		layElt.setHandlers(name, getHandlers(childNode, handlers));
	    } else if (name.equalsIgnoreCase(FOREACH_ELEMENT)) {
		// Found a FOREACH_ELEMENT
		layElt.addChildLayoutElement(
		    createLayoutForEach(layElt, childNode));
	    } else if (name.equalsIgnoreCase(WHILE_ELEMENT)) {
		// Found a WHILE_ELEMENT
		layElt.addChildLayoutElement(
		    createLayoutWhile(layElt, childNode));
	    } else {
		throw new RuntimeException("Unknown Element Found: '"
			+ childNode.getNodeName() + "' under '"
			+ node.getNodeName() + "'.");
	    }
	}
	*/
    }

    /**
     *	<p> This method creates a <code>List</code> of {@link Handler}s from
     *	    the provided <code>Node</code>.  It will look at the child
     *	    <code>Element</code>s for {@link HANDLER_ELEMENT} elements.  When
     *	    found, it will create a new {@link Handler} object and add it to a
     *	    <code>List</code> that is created internally.  This
     *	    <code>List</code> is returned.</p>
     *
     *	@param	node	    <code>Node</code> containing
     *			    {@link HANDLER_ELEMENT} elements.
     *	@param	handlers    <code>List</code> of existing {@link Handler}s.
     *
     *	@return	A <code>List</code> of {@link Handler} objects, empty
     *		<code>List</code> if no {@link Handler}s found
    private List getHandlers(Node node, List handlers) {
	// Get the child nodes
	Iterator it = getChildElements(node, HANDLER_ELEMENT).iterator();

	// Walk children (we only care about HANDLER_ELEMENT)
	if (handlers == null) {
	    handlers = new ArrayList();
	}
	while (it.hasNext()) {
	    // Found a HANDLER_ELEMENT
	    handlers.add(createHandler((Node) it.next()));
	}

	// Return the handlers
	return handlers;
    }
     */

    /**
     *	<p> This method creates a {@link Handler} from the given handler
     *	    <code>Node</code>.  It will add input and/or output mappings
     *	    specified by any child Elements named {@link INPUT_ELEMENT} or
     *	    {@link OUTPUT_MAPPING_ELEMENT}.</p>
     *
     *	@param	handlerNode	The <code>Node</code> describing the
     *				{@link Handler} to be created.
     *
     *	@return	The newly created {@link Handler}.
    private Handler createHandler(Node handlerNode) {
	// Pull off attributes...
	String id = (String) getAttributes(handlerNode).
	    get(ID_ATTRIBUTE);
	if ((id == null) || (id.trim().equals(""))) {
	    throw new RuntimeException("'" + ID_ATTRIBUTE
		    + "' attribute not found on '" + HANDLER_ELEMENT
		    + "' Element!");
	}

	// Find the HandlerDefinition associated with this Handler
	HandlerDefinition handlerDef = LayoutDefinitionManager.getGlobalHandlerDefinition.get(id);
	if (handlerDef == null) {
	    throw new IllegalArgumentException(HANDLER_ELEMENT + " elements "
		    + ID_ATTRIBUTE + " attribute must match the "
		    + ID_ATTRIBUTE + " attribute of a "
		    + HANDLER_DEFINITION_ELEMENT + ".  A HANDLER_ELEMENT with '"
		    + id + "' was specified, however there is no cooresponding "
		    + HANDLER_DEFINITION_ELEMENT + " with a matching "
		    + ID_ATTRIBUTE + " attribute.");
	}

	// Create new Handler
	Handler handler =  new Handler(handlerDef);

	// Add the inputs
	Map attributes = null;
	Node inputNode = null;
	Iterator it = getChildElements(handlerNode, INPUT_ELEMENT).iterator();
	while (it.hasNext()) {
	    // Processing an INPUT_ELEMENT
	    inputNode = (Node) it.next();
	    attributes = getAttributes(inputNode);
	    handler.setInputValue(
		(String) attributes.get(NAME_ATTRIBUTE),
		getValueFromNode(inputNode, attributes));
	}

	// Add the OutputMapping objects
	it = getChildElements(handlerNode, OUTPUT_MAPPING_ELEMENT).iterator();
	while (it.hasNext()) {
	    // Processing an OUTPUT_MAPPING_ELEMENT
	    attributes = getAttributes((Node) it.next());
	    handler.setOutputMapping(
		(String) attributes.get(OUTPUT_NAME_ATTRIBUTE),
		(String) attributes.get(TARGET_KEY_ATTRIBUTE),
		(String) attributes.get(TARGET_TYPE_ATTRIBUTE));
	}

	// Return the newly created handler
	return handler;
    }
     */

    /**
     *	<p> This method creates a new {@link LayoutIf}
     *	    {@link LayoutElement}.</p>
     *
     *	@param	parent	The parent {@link LayoutElement}.
     *	@param	node	The {@link IF_ELEMENT} node to extract information from
     *			when creating the {@link LayoutIf}
    private LayoutElement createLayoutIf(LayoutElement parent, Node node) {
	// Pull off attributes...
	String condition = (String) getAttributes(node).get(
	    CONDITION_ATTRIBUTE);
	if ((condition == null) || (condition.trim().equals(""))) {
	    throw new RuntimeException("'" + CONDITION_ATTRIBUTE
		    + "' attribute not found on '" + IF_ELEMENT + "' Element!");
	}

	// Create new LayoutIf
	LayoutElement ifElt =  new LayoutIf(parent, condition);

	// Add children...
	addChildLayoutElements(ifElt, node);

	// Return the if
	return ifElt;
    }
     */

    /**
     *	<p> This method creates a new {@link LayoutForEach}
     *	    {@link LayoutElement}.</p>
     *
     *	@param	parent	The parent {@link LayoutElement}.
     *	@param	node	The {@link #FOREACH_ELEMENT} node to extract
     *			information from when creating the
     *			{@link LayoutForEach}.
     *
     *	@return The new {@link LayoutForEach} {@link LayoutElement}.
    private LayoutElement createLayoutForEach(LayoutElement parent, Node node) {
	// Pull off attributes...
	String list = (String) getAttributes(node).get(
	    LIST_ATTRIBUTE);
	if ((list == null) || (list.trim().equals(""))) {
	    throw new RuntimeException("'" + LIST_ATTRIBUTE
		    + "' attribute not found on '" + FOREACH_ELEMENT
		    + "' Element!");
	}
	String key = (String) getAttributes(node).get(
	    KEY_ATTRIBUTE);
	if ((key == null) || (key.trim().equals(""))) {
	    throw new RuntimeException("'" + KEY_ATTRIBUTE
		    + "' attribute not found on '" + FOREACH_ELEMENT
		    + "' Element!");
	}

	// Create new LayoutForEach
	LayoutElement forEachElt =  new LayoutForEach(parent, list, key);

	// Add children...
	addChildLayoutElements(forEachElt, node);

	// Return the forEach
	return forEachElt;
    }
     */

    /**
     *	<p> This method creates a new {@link LayoutWhile}
     *	    {@link LayoutElement}.</p>
     *
     *	@param	parent	The parent {@link LayoutElement}.
     *	@param	node	The {@link #WHILE_ELEMENT} node to extract information
     *			from when creating the LayoutWhile.
     *
     *	@return The new {@link LayoutWhile} {@link LayoutElement}.
    private LayoutElement createLayoutWhile(LayoutElement parent, Node node) {
	// Pull off attributes...
	String condition = (String) getAttributes(node).get(
	    CONDITION_ATTRIBUTE);
	if ((condition == null) || (condition.trim().equals(""))) {
	    throw new RuntimeException("'" + CONDITION_ATTRIBUTE
		    + "' attribute not found on '" + WHILE_ELEMENT
		    + "' Element!");
	}

	// Create new LayoutWhile
	LayoutElement whileElt =  new LayoutWhile(parent, condition);

	// Add children...
	addChildLayoutElements(whileElt, node);

	// Return the while
	return whileElt;
    }
     */

    /**
     *
     *
     *	@param	parent	The parent {@link LayoutElement}.
     *	@param	node	The {@link #ATTRIBUTE_ELEMENT} node to extract
     *			information from when creating the
     *			{@link LayoutAttribute}
    private LayoutElement createLayoutAttribute(LayoutElement parent, Node node) {
	// Pull off attributes...
	Map attributes = getAttributes(node);
	String name = (String) attributes.get(NAME_ATTRIBUTE);
	if ((name == null) || (name.trim().equals(""))) {
	    throw new RuntimeException("'" + NAME_ATTRIBUTE
		    + "' attribute not found on '" + ATTRIBUTE_ELEMENT
		    + "' Element!");
	}
	LayoutElement attributeElt = null;

	// Check if we're setting this on a LayoutComponent vs. LayoutMarkup
	// Do this after checking for "name" to show correct error message
	LayoutComponent comp = null;
	if (parent instanceof LayoutComponent) {
	    comp = (LayoutComponent) parent;
	} else {
	    comp = getParentLayoutComponent(parent);
	}
	if (comp != null) {
	    // Treat this as a LayoutComponent "option" instead of "attribute"
	    addOption(comp, node);
	} else {
	    String value = (String) attributes.get(VALUE_ATTRIBUTE);
	    String property = (String) attributes.get(PROPERTY_ATTRIBUTE);

	    // Create new LayoutAttribute
	    attributeElt = new LayoutAttribute(parent, name, value, property);

	    // Add children... (event children are supported)
	    addChildLayoutElements(attributeElt, node);
	}

	// Return the LayoutAttribute (or null if inside LayoutComponent)
	return attributeElt;
    }
     */

    /**
     *	<p> This method creates a new {@link LayoutMarkup}.
     *
     *	@param	parent	The parent {@link LayoutElement}.
     *	@param	node	The {@link MARKUP_ELEMENT} node to extract information
     *			from when creating the {@link LayoutMarkup}.
    private LayoutElement createLayoutMarkup(LayoutElement parent, Node node) {
	// Pull off attributes...
	Map attributes = getAttributes(node);
	String tag = (String) attributes.get(TAG_ATTRIBUTE);
	if ((tag == null) || (tag.trim().equals(""))) {
	    throw new RuntimeException("'" + TAG_ATTRIBUTE
		    + "' attribute not found on '" + MARKUP_ELEMENT
		    + "' Element!");
	}

	// Check to see if this is inside a LayoutComponent, if so, we must
	// use a LayoutComponent for it to get rendered
	LayoutElement markupElt = null;
	if ((parent instanceof LayoutComponent)
		|| LayoutElementUtil.isNestedLayoutComponent(parent)) {
	    // Make a "markup" LayoutComponent..
	    ComponentType type = ensureMarkupType(parent);
	    markupElt = new LayoutComponent(
		    parent, MARKUP_ELEMENT + _markupCount++, type);
	    LayoutComponent markupComp = ((LayoutComponent) markupElt);
	    markupComp.addOption("tag", tag);
	    markupComp.setNested(true);
	    markupComp.setFacetChild(false);

	    // Add children...
	    addChildLayoutComponentChildren(markupComp, node);
	} else {
	    // Create new LayoutMarkup
	    String type = (String) attributes.get(TYPE_ATTRIBUTE);
	    markupElt =  new LayoutMarkup(parent, tag, type);

	    // Add children...
	    addChildLayoutElements(markupElt, node);
	}

	// Return the LayoutMarkup
	return markupElt;
    }
     */

    /**
     *	<p> This method is responsible for Creating a {@link LayoutFacet}
     *	    {@link LayoutElement}.</p>
     *
     *	@param	parent	The parent {@link LayoutElement}.
     *	@param	node	The {@link #FACET_ELEMENT} node to extract information
     *			from when creating the {@link LayoutFacet}.
     *
     *	@return	The new {@link LayoutFacet} {@link LayoutElement}.
    private LayoutElement createLayoutFacet(LayoutElement parent, Node node) {
	// Pull off attributes...
	// id
	String id = (String) getAttributes(node).get(ID_ATTRIBUTE);
	if ((id == null) || (id.trim().equals(""))) {
	    throw new RuntimeException("'" + ID_ATTRIBUTE
		    + "' attribute not found on '" + FACET_ELEMENT
		    + "' Element!");
	}

	// Create new LayoutFacet
	LayoutFacet facetElt =  new LayoutFacet(parent, id);

	// Set isRendered
	String rendered = (String) getAttributes(node).get(RENDERED_ATTRIBUTE);
	boolean isRendered = true;
	if ((rendered == null) || rendered.trim().equals("")
		|| rendered.equals(AUTO_RENDERED)) {
	    // Automatically determine if this LayoutFacet should be rendered
	    isRendered = !LayoutElementUtil.isNestedLayoutComponent(facetElt);
	} else {
	    isRendered = Boolean.getBoolean(rendered);
	}
	facetElt.setRendered(isRendered);

	// Add children...
	addChildLayoutElements(facetElt, node);

	// Return the LayoutFacet
	return facetElt;
    }
     */

    /**
     *
     *	@param	node	The {@link #COMPONENT_ELEMENT} node to extract
     *			information from when creating the
     *			{@link LayoutComponent}.
    private LayoutElement createLayoutComponent(LayoutElement parent, Node node) {
	// Pull off attributes...
	Map attributes = getAttributes(node);
	String id = (String) attributes.get(ID_ATTRIBUTE);
	String type = (String) attributes.get(TYPE_ATTRIBUTE);
	if ((type == null) || (type.trim().equals(""))) {
	    throw new RuntimeException("'" + TYPE_ATTRIBUTE
		    + "' attribute not found on '" + COMPONENT_ELEMENT
		    + "' Element!");
	}

	// Create new LayoutComponent
	ComponentType componentType = getGlobalComponentType(type);
	if (componentType == null) {
	    throw new IllegalArgumentException("ComponentType '" + type
		    + "' not defined!");
	}
	LayoutComponent component =  new LayoutComponent(parent, id, type);

	// Check for overwrite flag
	String overwrite = (String) attributes.get(OVERWRITE_ATTRIBUTE);
	if ((overwrite != null) && (overwrite.length() > 0)) {
	    component.setOverwrite(Boolean.valueOf(overwrite).booleanValue());
	}

	// Set flag to indicate if this LayoutComponent is nested in another
	// LayoutComponent.  This is significant b/c during rendering, events
	// will need to be fired differently (the TemplateRenderer /
	// LayoutElements will not have any control).  The strategy used will
	// rely on "instance" handlers, this flag indicates that "instance"
	// handlers should be used.
	// NOTE: While this could be implemented on the LayoutComponent
	//	 itself, I decided not to for performance reasons and to
	//	 allow this value to be overruled if desired.
	component.setNested(LayoutElementUtil.isNestedLayoutComponent(component));

	// Figure out if this should be stored as a facet, if so under what id
	if (isLayoutComponentChild(component)) {
	    component.setFacetChild(false);
	} else {
	    // Need to add this so that it has the correct facet name
	    // Check to see if this LayoutComponent is inside a LayoutFacet
	    while (parent != null) {
		if (parent instanceof LayoutFacet) {
		    // Inside a LayoutFacet, use its id... only if this facet
		    // is a child of a LayoutComponent (otherwise, it is a
		    // layout facet used for layout, not for defining a facet
		    // of a UIComponent)
		    if (isLayoutComponentChild(parent)) {
			id = parent.getUnevaluatedId();
		    }
		    break;
		}
		parent = parent.getParent();
	    }
	    // Set the facet name
	    component.addOption(LayoutComponent.FACET_NAME, id);
	}

	// Add children... (different for component LayoutElements)
	addChildLayoutComponentChildren(component, node);

	// Return the LayoutComponent
	return component;
    }
     */

    /**
     *
    private void addChildLayoutComponentChildren(LayoutComponent component, Node node) {
	// Get the child nodes
	Iterator it = getChildElements(node).iterator();

	// Walk children (we care about COMPONENT_ELEMENT, FACET_ELEMENT,
	// OPTION_ELEMENT, EVENT_ELEMENT, MARKUP_ELEMENT, and EDIT_ELEMENT)
	Node childNode = null;
	String name = null;
	while (it.hasNext()) {
	    childNode = (Node) it.next();
	    name = childNode.getNodeName();
	    if (name.equalsIgnoreCase(COMPONENT_ELEMENT)) {
		// Found a COMPONENT_ELEMENT
		component.addChildLayoutElement(
			createLayoutComponent(component, childNode));
	    } else if (name.equalsIgnoreCase(FACET_ELEMENT)) {
		// Found a FACET_ELEMENT
		component.addChildLayoutElement(
			createLayoutFacet(component, childNode));
	    } else if (name.equalsIgnoreCase(OPTION_ELEMENT)) {
		// Found a OPTION_ELEMENT
		addOption(component, childNode);
	    } else if (name.equalsIgnoreCase(EVENT_ELEMENT)) {
		// Found a EVENT_ELEMENT
		// Get the event type
		name = (String) getAttributes(childNode).
		    get(TYPE_ATTRIBUTE);

		// Set the Handlers for the given event type (name)
		List handlers = component.getHandlers(name);
		component.setHandlers(name, getHandlers(childNode, handlers));
	    } else if (name.equalsIgnoreCase(MARKUP_ELEMENT)) {
		// Found an MARKUP_ELEMENT
		component.addChildLayoutElement(
			createLayoutMarkup(component, childNode));
	    } else if (name.equalsIgnoreCase(ATTRIBUTE_ELEMENT)) {
		// Found a ATTRIBUTE_ELEMENT (actually in this case it will
		// just add an "option" to the LayoutComponent), technically
		// this case should only happen for LayoutMarkup components...
		// this mess is caused by trying to support 2 .dtd's w/ 1 .dtd
		// file... perhaps it's time to split.
		createLayoutAttribute(component, childNode);
	    } else {
		throw new RuntimeException("Unknown Element Found: '"
			+ childNode.getNodeName() + "' under '"
			+ COMPONENT_ELEMENT + "'.");
	    }
	}
    }
     */

    /**
     *	<p> This method ensures that a "markup" {@link ComponentType} has been
     *	    defined so that it can be used implicitly.</p>
    private ComponentType ensureMarkupType(LayoutElement elt) {
	// See if it is defined
	ComponentType type = getGlobalComponentType(MARKUP_ELEMENT);
	if (type == null) {
	    // Nope, define it...
	    type = new ComponentType(MARKUP_ELEMENT, MARKUP_FACTORY_CLASS);
	    elt.getLayoutDefinition().addComponentType(type);
	}

	// Return the type
	return type;
    }
     */

    /**
     *	<p> This method adds an option to the given {@link LayoutComponent}
     *	    based on the information in the given {@link #OPTION_ELEMENT}
     *	    <code>Node</code>.</p>
     *
     *	@param	component   The {@link LayoutComponent}.
     *	@param	node	    The {@link #OPTION_ELEMENT} <code>Node</code>.
    private void addOption(LayoutComponent component, Node node) {
	// Pull off the attributes
	Map attributes = getAttributes(node);

	// Get the name
	String name = (String) attributes.get(NAME_ATTRIBUTE);
	if ((name == null) || (name.trim().equals(""))) {
	    throw new RuntimeException("'" + NAME_ATTRIBUTE
		    + "' attribute not found on '" + OPTION_ELEMENT
		    + "' Element!");
	}
	name = name.trim();

	// Get the value
	Object value = getValueFromNode(node, attributes);

	// Add the option to the component (value may be null)
	component.addOption(name, value);
    }
     */

    /**
     *	<p> This method reads obtains the {@link #VALUE_ATTRIBUTE} from the
     *	    given node, or from the child {@link #LIST_ELEMENT} element.  If
     *	    neither are provided, <code>(null)</code> is returned.  The
     *	    attribute takes precedence over the child {@link #LIST_ELEMENT}
     *	    element.</p>
     *
     *	@param	node	    <code>Node</code> containing the value attribute
     *			    or {@link #LIST_ELEMENT}
     *	@param	attributes  <code>Map</code> of attributes which may contain
     *			    {@link #VALUE_ATTRIBUTE}
     *
     *	@return	The value (as a <code>String</code> or <code>List</code>), or
     *		<code>(null)</code> if not specified.
    private Object getValueFromNode(Node node, Map attributes) {
	Object value = attributes.get(VALUE_ATTRIBUTE);
	if (value == null) {
	    // The value attribute may be null if multiple values are supplied.
	    // Walk children (we only care about LIST_ELEMENT)
	    List list = new ArrayList();
	    Iterator it = getChildElements(node, LIST_ELEMENT).iterator();
	    while (it.hasNext()) {
		// Add a value to the List
		list.add(getAttributes((Node) it.next()).
		    get(VALUE_ATTRIBUTE));
	    }
	    if (list.size() > 0) {
		// Only use the list if it has values
		value = list;
	    }
	}
	return value;
    }
     */

    /**
     *
     *	@param	node	The {@link #STATIC_TEXT_ELEMENT} node to extract
     *			information from when creating the
     *			{@link LayoutStaticText}.
    private LayoutElement createLayoutStaticText(LayoutElement parent, Node node) {
	// Create new LayoutComponent
	LayoutStaticText text =
	    new LayoutStaticText(parent, "", getTextNodesAsString(node));

	// Add all the attributes from the static text as options
//	component.addOptions(getAttributes(node));

	// Add escape... FIXME

	// Return the LayoutStaticText
	return text;
    }
     */



    //////////////////////////////////////////////////////////////////////
    //	Utility Methods
    //////////////////////////////////////////////////////////////////////

    /**
     *	<p> This method returns a <code>List</code> of all child
     *	    <code>Element</code>s below the given <code>Node</code>.</p>
     *
     *	@param	node	The <code>Node</code> to pull child elements from.
     *
     *	@return	<code>List</code> of child <code>Element</code>s found below
     *		the given <code>Node</code>.
    public List getChildElements(Node node) {
	return getChildElements(node, null);
    }
     */

    /**
     *	<p> This method returns a List of all child Elements below the given
     *	    Node matching the given name.  If name equals null, all Elements
     *	    below this node will be returned.</p>
     *
     *	@param	node	The node to pull child elements from.
     *	@param	name	The name of the Elements to return.
     *
     *	@return	List of child elements found below the given node matching
     *		the name (if provided).
    public List getChildElements(Node node, String name) {
	// Get the child nodes
	NodeList nodes = node.getChildNodes();
	if (nodes == null) {
	    // No children, just return an empty List
	    return new ArrayList(0);
	}

	// Create a new List to store the child Elements
	List list = new ArrayList();

	// Add all the child Elements to the List
	Node childNode = null;
	for (int idx = 0; idx < nodes.getLength(); idx++) {
	    childNode = nodes.item(idx);
	    if (childNode.getNodeType() != Node.ELEMENT_NODE) {
		// Skip TEXT_NODE and other Node types
		continue;
	    }

	    // Add to the list if name is null, or it matches the node name
	    if ((name == null) || childNode.getNodeName().equalsIgnoreCase(name)) {
		list.add(childNode);
	    }
	}

	// Return the list of Elements
	return list;
    }
     */


    /**
     *	<p> This method returns the <code>String</code> representation of all
     *	    the <code>Node.TEXT_NODE</code> nodes that are children of the
     *	    given <code>Node</code>.
     *
     *	@param	node	The <code>Node</code> to pull child
     *			<code>Element</code>s from.
     *
     *	@return	The <code>String</code> representation of all the
     *		<code>Node.TEXT_NODE</code> type nodes under the given
     *		<code>Node</code>.
    public String getTextNodesAsString(Node node) {
	// Get the child nodes
	NodeList nodes = node.getChildNodes();
	if (nodes == null) {
	    // No children, return null
	    return null;
	}

	// Create a StringBuffer
	StringBuffer buf = new StringBuffer("");

	// Add all the child Element values to the StringBuffer
	Node childNode = null;
	for (int idx = 0; idx < nodes.getLength(); idx++) {
	    childNode = nodes.item(idx);
	    if ((childNode.getNodeType() != Node.TEXT_NODE)
		    && (childNode.getNodeType() != Node.CDATA_SECTION_NODE)) {
		// Skip all other Node types
		continue;
	    }
	    buf.append(childNode.getNodeValue());
	}

	// Return the String
	return buf.toString();
    }
     */



    /**
     *	<p> This method returns a <code>Map</code> of all attributes for the
     *	    given <code>Node</code>.  Each attribute name will be stored in the
     *	    <code>Map</code> in lower case so case can be ignored.</p>
     *
     *	@param	node	The node to pull attributes from.
     *
     *	@return	<code>Map</code> of attributes found on the given
     *		<code>Node</code>.
    public Map getAttributes(Node node) {
	// Get the attributes
	NamedNodeMap attributes = node.getAttributes();
	if ((attributes == null) || (attributes.getLength() == 0)) {
	    // No attributes, just return an empty Map
	    return new HashMap(0);
	}

	// Create a Map to contain the attributes
	Map map = new HashMap();

	// Add all the attributes to the Map
	Node attNode = null;
	for (int idx = 0; idx < attributes.getLength(); idx++) {
	    attNode = attributes.item(idx);
	    map.put(attNode.getNodeName().toLowerCase(),
		    attNode.getNodeValue());
	}

	// Return the map
	return map;
    }
     */

    /**
     *	<p> This method produces a generated ID.  It optionally uses the given
     *	    base as a prefix to the generated ID ({@link #DEFAULT_ID_BASE} is
     *	    used otherwise).  This implementation will generate an id that
     *	    contains a number between 1 and {@link #MAX_ID}.  Do not depend on
     *	    this implementation, it may change in the future.</p>
     */
    public static String getGeneratedId(String base) {
	if (base == null) {
	    base = DEFAULT_ID_BASE;
	} else {
	    base = base.trim();
	    if (base.equals("")) {
		base = DEFAULT_ID_BASE;
	    } else {
		StringBuffer buf = new StringBuffer();
		int lowch;
		for (int ch : base.toCharArray()) {
		    lowch = ch | 0x20;
		    if ((lowch >= 'a') && (lowch <= 'z')) {
			buf.append((char) ch);
		    } else {
			buf.append('_');
		    }
		}
		base = buf.toString();
	    }
	}
	return base + (_idNum++ % MAX_ID);
    }

    /**
     *	<p> This interface defines the operations that may be acted upon while
     *	    a "template" file is walked.  The intent is that this interface may
     *	    be implemented by the different "processing contexts" which occur
     *	    throught the template file.  This provides the opportunity for
     *	    context sensitive syntax in an easy to provide way.</p>
     */
    protected static interface ProcessingContext {
	/**
	 *  <p>	This is called when a component tag is found
	 *	(&lt;tagname ...).</p>
	 */
	void beginComponent(ProcessingContextEnvironment env, String content) throws IOException;

	/**
	 *  <p>	This is called when an end component tag is found
	 *	(&lt;/tagname ... or &lt;tagname ... /&gt;).</p>
	 */
	void endComponent(ProcessingContextEnvironment env, String content) throws IOException;

	/**
	 *  <p>	This is called when a special tag is found
	 *	(&lt;!tagname ...).</p>
	 */
	void beginSpecial(ProcessingContextEnvironment env, String content) throws IOException;

	/**
	 *  <p>	This is called when a special end tag is found
	 *	(&lt;/tagname ... or &lt;!tagname ... /&gt;).</p>
	 */
	void endSpecial(ProcessingContextEnvironment env, String content) throws IOException;

	/**
	 *  <p>	This is called when static text is found (").</p>
	 */
	void staticText(ProcessingContextEnvironment env, String content) throws IOException;

	/**
	 *  <p>	This is called when escaped static text is found (').  The
	 *	difference between this and staticText is that HTML is expected
	 *	to be escaped so the browser does not parse it.</p>
	 */
	void escapedStaticText(ProcessingContextEnvironment env, String content) throws IOException;

	/**
	 *  <p>This method is invoked when nothing else matches.</p>
	 */
	void handleDefault(ProcessingContextEnvironment env, String content) throws IOException;
    }

    /**
     *	<p> This class hold environmental information needed while a
     *	    {@link ProcessingContext} is processing.</p>
     */
    protected static class ProcessingContextEnvironment {
	public ProcessingContextEnvironment(TemplateReader reader, LayoutElement parent, boolean nested) {
	    _reader = reader;
	    _parent = parent;
	    _nested = nested;
	}

	/**
	 *  @return The <code>TemplateReader</code> instance.
	 */
	public TemplateReader getReader() {
	    return _reader;
	}

	/**
	 *  @return <code>true</code> if nested in a LayoutComponent.
	 */
	public boolean isNested() {
	    return _nested;
	}

	/**
	 *  @return The parent {@link LayoutElement}.
	 */
	public LayoutElement getParent() {
	    return _parent;
	}

	/**
	 *  <p>	This method marks the current <code>ProcessingContext</code>
	 *	as complete.</p>
	 */
	public void setFinished(boolean finished) {
	    _finished = finished;
	}

	/**
	 *  <p>	This method indicates if the current
	 *	<code>ProcessingContext</code> is still valid.</p>
	 */
	public boolean isFinished() {
	    return _finished;
	}

	/**
	 *  <p>	Being marked special, indicates a "special" command versus
	 *	a component.  In other words a tag that starts with a '!'
	 *	char.</p>
	 */
	public void setSpecial(boolean val) {
	    _special = val;
	}

	/**
	 *  <p>	Indicates if the current tag being processed is "special".</p>
	 */
	public boolean isSpecial() {
	    return _special;
	}


	boolean		_finished   = false;
	boolean		_special    = false;
	boolean		_nested	    = false;
	LayoutElement	_parent	    = null;
	TemplateReader	_reader	    = null;
    }

    /**
     *	<p> Since many contexts share common functionality (i.e. processing
     *	    static text output), it makes sense to have a base processing
     *	    context which may be specialized as needed.</p>
     */
    protected static class BaseProcessingContext implements ProcessingContext {
	/**
	 *  <p>	This is called when a component tag is found
	 *	(&lt;tagname ...).</p>
	 */
	public void beginComponent(ProcessingContextEnvironment env, String content) throws IOException {
	    // We have a UIComponent tag... first get the parser
	    // Skip white Space
	    TemplateReader reader = env.getReader();
	    reader.getTemplateParser().skipCommentsAndWhiteSpace(
		    TemplateParser.SIMPLE_WHITE_SPACE);

// tagStack.push(content);
	    // Create the LayoutComponent
	    LayoutElement parent = env.getParent();
	    LayoutComponent child = reader.createLayoutComponent(
		    parent, env.isNested(), content);
	    parent.addChildLayoutElement(child);

	    // See if this is a single tag or if there is a closing tag
	    boolean single = false;
	    TemplateParser parser = reader.getTemplateParser();
	    int ch = parser.nextChar();
	    if (ch == '/') {
		// Single Tag
		ch = parser.nextChar();  // Throw away '>'
		single = true;
	    }
	    if (ch != '>') {
		throw new SyntaxException(
		    "Expected '>' found '" + (char) ch + "'.");
	    }

	    if (single) {
		// This is also the end of the component in this case...
		endComponent(env, content);
	    } else {
		// Process child LayoutElements (recurse)
		reader.process(LAYOUT_COMPONENT_CONTEXT, child, true);
	    }
	}

	/**
	 *  <p>	This is called when an end component tag is found
	 *	(&lt;/tagname&gt; or &lt;tagname ... /&gt;).  Because it may
	 *	be called for either of the above syntaxes, the caller of this
	 *	method is responsible for maintaining the parser position, this
	 *	method (or its subclasses) should not effect the parser
	 *	position.</p>
	 */
	public void endComponent(ProcessingContextEnvironment env, String content) throws IOException {
// if (!tagStack.pop().equals(content)) {
//  throw SyntaxException(...);
// }
/*
		    ch = parser.nextChar();
// FIXME: ':' is no longer used to indicate resevered... ! is... don't require this on end tag???
		    if (ch == ':') {
			// We have an end reserved tag...
			str2 = parser.readToken();
		    } else {
			// We have an end UIComponent tag...
			parser.unread(ch);
// FIXME: pop UIcomponent name off stack
		    }
*/
	}

	/**
	 *  <p>	This is called when a special tag is found
	 *	(&lt;!tagname ...).</p>
	 */
	public void beginSpecial(ProcessingContextEnvironment env, String content) throws IOException {
//System.out.println("Reading Special: " + content);
// FIXME: Provide plugable way to add custom directives
	}

	/**
	 *  <p>	This is called when a special end tag is found
	 *	(&lt;/tagname ... or &lt;!tagname ... /&gt;).</p>
	 */
	public void endSpecial(ProcessingContextEnvironment env, String content) throws IOException {
	}

	/**
	 *  <p>	This is called when static text is found (").</p>
	 */
	public void staticText(ProcessingContextEnvironment env, String content) throws IOException {
	    LayoutElement parent = env.getParent();
	    LayoutElement child = null;
	    if (env.isNested()) {
		// Must create a LayoutComponent (for UIComponent tree)
		LayoutComponent component =
		    new LayoutComponent(parent, getGeneratedId("staticText"), STATIC_TEXT);
		component.addOption("value", content);
		component.setNested(true);
		checkForFacetChild(parent, component);
		child = component;
	    } else {
		// Don't need a LayoutComponent
		child = new LayoutStaticText(parent, "", content);
	    }
	    parent.addChildLayoutElement(child);
	}

	/**
	 *  <p>	This is called when escaped static text is found (').  The
	 *	difference between this and staticText is that HTML is expected
	 *	to be escaped so the browser does not parse it.</p>
	 */
	public void escapedStaticText(ProcessingContextEnvironment env, String content) throws IOException {
	    staticText(env, Util.htmlEscape(content));
	}

	/**
	 *  <p>This method is invoked when nothing else matches.</p>
	 *
	 *  <p>This implementation does nothing.</p>
	 */
	public void handleDefault(ProcessingContextEnvironment env, String content) throws IOException {
	    // The process() method unreads the character that sent us here, we
	    // don't want that to happen so read it here and ignore it.
	    env.getReader().getTemplateParser().nextChar();
//System.out.println("handleDefault: " + content);
	}
    }

    /**
     *	<p> This is the {@link ProcessingContext} for the
     *	    {@link LayoutDefinition}.</p>
     */
    protected static class LayoutDefinitionContext extends BaseProcessingContext {
    }

    /**
     *	<p> This is the {@link ProcessingContext} for
     *	    {@link LayoutComponent}s.</p>
     */
    protected static class LayoutComponentContext extends BaseProcessingContext {
	/**
	 *  <p>This method is invoked when nothing else matches.</p>
	 *
	 *  <p>This implementation uses this to store "body content".  This
	 *	content is used as the <code>value</code> of the component.
	 *	If a value is already set, then this content will be
	 *	ignored.</p>
	 */
	public void handleDefault(ProcessingContextEnvironment env, String content) throws IOException {
	    TemplateParser parser = env.getReader().getTemplateParser();
//System.out.println("handleDefault: " + parser.readUntil('<'));
// FIXME: **ignore comments and allow escaping**
	    String bodyContent = parser.readUntil('<', true);
	    parser.unread('<');
	}
    }

    //////////////////////////////////////////////////////////////////////
    //	Constants
    //////////////////////////////////////////////////////////////////////

    public static final String ATTRIBUTE_ELEMENT	    =
	"attribute";
    public static final String COMPONENT_ELEMENT	    =
	"component";
    public static final String COMPONENT_TYPE_ELEMENT	    =
	"componenttype";
    public static final String EDIT_ELEMENT		    =
	"edit";
    public static final String EVENT_ELEMENT		    =
	"event";
    public static final String FACET_ELEMENT		    =
	"facet";
    public static final String FOREACH_ELEMENT		    =
	"foreach";
    public static final String HANDLER_ELEMENT		    =
	"handler";
    public static final String HANDLERS_ELEMENT		    =
	"handlers";
    public static final String HANDLER_DEFINITION_ELEMENT   =
	"handlerdefinition";
    public static final String IF_ELEMENT		    =
	"if";
    public static final String INPUT_DEF_ELEMENT	    =
	"inputdef";
    public static final String INPUT_ELEMENT		    =
	"input";
    public static final String LAYOUT_DEFINITION_ELEMENT    =
	"layoutdefinition";
    public static final String LAYOUT_ELEMENT		    =
	"layout";
    public static final String LIST_ELEMENT		    =
	"list";
    public static final String MARKUP_ELEMENT		    =
	"markup";
    public static final String OPTION_ELEMENT		    =
	"option";
    public static final String OUTPUT_DEF_ELEMENT	    =
	"outputdef";
    public static final String OUTPUT_MAPPING_ELEMENT	    =
	"outputmapping";
    public static final String STATIC_TEXT_ELEMENT	    =
	"staticText";
    public static final String TYPES_ELEMENT		    =
	"types";
    public static final String RESOURCES_ELEMENT	    =
	"resources";
    public static final String RESOURCE_ELEMENT		    =
	"resource";
    public static final String WHILE_ELEMENT		    =
	"while";

    public static final String CLASS_NAME_ATTRIBUTE	    =
	"classname";
    public static final String CONDITION_ATTRIBUTE	    =
	"condition";
    public static final String DEFAULT_ATTRIBUTE	    =
	"default";
    public static final String DESCRIPTION_ATTRIBUTE	    =
	"description";
    public static final String EXTRA_INFO_ATTRIBUTE	    =
	"extrainfo";
    public static final String FACTORY_CLASS_ATTRIBUTE	    =
	"factoryclass";
    public static final String ID_ATTRIBUTE		    =
	"id";
    public static final String KEY_ATTRIBUTE		    =
	"key";
    public static final String LIST_ATTRIBUTE		    =
	"list";
    public static final String METHOD_NAME_ATTRIBUTE	    =
	"methodname";
    public static final String NAME_ATTRIBUTE		    =
	"name";
    public static final String OUTPUT_NAME_ATTRIBUTE	    =
	"outputname";
    public static final String OVERWRITE_ATTRIBUTE	    =
	"overwrite";
    public static final String PROPERTY_ATTRIBUTE	    =
	"property";
    public static final String RENDERED_ATTRIBUTE	    =
	"rendered";
    public static final String REQUIRED_ATTRIBUTE	    =
	"required";
    public static final String TAG_ATTRIBUTE		    =
	"tag";
    public static final String TARGET_KEY_ATTRIBUTE	    =
	"targetkey";
    public static final String TARGET_TYPE_ATTRIBUTE	    =
	"targettype";
    public static final String TYPE_ATTRIBUTE		    =
	"type";
    public static final String VALUE_ATTRIBUTE		    =
	"value";

    public static final String AUTO_RENDERED		    =
	"auto";

    public static final String MARKUP_FACTORY_CLASS	    =
	"com.sun.jsftemplating.component.factory.basic.MarkupFactory";

    public static final ComponentType STATIC_TEXT	    = 
	LayoutDefinitionManager.getGlobalComponentType("staticText");

    protected static final ProcessingContext LAYOUT_DEFINITION_CONTEXT	=
	new LayoutDefinitionContext();

    protected static final ProcessingContext LAYOUT_COMPONENT_CONTEXT	=
	new LayoutComponentContext();

    /**
     *	<p> This value represents the maximum number that is contained in an
     *	    auto generated id.  I intentionally did not make this final so that
     *	    if needed it can be tweaked at runtime.  However, I do not think
     *	    this will ever be necessary (id's can be specified, and this many
     *	    unspecified ids is unlikely to be needed on a single page!).</p>
     */
    public  static int	MAX_ID			= 0x00010000;
    private static int	_idNum			= 1;

    public static final String DEFAULT_ID_BASE	= "id";

    /**
     *	This is used to set the "value" option for static text fields.
     */
//    public static final String VALUE_OPTION	=   "value";

    private TemplateParser  _tpl		= null;

    /*
    private int		    _markupCount	= 1;
    */
}
