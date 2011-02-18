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

class AssertHelper{
	
	private AssertHelper(){}
	
	static <S, T extends ProgramElementDoc> String mostrar(String titulo, Map<String, Pair<S,T>> map){
		StringBuffer buff = new StringBuffer(titulo);
		for(Map.Entry< String, Pair<S, T>> entry: map.entrySet()){
			buff.append("\n[");
			buff.append(entry.getValue().d2.position().line());
			buff.append(":");
			buff.append(entry.getValue().d2.position().column());
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

/**
 * @param <T>
 */
class DocFilter<T extends ProgramElementDoc> implements Filter<T>{

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
abstract class Element implements XMLSerializable {
	
	final String name;
	
	Element(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if( o == null)
			return false;
		if( this == o)
			return true;
		if( !Element.class.isAssignableFrom(o.getClass()) )
			return false;
		return name == null ? ((Element)o).name == null : name.equals( ((Element)o).name );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return name == null ? 0 : name.hashCode();
	}
}

class Link implements XMLSerializable {
	
	final String link;
	
	Link(String link){
		this.link = link;
	}

	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
	 */
	@Override
	public void toXML(XMLPrinter out) {
		out.print("Link", link);
	}
}

/**
 * 
 */
abstract class DocumentedElement extends Element {
	
	static final Comparator<String> STRING_COMPARATOR = new Comparator<String>(){
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	};
	
	final String documentation;
	
	DocumentedElement(String name, String documentation){
		super(name);
		this.documentation = documentation;
	}
	
	DocumentedElement(String name){
		this(name, null);
	}
}

abstract class DocumentedElementLinks extends  DocumentedElement {
	
	static final Comparator<String> STRING_COMPARATOR = new Comparator<String>(){
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	};
	
	private static final Collection<Link> getLinks(Doc doc){
		if(doc == null)
			return null;
		SeeTag[] tags = doc.seeTags();
		if(tags == null || tags.length == 0)
			return null;
		Collection<Link> links = new ArrayDeque<Link>(tags.length);
		for(SeeTag tag:tags)
			links.add(new Link(tag.text()));
		return links;
	}
	
	final String documentation;
	final Collection<Link> links;
	
	<T extends Doc > DocumentedElementLinks(String name, T doc){
		super(name);
		this.documentation = doc != null ? doc.commentText(): null;
		this.links = getLinks(doc);
	}
	
	DocumentedElementLinks(String name){
		this(name, null);
	}
}

/**
 * 
 */
abstract class SimpleClassBinding extends Element {
	
	/**
	 * 
	 */
	static class SimpleInterfaceBinding extends SimpleClassBinding{
		
		SimpleInterfaceBinding(Type type){
			super( Adapter.toString(type) );
		}
		
		/* (non-Javadoc)
		 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
		 */
		@Override
		public void toXML(XMLPrinter out) {
			out.print("Interface", this.name);
		}
	}

	/**
	 * 
	 */
	static class SimpleEnumBinding extends SimpleClassBinding{
		
		SimpleEnumBinding(Type type){
			super( Adapter.toString(type) );
		}
		
		/* (non-Javadoc)
		 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
		 */
		@Override
		public void toXML(XMLPrinter out) {
			out.print("Enum", this.name);
		}
	}

	static class SimpleConcreteClassBinding extends SimpleClassBinding{

		final boolean isAbstract;
		
		SimpleConcreteClassBinding(Type type){
			super( Adapter.toString(type) );
			if(type instanceof Class<?>)
				this.isAbstract = Modifier.isAbstract( ((Class<?>)type).getModifiers() );
			else if(type instanceof ParameterizedType)
				this.isAbstract = Modifier.isAbstract( ((Class<?>)((ParameterizedType)type).getRawType()).getModifiers() );
			else
				throw new IllegalArgumentException(type.toString());
		}
		
		/* (non-Javadoc)
		 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
		 */
		@Override
		public void toXML(XMLPrinter out) {
			out.openNode("Class");
				if(isAbstract)
					out.addAttribute("isAbstract", isAbstract);
				out.print(this.name);
			out.closeNode();
		}
	}
	
	static final SimpleClassBinding from(Type type){
		
		if(type instanceof Class<?>){
			Class<?> clazz = (Class<?>)type;
			if(clazz.isInterface())
				return new SimpleInterfaceBinding( type );
			if(clazz.isEnum())
				return new SimpleEnumBinding( type );
			return new SimpleConcreteClassBinding( type );
		}
		if(type instanceof ParameterizedType){
			Class<?> clazz = (Class<?>)((ParameterizedType)type).getRawType();
			if(clazz.isInterface())
				return new SimpleInterfaceBinding( type );
			return new SimpleConcreteClassBinding( type );
		}
		throw new IllegalArgumentException();
	}
	
	SimpleClassBinding(String signature) {
		super(signature);
	}
}

/**
 * 
 */
class ConstantBinding extends DocumentedElementLinks{

	ConstantBinding(String name) {
		super(name);
	}

	ConstantBinding(String name, FieldDoc doc) {
		super( name, doc );
	}

	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
	 */
	@Override
	public void toXML(XMLPrinter out) {
		out.openNode("Constant");
			out.addAttribute("name", name);
			out.print("Documentation", documentation);
			out.print("Links", links);
		out.closeNode();
	}
}

class ExceptionBinding extends DocumentedElement{
	
	ExceptionBinding(Type type){
		super( Adapter.toString(type) );
	}
	
	ExceptionBinding(Type type, ThrowsTag tag){
		super(	Adapter.toString(type),
				tag != null ? tag.exceptionComment(): null
		);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
	 */
	@Override
	public void toXML(XMLPrinter out) {
		out.openNode("Exception");
			out.print("Type", name);
			out.print("Documentation", documentation);
		out.closeNode();
	}
}

/**
 * 
 */
class ParameterBinding extends DocumentedElement{
	
	final String type;
	
	private ParameterBinding(String name, String type){
		super( name );
		this.type = type;
	}
	
	private ParameterBinding( String name, String type, String documentation ){
		super( name, documentation );
		this.type = type;
	}
	
	ParameterBinding(TypeVariable<?> type, ParamTag tag){
		this(	null,
				Adapter.toString(type),
				tag != null ? tag.parameterComment(): null
		);
	}
	
	ParameterBinding(TypeVariable<?> type){
		this( null, Adapter.toString(type) );
	}
	
	ParameterBinding(Type type, ParamTag tag){
		this(	tag != null ? tag.parameterName(): null,
				Adapter.toString(type),
				tag != null ? tag.parameterComment(): null
		);
	}
	
	private static String concatComments(Tag[] tags){
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
	
	ParameterBinding(Type type, Tag[] tags){
		this( null, Adapter.toString(type), concatComments(tags) );
	}
	
	ParameterBinding(Type type){
		this( null, Adapter.toString(type) );
	}
	
	final void toXML(String nodeName, XMLPrinter out) {
		out.openNode(nodeName);
			if(name!=null)
				out.addAttribute("name", name);
			out.print("Type", type);
			out.print("Documentation", documentation);
		out.closeNode();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
	 */
	@Override
	public void toXML(XMLPrinter out) {
		toXML("Parameter", out);
	}
}

/**
 * 
 */
class FieldBinding extends DocumentedElementLinks{

	final Visibility visibility;
	final int modifiers;
	final String type;
	
	FieldBinding(Pair<Field,FieldDoc> field){
		super( 	field.d1.getName(), field.d2 );
		this.modifiers = field.d1.getModifiers();
		this.visibility = Visibility.fromModifiers(modifiers);
		this.type = Adapter.toString(field.d1.getGenericType());
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
	 */
	@Override
	public void toXML(XMLPrinter out) {
		out.openNode("Field");
			out.addAttribute("name", name);
			out.addAttribute("visibility", visibility.toChar());
			if(Modifier.isStatic(modifiers))
				out.addAttribute("isStatic", true);
			if(Modifier.isTransient(modifiers))
				out.addAttribute("isTransient", true);
			if(Modifier.isVolatile(modifiers))
				out.addAttribute("isVolatile", true);
			if(Modifier.isFinal(modifiers))
				out.addAttribute("isFinal", true);
			out.print("Type", type);
			out.print("Documentation", documentation);
			out.print("Links", links);
		out.closeNode();
	}
}

abstract class CommandBinding extends DocumentedElementLinks{

	private final static String getName(Member d){
		return d instanceof Constructor<?> ?
				((Constructor<?>)d).getDeclaringClass().getSimpleName():
				d.getName();
	}
	
	private final static <T extends Tag> T getTag(T[] tags, int index){
		try{
			return tags[index];
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	private static Collection<ParameterBinding> getParams(Pair<?, ? extends ExecutableMemberDoc> command){
		
		Type[] types = null;
		if(command.d1 instanceof Constructor<?>)
			types = ((Constructor<?>)command.d1).getGenericParameterTypes();
		else if (command.d1 instanceof Method)
			types =	((Method)command.d1).getGenericParameterTypes();
		
		if(types == null || types.length == 0)
			return null;
		
		Collection<ParameterBinding>  parameters= new ArrayDeque<ParameterBinding>();
		
		if(command.d2 == null)
			for(Type type: types)
				parameters.add( new ParameterBinding( type ) );
		else{
			ParamTag[] tags = command.d2.paramTags();
			int offset = types.length - command.d2.parameters().length; //Offset ignora Synthetic accesor 
			assert(offset >= 0): "!!offset:"+offset;
			for(int i= offset, j=0, len = types.length; i < len; i++, j++)
				parameters.add( new ParameterBinding( types[i], getTag( tags, j ) ) );
		}
		return parameters;
	}
	
	private static Collection<ExceptionBinding> getExceptions(Pair<?, ? extends ExecutableMemberDoc> command){
		
		Class<?> clazz = null;
		
		Type[] types = null;
		if(command.d1 instanceof Constructor<?>){
			clazz = ((Constructor<?>)command.d1).getDeclaringClass();
			types = ((Constructor<?>)command.d1).getGenericExceptionTypes();
		} else if (command.d1 instanceof Method) {
			clazz = ((Method)command.d1).getDeclaringClass();
			types =	((Method)command.d1).getGenericExceptionTypes();
		}
		
		if(types == null || types.length == 0)
			return null;
		
		Collection<ExceptionBinding> exceptions= new ArrayDeque<ExceptionBinding>();
		
		if(command.d2 == null)
			for(Type type: types)
				exceptions.add( new ExceptionBinding( type ) );
		else {
			Map<String, ThrowsTag> map = new TreeMap<String, ThrowsTag>(STRING_COMPARATOR);
			for(ThrowsTag e: command.d2.throwsTags())
				map.put( e.exceptionType().typeName(), e );
			for(Type type: types)
				exceptions.add( new ExceptionBinding( type, map.remove(Adapter.toString(type)) ) );
			assert( map.size() == 0 ): "!!!Error en "+ clazz.toString() + ":\n\t" +
				getName((Member)command.d1)+
				AssertHelper.mostrar("Documentacion sobrante :", map.keySet());	
		}
		return exceptions;
	}
	
	final Visibility visibility;
	final Collection<ParameterBinding> typeParams;
	final Collection<ParameterBinding> params;
	final Collection<ExceptionBinding> exceptions;

	<T extends AccessibleObject & Member & GenericDeclaration>
	CommandBinding(Pair<? extends T, ? extends ExecutableMemberDoc> command){
		super( getName(command.d1), command.d2 );
		this.visibility = Visibility.fromModifiers( command.d1.getModifiers() );
		
		typeParams = ClassBinding.getTypeParams(command);
		params = getParams(command);
		exceptions = getExceptions(command);
	}
}

/**
 * 
 */
class ConstructorBinding extends CommandBinding{

	ConstructorBinding(Pair<Constructor<?>,ConstructorDoc> constructor){
		super( constructor );
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
	 */
	@Override
	public void toXML(XMLPrinter out) {
		out.openNode("Constructor");
			out.addAttribute("name", name);
			out.addAttribute("visibility", visibility.toChar());
			out.print("Documentation", documentation);
			out.print("TypeParameters", typeParams );
			out.print("Parameters", params );
			out.print("Exceptions", exceptions );
			out.print("Links", links);
		out.closeNode();
	}
}

/**
 * 
 */
class MethodBinding extends CommandBinding{

	final int modifiers;

	final ParameterBinding  returnElement;
	
	static Pair<Method,MethodDoc> checkOverride(Pair<Method,MethodDoc> method){
		MethodDoc doc = method.d2;
		if( doc == null)
			return method;
		if( doc.overriddenMethod() != null && doc.getRawCommentText().length() == 0)
			doc.setRawCommentText("@see " + doc.overriddenClass().qualifiedTypeName() +"#"+Adapter.toString(doc));
		return method;
	}
	
	MethodBinding(Pair<Method,MethodDoc> method){
		super( checkOverride(method) );

		this.modifiers = method.d1.getModifiers();

		if(method.d2 == null){
			returnElement = method.d1.getReturnType().equals(java.lang.Void.TYPE)? null:
					new ParameterBinding(method.d1.getGenericReturnType());
		} else {
			returnElement = method.d1.getReturnType().equals(java.lang.Void.TYPE)? null:
					new ParameterBinding(method.d1.getGenericReturnType(), method.d2.tags("@return"));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
	 */
	@Override
	public void toXML(XMLPrinter out) {
		out.openNode("Method");
			out.addAttribute("name", name);
			out.addAttribute("visibility", visibility.toChar());
			if(Modifier.isStatic(modifiers))
				out.addAttribute("isStatic", true);
			if(Modifier.isAbstract(modifiers))
				out.addAttribute("isAbstract", true);
			if(Modifier.isNative(modifiers))
				out.addAttribute("isNative", true);
			if(Modifier.isStrict(modifiers))
				out.addAttribute("isStrictfp", true);
			if(Modifier.isFinal(modifiers))
				out.addAttribute("isFinal", true);
			if(Modifier.isSynchronized(modifiers))
				out.addAttribute("isSynchronized", true);
			out.print("Documentation", documentation);
			out.print("TypeParameters", typeParams );
			out.print("Parameters", params );
			if(returnElement != null)
				returnElement.toXML("ReturnType", out);
			out.print("Exceptions", exceptions );
			out.print("Links", links);
		out.closeNode();
	}
}

/**
 * 
 */
public abstract class ClassBinding extends DocumentedElementLinks{
	
	static class InterfaceBinding extends ClassBinding{
	
		final Collection<ParameterBinding> parameters;
		
		InterfaceBinding(Pair<Class<?>, ClassDoc> clazz){
			super( clazz );
			this.parameters = getTypeParams(clazz);
			
			this.enclosingClasses = getEnclosingClasses(clazz.d1);
			this.interfaces = getImplementedInterfaces(clazz.d1);
			
			this.fields = getFields(clazz);
			this.methods = getMethods(clazz);
		}
	
		/* (non-Javadoc)
		 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
		 */
		@Override
		public void toXML(XMLPrinter out) {
			out.openNode( "Interface" );
				out.addAttribute( "name", name );
				out.print( "Documentation", documentation );
				out.print( "TypeParameters", parameters );
				out.print( "Links", links);
				
				out.print( "EnclosingClasses", enclosingClasses );
				out.print( "Interfaces", interfaces );
				out.print( "Fields", fields );
				out.print( "Methods", methods );
			out.closeNode();
		}
	}

	static class EnumBinding extends ClassBinding{
		
		static Collection<ConstantBinding> getConstants(Pair<Class<?>, ClassDoc> clazz){
			Deque<ConstantBinding> constants = new ArrayDeque<ConstantBinding>();
			if(clazz.d2 == null) {
				for(Object constant: clazz.d1.getEnumConstants())
					constants.offerLast( new ConstantBinding(constant.toString() ) );
			} else {
				Map<String, FieldDoc> map = new TreeMap<String, FieldDoc>(STRING_COMPARATOR);
				for(FieldDoc field: clazz.d2.fields())
					map.put( field.name(), field );
				for(Object constant: clazz.d1.getEnumConstants()){
					FieldDoc field = map.remove(constant.toString());
					if( field != null )
						constants.offerLast( new ConstantBinding(constant.toString(), field ) );
					assert( field != null ): "!!!Documentacion no encontrada para: " + constant.toString();
				}
			}
			return constants;
		}
		
		Collection<ConstantBinding> constants;
		Collection<ConstructorBinding> constructors;
		
		EnumBinding(Pair<Class<?>, ClassDoc> clazz){
			super( clazz );
	
			this.enclosingClasses = getEnclosingClasses(clazz.d1);
			this.interfaces = getImplementedInterfaces(clazz.d1);
			
			this.constants = getConstants(clazz);
			this.fields = getFields(clazz);
			this.constructors = getConstructors(clazz);
			this.methods = getMethods(clazz);
		}
	
		/* (non-Javadoc)
		 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
		 */
		@Override
		public void toXML(XMLPrinter out) {
			out.openNode( "Enum" );
				out.addAttribute( "name", name );
				out.print( "Documentation", documentation);
				out.print( "Links", links);
	
				out.print( "EnclosingClasses", enclosingClasses );
				out.print( "Interfaces", interfaces );
				out.print( "Constants", constants );
				out.print( "Fields", fields );
				out.print( "Constructors", constructors );
				out.print( "Methods", methods );
			
			out.closeNode();
		}
	}

	static class ConcreteClassBinding extends ClassBinding{
		
		static Collection<SimpleClassBinding> getHierarchyBinding(Class<?> clazz){
			Deque<SimpleClassBinding> hierarchy = new ArrayDeque<SimpleClassBinding>();
			for(Type type: getHierarchy(clazz))
				hierarchy.offer( SimpleClassBinding.from(type) );

			return hierarchy;
		}
		
		final boolean isAbstract;
		final Collection<ParameterBinding> parameters;

		final Collection<SimpleClassBinding> hierarchy;
		final Collection<ConstructorBinding> constructors;
		
		ConcreteClassBinding(Pair<Class<?>, ClassDoc> clazz){
			super( clazz );

			this.isAbstract = Modifier.isAbstract( clazz.d1.getModifiers() );
			this.parameters = getTypeParams(clazz);
			
			this.hierarchy = getHierarchyBinding(clazz.d1);
			this.enclosingClasses = getEnclosingClasses(clazz.d1);
			this.interfaces = getImplementedInterfaces(clazz.d1);
			
			this.fields = getFields(clazz);
			this.constructors = getConstructors(clazz);
			this.methods = getMethods(clazz);
		}
		
		/* (non-Javadoc)
		 * @see org.sam.odf_doclet.bindings.XMLSerializable#toXML(org.sam.odf_doclet.bindings.XMLPrinter)
		 */
		@Override
		public void toXML(XMLPrinter out) {
			out.openNode( "Class" );
				out.addAttribute( "name", name );
				if(isAbstract)
					out.addAttribute( "isAbstract", isAbstract );
				out.print( "Documentation", documentation);
				out.print( "TypeParameters", parameters);
				out.print( "Links", links);
				
				out.print( "Hierarchy", hierarchy );
				out.print( "EnclosingClasses", enclosingClasses );
				out.print( "Interfaces", interfaces );
				out.print( "Fields", fields );
				out.print( "Constructors", constructors );
				out.print( "Methods", methods );
			out.closeNode();
		}
	}

	private static ClassLoader classLoader;
	
	public static final void setClassLoader(ClassLoader classLoader){
		ClassBinding.classLoader = classLoader;
	}
	
	static final ClassLoader getClassLoader(){
		if( classLoader == null)
			classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader;
	}
	
	private static Class<?> find(Class<?> containingClazz, String qualifiedName) throws ClassNotFoundException{
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
	
	private static Pair<Class<?>, ClassDoc> getPair( Class<?> clazz, ClassDoc classDoc ) throws ClassNotFoundException {
		if( classDoc.qualifiedName().equals(clazz.getCanonicalName()))
			return new Pair<Class<?>, ClassDoc>( clazz, classDoc );
		return new Pair<Class<?>, ClassDoc>( find(clazz, classDoc.qualifiedName()), classDoc );
	}

	private static Pair<Class<?>, ClassDoc> getPair(ClassDoc classDoc) throws ClassNotFoundException{
		if( classDoc.containingClass() == null)
			return new Pair<Class<?>, ClassDoc>(
					Class.forName( classDoc.qualifiedName(), false, ClassBinding.getClassLoader() ),
					classDoc
			);
		
		ClassDoc containingClass = classDoc;
		while(containingClass.containingClass() != null){
			containingClass = containingClass.containingClass();
		}
		
		return new Pair<Class<?>, ClassDoc>(
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
	
	private static Pair<Class<?>, ClassDoc> getPair(Class<?> clazz) {
		return new Pair<Class<?>, ClassDoc>( clazz, null );
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
	
	static ParamTag[] fileterTypeParameters(Tag[] tags){
		Collection<Tag> typeParameters = new ArrayDeque<Tag>();
		for(Tag tag: tags )
			if( (tag instanceof ParamTag) && ((ParamTag)tag).isTypeParameter())
				typeParameters.add(tag);
		int size = typeParameters.size();
		if( size == 0 )
			return new ParamTag[0];
		return typeParameters.toArray(new ParamTag[size]);
	}

	static Collection<ParameterBinding> getTypeParams(Pair<? extends GenericDeclaration, ? extends Doc> clazz){
		TypeVariable<?>[] types = clazz.d1.getTypeParameters();
		if(types.length == 0)
			return null;
		Collection<ParameterBinding>  parameters= new ArrayDeque<ParameterBinding>();
		ParamTag[] tags = clazz.d2 != null ? fileterTypeParameters(clazz.d2.tags("@param")): null;
		if( tags == null || tags.length != types.length )
			for(TypeVariable<?> type: types)
				parameters.add( new ParameterBinding( type ) );
		else{
			for( int i= 0, len = types.length; i < len; i++ )
				parameters.add( new ParameterBinding( types[i], tags[i] ) );
		}
		return parameters;
	}
	
	private static final Filter<Field> FieldsFilter = new Filter<Field>(){
		@Override
		public boolean validate(Field field) {
			return !field.isSynthetic() && !field.isEnumConstant();
		}
	};
	
	private static final DocFilter<ProgramElementDoc> docsFilter = new DocFilter<ProgramElementDoc>();
	

	
	static Collection<FieldBinding> getFields(Pair<Class<?>, ClassDoc> clazz){
		//TODO Ordenar
		Deque<FieldBinding> fields = new ArrayDeque<FieldBinding>();
		if(clazz.d2 == null) {
			for(Field field: clazz.d1.getDeclaredFields())
				if( FieldsFilter.validate(field) )
					fields.offer( new FieldBinding( new Pair<Field,FieldDoc>(field,null)) );
		} else {
			Map<String, Pair<Field,FieldDoc>> map = new TreeMap<String, Pair<Field,FieldDoc>>(STRING_COMPARATOR);
			docsFilter.classPosition =  clazz.d2.position();
			for(FieldDoc field: clazz.d2.fields()){
				if( docsFilter.validate(field) )
					map.put( field.name(), new Pair<Field,FieldDoc>(null,field) );
			}
			for(Field field: clazz.d1.getDeclaredFields()){
				if( FieldsFilter.validate(field) ){
					Pair<Field,FieldDoc> pair = map.remove(field.getName());
					if( pair != null ){
						pair.d1 = field;
						fields.offer( new FieldBinding( pair ) );
					} 
					assert( pair != null ): "!!!Documentacion no encontrada para: " + field.getName();
				}
			}
//			assert( map.size() == 0 ): mostrar("!!!Documentacion sobrante:", map);
		}
		return fields;
	}
	
	private static final Filter<Constructor<?>> ConstructorsFilter = new Filter<Constructor<?>>(){
		@Override
		public boolean validate(Constructor<?> constructor) {
			return !constructor.isSynthetic();
		}
	};
	
	static Collection<ConstructorBinding> getConstructors(Pair<Class<?>, ClassDoc> clazz){
		//TODO Ordenar
		Deque<ConstructorBinding> constructors = new ArrayDeque<ConstructorBinding>();
		if(clazz.d2 == null) {
			for(Constructor<?> constructor: clazz.d1.getDeclaredConstructors())
				if( ConstructorsFilter.validate(constructor) )
					constructors.add( new ConstructorBinding( new Pair<Constructor<?>,ConstructorDoc>(constructor, null) ) );
		} else {
			docsFilter.classPosition = clazz.d2.position();
			Map<String, Pair<Constructor<?>,ConstructorDoc>> map = 
				new TreeMap<String, Pair<Constructor<?>,ConstructorDoc>>(STRING_COMPARATOR);
			for(ConstructorDoc constructor: clazz.d2.constructors()){
				if( docsFilter.validate(constructor) )
					map.put( 
							Adapter.toString( constructor ),
							new Pair<Constructor<?>,ConstructorDoc>(null, constructor)
					);
			}
			Collection<String> undocumented = new ArrayDeque<String>();
			for(Constructor<?> constructor: clazz.d1.getDeclaredConstructors()){
				if( ConstructorsFilter.validate(constructor) ){
					Pair<Constructor<?>,ConstructorDoc> pair = map.remove( Adapter.toString( constructor ) );
					if( pair == null )
						pair = map.remove( Adapter.toString( constructor, true ) );
					if( pair != null ){
						pair.d1 = constructor;
						constructors.offer( new ConstructorBinding( pair ) );
					} else 
						undocumented.add( Adapter.toString( constructor ) );
				}
			}
			assert( map.size() == 0 ): "!!!Error en "+clazz.d2.name() + ":\n" + 
				AssertHelper.mostrar("Elementos indocumentados:",undocumented) + "\n" +
				AssertHelper.mostrar("Documentacion sobrante :", map);
		}
		return constructors;
	}
	
	private static final Filter<Method> MethodsFilter = new Filter<Method>(){
		@Override
		public boolean validate(Method method) {
			return !method.isSynthetic();
		}
	};
	
	private static final Filter<Method> EnumMethodsFilter = new Filter<Method>(){
		@Override
		public boolean validate(Method method) {
			String methodString = method.toString();
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
	
	static Collection<MethodBinding> getMethods(final Pair<Class<?>, ClassDoc> clazz){
		//TODO Ordenar
		final Filter<Method> filter = clazz.d1.isEnum() ? EnumMethodsFilter : MethodsFilter;
	
		Deque<MethodBinding> methods = new ArrayDeque<MethodBinding>();
		if(clazz.d2 == null) {
			for(Method method: clazz.d1.getDeclaredMethods())
				if( filter.validate(method) )
					methods.offer( new MethodBinding( new Pair<Method, MethodDoc>(method, null) ) );
		} else {
			final DocFilter<? super MethodDoc> filterDoc;
			if( clazz.d1.isEnum() )
				filterDoc = EnumMethodsDocFilter;
			else
				filterDoc = docsFilter;

			filterDoc.classPosition = clazz.d2.position();
			
			Map<String, Pair<Method, MethodDoc>> map = 
				new TreeMap<String, Pair<Method, MethodDoc>>(STRING_COMPARATOR);
			for(MethodDoc method: clazz.d2.methods()){
				if( filterDoc.validate(method) )
					map.put( 
							Adapter.toString( method ),
							new Pair<Method, MethodDoc>(null, method)
					);
			}
			Collection<String> undocumented = new ArrayDeque<String>();
			for(Method method: clazz.d1.getDeclaredMethods()){
				if( filter.validate(method) ){
					Pair<Method, MethodDoc> pair = map.remove( Adapter.toString( method ) );
					if( pair != null ) {
						pair.d1 = method;
						methods.offer( new MethodBinding( pair ) );
					} else 
						undocumented.add( Adapter.toString( method ) );
				}
			}
			assert( map.size() == 0 ):
				"!!!Error en " + clazz.d2.name() + ":\n" +
				AssertHelper.mostrar("Elementos indocumentados:",undocumented) + "\n" +
				AssertHelper.mostrar("Documentacion sobrante :", map);
		}
		return methods;
	}
	
	private final static ClassBinding from(Pair<Class<?>, ClassDoc> clazz){
		if(clazz.d1.isInterface())
			return new InterfaceBinding( clazz );
		if(clazz.d1.isEnum())
			return new EnumBinding( clazz );
		return new ConcreteClassBinding( clazz );
	}
	
	public final static ClassBinding from(Class<?> clazz){
		return from( getPair(clazz) );
	}
	
	public final static ClassBinding from(ClassDoc classDoc) throws ClassNotFoundException{
		return from( getPair(classDoc) );
	}
	
	public final static ClassBinding from(Class<?> clazz, ClassDoc classDoc) throws ClassNotFoundException{
		return from( getPair( clazz, classDoc ) );
	}
	
	Collection<SimpleClassBinding> enclosingClasses;
	Collection<SimpleClassBinding> interfaces;
	
	Collection<FieldBinding> fields;
	Collection<MethodBinding> methods;
	
	ClassBinding(Pair<Class<?>, ClassDoc> clazz) {
		super(Adapter.toString(clazz.d1), clazz.d2);
	}
	
	public final void toXML(OutputStream out) {
		toXML( new XMLPrinter(out) );
	}
}

