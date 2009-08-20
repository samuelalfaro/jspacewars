/**
 * 
 */
package org.sam.jspacewars;

import java.awt.*;
import java.io.IOException;

import javax.swing.JFrame;

import org.sam.jspacewars.cliente.Visor3D;
import org.sam.jspacewars.servidor.ServidorJuego;

/**
 * @author samuel
 *
 */
public class Launcher {
	//*
	private static void mostrarFrame(Container contentPane) {

		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame(myDevice.getDefaultConfiguration());
		frame.setUndecorated(true);
		frame.setResizable(false);

		if( myDevice.isFullScreenSupported() ){
			myDevice.setFullScreenWindow(frame);
			if( myDevice.isDisplayChangeSupported() ){
				DisplayMode currentDisplayMode = myDevice.getDisplayMode();
//				currentDisplayMode = new DisplayMode(640, 400, currentDisplayMode.getBitDepth(), currentDisplayMode.getRefreshRate());
				try{
					myDevice.setDisplayMode(currentDisplayMode);
					frame.setSize(currentDisplayMode.getWidth(), currentDisplayMode.getHeight());
				}catch( IllegalArgumentException e ){
					System.err.println("Display Mode: not supported!!");
				}
			}
			frame.setContentPane(contentPane);
			frame.validate();
		}else{
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(0, 0, dim.width, dim.height);
			frame.setContentPane(contentPane);
			frame.setVisible(true);
		}
	}
	/*/
	private static void mostrarFrame(Container contentPane) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setContentPane(contentPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	//*/
	
	private static void lanzarUnJugador() {
		try{
			DataGame dataGame = new DataGame();

			SplashWindow splashFrame = new SplashWindow("splash.png", dataGame);
			splashFrame.setVisible(true);
			
			ServidorJuego server = new ServidorJuego();
			Visor3D visor = new Visor3D( dataGame, server.getLocalChannelClientIn(), server.getLocalChannelClientOut() );

			splashFrame.setVisible(false);
			splashFrame = null;
			System.gc();
			
			visor.start();
			mostrarFrame( visor.getPanel() );
			visor.getPanel().requestFocus();
			server.atenderClientes();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
//	private static void lanzarAnfitrion(int port){
//		try{
//			ServidorJuego server = new ServidorJuego(port);
//			Visor3D visor = new Visor3D( server.getLocalChannelClientIn(), server.getLocalChannelClientOut() );
//			
//			mostrarFrame( visor.getPanel() );
//			visor.start();
//			server.atenderClientes();
//
//		}catch( IOException e ){
//			e.printStackTrace();
//		}
//	}
//	
//	private static void lanzarInvitado(String hostname, int port){
//		try{
//			DatagramChannel canalCliente = DatagramChannel.open();
//			canalCliente.connect(new InetSocketAddress(hostname, port));
//			Visor3D visor = new Visor3D(canalCliente, canalCliente);
//
//			mostrarFrame( visor.getPanel() );
//			visor.start();
//
//		}catch( SocketException e ){
//			e.printStackTrace();
//		}catch( UnknownHostException e ){
//			e.printStackTrace();
//		}catch( IOException e ){
//			e.printStackTrace();
//		}
//	}
	
	public static void main(String... args){
//		if(args.length == 0)
			lanzarUnJugador();
//		else if( args[0].equals("server") && args.length == 2 )
//			lanzarAnfitrion(Integer.parseInt(args[1]));
//		else if( args[0].equals("client") && args.length == 3 )
//			lanzarInvitado(args[1], Integer.parseInt(args[2]));
	}
}
