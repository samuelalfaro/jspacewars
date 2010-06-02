package org.sam.colisiones;

/**
 * Este interface proporciona, los métodos para tratar las colisones entre objetos.
 */
public interface Colisionable{
	
	public Limites getLimites();
	
	/**
	 * Este método evalua si hay una colisión, entre el objeto y otro objeto <code>Colisionable</code>.
	 * 
	 * @param otro El otro objeto con el que se evalua si hay una colisión.
	 * 
	 * @return <ul><li><code>true</code>, cuando hay una colisión. <li><code>false</code>, en caso contrario.</ul>
	 */
	public boolean hayColision(Colisionable otro);
	
	/**
	 * Este método trata la colisión, entre el objeto y otro objeto <code>Colisionable</code>.
	 * 
	 * @param otro El otro objeto con el que se produce la colisión.
	 */
	public void colisionar(Colisionable otro);
}
