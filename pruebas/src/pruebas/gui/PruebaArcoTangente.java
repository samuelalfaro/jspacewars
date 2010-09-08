/* 
 * PruebaArcoTangente.java
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
package pruebas.gui;

public class PruebaArcoTangente {
	
	public static void main(String arg[]){
		double dosPI = Math.PI *2;
		for(double ang =0; ang < dosPI;ang +=.1){
			double tan = Math.tan(ang);
			double x = Math.cos(ang);
			double y = Math.sin(ang);
			System.out.println("ang: "+ang+" tan: "+tan+" angulo: "+(Math.atan2(y,x)<0?dosPI+Math.atan2(y,x):Math.atan2(y,x)));
		}
	}
}
