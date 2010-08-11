package org.sam.colisiones;
/**
 * @author Samuel
 *
 */
public class Segmento {

	float x1, y1, x2, y2;
	
	public Segmento(){
		x1 = y1 = x2 = y2 = 0.0f;
	}
	
	public Segmento(float x1, float y1, float x2, float y2){
		setPoints(x1, y1, x2, y2);
	}
	
	public void setPoints(float x1, float y1, float x2, float y2){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public void setPoint1(float x1, float y1){
		this.x1 = x1;
		this.y1 = y1;
	}
	
	public void setPoint2(float x2, float y2){
		this.x2 = x2;
		this.y2 = y2;
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
	 *      Comprueba si el punto (x2,y2) forma parte de la recta definida por el segmento (x0,y0)(x1,y1).
	 *<br/>
	 *<br/> Par ello se usa la ecuacion de la recta, considerando el origen el punto (x0,y0)
	 *      para simplificar calculos:
	 *<br/><i> 
	 *<br/> Incremento de X= IncX = x1-x0
	 *<br/> Incremento de Y= IncY = y1-y0
	 *<br/> Coordenadas del punto:
	 *<br/>    x' = x2 - x0
	 *<br/>    y' = y2 - y0
	 *<br/></i>
	 *<br/> Considerando que el punto forma parte de la recta se obtien la siguinte ecuacion:
	 *<br/><i>
	 *<br/>  IncY/incX * x' = y'
	 *<br/>  IncY * x' = y' * IncX
	 *<br/>  IncY * x' - IncX * y' = 0
	 *<br/></i>
	 *<br/> Si el resultado es distinto de 0, es el valor que se considera Posicion Relativa Al Segmento
	 *
	 * @param x0,&nbsp;y0 Coordenadas del primer punto del segmento
	 * @param x1,&nbsp;y1 Coordenadas del segundo punto del segmento
	 * @param x2,&nbsp;y2 Coordenadas del punto a evaluar
	 * 
	 * @return Un entero que vale
	 * 		 0 si el punto esta en la linea del segmento,
	 * 		 1 si el punto esta por debajo de la linea del segmento 
 	 * 		 2 si el punto esta por encima de la linea del segmento 
	 */
	private static int isLeft( float x0, float y0, float x1, float y1, float x2, float y2 ){
		float pr = (y1 - y0)*(x2 - x0) - (x1 -x0) * (y2 - y0);
		return pr == 0.0f ? 0 : pr < 0.0f ? 1 : 2;
	}

	/**
	 * Comprueba si el segmento  (X1,Y1)(X2,Y2) corta al segmento (X3,Y3)(X4,Y4) 
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

	public static boolean hayInterseccion(
			float X1, float Y1, float X2, float Y2,
			float X3, float Y3, float X4, float Y4) {
	    return 
	    	( isLeft(X1, Y1, X2, Y2, X3, Y3) != isLeft(X1, Y1, X2, Y2, X4, Y4) ) && 
	    	( isLeft(X3, Y3, X4, Y4, X1, Y1) != isLeft(X3, Y3, X4, Y4, X2, Y2) );
	}
	
	public boolean hayInterseccion(float X3, float Y3, float X4, float Y4){
		return hayInterseccion(x1, y1, x2, y2, X3, Y3, X4, Y4);
	}
	
	public boolean hayInterseccion(Segmento otro) {
		return hayInterseccion(x1, y1, x2, y2, otro.x1, otro.y1, otro.x2, otro.y2);
	}
	
	public boolean hayInterseccion(LimiteRectangular rectangulo) {
		return rectangulo.hayInterseccion(this);
	}
}