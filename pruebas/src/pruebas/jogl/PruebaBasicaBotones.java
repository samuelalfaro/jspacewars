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

import org.sam.jogl.Hoja;
import org.sam.jogl.NodoTransformador;
import org.sam.jogl.gui.GLButton;
import org.sam.jogl.gui.GLComponent;
import org.sam.jogl.gui.GLContainer;

import com.jogamp.opengl.util.Animator;

public class PruebaBasicaBotones{
	
	private static final int VIRTUAL_AREA_WIDTH  = 1024;
	private static final int VIRTUAL_AREA_HEIGHT =  768;
	
	static int AREA_WIDTH;
	static int AREA_HEIGHT;
	
	static int VIRTUAL_VIEWPORT_X; 
	static int VIRTUAL_VIEWPORT_Y;
	static int VIRTUAL_VIEWPORT_WIDTH;
	static int VIRTUAL_VIEWPORT_HEIGHT;
	
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
	
	// TODO ñapa
	private static class CointainerListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		
		private void toVirtualPosition( Point point ){
			virtualX = VIRTUAL_AREA_WIDTH * ( point.x - VIRTUAL_VIEWPORT_X ) / VIRTUAL_VIEWPORT_WIDTH;
			virtualY = VIRTUAL_AREA_HEIGHT * ( point.y - VIRTUAL_VIEWPORT_Y) / VIRTUAL_VIEWPORT_HEIGHT;
			
			if( container.contains( virtualX, virtualY ) ){
				container.setHovered( true );
				for( GLComponent c: container.childs() )
					c.setHovered( c.contains( virtualX, virtualY ) );
			}else{
				container.setHovered( false );
				for( GLComponent c: container.childs() )
					c.setHovered( false );
			}
		}
		
		final GLContainer container;
		private float virtualX;
		private float virtualY;
		
		CointainerListener( GLContainer container ){
			this.container = container;
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
			toVirtualPosition( e.getPoint() );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited( MouseEvent e ){
			toVirtualPosition( e.getPoint() );
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
			toVirtualPosition( e.getPoint() );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved( MouseEvent e ){
			toVirtualPosition( e.getPoint() );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
		 */
		@Override
		public void mouseWheelMoved( MouseWheelEvent e ){
		}
	}
	
	
	private static class GUIListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		
		private static void setPosition( Point point, Matrix4f transform ){
			transform.m03 = VIRTUAL_AREA_WIDTH * ( point.x - VIRTUAL_VIEWPORT_X ) / VIRTUAL_VIEWPORT_WIDTH;
			transform.m13 = VIRTUAL_AREA_HEIGHT * ( point.y - VIRTUAL_VIEWPORT_Y) / VIRTUAL_VIEWPORT_HEIGHT;
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
		
		final GLGUI gui;
		final Cursor cursor;
		
		public GUIRenderer( GLGUI gui ){
			this.gui = gui;
			this.cursor = new Cursor();
		}
		
		public void init( GLAutoDrawable glDrawable ){
			GUIListener myListener = new GUIListener( cursor );
			
			Component component = ( (GLCanvas)glDrawable );
			component.addMouseListener( myListener );
			component.addMouseMotionListener( myListener );
			component.addMouseWheelListener( myListener );
			
			CointainerListener myListener2 = new CointainerListener( gui.getContentPane() );
			
			component.addMouseListener( myListener2 );
			component.addMouseMotionListener( myListener2 );
			component.addMouseWheelListener( myListener2 );
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
			cursor.draw( gl );
			
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

		GLCanvas canvas = new GLCanvas();
		
		GLGUI gui = new GLGUI();
		
		GLContainer container = new GLContainer();
		container.setBounds( 350, 350 );
		
		GLComponent button;
		
		button = new GLButton();
		button.setBounds( 50, 50, 100, 100 );
		container.add( button );
		
		button = new GLButton();
		button.setBounds( 200, 50, 100, 100 );
		container.add( button );
		
		button = new GLButton();
		button.setBounds( 50, 200, 100, 100 );
		container.add( button );
		
		button = new GLButton();
		button.setBounds( 200, 200, 100, 100 );
		container.add( button );
		
		container.setPosition( 
			( VIRTUAL_AREA_WIDTH - container.getWidth() ) /2,
			( VIRTUAL_AREA_HEIGHT - container.getHeight() ) /2
		);
		
		gui.setContentPane( container );
		
		canvas.addGLEventListener( new GUIRenderer( gui ) );
		
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
