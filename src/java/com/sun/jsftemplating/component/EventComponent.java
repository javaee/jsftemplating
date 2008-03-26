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
package com.sun.jsftemplating.component;


/**
 *  <p>	This <code>UIComponent</code> exists so that custom events may be
 *	queued and triggered at appropriate times.  This will allow
 *	"beforeEncode", "afterEncode", and even "command" events to be
 *	associated with components by wrapping 1 or more components with
 *	this component.</p>
 *
 *  @author Ken Paulsen (ken.paulsen@sun.com)
 */
public class EventComponent extends TemplateComponentBase {
    /**
     *	<p> Constructor for <code>EventComponent</code>.</p>
     */
    public EventComponent() {
	super();
	setRendererType("com.sun.jsftemplating.EventComponent");
	setLayoutDefinitionKey(LAYOUT_KEY);
    }

    /**
     *	<p> Return the family for this component.</p>
     */
    public String getFamily() {
	return "com.sun.jsftemplating.EventComponent";
    }

    /**
     *	<p> Restore the state of this component.</p>
    public void restoreState(FacesContext _context,Object _state) {
	Object _values[] = (Object[]) _state;
	super.restoreState(_context, _values[0]);
    }
    */

    /**
     *	<p> Save the state of this component.</p>
    public Object saveState(FacesContext _context) {
	Object _values[] = new Object[1];
	_values[0] = super.saveState(_context);
	return _values;
    }
     */

    /**
     *	<p> This is the location of the XML file that declares the layout for
     *	    the EventComponent. (/jsftemplating/event.xml)</p>
     */
    public static final String	LAYOUT_KEY  =	"/jsftemplating/event.xml";
}
