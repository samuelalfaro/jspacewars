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
	
	public static String toString(Type[] params, int offset, boolean showGenericType){
		StringBuffer buff = new StringBuffer();
		for(int i = offset; i < params.length; ){
			buff.append( showGenericType ? toString(params[i]) : toStringNoGerenic(params[i]) );
			if(++i < params.length)
				buff.append(", ");
		}
		return buff.toString();
	}
	
	public static String toString(Type[] params){
		return toString( params, 0, true );
	}
	
	public static String toString(TypeVariable<?>[] params){
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < params.length; ){
			buff.append( toString(params[i] ) );
			if(++i < params.length)
				buff.append(", ");
		}
		return buff.toString();
	}
	
	private static String toStringNoGerenic(Type type){
		if(type instanceof Class<?>)
			return ((Class<?>)type).getCanonicalName();
		if(type instanceof ParameterizedType)
			return ((Class<?>)((ParameterizedType)type).getRawType()).getCanonicalName();
		if(type instanceof GenericArrayType)
			return toStringNoGerenic(((GenericArrayType)type).getGenericComponentType())+"[]";
		if(type instanceof TypeVariable)
			return toStringNoGerenic(((TypeVariable<?>)type).getBounds()[0]);
		//Como no se trata ParameterizedType => no hay WildcardType
		//TypeVariable se trata como Object
		assert(type instanceof WildcardType);
			return null;
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
		// Cuando no se especifique explicitamente toString(? typeVariable)):
		// TypeVariable<?> tv = < T extends A & B & C >
		// Type ty = tv
		// toString(tv) --> T extends A & B & C
		// toString(ty) --> T
		if(type instanceof TypeVariable)
			return ((TypeVariable<?>)type).getName();

		assert(false):"Implementación desconcida";
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
	
	public static String toString(TypeVariable<?> type){
		StringBuffer buff = new StringBuffer(type.getName());
		Type[] bounds = type.getBounds();
		if( bounds != null && bounds.length > 0 &&
				!( bounds[0] instanceof Class<?>  && ((Class<?>)bounds[0]).equals(Object.class) ) ){
			buff.append(" extends ");
			int i = 0;
			while(true){
				buff.append(toString(bounds[i]));
				if( ++i ==  bounds.length)
					return buff.toString();
				buff.append(" & ");
			}
		}
		return buff.toString();
	}
	
	public static String toString(WildcardType type){
		StringBuffer buff = new StringBuffer("?");
		Type[] bounds = type.getUpperBounds();
		if( bounds != null && bounds.length > 0 &&
				!( bounds[0] instanceof Class<?>  && ((Class<?>)bounds[0]).equals(Object.class) ) ){
			buff.append(" extends ");
			int i = 0;
			while(true){
				buff.append(toString(bounds[i]));
				if( ++i ==  bounds.length)
					return buff.toString();
				buff.append(", ");
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
	
	public static String toString(Constructor<?> constructor, boolean hideSyntheticAccesor, boolean showGenericType){
		int offset = constructor.getDeclaringClass().isEnum() ? 2 : hideSyntheticAccesor ? 1: 0;
		return String.format( "%1$s(%2$s%3$s%2$s)", 
				constructor.getDeclaringClass().getSimpleName(),
				constructor.getGenericParameterTypes().length > offset ? " ":"",
				toString(constructor.getGenericParameterTypes(), offset, showGenericType )
		);
	}
	
	public static String toString(Constructor<?> constructor){
		return toString(constructor, false, true );
	}
	
	public static String toString(Method method, boolean showGenericType){
		Type   returnType = method.getGenericReturnType();
		String returnTypeStr =  showGenericType ? toString(returnType) : toStringNoGerenic(returnType);
		return String.format( "%1$s(%2$s%3$s%2$s)%4$s", 
				method.getName(),
				method.getGenericParameterTypes().length > 0 ? " ":"",
				toString( method.getGenericParameterTypes(), 0, showGenericType ),
				!returnTypeStr.equals("void") ? ": " + returnTypeStr :""
		);
	}
	
	public static String toString(Method method) {
		return toString(method, true);
	}
}
