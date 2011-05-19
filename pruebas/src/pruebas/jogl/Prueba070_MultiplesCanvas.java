/* 
 * Prueba070_MultiplesCanvas.java
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
package pruebas.jogl;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.util.ModificableBoolean;

import com.jogamp.opengl.util.Animator;

public class Prueba070_MultiplesCanvas{

	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba Multiple Canvas" );
		frame.setSize( 500, 500 );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		GLCanvas canvasLoader = new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );
		Loader.Data data = new Loader.Data();
		ModificableBoolean loading = new ModificableBoolean( true );
		canvasLoader.addGLEventListener( new Loader( data, loading ) );

		frame.getContentPane().setLayout( null );
		frame.getContentPane().add( canvasLoader );

		Animator animator = new Animator();
		animator.setRunAsFastAsPossible( true );
		animator.setPrintExceptions( true );

		animator.add( canvasLoader );

		frame.setVisible( true );
		canvasLoader.setBounds( ( frame.getContentPane().getWidth() - 400 ) / 2,
				frame.getContentPane().getHeight() - 40, 400, 20 );

		animator.start();

		if( loading.isTrue() ){
			synchronized( loading ){
				try{
					loading.wait();
				}catch( InterruptedException e ){
					e.printStackTrace();
				}
			}
		}

		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 0, 2, 5, 5 ) );

		GLCanvas[] canvas = new GLCanvas[4];

		for( int i = 0; i < 4; i++ ){
			canvas[i] = new GLCanvas( null, null, data.context, null );

			OrbitBehavior orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos( 0.0f, 0.0f, 4.0f );
			orbitBehavior.addMouseListeners( canvas[i] );

			Prueba060_Loader.Renderer renderer = new Prueba060_Loader.Renderer( data, null, orbitBehavior );
			canvas[i].addGLEventListener( renderer );
			canvas[i].addKeyListener( renderer );

			panel.add( canvas[i] );
		}

		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().add( panel, BorderLayout.CENTER );
		frame.validate();

		animator.remove( canvasLoader );
		// Se muestra por lo menos una vez el canvas antes de quitar el canvasLoader para que no
		// se liberen las texturas de memoria, al eliminarlo.
		for( GLCanvas c: canvas )
			c.display();
		frame.getContentPane().remove( canvasLoader );
		// Se añade despues el canvas al animator, para evitar dos llamadas simultaneas al
		// metodo display()
		for( GLCanvas c: canvas )
			animator.add( c );
		canvas[0].requestFocusInWindow();
	}
}
