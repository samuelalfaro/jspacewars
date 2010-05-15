/**
 * 
 */
package org.sam.jogl.gui;

import javax.media.opengl.GL;

public class Pixmap{
	/**
	 * Esquinas de las coordenadas de textura.
	 */
	public float u1, v1, u2, v2;
	
	public Pixmap(){
		this( 0, 0, 1, 1 );
	}
	
	public Pixmap(float x, float y, float w, float h){
		u1 = x;
		v1 = y;
		u2 = x + w;
		v2 = y + h;
	}
	
	public void draw(GL gl, float x1, float y1, float x2, float y2){
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f( u1, v1 );
		gl.glVertex2f  ( x1, y1 );
		gl.glTexCoord2f( u2, v1 );
		gl.glVertex2f  ( x2, y1 );
		gl.glTexCoord2f( u2, v2 );
		gl.glVertex2f  ( x2, y2 );
		gl.glTexCoord2f( u1, v2 );
		gl.glVertex2f  ( x1, y2 );
		gl.glEnd();
	}
}