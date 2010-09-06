package org.sam.jogl;

/**
 * Clase que representa un {@code Nodo} final (hoja) del grafo de escena.
 */
public abstract class Hoja implements Nodo {
	private transient Nodo parent;

	/**
	 * Construtor por defecto que crea un {@code Objeto3DAbs}.
	 */
	protected Hoja() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setParent(Nodo parent) {
		this.parent = parent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Nodo getParent() {
		return parent;
	}
	
	/**
	 * Vector de nodos vacío que será devuelto cuando 
	 * se invoque el método {@linkplain #getChilds()}.
	 */
	private static transient final Nodo[] nodos = new Nodo[]{}; 

	/**
	 * {@inheritDoc}
	 * <p>Este método siempre devuelve un vector de nodos vacío, puesto que
	 * los elementos de esta clase y sus derivadas, representan los nodos 
	 * finales (hojas), del grafo de escena.</p>
	 * @return Un vector de nodos vacío.
	 */
	@Override
	public final Nodo[] getChilds(){
		return nodos;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Hoja clone();
}
