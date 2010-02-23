/* 
 * DataGame.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars;

import javax.media.opengl.GLContext;

import org.sam.elementos.Cache;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.fondos.Fondo;
import org.sam.jspacewars.cliente.MarcoDeIndicadores;

/**
 * Clase que encapsula las referencias de los distintos elementos que necesitan
 * ser cargados para comenzar el juego.<br>
 * De esta forma se podrá usar un {@link javax.media.opengl.GLAutoDrawable
 * GLAutoDrawable} para cargar los elementos, y otro distinto para mostrarlos.
 */
public class DataGame {

	/**
	 * Objeto que permite compartir texturas y listas de llamada entre
	 * disitintos GLAutoDrawable
	 */
	private GLContext glContext;
	
	private MarcoDeIndicadores gui;

	/**
	 * Fondo de la pantalla.
	 */
	private Fondo fondo;
	
	/**
	 * Caché que almacena las distintas instancias cargadas.
	 */
	private final Cache<Instancia3D> cache;

	public DataGame() {
		cache = new Cache<Instancia3D>(500);
	}

	/**
	 * @return the glContext
	 */
	public GLContext getGLContext() {
		return glContext;
	}

	public MarcoDeIndicadores getGui() {
		return gui;
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
	 * @param glContext
	 *            the glContext to set
	 */
	public void setGLContext(GLContext glContext) {
		this.glContext = glContext;
	}
	
	public void setGui(MarcoDeIndicadores gui) {
		this.gui = gui;
	}

	/**
	 * @param fondo
	 *            the fondo to set
	 */
	public void setFondo(Fondo fondo) {
		this.fondo = fondo;
	}
}