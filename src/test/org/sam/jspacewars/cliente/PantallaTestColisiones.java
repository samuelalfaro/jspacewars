/* 
 * PantallaTestColisiones.java
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
package org.sam.jspacewars.cliente;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.sam.colisiones.LimiteRectangular;
import org.sam.colisiones.Poligono;
import org.sam.jogl.Apariencia;
import org.sam.jogl.Hoja;
import org.sam.jogl.Instancia3D;
import org.sam.jspacewars.servidor.elementos.Elemento;
import org.sam.util.Reflexion;

/**
 * Clase que encapsula los distintos métodos necesarios para mostrar por pantalla,
 * tanto los polígonos como sus límites asociados a cada elemento del juego.</br>
 * Esta clase es usada llamada por la clase {@code TestColisiones}.
 */
@SuppressWarnings( "serial" )
public class PantallaTestColisiones extends GLCanvas{
	
	private static class PoligonoDibujable extends Hoja{
		
		private static float[] getArray( Poligono p, String name ){
			java.lang.reflect.Field f = Reflexion.findField( Poligono.class, name );
			try{
				boolean accesible = f.isAccessible();
				f.setAccessible( true );
				float[] array = (float[])f.get( p );
				f.setAccessible( accesible );
				return array;
			}catch( IllegalAccessException ignorada ){
				return null;
			}
		}
		
		private static Poligono getPoligono( Elemento e ){
			java.lang.reflect.Field f = Reflexion.findField( Elemento.class, "forma" );
			if( f == null )
				return null;
			try{
				boolean accesible = f.isAccessible();
				f.setAccessible( true );
				Poligono p = (Poligono)f.get( e );
				f.setAccessible( accesible );
				return p;
			}catch( IllegalAccessException ignorada ){
				return null;
			}
		}
		
		private final Poligono p;

		PoligonoDibujable( Elemento e ){
			this.p = getPoligono( e );
		}

		@Override
		public PoligonoDibujable clone(){
			return this;
		}
		
		@Override
		public void draw( GL2 gl ){
			if( p != null ){
				LimiteRectangular l = p.getLimites();
				gl.glBegin( GL.GL_LINE_STRIP );
					gl.glColor3f( 0.25f, 0.25f, 0.25f );
					gl.glVertex3f( l.getXMin(), l.getYMin(), 0 );
					gl.glVertex3f( l.getXMin(), l.getYMax(), 0 );
					gl.glVertex3f( l.getXMax(), l.getYMax(), 0 );
					gl.glVertex3f( l.getXMax(), l.getYMin(), 0 );
					gl.glVertex3f( l.getXMin(), l.getYMin(), 0 );
				gl.glEnd();
				int nLados = p.getNLados();
				float coordX[] = getArray( p, "coordX" );
				float coordY[] = getArray( p, "coordY" );

				gl.glBegin( GL.GL_LINE_STRIP );
					gl.glColor3f( 0.25f, 1.0f, 0.25f );
					for( int i = 0; i < nLados; i++ ){
						gl.glVertex3f( coordX[i], coordY[i], 0 );
					}
					gl.glVertex3f( coordX[0], coordY[0], 0 );
				gl.glEnd();
			}
		}
	}
	
	private static class Renderer implements GLEventListener {

		private final transient GLU glu = new GLU();

		private transient final Rectangle viewport;
		private final transient ClientData data;
		private final transient Apariencia apLineas;
		private final transient MarcoDeIndicadores marco;
		private final transient float ratio_4_3;

		Renderer( ClientData data ){
			viewport = new Rectangle();
			this.data = data;
			this.apLineas = new Apariencia();
			this.marco = MarcoDeIndicadores.getMarco( 0 );
			Rectangle2D.Float a43 = new Rectangle2D.Float();
			marco.calcularAreaInterna( a43, 4, 3 );
			ratio_4_3 = a43.width / a43.height;
		}

		private boolean iniciado = false;

		public void init( GLAutoDrawable drawable ){
			GL2 gl = drawable.getGL().getGL2();
			while( !marco.isLoadComplete() )
				marco.loadTexturas( gl );
			iniciado = true;
		}

