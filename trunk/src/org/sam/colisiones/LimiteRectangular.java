package org.sam.colisiones;

public class LimiteRectangular{
	float xII, yII;  // esquina inf izda
	float xSD, ySD;  // esquina sup dcha
	
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

	public void calcular(float xCoord[], float yCoord[], int nPuntos) {
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		
		for (int i = 0; i < nPuntos; i++) {
			float x = xCoord[i];
			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			float y = yCoord[i];
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}
		setSortedValues(minX,minY,maxX,maxY);
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
		
		/*  Cuadrantes:
		 *  1001 | 1000 | 1010
		 * ------+------+------
		 *  0001 | 0000 | 0010
		 * ------+------+------
		 *  0101 | 0100 | 0110
		 */
		
		// Uno de los puntos esta dentro del rectangulo
		if( cuadranteP1 == 0 || cuadranteP2 == 0)
			return true;
		// Los dos puntos del segmento estan en la misma franja exterior del rectangulo
		if( (cuadranteP1 & cuadranteP2) != 0 )
			return false;
		
//		int cuadranteMIN  = Math.abs(cuadranteP1 | cuadranteP2;
		
		// Los puntos del segmento estan en ditintas franjas del rectangulo
		// y pueden cortar el rectangulo
		float dx = px2 - px1;
		float dy = py2 - py1;
		float m =  dx /dy ;

//		if(Math.abs(dx) < Math.abs(dy)){
		// se comprueba con los lados horiznontales
		float x = (yII - py1)*m + px1; 
		if (x >= xII && x <= xSD)
			return true;	
		x = (ySD - py1)*m + px1;
		if (x >= xII && x <= xSD)
			return true;
//		}
		// se comprueba con los lados verticales
		m =  dy / dx; //pendiente de la recta	
		float y = (xII - px1)*m + py1; 
		if (y >= yII && y <= ySD)
			return true;	
		y = (xSD - px1)*m + py1;
		return (y >= yII && y <= ySD);
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
}
