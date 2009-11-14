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

import java.awt.*;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.sam.jspacewars.cliente.*;
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
				// currentDisplayMode = new DisplayMode(640, 400,
				// currentDisplayMode.getBitDepth(),
				// currentDisplayMode.getRefreshRate());
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
		if( Container.class.isAssignableFrom(component.getClass()) )
			frame.setContentPane((Container) component);
		else{
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
	
	public static void main(String[] args) {

		final DataGame dataGame = new DataGame();

		SplashWindow splashFrame = new SplashWindow("splash.jpg", dataGame);
		splashFrame.setVisible(true);

		final Animator animator = new Animator();
//		animator.setRunAsFastAsPossible(true);
//		animator.setPrintExceptions(true);

		splashFrame.setVisible(false);
		splashFrame = null;
		System.gc();

		final JFrame frame = getFullScreenFrame();
		
		Map<String,IButtonPressedListener> actions = new Hashtable<String,IButtonPressedListener>();
		
		IButtonPressedListener lanzarUnJugador =  new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent event) {
				try{
					
					ServidorJuego server = new ServidorJuego();
					
					ClientData dataCliente = new ClientData();
					PantallaCliente pantalla = PantallaCliente.getNewPantalla(0, dataGame, dataCliente);
					
					animator.stop();
					mostrar(frame, pantalla);
					
					Cliente cliente = new Cliente(
							server.getLocalChannelClientIn(), server.getLocalChannelClientOut(),
							dataGame.getCache(), dataCliente, pantalla
					);
					cliente.start();
					server.atenderClientes();
				}catch( IOException exception ){
					exception.printStackTrace();
				}
			}
		};
		
		actions.put("lanzarUnJugador", lanzarUnJugador);
		
		GLCanvas canvas = new GLCanvas(new GLCapabilities(), null, dataGame.getGLContext(), null);
		canvas.addGLEventListener(new GLEventListenerBackgroundRenderer(dataGame.getFondo()));
		canvas.addGLEventListener(new GLEventListenerDisplayGUI(actions));
//		GameMenu gameMenu = new GameMenu();
//		canvas.addGLEventListener(new GLEventListenerDisplayGUI(gameMenu));
		animator.add(canvas);
		mostrar(frame, canvas);
		animator.start();
	}
}
