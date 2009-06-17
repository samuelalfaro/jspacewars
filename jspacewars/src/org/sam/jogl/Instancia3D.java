package org.sam.jogl;

import java.nio.ByteBuffer;
import java.util.*;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3f;

import org.sam.red.Recibible;
import org.sam.util.*;

public class Instancia3D extends NodoTransformador implements Prototipo<Instancia3D>, Modificable, Recibible, Cacheable{

	private static class ModificardorChilds implements Modificador{
		
		private final transient Modificador modificadores[];
	
		private ModificardorChilds(Collection<Modificador> modificadores){
			this.modificadores = modificadores.toArray( new Modificador[modificadores.size()] );
		}
		
		/* (non-Javadoc)
		 * @see org.sam.util.Modificador#modificar(float)
		 */
		@Override
		public boolean modificar(float steep) {
			boolean result = false;
			for( Modificador  modificador: this.modificadores )
				result = modificador.modificar(steep) || result;
			return result;
		}
	}

	private final int tipo;
	private transient int id;
	
	private final transient AxisAngle4f rotation;
	private final transient Vector3f translation;
	
	private final transient Modificador modificador;
	private final transient Cacheable cacheableChilds[];

	public Instancia3D(int tipo, Nodo child){
		super(child);

		Deque<Nodo> stack = new LinkedList<Nodo>();
		Collection<Cacheable> cacheables = new LinkedList<Cacheable>();
		Collection<Modificador> modificadores = new LinkedList<Modificador>();
		stack.push(child);
		do{
			Nodo actual = stack.pop();
			if( actual instanceof Cacheable )
				cacheables.add((Cacheable) actual);
			if( actual instanceof Modificable )
				modificadores.add(((Modificable) actual).getModificador());
			for( Nodo nodo: actual.getChilds() )
				stack.push(nodo);
		}while( !stack.isEmpty() );

		if( modificadores.size() > 0 )
			this.modificador = new ModificardorChilds(modificadores);
		else
			this.modificador = null;

		if( cacheables.size() > 0 )
			this.cacheableChilds = cacheables.toArray(new Cacheable[cacheables.size()]);
		else
			this.cacheableChilds = null;
		
		this.tipo = tipo;
		this.rotation = new AxisAngle4f();
		this.translation = new Vector3f();
	}
	
	protected Instancia3D(Instancia3D prototipo){
		super(prototipo);
		
		Deque<Nodo> stack = new LinkedList<Nodo>();
		Collection<Cacheable> cacheables = new LinkedList<Cacheable>();
		Collection<Modificador> modificadores = new LinkedList<Modificador>();
		
		stack.push(this.getChilds()[0]);
		do{
			Nodo actual = stack.pop();
			if( actual instanceof Cacheable )
				cacheables.add((Cacheable) actual);
			if( actual instanceof Modificable )
				modificadores.add(((Modificable) actual).getModificador());
			for( Nodo nodo: actual.getChilds() )
				stack.push(nodo);
		}while( !stack.isEmpty() );

		if( modificadores.size() > 0 )
			this.modificador = new ModificardorChilds(modificadores);
		else
			this.modificador = null;

		if( cacheables.size() > 0 )
			this.cacheableChilds = cacheables.toArray(new Cacheable[cacheables.size()]);
		else
			this.cacheableChilds = null;
		
		this.tipo = prototipo.tipo;
		this.rotation = new AxisAngle4f(prototipo.rotation);
		this.translation = new Vector3f();
	}
	
	public int getTipo(){
		return tipo;
	}
	
	public int hashCode(){
		return tipo;
	}
	
	public final void setId(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setAxis(Vector3f axis){
		this.rotation.x = axis.x;
		this.rotation.y = axis.y;
		this.rotation.z = axis.z;
	}

	public void recibir(ByteBuffer buff){
		translation.x = buff.getFloat();
		translation.y = buff.getFloat();
		rotation.angle = buff.getFloat();
		transformMatrix.setIdentity();
		transformMatrix.setRotation(rotation);
		transformMatrix.setTranslation(translation);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.util.Modificable#getModificador()
	 */
	@Override
	public Modificador getModificador() {
		return modificador;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Instancia3D clone(){
		return new Instancia3D(this);
	}
	
	public String toString(){
		return new String("Instancia3D: "+hashCode()+" : " + this.id);
	}

	/* (non-Javadoc)
	 * @see org.sam.util.Prototipo#reset()
	 */
	public void reset() {
		if(cacheableChilds == null)
			return;
		for(Cacheable child: cacheableChilds)
			child.reset();
	}
}
