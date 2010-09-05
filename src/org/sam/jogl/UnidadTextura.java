package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase que contiene los miembros relativos al manejo de texturas.
 */
public class UnidadTextura {
	
	private final transient static int[] units = new int[]{
		GL.GL_TEXTURE0,
		GL.GL_TEXTURE1,
		GL.GL_TEXTURE2,
		GL.GL_TEXTURE3,
		GL.GL_TEXTURE4,
		GL.GL_TEXTURE5,
		GL.GL_TEXTURE6,
		GL.GL_TEXTURE7,
	};
	
	/**
	 * Entero que indica el número máximo de unidades de textura que puede contener una {@code Apariencia}.<br/>
	 * Actualmente tiene un valor constante de 8. Queda para posteriores implementaciones, ajustar este valor,
	 * dinámicamente, en función de la capacidad del hardware.
	 */
	public final transient static int MAX = units.length;
	
	private final transient static Textura[] tAnteriores =			new Textura[MAX];
	private final transient static AtributosTextura[] aAnteriores =	new AtributosTextura[MAX];
	private final transient static GenCoordTextura[] gAnteriores =	new GenCoordTextura[MAX];
	
	private transient static int lastUnit = -1;
	
	private Textura textura;
	private AtributosTextura atributosTextura;
	private GenCoordTextura genCoordTextura;
	
	/**
	 * Constructor que crea una {@code UnidadTextura}.
	 */
	public UnidadTextura(){
		textura = null;
		atributosTextura = AtributosTextura.DEFAULT;
		genCoordTextura = null;
	}
	
	/**
	 * <i>Getter</i> que devuelve la {@code Textura} de esta {@code UnidadTextura}.
	 * @return La {@code Textura} solicitada.
	 */
	public Textura getTextura() {
		return textura;
	}
	
	/**
	 * <i>Setter</i> que asigna la {@code Textura} a esta {@code UnidadTextura}.
	 * @param textura La {@code Textura} asignada.
	 */
	public void setTextura(Textura textura) {
		this.textura = textura;
	}
	
	/**
	 * <i>Getter</i> que devuelve los {@code AtributosTextura} de esta {@code UnidadTextura}.
	 * @return Los {@code AtributosTextura} solicitados.
	 */
	public AtributosTextura getAtributosTextura() {
		return atributosTextura;
	}
	
	/**
	 * <i>Setter</i> que asigna los {@code AtributosTextura} a esta {@code UnidadTextura}.
	 * @param atributosTextura Los {@code AtributosTextura} asignados.
	 */
	public void setAtributosTextura(AtributosTextura atributosTextura) {
		this.atributosTextura = atributosTextura;
	}
	
	/**
	 * <i>Getter</i> que devuelve el {@code GenCoordTextura} de esta {@code UnidadTextura}.
	 * @return El {@code GenCoordTextura} solicitado.
	 */
	public GenCoordTextura getGenCoordTextura() {
		return genCoordTextura;
	}
	
	/**
	 * <i>Setter</i> que asigna el {@code GenCoordTextura} a esta {@code UnidadTextura}.
	 * @param genCoordTextura El {@code GenCoordTextura} asignado.
	 */
	public void setGenCoordTextura(GenCoordTextura genCoordTextura) {
		this.genCoordTextura = genCoordTextura;
	}
	
	/**
	 * Método que se encarga de usar activar esta {@code UnidadTextura}, activando o desactivando
	 * los atributos necesarios, dependiendo del estado anterior.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * @param unit Índice de la unidad de textura correspondiente.
	 */
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
	
	/**
	 * Método que se encarga de usar desactivar esta {@code UnidadTextura}, activando o desactivando
	 * los atributos necesarios, dependiendo del estado anterior.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * @param unit Índice de la unidad de textura correspondiente.
	 */
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
