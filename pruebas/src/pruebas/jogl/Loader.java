/* 
 * Loader.java
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

import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.ObjLoader;
import org.sam.jogl.Textura;
import org.sam.jspacewars.serialization.GrafoEscenaConverters;
import org.sam.jspacewars.serialization.Ignorado;
import org.sam.util.Imagen;
import org.sam.util.ModificableBoolean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Loader implements GLEventListener{

	private static class Properties{
		
		private Properties(){}

		private static final ResourceBundle BUNDLE = ResourceBundle.getBundle( "Loader" );

		private static String getProperty( String key ){
			try{
				return BUNDLE.getString( key );
			}catch( MissingResourceException e ){
				return '!' + key + '!';
			}
		}

		static final String fondo = getProperty( "Paths.fondo" );
		static final String instancias = getProperty( "Paths.instancias" );
	}

	public static class Data{
		GLContext context;
		Apariencia apFondo;
		Instancia3D[] instancias;
	}

	// Indices rgb
	@SuppressWarnings( "unused" )
	private static final transient int r = 0, g = 1, b = 2;

	private final transient Data data;
	private final transient ModificableBoolean loading;
	private final transient float[] color1, color2;

	Loader( Data data, ModificableBoolean loading ){
		this.data = data;
		this.loading = loading;
		color1 = new float[] { 1.0f, 0.0f, 0.0f };
		color2 = new float[] { 1.0f, 0.0f, 0.0f };
	}

	private transient int ciclo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable )
	 */
	@Override
	public void init( GLAutoDrawable drawable ){
		data.context = drawable.getContext();

		GL2 gl = drawable.getGL().getGL2();
		gl.glDepthMask( false );
		gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
		gl.glClearColor( 0.1f, 0.1f, 0.1f, 0.0f );
		ciclo = 0;
	}

	private static final transient int MAX_CICLOS = 1 + 54;
	private static final transient float inc1 = 1.0f / ( MAX_CICLOS / 2 );
	private static final transient float inc2 = 1.0f / MAX_CICLOS;

	private transient float proporcionesPantalla;

	private transient ObjectInputStream in;
	private transient Collection<Instancia3D> intanciasCollection;
	private transient boolean eof;

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.media.opengl.GLEventListener#display(javax.media.opengl. GLAutoDrawable)
	 */
	@Override
	public void display( GLAutoDrawable drawable ){
		GL2 gl = drawable.getGL().getGL2();
		if( ciclo == 0 ){

		}else if( data.apFondo == null ){
			BufferedImage img = Imagen.cargarToBufferedImage( Properties.fondo );
			data.apFondo = new Apariencia();

			data.apFondo.setTextura( new Textura( gl, Textura.Format.RGB, img, true ) );
			data.apFondo.setAtributosTextura( new AtributosTextura() );
			data.apFondo.getAtributosTextura().setMode( AtributosTextura.Mode.REPLACE );
		}else{
			try{
				if( in == null ){
					XStream xStream = new XStream( new DomDriver() );
					xStream.setMode( XStream.XPATH_RELATIVE_REFERENCES );
					GrafoEscenaConverters.register( xStream, true );

					xStream.registerConverter( new Ignorado() );
					xStream.alias( "NaveUsuario", Ignorado.class );
					xStream.alias( "DisparoLineal", Ignorado.class );
					xStream.alias( "DisparoInterpolado", Ignorado.class );
					xStream.alias( "Misil", Ignorado.class );
					xStream.alias( "NaveEnemiga", Ignorado.class );

					intanciasCollection = new LinkedList<Instancia3D>();
					in = xStream.createObjectInputStream( new FileReader( Properties.instancias ) );
				}
				if( !eof )
					try{
						Object object;
						do{
							object = in.readObject();
							if( object != null )
								intanciasCollection.add( (Instancia3D)object );
						}while( object == null );
					}catch( ClassNotFoundException e ){
						e.printStackTrace();
					}catch( EOFException eofEx ){
						eof = true;
						in.close();
					}
			}catch( FileNotFoundException e ){
				e.printStackTrace();
			}catch( IOException e ){
				e.printStackTrace();
			}catch( ObjLoader.ParsingErrorException e ){
				e.printStackTrace();
			}catch( GLException e ){
				e.printStackTrace();
			}
		}

		color1[g] += inc1;
		if( color1[g] > 1.0f ){
			color1[g] = 1.0f;
			color1[r] -= inc1;
			if( color1[r] < 0 )
				color1[r] = 0.0f;
		}
		color2[g] += inc2;
		if( color2[g] > 1.0f )
			color2[g] = 1.0f;

		gl.glClear( GL.GL_COLOR_BUFFER_BIT );

		ciclo++;

		gl.glBegin( GL2.GL_QUADS );
		gl.glColor3fv( color2, 0 );
		gl.glVertex3f( 0, 0, 0 );
		gl.glColor3fv( color1, 0 );
		gl.glVertex3f( proporcionesPantalla * ciclo / MAX_CICLOS, 0, 0 );
		gl.glColor3fv( color1, 0 );
		gl.glVertex3f( proporcionesPantalla * ciclo / MAX_CICLOS, 1, 0 );
		gl.glColor3fv( color2, 0 );
		gl.glVertex3f( 0, 1, 0 );
		gl.glEnd();
		gl.glFlush();

		if( eof && intanciasCollection != null ){
			synchronized( loading ){
				data.instancias = intanciasCollection.toArray( new Instancia3D[intanciasCollection.size()] );
				intanciasCollection = null;
				loading.setFalse();
				loading.notifyAll();
			}
			// drawable.removeGLEventListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.media.opengl.GLEventListener#reshape(javax.media.opengl. GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ){
		GL2 gl = drawable.getGL().getGL2();
		gl.glViewport( 0, 0, width, height );
		proporcionesPantalla = (float)width / height;

		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glLoadIdentity();
		gl.glOrtho( 0.0, proporcionesPantalla, 0.0, 1.0, 0, 1 );

		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glLoadIdentity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose( GLAutoDrawable drawable ){
	}
}
