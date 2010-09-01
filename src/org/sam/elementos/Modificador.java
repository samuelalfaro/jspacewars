package org.sam.elementos;

/**
 * Interface que proporciona el método para que la clase que lo implemente pueda modificar
 * un objeto de otra clase {@code Modificable}. Esta clase {@code Modificable} será la
 * responsable de instanciar y proporcionar su {@code Modificador}.
 */
public interface Modificador {
	/**
	 * Método que se se encarga de modificar un objeto {@code Modificable} durante un periodo de tiempo determinado.
	 * @param steep periodo de tiempo en segundos durante el cual se realiza la modificiación.
	 * @return <ul>
	 * <li>{@code true}  si se puede segir modificando el objeto asociado.</li>
	 * <li>{@code false} en caso contrario.</li>
	 * </ul>
	 */
	public boolean modificar(float steep);
}
