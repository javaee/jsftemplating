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

package com.sun.jsft.component;

import com.sun.jsft.tasks.Task;
import com.sun.jsft.tasks.TaskEvent;
import com.sun.jsft.tasks.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;


/**
 *
 */
public class DeferredFragment extends UIComponentBase {

    /**
     *	<p> Default constructor.</p>
     */
    public DeferredFragment() {
	subscribeToEvent(
	    PostAddToViewEvent.class,
	    new DeferredFragment.AfterCreateListener());
    }

    /**
     *
     */
    public String getFamily() {
	return FAMILY;
    }

    /**
     *
     */
    public boolean getRendersChildren() {
	return true;
    }

    public void encodeBegin(FacesContext context) throws IOException {
	System.out.println("Encode Begin ("+getId()+")...");
    }

    public void encodeEnd(FacesContext context) throws IOException {
	System.out.println("Encode End ("+getId()+")...");
    }

    /**
     *	<p> This method returns <code>true</code> when all tasks this deferred
     *	    fragment depends on are complete.  This method is not intended to
     *	    be used to poll this task for completion, you should instead
     *	    register for the ready event that it fires.</p>
     */
    private boolean isReady() {
	return (taskCount == 0);
    }

    /**
     *	<p> This method gets the id of the "place-holder" component for this
     *	    <code>DeferredFragment</code>.</p>
     */
    public String getPlaceHolderId() {
	return placeHolderId;
    }

    /**
     *	<p> This method sets the id of the "place-holder" component for this
     *	    <code>DeferredFragment</code>.</p>
     */
    public void setPlaceHolderId(String id) {
	placeHolderId = id;
    }

    /**
     *	<p> This method returns the number of tasks this DeferredFragment is
     *	    waiting for.</p>
     */
    public int getTaskCount() {
	return taskCount;
    }

    /**
     *	<p> This method sets the number of tasks this
     *	    <code>DeferredFragment</code> must wait for.</p>
     */
    public void setTaskCount(int count) {
	taskCount = count;
    }

    /**
     *	<p> This method registers the given
     *	    <code>ComponentSystemEventListener</code> that should be notified
     *	    when this <code>DeferredFragment</code> is ready to be
     *	    rendered.</p>
     */
    public void addReadyListener(ComponentSystemEventListener listener) {
	listeners.add(listener);
    }

    /**
     *	<p> This method is responsible for firing the {@link FragmentReadyEvent}
     *	    to signal to listeners that the {@link Task}s needed by this
     *	    <code>DeferredFragment</code> have completed and it is now ready to
     *	    be processed.</p>
     */
    protected void fireFragmentReadyEvent() {
	ComponentSystemEvent event = new FragmentReadyEvent(this);
System.out.println("listeners" + listeners);
	for (ComponentSystemEventListener listener : listeners) {
	    listener.processEvent(event);
	}
    }


    /**
     *	<p> Listener used to handle TaskEvents.</p>
     */
    public static class DeferredFragmentTaskListener implements SystemEventListener {

	/**
	 *  <p>	Default Constructor.</p>
	 */
	public DeferredFragmentTaskListener(DeferredFragment df) {
	    super();
	    this.df = df;
	}

	/**
	 *  <p>	The event passed in will be a {@link TaskEvent}.</p>
	 */
	public void processEvent(SystemEvent event) throws AbortProcessingException {
System.out.println("DeferredFragmentTaskListener.processEvent()!");
	    Task task = (Task) event.getSource();
	    String eventType = ((TaskEvent) event).getType();
	    int count = 0;
	    synchronized (df) {
		// Synch to ensure we don't change it during this time.
		count = df.getTaskCount() - 1;
		df.setTaskCount(count);
	    }
	    if (count == 0) {
		// We're done!
		df.fireFragmentReadyEvent();
	    }
	}

	public boolean isListenerForSource(Object source) {
	    // We only dispatch this correctly... this method is not needed.
	    return true;
	}

	// A reference to the DeferredFragment
	private DeferredFragment df = null;
    }

    /**
     *	<p> Listener used to relocate the children to a facet on the
     *	    UIViewRoot.</p>
     */
    public static class AfterCreateListener implements ComponentSystemEventListener {
	AfterCreateListener() {
	}

