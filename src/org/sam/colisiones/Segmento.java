package org.sam.colisiones;

/**
 * Clase que define un segmento formado por dos puntos.
 */
public class Segmento {

	/**
	 * Coordenadas de los puntos que forman el {@code Segmento}.
	 */
	float x1, y1, x2, y2;
	
	/**
	 * Constructor que crea un {@code Segmento} por defecto, con sus dos puntos en las coordenadas {@code (0, 0)}.
	 */
	public Segmento(){
		x1 = y1 = x2 = y2 = 0.0f;
	}
	
	/**
	 * Constructor que crea un {@code Segmento} definido por los puntos {@code (x1,y1)} y {@code (x2,y2)}.
	 * @param x1 Coodenada X del primer punto.
	 * @param y1 Coodenada Y del primer punto.
	 * @param x2 Coodenada X del segundo punto.
	 * @param y2 Coodenada Y del segundo punto.
	 */
	public Segmento(float x1, float y1, float x2, float y2){
		setPoints(x1, y1, x2, y2);
	}
	
	/**
	 * Método que asigna los coordenadas de los puntos que forman el {@code Segmento}.
	 * 
	 * @param x1 Coodenada X del primer punto.
	 * @param y1 Coodenada Y del primer punto.
	 * @param x2 Coodenada X del segundo punto.
	 * @param y2 Coodenada Y del segundo punto.
	 */
	public void setPoints(float x1, float y1, float x2, float y2){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/**
	 * Método que asigna los coordenadas del primer punto del {@code Segmento}.
	 * 
	 * @param x1 Coodenada X del primer punto.
	 * @param y1 Coodenada Y del primer punto.
	 */
	public void setPoint1(float x1, float y1){
		this.x1 = x1;
		this.y1 = y1;
	}
	
	/**
	 * Método que asigna los coordenadas del segundo punto del {@code Segmento}.
	 * 
	 * @param x2 Coodenada X del segundo punto.
	 * @param y2 Coodenada Y del segundo punto.
	 */
	public void setPoint2(float x2, float y2){
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/**
	 * Devuelve la distancia euclidea entre los puntos (X1,Y1)(X2,Y2)
	 *
	 * @param X1 Coordenada X del primer punto del segmento.
	 * @param Y1 Coordenada Y del primer punto del segmento.
	 * @param X2 Coordenada X del segundo punto del segmento.
	 * @param Y2 Coordenada Y del segundo punto del segmento.
	 * 
	 * @return La distancia de un punto a otro
	 */
	public static double distancia(float X1, float Y1, float X2, float Y2) {
		X2 -= X1; // se almacena en X2 incremento de X
		Y2 -= Y1; // se almacena en Y2 incremento de Y
		return  Math.sqrt(X2 * X2 + Y2 * Y2);
	}

	/**
	 * @return La distancia entre los puntos del segmento.
	 */
	public double distancia() {
		return  distancia(x1, y1, x2, y2);
	}
	
	/**
	 * Comprueba si el punto (x2,y2) forma parte de la recta definida por el segmento (x0,y0)(x1,y1).
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
	 * @param x0 Coordenada X del primer punto del {@code Segmento}.
	 * @param y0 Coordenada Y del primer punto del {@code Segmento}.
	 * @param x1 Coordenada X del segundo punto del {@code Segmento}.
	 * @param y1 Coordenada Y del segundo punto del {@code Segmento}.
	 * @param x2 Coordenada X del del punto a evaluar.
	 * @param y2 Coordenada Y del del punto a evaluar.
	 * 
	 * @return <ul>
	 * 		 <li>{@code 0} si el punto esta en la línea del {@code Segmento}.</li>
	 * 		 <li>{@code 1} si el punto esta por debajo de la línea del {@code Segmento}.</li>
 	 * 		 <li>{@code 2} si el punto esta por encima de la línea del {@code Segmento}.</li>
 	 * </ul>
	 */
	private static int isLeft( float x0, float y0, float x1, float y1, float x2, float y2 ){
		float pr = (y1 - y0)*(x2 - x0) - (x1 -x0) * (y2 - y0);
		return pr == 0.0f ? 0 : pr < 0.0f ? 1 : 2;
	}

	/**
	 * Comprueba si el {@code Segmento} (X1,Y1)(X2,Y2) corta al {@code Segmento} (X3,Y3)(X4,Y4). 
	 *
	 * @param X1 Coordenada X del primer punto del primer {@code Segmento}.
	 * @param Y1 Coordenada Y del primer punto del primer {@code Segmento}.
	 * @param X2 Coordenada X del segundo punto del primer {@code Segmento}.
	 * @param Y2 Coordenada Y del segundo punto del primer {@code Segmento}.
	 * @param X3 Coordenada X del primer punto del segundo {@code Segmento}.
	 * @param Y3 Coordenada Y del primer punto del segundo {@code Segmento}.
	 * @param X4 Coordenada X del segundo punto del segundo {@code Segmento}.
	 * @param Y4 Coordenada Y del segundo punto del segundo {@code Segmento}.
	 *
	 * @return <ul>
	 * 		 <li>{@code true} si los segmentos se cortan.
	 *		 <li>{@code false} en caso contrario.
	 *</ul>
	 */
	public static boolean hayInterseccion(
			float X1, float Y1, float X2, float Y2,
			float X3, float Y3, float X4, float Y4) {
	    return 
	    	( isLeft(X1, Y1, X2, Y2, X3, Y3) != isLeft(X1, Y1, X2, Y2, X4, Y4) ) && 
	    	( isLeft(X3, Y3, X4, Y4, X1, Y1) != isLeft(X3, Y3, X4, Y4, X2, Y2) );
	}
	
	/**
	 * Comprueba si este {@code Segmento} corta a otro {@code Segmento} (X3,Y3)(X4,Y4).
	 *
	 * @param X3 Coordenada X del primer punto del otro {@code Segmento} evaluado.
	 * @param Y3 Coordenada Y del primer punto del otro {@code Segmento} evaluado.
	 * @param X4 Coordenada X del segundo punto del otro {@code Segmento} evaluado.
	 * @param Y4 Coordenada Y del segundo punto del otro {@code Segmento} evaluado.
	 *
	 * @return <ul>
	 * 		 <li>{@code true} si los segmentos se cortan.
	 *		 <li>{@code false} en caso contrario.
	 *</ul>
	 */
	public boolean hayInterseccion(float X3, float Y3, float X4, float Y4){
		return hayInterseccion(x1, y1, x2, y2, X3, Y3, X4, Y4);
	}
	
	/**
	 * Comprueba si este {@code Segmento} corta a otro {@code Segmento}.
	 * 
	 * @param otro {@code Segmento} evaluado.
	 * @return <ul>
	 * 		 <li>{@code true} si los segmentos se cortan.
	 *		 <li>{@code false} en caso contrario.
	 *</ul>
	 */
	public boolean hayInterseccion(Segmento otro) {
		return hayInterseccion(x1, y1, x2, y2, otro.x1, otro.y1, otro.x2, otro.y2);
	}
	
	/**
	 * Comprueba si este {@code Segmento} corta alguno de los lados del {@code LimiteRectangular},
	 * que se pasa como parámetro.
	 * 
	 * @param rectangulo {@code LimiteRectangular} evaluado.
	 * @return <ul>
	 * 		 <li>{@code true} si hay intersección.
	 *		 <li>{@code false} en caso contrario.
	 *</ul>
	 *@see LimiteRectangular#hayInterseccion(Segmento)
	 */
	public boolean hayInterseccion(LimiteRectangular rectangulo) {
		return rectangulo.hayInterseccion(this);
	}
}