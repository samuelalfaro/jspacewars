/* 
 * Poligono.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.colisiones;

import org.sam.elementos.Prototipo;

/**
 * Clase que define un polígono.
 */
public class Poligono implements Prototipo<Poligono>{
	
	/**
	 * Función que calcula el valor de la menor potencia de {@code b} que es mayor que {@code n}.
	 * @param n Valor a evualuar.
	 * @param b Base de la potencia.
	 * @return Valor calculado.
	 * @throws IllegalArgumentException Si {@code n < 0 || b < 0}.
	 */
	private static int nextPowOf(int n, int b){
		if (n < 0 || b < 0)
			throw new IllegalArgumentException();
		if (n == 0)
			return 1;
		if (n < b)
			return b;
		return (int)Math.pow( b, Math.floor( Math.log(n) / Math.log(b) ) + 1.0 );
	}
	
	/** 
	 * Vectores de booleanos estáticos, dónde se almacenan la posición de los 
	 * segmentos que están dentro de la interesección de los límites de polígonos.
	 * De esta forma se evita comparar uno a uno todos los segmentos de un polígono
	 * con los segmentos del otro polígono.
	 * Se crean con un tamañano inicial de 32 posiciones y se incrementará este tamaño
	 * si hay un nuevo polígono con más segmentos. 
	 */
	private static transient
	boolean testSegmentos1[] = new boolean [32], testSegmentos2[] = new boolean [32];
	
	private final transient Poligono prototipo;
	private final int nLados;
	private final float coordX[], coordY[];
	private float posX, posY;
	
	private final transient LimiteRectangular limiteRectangular;
	
	/**
	 * Constructor que crea un poligono formado por los puntos que se pasan como parámetros.
	 * @param coordX Coordenadas en el eje X de los puntos.
	 * @param coordY Coordenadas en el eje Y de los puntos.
	 */
	public Poligono(float coordX[], float coordY[]){

		if( coordX == null || coordY == null || coordX.length != coordY.length ||  coordX.length < 3 )
			throw new IllegalArgumentException();
		
		this.prototipo = null;
		this.nLados = coordX.length;
		this.coordX = coordX;
		this.coordY = coordY;
		this.posX = 0.0f;
		this.posY = 0.0f;
		this.limiteRectangular = new LimiteRectangular(); 
		actualizarLimiteRectangular();
		
		if(this.nLados > testSegmentos1.length){
			int newLength = nextPowOf( this.nLados, 2 );
			testSegmentos1 = new boolean[ newLength ];
			testSegmentos2 = new boolean[ newLength ];
		}
	}
	
