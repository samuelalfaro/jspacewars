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
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCanvas;

import org.sam.util.Imagen;
import org.sam.util.ModificableBoolean;

/**
 * 
 * @author Samuel Alfaro
 */
@SuppressWarnings("serial")
class SplashWindow extends Window {

	private final transient Image fondo;
	private final transient ModificableBoolean loading;
	private final transient GLCanvas barra;

	SplashWindow( String ruta, GLCanvas barra, DataGame dataGame ) {
		super(null);
		this.setLayout(null);

		Image splashImage = Imagen.cargarImagen(ruta);

		Rectangle maxWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int w = splashImage.getWidth(null);
		int h = splashImage.getHeight(null);
		this.setBounds(new Rectangle((maxWindowBounds.width - w) / 2, (maxWindowBounds.height - h) / 2, w, h));

		if( ruta.toLowerCase().endsWith(".jpg") )
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
				Robot robot = new Robot();
				captura = robot.createScreenCapture(this.getBounds());
				captura.getGraphics().drawImage(splashImage, 0, 0, null);
			}catch( AWTException ignorada ){
			}
			fondo = captura;
		}

		loading = new ModificableBoolean(true);

		this.barra = barra;
		this.barra.setBounds(50, h - 10, w - 100, 5);
		this.barra.addGLEventListener(new GLEventListenerLoader(dataGame, loading));

		this.add(barra);
	}

	/*
	 * (non-Javadoc)
	 * 
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
	 *            <li>Si {@code true}, muestra la {@link java.awt.Window
	 *            ventana}, si no hay datos cargados, crea una nueva hebra, que
	 *            se encargará, de animar la barra de progreso.</li>
	 *            <li>En caso contrario, si todavía se están cargando datos,
	 *            bloquea la hebra llamante hasta que dichos datos hallan sido
	 *            cargados y finalmente oculta la {@link java.awt.Window
	 *            ventana}.</li>
	 *            </ul>
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {

		if( visible ){
			super.setVisible(true);
			if( loading.isTrue() ){
				new Thread() {
					public void run() {
						long tActual = System.currentTimeMillis(), tAnterior;
						do{
							tAnterior = tActual;
							barra.display();
							tActual = System.currentTimeMillis();
							long tRender = tActual - tAnterior;
							if( tRender < 40 )
								try{
									Thread.sleep(40 - tRender);
								}catch( InterruptedException ignorada ){
								}
						}while( loading.isTrue() );
					}
				}.start();
			}
		}else
			super.setVisible(false);
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
