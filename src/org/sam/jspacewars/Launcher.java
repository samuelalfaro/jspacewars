/* 
 * Launcher.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

import javax.swing.JFrame;

import org.sam.jspacewars.cliente.*;
import org.sam.jspacewars.servidor.ServidorJuego;

/**
 * 
 * @author Samuel Alfaro
 */
public class Launcher {
	//*
	private static void mostrar(Component component) {

		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame(myDevice.getDefaultConfiguration());
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setBackground(Color.BLACK);

		if( Container.class.isAssignableFrom(component.getClass()) )
			frame.setContentPane((Container) component);
		else{
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(component, BorderLayout.CENTER);
		}

		if( myDevice.isFullScreenSupported() ){
			myDevice.setFullScreenWindow(frame);
			if( myDevice.isDisplayChangeSupported() ){
				DisplayMode currentDisplayMode = myDevice.getDisplayMode();
					currentDisplayMode = new DisplayMode(640, 400,
				 	currentDisplayMode.getBitDepth(),
//					DisplayMode.BIT_DEPTH_MULTI,
					currentDisplayMode.getRefreshRate()
//					60
					);
				try{
					myDevice.setDisplayMode(currentDisplayMode);
					frame.setSize(currentDisplayMode.getWidth(), currentDisplayMode.getHeight());
				}catch( IllegalArgumentException e ){
					System.err.println("Display Mode: not supported!!");
				}
			}
			frame.validate();
		}else{
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(0, 0, dim.width, dim.height);
			frame.setVisible(true);
		}
		component.requestFocus();
	}
	
	/*/
	private static void mostrar(Component component) {
		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		if( Container.class.isAssignableFrom(component.getClass()) )
			frame.setContentPane((Container) component);
		else{
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(component, BorderLayout.CENTER);
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		component.requestFocus();
	} 
	//*/

	private static void lanzarUnJugador() {
		try{
			DataGame dataGame = new DataGame();

			SplashWindow splashFrame = new SplashWindow("splash.jpg", dataGame);
			splashFrame.setVisible(true);

			ServidorJuego server = new ServidorJuego();
			ClientData dataCliente = new ClientData();

			splashFrame.setVisible(false);
			splashFrame = null;
			System.gc();

			PantallaCliente pantalla = PantallaCliente.getNewPantalla(0, dataGame, dataCliente);
			mostrar(pantalla);

			Cliente cliente = new Cliente(server.getLocalChannelClientIn(), server.getLocalChannelClientOut(), dataGame
					.getCache(), dataCliente, pantalla);
			cliente.start();
			server.atenderClientes();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	private static void lanzarAnfitrion(int port) {
		try{
			DataGame dataGame = new DataGame();

			SplashWindow splashFrame = new SplashWindow("splash.jpg", dataGame);
			splashFrame.setVisible(true);

			ServidorJuego server = new ServidorJuego(port);
			ClientData dataCliente = new ClientData();

			splashFrame.setVisible(false);
			splashFrame = null;
			System.gc();

			PantallaCliente pantalla = PantallaCliente.getNewPantalla(0, dataGame, dataCliente);
			mostrar(pantalla);

			Cliente cliente = new Cliente(server.getLocalChannelClientIn(), server.getLocalChannelClientOut(), dataGame
					.getCache(), dataCliente, pantalla);
			cliente.start();
			server.atenderClientes();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	private static void lanzarInvitado(String hostname, int port) {
		try{
			DataGame dataGame = new DataGame();

			SplashWindow splashFrame = new SplashWindow("splash.jpg", dataGame);
			splashFrame.setVisible(true);

			DatagramChannel canalCliente = DatagramChannel.open();
			canalCliente.connect(new InetSocketAddress(hostname, port));
			ClientData clientData = new ClientData();

			splashFrame.setVisible(false);
			splashFrame = null;
			System.gc();

			PantallaCliente pantalla = PantallaCliente.getNewPantalla(0, dataGame, clientData);
			mostrar(pantalla);

			Cliente cliente = new Cliente(canalCliente, canalCliente, dataGame.getCache(), clientData, pantalla);
			cliente.run();

		}catch( SocketException e ){
			e.printStackTrace();
		}catch( UnknownHostException e ){
			e.printStackTrace();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	public static void main(String... args) {
		if( args.length == 0 )
			lanzarUnJugador();
		else if( args[0].equals("server") && args.length == 2 )
			lanzarAnfitrion(Integer.parseInt(args[1]));
		else if( args[0].equals("client") && args.length == 3 )
			lanzarInvitado(args[1], Integer.parseInt(args[2]));
	}
}
