package org.sam.j3d;

import java.util.Enumeration;
import java.util.Random;

import javax.media.j3d.*;

import com.sun.j3d.utils.image.TextureLoader;

public class Caleidoscopio extends Shape3D{
	
	private static final Random aleatorio = new Random();
	private IndexedGeometryArray geometria;
	
	private class ModificadorTexturaCoord implements GeometryUpdater{
//		private long tiempoAnterior;
//		private long tiempoActual;
		
		float inc = 0.001f;
		float texCoord[];
		float incs[];
		
		public ModificadorTexturaCoord(float textCoord[]){
			this.texCoord = textCoord;
			incs = new float[textCoord.length];
			for(int i=0; i < incs.length; i++)
				incs[i] = iniciar();
//			tiempoAnterior = System.currentTimeMillis();
		}
		
		public void updateData(Geometry geometry){
//			tiempoActual = System.currentTimeMillis();
//			long delta = tiempoActual - tiempoAnterior;
			for(int i=0, tam = texCoord.length; i < tam; i++){
				float newVal = texCoord[i] + incs[i]; 
				if (newVal < 0 || newVal > 1)
					incs[i] = rebotar(incs[i]);
				else
					texCoord[i] = newVal;
			}
//			tiempoAnterior = tiempoActual;
		}
		
		float iniciar(){
			return (aleatorio.nextFloat()-0.5f) * inc;
		}
		
		float rebotar(float x){
			return -Math.signum(x)*(aleatorio.nextFloat()+0.1f)* inc;
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
	
	public Caleidoscopio(int lados, String pahtTextura){

		Appearance app = new Appearance();
		Material mat = new Material();
		mat.setDiffuseColor(1.0f,1.0f,1.0f);
		mat.setLightingEnable(false);
		app.setMaterial(mat);
		TextureLoader pTexture = new TextureLoader(pahtTextura,TextureLoader.GENERATE_MIPMAP|TextureLoader.BY_REFERENCE, null);
		app.setTexture(pTexture.getTexture());

		geometria = new IndexedTriangleArray(lados*2+1, 
				GeometryArray.COORDINATES | 
				GeometryArray.TEXTURE_COORDINATE_2 |
				GeometryArray.BY_REFERENCE ,
				lados*2*3);

		float[] posicionCoord =  new float[(lados*2+1)*3];
		posicionCoord[0]= 0.0f;
		posicionCoord[1]= 0.0f;
		posicionCoord[2]= 0.0f;
		for(int i=0, j=3, tam=lados*2; i<tam; i++){
			posicionCoord[j++]= (float)Math.cos(Math.PI*i/lados +Math.PI/2);
			posicionCoord[j++]= (float)Math.sin(Math.PI*i/lados +Math.PI/2);
			posicionCoord[j++]= 0.0f;
		}
		geometria.setCoordRefFloat(posicionCoord);
		
		int[] carasCoord = new int[lados*2*3];
		for(int i = 0, l=1, tam=lados*2; l<=tam; l++){
			carasCoord[i++] = l;
			carasCoord[i++] = (l+1>tam)?1:l+1;
			carasCoord[i++] = 0;
		}
		geometria.setCoordinateIndices(0,carasCoord);
		
		float texCoordPoints[] = new float[6];
		for(int i= 0; i < 6; i++)
			texCoordPoints[i] = aleatorio.nextFloat();
		geometria.setTexCoordRefFloat(0,texCoordPoints);
		
		int[] texturaCoord = new int[lados*2*3];
		boolean par = true;
		for(int i = 0, l=0, tam=lados*2; l<tam; l++){
			texturaCoord[i++] = par?1:2;
			texturaCoord[i++] = par?2:1;
			texturaCoord[i++] = 0;
			par = !par;
		}
		geometria.setTextureCoordinateIndices(0,0,texturaCoord);
		
		geometria.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
		geometria.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
		
		this.setAppearance(app);
		this.setGeometry(geometria);
		comportamiento = new Comportamiento(new ModificadorTexturaCoord(texCoordPoints));
	}
	
	public Behavior getComportamiento(){
		return comportamiento;
	}
}