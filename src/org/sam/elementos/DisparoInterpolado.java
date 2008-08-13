package org.sam.elementos;

import org.sam.interpoladores.Trayectoria;

public class DisparoInterpolado extends Disparo{

	private transient float despX, despY;
	private transient float scaleX, scaleY;
	private transient float angulo;
	private transient long time;
	private final Trayectoria.Float<float[]> trayectoria;
	
	DisparoInterpolado(short code, Trayectoria.Float<float[]> trayectoria){
		super(code);
		this.trayectoria = trayectoria; 
	}
	
	protected DisparoInterpolado(DisparoInterpolado prototipo){
		super(prototipo);
		this.trayectoria = prototipo.trayectoria; 
	}
	
	public DisparoInterpolado clone() {
		return new DisparoInterpolado(this);
	}
	
	public void setValues(float posX, float posY, float scaleX, float scaleY){
		super.setPosicion(posX, posY);
		despX = posX;
		despY = posY;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public void setTime(long time){
		this.time = time;
	}
	
	public void actua(long milis){
		time += milis;
		float posicion[] = trayectoria.getPosTang((float)(time/1000.0));
		posX = posicion[0] * scaleX + despX;
		posY = posicion[1] * scaleY + despY;
		angulo = posicion[2]*scaleY;
	}
	
	public double getAngulo(){
		return angulo;
	}
}