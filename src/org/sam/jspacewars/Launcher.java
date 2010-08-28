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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

import org.sam.elementos.Cache;
import org.sam.jspacewars.cliente.Cliente;
import org.sam.jspacewars.serialization.Loader;
import org.sam.jspacewars.servidor.ServidorJuego;
import org.sam.jspacewars.servidor.elementos.Elemento;

/**
 * 
 * @author Samuel Alfaro
 */
public class Launcher {

	private static JFrame getFullScreenFrame(){
		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame(myDevice.getDefaultConfiguration());
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setBackground(Color.BLACK);

		if( myDevice.isFullScreenSupported() ){
			myDevice.setFullScreenWindow(frame);
			if( myDevice.isDisplayChangeSupported() ){
				DisplayMode currentDisplayMode = myDevice.getDisplayMode();
				/*
				 currentDisplayMode = new DisplayMode(640, 400,
				 currentDisplayMode.getBitDepth(),
				 currentDisplayMode.getRefreshRate());
				 */
				try{
					myDevice.setDisplayMode(currentDisplayMode);
					frame.setSize(currentDisplayMode.getWidth(), currentDisplayMode.getHeight());
				}catch( IllegalArgumentException e ){
					System.err.println("Display Mode: not supported!!");
				}
			}
		}else{
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(0, 0, dim.width, dim.height);
		}
		return frame;
	}
	
	private static void mostrar(JFrame frame, Component component) {
		if( Container.class.isAssignableFrom(component.getClass()) ){
			System.out.println("Usando componente como ContentPane");
			frame.setContentPane((Container) component);
		}else{
			System.out.println("Añadiendo content pane");
			if( !(frame.getContentPane().getLayout() instanceof BorderLayout) )
				frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().removeAll();
			frame.getContentPane().add(component, BorderLayout.CENTER);
		}
		frame.validate();
		if(!frame.isVisible()){
			System.out.println("eoo");
			frame.setVisible(true);
		}
		component.requestFocus();
	}

	private static void lanzarUnJugador() {
		try{
			final DataGame dataGame = new DataGame();
			GLCanvas splashCanvas = new GLCanvas(new GLCapabilities());

			SplashWindow splashWindow = new SplashWindow("splash.jpg", splashCanvas, dataGame);
			splashWindow.setVisible(true);
			
			Cache<Elemento> cache = new Cache<Elemento>(1000);
			Loader.loadData(cache);
			
			ServidorJuego server = new ServidorJuego(cache);
			splashWindow.waitForLoading();
			
			splashWindow.setVisible(false);
			splashWindow = null;
			System.gc();
			
			final JFrame frame = getFullScreenFrame();
			final GLCanvas canvas = new GLCanvas(null, null, splashCanvas.getContext(), null);
			
			Cliente cliente = new Cliente(server.getLocalChannelClientIn(), server.getLocalChannelClientOut(), dataGame, canvas);
			mostrar(frame, canvas);
			
			cliente.start();
			server.atenderClientes();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	private static void lanzarAnfitrion(int port) {
		try{
			final DataGame dataGame = new DataGame();
			GLCanvas splashCanvas = new GLCanvas(new GLCapabilities());

			SplashWindow splashWindow = new SplashWindow("splash.jpg", splashCanvas, dataGame);
			splashWindow.setVisible(true);

			Cache<Elemento> cache = new Cache<Elemento>(1000);
			Loader.loadData(cache);
			
			ServidorJuego server = new ServidorJuego( cache, port );
			splashWindow.waitForLoading();
			
			GLCanvas canvas = new GLCanvas(null, null, splashCanvas.getContext(), null);
			
			splashWindow.setVisible(false);
			splashWindow = null;
			System.gc();

			Cliente cliente = new Cliente(server.getLocalChannelClientIn(), server.getLocalChannelClientOut(), dataGame, canvas);
			mostrar(getFullScreenFrame(), canvas);
			cliente.start();
			server.atenderClientes();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	private static void lanzarInvitado(String hostname, int port) {
		try{
			final DataGame dataGame = new DataGame();
			GLCanvas splashCanvas = new GLCanvas(new GLCapabilities());

			SplashWindow splashWindow = new SplashWindow("splash.jpg", splashCanvas, dataGame);
			splashWindow.setVisible(true);
			splashWindow.waitForLoading();
			
			GLCanvas canvas = new GLCanvas(null, null, splashCanvas.getContext(), null);
			
			splashWindow.setVisible(false);
			splashWindow = null;
			System.gc();
			
			DatagramChannel canalCliente = DatagramChannel.open();
			canalCliente.connect(new InetSocketAddress(hostname, port));

			Cliente cliente = new Cliente(canalCliente, canalCliente, dataGame, canvas);
			mostrar(getFullScreenFrame(), canvas);
			cliente.start();
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
