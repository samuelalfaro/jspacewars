/* 
 * Prueba030_Particulas.java
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
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.MatrixSingleton;
import org.sam.jogl.Textura;
import org.sam.jogl.Textura.MagFilter;
import org.sam.jogl.Textura.MinFilter;
import org.sam.jogl.UnidadTextura;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

import com.jogamp.opengl.util.Animator;

/**
 */

public class Prueba030_Particulas{
	
	static class Renderer implements GLEventListener{
		
		private GLU glu;
		
		private final OrbitBehavior orbitBehavior;
		private Particulas explosion, humo, fuente;
		private UnidadTextura fondo, texture1, texture2, texture3;
	
		public Renderer(){
			
			orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos( 1.0f, 0.0f, 3.0f );
			orbitBehavior.setTargetPos( 1.0f, 0.0f, 0.0f );
			
			// TODO fixme
			// FactoriaDeParticulas.setPointSpritesEnabled(true);
	
			explosion = FactoriaDeParticulas.createParticulas( 60 );
			explosion.setEmision( Particulas.Emision.CONTINUA );
			explosion.setRangoDeEmision( 0.0f );
			explosion.setEmisor( new Emisor.Puntual() );
			explosion.setVelocidad( 1.0f, 0.2f, false );
			explosion.setTiempoVida( 0.5f );
			explosion.setGiroInicial( 0, 180, true );
			explosion.setVelocidadGiro( 180.0f, 90.0f, true );
			explosion.setColor( 0.3f, 0.2f, 0.15f, 0.9f );
	
			// explosion.setRadio(0.15f);
			explosion.setRadio( FactoriaDeParticulas.isPointSpritesEnabled() ? 64f: 0.15f ); // TODO Fix
			explosion.reset();
	
			humo = FactoriaDeParticulas.createParticulas( 30 );
			humo.setEmision( Particulas.Emision.CONTINUA );
			humo.setRangoDeEmision( 0.25f );
			humo.setEmisor( new Emisor.Cache( new Emisor.Puntual(), 512 ) );
			humo.setVelocidad( 0.2f, 0.04f, false );
			humo.setTiempoVida( 2.5f );
			humo.setGiroInicial( 0, 180, true );
			humo.setVelocidadGiro( 30.0f, 15.0f, true );
			humo.setColor(
					GettersFactory.Float.create(
							new float[] { 0.0f, 0.25f, 0.75f, 1.0f },
							new float[][] {
								{ 1.0f, 1.0f, 1.0f },
								{ 0.5f, 0.45f, 0.35f },
								{ 0.5f, 0.25f, 0.05f },
								{ 1.0f, 1.0f, 1.0f }
							},
							MetodoDeInterpolacion.Predefinido.COSENOIDAL
					),
					1.0f
			);
			// humo.setRadio(0.25f);
			humo.setRadio( FactoriaDeParticulas.isPointSpritesEnabled() ? 64f: 0.25f );// TODO fix
			humo.reset();
	
			// FactoriaDeParticulas.setPointSpritesEnabled(false);
			fuente = FactoriaDeParticulas.createParticulas( 1000 );
	
			Matrix4f tLocal = new Matrix4f();
			tLocal.rotZ( (float)Math.PI / 2 );
			tLocal.setTranslation( new Vector3f( 1.0f, 0.0f, 0.0f ) );
			fuente.setEmisor( new Emisor.Transformador( new Emisor.Conico( 0.02f, 0.1f ), tLocal ) );
	
			fuente.setEmision( Particulas.Emision.CONTINUA );
			fuente.setRangoDeEmision( 1.0f );
			// fuente.setRadio(0.0125f);
			fuente.setRadio( FactoriaDeParticulas.isPointSpritesEnabled() ? 64f: 0.0125f );// TODO fix
			if( !FactoriaDeParticulas.isPointSpritesEnabled() )
				fuente.setSemiEje( 0.025f );
			// fuente.setColor(0.1f, 0.3f, 0.5f, 1.0f);
			fuente.setColor(
					GettersFactory.Float.create(
							new float[] { 0.0f, 0.25f, 0.75f, 1.0f },
							new float[][] {
								{ 0.1f, 0.3f, 0.5f },
								{ 0.5f, 0.1f, 0.3f },
								{ 0.3f, 0.5f, 0.1f },
								{ 0.1f, 0.3f, 0.5f }
							},
							MetodoDeInterpolacion.Predefinido.COSENOIDAL
					),
					1.0f
			);
			fuente.setVelocidad( 0.6f );
			fuente.setFuerza( 0.0f, -0.392f, 0.0f );
			fuente.setTiempoVida( 3.5f );
			fuente.reset();
		}
	
