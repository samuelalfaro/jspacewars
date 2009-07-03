package org.sam.elementos;

import java.util.Collection;

public class CanionLineal extends Canion{
	/**
	 * Angulo de disparo del cañon
	 */
	private float angulo;
	
	public CanionLineal(CanionData data) {
		super(data);
	}

	private CanionLineal(CanionLineal prototipo){
		super(prototipo);
		this.angulo = prototipo.angulo;
	}

	public Canion clone(){
		return new CanionLineal(this);
	}
	
	public void setAngulo(float angulo){
		this.angulo = (float)(angulo/180.0f * Math.PI);
	}

	/* (non-Javadoc)
	 * @see org.sam.elementos.Canion#dispara(float, float, float, float, long, java.util.Collection)
	 */
	public void dispara( float mX, float nX, float mY, float nY, long nanos, Collection <Disparo> dst ){
		int tRecarga =  data.vTRecarga[grado];
		int t = tRecarga - (int)tTranscurrido;
		
		tTranscurrido += nanos;
		while(tTranscurrido >= tRecarga){
			DisparoLineal disparo = (DisparoLineal)cache.newObject(data.vIdDisparos[grado]);
			disparo.setId(Canion.getId());
			
			disparo.setValues( t *mX + nX + posX, t *mY + nY + posY, angulo, data.vVelocidades[grado]);
			disparo.actua(nanos-t);
			dst.add(disparo);
			t += tRecarga;
			tTranscurrido -= tRecarga;
		}
	}
}