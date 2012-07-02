/* 
 * Prueba050_Shaders.java
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
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.LinkedList;

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
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.elementos.Modificador;
import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.AtributosTransparencia.DstFunc;
import org.sam.jogl.AtributosTransparencia.Equation;
import org.sam.jogl.AtributosTransparencia.SrcFunc;
import org.sam.jogl.GenCoordTextura;
import org.sam.jogl.Grupo;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.Material;
import org.sam.jogl.MatrixSingleton;
import org.sam.jogl.NodoCompartido;
import org.sam.jogl.NodoTransformador;
import org.sam.jogl.ObjLoader;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.Shader;
import org.sam.jogl.Textura;
import org.sam.jogl.Textura.MagFilter;
import org.sam.jogl.Textura.MinFilter;
import org.sam.jogl.UnidadTextura;
import org.sam.jogl.particulas.Particulas;
import org.sam.jspacewars.serialization.GrafoEscenaConverters;
import org.sam.util.Imagen;

import com.jogamp.opengl.util.Animator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Prueba050_Shaders{

	static class Renderer implements GLEventListener{

		private GLU glu;
		private OrbitBehavior orbitBehavior;

		private Apariencia apFondo;
		private Instancia3D nave1;

		private final Collection<Modificador> gestorDeParticulas;

		Renderer(){
			gestorDeParticulas = new LinkedList<Modificador>();
		}

		private transient float proporcionesFondo, proporcionesPantalla;
		private transient long tAnterior, tActual;

		public void init( GLAutoDrawable drawable ){
			GL2 gl = drawable.getGL().getGL2();

			glu = new GLU();
			orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos( 0.0f, 0.0f, 4.0f );
			orbitBehavior.addMouseListeners( (GLCanvas)drawable );

			gl.glClearColor( 0.0f, 0.25f, 0.25f, 0.0f );

			try{
				BufferedImage img = Imagen.cargarToBufferedImage( "resources/texturas/cielo512.jpg" );
				proporcionesFondo = img.getHeight( null ) / img.getWidth( null );
				apFondo = new Apariencia();

				apFondo.setTextura( new Textura( gl, Textura.Format.RGB, img, true ) );
				apFondo.setAtributosTextura( new AtributosTextura() );
				apFondo.getAtributosTextura().setMode( AtributosTextura.Mode.REPLACE );

				Grupo childs;
				Objeto3D nave;
				Matrix4d mtd;
				Matrix4f mtf;

				XStream xStream = new XStream( new DomDriver() );
				GrafoEscenaConverters.register( xStream );
				Particulas humoNave = (Particulas)xStream.fromXML( new FileReader( "resources/particulas.xml" ) );

				mtd = new Matrix4d();
				mtd.rotY( Math.PI / 2 );
				mtd.setScale( 1.25 );
				nave = ObjLoader.load( "resources/obj3d/nave05/forma.obj", mtd );
				Material material = new Material();
				material.setAmbient( 0.0f, 0.0f, 0.0f, 1.0f );
				material.setDiffuse( 0.25f, 0.25f, 0.25f, 1.0f );
				material.setSpecular( 1.0f, 1.0f, 1.0f, 1.0f );
				material.setEmission( 0.0f, 0.0f, 0.0f, 1.0f );
				material.setShininess( 1.0f );
				nave.getApariencia().setMaterial( material );
				UnidadTextura unidadesTextura[] = new UnidadTextura[3];
				AtributosTextura atributosTextura;
				unidadesTextura[0] = new UnidadTextura();
				unidadesTextura[0].setTextura( new Textura(
						gl, MinFilter.TRILINEAR, MagFilter.LINEAR, Textura.Format.RGB,
						Imagen.cargarToBufferedImage( "resources/obj3d/nave05/t01.jpg" ), true )
				);
				unidadesTextura[1] = new UnidadTextura();
				unidadesTextura[1].setTextura( new Textura(
						gl, MinFilter.TRILINEAR, MagFilter.LINEAR, Textura.Format.LUMINANCE,
						Imagen.cargarToBufferedImage( "resources/obj3d/nave05/t02.jpg" ), true )
				);
				atributosTextura = new AtributosTextura();
				atributosTextura.setMode( AtributosTextura.Mode.COMBINE );
				atributosTextura.setCombineRgbMode( AtributosTextura.CombineMode.SUBTRACT );
				atributosTextura.setCombineRgbSource0( AtributosTextura.CombineSrc.PREVIOUS,
						AtributosTextura.CombineOperand.SRC_COLOR );
				atributosTextura.setCombineRgbSource1( AtributosTextura.CombineSrc.TEXTURE,
						AtributosTextura.CombineOperand.SRC_COLOR );
				unidadesTextura[1].setAtributosTextura( atributosTextura );

				unidadesTextura[2] = new UnidadTextura();
				unidadesTextura[2].setTextura(
					new Textura( gl, Textura.Format.RGB, Imagen
						.cargarToBufferedImage( "resources/texturas/reflect.jpg" ), true ) );
				atributosTextura = new AtributosTextura();
				atributosTextura.setMode( AtributosTextura.Mode.COMBINE );
				atributosTextura.setCombineRgbMode( AtributosTextura.CombineMode.ADD_SIGNED );
				atributosTextura.setCombineRgbSource0( AtributosTextura.CombineSrc.PREVIOUS,
						AtributosTextura.CombineOperand.SRC_COLOR );
				atributosTextura.setCombineRgbSource1( AtributosTextura.CombineSrc.TEXTURE,
						AtributosTextura.CombineOperand.SRC_COLOR );
				unidadesTextura[2].setAtributosTextura( atributosTextura );
				unidadesTextura[2].setGenCoordTextura( new GenCoordTextura( GenCoordTextura.Mode.REFLECTION_MAP,
						GenCoordTextura.Coordinates.TEXTURE_COORDINATE_2 ) );

				nave.getApariencia().setUnidadesTextura( unidadesTextura );

				String extensions = gl.glGetString( GL.GL_EXTENSIONS );
				if( extensions.indexOf( "GL_ARB_vertex_shader" ) != -1 ){
					Shader shader = new Shader( gl, "resources/shaders/reflexion.vert",
							"resources/shaders/reflexion.frag" );
					shader.addUniform( gl, "difuseMap", 0 );
					shader.addUniform( gl, "reflexionMap", 1 );
					shader.addUniform( gl, "reflexionEnv", 2 );

					nave.getApariencia().setShader( shader );
				}
//				nave.getApariencia().setAtributosTransparencia(
//					//new AtributosTransparencia( Equation.ADD, SrcFunc.ALPHA_SATURATE, DstFunc.ONE )
//					new AtributosTransparencia( Equation.ADD, SrcFunc.SRC_ALPHA, DstFunc.ONE_MINUS_SRC_ALPHA )
//				);

				childs = new Grupo();
				childs.add( new NodoCompartido( nave ) );

				mtf = new Matrix4f();
				mtf.rotY( (float)Math.PI );
				mtf.setTranslation( new Vector3f( -0.60f, -0.075f, 0.0f ) );
				childs.add( new NodoTransformador( mtf, humoNave ) );

				nave1 = new Instancia3D( (short)0, childs );
				nave1.reset();
				gestorDeParticulas.add( nave1.getModificador() );

			}catch( FileNotFoundException e ){
				e.printStackTrace();
			}catch( ObjLoader.ParsingErrorException e ){
				e.printStackTrace();
			}

			gl.glEnable( GL.GL_DEPTH_TEST );
			gl.glDepthFunc( GL.GL_LESS );

			gl.glDisable( GLLightingFunc.GL_LIGHTING );
			gl.glEnable(  GLLightingFunc.GL_LIGHT0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
					new float[] { 0.0f, 0.0f, 10.0f, 1.0f }, 0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,
					new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0 );
			gl.glLightfv( GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR,
					new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0 );
			gl.glEnable( GL.GL_CULL_FACE );
			
			if( gl.isExtensionAvailable( "GL_ARB_multisample" ) ){
				System.out.println(  "GL_ARB_multisample Available" );
				gl.glEnable( GL.GL_MULTISAMPLE );
				
				gl.glEnable( GL2ES1.GL_POINT_SMOOTH );
				gl.glHint( GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_NICEST );
				gl.glEnable( GL.GL_LINE_SMOOTH );
				gl.glHint( GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST );
				gl.glEnable( GL2GL3.GL_POLYGON_SMOOTH );
				gl.glHint( GL2GL3.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST );
			}
			if( gl.isExtensionAvailable( "GL_EXT_framebuffer_multisample" ) ){
				System.out.println(  "GL_EXT_framebuffer_multisample Available" );
			}
			if( gl.isExtensionAvailable( "GL_EXT_framebuffer_blit" ) ){
				System.out.println(  "GL_EXT_framebuffer_blit Available" );
			}
			int[] buf = new int[1];
			gl.glGetIntegerv( GL.GL_SAMPLE_BUFFERS, buf, 0 );
			System.out.println( "number of sample buffers is " + buf[0] );
			gl.glGetIntegerv( GL.GL_SAMPLES, buf, 0 );
			System.out.println( "number of samples is " + buf[0] );
			tActual = System.nanoTime();
		}

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
			gl.glOrtho( 0.0, proporcionesPantalla, 0.0, 1.0, 0, 1 );

			apFondo.usar( gl );
			gl.glClear( GL.GL_DEPTH_BUFFER_BIT );

			float s1 = 0.75f;
			float s2 = proporcionesPantalla * proporcionesFondo + s1;

			gl.glDepthMask( false );
			gl.glBegin( GL2.GL_QUADS );
			gl.glTexCoord2f( s1, 0 );
			gl.glVertex3f( 0, 0, 0 );
			gl.glTexCoord2f( s2, 0 );
			gl.glVertex3f( proporcionesPantalla, 0, 0 );
			gl.glTexCoord2f( s2, 1 );
			gl.glVertex3f( proporcionesPantalla, 1, 0 );
			gl.glTexCoord2f( s1, 1 );
			gl.glVertex3f( 0, 1, 0 );
			gl.glEnd();
			gl.glDepthMask( true );

			gl.glPopMatrix();
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			orbitBehavior.setLookAt( glu );
			MatrixSingleton.loadModelViewMatrix();

			for( Modificador modificador: gestorDeParticulas )
				modificador.modificar( incT );
	
			nave1.draw( gl );

			gl.glFlush();
		}

		public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ){
			GL2 gl = drawable.getGL().getGL2();
			gl.glViewport( 0, 0, w, h );
			proporcionesPantalla = (float)w / h;
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			double near = 0.01;
			double far = 100.0;
			double a1 = 45.0; // angulo en grados
			double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
			double d = near / Math.sqrt( ( 1 / Math.pow( Math.sin( a2 ), 2 ) ) - 1 );

			/*
			 * // formato zoom/recorte double ratio = (double) w / h; if( ratio > 1 ) gl.glFrustum(-d, d, -d / ratio, d
			 * / ratio, near, far); else gl.glFrustum(-d * ratio, d * ratio, -d, d, near, far); //
			 */
			// *
			// formato panoramico Horizontal/Vertical centrado
			double ratio = (double)w / h;
			if( ratio < 1 )
				gl.glFrustum( -d, d, -d / ratio, d / ratio, near, far );
			else
				gl.glFrustum( -d * ratio, d * ratio, -d, d, near, far );
			// */

			/*
			 * // formato recorte/panoramico derecha gl.glFrustum(-d,((2.0*w)/h -1.0)*d, -d, d, near, far); //
			 */

			MatrixSingleton.loadProjectionMatrix();
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
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
		boolean preferMultiSampling = true;
		
		JFrame frame = new JFrame( "Prueba Shaders" );
		frame.getContentPane().setBackground( Color.BLACK );
		frame.getContentPane().setPreferredSize( new Dimension( 640, 480 ) );
		frame.pack();
		frame.setLocationRelativeTo( null ); // center
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		DisplayMode tDesktopDisplayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		
		GLCapabilities tGLCapabilities = new GLCapabilities( GLProfile.getDefault() );
		//enable/configure multisampling support ...
		if( preferMultiSampling ){
			tGLCapabilities.setSampleBuffers( true );
			tGLCapabilities.setNumSamples( 8 );
			tGLCapabilities.setAccumAlphaBits( 16 );
			tGLCapabilities.setAccumBlueBits( 16 );
			tGLCapabilities.setAccumGreenBits( 16 );
			tGLCapabilities.setAccumRedBits( 16 );
			tGLCapabilities.setAlphaBits(8);
		}
        /*
        //test method for JOGL2 mutisampling bug: http://jogamp.org/bugzilla/show_bug.cgi?id=410
		GLCanvas canvas = new GLCanvas( tGLCapabilities, new GLCapabilitiesChooser(){

			@Override
			@SuppressWarnings( "rawtypes" )
			public int chooseCapabilities( CapabilitiesImmutable cpblts, List list, int i ){
				@SuppressWarnings( "unchecked" )
				List<CapabilitiesImmutable> cpbltss = list;
				for( CapabilitiesImmutable caps: cpbltss ){
					System.out.println( caps );
				}
				System.out.println( "recommended:" );
				System.out.println( cpblts );
				return i;
			}
		}, null, null );
		/*/
		GLCanvas canvas = new GLCanvas(tGLCapabilities);
		//*/
		
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
