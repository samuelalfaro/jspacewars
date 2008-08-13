package org.sam.tips;

/**  Viewer.java raw image viewer
 *  usage:  java Viewer 512 < lena.raw  
 *  512 is the size of the square image.
 *  Written by Yizong Cheng March 2002
 */

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Viewer extends JPanel{
	private static final long serialVersionUID = 1L;
	private final Image im;
	
	/**
	 *  The constructor
	 */   
	public Viewer(int size){
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int[] pix = new int[size*size];
		int index = 0;
		for (int i = 0; i < size; i++) 
			for (int j = 0; j < size; j++)
				try { 
					int c = System.in.read();   // grayscale
					pix[index++] = (255 << 24) | (c << 16) | (c << 8) | c;
				}catch(IOException e){ System.exit(1); }
				
		im = createImage(new MemoryImageSource(size,size,pix,0,size));
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(im,0,0,null);
	}
	
	/**
	 * main function
	 */
	public static void main(String[] args){
		if (args.length == 0){
			System.out.println("Usage: java Viewer size < image");
			System.exit(0);
		}
		int size = Integer.parseInt(args[0]);
		JFrame frame = new JFrame("Visor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new Viewer(size));
		frame.setSize(size,size+30);
		frame.setVisible(true); 
	}
}