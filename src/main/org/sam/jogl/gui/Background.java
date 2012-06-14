/* 
 * Background.java
 * 
 * Copyright (c) 2012 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jogl.gui;

import javax.media.opengl.GL2;
import javax.vecmath.Color4f;

import org.sam.jogl.Apariencia;
import org.sam.jogl.gui.Border.AbsBorder;

/**
 * 
 */
public interface Background{
	
	public abstract static class AbsBackground implements Background{
		
		Apariencia apariencia;
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#setApariencia(org.sam.jogl.Apariencia)
		 */
		@Override
		public void setApariencia( Apariencia apariencia ){
			this.apariencia = apariencia;
		}
	}
	
	public static class Solid extends AbsBackground{

		private float r, g, b, a;
		
		public Solid( Color4f color ){
			this.setColor( color );
		}
		
		public Solid( float r, float g, float b, float a ){
			this.setColor( r, g, b, a );
		}
		
		public void setColor( Color4f color ){
			setColor( color.x, color.y, color.z, color.w );
		}
		
		public void setColor( float r, float g, float b, float a ){
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#draw(javax.media.opengl.GL2, float, float, float, float)
		 */
		@Override
		public void draw( GL2 gl, float x1, float y1, float x2, float y2 ){
			apariencia.usar( gl );
			gl.glBegin(GL2.GL_QUADS);
				gl.glColor4f  ( r, g, b, a );
				gl.glVertex2f ( x1, y1 );
				gl.glVertex2f ( x1, y2 );
				gl.glVertex2f ( x2, y2 );
				gl.glVertex2f ( x2, y1 );
			gl.glEnd();
		}
	}
	
	public static class Gradient extends AbsBackground{
		
		//LeftTopColor
		private float r11, g11, b11, a11;
		//LeftBottomColor
		private float r12, g12, b12, a12;
		//RightTopColor
		private float r21, g21, b21, a21;
		//RightBottomColor
		private float r22, g22, b22, a22;
		
		Gradient( float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2, boolean horizontal ){
			if( horizontal ){
				this.setLeftColor( r1, g1, b1, a1 );
				this.setRightColor( r2, g2, b2, a2 );
			}else{
				this.setTopColor( r1, g1, b1, a1 );
				this.setBottomColor( r2, g2, b2, a2 );	
			}
		}
		
		Gradient( Color4f color1, Color4f color2, boolean horizontal ){
			this( color1.x, color1.y, color1.z, color1.w, color2.x, color2.y, color2.z, color2.w, horizontal );
		}
		
		public void setLeftColor( Color4f color ){
			setLeftColor( color.x, color.y, color.z, color.w );
		}
		
		public void setLeftColor( float r, float g, float b, float a ){
			setLeftTopColor( r, g, b, a );
			setLeftBottomColor( r, g, b, a );
		}
		
		public void setTopColor( Color4f color ){
			setTopColor( color.x, color.y, color.z, color.w );
		}
		
		public void setTopColor( float r, float g, float b, float a ){
			setLeftTopColor( r, g, b, a );
			setRightTopColor( r, g, b, a );
		}
		
		public void setRightColor( Color4f color ){
			setRightColor( color.x, color.y, color.z, color.w );
		}
		
		public void setRightColor( float r, float g, float b, float a ){
			setRightTopColor( r, g, b, a );
			setRightBottomColor( r, g, b, a );
		}
		
		public void setBottomColor( Color4f color ){
			setBottomColor( color.x, color.y, color.z, color.w );
		}
		
		public void setBottomColor( float r, float g, float b, float a ){
			setLeftBottomColor( r, g, b, a );
			setRightBottomColor( r, g, b, a );
		}
		
		public void setLeftTopColor( float r, float g, float b, float a ){
			this.r11 = r;
			this.g11 = g;
			this.b11 = b;
			this.a11 = a;
		}
		
		public void setRightTopColor( float r, float g, float b, float a ){
			this.r21 = r;
			this.g21 = g;
			this.b21 = b;
			this.a21 = a;
		}
		
		public void setLeftBottomColor( float r, float g, float b, float a ){
			this.r12 = r;
			this.g12 = g;
			this.b12 = b;
			this.a12 = a;
		}
		
		public void setRightBottomColor( float r, float g, float b, float a ){
			this.r22 = r;
			this.g22 = g;
			this.b22 = b;
			this.a22 = a;
		}

		public void draw( GL2 gl, float x1, float y1, float x2, float y2 ){
			apariencia.usar( gl );
			gl.glBegin(GL2.GL_QUADS);
				gl.glColor4f  ( r11, g11, b11, a11 );
				gl.glVertex2f ( x1, y1 );
				gl.glColor4f  ( r12, g12, b12, a12 );
				gl.glVertex2f ( x1, y2 );
				gl.glColor4f  ( r22, g22, b22, a22 );
				gl.glVertex2f ( x2, y2 );
				gl.glColor4f  ( r21, g21, b21, a21 );
				gl.glVertex2f ( x2, y1 );
			gl.glEnd();
		}
	}
	
	public static class Textured extends AbsBackground{

		private final Pixmap pixmap;
		
		public Textured(){
			this( 0.0f, 0.0f, 1.0f, 1.0f );
		}
		
		public Textured( int textureX0, int textureY0, int textureX1, int textureY1, int textureWidth, int textureHeight ){
			this(
					(float)textureX0/textureWidth, (float)textureY0/textureHeight,
					(float)textureX1/textureWidth, (float)textureY1/textureHeight
			);
		}
		
		public Textured( float u0, float v0, float u1, float v1 ){
			this( new Pixmap( u0, v0, u1 - u0, v1 - v0 ));
		}
		
		private Textured( Pixmap pixmap ){
			this.pixmap = pixmap;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#draw(javax.media.opengl.GL2, float, float, float, float)
		 */
		@Override
		public void draw( GL2 gl, float x1, float y1, float x2, float y2 ){
			apariencia.usar( gl );
			pixmap.drawCounterclockwise( gl, x1, y1, x2, y2 );
		}
	}
	
	/**
	 * Método que dibuja el fondo en la zona rectagular
	 * delimitada por las coordenadas correspondientes.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * @param x1 coordenada X de una de las esquinas.
	 * @param y1 coordenada Y de una de las esquinas.
	 * @param x2 coordenada X de la esquina opuesta.
	 * @param y2 coordenada Y de la esquina opuesta.
	 */
	public void draw( GL2 gl, float x1, float y1, float x2, float y2 );
	
	public void setApariencia( Apariencia apariencia );
}
