package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase que se encarga de agrupar todos los atributos relativos a la apariencia de los
 * objetos, para activarlos o desactivarlos convenientemente.
 */
public class Apariencia {

	private static transient Apariencia anterior;
	
	private AtributosColor atributosColor;
	private AtributosLinea atributosLinea;
	private AtributosPunto atributosPunto;
	private AtributosPoligono atributosPoligono;
	private AtributosRender atributosRender;
	private Material material;
	private AtributosTransparencia atributosTransparencia;
	private UnidadTextura[] unidadesTextura;
	private Shader shader;

	/**
	 * Constructor que crea una {@code Apariencia} con su valores por defecto.
	 */
	public Apariencia(){
		this.atributosColor= null;
		this.atributosLinea = null;
		this.atributosPunto= null;
		this.atributosPoligono= null;
		this.atributosRender= null;
		this.material= null;
		this.atributosTransparencia= null;
		this.unidadesTextura= null;
		this.shader= null;
	}
	
	/**
	 * <i>Getter</i> que devuelve los {@code AtributosColor} de esta {@code Apariencia}.
	 * @return Los {@code AtributosColor} solicitados.
	 */
	public AtributosColor getAtributosColor(){
		return atributosColor;
	}

	/**
	 * <i>Getter</i> que devuelve los {@code AtributosLinea} de esta {@code Apariencia}.
	 * @return Los {@code AtributosLinea} solicitados.
	 */
	public AtributosLinea getAtributosLinea(){
		return atributosLinea;
	}

	/**
	 * <i>Getter</i> que devuelve los {@code AtributosPunto} de esta {@code Apariencia}.
	 * @return Los {@code AtributosPunto} solicitados.
	 */
	public AtributosPunto getAtributosPunto(){
		return atributosPunto;
	}

	/**
	 * <i>Getter</i> que devuelve los {@code AtributosPoligono} de esta {@code Apariencia}.
	 * @return Los {@code AtributosPoligono} solicitados.
	 */
	public AtributosPoligono getAtributosPoligono(){
		return atributosPoligono;
	}

	/**
	 * <i>Getter</i> que devuelve los {@code AtributosRender} de esta {@code Apariencia}.
	 * @return Los {@code AtributosRender} solicitados.
	 */
	public AtributosRender getAtributosRender(){
		return atributosRender;
	}

	/**
	 * <i>Getter</i> que devuelve el {@code Material} de esta {@code Apariencia}.
	 * @return El {@code Material} solicitado.
	 */
	public Material getMaterial(){
		return material;
	}

	/**
	 * <i>Getter</i> que devuelve los {@code AtributosTransparencia} de esta {@code Apariencia}.
	 * @return Los {@code AtributosTransparencia} solicitados.
	 */
	public AtributosTransparencia getAtributosTransparencia(){
		return atributosTransparencia;
	}

	/**
	 * <i>Getter</i> que devuelve la {@code Textura} de esta {@code Apariencia}.
	 * <p><u>Nota:</u> Como la {@code Apariencia} está diseñada para soportar multitexturas,
	 * este método se ha implementado simplemente para facilitar el trabajo con apariencias
	 * con una única unidad de textura.
	 * Sería equivalente a {@code getUnidadTextura(0).getTextura()}.
	 * </p>
	 * @return <ul>
	 * <li>La {@code Textura} solicitada.</li>
	 * <li>{@code null} Si no se han asigando unidades de textura anteriormente.</li>
	 * </ul>
	 */
	public Textura getTextura(){
		return unidadesTextura == null ? null : unidadesTextura[0].getTextura();
	}

	/**
	 * <i>Getter</i> que devuelve los {@code AtributosTextura} de esta {@code Apariencia}.
	 * <p><u>Nota:</u> Como la {@code Apariencia} está diseñada para soportar multitexturas,
	 * este método se ha implementado simplemente para facilitar el trabajo con apariencias
	 * con una única unidad de textura.
	 * Sería equivalente a {@code getUnidadTextura(0).getAtributosTextura()}.
	 * </p>
	 * @return <ul>
	 * <li> Los {@code AtributosTextura} solicitados.</li>
	 * <li>{@code null} Si no se han asigando unidades de textura anteriormente.</li>
	 * </ul>
	 */
	public AtributosTextura getAtributosTextura(){
		return unidadesTextura == null ? null : unidadesTextura[0].getAtributosTextura();
	}

	/**
	 * <i>Getter</i> que devuelve los {@code GenCoordTextura} de esta {@code Apariencia}.
	 * <p><u>Nota:</u> Como la {@code Apariencia} está diseñada para soportar multitexturas,
	 * este método se ha implementado simplemente para facilitar el trabajo con apariencias
	 * con una única unidad de textura.
	 * Sería equivalente a {@code getUnidadTextura(0).getGenCoordTextura()}.
	 * </p> 
	 * @return <ul>
	 * <li>Los {@code GenCoordTextura} solicitados.</li>
	 * <li>{@code null} Si no se han asigando unidades de textura anteriormente.</li>
	 * </ul>
	 */
	public GenCoordTextura getGenCoordTextura(){
		return unidadesTextura == null ? null : unidadesTextura[0].getGenCoordTextura();
	}

