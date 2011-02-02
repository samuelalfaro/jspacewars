/* 
 * ClassBinding.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of odf-doclet.
 * 
 * odf-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * odf-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with odf-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odf_doclet.bindings;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.sam.odf_doclet.ClassToUMLAdapter;

import com.sun.javadoc.ClassDoc;

/**
 * 
 */
final class TabsSingleton{
	
	private TabsSingleton(){
	}
	
	static String tabs = "";
	
	final static void addTab(){
		tabs = tabs.concat("\t");
	}
	
	final static void removeTab(){
		tabs = tabs.substring(1);
	}
}

/**
 * 
 */
enum Visibility{

	PRIVATE('-'),
	PACKAGE('~'),
	PROTECTED('#'),
	PUBLIC('+');
	
	public static Visibility fromModifiers(int att){
		if( Modifier.isPublic(att) )
			return PUBLIC;
		if( Modifier.isProtected(att) )
			return PROTECTED;
		if( Modifier.isPrivate(att) )
			return PRIVATE;
		return PACKAGE;
	}
	
	private final char c;
	
	private Visibility(char c){
		this.c = c;
	}
	
	public final char toChar(){
		return c;
	}
}

/**
 * 
 */
abstract class Element {
	
	final String signature;
	
	Element(String signature){
		this.signature = signature;
	}
	
	/**
	 * @param <T>
	 * @param collection
	 * @param name
	 * @param out
	 */
	final static <T extends Element> void printCollection(Collection<T> collection, String name, PrintStream out){
		if( collection!= null && collection.size() > 0 ){
			out.format("%s<%s>\n", TabsSingleton.tabs, name);
			TabsSingleton.addTab();
			for(T element: collection)
				element.toXML(out);
			TabsSingleton.removeTab();
			out.format("%s</%s>\n", TabsSingleton.tabs, name);
		}
	}
	
	final void printSignature(PrintStream out){
		if(signature != null && signature.length() > 0)
			out.format("%s<Signature><![CDATA[%s]]></Signature>\n",
					TabsSingleton.tabs,
					signature
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if( o == null)
			return false;
		if( !SimpleBinding.class.isAssignableFrom(o.getClass()) )
			return false;
		return this.signature.equals( ((Element)o).signature );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return signature.hashCode();
	}
	
	public abstract void toXML(PrintStream out);
}

/**
 * 
 */
abstract class DocumentedElement extends Element {
	
	final String documentation;
	
	DocumentedElement(String signature, String documentation){
		super(signature);
		this.documentation = documentation;
	}
	
	protected final void printDocumentation(PrintStream out){
		if(documentation != null && documentation.length() > 0)
			out.format("%s<Documentation><![CDATA[%s]]></Documentation>\n",
					TabsSingleton.tabs,
					documentation
			);
	}
}

/**
 * 
 */
abstract class SimpleBinding extends Element {
	
	static final SimpleBinding from(Type type){
		
		if(type instanceof Class<?>){
			Class<?> clazz = (Class<?>)type;
			if(clazz.isInterface())
				return new SimpleInterfaceBinding( type );
			if(clazz.isEnum())
				return new SimpleEnumBinding( type );
			return new SimpleClassBinding( type );
		}
		if(type instanceof ParameterizedType){
			Class<?> clazz = (Class<?>)((ParameterizedType)type).getRawType();
			if(clazz.isInterface())
				return new SimpleInterfaceBinding( type );
			return new SimpleClassBinding( type );
		}
		return null;
	}
	
	protected SimpleBinding(String signature) {
		super(signature);
	}
}

/**
 * 
 */
class SimpleInterfaceBinding extends SimpleBinding{
	
	SimpleInterfaceBinding(String signature){
		super( signature );
	}
	
	SimpleInterfaceBinding(Type type){
		super( ClassToUMLAdapter.toString(type) );
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Interface><![CDATA[%s]]></Interface>\n",
				TabsSingleton.tabs,
				signature
		);
	}
}

/**
 * 
 */
class SimpleEnumBinding extends SimpleBinding{
	
	SimpleEnumBinding(String signature){
		super( signature );
	}
	
	SimpleEnumBinding(Type type){
		super( ClassToUMLAdapter.toString(type) );
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Enum><![CDATA[%s]]></Enum>\n",
				TabsSingleton.tabs,
				signature
		);
	}
}

