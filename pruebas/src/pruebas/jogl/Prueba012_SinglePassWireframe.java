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
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Color4f;
import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;

import org.sam.jogl.Apariencia;
import org.sam.jogl.Generator;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.OglList;
import org.sam.jogl.Shader;

import pruebas.jogl.generators.HelixGenerator;

import com.jogamp.opengl.util.Animator;

public class Prueba012_SinglePassWireframe{

	static class Renderer implements GLEventListener{

		private GLU glu;
		private OrbitBehavior orbitBehavior;

		private final Apariencia aparienciaNula = new Apariencia();
		private Objeto3D forma;
		private final Tuple2f viewport = new Point2f();
		
		@SuppressWarnings( "unused" )
		private static Objeto3D generateQuad( GL2 gl, float lado ){
			OglList oglList = new OglList( gl );
			gl.glBegin( GL2.GL_QUADS );
			
			float x0 = -lado/2;
			float y0 = -lado/2;
			float x1 =  lado/2;
			float y1 =  lado/2;
			
	   		gl.glNormal3f(           0,  0,  1);
			gl.glVertexAttrib3f( 1, x0, y0,  0);
			gl.glVertexAttrib3f( 2, x1, y0,  0);
			gl.glVertexAttrib3f( 3, x1, y1,  0);
			gl.glVertexAttrib3f( 4, x0, y1,  0);
			gl.glVertex3f(          x0, y0,  0);
			
	   		gl.glNormal3f(           0,  0,  1);
			gl.glVertexAttrib3f( 1, x0, y0,  0);
			gl.glVertexAttrib3f( 2, x1, y0,  0);
			gl.glVertexAttrib3f( 3, x1, y1,  0);
			gl.glVertexAttrib3f( 4, x0, y1,  0);
			gl.glVertex3f(          x1, y0,  0);
			
	   		gl.glNormal3f(           0,  0,  1);
			gl.glVertexAttrib3f( 1, x0, y0,  0);
			gl.glVertexAttrib3f( 2, x1, y0,  0);
			gl.glVertexAttrib3f( 3, x1, y1,  0);
			gl.glVertexAttrib3f( 4, x0, y1,  0);
			gl.glVertex3f(          x1, y1,  0);
			
	   		gl.glNormal3f(           0,  0,  1);
			gl.glVertexAttrib3f( 1, x0, y0,  0);
			gl.glVertexAttrib3f( 2, x1, y0,  0);
			gl.glVertexAttrib3f( 3, x1, y1,  0);
			gl.glVertexAttrib3f( 4, x0, y1,  0);
			gl.glVertex3f(          x0, y1,  0);
			
			gl.glEnd();
			OglList.endList( gl );
			
			return new Objeto3D( oglList, new Apariencia() );
		}
		
		Renderer(){
		}

		public void init( GLAutoDrawable drawable ){
			GL2 gl = drawable.getGL().getGL2();
			glu = new GLU();
			orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos( 0.0f, 0.0f, 22.0f );
			orbitBehavior.setTargetPos( 0.0f, 0.0f, 0.0f );
			orbitBehavior.addMouseListeners( (GLCanvas)drawable );

			gl.glClearColor( 0.0f, 0.0f, 0.5f, 0.0f );
			//*
			forma = HelixGenerator.generate( gl, Generator.WIREFRAME, 1.2f, 0.0f, 12, 1.25f, 0.05f, 36, 1.2f*2.8f, 9 );
			//forma = HelixGenerator.generate( gl, Generator.WIREFRAME, 1.2f, 3.0f, 6 );
			//forma = generateQuad( gl, 6.0f );
			/*/
			Matrix4d mtd = new Matrix4d();
			mtd.rotY( Math.PI / 2 );
			mtd.setScale( 18.0 );
			try{
				forma = ObjLoader
						.load( "resources/obj3d/nave02/forma.obj", ObjLoader.RESIZE | ObjLoader.WIREFRAME, mtd );
			}catch( Exception e ){
				e.printStackTrace();
			}
			// */

			Shader shader = new Shader( gl, "shaders/wireframe.vert", "shaders/wireframe.frag" );

			shader.bindAttribLocation( gl,  1, "vertice0" );
			shader.bindAttribLocation( gl,  2, "vertice1" );
			shader.bindAttribLocation( gl,  3, "vertice2" );
			shader.bindAttribLocation( gl,  4, "vertice3" );
//			System.out.println( "vertice0:" + shader.getAttribLocation( gl, "vertice0" ) );
//			System.out.println( "vertice1:" + shader.getAttribLocation( gl, "vertice1" ) );
//			System.out.println( "vertice2:" + shader.getAttribLocation( gl, "vertice2" ) );
//			System.out.println( "vertice3:" + shader.getAttribLocation( gl, "vertice3" ) );

			
			shader.addUniform( gl, "solid_width", 0.5f );
			shader.addUniform( gl, "translucid_width", 1.5f );
			shader.addUniform( gl, "empty_color", new Color4f( 0.0f, 1.0f, 0.0f, 1.0f ) );
			
			shader.addUniform( gl, "viewport", viewport );

			forma.getApariencia().setShader( shader );

			gl.glEnable( GL.GL_DEPTH_TEST );
			gl.glDepthFunc( GL.GL_LESS );

			gl.glEnable( GLLightingFunc.GL_LIGHT0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
					new float[] { 0.0f, 0.0f, 1.0f, 1.0f }, 0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,
					new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR,
					new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0 );

			gl.glEnable(GL.GL_CULL_FACE);
			gl.glHint( GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST );
		}

		public void display( GLAutoDrawable drawable ){

			GL2 gl = drawable.getGL().getGL2();

			aparienciaNula.usar( gl );
			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
			
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			orbitBehavior.setLookAt( glu );
			
			forma.draw( gl );

			//gl.glFlush();
		}

		public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ){
			GL2 gl = drawable.getGL().getGL2();
			gl.glViewport( 0, 0, w, h );
			viewport.set( w / 2.0f, h / 2.0f );
			
			Shader.desactivar( gl );
			forma.getApariencia().getShader().setUniform( "viewport", viewport );
			//forma.getApariencia().getShader().activar( gl );
			
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			double near = 0.01;
			double far = 10000.0;
			double a1 = 45.0; // angulo en grados
			double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
			double d = near / Math.sqrt( ( 1 / Math.pow( Math.sin( a2 ), 2 ) ) - 1 );

			// formato panoramico Horizontal/Vertical centrado
			double ratio = (double)w / h;
			if( ratio < 1 )
				gl.glFrustum( -d, d, -d / ratio, d / ratio, near, far );
			else
				gl.glFrustum( -d * ratio, d * ratio, -d, d, near, far );
		}

		/*
		 * (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable drawable ){
		}
	}

	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba: Single pass wireframe" );
		frame.getContentPane().setBackground( Color.BLACK );
		frame.getContentPane().setPreferredSize( new Dimension( 640, 480 ) );
		frame.pack();
		frame.setLocationRelativeTo( null ); // center
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		GLCanvas canvas = new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );
		canvas.addGLEventListener( new Renderer() );

		frame.getContentPane().add( canvas );

		Animator animator = new Animator();
		animator.add( canvas );

		frame.setVisible( true );

		animator.add( canvas );
		canvas.requestFocusInWindow();
		animator.start();
	}
}
