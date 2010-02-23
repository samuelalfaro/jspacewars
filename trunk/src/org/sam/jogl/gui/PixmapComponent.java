/**
 * 
 */
package org.sam.jogl.gui;

import javax.media.opengl.GL;

public class PixmapComponent extends GLComponent{
	public final Pixmap pixmap;
	
	public PixmapComponent( Pixmap pixmap ){
		this.pixmap = pixmap;
	}
	
	public void draw(GL gl){
		pixmap.draw(gl, x1, y1, x2, y2);
	}
}