class SimpleClassBinding extends SimpleBinding{

	boolean isAbstract;
	
	SimpleClassBinding(String signature, boolean isAbstract){
		super( signature );
		this.isAbstract = isAbstract;
	}
	
	SimpleClassBinding(Type type){
		super( ClassToUMLAdapter.toString(type) );
		if(type instanceof Class<?>)
			this.isAbstract = Modifier.isAbstract( ((Class<?>)type).getModifiers() );
		else if(type instanceof ParameterizedType)
			this.isAbstract = Modifier.isAbstract( ((Class<?>)((ParameterizedType)type).getRawType()).getModifiers() );
		else
			throw new IllegalArgumentException();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Class%s><![CDATA[%s]]></Class>\n",
				TabsSingleton.tabs,
				isAbstract ? " isAbstract=\"true\"" : "",
				signature
		);
	}
}

class ParametersBinding extends Element {

	ParametersBinding(String signature) {
		super(signature);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Parameters>\n", TabsSingleton.tabs);
		TabsSingleton.addTab();
			printSignature(out);
		TabsSingleton.removeTab();
		out.format("%s</Parameters>\n", TabsSingleton.tabs);
	}
}


/**
 * 
 */
class ConstantBinding extends DocumentedElement{

	ConstantBinding(String signature, String documentation) {
		super(signature, documentation);
	}

	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Constant>\n", TabsSingleton.tabs);
		TabsSingleton.addTab();
			printSignature(out);
			printDocumentation(out);
		TabsSingleton.removeTab();
		out.format("%s</Constant>\n", TabsSingleton.tabs);
	}
}

/**
 * 
 */
class FieldBinding extends DocumentedElement{

	Visibility visibility;
	boolean isStatic;
	
	FieldBinding(Field field){
		super(ClassToUMLAdapter.toString(field), null);
		int modifiers = field.getModifiers();
		this.visibility = Visibility.fromModifiers(modifiers);
		this.isStatic   = Modifier.isStatic(modifiers);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Field visibility=\"%c\"%s>\n",
				TabsSingleton.tabs,
				visibility.toChar(),
				isStatic ? " isStatic=\"true\"" : ""
		);
		TabsSingleton.addTab();
			printSignature(out);
			printDocumentation(out);
		TabsSingleton.removeTab();
		out.format("%s</Field>\n", TabsSingleton.tabs);
	}
}

/**
 * 
 */
class ConstructorBinding extends DocumentedElement{

	Visibility visibility;
	
	ConstructorBinding(Constructor<?> constructor){
		super(ClassToUMLAdapter.toString( constructor ), null);
		this.visibility = Visibility.fromModifiers( constructor.getModifiers() );
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Constructor visibility=\"%c\">\n",
				TabsSingleton.tabs,
				visibility.toChar()
		);
		TabsSingleton.addTab();
			printSignature(out);
			printDocumentation(out);
		TabsSingleton.removeTab();
		out.format("%s</Constructor>\n", TabsSingleton.tabs);
	}
}

/**
 * 
 */
class MethodBinding extends DocumentedElement{

	Visibility visibility;
	boolean isStatic;
	boolean isAbstract;
	
	MethodBinding(Method method){
		super( ClassToUMLAdapter.toString(method), null );
		int modifiers = method.getModifiers();
		this.visibility = Visibility.fromModifiers(modifiers);
		this.isStatic   = Modifier.isStatic(modifiers);
		this.isAbstract = Modifier.isAbstract(modifiers);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Method visibility=\"%c\"%s>\n",
				TabsSingleton.tabs,
				visibility.toChar(),
				isStatic ? " isStatic=\"true\"" : isAbstract ? " isAbstract=\"true\"" : ""
		);
		TabsSingleton.addTab();
			printSignature(out);
			printDocumentation(out);
		TabsSingleton.removeTab();
		out.format("%s</Method>\n", TabsSingleton.tabs);
	}
}

/**
 * 
 */
public abstract class ClassBinding extends DocumentedElement{
	
