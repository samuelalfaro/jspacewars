package org.sam.jogl;

import javax.media.opengl.GL;

public class Apariencia {

	private static Apariencia anterior;
	
	private AtributosColor atributosColor;
	private AtributosLinea atributosLinea;
	private AtributosPunto atributosPunto;
	private AtributosPoligono atributosPoligono;
	private AtributosRender atributosRender;
	private AtributosTransparencia atributosTransparencia;
	private Material material;
	private UnidadTextura[] unidadesTextura;

	public Apariencia(){
		material = Material.DEFAULT;
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

	public AtributosTextura getAtributosTextura(){
		return unidadesTextura[0].getAtributosTextura();
	}

	public AtributosTransparencia getAtributosTransparencia(){
		return atributosTransparencia;
	}

	public GenCoordTextura getGenCoordTextura(){
		return unidadesTextura[0].getGenCoordTextura();
	}

	public Material getMaterial(){
		return material;
	}

	public Textura getTextura(){
		return unidadesTextura[0].getTextura();
	}

	public int getUnidadTexturaCount(){
		return unidadesTextura != null ? unidadesTextura.length : -1;
	}

	public UnidadTextura[] getUnidadesTextura(){
		return unidadesTextura;
	}

	public UnidadTextura getUnidadTextura(int index){
		return unidadesTextura[index];
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

	public void setAtributosTextura(AtributosTextura atributosTextura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setAtributosTextura(atributosTextura);
	}

	public void setAtributosTransparencia(AtributosTransparencia atributosTransparencia){
		this.atributosTransparencia = atributosTransparencia;
	}

	public void setGenCoordTextura(GenCoordTextura genCoordTextura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setGenCoordTextura(genCoordTextura);
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void setTextura(Textura textura){
		if(unidadesTextura == null)
			unidadesTextura = new UnidadTextura[]{new UnidadTextura()};
		unidadesTextura[0].setTextura(textura);
	}

	public void setUnidadTextura(int index, UnidadTextura unidadTextura){
		this.unidadesTextura[index] = unidadTextura;
	}

	public void setUnidadesTextura(UnidadTextura[] unidadesTextura){
		if(unidadesTextura.length > UnidadTextura.MAX)
			throw new IllegalArgumentException();
		this.unidadesTextura = unidadesTextura;
	}

 	public void activar(GL gl){
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
			material.usar(gl);
 		if( unidadesTextura != null )
 			for( int unit= 0; unit < unidadesTextura.length; unit++)
 				unidadesTextura[unit].activar(gl, unit);
 		
 		anterior = this;
	}

	public void _desactivar(GL gl){
	}
}