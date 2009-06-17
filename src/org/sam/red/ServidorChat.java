package org.sam.red;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.sam.util.Consola;

public class ServidorChat{

	static class Emisor extends Thread{
		String texto;
		final JTextComponent in;
		final PrintStream out;
		
		public Emisor(JTextComponent in, PrintStream out){
			this.in = in;
			this.out = out;
		}
		
		public void run(){
			do{
				esperaNotificaion();
				if(texto.equalsIgnoreCase("exit")){
					out.close();
					break;
				}
				out.println("Server: "+texto);
			}while(true);
		}
		
		synchronized public void esperaNotificaion(){
			try{
				wait();
			}catch(InterruptedException ignorada){
			}
		}
		
		synchronized public void notificar(){
			texto = in.getText();
			texto = texto.substring(0,texto.length()-1);
			notify();
		}
	}	
	
	static public void main(String args[]){
		
		JFrame frame = new JFrame("Prueba Servidor");

		frame.setSize(400,400);
		frame.setDefaultCloseOperation(3);

		JTextArea logArea = new JTextArea();
		logArea.setBackground(Color.BLACK);
		logArea.setForeground(Color.GREEN);
		logArea.setEditable(false);
		
		JTextArea errArea = new JTextArea();
		errArea.setBackground(Color.BLACK);
		errArea.setForeground(Color.YELLOW);
		errArea.setEditable(false);
		
		JTextArea msgArea = new JTextArea();
		msgArea.setBackground(Color.BLACK);
		msgArea.setForeground(Color.GREEN);
		msgArea.setEditable(false);
		
		final JTextArea inArea = new JTextArea();
		inArea.setBackground(Color.BLACK);
		inArea.setForeground(Color.GREEN);
		inArea.setEditable(true);
		inArea.setRows(4);
		
		JTabbedPane tp = new JTabbedPane(SwingConstants.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);

		JPanel msgPanel = new JPanel(new BorderLayout());
		msgPanel.add(new JScrollPane(msgArea),BorderLayout.CENTER);
		msgPanel.add(new JScrollPane(inArea),BorderLayout.SOUTH);
		
		tp.addTab("Mensajes",msgPanel);
		
		JPanel logPanel = new JPanel(new GridLayout(2,1));
		logPanel.add(new JScrollPane(logArea));
		logPanel.add(new JScrollPane(errArea));
		
		tp.addTab("Log", logPanel);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(tp,BorderLayout.CENTER);
		
		frame.setVisible(true);
		
		System.setOut(Consola.getLog(logArea,1000));
		System.setErr(Consola.getLog(errArea,1000));
		final PrintStream msg = Consola.getLog(msgArea);
		
		frame.setVisible(true);
	
		int puerto = 1111;
		try{
			ServerSocket conexion = new ServerSocket(puerto);
			puerto = conexion.getLocalPort();
			System.out.println("Servidor esperando conexiones en el puerto :["+puerto+"]");
			Socket cliente = conexion.accept();
			
			System.out.println("Conexion aceptada de:"+cliente.getInetAddress().getHostName()+":["+cliente.getPort()+"]");
			final InputStream inCliente = cliente.getInputStream();
			final PrintStream outCliente = Consola.getLog(msgArea, cliente.getOutputStream()); 
			
			Thread oyente = new Thread(){
				int tbuf = 2048;
				byte[] buffer = new byte[tbuf];
				public void run(){
					while(true){
						try{	
							final int bytes = inCliente.read(buffer);
							if(bytes == -1){
								System.out.println("Cliente ha finalizado sesion");
								break;
							}
							msg.write(buffer,0,bytes);
						}catch(IOException e){
							System.err.println("Error leyendo socket cliente");
							e.printStackTrace();
							break;
						}
					}
				}
			};

			final Emisor emisor = new Emisor(inArea,outCliente);
			
			inArea.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e){
					if (e.getKeyCode() == KeyEvent.VK_ENTER){
						emisor.notificar();
						inArea.setText("");
					}
				}
			});
			
			oyente.start();
			emisor.start();
			try{
				oyente.join();
				emisor.join();
			}catch(InterruptedException ignorada){
			}
			
			outCliente.close();
			inCliente.close();
			cliente.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}