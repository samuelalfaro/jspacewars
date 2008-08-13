package org.sam.j3d;

import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.BranchGroup;

public class Eliminador extends Thread {
	private boolean 	hayQueEliminar;
	private List<BranchGroup> elementos;
		
	public Eliminador(){
		elementos = new LinkedList<BranchGroup>();
		hayQueEliminar = false;
	}
		
	public void eliminar(BranchGroup elemento){
		elementos.add(elemento);
		hayQueEliminar = true;
	}
		
	public void run(){
		while(true){
			while(!hayQueEliminar)
				Thread.yield();
			while(!elementos.isEmpty())
				elementos.remove(0).detach();
			hayQueEliminar = false;
		}
	}
}
