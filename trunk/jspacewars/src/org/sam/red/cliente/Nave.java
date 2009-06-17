package org.sam.red.cliente;

import java.awt.Polygon;

class Nave extends Elemento{
	Nave(int tipo, Polygon forma){
		super(tipo,forma);
	}
	
	public Nave clone(){
		return new Nave(this.tipo, this.forma);	
	}

	/* (non-Javadoc)
	 * @see org.sam.util.Prototipo#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}