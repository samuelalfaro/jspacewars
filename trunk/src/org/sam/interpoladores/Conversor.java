/* 
 * Conversor.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
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
