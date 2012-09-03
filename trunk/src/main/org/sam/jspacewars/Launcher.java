/* 
 * Launcher.java
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
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.sam.elementos.Cache;
import org.sam.jspacewars.cliente.Cliente;
import org.sam.jspacewars.serialization.Loader;
import org.sam.jspacewars.servidor.ServidorJuego;
import org.sam.jspacewars.servidor.elementos.Elemento;

/**
 * Lanzador del cliente, el servidor, juego mediante líenea de comandos.
 */
@Deprecated
public class Launcher{

	private static class Properties{
		
		static final String  elementos;
		
		static final boolean fullScreen;
		static final boolean hideToolBar;
		static final int     width;
		static final int     height;
		
		private static int getProperty( java.util.Properties properties, String key, int defaultValue ){
			try{
				return Integer.parseInt( properties.getProperty( key ) );
			}catch( Exception e ){
				return defaultValue;
			}
		}
		
		private static boolean getProperty( java.util.Properties properties, String key, boolean defaultValue ){
			try{
				String val = properties.getProperty( key );
				if( val != null && val.length() > 0 )
					return val.equalsIgnoreCase( "true" ) || val.equalsIgnoreCase( "yes" );
				return defaultValue;
			}catch( Exception e ){
				return defaultValue;
			}
		}
		
		private static String getProperty( java.util.Properties properties, String key, String defaultValue ){
			try{
				String val = properties.getProperty( key );
				if( val != null && val.length() > 0 )
					return val;
				return defaultValue;
			}catch( Exception e ){
				return defaultValue;
			}
		}
		
		static{
			java.util.Properties properties = new java.util.Properties();
			try{
				//File path = new File( Properties.class.getProtectionDomain().getCodeSource().getLocation().getPath() ).getParentFile();
				properties.load( new FileInputStream( "jspacewars.properties" ) );
			}catch( Exception e ){
				System.err.println( "Defautls" );
				properties = null;
			}

			elementos   = getProperty ( properties, "Elements.path", "resources/elementos-instancias3D-stream-sh.xml" );
			
			fullScreen  = getProperty ( properties, "Graphics.fullScreen", false );
			hideToolBar = getProperty ( properties, "Graphics.hideToolBar", false );
			width       = getProperty ( properties, "Graphics.width", -1 );
			height      = getProperty ( properties, "Graphics.height", -1 );
		}
		
		private Properties(){}
	}
	
	private static Window getFrame(){
		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		Frame frame = new Frame( myDevice.getDefaultConfiguration() );
		frame.setBackground( Color.BLACK );
		
		frame.setIgnoreRepaint(true);
		frame.setResizable( false );

		if( Properties.fullScreen && myDevice.isFullScreenSupported() ){
			frame.setUndecorated( true );
			myDevice.setFullScreenWindow( frame );
			if( myDevice.isDisplayChangeSupported() ){
				DisplayMode currentDisplayMode = myDevice.getDisplayMode();
				if( ( Properties.width > 0 && Properties.height > 0 ) &&
				    ( Properties.width != currentDisplayMode.getWidth() ||
				    Properties.height != currentDisplayMode.getHeight() )
				)
				try{
					myDevice.setDisplayMode(
						new DisplayMode(
								Properties.width,
								Properties.height,
								currentDisplayMode.getBitDepth(),
								currentDisplayMode.getRefreshRate()
						)
					);
				}catch( IllegalArgumentException e ){
					System.err.println( "Display Mode: not supported!!" );
				}
			}
		}
		Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		
		boolean windowed = !Properties.fullScreen && 
				Properties.width > 0 && Properties.width < maxBounds.width  && 
				Properties.height > 0 && Properties.height < maxBounds.height;
				
		System.err.println( windowed );

		frame.setAlwaysOnTop( !windowed && frame.isAlwaysOnTopSupported() );
		if( windowed ){
			frame.setTitle( "jSpaceWars" );
			frame.setBounds(
					maxBounds.x + ( maxBounds.width  - Properties.width ) / 2,
					maxBounds.y + ( maxBounds.height - Properties.height ) / 2,
					Properties.width,
					Properties.height
			);
		}else{
			if( !frame.isDisplayable() )
				frame.setUndecorated( true );
			
			if( Properties.hideToolBar )
				frame.setSize( myDevice.getDisplayMode().getWidth(), myDevice.getDisplayMode().getHeight() );
			else
				frame.setBounds( maxBounds );
		}
		return frame;
	}
	
