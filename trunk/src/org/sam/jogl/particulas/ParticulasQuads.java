package org.sam.jogl.particulas;

import java.nio.*;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.*;

import org.sam.interpoladores.Getter;
import org.sam.util.Modificador;

import com.sun.opengl.util.BufferUtil;

class ParticulasQuads extends Particulas{

	private static final Random aleatorio = new Random();
	
	private class ModificadorParticulas implements Modificador{

		private void setPos(
				float p11x, float p11y, 
				float p12x, float p12y,
				float p1z
				){
			pos.put(p11x); pos.put(p11y); pos.put(p1z);
			pos.put(p11x); pos.put(p12y); pos.put(p1z);
			pos.put(p12x); pos.put(p12y); pos.put(p1z);
			pos.put(p12x); pos.put(p11y); pos.put(p1z);
		}
		
		private void setPos(
				float p11x, float p11y, 
				float p12x, float p12y,
				float p1z,
				float p21x, float p21y,
				float p22x, float p22y,
				float p2z 
				){
			pos.put(p11x); pos.put(p11y); pos.put(p1z);
			pos.put(p12x); pos.put(p12y); pos.put(p1z);
			pos.put(p21x); pos.put(p21y); pos.put(p2z);
			pos.put(p22x); pos.put(p22y); pos.put(p2z);
		}
		
