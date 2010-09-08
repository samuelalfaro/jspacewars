/* 
 * FastMath.java
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
package pruebas.util;

import java.util.Random;

/**
 * Clase para optimizar operaciones en coma flotante, mediante tablas precalculadas,
 * algo no recomendable en Java. Puesto que en muchos casos es mas costoso el acceso
 * a una posicion de memoria de forma segura a traves de un vector, que el calculo de
 * dichas operaciones, mas aun, con la reduccion de coste computacional de estos calculos
 * en los procesadores modernos y el cacheo de resultados anteriores.<br/>
 * <br/>
 * Por tanto practicas comunes en C sobre todo en la demoscene no son muy eficaces en Java.<br/>
 * <br/>
 * Estos resultados tambien pueden depender de la forma en que gestiona la memoria el
 * sistema operativo:<br/>
 * <br/>
 * <table>
 * <thead>Resultados:</thead>
 * <tbody>
 * <tr><td>Windows XP:</td></tr>
 * <tr><td>Tiempo Math.atan2:            </td><td align=right>20.382.729.648 nSegundos</td></tr>
 * <tr><td>Tiempo FastMath.atan (float): </td><td align=right> 8.655.483.308 nSegundos</td></tr>
 * <tr><td>Tiempo FastMath.atan (int):   </td><td align=right>10.386.887.439 nSegundos</td></tr>
 * <tr><td></td></tr>
 * <tr><td>Tiempo Math.sqrt(a*a+b*b):    </td><td align=right> 8.574.624.149 nSegundos</td></tr>
 * <tr><td>Tiempo FastMath.len (f,f):    </td><td align=right> 8.718.444.663 nSegundos</td></tr>
 * <tr><td>Tiempo FastMath.len (i,i):    </td><td align=right> 9.164.339.043 nSegundos</td></tr>
 * <tr><td></br></td></tr>
 * <tr><td>Linux:</td></tr>
 * <tr><td>Tiempo Math.atan2:            </td><td align=right>19.445.116.001 nSegundos</td></tr>
 * <tr><td>Tiempo FastMath.atan (float): </td><td align=right> 7.256.841.093 nSegundos</td></tr>
 * <tr><td>Tiempo FastMath.atan (int):   </td><td align=right> 8.337.504.290 nSegundos</td></tr>
 * <tr><td></td></tr>
 * <tr><td>Tiempo Math.sqrt(a*a+b*b):    </td><td align=right> 7.442.535.783 nSegundos</td></tr>
 * <tr><td>Tiempo FastMath.len (f,f):    </td><td align=right> 6.280.101.928 nSegundos</td></tr>
 * <tr><td>Tiempo FastMath.len (i,i):    </td><td align=right> 7.982.940.708 nSegundos</td></tr>
 * </tbody></table>
 * 
 */
@Deprecated
public class FastMath {
	
    private static int highestOneBitPos(int i) {
        if (i == 0)
            return 0;
        int n = 1;
        if (i >>> 16 != 0) { n += 16; i >>>= 16; }
        if (i >>>  8 != 0) { n +=  8; i >>>=  8; }
        if (i >>>  4 != 0) { n +=  4; i >>>=  4; }
        if (i >>>  2 != 0) { n +=  2; i >>>=  2; }
        if (i >>>  1 != 0) { n +=  1; i >>>=  2; }
        return n;
    }
	
	private static final float PI_to_240 = (float)(120.0 / Math.PI);
	private static final float DEG_to_240 = (float)(2.0 / 3.0);
	
	private static final float[] cosTable = new float[240];
	static { for(int i = 0; i < 240; i++)
		cosTable[i] = (float)Math.cos( i * Math.PI / 120); 
	}
	
	public static final float cosRadians(float angrad){
		float angle = (angrad * PI_to_240 );
		if(angle < 0){
			int index =  (int)(angle - 0.5f);
			if(index < -240) 
				index = index % 240;
			return cosTable[ (index < 0) ?  240 + index : 0 ];
		}
		int index = (int)(angle + 0.5f);
		return cosTable[ ( index >= 240 ) ? index % 240 : index];
	}

