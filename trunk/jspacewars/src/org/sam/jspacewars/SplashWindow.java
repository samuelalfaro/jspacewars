package org.sam.jspacewars;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import org.sam.util.Imagen;
import org.sam.util.ModificableBoolean;

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
		barra.addGLEventListener( new Loader(dataGame, loading ) );
		
		this.setLayout( null);
		barra.setBounds( 50, this.getBounds().height-10, this.getBounds().width-100, 5 );
		this.add( barra);
		
		new Thread(){
			public void run(){
				while(loading.isTrue()){
					barra.display();
//					try{
//						Thread.sleep(1);
//					}catch( InterruptedException igonorada){
//					}
				}
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
	 * Método que sobreescribe y llama al método de la clase padre {@code java.awt.Window#setVisible(boolean)}.
     * @param visible
     * 	<ul>
     * 		<li>Si {@code true}, muestra la {@code Window}</li>
     * 		<li>En otro caso oculta la {@code Window}, y en caso de que todavia esten cargandose datos, 
     * 			bloquea a la hebra llamante hasta que dichos datos hallan sido cargados.</li>
     *  </ul>
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	public void setVisible(boolean visible){
		if( !visible && this.isVisible() && loading.isTrue() ){
			synchronized(loading){
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