package org.sam.tips;

import java.awt.*;
import java.awt.event.*;

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
	
	static public void main(String args[]){
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = ge.getScreenDevices();
		GraphicsDevice myDevice = devices[0];
		
		JFrame frame = new JFrame(myDevice.getDefaultConfiguration());
		frame.setContentPane(new MiPanel());
		boolean isFullScreen = myDevice.isFullScreenSupported();
		frame.setUndecorated(isFullScreen);
		frame.setResizable(!isFullScreen);
		
		if (isFullScreen) {
			System.out.println("Full-Screen mode");
			myDevice.setFullScreenWindow(frame);
			if(myDevice.isDisplayChangeSupported()){
				System.out.println("Cambiando resolucion de pantalla");
				DisplayMode oldDisplayMode = myDevice.getDisplayMode();
				DisplayMode displayMode = new DisplayMode(640,480,32,oldDisplayMode.getRefreshRate());
				myDevice.setDisplayMode(displayMode);
				frame.setSize(displayMode.getWidth(),displayMode.getHeight());
			}
			frame.validate();
		} else {
			System.out.println("Windowed mode");
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(0,0,dim.width,dim.height);
			frame.setVisible(true);
		}
	}
}
