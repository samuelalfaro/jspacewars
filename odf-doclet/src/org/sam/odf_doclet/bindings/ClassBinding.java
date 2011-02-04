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
import java.util.Collection;
import java.util.Deque;

import org.sam.odf_doclet.ClassToUMLAdapter;

import com.sun.javadoc.ClassDoc;

final class ClassPair {
	
	static Class<?> find(Class<?> containingClazz, String qualifiedName) throws ClassNotFoundException{
		Class<?> declaredClasses[] = containingClazz.getDeclaredClasses();
		if(declaredClasses.length > 0)
			for(Class<?> clazz: declaredClasses){
				if( qualifiedName.equals(clazz.getCanonicalName()))
					return clazz;
				if( qualifiedName.startsWith(clazz.getCanonicalName()+".") )
					return find(clazz, qualifiedName);
			}
		throw new ClassNotFoundException( qualifiedName+": not found in " +containingClazz.getCanonicalName() );
	}
	
	static ClassPair from(ClassDoc classDoc) throws ClassNotFoundException{
		if( classDoc.containingClass() == null)
			return new ClassPair(
					Class.forName( classDoc.qualifiedName(), false, ClassBinding.getClassLoader() ),
					classDoc
			);
		
		ClassDoc containingClass = classDoc;
		while(containingClass.containingClass() != null){
			containingClass = containingClass.containingClass();
		}
		
		return new ClassPair(
				find(
						Class.forName( 
								containingClass.qualifiedName(), 
								false, 
								ClassBinding.getClassLoader()
						), 
						classDoc.qualifiedName() 
				),
				classDoc
		);
	}
	
	static ClassPair from( Class<?> clazz, ClassDoc classDoc ) throws ClassNotFoundException {
		if( classDoc.qualifiedName().equals(clazz.getCanonicalName()))
			return new ClassPair( clazz, classDoc );
		return new ClassPair( find(clazz, classDoc.qualifiedName()), classDoc );
	}
	
	static ClassPair from(Class<?> clazz) {
		return new ClassPair( clazz, null );
	}
	
	final Class<?> clazz;
	final ClassDoc classDoc;

	private ClassPair(Class<?> clazz, ClassDoc classDoc) {
		this.clazz = clazz;
		this.classDoc = classDoc;
	}
	
	boolean isInterface(){
		if(clazz != null)
			return clazz.isInterface();
		return classDoc.isInterface();
	}
	
	boolean  isEnum(){
		if(clazz != null)
			return clazz.isEnum();
		return classDoc.isEnum();
	}
}

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
	
	private static ClassLoader classLoader;
	
	public static final void setClassLoader(ClassLoader classLoader){
		ClassBinding.classLoader = classLoader;
	}
	
	static final ClassLoader getClassLoader(){
		if( classLoader == null)
			classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader;
	}
	
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
	
	static Collection<SimpleBinding> getEnclosingClasses(ClassPair classPair){
		Deque<SimpleBinding> enclosingClasses = new ArrayDeque<SimpleBinding>();
		Class<?> enclosingClass = classPair.clazz.getEnclosingClass();
		while( enclosingClass != null ){
			enclosingClasses.offerFirst( SimpleBinding.from(enclosingClass) );
			enclosingClass = enclosingClass.getEnclosingClass();
		}
		return enclosingClasses;
	}
	
	static Collection<SimpleInterfaceBinding> getImplementedInterfaces(ClassPair classPair){
		Deque<SimpleInterfaceBinding> interfaces = new ArrayDeque<SimpleInterfaceBinding>();
		for(Type parent: getHierarchy(classPair.clazz)){
			Class<?> superClass = null;
			if( parent instanceof Class<?> )
				superClass = (Class<?>)parent;
			else if ( parent instanceof ParameterizedType)
				superClass = (Class<?>)((ParameterizedType)parent).getRawType();
			
			for(Type implementedInterface: ((Class<?>) superClass).getGenericInterfaces())
				if( !interfaces.contains(implementedInterface))
					interfaces.offerLast( new SimpleInterfaceBinding(implementedInterface) );
		}
		for(Type implementedInterface: classPair.clazz.getGenericInterfaces())
			if( !interfaces.contains(implementedInterface))
				interfaces.offerLast( new SimpleInterfaceBinding(implementedInterface) );
		return interfaces;
	}
	
	static Collection<FieldBinding> getFields(ClassPair classPair){
		Deque<FieldBinding> fields = new ArrayDeque<FieldBinding>();
		for(Field field: classPair.clazz.getDeclaredFields()){
			if( !field.isSynthetic() && !field.isEnumConstant() )
				fields.offer( new FieldBinding(field) );
		}
		return fields;
	}
	
	static Collection<ConstructorBinding> getConstructors(ClassPair classPair){
		Deque<ConstructorBinding> constructors = new ArrayDeque<ConstructorBinding>();
		for(Constructor<?> constructor: classPair.clazz.getDeclaredConstructors()){
			if( !constructor.isSynthetic() )
				constructors.add( new ConstructorBinding( constructor ) );
		}
		return constructors;
	}
	
	static Collection<MethodBinding> getMethods(ClassPair classPair){
		Deque<MethodBinding> methods = new ArrayDeque<MethodBinding>();
		if(classPair.clazz.isEnum())
			for(Method method: classPair.clazz.getDeclaredMethods()){
				String methodString = ClassToUMLAdapter.toString(method);
				if(!method.isSynthetic() && !methodString.startsWith("values():") &&  !methodString.startsWith("valueOf( String ):") )
					methods.offer( new MethodBinding(method) );
			}
		else
			for(Method method: classPair.clazz.getDeclaredMethods()){
				if(!method.isSynthetic() )
					methods.offer( new MethodBinding(method) );
			}
		return methods;
	}
	
	private final static ClassBinding from(ClassPair classPair){
		if(classPair.isInterface())
			return new InterfaceBinding( classPair );
		if(classPair.isEnum())
			return new EnumBinding( classPair );
		return new ConcreteClassBinding( classPair );
	}
	
	public final static ClassBinding from(Class<?> clazz){
		return from( ClassPair.from(clazz) );
	}
	
	public final static ClassBinding from(ClassDoc classDoc) throws ClassNotFoundException{
		return from( ClassPair.from(classDoc) );
	}
	
	Collection<SimpleBinding> enclosingClasses;
	Collection<SimpleInterfaceBinding> interfaces;
	
	Collection<FieldBinding> fields;
	Collection<MethodBinding> methods;
	
	ClassBinding(String signature, String documentation) {
		super(signature, documentation);
	}

	public final void toXML(OutputStream out) {
		if (out instanceof PrintStream)
			toXML((PrintStream) out);
		else
			try {
				toXML(new PrintStream(out, false, "UTF-8"));
			} catch (UnsupportedEncodingException ignorada) {
			}
	}
}