	/**
	 * <i>Getter</i> que devuelve la longitud del vector de {@code UnidadTextura} de esta {@code Apariencia}.
	 * @return <ul>
	 * <li>La longitud del vector de {@code UnidadTextura}.</li>
	 * <li>{@code -1} Si no se han asigando las unidades de textura anteriormente.</li>
	 * </ul>
	 */
	public int getUnidadTexturaCount(){
		return unidadesTextura == null ? -1 : unidadesTextura.length;
	}

	/**
	 * <i>Getter</i> que devuelve el vector de {@code UnidadTextura} de esta {@code Apariencia}.
	 * @return El vector de {@code UnidadTextura} solicitado.
	 */
	public UnidadTextura[] getUnidadesTextura(){
		return unidadesTextura;
	}

	/**
	 * <i>Getter</i> que devuelve la {@code UnidadTextura} que está en la posición {@code index}
	 * del vector de {@code UnidadTextura} de esta {@code Apariencia}.
	 * @param index Posición en el vector.
	 * @return La {@code UnidadTextura} solicitada.
	 * @throws NullPointerException Si no se han asigando las unidades de textura anteriormente.
	 * @throws ArrayIndexOutOfBoundsException Si la posición está fuera de los límites del vector.
	 */
	public UnidadTextura getUnidadTextura(int index){
		return unidadesTextura[index];
	}
	
	/**
	 * <i>Getter</i> que devuelve el {@code Shader} de esta {@code Apariencia}.
	 * @return El {@code Shader} solicitado.
	 */
	public Shader getShader() {
		return shader;
	}

	/**
	 * <i>Setter</i> que asigna los {@code AtributosColor} a esta {@code Apariencia}.
	 * @param atributosColor Los {@code AtributosColor} asignados.
	 */
	public void setAtributosColor(AtributosColor atributosColor){
		this.atributosColor = atributosColor;
	}

	/**
	 * <i>Setter</i> que asigna los {@code AtributosLinea} a esta {@code Apariencia}.
	 * @param atributosLinea Los {@code AtributosLinea} asignados.
	 */
	public void setAtributosLinea(AtributosLinea atributosLinea){
		this.atributosLinea = atributosLinea;
	}

	/**
	 * <i>Setter</i> que asigna los {@code AtributosPunto} a esta {@code Apariencia}.
	 * @param atributosPunto Los {@code AtributosPunto} asignados.
	 */
	public void setAtributosPunto(AtributosPunto atributosPunto){
		this.atributosPunto = atributosPunto;
	}

	/**
	 * <i>Setter</i> que asigna los {@code AtributosPoligono} a esta {@code Apariencia}.
	 * @param atributosPoligono Los {@code AtributosPoligono} asignados.
	 */
	public void setAtributosPoligono(AtributosPoligono atributosPoligono){
		this.atributosPoligono = atributosPoligono;
	}

	/**
	 * <i>Setter</i> que asigna los {@code AtributosRender} a esta {@code Apariencia}.
	 * @param atributosRender Los {@code AtributosRender} asignados.
	 */
	public void setAtributosRender(AtributosRender atributosRender){
		this.atributosRender = atributosRender;
	}

	/**
	 * <i>Setter</i> que asigna el {@code Material} a esta {@code Apariencia}.
	 * @param material El {@code Material} asignado.
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * <i>Setter</i> que asigna los {@code AtributosTransparencia} a esta {@code Apariencia}.
	 * @param atributosTransparencia Los {@code AtributosTransparencia} asignados.
	 */
	public void setAtributosTransparencia(AtributosTransparencia atributosTransparencia){
		this.atributosTransparencia = atributosTransparencia;
	}

	/**
	 * <i>Setter</i> que asigna la {@code Textura} a esta {@code Apariencia}.
	 * <p><u>Nota:</u> Como la {@code Apariencia} está diseñada para soportar multitexturas,
	 * este método se ha implementado simplemente para facilitar el trabajo con apariencias
	 * con una única unidad de textura.<br/>
	 * Sería equivalente a {@code getUnidadTextura(0).setTextura(textura)}.<br/>
	 * En caso de que no se hayan asigando las unidades de textura anteriormente, este método
	 * tambien encarga de crearlas de forma transparente.
	 * </p>
	 * @param textura La {@code Textura} asignada.
	 */
	public void setTextura(Textura textura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setTextura(textura);
	}

