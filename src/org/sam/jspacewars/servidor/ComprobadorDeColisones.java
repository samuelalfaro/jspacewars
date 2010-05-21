package org.sam.jspacewars.servidor;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.sam.elementos.Colisionable;

class ComprobadorDeColisones {

	private final Queue<Colisionable> colaConjuntoA;
	private final Queue<Colisionable> colaConjuntoB;

	private final Queue<Colisionable> colaActivosA;
	private final Queue<Colisionable> colaActivosB;

	ComprobadorDeColisones(int tamColas) {
		colaConjuntoA = new ArrayBlockingQueue<Colisionable>(tamColas);
		colaConjuntoB = new ArrayBlockingQueue<Colisionable>(tamColas);
		colaActivosA = new ArrayBlockingQueue<Colisionable>(tamColas);
		colaActivosB = new ArrayBlockingQueue<Colisionable>(tamColas);
	}

	private int compararInicios(Colisionable c1, Colisionable c2) {
		return 0;
		// return ((Elemento)c1).posX - ((Elemento)c2).posX;
	}

	private int compararInicioFin(Colisionable c1, Colisionable c2) {
		return 0;
		// return ((Elemento)c1).posX - (((Elemento)c2).posX + 10);
	}

	public void comprobarColisiones(Collection<? extends Colisionable> unConjuntoA,
			Collection<? extends Colisionable> unConjuntoB) {
		if( unConjuntoA.size() == 0 || unConjuntoB.size() == 0 )
			return;

		Queue<Colisionable> conjuntoA = colaConjuntoA;
		conjuntoA.clear();
		conjuntoA.addAll(unConjuntoA);

		Queue<Colisionable> conjuntoB = colaConjuntoB;
		conjuntoB.clear();
		conjuntoB.addAll(unConjuntoB);

		Queue<Colisionable> activosA = colaActivosA;
		activosA.clear();
		Queue<Colisionable> activosB = colaActivosB;
		activosB.clear();

		do{
			if( conjuntoA.size() > 0
					&& (conjuntoB.size() == 0 || compararInicios(conjuntoA.peek(), conjuntoB.peek()) < 0) ){
				Colisionable actual = conjuntoA.poll();
				// Se eliminan los elementos activos que han salido cuando
				// llega el nuevo
				while( activosA.size() > 0 && compararInicioFin(activosA.peek(), actual) > 0 )
					activosA.poll();
				while( activosB.size() > 0 && compararInicioFin(activosB.peek(), actual) > 0 )
					activosB.poll();
				activosA.offer(actual);
				for( Colisionable otro: activosB )
					if( actual.hayColision(otro) )
						actual.colisionar(otro);
			}else{
				Colisionable actual = conjuntoB.poll();
				// Se eliminan los elementos activos que han salido cuando
				// llega el nuevo
				while( activosA.size() > 0 && compararInicioFin(activosA.peek(), actual) > 0 )
					activosA.poll();
				while( activosB.size() > 0 && compararInicioFin(activosB.peek(), actual) > 0 )
					activosB.poll();
				activosB.offer(actual);
				for( Colisionable otro: activosA )
					if( actual.hayColision(otro) )
						actual.colisionar(otro);
			}
		}while( conjuntoA.size() > 0 || conjuntoB.size() > 0 );
	}
}