		public boolean modificar(float steep){
			
			if(particulasActivas == 0){
				if(emision == Emision.UNICA)
					return false;
				particulasActivas = nParticulas;
				for (int i = 0, len = vidas.length; i < len; i++)
//					vidas[i] = -((float)(i+1)/nParticulas)*rangoDeEmision;
				    vidas[i] = -((float)(nParticulas - i)/nParticulas)*rangoDeEmision;
			}
			
			float modelview[] = new float[16];
			GLU.getCurrentGL().glGetFloatv(GL.GL_MODELVIEW_MATRIX, modelview,0);
			
			Vector3f vDir = new Vector3f();
			Point3f p1 = new Point3f();
			Point3f p2 = new Point3f();
			Vector2f vDirProj = new Vector2f();

			Matrix4f model_view_matrix = new Matrix4f(modelview);
			model_view_matrix.transpose();

			float projection[] = new float[16];
			GLU.getCurrentGL().glGetFloatv(GL.GL_PROJECTION_MATRIX, projection,0);
			
//			for(int i=0; i< 16; ) 
//			System.out.println(projection[i++]+" \t"+projection[i++]+" \t"+projection[i++]+" \t"+projection[i++]);

			float fn1 = projection[10];
			float fn2 = projection[14];
			
//			GL gl = GLU.getCurrentGL();
//			gl.glMatrixMode( GL.GL_MODELVIEW );
//			gl.glPushMatrix();
//			gl.glLoadIdentity();
//
//			gl.glMatrixMode(GL.GL_PROJECTION);
//			gl.glPushMatrix();
//			gl.glLoadIdentity();
//			
//			gl.glDisable(GL.GL_TEXTURE_2D);
			
			float iVidaT = iVida * steep;
			
			Matrix4f t = getTransform();
			
			for (int i=0, j=0, k=0, len=vidas.length; i < len; i++, j+=3, k+=12){
				vidas[i] += iVidaT;
				float vida = vidas[i];
				
				// La particula todavia no ha nacido
				if(vida<=0)
					continue;
				// La particula ha muerto en este intervalo
				if (vida >= 1.0f){
					if(emision != Emision.CONTINUA){
						particulasActivas--;
						// Reducimos el tamaño de la particula a 0
						pos.position(k);
						pos.put(0); pos.put(0); pos.put(0);
						pos.put(0); pos.put(0); pos.put(0);
						pos.put(0); pos.put(0); pos.put(0);
						pos.put(0); pos.put(0); pos.put(0);
						vidas[i] -=  (float)Math.floor(vida) + 1.0f;
						continue;
					}
					// Emision continua se regenera
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
				
				//p = 1/2(f*t^2) + v*t + p0; se usa la vida como variable de tiempo.
				float x = ((fuerza.x * vida)/2 + diresVelocidad[j])*vida + posIni[j];
				float y = ((fuerza.y * vida)/2 + diresVelocidad[j+1])*vida + posIni[j+1];
				float z = ((fuerza.z * vida)/2 + diresVelocidad[j+2])*vida + posIni[j+2];
				
				if(iRadio != null){
					radio = iRadio.get(vida);
				}
				if(iSemiEje != null){
					semiEje = iSemiEje.get(vida);
				}
				
				float tmp1 = radio;
				float tmp2 = tmp1;
				
				if(semiEje != 0.0f){
					vDir.set(
							fuerza.x * vida + diresVelocidad[j],
							fuerza.y * vida + diresVelocidad[j+1],
							fuerza.z * vida + diresVelocidad[j+2]
					);
					vDir.normalize();

//					col.position(i<<4);
//					col.put(0.5f*vDir.x + 0.5f); col.put(0.5f*vDir.y + 0.5f); col.put(0.5f*vDir.z + 0.5f); col.put(1.0f);
//					col.put(0.5f*vDir.x + 0.5f); col.put(0.5f*vDir.y + 0.5f); col.put(0.5f*vDir.z + 0.5f); col.put(1.0f);
//					col.put(0.5f*vDir.x + 0.5f); col.put(0.5f*vDir.y + 0.5f); col.put(0.5f*vDir.z + 0.5f); col.put(1.0f);
//					col.put(0.5f*vDir.x + 0.5f); col.put(0.5f*vDir.y + 0.5f); col.put(0.5f*vDir.z + 0.5f); col.put(1.0f);
					
					vDir.scale(semiEje);
					p1.set( x, y, z ); p1.sub(vDir);
					p2.set( x, y, z ); p2.add(vDir);

					model_view_matrix.transform(p1);
					
					float sx1 = -projection[0] / p1.z;
					float sy1 = -projection[5] / p1.z;
					p1.x = -projection[8] + p1.x * sx1;
					p1.y = -projection[9] + p1.y * sy1;
					p1.z = -fn2/p1.z - fn1;
					
					model_view_matrix.transform(p2);

					float sx2 = -projection[0] / p2.z;
					float sy2 = -projection[5] / p2.z;
					p2.x = -projection[8] + p2.x * sx2;
					p2.y = -projection[9] + p2.y * sy2;
					p2.z = -fn2/p2.z - fn1;
					
					vDirProj.set( p2.x - p1.x, p2.y - p1.y);
					if(vDirProj.x != 0.0f || vDirProj.y != 0.0f){
						vDirProj.normalize();
						tmp1 *= vDirProj.x - vDirProj.y;
						tmp2 *= vDirProj.x + vDirProj.y;
					}
					
//					pos.put(p1.x - tmp1*sx1); pos.put(p1.y - tmp2*sy1); pos.put(p1.z);
//					pos.put(p1.x - tmp2*sx1); pos.put(p1.y + tmp1*sy1); pos.put(p1.z);
//					pos.put(p2.x + tmp1*sx2); pos.put(p2.y + tmp2*sy2); pos.put(p2.z);
//					pos.put(p2.x + tmp2*sx2); pos.put(p2.y - tmp1*sy2); pos.put(p2.z);
					
					setPos(
							p1.x - tmp1*sx1, p1.y - tmp2*sy1,
							p1.x - tmp2*sx1, p1.y + tmp1*sy1,
							p1.z,
							p2.x + tmp1*sx2, p2.y + tmp2*sy2,
							p2.x + tmp2*sx2, p2.y - tmp1*sy2,
							p2.z
					);
					
//					gl.glBegin(GL.GL_LINE_STRIP);
//					gl.glColor4f(0.5f - 0.5f*vDir.x,0.5f - 0.5f*vDir.y, 0.5f - 0.5f*vDir.z, 1.0f);
//					gl.glVertex2f(p1.x, p1.y);
//					gl.glVertex2f(p2.x, p2.y);
//					gl.glEnd();
//					gl.glBegin(GL.GL_LINE_STRIP);
//					gl.glColor4f(0.5f - 0.5f*vDir.x,0.5f - 0.5f*vDir.y, 0.5f - 0.5f*vDir.z, 1.0f);
//					gl.glVertex2f(p1.x - tmp1*sx1, p1.y - tmp2*sy1);
//					gl.glVertex2f(p1.x - tmp2*sx1, p1.y + tmp1*sy1);
//					gl.glVertex2f(p2.x + tmp1*sx2, p2.y + tmp2*sy2);
//					gl.glVertex2f(p2.x + tmp2*sx2, p2.y - tmp1*sy2);
//					gl.glVertex2f(p1.x - tmp1*sx1, p1.y - tmp2*sy1);
//					gl.glEnd();
				}else{
					p1.set( x, y, z );
					model_view_matrix.transform(p1);
					
					float sx = -projection[0] / p1.z;
					float sy = -projection[5] / p1.z;
					
					p1.x = -projection[8] + p1.x * sx;
					p1.y = -projection[9] + p1.y * sy;
					p1.z = -fn2/p1.z - fn1;
					
					sx *= radio;
					sy *= radio;
					
//					pos.put(p1.x - sx); pos.put(p1.y - sy); pos.put(p1.z);
//					pos.put(p1.x - sx); pos.put(p1.y + sy); pos.put(p1.z);
//					pos.put(p1.x + sx); pos.put(p1.y + sy); pos.put(p1.z);
//					pos.put(p1.x + sx); pos.put(p1.y - sy); pos.put(p1.z);
					
					setPos(
							p1.x - sx, p1.y - sy,
							p1.x + sx, p1.y + sy,
							p1.z
					);
				}
				
				if(iColores != null || iAlfa != null){
					if(iColores != null){
						float rgb[] = iColores.get(vida);
						r = rgb[0]; g = rgb[1]; b = rgb[2];
					}
					if(iAlfa != null)
						a = iAlfa.get(vida);
					
					col.position(i<<4);

					col.put(r); col.put(g); col.put(b); col.put(a);
					col.put(r); col.put(g); col.put(b); col.put(a);
					col.put(r); col.put(g); col.put(b); col.put(a);
					col.put(r); col.put(g); col.put(b); col.put(a);
				}
				
				if( velGiro != 0.0f ){
					float alfa = giros[i]*vida; 
					float cosA = (float)(Math.cos(alfa)*0.5);
					float senA = (float)(Math.sin(alfa)*0.5);
					tmp1 = cosA + senA;
					tmp2 = cosA - senA;
					tex.position(i<<3);
					tex.put(0.5f - tmp1); tex.put(0.5f - tmp2);
					tex.put(0.5f - tmp2); tex.put(0.5f + tmp1);
					tex.put(0.5f + tmp1); tex.put(0.5f + tmp2);
					tex.put(0.5f + tmp2); tex.put(0.5f - tmp1);
				}
			}
//			gl.glMatrixMode( GL.GL_MODELVIEW );
//			gl.glPopMatrix();
//			gl.glMatrixMode(GL.GL_PROJECTION);
//			gl.glPopMatrix();
//			gl.glEnable(GL.GL_TEXTURE_2D);
			return true;
		}
	}
	
