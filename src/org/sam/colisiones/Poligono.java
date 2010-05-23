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
		
		int i=0, iSig = 1;
		while(iSig<nLados){
			if(	segmentosEnLaInterseccion[i] = rectangulo.hayInterseccion(
					coordX[i],coordY[i],
					coordX[iSig],coordY[iSig]) )
				nSegmentosEnLaInterseccion++;
			i++;
			iSig ++;
		}
		if(	segmentosEnLaInterseccion[i] = rectangulo.hayInterseccion(
				coordX[i],coordY[i],
				coordX[0],coordY[0]) )
			nSegmentosEnLaInterseccion++;
		return nSegmentosEnLaInterseccion;
	}
	
	public boolean hayIntersecion(Poligono otro){
		if( !limiteRectangular.hayInterseccion(otro.getLimiteRectangular()))
			return false;
		LimiteRectangular interseccion = limiteRectangular.interseccion(otro.getLimiteRectangular());
		
		int segmentos1EnLaInterseccion;
		if( (segmentos1EnLaInterseccion = calcularSegmentosEnRectangulo(interseccion)) == 0)
			return false;
		
		int segmentos2EnLaInterseccion;
		if( (segmentos2EnLaInterseccion = otro.calcularSegmentosEnRectangulo(interseccion)) == 0)
			return false;
		
		for(int i = 0, segEv1 = 0; segEv1 < segmentos1EnLaInterseccion; i++){
			if (segmentosEnLaInterseccion[i]){
				float p1X,p1Y,p2X,p2Y;
				p1X = coordX[i];
				p1Y = coordY[i];
				if(i == coordX.length){
					p2X = coordX[i+1];
					p2Y = coordY[i+1];
				}else{
					p2X = coordX[0];
					p2Y = coordY[0];
				}
				for(int j = 0, segEv2 = 0; segEv2 < segmentos2EnLaInterseccion; j++){
					if (otro.segmentosEnLaInterseccion[j]){
						float p3X,p3Y,p4X,p4Y;
						p3X = otro.coordX[j];
						p3Y = otro.coordY[j];
						if(j == otro.coordX.length){
							p4X = otro.coordX[j+1];
							p4Y = otro.coordY[j+1];
						}else{
							p4X = otro.coordX[0];
							p4Y = otro.coordY[0];
						}
						if(Segmento.hayInterseccion(p1X,p1Y,p2X,p2Y,p3X,p3Y,p4X,p4Y))
							return true;
						segEv2++;
					}
				}
				segEv1++;
			}
		}
		return false;
	}
	
	public Segmento getSegmento(int pos){
		return getSegmento(pos, null);
	}
	
	public Segmento getSegmento(int pos, Segmento segmento){
		if( segmento == null )
			segmento = new Segmento();
		int sig = pos + 1;
		if( sig == coordX.length )
			sig = 0;
		segmento.setValues(coordX[pos], coordY[pos], coordX[sig], coordY[sig]);
		return segmento;
	}
}	