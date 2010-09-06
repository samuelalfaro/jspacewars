package org.sam.jogl;

/**
 * Interface que indica que las clases que lo implementan son un <i>nodo</i> del grafo de escena,
 * proporcionando los métodos necesarios para poder recorrer dicho grafo.
 */
public interface Nodo extends Dibujable, Cloneable{
	
	/**
	 * <i>Setter</i> que asigna el {@code Nodo} antecesor de este {@code Nodo}.
	 * @param parent El {@code Nodo} antecesor.
	 */
	public void setParent(Nodo parent);
	
	/**
	 * <i>Getter</i> que devuelve el {@code Nodo} antecesor de este {@code Nodo}.
	 * @return El {@code Nodo} antecesor.
	 */
	public Nodo getParent();
	
	/**
	 * <i>Getter</i> que devuelve un vector con los descendientes de este {@code Nodo}.
	 * @return El vector de {@code Nodo} descendientes.
	 */
	public Nodo[] getChilds();
	
	/**
	 * Método que crea una copia de este {@code Nodo}.
	 * @return El {@code Nodo} creado.
	 */
	public Nodo clone();

}
