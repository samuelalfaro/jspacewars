package org.sam.j3d;

import org.sam.interpoladores.Getter;

public class TunelMosaico extends Tunel{

	public TunelMosaico(int lados, int secciones, Getter.Float<float[]> interpolador, String pathTextura, float u1, float u2, float v1, float v2){
		super(lados, secciones, interpolador, pathTextura, u1, u2, v1, v2);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.j3d.Tunel#generarTexCoord(javax.vecmath.TexCoord2f[], int, int, float, float, float, float)
	 */
	public float[] generarTexCoord(int[] textCoordCaras, int lados, int secciones, float u1, float u2, float v1, float v2){
		float texCoordPoints[] = new float[(secciones+1)*2*2];
		for(int s =0, i=0; s<=secciones; s++){
			texCoordPoints[i++] = ((u2-u1)*s)/secciones;
			texCoordPoints[i++] = v1;
			texCoordPoints[i++] = ((u2-u1)*s)/secciones;
			texCoordPoints[i++] = v2;
		}
		
		for(int s=0, i=0; s<secciones; s++){
			boolean par = true;
			for(int l=0, off = s*2, tam=lados*2; l<tam; l++){
				textCoordCaras[i++] = par?off+1:off+0;
				textCoordCaras[i++] = par?off+3:off+2;
				textCoordCaras[i++] = par?off+2:off+3;
				textCoordCaras[i++] = par?off+0:off+1;
				par = !par;
			}
		}
//		boolean seccionPar =true;
//		for(int s=0, i=0; s<secciones; s++){
//			boolean ladoPar = true;
//			if (seccionPar)
//				for(int l=0, tam=lados*2; l<tam; l++){
//					textCoordCaras[i++] = ladoPar?1:0;
//					textCoordCaras[i++] = ladoPar?3:2;
//					textCoordCaras[i++] = ladoPar?2:3;
//					textCoordCaras[i++] = ladoPar?0:1;
//					ladoPar = !ladoPar;
//				}
//			else
//				for(int l=0, tam=lados*2; l<tam; l++){
//					textCoordCaras[i++] = ladoPar?3:2;
//					textCoordCaras[i++] = ladoPar?1:0;
//					textCoordCaras[i++] = ladoPar?0:1;
//					textCoordCaras[i++] = ladoPar?2:3;
//					ladoPar = !ladoPar;
//				}
//			seccionPar = !seccionPar;
//		}
		return texCoordPoints;
	}
}