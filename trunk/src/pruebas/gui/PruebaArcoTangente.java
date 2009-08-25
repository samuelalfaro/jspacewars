/*
 * Created on 11-sep-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
