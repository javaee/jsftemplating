/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
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

package com.sun.jsft.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;


/**
 *  <p>	This is the default {@link TaskManager} implementation.</p>
 */
public class DefaultTaskManager extends TaskManager {

    /**
     *	<p> Default constructor.</p>
     */
    protected DefaultTaskManager() {
	super();
    }

    /**
     *	<p> This method is responsible for executing the queued Tasks.  It is
     *	    possible this method may be called more than once (not common), so
     *	    care should be taken to ensure this is handled appropriately.  This
     *	    method is normally executed after the page (excluding
     *	    DefferedFragments, of course) have been rendered.</p>
     */
    public void start() {
	System.out.println("Starting to execute Tasks: " + getTasks());
	// Loop through the tasks and execute them...
	for (Task task : getTasks()) {
// FIXME: This implementation is a no-op, it just loops through the tasks and fires the TASK_COMPLETE event.
// FIXME: A real implementation would aggregate & dispatch the tasks and register listeners with the "backend dispatcher" which would fire the TASK_COMPLETE event.
// FIXME: This method should not block.
	    SystemEvent event = new TaskEvent(task);
	    List<SystemEventListener> listeners = task.getListeners(TaskEvent.TASK_COMPLETE);
	    if (listeners != null) {
		for (SystemEventListener listener : listeners) {
		    listener.processEvent(event);
		}
	    }
	}
    }
}
