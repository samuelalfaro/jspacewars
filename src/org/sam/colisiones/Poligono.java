package org.sam.colisiones;

public class Poligono{
	
	private static boolean testSegmentos1[] = new boolean [50];
	private static boolean testSegmentos2[] = new boolean [50];
	
	private float coordX[], coordY[];
	
	private float oX, oY;
	private float rotacion;
	private float escala;
	
	private LimiteRectangular limiteRectangular;
	
	public Poligono(float coordX[], float coordY[]){

		if( coordX == null || coordY == null || coordX.length != coordY.length ||  coordX.length < 3 )
			throw new IllegalArgumentException();
		
		if(coordX.length > testSegmentos1.length){
			testSegmentos1 = new boolean[ 3*coordX.length/2];
			testSegmentos2 = new boolean[ 3*coordX.length/2];
		}
		
		this.coordX = coordX;
		this.coordY = coordY;

		oX = 0.0f;
		oY = 0.0f;
		
		rotacion = 0.0f;
		escala = 1.0f;
		limiteRectangular = new LimiteRectangular(); 
		actualizarLimiteRectangular();
	}

	// Desplaza todos los puntos del poligono
	public void actualizarLimiteRectangular(){
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;

		for (int i = 0, nPuntos = coordX.length; i < nPuntos; i++) {
			float x = coordX[i];
			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			float y = coordY[i];
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}
		limiteRectangular.setSortedValues(minX,minY,maxX,maxY);
	}
	
	// Desplaza todos los puntos del poligono
	public void trasladar(float despX,float despY){
		despX -= oX;
		despY -= oY;
		for(int loop = 0, nLados = coordX.length; loop < nLados; loop++){
			coordX[loop] += despX;
			coordY[loop] += despY;
		}
		oX += despX;
		oY += despY;
	}

	// Rota todos los puntos del poligono desde el centro
	public void rotar(float alfa){
		alfa -= rotacion; 
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		for(int loop = 0, nLados = coordX.length; loop < nLados; loop++){
			float x = coordX[loop] - oX;
			float y = coordY[loop] - oY;
			coordX[loop] = x * cosAlfa - y * senAlfa + oX;
			coordY[loop] = x * senAlfa + y * cosAlfa + oY;
		}
		rotacion += alfa;
	}

