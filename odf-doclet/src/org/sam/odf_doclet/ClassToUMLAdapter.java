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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ClassToUMLAdapter {
	
	private static Class<?> getPrimaryComponentType(Class<?> clazz){
		if( clazz.isArray() )
			return getPrimaryComponentType(clazz.getComponentType());
		return clazz;
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
		Package pack = clazz.isArray() ? 
				getPrimaryComponentType(clazz).getPackage():
				clazz.getPackage();
		if(pack == null)
			return clazz.getCanonicalName();
		return  clazz.getCanonicalName().substring(pack.getName().length() + 1);
	}
	
	public static String toString(ParameterizedType type){
		return toString((Class<?>)type.getRawType()) + 
				"<" + toString(type.getActualTypeArguments()) + ">";
	}
	
	public static String toString(Field field){
		return String.format("%s: %s", field.getName(), toString(field.getGenericType()) );
	}

	public static String toString(String classSimpleName, Constructor<?> constructor){
		return String.format( "%1$s(%2$s%3$s%2$s)", 
				classSimpleName,
				constructor.getGenericParameterTypes().length > 0 ? " ":"",
				toString(constructor.getGenericParameterTypes())
		);
	}

	public static String toString(Method method) {
		String returnType = toString( method.getGenericReturnType() );
		return String.format( "%1$s(%2$s%3$s%2$s)%4$s", 
				method.getName(),
				method.getGenericParameterTypes().length > 0 ? " ":"",
				toString( method.getGenericParameterTypes() ),
				!returnType.equals("void") ? ": " + returnType :""
		);
	}
	
	public static char getVisibility(int att){
		if( Modifier.isPublic(att) )
			return '+';
		if( Modifier.isProtected(att) )
			return '~';
		if( Modifier.isPrivate(att) )
			return '-';
		return '#';
	}
}
