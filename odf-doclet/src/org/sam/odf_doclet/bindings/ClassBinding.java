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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;

import org.sam.odf_doclet.Adapter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;

final class Strings{
	
	private Strings(){}
	
	static final Comparator<String> CaseSensitiveComparator = new Comparator<String>(){
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	};

}

final class AssertHelper{
	
	private AssertHelper(){}
	
	static <T extends ProgramElementDoc> String mostrar(String titulo, Map<String, T> map){
		StringBuffer buff = new StringBuffer(titulo);
		for(Map.Entry< String, T> entry: map.entrySet()){
			buff.append("\n[");
			buff.append(entry.getValue().position().line());
			buff.append(":");
			buff.append(entry.getValue().position().column());
			buff.append("] ");
			buff.append(entry.getKey());
		}
		return buff.toString();
	}
	
	static String mostrar(String titulo, Collection<String> collection){
		StringBuffer buff = new StringBuffer(titulo);
		for(String entry: collection){
			buff.append('\n');
			buff.append(entry);
		}
		return buff.toString();
	}
}

final class Utils{
	
	private Utils(){}
	
	interface Filter<T>{
		boolean validate(T t);
	}
	
	static class DocFilter<T extends ProgramElementDoc> implements Filter<T>{

		SourcePosition classPosition;
		
		/* (non-Javadoc)
		 * @see org.sam.odf_doclet.bindings.Filter#validate(java.lang.Object)
		 */
		@Override
		public boolean validate(T doc) {
			// Comparamos la posicion pues el metodo isSynthetic() devuevle siempre ¿ false ?
			SourcePosition docPosition = doc.position();
			return docPosition != null && ( docPosition.line() != classPosition.line() || docPosition.column() != classPosition.column());
		}
	}
	
	private static final DocFilter<ProgramElementDoc> DocsFilter = new DocFilter<ProgramElementDoc>();

	private static final Filter<Field> FieldsFilter = new Filter<Field>(){
		@Override
		public boolean validate(Field field) {
			return !field.isSynthetic() && !field.isEnumConstant();
		}
	};

	private static final Filter<Constructor<?>> ConstructorsFilter = new Filter<Constructor<?>>(){
		@Override
		public boolean validate(Constructor<?> constructor) {
			return !constructor.isSynthetic();
		}
	};

	private static final Filter<Method> MethodsFilter = new Filter<Method>(){
		@Override
		public boolean validate(Method method) {
			return !method.isSynthetic();
		}
	};

	private static final Filter<Method> EnumMethodsFilter = new Filter<Method>(){
		@Override
		public boolean validate(Method method) {
			String methodString = Adapter.toString( method );
			return	!method.isSynthetic()
					&& !methodString.startsWith("valueOf(java.lang.String)")
					&& !methodString.startsWith("values()");
		
		}
	};

	private static final DocFilter<MethodDoc> EnumMethodsDocFilter = new DocFilter<MethodDoc>(){
		@Override
		public boolean validate(MethodDoc method) {
			if( super.validate(method) ){
				String methodString = Adapter.toString( method );
				return	!methodString.startsWith("valueOf(java.lang.String)")
						&& !methodString.startsWith("values()");
			}
			return false;
		}
	};

	private static ClassLoader classLoader;
	
	static void setClassLoader(ClassLoader classLoader){
		Utils.classLoader = classLoader;
	}
	
	private static ClassLoader getClassLoader(){
		if( classLoader == null)
			classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader;
	}
	
	static Class<?> find(Class<?> containingClazz, String canonicalName) throws ClassNotFoundException{
		if( canonicalName.equals(containingClazz.getCanonicalName()))
			return containingClazz;
		
		Class<?> declaredClasses[] = containingClazz.getDeclaredClasses();
		if(declaredClasses.length > 0)
			for(Class<?> clazz: declaredClasses){
				if( canonicalName.equals(clazz.getCanonicalName() ) )
					return clazz;
				if( canonicalName.startsWith(clazz.getCanonicalName()+"." ) )
					return find(clazz, canonicalName);
			}
		throw new ClassNotFoundException( canonicalName + ": not found in " + containingClazz.getCanonicalName() );
	}
	
