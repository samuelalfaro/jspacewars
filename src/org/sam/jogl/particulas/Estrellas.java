package org.sam.jogl.particulas;

import java.nio.*;
import java.util.Random;

import javax.media.opengl.GL;

import org.sam.elementos.Modificador;
import org.sam.interpoladores.Getter;

import com.sun.opengl.util.BufferUtil;

class Estrellas extends Particulas{

	private static final Random aleatorio = new Random();
	
	private class ModificadorDeParticulas implements Modificador{

		private void setPos( float p11x, float p11y, float p12x, float p12y ){
			// Orden inverso a las agujas del reloj, normal hacia el observador
			pos.put(p11x); pos.put(p11y);
			pos.put(p12x); pos.put(p11y);
			pos.put(p12x); pos.put(p12y); 
			pos.put(p11x); pos.put(p12y);
		}
		
		public boolean modificar(float steep){
			
			pos.rewind();
			col.rewind();
			tex.rewind();
			
			float iVidaT = iVida * steep;
			
			for (int i=0, j=0, len=vidas.length; i < len; i++){
				vidas[i] += iVidaT;
				float vida = vidas[i];
				
				// La particula todavia no ha nacido
				if(vida<=0)
					continue;
				// La particula ha muerto en este intervalo
				if (vida >= 1.0f){
					// Emision continua se regenera
					vida -= (float)Math.floor(vida);
					vidas[i] = vida;
				}
				//  La particula ha nacido en este intervalo
				if (vida <= iVidaT ){
					VectorLibre  vec = emisor.emite();
					
					float moduloVelocidad = ((pVelocidad != 0.0f) ?
							velocidad + (aleatorio.nextFloat()-0.5f) * pVelocidad :
							velocidad)
						* (sentidoVelocidadAleatorio ? (aleatorio.nextBoolean()?1:-1) : 1);
					vec.direccion.scale(moduloVelocidad);
					
					posIni[j]  =vec.posicion.x; velocidades[j]   = vec.direccion.x;
					posIni[j+1]=vec.posicion.y; velocidades[j+1] = vec.direccion.y;
				}
				
				float x = velocidades[j]*vida + posIni[j++];
				float y = velocidades[j]*vida + posIni[j++];
				setPos( x - radio, y - radio, x + radio, y + radio );
			}
			return true;
		}
	}
	
	private final transient float[] vidas;
	private final transient float[] posIni;
	private final transient float[] velocidades;
	
	private final transient FloatBuffer pos;
	private final transient FloatBuffer col;
	private final transient FloatBuffer tex;
	
	private float radio;

	protected Estrellas(int nParticulas){
		super(nParticulas);
		
		vidas = new float[nParticulas];
		posIni = new float[nParticulas*2];
		velocidades = new float[nParticulas*2];
		
		radio = 1.0f;

		pos = ByteBuffer.allocateDirect(nParticulas*4*2*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		col = ByteBuffer.allocateDirect(nParticulas*4*4*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		tex = ByteBuffer.allocateDirect(nParticulas*4*2*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		modificador = new ModificadorDeParticulas();
	}
	
	protected Estrellas(Estrellas me){
		super(me);
		vidas = new float[me.nParticulas];
		posIni = new float[me.nParticulas*2];
		velocidades = new float[me.nParticulas*2];
		
		this.radio = me.radio;

		pos = ByteBuffer.allocateDirect(nParticulas*4*2*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		col = ByteBuffer.allocateDirect(nParticulas*4*4*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		tex = ByteBuffer.allocateDirect(nParticulas*4*2*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		modificador = new ModificadorDeParticulas();
	}

	/* (non-Javadoc)
	 * @see org.sam.util.Cacheable#reset()
	 */
	public void reset(){
		particulasActivas = nParticulas;
		
		float[] posIni = new float[]{
				0,0,
				0,0,
				0,0,
				0,0 };
		pos.clear();
		
		float[] colIni = new float[]{
				r,g,b,a,
				r,g,b,a,
				r,g,b,a,
				r,g,b,a	};
		col.clear();
		
		float[] texIni = new float[]{
				0,0,
				1,0,
				1,1,
				0,1	};
		tex.clear();
		
		for (int i = 0; i < nParticulas; i++){
			vidas[i] = -((float)(i+1)/nParticulas)*rangoDeEmision;
			pos.put(posIni);
			if( pColor != 0 ){
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
				col.position(i<<4);
				col.put(rp); col.put(gp); col.put(bp); col.put(a);
				col.put(rp); col.put(gp); col.put(bp); col.put(a);
				col.put(rp); col.put(gp); col.put(bp); col.put(a);
				col.put(rp); col.put(gp); col.put(bp); col.put(a);
			}else
				col.put(colIni);
			
			float alfa = ((pGiroInicial != 0.0f) ?
					giroInicial	+ (aleatorio.nextFloat() - 0.5f) * pGiroInicial :
					giroInicial)
				* (sentidoGiroInicialAleatorio ? (aleatorio.nextBoolean() ? 1 : -1) : 1);
			
			if( alfa != 0.0f ){
				float cosA = (float)(Math.cos(alfa)*0.5);
				float senA = (float)(Math.sin(alfa)*0.5);
				float tmp1 = cosA + senA;
				float tmp2 = cosA - senA;
				
				tex.put(0.5f - tmp1); tex.put(0.5f - tmp2);
				tex.put(0.5f + tmp2); tex.put(0.5f - tmp1);
				tex.put(0.5f + tmp1); tex.put(0.5f + tmp2);
				tex.put(0.5f - tmp2); tex.put(0.5f + tmp1);
			}else
				tex.put(texIni);
		}
		pos.rewind();
		col.rewind();
		tex.rewind();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(float)
	 */
	@Override
	public void setRadio(float radio) {
		this.radio = radio;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(org.sam.interpoladores.Getter.Float)
	 */
	@Override
	public void setRadio(Getter.Float<Float> radio) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setSemiEje(float)
	 */
	@Override
	public void setSemiEje(float semiEje) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setSemiEje(org.sam.interpoladores.Getter.Float)
	 */
	@Override
	public void setSemiEje(Getter.Float<Float> semiEje) {
		throw new UnsupportedOperationException();
	}

	public Particulas clone(){
		return new Estrellas(this);
	}

	public void draw(GL gl) {
		super.draw(gl);
		
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

		pos.rewind();
		gl.glVertexPointer(2, GL.GL_FLOAT, 0, pos);
		col.rewind();
		gl.glColorPointer(4, GL.GL_FLOAT, 0, col);
		tex.rewind();
		gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, tex);
		
		gl.glDrawArrays(GL.GL_QUADS, 0, nParticulas * 4);
		
		gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
	}
}