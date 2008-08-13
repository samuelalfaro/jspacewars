package org.sam.red;

/**
 * Este interface proporciona, los m�todos para tratar las colisones entre objetos.
 */
public interface Colisionable{
	
	/**
	 * Este m�todo evalua si hay una colisi�n, entre el objeto y otro objeto <code>Colisionable</code>.
	 * 
	 * @param otro El otro objeto con el que se evalua si hay una colisi�n.
	 * 
	 * @return <ul><li><code>true</code>, cuando hay una colisi�n. <li><code>false</code>, en caso contrario.</ul>
	 */
	public boolean hayColision(Colisionable otro);
	
	/**
	 * Este m�todo trata la colisi�n, entre el objeto y otro objeto <code>Colisionable</code>.
	 * 
	 * @param otro El otro objeto con el que se produce la colisi�n.
	 */
	public void colisionar(Colisionable otro);
	
	/**
	 * Este m�todo devuleve la fuerza con la que impacta el objeto sobre otro.
	 * 
	 * @return Valor de dicha fuerza.
	 */
	public int getFuerzaImpacto();
	
	/**
	 * Este m�todo aplica el da�o causado por un impacto al objeto.
	 * 
	 * @param fuerzaDeImpacto Fuerza de impacto recibida. 
	 */
	public void recibirImpacto(int fuerzaDeImpacto);

	/**
	 * Este m�todo indica si un objeto <code>Colisionable</code> est� destruido.
	 * 
	 * @return <ul><li><code>true</code>, cuando est� destruido. <li><code>false</code>, en caso contrario.</ul>
	 */
	public boolean isDestruido();

}
