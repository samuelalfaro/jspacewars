package org.sam.j3d;

import org.sam.interpoladores.Getter;

public class TunelCilindrico extends Tunel{

	public TunelCilindrico(int lados, int secciones, Getter.Float<float[]> interpolador, String pathTextura, float u1, float u2, float v1, float v2){
		super(lados, secciones, interpolador, pathTextura, u1, u2, v1, v2);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.j3d.Tunel#generarTexCoord(javax.vecmath.TexCoord2f[], int, int, float, float, float, float)
	 */
	public float[] generarTexCoord(int[] textCoordCaras, int lados, int secciones, float u1, float u2, float v1, float v2){
		float texCoordPoints[] = new float[(lados*2+1)*(secciones+1)*2];
		
		for(int s=secciones, i=0; s>=0; s--)
			for(int l=0, tam=lados*2; l<=tam; l++){
				texCoordPoints[i++] =(u2-u1)*s/secciones +u1;
				texCoordPoints[i++] =(v2-v1)*l/tam +v1;
		}
		
		for(int s=secciones-1, i=0; s>=0; s--){
			for(int l=0, tam=lados*2; l<tam; l++){
				textCoordCaras[i++] = l+(s+1)*(tam+1);
				textCoordCaras[i++] = l+s*(tam+1);
				textCoordCaras[i++] = l+1+s*(tam+1);
				textCoordCaras[i++] = l+1+(s+1)*(tam+1);
			}
		}
		return texCoordPoints;
	}
}