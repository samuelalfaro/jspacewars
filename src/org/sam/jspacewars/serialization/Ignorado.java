/* 
 * Ignorado.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars.serialization;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class Ignorado implements Converter {
	
//	private static String tabs ="";
	
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return Ignorado.class == clazz;
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
//		System.out.println(tabs+'<'+reader.getNodeName()+'>');
		while(reader.hasMoreChildren()){
//			tabs = tabs.concat("   ");
			reader.moveDown();
			unmarshal(reader, context);
			reader.moveUp();
//			tabs = tabs.substring(0, tabs.length()-3);
		}
//		System.out.println(tabs+"</"+reader.getNodeName()+'>');
		return null;
	}
}
