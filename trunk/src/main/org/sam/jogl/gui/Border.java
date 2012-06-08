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
import org.sam.jogl.AtributosTransparencia;

/**
 * 
 */
public interface Border{
	
	public abstract static class AbsBorder implements Border{
		
		protected static final Apariencia apariencia = new Apariencia();
		
		static{
			apariencia.setAtributosTransparencia( 
					new AtributosTransparencia( 
							AtributosTransparencia.Equation.ADD,
							AtributosTransparencia.SrcFunc.SRC_ALPHA,
							AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA
					)
			);
		}
		
		Insets border;
		
		AbsBorder( Insets border ){
			this.border = border;
		}
		
		public final void setInsets( Insets border ){
			this.border = border;
		}
		
		public final Insets getInsets(){
			return border;
		}
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
			setColor( r, g, b, a );
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
		
		public void draw( GL2 gl, float x1, float y1, float x2, float y2 ){
			
			float x0 = x1 - border.left;
			float y0 = y1 - border.top;
			float x3 = x2 + border.right;
			float y3 = y2 + border.bottom;
			
			apariencia.usar( gl );
			gl.glBegin(GL2.GL_QUADS);
				gl.glColor4f( r, g, b, a );
				// Left quad
				gl.glVertex2f ( x1, y2 );
				gl.glVertex2f ( x0, y3 );
				gl.glVertex2f ( x0, y0 );
				gl.glVertex2f ( x1, y1 );
				// Top quad
				gl.glVertex2f ( x1, y1 );
				gl.glVertex2f ( x0, y0 );
				gl.glVertex2f ( x3, y0 );
				gl.glVertex2f ( x2, y1 );
				// Right quad
				gl.glVertex2f ( x2, y1 );
				gl.glVertex2f ( x3, y0 );
				gl.glVertex2f ( x3, y3 );
				gl.glVertex2f ( x2, y2 );
				// Bottom quad
				gl.glVertex2f ( x2, y2 );
				gl.glVertex2f ( x3, y3 );
				gl.glVertex2f ( x0, y3 );
				gl.glVertex2f ( x1, y2 );
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
		

		
		public void draw( GL2 gl, float x1, float y1, float x2, float y2 ){
			
			float x0 = x1 - border.left;
			float y0 = y1 - border.top;
			float x3 = x2 + border.right;
			float y3 = y2 + border.bottom;
			
			apariencia.usar( gl );
			gl.glBegin(GL2.GL_QUADS);
				// Left quad
				gl.glColor4f( rx1, gx1, bx1, ax1 );
				gl.glVertex2f ( x1, y2 );
				gl.glColor4f( rx0, gx0, bx0, ax0 );
				gl.glVertex2f ( x0, y3 );
				gl.glColor4f( rx0, gx0, bx0, ax0 );
				gl.glVertex2f ( x0, y0 );
				gl.glColor4f( rx1, gx1, bx1, ax1 );
				gl.glVertex2f ( x1, y1 );
				// Top quad
				gl.glColor4f( ry1, gy1, by1, ay1 );
				gl.glVertex2f ( x1, y1 );
				gl.glColor4f( ry0, gy0, by0, ay0 );
				gl.glVertex2f ( x0, y0 );
				gl.glColor4f( ry0, gy0, by0, ay0 );
				gl.glVertex2f ( x3, y0 );
				gl.glColor4f( ry1, gy1, by1, ay1 );
				gl.glVertex2f ( x2, y1 );
				// Right quad
				gl.glColor4f( rx2, gx2, bx2, ax2 );
				gl.glVertex2f ( x2, y1 );
				gl.glColor4f( rx3, gx3, bx3, ax3 );
				gl.glVertex2f ( x3, y0 );
				gl.glColor4f( rx3, gx3, bx3, ax3 );
				gl.glVertex2f ( x3, y3 );
				gl.glColor4f( rx2, gx2, bx2, ax2 );
				gl.glVertex2f ( x2, y2 );
				// Bottom quad
				gl.glColor4f( ry2, gy2, by2, ay2 );
				gl.glVertex2f ( x2, y2 );
				gl.glColor4f( ry3, gy3, by3, ay3 );
				gl.glVertex2f ( x3, y3 );
				gl.glColor4f( ry3, gy3, by3, ay3 );
				gl.glVertex2f ( x0, y3 );
				gl.glColor4f( ry2, gy2, by2, ay2 );
				gl.glVertex2f ( x1, y2 );
			gl.glEnd();
		}
	}
	
	public void draw( GL2 gl, float x1, float y1, float x2, float y2 );
	
	public void setInsets(Insets insets);
	
	public Insets getInsets();
}