/**
 * 
 */
package org.sam.jogl;

import java.util.*;

import javax.media.opengl.GL;

/**
 * @author samuel
 * 
 */
public class Grupo implements Nodo {
	private transient Nodo parent;
	private final Collection<Nodo> childs;

	public Grupo() {
		childs = new LinkedList<Nodo>();
	}

	protected Grupo(Grupo me) {
		childs = new LinkedList<Nodo>();
		for(Nodo nodo: me.childs){
			Nodo clon = nodo.clone();
			clon.setParent(this);
			childs.add(clon);
		}
	}
	
	public Iterator<Dibujable> iterator(){
		return iterator();
	}
	
	public boolean add(Nodo e) {
		if(childs.add(e)){
			e.setParent(this);
			return true;
		}
		return false;
	}

	public boolean remove(Nodo e) {
		if(childs.remove(e)){
			e.setParent(null);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL)
	 */
	public void draw(GL gl) {
		for( Nodo  child: childs )
			 child.draw(gl);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#setParent(org.sam.jogl.Nodo)
	 */
	public void setParent(Nodo parent) {
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#getParent()
	 */
	public Nodo getParent() {
		return parent;
	}
	
	private transient Nodo[] nodos;
	
	public Nodo[] getChilds(){
		if(nodos == null || nodos.length != childs.size()){
			nodos = new Nodo[childs.size()];
		}
		return childs.toArray(nodos);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Grupo clone(){
		return new Grupo(this);
	}
}
