package org.sam.jogl;

import javax.media.opengl.GL;

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
	
	public AtributosColor getAtributosColor(){
		return atributosColor;
	}

	public AtributosLinea getAtributosLinea(){
		return atributosLinea;
	}

	public AtributosPunto getAtributosPunto(){
		return atributosPunto;
	}

	public AtributosPoligono getAtributosPoligono(){
		return atributosPoligono;
	}

	public AtributosRender getAtributosRender(){
		return atributosRender;
	}

	public Material getMaterial(){
		return material;
	}

	public AtributosTransparencia getAtributosTransparencia(){
		return atributosTransparencia;
	}

	public Textura getTextura(){
		return unidadesTextura == null ? null : unidadesTextura[0].getTextura();
	}

	public AtributosTextura getAtributosTextura(){
		return unidadesTextura == null ? null : unidadesTextura[0].getAtributosTextura();
	}

	public GenCoordTextura getGenCoordTextura(){
		return unidadesTextura == null ? null : unidadesTextura[0].getGenCoordTextura();
	}

	public int getUnidadTexturaCount(){
		return unidadesTextura == null ? -1 : unidadesTextura.length;
	}

	public UnidadTextura[] getUnidadesTextura(){
		return unidadesTextura;
	}

	public UnidadTextura getUnidadTextura(int index){
		return unidadesTextura[index];
	}
	
	public Shader getShader() {
		return shader;
	}

	public void setAtributosColor(AtributosColor atributosColor){
		this.atributosColor = atributosColor;
	}

	public void setAtributosLinea(AtributosLinea atributosLinea){
		this.atributosLinea = atributosLinea;
	}

	public void setAtributosPunto(AtributosPunto atributosPunto){
		this.atributosPunto = atributosPunto;
	}

	public void setAtributosPoligono(AtributosPoligono atributosPoligono){
		this.atributosPoligono = atributosPoligono;
	}

	public void setAtributosRender(AtributosRender atributosRender){
		this.atributosRender = atributosRender;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void setAtributosTransparencia(AtributosTransparencia atributosTransparencia){
		this.atributosTransparencia = atributosTransparencia;
	}

	public void setTextura(Textura textura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setTextura(textura);
	}

	public void setAtributosTextura(AtributosTextura atributosTextura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setAtributosTextura(atributosTextura);
	}

	public void setGenCoordTextura(GenCoordTextura genCoordTextura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setGenCoordTextura(genCoordTextura);
	}

	public void setUnidadesTextura(UnidadTextura[] unidadesTextura){
		if(unidadesTextura.length > UnidadTextura.MAX)
			throw new IllegalArgumentException();
		this.unidadesTextura = unidadesTextura;
	}

	public void setUnidadTextura(int index, UnidadTextura unidadTextura){
		this.unidadesTextura[index] = unidadTextura;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

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