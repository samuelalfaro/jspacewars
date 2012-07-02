/* 
 * ExampleGameMenuJOGL.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Hashtable;
import java.util.Map;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.sam.elementos.Cache;
import org.sam.jogl.fondos.GLEventListenerBackgroundRenderer;
import org.sam.jogl.gui.ButtonAction;
import org.sam.jogl.gui.GLGUI;
import org.sam.jspacewars.cliente.Cliente;
import org.sam.jspacewars.serialization.Loader;
import org.sam.jspacewars.servidor.ServidorJuego;
import org.sam.jspacewars.servidor.elementos.Elemento;

import com.jogamp.opengl.util.Animator;

public class ExampleGameMenuJOGL {
	
	static String getPathElementos(){
		return "resources/elementos-instancias3D-stream-sh.xml";
	}

	static int getPort(){
		return 1111;
	}
	
	static String getHostName(){
		return "localhost";
	}
	
	private static Window getFullScreenFrame(){
		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		Frame frame = new Frame( myDevice.getDefaultConfiguration() );
		frame.setBackground( Color.BLACK );
		frame.setAlwaysOnTop( true );
		frame.setUndecorated( true );
//		frame.setIgnoreRepaint(true);
		frame.setResizable( false );

		if( myDevice.isFullScreenSupported() ){
			myDevice.setFullScreenWindow( frame );
			if( myDevice.isDisplayChangeSupported() ){
				DisplayMode currentDisplayMode = myDevice.getDisplayMode();
				/*
				 currentDisplayMode = new DisplayMode(800, 600,
				 currentDisplayMode.getBitDepth(),
				 currentDisplayMode.getRefreshRate());
				 //*/
				try{
					myDevice.setDisplayMode( currentDisplayMode );
					frame.setSize( currentDisplayMode.getWidth(), currentDisplayMode.getHeight() );
				}catch( IllegalArgumentException e ){
					System.err.println( "Display Mode: not supported!!" );
				}
			}
		}else{
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds( 0, 0, dim.width, dim.height );
		}
		return frame;
	}
	
	private static void mostrar( Window frame, Component component ){
//		if( Container.class.isAssignableFrom(component.getClass()) ){
//			frame.setContentPane((Container) component);
//		}else{
//			if( !(frame.getContentPane().getLayout() instanceof BorderLayout) )
//				frame.getContentPane().setLayout(new BorderLayout());
//			frame.getContentPane().removeAll();
//			frame.getContentPane().add(component, BorderLayout.CENTER);
//		}
		if( !( frame.getLayout() instanceof BorderLayout ) )
			frame.setLayout( new BorderLayout() );
		frame.removeAll();
		frame.add( component, BorderLayout.CENTER );
		frame.validate();
		if( !frame.isVisible() ){
			frame.setVisible( true );
		}
		component.requestFocus();
	}
	
	private static class ClientServer{
		Cliente cliente;
		ServidorJuego server;
		
		ClientServer(){}
	}
	
	public static void main( String[] args ){

		final ClientServer clientServer = new ClientServer();

		final DataGame dataGame = new DataGame();
		GLCanvas splashCanvas = new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );

		SplashWindow splashWindow = new SplashWindow( "splash.jpg", splashCanvas, dataGame );
		splashWindow.setVisible( true );

		final Cache<Elemento> cache = new Cache<Elemento>( 1000 );
		try{
			Loader.loadData( getPathElementos(), cache );
		}catch( FileNotFoundException e1 ){
			e1.printStackTrace();
		}catch( IOException e1 ){
			e1.printStackTrace();
		}

		final Animator animator = new Animator();
		animator.setRunAsFastAsPossible( true );

		splashWindow.waitForLoading();
		splashWindow.setVisible( false );

		final GLCanvas canvas = new GLCanvas( null, null, splashCanvas.getContext(), null );
		canvas.setBackground( Color.BLACK );
		
		final GLEventListener backgroundRenderer = new GLEventListenerBackgroundRenderer( dataGame.getFondo() );
		canvas.addGLEventListener( backgroundRenderer );

		splashWindow = null;
		System.gc();

		final GLGUI displayGUI = new GLGUI();
		Map<String, ButtonAction> actions = new Hashtable<String, ButtonAction>();

		ButtonAction action = new ButtonAction( "player1" ){
			public void run(){
				try{
					animator.stop();
					animator.remove( canvas );
					displayGUI.unbind( canvas );
					canvas.removeGLEventListener( backgroundRenderer );

					clientServer.server = new ServidorJuego( cache );
					clientServer.cliente = new Cliente( dataGame, canvas );
					clientServer.cliente.setChannelIn( clientServer.server.getLocalChannelClientIn() );
					clientServer.cliente.setChannelOut( clientServer.server.getLocalChannelClientOut() );
					clientServer.cliente.start();

					new Thread(){
						public void run(){
							try{
								clientServer.server.atenderClientes();
							}catch( IOException e ){
								e.printStackTrace();
							}
						}
					}.start();

				}catch( IOException exception ){
					exception.printStackTrace();
				}
			}
		};
		actions.put( action.getName(), action );

		action = new ButtonAction( "server" ){
			public void run(){
				try{
					animator.stop();
					animator.remove( canvas );
					displayGUI.unbind( canvas );
					canvas.removeGLEventListener( backgroundRenderer );

					clientServer.server = new ServidorJuego( cache, getPort() );

					clientServer.cliente = new Cliente( dataGame, canvas );
					clientServer.cliente.setChannelIn( clientServer.server.getLocalChannelClientIn() );
					clientServer.cliente.setChannelOut( clientServer.server.getLocalChannelClientOut() );
					clientServer.cliente.start();

					new Thread(){
						public void run(){
							try{
								clientServer.server.atenderClientes();
							}catch( IOException e ){
								e.printStackTrace();
							}
						}
					}.start();

				}catch( IOException exception ){
					exception.printStackTrace();
				}
			}
		};
		actions.put( action.getName(), action );

		action = new ButtonAction( "client" ){
			public void run(){
				try{
					animator.stop();
					animator.remove( canvas );
					displayGUI.unbind( canvas );
					canvas.removeGLEventListener( backgroundRenderer );
					
					DatagramChannel canalCliente = DatagramChannel.open();
					canalCliente.connect( new InetSocketAddress( getHostName(), getPort() ) );
					clientServer.cliente = new Cliente( dataGame, canvas );
					clientServer.cliente.setChannelIn( canalCliente );
					clientServer.cliente.setChannelOut( canalCliente );
					clientServer.cliente.start();
				}catch( IOException exception ){
					exception.printStackTrace();
				}
			}
		};
		actions.put( action.getName(), action );
		displayGUI.setContentPane( new GameMenu( actions ) );
		displayGUI.bind( canvas );

		animator.add( canvas );
		
		mostrar( getFullScreenFrame(), canvas );
		animator.start();
	}
}
