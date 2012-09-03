/* 
 * PruebaBotones.java
 * 
 * Copyright (c) 2008-2011
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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.NodoTransformador;
import org.sam.jogl.Textura;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

import com.jogamp.opengl.util.Animator;

public class PruebaCursor{	
	
	static class CursorListener implements MouseListener, MouseMotionListener{

		private transient final Matrix4f transform;
		
		CursorListener( Matrix4f transform ){
			this.transform = transform;
		}
		
		private void setPosition( Point point ){
			transform.m03 = point.x;
			transform.m13 = point.y;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked( MouseEvent e ){
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered( MouseEvent e ){
			setPosition( e.getPoint() );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited( MouseEvent e ){
			transform.m03 =	( e.getPoint().x - transform.m03 ) * 80 + e.getPoint().x;
			transform.m13 =	( e.getPoint().y - transform.m13 ) * 80 + e.getPoint().y;
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed( MouseEvent e ){
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased( MouseEvent e ){
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged( MouseEvent e ){
			setPosition( e.getPoint() );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved( MouseEvent e ){
			setPosition( e.getPoint() );
		}
	}

	static class Renderer implements GLEventListener{
 
		private final Particulas estela;
		private final NodoTransformador cursor;
		
		public Renderer( Matrix4f tCursor ){
			
			tCursor.setIdentity();
			
			FactoriaDeParticulas.setOptimizedFor2D(true);
			
			estela = FactoriaDeParticulas.createParticulas( 50 );

			estela.setEmisor( new Emisor.Puntual() );
			estela.setEmision( Particulas.Emision.CONTINUA );
			estela.setRangoDeEmision( 1.0f );
			estela.setVelocidad( 100.0f, 20.0f, false );
			estela.setTiempoVida( 0.25f );
			estela.setGiroInicial( 0, 180, true );
			estela.setVelocidadGiro( 30.0f, 15.0f, true );
			estela.setColor(
					0.15f,
					0.3f,
					0.2f,
					GettersFactory.Float.create( 
							new float[] { 0.25f, 1.0f },
							new float[] { 1.0f, 0.0f },
							MetodoDeInterpolacion.Predefinido.COSENOIDAL
					)
			);
			estela.setRadio( 	
					GettersFactory.Float.create( 
						new float[] { 0.0f, 0.25f },
						new float[] { 0.0f, 15.0f },
						MetodoDeInterpolacion.Predefinido.COSENOIDAL
					)
			);
			estela.reset();
			
			FactoriaDeParticulas.setOptimizedFor2D( false );
			
			cursor = new NodoTransformador( tCursor, estela );
		}
	
		public void init( GLAutoDrawable glDrawable ){
			
			GL2 gl = glDrawable.getGL().getGL2();
			
			Apariencia ap = new Apariencia();
			
			ap.setTextura( new Textura( gl, Textura.Format.ALPHA, Imagen.cargarToBufferedImage( "resources/texturas/spark.jpg" ), true ) );
			ap.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER);
			ap.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
			
			ap.setAtributosTextura( new AtributosTextura() );
			
			ap.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			ap.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			ap.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			ap.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			ap.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			ap.getAtributosTextura().setCombineAlphaSource1(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);			
			
			ap.setAtributosTransparencia( 
					new AtributosTransparencia( 
							AtributosTransparencia.Equation.ADD,
							AtributosTransparencia.SrcFunc.SRC_ALPHA,
							AtributosTransparencia.DstFunc.ONE
					) 
			);
			
			estela.setApariencia( ap );
			
			gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
			gl.glClearColor( 0.2f, 0.2f, 0.3f, 0.0f );
		}
	
		private transient long tAnterior, tActual;
		
		public void display( GLAutoDrawable glDrawable ){
			tAnterior = tActual;
			tActual = System.nanoTime();

			long incT = tActual - tAnterior;
		
			GL2 gl = glDrawable.getGL().getGL2();
	
			gl.glClear( GL.GL_COLOR_BUFFER_BIT );
			
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			
			estela.getModificador().modificar( incT );
			cursor.draw( gl );
			gl.glFlush();
		}
	
		public void reshape( GLAutoDrawable glDrawable, int x, int y, int w, int h ){
			if( h == 0 )
				h = 1;
			GL2 gl = glDrawable.getGL().getGL2();
			
			gl.glViewport( 0, 0, w, h );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			gl.glOrtho( 0, w, h, 0, -1, 1 );
		}
	
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable glDrawable ){
		}
	}

	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba Cursor" );
		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().setBackground( Color.BLACK );

		GLCanvas canvas = new GLCanvas();
		
		canvas.setCursor(
				Toolkit.getDefaultToolkit().createCustomCursor( 
						new BufferedImage( 16, 16, BufferedImage.TYPE_INT_ARGB ),
						new Point( 0, 0 ),
						"blank cursor"
				)
		);
		
		Matrix4f tCursor = new  Matrix4f();
		
		CursorListener cursorListener = new CursorListener( tCursor );
		canvas.addMouseListener( cursorListener );
		canvas.addMouseMotionListener( cursorListener );
		
		canvas.addGLEventListener( new Renderer( tCursor ) );
		
		frame.getContentPane().add( canvas, BorderLayout.CENTER );

		frame.getContentPane().setPreferredSize( new Dimension( 640, 480 ) );
		frame.pack();
		frame.setLocationRelativeTo( null ); // center
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
		
		Animator animator = new Animator();
		animator.setRunAsFastAsPossible( true );
		animator.add( canvas );
		animator.start();
		
		canvas.requestFocusInWindow();
	}
}