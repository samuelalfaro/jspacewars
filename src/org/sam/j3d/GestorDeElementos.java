package org.sam.j3d;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;

import org.sam.util.Modificador;
/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GestorDeElementos extends Gestor implements Modificador{
	
	/*
	private static class Insertor extends Thread{
		private final BranchGroup grupo;
		private boolean 	hayQueInsertar;
		private BranchGroup elemento;
		
		public Insertor(BranchGroup grupo){
			this.grupo = grupo;
			hayQueInsertar = false;
		}
		
		public void insertar(BranchGroup elemento){
			this.elemento = elemento;
			hayQueInsertar = true;
		}
		
		public void run(){
			while(true){
				while(!hayQueInsertar)
					Thread.yield();
				grupo.addChild(elemento);
				hayQueInsertar = false;
			}
		}
	}
	*/
	
	private BranchGroup grupo;
//	private final Insertor insertor; 
	
	GestorDeElementos(){
		grupo = new BranchGroup();
		grupo.setCapability(Group.ALLOW_CHILDREN_WRITE);
		grupo.setCapability(Group.ALLOW_CHILDREN_EXTEND);
//		insertor = new Insertor(grupo);
//		insertor.start();
	} 
	
	public void add(Elemento elemento){
		super.add(elemento);
		grupo.addChild(elemento);
//		insertor.insertar(elemento);
	}
	
	public Group getElementos(){
		return grupo;
	}
}
