package org.sam.j3d.particulas;

import java.util.Random;


public class EmisorConico implements Emisor{
	
	private static final Random aleatorio = new Random();
	
	private float radio;
	private float altura;
	private Vector v;
	
	public EmisorConico(){
		this.radio  = 1.0f;
		this.altura = 1.0f;
		v = new Vector();
	}

	public EmisorConico(float radio, float altura){
		this.radio  = radio;
		this.altura = altura;
		v = new Vector();
	}
	
	public Vector getVector(){
		double alfa = aleatorio.nextDouble() * Math.PI * 2.0;
		float  mod  = aleatorio.nextFloat() * radio;
		v.posicion.set(0.0f,(float)Math.sin(alfa)*mod,(float)Math.cos(alfa)*mod);

		v.direccion.x = altura;
		v.direccion.y = v.posicion.y;
		v.direccion.z = v.posicion.z;
		v.direccion.normalize();
		
		return v;
	}
}
