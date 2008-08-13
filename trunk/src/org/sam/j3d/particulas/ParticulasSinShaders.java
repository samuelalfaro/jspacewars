package org.sam.j3d.particulas;

import java.nio.*;
import java.util.Random;

import javax.media.j3d.*;
import javax.vecmath.*;

import org.sam.interpoladores.Getter;
import org.sam.util.Imagen;
import org.sam.util.Modificador;

class ParticulasSinShaders extends Particulas{

	private static final Random aleatorio = new Random();
	
	private class ModificadorEmisionDiscreta implements GeometryUpdater, Modificador{
		private final GeometryArray geometria;
		private transient float iVidaT;
		
		ModificadorEmisionDiscreta(GeometryArray geometria){
			this.geometria = geometria;
			iVidaT = 0.0f;
		}

		private transient Point3f  p = new Point3f();
		private transient Vector3f v = new Vector3f();
		private transient Vector3f f = new Vector3f();
		
		public void updateData(Geometry geometry){
			GeometryArray ga = (GeometryArray)geometry;
		
			FloatBuffer pos = (FloatBuffer)ga.getCoordRefBuffer().getBuffer();
			FloatBuffer col = (FloatBuffer)ga.getColorRefBuffer().getBuffer();
			FloatBuffer tex = (FloatBuffer)ga.getTexCoordRefBuffer(0).getBuffer();
			
			Transform3D t = getTransform();
			for (int i=0, j=0, k=0, len=vidas.length; i < len; i++, j+=3, k+=12){
				
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
						pos.position(k);
						pos.put(0); pos.put(0); pos.put(0);
						pos.put(0); pos.put(0); pos.put(0);
						pos.put(0); pos.put(0); pos.put(0);
						pos.put(0); pos.put(0); pos.put(0);
						// Hacemos transparente la particula
						col.put((i<<2)+3,0.0f); //inidice color i*4 +3 para la componente alfa 
						continue;
					}
					//  La particula ha nacido en este intervalo
					if (vida <= iVidaT ){
						Vector  vec = emisor.getVector();			
						t.transform(vec.posicion);
						vec.direccion.scale(modulosVelocidad[i]);
						t.transform(vec.direccion);
						
						posIni[j]  =vec.posicion.x; diresVelocidad[j]   = vec.direccion.x;
						posIni[j+1]=vec.posicion.y; diresVelocidad[j+1] = vec.direccion.y;
						posIni[j+2]=vec.posicion.z; diresVelocidad[j+2] = vec.direccion.z;
					}
					f.set(fuerza);
					f.scale(vida/2);
					v.set(diresVelocidad[j],diresVelocidad[j+1],diresVelocidad[j+2]);
					v.add(f);
					v.scale(vida);
					p.set(posIni[j],posIni[j+1],posIni[j+2]);
					p.add(v);
					
					try{
						escalaX = iEscalaX.get(vida) * 0.5f;
						escalaY = iEscalaY.get(vida) * 0.5f;
					}catch(NullPointerException laEscalaNoEstaInterpolada){
					}
					
					pos.position(k);
					pos.put(p.x -escalaX); pos.put(p.y -escalaY); pos.put(p.z);
					pos.put(p.x +escalaX); pos.put(p.y -escalaY); pos.put(p.z);
					pos.put(p.x +escalaX); pos.put(p.y +escalaY); pos.put(p.z);
					pos.put(p.x -escalaX); pos.put(p.y +escalaY); pos.put(p.z);
					
					if(giros[i]!=0.0f){
						float alfa = giros[i]*vida; 
						float cosA = (float)Math.cos(alfa);
						float senA = (float)Math.sin(alfa);
						float p1 = 0.5f*cosA + 0.5f*senA;
						float p2 = 0.5f*cosA - 0.5f*senA;
						tex.position(i<<3);
						tex.put(0.5f - p2); tex.put(0.5f - p1);
						tex.put(0.5f + p1); tex.put(0.5f - p2);
						tex.put(0.5f + p2); tex.put(0.5f + p1);
						tex.put(0.5f - p1); tex.put(0.5f + p2);
					}
				
					try{
						float[] rgb = iColores.get(vida);
						r = (float)rgb[0]; g = (float)rgb[1]; b = (float)rgb[2];
					}catch(NullPointerException elColorNoEstaInterpolado){
					}
					try{
						a = iAlfa.get(vida);
					}catch(NullPointerException laOpacidadNoEstaInterpolada){
					}
					col.position(i<<2);
					col.put(r); col.put(g); col.put(b); col.put(a);
				}
			}
		}

		public boolean modificar(float steep){
			if(particulasActivas == 0){
				if(modoDeEmision == Particulas.EMISION_UNICA)
					return false;
				particulasActivas = nParticulas;
				for (int i = 0, len = vidas.length; i < len; i++)
				    vidas[i] = -((float)(nParticulas - i)/nParticulas)*rangoDeEmision;
			}
			iVidaT = iVida * steep;
			geometria.updateData(this);
			return true;
		}
	}
	
	private class ModificadorEmisionContinua implements GeometryUpdater, Modificador{
		private final GeometryArray geometria;
		private transient float iVidaT;
		
		ModificadorEmisionContinua(GeometryArray geometria){
			this.geometria = geometria;
			iVidaT = 0.0f;
		}

		private transient Point3f  p = new Point3f();
		private transient Vector3f v = new Vector3f();
		private transient Vector3f f = new Vector3f();
		
		public void updateData(Geometry geometry){
			GeometryArray ga = (GeometryArray)geometry;
			
			FloatBuffer pos = (FloatBuffer)ga.getCoordRefBuffer().getBuffer();
			FloatBuffer col = (FloatBuffer)ga.getColorRefBuffer().getBuffer();
			FloatBuffer tex = (FloatBuffer)ga.getTexCoordRefBuffer(0).getBuffer();
			
			Transform3D t = getTransform();
			for (int i=0, j=0, k=0, len=vidas.length; i < len; i++, j+=3, k+=12){
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
					Vector  vec = emisor.getVector();			
					t.transform(vec.posicion);
					vec.direccion.scale(modulosVelocidad[i]);
					t.transform(vec.direccion);
					
					posIni[j]  =vec.posicion.x; diresVelocidad[j]   = vec.direccion.x;
					posIni[j+1]=vec.posicion.y; diresVelocidad[j+1] = vec.direccion.y;
					posIni[j+2]=vec.posicion.z; diresVelocidad[j+2] = vec.direccion.z;
				}
				f.set(fuerza);
				f.scale(vida/2);
				v.set(diresVelocidad[j],diresVelocidad[j+1],diresVelocidad[j+2]);
				v.add(f);
				v.scale(vida);
				p.set(posIni[j],posIni[j+1],posIni[j+2]);
				p.add(v);
				
				try{
					escalaX = iEscalaX.get(vida) * 0.5f;
					escalaY = iEscalaY.get(vida) * 0.5f;
				}catch(NullPointerException laEscalaNoEstaInterpolada){
				}
				
				pos.position(k);
				pos.put(p.x -escalaX); pos.put(p.y -escalaY); pos.put(p.z);
				pos.put(p.x +escalaX); pos.put(p.y -escalaY); pos.put(p.z);
				pos.put(p.x +escalaX); pos.put(p.y +escalaY); pos.put(p.z);
				pos.put(p.x -escalaX); pos.put(p.y +escalaY); pos.put(p.z);
				
				if(giros[i]!=0.0f){
					float alfa = giros[i]*vida; 
					float cosA = (float)Math.cos(alfa);
					float senA = (float)Math.sin(alfa);
					float p1 = 0.5f*cosA + 0.5f*senA;
					float p2 = 0.5f*cosA - 0.5f*senA;
					tex.position(i<<3);
					tex.put(0.5f - p2); tex.put(0.5f - p1);
					tex.put(0.5f + p1); tex.put(0.5f - p2);
					tex.put(0.5f + p2); tex.put(0.5f + p1);
					tex.put(0.5f - p1); tex.put(0.5f + p2);
				}
			
				try{
					float rgb[] = iColores.get(vida);
					r = (float)rgb[0]; g = (float)rgb[1]; b = (float)rgb[2];
				}catch(NullPointerException elColorNoEstaInterpolado){
				}
				try{
					a = iAlfa.get(vida);
				}catch(NullPointerException laOpacidadNoEstaInterpolada){
				}
				col.position(i<<2);
				col.put(r); col.put(g); col.put(b); col.put(a);
			}
		}
		
		public boolean modificar(float steep){
			iVidaT = iVida * steep;
			geometria.updateData(this);
			return true;
		}
	}
	
	private final float[] vidas;
	private final float[] posIni;
	private final float[] diresVelocidad;
	private final float[] modulosVelocidad;
	private final float[] giros;
	
	private float escalaX;
	private Getter.Float<Float> iEscalaX;
	private float escalaY;
	private Getter.Float<Float> iEscalaY;
	private float r;
	private float g;
	private float b;
	private Getter.Float<float[]> iColores;
	private float a;
	private Getter.Float<Float> iAlfa;
	
	protected ParticulasSinShaders(int nParticulas){
		super(nParticulas);
		
		Appearance app = new Appearance();
		
		TextureUnitState tus[] = new TextureUnitState[2];
		TextureAttributes tAtt;
		tus[0] = new TextureUnitState();
		tAtt = new TextureAttributes();
			tAtt.setTextureMode(TextureAttributes.MODULATE);
		tus[0].setTextureAttributes(tAtt);
		tus[1] = new TextureUnitState();
		
		app.setTextureUnitState(tus);	
		
		this.setAppearance(app);
		
		vidas = new float[nParticulas];
		posIni = new float[nParticulas*3];
		diresVelocidad = new float[nParticulas*3];
		modulosVelocidad = new float[nParticulas];
		giros = new float[nParticulas];
		modificador = new ModificadorEmisionDiscreta((GeometryArray)this.getGeometry());
	}
	
	protected ParticulasSinShaders(ParticulasSinShaders me){
		super(me);
		vidas = new float[me.nParticulas];
		posIni = new float[me.nParticulas*3];
		diresVelocidad = new float[me.nParticulas*3];
		modulosVelocidad = new float[me.nParticulas];
		giros = new float[me.nParticulas];
		
		this.escalaX = me.escalaX;
		this.iEscalaX = me.iEscalaX;
		this.escalaY = me.escalaY;
		this.iEscalaY = me.iEscalaY;
		this.r = me.r;
		this.g = me.g;
		this.b = me.b;
		this.iColores = me.iColores;
		this.a = me.a;
		this.iAlfa = me.iAlfa;

		modificador = (me.modoDeEmision == Particulas.EMISION_CONTINUA) ?
				new ModificadorEmisionContinua( (GeometryArray)this.getGeometry() ):
				new ModificadorEmisionDiscreta( (GeometryArray)this.getGeometry() );
	}
	
	private static final int[] taSets = { 0 };
	
	protected GeometryArray crearGeometria(int nParticulas){
		
		IndexedQuadArray quadArray = new IndexedQuadArray(
				nParticulas*4, 
				GeometryArray.COORDINATES
				| GeometryArray.COLOR_4
				| GeometryArray.TEXTURE_COORDINATE_2
				| GeometryArray.BY_REFERENCE
				| GeometryArray.USE_NIO_BUFFER,
				taSets.length, taSets,
				nParticulas*4);
		
		final ByteOrder order =  ByteOrder.nativeOrder();
		final int nBytesFloat = 4;
		FloatBuffer floatBuffer;
		
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nParticulas * 4 * 3).order(order).asFloatBuffer();
		J3DBuffer posicionCoordBuff = new J3DBuffer(floatBuffer);
		quadArray.setCoordRefBuffer(posicionCoordBuff);
		
		int[] coordIndices = new int[nParticulas*4];
		for(int i= 0, len = coordIndices.length; i< len; i++)
			coordIndices[i]= i;
		quadArray.setCoordinateIndices(0,coordIndices);
		
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nParticulas * 4).order(order).asFloatBuffer();
		J3DBuffer colorBuff = new J3DBuffer(floatBuffer);
		quadArray.setColorRefBuffer(colorBuff);
		
		int[] colorIndices = new int[nParticulas*4];
		for(int i= 0, j =0, len = colorIndices.length; i< len; j++)
			colorIndices[i++]= colorIndices[i++]= colorIndices[i++]= colorIndices[i++]= j;
		quadArray.setColorIndices(0,colorIndices);
		
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nParticulas * 4 * 2).order(order).asFloatBuffer();
		float[] texturaCoord = {
				0.0f, 0.0f,
				1.0f, 0.0f,
				1.0f, 1.0f,
				0.0f, 1.0f
		};
		for(int i=0; i< nParticulas; i++)
			floatBuffer.put(texturaCoord,0,8);
		J3DBuffer texturaCoordBuff = new J3DBuffer(floatBuffer);
		quadArray.setTexCoordRefBuffer(0,texturaCoordBuff);
		quadArray.setTextureCoordinateIndices(0,0,coordIndices);
	
		quadArray.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
		quadArray.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
		
		return quadArray;
	}
	
	public void iniciar(){
		particulasActivas = nParticulas;
		for (int i = 0, len = vidas.length; i < len; i++){
		    vidas[i] = -((float)(nParticulas - i)/nParticulas)*rangoDeEmision;
			giros[i] = 
				((pGiro != 0.0f)
					? giro + (aleatorio.nextFloat()-0.5f) * pGiro
					: giro)
					* (sentidoGiroAleatorio
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
	
	public void setModoDeEmision(int modoDeEmision) {
		if(modoDeEmision < Particulas.EMISION_UNICA || modoDeEmision > Particulas.EMISION_CONTINUA)
			throw new IllegalArgumentException("El rango de emision debe estar comprendido entre EMISION_UNICA y EMISION_CONTINUA");
		
		if(this.modoDeEmision != modoDeEmision){
			if(modoDeEmision == Particulas.EMISION_CONTINUA)
				modificador = new ModificadorEmisionContinua( (GeometryArray)this.getGeometry() );
			else if(this.modoDeEmision == Particulas.EMISION_CONTINUA)
				modificador = new ModificadorEmisionDiscreta( (GeometryArray)this.getGeometry() );
		}
		this.modoDeEmision = modoDeEmision;
	}
	
	public void setEscala(float escala){
		setEscala(escala, escala);
	}
	
	public void setEscala(float escalaX, float escalaY){
		this.escalaX = escalaY;
		this.iEscalaX = null;
		this.escalaY = escalaY;
		this.iEscalaY = null;
	}

	public void setEscala(Getter.Float<Float> iEscalas){
		setEscala(iEscalas,iEscalas);
	}
	public void setEscala(Getter.Float<Float> iEscalaX, Getter.Float<Float> iEscalaY){
		this.iEscalaX = iEscalaX;
		this.iEscalaY = iEscalaY;
	}

	public void setColor( Color3f color, float alfa){
		setColor(color.x, color.y, color.z, alfa);
	}
	public void setColor( float r, float g, float b, float a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.iColores = null;
		this.a = a;
		this.iAlfa = null;
	}
	
	public void setColor( Color3f color, Getter.Float<Float> iAlfa){
		setColor(color.x, color.y, color.z, iAlfa);
	}
	public void setColor( float r, float g, float b, Getter.Float<Float> iAlfa){
		this.r = r;
		this.g = g;
		this.b = b;
		this.iColores = null;
		this.iAlfa = iAlfa;
	}

	public void setColor( Getter.Float<float[]> iColores, float alfa){
		this.iColores = iColores;
		this.a = alfa;
		this.iAlfa = null;
	}

	public void setColor( Getter.Float<float[]> iColores, Getter.Float<Float> iAlfa){
		this.iColores = iColores;
		this.iAlfa = iAlfa;
	}
	
	private static Texture falsaTextura = new Texture2D(Texture.BASE_LEVEL,Texture.RGBA,1,1);
	static{
		ImageComponent2D ic = new ImageComponent2D(ImageComponent.FORMAT_RGBA,1,1);
		ic.set(Imagen.toBufferedImage(Imagen.toImage(new int[]{0},1,1)));
		falsaTextura.setImage(0,ic);
		falsaTextura.setMinFilter(Texture.FASTEST);
		falsaTextura.setMagFilter(Texture.FASTEST);
	}
	
	public void setColorModulado(boolean colorModulado){
		Appearance app = getAppearance();

		TextureUnitState tus[] = app.getTextureUnitState();
		if(colorModulado){
			TextureAttributes tAtt;
			tAtt = new TextureAttributes();
				tAtt.setTextureMode(TextureAttributes.COMBINE);
				tAtt.setCombineAlphaMode(TextureAttributes.COMBINE_MODULATE);
				tAtt.setCombineAlphaSource(0, TextureAttributes.COMBINE_OBJECT_COLOR );
				tAtt.setCombineAlphaSource(1, TextureAttributes.COMBINE_TEXTURE_COLOR );
			tus[0].setTextureAttributes(tAtt);
			tAtt = new TextureAttributes();
				tAtt.setTextureBlendColor(1.0f,1.0f,1.0f,1.0f);
				tAtt.setTextureMode(TextureAttributes.COMBINE);
				tAtt.setCombineRgbMode(TextureAttributes.COMBINE_INTERPOLATE);
				tAtt.setCombineRgbSource(0, TextureAttributes.COMBINE_OBJECT_COLOR );
				tAtt.setCombineRgbSource(1, TextureAttributes.COMBINE_CONSTANT_COLOR );
				tAtt.setCombineRgbSource(2, TextureAttributes.COMBINE_PREVIOUS_TEXTURE_UNIT_STATE );
				tAtt.setCombineRgbFunction(2, TextureAttributes.COMBINE_SRC_ALPHA );
			tus[1].setTextureAttributes(tAtt);
			tus[1].setTexture(falsaTextura);
		}else{
			TextureAttributes tAtt;
			tAtt = new TextureAttributes();
				tAtt.setTextureMode(TextureAttributes.MODULATE);
			tus[0].setTextureAttributes(tAtt);
		}
	}

	public Particulas clone(){
		return new ParticulasSinShaders(this);
	}
}