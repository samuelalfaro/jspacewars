package org.sam.j3d;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

public class ElementoMovil extends Elemento {
	private static Bounds limites = new BoundingBox(new Point3d(-30.0,-15.0,-8.0),new Point3d (30.0,15.0,8.0));
	private static Point3d posicion= new Point3d();
	private static Vector3f desplazamiento= new Vector3f();

	Vector3f direccion;
	float    velocidad;
	
	public ElementoMovil(int code,SharedGroup forma){
		super(code, forma);
	}
	
	protected ElementoMovil(ElementoMovil me){
		super(me);
		this.direccion = me.direccion;
		this.velocidad = me.velocidad;
	}
	
	public ElementoMovil clone(){
		return new ElementoMovil(this);
	}
	
	public void setVelocidad(Vector3f direccion, float velocidad){
		this.direccion = direccion;	
		this.velocidad = velocidad;
	}
	
	public boolean modificar(float steep){
		super.modificar(steep);
		posicion.set(getPoscion());
		if( limites.intersect(posicion) ){
			desplazamiento.set(direccion);
			desplazamiento.scale(velocidad*steep);
			mover(desplazamiento);
			aplicarTransformacion();
			return true;
		}
		eliminar();
		return false;
	}
}