	public static final float cosDegrees(float angdeg){
		float angle = (angdeg * DEG_to_240 );
		if(angle < 0){
			int index =  (int)(angle - 0.5f);
			if(index < -240) 
				index = index % 240;
			return cosTable[ (index < 0) ?  240 + index : 0 ];
		}
		int index = (int)(angle + 0.5f);
		return cosTable[ ( index >= 240 ) ? index % 240 : index];
	}
	
	public static final float cos240(float ang240){
		int index = (int)( ang240 + 0.5f );
		return cosTable[ index == 240 ? 0 : index ];
	}

	public static final float cos240(int ang240){
		return cosTable[ ang240 ];
	}
	
	private static final float[] sinTable = new float[240];
	static{	for(int i = 0; i < 240; i++)
		sinTable[i] = (float)Math.sin( i * Math.PI / 120);
	}
		
	public static final float sinRadians(float angrad){
		float angle = (angrad * PI_to_240 );
		if(angle < 0){
			int index =  (int)(angle - 0.5f);
			if(index < -240) 
				index = index % 240;
			return sinTable[ (index < 0) ?  240 + index : 0 ];
		}
		int index = (int)(angle + 0.5f);
		return sinTable[ ( index >= 240 ) ? index % 240 : index];
	}

	public static final float sinDegrees(float angdeg){
		float angle = (angdeg * DEG_to_240 );
		if(angle < 0){
			int index =  (int)(angle - 0.5f);
			if(index < -240) 
				index = index % 240;
			return sinTable[ (index < 0) ?  240 + index : 0 ];
		}
		int index = (int)(angle + 0.5f);
		return sinTable[ ( index >= 240 ) ? index % 240 : index];
	}
	
	public static final float sin240(float ang240){
		int index = (int)( ang240 + 0.5f );
		return sinTable[ index == 240 ? 0 : index ];
	}
	
	public static final float sin240(int ang240){
		return sinTable[ang240];
	}
	
	private static final int TABLE_LEN = 0x100;
	private static final float[][] LEN_ATAN_TABLE = new float[TABLE_LEN][TABLE_LEN];
	static{
		for(int y = 0; y < TABLE_LEN; y++){
			for(int x = 0; x < y; x ++)
				LEN_ATAN_TABLE[y][x] = (float)Math.atan2(x, y) * PI_to_240;
			for(int x = y; x < TABLE_LEN; x++)
				LEN_ATAN_TABLE[y][x] = (float)Math.sqrt(y*y + x*x);
		}
	}
	
	private static final float atan(final boolean posy, final int absy, final boolean posx, final int absx){
		int min, max;
		if( absx < absy ){
			min = absx;	max = absy;
		}else{
			min = absy;	max = absx;
		}
		
		// Ejes
		if( min == 0 ){
			if(absy == 0)
				return posx ? 0 : 120;
			return posy ? 60 : 180;
		}
		// Bisectrices
		if (min == max){
			if( posy )
				return posx ? 30 : 90;
			return !posx ? 150 : 210;
		}

		float atan = LEN_ATAN_TABLE[max][min];
		
		if( posy ){
			if( posx ){
				if( absx > absy )
					return atan; 
				return 60 - atan;
			}
			if( absy > absx )
				return 60 + atan;
			return 120 - atan;
		}
		if( !posx ){
			if( absx > absy )
				return 120 + atan;
			return 180 - atan;
		}
		if( absy > absx )
			return 180 + atan;
		return 240 - atan;
	}
	
	public static final float atan(final int y, final int x){
		boolean posy = (y >>> 31) == 0;
		int absy =  posy ? y : -y;
		boolean posx = (x >>> 31) == 0;
		int absx = posx ? x : -x;
		
		int max = absx | absy; 
		
		if( max < TABLE_LEN )
			return atan( posy, absy, posx, absx );
		
		int exp = highestOneBitPos( max ) - 9;
		absy = ((absy >>> exp) + 1) >>> 1;
		absx = ((absx >>> exp) + 1) >>> 1;

		return atan( posy, (absy == TABLE_LEN) ? 255: absy, posx, (absx == TABLE_LEN) ? 255: absx );
	}
	
