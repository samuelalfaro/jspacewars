/* 
 * CompositeGetter.java
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
 * Clase contenedora con las implementaciones de un <i>CompositeGetter</i>, tanto en precisión {@code double},
 * {@code float}, como {@code int}.
 * <p>Este <i>Getter</i> permite hacer una composición similar a <i>g(f(x))</i>.</p>
 */
public final class CompositeGetter{
	private CompositeGetter(){}
	
    /**
     * Clase que implementa un {@code CompositeGetter} con precisión {@code double}.
     * @param <T> Tipo genérico de datos empleados.
     */
    public static final class Double<T> implements Trayectoria.Double<T>{

		private final Getter.Double<java.lang.Double> key;
		private final Getter.Double<T> getter;
		private final Trayectoria.Double<T> trayectoria;

        /**
         * Constructor que genera un {@code CompositeGetter} con precisión {@code double}.
         * @param key {@code Getter} que genera la clave empleada para obtener el dato.
         * @param getter {@code Getter} que contiene los valores que serán devueltos.
         */
        public Double(Getter.Double<java.lang.Double> key, Getter.Double<T> getter) {
			this.key = key;
			this.getter = getter;
			this.trayectoria = getter instanceof Trayectoria.Double<?> ?(Trayectoria.Double<T>) getter : null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(double key) {
			return this.getter.get(this.key.get(key));
		}
		
		/**
		 * {@inheritDoc}
		 * @throws UnsupportedOperationException si el {@code Getter} encapsulado no es una {@code Trayectoria}.
		 */
		@Override
		public T getTan(double key) throws UnsupportedOperationException{
			if (this.trayectoria == null)
				throw new UnsupportedOperationException();
			// TODO calcular bien las derivadas, pues estás deben estar tb en función key.getTan(key)
			return this.trayectoria.getTan(this.key.get(key));
		}
		
		/**
		 * {@inheritDoc}
		 * @throws UnsupportedOperationException si el {@code Getter} encapsulado no es una {@code Trayectoria}.
		 */
		@Override
		public T[] getPosTan(double key) throws UnsupportedOperationException{
			if (this.trayectoria == null)
				throw new UnsupportedOperationException();
			// TODO calcular bien las derivadas, pues estás deben estar tb en función key.getTan(key)
			return this.trayectoria.getPosTan(this.key.get(key));
		}
	}

    /**
     * Clase que implementa un {@code CompositeGetter} con precisión {@code float}.
     * @param <T> Tipo genérico de datos empleados.
     */
    public static final class Float<T> implements Trayectoria.Float<T>{

		private final Getter.Float<java.lang.Float> key;
		private final Getter.Float<T> getter;
		private final Trayectoria.Float<T> trayectoria;

        /**
         * Constructor que genera un {@code CompositeGetter} con precisión {@code float}.
         * @param key {@code Getter} que genera la clave empleada para obtener el dato.
         * @param getter {@code Getter} que contiene los valores que serán devueltos.
         */
        public Float(Getter.Float<java.lang.Float> key, Getter.Float<T> getter) {
			this.key = key;
			this.getter = getter;
			this.trayectoria = getter instanceof Trayectoria.Float<?> ?(Trayectoria.Float<T>) getter : null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(float key) {
			return this.getter.get(this.key.get(key));
		}
		
		/**
		 * {@inheritDoc}
		 * @throws UnsupportedOperationException si el {@code Getter} encapsulado no es una {@code Trayectoria}.
		 */
		@Override
		public T getTan(float key) throws UnsupportedOperationException{
			if (this.trayectoria == null)
				throw new UnsupportedOperationException();
			// TODO calcular bien las derivadas, pues estás deben estar tb en función key.getTan(key)
			return this.trayectoria.getTan(this.key.get(key));
		}
		
		/**
		 * {@inheritDoc}
		 * @throws UnsupportedOperationException si el {@code Getter} encapsulado no es una {@code Trayectoria}.
		 */
		@Override
		public T[] getPosTan(float key) throws UnsupportedOperationException{
			if (this.trayectoria == null)
				throw new UnsupportedOperationException();
			// TODO calcular bien las derivadas, pues estás deben estar tb en función key.getTan(key)
			return this.trayectoria.getPosTan(this.key.get(key));
		}
	}

    /**
     * Clase que implementa un {@code CompositeGetter} con precisión {@code int}.
     * @param <T> Tipo genérico de datos empleados.
     */
    public static final class Integer<T> implements Getter.Integer<T>{

		private final Getter.Integer<java.lang.Integer> key;
		private final Getter.Integer<T> getter;

        /**
         * Constructor que genera un {@code CompositeGetter} con precisión {@code int}.
         * @param key {@code Getter} que genera la clave empleada para obtener el dato.
         * @param getter {@code Getter} que contiene los valores que serán devueltos.
         */
        public Integer(Getter.Integer<java.lang.Integer> key, Getter.Integer<T> getter) {
			this.key = key;
			this.getter = getter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(int key) {
			return this.getter.get(this.key.get(key));
		}
	}
}
