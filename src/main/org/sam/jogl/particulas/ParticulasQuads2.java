/* 
 * ParticulasQuads2.java
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
package org.sam.jogl.particulas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.sam.elementos.Modificador;
import org.sam.interpoladores.Getter;
import org.sam.jogl.MatrixSingleton;

import com.jogamp.common.nio.Buffers;

class ParticulasQuads2 extends Particulas{

	private static final Random aleatorio = new Random();

	@SuppressWarnings( "unused" )
	private static class Particula{
		float vida;
		float posIniX, posIniY, posIniZ;
		float velX, velY, velZ;
		float giroInicial;
		float velGiro;
		float r, g, b, a;
	}

	private class ModificadorParticulas implements Modificador{

		private void setPos( float p11x, float p11y, float p12x, float p12y, float p1z ){
			// Orden inverso a las agujas del reloj, normal hacia el observador
			pos.put( p11x );
			pos.put( p11y );
			pos.put( p1z );
			pos.put( p12x );
			pos.put( p11y );
			pos.put( p1z );
			pos.put( p12x );
			pos.put( p12y );
			pos.put( p1z );
			pos.put( p11x );
			pos.put( p12y );
			pos.put( p1z );
		}

		private void setPos( float p11x, float p11y, float p12x, float p12y, float p1z, float p21x, float p21y,
				float p22x, float p22y, float p2z ){
			// Orden inverso a las agujas del reloj, normal hacia el observador
			pos.put( p11x );
			pos.put( p11y );
			pos.put( p1z );
			pos.put( p22x );
			pos.put( p22y );
			pos.put( p2z );
			pos.put( p21x );
			pos.put( p21y );
			pos.put( p2z );
			pos.put( p12x );
			pos.put( p12y );
			pos.put( p1z );
		}

		private final transient Vector3f vDir = new Vector3f();
		private final transient Point3f p1 = new Point3f();
		private final transient Point3f p2 = new Point3f();
		private final transient Vector2f vDirProj = new Vector2f();

		private final transient Quat4f rEmisorPre = new Quat4f();
		private final transient Quat4f rEmisorAct = new Quat4f();
		private final transient Quat4f rEmisorInt = new Quat4f();

		private final transient Vector3f pEmisorPre = new Vector3f();
		private final transient Vector3f pEmisorAct = new Vector3f();
		private final transient Vector3f pEmisorInt = new Vector3f();

		public boolean modificar( float steep ){

			if( particulasActivas == 0 ){
				if( emision == Emision.UNICA )
					return false;
				particulasActivas = nParticulas;
				// for (int i = 0, len = vidas.length; i < len; i++)
				// vidas[i] = -((float)(i+1)/nParticulas)*rangoDeEmision;
			}
			pos.rewind();
			col.rewind();
			tex.rewind();

			float iVidaT = iVida * steep;

			Matrix4f transform_matrix = getTransformMatrix();

			if( interpolarEmisor ){
				rEmisorPre.set( rEmisorAct );
				transform_matrix.get( rEmisorAct );
				pEmisorPre.set( pEmisorAct );
				transform_matrix.get( pEmisorAct );
			}else{
				transform_matrix.get( rEmisorAct );
				rEmisorPre.set( rEmisorAct );
				transform_matrix.get( pEmisorAct );
				pEmisorPre.set( pEmisorAct );
				interpolarEmisor = true;
			}

			Matrix4f model_view_matrix = MatrixSingleton.getModelViewMatrix();
			Matrix4f projection_matrix = MatrixSingleton.getProjectionMatrix();

			for( int i = 0, j = 0, k = 0, len = vidas.length; i < len; i++, j += 3, k += 12 ){
				vidas[i] += iVidaT;
				float vida = vidas[i];

				// La particula todavia no ha nacido
				if( vida <= 0 )
					continue;
				// La particula ha muerto en este intervalo
				if( vida >= 1.0f ){
					if( emision != Emision.CONTINUA ){
						particulasActivas--;
						// Reducimos el tamaño de la particula a 0
						pos.position( k );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						pos.put( 0 );
						vidas[i] -= (float)Math.floor( vida ) + 1.0f;
						continue;
					}
					// Emision continua se regenera
					vida -= (float)Math.floor( vida );
					vidas[i] = vida;
				}
				// La particula ha nacido en este intervalo
				if( vida <= iVidaT ){
					VectorLibre vec = emisor.emite();
					float alpha = ( iVidaT - vida ) / iVidaT;
					rEmisorInt.interpolate( rEmisorPre, rEmisorAct, alpha );
					transform_matrix.setRotation( rEmisorInt );
					pEmisorInt.interpolate( pEmisorPre, pEmisorAct, alpha );
					transform_matrix.setTranslation( pEmisorInt );
					transform_matrix.transform( vec.posicion );
					vec.direccion.scale( modulosVelocidad[i] );
					transform_matrix.transform( vec.direccion );

					posIni[j] = vec.posicion.x;
					diresVelocidad[j] = vec.direccion.x;
					posIni[j + 1] = vec.posicion.y;
					diresVelocidad[j + 1] = vec.direccion.y;
					posIni[j + 2] = vec.posicion.z;
					diresVelocidad[j + 2] = vec.direccion.z;
				}

				// p = 1/2(f*t^2) + v*t + p0; se usa la vida como variable de tiempo.
				float x = ( ( fuerza.x * vida ) / 2 + diresVelocidad[j] ) * vida + posIni[j];
				float y = ( ( fuerza.y * vida ) / 2 + diresVelocidad[j + 1] ) * vida + posIni[j + 1];
				float z = ( ( fuerza.z * vida ) / 2 + diresVelocidad[j + 2] ) * vida + posIni[j + 2];

				if( iRadio != null ){
					radio = iRadio.get( vida );
				}
				if( iSemiEje != null ){
					semiEje = iSemiEje.get( vida );
				}

				float tmp1 = radio;
				float tmp2 = tmp1;

				if( semiEje != 0.0f ){
					vDir.set( fuerza.x * vida + diresVelocidad[j], fuerza.y * vida + diresVelocidad[j + 1], fuerza.z
							* vida + diresVelocidad[j + 2] );
					vDir.normalize();

					// col.position(i<<4);
					// col.put(0.5f*vDir.x + 0.5f); col.put(0.5f*vDir.y + 0.5f); col.put(0.5f*vDir.z + 0.5f);
					// col.put(1.0f);
					// col.put(0.5f*vDir.x + 0.5f); col.put(0.5f*vDir.y + 0.5f); col.put(0.5f*vDir.z + 0.5f);
					// col.put(1.0f);
					// col.put(0.5f*vDir.x + 0.5f); col.put(0.5f*vDir.y + 0.5f); col.put(0.5f*vDir.z + 0.5f);
					// col.put(1.0f);
					// col.put(0.5f*vDir.x + 0.5f); col.put(0.5f*vDir.y + 0.5f); col.put(0.5f*vDir.z + 0.5f);
					// col.put(1.0f);

					vDir.scale( semiEje );
					p1.set( x, y, z );
					p1.sub( vDir );
					p2.set( x, y, z );
					p2.add( vDir );

					model_view_matrix.transform( p1 );

					float sx1 = -projection_matrix.m00 / p1.z;
					float sy1 = -projection_matrix.m11 / p1.z;
					p1.x = -projection_matrix.m20 + p1.x * sx1;
					p1.y = -projection_matrix.m21 + p1.y * sy1;
					p1.z = -projection_matrix.m32 / p1.z - projection_matrix.m22;

					model_view_matrix.transform( p2 );

					float sx2 = -projection_matrix.m00 / p2.z;
					float sy2 = -projection_matrix.m11 / p2.z;
					p2.x = -projection_matrix.m20 + p2.x * sx2;
					p2.y = -projection_matrix.m21 + p2.y * sy2;
					p2.z = -projection_matrix.m32 / p2.z - projection_matrix.m22;

					vDirProj.set( p2.x - p1.x, p2.y - p1.y );
					if( vDirProj.x != 0.0f || vDirProj.y != 0.0f ){
						vDirProj.normalize();
						tmp1 *= vDirProj.x - vDirProj.y;
						tmp2 *= vDirProj.x + vDirProj.y;
					}

					// pos.put(p1.x - tmp1*sx1); pos.put(p1.y - tmp2*sy1); pos.put(p1.z);
					// pos.put(p1.x - tmp2*sx1); pos.put(p1.y + tmp1*sy1); pos.put(p1.z);
					// pos.put(p2.x + tmp1*sx2); pos.put(p2.y + tmp2*sy2); pos.put(p2.z);
					// pos.put(p2.x + tmp2*sx2); pos.put(p2.y - tmp1*sy2); pos.put(p2.z);

					setPos( p1.x - tmp1 * sx1, p1.y - tmp2 * sy1, p1.x - tmp2 * sx1, p1.y + tmp1 * sy1, p1.z, p2.x
							+ tmp1 * sx2, p2.y + tmp2 * sy2, p2.x + tmp2 * sx2, p2.y - tmp1 * sy2, p2.z );

					// gl.glBegin(GL.GL_LINE_STRIP);
					// gl.glColor4f(0.5f - 0.5f*vDir.x,0.5f - 0.5f*vDir.y, 0.5f - 0.5f*vDir.z, 1.0f);
					// gl.glVertex2f(p1.x, p1.y);
					// gl.glVertex2f(p2.x, p2.y);
					// gl.glEnd();
					// gl.glBegin(GL.GL_LINE_STRIP);
					// gl.glColor4f(0.5f - 0.5f*vDir.x,0.5f - 0.5f*vDir.y, 0.5f - 0.5f*vDir.z, 1.0f);
					// gl.glVertex2f(p1.x - tmp1*sx1, p1.y - tmp2*sy1);
					// gl.glVertex2f(p1.x - tmp2*sx1, p1.y + tmp1*sy1);
					// gl.glVertex2f(p2.x + tmp1*sx2, p2.y + tmp2*sy2);
					// gl.glVertex2f(p2.x + tmp2*sx2, p2.y - tmp1*sy2);
					// gl.glVertex2f(p1.x - tmp1*sx1, p1.y - tmp2*sy1);
					// gl.glEnd();
				}else{
					p1.set( x, y, z );

					model_view_matrix.transform( p1 );

					float sx = -projection_matrix.m00 / p1.z;
					float sy = -projection_matrix.m11 / p1.z;
					p1.x = -projection_matrix.m20 + p1.x * sx;
					p1.y = -projection_matrix.m21 + p1.y * sy;
					p1.z = -projection_matrix.m32 / p1.z - projection_matrix.m22;

					sx *= radio;
					sy *= radio;

					// pos.put(p1.x - sx); pos.put(p1.y - sy); pos.put(p1.z);
					// pos.put(p1.x - sx); pos.put(p1.y + sy); pos.put(p1.z);
					// pos.put(p1.x + sx); pos.put(p1.y + sy); pos.put(p1.z);
					// pos.put(p1.x + sx); pos.put(p1.y - sy); pos.put(p1.z);

					setPos( p1.x - sx, p1.y - sy, p1.x + sx, p1.y + sy, p1.z );
				}

				if( iColores != null && iAlfa != null ){
					float rgb[] = iColores.get( vida );
					r = rgb[0];
					g = rgb[1];
					b = rgb[2];
					a = iAlfa.get( vida );

					col.position( i << 4 );

					col.put( r );
					col.put( g );
					col.put( b );
					col.put( a );
					col.put( r );
					col.put( g );
					col.put( b );
					col.put( a );
					col.put( r );
					col.put( g );
					col.put( b );
					col.put( a );
					col.put( r );
					col.put( g );
					col.put( b );
					col.put( a );
				}else if( iColores != null ){
					float rgb[] = iColores.get( vida );
					r = rgb[0];
					g = rgb[1];
					b = rgb[2];

					col.position( i << 4 );
					col.put( r );
					col.put( g );
					col.put( b );
					col.position( ( i << 4 ) + 4 );
					col.put( r );
					col.put( g );
					col.put( b );
					col.position( ( i << 4 ) + 8 );
					col.put( r );
					col.put( g );
					col.put( b );
					col.position( ( i << 4 ) + 12 );
					col.put( r );
					col.put( g );
					col.put( b );
				}else if( iAlfa != null ){
					a = iAlfa.get( vida );
					col.position( ( i << 4 ) + 3 );
					col.put( a );
					col.position( ( i << 4 ) + 7 );
					col.put( a );
					col.position( ( i << 4 ) + 11 );
					col.put( a );
					col.position( ( i << 4 ) + 15 );
					col.put( a );
				}

				if( velGiro != 0.0f ){
					float alfa = velocidadesGiro[i] * vida + girosIniciales[i];
					float cosA = (float)( Math.cos( alfa ) * 0.5 );
					float senA = (float)( Math.sin( alfa ) * 0.5 );
					tmp1 = cosA + senA;
					tmp2 = cosA - senA;
					tex.position( i << 3 );
					tex.put( 0.5f - tmp1 );
					tex.put( 0.5f - tmp2 );
					tex.put( 0.5f + tmp2 );
					tex.put( 0.5f - tmp1 );
					tex.put( 0.5f + tmp1 );
					tex.put( 0.5f + tmp2 );
					tex.put( 0.5f - tmp2 );
					tex.put( 0.5f + tmp1 );
				}
			}
			return true;
		}
	}

	private final transient float[] vidas;
	private final transient float[] posIni;
	private final transient float[] diresVelocidad;
	private final transient float[] modulosVelocidad;
	private final transient float[] girosIniciales;
	private final transient float[] velocidadesGiro;

	private final transient FloatBuffer pos;
	private final transient FloatBuffer col;
	private final transient FloatBuffer tex;

	private float radio;
	private Getter.Float<Float> iRadio;
	private float semiEje;
	private Getter.Float<Float> iSemiEje;

	private float r;
	private float g;
	private float b;
	private Getter.Float<float[]> iColores;
	private float a;
	private Getter.Float<Float> iAlfa;

	protected ParticulasQuads2( int nParticulas ){
		super( nParticulas );

		vidas = new float[nParticulas];
		posIni = new float[nParticulas * 3];
		diresVelocidad = new float[nParticulas * 3];
		modulosVelocidad = new float[nParticulas];
		girosIniciales = new float[nParticulas];
		velocidadesGiro = new float[nParticulas];

		radio = 1.0f;
		semiEje = 0.0f;

		pos = ByteBuffer.allocateDirect( nParticulas * 4 * 3 * Buffers.SIZEOF_FLOAT ).order( ByteOrder.nativeOrder() )
				.asFloatBuffer();
		col = ByteBuffer.allocateDirect( nParticulas * 4 * 4 * Buffers.SIZEOF_FLOAT ).order( ByteOrder.nativeOrder() )
				.asFloatBuffer();
		tex = ByteBuffer.allocateDirect( nParticulas * 4 * 2 * Buffers.SIZEOF_FLOAT ).order( ByteOrder.nativeOrder() )
				.asFloatBuffer();

		modificador = new ModificadorParticulas();
	}

	protected ParticulasQuads2( ParticulasQuads2 me ){
		super( me );
		vidas = new float[me.nParticulas];
		posIni = new float[me.nParticulas * 3];
		diresVelocidad = new float[me.nParticulas * 3];
		modulosVelocidad = new float[me.nParticulas];
		girosIniciales = new float[me.nParticulas];
		velocidadesGiro = new float[me.nParticulas];

		this.semiEje = me.semiEje;
		this.iSemiEje = me.iSemiEje;
		this.radio = me.radio;
		this.iRadio = me.iRadio;
		this.r = me.r;
		this.g = me.g;
		this.b = me.b;
		this.iColores = me.iColores;
		this.a = me.a;
		this.iAlfa = me.iAlfa;

		pos = ByteBuffer.allocateDirect( nParticulas * 4 * 3 * Buffers.SIZEOF_FLOAT ).order( ByteOrder.nativeOrder() )
				.asFloatBuffer();
		col = ByteBuffer.allocateDirect( nParticulas * 4 * 4 * Buffers.SIZEOF_FLOAT ).order( ByteOrder.nativeOrder() )
				.asFloatBuffer();
		tex = ByteBuffer.allocateDirect( nParticulas * 4 * 2 * Buffers.SIZEOF_FLOAT ).order( ByteOrder.nativeOrder() )
				.asFloatBuffer();

		modificador = new ModificadorParticulas();
		// TODO cambiar
		this.reset();
	}

	private transient boolean interpolarEmisor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.util.Cacheable#reset()
	 */
	public void reset(){
		interpolarEmisor = false;
		particulasActivas = nParticulas;

		float[] posIni = new float[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		pos.clear();

		float[] colIni = new float[] { r, g, b, a, r, g, b, a, r, g, b, a, r, g, b, a };
		col.clear();

		float[] texIni = new float[] { 0, 0, 1, 0, 1, 1, 0, 1 };
		tex.clear();

		float pColor = 0.2f;
		boolean pHomogenea = false;

		for( int i = 0; i < nParticulas; i++ ){
			vidas[i] = -( (float)( i + 1 ) / nParticulas ) * rangoDeEmision;

			modulosVelocidad[i] = ( ( pVelocidad != 0.0f ) ? velocidad + ( aleatorio.nextFloat() - 0.5f ) * pVelocidad
					: velocidad ) * ( sentidoVelocidadAleatorio ? ( aleatorio.nextBoolean() ? 1: -1 ): 1 );
			girosIniciales[i] = ( ( pGiroInicial != 0.0f ) ? giroInicial + ( aleatorio.nextFloat() - 0.5f )
					* pGiroInicial: giroInicial )
					* ( sentidoGiroInicialAleatorio ? ( aleatorio.nextBoolean() ? 1: -1 ): 1 );
			velocidadesGiro[i] = ( ( pVelGiro != 0.0f ) ? velGiro + ( aleatorio.nextFloat() - 0.5f ) * pVelGiro
					: velGiro ) * ( sentidoVelGiroAleatorio ? ( aleatorio.nextBoolean() ? 1: -1 ): 1 );

			pos.put( posIni );
			if( pColor != 0 ){
				if( pHomogenea ){
					float p, rp, gp, bp;
					p = ( aleatorio.nextFloat() - 0.5f ) * pColor;
					rp = Math.min( 1, Math.max( 0, r + p ) );
					gp = Math.min( 1, Math.max( 0, g + p ) );
					bp = Math.min( 1, Math.max( 0, b + p ) );
					col.put( rp );
					col.put( gp );
					col.put( bp );
					col.put( a );
					col.put( rp );
					col.put( gp );
					col.put( bp );
					col.put( a );
					col.put( rp );
					col.put( gp );
					col.put( bp );
					col.put( a );
					col.put( rp );
					col.put( gp );
					col.put( bp );
					col.put( a );
				}else{
					float rp, gp, bp;
					rp = Math.min( 1, Math.max( 0, r + ( aleatorio.nextFloat() - 0.5f ) * pColor ) );
					gp = Math.min( 1, Math.max( 0, g + ( aleatorio.nextFloat() - 0.5f ) * pColor ) );
					bp = Math.min( 1, Math.max( 0, b + ( aleatorio.nextFloat() - 0.5f ) * pColor ) );
					col.put( rp );
					col.put( gp );
					col.put( bp );
					col.put( a );
					col.put( rp );
					col.put( gp );
					col.put( bp );
					col.put( a );
					col.put( rp );
					col.put( gp );
					col.put( bp );
					col.put( a );
					col.put( rp );
					col.put( gp );
					col.put( bp );
					col.put( a );
				}
			}else
				col.put( colIni );

			if( girosIniciales[i] != 0.0f ){
				float alfa = girosIniciales[i];
				float cosA = (float)( Math.cos( alfa ) * 0.5 );
				float senA = (float)( Math.sin( alfa ) * 0.5 );
				float tmp1 = cosA + senA;
				float tmp2 = cosA - senA;
				tex.position( i << 3 );
				tex.put( 0.5f - tmp1 );
				tex.put( 0.5f - tmp2 );
				tex.put( 0.5f + tmp2 );
				tex.put( 0.5f - tmp1 );
				tex.put( 0.5f + tmp1 );
				tex.put( 0.5f + tmp2 );
				tex.put( 0.5f - tmp2 );
				tex.put( 0.5f + tmp1 );
			}else
				tex.put( texIni );

		}
		pos.rewind();
		col.rewind();
		tex.rewind();

		tActual = System.nanoTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.jogl.particulas.Particulas#setRadio(float)
	 */
	@Override
	public void setRadio( float radio ){
		this.radio = radio;
		this.iRadio = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.jogl.particulas.Particulas#setRadio(org.sam.interpoladores.Getter.Float)
	 */
	@Override
	public void setRadio( Getter.Float<Float> radio ){
		this.iRadio = radio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.jogl.particulas.Particulas#setSemiEje(float)
	 */
	@Override
	public void setSemiEje( float semiEje ){
		this.semiEje = semiEje;
		this.iSemiEje = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.jogl.particulas.Particulas#setSemiEje(org.sam.interpoladores.Getter.Float)
	 */
	@Override
	public void setSemiEje( Getter.Float<Float> semiEje ){
		this.iSemiEje = semiEje;
	}

	public Particulas clone(){
		return new ParticulasQuads2( this );
	}

	private transient long tAnterior, tActual;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.jogl.Objeto3DAbs#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw( GL2 gl ){
		// TODO cambiar ñapa
		tAnterior = tActual;
		tActual = System.nanoTime();

		float incT = (float)( tActual - tAnterior ) / 1000000000;
		this.getModificador().modificar( incT );

		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glMatrixMode( GL2.GL_PROJECTION );
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glEnableClientState( GL2.GL_VERTEX_ARRAY );
		gl.glEnableClientState( GL2.GL_COLOR_ARRAY );
		gl.glEnableClientState( GL2.GL_TEXTURE_COORD_ARRAY );

		pos.rewind();
		gl.glVertexPointer( 3, GL.GL_FLOAT, 0, pos );
		col.rewind();
		gl.glColorPointer( 4, GL.GL_FLOAT, 0, col );
		tex.rewind();
		gl.glTexCoordPointer( 2, GL.GL_FLOAT, 0, tex );

		gl.glDrawArrays( GL2.GL_QUADS, 0, nParticulas * 4 );

		gl.glDisableClientState( GL2.GL_TEXTURE_COORD_ARRAY );
		gl.glDisableClientState( GL2.GL_COLOR_ARRAY );
		gl.glDisableClientState( GL2.GL_VERTEX_ARRAY );

		gl.glPopMatrix();
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glPopMatrix();
	}
}