		public void init( GLAutoDrawable drawable ){
			glu = new GLU();
			orbitBehavior.addMouseListeners( (GLCanvas)drawable );
			
			GL2 gl = drawable.getGL().getGL2();
	
			fondo = new UnidadTextura();
			fondo.setTextura( new Textura( gl, MinFilter.NEAREST, MagFilter.NEAREST, Textura.Format.RGB,
					Imagen.cargarToBufferedImage( "resources/fondo.png" ), true ) );
			fondo.setAtributosTextura( new AtributosTextura() );
			fondo.getAtributosTextura().setMode( AtributosTextura.Mode.REPLACE );

			texture1 = new UnidadTextura();
			texture1.setTextura( new Textura( gl, Textura.Format.ALPHA,
					Imagen.cargarToBufferedImage( "resources/texturas/smok.png" ), true ) );
			texture1.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			texture1.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
			
			texture1.setAtributosTextura( new AtributosTextura() );
			texture1.getAtributosTextura().setEnvColor( 1.0f, 1.0f, 1.0f, 1.0f );
			texture1.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			texture1.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.INTERPOLATE );
			// C' = S0 * S2 + S1*(1-S2)
			texture1.getAtributosTextura().setCombineRgbSource0( AtributosTextura.CombineSrc.OBJECT,
					AtributosTextura.CombineOperand.SRC_COLOR );
			texture1.getAtributosTextura().setCombineRgbSource1( AtributosTextura.CombineSrc.CONSTANT,
					AtributosTextura.CombineOperand.SRC_COLOR );
			texture1.getAtributosTextura().setCombineRgbSource2( AtributosTextura.CombineSrc.TEXTURE,
					AtributosTextura.CombineOperand.SRC_ALPHA );

			texture2 = new UnidadTextura();
			texture2.setTextura( texture1.getTextura() );

			texture3 = new UnidadTextura();
			texture3.setTextura( new Textura( gl, Textura.Format.ALPHA, Imagen
					.cargarToBufferedImage( "resources/texturas/flecha.jpg" ), true ) );
			texture3.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			texture3.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
				
			// FactoriaDeParticulas.setPointSpritesEnabled(true);
			gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
			gl.glEnable( GL.GL_DEPTH_TEST );
			gl.glDepthFunc( GL.GL_LESS );
			// gl.glEnable(GL.GL_CULL_FACE);
		}
	
		private transient long tAnterior, tActual;
	
		public void display( GLAutoDrawable drawable ){
			tAnterior = tActual;
			tActual = System.nanoTime();
			long incT = tActual - tAnterior;
	
			GL2 gl = drawable.getGL().getGL2();
	
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPushMatrix();
			gl.glLoadIdentity();
	
			gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
			
			float x = w / 2;
			float y = h / 2;
			float s = x / 256;
			float t = y / 256;
			gl.glOrtho( -x, x, -y, y, 0, 1 );
	
			fondo.activar( gl, 0 );

			gl.glDepthMask( false );
			gl.glBegin( GL2.GL_QUADS );
				gl.glTexCoord2f( -s, -t );
				gl.glVertex2f( -x, -y );
				gl.glTexCoord2f( s, -t );
				gl.glVertex2f( x, -y );
				gl.glTexCoord2f( s, t );
				gl.glVertex2f( x, y );
				gl.glTexCoord2f( -s, t );
				gl.glVertex2f( -x, y );
			gl.glEnd();
			gl.glDepthMask( true );
	
			gl.glPopMatrix();
	
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			// gl.glLoadIdentity();
			orbitBehavior.setLookAt( glu );
			MatrixSingleton.loadModelViewMatrix();
	
			gl.glEnable( GL.GL_BLEND );
			gl.glDepthMask( false );
	
			//
			texture1.activar( gl, 0 );
	
			// gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);
			gl.glBlendEquation( GL.GL_FUNC_ADD );
			gl.glBlendFunc( GL.GL_ZERO, GL.GL_SRC_COLOR );
	
			humo.getModificador().modificar( incT );
			humo.draw( gl );
	
			//UnidadTextura.desactivar( gl, 1 );
			// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			// gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_COLOR);
			// gl.glBlendFunc(GL.GL_ZERO,GL.GL_ONE_MINUS_SRC_ALPHA);
			// gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);
			texture2.activar( gl, 0 );
			gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE );
			explosion.getModificador().modificar( incT );
			explosion.draw( gl );
	
			texture3.activar( gl, 0 );
			// gl.glBlendEquation(GL.GL_FUNC_ADD);
			// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
			gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA );
	
			fuente.getModificador().modificar( incT );
			fuente.draw( gl );
	
			gl.glDepthMask( true );
			gl.glDisable( GL.GL_BLEND );
	
			gl.glFlush();
		}
	
		float w;
		float h;
	
		public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ){
			this.w = w;
			this.h = h;
			if( w != 0 && h != 0 ){
				GL2 gl = drawable.getGL().getGL2();
				gl.glViewport( 0, 0, w, h );
	
				gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
				gl.glLoadIdentity();
				double near = 0.001;
				double far = 30.0;
				double a1 = 30.0; // angulo en grados
				double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
				double d = near / Math.sqrt( ( 1 / Math.pow( Math.sin( a2 ), 2 ) ) - 1 );
	
				double ratio = (double)w / h;
				if( ratio < 1 )
					gl.glFrustum( -d, d, -d / ratio, d / ratio, near, far );
				else
					gl.glFrustum( -d * ratio, d * ratio, -d, d, near, far );
	
				MatrixSingleton.loadProjectionMatrix();
				gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			}
		}
	
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable drawable ){
		}
	}
	
	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba Particulas" );
		frame.getContentPane().setBackground( Color.BLACK );
		frame.getContentPane().setPreferredSize( new Dimension( 640, 480 ) );
		frame.pack();
		frame.setLocationRelativeTo( null ); // center
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		GLCanvas canvas = new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );
		canvas.addGLEventListener( new Renderer() );

		frame.getContentPane().add( canvas );

		Animator animator = new Animator();
		// animator.setRunAsFastAsPossible(true);
		animator.add( canvas );

		frame.setVisible( true );

		animator.add( canvas );
		canvas.requestFocusInWindow();
		animator.start();
	}
}