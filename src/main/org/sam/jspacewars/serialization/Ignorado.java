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

import org.sam.jogl.Instancia3D;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@code Converter} que devuelve siempre null, para evitar cargar determinados
 * datos.<br/>
 * <u>Ejemplo:</u><pre><tt>
 * 	xStream.registerConverter( new Ignorado() );
 * 	xStream.alias( "datos_a_ignorar", Ignorado.class );
 * 	xStream.alias( "otros_datos_a_ignorar", Ignorado.class );
 * </tt></pre>
 */
public class Ignorado implements Converter {
	
	@SuppressWarnings( "rawtypes" )
	public boolean canConvert( Class clazz ){
		return Ignorado.class.isAssignableFrom( clazz );
	}

	public void marshal( Object value, HierarchicalStreamWriter writer, MarshallingContext context ){
	}

	public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ){
//		System.out.println( '<' + reader.getNodeName() + "/>" );
		return null;
	}
}
