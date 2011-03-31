/* 
 * Recorders.java
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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.sam.xml.Recorder;
import org.sam.xml.RecordersMapper;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

/**
 * 
 */
public class Recorders {
	
	/**
	 * Method writeNode.
	 * @param nodeName String
	 * @param content String
	 * @param writer XMLWriter
	 */
	static void writeNode( String nodeName, String content, XMLWriter writer ) {
		if( content != null && content.length() > 0 ){
			writer.openNode(nodeName);
				writer.write(content);
			writer.closeNode();
		}
	}
	
	/**
	 * Method writeNode.
	 * @param nodeName String
	 * @param collection Collection<T>
	 * @param writer XMLWriter
	 * @param mapper RecordersMapper
	 */
	static <T> void writeNode( String nodeName, Collection<T> collection, XMLWriter writer, RecordersMapper mapper ) {
		if( collection != null && collection.size() > 0 ){
			writer.openNode( nodeName );
			for( T t: collection )
				mapper.getRecorder(t.getClass()).record(t, writer, mapper);
			writer.closeNode();
		}
	}
	
	private static final HtmlCleaner cleaner;
	private static final PrettyXmlSerializer serializer;
	    
	static{
		CleanerProperties  props = new CleanerProperties();
		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		
		cleaner = new HtmlCleaner(props);
		serializer = new PrettyXmlSerializer(props);
	}
	
	/**
	 * Method clean.
	 * @param s String
	 * @return String
	 */
	static String clean(String s){
		if(s == null || s.length() == 0)
			return s;
		try {
			return serializer.getAsString( cleaner.clean(s), true );
		} catch (IOException e) {
			return null;
		}
	}
	
	private Recorders(){}

	private static class SimpleInterfaceRecorder implements Recorder<SimpleClassBinding.Interface>{
		
		SimpleInterfaceRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(SimpleClassBinding.Interface t, XMLWriter writer, RecordersMapper mapper){
			writeNode( "Interface", t.name, writer );
		}
	}

	private static class SimpleEnumRecorder implements Recorder<SimpleClassBinding.Enum>{
		
		SimpleEnumRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(SimpleClassBinding.Enum t, XMLWriter writer, RecordersMapper mapper){
			writeNode( "Enum", t.name, writer );
		}
	}

	private static class SimpleClassRecorder implements Recorder<SimpleClassBinding.Clazz>{
		
		SimpleClassRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(SimpleClassBinding.Clazz t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode("Class");
				if(t.isAbstract)
					writer.addAttribute("isAbstract", t.isAbstract);
				writer.write(t.name);
			writer.closeNode();
		}
	}

	private static class DocumentedTypeRecorder implements Recorder<DocumentedType>{
		
		DocumentedTypeRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(DocumentedType t, XMLWriter writer, RecordersMapper mapper){
			String nodeName = null;
			if( t instanceof TypeParamBinding)
				nodeName = "Parameter";
			else if( t instanceof ReturnTypeBinding)
				nodeName = "ReturnType";
			else if( t instanceof ExceptionBinding)
				nodeName = "Exception";
			assert(nodeName != null): "!!!Tipo: "+ t.getClass().toString() + " desconocido";
			
			writer.openNode(nodeName);
				writeNode( "Type", t.type, writer );
				writeNode( "Documentation", clean( t.documentation ), writer );
			writer.closeNode();
		}
	}

	private static class ParameterRecorder implements Recorder<ParameterBinding>{
		
		ParameterRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(ParameterBinding t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode("Parameter");
				if(t.name!=null)
					writer.addAttribute("name", t.name);
				writeNode( "Type", t.type, writer );
				writeNode( "Documentation", clean( t.documentation ), writer );
			writer.closeNode();
		}
	}
	
	private static class LinkRecorder implements Recorder<LinkBinding>{
		
		LinkRecorder(){}

		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(LinkBinding t, XMLWriter writer, RecordersMapper mapper){
			writeNode( "Link", t.link,  writer );
		}
	}

	private static class ConstantRecorder implements Recorder<ConstantBinding>{
		
		ConstantRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(ConstantBinding t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode("Constant");
				writer.addAttribute("name", t.name);
				writeNode( "Documentation", clean( t.documentation ), writer );
				writeNode( "Links", t.links, writer, mapper );
			writer.closeNode();
		}
	}

	private static class FieldRecorder implements Recorder<FieldBinding>{
		
		FieldRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(FieldBinding t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode("Field");
				writer.addAttribute("name", t.name);
				writer.addAttribute("visibility", t.visibility.toChar());
				if(Modifier.isStatic(t.modifiers))
					writer.addAttribute("isStatic", true);
				if(Modifier.isTransient(t.modifiers))
					writer.addAttribute("isTransient", true);
				if(Modifier.isVolatile(t.modifiers))
					writer.addAttribute("isVolatile", true);
				if(Modifier.isFinal(t.modifiers))
					writer.addAttribute("isFinal", true);
				writeNode( "Type", t.type, writer );
				writeNode( "Documentation", clean( t.documentation ), writer );
				writeNode( "Links", t.links, writer, mapper );
			writer.closeNode();
		}
	}

