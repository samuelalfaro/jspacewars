/* 
 * MatrixSingleton.java
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
import javax.media.opengl.glu.GLU;
import javax.vecmath.Matrix4f;

/**
 * <i>Singleton</i> que permite almacenar y acceder las matrices <i>MODELVIEW</i> y <i>PROJECTION</i>,
 * que están actualmente en uso. Evitando, de este modo, las llamadas constantes al contexto gráfico.
 */
public final class MatrixSingleton {
	
	private MatrixSingleton(){
	}
	
	private static final float		model_view_array[] = new float[16];
	private static final Matrix4f	model_view_matrix  = new Matrix4f();
	private static final float		projection_array[] = new float[16];
	private static final Matrix4f	projection_matrix  = new Matrix4f();
	
	/**
	 * Método que almacena la matriz <i>MODELVIEW</i> que están actualmente en uso.
	 */
	public static final void loadModelViewMatrix(){
		GLU.getCurrentGL().glGetFloatv(GL.GL_MODELVIEW_MATRIX, model_view_array, 0);
		model_view_matrix.set(model_view_array);
		model_view_matrix.transpose();
	}
	
	/**
	 * <i>Getter</i> que devuelve la matriz <i>MODELVIEW</i> que está actualmente en uso.
	 * @return La matriz <i>MODELVIEW</i> solicitada.
	 */
	public static final Matrix4f getModelViewMatrix(){
		return model_view_matrix;
	}

	/**
	 * Método que almacena la matriz <i>PROJECTION</i> que están actualmente en uso.
	 */
	public static final void loadProjectionMatrix(){
		GLU.getCurrentGL().glGetFloatv(GL.GL_PROJECTION_MATRIX, projection_array, 0);
		projection_matrix.set(projection_array);
	}
	
	/**
	 * <i>Getter</i> que devuelve la matriz <i>PROJECTION</i> que está actualmente en uso.
	 * @return La matriz <i>PROJECTION</i> solicitada.
	 */
	public static final Matrix4f getProjectionMatrix(){
		return projection_matrix;
	}
}
