package org.sam.gui;

import java.awt.*;

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