	static Class<?> find(ClassDoc classDoc) throws ClassNotFoundException{
		if( classDoc.containingClass() == null)
			return Class.forName( classDoc.qualifiedName(), false, Utils.getClassLoader() );
		
		ClassDoc containing = classDoc;
		do{
			containing = containing.containingClass();
		}while(containing.containingClass() != null);
		
		
		Class<?> containingClass = Class.forName( containing.qualifiedName(), false, Utils.getClassLoader() );
		
		return find( containingClass, classDoc.qualifiedName() );
	}
	
	private static Collection<Type> getHierarchy(Class<?> clazz){
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
	
	static Collection<SimpleClassBinding> getHierarchyBinding(Class<?> clazz){
		Deque<SimpleClassBinding> hierarchy = new ArrayDeque<SimpleClassBinding>();
		for(Type type: getHierarchy(clazz))
			hierarchy.offer( SimpleClassBinding.from(type) );

		return hierarchy;
	}
	
	static Collection<SimpleClassBinding> getEnclosingClasses(Class<?> clazz){
		Deque<SimpleClassBinding> enclosingClasses = new ArrayDeque<SimpleClassBinding>();
		Class<?> enclosingClass = clazz.getEnclosingClass();
		while( enclosingClass != null ){
			enclosingClasses.offerFirst( SimpleClassBinding.from(enclosingClass) );
			enclosingClass = enclosingClass.getEnclosingClass();
		}
		return enclosingClasses;
	}
	
	static Collection<SimpleClassBinding> getImplementedInterfaces(Class<?> clazz){
		Deque<SimpleClassBinding> interfaces = new ArrayDeque<SimpleClassBinding>();
		for(Type parent: getHierarchy(clazz)){
			Class<?> superClass = null;
			if( parent instanceof Class<?> )
				superClass = (Class<?>)parent;
			else if ( parent instanceof ParameterizedType)
				superClass = (Class<?>)((ParameterizedType)parent).getRawType();
			
			for(Type implementedInterface: ((Class<?>) superClass).getGenericInterfaces())
				if( !interfaces.contains(implementedInterface))
					interfaces.offerLast( SimpleClassBinding.from(implementedInterface) );
		}
		for(Type implementedInterface: clazz.getGenericInterfaces())
			if( !interfaces.contains(implementedInterface))
				interfaces.offerLast( SimpleClassBinding.from(implementedInterface) );
		return interfaces;
	}
	
	static Collection<LinkBinding> getLinks(Doc doc){
		if(doc == null)
			return null;
		SeeTag[] tags = doc.seeTags();
		if(tags == null || tags.length == 0)
			return null;
		Collection<LinkBinding> links = new ArrayDeque<LinkBinding>(tags.length);
		for(SeeTag tag:tags)
			links.add(new LinkBinding(tag.text()));
		return links;
	}
	
	private static ParamTag[] fileterTypeParameters(Tag[] tags){
		Collection<Tag> typeParameters = new ArrayDeque<Tag>();
		for(Tag tag: tags )
			if( (tag instanceof ParamTag) && ((ParamTag)tag).isTypeParameter())
				typeParameters.add(tag);
		int size = typeParameters.size();
		if( size == 0 )
			return new ParamTag[0];
		return typeParameters.toArray(new ParamTag[size]);
	}

	static Collection<TypeParamBinding> getTypeParams( GenericDeclaration generic, Doc doc){
		TypeVariable<?>[] types = generic.getTypeParameters();
		if(types.length == 0)
			return null;
		Collection<TypeParamBinding>  parameters= new ArrayDeque<TypeParamBinding>();
		ParamTag[] tags = doc != null ? fileterTypeParameters(doc.tags("@param")): null;
		if( tags == null || tags.length != types.length )
			for(TypeVariable<?> type: types)
				parameters.add( new TypeParamBinding( type ) );
		else{
			for( int i= 0, len = types.length; i < len; i++ )
				parameters.add( new TypeParamBinding( types[i], tags[i] ) );
		}
		return parameters;
	}
	
	static Collection<ConstantBinding> getConstants( Class<?> clazz, ClassDoc classDoc ) {
		Deque<ConstantBinding> constants = new ArrayDeque<ConstantBinding>();
		if(classDoc == null) {
			for(Object constant: clazz.getEnumConstants())
				constants.offerLast( new ConstantBinding(constant.toString() ) );
		} else {
			Map<String, FieldDoc> map = new TreeMap<String, FieldDoc>(Strings.CaseSensitiveComparator);
			for(FieldDoc field: classDoc.fields())
				map.put( field.name(), field );
			for(Object constant: clazz.getEnumConstants()){
				FieldDoc field = map.remove(constant.toString());
				if( field != null )
					constants.offerLast( new ConstantBinding(constant.toString(), field ) );
				assert( field != null ): "!!!Documentacion no encontrada para: " + constant.toString();
			}
		}
		return constants;
	}
	
	static Collection<FieldBinding> getFields( Class<?> clazz, ClassDoc classDoc ) {
		//TODO Ordenar
		Deque<FieldBinding> fields = new ArrayDeque<FieldBinding>();
		if(classDoc == null) {
			for(Field field: clazz.getDeclaredFields())
				if( FieldsFilter.validate(field) )
					fields.offer( new FieldBinding( field, null ) );
		} else {
			Map<String, FieldDoc> map = new TreeMap<String,FieldDoc>(Strings.CaseSensitiveComparator);
			DocsFilter.classPosition = classDoc.position();
			for(FieldDoc field: classDoc.fields()){
				if( DocsFilter.validate(field) )
					map.put( field.name(), field );
			}
			for(Field field: clazz.getDeclaredFields()){
				if( FieldsFilter.validate(field) ){
					FieldDoc doc = map.remove(field.getName());
					assert( doc != null ): "!!!Documentacion no encontrada para: " + field.getName();
					fields.offer( new FieldBinding( field, doc ) );
				}
			}
//			assert( map.size() == 0 ): mostrar("!!!Documentacion sobrante:", map);
		}
		return fields;
	}
	
	static Collection<ConstructorBinding> getConstructors( Class<?> clazz, ClassDoc classDoc ) {
		//TODO Ordenar
		Deque<ConstructorBinding> constructors = new ArrayDeque<ConstructorBinding>();
		if(classDoc == null) {
			for(Constructor<?> constructor: clazz.getDeclaredConstructors())
				if( ConstructorsFilter.validate(constructor) )
					constructors.add( new ConstructorBinding( constructor, null) );
		} else {
			DocsFilter.classPosition = classDoc.position();
			Map<String, ConstructorDoc> map = new TreeMap<String, ConstructorDoc>(Strings.CaseSensitiveComparator);
			for(ConstructorDoc doc: classDoc.constructors()){
				if( DocsFilter.validate(doc) )
					map.put( Adapter.toString( doc ), doc );
			}
			Collection<String> undocumented = new ArrayDeque<String>();
			for(Constructor<?> constructor: clazz.getDeclaredConstructors()){
				if( ConstructorsFilter.validate(constructor) ){
					ConstructorDoc doc = map.remove( Adapter.toString( constructor ) );
					if( doc == null )
						doc = map.remove( Adapter.toString( constructor, true ) );
					if( doc != null )
						constructors.offer( new ConstructorBinding( constructor, doc ) );
					else 
						undocumented.add( Adapter.toString( constructor ) );
				}
			}
			assert( map.size() == 0 ): "!!!Error en "+classDoc.name() + ":\n" + 
				AssertHelper.mostrar("Elementos indocumentados:",undocumented) + "\n" +
				AssertHelper.mostrar("Documentacion sobrante :", map);
		}
		return constructors;
	}
	
	static Collection<MethodBinding> getMethods( Class<?> clazz, ClassDoc classDoc ) {
		//TODO Ordenar
		final Filter<Method> filter = clazz.isEnum() ? EnumMethodsFilter : MethodsFilter;
	
		Deque<MethodBinding> methods = new ArrayDeque<MethodBinding>();
		if(classDoc == null) {
			for(Method method: clazz.getDeclaredMethods())
				if( filter.validate(method) )
					methods.offer( new MethodBinding( method, null) );
		} else {
			final DocFilter<? super MethodDoc> filterDoc;
			if( clazz.isEnum() )
				filterDoc = EnumMethodsDocFilter;
			else
				filterDoc = DocsFilter;

			filterDoc.classPosition = classDoc.position();
			
			Map<String, MethodDoc> map = 
				new TreeMap<String, MethodDoc>(Strings.CaseSensitiveComparator);
			for(MethodDoc doc: classDoc.methods()){
				if( filterDoc.validate(doc) )
					map.put( Adapter.toString( doc ), doc );
			}
			Collection<String> undocumented = new ArrayDeque<String>();
			for(Method method: clazz.getDeclaredMethods()){
				if( filter.validate(method) ){
					MethodDoc doc = map.remove( Adapter.toString( method ) );
					if( doc != null )
						methods.offer( new MethodBinding( method, doc ) );
					else 
						undocumented.add( Adapter.toString( method ) );
				}
			}
			assert( map.size() == 0 ):
				"!!!Error en " + classDoc.name() + ":\n" +
				AssertHelper.mostrar("Elementos indocumentados:",undocumented) + "\n" +
				AssertHelper.mostrar("Documentacion sobrante :", map);
		}
		return methods;
	}
	
	private static <T extends Tag> T getTag(T[] tags, int index){
		try{
			return tags[index];
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	static String concatComments(Tag[] tags){
		if (tags == null || tags.length == 0)
			return null;

		StringBuilder builder = new StringBuilder();
		for (Tag returnTag : tags) {
			String returnTagText = returnTag.text();
			if (returnTagText != null) {
				builder.append(returnTagText);
				builder.append("\n");
			}
		}
		return builder.substring(0, builder.length() - 1);
	}
	
	static <T extends AccessibleObject & GenericDeclaration> 
	Collection<ParameterBinding> getParams(T command, ExecutableMemberDoc doc){
		
		Type[] types = null;
		if(command instanceof Constructor<?>)
			types = ((Constructor<?>)command).getGenericParameterTypes();
		else if (command instanceof Method)
			types =	((Method)command).getGenericParameterTypes();
		
		if(types == null || types.length == 0)
			return null;
		
		Collection<ParameterBinding>  parameters= new ArrayDeque<ParameterBinding>();
		
		if(doc == null)
			for(Type type: types)
				parameters.add( new ParameterBinding( type ) );
		else{
			ParamTag[] tags = doc.paramTags();
			int offset = types.length - doc.parameters().length; //Offset ignora Synthetic accesor 
			assert(offset >= 0): "!!offset:"+offset;
			for(int i= offset, j=0, len = types.length; i < len; i++, j++)
				parameters.add( new ParameterBinding( types[i], getTag( tags, j ) ) );
		}
		return parameters;
	}
	
	static <T extends AccessibleObject & GenericDeclaration & Member> 
	Collection<ExceptionBinding> getExceptions(T command, ExecutableMemberDoc doc ){
		
		Type[] types = null;
		if(command instanceof Constructor<?>)
			types = ((Constructor<?>)command).getGenericExceptionTypes();
		else if (command instanceof Method)
			types =	((Method)command).getGenericExceptionTypes();
		
		if(types == null || types.length == 0)
			return null;
		
		Collection<ExceptionBinding> exceptions= new ArrayDeque<ExceptionBinding>();
		
		if(doc == null)
			for(Type type: types)
				exceptions.add( new ExceptionBinding( type ) );
		else {
			Map<String, ThrowsTag> map = new TreeMap<String, ThrowsTag>(Strings.CaseSensitiveComparator);
			for(ThrowsTag e: doc.throwsTags())
				map.put( e.exceptionType().typeName(), e );
			for(Type type: types)
				exceptions.add( new ExceptionBinding( type, map.remove(Adapter.toString(type)) ) );
			assert( map.size() == 0 ): "!!!Error en "+ command.getDeclaringClass() + ":\n\t" +
				command.getName()+
				AssertHelper.mostrar("Documentacion sobrante :", map.keySet());	
		}
		return exceptions;
	}
}

/**
 * 
 */
enum Visibility{
	
	Public('+'),
	Protected('#'),
	Package('~'),
	Private('-');
	
	public static Visibility fromModifiers(int att){
		if( Modifier.isPublic(att) )
			return Public;
		if( Modifier.isProtected(att) )
			return Protected;
		if( Modifier.isPrivate(att) )
			return Private;
		return Package;
	}
	
	private final char c;
	
	private Visibility(char c){
		this.c = c;
	}
	
	public final char toChar(){
		return c;
	}
}

abstract class SimpleClassBinding {
	
	static class Interface extends SimpleClassBinding{
		Interface(Type type){
			super( Adapter.toString(type) );
		}
	}

	static class Enum extends SimpleClassBinding{
		Enum(Type type){
			super( Adapter.toString(type) );
		}
	}

	static class Clazz extends SimpleClassBinding{

		final boolean isAbstract;
		
		Clazz(Type type){
			super( Adapter.toString(type) );
			if(type instanceof Class<?>)
				this.isAbstract = Modifier.isAbstract( ((Class<?>)type).getModifiers() );
			else if(type instanceof ParameterizedType)
				this.isAbstract = Modifier.isAbstract( ((Class<?>)((ParameterizedType)type).getRawType()).getModifiers() );
			else
				throw new IllegalArgumentException(type.toString());
		}

	}
	
	static final SimpleClassBinding from(Type type){
		
		if(type instanceof Class<?>){
			Class<?> clazz = (Class<?>)type;
			if(clazz.isInterface())
				return new Interface( type );
			if(clazz.isEnum())
				return new Enum( type );
			return new Clazz( type );
		}
		if(type instanceof ParameterizedType){
			Class<?> clazz = (Class<?>)((ParameterizedType)type).getRawType();
			if(clazz.isInterface())
				return new Interface( type );
			return new Clazz( type );
		}
		throw new IllegalArgumentException();
	}
	
	final String name;
	
	SimpleClassBinding(String name) {
		this.name = name;
	}
}

abstract class DocumentedType {
	
	final String type;
	final String documentation;
	
	DocumentedType(String type){
		this.type = type;
		this.documentation = null;
	}
	
	DocumentedType(String type, String documentation){
		this.type = type;
		this.documentation = documentation;
	}
}

class TypeParamBinding extends DocumentedType{
	
	TypeParamBinding(TypeVariable<?> type){
		super( Adapter.toString(type) );
	}
	
	TypeParamBinding(TypeVariable<?> type, ParamTag tag){
		super( Adapter.toString(type), tag != null ? tag.parameterComment(): null );
	}
}

class ParameterBinding extends DocumentedType{
	
	final String name;
	
	ParameterBinding(Type type){
		super( Adapter.toString(type) );
		this.name = null;
	}
	
	ParameterBinding(Type type, ParamTag tag){
		super(Adapter.toString(type), tag != null ? tag.parameterComment(): null );
		this.name =	tag != null ? tag.parameterName(): null;
	}
}

class ReturnTypeBinding extends DocumentedType{
	
	ReturnTypeBinding(Type type){
		super( Adapter.toString(type) );
	}
	
	ReturnTypeBinding(Type type, Tag[] tags){
		super(	Adapter.toString(type), Utils.concatComments(tags) );
	}
}

class ExceptionBinding extends DocumentedType{
	
	ExceptionBinding(Type type){
		super( Adapter.toString(type) );
	}
	
	ExceptionBinding(Type type, ThrowsTag tag){
		super(	Adapter.toString(type), tag != null ? tag.exceptionComment(): null );
	}
}

class LinkBinding{
	
	final String link;
	
	LinkBinding(String link){
		this.link = link;
	}
}

abstract class DocumentedElement {
	
	final String name;
	final String documentation;
	final Collection<LinkBinding> links;
	
	DocumentedElement(String name, Doc doc){
		this.name = name;
		this.documentation = doc != null ? doc.commentText(): null;
		this.links = Utils.getLinks(doc);
	}
	
	DocumentedElement(String name){
		this(name, null);
	}
}

class ConstantBinding extends DocumentedElement{

	ConstantBinding(String name) {
		super(name);
	}

	ConstantBinding(String name, FieldDoc doc) {
		super( name, doc );
	}
}

class FieldBinding extends DocumentedElement{

	final Visibility visibility;
	final int modifiers;
	final String type;
	
	FieldBinding( Field field, FieldDoc doc ){
		super( 	field.getName(), doc );
		this.modifiers = field.getModifiers();
		this.visibility = Visibility.fromModifiers(modifiers);
		this.type = Adapter.toString(field.getGenericType());
	}
	
}

abstract class CommandBinding extends DocumentedElement{

	private static String getName(Member d){
		return d instanceof Constructor<?> ?
				((Constructor<?>)d).getDeclaringClass().getSimpleName():
				d.getName();
	}
	
	final Visibility visibility;
	final Collection<TypeParamBinding> typeParams;
	final Collection<ParameterBinding> params;
	final Collection<ExceptionBinding> exceptions;

	<T extends AccessibleObject & Member & GenericDeclaration>
	CommandBinding(T command, ExecutableMemberDoc doc){
		super( getName(command), doc );
		this.visibility = Visibility.fromModifiers( command.getModifiers() );
		
		typeParams = Utils.getTypeParams(command, doc);
		params = Utils.getParams(command, doc);
		exceptions = Utils.getExceptions(command, doc);
	}
}

class ConstructorBinding extends CommandBinding{
	ConstructorBinding( Constructor<?> constructor, ConstructorDoc doc){
		super( constructor, doc );
	}
}

class MethodBinding extends CommandBinding{

	private static MethodDoc checkOverride( MethodDoc doc ){
		if( doc != null && doc.overriddenMethod() != null && doc.getRawCommentText().length() == 0)
			doc.setRawCommentText("@see " + doc.overriddenClass().qualifiedTypeName() +"#"+Adapter.toString(doc));
		return doc;
	}
	
	final int modifiers;
	final ReturnTypeBinding  returnType;
	
	MethodBinding( Method method, MethodDoc doc){
		super( method, checkOverride(doc) );

		this.modifiers = method.getModifiers();

		if( method.getReturnType().equals(java.lang.Void.TYPE) )
			returnType = null;
		else{
			if( doc == null )
				returnType = new ReturnTypeBinding(method.getGenericReturnType());
			else
				returnType = new ReturnTypeBinding(method.getGenericReturnType(), doc.tags("@return"));
		}
	}
}

public abstract class ClassBinding extends DocumentedElement{
	
	static class Interface extends ClassBinding{
	
		final Collection<TypeParamBinding> parameters;
		
		Interface( Class<?> clazz, ClassDoc classDoc) {
			super( clazz, classDoc );
			this.parameters = Utils.getTypeParams(clazz, classDoc);
			
			this.enclosingClasses = Utils.getEnclosingClasses(clazz);
			this.interfaces = Utils.getImplementedInterfaces(clazz);
			
			this.fields = Utils.getFields(clazz, classDoc);
			this.methods = Utils.getMethods(clazz, classDoc);
		}
	}
	
	static class Enum extends ClassBinding{
		
		Collection<ConstantBinding> constants;
		Collection<ConstructorBinding> constructors;
		
		Enum( Class<?> clazz, ClassDoc classDoc) {
			super( clazz, classDoc );
	
			this.enclosingClasses = Utils.getEnclosingClasses(clazz);
			this.interfaces = Utils.getImplementedInterfaces(clazz);
			
			this.constants = Utils.getConstants(clazz, classDoc);
			this.fields = Utils.getFields(clazz, classDoc);
			this.constructors = Utils.getConstructors(clazz, classDoc);
			this.methods = Utils.getMethods(clazz, classDoc);
		}
	
	}

	static class Clazz extends ClassBinding{
		
		final boolean isAbstract;
		final Collection<TypeParamBinding> parameters;

		final Collection<SimpleClassBinding> hierarchy;
		final Collection<ConstructorBinding> constructors;
		
		Clazz( Class<?> clazz, ClassDoc classDoc) {
			super( clazz, classDoc);

			this.isAbstract = Modifier.isAbstract( clazz.getModifiers() );
			this.parameters = Utils.getTypeParams(clazz, classDoc);
			
			this.hierarchy = Utils.getHierarchyBinding(clazz);
			this.enclosingClasses = Utils.getEnclosingClasses(clazz);
			this.interfaces = Utils.getImplementedInterfaces(clazz);
			
			this.fields = Utils.getFields(clazz, classDoc);
			this.constructors = Utils.getConstructors(clazz, classDoc);
			this.methods = Utils.getMethods(clazz, classDoc);
		}
	}
	
	Collection<SimpleClassBinding> enclosingClasses;
	Collection<SimpleClassBinding> interfaces;
	
	Collection<FieldBinding> fields;
	Collection<MethodBinding> methods;
	
	ClassBinding( Class<?> clazz, ClassDoc classDoc ) {
		super( Adapter.toString(clazz), classDoc );
	}
}

