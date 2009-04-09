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
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
