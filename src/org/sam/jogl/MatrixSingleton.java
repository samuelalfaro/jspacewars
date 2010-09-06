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
