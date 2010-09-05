package org.sam.colisiones;

/**
 * Implementación del interface {@code Limites} en forma de rectángulo.
 */
public class LimiteRectangular implements Limites{
	
	/**
	 * Coordenada X de la esquina inferior izquierda.
	 */
	float xII;
	/**
	 * Coordenada Y de la esquina inferior izquierda.
	 */
	float yII;
	/**
	 * Coordenada X de la esquina superior derecha.
	 */
	float xSD;
	/**
	 * Coordenada Y de la esquina superior derecha.
	 */
	float ySD;
	
	/**
	 * Constructor por defecto, crea un {@code LimiteRectangular} de 0 unidades de ancho por 0 unides de alto.
	 */
	public LimiteRectangular(){
		this(0,0);
	}
	
	/**
	 * Constructor que crea un {@code LimiteRectangular} de dimensiones {@code ancho x alto}.
	 * @param ancho anchura del rectangulo creado.
	 * @param alto  altura del rectangulo creado.
	 */
	public LimiteRectangular(float ancho, float alto){
		xSD = ancho/2;
		xII = - xSD;
		ySD = alto/2;
		yII = - ySD;
	}
	
	/**
	 * Constructor que crea un {@code LimiteRectangular} a partir de las coordenadas de dos puntos.
	 * 
	 * @param x1 Coordenada X del primer punto.
	 * @param y1 Coordenada Y del primer punto.
	 * @param x2 Coordenada X del segundo punto.
	 * @param y2 Coordenada Y del segundo punto.
	 */
	public LimiteRectangular(float x1, float y1, float x2, float y2){
		setValues( x1, y1, x2, y2 );
	}
	
	/**
	 * Método que asigna las coodenadas de los esquinas del {@code LimiteRectangular} a partir de las coordenadas
	 * de dos puntos.
	 * 
	 * @param x1 Coordenada X del primer punto.
	 * @param y1 Coordenada Y del primer punto.
	 * @param x2 Coordenada X del segundo punto.
	 * @param y2 Coordenada Y del segundo punto.
	 */
	public void setValues(float x1, float y1, float x2, float y2){
		if(x1 < x2){
			this.xII = x1;
			this.xSD = x2;
		}else{
			this.xII = x2;
			this.xSD = x1;
		}
		if(y1 < y2){
			this.yII = y1;
			this.ySD = y2;
		}else{
			this.yII = y2;
			this.ySD = y1;
		}
	}
	
	/**
	 * Método que asigna directamente las coodenadas de los esquinas del {@code LimiteRectangular}.
	 * 
	 * @param xII Coordenada X de la esquina inferior izquierda.
	 * @param yII Coordenada Y de la esquina inferior izquierda.
	 * @param xSD Coordenada X de la esquina superior derecha.
	 * @param ySD Coordenada Y de la esquina superior derecha.
	 */
	public void setSortedValues(float xII, float yII, float xSD, float ySD) {
		this.xII = xII;
		this.yII = yII;
		this.xSD = xSD;
		this.ySD = ySD;
	}

	/**
	 * Método que traslada el {@code LimiteRectangular}.
	 * @param desX desplazamiento en el eje X.
	 * @param desY desplazamiento en el eje Y.
	 */
	public void trasladar(float desX, float desY) {
		xII += desX;
		yII += desY;
		xSD += desX;
		ySD += desY;
	}

	/**
	 * Función que evalúa si un determinado punto está dentro de los límites definidos por el objeto.
	 * @param x Coordenada X del punto evaluado.
	 * @param y Coordenada Y del punto evaluado.
	 * @return <ul>
	 * <li>{@code true}: si contiene el punto evaluado.</li>
	 * <li>{@code false}: en caso contrario.</li>
	 * </ul>
	 */
	public boolean contiene(float x, float y){
		return xII<= x && x<=xSD && yII<=y && y<=ySD;
	}

