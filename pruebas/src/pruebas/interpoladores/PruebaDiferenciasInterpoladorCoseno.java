/* 
 * PruebaDiferenciasInterpoladorCoseno.java
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
package pruebas.interpoladores;

import org.sam.interpoladores.*;

import pruebas.util.FastMath;

@SuppressWarnings("deprecation")
public class PruebaDiferenciasInterpoladorCoseno {

	public strictfp static void main(String[] args) {
		Getter.Double<Double> doubleCos = GettersFactory.Double.create(
				Keys.HOMOGENEAS, Math.PI*2, 0.0, new double[] {1.0,-1.0,1.0},MetodoDeInterpolacion.Predefinido.COSENOIDAL);
		
		Getter.Float<Float> floatCos = GettersFactory.Float.create(
				Keys.HOMOGENEAS, (float)Math.PI*2, 0.0f, new float[] {1.0f,-1.0f,1.0f},MetodoDeInterpolacion.Predefinido.COSENOIDAL);
		
		for(float alfa=0.0f, pi2 = (float)Math.PI*2, ia = (float)Math.PI/360; alfa <= pi2; alfa+=ia)
			System.out.println("Angulo:\t"+alfa+(Math.abs(Math.cos(alfa)-floatCos.get(alfa))>0.01?"\tMal"+"\n\tcos(alfa):"+Math.cos(alfa)+"\n\tcIn(alfa):"+floatCos.get(alfa):"\tBien") );
		
		double difMaxima = 0.0;
		for(float alfa=0.0f, pi2 = (float)Math.PI*2; alfa <= pi2; alfa+=0.0001f){
			double dif = Math.abs(Math.cos(alfa)-floatCos.get(alfa));
			if ( dif > difMaxima)
				difMaxima = dif;
		}
		System.out.println("Diferencia maxima:\t"+difMaxima);
		
		long tAnt, tAct;

		double[] angulos = new double[1024];
		for(int i=0; i< angulos.length; i++)
			angulos[i] = (Math.random() * Math.PI * 2);
		
		tAnt = System.currentTimeMillis();
		for(int i=0; i< 100000; i++)
			for(double alfa:angulos)
				Math.cos(alfa);
		tAct = System.currentTimeMillis();
		System.out.println("Tiempo coseno:\t"+(tAct-tAnt));
		
		tAnt = System.currentTimeMillis();
		for(int i=0; i< 100000; i++)
			for(double alfa:angulos)
				doubleCos.get(alfa);
		tAct = System.currentTimeMillis();
		System.out.println("Tiempo coseno interpolado(double):\t"+(tAct-tAnt));

		tAnt = System.currentTimeMillis();
		for(int i=0; i< 100000; i++)
			for(double alfa:angulos)
				floatCos.get((float)alfa);
		tAct = System.currentTimeMillis();
		System.out.println("Tiempo coseno interpolado(float):\t"+(tAct-tAnt));
		
		
		for(float alfa=0.0f, pi2 = (float)Math.PI*2, ia = (float)Math.PI/360; alfa <= pi2; alfa+=ia)
			System.out.println("Angulo:\t"+alfa+(Math.abs(Math.cos(alfa)-FastMath.cosRadians(alfa))>0.01?"\tMal"+"\n\tcos(alfa):"+Math.cos(alfa)+"\n\tcFM(alfa):"+FastMath.cosRadians(alfa):"\tBien") );
		
		for(float alfa=0.0f, pi2 = (float)Math.PI*2; alfa <= pi2; alfa+=0.0001f){
			double dif = Math.abs(Math.cos(alfa)-FastMath.cosRadians(alfa));
			if ( dif > difMaxima)
				difMaxima = dif;
		}
		System.out.println("Diferencia maxima:\t"+difMaxima);
		
		tAnt = System.currentTimeMillis();
		for(int i=0; i< 100000; i++)
			for(double alfa:angulos)
				FastMath.cosRadians((float)alfa);
		tAct = System.currentTimeMillis();
		System.out.println("Tiempo coseno fastMath:\t"+(tAct-tAnt));
	}
}
