package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.component.EventComponent;
import com.sun.jsftemplating.component.factory.basic.StaticTextFactory;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;

import junit.framework.*;


/**
 *  <p>	Tests for the {@link LayoutDefinitionManager}.</p>
 */
public class LayoutDefinitionManagerTest extends TestCase {

    /**
     *
     */
    protected void setUp() {
    }

    /**
     *	<p> Test to ensure we can read a global {@link ComponentType}s.</p>
     */
    public void testReadGlobalComponentTypes() {
	try {
	    ComponentType staticText = LayoutDefinitionManager.getGlobalComponentType("staticText");
	    assertTrue(staticText != null);
	    ComponentType event = LayoutDefinitionManager.getGlobalComponentType("event");
	    assertTrue(event != null);
	    assertTrue(staticText != event);
	    assertTrue(staticText.getFactory() instanceof StaticTextFactory);
	    assertTrue(event.getFactory().create(null, null, null) instanceof EventComponent);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }
}
