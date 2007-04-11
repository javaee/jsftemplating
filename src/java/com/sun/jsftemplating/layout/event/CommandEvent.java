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
package com.sun.jsftemplating.layout.event;

import java.util.EventObject;

import javax.faces.component.UIComponent;


/**
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class CommandEvent extends EventObjectBase implements UIComponentHolder {
    private static final long serialVersionUID = 1L;

    /**
     *	<p> Constructor.</p>
     *
     *	@param	component   The <code>UIComponent</code> associated with this
     *			    <code>EventObject</code>.
     */
    public CommandEvent(UIComponent component, EventObject actionEvent) {
	super(component);
	setActionEvent(actionEvent);
    }

    /**
     *	<p> Setter for <code>actionEvent</code>.  When a
     *	    <code>CommandEvent</code> is created, there is often another event
     *	    involved.  This property contains that other event (if any).  The
     *	    type of this event is often an <code>ActionEvent</code>, hence the
     *	    name of this property.  However, this should not always be assumed
     *	    to be set or of type <code>ActionEvent</code>.</p>
     *
     *	@param	actionEvent The <code>EventObject</code> to set.
     */
    public void setActionEvent(EventObject actionEvent) {
	_actionEvent = actionEvent;
    }

    /**
     *	<p> Getter for <code>actionEvent</code>.  When a
     *	    <code>CommandEvent</code> is created, there is often another event
     *	    involved.  This property contains that other event (if any).  The
     *	    type of this event is often an <code>ActionEvent</code>, hence the
     *	    name of this property.  However, this should not always be assumed
     *	    to be set or of type <code>ActionEvent</code>.</p>
     *
     *	@return	The actionEvent <code>EventObject</code>.
     */
    public EventObject getActionEvent() {
	return _actionEvent;
    }

    private EventObject _actionEvent = null;

    /**
     *	<p> The "command" event type. ("command")</p>
     */
    public static final String	EVENT_TYPE  = "command";
}
