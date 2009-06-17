/*
 * Created on 25-dic-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sam.tips;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ObjetoPrueba {
	int campo1;
	String campo2;
	double campo3;
	boolean campo4;
	
	public ObjetoPrueba(int c1, String c2, double c3, boolean c4){
		campo1 = c1;
		campo2 = c2;
		campo3 = c3;
		campo4 = c4;
	}
	
	public String toString(){
		return (campo1+" "+campo2);
	}
	
	public void abrirVentanaPropiedades(){
		// aki abriamos la ventana de propiedades
		System.out.println("abriendo ventana del objeto "+campo1);
	}
}