	private Poligono(Poligono prototipo){
		this.prototipo = prototipo;
		this.nLados = prototipo.nLados;
		this.coordX = new float[this.nLados];
		System.arraycopy(prototipo.coordX, 0, this.coordX, 0, this.nLados);
		this.coordY = new float[this.nLados];
		System.arraycopy(prototipo.coordY, 0, this.coordY, 0, this.nLados);
		this.posX = 0.0f;
		this.posY = 0.0f;	
		this.limiteRectangular = new LimiteRectangular(); 
		actualizarLimiteRectangular();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Poligono clone(){
		return new Poligono(this);
	}

	/**
	 * Método que actualiza el {@code LimiteRectangular} que contiene este {@code Poligono}.
	 */
	public void actualizarLimiteRectangular(){
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;

		for (int i = 0; i < nLados; i++) {
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
	
	/**
	 * Desplaza todos los puntos del polígono.
	 * 
	 * @param despX Desplazamiento en el eje X.
	 * @param despY Desplazamiento en el eje Y.
	 */
	public void trasladar(float despX,float despY){
		float incX = despX - posX;
		float incY = despY - posY;
		for(int loop = 0; loop < nLados; loop++){
			coordX[loop] += incX;
			coordY[loop] += incY;
		}
		limiteRectangular.xII += incX;
		limiteRectangular.xSD += incX;
		limiteRectangular.yII += incY;
		limiteRectangular.ySD += incY;
		posX = despX;
		posY = despY;
	}

	/**
	 * Rota todos los puntos del polígono {@code alfa} radianes desde el origen de coodenadas {@code (0,0)}.
	 * @param alfa Ángulo de rotación en radianes.
	 */
	public void rotar(float alfa){
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		for(int loop = 0; loop < nLados; loop++){
			float x = prototipo.coordX[loop];
			float y = prototipo.coordY[loop];
			coordX[loop] = x * cosAlfa - y * senAlfa;
			coordY[loop] = x * senAlfa + y * cosAlfa;
		}
		this.posX = 0.0f;
		this.posY = 0.0f;
	}

	/**
	 * Rota todos los puntos del polígono {@code alfa} radianes desde el punto {@code (pX,pY)}.
	 * @param alfa Ángulo de rotación en radianes.
	 * @param pX Posición X del origen de rotación.
	 * @param pY Posición Y del origen de rotación.
	 */
	public void rotar(float alfa, float pX, float pY){
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		for(int loop = 0; loop < nLados; loop++){
			float x = prototipo.coordX[loop] - pX;
			float y = prototipo.coordY[loop] - pY;
			coordX[loop] = x * cosAlfa - y * senAlfa + pX;
			coordY[loop] = x * senAlfa + y * cosAlfa + pY;
		}
	}
	
	/**
	 * Escala uniformemente todos los puntos del polígono.
	 * 
	 * @param escala Valor de escalado.
	 */
	public void escalar(float escala){
		escalar(escala, escala);
	}

	/**
	 * Escala de forma no uniforme todos los puntos del polígono.
	 * 
	 * @param escalaX Valor de escalado en X.
	 * @param escalaY Valor de escalado en Y.
	 */
	public void escalar(float escalaX, float escalaY){
		for(int loop = 0; loop < nLados; loop++){
			coordX[loop] = prototipo.coordX[loop] * escalaX;
			coordY[loop] = prototipo.coordY[loop] * escalaY;
		}
	}

	/**
	 * Rota, escala y traslada, en este orden, todos los puntos del polígono.
	 * 
	 * Rota todos los puntos del polígono {@code alfa} radianes desde el origen de coodenadas {@code (0,0)}.
	 * Escala uniformemente todos los puntos del polígono.
	 * 
	 * @param alfa Ángulo de rotación en radianes.
	 * @param escala Valor de escalado.
	 * @param despX Desplazamiento en el eje X.
	 * @param despY Desplazamiento en el eje Y.
	 */
	public void transformar( float alfa, float escala, float despX, float despY ){
		float cosAlfa = (float)Math.cos(alfa);
		float senAlfa = (float)Math.sin(alfa);
		for(int loop = 0; loop < nLados; loop++){
			float x = prototipo.coordX[loop];
			float y = prototipo.coordY[loop];
			coordX[loop] = (x * cosAlfa - y * senAlfa) * escala + despX;
			coordY[loop] = (x * senAlfa + y * cosAlfa) * escala + despY;
		}
	}
	
	/**
	 * @return El número de lados del polígono.
	 */
	public int getNLados(){
		return nLados;
	}
	
	/**
	 * @return El {@code LimiteRectangular} que contiene todos los puntos del polígono.
	 */
	public LimiteRectangular getLimites(){
		return limiteRectangular;
	}
	
	private int nSegmentosEn(LimiteRectangular rectangulo, boolean[] testeados){

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
	 * Función que evalúa si un punto está en el interior del polígono.</br>
	 * El código de esta función está basado en {@linkplain java.awt.Polygon#contains(double, double)}.
	 * 
	 * @param x Coordenada X del punto a evaluar.
	 * @param y Coordenada Y del punto a evaluar.
	 * @return <ul>
	 * <li>{@code true}: si el punto {@code (x,y)} está en el interior del polígono.</li>
	 * <li>{@code false}: en caso contrario.</li>
	 * </ul>
	 * @see java.awt.Polygon#contains(double, double)
	 */
	boolean contiene(float x, float y) {

//		if (npoints <= 2 || !getLimiteRectangular().contiene(x, y)) {
//			return false;
//		}
		int hits = 0;

		float lastx = coordX[nLados - 1];
		float lasty = coordY[nLados - 1];
		float curx, cury;

		// Recorrer los lados del poligono
		for (int i = 0; i < nLados; lastx = curx, lasty = cury, i++) {
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
	
	/**
	 * Función que evalúa si hay una colisión con otro {@code Poligono}.
	 * @param otro {@code Poligono} con el que se hace la comprobación.
	 * @return <ul>
	 * <li>{@code true}: si hay colisión.</li>
	 * <li>{@code false}: en caso contrario.</li>
	 * </ul>
	 */
	public boolean hayColision(Poligono otro){
		
		LimiteRectangular interseccion = limiteRectangular.interseccion(otro.getLimites());
		if( interseccion == null)
			return false;
		
		int nSegIntPol1 = nSegmentosEn(interseccion, testSegmentos1);
		int nSegIntPol2 = otro.nSegmentosEn(interseccion, testSegmentos2);
		
		if( nSegIntPol1 == 0 && nSegIntPol2 == 0 )
			return false;
		
		if( (nSegIntPol1 == 0 && nSegIntPol2 != otro.nLados) || (nSegIntPol2 == 0 && nSegIntPol1 != nLados))
			return false;
		
		if( nSegIntPol1 == nLados &&  otro.contiene( coordX[0], coordY[0]) )
			return true;
		
		if( nSegIntPol2 == otro.nLados && contiene( otro.coordX[0], otro.coordY[0] ) )
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
	
	/**
	 * Método para test, igual al método {@linkplain #hayColision(Poligono)}.
	 * @param otro {@code Poligono} con el que se hace la comprobación.
	 * @param evaluados Lista de objetos donde se almacenan los puntos y segmentos de los polígonos evaluados
	 * durante la comprobación de la colisión.
	 * @return <ul>
	 * <li>{@code true}: si hay colisión.</li>
	 * <li>{@code false}: en caso contrario.</li>
	 * </ul>
	 */
	boolean hayColision(Poligono otro, java.util.List<Object> evaluados){

		evaluados.clear();
		
		LimiteRectangular interseccion = limiteRectangular.interseccion(otro.getLimites());
		if( interseccion == null)
			return false;
		
		int nSegIntPol1 = nSegmentosEn(interseccion, testSegmentos1);
		int nSegIntPol2 = otro.nSegmentosEn(interseccion, testSegmentos2);
		
		if( nSegIntPol1 == 0 && nSegIntPol2 == 0 )
			return false;
		
		if( (nSegIntPol1 == 0 && nSegIntPol2 != otro.nLados) || (nSegIntPol2 == 0 && nSegIntPol1 != nLados))
			return false;
		
		if( nSegIntPol1 == nLados ){
			evaluados.add( new java.awt.geom.Point2D.Float( coordX[0], coordY[0] ));
			if( otro.contiene( coordX[0], coordY[0]) )
				return true;
		}
		
		if( nSegIntPol2 == otro.nLados ){
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
	
	/**
	 * {@code Segmento} compartido, para evitar crear múltiples instancias al llamar al método {@linkplain #getSegmento(int)}.
	 */
	private final transient Segmento shared = new Segmento();
	
	/**
	 * Método que devuelve el {@code Segmento} del polígono que está en la posición {@code index}.
	 * <p><u>Atención:</u> Este método no genera nuevas instancias y siempre devuelve el 
	 * {@code Segmento} {@linkplain #shared} con los valores correspondientes a la posición solicitada.</p>
	 * @param index Posición del {@code Segmento} solicitado.
	 * @return El {@code Segmento} solicitado.
	 * @see #getSegmento(int, Segmento)
	 */
	Segmento getSegmento(int index){
		return getSegmento(index, shared);
	}
	
	/**
	 * Método que devuelve el {@code Segmento} del polígono que está en la posición {@code index}.
	 * @param index Posición del {@code Segmento} solicitado.
	 * @param segmento Instancia donde se almacenarán los valores del {@code Segmento} solicitado. En caso de ser nulo
	 * se generará una nueva instancia.
	 * @return El {@code Segmento} solicitado.
	 */
	Segmento getSegmento(int index, Segmento segmento){
		if( segmento == null )
			segmento = new Segmento();
		int sig = index + 1;
		if( sig == coordX.length )
			sig = 0;
		segmento.setPoints(coordX[index], coordY[index], coordX[sig], coordY[sig]);
		return segmento;
	}
}
