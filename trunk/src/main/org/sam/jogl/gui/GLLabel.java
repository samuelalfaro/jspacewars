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

import javax.media.opengl.GL2;

import org.sam.jogl.gui.GLTextRenderer.HorizontalAlignment;
import org.sam.jogl.gui.GLTextRenderer.VerticalAlignment;

/**
 * 
 */
public class GLLabel extends GLComponent{
	
	private float textX;
	private float textY;
	
	private String text;
	private HorizontalAlignment horizontalAlignment;
	private VerticalAlignment verticalAlignment;
	private TextRendererProperties properties;

	public GLLabel( String text, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment ){
		this.setText( text );
		this.horizontalAlignment = horizontalAlignment;
		this.verticalAlignment = verticalAlignment;
		this.setPadding( 10 );
	}
	
	/* (non-Javadoc)
	 * @see org.sam.elementos.Initializable#init()
	 */
	@Override
	public void init(){
		this.setBackground( UIManager.getBackground( "Label.background.default" ) );
		this.setBorder( UIManager.getBorder( "Label.border.default" ) );
		this.setTextRendererProperties( UIManager.getTextRendererProperties( "Label.properties.default" ) );
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
	
	public final void setTextRendererProperties( TextRendererProperties properties ){
		this.properties = properties;
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
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.gui.GLComponent#drawComponent(javax.media.opengl.GL2)
	 */
	@Override
	public void drawComponent( GL2 gl ){
		if( text.length() > 0 && properties != null ){
			TEXT_RENDERER.setHorizontalAlignment( horizontalAlignment );
			TEXT_RENDERER.setVerticalAlignment( verticalAlignment );
			if( properties.shadowFont != null){
				TEXT_RENDERER.setColor( properties.shadowColor );
				TEXT_RENDERER.setFont( properties.shadowFont );
				TEXT_RENDERER.glPrint( gl, textX + properties.shadowOfsetX, textY + properties.shadowOfsetY, text );
			}
			TEXT_RENDERER.setColor( properties.color );
			TEXT_RENDERER.setFont( properties.font );
			TEXT_RENDERER.glPrint( gl, textX, textY, text );
			if( properties.fxFont != null){
				TEXT_RENDERER.setColor( properties.fxColor );
				TEXT_RENDERER.setFont( properties.fxFont );
				TEXT_RENDERER.glPrint( gl, textX + properties.fxOfsetX, textY + properties.fxOfsetY, text );
			}
		}
	}
}
