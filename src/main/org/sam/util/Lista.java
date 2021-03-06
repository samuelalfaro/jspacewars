/* 
 * Lista.java
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
package org.sam.util;

import java.util.*;

/**
 * Clase que implementa {@code List<E>}. La peculiaridad de esta implementación, es que permite seguir recorriendo la
 * lista con un {@code ListIterator<E>}, a la vez que se añaden elementos al final de la lista, sin lanzar una excepción
 * de acceso concurrente.
 * 
 * @param <E>
 *            Tipo genérico contenido por la lista.
 */
public class Lista <E> implements List<E>{

	private static final class Nodo <D>{
		Nodo<D> pre, pos;
		D datos;

		Nodo( Nodo<D> pre, D datos, Nodo<D> pos ){
			this.datos = datos;
			this.pre = pre;
			this.pos = pos;
		}
	}

	/**
	 * @param nodo que será añadido.
	 */
	void addNodo( Nodo<E> nodo ){
		if( nodo.pre == null )
			primero = nodo;
		else
			nodo.pre.pos = nodo;
		if( nodo.pos == null )
			ultimo = nodo;
		else
			nodo.pos.pre = nodo;
	}

	/**
	 * @param nodo que será eliminado.
	 */
	void removeNodo( Nodo<E> nodo ){
		if( nodo.pre == null )
			primero = nodo.pos;
		else
			nodo.pre.pos = nodo.pos;
		if( nodo.pos == null )
			ultimo = nodo.pre;
		else
			nodo.pos.pre = nodo.pre;
		nodo.pre = null;
		nodo.pos = null;
	}

	private final class Iterador implements ListIterator<E>{
		protected Nodo<E> it, lastReturned;
		protected int index;

		Iterador(){
			this.gotoBegin();
		}

		void gotoBegin(){
			this.it = primero;
			this.lastReturned = null;
			this.index = 0;
		}

		void gotoIndex( final int index ){
			if( index < 0 || index >= size )
				throw new NoSuchElementException( "\nIndex: " + index + "\tSize:" + size );

			if( this.index == index )
				return;

			boolean avanzar;

			if( Math.abs( index - this.index ) < ( size >> 1 ) )
				avanzar = this.index < index;
			else if( avanzar = index < ( size >> 1 ) ){
				this.it = primero;
				this.index = 0;
			}else{
				this.it = ultimo;
				this.index = size - 1;
			}

			if( avanzar ){
				while( this.index < index ){
					this.index++;
					this.it = this.it.pos;
				}
			}else{
				while( this.index > index ){
					this.index--;
					this.it = this.it.pre;
				}
			}
		}

