package org.sam.red.cliente;

import java.awt.Polygon;

class Disparo extends Elemento{
	Disparo(int tipo, Polygon forma){
		super(tipo,forma);
	}
	public Disparo clone(){
		return new Disparo(this.tipo, this.forma);	
	}
	/* (non-Javadoc)
	 * @see org.sam.util.Prototipo#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}