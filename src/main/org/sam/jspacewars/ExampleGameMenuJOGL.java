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
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesChooser;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLRunnable;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

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
	
	private static class Properties{
		
		static final String  elementos;
		
		static final boolean fullScreen;
		static final boolean hideToolBar;
		static final int     width;
		static final int     height;
		
		static final int     port;
		static final String  hostName;
		
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
				
			port        = getProperty ( properties, "Network.port", 1111 );
			hostName    = getProperty ( properties, "Network.hostName", "localhost" );
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
				
		frame.setAlwaysOnTop( !windowed && frame.isAlwaysOnTopSupported() );
		frame.setIconImage( new ImageIcon("splash2.png").getImage() );
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
	
	private static class ClientServer{
		Cliente cliente;
		ServidorJuego server;
		
		ClientServer(){}
	}
	
	private static class DebugCanvas extends GLCanvas{
		
		private static class MultiGLEventListener implements GLEventListener{
			
			private final List<GLEventListener> listeners;
			private final Object lock;
			private boolean busy, waiting;
			
			
			MultiGLEventListener(){
				listeners = new ArrayList<GLEventListener>();
				lock = new Object();
				busy = false;
				waiting = false;
			}

			/* (non-Javadoc)
			 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
			 */
			@Override
			public void init( GLAutoDrawable drawable ){
				busy = true;
				for( GLEventListener listener: listeners )
					listener.init( drawable );
				if( waiting ){
					System.err.println("Notificando ... ");
					synchronized( lock ){
						lock.notifyAll();
					}
				}
				busy = false;
			}
			
			/* (non-Javadoc)
			 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
			 */
			@Override
			public void display( GLAutoDrawable drawable ){
				busy = true;
				for( GLEventListener listener: listeners )
					listener.display( drawable );
				drawable.getGL().glFlush();
				if( waiting ){
					System.err.println("Notificando ... ");
					synchronized( lock ){
						lock.notifyAll();
					}
				}
				busy = false;
			}
			
			/* (non-Javadoc)
			 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
			 */
			@Override
			public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ){
				busy = true;
				for( GLEventListener listener: listeners )
					listener.reshape( drawable, x, y, w, h );
				if( waiting ){
					System.err.println("Notificando ... ");
					synchronized( lock ){
						lock.notifyAll();
					}
				}
				busy = false;
			}

			/* (non-Javadoc)
			 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
			 */
			@Override
			public void dispose( GLAutoDrawable drawable ){
				busy = true;
				for( GLEventListener listener: listeners )
					listener.dispose( drawable );
				if( waiting ){
					System.err.println("Notificando ... ");
					synchronized( lock ){
						lock.notifyAll();
					}
				}
				busy = false;
			}
			
			public void add( GLEventListener listener ){
				System.err.println( "addGLEventListener: " + listener.getClass().getName() );
				if( busy ){
					System.err.println("Esperando ... ");
					synchronized( lock ){
						waiting = true;
						try{
							lock.wait();
						}catch( InterruptedException ignorada ){
						}
						waiting = false;
					}
				}
				listeners.add( listener );
			}

			public void remove( GLEventListener listener ){
				System.err.println( "removeGLEventListener: " + listener.getClass().getName() );
				if( busy ){
					System.err.println("Esperando ... ");
					synchronized( lock ){
						waiting = true;
						try{
							lock.wait();
						}catch( InterruptedException ignorada ){
						}
						waiting = false;
					}
				}
				listeners.remove( listener );
			}
		}
		
		private final MultiGLEventListener multilistener;
		
		DebugCanvas(){
			super();
			multilistener = new MultiGLEventListener();
			super.addGLEventListener( multilistener );
		}

		DebugCanvas( GLCapabilities capabilities ){
			super( capabilities );
			multilistener = new MultiGLEventListener();
			super.addGLEventListener( multilistener );
		}

		DebugCanvas( GLCapabilities capabilities, GLCapabilitiesChooser chooser, GLContext shareWith,
				GraphicsDevice device ){
			super( capabilities, chooser, shareWith, device );
			System.out.println("AutoSwapBufferMode: " + this.getAutoSwapBufferMode());
		//	this.isRealized()
			
			multilistener = new MultiGLEventListener();
			super.addGLEventListener( multilistener );
		}

		/*
		 * (non-Javadoc)
		 * @see javax.media.opengl.awt.GLCanvas#addGLEventListener(javax.media.opengl.GLEventListener)
		 */
		@Override
		public void addGLEventListener( GLEventListener listener ){
			multilistener.add( listener );
		}

		/*
		 * (non-Javadoc)
		 * @see javax.media.opengl.awt.GLCanvas#removeGLEventListener(javax.media.opengl.GLEventListener)
		 */
		@Override
		public void removeGLEventListener( GLEventListener listener ){
			multilistener.remove( listener );
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.awt.GLCanvas#setAnimator(javax.media.opengl.GLAnimatorControl)
		 */
		@Override
		public void setAnimator(GLAnimatorControl animatorControl){
			System.err.println( "setAnimator: " + animatorControl );
			super.setAnimator( animatorControl );
		}
		
		/*
		 * (non-Javadoc)
		 * @see javax.media.opengl.awt.GLCanvas#display()
		 */
		@Override
		public void display(){
//			//System.err.println( "Display canvas" );
			super.display();
			//multilistener.display( this );
		}
	}
	
	public static void main( String[] args ){
		
//		Toolkit.getDefaultToolkit().setDynamicLayout(true);
//		System.setProperty("sun.awt.noerasebackground", "true");

		final ClientServer clientServer = new ClientServer();

		final DataGame dataGame = new DataGame();
		GLCanvas splashCanvas = new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );

		SplashWindow splashWindow = new SplashWindow( "splash.jpg", splashCanvas, dataGame );
		splashWindow.setVisible( true );

		final Cache<Elemento> cache = new Cache<Elemento>( 1000 );
		try{
			Loader.loadData( Properties.elementos, cache );
		}catch( FileNotFoundException e1 ){
			e1.printStackTrace();
		}catch( IOException e1 ){
			e1.printStackTrace();
		}

		splashWindow.waitForLoading();
		
		GLCapabilities caps = new GLCapabilities( GLProfile.get( GLProfile.GL2 ) );
		caps.setDoubleBuffered( true );
		
		//final GLCanvas canvas = new DebugCanvas( caps, null, splashCanvas.getContext(), null );
		final GLCanvas canvas = new GLCanvas( caps, null, splashCanvas.getContext(), null );
		canvas.setBackground( Color.BLACK );
		
		Animator animator = new Animator();
		animator.setRunAsFastAsPossible( true );
		animator.add( canvas );
		
		final GLEventListener backgroundRenderer = new GLEventListenerBackgroundRenderer( dataGame.getFondo() );
		canvas.addGLEventListener( backgroundRenderer );

		final GLGUI displayGUI = new GLGUI();
		Map<String, Runnable> actions = new Hashtable<String, Runnable>();

		ButtonAction action = new ButtonAction( "player1" ){
			public void run(){
//				try{
//					
//					animator.remove( canvas );
//					animator.stop();
//					
//					displayGUI.unbind( canvas );
//					canvas.removeGLEventListener( backgroundRenderer );
//
//					clientServer.server = new ServidorJuego( cache );
//					clientServer.cliente = new Cliente( dataGame, canvas );
//					clientServer.cliente.setChannelIn( clientServer.server.getLocalChannelClientIn() );
//					clientServer.cliente.setChannelOut( clientServer.server.getLocalChannelClientOut() );
//					
//					new Thread(){
//						public void run(){
//							try{
//								clientServer.server.atenderClientes();
//							}catch( IOException e ){
//								e.printStackTrace();
//							}
//						}
//					}.start();
//					clientServer.cliente.start();
//
//				}catch( IOException exception ){
//					exception.printStackTrace();
//				}
				
				canvas.invoke( false, new GLRunnable(){ 
					@Override
					public boolean run( GLAutoDrawable autoDrawable ){
						System.err.println( "1 Player button pressed" );
						try{
							GLAnimatorControl animator = autoDrawable.getAnimator();
							if( animator != null ){
								if( animator.isStarted()  )
									animator.stop();
								animator.remove( autoDrawable );
							}
							displayGUI.unbind( autoDrawable );
							canvas.removeGLEventListener( backgroundRenderer );
							
							clientServer.server = new ServidorJuego( cache );
							clientServer.cliente = new Cliente( dataGame, (GLCanvas)autoDrawable );
							clientServer.cliente.setChannelIn( clientServer.server.getLocalChannelClientIn() );
							clientServer.cliente.setChannelOut( clientServer.server.getLocalChannelClientOut() );

							new Thread(){
								public void run(){
									try{
										clientServer.server.atenderClientes();
									}catch( IOException e ){
										e.printStackTrace();
									}
								}
							}.start();
							clientServer.cliente.start();

						}catch( IOException exception ){
							exception.printStackTrace();
						}
						return true;
					}
				});
			}
		};
		actions.put( action.getName(), action );

		action = new ButtonAction( true, "server" ){
			public void run(){
				try{
					GLAnimatorControl animator = canvas.getAnimator();
					if( animator != null ){
						if( animator.isStarted()  )
							animator.stop();
						animator.remove( canvas );
					}
					
					displayGUI.unbind( canvas );
					canvas.removeGLEventListener( backgroundRenderer );
					
					clientServer.server = new ServidorJuego( cache, Properties.port );

					clientServer.cliente = new Cliente( dataGame, canvas );
					clientServer.cliente.setChannelIn( clientServer.server.getLocalChannelClientIn() );
					clientServer.cliente.setChannelOut( clientServer.server.getLocalChannelClientOut() );

					new Thread(){
						public void run(){
							try{
								clientServer.server.atenderClientes();
							}catch( IOException e ){
								e.printStackTrace();
							}
						}
					}.start();
					clientServer.cliente.start();
					

				}catch( IOException exception ){
					exception.printStackTrace();
				}
			}
		};
		actions.put( action.getName(), action );

		action = new ButtonAction( true, "client" ){
			public void run(){
				try{
					
					GLAnimatorControl animator = canvas.getAnimator();
					if( animator != null ){
						if( animator.isStarted()  )
							animator.stop();
						animator.remove( canvas );
					}
					
					displayGUI.unbind( canvas );
					canvas.removeGLEventListener( backgroundRenderer );
					
					DatagramChannel canalCliente = DatagramChannel.open();
					canalCliente.connect( new InetSocketAddress( Properties.hostName, Properties.port ) );
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

		mostrar( canvas );
		
		splashWindow.setVisible( false );
		splashWindow = null;
		System.gc();
		
		canvas.getAnimator().start();
	}
}
