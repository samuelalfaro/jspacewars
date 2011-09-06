/* 
 * Font.java
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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.media.opengl.GL2;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Font{
	
	private static XStream xStream = null;
	
	private static final XStream getXStream(){
		if( xStream == null ){
			xStream = new XStream(new DomDriver());
			xStream.alias( "CharacterPixmap", CharacterPixmapData.class );
			xStream.registerConverter( new CharacterPixmapConverter() );
			xStream.alias( "Font", FontData.class );
			xStream.registerConverter( new FontConverter() );
		}
		return xStream;
	}
	
	static float readAttribute( HierarchicalStreamReader reader, String name, float defaultValue ){
		String att = reader.getAttribute( name );
		if( att != null && att.length() > 0 )
			try{
				return (float)Double.parseDouble( att );
			}catch( NumberFormatException e ){
			}
		return defaultValue;
	}
	
	static int readAttribute( HierarchicalStreamReader reader, String name, int defaultValue ){
		String att = reader.getAttribute( name );
		if( att != null && att.length() > 0 )
			try{
				return Integer.parseInt( att );
			}catch( NumberFormatException e ){
			}
		return defaultValue;
	}
	
	private static class CharacterPixmapData{
		
		static class Comparator implements java.util.Comparator<CharacterPixmapData>{
		    public int compare(CharacterPixmapData o1, CharacterPixmapData o2){
		    	return o1.c - o2.c;
		    }
		}
		
		final Character c;
		final float x;
		final float y;
		final float width;
		final float height;
		final float offsetX;
		final float charWidth;
		
		CharacterPixmapData( Character c, float x, float y, float width, float height, float offsetX, float charWidth ){
			this.c = c;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.offsetX = offsetX;
			this.charWidth = charWidth;
		}
		
		void buidCharacterPixmap( 
				GL2 gl, int listId,
				int fontAscent, int fontDescent, int gap, 
				int textureWidth, int textureHeight, float scaleX, float scaleY
		){
			float u1 = x / textureWidth;
			float v1 = y / textureHeight;
			float u2 = ( x + width ) / textureWidth;
			float v2 = ( y + height ) / textureHeight;
			
			float x1 = offsetX * scaleX;
			float x2 = ( offsetX + width ) * scaleX;
			
			float y1 = -scaleY * ( height + fontAscent - fontDescent ) / 2;
			float y2 =  scaleY * ( height - fontAscent + fontDescent ) / 2;

			gl.glNewList( listId, GL2.GL_COMPILE );
				gl.glBegin( GL2.GL_QUADS );
					gl.glTexCoord2f( u1, v1 );
					gl.glVertex2f( x1, y1 );
					gl.glTexCoord2f( u2, v1 );
					gl.glVertex2f( x2, y1 );
					gl.glTexCoord2f( u2, v2 );
					gl.glVertex2f( x2, y2 );
					gl.glTexCoord2f( u1, v2 );
					gl.glVertex2f( x1, y2 );
				gl.glEnd();
				gl.glTranslated( ( charWidth + gap ) * scaleX, 0, 0 );
			gl.glEndList();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals( Object otro ){
			return
				( otro != null ) &&
				( otro instanceof CharacterPixmapData) &&
				( (CharacterPixmapData)otro ).c.equals( this.c );
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode(){
			return c.hashCode();
		}
	}
	
	private static class CharacterPixmapConverter implements Converter{

		CharacterPixmapConverter(){
		}

		@SuppressWarnings( "rawtypes" )
		public boolean canConvert( Class clazz ){
			return CharacterPixmapData.class == clazz;
		}

		public void marshal( Object value, HierarchicalStreamWriter writer, MarshallingContext context ){
		}
		
		public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ){
			char c = 0;
			float x = readAttribute( reader, "x", 0.0f );
			float y = readAttribute( reader, "y", 0.0f );
			float width  = readAttribute( reader, "width", 0.0f );
			float height = readAttribute( reader, "height", 0.0f );
			float offsetX  = readAttribute( reader, "offsetX", 0.0f );
			float charWidth = readAttribute( reader, "charWidth", width );
			if( reader.hasMoreChildren() ){
				reader.moveDown();
				c = reader.getValue().charAt( 0 );
				reader.moveUp();
			}
			return new CharacterPixmapData( c, x, y, width, height, offsetX, charWidth );
		}
	}
	
	private static class FontData{

		final int   maxAscent;
		final int   maxDescent;
		final int   gap;
		final int   textureWidth;
		final int   textureHeight;
		final float scaleX;
		final float scaleY;
		
		final SortedSet<CharacterPixmapData> charactersData;
		
		FontData( int maxAscent, int maxDescent, int gap, int textureWidth, int textureHeight, float scaleX, float scaleY ){
			this.maxAscent      = maxAscent;
			this.maxDescent     = maxDescent;
			this.gap            = gap;
			this.textureWidth   = textureWidth;
			this.textureHeight  = textureHeight;
			this.charactersData = new TreeSet<CharacterPixmapData>( new CharacterPixmapData.Comparator() );
			this.scaleX         = scaleX;
			this.scaleY         = scaleY;
		}
	}
	
	private static class FontConverter implements Converter {
		
		FontConverter(){
		}
		
		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return FontData.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			FontData fontData = new FontData(
				readAttribute( reader, "maxAscent", 0 ),
				readAttribute( reader, "maxDescent", 0 ),
				readAttribute( reader, "gap", 0 ),
				readAttribute( reader, "textureWidth", 256 ),
				readAttribute( reader, "textureHeight", 256 ),
				readAttribute( reader, "scaleX", 1.0f ),
				readAttribute( reader, "scaleY", 1.0f )
			);
			CharacterPixmapData charData;
			while (reader.hasMoreChildren()){
				reader.moveDown();
					charData = (CharacterPixmapData)context.convertAnother( fontData, CharacterPixmapData.class );
					fontData.charactersData.add( charData );
				reader.moveUp();
			}
			return fontData;
		}
	}
	
	final char[]  characters;
	final int[]   charactersIds;
	final int     unknownId;
	
	final float[] charactersWidths;
	final float   defaultWidth;
	final float   maxAscent;
	final float   maxDescent;
	
	final float   scaleX;
	final float   scaleY;
	
	public Font( GL2 gl, InputStream xml ){
		this( gl, (FontData)getXStream().fromXML( xml ) );
	}
	
	private Font( GL2 gl, FontData data ){
		
		int size = data.charactersData.size();
		int base = gl.glGenLists( size );
		int spaceIndex = 0;
		
		characters       = new char [ size ];
		charactersIds    = new int  [ size ];
		charactersWidths = new float[ size ];
		
		int i = 0;
		while( !data.charactersData.isEmpty() ){
			CharacterPixmapData cData = data.charactersData.first();
			data.charactersData.remove( cData );
			if( cData.c == ' ' ){
				spaceIndex = i;
				gl.glNewList( base + i, GL2.GL_COMPILE );
					gl.glTranslated( ( cData.charWidth + data.gap )/data.scaleX, 0, 0 );
				gl.glEndList();
			}else
				cData.buidCharacterPixmap( 
						gl, base + i,  
						data.maxAscent, data.maxDescent, data.gap, 
						data.textureWidth, data.textureHeight, 1.0f/data.scaleX, 1.0f/data.scaleY
				);
			
			characters[i]       = cData.c;
			charactersIds[i]    = base + i;
			charactersWidths[i] = ( cData.charWidth + data.gap ) / data.scaleX;
			i++;
		}
		
		unknownId = base + spaceIndex;
		defaultWidth = charactersWidths[ spaceIndex ];
		
		this.maxAscent  = data.maxAscent  / data.scaleY;
		this.maxDescent = data.maxDescent / data.scaleY;
	
		// FIXME La escala cargada sirven para ajustar las proporciones
		// la escala orginal es totalmente arbitraria.
		this.scaleX     = 1.0f;
		this.scaleY     = 1.0f;
	}
	
	private Font( Font me, float scaleX, float scaleY ){
		this.characters       = me.characters;
		this.charactersIds    = me.charactersIds;
		this.unknownId        = me.unknownId;
		
		this.charactersWidths = me.charactersWidths;
		this.defaultWidth     = me.defaultWidth;
		this.maxAscent        = me.maxAscent;
		this.maxDescent       = me.maxDescent;
		
		this.scaleX           = me.scaleX * scaleX;
		this.scaleY           = me.scaleY * scaleY;
	}
	
	public Font deriveFont( float scale ){
		return new Font( this, scale, scale );
	}
	
	public Font deriveFont( float scaleX, float scaleY ){
		return new Font( this, scaleX, scaleY );
	}
}