	private static class ConstructorRecorder implements Recorder<ConstructorBinding>{
		
		ConstructorRecorder(){}

		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(final ConstructorBinding t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode("Constructor");
				writer.addAttribute("name", t.name);
				writer.addAttribute("visibility", t.visibility.toChar());
				writeNode( "Documentation", clean( t.documentation ), writer );
				writeNode( "TypeParameters", t.typeParams, writer, mapper );
				writeNode( "Parameters", t.params, writer, mapper );
				writeNode( "Exceptions", t.exceptions, writer, mapper );
				writeNode( "Links", t.links, writer, mapper );
			writer.closeNode();
		}
	}

	private static class MethodRecorder implements Recorder<MethodBinding>{
		
		MethodRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(MethodBinding t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode("Method");
				writer.addAttribute("name", t.name);
				writer.addAttribute("visibility", t.visibility.toChar());
				if(Modifier.isStatic(t.modifiers))
					writer.addAttribute("isStatic", true);
				if(Modifier.isAbstract(t.modifiers))
					writer.addAttribute("isAbstract", true);
				if(Modifier.isNative(t.modifiers))
					writer.addAttribute("isNative", true);
				if(Modifier.isStrict(t.modifiers))
					writer.addAttribute("isStrictfp", true);
				if(Modifier.isFinal(t.modifiers))
					writer.addAttribute("isFinal", true);
				if(Modifier.isSynchronized(t.modifiers))
					writer.addAttribute("isSynchronized", true);
				writeNode( "Documentation", clean( t.documentation ), writer );
				writeNode( "TypeParameters", t.typeParams, writer, mapper );
				writeNode( "Parameters", t.params, writer, mapper );
				if(t.returnType != null)
					mapper.getRecorder( ReturnTypeBinding.class ).record(t.returnType, writer, mapper );
				writeNode( "Exceptions", t.exceptions, writer, mapper );
				writeNode( "Links", t.links, writer, mapper );
			writer.closeNode();
		}
	}

	private static class InterfaceRecorder implements Recorder<ClassBinding.Interface>{
		
		InterfaceRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(ClassBinding.Interface t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode( "Interface" );
				writer.addAttribute( "name", t.name );
				writeNode( "Documentation", clean( t.documentation ), writer );
				writeNode( "TypeParameters", t.parameters, writer, mapper );
				writeNode( "Links", t.links, writer, mapper );
				
				writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
				writeNode( "Interfaces", t.interfaces, writer, mapper );
				writeNode( "Fields", t.fields, writer, mapper );
				writeNode( "Methods", t.methods, writer, mapper );
			writer.closeNode();
		}
	}

	private static class EnumRecorder implements Recorder<ClassBinding.Enum>{
		
		EnumRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(ClassBinding.Enum t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode( "Enum" );
				writer.addAttribute( "name", t.name );
				writeNode( "Documentation", clean( t.documentation ), writer );
				writeNode( "Links", t.links, writer, mapper );
				
				writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
				writeNode( "Interfaces", t.interfaces, writer, mapper );
				writeNode( "Constants", t.constants, writer, mapper );
				writeNode( "Fields", t.fields, writer, mapper );
				writeNode( "Methods", t.methods, writer, mapper );
			writer.closeNode();
		}
	}

	private static class ClassRecorder implements Recorder<ClassBinding.Clazz>{
		
		ClassRecorder(){}
		
		/* (non-Javadoc)
		 * @see org.sam.xml.Recorder#record(java.lang.Object, org.sam.xml.XMLWriter, org.sam.xml.RecordersMapper)
		 */
		@Override
		public void record(ClassBinding.Clazz t, XMLWriter writer, RecordersMapper mapper){
			writer.openNode( "Class" );
				writer.addAttribute( "name", t.name );
				if(t.isAbstract)
					writer.addAttribute( "isAbstract", t.isAbstract );
				writeNode( "Documentation", clean( t.documentation ), writer );
				writeNode( "TypeParameters", t.parameters, writer, mapper );
				writeNode( "Links", t.links, writer, mapper );
				
				writeNode( "Hierarchy", t.hierarchy, writer, mapper );
				writeNode( "EnclosingClasses", t.enclosingClasses, writer, mapper );
				writeNode( "Interfaces", t.interfaces, writer, mapper );
				writeNode( "Fields", t.fields, writer, mapper );
				writeNode( "Constructors", t.constructors, writer, mapper );
				writeNode( "Methods", t.methods, writer, mapper );
			writer.closeNode();
		}
	}
	
	/**
	 * Method register.
	 * @param converter XMLConverter
	 */
	public static void register(XMLConverter converter){

		converter.register( new SimpleInterfaceRecorder());
		converter.register( new SimpleEnumRecorder());
		converter.register( new SimpleClassRecorder());
		converter.register( new DocumentedTypeRecorder());
		converter.register( new ParameterRecorder());
		converter.register( new LinkRecorder());
		converter.register( new ConstantRecorder());
		converter.register( new FieldRecorder());
		converter.register( new ConstructorRecorder());
		converter.register( new MethodRecorder());
		converter.register( new InterfaceRecorder());
		converter.register( new EnumRecorder());
		converter.register( new ClassRecorder());

	}
}
