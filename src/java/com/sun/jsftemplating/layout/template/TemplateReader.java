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
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutAttribute;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutFacet;
import com.sun.jsftemplating.layout.descriptors.LayoutForEach;
import com.sun.jsftemplating.layout.descriptors.LayoutIf;
import com.sun.jsftemplating.layout.descriptors.LayoutMarkup;
import com.sun.jsftemplating.layout.descriptors.LayoutWhile;
import com.sun.jsftemplating.layout.descriptors.Resource;
import com.sun.jsftemplating.util.LayoutElementUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


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
	if (_parserCmds == null) {
	    initCustomParserCommands();
	}
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
    public LayoutElement process(ProcessingContext ctx, LayoutElement parent, boolean nested) throws IOException {
	// Get the parser...
	TemplateParser parser = getTemplateParser();

	// Skip White Space...
	parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);

	ProcessingContextEnvironment env = new ProcessingContextEnvironment(
		this, parent, nested);
	int ch = parser.nextChar();
	String startTag = null;
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
			// Get the expected String
			if (isTagStackEmpty()) {
			    parser.skipCommentsAndWhiteSpace(
				parser.SIMPLE_WHITE_SPACE + '!');
			    throw new SyntaxException("Found end tag '</"
				+ parser.readToken()
				+ "...' but did not find matching begin tag!");
			}
			startTag = popTag();
			if ((startTag.length() > 0)
				&& (startTag.charAt(0) == '!')) {
			    // Check for special flag, might have a '!'
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
			    tmpstr = parser.readToken();
			    if (!startTag.contains(tmpstr)) {
				throw new SyntaxException(
				    "Expected to find closing tag '</"
				    + startTag + "...' but instead found '</'"
				    + ((ch == '!') ? '!' : "")
				    + tmpstr + "...'.");
			    }
			    ctx.endSpecial(env, tmpstr);
			} else {
			    tmpstr = parser.readToken();
			    if (!startTag.equals(tmpstr)) {
				throw new SyntaxException(
				    "Expected to find closing tag '</"
				    + startTag + "...' but instead found '</'"
				    + tmpstr + "...'.");
			    }
			    ctx.endComponent(env, tmpstr);
			}
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
			tmpstr = parser.readToken();
			pushTag("!" + tmpstr);
			ctx.beginSpecial(env, tmpstr);
		    } else {
			// Open tag
			parser.unread(ch);
			tmpstr = parser.readToken();
			pushTag(tmpstr);
			ctx.beginComponent(env, tmpstr);
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
     *	<p> This method is responsible for parsing and creating a
     *	    {@link LayoutComponent}.</p>
     *
     *	@param	parent	The parent {@link LayoutElement}.
     *	@param	nested	<code>true</code> if nested inside another
     *			{@link LayoutComponent}.
     *	@param	type	The type of component to create.
     */
    public LayoutComponent createLayoutComponent(LayoutElement parent, boolean nested, String type) throws IOException {
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

		// We're at the end of the parameters and the component
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
	    id = LayoutElementUtil.getGeneratedId(type, getNextIdNumber());
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
    public static void checkForFacetChild(LayoutElement parent, LayoutComponent component) {
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
     *	<p> This method removes a tag from the Stack.  This should be called
     *	    outside of <code>TemplateReader</code> when writing
     *	    {@link ProcessingContext} code and a tag starts and ends in a
     *	    single tag (i.e. &lt;tag /&gt;).  In other cases, it should be
     *	    handled within the </code>TemplateReader</code>.</p>
     */
    public String popTag() {
	return _tagStack.pop();
    }

    /**
     *	<p> This method exists because popTag() does, it likely doesn't have
     *	    much use outside of <code>TemplateReader</code>.</p>
     */
    public void pushTag(String tag) {
	_tagStack.push(tag);
    }

    /**
    /**
     *	<p> This method checks to see if the tag <code>Stack</code> is
     *	    empty.</p>
     */
    public boolean isTagStackEmpty() {
	return _tagStack.empty();
    }

    /**
     *	<p> This method returns the next ID number.  Calling this method will
     *	    increment the id number.</p>
     */
    public int getNextIdNumber() {
	return _idNumber++;
    }

    //////////////////////////////////////////////////////////////////////
    //	Utility Methods
    //////////////////////////////////////////////////////////////////////


    /**
     *	<p> This method provides access to registered
     *	    {@link CustomParserCommand}s.</p>
     */
    public static CustomParserCommand getCustomParserCommand(String id) {
	return _parserCmds.get(id);
    }

    /**
     *	<p> This method allows you to set a {@link CustomParserCommand}.</p>
     */
    public static void setCustomParserCommand(String id, CustomParserCommand command) {
	_parserCmds.put(id, command);
    }

    /**
     *	<p> This method initializes the {@link CustomParserCommand}s.</p>
     */
    protected static void initCustomParserCommands() {
	Map<String, CustomParserCommand> map =
	    new HashMap<String, CustomParserCommand>();
	map.put("if", new IfParserCommand());
	map.put("while", new WhileParserCommand());
	map.put("foreach", new ForeachParserCommand());
	map.put("facet", new FacetParserCommand());
// FIXME: Do initialization via @annotations??
	_parserCmds = map;
    }


    //////////////////////////////////////////////////////////////////////
    //	Inner Classes
    //////////////////////////////////////////////////////////////////////


    /**
     *	<p> This is the {@link ProcessingContext} for the
     *	    {@link LayoutDefinition}.</p>
     */
    protected static class LayoutDefinitionContext extends BaseProcessingContext {
    }

    /**
     *	<p> This is the {@link ProcessingContext} for
     *	    {@link LayoutIf}s.</p>
     */
    protected static class LayoutIfContext extends BaseProcessingContext {
    }

    /**
     *	<p> This is the {@link ProcessingContext} for
     *	    {@link LayoutForEach}es.</p>
     */
    protected static class LayoutForEachContext extends BaseProcessingContext {
    }

    /**
     *	<p> This is the {@link ProcessingContext} for
     *	    {@link LayoutWhile}s.</p>
     */
    protected static class LayoutWhileContext extends BaseProcessingContext {
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
// FIXME: **ignore comments and allow escaping**
// Store body content in env until end component, set as 'value' if value is not set?
	    String bodyContent = parser.readUntil('<', true);
	    parser.unread('<');
	}

	/**
	 *
	 */
	public void beginSpecial(ProcessingContextEnvironment env, String content) throws IOException {
	    super.beginSpecial(env, content);
	}
    }

    /**
     *	<p> This {@link CustomParserCommand} implementation processes event
     *	    declarations.</p>
     */
    public static class EventParserCommand implements CustomParserCommand {
	public void process(ProcessingContext ctx, ProcessingContextEnvironment env, String eventName) throws IOException {
	    String handlerId = null;
	    String target = null;
	    NameValuePair nvp = null;
	    HandlerDefinition def = null;
	    Handler handler = null;
	    ArrayList handlers = new ArrayList();
	    TemplateReader reader = env.getReader();
	    TemplateParser parser = reader.getTemplateParser();

	    // Read the Handler(s)...
	    parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);
	    int ch = parser.nextChar();
	    while ((ch != -1) && (ch != '/') && (ch != '>')) {
		// Get Handler ID / Definition
		parser.unread(ch);
		handlerId = parser.readToken();
		def = LayoutDefinitionManager.
		    getGlobalHandlerDefinition(handlerId);
		if (def == null) {
		    throw new SyntaxException("Handler '" + handlerId
			+ "' in event '" + eventName + "' is not declared!  "
			+ "Ensure the '@Handler' annotation has been defined "
			+ "on the handler Java method, that it has been "
			+ "compiled with the annotation processing tool, and "
			+ "that the resulting"
			+ " 'META-INF/jsftemplating/Handler.map' is located "
			+ "in your classpath (you may need to do a clean "
			+ "build).");
		}

		// Create a Handler
		handler = new Handler(def);
		handlers.add(handler);

		// Ensure we have an opening parenthesis
		parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);
		ch = parser.nextChar();
		if (ch != '(') {
		    throw new SyntaxException("While processing '<!" + eventName
			+ "...' the handler '" + handlerId
			+ "' was missing the '(' character!");
		}

		// Read NVP(s)...
		parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);
		ch = parser.nextChar();
		while ((ch != -1) && (ch != ')')) {
		    // Read NVP
		    parser.unread(ch);
		    nvp = parser.getNVP();
		    parser.skipCommentsAndWhiteSpace(
			parser.SIMPLE_WHITE_SPACE + ",;");
		    ch = parser.nextChar();

		    // Store the NVP..
		    target = nvp.getTarget();
		    if (target != null) {
			// We have an OutputMapping
			handler.setOutputMapping(
			    nvp.getName(), nvp.getValue(), target);
		    } else {
			// We have an Input
			handler.setInputValue(nvp.getName(), nvp.getValue());
		    }
		}

		// ';' or ',' characters may appear between handlers
		parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE + ",;");
		ch = parser.nextChar();
	    }
	    if (ch == -1) {
		// Make sure we didn't get to the end of the file
		throw new SyntaxException("Unexpected EOF encountered while "
		    + "parsing handlers for event '" + eventName + "'!");
	    }
	    if (ch == '>') {
// FIXME: Deal w/ nested Handlers
		// Until we allow Handlers w/i Handlers, throw an exception
		throw new SyntaxException("Handlers for event '" + eventName
		    + "' did not end with '/>' but instead ended with '>'!");
	    }
	    if (ch == '/') {
		// Make sure we have a "/>"...
		parser.skipCommentsAndWhiteSpace(parser.SIMPLE_WHITE_SPACE);
		ch = parser.nextChar();
		if (ch != '>') {
		    throw new SyntaxException("Expected '/>' a end of '"
			+ eventName + "' event.  But found '/"
			+ (char) ch + "'.");
		}
		reader.popTag();   // Get rid of this event tag from the Stack
		ctx.endSpecial(env, eventName);
	    }

	    // Set the Handlers on the parent...
	    env.getParent().setHandlers(eventName, handlers);
	}
    }

    /**
     *	<p> This {@link CustomParserCommand} handles "if" statements.  To
     *	    obtain the condition, it simply reads until it finds '&gt;'.  This
     *	    means '&gt;' must be escaped if it appears in the condition.</p>
     */
    public static class IfParserCommand implements CustomParserCommand {
	public void process(ProcessingContext ctx, ProcessingContextEnvironment env, String name) throws IOException {
	    // Get the condition for this if statement.  We simply read until
	    // we find '>'.  This means '>' must be escaped if it appears in
	    // the condition.
	    TemplateReader reader = env.getReader();
	    TemplateParser parser = reader.getTemplateParser();
	    String condition = parser.readUntil('>', true).trim();

	    // Create new LayoutIf
	    LayoutElement parent = env.getParent();
	    LayoutElement ifElt =  new LayoutIf(parent, condition);
	    parent.addChildLayoutElement(ifElt);

	    if (condition.endsWith("/")) {
		reader.popTag();  // Don't look for end tag
	    } else {
		// Process child LayoutElements (recurse)
		reader.process(
		    TemplateReader.LAYOUT_IF_CONTEXT, ifElt,
		    LayoutElementUtil.isLayoutComponentChild(ifElt));
	    }
	}
    }

    /**
     *	<p> This {@link CustomParserCommand} handles "while" statements. To
     *	    obtain the condition, it simply reads until it finds '&gt;'.  This
     *	    means '&gt;' must be escaped if it appears in the condition.</p>
     */
    public static class WhileParserCommand extends IfParserCommand {
	public void process(ProcessingContext ctx, ProcessingContextEnvironment env, String name) throws IOException {
	    // Get the condition for this while statement.  We simply read
	    // until we find '>'.  This means '>' must be escaped if it
	    // appears in the condition.
	    TemplateReader reader = env.getReader();
	    TemplateParser parser = reader.getTemplateParser();
	    String condition = parser.readUntil('>', true).trim();

	    // Create new LayoutWhile
	    LayoutElement parent = env.getParent();
	    LayoutElement elt =  new LayoutWhile(parent, condition);
	    parent.addChildLayoutElement(elt);

	    if (condition.endsWith("/")) {
		reader.popTag();  // Don't look for end tag
	    } else {
		// Process child LayoutElements (recurse)
		reader.process(
		    TemplateReader.LAYOUT_WHILE_CONTEXT, elt,
		    LayoutElementUtil.isLayoutComponentChild(elt));
	    }
	}
    }

    /**
     *	<p> This {@link CustomParserCommand} handles "foreach" statements.</p>
     *
     *	<p> The syntax must look like:</p>
     *
     *	<code>
     *	    <!foreach key : $something{something}>
     *		...
     *	    </!foreach>
     *	</code>
     *
     *	<p> The "key" is a <code>String</code> that will be used to store each
     *	    <code>Object</code> in a request attribute on each iteration.
     *	    $something{something} must resolve to a <code>List</code>.  It may
     *	    also be in the form <code>#{value.binding}</code> if you prefer,
     *	    in either case it must resolve to a <code>List</code>.</p>
     */
    public static class ForeachParserCommand implements CustomParserCommand {
	public void process(ProcessingContext ctx, ProcessingContextEnvironment env, String name) throws IOException {
	    TemplateReader reader = env.getReader();
	    TemplateParser parser = reader.getTemplateParser();

	    // First get the key
// FIXME: Don't do it this way... read until the '>' first then split the String.  This will allow detecting the missing ':' much more easily.
	    String key = parser.readUntil(':', true).trim();
	    String listExp = parser.readUntil('>', true).trim();

	    // Create new LayoutForEach
	    LayoutElement parent = env.getParent();
	    LayoutElement elt =  new LayoutForEach(parent, listExp, key);
	    parent.addChildLayoutElement(elt);

	    if (listExp.endsWith("/")) {
		reader.popTag();  // Don't look for end tag
	    } else {
		// Process child LayoutElements (recurse)
		reader.process(
		    TemplateReader.LAYOUT_FOREACH_CONTEXT, elt,
		    LayoutElementUtil.isLayoutComponentChild(elt));
	    }
	}
    }

    /**
     *	<p> This {@link CustomParserCommand} handles "facets".</p>
     */
    public static class FacetParserCommand implements CustomParserCommand {
	public void process(ProcessingContext ctx, ProcessingContextEnvironment env, String name) throws IOException {
// FIXME: TBD...
	    TemplateParser parser = env.getReader().getTemplateParser();
	    String content = parser.readUntil('>', true).trim();
	    if (content.endsWith("/")) {
		env.getReader().popTag();  // Don't look for end tag
	    }
	}
    }


    //////////////////////////////////////////////////////////////////////
    //	Constants
    //////////////////////////////////////////////////////////////////////

    public static final String FACET_ELEMENT		    =
	"facet";
    public static final String FOREACH_ELEMENT		    =
	"foreach";
    public static final String IF_ELEMENT		    =
	"if";
    public static final String LIST_ELEMENT		    =
	"list";
    public static final String MARKUP_ELEMENT		    =
	"markup";
    public static final String WHILE_ELEMENT		    =
	"while";

    public static final String ID_ATTRIBUTE		    =
	"id";
    public static final String OVERWRITE_ATTRIBUTE	    =
	"overwrite";

    public static final ProcessingContext LAYOUT_DEFINITION_CONTEXT	=
	new LayoutDefinitionContext();

    public static final ProcessingContext LAYOUT_COMPONENT_CONTEXT	=
	new LayoutComponentContext();

    public static final ProcessingContext LAYOUT_IF_CONTEXT		=
	new LayoutIfContext();

    public static final ProcessingContext LAYOUT_FOREACH_CONTEXT	=
	new LayoutForEachContext();

    public static final ProcessingContext LAYOUT_WHILE_CONTEXT		=
	new LayoutWhileContext();


    public static CustomParserCommand EVENT_PARSER_COMMAND =
	new EventParserCommand();

    private static Map<String, CustomParserCommand> _parserCmds	= null;

    /**
     *	<p> This <code>Stack</code> keep track of the nesting.</p>
     */
    private Stack<String> _tagStack = new Stack<String>();

    private TemplateParser  _tpl    = null;
    private int _idNumber	    = 1;
}
