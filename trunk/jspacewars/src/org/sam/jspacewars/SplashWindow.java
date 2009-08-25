/* 
 * SplashWindow.java
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
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import org.sam.util.Imagen;
import org.sam.util.ModificableBoolean;

/**
 *
 * @author Samuel Alfaro
 */
@SuppressWarnings("serial")
class SplashWindow extends Window{
	
	private final transient Image fondo;
	private final transient ModificableBoolean loading;

	SplashWindow(String ruta, final DataGame dataGame ){
		super(null);
		
		this.setBackground( new Color(128, 128, 128, 0) );
		Image splashImage =  Imagen.cargarImagen(ruta) ;
		
		Rectangle maxWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		this.setBounds( new Rectangle(
				(maxWindowBounds.width - splashImage.getWidth(null))/2,
				(maxWindowBounds.height - splashImage.getHeight(null))/2,
				splashImage.getWidth(null), 
				splashImage.getHeight(null) 
		) );
		
//		if(( ruta.toLowerCase().endsWith(".jpg") || ((ToolkitImage)splashImage).getColorModel().getTransparency() == Transparency.OPAQUE ) )
//		Al crearse una imagen compatible con la pantalla, la imagen resultante tendra transparecia,
//		aunque el fichero no contenga informacion alpha. Por eso simplemente, nos basamos en la extension
//		para considerarla opaca (jpg) o translucida (resto: png, gif)
		if( ruta.toLowerCase().endsWith(".jpg") )
			fondo =  splashImage;
		else{
			BufferedImage aux = null;
			try{
				Robot robot = new Robot();
				aux = robot.createScreenCapture( this.getBounds() );
				aux.getGraphics().drawImage(splashImage, 0, 0, this);
			}catch( AWTException ignorada ){
			}
			fondo = aux;
		}
		
		final GLCanvas barra = new GLCanvas(new GLCapabilities ());
		
		loading = new ModificableBoolean(true);
		barra.addGLEventListener( new GLEventListenerLoader(dataGame, loading ) );
		
		this.setLayout( null);
		barra.setBounds( 50, this.getBounds().height-10, this.getBounds().width-100, 5 );
		this.add( barra);
		
		new Thread(){
			public void run(){
				while(loading.isTrue())
					barra.display();
			}
		}.start();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		g.drawImage(fondo, 0, 0, this);
	    super.paint(g);
	}
	
	/**
	 * Método que sobreescribe y llama al método de la clase padre
	 * {@link java.awt.Window#setVisible(boolean) setVisible(boolean)}.
	 * 
	 * @param visible
	 *            <ul>
	 *            <li>Si {@code true}, muestra la {@link java.awt.Window Window}
	 *            </li>
	 *            <li>En caso contrario oculta la {@link java.awt.Window Window}
	 *            , y si todavía se están cargando datos, bloquea a la hebra
	 *            llamante hasta que dichos datos hallan sido cargados.</li>
	 *            </ul>
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if( !visible && this.isVisible() && loading.isTrue() ){
			synchronized( loading ){
				try{
					loading.wait();
				}catch( InterruptedException e ){
					e.printStackTrace();
				}
			}
		}
		super.setVisible(visible);
	}
}