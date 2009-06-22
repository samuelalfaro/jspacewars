package org.sam.pruebas.gui;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.util.FastMath;

@SuppressWarnings("deprecation")
public class TestLen extends JPanel {
	private static final long serialVersionUID = 1L;
	
	static public void main(String args[]){
		JFrame frame=new JFrame();
		frame.setBounds(100, 100, 1024, 1024);
		frame.setTitle("Test Len");
		frame.setDefaultCloseOperation(3);
//		frame.setResizable(false);

		TestLen panel=new TestLen();
		frame.getContentPane().add(panel);
		frame.setVisible(true);
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.RED);
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.BLACK);
		for(int i = 0; i < this.getWidth(); i ++)
			for(int j = 0; j < this.getHeight(); j++){
				int len = (int)FastMath.len(i +2048, j +2048);
				if( len % 30 < 5 )
					g2.drawRect(i, j, 0, 0);
			}
		g.setColor(Color.WHITE);
		for(int i = 0; i < this.getWidth(); i ++)
			for(int j = 0; j < this.getHeight(); j++)
				if( ((int)Math.sqrt( Math.pow(i+2048,2) + Math.pow(j+2048,2) ) + 15) % 30 < 5 )
					g2.drawRect(i, j, 0, 0);
	}
}

