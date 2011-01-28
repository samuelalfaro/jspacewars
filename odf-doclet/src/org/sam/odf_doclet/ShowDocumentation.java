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

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;

public class ShowDocumentation {
	
	public static boolean start(RootDoc root) {
		ClassDoc[] classes = root.classes();
		for(ClassDoc classDoc: classes){
			System.err.println("\n"+classDoc.name());
			System.err.println("\t"+classDoc.commentText());
			System.err.println("\nFields:");
			for(FieldDoc fieldDoc: classDoc.fields()){
				System.err.println("\t"+fieldDoc.name()+": "+fieldDoc.type().typeName()+fieldDoc.type().dimension());
				System.err.println("\t\t"+fieldDoc.commentText());
			}
			System.err.println("\nConstructors:");
			for(ConstructorDoc constructorDoc: classDoc.constructors()){
				if( constructorDoc.position() != null && constructorDoc.position().line() != classDoc.position().line() ){
					System.err.println("\t"+constructorDoc.commentText());
					System.err.print( "\t\t["+constructorDoc.position().line()+"] "+constructorDoc.name() +"(");
					
					for(Parameter parameter:constructorDoc.parameters()){
						System.err.print(parameter.name()+": "+parameter.type().typeName()+parameter.type().dimension()+", ");
					}
					System.err.println(")");
					for(ParamTag tag: constructorDoc.paramTags()){
						System.err.println( "\t\t\t"+tag.parameterName()+"\t"+tag.parameterComment());
					}
					for(SeeTag tag: constructorDoc.seeTags()){
						System.err.println( "\t\t\t"+tag.referencedMember());
					}
				}
			}
			System.err.println("\nMethods:");
			for(MethodDoc methodDoc: classDoc.methods()){
				if( methodDoc.position() != null && methodDoc.position().line() != classDoc.position().line() ){
					System.err.println("\t"+methodDoc.commentText());
					System.err.print( "\t\t["+methodDoc.position().line()+"] "+methodDoc.name() +"(");
					
					for(Parameter parameter: methodDoc.parameters()){
						System.err.print(parameter.name()+": "+parameter.type().typeName()+parameter.type().dimension()+", ");
					}
					if (methodDoc.returnType().typeName().equals("void"))
						System.err.println(")");
					else
						System.err.println("):" + methodDoc.returnType().typeName() + methodDoc.returnType().dimension());

					for (ParamTag tag : methodDoc.paramTags()) {
						System.err.println("\t\t\t" + tag.parameterName() + "\t" + tag.parameterComment());
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
						System.err.println( "\t\t\treturn\t"+ builder.substring(0, builder.length() - 1) );
			        }
					for(ThrowsTag tag: methodDoc.throwsTags()){
						System.err.println( "\t\t\t"+tag.exceptionName()+"\t"+tag.exceptionComment());
					}
				}
			}
			
		}
		return true;
	}
}