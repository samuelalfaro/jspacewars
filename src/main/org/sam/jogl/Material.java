/* 
 * Material.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase que conteniene los atributos relaccionados la iluminación de elementos,
 * para poder gestinarlos a través de la {@code Apariencia} que los contiene.<br/>
 */
public class Material {
	
	private transient static Material anterior;
	
	private final float ambient[];
	private final float diffuse[];
	private final float emission[];
	private final float shininess[];
	private final float specular[];
	
	/**
	 * Instancia de una clase anónima {@code Material}, con sus
	 * valores por defecto, que puede ser compartida en múltiples
	 * {@code Apariencia}.<br/>
	 * Esta clase anónima sobreescribe todos los <i>setters</i> impidiendo
	 * que sus valores puedan ser modificados.
	 */
	public static final Material DEFAULT = new Material() {
		
		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setAmbient(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}
		
		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setDiffuse(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}
		
		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setEmission(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}
		
		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setSpecular(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}
		
		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setShininess(float shininess) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}
	};
	
	/**
	 * Constructor que crea un {@code Material} con su valores por defecto.
	 */
	public Material(){
		ambient   = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
		diffuse   = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
		emission  = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
		shininess = new float[]{64.0f};
		specular  = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	}
	
	private static float[] rgbaToArray(float r, float g, float b, float a, float[] dst){
		if ((r < 0.0f  || r > 1.0f) ||
			(g < 0.0f  || g > 1.0f) ||
			(b < 0.0f  || b > 1.0f) ||
			(a < 0.0f  || a > 1.0f))
			throw new IllegalArgumentException();
		if(dst  == null)
			dst = new float[]{r,g,b,a};
		else{
			dst[0] = r;
			dst[1] = g;
			dst[2] = b;
			dst[3] = a;
		}
		return dst;
	}
	
	/**
	 * <i>Setter</i> que asigna el color Ambiente a este {@code Material}.<br/>
	 * @param r Componente rojo del color asignado.
	 * @param g Componente verde del color asignado.
	 * @param b Componente azul del color asignado.
	 * @param a Componente alpha del color asignado.
	 */
	public void setAmbient(float r, float g, float b, float a) {
		rgbaToArray(r, g, b, a, this.ambient);
	}

	/**
	 * <i>Setter</i> que asigna el color Difuso a este {@code Material}.<br/>
	 * @param r Componente rojo del color asignado.
	 * @param g Componente verde del color asignado.
	 * @param b Componente azul del color asignado.
	 * @param a Componente alpha del color asignado.
	 */
	public void setDiffuse(float r, float g, float b, float a) {
		rgbaToArray(r, g, b, a, this.diffuse);
	}

	/**
	 * <i>Setter</i> que asigna el color Emisivo a este {@code Material}.<br/>
	 * @param r Componente rojo del color asignado.
	 * @param g Componente verde del color asignado.
	 * @param b Componente azul del color asignado.
	 * @param a Componente alpha del color asignado.
	 */
	public void setEmission(float r, float g, float b, float a) {
		rgbaToArray(r, g, b, a, this.emission);
	}

	/**
	 * <i>Setter</i> que asigna el valor de {@code shininess} a este {@code Material}.<br/>
	 * @param shininess El valor de {@code shininess} asignado.
	 */
	public void setShininess(float shininess) {
		if(shininess < 0.0f || shininess > 128.0f)
			throw new IllegalArgumentException(); 
		this.shininess[0] = shininess;
	}
	
	/**
	 * <i>Setter</i> que asigna el color Especualar a este {@code Material}.<br/>
	 * @param r Componente rojo del color asignado.
	 * @param g Componente verde del color asignado.
	 * @param b Componente azul del color asignado.
	 * @param a Componente alpha del color asignado.
	 */
	public void setSpecular(float r, float g, float b, float a) {
		rgbaToArray(r, g, b, a, this.specular);
	}
	
	/**
	 * Método que activa la iluminación de elementos con los valores de
	 * este {@code Material}.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public void activar(GL gl){
		if( anterior == this )
			return;
		if( anterior == null )
			gl.glEnable(GL.GL_LIGHTING);

		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambient, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, emission, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, shininess, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specular, 0);

		anterior = this;
	}
	
	/**
	 * Método estático que desactiva la iluminación de elementos.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public static  void desactivar(GL gl){
		if( anterior == null )
			return;
		gl.glDisable(GL.GL_LIGHTING);
		anterior = null;
	}
}
