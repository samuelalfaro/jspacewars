package org.sam.j3d;

import java.util.Enumeration;
import java.util.Random;

import javax.media.j3d.*;

import org.sam.interpoladores.Getter;

import com.sun.j3d.utils.image.TextureLoader;

public abstract class Tunel extends Shape3D{

	private IndexedGeometryArray geometria;
	
	private class ModicadorSeccion{

		int inicio, fin;
		
		Getter.Float<float[]> interpolador;
		float factor;
		float key, incKey;
	
		float despXant, despYant, despXact, despYact;
		
		/**
		 * 
		 * @param inicio
		 * @param fin
		 * @param interpolador
		 * @param factor
		 * @param key
		 * @param incKey
		 */
		ModicadorSeccion( int inicio, int fin, Getter.Float<float[]> interpolador, float factor, float key, float incKey ){
			this.inicio = inicio;
			this.fin = fin;
			
			this.interpolador = interpolador;
			this.factor = factor;		
			this.key = key;
			this.incKey = incKey;

			despXant = 0.0f;
			despYant = 0.0f;
		}
		
		public void modificar(float seccion[]){
			if((key = key + incKey) > 1)
				key -= 1;
			float values[] = interpolador.get(key);
			despXact = values[0];
			despYact = values[1];
			float despX = (despXact-despXant)*factor;
			float despY = (despYact-despYant)*factor;
			for(int i= inicio; i < fin; i++){
				seccion[i++] += despX;
				seccion[i++] += despY;
			}
			despXant = despXact;
			despYant = despYact;
		}
	}
	
	private class ModificadorTexturas{
		float incX, incY;

		ModificadorTexturas(float incX, float incY){
			this.incX = incX;
			this.incY = incY;
		}
		
		public void modificar(float texCoord[]){
			if(incX != 0 && incY != 0){
				for (int i= 0, t = texCoord.length; i < t;){
					texCoord[i++] += incX;
					texCoord[i++] += incY;
				}
			}else if(incX!=0){
				for (int i= 0, t = texCoord.length; i < t; i+=2)
					texCoord[i] += incX;
			}else if (incY!=0){
				for (int i= 1, t = texCoord.length; i < t; i+=2)
					texCoord[i] += incY; 
			}
		}
	}

	@SuppressWarnings("unused")
	private class ModificadorTexturas2 extends ModificadorTexturas{
		private Random aleatorio;
		private float inc1Y, inc2Y;

		ModificadorTexturas2(float incX, float incY){
			super(incX, incY);
			aleatorio = new Random();
			this.inc1Y = iniciar();
			this.inc2Y = iniciar();
		}
		
		public void modificar(float texCoord[]){
			boolean par = true;
			for (int i= 0; i< texCoord.length;){
				texCoord[i++] += incX;
				float newVal = texCoord[i] + (par?inc1Y:inc2Y); 
				if (newVal < 0 || newVal > 1)
					if (par){
						inc1Y = rebotar(inc1Y);
						newVal = texCoord[i] + inc1Y;
					}else{
						inc2Y = rebotar(inc2Y);
						newVal = texCoord[i] + inc2Y;
					}
				texCoord[i++] = newVal;
				par = !par;
			}
		}
		
		float iniciar(){
			return Math.signum(aleatorio.nextFloat()-0.5f)*(aleatorio.nextFloat()+0.1f)* incY;
		}
		
		float rebotar(float x){
			return -Math.signum(x)*(aleatorio.nextFloat()+0.1f)* incY;
		}
	}
	
	private class Modificador implements GeometryUpdater{
		@SuppressWarnings("unused")
		private long tiempoAnterior;
		private long tiempoActual;
		
		ModicadorSeccion[] modicadoresSeccion;
		ModificadorTexturas modificadorTexturas;

		public Modificador(ModicadorSeccion[] modicadoresSeccion, ModificadorTexturas modificadorTexturas){
			this.modicadoresSeccion = modicadoresSeccion;
			this.modificadorTexturas = modificadorTexturas;
			tiempoAnterior = System.currentTimeMillis();
		}
		
		public void updateData(Geometry geometry){
			try{
				GeometryArray ga = (GeometryArray)geometry;
				tiempoActual = System.currentTimeMillis();
				try{
					float seccines[] = ga.getCoordRefFloat();
					for (int s =0, tam = modicadoresSeccion.length; s < tam; s++)
						modicadoresSeccion[s].modificar(seccines);
				}catch(NullPointerException noSeModificanLasSecciones){
				}
				try{
					modificadorTexturas.modificar(ga.getTexCoordRefFloat(0));
				}catch(NullPointerException noSeModificanLasTexturas){
				}
				tiempoAnterior = tiempoActual;
			}catch(ClassCastException noEsUnGeometryArray){
			}
		}
	}

	private class Comportamiento extends Behavior{
		private WakeupCriterion onElapsedFrames; 
		private GeometryUpdater modificador;

