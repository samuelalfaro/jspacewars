/* 
 * GetterArraySelector.java
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
 * Clase contenedora con las implementaciones de un <i>GetterArraySelector</i>, tanto en precisión {@code double},
 * {@code float}, como {@code int}.
 * <p>Se considera un <i>GetterArraySelector</i> a un tipo de <i>Getter</i> que contiene los valores distribuidos
 * uniformente, por tanto no es necesario almacenar las claves.
 * En esta implemetación se almacena la escala y traslación que se aplica a la clave para obtener el índice
 * del vector dónde se encuentra el valor que será devuelto.</p>
 * <p>Esta implementación es usada por {@code GettersFactory} cuando no pasan claves y
 * el {@code MetodoDeInterpolacion} seleccionado es {@linkplain MetodoDeInterpolacion.Predefinido#ESCALON}.</p>
 * <p><u>Nota:</u> Los valores no se interpolan realmente, pasando bruscamente de un valor a otro, lo que da esta
 * forma característica de escalón.</p>
 */
final class GetterArraySelector{
	
	private GetterArraySelector(){}
	
	private static<T> T valueAt(int index, T[] array){
		return array[(index < 0) ? 0 : index < array.length ? index : array.length - 1 ];
	}
	
    /**
     * Clase que implementa un {@code GetterArraySelector} con precisión {@code double}.
     * @param <T> Tipo genérico de datos empleados.
     */
	final static class Double<T> implements Getter.Double<T>{
		private final double scale;
		private final double translation;
		private final T values[];

		/**
		 * Constructor que crea un {@code GetterArraySelector} con precisión {@code double}, 
		 * con una escala = 1 y una traslación = 0.
		 * @param values valores que serán devueltos.
		 */
		Double(T[] values) {
			this(1.0, 0.0, values);
		}

		/**
		 * Constructor que crea un {@code GetterArraySelector} con precisión {@code double}.
		 * @param scale escala que se aplicará a la clave.
		 * @param translation traslación que se aplicará a la clave.
		 * @param values valores que serán devueltos.
		 */
		Double(double scale, double translation, T[] values) {
			this.scale = (values.length-1)/ scale;
			this.translation = -translation;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(double key) {
			return valueAt((int) ((key + translation) * scale + 0.5), values);
		}
	}
	
    /**
     * Clase que implementa un {@code GetterArraySelector} con precisión {@code float}.
     * @param <T> Tipo genérico de datos empleados.
     */
	final static class Float<T> implements Getter.Float<T>{
		private final float scale;
		private final float translation;
		private final T values[];

		/**
		 * Constructor que crea un {@code GetterArraySelector} con precisión {@code float}, 
		 * con una escala = 1 y una traslación = 0.
		 * @param values valores que serán devueltos.
		 */
		Float(T[] values) {
			this(1.0f, 0.0f, values);
		}

		/**
		 * Constructor que crea un {@code GetterArraySelector} con precisión {@code float}.
		 * @param scale escala que se aplicará a la clave.
		 * @param translation traslación que se aplicará a la clave.
		 * @param values valores que serán devueltos.
		 */
		Float(float scale, float translation, T[] values) {
			this.scale = (values.length-1)/ scale;
			this.translation = -translation;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(float key) {
			return valueAt((int) ((key + translation) * scale + 0.5f), values);
		}
	}
	
    /**
     * Clase que implementa un {@code GetterArraySelector} con precisión {@code int}.
     * @param <T> Tipo genérico de datos empleados.
     */
	final static class Integer<T> implements Getter.Integer<T>{
		private final int scale;
		private final int translation;
		private final T values[];

		/**
		 * Constructor que crea un {@code GetterArraySelector} con precisión {@code int}, 
		 * con una escala = 1 y una traslación = 0.
		 * @param values valores que serán devueltos.
		 */
		Integer(T[] values) {
			this(1, 0, values);
		}

		/**
		 * Constructor que crea un {@code GetterArraySelector} con precisión {@code int}.
		 * @param scale escala que se aplicará a la clave.
		 * @param translation traslación que se aplicará a la clave.
		 * @param values valores que serán devueltos.
		 */
		Integer(int scale, int translation, T[] values) {
			this.scale = (values.length-1)/ scale;
			this.translation = -translation;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(int key) {
			return valueAt(((key + translation) * scale), values);
		}
	}
}
