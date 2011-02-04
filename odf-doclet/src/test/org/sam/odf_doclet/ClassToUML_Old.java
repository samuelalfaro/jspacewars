/* 
 * ClassToUML_Old.java
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

import java.io.IOException;
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

import org.apache.batik.transcoder.TranscoderException;
import org.sam.odf_doclet.bindings.ClassBinding;

@Deprecated
public class ClassToUML_Old {
	
	private static char getVisibility(int att){
		if( Modifier.isPublic(att) )
			return '+';
		if( Modifier.isProtected(att) )
			return '#';
		if( Modifier.isPrivate(att) )
			return '-';
		return '~';
	}
	
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
						ClassToUMLAdapter.toString(superClassType)
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
					out.format("%s<Enum><![CDATA[%s]]></Enum>\n",tabs, ClassToUMLAdapter.toString(enclosingClass));
				else if (enclosingClass.isInterface())
					out.format("%s<Interface><![CDATA[%s]]></Interface>\n",tabs, ClassToUMLAdapter.toString(enclosingClass));
				else
					out.format("%s<Class%s><![CDATA[%s]]></Class>\n",
							tabs,
							Modifier.isAbstract(enclosingClass.getModifiers()) ? " isAbstract=\"true\"" : "",
							ClassToUMLAdapter.toString(enclosingClass)
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
				out.format("%s<Interface><![CDATA[%s]]></Interface>\n",tabs, ClassToUMLAdapter.toString(implementedInterface));
			removeTab();
			out.println(tabs + "</Interfaces>");
		}
	}

	private static void printConstants(Collection<?> constants, PrintStream out){
		if( constants.size() > 0){
			out.println(tabs + "<Constants>");
			addTab();
				for(Object constant: constants)
					printConstant( constant, out );
			removeTab();
			out.println(tabs + "</Constants>");
		}
	}
	
	private static void printConstant(Object constant, PrintStream out){
		out.format("%1$s<Constant>\n%1$s\t<Signature><![CDATA[%2$s]]></Signature>\n%1$s</Constant>\n",
				tabs,
				constant.toString()	
		);
	}
	
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
		out.format("%1$s<Field visibility=\"%2$c\"%3$s>\n%1$s\t<Signature><![CDATA[%4$s]]></Signature>\n%1$s</Field>\n",
				tabs,
				getVisibility(modifiers),
				Modifier.isStatic(modifiers) ? " isStatic=\"true\"" : "",
				ClassToUMLAdapter.toString(field)		
		);
	}
	
	private static void printConstructors(Collection<Constructor<?>> constructors, PrintStream out){
		if( constructors.size() > 0){
			out.println(tabs + "<Constructors>");
			addTab();
			for(Constructor<?> constructor: constructors)
				print( constructor, out );
			removeTab();
			out.println(tabs + "</Constructors>");
		}
	}
	
	private static void print(Constructor<?> constructor, PrintStream out){
		int modifiers = constructor.getModifiers();
		out.format("%1$s<Constructor visibility=\"%2$c\">\n%1$s\t<Signature><![CDATA[%3$s]]></Signature>\n%1$s</Constructor>\n",
				tabs,
				getVisibility(modifiers),
				ClassToUMLAdapter.toString(constructor)	
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
		out.format("%1$s<Method visibility=\"%2$c\"%3$s%4$s>\n%1$s\t<Signature><![CDATA[%5$s]]></Signature>\n%1$s</Method>\n",
				tabs,
				getVisibility(modifiers),
				Modifier.isStatic(modifiers) ? " isStatic=\"true\"" : "",
				Modifier.isAbstract(modifiers) ? " isAbstract=\"true\"" : "",
				ClassToUMLAdapter.toString(method)		
		);
	}
	
	private static void printInterface(Class<?> clazz, PrintStream out){
		out.format("%1$s<Interface>\n%1$s\t<Signature><![CDATA[%2$s]]></Signature>\n",
				tabs,
				ClassToUMLAdapter.toString(clazz)
		);
		addTab();
		if( clazz.getTypeParameters().length > 0 )
			out.format("%1$s<Parameters>\n%1$s\t<Signature><![CDATA[%2$s]]></Signature>\n%1$s</Parameters>\n",
					tabs,
					ClassToUMLAdapter.toString(clazz.getTypeParameters())
			);

		printEnclosingClasses(clazz, out);
		printImplementedInterfaces(clazz, out);
		
		Queue<Field> fields = new ArrayDeque<Field>();
		for(Field field: clazz.getDeclaredFields()){
			if( !field.isSynthetic() )
				fields.offer( field );
		}
		printFields(fields, out);
		
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
		out.format("%1$s<Enum>\n%1$s\t<Signature><![CDATA[%2$s]]></Signature>\n",
				tabs,
				ClassToUMLAdapter.toString(clazz)
		);
		addTab();
		
		printEnclosingClasses(clazz, out);
		printImplementedInterfaces(clazz, out);
		
		printConstants(Arrays.asList(clazz.getEnumConstants()), out);
		
		Queue<Field> fields = new ArrayDeque<Field>();
		for(Field field: clazz.getDeclaredFields()){
			if(!field.isSynthetic() && !field.isEnumConstant())
				fields.offer( field );
		}
		printFields(fields, out);
		
		Queue<Method> methods = new ArrayDeque<Method>();
		for(Method method: clazz.getDeclaredMethods()){
			String methodString = ClassToUMLAdapter.toString(method);
			if(!method.isSynthetic() && !methodString.startsWith("values():") &&  !methodString.startsWith("valueOf( String ):") )
				methods.offer( method );
		}
		printMethods(methods, out);
		removeTab();
		out.println(tabs + "</Enum>");
	}
	
	private static void printClass(Class<?> clazz, PrintStream out){
		out.format("%1$s<Class%2$s>\n%1$s\t<Signature><![CDATA[%3$s]]></Signature>\n",
				tabs,
				Modifier.isAbstract(clazz.getModifiers()) ? " isAbstract=\"true\"" : "",
				ClassToUMLAdapter.toString(clazz)
				
		);
		addTab();
		if( clazz.getTypeParameters().length > 0 )
			out.format("%1$s<Parameters>\n%1$s\t<Signature><![CDATA[%2$s]]></Signature>\n%1$s</Parameters>\n",
					tabs,
					ClassToUMLAdapter.toString(clazz.getTypeParameters())
			);
	
		printHierarchy(clazz, out);
		printEnclosingClasses(clazz, out);
		printImplementedInterfaces(clazz, out);
		
		Queue<Field> fields = new ArrayDeque<Field>();
		for(Field field: clazz.getDeclaredFields()){
			if( !field.isSynthetic() )
				fields.offer( field );
		}
		printFields(fields, out);
		
		Queue<Constructor<?>> constructors = new ArrayDeque<Constructor<?>>();
		for(Constructor<?> constructor: clazz.getDeclaredConstructors()){
			if( !constructor.isSynthetic() )
				constructors.add( constructor );
		}
		printConstructors( constructors, out );
		
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
	 * @throws TranscoderException 
	 * @throws IOException 
	 */
	public static void main(String... args) throws ClassNotFoundException, TranscoderException, IOException {
//		Class<?> clazz = java.util.concurrent.ConcurrentHashMap.class;
		Class<?> clazz = pruebas.ClaseDePrueba.class;
		ClassBinding.from(clazz).toXML(System.out);
		toXML( clazz, System.out);
//		toPNG( java.util.concurrent.ConcurrentHashMap.class, new FileOutputStream("output/out.png") );
//		toPNG( pruebas.ClaseDePrueba.class, new FileOutputStream("output/out.png") );
	}
}