	// Rota todos los puntos del poligono desde un punto dado
	public void rotar(float alfa, float pX, float pY){
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		for(int loop = 0, nLados = coordX.length; loop < nLados; loop++){
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
		for(int loop = 0, nLados = coordX.length; loop < nLados; loop++){
			coordX[loop] = (coordX[loop] - oX) * fEscala + oX;
			coordY[loop] = (coordY[loop] - oY) * fEscala + oY; 
		}
		escala *= fEscala;
	}

	// Escala todos los puntos del poligono desde el centro de con distinta escala
	public void escalar(float escalaX, float escalaY){
		for(int loop = 0, nLados = coordX.length; loop < nLados; loop++){
			coordX[loop] = (coordX[loop] - oX) * escalaX + oX;
			coordY[loop] = (coordY[loop] - oY) * escalaY + oY; 
		}
	}

	// Traslada, rota y escala
	public void transformar(float despX, float despY, float alfa, float escala){
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		despX = oX + despX;
		despY = oY + despY;
		for(int loop = 0, nLados = coordX.length; loop < nLados; loop++){
			float x = coordX[loop] - oX;
			float y = coordY[loop] - oY;
			coordX[loop] = (x * cosAlfa - y * senAlfa) * escala + despX;
			coordY[loop] = (x * senAlfa + y * cosAlfa) * escala + despY;
		}
		oX = despX;
		oY = despY;
	}
	
	public LimiteRectangular getLimiteRectangular(){
		return limiteRectangular;
	}
	
	private int nSegmentosEn(LimiteRectangular rectangulo, boolean[] testeados){
		final int nLados = coordX.length;
		if( limiteRectangular.equals(rectangulo) ){
			for(int i=0; i < nLados; i++)
				testeados[i] = true;
			return nLados;
		}
		
		int nSegmentosEnLaInterseccion = 0;
		int i = 0;
		for(int iSig = 1; iSig < nLados; i = iSig, iSig ++){
			if(	testeados[i] = rectangulo.hayInterseccion(
					coordX[i],coordY[i],
					coordX[iSig],coordY[iSig]) )
				nSegmentosEnLaInterseccion++;
		}
		if(	testeados[i] = rectangulo.hayInterseccion(
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
	boolean contiene(float x, float y) {
		int npoints = coordX.length;
//		if (npoints <= 2 || !getLimiteRectangular().contiene(x, y)) {
//			return false;
//		}
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

			float test1, test2;
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
	
	public boolean hayIntersecion( Poligono otro ){
		LimiteRectangular interseccion = limiteRectangular.interseccion(otro.getLimiteRectangular());
		if( interseccion == null)
			return false;
	
		int nSegIntPol1 = nSegmentosEn(interseccion, testSegmentos1);
		int nSegIntPol2 = otro.nSegmentosEn(interseccion, testSegmentos2);
		
		if( nSegIntPol1 == 0 && nSegIntPol2 == 0 )
			return false;
		
		if( (nSegIntPol1 == 0 && nSegIntPol2 != otro.getNLados()) || (nSegIntPol2 == 0 && nSegIntPol1 != getNLados()))
			return false;
		
		if( nSegIntPol1 == getNLados() &&  otro.contiene( coordX[0], coordY[0]) )
			return true;
		
		if( nSegIntPol2 == otro.getNLados() && contiene( otro.coordX[0], otro.coordY[0] ) )
			return true;
		
		if( nSegIntPol1 == 0 )
			return contiene( otro.coordX[0], otro.coordY[0] );
		if( nSegIntPol2 == 0 )
			return otro.contiene( coordX[0], coordY[0] );
		
		for(int i = 0, sig_i = 1, eva1 = 0; eva1 < nSegIntPol1; i = sig_i++){
			if (testSegmentos1[i]){
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
					if (testSegmentos2[j]){
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
	
	int getNLados(){
		return coordX.length;
	}

	boolean hayIntersecion( Poligono otro, java.util.List<Object> evaluados){
		
		evaluados.clear();
		
		LimiteRectangular interseccion = limiteRectangular.interseccion(otro.getLimiteRectangular());
		if( interseccion == null)
			return false;
		
		int nSegIntPol1 = nSegmentosEn(interseccion, testSegmentos1);
		int nSegIntPol2 = otro.nSegmentosEn(interseccion, testSegmentos2);
		
		if( nSegIntPol1 == 0 && nSegIntPol2 == 0 )
			return false;
		
		if( (nSegIntPol1 == 0 && nSegIntPol2 != otro.getNLados()) || (nSegIntPol2 == 0 && nSegIntPol1 != getNLados()))
			return false;
		
		if( nSegIntPol1 == getNLados() ){
			evaluados.add( new java.awt.geom.Point2D.Float( coordX[0], coordY[0] ));
			if( otro.contiene( coordX[0], coordY[0]) )
				return true;
		}
		
		if( nSegIntPol2 == otro.getNLados() ){
			evaluados.add( new java.awt.geom.Point2D.Float( otro.coordX[0], otro.coordY[0] ));
			if( contiene( otro.coordX[0], otro.coordY[0] ) )
				return true;
		}
		
		for(int i = 0, sig_i = 1, eva1 = 0; eva1 < nSegIntPol1; i = sig_i++){
			if (testSegmentos1[i]){
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
					if (testSegmentos2[j]){
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
							evaluados.add( new Segmento(p1X,p1Y,p2X,p2Y));
							evaluados.add( new Segmento(p3X,p3Y,p4X,p4Y));
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
	
	Segmento getSegmento(int pos){
		return getSegmento(pos, shared);
	}
	
	Segmento getSegmento(int pos, Segmento segmento){
		if( segmento == null )
			segmento = new Segmento();
		int sig = pos + 1;
		if( sig == coordX.length )
			sig = 0;
		segmento.setPoints(coordX[pos], coordY[pos], coordX[sig], coordY[sig]);
		return segmento;
	}
}	