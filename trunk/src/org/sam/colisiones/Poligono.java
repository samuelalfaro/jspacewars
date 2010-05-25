package org.sam.colisiones;

import java.awt.geom.Point2D;
import java.util.Random;

public class Poligono{
	
	private float coordX[], coordY[];
	private boolean segmentosEnLaInterseccion[];
	
	private float oX, oY;
	private float rotacion;
	private float escala;
	
	private LimiteRectangular limiteRectangular;
	
	// Crea un poligono aleatorio convexo
	public Poligono(int nLados){
		if(nLados < 3)
			throw new IllegalArgumentException();
		
		Random r=new Random();
		
		coordX = new float[nLados];
		coordY = new float[nLados];
		segmentosEnLaInterseccion = new boolean[nLados]; 
		oX = 0.0f;
		oY = 0.0f;
		
		double iAng = Math.PI*2 / nLados;
		double ang = iAng * (r.nextDouble() - 0.5);
		int loop = 0;
		while(loop < nLados){

			double dist = r.nextDouble();
		
			double val  = Math.cos(ang)*dist;
			coordX[loop]= (float)val; 
			oX += val;
			
			val  = Math.sin(ang)*dist;
			coordY[loop]= (float)val;
			oY += val;
			
			loop++;
			ang = iAng * (loop + r.nextDouble() - 0.5 );
		}
		oX /= nLados;
		oY /= nLados;
		rotacion = 0.0f;
		escala = 1.0f;
		trasladar(0.0f,0.0f);
		limiteRectangular = new LimiteRectangular(); 
		limiteRectangular.calcular(coordX, coordY, nLados);
	}

	// Desplaza todos los puntos del poligono
	public void actualizarLimiteRectangular(){
		limiteRectangular.calcular(coordX, coordY, coordX.length);
	}
	
	// Desplaza todos los puntos del poligono
	public void trasladar(float despX,float despY){
		despX -= oX;
		despY -= oY;
		int nLados = coordX.length;
		for(int loop = 0; loop < nLados; loop++){
			coordX[loop] += despX;
			coordY[loop] += despY;
		}
		oX += despX;
		oY += despY;
	}

	// Rota todos los puntos del poligono desde el centro
	public void rotar(float alfa){
		alfa -= rotacion; 
		int nLados = coordX.length;
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		for(int loop = 0; loop < nLados; loop++){
			float x = coordX[loop] - oX;
			float y = coordY[loop] - oY;
			coordX[loop] = x * cosAlfa - y * senAlfa + oX;
			coordY[loop] = x * senAlfa + y * cosAlfa + oY;
		}
		rotacion += alfa;
	}

	// Rota todos los puntos del poligono desde un punto dado
	public void rotar(float alfa, float pX, float pY){
		int nLados = coordX.length;
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		for(int loop = 0; loop < nLados; loop++){
			float x = coordX[loop] - pX;
			float y = coordY[loop] - pY;
			coordX[loop] = x * cosAlfa - y * senAlfa + pX;
			coordY[loop] = x * senAlfa + y * cosAlfa + pY;
		}
		float x = oX - pX;
		float y = oY - pY;
		oX = x * cosAlfa - y * senAlfa + pX;
		oY = x * senAlfa + y * cosAlfa + pY;
	}
	
	// Escala todos los puntos del poligono desde el centro de forma uniforme
	public void escalar(float fEscala){
		if (fEscala == 0) return;
		fEscala /= escala;
		int nLados = coordX.length;
		for(int loop = 0; loop < nLados; loop++){
			coordX[loop] = (coordX[loop] - oX) * fEscala + oX;
			coordY[loop] = (coordY[loop] - oY) * fEscala + oY; 
		}
		escala *= fEscala;
	}

	// Escala todos los puntos del poligono desde el centro de con distinta escala
	public void escalar(float escalaX, float escalaY){
		int nLados = coordX.length;
		for(int loop = 0; loop < nLados; loop++){
			coordX[loop] = (coordX[loop] - oX) * escalaX + oX;
			coordY[loop] = (coordY[loop] - oY) * escalaY + oY; 
		}
	}

	// Traslada, rota y escala
	public void transformar(float despX, float despY, float alfa, float escala){
		int nLados = coordX.length;
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		despX = oX + despX;
		despY = oY + despY;
		for(int loop = 0; loop < nLados; loop++){
			float x = coordX[loop] - oX;
			float y = coordY[loop] - oY;
			coordX[loop] = (x * cosAlfa - y * senAlfa) * escala + despX;
			coordY[loop] = (x * senAlfa + y * cosAlfa) * escala + despY;
		}
		oX = despX;
		oY = despY;
	}
	
