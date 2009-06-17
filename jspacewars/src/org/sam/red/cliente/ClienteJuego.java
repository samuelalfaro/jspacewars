package org.sam.red.cliente;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

import javax.swing.JFrame;

import org.sam.gui.Visor3D;
import org.sam.red.servidor.ServidorJuego;

public class ClienteJuego {

	public static void main(String args[]) {
		try{
			DatagramChannel canalCliente = DatagramChannel.open();
			canalCliente.connect(new InetSocketAddress("localhost", ServidorJuego.PORT));

			Visor3D visor = new Visor3D(canalCliente, canalCliente);

			JFrame frame = new JFrame("Cliente Juego");
			frame.setSize(ServidorJuego.ANCHO, ServidorJuego.ALTO);
			// frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setContentPane(visor.getPanel());
			frame.setVisible(true);
			visor.start();

		}catch( SocketException e ){
			e.printStackTrace();
		}catch( UnknownHostException e ){
			e.printStackTrace();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}
}