	/**
	 * <i>Setter</i> que asigna los {@code AtributosTextura} a esta {@code Apariencia}.
	 * <p><u>Nota:</u> Como la {@code Apariencia} está diseñada para soportar multitexturas,
	 * este método se ha implementado simplemente para facilitar el trabajo con apariencias
	 * con una única unidad de textura.<br/>
	 * Sería equivalente a {@code getUnidadTextura(0).setAtributosTextura(atributosTextura)}.<br/>
	 * En caso de que no se hayan asigando las unidades de textura anteriormente, este método
	 * tambien encarga de crearlas de forma transparente.
	 * </p>
	 * @param atributosTextura Los {@code AtributosTextura} asignados.
	 */
	public void setAtributosTextura(AtributosTextura atributosTextura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setAtributosTextura(atributosTextura);
	}

	/**
	 * <i>Setter</i> que asigna el {@code GenCoordTextura} a esta {@code Apariencia}.
	 * <p><u>Nota:</u> Como la {@code Apariencia} está diseñada para soportar multitexturas,
	 * este método se ha implementado simplemente para facilitar el trabajo con apariencias
	 * con una única unidad de textura.<br/>
	 * Sería equivalente a {@code getUnidadTextura(0).setGenCoordTextura(genCoordTextura)}.<br/>
	 * En caso de que no se hayan asigando las unidades de textura anteriormente, este método
	 * tambien encarga de crearlas de forma transparente.
	 * </p>
	 * @param genCoordTextura El {@code GenCoordTextura} asignado.
	 */
	public void setGenCoordTextura(GenCoordTextura genCoordTextura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setGenCoordTextura(genCoordTextura);
	}

	/**
	 * <i>Setter</i> que asigna el vector de {@code UnidadTextura} de esta {@code Apariencia}.
	 * @param unidadesTextura El vector de {@code UnidadTextura} asignado.
	 * @throws IllegalArgumentException
	 *       Si la longitud del vector de {@code UnidadTextura} es mayor de {@link UnidadTextura#MAX}.
	 */
	public void setUnidadesTextura(UnidadTextura[] unidadesTextura){
		if(unidadesTextura.length > UnidadTextura.MAX)
			throw new IllegalArgumentException();
		this.unidadesTextura = unidadesTextura;
	}

	/**
	 * <i>Setter</i> que asigna la {@code UnidadTextura} que está en la posición {@code index}
	 * del vector de {@code UnidadTextura} de esta {@code Apariencia}.
	 * <p><u>Nota:</u> Este método no añade nuevas {@code UnidadTextura} al vector, simplemente
	 * cambia el valor anterior. Por tanto si la posición es mayor que la longitud del vector 
	 * lanzará la excepción correspondiente.
	 * </p>
	 * @param index Posición en el vector.
	 * @param unidadTextura La {@code UnidadTextura} asignada.
	 * @throws NullPointerException Si no se han asigando las unidades de textura anteriormente.
	 * @throws ArrayIndexOutOfBoundsException Si la posición está fuera de los límites del vector.
	 */
	public void setUnidadTextura(int index, UnidadTextura unidadTextura){
		this.unidadesTextura[index] = unidadTextura;
	}

	/**
	 * <i>Setter</i> que asigna el {@code Shader} a esta {@code Apariencia}.
	 * @param shader El {@code Shader} asignados.
	 */
	public void setShader(Shader shader) {
		this.shader = shader;
	}

	/**
	 * Método que se encarga de usar esta {@code Apariencia}, activando o desactivando
	 * los atributos necesarios.
	 * <p>Como OpenGL funciona como una máquina de estados, en caso de que la última
	 * {@code Apariencia} usada haya sido esta, no hace nada.<br/>
	 * Tambien se encarga de comparar los atributos esta {@code Apariencia} con los
	 * de la {@code Apariencia} anteriormente usada, para activarlos o desactivalos
	 * sólo cuando sea necesario.
	 * </p>
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public void usar(GL gl){
 		if( anterior == this )
 			return;

 		if( anterior != null ){
 	 		if( anterior.atributosTransparencia != null && atributosTransparencia == null )
 	 			AtributosTransparencia.desactivar(gl);
 			if( anterior.unidadesTextura != null ){
 				int ini = unidadesTextura == null ? 0 : unidadesTextura.length; 
 				for( int unit = ini; unit < anterior.unidadesTextura.length; unit++)
 					UnidadTextura.desactivar(gl, unit);
 			}
 			if( material == null && anterior.material != null )
 				Material.desactivar(gl);
 			if( shader == null && anterior.shader != null )
 	 			Shader.desactivar(gl);
 		}
 		
  		if( atributosColor != null )
 			atributosColor.usar(gl);
 		if( atributosLinea != null )
 			atributosLinea.usar(gl);
 		if( atributosPunto != null )
 			atributosPunto.usar(gl);
 		if( atributosPoligono != null )
 			atributosPoligono.usar(gl);
 		if( atributosRender != null )
 			atributosRender.usar(gl);
 		if( atributosTransparencia != null )
 			atributosTransparencia.activar(gl);
		if( material != null )
			material.activar(gl);
 		if( unidadesTextura != null )
 			for( int unit= 0; unit < unidadesTextura.length; unit++)
 				unidadesTextura[unit].activar(gl, unit);
 		if( shader != null )
 			shader.activar(gl);
 		
 		anterior = this;
	}
}