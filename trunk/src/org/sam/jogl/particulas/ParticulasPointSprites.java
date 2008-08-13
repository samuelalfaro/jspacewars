package org.sam.jogl.particulas;

import java.nio.*;
import java.util.Random;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4f;

import org.sam.interpoladores.Getter;
import org.sam.util.Modificador;

import com.sun.opengl.util.BufferUtil;

class ParticulasPointSprites extends Particulas{

	private static final Random aleatorio = new Random();
	
	private class ModificadorEmisionDiscreta implements Modificador{
		
		public boolean modificar(float steep){
			if(particulasActivas == 0){
				if(emision == Particulas.Emision.UNICA)
					return false;
				particulasActivas = nParticulas;
				for (int i = 0, len = vidas.length; i < len; i++)
				    vidas[i] = -((float)(nParticulas - i)/nParticulas)*rangoDeEmision;
			}
			float iVidaT = iVida * steep;
			Matrix4f t = getTransform();
			
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
						// Reducimos el tama�o de la particula a 0
						pos.position(j);
						pos.put(0); pos.put(0); pos.put(0);
						// Hacemos transparente la particula
						col.put((i<<2)+3,0.0f); //inidice color i*4 +3 para la componente alfa
						continue;
					}
					//  La particula ha nacido en este intervalo
					if (vida <= iVidaT ){
						VectorLibre  vec = emisor.get();			
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
						float rgb[] = iColores.get(vida);
						r = rgb[0]; g = rgb[1]; b = rgb[2];
					}
					if(iAlfa != null)
						a = iAlfa.get(vida);
					col.position(i<<2);
					
					col.put(r); col.put(g); col.put(b); col.put(a);
				}
			}
			return true;
		}
	}
	
	private class ModificadorEmisionContinua implements Modificador{

		public boolean modificar(float steep){
			float iVidaT = iVida * steep;
			
			Matrix4f t = getTransform();
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
					VectorLibre  vec = emisor.get();			
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
					float rgb[] = iColores.get(vida);
					r = rgb[0]; g = rgb[1]; b = rgb[2];
				}
				if(iAlfa != null)
					a = iAlfa.get(vida);
				col.position(i<<2);
				
				col.put(r); col.put(g); col.put(b); col.put(a);
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
	
	private float r;
	private float g;
	private float b;
	private Getter.Float<float[]> iColores;
	private float a;
	private Getter.Float<Float> iAlfa;
	
	protected ParticulasPointSprites(int nParticulas){
		super(nParticulas);
		
		vidas = new float[nParticulas];
		posIni = new float[nParticulas*3];
		diresVelocidad = new float[nParticulas*3];
		modulosVelocidad = new float[nParticulas];
		giros = new float[nParticulas];

		pos = ByteBuffer.allocateDirect(nParticulas*3*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		col = ByteBuffer.allocateDirect(nParticulas*4*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		modificador = new ModificadorEmisionDiscreta();
	}
	
	protected ParticulasPointSprites(ParticulasPointSprites me){
		super(me);
		vidas = new float[me.nParticulas];
		posIni = new float[me.nParticulas*3];
		diresVelocidad = new float[me.nParticulas*3];
		modulosVelocidad = new float[me.nParticulas];
		giros = new float[me.nParticulas];
		
		this.r = me.r;
		this.g = me.g;
		this.b = me.b;
		this.iColores = me.iColores;
		this.a = me.a;
		this.iAlfa = me.iAlfa;

		pos = ByteBuffer.allocateDirect(nParticulas*3*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		col = ByteBuffer.allocateDirect(nParticulas*4*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		modificador = (me.emision == Particulas.Emision.CONTINUA) ?
				new ModificadorEmisionContinua():
				new ModificadorEmisionDiscreta();
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#iniciar()
	 */
	@Override
	public void iniciar(){
		particulasActivas = nParticulas;
		for (int i = 0, len = vidas.length; i < len; i++){
		    vidas[i] = -((float)(nParticulas - i)/nParticulas)*rangoDeEmision;
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
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(float)
	 */
	@Override
	public void setRadio(float radio){
		//TODO Ajustar PointSize al radio
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
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setColor(float, float, float, float)
	 */
	@Override
	public void setColor( float r, float g, float b, float a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.iColores = null;
		this.a = a;
		this.iAlfa = null;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setColor(float, float, float, org.sam.interpoladores.InterpoladorDeValores)
	 */
	@Override
	public void setColor( float r, float g, float b, Getter.Float<Float> iAlfa){
		this.r = r;
		this.g = g;
		this.b = b;
		this.iColores = null;
		this.iAlfa = iAlfa;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setColor(org.sam.interpoladores.InterpoladorPolinomico3D, float)
	 */
	@Override
	public void setColor( Getter.Float<float[]> iColores, float alfa){
		this.iColores = iColores;
		this.a = alfa;
		this.iAlfa = null;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setColor(org.sam.interpoladores.InterpoladorPolinomico3D, org.sam.interpoladores.InterpoladorDeValores)
	 */
	@Override
	public void setColor( Getter.Float<float[]> iColores, Getter.Float<Float> iAlfa){
		this.iColores = iColores;
		this.iAlfa = iAlfa;
	}
	
	public Particulas clone(){
		return new ParticulasPointSprites(this);
	}

	public void draw(GL gl) {
		float[] tmp = new float[1];
		gl.glGetFloatv( GL.GL_POINT_SIZE_MAX, tmp, 0 );
		float maxSize = tmp[0];

		if( maxSize > 64.0f )
			maxSize = 64.0f;

		gl.glPointSize( maxSize );

		gl.glPointParameterf( GL.GL_POINT_FADE_THRESHOLD_SIZE, 60.0f );
		gl.glPointParameterf( GL.GL_POINT_SIZE_MIN, 1.0f );
		gl.glPointParameterf( GL.GL_POINT_SIZE_MAX, maxSize );

		float quadratic[] =  { 1.0f, 0.0f, 0.01f };
		gl.glPointParameterfv( GL.GL_POINT_DISTANCE_ATTENUATION, quadratic, 0 );

		gl.glTexEnvf( GL.GL_POINT_SPRITE, GL.GL_COORD_REPLACE, GL.GL_TRUE );	    

		gl.glEnable( GL.GL_POINT_SPRITE );
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
		pos.rewind();
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, pos);
		col.rewind();
		gl.glColorPointer(4, GL.GL_FLOAT, 0, col);

		gl.glDrawArrays(GL.GL_POINTS, 0, nParticulas);

		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY); 
		gl.glDisable( GL.GL_POINT_SPRITE );
	}
}