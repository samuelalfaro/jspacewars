/* 
 * SplashWindow.java
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

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLAutoDrawable;

import org.sam.jogl.ConditionalAnimator;
import org.sam.util.Imagen;
import org.sam.util.ModificableBoolean;

/**
 * 
 */
@SuppressWarnings("serial")
class SplashWindow extends Window {

	private final transient Image fondo;
	private final transient ModificableBoolean loading;
	private final transient ConditionalAnimator animator;

	SplashWindow( String ruta, GLAutoDrawable barra, DataGame dataGame ){
		super( null );
		this.setLayout( null );

		Image splashImage = Imagen.cargarImagen( ruta );

		Rectangle maxWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int w = splashImage.getWidth( null );
		int h = splashImage.getHeight( null );
		this.setBounds( new Rectangle( ( maxWindowBounds.width - w ) / 2, ( maxWindowBounds.height - h ) / 2, w, h ) );

		if( ruta.toLowerCase().endsWith( ".jpg" ) )
			/*
			 * Sería más correcto algo parecido a esto:
			 * ((ToolkitImage)splashImage).getColorModel().getTransparency() ==
			 * Transparency.OPAQUE ) Pero al crearse una imagen compatible con
			 * la pantalla, la imagen resultante tiene transparecia, aunque el
			 * fichero no contenga informacion alpha. Por eso simplemente, se
			 * comprueba la extensión para considerarla opaca (jpg) o
			 * translucida (resto: png, gif)
			 */
			fondo = splashImage;
		else{
			BufferedImage captura = null;
			try{
				captura = new Robot().createScreenCapture( this.getBounds() );
				captura.getGraphics().drawImage( splashImage, 0, 0, null );
			}catch( AWTException ignorada ){
			}
			fondo = captura;
		}

		loading = new ModificableBoolean( true );

		barra.addGLEventListener( new GLEventListenerLoader( dataGame, loading ) );
		this.animator = new ConditionalAnimator( barra, loading, 60 );
		Component component = (Component)barra;
		component.setBounds( 50, h - 10, w - 100, 5 );
		
		this.add( component );
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	@Override
	public void paint( Graphics g ){
		g.drawImage( fondo, 0, 0, this );
		super.paint( g );
	}

	/* (non-Javadoc)
	 * @see java.awt.Window#setVisible(boolean)
	 */
	@Override
	public void setVisible( boolean visible ){
		super.setVisible( visible );
		if( visible && loading.isTrue() )
			animator.start();
	}

	public void waitForLoading(){
		if( loading.isTrue() ){
			synchronized( loading ){
				try{
					loading.wait();
				}catch( InterruptedException e ){
					e.printStackTrace();
				}
			}
		}
	}
}
