/* 
 * XStreamUtils.java
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
package org.sam.util;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * 
 */
public final class XStreamUtils{
	
	private XStreamUtils(){}
	
	public static double readAttribute( HierarchicalStreamReader reader, String name, double defaultValue ){
		String att = reader.getAttribute( name );
		if( att != null && att.length() > 0 )
			try{
				return Double.parseDouble( att );
			}catch( NumberFormatException e ){
			}
		return defaultValue;
	}
	
	public static float readAttribute( HierarchicalStreamReader reader, String name, float defaultValue ){
		String att = reader.getAttribute( name );
		if( att != null && att.length() > 0 )
			try{
				return (float)Double.parseDouble( att );
			}catch( NumberFormatException e ){
			}
		return defaultValue;
	}
	
	public static long readAttribute( HierarchicalStreamReader reader, String name, long defaultValue ){
		String att = reader.getAttribute( name );
		if( att != null && att.length() > 0 )
			try{
				return Long.parseLong( att );
			}catch( NumberFormatException e ){
			}
		return defaultValue;
	}

	public static int readAttribute( HierarchicalStreamReader reader, String name, int defaultValue ){
		String att = reader.getAttribute( name );
		if( att != null && att.length() > 0 )
			try{
				return Integer.parseInt( att );
			}catch( NumberFormatException e ){
			}
		return defaultValue;
	}

}
