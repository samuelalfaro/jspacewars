/* 
 * ClassToUML_XML.java
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

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Queue;

public class ClassToUML_XML {
	
	private static String tabs = "";
	
	private static void addTab(){
		tabs = tabs.concat("\t");
	}
	
	private static void removeTab(){
		tabs = tabs.substring(1);
	}
	
	private static Deque<Type> getHierarchy(Class<?> clazz){
		Deque<Type> hierarchy = new ArrayDeque<Type>();
		Type superClass = clazz.getGenericSuperclass();
		while( superClass != null && !superClass.equals(Object.class) ){
			if( superClass instanceof Class<?> ){
				hierarchy.push(superClass);
				superClass = ((Class<?>)superClass).getGenericSuperclass();
			}else if ( superClass instanceof ParameterizedType){
				Type rawType = ((ParameterizedType)superClass).getRawType();
				if( !rawType.equals(Enum.class) )
					hierarchy.push(superClass);
				superClass = ((Class<?>)rawType).getGenericSuperclass();
			}
			else
				superClass = null;
		}
		return hierarchy;
	}
	
	private static void printHierarchy(Class<?> clazz, PrintStream out){
		Deque<Type> hierarchy = getHierarchy(clazz);
		if( hierarchy.size() > 0){
			out.println( tabs + "<Hierarchy>");
			addTab();
			do{
				Type superClassType = hierarchy.poll();
				Class<?> superClass = (Class<?>)(superClassType instanceof ParameterizedType ? 
						((ParameterizedType)superClassType).getRawType():
						superClassType);
						
				out.format("%s<Class%s><![CDATA[%s]]></Class>\n",
						tabs,
						Modifier.isAbstract(superClass.getModifiers()) ? " isAbstract=\"true\"" : "",
						ClassToUML.toString(superClassType)
				);
			}while( hierarchy.size() > 0);
			removeTab();
			out.println( tabs + "</Hierarchy>");
		}
	}

	private static void printEnclosingClasses(Class<?> clazz, PrintStream out){
		Deque<Class<?>> enclosingClasses = new ArrayDeque<Class<?>>();
		Class<?> enclosingClass = clazz.getEnclosingClass();
		while( enclosingClass != null ){
			enclosingClasses.push(enclosingClass);
			enclosingClass = enclosingClass.getEnclosingClass();
		}
		if( enclosingClasses.size() > 0 ){
			out.println(tabs + "<EnclosingClasses>");
			addTab();
			do{
				enclosingClass = enclosingClasses.poll();
				
				if(enclosingClass.isEnum())
					out.format("%s<Enum><![CDATA[%s]]></Enum>\n",tabs, ClassToUML.toString(enclosingClass));
				else if (enclosingClass.isInterface())
					out.format("%s<Interface><![CDATA[%s]]></Interface>\n",tabs, ClassToUML.toString(enclosingClass));
				else
					out.format("%s<Class%s><![CDATA[%s]]></Class>\n",
							tabs,
							Modifier.isAbstract(enclosingClass.getModifiers()) ? " isAbstract=\"true\"" : "",
							ClassToUML.toString(enclosingClass)
					);
				
			}while( enclosingClasses.size() > 0 );
			removeTab();
			out.println(tabs + "</EnclosingClasses>");
		}
	}

	private static void printImplementedInterfaces(Class<?> clazz, PrintStream out){
		Deque<Type> interfaces = new ArrayDeque<Type>();
		for(Type parent: getHierarchy(clazz)){
			Class<?> superClass = null;
			if( parent instanceof Class<?> )
				superClass = (Class<?>)parent;
			else if ( parent instanceof ParameterizedType)
				superClass = (Class<?>)((ParameterizedType)parent).getRawType();
			for(Type implementedInterface: ((Class<?>) superClass).getGenericInterfaces())
				if( !interfaces.contains(implementedInterface))
					interfaces.add(implementedInterface);
		}
		for(Type implementedInterface: clazz.getGenericInterfaces())
			if( !interfaces.contains(implementedInterface))
				interfaces.add(implementedInterface);
		if(interfaces.size() > 0){
			out.println(tabs + "<Interfaces>");
			addTab();
			for(Type implementedInterface : interfaces)
				out.format("%s<Interface><![CDATA[%s]]></Interface>\n",tabs, ClassToUML.toString(implementedInterface));
			removeTab();
			out.println(tabs + "</Interfaces>");
		}
	}

	/*
	private static void printEnumConstant(Object constant, Class<?> enumClass, PrintStream out){
		System.err.println(constant);
		if( constant.getClass().equals(enumClass) )
			out.format("%s<Constant name=\"%s\"/>\n",tabs,constant.toString());
		else{
			out.format("%s<Constant name=\"%s\">\n",tabs,constant.toString());
			addTab();
				printEnumMethods( constant.getClass(), out );
			removeTab();
			out.println(tabs + "</Constant>");
		}
	}*/
	
	private static void printFields(Collection<Field> fields, PrintStream out){
		if( fields.size() > 0){
			out.println(tabs + "<Fields>");
			addTab();
			for(Field field: fields)
				print( field, out );
			removeTab();
			out.println(tabs + "</Fields>");
		}
	}
	
	private static void print(Field field, PrintStream out){
		int modifiers = field.getModifiers();
		out.format("%s<Field visibility=\"%c\"%s><![CDATA[%s]]></Field>\n",
				tabs,
				ClassToUML.getVisibility(modifiers),
				Modifier.isStatic(modifiers) ? " isStatic=\"true\"" : "",
				ClassToUML.toString(field)		
		);
	}
	
	private static void printConstructors(Collection<Constructor<?>> constructors, String classSimpleName, PrintStream out){
		if( constructors.size() > 0){
			out.println(tabs + "<Constructors>");
			addTab();
			for(Constructor<?> constructor: constructors)
				print( constructor, classSimpleName, out );
			removeTab();
			out.println(tabs + "</Constructors>");
		}
	}
	
	private static void print(Constructor<?> constructor, String classSimpleName, PrintStream out){
		int modifiers = constructor.getModifiers();
		out.format("%s<Constructor visibility=\"%c\"><![CDATA[%s]]></Constructor>\n",
				tabs,
				ClassToUML.getVisibility(modifiers),
				ClassToUML.toString(classSimpleName, constructor)		
		);
	}
	
	private static void printMethods(Collection<Method> methods, PrintStream out){
		if( methods.size() > 0){
			out.println(tabs + "<Methods>");
			addTab();
			for(Method method: methods)
				print( method, out );
			removeTab();
			out.println(tabs + "</Methods>");
		}
	}
	
	private static void print(Method method, PrintStream out){
		int modifiers = method.getModifiers();
		out.format("%s<Method visibility=\"%c\"%s%s><![CDATA[%s]]></Method>\n",
				tabs,
				ClassToUML.getVisibility(modifiers),
				Modifier.isStatic(modifiers) ? " isStatic=\"true\"" : "",
				Modifier.isAbstract(modifiers) ? " isAbstract=\"true\"" : "",
				ClassToUML.toString(method)		
		);
	}
	
	private static void printInterface(Class<?> clazz, PrintStream out){
		out.format("%s<Interface name=\"%s\">\n",
				tabs,
				ClassToUML.toString(clazz)
		);
		addTab();
		if( clazz.getTypeParameters().length > 0 )
			out.format("%s<Parameters><![CDATA[%s]]></Parameters>\n",
					tabs,
					ClassToUML.toString(clazz.getTypeParameters())
			);

		printEnclosingClasses(clazz, out);
		printImplementedInterfaces(clazz, out);
		
		printFields( Arrays.asList(clazz.getDeclaredFields()), out );
		
		Queue<Method> methods = new ArrayDeque<Method>();
		for(Method method: clazz.getDeclaredMethods()){
			if(!method.isSynthetic() )
				methods.offer( method );
		}
		printMethods(methods, out);
		removeTab();
		out.println(tabs + "</Interface>");
	}
	
	private static void printEnum(Class<?> clazz, PrintStream out){
		out.format("%s<Enum name=\"%s\">\n",
				tabs,
				ClassToUML.toString(clazz)
		);
		addTab();
		
		printEnclosingClasses(clazz, out);
		printImplementedInterfaces(clazz, out);
		
		for(Object constant: clazz.getEnumConstants())
			out.format("%s<Constant name=\"%s\"/>\n",tabs,constant.toString());
		
		Queue<Field> fields = new ArrayDeque<Field>();
		for(Field field: clazz.getDeclaredFields()){
			if(!field.isSynthetic() && !field.isEnumConstant())
				fields.offer( field );
		}
		printFields(fields, out);
		
		Queue<Method> methods = new ArrayDeque<Method>();
		for(Method method: clazz.getDeclaredMethods()){
			String methodString = ClassToUML.toString(method);
			if(!method.isSynthetic() && !methodString.startsWith("values():") &&  !methodString.startsWith("valueOf( String ):") )
				methods.offer( method );
		}
		printMethods(methods, out);
		removeTab();
		out.println(tabs + "</Enum>");
	}
	
	private static void printClass(Class<?> clazz, PrintStream out){
		out.format("%s<Class name=\"%s\"%s>\n",
				tabs,
				ClassToUML.toString(clazz),
				Modifier.isAbstract(clazz.getModifiers()) ? " isAbstract=\"true\"" : ""
		);
		addTab();
		if( clazz.getTypeParameters().length > 0 )
			out.format("%s<Parameters><![CDATA[%s]]></Parameters>\n",
					tabs,
					ClassToUML.toString(clazz.getTypeParameters())
			);
	
		printHierarchy(clazz, out);
		printEnclosingClasses(clazz, out);
		printImplementedInterfaces(clazz, out);
		
		printFields( Arrays.asList(clazz.getDeclaredFields()), out );
		
		printConstructors( Arrays.asList(clazz.getDeclaredConstructors()), clazz.getSimpleName(), out );
		
		Queue<Method> methods = new ArrayDeque<Method>();
		for(Method method: clazz.getDeclaredMethods()){
			if(!method.isSynthetic() )
				methods.offer( method );
		}
		printMethods(methods, out);
		removeTab();
		out.println(tabs + "</Class>");
	}
	
	private static void print(Class<?> clazz, PrintStream out){
		if( clazz.isInterface() )
			printInterface(clazz, out);
		else if( clazz.isEnum() )
			printEnum(clazz, out);
		else
			printClass(clazz, out);
	}
	
	public static void toXML(Class<?> clazz, OutputStream out) {
		if (out instanceof PrintStream)
			print(clazz, (PrintStream) out);
		else
			try {
				print(clazz, new PrintStream(out, false, "UTF-8"));
			} catch (UnsupportedEncodingException ignorada) {
			}
	}
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 */
	public static void main(String... args) throws ClassNotFoundException {
//		toXML( org.sam.interpoladores.GeneradorDeFunciones.Predefinido.class, System.out );
//		toXML( org.sam.tools.textureGenerator.ColorRamp.Predefinidas.class, System.out );
		toXML( Class.forName("org.sam.jogl.particulas.Estrellas"), System.out );
		toXML( java.util.concurrent.ArrayBlockingQueue.class, System.out );
	}
}
