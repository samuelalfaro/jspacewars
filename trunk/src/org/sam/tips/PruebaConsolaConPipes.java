package org.sam.tips;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class PruebaConsolaConPipes{ 

	private static class Dibujante implements Runnable{
		
		private final JTextComponent tc;
		private final int tamIdeal;
		private final int maxExceso;
		
		private byte[]  buf;
		private int len;

		public Dibujante(JTextComponent _tc, int _tamIdeal){
			tc = _tc;
			tamIdeal = _tamIdeal;
			maxExceso = _tamIdeal/4;
		}

		void setBuffer(byte[] _buf){
			buf = _buf;
		}
		void setLen(int _len){
			synchronized(this){
				len = _len;
			}
		}
		
		public void run() {
			Document doc = tc.getDocument();
			if (doc != null) {
				synchronized(this){
					Thread.yield();
					try {
						// Se añade el texto al  documento
						doc.insertString(doc.getLength(), new String(buf, 0, len), null);
					} catch (BadLocationException e) {
					}
					// Cambia el Caret para que lo ultimo insertado sea visible 
					tc.setCaretPosition(doc.getLength());
					if(tamIdeal >0){
						// Se calcula el tamaño del docueneto y se compara con el tamaño ideal
						int exceso = doc.getLength() - tamIdeal;
						// Para no estar continumente eliminando se deja de margen maxExceso
						if (exceso >= maxExceso){
							try {
								// Elimina el texto mas antiguo
								doc.remove(0, exceso);
							} catch (BadLocationException e) {
							}
						}
					}
				}
			}	
		}
	}
	
	private static class Lector extends Thread {
		private final PipedInputStream pi;
		private final JTextComponent tc;
		private final Dibujante dibujante;
		
		public Lector(PipedInputStream _pi, JTextComponent _tc, int tamDoc) {
			this.pi = _pi;
			this.tc = _tc;
			dibujante = new Dibujante(tc, tamDoc);
		}
		
		public void run() {
			final byte[] buf = new byte[2048];
			dibujante.setBuffer(buf);
			try {
				while (true) {
					final int len = pi.read(buf);
					if (len == -1) {
						break;
					}
					dibujante.setLen(len);
					try{
						SwingUtilities.invokeAndWait(dibujante);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static PrintStream nuevaConsola(JTextComponent tc) throws IOException {
		return nuevaConsola(tc,-1);
	}
	
	public static PrintStream nuevaConsola(JTextComponent tc, int tamDoc) throws IOException {
		
		PipedOutputStream outPipe = new PipedOutputStream();
		PrintStream log = new PrintStream(outPipe,false);
		
		Lector lector = new Lector (new PipedInputStream(outPipe),tc,tamDoc);
			
		lector.start();
		
		return log;
	}
	
	static public void main(String args[]){
		
		JFrame frame = new JFrame("Prueba Consolas");

		frame.setSize(400,400);
		frame.setDefaultCloseOperation(3);
		
		JTextArea tOut = new JTextArea();
		tOut.setEditable(false);
		tOut.setForeground(Color.BLUE);
		tOut.setRows(10);
		tOut.setColumns(40);

		JTextArea tErr = new JTextArea();
		tErr.setEditable(false);
		tErr.setForeground(Color.RED);
		tErr.setRows(10);
		tErr.setColumns(40);
		
		JTextArea tLog = new JTextArea();
		tLog.setEditable(false);
		tLog.setRows(10);
		tLog.setColumns(40);
		
		frame.getContentPane().setLayout(new GridLayout(3,1));
		frame.getContentPane().add(new JScrollPane(tOut));
		frame.getContentPane().add(new JScrollPane(tErr));
		frame.getContentPane().add(new JScrollPane(tLog));
		frame.pack();
		frame.setVisible(true);
		
		PrintStream log1 = null;
		try{
			System.setOut(nuevaConsola(tOut));
			System.setErr(nuevaConsola(tErr));
			log1 = nuevaConsola(tLog);
		}catch(IOException e){
		}
		
		System.out.println("Hola");
		System.err.println("Espera un poco...");
		for(int cont= 0; cont < 10; cont++){
			System.out.println("Mensaje "+cont);
		}
		for(int cont= 0; cont < 100; cont++){
			System.out.println("Mensaje a to la ostia por System.out "+cont);
			System.err.println("Mensaje a to la ostia por System.err "+cont);
			log1.println("Mensaje a to la ostia por Log1 "+cont);
		}
		System.out.println("Ya ta!!");
		System.out.close();
		System.err.close();
		log1.close();
	}
}
