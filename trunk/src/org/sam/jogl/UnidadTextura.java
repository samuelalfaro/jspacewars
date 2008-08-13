package org.sam.jogl;

import javax.media.opengl.GL;

public class UnidadTextura {
	
	private final static int[] units = new int[]{
		GL.GL_TEXTURE0,
		GL.GL_TEXTURE1,
		GL.GL_TEXTURE2,
		GL.GL_TEXTURE3,
		GL.GL_TEXTURE4,
		GL.GL_TEXTURE5,
		GL.GL_TEXTURE6,
		GL.GL_TEXTURE7,
	};
	public final static int MAX = units.length;
	
	private final static Textura[] tAnteriores =			new Textura[MAX];
	private final static AtributosTextura[] aAnteriores =	new AtributosTextura[MAX];
	private final static GenCoordTextura[] gAnteriores =	new GenCoordTextura[MAX];
	
	private static int lastUnit = -1;
	
	private Textura textura;
	private AtributosTextura atributosTextura;
	private GenCoordTextura genCoordTextura;
	
	public UnidadTextura(){
		textura = null;
		atributosTextura = AtributosTextura.DEFAULT;
		genCoordTextura = null;
	}
	
	public Textura getTextura() {
		return textura;
	}
	
	public void setTextura(Textura textura) {
		this.textura = textura;
	}
	
	public AtributosTextura getAtributosTextura() {
		return atributosTextura;
	}
	
	public void setAtributosTextura(AtributosTextura atributosTextura) {
		this.atributosTextura = atributosTextura;
	}
	
	public GenCoordTextura getGenCoordTextura() {
		return genCoordTextura;
	}
	
	public void setGenCoordTextura(GenCoordTextura genCoordTextura) {
		this.genCoordTextura = genCoordTextura;
	}
	
	public void activar(GL gl, int unit){
		if(textura == null)
			return;
		
		if(lastUnit != unit)
			gl.glActiveTexture(units[unit]);
		
		if(tAnteriores[unit] != textura){
			textura.activar(gl, tAnteriores[unit]);
			tAnteriores[unit] = textura;
		}
		if(aAnteriores[unit] != atributosTextura && atributosTextura != null){
			atributosTextura.usar(gl);
			aAnteriores[unit] = atributosTextura;
		}
		if(gAnteriores[unit] != null && genCoordTextura == null){
			gAnteriores[unit].desactivar(gl);
			gAnteriores[unit] = null;
		}
		if(gAnteriores[unit] != genCoordTextura && genCoordTextura != null){
			genCoordTextura.activar(gl,gAnteriores[unit]);
			gAnteriores[unit] = genCoordTextura;
		}
		lastUnit = unit;
	}
	
	public static void desactivar(GL gl, int unit){
		if(tAnteriores[unit] == null)
	        return;
		
		if(lastUnit != unit)
			gl.glActiveTexture(units[unit]);
        
		tAnteriores[unit].desactivar(gl);
		tAnteriores[unit] = null;
		aAnteriores[unit] = null;
		if(gAnteriores[unit] != null){
			gAnteriores[unit].desactivar(gl);
			gAnteriores[unit] = null;
		}
		lastUnit = unit;
	}
}
