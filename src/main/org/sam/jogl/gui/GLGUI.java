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

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.NodoTransformador;
import org.sam.jogl.Textura;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

public class GLGUI{
	
	private static final Cursor defaultCursor;
	
	static{
		BufferedImage bi = new BufferedImage( 32, 32, BufferedImage.TYPE_INT_ARGB );
		bi.createGraphics().drawImage( Imagen.cargarToBufferedImage( "resources/cursor.png" ), 0, 0, null );
		
		defaultCursor = Toolkit.getDefaultToolkit().createCustomCursor(
						bi,
						new Point( 0, 0 ),
						"spaceship cursor"
				);
	}
	
	private static class CursorRenderer implements GLEventListener{
		
		private transient final Matrix4f transform;
		private transient NodoTransformador cursor;
		private transient Particulas estela;
		
		CursorRenderer( Matrix4f transform ){
			this.transform = transform;
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
			
			estela.setEmisor( new Emisor.Cache( new Emisor.Transformador( new Emisor.Puntual( 0.25f, -0.25f, 45 ), t1 ), 256 ) );
			
			estela.setEmision( Particulas.Emision.CONTINUA );
			estela.setRangoDeEmision( 1.0f );
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
						new float[] { 0.0f, 8.0f },
						MetodoDeInterpolacion.Predefinido.COSENOIDAL
					)
			);
//			estela.setRadio( 8.0f );
			estela.reset();
			estela.setApariencia( ap );
		
			cursor = new NodoTransformador( transform, estela );
		}

		private transient long tAnterior, tActual;
		
		public void display( GLAutoDrawable glDrawable ){
			tAnterior = tActual;
			tActual = System.nanoTime();
			// @SuppressWarnings("unused")
			float incT = (float)( tActual - tAnterior ) / 1000000000;
		
			GL2 gl = glDrawable.getGL().getGL2();

			gl.glViewport( 0, 0, glDrawable.getWidth(), glDrawable.getHeight() );
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho( 0, glDrawable.getWidth(), glDrawable.getHeight(), 0, -1, 1 );
	
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			
			estela.getModificador().modificar( incT );
			cursor.draw( gl );
			gl.glFlush();
			
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPopMatrix();
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
		private int virtualAreaWidth;
		private int virtualAreaHeight;
		private GLContainer contentPane;

		GUIRenderer( Rectangle viewport ){
			this.viewport = viewport;
		}

		public void init( GLAutoDrawable glDrawable ){
			UIManager.Init( glDrawable.getGL().getGL2() );
			if( contentPane != null )
				contentPane.init();
		}

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
		}

		public void reshape( GLAutoDrawable glDrawable, int x, int y, int width, int height ){
			if( width != 0 && height != 0 ){
				viewport.x      = ( virtualAreaHeight * width - virtualAreaWidth * height ) 
				                          / ( 2 * virtualAreaHeight );
				viewport.y      = 0;
				viewport.width  = virtualAreaWidth * height / virtualAreaHeight;
				viewport.height = height;
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
	
	private static class GUIListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		
		private final Rectangle viewport;
		private int virtualAreaWidth;
		private int virtualAreaHeight;
		private GLContainer contentPane;
		
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
			if( !contentPane.isEnabled() )
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
			if( !contentPane.isEnabled() )
				return;
			Point2f cursorPosition = toVirtualPosition( e.getPoint() );
			contentPane.processMouseEvent( e.getID(), e.getButton(), cursorPosition.x, cursorPosition.y );
		}
	
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed( MouseEvent e ){
			if( !contentPane.isEnabled() )
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
			if( !contentPane.isEnabled() )
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
			if( !contentPane.isEnabled() )
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
			if( !contentPane.isEnabled() )
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
		
		Rectangle viewport = new Rectangle();
		guiRenderer = new GUIRenderer( viewport );
		guiListener = new GUIListener( viewport );
		
		//setVirtualAreaDimesions( 1024, 768 );
		setVirtualAreaDimesions( 640, 480 );
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
	
	public void bind( GLAutoDrawable glDrawable ){
	
		glDrawable.addGLEventListener( guiRenderer );
		glDrawable.addGLEventListener( cursorRenderer );
		
		Component component = ((Component)glDrawable);
			
		component.addMouseListener( guiListener );
		component.addMouseMotionListener( guiListener );
		component.addMouseWheelListener( guiListener );
		component.addMouseListener( cursorListener );
		component.addMouseMotionListener( cursorListener );
		
		component.setCursor(defaultCursor);
	}
	
	public void unbind( GLAutoDrawable glDrawable ){
		
		glDrawable.removeGLEventListener( guiRenderer );
		glDrawable.removeGLEventListener( cursorRenderer );
		
		Component component = ((Component)glDrawable);
		
		component.removeMouseListener( guiListener );
		component.removeMouseMotionListener( guiListener );
		component.removeMouseWheelListener( guiListener );
		component.removeMouseListener( cursorListener );
		component.removeMouseMotionListener( cursorListener );
	}
}
