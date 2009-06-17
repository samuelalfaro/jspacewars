/*
 * Created on 25-dic-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sam.gui;

import java.awt.*;
/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class FloatLayout implements LayoutManager {
	
	public void addLayoutComponent(String name, Component comp){
	}

	public void removeLayoutComponent(Component comp){
	}

	public Dimension preferredLayoutSize(Container target) {
		return new Dimension(0,0);
	}

	public Dimension minimumLayoutSize(Container target) {
		return new Dimension(0,0);
	}
	
	public void layoutContainer(Container target) {
		int count = target.getComponentCount();
		for (int i = 0; i < count; i++) {
			Component comp = target.getComponent(i);
			comp.getPreferredSize();
		}
	}
}
