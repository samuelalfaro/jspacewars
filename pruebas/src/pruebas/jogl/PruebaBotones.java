/* 
 * PruebaBotones.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.NodoTransformador;
import org.sam.jogl.Textura;
import org.sam.jogl.gui.Font;
import org.sam.jogl.gui.TextRenderer;
import org.sam.jogl.gui.TextRenderer.HorizontalAlignment;
import org.sam.jogl.gui.TextRenderer.VerticalAlignment;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

import com.jogamp.opengl.util.Animator;

public class PruebaBotones{
	
	private static final int VIRTUAL_AREA_WIDTH  = 1024;
	private static final int VIRTUAL_AREA_HEIGHT =  768;
	
	static int AREA_WIDTH;
	static int AREA_HEIGHT;
	
	static int VIRTUAL_VIEWPORT_X;
	static int VIRTUAL_VIEWPORT_Y;
	static int VIRTUAL_VIEWPORT_WIDTH;
	static int VIRTUAL_VIEWPORT_HEIGHT;
	
	private static class GUIListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		
		private static void setPosition( Point point, Matrix4f transform ){
			transform.m03 = point.x; 
			transform.m13 = point.y;
		}
		
		final NodoTransformador cursor;
		
		GUIListener( NodoTransformador cursor ){
			this.cursor = cursor;
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
			setPosition( e.getPoint(), cursor.getTransform() );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited( MouseEvent e ){
			Point    point = e.getPoint();
			Matrix4f transform = cursor.getTransform();
			transform.m03 = ( point.x - transform.m03 ) * 80 + point.x; 
			transform.m13 = ( point.y - transform.m13 ) * 80 + point.y; 
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
			setPosition( e.getPoint(), cursor.getTransform() );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved( MouseEvent e ){
			setPosition( e.getPoint(), cursor.getTransform() );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
		 */
		@Override
		public void mouseWheelMoved( MouseWheelEvent e ){
		}
	}

	private static class GUIRenderer implements GLEventListener{
		
		private final static String fontDef = "resources/texturas/fonts/arbeka.xml";
		private final static String font1Texture = "resources/texturas/fonts/arbeka.png";
		private final static String font2Texture = "resources/texturas/fonts/arbeka-blur.png";

		private TextRenderer renderer1;
		private TextRenderer renderer2;
		
		public GUIRenderer(){
			renderer1 = new TextRenderer();
			renderer1.setHorizontalAlignment( HorizontalAlignment.CENTER );
			renderer1.setVerticalAlignment( VerticalAlignment.TOP );
			
			renderer2 = new TextRenderer();
			renderer2.setHorizontalAlignment( HorizontalAlignment.CENTER );
			renderer2.setVerticalAlignment( VerticalAlignment.TOP );
		}
		
		public void init( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();
			
			try{
				Font font = new Font( gl, new FileInputStream( fontDef ) ).deriveFont( 0.9f );
				renderer1.setFont( font );
				renderer2.setFont( font );
			}catch( FileNotFoundException e ){
				e.printStackTrace();
			}
		
			BufferedImage img = Imagen.cargarToBufferedImage( font1Texture );
			
			Apariencia apFont = new Apariencia();
			
			//*
			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER);
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
			
			apFont.setAtributosTextura( new AtributosTextura() );
			
			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.getAtributosTextura().setCombineAlphaSource1(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			/*/
			apFont.setTextura( new Textura( gl, Textura.Format.RGBA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP );
			
			apFont.setAtributosTextura( new AtributosTextura() );
			
			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.ADD_SIGNED );
			apFont.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineRgbSource1(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			//*/
			
			apFont.setAtributosTransparencia( 
					new AtributosTransparencia( 
							AtributosTransparencia.Equation.ADD,
							AtributosTransparencia.SrcFunc.SRC_ALPHA,
							AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA
					) 
			);
			renderer1.setApariencia( apFont );
			
			img = Imagen.cargarToBufferedImage( font2Texture );
			apFont = new Apariencia();
			
			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
			
			apFont.setAtributosTextura( new AtributosTextura() );
			
			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.getAtributosTextura().setCombineAlphaSource1(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			
			apFont.setAtributosTransparencia( 
					new AtributosTransparencia( 
							AtributosTransparencia.Equation.ADD,
							AtributosTransparencia.SrcFunc.SRC_ALPHA,
							AtributosTransparencia.DstFunc.ONE
					) 
			);
			renderer2.setApariencia( apFont );
			
			gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
			gl.glClearColor( 0.2f, 0.2f, 0.3f, 0.0f );
		}

		public void display( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();
			
			gl.glViewport( VIRTUAL_VIEWPORT_X, VIRTUAL_VIEWPORT_Y, VIRTUAL_VIEWPORT_WIDTH, VIRTUAL_VIEWPORT_HEIGHT );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			gl.glOrtho( 0, VIRTUAL_AREA_WIDTH, VIRTUAL_AREA_HEIGHT, 0, -1, 1 );
			
			gl.glClear( GL.GL_COLOR_BUFFER_BIT );
			

			renderer2.setColor( 1.0f, 1.0f, 0.5f, 0.25f );
			renderer2.glPrint( gl, 512, 275, "1 Jugador" );
			renderer1.setColor( 0.0f, 0.75f, 0.25f, 1.0f );
			renderer1.glPrint( gl, 512, 275, "1 Jugador" );

			renderer1.glPrint( gl, 512, 350, "2 Jugadores" );
			renderer2.setColor( 1.0f, 1.0f, 0.5f, 0.75f );
			renderer2.glPrint( gl, 512, 350, "2 Jugadores" );
			renderer1.glPrint( gl, 512, 350, "2 Jugadores" );

			renderer1.glPrint( gl, 512, 425, "Opciones" );
			renderer2.setColor( 1.0f, 1.0f, 0.5f, 0.25f );
			renderer2.glPrint( gl, 512, 425, "Opciones" );
			
			renderer1.glPrint( gl, 512, 500, "Records" );
			renderer2.glPrint( gl, 512, 500, "Records" );
			
			renderer1.glPrint( gl, 512, 575, "Acerca de..." );
			renderer2.glPrint( gl, 512, 575, "Acerca de..." );

			renderer2.setColor( 1.0f, 1.0f, 0.0f, 0.75f );
			renderer2.glPrint( gl, 512, 675,  "Salir" );
			renderer1.setColor( 0.75f, 0.375f, 0.75f, 0.5f );
			renderer1.glPrint( gl, 512, 675, "Salir" );

		}

		public void reshape( GLAutoDrawable glDrawable, int x, int y, int width, int height ){
			if( width != 0 && height != 0 ){
				AREA_WIDTH              = width;
				AREA_HEIGHT             = height;
				VIRTUAL_VIEWPORT_X      = ( VIRTUAL_AREA_HEIGHT * width - VIRTUAL_AREA_WIDTH * height ) 
				                          / ( 2 * VIRTUAL_AREA_HEIGHT );
				VIRTUAL_VIEWPORT_Y      = 0;
				VIRTUAL_VIEWPORT_WIDTH  = VIRTUAL_AREA_WIDTH * height / VIRTUAL_AREA_HEIGHT;
				VIRTUAL_VIEWPORT_HEIGHT = height;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable glDrawable ){
		}
	}

	private static class CursorRenderer implements GLEventListener{
		
		private Particulas estela;
		private NodoTransformador cursor;
		
		public CursorRenderer(){
		}
	
		public void init( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();
			
			Apariencia ap = new Apariencia();
			
			ap.setTextura( new Textura( gl, Textura.Format.ALPHA, Imagen.cargarToBufferedImage( "resources/texturas/smok.png" ), true ) );
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
			
			FactoriaDeParticulas.setOptimizedFor2D(true);
			
			estela = FactoriaDeParticulas.createParticulas( 50 );
			estela.setEmision( Particulas.Emision.CONTINUA );
			estela.setRangoDeEmision( 1.0f );
			estela.setEmisor( new Emisor.Puntual( 0.25f, -0.25f, 45 ) );
			estela.setVelocidad( 300.0f, 20.0f, false );
			estela.setTiempoVida( 0.25f );
			estela.setGiroInicial( 0, 180, true );
			estela.setVelocidadGiro( 30.0f, 15.0f, true );
			estela.setColor(
					0.16f,
					0.24f,
					0.08f,
					GettersFactory.Float.create( 
						new float[] { 0.25f, 1.0f },
						new float[] { 1.0f, 0.0f },
						MetodoDeInterpolacion.Predefinido.COSENOIDAL
					)
			);
			estela.setRadio( 	
					GettersFactory.Float.create( 
						new float[] { 0.0f, 0.25f },
						new float[] { 0.0f, 12.0f },
						MetodoDeInterpolacion.Predefinido.COSENOIDAL
					)
			);
			estela.reset();
			estela.setApariencia( ap );
			
			Matrix4f t1 = new Matrix4f();
			t1.setIdentity();
			t1.rotX( (float)(Math.PI /2) );
			
			Matrix4f t2 = new Matrix4f();
			t2.setIdentity();
			t2.rotZ( (float)(Math.PI /4) );
			t2.mul( t1 );
			
			t1 = new Matrix4f();
			t1.setIdentity();
			t1.setTranslation( new Vector3f( 8, 8, 0 ) );
			t1.mul( t2 );
			
			NodoTransformador nt = new NodoTransformador( t1, estela );
			
			Matrix4f tCursor = new Matrix4f();
			tCursor.setIdentity();
			cursor = new NodoTransformador( tCursor, nt );
			
			GUIListener myListener = new GUIListener( cursor );
			
			Component component = ( (GLCanvas)glDrawable );
			component.addMouseListener( myListener );
			component.addMouseMotionListener( myListener );
			component.addMouseWheelListener( myListener );
		}
	
		private transient long tAnterior, tActual;
		
		public void display( GLAutoDrawable glDrawable ){
			tAnterior = tActual;
			tActual = System.nanoTime();

			float incT = (float)( tActual - tAnterior ) / 1000000000;
		
			GL2 gl = glDrawable.getGL().getGL2();
			
			gl.glViewport( 0, 0, AREA_WIDTH, AREA_HEIGHT );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			gl.glOrtho( 0, AREA_WIDTH, AREA_HEIGHT, 0, -1, 1 );
	
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			
			estela.getModificador().modificar( incT );
			cursor.draw( gl );
			gl.glFlush();
		}
	
		public void reshape( GLAutoDrawable glDrawable, int x, int y, int w, int h ){
		}
	
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable glDrawable ){
		}
	}

	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba botones" );
		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().setBackground( Color.BLACK );

		GLCanvas canvas = new GLCanvas();
		
		
		BufferedImage bi = new BufferedImage( 32, 32, BufferedImage.TYPE_INT_ARGB );
		bi.createGraphics().drawImage( Imagen.cargarToBufferedImage( "resources/cursor.png" ), 0, 0, null );
		
		canvas.setCursor(
				Toolkit.getDefaultToolkit().createCustomCursor(
						bi,
						new Point( 0, 0 ),
						"spaceship cursor"
				)
		);
		System.out.println( Toolkit.getDefaultToolkit().getMaximumCursorColors() );
		
		canvas.addGLEventListener( new GUIRenderer() );
		canvas.addGLEventListener( new CursorRenderer() );
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
