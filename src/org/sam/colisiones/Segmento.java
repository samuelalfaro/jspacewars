package org.sam.colisiones;
/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Segmento {

	float x1, y1, x2, y2;
	
	public Segmento(){
		x1 = y1 = x2 = y2 = 0.0f;
	}
	
	public Segmento(float X1, float Y1, float X2, float Y2){
		x1 = X1;
		y1 = Y1;
		x2 = X2;
		y2 = Y2;
	}
	
	public void setValues(float X1, float Y1, float X2, float Y2){
		x1 = X1;
		y1 = Y1;
		x2 = X2;
		y2 = Y2;
	}
	
	/**
	 * Devuelve la distancia euclidea entre los puntos (X1,Y1)(X2,Y2)
	 *
	 * @param X1,&nbsp;Y1 Coordenadas del primer punto del segmento
	 * @param X2,&nbsp;Y2 Coordenadas del segundo punto del segmento
	 * 
	 * @return La distancia de un punto a otro
	 */

	public static double distancia(float X1, float Y1, float X2, float Y2) {
		X2 -= X1; // se almacena en X2 incremento de X
		Y2 -= Y1; // se almacena en Y2 incremento de Y
		return  Math.sqrt(X2 * X2 + Y2 * Y2);
	}

	public double distancia() {
		return  distancia(x1, y1, x2, y2);
	}
	
	/**
	 * Coprueba si el punto (PX,PY) esta en linea con el segmento (X1,Y1)(X2,Y2)
	 *
	 * Par ello se usa la ecuacion de la recta, considerando el origen el punto (X1,Y1)
	 * para simplificar calculos:  
	 *    
	 * Incremento de X= IncX = X2-X1
	 * Incremento de Y= IncY = Y2-Y1
	 * Coordenadas del punto:
	 *    PX = PX - X1
	 *    PY = PY - Y1
	 * 
	 * Considerando que el punto esta en linea con la recta se obtien la siguinte ecuacion:
	 *
	 *  IncY/incX * PX = PY
	 *  IncY * PX = PY * IncX
	 *  IncY * PX - IncX * PY = 0 
	 *
	 * Si el resultado es distinto de 0, es el valor que se considera Posicion Relativa Al Segmento
	 *
	 * @param X1,&nbsp;Y1 Coordenadas del primer punto del segmento
	 * @param X2,&nbsp;Y2 Coordenadas del segundo punto del segmento
	 * @param PX,&nbsp;PY Coordenadas del punto a evaluar
	 * 
	 * @return Un entero que vale
	 * 		 0 si el punto esta en la linea del segmento,
	 * 		 1 si el punto esta por debajo de la linea del segmento 
 	 * 		 2 si el punto esta por encima de la linea del segmento 
	 */

	private static int posicionRelaivaAlSegmento(float X1, float Y1,
			float X2, float Y2,
			float PX, float PY) {
		
		X2 -= X1; // se almacena en X2 incremento de X
		Y2 -= Y1; // se almacena en Y2 incremento de Y
		PX -= X1; // la posicion del punto en X se hace realitiva al primer punto
		PY -= Y1; // la posicion del punto en Y se hace realitiva al primer punto

		float prs = Y2 * PX - X2 * PY;
		return (prs < 0.0) ? 1 : ((prs > 0.0) ? 2 : 0);
	}

	/**
	 * Coprueba si el segmento  (X1,Y1)(X2,Y2) corta al segmento (X3,Y3)(X4,Y4) 
	 *
	 * @param X1,&nbsp;Y1 Coordenadas del primer punto del primer segmento
	 * @param X2,&nbsp;Y2 Coordenadas del segundo punto del primer segmento
	 *
	 * @param X3,&nbsp;Y3 Coordenadas del primer punto del segundo segmento
	 * @param X4,&nbsp;Y4 Coordenadas del segundo punto del segundo segmento
	 *
	 * @return Un booleano que vale
	 *		true si los segmentos se cortan
	 *		false en caso contrario
	 */

	public static boolean hayInterseccion(float X1, float Y1,
			float X2, float Y2,
			float X3, float Y3,
			float X4, float Y4) {

		return  ((	posicionRelaivaAlSegmento(X1, Y1, X2, Y2, X3, Y3) &
					posicionRelaivaAlSegmento(X1, Y1, X2, Y2, X4, Y4)	) == 0)
		&&
				((	posicionRelaivaAlSegmento(X3, Y3, X4, Y4, X1, Y1) &
					posicionRelaivaAlSegmento(X3, Y3, X4, Y4, X2, Y2)	) == 0);
	}
	
	public boolean hayInterseccion(float X3, float Y3, float X4, float Y4){
		return  hayInterseccion(x1, y1, x2, y2, X3, Y3, X4, Y4);
	}
	
	public boolean hayInterseccion(Segmento otro) {
		return  hayInterseccion(x1, y1, x2, y2, otro.x1, otro.y1, otro.x2, otro.y2);
	}
}