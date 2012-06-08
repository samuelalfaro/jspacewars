/* 
 * GLButton.java
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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.sam.jogl.gui.GLTextRenderer.HorizontalAlignment;
import org.sam.jogl.gui.GLTextRenderer.VerticalAlignment;

/**
 * 
 */
public class GLLabel extends GLComponent{

	protected float textX;
	protected float textY;
	
	protected String text;
	protected HorizontalAlignment horizontalAlignment;
	protected VerticalAlignment verticalAlignment;

	public GLLabel( String text, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment ){
		setText( text );
		this.horizontalAlignment = horizontalAlignment;
		this.verticalAlignment = verticalAlignment;
		setPadding( 10 );
	}
            
	public GLLabel( String text, HorizontalAlignment horizontalAlignment ){
		this( text, horizontalAlignment, VerticalAlignment.CENTER );
	}

	public GLLabel( String text ){
		this( text, HorizontalAlignment.LEFT, VerticalAlignment.CENTER );
	}

    public GLLabel() {
    	this( "", HorizontalAlignment.LEFT, VerticalAlignment.CENTER );
    }
	
    private final void updateTextX(){
		switch( horizontalAlignment ){
		case LEFT:
			textX = x1 + padding.left;
			break;
		case CENTER:
			textX = ( x1 + padding.left + x2 - padding.right ) / 2;
			break;
		case RIGHT:
			textX = x2 - padding.right;
			break;
		}
    }
    
    private final void updateTextY(){
		switch( verticalAlignment ){
		case BASE_LINE:
			textY = y2 - padding.bottom;
			break;
		case TOP:
			textY = y1 + padding.top;
			break;
		case CENTER:
			textY = ( y1 + padding.top + y2 - padding.bottom ) / 2;
			break;
		case BOTTOM:
			textY = y2 - padding.bottom;
			break;
		}
    }
    
	private final void updateTextPosition(){
		updateTextX();
		updateTextY();
	}
    
    /* (non-Javadoc)
     * @see org.sam.jogl.gui.GLComponent#setPadding(float, float, float, float)
     */
    @Override
    public void setPadding( float leftPadding, float topPadding, float rightPadding, float bottomPadding ){
		super.setPadding( leftPadding, topPadding, rightPadding, bottomPadding );
		updateTextPosition();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.gui.GLRectangle#setBounds(float, float, float, float)
	 */
	@Override
	public void setBounds( float x, float y, float w, float h ){
		super.setBounds( x, y, w, h );
		updateTextPosition();
	}

	/*
	 * (non-Javadoc)
	 * @see org.sam.jogl.gui.GLComponent#setPosition(float, float)
	 */
	@Override
	public void setPosition( float x, float y ){
		super.setPosition( x, y );
		updateTextPosition();
	}
    
	public final void setHorizontalAlignment( HorizontalAlignment horizontalAlignment ){
		this.horizontalAlignment = horizontalAlignment;
		updateTextX();
	}
	
	public final void setVerticalAlignment( VerticalAlignment verticalAlignment ){
		this.verticalAlignment = verticalAlignment;
		updateTextY();
	}
	
	/**
	 * @param text valor del text asignado.
	 */
	public void setText( String text ){
		this.text = text;
	}
	
	/**
	 * @return el text solicitado.
	 */
	public String getText(){
		return text;
	}

	protected void draw( GL2 gl, float rf, float gf, float bf, float rb, float gb, float bb ){
		apariencia.usar( gl );
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f  ( rf, gf, bf );
			gl.glVertex2f ( x1, y1 );
			gl.glVertex2f ( x2, y1 );
			gl.glVertex2f ( x2, y2 );
			gl.glVertex2f ( x1, y2 );
		gl.glEnd();
		gl.glBegin( GL.GL_LINE_STRIP );
			gl.glColor3f  ( rb, gb, bb );
			gl.glVertex2f ( x1, y1 );
			gl.glVertex2f ( x2, y1 );
			gl.glVertex2f ( x2, y2 );
			gl.glVertex2f ( x1, y2 );
			gl.glVertex2f ( x1, y1 );
		gl.glEnd();
		gl.glBegin( GL.GL_LINE_STRIP );
			gl.glColor4f  ( rb, gb, bb, 0.5f );
			gl.glVertex2f ( x1 + padding.left, y1 +  padding.top );
			gl.glVertex2f ( x2 - padding.right, y1 + padding.top );
			gl.glVertex2f ( x2 - padding.right, y2 - padding.bottom );
			gl.glVertex2f ( x1 + padding.left, y2 - padding.bottom );
			gl.glVertex2f ( x1 + padding.left, y1 + padding.top );
		gl.glEnd();
	}
	
	protected final void printText( GL2 gl ){
		if( text.length() > 0 ){
			TEXT_RENDERER.setHorizontalAlignment( horizontalAlignment );
			TEXT_RENDERER.setVerticalAlignment( verticalAlignment );
			TEXT_RENDERER.glPrint( gl, textX, textY, text );
		}
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw( GL2 gl ){
		super.draw( gl );
		if( !isEnabled() ){
			draw( gl, 0.25f, 0.25f, 0.25f, 0.5f, 0.5f, 0.5f ); 	// desactivado
			TEXT_RENDERER.setColor( 0.5f, 0.5f, 0.5f,  1.0f );
		}else{
			draw( gl, 0.5f, 0.5f, 0.5f, 0.75f, 0.75f, 0.75f ); // default
			TEXT_RENDERER.setColor( 1.0f,  1.0f,  1.0f,  1.0f );
		}
		printText(gl);
	}
}
