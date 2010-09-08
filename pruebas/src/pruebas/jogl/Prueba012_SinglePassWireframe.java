/* 
 * Prueba012_SinglePassWireframe.java
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
package pruebas.jogl;

import java.awt.Color;
import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Color4f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple4f;

import org.sam.jogl.Objeto3D;
import org.sam.jogl.Shader;

import pruebas.jogl.generators.*;

import com.sun.opengl.util.Animator;

public class Prueba012_SinglePassWireframe{
	
	private static class Renderer implements GLEventListener{
		
		private GLU glu;
		private OrbitBehavior orbitBehavior;

		private Objeto3D forma;
		private final Tuple4f viewport = new Point4f();
		
		public void init(GLAutoDrawable drawable){
			GL gl = drawable.getGL();
			glu = new GLU();
			orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos(0.0f, 0.0f, 22.0f);
			orbitBehavior.setTargetPos(0.0f, 0.0f, 0.0f);
			orbitBehavior.addMouseListeners((GLCanvas)drawable);

			gl.glClearColor(0.0f,0.0f,0.0f,0.0f);

			//*
			forma = HelixGenerator.generate( gl, HelixGenerator.WIREFRAME, 1.2f, 0.0f, 12, 1.25f, 0.05f, 36, 1.2f*2.8f, 9 );
			//forma = SphereGenerator.generate( gl, HelixGenerator.WIREFRAME, 1.2f, 12, 36 );
			/*/
			Matrix4d mtd = new Matrix4d();
			mtd.rotY(Math.PI/2);
			mtd.setScale(18.0);
			try {
				forma = ObjLoader.load(
						"resources/obj3d/nave06/forma.obj",
						ObjLoader.RESIZE|ObjLoader.WIREFRAME,
						mtd
				);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			//*/
			
        	Shader shader = new Shader(
        			gl,
        			"shaders/wireframe.vert",
					"shaders/wireframe.frag"
        	);
        	shader.addUniform(gl, "viewport",     viewport );
        	shader.addUniform(gl, "line_width",   0.25f );
        	shader.addUniform(gl, "line_fadeOut", 2.0f );
        	shader.addUniform(gl, "line_color", new Color4f( 0.0f, 0.0f, 0.0f, 1.0f) );
   	
        	forma.getApariencia().setShader(shader);
        	
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LESS);

			gl.glEnable(GL.GL_LIGHT0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, 	new float[]{ 0.0f, 0.0f, 1.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,	new float[]{ 1.0f, 0.0f, 1.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,	new float[]{ 1.0f, 1.0f, 1.0f, 1.0f }, 0);
			
			//gl.glEnable(GL.GL_CULL_FACE);
			gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT,GL.GL_NICEST);
		}

		public void display(GLAutoDrawable drawable){

			GL gl = drawable.getGL();

			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
			
			gl.glMatrixMode( GL.GL_MODELVIEW );
			gl.glLoadIdentity();
			orbitBehavior.setLookAt(glu);
			
			forma.draw(gl);
			
			gl.glFlush();
		}
		
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
			GL gl = drawable.getGL();
			gl.glViewport( 0, 0, w, h );
			viewport.set( 0, 0, w, h );
			forma.getApariencia().getShader().setUniform( "viewport", viewport );
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.01;
			double far  = 100.0;
			double a1 = 45.0;			// angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);

			// formato panoramico Horizontal/Vertical centrado
			double ratio = (double) w / h;
			if( ratio < 1 )
				gl.glFrustum(-d, d, -d / ratio, d / ratio, near, far);
			else
				gl.glFrustum(-d * ratio, d * ratio, -d, d, near, far);

			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
				boolean deviceChanged){
		}
	}
	
	public static void main(String[] args){
		
		JFrame frame = new JFrame("Prueba: Single pass wireframe");
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().setPreferredSize(new Dimension(640, 480));
		frame.pack();
		frame.setLocationRelativeTo(null); // center
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GLCanvas canvas = new GLCanvas(new GLCapabilities());
		canvas.addGLEventListener(new Renderer());

		frame.getContentPane().add(canvas);
		
		Animator animator = new Animator();
		animator.add(canvas);
		
		frame.setVisible(true);

		animator.add(canvas);
		canvas.requestFocusInWindow();
		animator.start();
	}
}
