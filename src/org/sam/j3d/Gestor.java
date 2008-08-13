package org.sam.j3d;

import java.util.Iterator;
import java.util.List;

import org.sam.util.Lista;
import org.sam.util.Modificador;
/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Gestor implements Modificador{
	private List<Modificador> elementos;
	
	public Gestor(){
		elementos = new Lista<Modificador>();
	} 
	
	public void add(Modificador modificador){
		elementos.add(modificador);
	}
	
	public boolean modificar(float steep){
		Iterator<Modificador> i = elementos.iterator();
		while(i.hasNext())
			if( !i.next().modificar(steep) )
				i.remove();
		return true;
	}
}
