package org.sam.j3d.particulas;

import java.util.Random;


public class EmisorPiramidal implements Emisor{
	private static final Random aleatorio = new Random();
	
	private float lado;
	private float altura;
	private Vector v;
	
	public EmisorPiramidal(){
		this.lado   = 1.0f;
		this.altura = 1.0f;
		v = new Vector();
	}
	
	public EmisorPiramidal(float lado, float altura){
		this.lado   = lado;
		this.altura = altura;
		v = new Vector();
	}
	
	public Vector getVector(){
		v.posicion.set(0.0f,(aleatorio.nextFloat()-0.5f)*lado,(aleatorio.nextFloat()-0.5f)*lado);
		
		v.direccion.x = altura;
		v.direccion.y = v.posicion.y;
		v.direccion.z = v.posicion.z;
		v.direccion.normalize();
		
		return v;
	}
}
