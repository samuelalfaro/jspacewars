package org.sam.red.cliente;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.vecmath.*;

import org.sam.jogl.*;
import org.sam.red.Recibible;
import org.sam.util.Prototipo;

public class Instancia3D extends Objeto3D implements Prototipo<Instancia3D>, Recibible{

	protected final int tipo;
	protected int id;
	protected float posX, posY;
	
	Instancia3D(int tipo, Forma3D forma3D, Apariencia apariencia){
		super(forma3D, apariencia);
		this.tipo = tipo;
	}
	
	Instancia3D(int tipo, Objeto3D obj3D){
		this(tipo, obj3D.getForma3D(), obj3D.getApariencia());
	}
	
	Instancia3D(Instancia3D prototipo){
		this(prototipo.tipo, prototipo.getForma3D(), prototipo.getApariencia());
	}
	
	public int hashCode(){
		return tipo;
	}
	
	public final void setId(int id){
		this.id = id;
	}
	
	public final void setPosicion(float posX, float posY){
		this.posX = posX;
		this.posY = posY;
	}
	
	public final float getX(){
		return this.posX;
	}
	
	public final float getY(){
		return this.posY;
	}
	
	public void recibir(ByteBuffer buff){
		this.posX = buff.getFloat();
		this.posY = buff.getFloat();
	}
	
	public void draw(GL gl) {
		gl.glPushMatrix();
//		gl.glTranslatef(posX, posY, 0.0f);
		Matrix4f mt = new Matrix4f();
		mt.setIdentity();
//		mt.rotX((float)Math.PI/2);
		mt.setTranslation(new Vector3f(posX, posY, 0.0f));
		float mt_array[] = new float[]{
			mt.m00, mt.m10, mt.m20, mt.m30,
			mt.m01, mt.m11, mt.m21, mt.m31,
			mt.m02, mt.m12, mt.m22, mt.m32,
			mt.m03, mt.m13, mt.m23, mt.m33
		};
		gl.glMultMatrixf(mt_array, 0);
		/*
		gl.glDisable(GL.GL_LIGHT0);
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
		super.draw(gl);
		gl.glPopMatrix();
	}
	
	public Instancia3D clone(){
		return new Instancia3D(this);
	}
}
