package org.sam.util;

/**
 * Clase que encapsula un dato primitivo de tipo {@code boolean}.</br>
 * La finalidad de esta clase es poder compartir un dato booleano entre distintos objetos.
 * Pudiendo consultarlo y modificarlo, ya que el valor encapsulado por calse {@link Boolean}
 * es {@code final} y no se puede cambiar.
 */
public class ModificableBoolean {
	
	private boolean value;
	
	/**
	 * Constructor que genera un {@code ModificableBoolean}. El valor inicial por defecto es {@code false}.
	 */
	public ModificableBoolean(){
		this(false);
	}
	
	/**
	 * Constructor que genera un {@code ModificableBoolean} inicializado
	 * con el valor que se pasa como parámetro.
	 * @param value {@code boolean} que contiene el valor inicial.
	 */
	public ModificableBoolean(boolean value){
		this.value = value;
	}
	
	/**
	 * @return <ul>
	 * <li>{@code true} si el valor encapuslado es {@code true}.</li>
	 * <li>{@code false} en caso contrario</li>
	 * </ul>
	 */
	public boolean isTrue(){
		return value;
	}
	
	/**
	 * @return <ul>
	 * <li>{@code true} si el valor encapuslado es {@code false}.</li>
	 * <li>{@code false} en caso contrario</li>
	 * </ul>
	 */
	public boolean isFalse(){
		return !value;
	}
	
	/**
	 * Método que asigna {@code true} al valor encapsulado.
	 */
	public void setTrue(){
		this.value = true;
	}
	
	/**
	 * Método que asigna {@code false} al valor encapsulado.
	 */
	public void setFalse(){
		this.value = false;
	}
}
