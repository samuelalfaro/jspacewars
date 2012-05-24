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
package org.sam.jspacewars.servidor.tareas;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import org.sam.util.Reflexion;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class PruebaTareas {
	
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

	static Tarea[] getSubTareas( HierarchicalStreamReader reader, UnmarshallingContext context ){
		Collection<Tarea> subTareas = new LinkedList<Tarea>();
		while( reader.hasMoreChildren() ){
			reader.moveDown();
			Class<?> clazz;
			String className = reader.getNodeName();
			if( className.equalsIgnoreCase( "TareasParalelas" ) )
				clazz = TareasParalelas.class;
			else if( className.equalsIgnoreCase( "Secuencia" ) )
				clazz = Secuencia.class;
			else if( className.equalsIgnoreCase( "Esperar" ) )
				clazz = Esperar.class;
			else if( className.equalsIgnoreCase( "Mover" ) )
				clazz = Mover.class;
			else if( className.equalsIgnoreCase( "SetAnguloCanion" ) )
				clazz = SetAnguloCanion.class;
			else if( className.equalsIgnoreCase( "OrientarCanion" ) )
				clazz = OrientarCanion.class;
			else if( className.equalsIgnoreCase( "Disparar" ) )
				clazz = Disparar.class;
			else
				clazz = null;
			subTareas.add( (Tarea)context.convertAnother( null, clazz ) );
			reader.moveUp();
		}
		Tarea[] result = subTareas.toArray( new Tarea[subTareas.size()] );
		subTareas.clear();
		return result;
	}

	static long getLongAttribute( HierarchicalStreamReader reader, String att, long defecto ){
		try{
			return Long.parseLong( reader.getAttribute( att ) );
		}catch( Exception e ){
			return defecto;
		}
	}

	private static class TareasParalelasConverter implements Converter {

		public TareasParalelasConverter(){}

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return TareasParalelas.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			context.convertAnother(getSubTareas((TareasParalelas)value));
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new TareasParalelas(getSubTareas(reader, context));
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
			return new Secuencia(getSubTareas(reader, context));
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
			long duracion = getLongAttribute( reader, "duracion", -1);
			if(duracion < 0)
				return new Esperar(
						getLongAttribute( reader, "duracionMin", 0),
						getLongAttribute( reader, "duracionMax", 0)
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
			return new Mover( getLongAttribute( reader, "duracion", 0) );
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
			return new OrientarCanion( 0, 0.0f, getLongAttribute( reader, "duracion", 0) );
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
			return new Disparar( 0, getLongAttribute( reader, "duracion", 0) );
		}
	}
	
	public static void main(String[] args) {
	
		/*
		Tarea tarea = new TareasParalelas( new Tarea[]{
			new Mover(30),
			new Secuencia( new Tarea[]{
				new Esperar(10),
				new Esperar(3,5),
				new TareasParalelas( new Tarea[]{
					new Secuencia( new Tarea[]{
						new SetAnguloCanion(1,0),
						new OrientarCanion(1, 1, 5),
					}),
					new Disparar(1,5)
				})
			}),
			new Secuencia( new Tarea[]{
				new Esperar(15),
				new Esperar(3,5),
				new TareasParalelas( new Tarea[]{
					new Secuencia( new Tarea[]{
						new SetAnguloCanion(2,0),
						new OrientarCanion(2,-1,5),
					}),
					new Disparar(2,5)
				})	
			}),
		});
		/*/
		
		XStream xStream = new XStream(new DomDriver());
		
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
		
		String xmlIN = 
			"<TareasParalelas>"+
			"  <Mover duracion=\"30\"/>"+
			"  <Secuencia>"+
			"    <Esperar duracion=\"10\"/>"+
			"    <Esperar duracionMin=\"3\" duracionMax=\"5\"/>"+
			"    <TareasParalelas>"+
			"      <Secuencia>"+
			"        <SetAnguloCanion/>"+
			"        <OrientarCanion duracion=\"5\"/>"+
			"      </Secuencia>"+
			"      <Disparar duracion=\"5\"/>"+
			"    </TareasParalelas>"+
			"  </Secuencia>"+
			"  <Secuencia>"+
			"    <Esperar duracion=\"15\"/>"+
			"    <Esperar duracionMin=\"3\" duracionMax=\"5\"/>"+
			"    <TareasParalelas>"+
			"      <Secuencia>"+
			"        <SetAnguloCanion/>"+
			"        <OrientarCanion duracion=\"5\"/>"+
			"      </Secuencia>"+
			"      <Disparar duracion=\"5\"/>"+
			"    </TareasParalelas>"+
			"  </Secuencia>"+
			"</TareasParalelas>";
		
		Tarea tarea = (Tarea)xStream.fromXML(xmlIN);
		
		System.out.println(xStream.toXML(tarea));
		
		//*/
		
		Random r = new Random();
		long i = 0;
		while(i <= 30){
			System.out.println("\nInstante: " + i);
			long f = i + r.nextInt(4) +1;
			tarea.realizar( null, i, f );
			i = f;
		}
	}
}
