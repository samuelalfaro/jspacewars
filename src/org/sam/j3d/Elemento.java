package org.sam.j3d;

import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.sam.j3d.particulas.Particulas;
import org.sam.util.*;

/**
 * @author Samuel
 *
 */
public class Elemento extends BranchGroup implements Prototipo<Elemento>, Modificable, Modificador{

//	protected final static Eliminador eliminador = new Eliminador();
//	static{
//		eliminador.start();
//	}
	private final int code;
	private Vector3f posicion;
	private Transform3D    tr;
	private TransformGroup tg;
	private final SharedGroup forma;
	private List<Particulas> particulas = new LinkedList<Particulas>();
	private Gestor gestorParticulas = new Gestor();
	
	public Elemento(int code, SharedGroup forma) {
		this.code = code;
		this.forma = forma;
		this.posicion = new Vector3f();
		tr = new Transform3D();
		
		tg = new TransformGroup(tr);
		tg.setCapability(Group.ALLOW_CHILDREN_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.addChild(new Link(forma) );

		this.setCapability(BranchGroup.ALLOW_DETACH);
		this.addChild(tg);
		//this.compile();
	}
	
	protected Elemento(Elemento me){
		this.code = me.code;
		this.forma = me.forma;
		this.posicion = new Vector3f(me.posicion);
		tr = new Transform3D(me.tr);
			
		tg = new TransformGroup(tr);
		tg.setCapability(Group.ALLOW_CHILDREN_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.addChild(new Link(forma) );

		this.setCapability(BranchGroup.ALLOW_DETACH);
		super.addChild(tg);
		
		Iterator<Particulas> it = me.particulas.iterator();
		while(it.hasNext()){
			this.addParticulas(it.next().clone());
		}
		this.compile();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.util.Prototipo#clone()
	 */
	public Elemento clone() {
		return new Elemento(this);
	}
	
	public final int hashCode(){
		return code;
	}
	
	public void addParticulas(Particulas particula){
		particula.iniciar();
		particulas.add(particula);
		particula.setTExterna(tr);
		addChild(particula);
		gestorParticulas.add(particula.getModificador());
	}
	
	/*
	public void addChild(Node n){
		throw new UnsupportedOperationException();
	}
	public Enumeration	getAllChildren(){
		throw new UnsupportedOperationException();
	}
	public Node	getChild(int index){
		throw new UnsupportedOperationException();
	}
	public int	indexOfChild(Node child){
		throw new UnsupportedOperationException();
	}
	public void	insertChild(Node child, int index){
		throw new UnsupportedOperationException();
	}
	public int	numChildren(){
		throw new UnsupportedOperationException();
	}
	public void	removeAllChildren(){
		throw new UnsupportedOperationException();
	}
	public void	removeChild(int index){
		throw new UnsupportedOperationException();
	}
	public void	removeChild(Node child){
		throw new UnsupportedOperationException();
	}
	public void	setChild(Node child, int index){
		throw new UnsupportedOperationException();
	}
	*/
	
	public void setEscala(float scale){
		tr.setScale(scale);
	}

	private static Transform3D rot = new Transform3D();
	
	public void setRotaciones(float x, float y, float z){
		if(x!=0){
			rot.rotX(x);
			tr.mul(rot);
		}
		if(y!=0){
			rot.rotY(y);
			tr.mul(rot);
		}
		if(z!=0){
			rot.rotZ(z);
			tr.mul(rot);
		}
	}
	
	public void setPosicion(Tuple3f posicion){
		this.posicion.set(posicion);
		tr.setTranslation(this.posicion);
	}
	
	public void setPosicion(float x, float y, float z){
		posicion.set(x,y,z);
		tr.setTranslation(this.posicion);
	}
	
	public void mover(Vector3f velocidad){
		this.posicion.add(velocidad);
		tr.setTranslation(posicion);
	}
	
	public void aplicarTransformacion(){
		tg.setTransform(tr);
	}
	
	public Vector3f getPoscion(){
		return posicion;
	}
	
	public Modificador getModificador(){
		return this;
	}
	
	public boolean modificar(float steep){
		gestorParticulas.modificar(steep);
		return true;
	}
	
	public final void eliminar(){
		CacheDeElementos.cached(this);
		this.detach();
//		eliminador.eliminar(this);
	}
}
