/* 
 * FractalImage.java
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
package org.sam.tools.textureGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import javax.swing.*;

public class FractalImage extends BufferedImage{

	private static void normalize(double[][] v, double min, double max){
		for(int y = 0; y < v.length; y++)
			for(int x = 0; x < v[0].length; x++){
				v[y][x] = (v[y][x] - min)/(max - min);
			}
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public FractalImage(int width, int height) {
		this(width, height, new Coulds(), ColorRamp.Predefinas.BlackRedYellowWhite, BufferedImage.TYPE_INT_RGB);
	}
	
	/**
	 * @param width
	 * @param height
	 * @param imageType
	 */
	public FractalImage(int width, int height, Coulds noiseFunction, ColorRamp colorRamp, int imageType) {
		super(width, height, imageType);
		noiseFunction.setDimensions(width, height);
		
		double values[][] = new double[height][width];
		
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;

		for(int y = 0; y < this.getHeight(); y++)
			for(int x = 0; x < this.getWidth(); x++){
				double v = noiseFunction.calculate(x, y);
				if( v < min )
					min = v;
				if( v > max )
					max = v;
				values[y][x] = v;
			}
		normalize(values,min,max);
		
		DataBuffer db = this.getRaster().getDataBuffer();
		if(imageType == BufferedImage.TYPE_INT_ARGB)
			for(int y = 0, i = 0; y < height; y++)
				for(int x =0; x < width; x++, i++){
					double vn = values[y][x];
					double a  = Ramp.Predefinas.DECELERADA.compute( Math.min( 5 * vn, 1.0) );
					double c  = Ramp.Predefinas.ACELERADA_DECELERADA.compute( vn );
					
					int pixel = ((int)Math.max(0, Math.min( a * 0xFF + 0.5, 255))<<24 & 0xFF000000) | colorRamp.computeToIntRGB(c);
					//int pixel = 0xFF000000 | colorRamp.computeToIntRGB( Math.sin( Math.PI*( 3 * values[x][y] - 0.5 ) )/2 +0.5 );
					db.setElem(i, pixel);
				}
		else
			for(int y = 0, i = 0; y < height; y++)
				for(int x =0; x < width; x++, i++){
					db.setElem(i, colorRamp.computeToIntRGB( Ramp.Predefinas.ACELERADA_DECELERADA.compute(Math.pow( values[y][x], 0.5) ) ) );
					//db.setElem(i, colorRamp.computeToIntRGB(values[y][x]));
				}
	}
	
	public static void main(String... args) {
		Coulds noiseFunction = new Puff();

		noiseFunction.setAbsPartial(true);
		noiseFunction.setAmplitude(1.0);
		noiseFunction.setPersistence(0.7);
		noiseFunction.setFrequency(2.25);
		noiseFunction.setHarmonicScale(1.5);
		noiseFunction.setOctaves(8);
		
		ColorRamp colorRamp = new ColorRamp.RGBColorRamp( 2.5, 0.0, 2.0, -0.5, 2.0, -1.0);
		//ColorRamp colorRamp = ColorRamp.Predefinas.GreyScale;
		
		Image[] frames = new Image[8];
		
		for(int i = 0; i < frames.length; i ++){
//			z = ramp( 0.65, 0.8, i, frames.length-1 );
			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( -0.1, 1.0, i, frames.length ) );
//			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( 3.90, 4.025, i, frames.length ) );
//			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( 1.69, 2.18, i, frames.length ) );
//			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( 4.05, 4.15, i, frames.length ) );
//			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( 9.2, 9.71, i, frames.length ) );
//			frames[i] = new FractalImage( 256, 256, noiseFunction, ColorRamp.Predefinas.GreyScale, BufferedImage.TYPE_BYTE_GRAY );
			frames[i] = new FractalImage( 256, 64, noiseFunction, colorRamp, BufferedImage.TYPE_INT_ARGB );
//			frames[i] = new FractalImage( 256, 256);
		}
		JFrame frame = new JFrame("FractalImage");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
//		panel.setBackground(Color.MAGENTA);
		panel.setLayout(new GridLayout(1,0));
		for(int i = 0; i < frames.length; i ++){
			panel.add(new JLabel(new ImageIcon(frames[i])));
		}
		JScrollPane scrollPane = new JScrollPane(
				panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		frame.setContentPane(scrollPane);
		frame.pack();
		frame.setVisible(true);
	}
}
