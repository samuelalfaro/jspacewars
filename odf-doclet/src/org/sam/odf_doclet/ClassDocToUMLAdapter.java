/* 
 * ClassToUMLAdapter.java
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

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

public class ClassDocToUMLAdapter {
	
	public static String toString(Parameter[] params){
		String stringParams = "";
		for(int i = 0; i < params.length; ){
			stringParams += toString(params[i].type());
			if(++i < params.length)
				stringParams += ", ";
		}
		return stringParams;
	}
	
	public static String toString(Type[] params){
		String stringParams = "";
		for(int i = 0; i < params.length; ){
			stringParams += toString(params[i]);
			if(++i < params.length)
				stringParams += ", ";
		}
		return stringParams;
	}
	
	public static String toString(Type type){
		String name = type.qualifiedTypeName();
		ClassDoc clazz = type.asClassDoc();
		if(clazz != null){
			int packageLength = clazz.containingPackage().name().length();
			if( packageLength > 0 )
				name = name.substring(packageLength + 1);
		}
		ParameterizedType parameterizedType = type.asParameterizedType();
		if(parameterizedType != null){
			System.err.print("**ParameterizedType: " + type);
			for(Type p:parameterizedType.typeArguments())
				System.err.print(toString(p) + ", ");
			System.err.println();
		}		
		TypeVariable typeVariable = type.asTypeVariable();
		if(typeVariable != null){
			System.err.println("**TypeVariable: " + type);
		}
		WildcardType wildcardType = type.asWildcardType();
		if(wildcardType != null){
			System.err.println("**WildcardType: " + type);
		}
		return name + type.dimension();
	}

//	public static String toString(ClassDoc clazz){
//		int packageLength = clazz.containingPackage().name().length();
//		return packageLength > 0 ?
//				clazz.qualifiedTypeName().substring( packageLength + 1 ):
//				clazz.qualifiedTypeName();	
//	}
//	
//	public static String toString(ParameterizedType type){
//		System.err.println("**typeArguments: " + type.asClassDoc().typeParameters().length);
//		int packageLength = type.asClassDoc().containingPackage().name().length();
//		return ( packageLength > 0 ?
//				type.qualifiedTypeName().substring( packageLength + 1 ):
//				type.qualifiedTypeName() )
//				+ "<" + toString(type.asClassDoc().typeParameters()) + ">";
//	}
	
	public static String toString(FieldDoc field){
		return String.format("%s: %s", field.name(), toString(field.type()) );
	}

	public static String toString(ConstructorDoc constructor){
		return String.format( "%1$s(%2$s%3$s%2$s)", 
				constructor.name(),
				constructor.parameters().length > 0 ? " ":"",
				toString(constructor.parameters() )
		);
	}

	public static String toString(MethodDoc method) {
		String returnType = toString( method.returnType() );
		return String.format( "%1$s(%2$s%3$s%2$s)%4$s", 
				method.name(),
				method.parameters().length > 0 ? " ":"",
				toString(method.parameters()),
				!returnType.equals("void") ? ": " + returnType :""
		);
	}
}
