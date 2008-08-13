package org.sam.pruebas.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

import org.sam.gui.Marco;
import org.sam.j3d.Escena;
import org.sam.j3d.particulas.FactoriaDeParticulas;
import org.sam.util.Sounds;

import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PruebaJuego{
	
	static public void main(String args[]){
		boolean use_shaders = true, full_screen = false;
	    int ancho=800, alto=600, bitsColor=32;
	    try {
	    	Properties properties = new Properties();
	        properties.load(new FileInputStream("_juego.properties"));
		    use_shaders = getBoolProperty(properties,"USE_SHADERS");
		   	full_screen = getBoolProperty(properties,"FULL_SCREEN");
		   	if(full_screen){
		   		ancho       = getIntProperty(properties,"ANCHO");
		   		alto        = getIntProperty(properties,"ALTO");
		   		bitsColor   = getIntProperty(properties,"PROFUNDIDAD_DE_COLOR");
		   	}
	    } catch (IOException ioE) {
	    	System.err.println("Error procesando el fichero: \"juego.properties\"");
	    }
	    
	    FactoriaDeParticulas.setShadersActivos(use_shaders);
	    
		JFrame frame = new JFrame(SimpleUniverse.getPreferredConfiguration());
		
		frame.setContentPane(Marco.getPanel());
		frame.getContentPane().add(Escena.getCanvas3D(),BorderLayout.CENTER);

		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		full_screen = full_screen && myDevice.isFullScreenSupported();
		frame.setUndecorated(true);
		frame.setResizable(false);
		
		if (full_screen) {
			System.out.println("Full-Screen real");
			myDevice.setFullScreenWindow(frame);
			if(myDevice.isDisplayChangeSupported()){
				DisplayMode displayMode = getDisplayMode(myDevice,ancho, alto, bitsColor); 
				myDevice.setDisplayMode(displayMode);
				frame.setSize(displayMode.getWidth(),displayMode.getHeight());
			}
			frame.validate();
		} else {
			System.out.println("Full-Screen emulado");
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(0,0,dim.width,dim.height);
			frame.setVisible(true);
		}
		Sounds.playTracker("side effects.mod");
//		Sounds.playMidi("resources/sounds/midis/AC-DC_-_Thunderstruck.mid");
	}
	
	public static boolean getBoolProperty(Properties properties, String k){
	    boolean res = false;
		try{
	    	res = properties.getProperty(k).equalsIgnoreCase("true");
	    }catch(NullPointerException npE){
	    	System.err.println("No ha podido encontrarse la propiedad: \""+k+"\"");
	    }
	    return res;
	}
	
	public static int getIntProperty(Properties properties, String k){
	    int res = 0; 
		try{
			String p = properties.getProperty(k);
			if(p == null)
				System.err.println("No ha podido encontrarse la propiedad: \""+k+"\"");
			else
				res = Integer.parseInt(p);
		}catch(NumberFormatException nfE){
	    	System.err.println("El valor de la propiedad \""+k+"\" no es un número válido");
	    }
	    return res;
	}

	public static DisplayMode getDisplayMode(GraphicsDevice myDevice, int ancho, int alto, int profundidadDeColor) {
		DisplayMode oldDisplayMode = myDevice.getDisplayMode();

		int ratioDeRefrescoPreferido = 
			(oldDisplayMode.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN)
			? oldDisplayMode.getRefreshRate()
			: 120;
		int ratioDeRefrescoActual = -1;

		DisplayMode modes[] = myDevice.getDisplayModes();
		boolean disponible = false;
		
		for(int i = 0, len = modes.length; i < len; i++) {
			if (modes[i].getWidth() == ancho && modes[i].getHeight() == alto && modes[i].getBitDepth() == profundidadDeColor){
				int r = modes[i].getRefreshRate(); 
				if( r > ratioDeRefrescoActual && r <= ratioDeRefrescoPreferido)
					ratioDeRefrescoActual = r;
				disponible = true;
			}
		}
		return (disponible && ratioDeRefrescoActual > -1)
			? new DisplayMode(ancho, alto, profundidadDeColor, ratioDeRefrescoActual)
			: oldDisplayMode;
	}
}