		public Comportamiento(GeometryUpdater modificador){
			this.modificador = modificador;
			onElapsedFrames = new WakeupOnElapsedFrames(0);
		}
		
		public void initialize(){
			this.wakeupOn(onElapsedFrames);
		}
		
		@SuppressWarnings("unchecked")
		public void processStimulus(Enumeration criteria) {
			geometria.updateData(modificador);
			this.wakeupOn(onElapsedFrames);
		}
	}
	
	private Behavior   comportamiento;
	
	public Tunel(int lados, int secciones, Getter.Float<float[]> interpolador, String pathTextura, float u1, float u2, float v1, float v2){
		
		this.setGeometry(geometria = crearGeometria(lados, secciones, u1, u2, v1, v2) );
		this.setAppearance(crearApariencia(pathTextura));

		ModicadorSeccion[] modicadoresSeccion = null;
		if (interpolador != null){
			modicadoresSeccion = new ModicadorSeccion[secciones+1];
			for(int s=0; s<=secciones; s++)
				modicadoresSeccion[s] = new ModicadorSeccion(
						s*lados*2*3,
						(s+1)*lados*2*3,
						interpolador,
						(float)(secciones-s)/secciones,
						(float)s/secciones,
						0.001f);
		}
		ModificadorTexturas modificadorTexturas = new ModificadorTexturas(-0.005f, 0.001f);
		
		comportamiento = new Comportamiento(new Modificador(modicadoresSeccion, modificadorTexturas));
	}
	
	private IndexedGeometryArray crearGeometria(int lados, int secciones, float u1, float u2, float v1, float v2){
		IndexedGeometryArray geometria = new IndexedQuadArray(lados*2*secciones, 
				GeometryArray.COORDINATES 
				|GeometryArray.TEXTURE_COORDINATE_2 
				|GeometryArray.BY_REFERENCE
				,lados*2*secciones*4);

		float[] posicionCoord =  new float[lados*2*(secciones+1)*3]; 
		
		for(int s=secciones, i=0; s>=0; s--)
			for(int l=0, tam=lados*2; l<tam; l++){
				posicionCoord[i++] = (float)Math.cos(Math.PI*l/lados +Math.PI/2);
				posicionCoord[i++] = (float)Math.sin(Math.PI*l/lados +Math.PI/2);
				posicionCoord[i++] = -50.0f*(s)/secciones;
			}
		geometria.setCoordRefFloat(posicionCoord);
		
		int[] carasCoord = new int[lados*secciones*8];
		for(int s=0, i=0; s<secciones; s++){
			for(int l=0, tam=lados*2; l<tam; l++){
				carasCoord[i++] = l+s*tam;
				carasCoord[i++] = l+(s+1)*tam;
				carasCoord[i++] = (l+1== tam)?(s+1)*tam:l+1+(s+1)*tam;
				carasCoord[i++] = (l+1== tam)?s*tam:l+1+s*tam;
			}
		}
		geometria.setCoordinateIndices(0,carasCoord);
		
		int[] texturaCoord = new int[lados*secciones*8];
		float texCoordPoints[] = generarTexCoord(texturaCoord, lados, secciones, u1, u2, v1, v2);
		                    
		geometria.setTexCoordRefFloat(0,texCoordPoints);
		geometria.setTextureCoordinateIndices(0,0,texturaCoord);
		
		geometria.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
		geometria.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
		
		return geometria;
	}
	
	private static Appearance crearApariencia(String pathTextura){
		Appearance app = new Appearance();
		Material mat = new Material();
		mat.setDiffuseColor(1.0f,1.0f,1.0f);
		mat.setLightingEnable(false);
		app.setMaterial(mat);
		TextureLoader pTexture = new TextureLoader(pathTextura,TextureLoader.GENERATE_MIPMAP|TextureLoader.BY_REFERENCE, null);
		app.setTexture(pTexture.getTexture());
		
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(pa);
		
		return app;
	}
	
	public final Behavior getBehavior(){
		return comportamiento;
	}
	
	/**
	 * Este m�todo genera las coordenadas de textura de las caras, y devuelve los puntos de coordenadas de textura
	 * empleados.
	 * 
	 * @param textCoordCaras Vector donde se almacenan indices de las coordenadas de textura de cada una de las caras.
	 * @param lados Lados del tunel.
	 * @param secciones Seccones del tunel.
	 * @param u1 Inicio de las coordenadas u de textura.
	 * @param u2 Fin de las coordenadas u de textura.
	 * @param v1 Inicio de las coordenadas v de textura.
	 * @param v2 Fin de las coordenadas v de textura.
	 * @return Puntos de coordenadas de textura empleados para generar las coordenadas de textura de las caras.
	 */
	public abstract float[] generarTexCoord(int[] textCoordCaras, int lados, int secciones, float u1, float u2, float v1, float v2 );
}