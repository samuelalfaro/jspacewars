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
	
	public static final int ANCHO = 640;	// TODO Borrar es por comodidad
	public static final int ALTO = 480;		// TODO Borrar es por comodidad
	
	private static void lanzarUnJugador(){
		try{
			ServidorJuego server = new ServidorJuego();
			Visor3D visor = new Visor3D( server.getLocalChannelClientIn(), server.getLocalChannelClientOut() );
			
			JFrame frame = new JFrame("jSpaceWars");
			frame.setSize(ANCHO, ALTO);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setContentPane(visor.getPanel());
			frame.setVisible(true);
			visor.start();
			server.atenderClientes();

		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	private static void lanzarAnfitrion(int port){
		try{
			ServidorJuego server = new ServidorJuego(port);
			Visor3D visor = new Visor3D( server.getLocalChannelClientIn(), server.getLocalChannelClientOut() );
			
			JFrame frame = new JFrame("jSpaceWars Anfitrión");
			frame.setSize(ANCHO, ALTO);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setContentPane(visor.getPanel());
			frame.setVisible(true);
			visor.start();
			server.atenderClientes();

		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	private static void lanzarInvitado(String hostname, int port){
		try{
			DatagramChannel canalCliente = DatagramChannel.open();
			canalCliente.connect(new InetSocketAddress(hostname, port));

			Visor3D visor = new Visor3D(canalCliente, canalCliente);

			JFrame frame = new JFrame("jSpaceWars Invitado");
			frame.setSize(ANCHO, ALTO);
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
	
	public static void main(String... args){
		if(args.length == 0)
			lanzarUnJugador();
		else if( args[0].equals("server") && args.length == 2 )
			lanzarAnfitrion(Integer.parseInt(args[1]));
		else if( args[0].equals("client") && args.length == 3 )
			lanzarInvitado(args[1], Integer.parseInt(args[2]));
	}
}
