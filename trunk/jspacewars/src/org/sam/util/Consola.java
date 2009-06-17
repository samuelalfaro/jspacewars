package org.sam.util;

import java.io.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * Clase que proporciona m�todos est�ticos para poder crear instancias de la clase encapsulada <i>OutputDocumentStream</i> que es
 * un OutputStream que permite mostrar el texto en un componente de texto a modo de consola.
 */
public class Consola {

	private static final IllegalArgumentException 
		textAreaNull =
		new IllegalArgumentException(
			"\nError creando log:" +
			"\n[ El componente que contendr� la consola no puede ser nulo ]");
		
	/**
	 * Clase derivada de OutputStream que sobreescribe los m�todos: <ul>
	 * <li><code>public void write(int b)</code>
	 * <li><code>public voidwrite(byte buf[], int off, int len)</code></ul>
	 * para que ademas de mostrar el texto por su salida orignal, aparezca por un componente de texto asociado
	 */
	private static final class OutputDocumentStream extends OutputStream{
		private final JTextComponent textComponent;
		private final AttributeSet attributeSet;
		private final int tamIdeal;
		private final int excesoMax;
		private final OutputStream mirror;

		private OutputDocumentStream(JTextComponent tc, AttributeSet at, int tamIdeal, OutputStream  mirror) {
			if (tc == null)
				throw textAreaNull;
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
					// lo ultimo insertado
					textComponent.setCaretPosition(doc.getLength());
					// En caso de q se haya definido un tama�o de documento
					// se comprueba si hay q eliminar algo
					if(tamIdeal >0){
						// Se calcula el tama�o del docueneto y se compara con el tama�o ideal
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
				// se ha intentado insertar o eliminar en una posicion invalida
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
	 * M�todo que llama a <code>getLog( tc, null, -1, null)</code>
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc){
		return getLog( tc, null, -1, null);
	}

	/**
	 * M�todo que llama a <code>getLog( tc, at, -1, null)</code>
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc, AttributeSet at){
		return getLog( tc, at, -1, null);
	}
	
	/**
	 * M�todo que llama a <code>getLog( tc, null, tamDoc, null)</code>
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc, int tamDoc){
		return getLog( tc, null, tamDoc, null);
	}

	/**
	 * M�todo que llama a <code>getLog( tc, at, tamDoc, null)</code>
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc, AttributeSet at, int tamDoc){
		return getLog( tc, at, tamDoc, null);
	}
	
	/**
	 * M�todo que llama a <code>getLog( tc, null, -1, mirror)</code>
	 * @see #getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror)
	 */
	public static PrintStream getLog(JTextComponent tc, OutputStream mirror){
		return getLog( tc, null, -1, mirror);
	}
	
	/**
	 * M�todo que crea un PrintStream, que mostrar� su contenido por un componente de
	 * texto.
	 * 
	 * @param tc Componente de texto donde se va a mostrar el contenido
	 * @param at Atributos del texto mostrado
	 * @param tamDoc Tama�o maximo del documento, cuando el documento llega al tama�o maximo, se van eliminando
	 * los caracteres m�s antiguos. Si el tamaño es menor de 0, y el tama�o maximo es indifenido, y no se elimina nada. 
	 * @param mirror Indica el OutputStream en el que se copiara el texto mostrado en el componente.
	 * @return El PrintStream creado
	 */
	public static PrintStream getLog(JTextComponent tc, AttributeSet at, int tamDoc, OutputStream mirror){
		return new PrintStream(new OutputDocumentStream(tc, at, tamDoc, mirror));
	}
}
