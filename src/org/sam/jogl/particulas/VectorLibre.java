package org.sam.jogl.particulas;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class VectorLibre{
	public Point3f posicion;
	public Vector3f direccion;

	public VectorLibre(){
		posicion = new Point3f();
		direccion = new Vector3f();
	}

	public VectorLibre(VectorLibre v){
		posicion = new Point3f();
		direccion = new Vector3f();
		this.set(v);
	}
	
	public void set(VectorLibre v){
		posicion.set(v.posicion);
		direccion.set(v.direccion);
	}
}
