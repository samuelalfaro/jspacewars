package org.sam.jspacewars.servidor.tareas;

import java.util.Random;

public class PruebaTareas {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		Tarea tarea = new Secuencia( new Tarea[]{
			new SetAnguloCanion(1,0),
			new Disparar(1, 2l),
			new SetAnguloCanion(11,0),
			new SetAnguloCanion(12,0),
			new Disparar(1, 4l),
			new SetAnguloCanion(2,0),
			new SetAnguloCanion(3,0),
			new Disparar(2, 7l),
			new Disparar(3, 4l),
			new SetAnguloCanion(4,0),
			new SetAnguloCanion(5,0),
			new Disparar(4, 2l),
			new Disparar(5, 1l),
			new SetAnguloCanion(6,0),
			new SetAnguloCanion(7,0),
			new SetAnguloCanion(8,0),
			new SetAnguloCanion(9,0)
		});
		
		/*/
		Tarea tarea = new TareasParalelas( new Tarea[]{
			new Mover(30),
			new Secuencia( new Tarea[]{
				new Esperar(10),
				new Esperar(3,5),
				new TareasParalelas( new Tarea[]{
					new Secuencia( new Tarea[]{
						new SetAnguloCanion(1,0),
						new OrientarCanion(1, 1, 5),
					}),
					new Disparar(1,5)
				})
			}),
			new Secuencia( new Tarea[]{
				new Esperar(15),
				new Esperar(3,5),
				new TareasParalelas( new Tarea[]{
					new Secuencia( new Tarea[]{
						new SetAnguloCanion(2,0),
						new OrientarCanion(2,-1,5),
					}),
					new Disparar(2,5)
				})	
			}),
		});
		//*/
		
		Random r = new Random();
		long i = 0;
		while(i <= 30){
			System.out.println("\nInstante: " + i);
			long f = i + r.nextInt(4) +1;
			tarea.realizar( i, f );
			i = f;
		}
	}
}
