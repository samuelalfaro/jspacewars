/* 
 * Cache.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.elementos;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

/**
 * Clase que implementa una caché de objetos.</br>
 * Los objetos que aquí almacenados deberán implementar el interface {@code PrototipoCacheable<T>}.
 * Esto permite: tanto reutilizar las antiguas instancias almacenadas, inicializandolas de nuevo,
 * como generar nuevas instancias, a partir del prototipo almacenado, cuando no quedan más.
 * 
 * @param <T> Tipo genérico de datos almacenados en la caché.
 */
public class Cache<T extends PrototipoCacheable<T>>{

	/**
	 * Clase que encapsula la excepción lanzada cuando se pretende
	 * recuperar la caché un elemento del cual no tiene un prototipo.
	 */
	@SuppressWarnings("serial")
	public static class PrototipoDesconocidoException extends RuntimeException{
		/**
		 * Constructor que crea una nueva excepción con el mensaje detallado de
		 * la causa.
		 * @param str Mensaje detallado de la causa que ha producido la excepción.
		 */
		PrototipoDesconocidoException( String str ){
			super( str );
		}
	}

	private static class Nodo <S>{
		PrototipoCacheable<S> prototipo;
		Queue<S> instancias;

		Nodo( PrototipoCacheable<S> prototipo ){
			this.prototipo = prototipo;
			this.instancias = new LinkedList<S>();
		}
	}

	private int elementos;
	private int tam;
	private int tamMax;
	private Map<Integer, Nodo<T>> prototipos;
	//private int dCreados, dRecuperados; //Borrar comentarios para testear.
	
	private static Comparator<Integer> COMPARADOR = new Comparator<Integer>(){
		public int compare( Integer o1, Integer o2 ){
			return o1.compareTo( o2 );
		}
	};
	
	/**
	 * Constructor que genera una {@code Cache} con una capacidad determinada.
	 * @param tam valor de la capacidad deseada de la {@code Cache}.
	 */
	public Cache( int tam ){
		this.prototipos = new TreeMap<Integer, Nodo<T>>( COMPARADOR );
		this.elementos = 0;
		this.tam = tam;
		this.tamMax = ( tam * 125 ) / 100;
		//this.dCreados = 0;
		//this.dRecuperados = 0;
	}
	
	/**
	 * Método que registra un {@code PrototipoCacheable<T>} en la {@code Cache}.
	 * @param prototipo registrado.
	 */
	public void addPrototipo( PrototipoCacheable<T> prototipo ){
		prototipos.put( prototipo.hashCode(), new Nodo<T>( prototipo ) );
	}
	
	/**
	 * Método que recupera una instancia del tipo solicitado, o genera una nueva 
	 * cuando no quedan.
	 * 
	 * @param tipo valor entero que define el tipo de instancia solicitado.
	 * @return Una instancia del tipo solicitado.
	 * @throws PrototipoDesconocidoException
	 * Si la {@code Cache} no contine ningún prototipo con el código solicitado.
	 */
	public T newObject( int tipo ) throws PrototipoDesconocidoException{
		Nodo<T> nodo = prototipos.get( tipo );
		if( nodo == null )
			throw new PrototipoDesconocidoException( "\nNo existe ningún prototipo con el identificador: " + tipo );
		T newObject;
		if( nodo.instancias.size() > 0 ){
			elementos--;
			//dRecuperados++;
			newObject = nodo.instancias.poll();
		}else{
			//dCreados++;
			newObject = nodo.prototipo.clone();
		}
		newObject.reset();
		return newObject;
	}
	
	/**
	 * Método que almacena una instancia para su posterior reutilización.</br>
	 * En caso de que al almacenar esta última instancia, se sobrepase la capacidad máxima de la {@code Cache},
	 * se elimina un determinado número de instancias de cada tipo almacenando hasta llegar a la capacidad deseada.
	 * De esta forma, siempre habrá más instancias almacenadas de los tipos usados más recientemente.
	 * 
	 * @param elemento a almacenar.
	 * @throws PrototipoDesconocidoException
	 * Si la {@code Cache} no contine ningún prototipo con el código solicitado.
	 */
	public void cached( T elemento ) throws PrototipoDesconocidoException{
		Nodo<T> nodo = prototipos.get( elemento.hashCode() );
		if( nodo == null )
			throw new PrototipoDesconocidoException( "Prototipo " + elemento.hashCode() + " desconcido" );
		nodo.instancias.offer( elemento );
		elementos++;
		if( elementos > tamMax )
			while( elementos > tam ){
				for( Nodo<T> n: prototipos.values() ){
					if( n.instancias.size() > 0 ){
						n.instancias.poll();
						elementos--;
					}
				}
			}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String me = "Caché con capacidad para "+tam+" elementos:\n"+
		"\tElementos en cache:\t"+elementos+"\n"+
		//"\tElementos creados: \t"+dCreados+"\n"+
		//"\tElementos recuperados: \t"+dRecuperados+"\n"+
		"\n";
		return me;
	}
}
