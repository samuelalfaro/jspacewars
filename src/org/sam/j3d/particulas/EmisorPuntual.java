package org.sam.j3d.particulas;

import java.util.Random;
import javax.vecmath.Vector3f;


public class EmisorPuntual implements Emisor{
	
	private static final Random aleatorio = new Random();
	
	private float rangoPer;
	private float rangoNor;
	private Vector v;

	public EmisorPuntual(){
		this.rangoPer = (float)(Math.PI*2);
		this.rangoNor = (float)(Math.PI);;
		v = new Vector();
	}
	
	public EmisorPuntual(float rangoPer, float rangoNor){
		this.rangoPer = (float)(rangoPer * Math.PI /180.0);
		this.rangoNor = (float)(rangoNor * Math.PI /180.0);
		v = new Vector();
	}

	public Vector getVector(){
		v.posicion.set(0.0f,0.0f,0.0f);

		float m_rotPer = (aleatorio.nextFloat()-0.5f)*rangoPer;
		float m_rotNor = (aleatorio.nextFloat()-0.5f)*rangoNor;
		Vector3f direccion = v.direccion;
		direccion.x = (float)Math.cos(m_rotNor);
		direccion.y = (float)Math.sin(m_rotNor);
		direccion.z =  (float)Math.sin(m_rotPer) * direccion.x;
		direccion.x *= (float)Math.cos(m_rotPer);
		
		return v;
	}
}
