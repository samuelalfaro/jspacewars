/* 
 * TextureFont.java
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

import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Arrays;

import javax.media.opengl.GL2;

import org.sam.jogl.Apariencia;

import com.jogamp.common.nio.Buffers;

public class TextureFont{

	private final char[] characters;
	private final int[]  charactersIds;
	private final int    unknownId;

	private final float[] charactersWidths;
	private final float defaultWidth;
	
	final float maxAscent;
	final float maxDescent;

	final float scaleX;
	final float scaleY;
	
	private Apariencia apariencia;

	private static void buildCharacter( GL2 gl, int listId, FontData fData, FontData.CharacterData cData ){
		float u1 = cData.x / fData.textureWidth;
		float v1 = cData.y / fData.textureHeight;
		float u2 = ( cData.x + cData.width ) / fData.textureWidth;
		float v2 = ( cData.y + cData.height ) / fData.textureHeight;

		float x1 = cData.offsetX / fData.scaleX;
		float x2 = ( cData.offsetX + cData.width ) / fData.scaleX;

		float y1 = ( fData.maxDescent - fData.maxAscent - cData.height ) / ( 2 * fData.scaleY );
		float y2 = ( fData.maxDescent - fData.maxAscent + cData.height ) / ( 2 * fData.scaleY );

		gl.glNewList( listId, GL2.GL_COMPILE );
		gl.glBegin( GL2.GL_QUADS );
		gl.glTexCoord2f( u1, v1 );
		gl.glVertex2f( x1, y1 );
		gl.glTexCoord2f( u1, v2 );
		gl.glVertex2f( x1, y2 );
		gl.glTexCoord2f( u2, v2 );
		gl.glVertex2f( x2, y2 );
		gl.glTexCoord2f( u2, v1 );
		gl.glVertex2f( x2, y1 );
		gl.glEnd();
		gl.glTranslated( ( cData.charWidth + fData.gap ) / fData.scaleX, 0, 0 );
		gl.glEndList();
	}
	
	public TextureFont( GL2 gl, FontData fData ){

		int size = fData.charactersData.size();
		int base = gl.glGenLists( size );
		int spaceIndex = 0;

		characters = new char[size];
		charactersIds = new int[size];
		charactersWidths = new float[size];

		int i = 0;
		while( !fData.charactersData.isEmpty() ){
			FontData.CharacterData cData = fData.charactersData.first();
			fData.charactersData.remove( cData );
			if( cData.c == ' ' ){
				spaceIndex = i;
				gl.glNewList( base + i, GL2.GL_COMPILE );
				gl.glTranslated( ( cData.charWidth + fData.gap ) / fData.scaleX, 0, 0 );
				gl.glEndList();
			}else
				buildCharacter( gl, base + i, fData, cData );

			characters[i] = cData.c;
			charactersIds[i] = base + i;
			charactersWidths[i] = ( cData.charWidth + fData.gap ) / fData.scaleX;
			i++;
		}

		unknownId = base + spaceIndex;
		defaultWidth = charactersWidths[spaceIndex];

		this.maxAscent  = fData.maxAscent / fData.scaleY;
		this.maxDescent = fData.maxDescent / fData.scaleY;

		// FIXME Las escalas cargadas se usan sólo para ajustar las proporciones.
		this.scaleX = 1.0f;
		this.scaleY = 1.0f;
	}

	public TextureFont( GL2 gl, InputStream xml ){
		this( gl, FontDataReader.fromXML( xml ) );
	}
	
	private TextureFont( TextureFont me, float scaleX, float scaleY ){
		this.characters = me.characters;
		this.charactersIds = me.charactersIds;
		this.unknownId = me.unknownId;

		this.charactersWidths = me.charactersWidths;
		this.defaultWidth = me.defaultWidth;
		this.maxAscent = me.maxAscent;
		this.maxDescent = me.maxDescent;

		this.scaleX = me.scaleX * scaleX;
		this.scaleY = me.scaleY * scaleY;
		
		this.apariencia = me.apariencia;
	}

	public TextureFont deriveFont( float scale ){
		return new TextureFont( this, scale, scale );
	}

	public TextureFont deriveFont( float scaleX, float scaleY ){
		return new TextureFont( this, scaleX, scaleY );
	}
	
	public void setApariencia( Apariencia apariencia ){
		this.apariencia = apariencia;
	}
	
	public Apariencia getApariencia(){
		return this.apariencia;
	}
	
	private transient IntBuffer stringBuffer = Buffers.newDirectIntBuffer( 256 );
	private transient String    lastString   = null;
	private transient float     stringWidth  = 0.0f;
	
	IntBuffer toBuffer( String string ){
		// TODO caché de cadenas usadas anteriormente
		if( string.length() == 0 || string.equals( lastString ) ){
			stringBuffer.flip();
			lastString = string;
			return stringBuffer;
		}
	
		if( stringBuffer.capacity() < string.length() ){
			stringBuffer = Buffers.newDirectIntBuffer( string.length() );
		}
		stringBuffer.clear();
		stringWidth = 0;
		for( int i = 0; i < string.length(); i++ ){
			char c = string.charAt( i );
			int charIndex = Arrays.binarySearch( characters, c );
			stringBuffer.put( charIndex < 0 ? unknownId: charactersIds[charIndex] );
			stringWidth += charIndex < 0 ? defaultWidth: charactersWidths[charIndex];
		}
		stringBuffer.flip();
		lastString = string;
		return stringBuffer;
	}
	
	float getWidth( String string ){
		if( string == null || string.length() == 0 )
			return 0;
		if( string.equals( lastString ) )
			return stringWidth;
			
		stringWidth = 0;
		for( int i = 0; i < string.length(); i++ ){
			char c = string.charAt( i );
			int charIndex = Arrays.binarySearch( characters, c );
			stringWidth += charIndex < 0 ? defaultWidth: charactersWidths[charIndex];
		}
		return stringWidth;
	}
}