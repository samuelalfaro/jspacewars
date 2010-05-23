package org.sam.colisiones;

public class LimiteRectangular{
	float x1, y1;  // esquina inf izda
	float x2, y2;  // esquina sup dcha
	
	public LimiteRectangular(){
		x1 = y1 = x2 = y2 = 0.0f;
	}
	
	public LimiteRectangular(float ancho,float alto){
		x2 = ancho/2;
		x1 = - x2;
		y2 = alto/2;
		y1 = - y2;
	}
	
	public LimiteRectangular(float xII,float yII,float xSD,float ySD){
		setValues(xII,yII,xSD,ySD);
	}
	
	public void setValues(float xII,float yII,float xSD,float ySD){
		x1 = xII;
		y1 = yII;
		x2 = xSD;
		y2 = ySD;
	}
	
	public void setValues(float xII,float yII,float xSD,float ySD, boolean ordenados){
		if(ordenados){
			x1 = xII;
			y1 = yII;
			x2 = xSD;
			y2 = ySD;
			return;
		}
		if(xII < xSD){
			x1 = xII;
			x2 = xSD;
		}else{
			x1 = xSD;
			x2 = xII;
		}
		if(yII < ySD){
			y1 = yII;
			y2 = ySD;
		}else{
			y1 = ySD;
			y2 = yII;
		}
	}

	public void calcular(float xCoord[], float yCoord[], int nPuntos) {
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		
		for (int i = 0; i < nPuntos; i++) {
			float x = xCoord[i];
			minX = (minX < x)? minX : x;
			maxX = (maxX < x)? x : maxX;
			float y = yCoord[i];
			minY = (minY < y)? minY : y;
			maxY = (maxY < y)? y : maxY;
		}
		setValues(minX,minY,maxX,maxY);
	}
	
	public void trasladar(float velX,float velY){
		x1 += velX;
		y1 += velY;
		x2 += velX;
		y1 += velY;
	}

	public boolean contiene(double x, double y){
		return x1<x && x<x2 && y1<y && y<y2;
	}

	public boolean hayInterseccion(double px1, double py1,double px2, double py2){
		// Los dos puntos del segmento no estan en las franjas del rectangulo
		if ( (px1 < x1 && px2 < x1) || (px1 > x2 && px2 > x2) ||
			 (py1 < y1 && py2 < y1) || (py1 > y2 && py2 > y2) )
			return false;
		// Los dos puntos del segmento estan en una franja del rectangulo
		if ( (px1 >= x1 && px1 <= x2) && (px2 >= x1 && px2 <= x2) ||
			 (py1 >= y1 && py1 <= y2) && (py2 >= x1 && py2 <= y2) )
			return true;

		// Los puntos del segmento estan en ditintas franjas del rectangulo
		// y pueden cortar el rectangulo
		double dx = px2 - px1;
		double dy = py2 - py1;

		if(Math.abs(dx) < Math.abs(dy)){
		// el segmento tiene una pendiente mayor de 1
		// y se comprueba con los lados horiznontales
			dx /=dy; //inversa pendiente de la recta
			double x = (y1 - py1)*dx + px1; 
			if (x >= x1 && x <= x2)
				return true;	
			x = (y2 - py1)*dx + px1;
			return (x >= x1 && x <= x2);
		}
		// el segmento tiene una pendiente menor de 1
		// y se comprueba con los lados verticales
		dy /=dx; //pendiente de la recta	
		double y = (x1 - px1)*dy + py1; 
		if (y >= y1 && y <= y2)
			return true;	
		y = (x2 - px1)*dy + py1;
		return (y >= y1 && y <= y2);
	}
	
	public boolean hayInterseccion(LimiteRectangular otro){
		//return !(x1>otro.x2 || x2<otro.x1 || y1>otro.y2 || y2<otro.y1);
		return (x1<=otro.x2 && x2>=otro.x1 && y1<=otro.y2 && y2>=otro.y1);
	}

	public LimiteRectangular interseccion(LimiteRectangular otro){
		float newX1 = x1 > otro.x1 ? x1 : otro.x1;
		float newY1 = y1 > otro.y1 ? y1 : otro.y1;		
		float newX2 = x2 < otro.x2 ? x2 : otro.x2;		
		float newY2 = y2 < otro.y2 ? y2 : otro.y2;
		return new LimiteRectangular(newX1,newY1,newX2,newY2);		
	}
}
