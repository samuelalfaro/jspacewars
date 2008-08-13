package org.sam.elementos;

public class CanionInterpolado extends Canion{
	/**
	 * Escala de la trayectoria del disparo
	 */
	private float scaleX, scaleY;

	public CanionInterpolado(
			int[] vIdDisparos,
			int[] vTRecarga,
			int[] vDesfases,
			float[] vVelocidades,
			int grado){
		super(vIdDisparos,vTRecarga,vDesfases,vVelocidades,grado);
	}

	private CanionInterpolado(CanionInterpolado prototipo){
		super(prototipo);
		this.scaleX = prototipo.scaleX;
		this.scaleY = prototipo.scaleY;
	}

	public Canion clone(){
		return new CanionInterpolado(this);
	}
	
	public void setScales(float scaleX, float scaleY){
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	public void dispara(Nave contenedor, long milis){
		tTranscurrido += milis;
		while(tTranscurrido >= vTRecarga[grado]){
			DisparoInterpolado disparo = (DisparoInterpolado)cache.newObject(vIdDisparos[grado]);
			disparo.setId(Canion.getId());	
			disparo.setValues(contenedor.getX()+posX,contenedor.getY()+posY, scaleX, scaleY);
			disparo.setTime(0);
			contenedor.getDstDisparos().add(disparo);
			tTranscurrido -= vTRecarga[grado];
		}
	}
}