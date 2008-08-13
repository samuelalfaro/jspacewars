package org.sam.red.cliente;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;

import javax.swing.JFrame;

import org.sam.gui.Marco;
import org.sam.red.servidor.ServidorJuego;
import org.sam.red.servidor3d.ServidorJuego3D;

public class ClienteJuego{
	
	static public void main(String args[]){
		VisorCliente3D cliente = null;
		try{
			DatagramChannel canalCliente = DatagramChannel.open();
			canalCliente.connect(new InetSocketAddress("sobremesa",ServidorJuego.PORT));
			cliente = new VisorCliente3D(canalCliente,canalCliente);
		}catch (SocketException e) {
			e.printStackTrace();
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JFrame frame = new JFrame("Cliente Juego");
		frame.setSize(ServidorJuego3D.ANCHO,ServidorJuego3D.ALTO);
		//frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setContentPane(Marco.getPanel());
		frame.getContentPane().add(cliente.getPantalla(),BorderLayout.CENTER);
		frame.setVisible(true);
		cliente.start();
	}
}