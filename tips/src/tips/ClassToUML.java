/* 
 * ClassToUML.java
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
package tips;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

import org.sam.interpoladores.GeneradorDeFunciones;
import org.sam.tools.textureGenerator.ColorRamp;

public class ClassToUML {
	
	private static String tabs = "";
	
	static void addTab(){
		tabs = tabs.concat("\t");
	}
	static void removeTab(){
		tabs = tabs.substring(1);
	}
	
	private static Class<?> getPrimaryComponentType(Class<?> clazz){
		if( clazz.isArray() )
			return getPrimaryComponentType(clazz.getComponentType());
		return clazz;
	}
	
	public static String toString(Type[] params, boolean isVarArgs){
		String stringParams = "";
		for(int i = 0; i < params.length; ){
			stringParams += toString(params[i]);
			if(++i < params.length)
				stringParams += ", ";
		}
		if(isVarArgs)
			return stringParams.substring(0, stringParams.length() - 2).concat("...");
		return stringParams;
	}

	public static String toString(TypeVariable<?>[] typeParameters){
		String stringParams = "";
		for(int i = 0; i < typeParameters.length; ){
			stringParams += toString(typeParameters[i]);
			if(++i < typeParameters.length)
				stringParams += ", ";
		}
		return stringParams;
	}
	
	public static String toString(Type type){
		if(type instanceof Class<?>)
			return toString((Class<?>)type);
		if(type instanceof ParameterizedType)
			return toString((ParameterizedType)type);
		return type.toString();
	}

	public static String toString(Class<?> clazz){
		String canonicalName = clazz.getCanonicalName();
		if(canonicalName == null)
			return "Clase Anónima";
		Package pack = clazz.isArray() ? 
				getPrimaryComponentType(clazz).getPackage():
				clazz.getPackage();
		if(pack == null)
			return canonicalName;
		return  canonicalName.substring(pack.getName().length() + 1);
	}
	
	public static String toString(ParameterizedType type){
		return toString((Class<?>)type.getRawType()) + 
				"<" + toString(type.getActualTypeArguments(), false) + ">";
	}
	
	public static String toString(Field field){
		return String.format("%s: %s", field.getName(), toString(field.getGenericType()) );
	}

	public static String toString(String classSimpleName, Constructor<?> constructor){
		return String.format( "%1$s(%2$s%3$s%2$s)", 
				classSimpleName,
				constructor.getGenericParameterTypes().length > 0 ? " ":"",
				toString(constructor.getGenericParameterTypes(), constructor.isVarArgs())
		);
	}

	public static String toString(Method method) {
		String returnType = toString( method.getGenericReturnType() );
		return String.format( "%1$s(%2$s%3$s%2$s)%4$s", 
				method.getName(),
				method.getGenericParameterTypes().length > 0 ? " ":"",
				toString( method.getGenericParameterTypes(), method.isVarArgs() ),
				returnType.equals("void") ? "" : ": " + returnType 
		);
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
	
	private static void printHierarchy(Class<?> clazz, PrintStream out){
		
		Deque<Type> hierarchy = new ArrayDeque<Type>();
		Type superClass = clazz.getGenericSuperclass();
		while( superClass != null && !superClass.equals(Object.class) ){
			hierarchy.push(superClass);
			if( superClass instanceof Class<?> )
				superClass = ((Class<?>)superClass).getGenericSuperclass();
			else if ( superClass instanceof ParameterizedType)
				superClass = ((Class<?>)((ParameterizedType)superClass).getRawType()).getGenericSuperclass();
			else
				superClass = null;
		}
		if( hierarchy.size() > 0){
			out.println( tabs + "<Hierarchy>");
			addTab();
			do{
				superClass = hierarchy.poll();
				out.format("%s<Class><\\![CDATA[%s]]></Class>\n",tabs, toString(superClass));
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
		if( enclosingClasses.size() > 0){
			out.println(tabs + "<EnclosingClasses>");
			addTab();
			do{
				enclosingClass = enclosingClasses.poll();
				out.format("%s<Class><\\![CDATA[%s]]></Class>\n",tabs,toString(enclosingClass));
			}while( enclosingClasses.size() > 0);
			removeTab();
			out.println(tabs + "</EnclosingClasses>");
		}
	}

	private static void printImplementedInterfaces(Class<?> clazz, PrintStream out){
		Type[] interfaces = clazz.getGenericInterfaces();
		if(interfaces.length > 0){
			out.println(tabs + "<Interfaces>");
			addTab();
			for(Type implementedInterface : interfaces)
				out.format("%s<Interface><\\![CDATA[%s]]></Interface>\n",tabs,toString(implementedInterface));
			removeTab();
			out.println(tabs + "</Interfaces>");
		}
	}

	private static void printHeader(Class<?> clazz, PrintStream out){
		if(clazz.isInterface())
			out.println("<<Interface>>");
		else if(clazz.isEnum())
			out.println("<<Enum>>");
		if(clazz.getTypeParameters().length > 0)
			out.println(toString(clazz) + "<" + toString(clazz.getTypeParameters()) + ">");
		else
			out.println(toString(clazz));
	}
	
	private static void printEnumConstant(Object constant, Class<?> enumClass, PrintStream out){
//		System.err.println(constant);
		if( constant.getClass().equals(enumClass) )
			out.format("%s<Constant name=\"%s\"/>\n",tabs,constant.toString());
		else{
			out.format("%s<Constant name=\"%s\">\n",tabs,constant.toString());
			addTab();
				printEnumMethods( constant.getClass(), out );
			removeTab();
			out.println(tabs + "</Constant>");
		}
	}
	
	private static void printEnumFields(Class<?> clazz, PrintStream out){
		Queue<Field> fields = new ArrayDeque<Field>();
		for(Field field: clazz.getDeclaredFields()){
			if(!field.isSynthetic() && !field.isEnumConstant())
				fields.offer( field );
		}
		if( fields.size() > 0){
			out.println(tabs + "<Fields>");
			addTab();
			do{
				print( fields.poll(), out );
			}while( fields.size() > 0);
			removeTab();
			out.println(tabs + "<Fields>");
		}
	}
	private static void printEnumMethods(Class<?> clazz, PrintStream out){
		Queue<Method> methods = new ArrayDeque<Method>();
		for(Method method: clazz.getDeclaredMethods()){
			String methodString = toString(method);
			if(!method.isSynthetic() && !methodString.startsWith("values():") &&  !methodString.startsWith("valueOf( String ):") )
				methods.offer( method );
		}
		if( methods.size() > 0){
			out.println(tabs + "<Methods>");
			addTab();
			do{
				print( methods.poll(), out );
			}while( methods.size() > 0);
			removeTab();
			out.println(tabs + "</Methods>");
		}
	}
	private static void print(Field field, PrintStream out){
		int modifiers = field.getModifiers();
		out.format("%s<Field visibility=\"%c\"%s><\\![CDATA[%s]]></Field>\n",
				tabs,
				getVisibility(modifiers),
				Modifier.isStatic(modifiers) ? " isStatic=\"true\"" : "",
				toString(field)		
		);
	}
	
	private static void printFields(Class<?> clazz, PrintStream out){
		Field[] fields = clazz.getDeclaredFields();
		if(fields.length > 0){
			out.println(tabs + "<Fields>");
			addTab();
			for(Field field: fields)
				print( field, out );
			removeTab();
			out.println(tabs + "<Fields>");
		}
	}
	
	private static void print(String classSimpleName, Constructor<?> constructor, PrintStream out){
		int modifiers = constructor.getModifiers();
		out.format("%s<Constructor visibility=\"%c\"><\\![CDATA[%s]]></Constructor>\n",
				tabs,
				getVisibility(modifiers),
				toString(classSimpleName, constructor)		
		);
	}
	
	private static void print(Method method, PrintStream out){
		int modifiers = method.getModifiers();
		out.format("%s<Method visibility=\"%c\"%s%s><\\![CDATA[%s]]></Method>\n",
				tabs,
				getVisibility(modifiers),
				Modifier.isStatic(modifiers) ? " isStatic=\"true\"" : "",
				Modifier.isAbstract(modifiers) ? " isAbstract=\"true\"" : "",
				toString(method)		
		);
	}
	
	private static void printMethods(Class<?> clazz, PrintStream out){
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		if(constructors.length > 0){
			out.println( tabs + "<Constructors>");
			addTab();
			for(Constructor<?> constructor: constructors ){
				print(clazz.getSimpleName(), constructor, out );
			}
			removeTab();
			out.println( tabs + "</Constructors>");
		}
		Queue<Method> methods = new ArrayDeque<Method>();
		for(Method method: clazz.getDeclaredMethods()){
			if(!method.isSynthetic() )
				methods.offer( method );
		}
		if( methods.size() > 0){
			out.println(tabs + "<Methods>");
			addTab();
			do{
				print( methods.poll(), out );
			}while( methods.size() > 0);
			removeTab();
			out.println(tabs + "</Methods>");
		}
	}
	
	private static void printInterface(Class<?> clazz, PrintStream out){
		//TODO
		printClass( clazz, out );
	}
	
	private static void printClass(Class<?> clazz, PrintStream out){
		printHeader(clazz, out);
		printFields(clazz, out);
		printMethods(clazz, out);
	}
	
	private static void printEnum(Class<?> clazz, PrintStream out){
		printHeader(clazz, out);
		for(Object constant: clazz.getEnumConstants())
			printEnumConstant(constant, clazz, out);
		printEnumFields(clazz, out);
		printEnumMethods(clazz, out);
	}
	
	public static void printUML(Class<?> clazz, PrintStream out){
		if(clazz.isInterface()){
			printEnclosingClasses(clazz, out);
			printImplementedInterfaces(clazz, out);
			printInterface(clazz, out);
		}else if( clazz.isEnum() ){
			printEnclosingClasses(clazz, out);
			printImplementedInterfaces(clazz, out);
			printEnum(clazz, out);
		}else{
			printHierarchy(clazz, out);
			printEnclosingClasses(clazz, out);
			printImplementedInterfaces(clazz, out);
			printClass(clazz, out);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String... args) {
		printUML(GeneradorDeFunciones.Predefinido.class, System.out);
		printUML(ColorRamp.Predefinidas.class, System.out);
	}
}
