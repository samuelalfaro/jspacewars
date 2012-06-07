/* 
 * PruebaBloom.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import org.sam.jogl.Material;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.Shader;

import pruebas.jogl.generators.HelixGenerator;

import com.jogamp.opengl.util.Animator;

/**
 * Prueba del efecto Bloom.<br/>
 * <br/>
 * Basado en port a jogl de <a
 * href="http://www.java-tips.org/other-api-tips/jogl/radial-blur-and-rendering-to-a-texture-nehe-tutorial-jogl.html"
 * >Pepijn Van Eeckhoudt</a> de la <a href="http://nehe.gamedev.net/data/lessons/lesson.asp?lesson=36">leccion 36</a> de
 * los tutoriales <a href="http://nehe.gamedev.net/">NeHe</a>.<br/>
 * Y en el código de la demo <a href="http://pouet.scene.org/prod.php?which=54043">coffee</a> de <a
 * href="http://pouet.scene.org/groups.php?which=10834">rustbloom</a>.
 */
public class PruebaBloom{

	static int initTexture( GL gl, int w, int h ){
		int[] textuId = new int[1];
		gl.glGenTextures( 1, textuId, 0 );
		gl.glBindTexture( GL.GL_TEXTURE_2D, textuId[0] );
		gl.glTexImage2D( GL.GL_TEXTURE_2D, 0, GL.GL_RGB, w, h, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null );
		gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR );
		gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );

		return textuId[0];
	}

	static int initFBO( GL2 gl, int textuId, int w, int h ){
		int FBO[] = new int[1];
		gl.glGenFramebuffers( 1, FBO, 0 );
		gl.glBindTexture( GL.GL_TEXTURE_2D, textuId );
		gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, FBO[0] );
		gl.glFramebufferTexture2D( GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, textuId, 0 );

		int[] depthId = new int[1];
		gl.glGenRenderbuffers( 1, depthId, 0 );
		gl.glBindRenderbuffer( GL.GL_RENDERBUFFER, depthId[0] );
		gl.glRenderbufferStorage( GL.GL_RENDERBUFFER, GL.GL_DEPTH_COMPONENT24, w, h );
		gl.glFramebufferRenderbuffer( GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER, depthId[0] );

		// *
		int fbostate = gl.glCheckFramebufferStatus( GL.GL_FRAMEBUFFER );
		switch( fbostate ){
		case GL.GL_FRAMEBUFFER_COMPLETE:
			System.out.println( "framebuffer complete" );
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
			System.out.println( "incomplete: GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT" );
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
			System.out.println( "incomplete: GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS" );
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
			System.out.println( "incomplete: GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT" );
			break;
		case GL.GL_FRAMEBUFFER_UNSUPPORTED:
			System.out.println( "incomplete: GL_FRAMEBUFFER_UNSUPPORTED" );
			break;
		}
		// */
		gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, 0 );

		return FBO[0];
	}

	static void viewOrtho( GL2 gl, int w, int h ){
		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho( 0, w, 0, h, -1, 1 );
		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glPushMatrix();
		gl.glLoadIdentity();
	}

	static void viewPerspective( GL2 gl ){
		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glPopMatrix();
		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glPopMatrix();
	}

	static class Renderer implements GLEventListener{

		// User Defined Variables
		private GLU glu = new GLU();
		private int width, height;

		private Objeto3D helix;
		private int textuId[];
		private int FBO[];
		private Shader corte, radialBlur;

		private float angle;
		private float bloom;
		private float center[];
		private long previousTime = System.currentTimeMillis();

		Renderer(){}

		public void init( GLAutoDrawable drawable ){
			GL2 gl = drawable.getGL().getGL2();
			angle = 0.0f;
			bloom = 3.0f;
			center = new float[] { 0.25f, 0.25f };

			textuId = new int[2];
			textuId[0] = initTexture( gl, 128, 128 );
			textuId[1] = initTexture( gl, 128, 128 );
			FBO = new int[2];
			FBO[0] = initFBO( gl, textuId[0], 128, 128 );
			FBO[1] = initFBO( gl, textuId[1], 128, 128 );

			helix = HelixGenerator.generate( gl, 2.5f, 5.0f, 20 );

			Material material = new Material();
			material.setDiffuse( 0.4f, 0.2f, 0.8f, 1.0f );
			material.setSpecular( 1.0f, 1.0f, 1.0f, 1.0f );
			material.setShininess( 128.0f );

			helix.getApariencia().setMaterial( material );

			corte = new Shader( gl, "shaders/filter.vert", "shaders/corte.frag" );
			corte.addUniform( gl, "textureIn", 0 );
			corte.addUniform( gl, "pixelSize", 1.0f / 128 );
			corte.addUniform( gl, "corte", 0.18f );
			corte.addUniform( gl, "bloom", bloom );

			radialBlur = new Shader( gl, "shaders/filter.vert", "shaders/radialBlur.frag" );
			radialBlur.addUniform( gl, "textureIn", 0 );
			radialBlur.addUniform( gl, "pixelSize", 1.0f / 128 );
			radialBlur.addUniform( gl, "center", center );

			gl.glEnable( GL.GL_DEPTH_TEST );

			gl.glEnable( GLLightingFunc.GL_LIGHT0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
					new float[] { 0.0f, 0.0f, 10.0f, 1.0f }, 0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,
					new float[] { 0.9f, 1.0f, 1.0f, 1.0f }, 0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR,
					new float[] { 1.0f, 1.0f, 0.9f, 1.0f }, 0 );

			gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
		}

		private void drawHelix( GL2 gl ){

			gl.glPushMatrix();
			gl.glTranslatef( 0, 0, -25 );
			gl.glRotatef( angle / 3.0f, 0, 0, 1 );
			gl.glRotatef( angle / 2.0f, 1, 0, 0 );
			gl.glRotatef( angle / 3.0f, 0, 1, 0 );

			gl.glEnable( GLLightingFunc.GL_LIGHTING );
			helix.draw( gl );
			gl.glDisable( GLLightingFunc.GL_LIGHTING );

			gl.glPopMatrix();
		}

		private void renderToTexture( GL2 gl, int w, int h ){

			gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, FBO[0] );

			gl.glViewport( 0, 0, w, h );
			gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

			drawHelix( gl );

			viewOrtho( gl, w, h );

			gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, FBO[1] );
			gl.glClear( GL.GL_DEPTH_BUFFER_BIT );

			gl.glEnable( GL.GL_TEXTURE_2D );
			gl.glBindTexture( GL.GL_TEXTURE_2D, textuId[0] );
			// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE );
			gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE );
			gl.glTexEnvi( GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE );
			corte.setUniform( "bloom", bloom );
			corte.activar( gl );

			gl.glBegin( GL2.GL_QUADS );
			gl.glTexCoord2f( 0, 0 );
			gl.glVertex2f( 0, 0 );
			gl.glTexCoord2f( 0, 1 );
			gl.glVertex2f( 0, h );
			gl.glTexCoord2f( 1, 1 );
			gl.glVertex2f( w, h );
			gl.glTexCoord2f( 1, 0 );
			gl.glVertex2f( w, 0 );
			gl.glEnd();

			gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, FBO[0] );
			gl.glClear( GL.GL_DEPTH_BUFFER_BIT );

			gl.glEnable( GL.GL_TEXTURE_2D );
			gl.glBindTexture( GL.GL_TEXTURE_2D, textuId[1] );
			gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE );
			gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE );
			gl.glTexEnvi( GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE );
			radialBlur.setUniform( "center", center );
			radialBlur.activar( gl );

			gl.glBegin( GL2.GL_QUADS );
			gl.glTexCoord2f( 0, 0 );
			gl.glVertex2f( 0, 0 );
			gl.glTexCoord2f( 0, 1 );
			gl.glVertex2f( 0, h );
			gl.glTexCoord2f( 1, 1 );
			gl.glVertex2f( w, h );
			gl.glTexCoord2f( 1, 0 );
			gl.glVertex2f( w, 0 );
			gl.glEnd();

			Shader.desactivar( gl );
			viewPerspective( gl );

			gl.glDisable( GL.GL_TEXTURE_2D );
			gl.glBindTexture( GL.GL_TEXTURE_2D, 0 );
			gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, 0 );

			gl.glViewport( 0, 0, width, height );
		}

		private void drawBlur( GL2 gl, float alpha0, float alpha1, float centerX, float centerY, float zoom, int steps ){

			float alpha = alpha0;
			float incAlpha = ( alpha1 - alpha0 ) / ( steps - 1 );

			float zoomFactor = ( zoom - 1.0f ) / ( steps - 1 );

			float offsetIzq = 0.0f;
			float incIzq = centerX * zoomFactor;
			float offsetDer = 1.0f;
			float incDer = ( centerX - 1.0f ) * zoomFactor;
			float offsetSup = 1.0f;
			float incSup = ( centerY - 1.0f ) * zoomFactor;
			float offsetInf = 0.0f;
			float incInf = centerY * zoomFactor;

			gl.glEnable( GL.GL_TEXTURE_2D );
			gl.glBindTexture( GL.GL_TEXTURE_2D, textuId[0] );
			gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR );
			gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );
			gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2GL3.GL_CLAMP_TO_BORDER );
			gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2GL3.GL_CLAMP_TO_BORDER );
			gl.glTexEnvi( GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE );

			gl.glDisable( GL.GL_DEPTH_TEST );
			gl.glEnable( GL.GL_BLEND );

			gl.glBlendEquation( GL.GL_FUNC_ADD );
			// *
			gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE );
			/*
			 * / gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); //
			 */

			viewOrtho( gl, width, height );

			gl.glBegin( GL2.GL_QUADS );
			for( int num = 0; num < steps; num++ ){
				gl.glColor3f( alpha, alpha, alpha );
				gl.glTexCoord2f( offsetIzq, offsetInf );
				gl.glVertex2f( 0, 0 );
				gl.glTexCoord2f( offsetIzq, offsetSup );
				gl.glVertex2f( 0, height );
				gl.glTexCoord2f( offsetDer, offsetSup );
				gl.glVertex2f( width, height );
				gl.glTexCoord2f( offsetDer, offsetInf );
				gl.glVertex2f( width, 0 );

				offsetIzq += incIzq;
				offsetDer += incDer;
				offsetSup += incSup;
				offsetInf += incInf;
				alpha += incAlpha;
			}
			gl.glEnd();

			viewPerspective( gl );

			gl.glEnable( GL.GL_DEPTH_TEST );
			gl.glBindTexture( GL.GL_TEXTURE_2D, 0 );
			gl.glDisable( GL.GL_TEXTURE_2D );
			gl.glDisable( GL.GL_BLEND );
		}

		private long t = 0;

		private void update( long milliseconds ){
			t += milliseconds;
			if( t > 12000 )
				t -= 12000;
			double a = t * Math.PI * 2;
			angle += milliseconds / 5.0f;
			bloom = (float)( Math.sin( a / 1000 ) + 1.0 ) * 1.5f;
			center[0] = (float)( 0.25 * Math.cos( a / 2000 ) + 0.5 );
			center[1] = (float)( 0.25 * Math.sin( a / 3000 ) + 0.5 );
		}

		public void display( GLAutoDrawable drawable ){
			long currentTime = System.currentTimeMillis();
			update( currentTime - previousTime );
			previousTime = currentTime;

			GL2 gl = drawable.getGL().getGL2();

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			glu.gluLookAt( 0, 50, 100, 0, 0, 0, 0, 1, 0 );
			// gl.glMatrixMode(GL.GL_MODELVIEW);
			// gl.glLoadIdentity();

			renderToTexture( gl, 128, 128 );
			gl.glClearColor( 0.0f, 0.0f, 0.5f, 1.0f );
			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
			drawHelix( gl );

			// *
			drawBlur( gl, 0.5f, 0.1f, center[0], center[1], 2.0f, 10 );
			/*
			 * / drawBlur(gl, 0.5f,0.25f, 1, 0); //
			 */
			gl.glFlush();
		}

		public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ){
			GL2 gl = drawable.getGL().getGL2();

			height = ( height == 0 ) ? 1: height;
			this.width = width;
			this.height = height;

			gl.glViewport( 0, 0, width, height );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			glu.gluPerspective( 50, (float)width / height, 5, 2000 );
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
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

		JFrame frame = new JFrame( "Bloom" );
		frame.getContentPane().setPreferredSize( new Dimension( 800, 600 ) );
		frame.getContentPane().setLayout( new BorderLayout() );
		frame.pack();

		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		GLCanvas canvas = new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );
		canvas.setBackground( Color.BLACK );

		Renderer renderer = new Renderer();
		canvas.addGLEventListener( renderer );

		frame.getContentPane().add( canvas, BorderLayout.CENTER );

		Animator animator = new Animator();
		animator.setRunAsFastAsPossible( true );
		animator.add( canvas );

		frame.setVisible( true );
		animator.start();
	}
}