	/**
	 * Función que evalúa si un determinado {@code Segmento} corta alguno de los lados que forman
	 * el {@code LimiteRectangular} definido por el objeto.
	 * @param segmento {@code Segmento} con el que se hace la comprobación.
	 * @return <ul>
	 * <li>{@code true}: si hay intersección.</li>
	 * <li>{@code false}: en caso contrario.</li>
	 * </ul>
	 */
	public boolean hayInterseccion(Segmento segmento){
		return hayInterseccion(segmento.x1, segmento.y1, segmento.x2, segmento.y2 );
	}
	
	/**
	 * Función que evalúa si un determinado segmento, definido por dos puntos, corta alguno de los lados que forman
	 * el {@code LimiteRectangular} definido por el objeto.
	 * 
	 * @param px1 Coordenada X del primer punto.
	 * @param py1 Coordenada Y del primer punto.
	 * @param px2 Coordenada X del segundo punto.
	 * @param py2 Coordenada Y del segundo punto.
	 * @return <ul>
	 * <li>{@code true}: si hay intersección.</li>
	 * <li>{@code false}: en caso contrario.</li>
	 * </ul>
	 */
	public boolean hayInterseccion(float px1, float py1,float px2, float py2){
		/*  Cuadrantes:
		 *  1001 | 1000 | 1010
		 * ------+------+------
		 *  0001 | 0000 | 0010
		 * ------+------+------
		 *  0101 | 0100 | 0110
		 */
		
		int cuadranteP1 = 0;
		int cuadranteP2 = 0;
		
		if(	px1 < xII ) 
			cuadranteP1 = 0x01;
		else if(px1 > xSD)
			cuadranteP1 = 0x02;
		if(	py1 < yII ) 
			cuadranteP1 |= 0x04;
		else if(py1 > ySD)
			cuadranteP1 |= 0x08;
		
		if(	px2 < xII ) 
			cuadranteP2 = 0x01;
		else if(px2 > xSD)
			cuadranteP2 = 0x02;
		if(	py2 < yII ) 
			cuadranteP2 |= 0x04;
		else if(py2 > ySD)
			cuadranteP2 |= 0x08;
		
		// Uno de los puntos esta dentro del rectangulo
		if( cuadranteP1 == 0 || cuadranteP2 == 0)
			return true;
		// Los dos puntos del segmento estan en la misma franja exterior del rectangulo
		if( (cuadranteP1 & cuadranteP2) != 0 )
			return false;
		
		// Los puntos del segmento estan en ditintas franjas del rectangulo
		// y pueden cortar el rectangulo
		float dx = px2 - px1;
		float dy = py2 - py1;
		
		float xIIdy = xII * dy;
		float yIIdx = yII * dx;
		float xSDdy = xSD * dy;
		float ySDdx = ySD * dx;
		float px1dy_minus_py1dx = px1 * dy - py1 * dx;
		
		float x_dy, y_dx;
		float limIzq, limDer;
		
		// se comprueba con los lados horizontales
		// x = dx·(yII - py1)/dy + px1 --> dy·x = dx·(yII - py1) + dy·px1 --> dy·x = dx·yII - dx·py1 + dy·px1 
		if(Math.signum(dy) > 0){
			limIzq = xIIdy;
			limDer = xSDdy;
		}else{
			limIzq = xSDdy;
			limDer = xIIdy;
		}
		x_dy = yIIdx + px1dy_minus_py1dx; 	
		if (x_dy >= limIzq && x_dy <= limDer)
			return true;	
		x_dy = ySDdx + px1dy_minus_py1dx; 
		if (x_dy >= limIzq && x_dy <= limDer)
			return true;
		
		// se comprueba con los lados verticales
		// y = dy·(xII - px1)/dx + py1 --> dx·y = dy·(xII - px1) + dx·py1 --> dx·y = dy·xII - dy·px1 + dx·py1
		if(Math.signum(dx) > 0){
			limIzq = yIIdx;
			limDer = ySDdx;
		}else{
			limIzq = ySDdx;
			limDer = yIIdx;
		}
		y_dx = xIIdy - px1dy_minus_py1dx; 
		if (y_dx >= limIzq && y_dx <= limDer)
			return true;	
		y_dx = xSDdy - px1dy_minus_py1dx;
		return (y_dx >= limIzq && y_dx <= limDer);
	}
	
