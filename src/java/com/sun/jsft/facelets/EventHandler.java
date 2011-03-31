/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2011 Ken Paulsen
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jsft.facelets;

import com.sun.jsft.event.CommandEventListener;
import com.sun.jsft.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PostConstructViewMapEvent;
import javax.faces.event.PostRestoreStateEvent;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreDestroyViewMapEvent;
import javax.faces.event.PreRemoveFromViewEvent;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.event.SystemEvent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;


/**
 *  <p>	This is the TagHandler for the jsft:event tag.</p>
 *
 *  Created March 29, 2011
 *  @author Ken Paulsen kenapaulsen@gmail.com
 */
public class EventHandler extends TagHandler {

    /**
     *	<p> Constructor.</p>
     */
    public EventHandler(TagConfig config) {
        super(config);
	
        this.type = this.getRequiredAttribute("type");
	if (!(config.getNextHandler().getClass().getName().equals(
		    "com.sun.faces.facelets.compiler.UIInstructionHandler"))) {
	    // This occurs when an empty jsft:event tag is used... ignore
	    return;
	}

	/*
	TagAttribute atts[] = config.getTag().getAttributes().getAll();
	for (TagAttribute att : atts) {
	    System.out.println("ATTRIBUTE[" + att.getLocalName() + "] = " + att.getValue());
	}
	System.out.println("NEXT HANDLER: " + config.getNextHandler());
	*/

	// Split apart the EL commands
	StringTokenizer tok = new StringTokenizer(config.getNextHandler().toString(), ";");
	String next = null;
	while (tok.hasMoreTokens()) {
	    next = tok.nextToken().trim();
	    if (next.equals("")) {
		continue;
	    }
	    if (next.startsWith("#{")) {
		if (!next.endsWith("}")) {
		    throw new FacesException(
			"Expression started with #{ but did not end with }!");
		}
		next = next.substring(2, next.length() - 1);
	    }
	    commands.add(next);
	}
    }

    /**
     *	<p> This method is responsible for queueing up the EL that should be
     *	:   invoked when the event is fired.</p>
     */
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        if (ComponentHandler.isNew(parent)) {
            Class<? extends SystemEvent> eventClass = getEventClass(ctx);
            // ensure that f:event can be used anywhere on the page for
	    // these events, not just as a direct child of the viewRoot
            if ((PreRenderViewEvent.class == eventClass)
		    || (PostConstructViewMapEvent.class == eventClass)
		    || (PreDestroyViewMapEvent.class == eventClass)) {
                parent = ctx.getFacesContext().getViewRoot();
            }
            if ((eventClass != null) && (parent != null)) {
                parent.subscribeToEvent(eventClass, new CommandEventListener(this.commands));
            }
        } else {
	    // already done...
	}
    }

    /**
     *	<p> This method returns the event <code>Class</code>.  Many event types
     *	    have short aliases that are recognized by this method, others may
     *	    need the fully qualified classname.  The supported types are:</p>
     *
     *	    <ul><li>afterCreate</li>
     *		<li>afterCreateView</li>
     *		<li>afterValidate</li>
     *		<li>beforeEncode</li>
     *		<li>beforeEncodeView<li>
     *		<li>beforeValidate</li>
     *		<li>preRenderComponent</li>
     *		<li>javax.faces.event.PreRenderComponent</li>
     *		<li>preRenderView</li>
     *		<li>javax.faces.event.PreRenderView</li>
     *		<li>postAddToView</li>
     *		<li>javax.faces.event.PostAddToView</li>
     *		<li>preValidate</li>
     *		<li>javax.faces.event.PreValidate</li>
     *		<li>postValidate</li>
     *		<li>javax.faces.event.PostValidate</li>
     *		<li>preRemoveFromView</li>
     *		<li>javax.faces.event.PreRemoveFromViewEvent</li>
     *		<li>postRestoreState</li>
     *		<li>javax.faces.event.PostRestoreStateEvent</li>
     *		<li>postConstructViewMap</li>
     *		<li>javax.faces.event.PostConstructViewMapEvent</li>
     *		<li>preDestroyViewMap</li>
     *		<li>javax.faces.event.PreDestroyViewMapEvent</li>
     *	    </ul>
     *
     *	@param	ctx	The <code>FaceletContext</code>.
     *
     *	@return	    The <code>SystemEvent</code> class associated with the
     *		    event type.
     */
    protected Class<? extends SystemEvent> getEventClass(FaceletContext ctx) {
        String eventType = (String) this.type.getValueExpression(ctx, String.class).getValue(ctx);
        if (eventType == null) {
            throw new FacesException("Attribute 'type' can not be null!");
        }

	// Check the pre-defined types / aliases
	Class cls = eventAliases.get(eventType);

	if (cls == null) {
	    // Not found, try reflection...
	    try {
		cls = Util.loadClass(eventType, eventType);
	    } catch (ClassNotFoundException ex) {
		throw new FacesException("Invalid event type: " + eventType, ex);
	    }
	}

	// Return the result...
	return cls;
    }

    private static Map<String, Class<? extends SystemEvent>> eventAliases = new HashMap<String, Class<? extends SystemEvent>>(20);
    static {
        eventAliases.put("beforeEncode", PreRenderComponentEvent.class);
        eventAliases.put("preRenderComponent", PreRenderComponentEvent.class);
        eventAliases.put("javax.faces.event.PreRenderComponent", PreRenderComponentEvent.class);

        eventAliases.put("beforeEncodeView", PreRenderViewEvent.class);
        eventAliases.put("preRenderView", PreRenderViewEvent.class);
        eventAliases.put("javax.faces.event.PreRenderView", PreRenderViewEvent.class);

        eventAliases.put("afterCreate", PostAddToViewEvent.class);
        eventAliases.put("postAddToView", PostAddToViewEvent.class);
        eventAliases.put("javax.faces.event.PostAddToView", PostAddToViewEvent.class);

	eventAliases.put("afterCreateView", PostRestoreStateEvent.class);
	eventAliases.put("postRestoreState", PostRestoreStateEvent.class);
	eventAliases.put("javax.faces.event.PostRestoreStateEvent", PostRestoreStateEvent.class);

        eventAliases.put("beforeValidate", PreValidateEvent.class);
        eventAliases.put("preValidate", PreValidateEvent.class);
        eventAliases.put("javax.faces.event.PreValidate", PreValidateEvent.class);

        eventAliases.put("afterValidate", PostValidateEvent.class);
        eventAliases.put("postValidate", PostValidateEvent.class);
        eventAliases.put("javax.faces.event.PostValidate", PostValidateEvent.class);

	eventAliases.put("preRemoveFromView", PreRemoveFromViewEvent.class);
	eventAliases.put("javax.faces.event.PreRemoveFromViewEvent", PreRemoveFromViewEvent.class);
	eventAliases.put("postConstructViewMap", PostConstructViewMapEvent.class);
	eventAliases.put("javax.faces.event.PostConstructViewMapEvent", PostConstructViewMapEvent.class);
	eventAliases.put("preDestroyViewMap", PreDestroyViewMapEvent.class);
	eventAliases.put("javax.faces.event.PreDestroyViewMapEvent", PreDestroyViewMapEvent.class);
/*
  FIXME: Look at supporting these too...
	Non component, system events:
	  postConstructApplication
	  ActionEvent... hmm...
	  ValueChangedEvent
	  exceptionQueued
	  BehaviorEvent
	    - AjaxBehaviorEvent
*/
    }

    protected final TagAttribute type;
    protected List<String> commands = new ArrayList<String>(5);
}