	private static void mostrar( Component component ){
		
		Window frame = getFrame();
		
		if( JFrame.class.isAssignableFrom( frame.getClass() ) ){
			JFrame jFrame = (JFrame)frame;
			if( Container.class.isAssignableFrom( component.getClass() ) )
				jFrame.setContentPane( (Container)component );
			else{
				if( !( jFrame.getContentPane().getLayout() instanceof BorderLayout ) )
					jFrame.getContentPane().setLayout( new BorderLayout() );
				jFrame.getContentPane().removeAll();
				jFrame.getContentPane().add( component, BorderLayout.CENTER );
			}
		}else{
			if( !( frame.getLayout() instanceof BorderLayout ) )
				frame.setLayout( new BorderLayout() );
			frame.removeAll();
			frame.add( component, BorderLayout.CENTER );
		}
		frame.validate();
		if( !frame.isVisible() ){
			frame.setVisible( true );
		}
		component.requestFocus();
	}

	private static void lanzarUnJugador(){
		try{
			final DataGame dataGame = new DataGame();
			GLCanvas splashCanvas =  new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );

			SplashWindow splashWindow = new SplashWindow( "splash.jpg", splashCanvas, dataGame );
			splashWindow.setVisible( true );

			Cache<Elemento> cache = new Cache<Elemento>( 1000 );
			Loader.loadData( Properties.elementos, cache );

			ServidorJuego server = new ServidorJuego( cache );
			splashWindow.waitForLoading();

			splashWindow.setVisible( false );
			splashWindow = null;
			System.gc();

			final GLCanvas canvas = new GLCanvas( null, null, splashCanvas.getContext(), null );

			Cliente cliente = new Cliente( dataGame, canvas );
			cliente.setChannelIn( server.getLocalChannelClientIn() );
			cliente.setChannelOut( server.getLocalChannelClientOut() );
			mostrar( canvas );

			cliente.start();
			server.atenderClientes();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	private static void lanzarAnfitrion( int port ){
		try{
			final DataGame dataGame = new DataGame();
			GLCanvas splashCanvas =  new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );

			SplashWindow splashWindow = new SplashWindow( "splash.jpg", splashCanvas, dataGame );
			splashWindow.setVisible( true );

			Cache<Elemento> cache = new Cache<Elemento>( 1000 );
			Loader.loadData( Properties.elementos, cache );

			ServidorJuego server = new ServidorJuego( cache, port );
			splashWindow.waitForLoading();

			GLCanvas canvas = new GLCanvas( null, null, splashCanvas.getContext(), null );

			splashWindow.setVisible( false );
			splashWindow = null;
			System.gc();

			Cliente cliente = new Cliente( dataGame, canvas );
			cliente.setChannelIn( server.getLocalChannelClientIn() );
			cliente.setChannelOut( server.getLocalChannelClientOut() );
			mostrar( canvas );

			cliente.start();
			server.atenderClientes();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	private static void lanzarInvitado( String hostname, int port ){
		try{
			final DataGame dataGame = new DataGame();
			GLCanvas splashCanvas =  new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );

			SplashWindow splashWindow = new SplashWindow( "splash.jpg", splashCanvas, dataGame );
			splashWindow.setVisible( true );
			splashWindow.waitForLoading();

			GLCanvas canvas = new GLCanvas( null, null, splashCanvas.getContext(), null );

			splashWindow.setVisible( false );
			splashWindow = null;
			System.gc();

			DatagramChannel canalCliente = DatagramChannel.open();
			canalCliente.connect( new InetSocketAddress( hostname, port ) );

			Cliente cliente = new Cliente( dataGame, canvas );
			cliente.setChannelIn( canalCliente );
			cliente.setChannelOut( canalCliente );

			mostrar( canvas );
			cliente.start();

		}catch( SocketException e ){
			e.printStackTrace();
		}catch( UnknownHostException e ){
			e.printStackTrace();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 *            agumentos que indican el comportamiento de este lanzador:
	 *            <ul>
	 *            <li>Si están vacios, lanza el juego para un jugador individual.</li>
	 *            <li>Si el primer argumento es: <b>server</b>, lanza un servidor que atenderá a los clientes a través
	 *            del puerto indicado como segundo argumento.</li>
	 *            <li>Si el primer argumento es: <b>client</b>, lanza un cliente, que se conecta al servidor indicado en
	 *            el segundo argumento, a través del puerto contenido en el tercer argumento.</li>
	 *            <li>En los demás casos, no hace nada</li>
	 *            </ul>
	 */
	public static void main( String... args ){
		//GLProfile.initSingleton( true );
		if( args.length == 0 )
			lanzarUnJugador();
		else if( args[0].equals( "server" ) && args.length == 2 )
			lanzarAnfitrion( Integer.parseInt( args[1] ) );
		else if( args[0].equals( "client" ) && args.length == 3 )
			lanzarInvitado( args[1], Integer.parseInt( args[2] ) );
	}
}
