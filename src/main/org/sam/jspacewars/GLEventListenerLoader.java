/* 
 * GLEventListenerLoader.java
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
package org.sam.jspacewars;

import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.fenggui.binding.clipboard.IClipboard;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.CursorFactory;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.binding.render.jogl.JOGLOpenGL;
import org.fenggui.binding.render.jogl.JOGLTexture;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.ObjLoader;
import org.sam.jogl.fondos.CieloEstrellado;
import org.sam.jspacewars.cliente.MarcoDeIndicadores;
import org.sam.jspacewars.serialization.GrafoEscenaConverters;
import org.sam.jspacewars.serialization.Ignorado;
import org.sam.util.ModificableBoolean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author Samuel Alfaro
 */
class GLEventListenerLoader implements GLEventListener {
	@SuppressWarnings("unused")
	// Indices rgb
	private final static transient int r = 0, g = 1, b = 2;
	
	/**
	 * Implementación de la clase abstracta
	 * {@link org.fenggui.binding.render.Binding Binding}.
	 * <p>
	 * Esta clase se limita a encapsular una instancia del interface
	 * {@link javax.media.opengl.GL GL}, para poder cargar y almacenar en la
	 * memoria de video las texturas empleadas por el GUI.
	 * </p>
	 */
	private static class LoaderBinding extends Binding{

		private final transient GL gl;

		LoaderBinding( GL gl ){
			super( new JOGLOpenGL( gl.getGL2() ) );
			this.gl = gl;
		}

		/*
		 * (non-Javadoc)
		 * @see org.fenggui.binding.render.Binding#getCanvasHeight()
		 */
		@Override
		public int getCanvasHeight(){
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * @see org.fenggui.binding.render.Binding#getCanvasWidth()
		 */
		@Override
		public int getCanvasWidth(){
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * @see org.fenggui.binding.render.Binding#getClipboard()
		 */
		@Override
		public IClipboard getClipboard(){
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.fenggui.binding.render.Binding#getCursorFactory()
		 */
		@Override
		public CursorFactory getCursorFactory(){
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see joglui.binding.Binding#getTexture(java.lang.String)
		 */
		@Override
		public ITexture getTexture( InputStream stream ) throws IOException{
//			if( !this.getOpenGL().isOpenGLThread() )
//				throw new RuntimeException( "getTexture called not in OpenGL Thread!" );
			return JOGLTexture.uploadTextureToVideoRAM( gl, ImageIO.read( stream ) );
		}

		/*
		 * (non-Javadoc)
		 * @see joglui.binding.Binding#getTexture(java.awt.image.BufferedImage)
		 */
		@Override
		public ITexture getTexture( BufferedImage bi ){
//			if( !this.getOpenGL().isOpenGLThread() )
//				throw new RuntimeException( "getTexture called not in OpenGL Thread!" );
			return JOGLTexture.uploadTextureToVideoRAM( gl, bi );
		}
	}

	private final DataGame dataGame;
	private final ModificableBoolean loading;

	private transient float proporcionesPantalla;
	private final transient float[] color1, color2;

	GLEventListenerLoader( DataGame dataGame, ModificableBoolean loading ){
		this.dataGame = dataGame;
		this.loading = loading;
		color1 = new float[] { 1.0f, 0.0f, 0.0f };
		color2 = new float[] { 1.0f, 0.0f, 0.0f };
	}

	private static final int MAX_CICLOS = 46;
	private static final float inc1 = 1.0f / ( MAX_CICLOS / 2 );
	private static final float inc2 = 1.0f / MAX_CICLOS;
	private transient int ciclo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	public void init( final GLAutoDrawable drawable ){
		dataGame.setGLContext( drawable.getContext() );
		new LoaderBinding( drawable.getGL() );

		GL2 gl = drawable.getGL().getGL2();
		gl.glDepthMask( false );
		gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
		ciclo = 0;
	}

	private transient ObjectInputStream in;
	private transient boolean eof;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		if( ciclo == 0 ){

		}else if( !MyGameMenuButton.hasPrototipo() ){
			TextRenderers.init();
			Pixmap defaultState = null;
			Pixmap hoverState = null;
			Pixmap pressedState = null;
			try{
				defaultState = new Pixmap(Binding.getInstance().getTexture("resources/img/botones/boton0.jpg"));
				hoverState = new Pixmap(Binding.getInstance().getTexture("resources/img/botones/boton1.jpg"));
				pressedState = new Pixmap(Binding.getInstance().getTexture("resources/img/botones/boton2.jpg"));
			}catch( IOException ignorada ){
			}
			MyGameMenuButton.setPrototipo(new MyGameMenuButton("Prototipo", defaultState, hoverState, pressedState));
		}else if( dataGame.getGui() == null ){
			dataGame.setGui( MarcoDeIndicadores.getMarco( 0 ) );
		}else if( !dataGame.getGui().isLoadComplete() ){
			dataGame.getGui().loadTexturas( gl );
		}else if( dataGame.getFondo() == null ){
			dataGame.setFondo(
				new CieloEstrellado(
					gl,
					"resources/texturas/cielo1.jpg",
					"resources/texturas/spark.jpg"
				)
			);
		}else{
			try{
				if( in == null ){
					XStream xStream = new XStream( new DomDriver() );
					xStream.setMode( XStream.XPATH_ABSOLUTE_REFERENCES );
					GrafoEscenaConverters.register( xStream );
					GrafoEscenaConverters.setReusingReferenceByXPathMarshallingStrategy( xStream );

					xStream.registerConverter( new Ignorado() );
					xStream.alias( "NaveUsuario", Ignorado.class );
					xStream.alias( "DisparoLineal", Ignorado.class );
					xStream.alias( "DisparoInterpolado", Ignorado.class );
					xStream.alias( "Misil", Ignorado.class );
					xStream.alias( "NaveEnemiga", Ignorado.class );

					in = xStream.createObjectInputStream(
							new FileReader( "resources/elementos-instancias3D-stream-sh.xml" )
					);
				}
				if( !eof )
					try{
						Object object;
						do{
							object = in.readObject();
							if( object != null )
								dataGame.getCache().addPrototipo( (Instancia3D)object );
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

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		ciclo++;

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3fv(color2, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glColor3fv(color1, 0);
		gl.glVertex3f(proporcionesPantalla * ciclo / MAX_CICLOS, 0, 0);
		gl.glColor3fv(color1, 0);
		gl.glVertex3f(proporcionesPantalla * ciclo / MAX_CICLOS, 1, 0);
		gl.glColor3fv(color2, 0);
		gl.glVertex3f(0, 1, 0);
		gl.glEnd();
		gl.glFlush();

		if( eof ){
			synchronized( loading ){
				loading.setFalse();
				loading.notifyAll();
			}
			drawable.removeGLEventListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable
	 * , int, int, int, int)
	 */
	public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ){
		GL2 gl = drawable.getGL().getGL2();
		gl.glViewport( 0, 0, w, h );
		proporcionesPantalla = (float)w / h;

		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glLoadIdentity();
		gl.glOrtho( 0.0, proporcionesPantalla, 0.0, 1.0, 0, 1 );

		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glLoadIdentity();
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose( GLAutoDrawable drawable ){
		
	}
}
