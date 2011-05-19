/* 
 * ParticulasPointSprites.java
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

import org.sam.elementos.Modificador;
import org.sam.interpoladores.Getter;
import org.sam.jogl.MatrixSingleton;

import com.jogamp.common.nio.Buffers;

class ParticulasPointSprites extends Particulas{

	private static final Random aleatorio = new Random();
	
	private class ModificadorEmisionDiscreta implements Modificador{
		
		public boolean modificar(float steep){
			
			if(particulasActivas == 0){
				if(emision == Particulas.Emision.UNICA)
					return false;
				particulasActivas = nParticulas;
				for (int i = 0, len = vidas.length; i < len; i++)
					 vidas[i] = -((float)(i+1)/nParticulas)*rangoDeEmision;
			}
			
			pos.rewind();
			col.rewind();
			float iVidaT = iVida * steep;
			Matrix4f t = getTransformMatrix();
			
			for (int i=0, j=0, len=vidas.length; i < len; i++, j+=3){
				
				if(vidas[i] < 1.0f){
					vidas[i] += iVidaT;
					float vida = vidas[i];
					// La particula todavia no ha nacido
					if(vida<=0)
						continue;
					// La particula ha muerto en este intervalo
					if (vida >= 1.0f){
						particulasActivas--;
						// Hacemos transparente la particula
						col.put((i<<2)+3,0.0f); //inidice color i*4 +3 para la componente alfa
						continue;
					}
					//  La particula ha nacido en este intervalo
					if (vida <= iVidaT ){
						VectorLibre  vec = emisor.emite();			
						t.transform(vec.posicion);
						vec.direccion.scale(modulosVelocidad[i]);
						t.transform(vec.direccion);
						
						posIni[j]  =vec.posicion.x; diresVelocidad[j]   = vec.direccion.x;
						posIni[j+1]=vec.posicion.y; diresVelocidad[j+1] = vec.direccion.y;
						posIni[j+2]=vec.posicion.z; diresVelocidad[j+2] = vec.direccion.z;
						
						if(iAlfa == null){
							col.put( (i<<2) +3 , a);
						}
					}
					//p = 1/2(f*t^2) + v*t + p0; usamos la vida como variable de tiempo.
					float x = ((fuerza.x * vida)/2 + diresVelocidad[j])*vida + posIni[j];
					float y = ((fuerza.y * vida)/2 + diresVelocidad[j+1])*vida + posIni[j+1];
					float z = ((fuerza.z * vida)/2 + diresVelocidad[j+2])*vida + posIni[j+2];
					
					pos.position(j);
					pos.put(x); pos.put(y); pos.put(z);
					
					if(iColores != null){
						col.position(i<<2);
						float rgb[] = iColores.get(vida);
						col.put(rgb[0]); col.put(rgb[1]); col.put(rgb[2]);
						if( iAlfa != null )
							col.put(iAlfa.get(vida));
					}else if(iAlfa != null)
						col.put( (i<<2) +3, iAlfa.get(vida));
				}
			}
			return true;
		}
	}
	
	private class ModificadorEmisionContinua implements Modificador{

		public boolean modificar(float steep){
	
			pos.rewind();
			col.rewind();
			
			float iVidaT = iVida * steep;
			Matrix4f t = getTransformMatrix();
			
			for (int i=0, j=0, len=vidas.length; i < len; i++, j+=3){
				vidas[i] += iVidaT;
				float vida = vidas[i];
				
				// La particula todavia no ha nacido
				if(vida<=0)
					continue;
				// La particula ha muerto en este intervalo y se regenera
				if (vida >= 1.0f){
					vida -= (float)Math.floor(vida);
					vidas[i] = vida;
				}
				//  La particula ha nacido en este intervalo
				if (vida <= iVidaT ){
					VectorLibre  vec = emisor.emite();			
					t.transform(vec.posicion);
					vec.direccion.scale(modulosVelocidad[i]);
					t.transform(vec.direccion);
					
					posIni[j]  =vec.posicion.x; diresVelocidad[j]   = vec.direccion.x;
					posIni[j+1]=vec.posicion.y; diresVelocidad[j+1] = vec.direccion.y;
					posIni[j+2]=vec.posicion.z; diresVelocidad[j+2] = vec.direccion.z;
				}
				
				//p = 1/2(f*t^2) + v*t + p0; usamos la vida como variable de tiempo.
				float x = ((fuerza.x * vida)/2 + diresVelocidad[j])*vida + posIni[j];
				float y = ((fuerza.y * vida)/2 + diresVelocidad[j+1])*vida + posIni[j+1];
				float z = ((fuerza.z * vida)/2 + diresVelocidad[j+2])*vida + posIni[j+2];
				
				pos.position(j);
				pos.put(x); pos.put(y); pos.put(z);
				
				if(iColores != null){
					col.position(i<<2);
					float rgb[] = iColores.get(vida);
					col.put(rgb[0]); col.put(rgb[1]); col.put(rgb[2]);
					if( iAlfa != null )
						col.put(iAlfa.get(vida));
				}else if(iAlfa != null)
					col.put( (i<<2) +3, iAlfa.get(vida));
			}
			return true;
		}
	}
	
	private final float[] vidas;
	private final float[] posIni;
	private final float[] diresVelocidad;
	private final float[] modulosVelocidad;
	private final float[] giros;
	
	private final transient FloatBuffer pos;
	private final transient FloatBuffer col;
	
	protected ParticulasPointSprites(int nParticulas){
		super(nParticulas);
		
		vidas = new float[nParticulas];
		posIni = new float[nParticulas*3];
		diresVelocidad = new float[nParticulas*3];
		modulosVelocidad = new float[nParticulas];
		giros = new float[nParticulas];

		pos = ByteBuffer.allocateDirect(nParticulas*3*Buffers.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		col = ByteBuffer.allocateDirect(nParticulas*4*Buffers.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		modificador = new ModificadorEmisionDiscreta();
	}
	
	protected ParticulasPointSprites(ParticulasPointSprites me){
		super(me);
		vidas = new float[me.nParticulas];
		posIni = new float[me.nParticulas*3];
		diresVelocidad = new float[me.nParticulas*3];
		modulosVelocidad = new float[me.nParticulas];
		giros = new float[me.nParticulas];
		
		pos = ByteBuffer.allocateDirect(nParticulas*3*Buffers.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		col = ByteBuffer.allocateDirect(nParticulas*4*Buffers.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		modificador = (me.emision == Particulas.Emision.CONTINUA) ?
				new ModificadorEmisionContinua():
				new ModificadorEmisionDiscreta();
	}


	/* (non-Javadoc)
	 * @see org.sam.util.Cacheable#reset()
	 */
	public void reset(){
		particulasActivas = nParticulas;
		col.rewind();
		for (int i = 0, len = vidas.length; i < len; i++){
		    vidas[i] = -((float)(i+1)/nParticulas)*rangoDeEmision;
			giros[i] = 
				((pVelGiro != 0.0f)
					? velGiro + (aleatorio.nextFloat()-0.5f) * pVelGiro
					: velGiro)
					* (sentidoVelGiroAleatorio
						? (aleatorio.nextBoolean()?1:-1)
						: 1);
			modulosVelocidad[i] =
				((pVelocidad != 0.0f)
					? velocidad + (aleatorio.nextFloat()-0.5f) * pVelocidad
					: velocidad)
					* (sentidoVelocidadAleatorio
						? (aleatorio.nextBoolean()?1:-1)
						: 1);
			
			if(pColor != 0){
				float rp, gp, bp;
				if(pColorHomogenea){
					if(pColorEscalar){
						float p = 1 + (aleatorio.nextFloat() - 0.5f) * pColor;
						rp = Math.min( 1, Math.max(0, r * p) );
						gp = Math.min( 1, Math.max(0, g * p) );
						bp = Math.min( 1, Math.max(0, b * p) );
					}else{
						float p = (aleatorio.nextFloat() - 0.5f) * pColor;
						rp = Math.min( 1, Math.max(0, r + p) );
						gp = Math.min( 1, Math.max(0, g + p) );
						bp = Math.min( 1, Math.max(0, b + p) );
					}
				}else{
					if(pColorEscalar){
						rp = Math.min( 1, Math.max(0, r * ( 1 + (aleatorio.nextFloat() - 0.5f) * pColor) ) );
						gp = Math.min( 1, Math.max(0, g * ( 1 + (aleatorio.nextFloat() - 0.5f) * pColor) ) );
						bp = Math.min( 1, Math.max(0, b * ( 1 + (aleatorio.nextFloat() - 0.5f) * pColor) ) );
					}else{
						rp = Math.min( 1, Math.max(0, r + (aleatorio.nextFloat() - 0.5f) * pColor) );
						gp = Math.min( 1, Math.max(0, g + (aleatorio.nextFloat() - 0.5f) * pColor) );
						bp = Math.min( 1, Math.max(0, b + (aleatorio.nextFloat() - 0.5f) * pColor) );
					}
				}
				col.put(rp); col.put(gp); col.put(bp); col.put(a);
			}else{
				col.put(r); col.put(g); col.put(b); col.put(a);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setModoDeEmision(int)
	 */
	@Override
	public void setEmision(Emision modoDeEmision) {
		if(this.emision != modoDeEmision){
			if(modoDeEmision == Particulas.Emision.CONTINUA)
				modificador = new ModificadorEmisionContinua();
			else if(this.emision == Particulas.Emision.CONTINUA)
				modificador = new ModificadorEmisionDiscreta();
		}
		this.emision = modoDeEmision;
	}
	
	float radio;
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(float)
	 */
	@Override
	public void setRadio(float radio){
		this.radio = radio;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(org.sam.interpoladores.Getter.Float)
	 */
	@Override
	public void setRadio(Getter.Float<Float> radio){
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(float)
	 */
	@Override
	public void setSemiEje(float semiEje){
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(org.sam.interpoladores.Getter.Float)
	 */
	@Override
	public void setSemiEje(Getter.Float<Float> semiEje){
		throw new UnsupportedOperationException();
	}
	
	public Particulas clone(){
		return new ParticulasPointSprites(this);
	}
	
	private transient final float[]  mt_array = new float[16];
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Objeto3DAbs#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw(GL2 gl) {
		
		super.draw(gl);
		
		float[] tmp = new float[1];
		gl.glGetFloatv( GL2.GL_POINT_SIZE_MAX, tmp, 0 );
		float maxSize = tmp[0];

		gl.glPointSize(radio > maxSize ? maxSize: radio);
//		System.out.println(radio+" "+maxSize+" "+(radio > maxSize ? maxSize: radio));

//		gl.glPointParameterf( GL.GL_POINT_FADE_THRESHOLD_SIZE, 60.0f );
//		gl.glPointParameterf( GL.GL_POINT_SIZE_MIN, 1.0f );
//		gl.glPointParameterf( GL.GL_POINT_SIZE_MAX, maxSize );
//
//		float quadratic[] =  { 1.0f, 0.0f, 0.01f };
//		gl.glPointParameterfv( GL.GL_POINT_DISTANCE_ATTENUATION, quadratic, 0 );

		gl.glTexEnvf( GL2.GL_POINT_SPRITE, GL2.GL_COORD_REPLACE, GL.GL_TRUE );	 
		
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		Matrix4f mt = MatrixSingleton.getModelViewMatrix();
		
		mt_array[ 0] = mt.m00; mt_array[ 1] = mt.m10; mt_array[ 2] = mt.m20; mt_array[ 3] = mt.m30;
		mt_array[ 4] = mt.m01; mt_array[ 5] = mt.m11; mt_array[ 6] = mt.m21; mt_array[ 7] = mt.m31;
		mt_array[ 8] = mt.m02; mt_array[ 9] = mt.m12; mt_array[10] = mt.m22; mt_array[11] = mt.m32;
		mt_array[12] = mt.m03; mt_array[13] = mt.m13; mt_array[14] = mt.m23; mt_array[15] = mt.m33;
		
		gl.glMultMatrixf(mt_array, 0);

		gl.glEnable( GL2.GL_POINT_SPRITE );
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		pos.rewind();
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, pos);
		col.rewind();
		gl.glColorPointer(4, GL.GL_FLOAT, 0, col);

		gl.glDrawArrays(GL.GL_POINTS, 0, nParticulas);

		gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY); 
		gl.glDisable( GL2.GL_POINT_SPRITE );
		
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glPopMatrix();
	}
}
