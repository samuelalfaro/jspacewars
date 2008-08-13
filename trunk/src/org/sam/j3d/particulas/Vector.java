package org.sam.j3d.particulas;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Vector{
	public Point3f posicion;
	public Vector3f direccion;

	public Vector(){
		posicion = new Point3f();
		direccion = new Vector3f();
	}
	
	public void set(Vector v){
		posicion.set(v.posicion);
		direccion.set(v.direccion);
	}
}
