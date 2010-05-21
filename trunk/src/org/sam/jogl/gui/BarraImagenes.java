package org.sam.jogl.gui;

import javax.media.opengl.GL;

public class BarraImagenes extends GLComponent{
		
		private final Pixmap[] pixmaps;
		private final int[] valores;
		private float bordeH, bordeV;
		
		public BarraImagenes(int nValores, Pixmap[] pixmaps) throws IllegalArgumentException{
			if( nValores < 1 )
				throw new IllegalArgumentException();
			valores = new int[nValores];
			this.pixmaps = pixmaps;
		}
		
		public void setBorders(float bordeH, float bordeV ){
			this.bordeH = (bordeH < 0 || bordeH > (1.0f/(valores.length +1)))? 0.0f : bordeH;	
			this.bordeV = (bordeV < 0 || bordeV > .5f) ? 0.0f : bordeV;
		}
		
		public int getNValues(){
			return valores.length;
		}

		public int getValueAt(int index){
			return valores[index];
		}
		
		public void setValueAt(int index, int value){
			valores[index] = value;
		}
		
		public void draw(GL gl){
			int nImgs = valores.length;
			float ancho = x2 - x1;
			float anchoBorde = bordeH * ancho;
			float anchoImg = ( ancho - (nImgs + 1) * anchoBorde ) / nImgs;
			float alto = y2 - y1;
			float altoBorde = bordeV * alto;
			float altoImg = alto - 2 * altoBorde;

			int cont = 0;
			float x = anchoBorde + x1;
			float y = altoBorde + y1;
			float dx = anchoBorde + anchoImg;
			while( cont < nImgs ){
				try{
					pixmaps[valores[cont]].draw(gl, x, y, x+anchoImg, y+altoImg );
				}catch( ArrayIndexOutOfBoundsException e ){
					System.err.println(e);
				}catch( NullPointerException e ){
					System.err.println(e);
				}
				x += dx;
				cont++;
			}
		}
	}