/* 
 * PruebaRGB2Luminance.java
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
/**
 * Clase para comprobar que optimizar operaciones en coma flotante, mediante tablas
 * precalculadas, en Java puede ser totalmente ineficiente. En muchos casos cuesta mas
 * adceder a una posicion de memoria de forma segura a traves de un vector, que hacer
 * dichas operaciones en coma flotante, mas aun, con la reduccion de coste de estos calculos
 * en los procesadores modernos y el cacheo de resultados anteriores.</br>
 * </br>
 * Por tanto practicas comunes en C sobre todo en la demoscene no son muy eficaces en Java.</br>
 * </br>
 * Estos resultados tambien pueden depender de la forma en que gestiona la memoria el
 * sistema operativo:</br>
 * </br>
 * <table>
 * <thead>Resultados:</thead>
 * <tbody>
 * <tr><td>Windows XP</td></tr>
 * <tr><td>Tiempo suma tabla ints            </td><td align=right>17.616.624.739 nSegundos</td></tr>
 * <tr><td>Tiempo multiplicacion suma floats </td><td align=right>14.027.159.038 nSegundos</td></tr>
 * <tr><td>Tiempo suma tabla floats          </td><td align=right>17.535.836.817 nSegundos</td></tr>
 * <tr><td></br></td></tr>
 * <tr><td>Linux:</td></tr>
 * <tr><td>Tiempo suma tabla ints            </td><td align=right> 4.780.109.009 nSegundos</td></tr>
 * <tr><td>Tiempo multiplicacion suma floats </td><td align=right> 7.157.811.111 nSegundos</td></tr>
 * <tr><td>Tiempo suma tabla floats          </td><td align=right> 5.540.043.213 nSegundos</td></tr>
 * </tbody></table>
 */
public class PruebaRGB2Luminance {

	static int[][] luminanceData = new int[3][256];
	static{
		for (int i = 0; i < 256; i++){
			luminanceData[0][i] = (int)( 0.299f * i );
			luminanceData[1][i] = (int)( 0.587f * i );
			luminanceData[2][i] = (int)( 0.114f * i );
		}
	}
	
	static int[][] luminanceData2 = new int[3][256];
	static{
		for (int i = 0; i < 256; i++){
			luminanceData2[0][i] = (int)(0.299f*i +.5f);
			luminanceData2[1][i] = (int)(0.587f*i +.5f);
			luminanceData2[2][i] = (int)(0.114f*i +.5f);
		}
	}
	
	static int[][] luminanceData3 = new int[3][256];
	static{
		for (int i = 0; i < 256; i++){
			luminanceData3[0][i] = (int)( 0.299f * i );
			luminanceData3[1][i] = (int)( 0.587f * i );
			luminanceData3[2][i] = (int)( 0.114f * i );
			int r = luminanceData3[0][i] + luminanceData3[1][i] + luminanceData3[2][i];
			if( r+1 == i){
				if(luminanceData3[0][i-1] > luminanceData3[0][i])
					luminanceData3[0][i]++;
				else
					luminanceData3[1][i]++;
			}else if( r+2 == i){
				luminanceData3[0][i]++;
				luminanceData3[1][i]++;
			}
		}
	}
	
	static float[][] luminanceData4 = new float[3][256];
	static{
		for (int i = 0; i < 256; i++){
			luminanceData4[0][i] = 0.299f * i;
			luminanceData4[1][i] = 0.587f * i;
			luminanceData4[2][i] = 0.114f * i;
		}
	}

	static long calcular1(){
		long result = 0;
		for(int r = 0; r<256; r++)
			for(int g = 0; g<256; g++)
				for(int b = 0; b<256; b++)
					result += (byte)(luminanceData[0][r] + luminanceData[1][g] + luminanceData[2][b]);
		return result & 0x1;
	}
	
	static long calcular2(){
		long result = 0;
		for(int r = 0; r<256; r++)
			for(int g = 0; g<256; g++)
				for(int b = 0; b<256; b++)
					result += (byte)(0.299f*r + 0.587f*g + 0.114f*b);
		return result & 0x1;
	}
	
