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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class ClassToUMLAdapter {
	
	private static Class<?> getPrimaryComponentType(Class<?> clazz){
		if( clazz.isArray() )
			return getPrimaryComponentType(clazz.getComponentType());
		return clazz;
	}
	
	public static String toString(Type[] params, int offset){
		String stringParams = "";
		for(int i = offset; i < params.length; ){
			stringParams += toString(params[i]);
			if(++i < params.length)
				stringParams += ", ";
		}
		return stringParams;
	}
	
	public static String toString(Type[] params){
		return toString( params, 0 );
	}
	
	public static String toString(Type type){
		if(type instanceof Class<?>)
			return toString((Class<?>)type);
		if(type instanceof ParameterizedType)
			return toString((ParameterizedType)type);
		if(type instanceof GenericArrayType)
			return toString(((GenericArrayType)type).getGenericComponentType())+"[]";
		if(type instanceof WildcardType)
			return toString((WildcardType)type);
		//"*TypeVariable*: {"+type.getName()+"}";
		return ((TypeVariable<?>)type).getName();
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
	
	public static String toString(WildcardType type){
		StringBuffer buff = new StringBuffer("?");
		Type[] bounds = type.getUpperBounds();
		if( bounds != null && bounds.length > 0){
			if( !( bounds[0] instanceof Class<?>  && ((Class<?>)bounds[0]).equals(Object.class) ) ){
				int i = 0;
				buff.append(" extends ");
				while(true){
					buff.append(toString(bounds[i]));
					if( ++i ==  bounds.length)
						return buff.toString();
					buff.append(", ");
				}
			}
		}
		bounds = type.getLowerBounds();
		if( bounds != null && bounds.length > 0){
			int i = 0;
			buff.append(" super ");
			while(true){
				buff.append(toString(bounds[i]));
				if( ++i ==  bounds.length)
					return buff.toString();
				buff.append(", ");
			}
		}
		return buff.toString();
	}
	
	public static String toString(Field field){
		return String.format("%s: %s", field.getName(), toString(field.getGenericType()) );
	}

	public static String toString(Constructor<?> constructor){
		return String.format( "%1$s(%2$s%3$s%2$s)", 
				constructor.getDeclaringClass().getSimpleName(),
				constructor.getGenericParameterTypes().length > 0 ? " ":"",
				toString(constructor.getGenericParameterTypes() , constructor.getDeclaringClass().isEnum() ? 2: 0 )
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
}
