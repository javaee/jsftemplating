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
package com.sun.jsftemplating.component.factory.sun;

import java.util.ArrayList;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutStaticText;
import com.sun.jsftemplating.layout.event.CreateChildEvent;


/**
 *  <p>	This factory is responsible for instantiating a <code>TableRowGroup
 *	UIComponent</code> which supports a dynamic # of columns.</p>
 *
 *  <p>	The {@link ComponentType} id for this factory is:
 *	"sun:dynamicColumnRowGroup".</p>
 *
 *  <p>	This factory fires a "createChild" event for each column that it
 *	creates.  This allows the children of the column to be created in the
 *	event allowing for the page developer to customize this (otherwise
 *	only "StaticText" children will be created in the columns).  In the
 *	"createChild" event handler, you may return a <code>UIComponent</code>
 *	which will be used as the <code>UIComponent</code> for that column.
 *	You also have access to the column <code>UICompoent</code> and may add
 *	children directly to it if you wish.  The "data" value set in the
 *	<code>CreateChildEvent</code> object is the value passed in for the
 *	column.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("sun:dynamicColumnRowGroup")
public class DynamicColumnTableRowGroupFactory extends TableRowGroupFactory {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	desc	    The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>TableRowGroup</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent desc, UIComponent parent) {
	// Create the UIComponent
	UIComponent comp = super.create(context, desc, parent);
	String id = comp.getId();

	// Dynamically create the child columns...
	// First find all the column-specific properties
	ArrayList<String> colAttKeys = new ArrayList<String>();
	for (String key : desc.getOptions().keySet()) {
	    if (key.startsWith("column")) {
		colAttKeys.add(key);
	    }
	}

	// Make sure we have some...
	if (colAttKeys.size() == 0) {
	    // Nothing to do... hopefully there are table column children,
	    // otherwise nothing will happen
	    return comp;
	}

	// Now determine the # of columns
	List<Object> values = getColumnPropertyValues(
		context, desc, colAttKeys.get(0), parent);
	int size = values.size();

	// Create the LC's for the columns
	List<LayoutComponent> columns = new ArrayList<LayoutComponent>(size);
	ComponentType colType =
	    LayoutDefinitionManager.getGlobalComponentType(COLUMN_TYPE);
	for (int idx = 0; idx < size; idx++) {
	    LayoutComponent column = new LayoutComponent(
		desc, id + COLUMN_SEPERATOR + idx, colType);
	    columns.add(column);
	}

	// Loop through the properties to set on the columns
	for (String key : colAttKeys) {
	    if (key.equals(COLUMN_VALUE_KEY)) {
		// Skip the value as it is added as a child.
		continue;
	    }
	    values = getColumnPropertyValues(context, desc, key, parent);

	    // Loop through the columns
	    int idx = 0;
	    for (Object entry : values) {
		// Set the value of each property on each column
		columns.get(idx++).addOption(getColumnKey(key), entry);
	    }
	}

	// Create the columns...
	UIComponent columnComp = null;
	String value = null;
	Object eventVal = null;
	values = getColumnPropertyValues(context, desc, COLUMN_VALUE_KEY, parent);
	int idx = 0;
	ComponentUtil compUtil = ComponentUtil.getInstance(context);
	for (LayoutComponent columnDesc : columns) {
	    columnComp = compUtil.createChildComponent(
		    context, columnDesc, comp);

	    value = "" + values.get(idx++);
	    eventVal = desc.dispatchHandlers(context,
		    CreateChildEvent.EVENT_TYPE,
		    new CreateChildEvent(columnComp, value));

	    if ((eventVal != null) && (eventVal instanceof UIComponent)) {
		// Add the child created during the event.
		columnComp.getChildren().add((UIComponent) eventVal);
	    } else {
		// Create the column (value) child
		// The child of each TableColumn will be a LayoutStaticText for now...
		compUtil.createChildComponent(context,
			new LayoutStaticText(columnDesc,
			    columnDesc.getUnevaluatedId() + CHILD_SUFFIX, value),
			columnComp);
	    }
	}

	// Return the component
	return comp;
    }

    /**
     *	<p> This method obtains a <code>List</code> from the given
     *	    property name.  If the value is not a <code>List</code>,
     *	    it will throw an exception.</p>
     */
    private List<Object> getColumnPropertyValues(FacesContext context, LayoutComponent parentDesc, String propertyName, UIComponent parent) {
	Object val = parentDesc.getEvaluatedOption(context, propertyName, parent);
	if (val == null) {
	    throw new IllegalArgumentException(
		"DynamicColumnTableRowGroupFactory requires a valid '"
		+ propertyName + "' attribute, however one was not supplied!");
	}
	if (val instanceof String) {
	    // Only try to do this if we have a String (not for Lists)
	    if (ComponentUtil.getInstance(context).isValueReference((String) val)) {
// FIXME: resolve $x{y} values: Object newBinding = VariableResolver.resolveVariables(context, elt, parent, binding);??
// FIXME: I believe I have a util method for this (resolveValue?)
		ValueExpression ve =
		    context.getApplication().getExpressionFactory().
			createValueExpression(context.getELContext(),
				(String) val, Object.class);
		val = ve.getValue(context.getELContext());
	    }
	}
	if (!(val instanceof List)) {
	    throw new IllegalArgumentException(
		"DynamicColumnTableRowGroupFactory requires all 'column*' "
		+ "properties to resolve to an instance of a List. '"
		+ propertyName + "' is a '" + val.getClass().getName()
		+ "'!");
	}

	// Return the result
	return (List<Object>) val;
    }

    /**
     *	<p> This method removes the "column" prefix and lower-cases the first
     *	    letter of the property name.</p>
     */
    private String getColumnKey(String key) {
	return key.substring(COLUMN_LEN, COLUMN_LEN + 1).toLowerCase() +
	    key.substring(COLUMN_LEN + 1);
    }

    /**
     *	<p> This method is overriden to prevent setting properties that don't
     *	    apply to this component (but may apply to their children or to
     *	    the behavior of this factory).</p>
     */
    protected void setOption(FacesContext context, UIComponent comp, LayoutComponent desc, String key, Object value) {
	if (key.startsWith("column")) {
	    return;
	}
	super.setOption(context, comp, desc, key, value);
    }


    /**
     *	<p> The component type of the column component to create.</p>
     */
    public static final String	COLUMN_TYPE	=   "sun:tableColumn";
    public static final String	COLUMN_VALUE_KEY=   "columnValue";
    public static final String	COLUMN_SEPERATOR=   "col";
    public static final String	CHILD_SUFFIX	=   "child";
    public static final int	COLUMN_LEN	=   "column".length();
}