	static Collection<Type> getHierarchy(Class<?> clazz){
		Deque<Type> hierarchy = new ArrayDeque<Type>();
		Type superClass = clazz.getGenericSuperclass();
		while( superClass != null && !superClass.equals(Object.class) ){
			if( superClass instanceof Class<?> ){
				hierarchy.offerFirst(superClass);
				superClass = ((Class<?>)superClass).getGenericSuperclass();
			}else if ( superClass instanceof ParameterizedType){
				Type rawType = ((ParameterizedType)superClass).getRawType();
				if( !rawType.equals(Enum.class) )
					hierarchy.offerFirst(superClass);
				superClass = ((Class<?>)rawType).getGenericSuperclass();
			}
			else
				superClass = null;
		}
		return hierarchy;
	}
	
	static Collection<SimpleBinding> getEnclosingClasses(Class<?> clazz){
		Deque<SimpleBinding> enclosingClasses = new ArrayDeque<SimpleBinding>();
		Class<?> enclosingClass = clazz.getEnclosingClass();
		while( enclosingClass != null ){
			enclosingClasses.offerFirst( SimpleBinding.from(enclosingClass) );
			enclosingClass = enclosingClass.getEnclosingClass();
		}
		return enclosingClasses;
	}
	
	static Collection<SimpleInterfaceBinding> getImplementedInterfaces(Class<?> clazz){
		Deque<SimpleInterfaceBinding> interfaces = new ArrayDeque<SimpleInterfaceBinding>();
		for(Type parent: getHierarchy(clazz)){
			Class<?> superClass = null;
			if( parent instanceof Class<?> )
				superClass = (Class<?>)parent;
			else if ( parent instanceof ParameterizedType)
				superClass = (Class<?>)((ParameterizedType)parent).getRawType();
			
			for(Type implementedInterface: ((Class<?>) superClass).getGenericInterfaces())
				if( !interfaces.contains(implementedInterface))
					interfaces.offerLast( new SimpleInterfaceBinding(implementedInterface) );
		}
		for(Type implementedInterface: clazz.getGenericInterfaces())
			if( !interfaces.contains(implementedInterface))
				interfaces.offerLast( new SimpleInterfaceBinding(implementedInterface) );
		return interfaces;
	}
	
	static Collection<FieldBinding> getFields(Class<?> clazz){
		Deque<FieldBinding> fields = new ArrayDeque<FieldBinding>();
		for(Field field: clazz.getDeclaredFields()){
			if( !field.isSynthetic() && !field.isEnumConstant() )
				fields.offer( new FieldBinding(field) );
		}
		return fields;
	}
	
	static Collection<ConstructorBinding> getConstructors(Class<?> clazz){
		Deque<ConstructorBinding> constructors = new ArrayDeque<ConstructorBinding>();
		for(Constructor<?> constructor: clazz.getDeclaredConstructors()){
			if( !constructor.isSynthetic() )
				constructors.add( new ConstructorBinding( constructor ) );
		}
		return constructors;
	}
	
	static Collection<MethodBinding> getMethods(Class<?> clazz){
		Deque<MethodBinding> methods = new ArrayDeque<MethodBinding>();
		if(clazz.isEnum())
			for(Method method: clazz.getDeclaredMethods()){
				String methodString = ClassToUMLAdapter.toString(method);
				if(!method.isSynthetic() && !methodString.startsWith("values():") &&  !methodString.startsWith("valueOf( String ):") )
					methods.offer( new MethodBinding(method) );
			}
		else
			for(Method method: clazz.getDeclaredMethods()){
				if(!method.isSynthetic() )
					methods.offer( new MethodBinding(method) );
			}
		return methods;
	}
	
	public final static ClassBinding from(Class<?> clazz){
		if(clazz.isInterface())
			return new InterfaceBinding( clazz );
		if(clazz.isEnum())
			return new EnumBinding( clazz );
		return new ConcreteClassBinding( clazz );
	}
	
	public final static ClassBinding from(ClassDoc clazz){
		return null;
	}
	
	Collection<SimpleBinding> enclosingClasses;
	Collection<SimpleInterfaceBinding> interfaces;
	
	Collection<FieldBinding> fields;
	Collection<MethodBinding> methods;
	
	ClassBinding(String signature, String documentation) {
		super(signature, documentation);
	}

}

class ConcreteClassBinding extends ClassBinding{
	
	static Collection<SimpleClassBinding> getHierarchyBinding(Class<?> clazz){
		Deque<SimpleClassBinding> hierarchy = new ArrayDeque<SimpleClassBinding>();
		for(Type type: getHierarchy(clazz))
			hierarchy.offer( new SimpleClassBinding(type) );

		return hierarchy;
	}
	
