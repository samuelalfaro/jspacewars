/* 
 * PruebaTexto.java
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

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.Textura;
import org.sam.jogl.Textura.MagFilter;
import org.sam.jogl.Textura.MinFilter;
import org.sam.jogl.gui.TextureFont;
import org.sam.jogl.gui.GLTextRenderer;
import org.sam.jogl.gui.GLTextRenderer.HorizontalAlignment;
import org.sam.jogl.gui.GLTextRenderer.VerticalAlignment;
import org.sam.util.Imagen;

import com.jogamp.opengl.util.Animator;

public class PruebaTexto{

	private static final int AREA_WIDTH  = 1024;
	private static final int AREA_HEIGHT =  768;

	private static class GUIRenderer implements GLEventListener{

		//*
		private final static String fontDef      = "resources/texturas/fonts/arbeka.xml";
		private final static String font1Texture = "resources/texturas/fonts/arbeka.png";
		private final static String font2Texture = "resources/texturas/fonts/arbeka-neon.png";
		/*/
		private final static String fontDef      = "resources/texturas/fonts/saved.xml";
		private final static String font1Texture = "resources/texturas/fonts/saved.png";
		private final static String font2Texture = "resources/texturas/fonts/saved-neon.png";
		//*/
		private final GLTextRenderer renderer;
		
		private TextureFont font1;
		private TextureFont font2;
		private TextureFont font3;


		public GUIRenderer(){
			renderer = new GLTextRenderer();
		}

		public void init( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();

			try{
				TextureFont font = new TextureFont( gl, new FileInputStream( fontDef ) );
				font1 = font.deriveFont( 0.35f, 0.666f );
				font2 = font;
				font3 = font.deriveFont( 0.25f ).deriveFont( 4.0f );
			}catch( FileNotFoundException e ){
				e.printStackTrace();
			}

			BufferedImage img = Imagen.cargarToBufferedImage( font1Texture );

			Apariencia apFont = new Apariencia();

			//*
			apFont.setTextura( new Textura( gl, MinFilter.NEAREST, MagFilter.LINEAR, Textura.Format.ALPHA, img, false ) );
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

			apFont.setAtributosTransparencia( new AtributosTransparencia( AtributosTransparencia.Equation.ADD,
					AtributosTransparencia.SrcFunc.SRC_ALPHA, AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA ) );
			font1.setApariencia( apFont );
			font2.setApariencia( apFont );

			img = Imagen.cargarToBufferedImage( font2Texture );
			apFont = new Apariencia();

			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );

			apFont.setAtributosTextura( new AtributosTextura() );

			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0(
				AtributosTextura.CombineSrc.OBJECT, 
				AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
				AtributosTextura.CombineSrc.OBJECT,
				AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.getAtributosTextura().setCombineAlphaSource1(
				AtributosTextura.CombineSrc.TEXTURE,
				AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.setAtributosTransparencia(
				new AtributosTransparencia(
					AtributosTransparencia.Equation.ADD,
					AtributosTransparencia.SrcFunc.SRC_ALPHA,
					AtributosTransparencia.DstFunc.ONE
				)
			);
			font3.setApariencia( apFont );

			gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
			gl.glClearColor( 0.2f, 0.2f, 0.3f, 0.0f );
		}

		public void display( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();

			gl.glClear( GL.GL_COLOR_BUFFER_BIT );

			new Apariencia().usar( gl );
			gl.glBegin( GL.GL_LINES );
			gl.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );
			gl.glVertex2f( 0, 200 );
			gl.glVertex2f( 1024, 200 );
			gl.glVertex2f( 0, 350 );
			gl.glVertex2f( 1024, 350 );
			gl.glVertex2f( 0, 450 );
			gl.glVertex2f( 1024, 450 );
			gl.glVertex2f( 0, 550 );
			gl.glVertex2f( 1024, 550 );
			gl.glEnd();

			renderer.setFont( font1 );
			
			renderer.setHorizontalAlignment( HorizontalAlignment.CENTER );
			renderer.setVerticalAlignment( VerticalAlignment.TOP );
			renderer.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
			renderer.glPrint( gl, 512, 5, "Prueba Texto" );

			// Horizontal Alignment Test
			renderer.setColor( 0.5f, 1.0f, 0.5f, 1.0f );

			renderer.setHorizontalAlignment( HorizontalAlignment.LEFT );
			renderer.glPrint( gl, 5, 80, "Izquierda" );

			renderer.setHorizontalAlignment( HorizontalAlignment.CENTER );
			renderer.glPrint( gl, 512, 80, "Centro" );

			renderer.setHorizontalAlignment( HorizontalAlignment.RIGHT );
			renderer.glPrint( gl, 1019, 80, "Derecha" );

			// Vertical Alignment Test
			renderer.setColor( 1.0f, 0.5f, 1.0f, 1.0f );

			renderer.setHorizontalAlignment( HorizontalAlignment.CENTER );
			renderer.setVerticalAlignment( VerticalAlignment.BASE_LINE );
			renderer.glPrint( gl, 128, 200, "Lìnea base [j]" );

			renderer.setVerticalAlignment( VerticalAlignment.TOP );
			renderer.glPrint( gl, 384, 200, "Arriba [j]" );

			renderer.setVerticalAlignment( VerticalAlignment.CENTER );
			renderer.glPrint( gl, 640, 200, "Centro [j]" );

			renderer.setVerticalAlignment( VerticalAlignment.BOTTOM );
			renderer.glPrint( gl, 896, 200, "Abajo [j]" );

			renderer.setHorizontalAlignment( HorizontalAlignment.CENTER );
			renderer.setVerticalAlignment( VerticalAlignment.BASE_LINE );

			renderer.setFont( font2 );
			renderer.setColor( 0.5f, 0.25f, 0.5f, 1.0f );
			renderer.glPrint( gl, 512, 350, "ABCDEFGHIJKLMNOPQRSTUVXYZ" );
			renderer.setFont( font3 );
			renderer.setColor( 1.0f, 1.0f, 0.0f, 0.75f );
			renderer.glPrint( gl, 512, 350, "ABCDEFGHIJKLMNOPQRSTUVXYZ" );

			renderer.setFont( font2 );
			renderer.setColor( 0.0f, 0.5f, 0.5f, 1.0f );
			renderer.glPrint( gl, 512, 450, "abcdefghijklmnopqrstuvxyz" );
			renderer.setFont( font3 );
			renderer.setColor( 1.0f, 0.5f, 1.0f, 0.75f );
			renderer.glPrint( gl, 512, 450, "abcdefghijklmnopqrstuvxyz" );

			renderer.setFont( font2 );
			renderer.setColor( 0.0f, 0.0f, 0.5f, 1.0f );
			renderer.glPrint( gl, 512, 550, "12345,67890.12345;67890" );
			renderer.setFont( font3 );
			renderer.setColor( 1.0f, 0.5f, 1.0f, 0.75f );
			renderer.glPrint( gl, 512, 550, "12345,67890.12345;67890" );

			renderer.setFont( font2 );
			renderer.setColor( 0.0f, 0.0f, 0.5f, 1.0f );
			renderer.glPrint( gl, 512, 650, "áèïóú âêîôû" );
			renderer.setFont( font3 );
			renderer.setColor( 1.0f, 0.5f, 1.0f, 0.5f );
			renderer.glPrint( gl, 512, 650, "áèïóú âêîôû" );
		}

		public void reshape( GLAutoDrawable glDrawable, int x, int y, int width, int height ){
			if( width != 0 && height != 0 ){
				GL2 gl = glDrawable.getGL().getGL2();
				
				/* Centra el área, conservando el tamaño fijo:
				 * Añadiendo bordes / recortando extremos, dejando el centro del área en el centro
				 * de la ventana.
				 */
				//gl.glViewport( ( width - AREA_WIDTH ) / 2, ( height - AREA_HEIGHT ) / 2, AREA_WIDTH, AREA_HEIGHT );
				//gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
				//gl.glLoadIdentity();
				//gl.glOrtho( 0, width, height, 0, -1, 1 );
				
				/* Centra y escala el área, conservando las proporciones:
				 * La altura que se ajusta a la altura de la ventana y en función de la anchura correspondiente,
				 * se añanden bordes laterales o se recortan los extremos laterales.
				 */
				gl.glViewport( ( AREA_HEIGHT * width - AREA_WIDTH * height ) / ( 2 * AREA_HEIGHT ), 0, AREA_WIDTH
						* height / AREA_HEIGHT, height );
				gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
				gl.glLoadIdentity();
				gl.glOrtho( 0, AREA_WIDTH, AREA_HEIGHT, 0, -1, 1 );
			}
		}

		/*
		 * (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable glDrawable ){
		}
	}

	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba Texto" );
		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().setBackground( Color.BLACK );

		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener( new GUIRenderer() );
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
