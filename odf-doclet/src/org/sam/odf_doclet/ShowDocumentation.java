/* 
 * ShowDocumentation.java
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
package org.sam.odf_doclet;

import java.io.PrintStream;

import org.sam.odf_doclet.bindings.ClassBinding;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;

public class ShowDocumentation {
	
	private static final boolean equals(SourcePosition o1, SourcePosition o2) {
		if(o1 == null)
			return o2 == null;
		return o1.line() == o2.line() && o1.column() == o2.column();
	}
	
	@SuppressWarnings("unused")
	private static void print(ClassDoc classDoc, PrintStream out){
		out.println("\n"+classDoc.name());
		out.println("\t"+classDoc.commentText());
		out.println("\nFields:");
		for(FieldDoc fieldDoc: classDoc.fields()){

			out.println("\t"+ClassDocToUMLAdapter.toString( fieldDoc ) );
			out.println("\t\t"+fieldDoc.commentText());
		}
		out.println("\nConstructors:");
		for(ConstructorDoc constructorDoc: classDoc.constructors()){
			if( !equals( constructorDoc.position(), classDoc.position() ) ){
				out.println("\t["+constructorDoc.position().line()+"] "+ClassDocToUMLAdapter.toString( constructorDoc ) );
				out.println("\t\t"+constructorDoc.commentText());
				for(ParamTag tag: constructorDoc.paramTags()){
					out.println( "\t\t\t"+tag.parameterName()+"\t"+tag.parameterComment());
				}
				for(SeeTag tag: constructorDoc.seeTags()){
					out.println( "\t\t\t"+tag.referencedMember());
				}
			}
		}
		out.println("\nMethods:");
		for(MethodDoc methodDoc: classDoc.methods()){
			if( !equals( methodDoc.position(), classDoc.position() ) ){
				out.println("\t["+methodDoc.position().line()+"] "+ClassDocToUMLAdapter.toString( methodDoc ) );
				out.println("\t"+methodDoc.commentText());

				for (ParamTag tag : methodDoc.typeParamTags()) {
					out.println("\t\t\t" + tag.parameterName() + "\t" + tag.parameterComment());
				}

				for (ParamTag tag : methodDoc.paramTags()) {
					out.println("\t\t\t" + tag.parameterName() + "\t" + tag.parameterComment());
				}

				Tag[] returnTags = methodDoc.tags("@return");
				if (returnTags != null && returnTags.length > 0) {
					StringBuilder builder = new StringBuilder();
					for (Tag returnTag : returnTags) {
						String returnTagText = returnTag.text();
						if (returnTagText != null) {
							builder.append(returnTagText);
							builder.append("\n");
						}
					}
					out.println( "\t\t\treturn\t"+ builder.substring(0, builder.length() - 1) );
				}
				for(ThrowsTag tag: methodDoc.throwsTags()){
					out.println( "\t\t\t"+tag.exceptionName()+"\t"+tag.exceptionComment());
				}
			}
		}
	}
	
	public static boolean start(RootDoc root) throws ClassNotFoundException {

		ClassDoc[] classes = root.classes();

		for(ClassDoc classDoc: classes)
			ClassBinding.from(classDoc).toXML(System.out);
		return true;
	}
	
	public static int optionLength(String option) {
		if (option.equalsIgnoreCase("-projectRootpath")) 
			return 2;
		if (option.equalsIgnoreCase("-projectClasspath"))
			return 2;
		return 0;
	}

	public static boolean validOptions(String options[][], DocErrorReporter reporter) {
		
		String projectRootpath  = ""; boolean foundProjectRootpath = false;
		String projectClasspath = ""; boolean foundProjectClasspath = false;
		
		for(String[] option: options){
			if(option[0].equalsIgnoreCase("-projectRootpath")){
				if(foundProjectRootpath){
					reporter.printError("Only one -projectRootpath option allowed.");
					return false;
				}
				foundProjectRootpath = true;
				projectRootpath = option[1];
			}else if(option[0].equalsIgnoreCase("-projectClasspath")){
				if(foundProjectClasspath){
					reporter.printError("Only one -projectClasspath option allowed.");
					return false;
				}
				foundProjectClasspath = true;
				projectClasspath = option[1];
			}
		}
		ClassBinding.setClassLoader(ClassLoaderTools.getLoader( projectRootpath, projectClasspath ) );
		return true;
	}
}