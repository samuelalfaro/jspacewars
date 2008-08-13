package org.sam.elementos;

public class LanzaMisiles extends Canion{
	/**
	 * Angulo de disparo del cañon
	 */
	private float angulo;
	
	public LanzaMisiles(
			int[] vIdDisparos,
			int[] vTRecarga,
			int[] vDesfases,
			float[] vVelocidades,
			int grado){
		super(vIdDisparos,vTRecarga,vDesfases,vVelocidades,grado);
	}

	private LanzaMisiles(LanzaMisiles prototipo){
		super(prototipo);
		this.angulo = prototipo.angulo;
	}

	public Canion clone(){
		return new LanzaMisiles(this);
	}
	
	public void setAngulo(float angulo){
		this.angulo = (float)(angulo/180.0 * Math.PI);
	}

	public void dispara(Nave contenedor, long milis){
		tTranscurrido += milis;
		while(tTranscurrido >= vTRecarga[grado]){
			Misil disparo = (Misil)cache.newObject(vIdDisparos[grado]);
			disparo.setId(Canion.getId());
			disparo.setValues(contenedor.getX()+posX,contenedor.getY()+posY, angulo, vVelocidades[grado]);
			//TODO disparo.setObjetivo(diana);
			contenedor.getDstDisparos().add(disparo);
			tTranscurrido -= vTRecarga[grado];
		}
	}
}