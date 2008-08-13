package org.sam.util;

import java.lang.reflect.*;

/**
 * Clase que proporciona métodos estaticos, para trabajar de forma más cómoda con la reflexión.
 */
public class Reflexion {
	
	private static boolean isAssignableOrPrimitiveFrom( Class<?>  clazz, Object obj ){
		if(clazz.isAssignableFrom(obj.getClass()))
			return true;
		if(clazz.isPrimitive()){
			try {
				Field field =  obj.getClass().getField("TYPE");
				return clazz.isAssignableFrom( (Class<?>)field.get(null) );
			} catch (Exception e) {
			}
		} 
		return false;
	}
	
	private static boolean isAssignable(Class<?> objTypes[], Object objs[]){
		if(objTypes.length != objs.length)
			return false;
		int i = 0, len = objTypes.length;
		while(i < len){
			if ( !isAssignableOrPrimitiveFrom( objTypes[i], objs[i] ) )
				return false;
			i++;
		}
		return true;
	}
	
	/**
	 * Busca el constructor de una clase que tenga los parametros indicados.
	 * 
	 * @param clase Clase en la que se busca el constructor.
	 * @param args Parametros que recibe el constructor buscado.
	 * @return El constructor buscado, o null si no se ha podido encontrar.
	 */
	public static Constructor<?> findConstructor(Class<?> clase, Object... args){
		Constructor<?> constructors[] = clase.getDeclaredConstructors();
		if(constructors == null)
			return null;
		for(Constructor<?> constructor: constructors){
			if( isAssignable( constructor.getParameterTypes(), args ))
				return constructor;
		}
		return null;
	}
	
	/**
	 * Busca un método en una clase que tenga el nombre y los parametros indicados.
	 * 
	 * @param clase Clase en la que se busca el método.
	 * @param name Nombre del método buscado.
	 * @param args Parámetros que recibe el método buscado.
	 * @return El método buscado, o null si no se ha podido encontrar.
	 */
	public static Method findMethod(Class<?> clazz, String name, Object... args){
		Method metodos[] = clazz.getMethods();
		if(metodos == null)
			return null;
		for(Method metodo: metodos){
			if( metodo.getName().equals(name) && isAssignable(metodo.getParameterTypes(),args))
				return metodo;
		}
		return null;
	}
	
	/**
	 * Busca un setter en una clase que con el nombre "set"+"FieldName" y los parametros indicados.
	 * 
	 * @param clase Clase en la que se busca el método.
	 * @param fieldName Nombre del miembro para el que buscamos el setter.
	 * @param args Parámetros que recibe el método buscado.
	 * @return El setter buscado, o null si no se ha podido encontrar.
	 */
	public static Method findSetter(Class<?> clazz, String fieldName, Object... args){
		String setterName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1,fieldName.length());
		return findMethod(clazz, setterName, args);
	}
	
	/**
	 * Busca un miembro en una clase que tenga el nombre indicado.
	 * 
	 * @param clase Clase en la que se busca el miembro.
	 * @param name Nombre del miembro buscado.
	 * @return El miembro buscado, o null si no se ha podido encontrar.
	 */
	public static Field findField(Class<?> clazz, String fieldName){
		try {
			return clazz.getField(fieldName);
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
		return null;
	}
	
	public static String findFieldName(Class<?> clazz, int value){
		Field miembros[] = clazz.getFields();
		try {
			for (Field miembro : miembros) {
				int modifiers = miembro.getModifiers();
				if (Modifier.isPublic(modifiers)
						&& Modifier.isStatic(modifiers)
						&& Modifier.isFinal(modifiers)
						&& miembro.getInt(null) == value)
					return miembro.getName();
			}
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}
	
	public static String findFieldName(Class<?> clazz, Object value){
		Field miembros[] = clazz.getFields();
		try{
			for (Field miembro : miembros) {
				int modifiers = miembro.getModifiers();
				if (Modifier.isPublic(modifiers)
						&& Modifier.isStatic(modifiers)
						&& Modifier.isFinal(modifiers)) {
					Object valorMiembro = miembro.get(null);
					if ((valorMiembro != null && valorMiembro.equals(value))
							|| (valorMiembro == null && value == null))
						return miembro.getName();
				}
			}
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}
}
