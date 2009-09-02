/* 
 * GLEventListenerLoader.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.media.opengl.*;

import org.fenggui.binding.clipboard.IClipboard;
import org.fenggui.binding.render.*;
import org.fenggui.binding.render.jogl.JOGLOpenGL;
import org.fenggui.binding.render.jogl.JOGLTexture;
import org.sam.jogl.*;
import org.sam.jogl.fondos.CieloEstrellado;
import org.sam.util.ModificableBoolean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author Samuel Alfaro
 */
class GLEventListenerLoader implements GLEventListener {

	/**
	 * Implementación de la clase abstracta
	 * {@link org.fenggui.binding.render.Binding Binding}.
	 * <p>
	 * Esta clase limita a encapsular una instancia del interface
	 * {@link javax.media.opengl.GL GL}, para poder cargar y almacenar en la
	 * memoria de video las texturas empleadas por el GUI.
	 * </p>
	 */
	private static class LoaderBinding extends Binding {

		private final transient GL gl;

		LoaderBinding(GL gl) {
			super(new JOGLOpenGL(gl));
			this.gl = gl;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.fenggui.binding.render.Binding#getCanvasHeight()
		 */
		@Override
		public int getCanvasHeight() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.fenggui.binding.render.Binding#getCanvasWidth()
		 */
		@Override
		public int getCanvasWidth() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.fenggui.binding.render.Binding#getClipboard()
		 */
		@Override
		public IClipboard getClipboard() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.fenggui.binding.render.Binding#getCursorFactory()
		 */
		@Override
		public CursorFactory getCursorFactory() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see joglui.binding.Binding#getTexture(java.lang.String)
		 */
		@Override
		public ITexture getTexture(InputStream stream) throws IOException {
			if( !this.getOpenGL().isOpenGLThread() )
				throw new RuntimeException("getTexture called not in OpenGL Thread!");
			return JOGLTexture.uploadTextureToVideoRAM(gl, ImageIO.read(stream));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see joglui.binding.Binding#getTexture(java.awt.image.BufferedImage)
		 */
		@Override
		public ITexture getTexture(BufferedImage bi) {
			if( !this.getOpenGL().isOpenGLThread() )
				throw new RuntimeException("getTexture called not in OpenGL Thread!");
			return JOGLTexture.uploadTextureToVideoRAM(gl, bi);
		}
	}

	private final DataGame dataGame;
	private final ModificableBoolean loading;

	private transient float proporcionesPantalla;
	private final transient float[] color1, color2;
	@SuppressWarnings("unused")
	private final transient int r = 0, g = 1, b = 2;

	GLEventListenerLoader(DataGame dataGame, ModificableBoolean loading) {
		this.dataGame = dataGame;
		this.loading = loading;
		color1 = new float[] { 1.0f, 0.0f, 0.0f };
		color2 = new float[] { 1.0f, 0.0f, 0.0f };
	}

	private transient int ciclo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	public void init(final GLAutoDrawable drawable) {
		dataGame.setGLContext(drawable.getContext());
		new LoaderBinding(drawable.getGL());

		GL gl = drawable.getGL();
		gl.glDepthMask(false);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		ciclo = 0;
	}

	private static final int MAX_CICLOS = 46;
	private static final float inc1 = 1.0f / (MAX_CICLOS / 2);
	private static final float inc2 = 1.0f / MAX_CICLOS;

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
		GL gl = drawable.getGL();
		if( ciclo == 0 ){

		}else if( !MyGameMenuButton.hasPrototipo() ){
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
		}else if( dataGame.getFondo() == null ){
			dataGame.setFondo(new CieloEstrellado(gl, "resources/texturas/cielo1.jpg", "resources/texturas/spark.jpg"));
		}else{
			try{
				if( in == null ){
					XStream xStream = new XStream(new DomDriver());
					xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
					GrafoEscenaConverters.register(xStream);
					GrafoEscenaConverters.setReusingReferenceByXPathMarshallingStrategy(xStream);

					in = xStream.createObjectInputStream(new FileReader("instancias3D-stream.xml"));
				}
				if( !eof )
					try{
						dataGame.getCache().addPrototipo((Instancia3D) in.readObject());
					}catch( ClassNotFoundException e ){
						e.printStackTrace();
					}catch( EOFException eofEx ){
						eof = true;
						in.close();
					}
				// Instancia3D[] instancias = (Instancia3D[])xStream.fromXML(new
				// FileReader("instancias3D.xml"));
				// StringWriter writer = new StringWriter();
				// try{
				// ObjectOutputStream out =
				// xStream.createObjectOutputStream(writer);
				// for(Instancia3D instancia: instancias)
				// out.writeObject(instancia);
				// }catch( IOException e ){
				// e.printStackTrace();
				// }
				// System.out.println(writer.toString());
				// for(Instancia3D instancia: instancias)
				// cache.addPrototipo( instancia );
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

		gl.glBegin(GL.GL_QUADS);
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
	 * javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.
	 * GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable
	 * , int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		GL gl = drawable.getGL();
		gl.glViewport(0, 0, w, h);
		proporcionesPantalla = (float) w / h;

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}