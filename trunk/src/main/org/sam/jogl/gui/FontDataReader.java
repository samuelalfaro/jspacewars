/* 
 * FontDataReader.java
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

import java.io.InputStream;

import org.sam.util.XStreamUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 */
final class FontDataReader{

	private static class CharacterPixmapConverter implements Converter{

		CharacterPixmapConverter(){
		}

		@SuppressWarnings( "rawtypes" )
		public boolean canConvert( Class clazz ){
			return FontData.CharacterData.class == clazz;
		}

		public void marshal( Object value, HierarchicalStreamWriter writer, MarshallingContext context ){
		}

		public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ){
			char c = 0;
			float x = XStreamUtils.readAttribute( reader, "x", 0.0f );
			float y = XStreamUtils.readAttribute( reader, "y", 0.0f );
			float width = XStreamUtils.readAttribute( reader, "width", 0.0f );
			float height = XStreamUtils.readAttribute( reader, "height", 0.0f );
			float offsetX = XStreamUtils.readAttribute( reader, "offsetX", 0.0f );
			float charWidth = XStreamUtils.readAttribute( reader, "charWidth", width );
			if( reader.hasMoreChildren() ){
				reader.moveDown();
				c = reader.getValue().charAt( 0 );
				reader.moveUp();
			}
			return new FontData.CharacterData( c, x, y, width, height, offsetX, charWidth );
		}
	}

	private static class FontConverter implements Converter{

		FontConverter(){
		}

		@SuppressWarnings( "rawtypes" )
		public boolean canConvert( Class clazz ){
			return FontData.class == clazz;
		}

		public void marshal( Object value, HierarchicalStreamWriter writer, MarshallingContext context ){
		}

		public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ){
			FontData fontData = new FontData(
					XStreamUtils.readAttribute( reader, "maxAscent", 0 ), XStreamUtils.readAttribute( reader, "maxDescent", 0 ),
					XStreamUtils.readAttribute( reader, "gap", 0 ),
					XStreamUtils.readAttribute( reader, "textureWidth", 256 ), XStreamUtils.readAttribute( reader, "textureHeight", 256 ),
					XStreamUtils.readAttribute( reader, "scaleX", 1.0f ), XStreamUtils.readAttribute( reader, "scaleY", 1.0f )
			);
			FontData.CharacterData charData;
			while( reader.hasMoreChildren() ){
				reader.moveDown();
				charData = (FontData.CharacterData)context.convertAnother( fontData, FontData.CharacterData.class );
				fontData.charactersData.add( charData );
				reader.moveUp();
			}
			return fontData;
		}
	}
	
	private FontDataReader(){}
	
	private static XStream xStream = null;

	private static XStream getXStream(){
		if( xStream == null ){
			xStream = new XStream( new DomDriver() );
			xStream.alias( "CharacterPixmap", FontData.CharacterData.class );
			xStream.registerConverter( new CharacterPixmapConverter() );
			xStream.alias( "Font", FontData.class );
			xStream.registerConverter( new FontConverter() );
		}
		return xStream;
	}

	public static FontData fromXML( InputStream xml ){
		return (FontData)getXStream().fromXML( xml );
	}
}
