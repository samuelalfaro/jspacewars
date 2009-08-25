package org.sam.util;

import java.util.*;

/**
 * @author Samuel
 *
 */
public class Lista<E> implements List<E>{
	
	private static class Nodo<D>{
		protected Nodo<D> pre, pos;
		protected D datos;
		
		Nodo(Nodo<D> pre, D datos, Nodo<D> pos){
			this.datos = datos;
			this.pre = pre;
			this.pos = pos;
		}
	}
	
	private void addNodo(Nodo<E> nodo){
		if (nodo.pre == null)
			primero = nodo;
		else
			nodo.pre.pos = nodo;
		if (nodo.pos == null)
			ultimo = nodo;
		else
			nodo.pos.pre = nodo;
	}
	
	private void removeNodo(Nodo<E> nodo){
		if(nodo.pre == null)
			primero = nodo.pos;
		else
			nodo.pre.pos = nodo.pos;
		if(nodo.pos == null)
			ultimo = nodo.pre;
		else
			nodo.pos.pre = nodo.pre;
		nodo.pre = null;
		nodo.pos = null;
	}
	
	private class Iterador implements ListIterator<E>{
		protected Nodo<E> it, lastReturned;
		protected int index;
		
		private Iterador(){
			this.gotoBegin();
		}
		
		private void gotoBegin(){
			this.it = primero;
			this.lastReturned = null;
			this.index = 0;
		}
		
		private void gotoIndex(final int index){
			if (index<0 || index >=size)
				throw new NoSuchElementException("\nIndex: "+index+"\tSize:"+size);
			
			if(this.index == index)
				return;
			
			boolean avanzar;
			
			if(Math.abs(index-this.index)< (size >> 1))
				avanzar = this.index<index;
			else if( avanzar = index < (size >> 1) ){
				this.it = primero;
				this.index = 0;
			}else{
				this.it = ultimo;
				this.index = size-1;
			}
			
			if(avanzar){
				while(this.index < index){
					this.index ++;
					this.it = this.it.pos;
				}
			}else{
				while(this.index > index){
					this.index --;
					this.it = this.it.pre;
				}
			}
		}
		
		private void gotoEnd(){
			this.it = ultimo;
			this.lastReturned = null;
			this.index = size-1;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return it != null;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public E next() {
			if (it == null)
				throw new NoSuchElementException("\nIndex: "+index+"\tSize:"+size);
			lastReturned = it;
			it = it.pos;
			index++;
			return lastReturned.datos;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			if(lastReturned == null)
				throw new IllegalStateException();
			removeNodo(lastReturned);
			index--;
			size --;
		}
		
		/* (non-Javadoc)
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious() {
			return it != null;
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#previous()
		 */
		public E previous() {
			if (it == null)
				throw new NoSuchElementException("\nIndex: "+index+"\tSize:"+size);
		    lastReturned = it;
			it = it.pre;
			index--;
			return lastReturned.datos;
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex() {
			return index+1;
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex() {
			return index-1;
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set(E datos) {
			if(lastReturned == null)
				throw new IllegalStateException();
//			Nodo aux = lastReturned;
//			if(aux.pre != null && comparar(aux.pre.datos,datos)>0){
//				do{
//					aux = aux.pre;
//				}while(aux.pre != null && comparar(aux.pre.datos,datos)>0);
//				lastReturned.removeMe();
//				lastReturned.datos = datos;
//				lastReturned.addMe(aux.pre,aux);
//				return;
//			}
//			if (aux.pos != null && comparar(aux.pos.datos,datos)<0){
//				do{
//					aux = aux.pos;
//				}while(aux.pos != null && comparar(aux.pos.datos,datos)<0);
//				lastReturned.removeMe();
//				lastReturned.datos = datos;
//				lastReturned.addMe(aux,aux.pos);
//				return;
//			}
			lastReturned.datos = datos;
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		public void add(E datos) {
			addNodo(new Nodo<E>(lastReturned==null?ultimo:lastReturned.pre, datos, lastReturned));
			index++;
			size++;
		}
	}
	
	private final Iterador iterador;
	private Nodo<E> primero, ultimo;
	private int size;
	
	/**
	 * 
	 */
	public Lista(){
		this.primero = this.ultimo = null;
		size = 0;
		this.iterador = new Iterador();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size(){
		return size;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty(){
		return size==0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<E> iterator(){
		iterador.gotoBegin();
		return iterador;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#add(E)
	 */
	public boolean add(E o){
		addNodo(new Nodo<E>(ultimo, o, null));
		size++;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o){
		if(indexOf(o)!=-1){
			iterador.remove();
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o){
		iterador.gotoBegin();
		if (o==null){
			while(iterador.hasNext()){
				if (iterador.next()==null)
					return true;
			}
		}else{
			while(iterador.hasNext()){
				if (o.equals(iterador.next()))
					return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> collection){
		for(Object o: collection)
			if(!contains(o))
				return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> collection){
		boolean modified = false;
		for(E e: collection)
			if (add(e))
				modified = true;
		return modified;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c){
		boolean modified = false;
		iterador.gotoBegin();
		while (iterador.hasNext()) {
			if (!c.contains(iterador.next())) {
				iterador.remove();
				modified = true;
			}
		}
		return modified;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		iterador.gotoBegin();
		while (iterador.hasNext()) {
			if (c.contains(iterador.next())) {
				iterador.remove();
				modified = true;
			}
		}
		return modified;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		iterador.gotoBegin();
		while (iterador.hasNext()) {
			iterador.next();
			iterador.remove();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray(){
		if( size()== 0)
			return null;
		Object[] result = (Object[])java.lang.reflect.Array.newInstance(primero.datos.getClass().getComponentType(), size());
		int i = 0;
        for (Nodo<E> n = primero; n != null; n = n.pos)
            result[i++] = n.datos;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = size();
		Object[] result;
		if (a.length < size)
			result = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		else
			result = a;
		int i = 0;
        for ( Nodo n = primero; n != null; n = n.pos)
        	result[i++] = n.datos;
		while(i < a.length)
			a[i++] = null;
		return (T[])result;
	}
    
	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public E get(int index) {
		iterador.gotoIndex(index);
		return iterador.next();
	}

	/* (non-Javadoc)
	 * @see
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public E remove(int index) {
		iterador.gotoIndex(index);
		E datos = iterador.next();
		iterador.remove();
		return datos;
	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		iterador.gotoBegin();
		if (o==null)
			while(iterador.hasNext()){
				if (iterador.next() == null)
					return iterador.previousIndex();
			}
		else
			while(iterador.hasNext()){
				if (o.equals(iterador.next()))
					return iterador.previousIndex();
			}
		return -1;
	}

	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		iterador.gotoEnd();
		if (o==null)
			while(iterador.hasPrevious()){
				if (iterador.previous() == null)
					return iterador.nextIndex();
			}
		else
			while(iterador.hasPrevious()){
				if (o.equals(iterador.previous()))
					return iterador.nextIndex();
			}
		return -1;
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<E> listIterator() {
		iterador.gotoBegin();
		return iterador;
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator(final int index) {
		iterador.gotoIndex(index);
		return iterador;
	}

	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
}
