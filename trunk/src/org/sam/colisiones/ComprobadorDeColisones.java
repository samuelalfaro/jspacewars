/* 
 * ComprobadorDeColisones.java
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
package org.sam.colisiones;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;


/**
 * Clase que sirve para comprobar las colisiones entre dos conjuntos de objetos {@code Colisionable}.
 */
public class ComprobadorDeColisones {
	
	private static int compare(float f1, float f2){
		return
			f1 < f2 ? -1:
			f1 > f2 ?  1:
			0;
	}
	
	private static int compararInicios(Colisionable c1, Colisionable c2) {
		return compare( c1.getLimites().getXMin(), c2.getLimites().getXMin() );
	}

	private static int compararInicioFin(Colisionable c1, Colisionable c2) {
		return compare( c1.getLimites().getXMin(), c2.getLimites().getXMax() );
	}
	
	private final Deque<Colisionable> colaConjuntoA;
	private final Deque<Colisionable> colaConjuntoB;

	private final Deque<Colisionable> colaActivosA;
	private final Deque<Colisionable> colaActivosB;

	/**
	 * Constructor que inicializa las colas internas que se emplearán para realizar las comprobaciones.
	 * @param tamColas Tamaño inicial de las colas internas.
	 */
	public ComprobadorDeColisones(int tamColas) {
		colaConjuntoA = new ArrayDeque<Colisionable>(tamColas);
		colaConjuntoB = new ArrayDeque<Colisionable>(tamColas);
		colaActivosA = new ArrayDeque<Colisionable>(tamColas);
		colaActivosB = new ArrayDeque<Colisionable>(tamColas);
	}

	/**
	 * Método que comprueba las posibles colisiones entre los elementos de los dos conjuntos de {@code Colisionable},
	 * que se reciben como parámetros.</br>
	 * Emplea un algoritmo de barrido horizontal, comprobando únicamente los elementos que se encuentran en la misma
	 * franja vertical, evitando las comprobaciones por fuerza bruta.</br>
	 * No se hacen comprobaciones entre los elementos de un mismo conjunto.
	 * 
	 * @param unConjuntoA Primer conjunto de {@code Colisionable} a evaluar.
	 * @param unConjuntoB Segundo conjunto de {@code Colisionable} a evaluar.
	 */
	public void comprobarColisiones(
			Collection<? extends Colisionable> unConjuntoA,
			Collection<? extends Colisionable> unConjuntoB) {
		
		if( unConjuntoA.size() == 0 || unConjuntoB.size() == 0 )
			return;

		Deque<Colisionable> conjuntoA = colaConjuntoA;
		conjuntoA.clear();
		conjuntoA.addAll(unConjuntoA);

		Deque<Colisionable> conjuntoB = colaConjuntoB;
		conjuntoB.clear();
		conjuntoB.addAll(unConjuntoB);

		Deque<Colisionable> activosA = colaActivosA;
		activosA.clear();
		Deque<Colisionable> activosB = colaActivosB;
		activosB.clear();

		do{
			if( conjuntoA.size() > 0 && (
					conjuntoB.size() == 0 ||
					compararInicios(conjuntoA.peek(), conjuntoB.peek()) < 0
			) ){
				Colisionable actual = conjuntoA.pollFirst();
				// Se eliminan los elementos activos que han salido cuando
				// llega el nuevo
				while( activosA.size() > 0 && compararInicioFin(activosA.peekFirst(), actual) > 0 )
					activosA.pollFirst();
				while( activosB.size() > 0 && compararInicioFin(activosB.peekFirst(), actual) > 0 )
					activosB.pollFirst();
				activosA.addLast(actual);
				for( Colisionable otro: activosB )
					if( actual.hayColision(otro) ){
						actual.colisionar(otro);
						otro.colisionar(actual);
					}
			}else{
				Colisionable actual = conjuntoB.pollFirst();
				// Se eliminan los elementos activos que han salido cuando
				// llega el nuevo
				while( activosA.size() > 0 && compararInicioFin(activosA.peekFirst(), actual) > 0 )
					activosA.pollFirst();
				while( activosB.size() > 0 && compararInicioFin(activosB.peekFirst(), actual) > 0 )
					activosB.pollFirst();
				activosB.addLast(actual);
				for( Colisionable otro: activosA )
					if( actual.hayColision(otro) ){
						actual.colisionar(otro);
						otro.colisionar(actual);
					}
			}
		}while( conjuntoA.size() > 0 || conjuntoB.size() > 0 );
	}
}
