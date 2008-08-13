package org.sam.elementos;

public class CanionLineal extends Canion{
	/**
	 * Angulo de disparo del cañon
	 */
	private float angulo;
	
	public CanionLineal(
			int[] vIdDisparos,
			int[] vTRecarga,
			int[] vDesfases,
			float[] vVelocidades,
			int grado){
		super(vIdDisparos,vTRecarga,vDesfases,vVelocidades,grado);
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

	public void dispara(Nave contenedor, long milis){
		tTranscurrido += milis;
		while(tTranscurrido >= vTRecarga[grado]){
			DisparoLineal disparo = (DisparoLineal)cache.newObject(vIdDisparos[grado]);
			disparo.setId(Canion.getId());
			disparo.setValues(contenedor.getX()+posX,contenedor.getY()+posY, angulo, vVelocidades[grado]);
			contenedor.getDstDisparos().add(disparo);
			tTranscurrido -= vTRecarga[grado];
		}
	}
}