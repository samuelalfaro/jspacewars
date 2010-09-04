package org.sam.interpoladores;

/**
 * Interface que proporciona a las clases que lo implementan un método para convertir datos de un tipo en otro.
 * 
 * @param <TIn> Tipo genérico de entrada.
 * @param <TOut> Tipo genérico de salida.
 */
public interface Conversor<TIn,TOut> {

    /**
     * Método para convertir datos de un tipo en otro.
     * 
     * @param t dato del tipo genérico {@code TIn} a convertir.
     * @return dato del tipo genérico {@code TOut} convertido.
     */
    public TOut convert(TIn t);
}
