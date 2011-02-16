/* 
 * Consola.java
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
package org.sam.util;

import java.io.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * Clase que proporciona métodos estáticos para poder crear instancias de la clase encapsulada {@code OutputDocumentStream},
 * que es un {@code OutputStream} que permite mostrar el texto en un componente de texto a modo de consola.
 */
public class Consola {

	/**
	 * Clase derivada de OutputStream que sobreescribe los métodos: <ul>
	 * <li>{@code public void write(int b)}
	 * <li>{@code public voidwrite(byte buf[], int off, int len)}</ul>
	 * para que además de mostrar el texto por su salida orignal, aparezca por un componente de texto asociado
	 */
	private static final class OutputDocumentStream extends OutputStream{
		private final JTextComponent textComponent;
		private final AttributeSet attributeSet;
		private final int tamIdeal;
		private final int excesoMax;
		private final OutputStream mirror;

		OutputDocumentStream(JTextComponent tc, AttributeSet at, int tamIdeal, OutputStream  mirror) {
			if (tc == null)
				throw new IllegalArgumentException(
					"\nError creando log:" +
					"\n[ El componente que contendrá la consola no puede ser nulo ]"
				);
			this.textComponent = tc;
			this.attributeSet = at;
			this.tamIdeal = tamIdeal;
			this.excesoMax = tamIdeal/4;
			this.mirror = mirror;
		}
		
		public void write(int b) throws IOException {
			// Se captura siempre el documento, porque el JTextComponent puede cambiar
			// el documento
			Document doc = textComponent.getDocument();
			try {
				synchronized (this) {
					// Se inserta el dato en el documento
					doc.insertString(doc.getLength(), String.valueOf( ( char )b ), attributeSet);
					// Se cambia la posicion del JTextComponent para que muestre
					// lo último insertado
					textComponent.setCaretPosition(doc.getLength());
					// En caso de q se haya definido un tamaño de documento
					// se comprueba si hay q eliminar algo
					if(tamIdeal >0){
						// Se calcula el tamaño del docueneto y se compara con el tamaño ideal
						int exceso = doc.getLength() - tamIdeal;
						//Para no estar continumente eliminando se deja de margen maxExceso
						if (exceso >= excesoMax)
							// Elimina el texto mas antiguo
							doc.remove(0, exceso);
					}
				}
			}catch (NullPointerException ignorada){
				// el documento obtenido es null
			} catch (BadLocationException ignorada) {
				// se ha intentado insertar o eliminar en una posicion inválida
				// del documento
			}
			if (mirror != null){
				mirror.write(b);
			}
		}

		public void write(byte buf[], int off, int len) throws IOException {
			Document doc = textComponent.getDocument();
			try {
				synchronized (this) {
					doc.insertString(doc.getLength(), new String(buf, off, len), attributeSet);
					textComponent.setCaretPosition(doc.getLength());
					if(tamIdeal >0){
						int exceso = doc.getLength() - tamIdeal;
						if (exceso >= excesoMax)
							doc.remove(0, exceso);
					}
				}
			} catch (NullPointerException ignorada) {
			} catch (BadLocationException ignorada) {
			}
			if (mirror != null){
				mirror.write(buf, off, len);
			}
		}
	}
	
	/**
	 * Método que crea un {@code PrintStream}, que mostrará su contenido por un componente de
	 * texto.
	 * 
	 * @param tc Componente de texto donde se va a mostrar el contenido.
	 * 
	 * @return El {@code PrintStream} creado.
	 * 
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc){
		return getLog( tc, null, -1, null);
	}

	/**
	 * Método que crea un {@code PrintStream}, que mostrará su contenido por un componente de
	 * texto.
	 * 
	 * @param tc Componente de texto donde se va a mostrar el contenido.
	 * @param at Atributos del texto mostrado.
	 * 
	 * @return El {@code PrintStream} creado.
	 * 
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc, AttributeSet at){
		return getLog( tc, at, -1, null);
	}
	
	/**
	 * Método que crea un {@code PrintStream}, que mostrará su contenido por un componente de
	 * texto.
	 * 
	 * @param tc Componente de texto donde se va a mostrar el contenido.
	 * @param tamDoc Tamaño máximo del documento, cuando el documento llega al tamaño maximo, se van eliminando
	 * los caracteres más antiguos. Si el tamaño es menor de 0, y el tamaño máximo es indifenido, y no se elimina nada. 
	 * 
	 * @return El {@code PrintStream} creado.
	 * 
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc, int tamDoc){
		return getLog( tc, null, tamDoc, null);
	}

	/**
	 * Método que crea un {@code PrintStream}, que mostrará su contenido por un componente de
	 * texto.
	 * 
	 * @param tc Componente de texto donde se va a mostrar el contenido.
	 * @param at Atributos del texto mostrado.
	 * @param tamDoc Tamaño máximo del documento, cuando el documento llega al tamaño maximo, se van eliminando
	 * los caracteres más antiguos. Si el tamaño es menor de 0, y el tamaño máximo es indifenido, y no se elimina nada. 
	 * 
	 * @return El {@code PrintStream} creado.
	 * 
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc, AttributeSet at, int tamDoc){
		return getLog( tc, at, tamDoc, null);
	}
	
	/**
	 * Método que crea un {@code PrintStream}, que mostrará su contenido por un componente de
	 * texto.
	 * 
	 * @param tc Componente de texto donde se va a mostrar el contenido.
	 * @param mirror Indica el {@code OutputStream} en el que se copiará el texto mostrado en el componente.
	 * 
	 * @return El {@code PrintStream} creado.
	 * 
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc, OutputStream mirror){
		return getLog( tc, null, -1, mirror);
	}
	
	/**
	 * Método que crea un {@code PrintStream}, que mostrará su contenido por un componente de
	 * texto.
	 * 
	 * @param tc Componente de texto donde se va a mostrar el contenido.
	 * @param at Atributos del texto mostrado.
	 * @param tamDoc Tamaño máximo del documento, cuando el documento llega al tamaño maximo, se van eliminando
	 * los caracteres más antiguos. Si el tamaño es menor de 0, y el tamaño máximo es indifenido, y no se elimina nada. 
	 * @param mirror Indica el {@code OutputStream} en el que se copiará el texto mostrado en el componente.
	 * 
	 * @return El {@code PrintStream} creado.
	 */
	public static PrintStream getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror){
		return new PrintStream(new OutputDocumentStream(tc, at, tamDoc, mirror));
	}
}
