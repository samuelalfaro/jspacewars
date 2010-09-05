/* 
 * ClientData.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars.cliente;

import java.util.*;

import org.sam.elementos.Modificador;
import org.sam.jogl.Instancia3D;
import org.sam.util.Lista;

/**
 * <p>Clase que encapsula todos los datos que enviará y recibirá el cliente, para
 * facilitar la comunicacion entre las distintas clases que componen de la parte
 * cliente.</p>
 * <p>Los atributos de esta clase no contienen modificadores de visibilidad, para
 * evitar la necesidad de crear <i>getters</i> ni <i>setters</i>, ya que sólo
 * serán accesibles por las clases que formen parte de este paquete.</p>
 */
class ClientData {

	/**
	 * Entero que enmascara en sus distintos bits, los estados de las distintas
	 * teclas pulsadas.
	 * <p>
	 * Será modificado por la clase
	 * {@link org.sam.jspacewars.cliente.GameKeyListener GameKeyListener} y
	 * leido por la clase {@link org.sam.jspacewars.cliente.Cliente Cliente}
	 * para enviarlo al servidor.
	 * </p>
	 */
	transient int key_state;

	/**
	 * Entero donde se almacena el número de bombas que dispone el protagonista.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrarlo en pantalla.
	 * </p>
	 */
	transient int nBombas;
	/**
	 * Entero donde se almacena el número de vidas que dispone el protagonista.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrarlo en pantalla.
	 * </p>
	 */
	transient int nVidas;
	/**
	 * Entero donde se almacena los puntos conseguidos por el protagonista.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrarlo en pantalla.
	 * </p>
	 */
	transient int puntos;

	/**
	 * Vector de enteros donde se almacenan los valores de los niveles fijos de
	 * la nave protagonista.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrarlo en pantalla.
	 * </p>
	 */
	final transient int[] nivelesFijos;
	/**
	 * Vector de enteros donde se almacenan los valores de los niveles actuales
	 * de la nave protagonista.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrarlo en pantalla.
	 * </p>
	 */
	final transient int[] nivelesActuales;
	/**
	 * Vector de enteros donde se almacenan los valores de los niveles
	 * disponibles de la nave protagonista.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrarlo en pantalla.
	 * </p>
	 */
	final transient int[] nivelesDisponibles;

	/**
	 * Entero donde se almacena el indicador que habilita las distintas mejoras
	 * de la nave protagonista.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrarlo en pantalla.
	 * </p>
	 */
	transient int indicador;
	/**
	 * Entero donde se almacena el grado de la nave protagonista.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrarlo en pantalla.
	 * </p>
	 */
	transient int grado;

	/**
	 * Lista que contiene todas las {@code Instancia3D} que son visibles en pantalla.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y leido por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para
	 * mostrar, en pantalla, las distintas {@code Instancia3D} que contiene.
	 * </p>
	 */
	final transient List<Instancia3D> elementos;
	/**
	 * Lista que contiene todos los modificadores de los elementos modificables
	 * que son visibles en pantalla.
	 * <p>
	 * Será modificado por la clase {@link org.sam.jspacewars.cliente.Cliente
	 * Cliente} con los datos recibidos del servidor y usado por la clase
	 * {@link org.sam.jspacewars.cliente.Renderer Renderer} para modificar 
	 * los elementos modificables visibles.
	 * </p>
	 */
	final transient Collection<Modificador> modificadores;

	/**
	 * Constructor que inicializa los distintos valores contenidos por la clase.
	 */
	ClientData() {

		key_state = 0;

		nBombas = 0;
		nVidas = 0;
		puntos = 0;

		nivelesFijos = new int[5];
		nivelesActuales = new int[5];
		nivelesDisponibles = new int[5];

		indicador = 0;
		grado = 0;

		elementos = new Lista<Instancia3D>();
		modificadores = new LinkedList<Modificador>();
	}
}