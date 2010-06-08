/* 
 * ExampleGameMenuJOGL.java
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.sam.jspacewars.cliente.Cliente;
import org.sam.jspacewars.servidor.ServidorJuego;

import com.sun.opengl.util.Animator;

public class ExampleGameMenuJOGL {

	private static JFrame getFullScreenFrame(){
		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame(myDevice.getDefaultConfiguration());
		frame.setUndecorated(true);
		frame.setResizable(false);

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
	
	private static class ClientServer{
		Cliente cliente;
		ServidorJuego server;
	}
	
	
	public static void main(String[] args) {

		final ClientServer clientServer= new ClientServer();
		
		final DataGame dataGame = new DataGame();
		GLCanvas splashCanvas = new GLCanvas(new GLCapabilities());

		SplashWindow splashWindow = new SplashWindow("splash.jpg", splashCanvas, dataGame);
		splashWindow.setVisible(true);

		final Animator animator = new Animator();
		animator.setRunAsFastAsPossible(true);
		animator.setPrintExceptions(true);
		splashWindow.waitForLoading();
		splashWindow.setVisible(false);

//		canvas.removeAllGLEventListeners();
		final GLCanvas canvas = new GLCanvas(null, null, splashCanvas.getContext(), null);
		
		final GLEventListener backgroundRenderer = new GLEventListenerBackgroundRenderer(dataGame.getFondo());
		Map<String,IButtonPressedListener> actions = new Hashtable<String,IButtonPressedListener>();
		final GLEventListener displayGUI = new GLEventListenerDisplayGUI(actions);
		
		canvas.addGLEventListener(backgroundRenderer);
		canvas.addGLEventListener(displayGUI);
		
		splashWindow = null;
		System.gc();

		final JFrame frame = getFullScreenFrame();
		
		IButtonPressedListener lanzarUnJugador =  new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent event) {
				try{
					animator.stop();
					animator.remove(canvas);
					canvas.removeGLEventListener(backgroundRenderer);
					canvas.removeGLEventListener(displayGUI);
					
					clientServer.server = new ServidorJuego();
					
					clientServer.cliente = new Cliente(
							clientServer.server.getLocalChannelClientIn(), clientServer.server.getLocalChannelClientOut(),
							dataGame, canvas
					);
					canvas.addGLEventListener(displayGUI);
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
		
		actions.put("lanzarUnJugador", lanzarUnJugador);
		
		animator.add(canvas);
		mostrar(frame, canvas);
		animator.start();
	}
}
