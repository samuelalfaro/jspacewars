package org.sam.jogl;

import javax.media.opengl.GL;
import javax.vecmath.*;

public class NodoTransformador implements Nodo {
	
	private transient Nodo parent;
	protected final Matrix4f transformMatrix;
	private Nodo child;
	
	public NodoTransformador(Nodo child) {
		this(null, child);
	}
	
	public NodoTransformador(Matrix4f transformMatrix, Nodo child){
		this.transformMatrix = new Matrix4f();
		if(transformMatrix == null)
			this.transformMatrix.setIdentity();
		else
			this.transformMatrix.set(transformMatrix);
		this.child = child;
		this.child.setParent(this);
	}
	
	protected NodoTransformador(NodoTransformador me){
		this.transformMatrix = new Matrix4f(me.transformMatrix);
		this.child = me.child.clone();
		this.child.setParent(this);
	}

	public Matrix4f getTransform() {
		return transformMatrix;
	}

	public void setTransform(Matrix4f transform) {
		transformMatrix.set(transform);
	}
	
	private transient final float[]  mt_array = new float[16];
	
	public void draw(GL gl) {
		gl.glPushMatrix();

		mt_array[ 0] = transformMatrix.m00; mt_array[ 1] = transformMatrix.m10; mt_array[ 2] = transformMatrix.m20; mt_array[ 3] = transformMatrix.m30;
		mt_array[ 4] = transformMatrix.m01; mt_array[ 5] = transformMatrix.m11; mt_array[ 6] = transformMatrix.m21; mt_array[ 7] = transformMatrix.m31;
		mt_array[ 8] = transformMatrix.m02; mt_array[ 9] = transformMatrix.m12; mt_array[10] = transformMatrix.m22; mt_array[11] = transformMatrix.m32;
		mt_array[12] = transformMatrix.m03; mt_array[13] = transformMatrix.m13; mt_array[14] = transformMatrix.m23; mt_array[15] = transformMatrix.m33;
		
		gl.glMultMatrixf(mt_array, 0);
		/*
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
		//*/
		child.draw(gl);
		gl.glPopMatrix();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#setParent(org.sam.jogl.Nodo)
	 */
	public void setParent(Nodo parent) {
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#getParent()
	 */
	public Nodo getParent() {
		return parent;
	}
	
	private transient Nodo[] nodos; 
	public Nodo[] getChilds(){
		if(nodos == null){
			nodos = new Nodo[]{child};
		}
		return nodos;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public NodoTransformador clone(){
		return new NodoTransformador(this);
	}
}