	/**
	 * Función que comprueba que si este {@code LimiteRectangular} es igual o equivalente a otro {@code LimiteRectangular},
	 * que se pasa como parámetro.
	 * @param otro {@code LimiteRectangular} con el que se hace la comprobación.
	 * @return <ul>
	 * <li>{@code true}: si son iguales o tienen las mismas dimensiones y posiciones.</li>
	 * <li>{@code false}: en caso contrario.</li>
	 * </ul>
	 */
	public boolean equals(LimiteRectangular otro){
		return	(otro != null) && (
					( otro == this ) || 
					(xII == otro.xII && yII == otro.yII && xSD == otro.xSD && ySD == otro.ySD )
				);
	}
	
	/**
	 * Función que comprueba que si este {@code LimiteRectangular} intersecta
	 * con otro {@code LimiteRectangular}, que se pasa como parámetro.
	 * @param otro {@code LimiteRectangular} con el que se hace la comprobación.
	 * @return <ul>
	 * <li>{@code true}: si hay intersección.</li>
	 * <li>{@code false}: en caso contrario.</li>
	 * </ul>
	 */
	public boolean hayInterseccion(LimiteRectangular otro){
		return (xII<=otro.xSD && xSD>=otro.xII && yII<=otro.ySD && ySD>=otro.yII);
	}

	private final static LimiteRectangular shared = new LimiteRectangular();
	
	/**
	 * Función que devuelve un {@code LimiteRectangular} correspondiente a la intersección 
	 * entre este {@code LimiteRectangular} con otro {@code LimiteRectangular}, que se pasa como parámetro.
	 * @param otro {@code LimiteRectangular} con el que se hace la comprobación.
	 * @return <ul>
	 * <li> {@code LimiteRectangular}: con el area que forma la intersección.</li>
	 * <li>{@code null}: si no hay intersección.</li>
	 * </ul>
	 * <u>Nota:</u> esta función no genera nuevas instancias, y siempre devuelve el mismo {@code LimiteRectangular}
	 * compartido. Si se necesitan instancias nuevas, use {@link #interseccion(LimiteRectangular, LimiteRectangular)}.
	 */
	public LimiteRectangular interseccion(LimiteRectangular otro){
		return interseccion(otro, shared);
	}
	
	/**
	 * Función que devuelve un {@code LimiteRectangular} correspondiente a la intersección
	 * entre este {@code LimiteRectangular} con otro {@code LimiteRectangular}, que se pasa como parámetro.
	 * @param otro {@code LimiteRectangular} con el que se hace la comprobación.
	 * @param interseccion Instancia dónde almacenar los valores de la intersección.
	 * @return <ul>
	 * <li> {@code LimiteRectangular}: con el area que forma la intersección.</li>
	 * <li>{@code null}: si no hay intersección.</li>
	 * </ul>
	 */
	public LimiteRectangular interseccion(LimiteRectangular otro, LimiteRectangular interseccion){
		if(!hayInterseccion(otro))
			return null;
		interseccion.setSortedValues(
			xII > otro.xII ? xII : otro.xII,
			yII > otro.yII ? yII : otro.yII,
			xSD < otro.xSD ? xSD : otro.xSD,
			ySD < otro.ySD ? ySD : otro.ySD
		);
		return interseccion;
	}

	/* (non-Javadoc)
	 * @see org.sam.colisiones.Limites#getXMin()
	 */
	@Override
	public float getXMin() {
		return 	xII; 
	}

	/* (non-Javadoc)
	 * @see org.sam.colisiones.Limites#getYMin()
	 */
	@Override
	public float getYMin() {
		return yII;
	}

	/* (non-Javadoc)
	 * @see org.sam.colisiones.Limites#getXMax()
	 */
	@Override
	public float getXMax() {
		return xSD;
	}

	/* (non-Javadoc)
	 * @see org.sam.colisiones.Limites#getYMax()
	 */
	@Override
	public float getYMax() {
		return ySD;
	}
}