	boolean isAbstract;
	ParametersBinding parameters;

	Collection<SimpleClassBinding> hierarchy;
	Collection<ConstructorBinding> constructors;
	
	ConcreteClassBinding(Class<?> clazz){
		super( ClassToUMLAdapter.toString(clazz), null);
		this.isAbstract = Modifier.isAbstract( clazz.getModifiers() );
		this.parameters = clazz.getTypeParameters().length > 0 ? 
				new ParametersBinding( ClassToUMLAdapter.toString(clazz.getTypeParameters()) ):
				null;
		
		this.hierarchy = getHierarchyBinding(clazz);
		this.enclosingClasses = getEnclosingClasses(clazz);
		this.interfaces = getImplementedInterfaces(clazz);
		
		this.fields = getFields(clazz);
		this.constructors = getConstructors(clazz);
		this.methods = getMethods(clazz);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Class%s>\n", TabsSingleton.tabs, isAbstract ? " isAbstract=\"true\"" : "");
		TabsSingleton.addTab();
			printSignature(out);
			if(parameters != null)
				parameters.toXML(out);
			printDocumentation(out);
			
			printCollection( hierarchy, "Hierarchy", out );
			printCollection( enclosingClasses, "EnclosingClasses", out );
			printCollection( interfaces, "Interfaces", out );
			printCollection( fields, "Fields", out );
			printCollection( constructors, "Constructors", out );
			printCollection( methods, "Methods", out );
		
		TabsSingleton.removeTab();
		out.format("%s</Class>\n", TabsSingleton.tabs);
	}
}

class EnumBinding extends ClassBinding{
	
	static Collection<ConstantBinding> getConstants(Class<?> clazz){
		Deque<ConstantBinding> constants = new ArrayDeque<ConstantBinding>();
		for(Object constant: clazz.getEnumConstants()){
			constants.offerLast( new ConstantBinding(constant.toString(), null) );
		}
		return constants;
	}
	
	Collection<ConstantBinding> constants;
	Collection<ConstructorBinding> constructors;
	
	EnumBinding(Class<?> clazz){
		super( ClassToUMLAdapter.toString(clazz), null);
		
		this.enclosingClasses = getEnclosingClasses(clazz);
		this.interfaces = getImplementedInterfaces(clazz);
		
		this.constants = getConstants(clazz);
		this.fields = getFields(clazz);
		this.constructors = getConstructors(clazz);
		this.methods = getMethods(clazz);
	}

	/* (non-Javadoc)
	 * @see org.saHierarchym.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Enum>\n", TabsSingleton.tabs);
		TabsSingleton.addTab();
			printSignature(out);
			printDocumentation(out);
			
			printCollection( enclosingClasses, "EnclosingClasses", out );
			printCollection( interfaces, "Interfaces", out );
			printCollection( constants, "Constants", out );
			printCollection( fields, "Fields", out );
			printCollection( constructors, "Constructors", out );
			printCollection( methods, "Methods", out );
			
		TabsSingleton.removeTab();
		out.format("%s</Enum>\n", TabsSingleton.tabs);
	}
}

class InterfaceBinding extends ClassBinding{

	ParametersBinding parameters;
	
	InterfaceBinding(Class<?> clazz){
		super( ClassToUMLAdapter.toString(clazz), null);
		this.parameters = clazz.getTypeParameters().length > 0 ? 
				new ParametersBinding( ClassToUMLAdapter.toString(clazz.getTypeParameters()) ):
				null;
		
		this.enclosingClasses = getEnclosingClasses(clazz);
		this.interfaces = getImplementedInterfaces(clazz);
		
		this.fields = getFields(clazz);
		this.methods = getMethods(clazz);
	}

	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.Element#toXML(java.io.PrintStream)
	 */
	@Override
	public void toXML(PrintStream out) {
		out.format("%s<Interface>\n", TabsSingleton.tabs);
		TabsSingleton.addTab();
			printSignature(out);
			if(parameters != null)
				parameters.toXML(out);
			printDocumentation(out);
		
			printCollection( enclosingClasses, "EnclosingClasses", out );
			printCollection( interfaces, "Interfaces", out );
			printCollection( fields, "Fields", out );
			printCollection( methods, "Methods", out );
		TabsSingleton.removeTab();
		out.format("%s</Interface>\n", TabsSingleton.tabs);
	}
}