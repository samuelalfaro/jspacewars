/**
 * 
 */
package org.sam.jogl.gui;

import javax.media.opengl.GL;

public abstract class GLComponent{
	/**
	 * Esquinas / Corners
	 */
	protected float x1, y1, x2, y2;
	
	public void setBounds( float x, float y, float w, float h ){
		this.x1 = x;
		this.y1 = y;
		this.x2 = x + w;
		this.y2 = y + h;
	}
	
	public void setCorners( float x1, float y1, float x2, float y2 ){
		setBounds(x1, y1, x2-x1, y2 -y1);
//		this.x1 = x1;
//		this.y1 = y1;
//		this.x2 = x2;
//		this.y2 = y2;
	}
	
	public abstract void draw(GL gl);
}