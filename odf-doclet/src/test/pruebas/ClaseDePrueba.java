/* 
 * ClaseDePrueba.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of odf-doclet.
 * 
 * odf-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * odf-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with odf-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package pruebas;

import java.util.Collection;
import java.util.Map;

/**
 * Documentacion de la clase ClaseDePrueba.
 * @param <E> Documentacion del parámetro.
 */
public class ClaseDePrueba<E> {
	
	/**
	 *  Documentacion de la clase InerClassAbs.
	 */
	private static abstract class InerClassAbs{
		/**
		 *  Documentacion de la clase InerInerClass.
		 */
		private static class InerInerClass {
		}
	}
	
	/**
	 *  Documentacion de la clase InerClass.
	 */
	private static class InerClass extends InerClassAbs{
	}
	
	/**
	 *  Documentacion de la enumeración DiasDeLaSemana.
	 */
	public static enum DiasDeLaSemana{
		Monday("you can fall apart"),
		Tuesday("break my heart"),
		Wednesday("break my heart"),
		Thursday("doesn't even start"),
		Friday("I'm in love"),
		Saturday("wait"),
		Sunday("always comes too late");

		private final String accion;
		
		DiasDeLaSemana(String accion){
			this.accion = accion;
		}
		/**
		 * Documentación del método {@code accion}.
		 */
		public final String getAccion(){
			return accion;
		}
	}
	
	/**
	 * Documentación de la enumeración {@code ColorPrimario}.
	 */
	public static enum ColorPrimario{
		/** {@code ColorPrimario} que representa el color: ROJO*/
		ROJO, 
		/** {@code ColorPrimario} que representa el color: VERDE*/
		VERDE,
		/** {@code ColorPrimario} que representa el color: AZUL*/
		AZUL;
	}
	
	/** Documentación de a1 */
	@SuppressWarnings("unused")
	private int a1;
	/** Documentación de a2 */
	int[][] a2;
	/** Documentación de a3 */
	protected Map< Collection<? extends E>, Collection<? super E> >[] a3;
	/** Documentación de a4 */
	public InerClass a4[];
	
	/**
	 * Documentación del constructor.
	 * @param parametro1 primer {@code a, b, c} argumento.<br/>
	 * sigue el comentario en otra lista {@linkplain #metodo2(int)}.
	 * @param parametro2 segundo argumento.
	 * @see #metodo1(int, float, Collection)
	 */
	private ClaseDePrueba(InerClass[] parametro1, int parametro2){
		
	}
	
	/**
	 * Documentación del método {@code metodo1}.
	 * @param a1 primer argumento.
	 * @param a2 segundo argumento.
	 * @param a3 tercer argumento.
	 * @return
	 * <ul><li>{@code true} si se cumple la condición.</li>
	 * <li>{@code false} en caso contrario.</li></ul>
	 */
	public boolean metodo1(int a1, float a2, Collection<?> a3){
		return false;
	}
	
	/**
	 * Documentación del método {@code metodo2}.
	 * @param a primer argumento.
	 * @throws IllegalArgumentException causa de la excepción.
	 */
	void metodo2(InerClassAbs.InerInerClass a) throws IllegalArgumentException{
	}
	
	/**
	 * Documentación del método {@code metodo3}.
	 * @param <T> Documentación del parámetro del método.
	 * @param a1 primer argumento.
	 * @param a2 segundo argumento.
	 * @param a3 tercer argumento.
	 * @return valor devuelto.
	 */
	@SuppressWarnings("unused")
	private <T> E metodo3(T a1, Map<? super Number, ? extends InerClass> a2, T... a3){
		return null;
	}
}