	public int getNLados(){
		return coordX.length;
	}

	// Devuelve el centro del poligono
	public Point2D getCentro(){
		return new Point2D.Float(oX,oY);
	}
	
	public LimiteRectangular getLimiteRectangular(){
		return limiteRectangular;
	}
	
	private int calcularSegmentosEnRectangulo(LimiteRectangular rectangulo){
		int nSegmentosEnLaInterseccion = 0;
		int nLados = coordX.length;
		
		int i = 0, iSig = 1;
		while( iSig < nLados ){
			if(	segmentosEnLaInterseccion[i] = rectangulo.hayInterseccion(
					coordX[i],coordY[i],
					coordX[iSig],coordY[iSig]) )
				nSegmentosEnLaInterseccion++;
			i = iSig; iSig ++;
		}
		if(	segmentosEnLaInterseccion[i] = rectangulo.hayInterseccion(
				coordX[i],coordY[i],
				coordX[0],coordY[0]) )
			nSegmentosEnLaInterseccion++;
		return nSegmentosEnLaInterseccion;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 * @see java.awt.Polygon#contains(double, double)
	 */
	public boolean contiene(float x, float y) {
		int npoints = coordX.length;
		if (npoints <= 2 || !getLimiteRectangular().contiene(x, y)) {
			return false;
		}
		int hits = 0;

		float lastx = coordX[npoints - 1];
		float lasty = coordY[npoints - 1];
		float curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
			curx = coordX[i];
			cury = coordY[i];

			if (cury == lasty) {
				continue;
			}

			float leftx;
			if (curx < lastx) {
				if (x >= lastx) {
					continue;
				}
				leftx = curx;
			} else {
				if (x >= curx) {
					continue;
				}
				leftx = lastx;
			}

			double test1, test2;
			if (cury < lasty) {
				if (y < cury || y >= lasty) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - curx;
				test2 = y - cury;
			} else {
				if (y < lasty || y >= cury) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - lastx;
				test2 = y - lasty;
			}

			if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
				hits++;
			}
		}
		return ((hits & 1) != 0);
    }
	
	public boolean hayIntersecion(Poligono otro){
		return hayIntersecion(otro, null);
	}
	
	public boolean hayIntersecion(Poligono otro, int segmentos[]){
		LimiteRectangular interseccion = limiteRectangular.interseccion(otro.getLimiteRectangular());
		if( interseccion == null)
			return false;
		
		int nSegIntPol1;
		if( (nSegIntPol1 = calcularSegmentosEnRectangulo(interseccion)) == 0)
			return false;
		
		int nSegIntPol2;
		if( (nSegIntPol2 = otro.calcularSegmentosEnRectangulo(interseccion)) == 0)
			return false;
		
		for(int i = 0, sig_i = 1, eva1 = 0; eva1 < nSegIntPol1; i = sig_i++){
			if (segmentosEnLaInterseccion[i]){
				float p1X,p1Y,p2X,p2Y;
				p1X = coordX[i];
				p1Y = coordY[i];
				if(sig_i < coordX.length){
					p2X = coordX[sig_i];
					p2Y = coordY[sig_i];
				}else{
					p2X = coordX[0];
					p2Y = coordY[0];
				}
				for(int j = 0, sig_j=1, eva2 = 0; eva2 < nSegIntPol2; j = sig_j++){
					if (otro.segmentosEnLaInterseccion[j]){
						float p3X,p3Y,p4X,p4Y;
						p3X = otro.coordX[j];
						p3Y = otro.coordY[j];
						if(sig_j < otro.coordX.length){
							p4X = otro.coordX[sig_j];
							p4Y = otro.coordY[sig_j];
						}else{
							p4X = otro.coordX[0];
							p4Y = otro.coordY[0];
						}
						if(Segmento.hayInterseccion(p1X,p1Y,p2X,p2Y,p3X,p3Y,p4X,p4Y)){
							if(segmentos != null){
								segmentos[0] = i;
								segmentos[1] = j;
							}
							return true;
						}
						eva2++;
					}
				}
				eva1++;
			}
		}
		return false;
	}
	
	private final Segmento shared = new Segmento();
	
	public Segmento getSegmento(int pos){
		return getSegmento(pos, shared);
	}
	
	public Segmento getNewSegmento(int pos){
		return getSegmento(pos, null);
	}
	
	public Segmento getSegmento(int pos, Segmento segmento){
		if( segmento == null )
			segmento = new Segmento();
		int sig = pos + 1;
		if( sig == coordX.length )
			sig = 0;
		segmento.setPoints(coordX[pos], coordY[pos], coordX[sig], coordY[sig]);
		return segmento;
	}
}	