	public static final float atan(final float y, final float x){
		int iby = Float.floatToRawIntBits(y);
		int ibx = Float.floatToRawIntBits(x);

		int ey = (iby << 1) >>> 24;
		int ex = (ibx << 1) >>> 24;

		int difex = Math.abs( ey - ex );
		if( difex > 8 || ey == 0 || ex == 0 ){
			if(ey > ex)
				return (iby & 0x80000000) == 0 ? 60 : 180;
			return (ibx & 0x80000000) == 0 ? 0 : 120;
		}
		
		int my,	mx;
		
		if (ey < ex) {
			my = ((((iby & 0x007FFFFF) | 0x00800000) >> (15 + difex)) + 1) >> 1;
			mx = ((((ibx & 0x007FFFFF) | 0x00800000) >> 15) + 1) >> 1;
			if (mx == 256)
				mx = 255;
		} else if (ey == ex) {
			my = ((((iby & 0x007FFFFF) | 0x00800000) >> 15) + 1) >> 1;
			if (my == 256)
				my = 255;
			mx = ((((ibx & 0x007FFFFF) | 0x00800000) >> 15) + 1) >> 1;
			if (mx == 256)
				mx = 255;
		} else {
			my = ((((iby & 0x007FFFFF) | 0x00800000) >> 15) + 1) >> 1;
			if (my == 256)
				my = 255;
			mx = ((((ibx & 0x007FFFFF) | 0x00800000) >> (15 + difex)) + 1) >> 1;
		}
		return atan( (iby >>> 31) == 0, my, (ibx >>> 31) == 0, mx);
	}
	
	public static final float len(final int y, final int x){
		
		int min, max;
		int absy = Math.abs(y);
		int absx = Math.abs(x);
		
		if( absx < absy ){
			min = absx;	max = absy;
		}else{
			min = absy;	max = absx;
		}
		
		if(max < TABLE_LEN)
			return LEN_ATAN_TABLE[min][max];
		
		int exp = highestOneBitPos(max) - 8;
		int ia = min >> exp;
		int ib = max >> exp;
		return ( min * ia + max * ib ) / LEN_ATAN_TABLE[ia][ib];
	}
	
	public static final float len(final float y, final float x){
		return (float)Math.sqrt(y*y + x*x);
	}
	
	@SuppressWarnings("unused")
	private static final void printInts(float y, float x){
		int iby = Float.floatToRawIntBits(y);
		int ibx = Float.floatToRawIntBits(x);

		int ey = (iby & 0x7F800000)>>23;
		int ex = (ibx & 0x7F800000)>>23;

		int difex = Math.abs( ey - ex );
		
		int my,	mx;
		
		if (ey < ex) {
			my = ((((iby & 0x007FFFFF) | 0x00800000) >> (15 + difex)) + 1) >> 1;
			mx = ((((ibx & 0x007FFFFF) | 0x00800000) >> 15) + 1) >> 1;
			if (mx == 256)
				mx = 255;

		} else if (ey == ex) {
			my = ((((iby & 0x007FFFFF) | 0x00800000) >> 15) + 1) >> 1;
			if (my == 256)
				my = 255;
			mx = ((((ibx & 0x007FFFFF) | 0x00800000) >> 15) + 1) >> 1;
			if (mx == 256)
				mx = 255;
		} else {
			my = ((((iby & 0x007FFFFF) | 0x00800000) >> 15) + 1) >> 1;
			if (my == 256)
				my = 255;
			mx = ((((ibx & 0x007FFFFF) | 0x00800000) >> (15 + difex)) + 1) >> 1;
		}

		System.out.printf("y: (%08X ^ %3d) = %1$+4d  x: (%08X ^ %3d) = %3$+4d \t",my,ey,mx,ex);
	}
	@SuppressWarnings("unused")
	private static final void printInts(int y, int x){
		int absy = (y & 0x80000000) == 0 ? y : -y;
		int absx = (x & 0x80000000) == 0 ? x : -x;
		
		int exp = highestOneBitPos(Math.max(absy, absx)) - 9;

		if((exp & 0x80000000) == 0){
			absy = ((absy >> exp) + 1) >> 1;
			if (absy == 256)
				absy = 255;
			absx = ((absx >> exp) + 1) >> 1;
			if (absx == 256)
				absx = 255;
		}
		System.out.printf("y: %02X = %1$+4d x: %02X = %2$+4d\t",absy,absx);
	}
	@SuppressWarnings("unused")
	private static float radiansTo240(double angrad){
		if(angrad < 0)
			angrad = 2*Math.PI + angrad;
		return (float)angrad*PI_to_240;
	}
	@SuppressWarnings("unused")
	private static float dif(float a, float b){
		float dif = Math.abs( a - b );
		return (dif > 120)? 240 - dif: dif;
	}
	
