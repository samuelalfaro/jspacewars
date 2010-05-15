package org.sam.jogl.gui;

import javax.media.opengl.GL;

public abstract class GLComponent{
	/**
	 * Esquinas / Corners
	 */
	protected float x1, y1, x2, y2;
	
	public void setBounds( float x, float y, float w, float h ){
		setCorners(x, y, x + w, y + h);
	}
	
	public final void setCorners( float x1, float y1, float x2, float y2 ){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public abstract void draw(GL gl);
}