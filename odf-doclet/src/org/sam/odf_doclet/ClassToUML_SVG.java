/* 
 * ClassToUML_SVG.java
 * 
 * Copyright (c) 2010 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of tips.
 * 
 * tips is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * tips is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tips.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odf_doclet;

import java.awt.Dimension;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Deprecated
//TODO mirar plantillas velocity
public class ClassToUML_SVG {
	
	private static class StringPrintStream extends PrintStream {
		StringPrintStream(){
			super(new ByteArrayOutputStream());
		}
		public String getString(){
			try {
				return ((ByteArrayOutputStream)out).toString("UTF8");
			} catch (UnsupportedEncodingException ignorada) {
				return ((ByteArrayOutputStream)out).toString();
			}
		}
	}

	/*
	private interface Interface<T>{
	}
	
	@SuppressWarnings("unused")
	private interface MiInterface{
		public static final int CONSTANTE1 = 0;
		public static final int CONSTANTE2 = 0;
		
		public void metodo1();
		public void metodo2();
	}
	
	private interface OtroInterface extends MiInterface{
		public void metodo3();
	}
	
	@SuppressWarnings("unused")
	private static abstract class Prueba<T> implements OtroInterface{
		
		public static final int CONSTANTE = 0;
		
		private String field1;
		protected int field2;
		final T[] field3;
		public double[] field4[];
		public Interface<?> field5;
		public Interface<? super T> field6;
		public Interface<? extends T> field7;
		Getter.Double<Float> field8;
		
		Prueba(T[] param){
			this.field3 = param;
		}
		
		Prueba(String param1, int param2){
			this(null);
			this.field1 = param1;
			this.field2 = param2;
		}
		
		public void metodo1(T param1, int param2){
			
		}
		
		protected void metodo1(String param1, int param2){
			
		}
		
		private void metodo1(String param1, T param2, int... args){
			
		}
		
		public void metodo2(){
		}
		
	}
	*/
	
	private final static float iconWidth = SVGProperties.getProperty("iconWidth",20.0f);
	private final static float monospaceCharWidth = SVGProperties.getProperty("monospaceCharWidth",7.25f);
	private final static float monospaceCharHeight = SVGProperties.getProperty("monospaceCharHeight",20.0f);
	private final static float monospaceCharBaseLine = SVGProperties.getProperty("monospaceCharBaseLine",13.0f);

	private final static String fileTemplate;
	static{
		String template = SVGProperties.getTemplate("file");
		template = template.replace("%stylesheet%", "%1$s");
		template = template.replace("%content%", "%2$s");
		template = template.replace("%width%", "%3$d");
		template = template.replace("%height%", "%4$d");
		template = template.replace("%viewBoxWidth%", "%5$d");
		template = template.replace("%viewBoxHeight%", "%6$d");
		fileTemplate  = template;
	}
	
	private final static String stylesheetTemplate =
		SVGProperties.getTemplate("stylesheet").replace("%stylesheet%", "%1$s");
	
	private final static String useTemplate;
	static{
		String template = SVGProperties.getTemplate("use");
		template = template.replace("%x%", "%1$d");
		template = template.replace("%y%", "%2$d");
		template = template.replace("%link%", "%3$s");
		useTemplate  = template;
	}
	
	private final static String attTemplate; 
	static{
		String template = SVGProperties.getTemplate("att");
		template = template.replace("%attId%", "%1$s");
		template = template.replace("%attValue%", "%2$s");
		attTemplate  = template;
	}
	
	private final static String textTemplate;
	static{
		String template = SVGProperties.getTemplate("text");
		template = template.replace("%x%", "%1$d");
		template = template.replace("%y%", "%2$d");
		template = template.replace("%text%", "%3$s");
		template = template.replace("%classAtt%", "%4$s");
		template = template.replace("%idAtt%", "%5$s");
		textTemplate  = template;
	}
	
	private static char getVisibility(int att){
		if( Modifier.isPublic(att) )
			return '+';
		if( Modifier.isProtected(att) )
			return '~';
		if( Modifier.isPrivate(att) )
			return '-';
		return '#';
	}
	
	private static String getBulletField(int x, int y, int att ){
		StringBuffer key = new StringBuffer( Modifier.isStatic(att) ? "S": "" );
		key.append(getVisibility(att)).append('f');
		return String.format(useTemplate, x, y, SVGProperties.getProperty(key.toString()));
	}
	
	private static String getBulletConstructor(int x, int y, int att ){
		StringBuffer key = new StringBuffer();
		key.append(getVisibility(att)).append('c');
		return String.format(useTemplate, x, y, SVGProperties.getProperty(key.toString()) );
	}
	
	private static String getBulletMethod(int x, int y, int att ){
		StringBuffer key = new StringBuffer( Modifier.isStatic(att) ? "S": Modifier.isAbstract(att) ? "A": "" );
		key.append(getVisibility(att)).append('m');
		return String.format(useTemplate, x, y, SVGProperties.getProperty(key.toString()));
	}
	
	private static String getTextTag(int x, int y, String text, String classAtt, String idAtt){
		return String.format(textTemplate, x, y, text,
				classAtt != null ? String.format( attTemplate, "class", classAtt ) : "",
				idAtt != null ? String.format( attTemplate, "id", idAtt ) : ""
		);
	}
	
	/**
	 * @param out  
	 */
	private static Dimension drawSuperClasses(Class<?> clazz, PrintStream out){
		clazz.getClasses();
		return null;
		//TODO hacer
	}

	/**
	 * @param out  
	 */
	private static Dimension drawEnclosingClasses(Class<?> clazz, PrintStream out){
		Class<?> enclosingClass = clazz.getEnclosingClass();
		while(enclosingClass != null){
			enclosingClass = enclosingClass.getEnclosingClass();
		}
		return null;
		//TODO hacer
	}

	/**
	 * @param out  
	 */
	private static Dimension drawImplementedInterfaces(Class<?> clazz, PrintStream out){
		clazz.getClasses();
		return null;
		//TODO hacer
	}

	private static Dimension drawHeader(Class<?> clazz, PrintStream out){
		// TODO adaptar SVG
		if(clazz.isInterface())
			out.println("<<Interface>>");
		else if(clazz.isEnum())
			out.println("<<Enum>>");
		if(clazz.getTypeParameters().length > 0)
			out.println(ClassToUMLAdapter.toString(clazz) + "<" + ClassToUMLAdapter.toString(clazz.getTypeParameters()) + ">");
		else
			out.println(ClassToUMLAdapter.toString(clazz));

		return new Dimension();
	}
	
	private static int draw(int pos, Field field, PrintStream out){
		String stringField = ClassToUMLAdapter.toString(field);
		int modifiers = field.getModifiers();
		out.println( getBulletField(
				0,
				(int)(pos * monospaceCharHeight+ 0.5f),
				modifiers )
		);
		out.println( getTextTag(
				(int)iconWidth,
				(int)( pos* monospaceCharHeight + monospaceCharBaseLine + 0.5f),
				stringField,
				Modifier.isStatic(modifiers) ? "static": null,
				null)
		);
		return stringField.length();
	}
	
	private static Dimension drawFields(Class<?> clazz, PrintStream out){
		int max = 0;
		Field[] fields = clazz.getDeclaredFields();
		for(int i = 0; i < fields.length; i++ ){
			int l = draw( i, fields[i], out );
			if(l > max)
				max = l;
		}
		return new Dimension( (int)(max*monospaceCharWidth + 0.5f),(int)( fields.length * monospaceCharHeight + 0.5f) );
	}
	
	private static int draw(int pos, String classSimpleName, Constructor<?> constructor, PrintStream out){
		String stringConstructor = ClassToUMLAdapter.toString(classSimpleName, constructor);
		out.println(getBulletConstructor(
				0,
				(int)(pos * monospaceCharHeight+ 0.5f),
				constructor.getModifiers())
		);
		out.println( getTextTag(
				(int)iconWidth,
				(int)( pos* monospaceCharHeight + monospaceCharBaseLine + 0.5f),
				stringConstructor,
				null,
				null)
		);
		return stringConstructor.length();
	}
	
	private static int draw(int pos, Method method, PrintStream out){
		String stringMethod = ClassToUMLAdapter.toString(method);
		int modifiers = method.getModifiers();
		out.println( getBulletMethod(
				0,
				(int)(pos * monospaceCharHeight+ 0.5f),
				method.getModifiers())
		);
		out.println( getTextTag(
				(int)iconWidth,
				(int)( pos* monospaceCharHeight + monospaceCharBaseLine + 0.5f),
				stringMethod,
				Modifier.isStatic(modifiers) ? "static": Modifier.isAbstract(modifiers) ?  "abstract" : null,
				null)
		);
		return stringMethod.length();
	}
	
	private static Dimension drawMethods(Class<?> clazz, PrintStream out){
		int max = 0, pos = 0;
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for(int i = 0; i < constructors.length; i++, pos++ ){
			int l = draw( i, clazz.getSimpleName(), constructors[i], out );
			if(l > max)
				max = l;
		}
		Method[] methods = clazz.getDeclaredMethods();
		for(int i = 0; i < methods.length; i++ ){
			if(!methods[i].isSynthetic()){
				draw( pos, methods[i], out );
				pos++;
			}
		}
		return new Dimension((int)( max * monospaceCharWidth + 0.5f),(int)( pos * monospaceCharHeight + 0.5f));
	}
	
	private static Dimension draw(Class<?> clazz, PrintStream out){
		StringPrintStream sps = new StringPrintStream();
		Dimension dHeader  = drawHeader(clazz, sps);
		// TODO hacer grupos
		sps.println("\n\t Aquí creamos un grupo desplazado: " + dHeader.height );
		Dimension dFields  = drawFields(clazz, sps);
		// TODO hacer grupos
		sps.println("\n\t Aquí creamos un grupo desplazado: " + (dHeader.height + dFields.height) );
		Dimension dMethods = drawMethods(clazz, sps);
		out.print(sps.getString());
		return new Dimension(
				Math.max(dHeader.width, Math.max(dFields.width, dMethods.width)),
				dHeader.height + dFields.height + dMethods.height
		);
	}
	
	private static String getStylesheet(){
		String stylesheet = SVGProperties.getProperty("stylesheet");
		if( stylesheet != null)
			return String.format(stylesheetTemplate, stylesheet);
		return "";
	}
	
	public static Image drawUML(Class<?> clazz, PrintStream out){
		StringPrintStream sps = new StringPrintStream();
		drawSuperClasses(clazz, sps);
		drawEnclosingClasses(clazz, sps);
		drawImplementedInterfaces(clazz, sps);
		Dimension dClazz = draw(clazz, sps);
		out.printf(fileTemplate, getStylesheet(), sps.getString(), dClazz.width*2, dClazz.height*2, dClazz.width, dClazz.height);
		return null;
	}
}
