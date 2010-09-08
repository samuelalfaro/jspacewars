/* 
 * KeySelector.java
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
 * Clase contenedora con las implementaciones de un <i>KeySelector</i>, tanto en precisión {@code double},
 * {@code float}, como {@code int}.
 * <p>Se considera un <i>KeySelector</i> a un tipo de <i>Getter</i> que contiene los valores distribuidos
 * de forma no uniforme. Asociando cada valor a una clave, que se empleará par buscar índice
 * del vector dónde se encuentra el valor que será devuelto.</p>
 * <p>Esta implementación es usada por {@code GettersFactory} cuando se pasan las claves y
 * el {@code MetodoDeInterpolacion} seleccionado es {@linkplain MetodoDeInterpolacion.Predefinido#ESCALON}.</p>
 * <p><u>Nota:</u> Los valores no se interpolan realmente, pasando bruscamente de un valor a otro, lo que da esta
 * forma característica de escalón.</p>
 */
final class KeySelector{
	private KeySelector(){}
	
    /**
     * Clase que implementa un {@code KeySelector} con precisión {@code double}.
     * @param <T> Tipo genérico de datos empleados.
     */
	final static class Double<T> implements Getter.Double<T>{

		private final double[] keys;
		private final T values[];

		/**
		 * Constructor que crea un {@code KeySelector} con precisión {@code double}.
		 * @param keys claves asociadas a los valores.
		 * @param values valores que serán devueltos.
		 */
		Double(double keys[], T[] values) {
			assert(keys.length == values.length);
			this.keys = keys;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(double key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return values[0];
			if ((index == values.length -1) || (key - keys[index] < keys[index + 1] - key ))
				return values[index];
			return values[index+1];
		}
	}
	
    /**
     * Clase que implementa un {@code KeySelector} con precisión {@code float}.
     * @param <T> Tipo genérico de datos empleados.
     */
	final static class Float<T> implements Getter.Float<T>{

		private final float[] keys;
		private final T values[];

		/**
		 * Constructor que crea un {@code KeySelector} con precisión {@code float}.
		 * @param keys claves asociadas a los valores.
		 * @param values valores que serán devueltos.
		 */
		Float(float keys[], T[] values) {
			assert(keys.length == values.length);
			this.keys = keys;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(float key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return values[0];
			if ((index == values.length -1) || (key - keys[index] < keys[index + 1] - key ))
				return values[index];
			return values[index+1];
		}
	}
	
    /**
     * Clase que implementa un {@code KeySelector} con precisión {@code int}.
     * @param <T> Tipo genérico de datos empleados.
     */
	final static class Integer<T> implements Getter.Integer<T>{

		private final int[] keys;
		private final T values[];

		/**
		 * Constructor que crea un {@code KeySelector} con precisión {@code int}.
		 * @param keys claves asociadas a los valores.
		 * @param values valores que serán devueltos.
		 */
		Integer(int keys[], T[] values) {
			assert(keys.length == values.length);
			this.keys = keys;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(int key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return values[0];
			if ((index == values.length -1) || (key - keys[index] < keys[index + 1] - key ))
				return values[index];
			return values[index+1];
		}
	}
}
