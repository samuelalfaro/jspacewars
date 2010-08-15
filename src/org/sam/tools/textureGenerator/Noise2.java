package org.sam.tools.textureGenerator;
@Deprecated
public class Noise2 {
	//*
	static java.util.Random r = new java.util.Random();
	
	static int random(int min, int max){
		return (int)( ( r.nextDouble() * (max - min) ) + min );
	}
	
	static int r1 = random(1000, 10000);
	static int r2 = random(100000, 1000000);
	static int r3 = random(1000000000, 2000000000);
	/*/
	static int r1 = 5500;
	static int r2 = 550000;
	static int r3 = 5500000;
	//*/
	
	static double noise(int x, int y) {
		int n = x + y * 57;
		n = (n << 13) ^ n;

		return (1.0 - ((n * (n * n * r1 + r2) + r3) & 0x7fffffff) / 1073741824.0);
	}
	
	/*
	static double interpolate(double x0, double x1, double a) {
//		return x0 + (x1-x0) * a; lineal
//	    return x0 + (x1-x0) * (1 - Math.cos(a * Math.PI)) * .5; ajuste con coseno
		return ( 3 -2*a )*(x1-x0)*a*a + x0; // cubica similar al coseno las tangencias de los puntos son 0
	}
	
	public static double noise(double x, double y){
	    return 
		    interpolate( 
		    		interpolate(
		    				noise((int)x    , (int)y),
		    				noise((int)x + 1, (int)y),
		    				x - (int)x 
		    		), 
		    		interpolate(
		    				noise((int)x    , (int)y + 1),
		    				noise((int)x + 1, (int)y + 1),
		    				x - (int)x 
		    		),
		    		y - (int)y 
		    );
	}
	/*/
	public static double noise(double x, double y){
		BicubicInterpolator bi = new BicubicInterpolator(
				new double[][]{
						{noise((int)x-1,(int)y-1),noise((int)x-1,(int)y ),noise((int)x-1,(int)y+1),noise((int)x-1,(int)y+2)},
						{noise((int)x  ,(int)y-1),noise((int)x  ,(int)y ),noise((int)x  ,(int)y+1),noise((int)x  ,(int)y+2)},
						{noise((int)x+1,(int)y-1),noise((int)x+1,(int)y ),noise((int)x+1,(int)y+1),noise((int)x+1,(int)y+2)},
						{noise((int)x+2,(int)y-1),noise((int)x+2,(int)y ),noise((int)x+2,(int)y+1),noise((int)x+2,(int)y+2)}
				}
			);
	    return bi.interpolate(x - (int)x , y - (int)y);
	}
	//*/
}
