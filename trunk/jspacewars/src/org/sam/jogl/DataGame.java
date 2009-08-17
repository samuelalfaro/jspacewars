package org.sam.jogl;

import javax.media.opengl.GLContext;

import org.sam.jogl.fondos.Fondo;
import org.sam.util.Cache;

/**
 * Clase donde almacenar las referencias de los distintos elementos cargados.
 * De esta forma se podra usar un CLCanvas para cargar los elementos del juego, y otro distinto para mostrarlos.
 */
public class DataGame{
	
	/**
	 * Objeto que permite compartir texturas y listas de llamada entre disitintos GLAutoDrawable
	 */
	private GLContext glContext; 

	/**
	 * Fondo de la pantalla.
	 */
	private Fondo fondo;
	
	/**
	 * Caché que almacena las distintas instancias cargadas.
	 */
	private final Cache<Instancia3D> cache;

	public DataGame(){
		cache = new Cache<Instancia3D>(500);
	}
	
	/**
	 * @return the glContext
	 */
	public GLContext getGLContext() {
		return glContext;
	}

	/**
	 * @return the fondo
	 */
	public Fondo getFondo() {
		return fondo;
	}

	/**
	 * @return the cache
	 */
	public Cache<Instancia3D> getCache() {
		return cache;
	}

	/**
	 * @param glContext the glContext to set
	 */
	public void setGLContext(GLContext glContext) {
		this.glContext = glContext;
	}

	/**
	 * @param fondo the fondo to set
	 */
	public void setFondo(Fondo fondo) {
		this.fondo = fondo;
	}
}