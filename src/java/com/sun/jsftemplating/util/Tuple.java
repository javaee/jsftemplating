/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.jsftemplating.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jason CTR Lee
 */
public class Tuple {
    private List<Object> elements = new ArrayList<Object>();
    public Tuple(Object... elements) {
        for (Object element : elements) {
            this.elements.add(element);
        }
    }
    
    public Object getElement(int index) {
        return elements.get(index);
    }

    public List<Object> getElements() {
        return Collections.unmodifiableList(elements);
    }
    
}
