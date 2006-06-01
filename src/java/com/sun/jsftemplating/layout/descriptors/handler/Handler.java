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
package com.sun.jsftemplating.layout.descriptors.handler;

import com.sun.jsftemplating.component.ComponentUtil;
import com.sun.jsftemplating.layout.event.UIComponentHolder;
import com.sun.jsftemplating.util.TypeConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;


/**
 *  <p>	This class contains the information necessary to invoke a Handler.  The
 *	{@link HandlerDefinition} class provides a definition of how to invoke
 *	a Handler, this class uses that information with in conjuction with
 *	information provided in this class to execute the <strong>handler
 *	method</strong>.  This class typically will hold input values and
 *	specify where output should be stored.</p>
 *
 *  <p>	The <strong>handler method</strong> to be invoked must have the
 *	following method signature:</p>
 *
 *  <p> <BLOCKQUOTE>
 *	    </CODE>
 *		public void beginDisplay(HandlerContext handlerCtx)
 *	    </CODE>
 *	</BLOCKQUOTE></p>
 *
 *  <p>	<code>void</code> above can return a value.  Depending on the type of
 *	event, return values may be handled differently.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class Handler implements java.io.Serializable {

    /**
     *	<p> Constructor.</p>
     */
    public Handler(HandlerDefinition handlerDef) {
	setHandlerDefinition(handlerDef);
    }

    /**
     *	<p> Accessor for the {@link HandlerDefinition}.</p>
     */
    public HandlerDefinition getHandlerDefinition() {
	return _handlerDef;
    }

    /**
     *	<p> This method sets the HandlerDefinition used by this Handler.</p>
     */
    protected void setHandlerDefinition(HandlerDefinition handler) {
	_handlerDef = handler;
    }

    /**
     *
     */
    public void setInputValue(String name, Object value) {
	_inputs.put(name, value);
    }

    /**
     *	<p> This method returns a Map of NVPs representing the input to this
     *	    handler.</p>
     */
    protected Map getInputMap() {
	return _inputs;
    }

    /**
     *	<p> This method simply returns the named input value, null if not
     *	    found.  It will not attempt to resolve $...{...} expressions or
     *	    do modifications of any kind.  If you are looking for a method to
     *	    do these types of operations, try:</p>
     *
     *		getInputValue(FacesContext, String).
     *
     *	@param	name	The name used to identify the input value.
     */
    public Object getInputValue(String name) {
	return _inputs.get(name);
    }

    /**
     *	<p> This method returns the value for the named input.  Input values
     *	    are not stored in this HandlerContext itself, but in the Handler.
     *	    If you are trying to set input values for a handler, you must
     *	    create a new Handler object and set its input values.</p>
     *
     *	<p> This method attempts to resolve $...{...} expressions.  It also
     *	    will return the default value if the value is null.  If you don't
     *	    want these things to happen, look at
     *	    Handler.getInputValue(String).</p>
     *
     *	@param	name	    The input name
     *
     *	@return	The value of the input (null if not found)
     */
    public Object getInputValue(HandlerContext ctx, String name) {
	// Make sure the requested name is valid
	IODescriptor inDesc = getHandlerDefinition().getInputDef(name);
	if (inDesc == null) {
	    throw new RuntimeException("Attempted to get input value '"
		    + name + "', however, this is not a declared input "
		    + "parameter in handler definition '"
		    + getHandlerDefinition().getId() + "'!  Check your handler "
		    + " and/or the XML (near LayoutElement '"
		    + ctx.getLayoutElement().getId(ctx.getFacesContext(), null)
		    + "')");
	}

	// Get the value, and parse it
	Object value = getInputValue(name);
	if (value == null) {
	    if (inDesc.isRequired()) {
		throw new RuntimeException("'" + name
			+ "' is required for handler '"
			+ getHandlerDefinition().getId() + "'!");
	    }
	    value = inDesc.getDefault();
	}

	// Resolve any expressions
	EventObject event = ctx.getEventObject();
	UIComponent component = null;
	if (event instanceof UIComponentHolder) {
	    component = ((UIComponentHolder) event).getUIComponent();
	} else {
	    Object src = event.getSource();
	    if (src instanceof UIComponent) {
		component = (UIComponent) src;
	    }
	}
	if ((value != null) && (value instanceof String)) {
	    value = ComponentUtil.resolveValue(ctx.getFacesContext(),
		    ctx.getLayoutElement(), component, "" + value);
	}

	// Make sure the value is the correct type...
	value = TypeConverter.asType(inDesc.getType(), value);

	return value;
    }

    /**
     *	<p> This method retrieves an output value.  Output values are stored
     *	    in the location specified by the {@link OutputType} in the
     *	    Handler.</p>
     *
     *	@param	context	    The HandlerContext
     *	@param	name	    The output name
     *
     *	@return	The value of the output (null if not set)
     */
    public Object getOutputValue(HandlerContext context, String name) {
	// Make sure the requested name is valid
	HandlerDefinition handlerDef = getHandlerDefinition();
	IODescriptor outIODesc = handlerDef.getOutputDef(name);
	if (outIODesc == null) {
	    throw new RuntimeException("Attempted to get output value '"
		    + name + "' from handler '" + handlerDef.getId()
		    + "', however, this is not a declared output parameter!  "
		    + "Check your handler and/or the XML.");
	}

	// Get the OutputMapping that describes how to store this output
	OutputMapping outputDesc = getOutput(name);

	// Return the value
	return outputDesc.getOutputType().
	    getValue(context, outIODesc, outputDesc.getOutputKey());
    }

    /**
     *	<p> This method stores an output value.  Output values are stored
     *	    as specified by the {@link OutputType} in the Handler.  This
     *	    method is not used to create the "mapping" of an output value,
     *	    for that see {@link #setOutputMapping(String, String, String)}.</p>
     *
     *	@param	context	    The HandlerContext
     *	@param	name	    The name the Handler uses for the output
     *	@param	value	    The value to set
     */
    public void setOutputValue(HandlerContext context, String name, Object value) {
	// Make sure the requested name is valid
	HandlerDefinition handlerDef = getHandlerDefinition();
	IODescriptor outIODesc = handlerDef.getOutputDef(name);
	if (outIODesc == null) {
	    throw new RuntimeException("Attempted to set output value '"
		    + name + "' from handler '" + handlerDef.getId()
		    + "', however, this is not a declared output parameter!  "
		    + "Check your handler and/or the XML.");
	}

	// Get the OutputMapping that describes how to store this output
	OutputMapping outputMapping = getOutput(name);
	if (outputMapping == null) {
	    // They did not Map the output, do nothing...
	    return;
	}

	// Make sure the value is the correct type...
	value = TypeConverter.asType(outIODesc.getType(), value);

	// Set the value
	EventObject event = context.getEventObject();
	UIComponent component = null;
	if (event instanceof UIComponentHolder) {
	    component = ((UIComponentHolder) event).getUIComponent();
	}
	outputMapping.getOutputType().setValue(
	    context, outIODesc, "" + ComponentUtil.resolveValue(
		context.getFacesContext(),
		context.getLayoutElement(),
		component,
		outputMapping.getOutputKey()), value);
    }

    /**
     *	<p> This method adds a new OutputMapping to this handler.  An
     *	    OutputMapping allows the handler to return a value and have it
     *	    "mapped" to the location of your choice.  The "outputType"
     *	    corresponds to a registered {@link OutputType}
     *	    (see {@link OutputTypeManager}).</p>
     *
     *	@param	outputName  The Handler's name for the output value
     *	@param	targetKey   The 'key' the OutputType uses to store the output
     *	@param	targetType  The OutputType implementation map the output
     */
    public void setOutputMapping(String outputName, String targetKey, String targetType) {
	// Ensure we have a valid outputName (check HandlerDefinition)
	if (getHandlerDefinition().getOutputDef(outputName) == null) {
	    throw new IllegalArgumentException("Handler named '"
		+ getHandlerDefinition().getId() + "' does not declare output "
		+ "mapping named '" + outputName + "'.");
	}

	// Ensure the data is trim
	if (targetKey != null) {
	    targetKey = targetKey.trim();
	    if (targetKey.length() == 0) {
		targetKey = null;
	    }
	}
	targetType = targetType.trim();

	try {
	    _outputs.put(outputName, new OutputMapping(
			outputName, targetKey, targetType));
	} catch (IllegalArgumentException ex) {
	    throw new RuntimeException(
		"Unable to create OutputMapping with given information: "
		+ "outputName='" + outputName
		+ "', targetKey='" + targetKey
		+ "', targetType=" + targetType + "'", ex);
	}
    }

    /**
     *
     */
    public OutputMapping getOutput(String name) {
	return (OutputMapping) _outputs.get(name);
    }

    /**
     *	<p> This method determines if the handler is static.</p>
     */
    public boolean isStatic() {
	return getHandlerDefinition().isStatic();
    }

    /**
     *
     */
    public Object invoke(HandlerContext handlerContext) throws InstantiationException, IllegalAccessException, InvocationTargetException {
	Object retVal = null;
	HandlerDefinition handlerDef = getHandlerDefinition();
	Method method = handlerDef.getHandlerMethod();

	// First execute all child handlers
	// A copy is provided of the HandlerContext to avoid the Handler being
	// changed before we execute this Handler.
	Object result = handlerContext.getLayoutElement().dispatchHandlers(
		new HandlerContextImpl(handlerContext),
		handlerDef.getChildHandlers());

	// Only attempt to do this if there is a handler method, there
	// might only be child handlers
	if (method != null) {
	    Object instance = null;
	    if (!isStatic()) {
		// Get the class that contains the method
		instance = method.getDeclaringClass().newInstance();
	    }

	    // Invoke the Method
	    retVal = method.invoke(instance, new Object[] {handlerContext});
	    if (retVal != null) {
		result = retVal;
	    }
	}

	// Return the result (null if no result)
	return result;
    }


    private HandlerDefinition 	_handlerDef	= null;
    private Map			_inputs		= new HashMap();
    private Map			_outputs	= new HashMap();
}
