
package com.sun.jsftemplating.samples.editor;

import java.util.*;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;

public class Test {
    
    /** Creates a new instance of Test */
    public Test() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	LayoutDefinition ld = LayoutDefinitionManager.getLayoutDefinition(null, "propSheet.jsf");

	List compList = getList(ld, "", new ArrayList());

	for (int i=0; i < compList.size(); i++) {
		System.out.println(compList.get(i));
	}
        
    }


    private static List<String> getList(LayoutElement le, String indent, List newList) {
	List list = le.getChildLayoutElements();
	
	for (int i=0; i < list.size(); i++) {
		LayoutElement lel = (LayoutElement)list.get(i);
		if (lel instanceof LayoutComponent) {
			newList.add(indent + lel.getUnevaluatedId());
			getList(lel, indent + " ", newList);
			
		}	
	}
	
	return newList;
    }
    
}

