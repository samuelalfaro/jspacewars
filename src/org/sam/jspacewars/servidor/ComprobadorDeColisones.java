package org.sam.jspacewars.servidor;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.sam.colisiones.Colisionable;

class ComprobadorDeColisones {
	
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

	ComprobadorDeColisones(int tamColas) {
		colaConjuntoA = new ArrayDeque<Colisionable>(tamColas);
		colaConjuntoB = new ArrayDeque<Colisionable>(tamColas);
		colaActivosA = new ArrayDeque<Colisionable>(tamColas);
		colaActivosB = new ArrayDeque<Colisionable>(tamColas);
	}

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