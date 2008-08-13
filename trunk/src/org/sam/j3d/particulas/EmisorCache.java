package org.sam.j3d.particulas;

import java.util.Random;

import javax.media.j3d.Transform3D;


public class EmisorCache implements Emisor{
	
	@SuppressWarnings("serial")
	private static Random aleatorio = new Random(){
		public int nextInt(int nBits) {
			return next(nBits)*3;
		}
	};
	
	private static int log2(int val){
		val--;
		int n=0;
		while(val>0){
			val >>=1;
			n++;
		}
		return n;
	}
	
	private final int pow2;
	private final float[] pos;
	private final float[] dir;
	private Vector v;
	
	public EmisorCache(Emisor e, int size){
		this.pow2 = log2(size);
		size = (int)Math.pow(2,pow2);
		pos = new float[size*3];
		dir = new float[size*3];
		for(int i = 0, j=0; i<size; i++){
			Vector aux = e.getVector();
			pos[j]= aux.posicion.x; dir[j++]= aux.direccion.x;
			pos[j]= aux.posicion.y; dir[j++]= aux.direccion.y;
			pos[j]= aux.posicion.z; dir[j++]= aux.direccion.z;
		}
		v = new Vector();
	}
	
	public EmisorCache(Emisor e, Transform3D t, int size){
		this.pow2 = log2(size);
		size = (int)Math.pow(2,pow2);
		pos = new float[size*3];
		dir = new float[size*3];
		for(int i = 0, j=0; i<size; i++){
			Vector aux = e.getVector();
			t.transform(aux.posicion);
			t.transform(aux.direccion);
			pos[j]= aux.posicion.x; dir[j++]= aux.direccion.x;
			pos[j]= aux.posicion.y; dir[j++]= aux.direccion.y;
			pos[j]= aux.posicion.z; dir[j++]= aux.direccion.z;
		}
		v = new Vector();
	}
	
	public Vector getVector(){
		int i = aleatorio.nextInt(pow2);
		v.posicion.x = pos[i]; v.direccion.x = dir[i++];
		v.posicion.y = pos[i]; v.direccion.y = dir[i++];
		v.posicion.z = pos[i]; v.direccion.z = dir[i++];
		return v;
	}
}