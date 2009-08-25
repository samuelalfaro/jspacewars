package tips;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.sam.util.Consola;

public class PruebaConsolas {

	static public void main(String args[]){
		
		JFrame frame = new JFrame("Prueba Consolas");

		frame.setSize(400,400);
		frame.setDefaultCloseOperation(3);
		
		// Area donde se mostrar� System.out
		JTextArea tOut = new JTextArea();
		tOut.setEditable(false);
		tOut.setForeground(Color.BLUE);
		tOut.setRows(10);
		tOut.setColumns(40);

		// Area donde se mostrar� System.err
		JTextArea tErr = new JTextArea();
		tErr.setEditable(false);
		tErr.setForeground(Color.RED);
		tErr.setRows(10);
		tErr.setColumns(40);
		
		// Area donde se mostrar�n los demas logs en este caso como
		// queremos darle estilos a los log, trabajamos con un JTextPane
		JTextPane tLog = new JTextPane(new DefaultStyledDocument());
		tLog.setEditable(false);
		
		// Se a�aden los componentes de texto a la ventana.
		frame.getContentPane().setLayout(new GridLayout(3,1));
		frame.getContentPane().add(new JScrollPane(tOut));
		frame.getContentPane().add(new JScrollPane(tErr));
		frame.getContentPane().add(new JScrollPane(tLog));
		frame.pack();
		frame.setVisible(true);
		
		// Se redirecciona la salida estandar al PrintStream asociado a tOut
		// Ademas se limita el tama�o del documento a 10000  caracteres
		System.setOut(Consola.getLog(tOut,10000));
		// Se redirecciona la salida de errores al PrintStream asociado a tErr
		// se le da un valor de mirror = true para seguir mostrando 
		// los mensajes por la salida de errores original
		System.setErr(Consola.getLog(tErr, System.err));

		// Se crea un estilo para log1
		SimpleAttributeSet at;
		at = new SimpleAttributeSet();
		StyleConstants.setForeground(at,Color.GREEN.darker().darker().darker());
		StyleConstants.setBold(at,true);
		
		// Se crea el log con estilo log1, asociado a tLog
		final PrintStream log1 = Consola.getLog(tLog,at);
		
		// Se crea otro estilo para log2
		at = new SimpleAttributeSet();
		StyleConstants.setForeground(at,Color.MAGENTA.darker().darker());
		StyleConstants.setItalic(at,true);
		// Se crea el log con estilo log2, asociado a tLog
		PrintStream log2 = Consola.getLog(tLog,at);

		/*
		Se crean dos hilos para probar el acceso concurrente
		
		NOTA: se ha detectado q cuando se imprime con println
		pueden aparecer los datos escritos, luego otra linea de otro hilo
		y por ultimo el salto de linea. Esto s�lo se ha detectado
		caundo varios logs comparten un mismo componente de texto y hay 
		acceso concurrente.
		As� q en estos casos se recomienda 
		sustituir los println(x) por print(x+"\n")
		 */
		Thread hilo1 = new Thread(){
			public void run(){
				for(int cont= 0; cont < 100; cont++){
					System.out.println("Mensaje del hilo 1 por System.out "+cont);
					log1.print("Mensaje del hilo 1 por Log1 "+cont+"\n");
				}
			}
		};
		
		Thread hilo2 = new Thread(){
			public void run(){
				for(int cont= 0; cont < 100; cont++){
					System.err.println("Mensaje del hilo 2 por System.err "+cont);
					log1.print("Mensaje del hilo 2 por Log1 "+cont+"\n");
				}
			}
		};
		
		System.out.println("Hola");

		hilo1.start();
		hilo2.start();
		
		for(int cont= 0; cont < 100; cont++){
			System.out.println("Mensaje por System.out "+cont);
			System.err.println("Mensaje por System.err "+cont);
			log1.print("Mensaje por Log1 "+cont+"\n");
			log2.print("Mensaje por Log2 "+cont+"\n");
		}

		try{
			hilo1.join();
			hilo2.join();
		}catch(InterruptedException ignorada){
		}
		System.out.print("Ya ta!!\n");
	}
}