	private final transient float[] vidas;
	private final transient float[] posIni;
	private final transient float[] diresVelocidad;
	private final transient float[] modulosVelocidad;
	private final transient float[] giros;
	
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
	
	protected ParticulasQuads(int nParticulas){
		super(nParticulas);
		
		vidas = new float[nParticulas];
		posIni = new float[nParticulas*3];
		diresVelocidad = new float[nParticulas*3];
		modulosVelocidad = new float[nParticulas];
		giros = new float[nParticulas];
		
		radio = 1.0f;
		semiEje = 0.0f;

		pos = ByteBuffer.allocateDirect(nParticulas*4*3*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		col = ByteBuffer.allocateDirect(nParticulas*4*4*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		tex = ByteBuffer.allocateDirect(nParticulas*4*2*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		modificador = new ModificadorParticulas();
	}
	
	protected ParticulasQuads(ParticulasQuads me){
		super(me);
		vidas = new float[me.nParticulas];
		posIni = new float[me.nParticulas*3];
		diresVelocidad = new float[me.nParticulas*3];
		modulosVelocidad = new float[me.nParticulas];
		giros = new float[me.nParticulas];
		
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

		pos = ByteBuffer.allocateDirect(nParticulas*4*3*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		col = ByteBuffer.allocateDirect(nParticulas*4*4*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		tex = ByteBuffer.allocateDirect(nParticulas*4*2*BufferUtil.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		modificador = new ModificadorParticulas();
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#iniciar()
	 */
	@Override
	public void iniciar(){
		particulasActivas = nParticulas;
		
		float[] posIni = new float[]{
				0,0,0,
				0,0,0,
				0,0,0,
				0,0,0 };
		pos.clear();
		
		float[] colIni = new float[]{
				r,g,b,a,
				r,g,b,a,
				r,g,b,a,
				r,g,b,a	};
		col.clear();
		
		float[] texIni = new float[]{
				0,0,
				0,1,
				1,1,
				1,0	};
		tex.clear();

		for (int i = 0; i < nParticulas; i++){
		    //vidas[i] = -((float)(nParticulas - i)/nParticulas)*rangoDeEmision;
			vidas[i] = -((float)(i+1)/nParticulas)*rangoDeEmision;
			giros[i] = ((pVelGiro != 0.0f) ?
					velGiro	+ (aleatorio.nextFloat() - 0.5f) * pVelGiro :
					velGiro)
			* (sentidoVelGiroAleatorio ? 
					(aleatorio.nextBoolean() ? 1 : -1) :
					1);
			modulosVelocidad[i] =
				((pVelocidad != 0.0f)
					? velocidad + (aleatorio.nextFloat()-0.5f) * pVelocidad
					: velocidad)
					* (sentidoVelocidadAleatorio
						? (aleatorio.nextBoolean()?1:-1)
						: 1);
			pos.put(posIni);
			col.put(colIni);
			tex.put(texIni);
		}
		pos.rewind();
		col.rewind();
		tex.rewind();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setModoDeEmision(int)
	 */
	@Override
	public void setEmision(Emision emision) {
		this.emision = emision;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(float)
	 */
	@Override
	public void setRadio(float radio) {
		this.radio = radio;
		this.iRadio = null;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setRadio(org.sam.interpoladores.Getter.Float)
	 */
	@Override
	public void setRadio(Getter.Float<Float> radio) {
		this.iRadio = radio;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setSemiEje(float)
	 */
	@Override
	public void setSemiEje(float semiEje) {
		this.semiEje = semiEje;
		this.iSemiEje = null;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.particulas.Particulas#setSemiEje(org.sam.interpoladores.Getter.Float)
	 */
	@Override
	public void setSemiEje(Getter.Float<Float> semiEje) {
		this.iSemiEje = semiEje;
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
		return new ParticulasQuads(this);
	}

	public void draw(GL gl) {

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

		pos.rewind();
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, pos);
		col.rewind();
		gl.glColorPointer(4, GL.GL_FLOAT, 0, col);
		tex.rewind();
		gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, tex);
		
		gl.glDrawArrays(GL.GL_QUADS, 0, nParticulas * 4);
		
		gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
	}
}