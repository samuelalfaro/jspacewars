package org.sam.tips;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PruebaFullScreen {
	@SuppressWarnings("serial")
	static class MiPanel extends JPanel{
		public MiPanel(){
			this.addKeyListener( new KeyAdapter() {
				public void keyPressed(KeyEvent e){
					int keyCode = e.getKeyCode();
					if ((keyCode == KeyEvent.VK_ESCAPE)
						|| (keyCode == KeyEvent.VK_Q)
						|| (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown()) )
						System.exit(0);
				}
			});
			this.setFocusable(true);
			this.requestFocus();
		}
		public void paintComponent(Graphics g){
			g.drawLine(0,0,this.getWidth(),this.getHeight());
		}
	}
	
	private static String DisplayModeToString(DisplayMode mode){
		return String.format("%d x %d [%s bits] [%s Hz]",
				mode.getWidth(),
				mode.getHeight(),
				mode.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI ?
						"BIT_DEPTH_MULTI" : Integer.toString( mode.getBitDepth() ),
				mode.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN ?
						"REFRESH_RATE_UNKNOWN" : Integer.toString( mode.getRefreshRate() )
		);
	}
	
	static public void main(String args[]){
		
		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame(myDevice.getDefaultConfiguration());
		frame.setUndecorated(true);
		frame.setResizable(false);
		
		if ( myDevice.isFullScreenSupported() ) {
			System.out.println("Full-Screen mode");
			
			myDevice.setFullScreenWindow(frame);
			
			if( myDevice.isDisplayChangeSupported() ){
				
				System.out.println("Cambiando resolucion de pantalla");
				
				System.out.println("Current Display Mode:");
				DisplayMode currentDisplayMode = myDevice.getDisplayMode();
				System.out.println( DisplayModeToString(currentDisplayMode) );
				
				System.out.println("\nSuported Display Modes:");
				for(DisplayMode mode: myDevice.getDisplayModes())
					System.out.println( DisplayModeToString(mode) );
				
				DisplayMode newDisplayMode = 
					new DisplayMode( 640, 400, currentDisplayMode.getBitDepth(), currentDisplayMode.getRefreshRate() );
				try{
					myDevice.setDisplayMode(newDisplayMode);
					frame.setSize(newDisplayMode.getWidth(),newDisplayMode.getHeight());
				}catch(IllegalArgumentException e){
					System.err.println("Display Mode: "+ DisplayModeToString(newDisplayMode) +" not supported!!");
				}
			}
			frame.setContentPane( new MiPanel() );
			
			frame.validate();
		} else {
			System.out.println("Windowed mode");
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(0,0,dim.width,dim.height);
			frame.setContentPane( new MiPanel() );
			frame.setVisible(true);
		}
	}
}