	static long calcular3(){
		long result = 0;
		for(int r = 0; r<256; r++)
			for(int g = 0; g<256; g++)
				for(int b = 0; b<256; b++)
					result += (byte)(luminanceData4[0][r] + luminanceData4[1][g] + luminanceData4[2][b]);
		return result & 0x1;
	}
	
	static void mostrarTabla(int tabla[][]){
		for(int i=0; i< 256; i++){
			int r = tabla[0][i] + tabla[1][i] + tabla[2][i];
			System.out.format(
					"[ %3d : %3d : %3d ] --> %3d + %3d + %3d = %4d%s",
					i, i, i,
					tabla[0][i], tabla[1][i],tabla[2][i], r,
					r==i?"":" ----> ERROR!!!"
			);
			if( i>0 && (tabla[0][i] < tabla[0][i-1] || tabla[1][i] < tabla[1][i-1] || tabla[2][i] < tabla[2][i-1]) )
				System.out.print(" ----> ERROR (Salto)!!!");
			if( tabla[0][i] != (int)(0.299f*i) )
				System.out.format(" ----> Rojo %s!!!", tabla[0][i] >(int)(0.299f*i) ? "Aumentado" : "Disminuido");
			if( tabla[1][i] != (int)(0.587f*i) )
				System.out.format(" ----> Verde %s!!!", tabla[1][i] >(int)(0.587f*i) ? "Aumentado" : "Disminuido");
			if( tabla[2][i] != (int)(0.114f*i) )
				System.out.format(" ----> Azul %s!!!", tabla[2][i] > (int)(0.114f*i) ? "Aumentado" : "Disminuido");
			System.out.println();
		}
	}
	
	static void contarErroresTabla(int tabla[][]){
		long error1 = 0;
		long error2 = 0;
		long error3 = 0;
		for(int r = 0; r<256; r++)
			for(int g = 0; g<256; g++)
				for(int b = 0; b<256; b++){
					int l1 = tabla[0][r] + tabla[1][g] + tabla[2][b];
					int l2 = (int)(0.299f*r + 0.587f*g + 0.114f*b);
					switch ( Math.abs( l1 - l2) ){
					case 0: 
						break;
					case 1: 
						error1++; 
						break;
					case 2: 
						error2++;
						break;
					default:
						error3++;
						break;
					}
				}
		System.out.format("Errores de 1: %,10d %5.2f%%\n", error1, error1*100f/Math.pow(2, 24) );
		System.out.format("Errores de 2: %,10d %5.2f%%\n", error2, error2*100f/Math.pow(2, 24) );
		System.out.format("Errores de 3: %,10d %5.2f%%\n", error3, error3*100f/Math.pow(2, 24) );
		System.out.println("              ----------");
		System.out.format("              %,10d %5.2f%%\n", error1 + error2 + error3, (error1 + error2 + error3)*100f / Math.pow(2, 24) );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		mostrarTabla(luminanceData3);
		System.out.println();
		contarErroresTabla(luminanceData);
		System.out.println();
		contarErroresTabla(luminanceData2);
		System.out.println();
		contarErroresTabla(luminanceData3);
		
		long r1 = 0, r2= 0, r3 = 0;
		long nanos1 = System.nanoTime();
		for(int i = 0; i< 100; i++)
			r1 ^= calcular1();
		long nanos2 = System.nanoTime();
		for(int i = 0; i< 100; i++)
			r2 ^= calcular2();
		long nanos3 = System.nanoTime();
		for(int i = 0; i< 100; i++)
			r3 ^= calcular3();
		long nanos4 = System.nanoTime();
		
		System.out.format("Tiempo suma tabla ints             %,14d nSegundos  Resultado de control [%d]\n",nanos2-nanos1, r1);
		System.out.format("Tiempo multiplicacion suma floats  %,14d nSegundos  Resultado de control [%d]\n",nanos3-nanos2, r2);
		System.out.format("Tiempo suma tabla floats           %,14d nSegundos  Resultado de control [%d]\n",nanos4-nanos3, r3);
	}
}
