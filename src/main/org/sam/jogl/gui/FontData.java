/* 
 * FontData.java
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

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 */
class FontData{
	
	static class CharacterData{

		static class Comparator implements java.util.Comparator<CharacterData>{
			public int compare( CharacterData o1, CharacterData o2 ){
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

		CharacterData( Character c, float x, float y, float width, float height, float offsetX, float charWidth ){
			this.c = c;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.offsetX = offsetX;
			this.charWidth = charWidth;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals( Object otro ){
			return ( otro != null ) && ( otro instanceof CharacterData )
					&& ( (CharacterData)otro ).c.equals( this.c );
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode(){
			return c.hashCode();
		}
	}
	
	final int maxAscent;
	final int maxDescent;
	final int gap;
	final int textureWidth;
	final int textureHeight;
	final float scaleX;
	final float scaleY;

	final SortedSet<CharacterData> charactersData;

	FontData( int maxAscent, int maxDescent, int gap, int textureWidth, int textureHeight, float scaleX,
			float scaleY ){
		this.maxAscent = maxAscent;
		this.maxDescent = maxDescent;
		this.gap = gap;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.charactersData = new TreeSet<CharacterData>( new CharacterData.Comparator() );
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
}
