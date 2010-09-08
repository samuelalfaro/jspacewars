package org.sam.jogl;

import java.nio.ByteBuffer;
import java.util.*;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3f;

import org.sam.elementos.*;

/**
 * Implementación de un {@code Nodo} que permite respresentar,
 * los elementos, de una rama del grafo, como un único elemento independiente.
 */
public class Instancia3D extends NodoTransformador implements Modificable, Recibible, PrototipoCacheable<Instancia3D>{

	private static class ModificardorChilds implements Modificador{
		
		private final transient Modificador modificadores[];
	
		private ModificardorChilds(Collection<Modificador> modificadores){
			this.modificadores = modificadores.toArray( new Modificador[modificadores.size()] );
		}
		
		public boolean modificar(float steep) {
			boolean result = false;
			for( Modificador  modificador: this.modificadores )
				result = modificador.modificar(steep) || result;
			return result;
		}
	}

	private final short type;
	private transient short id;
	
	private final transient AxisAngle4f rotation;
	private final transient Vector3f translation;
	
	private final transient Modificador modificador;
	private final transient Reseteable reseteableChilds[];

	/**
	 * Constructor que crea {@code Instancia3D} con los datos correspondientes.
	 * 
	 * @param type Entero que sirve de indentificador tanto de esta {@code Instancia3D},
	 * como de las generen usando esta como prototipo. 
	 * @param child {@code Nodo} descendiente.
	 */
	public Instancia3D(short type, Nodo child){
		super(child);

		Deque<Nodo> stack = new LinkedList<Nodo>();
		Collection<Reseteable> reseteables = new LinkedList<Reseteable>();
		Collection<Modificador> modificadores = new LinkedList<Modificador>();
		stack.push(child);
		do{
			Nodo actual = stack.pop();
			if( actual instanceof Reseteable )
				reseteables.add((Reseteable) actual);
			if( actual instanceof Modificable )
				modificadores.add(((Modificable) actual).getModificador());
			for( Nodo nodo: actual.getChilds() )
				stack.push(nodo);
		}while( !stack.isEmpty() );

		if( modificadores.size() > 0 )
			this.modificador = new ModificardorChilds(modificadores);
		else
			this.modificador = null;

		if( reseteables.size() > 0 )
			this.reseteableChilds = reseteables.toArray(new Reseteable[reseteables.size()]);
		else
			this.reseteableChilds = null;
		
		this.type = type;
		this.rotation = new AxisAngle4f();
		this.translation = new Vector3f();
	}
	
	/**
	 * Constructor que crea una {@code Instancia3D}, a partir de 
	 * los datos de otra {@code Instancia3D} que se emplea como
	 * prototipo.
	 * 
	 * @param prototipo {@code Instancia3D} empleada como prototipo.
	 */
	protected Instancia3D(Instancia3D prototipo){
		super(prototipo);
		
		Deque<Nodo> stack = new LinkedList<Nodo>();
		Collection<Reseteable> reseteables = new LinkedList<Reseteable>();
		Collection<Modificador> modificadores = new LinkedList<Modificador>();
		
		stack.push(this.getChilds()[0]);
		do{
			Nodo actual = stack.pop();
			if( actual instanceof Reseteable )
				reseteables.add((Reseteable) actual);
			if( actual instanceof Modificable )
				modificadores.add(((Modificable) actual).getModificador());
			for( Nodo nodo: actual.getChilds() )
				stack.push(nodo);
		}while( !stack.isEmpty() );

		if( modificadores.size() > 0 )
			this.modificador = new ModificardorChilds(modificadores);
		else
			this.modificador = null;

		if( reseteables.size() > 0 )
			this.reseteableChilds = reseteables.toArray(new Reseteable[reseteables.size()]);
		else
			this.reseteableChilds = null;
		
		this.type = prototipo.type;
		this.rotation = new AxisAngle4f(prototipo.rotation);
		this.translation = new Vector3f();
	}
	
	/**
	 * <i>Getter</i> que devuelve el entero que sirve de indentificador
	 * tanto de esta {@code Instancia3D}, como las generadas a partir 
	 * del mismo prototipo que esta.
	 * @return Valor solicitado.
	 */
	public short getType(){
		return type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode(){
		return type;
	}
	
	/**
	 * <i>Setter</i> que asigna el entero que sirve de indentificador
	 * único de esta {@code Instancia3D}.
	 * @param id Valor asignado.
	 */
	public final void setId(short id){
		this.id = id;
	}
	
	/**
	 * <i>Getter</i> que devuelve el entero que sirve de indentificador
	 * único de esta {@code Instancia3D}.
	 * @return Valor solicitado.
	 */
	public int getId() {
		return id;
	}

	/**
	 * <i>Setter</i> que asigna el eje de rotación.
	 * @param axis {@code Vector3f} con el eje de rotación asignado.
	 */
	public void setAxis(Vector3f axis){
		this.rotation.x = axis.x;
		this.rotation.y = axis.y;
		this.rotation.z = axis.z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recibir(ByteBuffer buff){
		translation.x = buff.getFloat();
		translation.y = buff.getFloat();
		rotation.angle = buff.getFloat();
		transformMatrix.setIdentity();
		transformMatrix.setRotation(rotation);
		transformMatrix.setTranslation(translation);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Modificador getModificador() {
		return modificador;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instancia3D clone(){
		return new Instancia3D(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(){
		return new String("Instancia3D: "+hashCode()+" : " + this.id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		if(reseteableChilds == null)
			return;
		for(Reseteable child: reseteableChilds)
			child.reset();
	}
}
