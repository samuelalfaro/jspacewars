/* 
 * PruebaBotones.java
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
import java.awt.Color;
import java.awt.Dimension;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.sam.jogl.gui.GLButton;
import org.sam.jogl.gui.GLContainer;
import org.sam.jogl.gui.GLGUI;
import org.sam.jogl.gui.GLLabel;
import org.sam.jogl.gui.GLTextRenderer.HorizontalAlignment;
import org.sam.jogl.gui.GLTextRenderer.VerticalAlignment;

import com.jogamp.opengl.util.Animator;

public class PruebaBasicaBotones2{
	
	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba botones" );
		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().setBackground( Color.BLACK );

		GLCanvas canvas = new GLCanvas( new GLCapabilities( GLProfile.get( GLProfile.GL2 ) ) );
		
		canvas.addGLEventListener( new pruebas.jogl.Prueba020_FondoCieloEstrellado.Renderer() );
		
		frame.getContentPane().add( canvas, BorderLayout.CENTER );

		frame.getContentPane().setPreferredSize( new Dimension( 640, 480 ) );
		frame.pack();
		frame.setLocationRelativeTo( null ); // center
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
		
		GLContainer container = new GLContainer();
		container.setBounds( 350, 350 );
		
		GLLabel component;
		
		component = new GLButton( "Botón 1" );
		component.setBounds( 50, 50, 200, 50 );
		component.setVerticalAlignment( VerticalAlignment.TOP );
		component.setHorizontalAlignment( HorizontalAlignment.LEFT );
		container.add( component );
		
		component = new GLButton( "Botón 2" );
		component.setBounds( 50, 125, 200, 50 );
		component.setVerticalAlignment( VerticalAlignment.CENTER);
		component.setHorizontalAlignment( HorizontalAlignment.CENTER );
		container.add( component );
		
		component = new GLLabel( "Etiqueta" );
		component.setBounds( 50, 200, 200, 50 );
		component.setVerticalAlignment( VerticalAlignment.BOTTOM );
		container.add( component );
		
		component = new GLButton( "Botón 3" );
		component.setBounds( 50, 275, 200, 50 );
		component.setVerticalAlignment( VerticalAlignment.BASE_LINE );
		component.setHorizontalAlignment( HorizontalAlignment.RIGHT );
		container.add( component );
		
		GLGUI gui = new GLGUI();
		gui.setContentPane( container );
		gui.bind( canvas );
		
		Animator animator = new Animator();
		animator.setRunAsFastAsPossible( true );
		animator.add( canvas );
		animator.start();
	
		canvas.requestFocusInWindow();
	}
}