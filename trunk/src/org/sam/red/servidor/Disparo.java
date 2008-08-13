package org.sam.red.servidor;

class Disparo extends Elemento{
	private int incX, incY;
	private boolean destruido;
	
	Disparo(int tipo){
		super(tipo);
		this.destruido = false;
	}
	
	private Disparo(Disparo prototipo){
		super(prototipo);
		this.incX = prototipo.incX;
		this.incY = prototipo.incY;
		this.destruido = false;
	}
	
	public Disparo clone(){
		return new Disparo(this);
	}
	
	public void setVelocidad(int incX, int incY){
		this.incX = incX;
		this.incY = incY;
	}
	
	public void actua(){
		posX += incX;
		posY += incY;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.red.Colisionable#getFuerzaImpacto()
	 */
	public int getFuerzaImpacto() {
		return 10;
	}

	/* (non-Javadoc)
	 * @see org.sam.red.Colisionable#recibirImpacto(int)
	 */
	public void recibirImpacto(int fuerzaDeImpacto) {
		this.destruido = true;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.red.Colisionable#getDestruido()
	 */
	public boolean isDestruido() {
		return this.destruido;
	}
	
	public void setDestruido(boolean destruido) {
		this.destruido = destruido;
	}
}