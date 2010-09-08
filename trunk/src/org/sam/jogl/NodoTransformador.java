package org.sam.jogl;

import javax.media.opengl.GL;
import javax.vecmath.*;

/**
 * Clase que representa un {@code Nodo} que puede ser
 * transformado junto con sus descendientes.
 */
public class NodoTransformador implements Nodo {
	
	private transient Nodo parent;
	/**
	 * {@code Matrix4f} que contiene la transformación aplicada
	 * a los elementos de este {@code NodoTransformador}.
	 */
	protected final Matrix4f transformMatrix;
	private Nodo child;
	
	/**
	 * Constructor que crea un {@code NodoTransformador} que contiene
	 * un {@code Nodo} descendiente, al que se le aplicará la 
	 * transformación correspondiente.
	 * @param child {@code Nodo} descendiente.
	 */
	public NodoTransformador(Nodo child) {
		this(null, child);
	}
	
	/**
	 * Constructor que crea un {@code NodoTransformador}, asignando
	 * la {@code Matrix4f} con la transformación, que se le aplicará
	 * al {@code Nodo} descendiente.
	 * @param transformMatrix {@code Matrix4f} con la transformación asignada.
	 * @param child {@code Nodo} descendiente.
	 */
	public NodoTransformador(Matrix4f transformMatrix, Nodo child){
		this.transformMatrix = new Matrix4f();
		if(transformMatrix == null)
			this.transformMatrix.setIdentity();
		else
			this.transformMatrix.set(transformMatrix);
		this.child = child;
		this.child.setParent(this);
	}
	
	/**
	 * Constructor que crea un {@code NodoTransformador}, a partir de 
	 * los datos de otro {@code NodoTransformador} que se emplea como
	 * prototipo.
	 * 
	 * @param me {@code NodoTransformador} empleado como prototipo.
	 */
	protected NodoTransformador(NodoTransformador me){
		this.transformMatrix = new Matrix4f(me.transformMatrix);
		this.child = me.child.clone();
		this.child.setParent(this);
	}

	/**
	 * <i>Getter</i> que devuelve la {@code Matrix4f} que contiene la transformación
	 * aplicada a los elementos de este {@code NodoTransformador}.
	 * @return La {@code Matrix4f} solicitada.
	 */
	public Matrix4f getTransform() {
		return transformMatrix;
	}

	/**
	 * @param transform La {@code Matrix4f} asignada.
	 */
	public void setTransform(Matrix4f transform) {
		transformMatrix.set(transform);
	}
	
	private transient final float[]  mt_array = new float[16];
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(GL gl) {
		gl.glPushMatrix();

		mt_array[ 0] = transformMatrix.m00; mt_array[ 1] = transformMatrix.m10; mt_array[ 2] = transformMatrix.m20; mt_array[ 3] = transformMatrix.m30;
		mt_array[ 4] = transformMatrix.m01; mt_array[ 5] = transformMatrix.m11; mt_array[ 6] = transformMatrix.m21; mt_array[ 7] = transformMatrix.m31;
		mt_array[ 8] = transformMatrix.m02; mt_array[ 9] = transformMatrix.m12; mt_array[10] = transformMatrix.m22; mt_array[11] = transformMatrix.m32;
		mt_array[12] = transformMatrix.m03; mt_array[13] = transformMatrix.m13; mt_array[14] = transformMatrix.m23; mt_array[15] = transformMatrix.m33;
		
		gl.glMultMatrixf(mt_array, 0);
		//drawBox(gl);
		child.draw(gl);
		gl.glPopMatrix();
	}
	
	@SuppressWarnings("unused")
	private static void drawBox(GL gl){
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_TEXTURE_2D);

		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(-0.5f,-0.5f, 0.5f);
		gl.glVertex3f( 0.5f,-0.5f, 0.5f);
		gl.glVertex3f( 0.5f, 0.5f, 0.5f);
		gl.glVertex3f(-0.5f, 0.5f, 0.5f);
		gl.glVertex3f(-0.5f,-0.5f, 0.5f);
		gl.glVertex3f(-0.5f,-0.5f,-0.5f);
		gl.glVertex3f( 0.5f,-0.5f,-0.5f);
		gl.glVertex3f( 0.5f, 0.5f,-0.5f);
		gl.glVertex3f(-0.5f, 0.5f,-0.5f);
		gl.glVertex3f(-0.5f,-0.5f,-0.5f);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(-0.5f, 0.5f, 0.5f);
		gl.glVertex3f(-0.5f, 0.5f,-0.5f);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f( 0.5f, 0.5f, 0.5f);
		gl.glVertex3f( 0.5f, 0.5f,-0.5f);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f( 0.5f,-0.5f, 0.5f);
		gl.glVertex3f( 0.5f,-0.5f,-0.5f);
		gl.glEnd();

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParent(Nodo parent) {
		this.parent = parent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Nodo getParent() {
		return parent;
	}
	
	private transient Nodo[] nodos;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Nodo[] getChilds(){
		if(nodos == null){
			nodos = new Nodo[]{child};
		}
		return nodos;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodoTransformador clone(){
		return new NodoTransformador(this);
	}
}
