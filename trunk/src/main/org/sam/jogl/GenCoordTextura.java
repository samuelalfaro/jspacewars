/* 
 * GenCoordTextura.java
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

import javax.media.opengl.GL2;

/**
 * Clase que contiene los atributos que indican como se generarán automáticamente
 * las coordenadas de textura.
 */
public class GenCoordTextura {
	
	/**
	 * Enumeración que contiene los distintos modos de generación
	 * automática, de coordenadas de textura.
	 */
	@SuppressWarnings( "static-access" )
	public enum Mode{
		/**
		 * Encapsula el valor GL_OBJECT_LINEAR.
		 */
		OBJECT_LINEAR	  (GL2.GL_OBJECT_LINEAR),
		/**
		 * Encapsula el valor GL_EYE_LINEAR.
		 */
		EYE_LINEAR    	  (GL2.GL_EYE_LINEAR),
		/**
		 * Encapsula el valor GL_SPHERE_MAP.
		 */
		SPHERE_MAP		  (GL2.GL_SPHERE_MAP),
		/**
		 * Encapsula el valor GL_NORMAL_MAP.
		 */
		NORMAL_MAP    	  (GL2.GL_NORMAL_MAP),
		/**
		 * Encapsula el valor GL_REFLECTION_MAP.
		 */
		REFLECTION_MAP	  (GL2.GL_REFLECTION_MAP);

		/** Entero que almacena el valor encapsulado.*/
		final int value;

		private Mode(int value){
			this.value = value;
		}
	}

	/**
	 * Enumeración que contiene los distintos valoresque indican,
	 * las dimensiones que tendrán las coordenadas de textura generadas.
	 */
	public enum Coordinates{
		/**
		 * Valor que indica que las coodenadas de textura tendrán
		 * dos dimensiones.
		 */
		TEXTURE_COORDINATE_2,
		/**
		 * Valor que indica que las coodenadas de textura tendrán
		 * tres dimensiones.
		 */
    	TEXTURE_COORDINATE_3,
		/**
		 * Valor que indica que las coodenadas de textura tendrán
		 * cuatro dimensiones.
		 */
    	TEXTURE_COORDINATE_4;
	}
	
	private Mode mode;
    private Coordinates coordinates;
    
	/**
	 * Constructor que crea el {@code GenCoordTextura} con los parámetros que recibe.
     * @param mode  {@code Mode} de este {@code GenCoordTextura}.
     * @param coordinates {@code Coordinates} de este {@code GenCoordTextura}.
     */
    public GenCoordTextura(Mode mode, Coordinates coordinates){
    	this.mode = mode;
    	this.coordinates = coordinates;
    }
    
	/**
	 * Método que activa la generación automática de las coordenadas de textura
	 * con los valores de este {@code GenCoordTextura}.<br/>
	 * Este método compara el {@code GenCoordTextura} anteriormente usado para,
	 * activar o desactivar los atributos correspondientes.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
     * @param anterior {@code GenCoordTextura} anteriormente usado.
     */
    @SuppressWarnings( "static-access" )
	public void activar( GL2 gl, GenCoordTextura anterior ){
		switch( coordinates ){
		case TEXTURE_COORDINATE_2:
			gl.glTexGeni( GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			gl.glTexGeni( GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			if( anterior == null ){
				gl.glEnable( GL2.GL_TEXTURE_GEN_S );
				gl.glEnable( GL2.GL_TEXTURE_GEN_T );
			}else{
				switch( anterior.coordinates ){
				case TEXTURE_COORDINATE_2:
					break;
				case TEXTURE_COORDINATE_3:
					gl.glDisable( GL2.GL_TEXTURE_GEN_R );
					break;
				case TEXTURE_COORDINATE_4:
					gl.glDisable( GL2.GL_TEXTURE_GEN_Q );
					gl.glDisable( GL2.GL_TEXTURE_GEN_R );
					break;
				}
			}
			break;
		case TEXTURE_COORDINATE_3:
			gl.glTexGeni( GL2.GL_R, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			gl.glTexGeni( GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			gl.glTexGeni( GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			if( anterior == null ){
				gl.glEnable( GL2.GL_TEXTURE_GEN_R );
				gl.glEnable( GL2.GL_TEXTURE_GEN_S );
				gl.glEnable( GL2.GL_TEXTURE_GEN_T );
			}else{
				switch( anterior.coordinates ){
				case TEXTURE_COORDINATE_2:
					gl.glEnable( GL2.GL_TEXTURE_GEN_R );
					break;
				case TEXTURE_COORDINATE_3:
					break;
				case TEXTURE_COORDINATE_4:
					gl.glDisable( GL2.GL_TEXTURE_GEN_Q );
					break;
				}
			}
			break;
		case TEXTURE_COORDINATE_4:
			gl.glTexGeni( GL2.GL_Q, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			gl.glTexGeni( GL2.GL_R, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			gl.glTexGeni( GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			gl.glTexGeni( GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, mode.value );
			if( anterior == null ){
				gl.glEnable( GL2.GL_TEXTURE_GEN_Q );
				gl.glEnable( GL2.GL_TEXTURE_GEN_R );
				gl.glEnable( GL2.GL_TEXTURE_GEN_S );
				gl.glEnable( GL2.GL_TEXTURE_GEN_T );
			}else{
				switch( anterior.coordinates ){
				case TEXTURE_COORDINATE_2:
					gl.glEnable( GL2.GL_TEXTURE_GEN_Q );
					gl.glEnable( GL2.GL_TEXTURE_GEN_R );
					break;
				case TEXTURE_COORDINATE_3:
					gl.glEnable( GL2.GL_TEXTURE_GEN_Q );
					break;
				case TEXTURE_COORDINATE_4:
					break;
				}
			}
		}
	}
	
	/**
	 * Método que desactiva la generación automática de las coordenadas de textura.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public void desactivar( GL2 gl ){
		switch( coordinates ){
		case TEXTURE_COORDINATE_2:
			gl.glDisable( GL2.GL_TEXTURE_GEN_S );
			gl.glDisable( GL2.GL_TEXTURE_GEN_T );
		case TEXTURE_COORDINATE_3:
			gl.glDisable( GL2.GL_TEXTURE_GEN_R );
			gl.glDisable( GL2.GL_TEXTURE_GEN_S );
			gl.glDisable( GL2.GL_TEXTURE_GEN_T );
		case TEXTURE_COORDINATE_4:
			gl.glDisable( GL2.GL_TEXTURE_GEN_Q );
			gl.glDisable( GL2.GL_TEXTURE_GEN_R );
			gl.glDisable( GL2.GL_TEXTURE_GEN_S );
			gl.glDisable( GL2.GL_TEXTURE_GEN_T );
		}
	}
}