	/**
	 * Método principal que realiza distintas comprobaciones tanto de fiabilidad como de velocidad.
	 * @param args ignorados.
	 */
	public static void main(String args[]){

		System.out.printf(" %8.4f\n",atan( 1, 4));
		System.out.printf(" %8.4f\n",atan( 4, 1));
		System.out.printf(" %8.4f\n",atan( 4,-1));
		System.out.printf(" %8.4f\n",atan( 1,-4));
		System.out.printf(" %8.4f\n",atan(-1,-4));
		System.out.printf(" %8.4f\n",atan(-4,-1));
		System.out.printf(" %8.4f\n",atan(-4, 1));
		System.out.printf(" %8.4f\n",atan(-1, 4));

		System.out.printf("\n %5.1f\n\n",atan( 0, 0));

		System.out.printf(" %5.1f\n",atan( 0, 1));
		System.out.printf(" %5.1f\n",atan( 1, 1));
		System.out.printf(" %5.1f\n",atan( 1, 0));
		System.out.printf(" %5.1f\n",atan( 1,-1));
		System.out.printf(" %5.1f\n",atan( 0,-1));
		System.out.printf(" %5.1f\n",atan(-1,-1));
		System.out.printf(" %5.1f\n",atan(-1, 0));
		System.out.printf(" %5.1f\n",atan(-1, 1));

		System.out.println("\nProbando diferecias\n");
//		for(int x = -4092; x < 4093; x++)
//			for(int y=-4092; y < 4093; y++)
//				if( Math.abs( Math.sqrt(x*x + y*y) - len(x,y)) > 0.0549f){
//					System.out.printf("mal ( %5d, %5d )\t",y,x);
//					printInts(y,x);
//					System.out.printf("-->  %7.3f != %7.3f %10.7f\n",Math.sqrt(x*x + y*y), len(x,y), Math.sqrt(x*x + y*y) - len(x,y) );
//				}
//		
//		for(int y = -1024; y < 1025; y++)
//			for(int x = -1024; x < 1025; x++)
//				if(dif(radiansTo240(Math.atan2(y,x)),atan(y,x)) > 0.15785){
//					System.out.printf("mal ( %5d, %5d )\t",y,x);
//					printInts(y,x);
//					System.out.printf("-->  %7.3f != %7.3f %10.7f\n",radiansTo240(Math.atan2(y,x)),atan(y,x), radiansTo240(Math.atan2(y,x)) - atan(y,x));
//				}
//
//		for(float y = -1024.0f; y < 1025.0f; y+=1.0f)
//			for(float x = -1024.0f; x < 1025.0f; x+=1.0f)
//				if(dif(radiansTo240(Math.atan2(y,x)),atan(y,x)) > 0.15785){
//					System.out.printf("mal ( %6.1f, %6.1f )\t",y,x);
//					printInts(y,x);
//					System.out.printf("-->  %7.3f != %7.3f %10.7f\n",radiansTo240(Math.atan2(y,x)),atan(y,x), radiansTo240(Math.atan2(y,x)) - atan(y,x));
//				}
//
//		for(float y = -1024.0f; y < 1025.0f; y+=1.0f)
//			for(float x = -1.0f; x < 1.0f + 1.0f/1024; x+=1.0f/1024)
//				if(dif(radiansTo240(Math.atan2(y,x)),atan(y,x)) > 0.1492){
//					System.out.printf("mal ( %6.1f, %6.3f )\t",y,x);
//					printInts(y,x);
//					System.out.printf("-->  %7.3f != %7.3f %10.7f\n",radiansTo240(Math.atan2(y,x)),atan(y,x), radiansTo240(Math.atan2(y,x)) - atan(y,x));
//				}
		
		System.out.println("\nMidiendo tiempos\n");
		
		long tAnt, tAct;
		
		Random random = new Random();
//		tAnt = System.nanoTime();
//		for(int x = -4092; x < 4093; x++)
//			for(int y=-4092; y < 4093; y++){
//				random.nextInt(); random.nextInt();
//			}
//		tAct = System.nanoTime();
//		long tRandomInt = tAct-tAnt;
//		
//		tAnt = System.nanoTime();
//		for(int x = -4092; x < 4093; x++)
//			for(int y=-4092; y < 4093; y++){
//				random.nextFloat(); random.nextFloat();
//			}
//		tAct = System.nanoTime();
//		long tRandomFloat = tAct-tAnt;
		
		int a1=0, a2=0, a3=0;
		
		tAnt = System.nanoTime();
		for(int x = -4092; x < 4093; x++)
			for(int y=-4092; y < 4093; y++){
				int a = random.nextInt();
				int b = random.nextInt();
				a1 ^= ((int) Math.atan2(a, b) & 0x1);
			}
		tAct = System.nanoTime();
		long tMathAtan2 = tAct-tAnt;
		
		tAnt = System.nanoTime();
		for(int x = -4092; x < 4093; x++)
			for(int y=-4092; y < 4093; y++){
				a2 ^= ((int) atan(random.nextFloat(), random.nextFloat()) & 0x1);
			}
		tAct = System.nanoTime();
		long tAtanFloat = tAct-tAnt;
	
		tAnt = System.nanoTime();
		for(int x = -4092; x < 4093; x++)
			for(int y=-4092; y < 4093; y++){
				a3 ^= ((int) atan(random.nextInt(), random.nextInt()) & 0x1);
			}
		tAct = System.nanoTime();
		long tAtanInt = tAct-tAnt;
		
		System.out.printf("Tiempo Math.atan2:            %,14d nSegundos\tResultado de control [%d]\n",tMathAtan2,a1);
		System.out.printf("Tiempo FastMath.atan (float): %,14d nSegundos\tResultado de control [%d]\n",tAtanFloat,a2);
		System.out.printf("Tiempo FastMath.atan (int):   %,14d nSegundos\tResultado de control [%d]\n",tAtanInt,a3);
		
		int l1=0, l2=0, l3=0;
		tAnt = System.nanoTime();
		for(int x = -4092; x < 4093; x++)
			for(int y=-4092; y < 4093; y++){
				float a = random.nextFloat();
				float b = random.nextFloat();
				l1 ^= ((int)Math.sqrt(a*a + b*b) & 0x1);
			}
		tAct = System.nanoTime();
		long tMathLen = tAct-tAnt;
		
		tAnt = System.nanoTime();
		for(int x = -4092; x < 4093; x++)
			for(int y=-4092; y < 4093; y++){
				l2 ^= ((int)len(random.nextInt(), random.nextInt()) & 0x1);
			}
		tAct = System.nanoTime();
		long tLenInt = tAct-tAnt;
	
		tAnt = System.nanoTime();
		for(int x = -4092; x < 4093; x++)
			for(int y=-4092; y < 4093; y++){
				l3 ^= ((int)len(random.nextFloat(), random.nextFloat()) & 0x1);				
			}
		tAct = System.nanoTime();
		long tLenFloat = tAct-tAnt;
		
		System.out.printf("Tiempo Math.sqrt(a*a+b*b):    %,14d nSegundos\tResultado de control [%d]\n",tMathLen, l1);
		System.out.printf("Tiempo FastMath.len (f,f):    %,14d nSegundos\tResultado de control [%d]\n",tLenFloat, l2);
		System.out.printf("Tiempo FastMath.len (i,i):    %,14d nSegundos\tResultado de control [%d]\n",tLenInt, l3);
	}
}
