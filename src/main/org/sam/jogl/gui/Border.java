/* 
 * Border.java
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
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.Textura;
import org.sam.jogl.UnidadTextura;

public interface Border{
	
	public abstract static class AbsBorder implements Border{
		
		Apariencia apariencia;
		
		Insets outer;
		//TODO implementar
		Insets inner;
		
		AbsBorder( Insets border ){
			this.outer = border;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#setApariencia(org.sam.jogl.Apariencia)
		 */
		@Override
		public void setApariencia( Apariencia apariencia ){
			this.apariencia = apariencia;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#setOuterInsets(org.sam.jogl.gui.Insets)
		 */
		@Override
		public void setOuterInsets( Insets insets ){
			this.outer = insets;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#setInnerInsets(org.sam.jogl.gui.Insets)
		 */
		@Override
		public void setInnerInsets( Insets insets ){
			this.inner = insets;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#getOuterInsets()
		 */
		@Override
		public final Insets getOuterInsets(){
			return outer;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#getInnerInsets()
		 */
		@Override
		public final Insets getInnerInsets(){
			return inner;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.jogl.gui.Border#draw(javax.media.opengl.GL2, float, float, float, float)
		 */
		@Override
		public final void draw( GL2 gl, float rx1, float ry1, float rx2, float ry2 ){
			final float x0, y0, x1, y1, x2, y2, x3, y3;
			if( outer == null ){
				x0 = rx1;
				y0 = ry1;
				x3 = rx2;
				y3 = ry2;
			}else{
				x0 = rx1 - outer.left;
				y0 = ry1 - outer.top;
				x3 = rx2 + outer.right;
				y3 = ry2 + outer.bottom;
			}
			if( inner == null ){
				x1 = rx1;
				y1 = ry1;
				x2 = rx2;
				y2 = ry2;
			}else{
				x1 = rx1 + inner.left;
				y1 = ry1 + inner.top;
				x2 = rx2 - inner.right;
				y2 = ry2 - inner.bottom;
			}
			apariencia.usar( gl );
			draw( gl, x0, y0, x1, y1, x2, y2, x3, y3 );
		}
		
		abstract void draw( GL2 gl, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3);
	}
	
	public static class Solid extends AbsBorder{

		private float r, g, b, a;
		
		public Solid( Color4f color ){
			this( new Insets( 1.0f ), color.x, color.y, color.z, color.w );
		}
		
		public Solid( Insets insets, Color4f color ){
			this( insets, color.x, color.y, color.z, color.w );
		}
		
		public Solid( Insets insets, float r, float g, float b, float a ){
			super( insets );
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
		
		@Override
		void draw( GL2 gl, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3){
			gl.glBegin(GL2.GL_QUADS);
				gl.glColor4f( r, g, b, a );
				// Left quad
				gl.glVertex2f ( x1, y2 );
				gl.glVertex2f ( x1, y1 );
				gl.glVertex2f ( x0, y0 );
				gl.glVertex2f ( x0, y3 );
				// Top quad
				gl.glVertex2f ( x1, y1 );
				gl.glVertex2f ( x2, y1 );
				gl.glVertex2f ( x3, y0 );
				gl.glVertex2f ( x0, y0 );
				// Right quad
				gl.glVertex2f ( x2, y1 );
				gl.glVertex2f ( x2, y2 );
				gl.glVertex2f ( x3, y3 );
				gl.glVertex2f ( x3, y0 );
				// Bottom quad
				gl.glVertex2f ( x2, y2 );
				gl.glVertex2f ( x1, y2 );
				gl.glVertex2f ( x0, y3 );
				gl.glVertex2f ( x3, y3 );
			gl.glEnd();
		}
	}
	
	public static class Gradient extends AbsBorder{
		
		private float rx0, gx0, bx0, ax0;
		private float ry0, gy0, by0, ay0;
		private float rx1, gx1, bx1, ax1;
		private float ry1, gy1, by1, ay1;
		private float rx2, gx2, bx2, ax2;
		private float ry2, gy2, by2, ay2;
		private float rx3, gx3, bx3, ax3;
		private float ry3, gy3, by3, ay3;
		
		public Gradient( Color4f eColor, Color4f iColor ){
			this( new Insets( 1.0f ), eColor, iColor, iColor, eColor );
		}
		
		public Gradient( Insets insets, Color4f eColor, Color4f iColor ){
			this( insets, eColor, iColor, iColor, eColor );
		}
		
		public Gradient( Color4f eltColor, Color4f iColor, Color4f erbColor ){
			this( new Insets( 1.0f ), eltColor, iColor, iColor, erbColor );
		}
		
		public Gradient( Insets insets, Color4f eltColor, Color4f iColor, Color4f erbColor ){
			this( insets, eltColor, iColor, iColor, erbColor );
		}
		
		public Gradient( Color4f eltColor, Color4f iltColor, Color4f irbColor, Color4f erbColor ){
			this( new Insets( 1.0f ), eltColor, iltColor, irbColor, erbColor );
		}
		
		public Gradient( Insets insets, Color4f eltColor, Color4f iltColor, Color4f irbColor, Color4f erbColor ){
			super( insets );
			this.setExternalLeftTopColor( eltColor );
			this.setInternalLeftTopColor( iltColor );
			this.setInternalRightBottomColor( irbColor );
			this.setExternalRightBottomColor( erbColor );
		}
		
		public void setInternalLeftTopColor( float r, float g, float b, float a){
			this.rx1 = this.ry1 = r;
			this.gx1 = this.gy1 = g;
			this.bx1 = this.by1 = b;
			this.ax1 = this.ay1 = a;
		}
		
		public void setInternalLeftTopColor( Color4f color ){
			setInternalLeftTopColor( color.x, color.y, color.z, color.w );
		}
		
		public void setInternalRightBottomColor( float r, float g, float b, float a){
			this.rx2 = this.ry2 = r;
			this.gx2 = this.gy2 = g;
			this.bx2 = this.by2 = b;
			this.ax2 = this.ay2 = a;
		}
		
		public void setInternalRightBottomColor( Color4f color ){
			setInternalRightBottomColor( color.x, color.y, color.z, color.w );
		}
		
		public void setInternalColor( float r, float g, float b, float a){
			setInternalLeftTopColor( r, g, b, a);
			setInternalRightBottomColor( r, g, b, a);
		}
		
		public void setInternalColor( Color4f color ){
			setInternalColor( color.x, color.y, color.z, color.w );
		}
		
		public void setExternalLeftTopColor( float r, float g, float b, float a){
			this.rx0 = this.ry0 = r;
			this.gx0 = this.gy0 = g;
			this.bx0 = this.by0 = b;
			this.ax0 = this.ay0 = a;
		}
		
		public void setExternalLeftTopColor( Color4f color ){
			setExternalLeftTopColor( color.x, color.y, color.z, color.w );
		}

		public void setExternalRightBottomColor( float r, float g, float b, float a){
			this.rx3 = this.ry3 = r;
			this.gx3 = this.gy3 = g;
			this.bx3 = this.by3 = b;
			this.ax3 = this.ay3 = a;
		}
		
		public void setExternalRightBottomColor( Color4f color ){
			setExternalRightBottomColor( color.x, color.y, color.z, color.w );
		}
		
		public void setExternalColor( float r, float g, float b, float a){
			setExternalLeftTopColor( r, g, b, a);
			setExternalRightBottomColor( r, g, b, a);
		}
		
		public void setExternalColor( Color4f color ){
			setExternalColor( color.x, color.y, color.z, color.w );
		}
		
		@Override
		void draw( GL2 gl, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3){
			gl.glBegin(GL2.GL_QUADS);
				// Left quad
				gl.glColor4f( rx1, gx1, bx1, ax1 );
				gl.glVertex2f ( x1, y2 );
				gl.glColor4f( rx1, gx1, bx1, ax1 );
				gl.glVertex2f ( x1, y1 );
				gl.glColor4f( rx0, gx0, bx0, ax0 );
				gl.glVertex2f ( x0, y0 );
				gl.glColor4f( rx0, gx0, bx0, ax0 );
				gl.glVertex2f ( x0, y3 );
				// Top quad
				gl.glColor4f( ry1, gy1, by1, ay1 );
				gl.glVertex2f ( x1, y1 );
				gl.glColor4f( ry1, gy1, by1, ay1 );
				gl.glVertex2f ( x2, y1 );
				gl.glColor4f( ry0, gy0, by0, ay0 );
				gl.glVertex2f ( x3, y0 );
				gl.glColor4f( ry0, gy0, by0, ay0 );
				gl.glVertex2f ( x0, y0 );
				// Right quad
				gl.glColor4f( rx2, gx2, bx2, ax2 );
				gl.glVertex2f ( x2, y1 );
				gl.glColor4f( rx2, gx2, bx2, ax2 );
				gl.glVertex2f ( x2, y2 );
				gl.glColor4f( rx3, gx3, bx3, ax3 );
				gl.glVertex2f ( x3, y3 );
				gl.glColor4f( rx3, gx3, bx3, ax3 );
				gl.glVertex2f ( x3, y0 );
				// Bottom quad
				gl.glColor4f( ry2, gy2, by2, ay2 );
				gl.glVertex2f ( x2, y2 );
				gl.glColor4f( ry2, gy2, by2, ay2 );
				gl.glVertex2f ( x1, y2 );
				gl.glColor4f( ry3, gy3, by3, ay3 );
				gl.glVertex2f ( x0, y3 );
				gl.glColor4f( ry3, gy3, by3, ay3 );
				gl.glVertex2f ( x3, y3 );
			gl.glEnd();
		}
	}
		
	public static class Textured extends AbsBorder{

		private final Pixmap leftTopPixmap,      topPixmap,     rightTopPixmap;
		private final Pixmap leftPixmap,                           rightPixmap;
		private final Pixmap leftBottomPixmap, bottomPixmap, rightBottomPixmap;
		
		public Textured( Insets border, int textureWidth, int textureHeight ){
			this( 
					border,	
					0.0f, 0.0f,
					border.left/textureWidth, border.top/textureHeight,
					1.0f - border.right/textureWidth, 1.0f - border.bottom/textureHeight,
					1.0f, 1.0f
			);
		}
		
		public Textured( Insets border, int textureX0, int textureY0, int textureX1, int textureY1, int textureWidth, int textureHeight ){
			this(
					border, 
					(float)textureX0/textureWidth,            (float)textureY0/textureHeight,
					( textureX0 + border.left)/textureWidth,  ( textureY0 + border.top )/textureHeight,
					( textureX1 - border.right)/textureWidth, ( textureY1 - border.bottom)/textureHeight,
					(float)textureX1/textureWidth,            (float)textureY1/textureHeight
			);
		}
		
		public Textured( Insets border, float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3 ){
			this(
					border,
					new Pixmap( u0, v0, u1 - u0, v1 - v0 ),
					new Pixmap( u1, v0, u2 - u1, v1 - v0 ),
					new Pixmap( u2, v0, u3 - u2, v1 - v0 ),
					
					new Pixmap( u0, v1, u1 - u0, v2 - v1 ),
					new Pixmap( u2, v1, u3 - u2, v2 - v1 ),
					
					new Pixmap( u0, v2, u1 - u0, v3 - v2 ),
					new Pixmap( u1, v2, u2 - u1, v3 - v2 ),
					new Pixmap( u2, v2, u3 - u2, v3 - v2 )
			);
		}
		
		private Textured(
				Insets border,
				Pixmap leftTopPixmap,      Pixmap topPixmap,     Pixmap rightTopPixmap,
				Pixmap leftPixmap,                                  Pixmap rightPixmap,
				Pixmap leftBottomPixmap, Pixmap bottomPixmap, Pixmap rightBottomPixmap
		){
			super( border );
			this.leftTopPixmap     = leftTopPixmap;
			this.topPixmap         = topPixmap;
			this.rightTopPixmap    = rightTopPixmap;
			this.leftPixmap        = leftPixmap;
			this.rightPixmap       = rightPixmap;
			this.leftBottomPixmap  = leftBottomPixmap;
			this.bottomPixmap      = bottomPixmap; 
			this.rightBottomPixmap = rightBottomPixmap;
		}
		
		@Override
		void draw( GL2 gl, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3){
			leftTopPixmap.drawCounterclockwise(     gl, x0, y0, x1, y1 );
			topPixmap.drawCounterclockwise(         gl, x1, y0, x2, y1 );
			rightTopPixmap.drawCounterclockwise(    gl, x2, y0, x3, y1 );
			leftPixmap.drawCounterclockwise(        gl, x0, y1, x1, y2 );
			rightPixmap.drawCounterclockwise(       gl, x2, y1, x3, y2 );
			leftBottomPixmap.drawCounterclockwise(  gl, x0, y2, x1, y3 );
			bottomPixmap.drawCounterclockwise(      gl, x1, y2, x2, y3 );
			rightBottomPixmap.drawCounterclockwise( gl, x2, y2, x3, y3 );
		}
		
	}
	
	/**
	 * Método que dibuja el borde alrededor en la zona rectagular
	 * delimitada por las coordenadas correspondientes.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * @param rx1 coordenada X de una de las esquinas.
	 * @param ry1 coordenada Y de una de las esquinas.
	 * @param rx2 coordenada X de la esquina opuesta.
	 * @param ry2 coordenada Y de la esquina opuesta.
	 */
	public void draw( GL2 gl, float rx1, float ry1, float rx2, float ry2 );
	
	public void setOuterInsets( Insets insets );
	
	public void setInnerInsets( Insets insets );
	
	public void setApariencia( Apariencia apariencia );

	public Insets getOuterInsets();
	
	public Insets getInnerInsets();
}
