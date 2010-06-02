package org.sam.jspacewars.elementos;

/**
 * Este interface proporciona, los métodos para tratar las impactos entre objetos.
 */
public interface Destruible{
	
	/**
	 * Este método aplica el daño causado por un impacto al objeto.
	 * 
	 * @param fuerzaDeImpacto Fuerza de impacto recibida. 
	 */
	public void recibirImpacto(int fuerzaDeImpacto);

	/**
	 * Este método indica si un objeto <code>Destruible</code> está destruido.
	 * 
	 * @return <ul><li><code>true</code>, cuando está destruido. <li><code>false</code>, en caso contrario.</ul>
	 */
	public boolean isDestruido();

}