		public void display( GLAutoDrawable drawable ){
			if( !iniciado ){
				reshape( drawable, 0, 0, drawable.getWidth(), drawable.getHeight() );
				init( drawable );
			}
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glViewport( viewport.x, viewport.y, viewport.width, viewport.height );
			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			glu.gluLookAt( 0.0, 0.0, 11, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 );

			apLineas.usar( gl );
			for( Instancia3D elemento: data.elementos )
				elemento.draw( gl );
			// gl.glDepthMask(false);

			marco.update( data );
			marco.draw( gl );
			gl.glFlush();
		}

		private final transient Rectangle2D.Float areaInterna = new Rectangle2D.Float();
		
		public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ){
			GL2 gl = drawable.getGL().getGL2();
			
			marco.setBounds( 0, 0, width, height );
			marco.calcularAreaInterna( areaInterna, width, height );
			viewport.x = (int)areaInterna.x;
			viewport.y = (int)areaInterna.y;
			viewport.width = (int)areaInterna.width;
			viewport.height = (int)areaInterna.height;

			double near = 0.5;
			double far = 240.0;
			double a1 = 35.0; // angulo en grados
			double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
			double d = near / Math.sqrt( ( 1 / Math.pow( Math.sin( a2 ), 2 ) ) - 1 );

			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			// Formato 4/3 centrado, panorámico a la derecha en caso contrario.
			gl.glFrustum( -ratio_4_3 * d, ( ( 2.0 * areaInterna.width ) / areaInterna.height - ratio_4_3 ) * d, -d, d, near, far );

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable drawable ){
		}
	}
	
	private final transient ClientData data;
	
	/**
	 * Constructor que genera un cliente que se encarga de mostrar los poligonos
	 * asociados a los distintos elementos elementos del juego. 
	 */
	public PantallaTestColisiones(){
		this.data = new ClientData();

		this.setBackground( Color.BLACK );
		this.setIgnoreRepaint( true );
		this.addGLEventListener( new Renderer( data ) );
		this.addKeyListener( new GameKeyListener( data ) );
	}
	
	/**
	 * @return {@code int} que enmascara las distintas teclas pulsadas.
	 */
	public int getKeyState(){
		return data.key_state;
	}

	/**
	 * @param nVidas valor del número de vidas asignado.
	 */
	public void setNVidas( int nVidas ){
		data.nVidas = nVidas;
	}

	/**
	 * @param nBombas valor del número de bombas asignado.
	 */
	public void setNBombas( int nBombas ){
		data.nBombas = nBombas;
	}

	/**
	 * @param puntos valor de los puntos asignados.
	 */
	public void setPuntos( int puntos ){
		data.puntos = puntos;
	}

	/**
	 * @param nivelesFijos valores de los niveles fijos asignados.
	 */
	public void setNivelesFijos( int[] nivelesFijos ){
		for( int i = 0; i < nivelesFijos.length; i++ )
			data.nivelesFijos[i] = nivelesFijos[i];
	}

	/**
	 * @param nivelesActuales valores de los niveles actuales asignados.
	 */
	public void setNivelesActuales( int[] nivelesActuales ){
		for( int i = 0; i < nivelesActuales.length; i++ )
			data.nivelesActuales[i] = nivelesActuales[i];
	}

	/**
	 * @param nivelesDisponibles valores de los niveles disponibles asignados.
	 */
	public void setNivelesDisponibles( int[] nivelesDisponibles ){
		for( int i = 0; i < nivelesDisponibles.length; i++ )
			data.nivelesDisponibles[i] = nivelesDisponibles[i];
	}

	/**
	 * @param indicador valor del indicador asignado.
	 */
	public void setIndicador( int indicador ){
		data.indicador = indicador;
	}

	/**
	 * @param grado valor del grado asignado.
	 */
	public void setGrado( int grado ){
		data.grado = grado;
	}

	/**
	 * Método que limpia la lista de elementos.
	 */
	public void clearList(){
		data.elementos.clear();
	}

	/**
	 * Método que añade un nuevo elemento la lista de elementos.
	 * @param e elemento a añadir.
	 */
	public void add( Elemento e ){
		data.elementos.add( new Instancia3D( (short)0, new PoligonoDibujable( e ) ) );
	}
}
