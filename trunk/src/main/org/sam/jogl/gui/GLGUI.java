/* 
 * GLGUI.java
 * 
 * Copyright (c) 2012 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
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
package org.sam.jogl.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;

import org.sam.elementos.Initializable;
import org.sam.elementos.Modificable;
import org.sam.elementos.Modificador;
import org.sam.jogl.Nodo;
import org.sam.jogl.NodoTransformador;
import org.sam.util.Imagen;

public class GLGUI implements Initializable{
	
	private static final Cursor blankCursor;
	private static final Cursor spaceshipCursor;
	
	static{
		BufferedImage bi = new BufferedImage( 32, 32, BufferedImage.TYPE_INT_ARGB );
		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor( 
				bi,
				new Point( 0, 0 ),
				"blank cursor"
		);
		bi.createGraphics().drawImage( Imagen.cargarToBufferedImage( "resources/cursor.png" ), 0, 0, null );
		spaceshipCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				bi,
				new Point( 0, 0 ),
				"spaceship cursor"
		);
	}
	
	private static class CursorRenderer implements GLEventListener{
		
		private transient final Matrix4f transform;
		private transient NodoTransformador cursor;
		private transient Modificador modificadores[];
		
		CursorRenderer( Matrix4f transform ){
			this.transform = transform;
		}
		
		boolean isAssigned(){
			return cursor != null;
		}
		
		void setCursor( Nodo cursor ){
			if( cursor == null )
				return;
			
			this.cursor = new NodoTransformador( transform, cursor );
			
			Deque<Nodo> stack = new LinkedList<Nodo>();
			Collection<Modificador> modificadores = new LinkedList<Modificador>();
			stack.push( cursor );
			do{
				Nodo actual = stack.pop();
				if( actual instanceof Modificable )
					modificadores.add( ( (Modificable)actual ).getModificador() );
				for( Nodo nodo: actual.getChilds() )
					stack.push( nodo );
			}while( !stack.isEmpty() );
			if( !modificadores.isEmpty() )
				this.modificadores = modificadores.toArray( new Modificador[modificadores.size()] );
		}
		
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void init( GLAutoDrawable glDrawable ){
		}
		
		private transient long tAnterior, tActual;
		
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void display( GLAutoDrawable glDrawable ){
			tAnterior = tActual;
			tActual = System.nanoTime();
			long incT = tActual - tAnterior;
			
			GL2 gl = glDrawable.getGL().getGL2();

			gl.glViewport( 0, 0, glDrawable.getWidth(), glDrawable.getHeight() );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho( 0, glDrawable.getWidth(), glDrawable.getHeight(), 0, -1, 1 );
	
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			
			if( modificadores != null )
				for( Modificador m: modificadores )
					m.modificar( incT );
			cursor.draw( gl );
		
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPopMatrix();
			
			gl.glFlush();
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
		 */
		@Override
		public void reshape( GLAutoDrawable glDrawable, int x, int y, int w, int h ){
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable glDrawable ){
		}
	}
	
	private static class CursorListener implements MouseListener, MouseMotionListener{

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
	
	private static class GUIRenderer implements GLEventListener{
		
		private final Rectangle viewport;
		int virtualAreaWidth;
		int virtualAreaHeight;
		GLContainer contentPane;

		GUIRenderer( Rectangle viewport ){
			this.viewport = viewport;
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void init( GLAutoDrawable glDrawable ){
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void display( GLAutoDrawable glDrawable ){
			if( contentPane == null )
				return;
				
			GL2 gl = glDrawable.getGL().getGL2();
			
			gl.glViewport( viewport.x, viewport.y, viewport.width, viewport.height );
			
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho( 0, virtualAreaWidth, virtualAreaHeight, 0, -1, 1 );
			
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			
			contentPane.draw( gl );
			
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPopMatrix();
			
			//FIXME llamar en otro hilo
			UIManager.runActions();
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
		 */
		@Override
		public void reshape( GLAutoDrawable glDrawable, int x, int y, int width, int height ){
			if( width != 0 && height != 0 ){
				viewport.x      = ( virtualAreaHeight * width - virtualAreaWidth * height ) 
				                          / ( 2 * virtualAreaHeight );
				viewport.y      = 0;
				viewport.width  = virtualAreaWidth * height / virtualAreaHeight;
				viewport.height = height;
			}
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable glDrawable ){
		}
	}
	
	private static class GUIListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		
		private final Rectangle viewport;
		int virtualAreaWidth;
		int virtualAreaHeight;
		GLContainer contentPane;
		
		GUIListener( Rectangle viewport ){
			this.viewport = viewport;
		}
		
		private transient final Point2f virtualPoint = new Point2f();

		Point2f toVirtualPosition( Point point ){
			virtualPoint.x = virtualAreaWidth * ( point.x - viewport.x ) / viewport.width;
			virtualPoint.y = virtualAreaHeight * ( point.y - viewport.y) / viewport.height;
			return virtualPoint;
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
			if( contentPane == null || !contentPane.isEnabled() )
				return;
			Point2f cursorPosition = toVirtualPosition( e.getPoint() );
			if( contentPane.contains( cursorPosition ) ){
				contentPane.processMouseEvent( e.getID(), e.getButton(), cursorPosition.x, cursorPosition.y );
			}
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited( MouseEvent e ){
			if( contentPane == null || !contentPane.isEnabled() )
				return;
			Point2f cursorPosition = toVirtualPosition( e.getPoint() );
			contentPane.processMouseEvent( e.getID(), e.getButton(), cursorPosition.x, cursorPosition.y );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed( MouseEvent e ){
			if( contentPane == null || !contentPane.isEnabled() )
				return;
			Point2f cursorPosition = toVirtualPosition( e.getPoint() );
			if( contentPane.contains( cursorPosition ) ){
				contentPane.processMouseEvent( e.getID(), e.getButton(), cursorPosition.x, cursorPosition.y );
			}
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased( MouseEvent e ){
			if( contentPane == null || !contentPane.isEnabled() )
				return;
			Point2f cursorPosition = toVirtualPosition( e.getPoint() );
			if( contentPane.contains( cursorPosition ) ){
				contentPane.processMouseEvent( e.getID(), e.getButton(), cursorPosition.x, cursorPosition.y );
			}
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged( MouseEvent e ){
			if( contentPane == null || !contentPane.isEnabled() )
				return;
			Point2f cursorPosition = toVirtualPosition( e.getPoint() );
			if( contentPane.contains( cursorPosition ) ){
				if( !contentPane.isHovered() )
					contentPane.processMouseEvent( MouseEvent.MOUSE_ENTERED, e.getButton(), cursorPosition.x, cursorPosition.y );
				else
					contentPane.processMouseEvent( e.getID(), e.getButton(), cursorPosition.x, cursorPosition.y );
			}else
				if( contentPane.isHovered() )
					contentPane.processMouseEvent( MouseEvent.MOUSE_EXITED, e.getButton(), cursorPosition.x, cursorPosition.y );
			
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved( MouseEvent e ){
			if( contentPane == null || !contentPane.isEnabled() )
				return;
			Point2f cursorPosition = toVirtualPosition( e.getPoint() );
			if( contentPane.contains( cursorPosition ) ){
				if( !contentPane.isHovered() )
					contentPane.processMouseEvent( MouseEvent.MOUSE_ENTERED, e.getButton(), cursorPosition.x, cursorPosition.y );
				else
					contentPane.processMouseEvent( e.getID(), e.getButton(), cursorPosition.x, cursorPosition.y );
			}else
				if( contentPane.isHovered() )
					contentPane.processMouseEvent( MouseEvent.MOUSE_EXITED, e.getButton(), cursorPosition.x, cursorPosition.y );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
		 */
		@Override
		public void mouseWheelMoved( MouseWheelEvent e ){
		}
	}
	
	private final CursorRenderer cursorRenderer; 
	private final CursorListener cursorListener;
	private final GUIRenderer    guiRenderer; 
	private final GUIListener    guiListener;
	
	private int virtualAreaWidth;
	private int virtualAreaHeight;
	
	private GLContainer contentPane;
	
	public GLGUI(){
		Matrix4f tLocal = new Matrix4f();
		tLocal.setIdentity();
		tLocal.m03 = -80; // x out
		tLocal.m13 = -80; // y out
		
		cursorRenderer = new CursorRenderer( tLocal );
		cursorListener = new CursorListener( tLocal );
		
		Rectangle viewport = new Rectangle( 0, 0, 1, 1 );
		guiRenderer = new GUIRenderer( viewport );
		guiListener = new GUIListener( viewport );
		
		//setVirtualAreaDimesions( 1024, 768 );
		setVirtualAreaDimesions( 640, 480 );
		
		UIManager.registerInitializable( this );
	}
	
	public void setVirtualAreaDimesions( int width, int height ){
		virtualAreaWidth  = guiRenderer.virtualAreaWidth  = guiListener.virtualAreaWidth =  width;
		virtualAreaHeight = guiRenderer.virtualAreaHeight = guiListener.virtualAreaHeight = height;
	}
	
	public void setContentPane( GLContainer container ){
		this.contentPane = guiRenderer.contentPane = guiListener.contentPane = container;
//		container.setBounds( 0, 0, virtualAreaWidth, virtualAreaHeight );
		container.setPosition( 
				( virtualAreaWidth  - container.getWidth()  ) / 2,
				( virtualAreaHeight - container.getHeight() ) / 2
		);
	}
	
	public GLContainer getContentPane(){
		return this.contentPane;
	}
	
	void _bind( GLAutoDrawable glDrawable ){
		
		glDrawable.addGLEventListener( guiRenderer );
		Component component = ((Component)glDrawable);
			
		component.addMouseListener( guiListener );
		component.addMouseMotionListener( guiListener );
		component.addMouseWheelListener( guiListener );
		
		if( cursorRenderer.isAssigned() ){
			glDrawable.addGLEventListener( cursorRenderer );
			component.addMouseListener( cursorListener );
			component.addMouseMotionListener( cursorListener );
		}
		component.setCursor( spaceshipCursor );
	}
	
	public void bind( final GLAutoDrawable glDrawable ){
	
		if( UIManager.isInitialized() )
			_bind( glDrawable );
		else
			glDrawable.addGLEventListener( new GLEventListener(){
				public void init( GLAutoDrawable glDrawable ){
					UIManager.init( glDrawable.getGL().getGL2() );
					glDrawable.removeGLEventListener( this );
					_bind( glDrawable );
				}
				public void reshape( GLAutoDrawable glDrawable, int x, int y, int w, int h ){}
				
				public void display( GLAutoDrawable glDrawable ){}

				public void dispose( GLAutoDrawable glDrawable ){}
			} );
	}
		
	public void unbind( GLAutoDrawable glDrawable ){
		glDrawable.removeGLEventListener( guiRenderer );
		
		Component component = ((Component)glDrawable);
		
		component.removeMouseListener( guiListener );
		component.removeMouseMotionListener( guiListener );
		component.removeMouseWheelListener( guiListener );
		
		if( cursorRenderer.isAssigned() ){
			glDrawable.removeGLEventListener( cursorRenderer );
			component.removeMouseListener( cursorListener );
			component.removeMouseMotionListener( cursorListener );
		}
		component.setCursor( blankCursor );
	}

	/* (non-Javadoc)
	 * @see org.sam.elementos.Initializable#init()
	 */
	@Override
	public void init(){
		cursorRenderer.setCursor( UIManager.getNodo( "Cursor.decorator" ) );
	}
}
