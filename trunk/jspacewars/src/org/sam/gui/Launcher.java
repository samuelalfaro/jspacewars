/**
 * 
 */
package org.sam.gui;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

import javax.swing.JFrame;

import org.sam.red.cliente.Visor3D;
import org.sam.red.servidor.ServidorJuego;

/**
 * @author samuel
 *
 */
public class Launcher {
	private static void lanzarUnJugador(){
		try{
			ServidorJuego server = new ServidorJuego(true);
			Visor3D visor = new Visor3D( server.getLocalChannelClientIn(), server.getLocalChannelClientOut() );
			
			JFrame frame = new JFrame("jSpaceWars");
			frame.setSize(ServidorJuego.ANCHO, ServidorJuego.ALTO);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setContentPane(visor.getPanel());
			frame.setVisible(true);
			visor.start();
			server.atenderClientes();

		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	private static void lanzarDosJugadoresAnfitrion(){
		try{
			ServidorJuego server = new ServidorJuego(false);
			Visor3D visor = new Visor3D( server.getLocalChannelClientIn(), server.getLocalChannelClientOut() );
			
			JFrame frame = new JFrame("jSpaceWars Anfitrión");
			frame.setSize(ServidorJuego.ANCHO, ServidorJuego.ALTO);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setContentPane(visor.getPanel());
			frame.setVisible(true);
			visor.start();
			server.atenderClientes();

		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	private static void lanzarDosJugadoresInvitado(){
		try{
			DatagramChannel canalCliente = DatagramChannel.open();
			canalCliente.connect(new InetSocketAddress("localhost", ServidorJuego.PORT));

			Visor3D visor = new Visor3D(canalCliente, canalCliente);

			JFrame frame = new JFrame("jSpaceWars Invitado");
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
