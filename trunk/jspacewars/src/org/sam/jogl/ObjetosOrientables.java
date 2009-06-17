/**
 * 
 */
package org.sam.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Matrix4f;

/**
 * @author samuel
 *
 */
public final class ObjetosOrientables {
	
	private ObjetosOrientables(){
	}
	
	private static final float		model_view_array[] = new float[16];
	private static final Matrix4f	model_view_matrix  = new Matrix4f();
	private static final float		projection_array[] = new float[16];
	private static final Matrix4f	projection_matrix  = new Matrix4f();
	
	public static final void loadModelViewMatrix(){
		GLU.getCurrentGL().glGetFloatv(GL.GL_MODELVIEW_MATRIX, model_view_array, 0);
		model_view_matrix.set(model_view_array);
		model_view_matrix.transpose();
	}
	
	public static final Matrix4f getModelViewMatrix(){
		return model_view_matrix;
	}

	public static final void loadProjectionMatrix(){
		GLU.getCurrentGL().glGetFloatv(GL.GL_PROJECTION_MATRIX, projection_array, 0);
		projection_matrix.set(projection_array);
	}
	
	public static final Matrix4f getProjectionMatrix(){
		return projection_matrix;
	}
}
