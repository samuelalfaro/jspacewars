package org.sam.gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import org.sam.jogl.DataGame;
import org.sam.jogl.Loader;
import org.sam.util.Imagen;
import org.sam.util.ModificableBoolean;

@SuppressWarnings("serial")
class SplashWindow extends Window{
	
	final Image fondo;

	SplashWindow(String ruta, final DataGame dataGame, final ModificableBoolean loading ){
		super(null);
		
		Image splashImage =  Imagen.cargarImagen(ruta) ;
		
		Rectangle maxWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Rectangle bounds = new Rectangle(
				(maxWindowBounds.width - splashImage.getWidth(null))/2,
				(maxWindowBounds.height - splashImage.getHeight(null))/2,
				splashImage.getWidth(null), 
				splashImage.getHeight(null) 
		);
		
//		if(( ruta.toLowerCase().endsWith(".jpg") || ((ToolkitImage)splashImage).getColorModel().getTransparency() == Transparency.OPAQUE ) )
//		Al crearse una imagen compatible con la pantalla, la imagen tendra transparecia, a pesar de que el fichero no contenga informacion alpha
		if( ruta.toLowerCase().endsWith(".jpg") )
			fondo =  splashImage;
		else{
			BufferedImage aux = null;
			try{
				Robot robot = new Robot();
				aux = robot.createScreenCapture( bounds );
				aux.getGraphics().drawImage(splashImage, 0, 0, this);
			}catch( AWTException ignorada ){
			}
			fondo = aux;
		}
		
		final GLCanvas barra = new GLCanvas(new GLCapabilities ());
		barra.setBounds( 50, bounds.height-10, bounds.width-100, 5 );
		barra.addGLEventListener( new Loader(dataGame, loading ) );
		
		this.setLayout( null);
		this.add( barra);
		
		new Thread(){
			@Override
			public void run(){
				while(loading.isTrue()){
					barra.display();
					try{
						Thread.sleep(1);
					}catch( InterruptedException igonorada){
					}
				}
			}
		}.start();
		this.setBackground( new Color(128, 128, 128, 0) );
		this.setBounds(bounds);
	}
	
	public void paint(Graphics g) {
//		if( this.isVisible() && !this.getBounds().equals(bounds))
//			this.setBounds(bounds);
		g.drawImage(fondo, 0, 0, this);
	    super.paint(g);
	}
}