class ConcreteClassBinding extends ClassBinding{
	
	static Collection<SimpleClassBinding> getHierarchyBinding(ClassPair classPair){
		Deque<SimpleClassBinding> hierarchy = new ArrayDeque<SimpleClassBinding>();
		for(Type type: getHierarchy(classPair.clazz))
			hierarchy.offer( new SimpleClassBinding(type) );

		return hierarchy;
	}
	
	boolean isAbstract;
	ParametersBinding parameters;

	Collection<SimpleClassBinding> hierarchy;
	Collection<ConstructorBinding> constructors;
	
	ConcreteClassBinding(ClassPair classPair){
		super( ClassToUMLAdapter.toString(classPair.clazz), null);
		this.isAbstract = Modifier.isAbstract( classPair.clazz.getModifiers() );
		this.parameters = classPair.clazz.getTypeParameters().length > 0 ? 
				new ParametersBinding( ClassToUMLAdapter.toString(classPair.clazz.getTypeParameters()) ):
				null;
		
		this.hierarchy = getHierarchyBinding(classPair);
		this.enclosingClasses = getEnclosingClasses(classPair);
		this.interfaces = getImplementedInterfaces(classPair);
		
		this.fields = getFields(classPair);
		this.constructors = getConstructors(classPair);
		this.methods = getMethods(classPair);
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
	
	static Collection<ConstantBinding> getConstants(ClassPair classPair){
		Deque<ConstantBinding> constants = new ArrayDeque<ConstantBinding>();
		for(Object constant: classPair.clazz.getEnumConstants()){
			constants.offerLast( new ConstantBinding(constant.toString(), null) );
		}
		return constants;
	}
	
	Collection<ConstantBinding> constants;
	Collection<ConstructorBinding> constructors;
	
	EnumBinding(ClassPair classPair){
		super( ClassToUMLAdapter.toString(classPair.clazz), null);
		
		this.enclosingClasses = getEnclosingClasses(classPair);
		this.interfaces = getImplementedInterfaces(classPair);
		
		this.constants = getConstants(classPair);
		this.fields = getFields(classPair);
		this.constructors = getConstructors(classPair);
		this.methods = getMethods(classPair);
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
	
	InterfaceBinding(ClassPair classPair){
		super( ClassToUMLAdapter.toString(classPair.clazz), null);
		this.parameters = classPair.clazz.getTypeParameters().length > 0 ? 
				new ParametersBinding( ClassToUMLAdapter.toString(classPair.clazz.getTypeParameters()) ):
				null;
		
		this.enclosingClasses = getEnclosingClasses(classPair);
		this.interfaces = getImplementedInterfaces(classPair);
		
		this.fields = getFields(classPair);
		this.methods = getMethods(classPair);
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