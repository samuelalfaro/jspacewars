/* 
 * Prueba002_VBO.java
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
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.fixedfunc.GLPointerFunc;
import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Prueba002_VBO{

	private static class VBO{
		int idBuff;
		ByteBuffer byteBuffer;
		FloatBuffer floatBuffer;

		int count = 0;
		
		VBO(){}

		FloatBuffer get( GL gl, int tam ){
			count++;
			if( byteBuffer == null ){
				int[] tmp = new int[1];
				gl.glGenBuffers( 1, tmp, 0 );
				idBuff = tmp[0];
				gl.glBindBuffer( GL.GL_ARRAY_BUFFER, idBuff );
				gl.glBufferData( GL.GL_ARRAY_BUFFER, tam * Buffers.SIZEOF_FLOAT, null, GL2ES2.GL_STREAM_DRAW );
				byteBuffer = gl.glMapBuffer( GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY );
				if( byteBuffer == null ){
					throw new RuntimeException( "Unable to map vertex buffer object" );
				}
				floatBuffer = byteBuffer.order( ByteOrder.nativeOrder() ).asFloatBuffer();
				System.err.println( "new " + count + ":" + byteBuffer );
			}else{
				ByteBuffer tmp;
				gl.glBindBuffer( GL.GL_ARRAY_BUFFER, idBuff );
				tmp = gl.glMapBuffer( GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY );
				if( tmp == null ){
					throw new RuntimeException( "Unable to map vertex buffer object" );
				}
				if( tmp != byteBuffer ){
					byteBuffer = tmp;
					floatBuffer = byteBuffer.order( ByteOrder.nativeOrder() ).asFloatBuffer();
					System.err.println( "cambio " + count + ":\n" + byteBuffer );
				}
			}
			gl.glUnmapBuffer( GL.GL_ARRAY_BUFFER );
			gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0 );
			return floatBuffer;
		}
	}

	private static class Renderer implements GLEventListener{

		private Texture texture1;

		private VBO posVBO;
		private FloatBuffer pos;
		private VBO colVBO;
		private FloatBuffer col;
		private VBO texVBO;
		private FloatBuffer tex;

		public Renderer(){
			posVBO = new VBO();
			colVBO = new VBO();
			texVBO = new VBO();
		}

		public void init( GLAutoDrawable drawable ){
			GL2 gl = drawable.getGL().getGL2();
			gl.glClearColor( 0.0f, 0.25f, 0.25f, 0.0f );

			try{
				texture1 = TextureIO.newTexture( new File( "resources/fondo.png" ), true );
			}catch( GLException e ){
				e.printStackTrace();
			}catch( IOException e ){
				e.printStackTrace();
			}

			gl.glEnable( GL.GL_DEPTH_TEST );
			gl.glDepthFunc( GL.GL_LESS );
			gl.glEnable( GL.GL_TEXTURE_2D );
			gl.glTexEnvi( GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE );
			gl.glTexParameterf( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE );
			gl.glTexParameterf( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE );
			gl.glEnable( GL.GL_BLEND );
			gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA );
		}

		private double alfa = 0.0f;

		public void display( GLAutoDrawable drawable ){

			GL2 gl = drawable.getGL().getGL2();

			float cosA = (float)( Math.cos( alfa ) * 0.5 );
			float senA = (float)( Math.sin( alfa ) * 0.5 );
			float p1 = cosA + senA;
			float p2 = cosA - senA;

			alfa += 0.01;

			pos = posVBO.get( gl, 4 * 3 );
			pos.rewind();
			pos.put( -p2 );
			pos.put( -p1 );
			pos.put( 0.0f );
			pos.put( +p1 );
			pos.put( -p2 );
			pos.put( 0.0f );
			pos.put( +p2 );
			pos.put( +p1 );
			pos.put( 0.0f );
			pos.put( -p1 );
			pos.put( +p2 );
			pos.put( 0.0f );
			pos.rewind();

			col = colVBO.get( gl, 4 * 4 );
			col.rewind();
			// col.put(1.0f);col.put(1.0f);col.put(1.0f);col.put(1.0f);
			// col.put(1.0f);col.put(1.0f);col.put(1.0f);col.put(1.0f);
			// col.put(1.0f);col.put(1.0f);col.put(1.0f);col.put(1.0f);
			// col.put(1.0f);col.put(1.0f);col.put(1.0f);col.put(1.0f);
			col.put( 0.5f + cosA );
			col.put( 0.5f + senA );
			col.put( 0.5f + senA );
			col.put( 0.25f );
			col.put( 0.5f + senA );
			col.put( 0.5f + senA );
			col.put( 0.5f + senA );
			col.put( 0.0f );
			col.put( 0.5f + senA );
			col.put( 0.5f + cosA );
			col.put( 0.5f + senA );
			col.put( 0.25f );
			col.put( 0.5f + senA );
			col.put( 0.5f + senA );
			col.put( 0.5f + cosA );
			col.put( 1.0f );
			col.rewind();

			tex = texVBO.get( gl, 4 * 2 );
			tex.rewind();
			tex.put( cosA );
			tex.put( senA );
			tex.put( cosA );
			tex.put( 1.0f + cosA );
			tex.put( 1.0f + senA );
			tex.put( 1.0f + cosA );
			tex.put( 1.0f + senA );
			tex.put( senA );
			tex.rewind();

			gl.glClear( GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT );

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();

			texture1.bind();

			gl.glEnableClientState( GLPointerFunc.GL_VERTEX_ARRAY );
			gl.glEnableClientState( GLPointerFunc.GL_COLOR_ARRAY );
			gl.glEnableClientState( GLPointerFunc.GL_TEXTURE_COORD_ARRAY );

			gl.glBindBuffer( GL.GL_ARRAY_BUFFER, posVBO.idBuff );
			gl.glVertexPointer( 3, GL.GL_FLOAT, 0, 0 );
			// gl.glVertexPointer(3, GL.GL_FLOAT, 0, posVBO.floatBuffer);

			gl.glBindBuffer( GL.GL_ARRAY_BUFFER, colVBO.idBuff );
			gl.glColorPointer( 4, GL.GL_FLOAT, 0, 0 );
			// gl.glColorPointer(4, GL.GL_FLOAT, 0, colVBO.floatBuffer);

			gl.glBindBuffer( GL.GL_ARRAY_BUFFER, texVBO.idBuff );
			gl.glTexCoordPointer( 2, GL.GL_FLOAT, 0, 0 );
			// gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texVBO.floatBuffer);

			gl.glDrawArrays( GL2.GL_QUADS, 0, 4 );

			gl.glDisableClientState( GLPointerFunc.GL_TEXTURE_COORD_ARRAY );
			gl.glDisableClientState( GLPointerFunc.GL_COLOR_ARRAY );
			gl.glDisableClientState( GLPointerFunc.GL_VERTEX_ARRAY );

			gl.glFlush();

		}

		public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ){
			GL2 gl = drawable.getGL().getGL2();
			gl.glViewport( 0, 0, w, h );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			gl.glOrtho( -(double)w / h, (double)w / h, 1, -1, -1, 1 );
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable drawable ){
		}
	}

	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba VBO" );
		frame.getContentPane().setBackground( Color.BLACK );
		frame.getContentPane().setPreferredSize( new Dimension( 500, 500 ) );
		frame.pack();
		frame.setLocationRelativeTo( null ); // center
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		GLCanvas canvas = new GLCanvas();
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
