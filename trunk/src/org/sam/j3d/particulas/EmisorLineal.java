package org.sam.j3d.particulas;

import java.util.Random;

public class EmisorLineal implements Emisor{
	
	private static final Random aleatorio = new Random();
	
	private float longitud;
	private float rangoPer;
	private Vector v;

	public EmisorLineal(){
		this.longitud = 1.0f;
		this.rangoPer = (float)(Math.PI*2);
		v = new Vector();
	}
	
	public EmisorLineal(float longitud, float rangoPer){
		this.longitud = longitud;
		this.rangoPer = (float)(rangoPer * Math.PI /180.0);
		v = new Vector();
	}

	public Vector getVector(){
		v.posicion.set(0.0f,0.0f,(aleatorio.nextFloat()-0.5f)*longitud);
		float m_rotPer = (aleatorio.nextFloat()-0.5f)*rangoPer;
		v.direccion.set((float)Math.cos(m_rotPer),(float)Math.sin(m_rotPer), 0.0f);
		return v;
	}
}