		void gotoEnd(){
			this.it = ultimo;
			this.lastReturned = null;
			this.index = size - 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext(){
			return it != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public E next(){
			if( it == null )
				throw new NoSuchElementException( "\nIndex: " + index + "\tSize:" + size );
			lastReturned = it;
			it = it.pos;
			index++;
			return lastReturned.datos;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove(){
			if( lastReturned == null )
				throw new IllegalStateException();
			removeNodo( lastReturned );
			index--;
			size--;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious(){
			return it != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#previous()
		 */
		public E previous(){
			if( it == null )
				throw new NoSuchElementException( "\nIndex: " + index + "\tSize:" + size );
			lastReturned = it;
			it = it.pre;
			index--;
			return lastReturned.datos;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex(){
			return index + 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex(){
			return index - 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set( E datos ){
			if( lastReturned == null )
				throw new IllegalStateException();
			// Nodo aux = lastReturned;
			// if(aux.pre != null && comparar(aux.pre.datos,datos)>0){
			// do{
			// aux = aux.pre;
			// }while(aux.pre != null && comparar(aux.pre.datos,datos)>0);
			// lastReturned.removeMe();
			// lastReturned.datos = datos;
			// lastReturned.addMe(aux.pre,aux);
			// return;
			// }
			// if (aux.pos != null && comparar(aux.pos.datos,datos)<0){
			// do{
			// aux = aux.pos;
			// }while(aux.pos != null && comparar(aux.pos.datos,datos)<0);
			// lastReturned.removeMe();
			// lastReturned.datos = datos;
			// lastReturned.addMe(aux,aux.pos);
			// return;
			// }
			lastReturned.datos = datos;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		public void add( E datos ){
			addNodo( new Nodo<E>( lastReturned == null ? ultimo: lastReturned.pre, datos, lastReturned ) );
			index++;
			size++;
		}
	}

	private final Iterador iterador;
	/** Nodo<E> que contiene: */
	Nodo<E> primero;
	/** Nodo<E> que contiene: */
	Nodo<E> ultimo;
	/** int que contiene: */
	int size;

	/**
	 * 
	 */
	public Lista(){
		this.primero = this.ultimo = null;
		size = 0;
		this.iterador = new Iterador();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#size()
	 */
	public int size(){
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty(){
		return size == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<E> iterator(){
		iterador.gotoBegin();
		return iterador;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#add(E)
	 */
	public boolean add( E o ){
		addNodo( new Nodo<E>( ultimo, o, null ) );
		size++;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove( Object o ){
		if( indexOf( o ) != -1 ){
			iterador.remove();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains( Object o ){
		iterador.gotoBegin();
		if( o == null ){
			while( iterador.hasNext() ){
				if( iterador.next() == null )
					return true;
			}
		}else{
			while( iterador.hasNext() ){
				if( o.equals( iterador.next() ) )
					return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll( Collection<?> collection ){
		for( Object o: collection )
			if( !contains( o ) )
				return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll( Collection<? extends E> collection ){
		boolean modified = false;
		for( E e: collection )
			if( add( e ) )
				modified = true;
		return modified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll( Collection<?> c ){
		boolean modified = false;
		iterador.gotoBegin();
		while( iterador.hasNext() ){
			if( !c.contains( iterador.next() ) ){
				iterador.remove();
				modified = true;
			}
		}
		return modified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll( Collection<?> c ){
		boolean modified = false;
		iterador.gotoBegin();
		while( iterador.hasNext() ){
			if( c.contains( iterador.next() ) ){
				iterador.remove();
				modified = true;
			}
		}
		return modified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	public void clear(){
		iterador.gotoBegin();
		while( iterador.hasNext() ){
			iterador.next();
			iterador.remove();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray(){
		if( size() == 0 )
			return null;
		Object[] result = (Object[])java.lang.reflect.Array.newInstance( primero.datos.getClass().getComponentType(),
				size() );
		int i = 0;
		for( Nodo<E> n = primero; n != null; n = n.pos )
			result[i++] = n.datos;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */

	@SuppressWarnings( "unchecked" )
	public <T>T[] toArray( T[] a ){
		Object[] result;
		if( a.length < size )
			result = (Object[])java.lang.reflect.Array.newInstance( a.getClass().getComponentType(), size );
		else
			result = a;
		int i = 0;
		for( Nodo<E> n = primero; n != null; n = n.pos )
			result[i++] = n.datos;
		while( i < a.length )
			a[i++] = null;
		return (T[])result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#get(int)
	 */
	public E get( int index ){
		iterador.gotoIndex( index );
		return iterador.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 */
	public boolean addAll( int index, Collection<? extends E> c ){
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public E set( int index, E element ){
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add( int index, E element ){
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(int)
	 */
	public E remove( int index ){
		iterador.gotoIndex( index );
		E datos = iterador.next();
		iterador.remove();
		return datos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf( Object o ){
		iterador.gotoBegin();
		if( o == null )
			while( iterador.hasNext() ){
				if( iterador.next() == null )
					return iterador.previousIndex();
			}
		else
			while( iterador.hasNext() ){
				if( o.equals( iterador.next() ) )
					return iterador.previousIndex();
			}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf( Object o ){
		iterador.gotoEnd();
		if( o == null )
			while( iterador.hasPrevious() ){
				if( iterador.previous() == null )
					return iterador.nextIndex();
			}
		else
			while( iterador.hasPrevious() ){
				if( o.equals( iterador.previous() ) )
					return iterador.nextIndex();
			}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<E> listIterator(){
		iterador.gotoBegin();
		return iterador;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator( final int index ){
		iterador.gotoIndex( index );
		return iterador;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	public List<E> subList( int fromIndex, int toIndex ){
		throw new UnsupportedOperationException();
	}
}
