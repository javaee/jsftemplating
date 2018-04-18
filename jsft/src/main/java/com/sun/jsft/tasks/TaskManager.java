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
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;


/**
 *  <p>	To get an instance of this class, use {@link #getInstance()}.  This
 *	will check the "<code>com.sun.jsft.TASK_MANAGER</code>"
 *	<code>web.xml</code> <code>context-param</code> to find the
 *	correct implementation to use.  If not specified, it will use the
 *	{@link DefaultTaskManager}.  Alternatively, you can invoke
 *	{@link setTaskManager(TaskManager)} directly to specify the desired
 *	implementation.</p>
 */
public abstract class TaskManager {

    /**
     *	<p> Default constructor.</p>
     */
    public TaskManager() {
	super();
    }

    /**
     *	<p> This method is responsible for executing the queued Tasks.  It is
     *	    possible this method may be called more than once (not common), so
     *	    care should be taken to ensure this is handled appropriately.  This
     *	    method is normally executed after the page (excluding
     *	    DefferedFragments, of course) have been rendered.</p>
     */
    public abstract void start();

    /**
     *	<p> This method locates or creates the TaskManager instance associated
     *	    with this request.</p>
     */
    public static TaskManager getInstance() {
	// See if we already calculated the TaskManager for this request
	FacesContext ctx = FacesContext.getCurrentInstance();
	TaskManager taskManager = null;
	Map<String, Object> requestMap = null;
	if (ctx != null) {
	    requestMap = ctx.getExternalContext().getRequestMap();
	    taskManager = (TaskManager) requestMap.get(TASK_MANAGER);
	}
	if (taskManager == null) {
	    Map initParams = ctx.getExternalContext().getInitParameterMap();
	    String className = (String) initParams.get(IMPL_CLASS);
	    if (className != null) {
		try {
		    taskManager = (TaskManager) Class.forName(className).newInstance();
		} catch (Exception ex) {
		    throw new RuntimeException(ex);
		}
	    } else {
		taskManager = new DefaultTaskManager();
	    }
	    if (requestMap != null) {
		requestMap.put(TASK_MANAGER, taskManager);
	    }
	}
	return taskManager;
    }

    /**
     *	<p> This method is provided in case the developer would like to provide
     *	    their own way to calculate and create the <code>TaskManager</code>
     *	    implementation to use.</p>
     */
    public static void setTaskManager(TaskManager taskManager) {
	FacesContext ctx = FacesContext.getCurrentInstance();
	if (ctx != null) {
	    ctx.getExternalContext().getRequestMap().put(
		    TASK_MANAGER, taskManager);
	} else {
	    throw new RuntimeException(
		"Currently only JSF is supported!  FacesContext not found.");
	}
    }

    /**
     *	<p> This method is responsible for queuing up a <code>task</code> to
     *	    be performed.  The given <code>newListeners</code> will be fired
     *	    according to the requested event <code>type</code>.  If the
     *	    <code>type</code> is not specified, it will default to
     *	    {@link Task#DEFAULT_EVENT_TYPE} indicating that the given
     *	    <code>newListeners</code> should be fired at the completion of the
     *	    task.</p>
     *
     *	<p> Note: If the <code>task</code> is already queued, it will NOT be
     *	    performed twice.  The <code>newListeners</code> will be added to
     *	    the already-queued <code>task</code>.</p>
     *
     *	@param	task	A unique string identifying a task to perform. This is
     *			implementation specific to the TaskManager
     *			implementation.
     *
     *	@param	type	Optional String identifying the event name within the
     *			task in which the given Listeners are associated.  If
     *			no type is given, the listeners will be fired at the
     *			end of the task ({@link Task#DEFAULT_EVENT_TYPE}).
     *
     *	@param	newListeners	The SystemEventListener to be associated with this
     *			task and optional type if specified.
     */
    public void addTask(String taskName, String type, SystemEventListener ... newListeners) {
// FIXME: Do I want to accept priority too??  Or perhaps that is handled in
// FIXME: the implementation-specific way tasks are registered?  Or is priority
// FIXME: only associated with DeferredFragments?
	Task task = tasks.get(taskName);
	if (task == null) {
	    // New Task, create and add...
	    task = new Task(taskName);
	    task.setListeners(type, toArrayList(newListeners));
	    tasks.put(taskName, task);
	} else {
	    // Task already created, add the listeners for this type...
	    List<SystemEventListener> taskListeners = task.getListeners(type);
	    if (taskListeners == null) {
		task.setListeners(type, toArrayList(newListeners));
	    } else {
		taskListeners.addAll(toArrayList(newListeners));
	    }
	}
    }

    /**
     *	<p> This method returns the <code>List&lt;Task&gt;</code>.</p>
     */
    public Collection<Task> getTasks() {
	return tasks.values();
    }

    /**
     *	<p> Convert an array of <code>T</code> to an
     *	    <code>ArrayList&lt;T&gt;</code>.</p>
     */
    private <T> ArrayList<T> toArrayList(T arr[]) {
	ArrayList<T> list = new ArrayList<T>(arr.length);
	for (T item : arr) {
	    list.add(item);
	}
	return list;
    }


    /**
     *	<p> This <code>Map</code> will hold all the Tasks.</p>
     */
    private Map<String, Task> tasks = new HashMap<String, Task>(2);

    /**
     *	<p> The request scope key for holding the TASK_MANAGER instance to
     *	    make it easily obtained.</p>
     */
    private static final String	TASK_MANAGER	= "_jsftTM";

    /**
     *	<p> The web.xml <code>context-param</code> for declaring the
     *	    implementation of this class to use.</p>
     */
    public static final String	IMPL_CLASS	= "com.sun.jsft.TASK_MANAGER";
}
