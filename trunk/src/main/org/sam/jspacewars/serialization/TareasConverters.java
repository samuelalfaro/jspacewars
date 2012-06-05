/* 
 * PruebaTareas.java
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import org.sam.jspacewars.servidor.tareas.Disparar;
import org.sam.jspacewars.servidor.tareas.Esperar;
import org.sam.jspacewars.servidor.tareas.Mover;
import org.sam.jspacewars.servidor.tareas.OrientarCanion;
import org.sam.jspacewars.servidor.tareas.Secuencia;
import org.sam.jspacewars.servidor.tareas.SetAnguloCanion;
import org.sam.jspacewars.servidor.tareas.Tarea;
import org.sam.jspacewars.servidor.tareas.TareasParalelas;
import org.sam.util.Reflexion;
import org.sam.util.XStreamUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TareasConverters {
	
	static Tarea[] getSubTareas( Tarea t ){
		java.lang.reflect.Field f = Reflexion.findField( t.getClass(), "tareas" );
		Tarea[] array = null;
		try{
			boolean accesible = f.isAccessible();
			f.setAccessible( true );
			array = (Tarea[])f.get( t );
			f.setAccessible( accesible );
			return array;
		}catch( IllegalAccessException ignorada ){
			return null;
		}
	}

	static Tarea[] readSubTareas( HierarchicalStreamReader reader, UnmarshallingContext context ){
		Collection<Tarea> subTareas = new LinkedList<Tarea>();
		while( reader.hasMoreChildren() ){
			reader.moveDown();
			Class<?> clazz = null;
			String className = reader.getNodeName();
			try{
				clazz = Class.forName( className );
			}catch( ClassNotFoundException e ){
				try{
					clazz = Class.forName( Tarea.class.getPackage().getName() + "." + className );
				}catch( ClassNotFoundException e1 ){}
			}
			subTareas.add( (Tarea)context.convertAnother( null, clazz ) );
			reader.moveUp();
		}
		Tarea[] result = subTareas.toArray( new Tarea[subTareas.size()] );
		subTareas.clear();
		return result;
	}

	private static class TareasParalelasConverter implements Converter {

		public TareasParalelasConverter(){}

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return TareasParalelas.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			context.convertAnother( getSubTareas( (TareasParalelas)value ) );
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new TareasParalelas( readSubTareas( reader, context ) );
		}
	}
	
	private static class SecuenciaConverter implements Converter {

		public SecuenciaConverter(){}

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return Secuencia.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			context.convertAnother(getSubTareas((Secuencia)value));
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new Secuencia( readSubTareas(reader, context));
		}
	}
	
	private static class EsperarConverter implements Converter {

		public EsperarConverter(){}

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return Esperar.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Esperar tarea = (Esperar)value;
			long dMin = tarea.getDuracionMin();
			long dMax = tarea.getDuracionMax();
			if(dMin == dMax)
				writer.addAttribute("duracion", Long.toString(tarea.getDuracion()));
			else{
				writer.addAttribute("duracionMin", Long.toString(dMin));
				writer.addAttribute("duracionMax", Long.toString(dMax));
			}
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			long duracion = XStreamUtils.readAttribute( reader, "duracion", -1l );
			if(duracion < 0)
				return new Esperar(
						XStreamUtils.readAttribute( reader, "duracionMin", 0l ),
						XStreamUtils.readAttribute( reader, "duracionMax", 0l )
						);
			return new Esperar(duracion);
		}
	}
	
	private static class MoverConverter implements Converter {

		MoverConverter(){}

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return Mover.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Mover tarea = (Mover)value;
			writer.addAttribute("duracion", Long.toString(tarea.getDuracion()));
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new Mover( XStreamUtils.readAttribute( reader, "duracion", 0l ) );
		}
	}
	
	private static class SetAnguloCanionConverter implements Converter {

		SetAnguloCanionConverter(){}

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return SetAnguloCanion.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new SetAnguloCanion( 0, 0.0f );
		}
	}
	
	private static class OrientarCanionConverter implements Converter {

		OrientarCanionConverter(){}

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return OrientarCanion.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			OrientarCanion tarea = (OrientarCanion)value;
			writer.addAttribute("duracion", Long.toString(tarea.getDuracion()));
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new OrientarCanion( 0, 0.0f, XStreamUtils.readAttribute( reader, "duracion", 0l ) );
		}
	}
	
	private static class DispararConverter implements Converter {

		DispararConverter(){}

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return Disparar.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Disparar tarea = (Disparar)value;
			writer.addAttribute("duracion", Long.toString(tarea.getDuracion()));
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new Disparar( 0, XStreamUtils.readAttribute( reader, "duracion", 0l ) );
		}
	}
	
	public static void register(XStream xStream) {
		xStream.alias("TareasParalelas", TareasParalelas.class);
		xStream.registerConverter( new TareasParalelasConverter() );
		xStream.alias("Secuencia", Secuencia.class);
		xStream.registerConverter( new SecuenciaConverter() );
		xStream.alias("Esperar", Esperar.class);
		xStream.registerConverter( new EsperarConverter() );
		xStream.alias("Mover", Mover.class);
		xStream.registerConverter( new MoverConverter() );
		xStream.alias("SetAnguloCanion", SetAnguloCanion.class);
		xStream.registerConverter( new SetAnguloCanionConverter() );
		xStream.alias("OrientarCanion", OrientarCanion.class);
		xStream.registerConverter( new OrientarCanionConverter() );
		xStream.alias("Disparar", Disparar.class);
		xStream.registerConverter( new DispararConverter() );
	}
}