	/**
	 *  <p>	This method is responsible for setting up this
	 *	<code>DeferredFragment</code>.  This includes the following
	 *	steps:</p>
	 *
	 *  <ul><li>Put a "place-holder" component at the location of the
	 *	    fragment so it can be swapped out by the client at a later
	 *	    time.</li>
	 *	<li>Move this component to a facet in the UIViewRoot.</li>
	 *	<li>Register any tasks that need to be executed, add a listener
	 *	    for each task or the specified event within the task.</li>
	 *	<li>Ensure a FragmentRenderer component exists at the end of the page.</li>
	 */
	public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
	    // Ensure we only do this once... NOTE: I tried to unsubscribe from
	    // the event, but ran into a ConcurrentModificationException... the
	    // list of listeners is probably still being looped through while I
	    // am attempting to remove this Listener.  So I'll do this instead:
	    if (done) {
		return;
	    }
	    done = true;

	    // Get the component
	    DeferredFragment comp = (DeferredFragment) event.getComponent();

	    // Get the UIViewRoot Facet Map
	    FacesContext ctx = FacesContext.getCurrentInstance();
	    UIViewRoot viewRoot = ctx.getViewRoot();
	    Map<String, UIComponent> facetMap = viewRoot.getFacets();

	    // Create a place holder...
	    String key = "jsft_" + comp.getClientId(ctx);
	    UIComponent placeHolder = new UIOutput();
	    placeHolder.getAttributes().put(
		    "value", "<span id='" + key + "'></span>");

	    // Swap comp with the placeHolder...
	    List<UIComponent> peers = comp.getParent().getChildren();
	    int index = peers.indexOf(comp);
	    peers.set(index, placeHolder);
	    comp.setPlaceHolderId(placeHolder.getClientId(ctx));

	    // Move this component to the FacetMap
	    facetMap.put(key, comp);

	    // Register task(s)
	    String task = (String) comp.getAttributes().get("task");
	    StringTokenizer tok = new StringTokenizer(task, ";");
	    TaskManager tm = TaskManager.getInstance();
	    int taskCount = 0;
	    while (tok.hasMoreTokens()) {
		task = tok.nextToken().trim();

		// Check to see if we have task:listenerType
		int idx = task.indexOf(":");
		String type = null;
		if (idx != -1) {
		    type = task.substring(idx + 1);
		    task = task.substring(0, idx);
	    }

		// Register the Task...
		tm.addTask(task, type, new DeferredFragmentTaskListener(comp));

		// Count the tasks we depend on...
		taskCount++;
	    }

	    // Store the task count...
	    comp.setTaskCount(taskCount);

	    // Ensure we have a FragmentRenderer component...
	    Map<String, Object> requestScope =
		    ctx.getExternalContext().getRequestMap();
	    FragmentRenderer fragmentRenderer =
		    (FragmentRenderer) requestScope.get(FRAGMENT_RENDERER);
	    if (fragmentRenderer == null) {
		// Create one...
		fragmentRenderer = new FragmentRenderer();
		fragmentRenderer.setId(FRAGMENT_RENDERER);

		// Store FragmentRenderer in request scope as well as the last
		// component in the UIViewRoot. (request scope for fast access)
		viewRoot.getChildren().add(fragmentRenderer);
		requestScope.put(FRAGMENT_RENDERER, fragmentRenderer);
	    }

	    // Increment fragment count on FragmentRenderer component...
	    fragmentRenderer.addDeferredFragment(comp);
	    comp.addReadyListener(fragmentRenderer);
	}

	private boolean done = false;
	private static final String FRAGMENT_RENDERER	= "jsft-FR";
    }

    /**
     *	<p> The component family.</p>
     */
    public static final String FAMILY	=   DeferredFragment.class.getName();


    /**
     *	<p> The number of tasks that need to be complete before this
     *	    <code>DeferredFragment</code> can be rendered.  It is initialized
     *	    to a postiive value (1) so that {@link #isReady()} will return
     *	    <code>false</code> -- important since the tasks have not yet been
     *	    counted.</p>
     */
    private int taskCount = 1;

    /**
     *	<p> The id of the placeholder for this component so we can find it
     *	    later.</p>
     */
    private transient String placeHolderId = "";

    /**
     *	<p> This <code>List</code> will hold the list of listeners interested
     *	    in being notified with this <code>DeferredFragment</code> is ready
     *	    to be rendered.</p>
     */
    private List<ComponentSystemEventListener> listeners =
	    new ArrayList<ComponentSystemEventListener>(2);
}
