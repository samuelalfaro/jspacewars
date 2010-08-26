package org.sam.colisiones;

public class LimiteRectangular implements Limites{
	
	/**
	 * Coordenadas de la esquina inferior izquierda.
	 */
	float xII, yII;
	/**
	 * Coordenadas de la esquina superior derecha.
	 */
	float xSD, ySD;
	
	public LimiteRectangular(){}
	
	public LimiteRectangular(float ancho, float alto){
		xSD = ancho/2;
		xII = - xSD;
		ySD = alto/2;
		yII = - ySD;
	}
	
	public LimiteRectangular(float xII,float yII,float xSD,float ySD){
		setValues(xII,yII,xSD,ySD);
	}
	
	public void setValues(float x1,float y1,float x2,float y2){
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
	
	public void setSortedValues(float xII,float yII,float xSD,float ySD){
		this.xII = xII;
		this.yII = yII;
		this.xSD = xSD;
		this.ySD = ySD;
	}

	public void trasladar(float desX,float desY){
		xII += desX;
		yII += desY;
		xSD += desX;
		ySD += desY;
	}

	public boolean contiene(float x, float y){
		return xII<= x && x<=xSD && yII<=y && y<=ySD;
	}

	public boolean hayInterseccion(Segmento segmento){
		return hayInterseccion(segmento.x1, segmento.y1, segmento.x2, segmento.y2 );
	}
	
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
	
	public boolean equals(LimiteRectangular otro){
		return	(otro != null) && (
					( otro == this ) || 
					(xII == otro.xII && yII == otro.yII && xSD == otro.xSD && ySD == otro.ySD )
				);
	}
	
	public boolean hayInterseccion(LimiteRectangular otro){
		return (xII<=otro.xSD && xSD>=otro.xII && yII<=otro.ySD && ySD>=otro.yII);
	}

	private final static LimiteRectangular shared = new LimiteRectangular();
	
	public LimiteRectangular interseccion(LimiteRectangular otro){
		return interseccion(otro, shared);
	}
	
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
