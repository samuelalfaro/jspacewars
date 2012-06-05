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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;

import org.sam.jogl.Hoja;
import org.sam.jogl.NodoTransformador;
import org.sam.jogl.gui.GLButton;
import org.sam.jogl.gui.GLComponent;
import org.sam.jogl.gui.GLContainer;

import com.jogamp.opengl.util.Animator;

public class PruebaBasicaBotones2{
	
	private static final int VIRTUAL_AREA_WIDTH  = 1024;
	private static final int VIRTUAL_AREA_HEIGHT =  768;
	
	static int AREA_WIDTH = 1;
	static int AREA_HEIGHT = 1;
	
	static int VIRTUAL_VIEWPORT_X = 0; 
	static int VIRTUAL_VIEWPORT_Y = 0;
	static int VIRTUAL_VIEWPORT_WIDTH = 1;
	static int VIRTUAL_VIEWPORT_HEIGHT = 1;
	
	static void toVirtualPosition( Point point, Point2f virtualPoint ){
		virtualPoint.x = VIRTUAL_AREA_WIDTH * ( point.x - VIRTUAL_VIEWPORT_X ) / VIRTUAL_VIEWPORT_WIDTH;
		virtualPoint.y = VIRTUAL_AREA_HEIGHT * ( point.y - VIRTUAL_VIEWPORT_Y) / VIRTUAL_VIEWPORT_HEIGHT;
	}
	
	private static class GLGUI{
		
		private GLContainer container;
		
		GLGUI(){
		}
		
		public void setContentPane( GLContainer container ){
			this.container = container;
		}
		
		public GLContainer getContentPane(){
			return this.container;
		}
	}
	
	private static class Cursor extends NodoTransformador{

		public Cursor(){
			super(
					new Matrix4f(), 
					new Hoja(){
						public void draw( GL2 gl ){
							gl.glColor3f( 1.0f, 1.0f, 1.0f );
							gl.glBegin( GL.GL_LINES );
								gl.glVertex2f( -10,   0 );
								gl.glVertex2f(  10,   0 );
								gl.glVertex2f(   0, -10 );
								gl.glVertex2f(   0,  10 );
							gl.glEnd();
						}

						@Override
						public Hoja clone(){
							return null;
						}
					}
			);
			this.getTransform().setIdentity();
		}
	}
	
	private static class GUIListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		
		private static void setPosition( Matrix4f t, Point2f p ){
			t.m03 = p.x;
			t.m13 = p.y;
		}
		
		private final Point2f virtualPoint;
		Matrix4f    cursorTransform;
		GLContainer container;
		
		GUIListener( GLGUI gui ){
			virtualPoint = new Point2f();
			container = gui.getContentPane();
		}
		
		public void setCursor( NodoTransformador cursor ){
			this.cursorTransform = cursor.getTransform();
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
			toVirtualPosition( e.getPoint(), virtualPoint );
			setPosition( cursorTransform, virtualPoint );
			container.checkCursorPosition( virtualPoint );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited( MouseEvent e ){
			toVirtualPosition( e.getPoint(), virtualPoint );
			setPosition( cursorTransform, virtualPoint );
			container.checkCursorPosition( virtualPoint );
			
//			Point    point = e.getPoint();
//			Matrix4f transform = cursor.getTransform();
//			transform.m03 = ( point.x - transform.m03 ) * 80 + point.x; 
//			transform.m13 = ( point.y - transform.m13 ) * 80 + point.y; 
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
			toVirtualPosition( e.getPoint(), virtualPoint );
			setPosition( cursorTransform, virtualPoint );
			container.checkCursorPosition( virtualPoint );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved( MouseEvent e ){
			toVirtualPosition( e.getPoint(), virtualPoint );
			setPosition( cursorTransform, virtualPoint );
			container.checkCursorPosition( virtualPoint );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
		 */
		@Override
		public void mouseWheelMoved( MouseWheelEvent e ){
		}
	}

	private static class GUIRenderer implements GLEventListener{
		
		final GLGUI gui;
		
		public GUIRenderer( GLGUI gui ){
			this.gui = gui;
		}
		
		public void init( GLAutoDrawable glDrawable ){
		}

		public void display( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();
			
			gl.glViewport( VIRTUAL_VIEWPORT_X, VIRTUAL_VIEWPORT_Y, VIRTUAL_VIEWPORT_WIDTH, VIRTUAL_VIEWPORT_HEIGHT );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			gl.glOrtho( 0, VIRTUAL_AREA_WIDTH, VIRTUAL_AREA_HEIGHT, 0, -1, 1 );
			
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			
			gl.glClear( GL.GL_COLOR_BUFFER_BIT );
			
			gui.getContentPane().draw( gl );
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
		
		final Cursor cursor;
		
		public CursorRenderer( Cursor cursor ){
			this.cursor = cursor;
		}
		
		public void init( GLAutoDrawable glDrawable ){
		}

		public void display( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();
			
			gl.glViewport( VIRTUAL_VIEWPORT_X, VIRTUAL_VIEWPORT_Y, VIRTUAL_VIEWPORT_WIDTH, VIRTUAL_VIEWPORT_HEIGHT );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			gl.glOrtho( 0, VIRTUAL_AREA_WIDTH, VIRTUAL_AREA_HEIGHT, 0, -1, 1 );
			
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			
			//cursor.draw( gl );
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
	
	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba botones" );
		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().setBackground( Color.BLACK );

//		GLCanvas canvas = new GLCanvas(){
//		    protected void processEvent(AWTEvent e) {
//		    	System.err.println( e );
//		    	super.processEvent( e );
//		    }
//		};
		
		GLCanvas canvas = new GLCanvas();
		
		GLGUI gui = new GLGUI();
		
		GLContainer container = new GLContainer();
		container.setBounds( 350, 350 );
		
		GLComponent button;
		
		button = new GLButton();
		button.setBounds( 50, 50, 200, 50 );
		container.add( button );
		
		button = new GLButton();
		button.setBounds( 50, 125, 200, 50 );
		container.add( button );
		
		button = new GLButton();
		button.setBounds( 50, 200, 200, 50 );
		container.add( button );
		
		button = new GLButton();
		button.setBounds( 50, 275, 200, 50 );
		container.add( button );
		
		container.setPosition( 
			( VIRTUAL_AREA_WIDTH - container.getWidth() ) /2,
			( VIRTUAL_AREA_HEIGHT - container.getHeight() ) /2
		);
		
		gui.setContentPane( container );
		
		canvas.addGLEventListener( new GUIRenderer( gui ) );
		
		
		Cursor cursor = new Cursor();
		
		canvas.addGLEventListener( new CursorRenderer( cursor ) );
		
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
		
		GUIListener guiListener = new GUIListener( gui );
		guiListener.setCursor( cursor );

		canvas.addMouseListener( guiListener );
		canvas.addMouseMotionListener( guiListener );
		canvas.addMouseWheelListener( guiListener );
		canvas.addKeyListener( new KeyAdapter(){} );
		
		canvas.requestFocusInWindow();
	}
}
