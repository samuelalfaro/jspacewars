/* 
 * TextureTextRenderer.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of pruebas.
 * 
 * pruebas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * pruebas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with pruebas.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jogl.gui;

import java.nio.IntBuffer;
import java.util.Arrays;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.sam.jogl.Apariencia;

import com.jogamp.common.nio.Buffers;

public class TextureTextRenderer{

	public static enum HorizontalAlignment{
		LEFT, CENTER, RIGHT
	}

	public static enum VerticalAlignment{
		BASE_LINE, TOP, CENTER, BOTTOM
	}

	private static IntBuffer stringBuffer = Buffers.newDirectIntBuffer( 256 );

	private TextureFont font;
	private HorizontalAlignment horizontalAlignment;
	private VerticalAlignment verticalAlignment;
	private float r, g, b, a;
	private Apariencia apariencia;

	public TextureTextRenderer(){
		horizontalAlignment = HorizontalAlignment.LEFT;
		verticalAlignment = VerticalAlignment.BASE_LINE;
	}

	public void setFont( TextureFont font ){
		this.font = font;
	}

	public void setHorizontalAlignment( HorizontalAlignment horizontalAlignment ){
		this.horizontalAlignment = horizontalAlignment;
	}

	public void setVerticalAlignment( VerticalAlignment verticalAlignment ){
		this.verticalAlignment = verticalAlignment;
	}

	public void setColor( float r, float g, float b, float a ){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setApariencia( Apariencia apariencia ){
		this.apariencia = apariencia;
	}

	public void glPrint( GL2 gl, float x, float y, String string ){

		if( font == null )
			return;

		if( stringBuffer.capacity() < string.length() ){
			stringBuffer = Buffers.newDirectIntBuffer( string.length() );
		}

		stringBuffer.clear();
		float stringWidth = 0;
		for( int i = 0; i < string.length(); i++ ){
			char c = string.charAt( i );
			int charIndex = Arrays.binarySearch( font.characters, c );
			stringBuffer.put( charIndex < 0 ? font.unknownId: font.charactersIds[charIndex] );
			stringWidth += charIndex < 0 ? font.defaultWidth: font.charactersWidths[charIndex];
		}
		stringBuffer.flip();

		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glScalef( font.scaleX, font.scaleY, 1.0f );

		float posX = x / font.scaleX;

		switch( horizontalAlignment ){
		case LEFT:
			break;
		case CENTER:
			posX -= stringWidth / 2;
			break;
		case RIGHT:
			posX -= stringWidth;
			break;
		}

		float posY = y / font.scaleY;
		switch( verticalAlignment ){
		case BASE_LINE:
			break;
		case TOP:
			posY += font.maxAscent;
			break;
		case CENTER:
			posY += ( font.maxAscent - font.maxDescent ) / 2;
			break;
		case BOTTOM:
			posY -= font.maxDescent;
			break;
		}
		gl.glTranslatef( posX, posY, 0 );

		apariencia.usar( gl );
		gl.glColor4f( r, g, b, a );
		gl.glCallLists( string.length(), GL2ES2.GL_INT, stringBuffer );

		gl.glPopMatrix();
	}
}