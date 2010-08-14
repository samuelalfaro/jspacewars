/* 
 * FractalImage.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of tips.
 * 
 * tips is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * tips is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tips.  If not, see <http://www.gnu.org/licenses/>.
 */

package textureGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import javax.swing.*;

/**
 * @author Samuel Alfaro
 */
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
	//*
	public FractalImage(int width, int height, Coulds noiseFunction, ColorRamp colorRamp, int imageType) {
		super(width, height, imageType);
		noiseFunction.setWidth(width);
		
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
					db.setElem(i, colorRamp.computeToIntRGB( Ramp.Predefinas.ACELERADA_DECELERADA.compute(values[y][x]) ) );
					//db.setElem(i, colorRamp.computeToIntRGB(values[y][x]));
				}
	}
    /*/
	static double valueFromRGB( int rgb ){
		int i = 0;
		
		i |= (((rgb & 0x00800000)>>23) + ((rgb & 0x00008000)>>15) + ((rgb & 0x00000080)>>7)) << 14;
		i |= (((rgb & 0x00400000)>>22) + ((rgb & 0x00004000)>>14) + ((rgb & 0x00000040)>>6)) << 12;
		i |= (((rgb & 0x00200000)>>21) + ((rgb & 0x00002000)>>13) + ((rgb & 0x00000020)>>5)) << 10;
		i |= (((rgb & 0x00100000)>>20) + ((rgb & 0x00001000)>>12) + ((rgb & 0x00000010)>>4)) << 8;
		i |= (((rgb & 0x00080000)>>19) + ((rgb & 0x00000800)>>11) + ((rgb & 0x00000008)>>3)) << 6;
		i |= (((rgb & 0x00040000)>>18) + ((rgb & 0x00000400)>>10) + ((rgb & 0x00000004)>>2)) << 4;
		i |= (((rgb & 0x00020000)>>17) + ((rgb & 0x00000200)>> 9) + ((rgb & 0x00000002)>>1)) << 2;
		i |= (((rgb & 0x00010000)>>16) + ((rgb & 0x00000100)>> 8) +  (rgb & 0x00000001) );
		
		return (double)i/0x10000;
	}

	public Plasma(int width, int height, int imageType) {
		super(width, height, imageType);
		
		DataBuffer db = this.getRaster().getDataBuffer();
		
		z = Math.random()*512;
		for(int i = 0, y = 0; y < this.getHeight(); y++)
			for(int x =0; x < this.getWidth(); x++, i++){
				int pixel = (int)( ( Math.sin( ( perlinNoise2D(x, y) - .5 )*Math.PI* 5 )/2 + 0.5) * 0x100 ) << 16 & 0x00FF0000;
				db.setElem(i, pixel);
			}
		
		z = Math.random()*512;
		for(int i = 0, y = 0; y < this.getHeight(); y++)
			for(int x =0; x < this.getWidth(); x++, i++){
				int pixel = db.getElem(i) | ((int)( ( Math.sin( ( perlinNoise2D(x, y) - .5 )* Math.PI * 9 )/2 + 0.5) * 0x100 ) << 8 & 0x0000FF00);
				db.setElem(i, pixel);
			}
		
		z = Math.random()*512;
		for(int i = 0, y = 0; y < this.getHeight(); y++)
			for(int x =0; x < this.getWidth(); x++, i++){
				int pixel = db.getElem(i) | ((int)( ( Math.sin( ( perlinNoise2D(x, y) - .5 )*Math.PI * 13 )/2 + 0.5) * 0x100 ) & 0x000000FF);
				db.setElem(i, pixel);
			}
		
		for(int i = 0, y = 0; y < this.getHeight(); y++)
			for(int x =0; x < this.getWidth(); x++, i++)
				db.setElem(i, ColorRamp.BlackGreenYellowWhite.computeToIntRGB(valueFromRGB(db.getElem(i))));
	}

	public Plasma(int width, int height, int imageType) {
		super(width, height, imageType);
		
		double values[][] = new double[width][height];
		
		z = Math.random();
		for(int x = 0; x < this.getWidth(); x++)
			for(int y = 0; y < this.getHeight(); y++)
				values[x][y] = Math.sin( 5 * Math.PI * ( perlinNoise2D(x, y) - .5 ) )/2 + 0.5;
		
		z = Math.random();
		for(int x = 0; x < this.getWidth(); x++)
			for(int y = 0; y < this.getHeight(); y++)
				values[x][y] += Math.sin( 5 * Math.PI * ( perlinNoise2D(x, y) - .5 ) )/2 + 0.5;
		
		z = Math.random();
		for(int x = 0; x < this.getWidth(); x++)
			for(int y = 0; y < this.getHeight(); y++)
				values[x][y] += Math.sin( 5 * Math.PI * ( perlinNoise2D(x, y) - .5 ) )/2 + 0.5;


		DataBuffer db = this.getRaster().getDataBuffer();
		for(int i = 0, y = 0; y < this.getHeight(); y++)
			for(int x =0; x < this.getWidth(); x++, i++)
				db.setElem(i, ColorRamp.BlackRedYellowWhite.computeToIntRGB( values[x][y]/3 ));
	}
	//*/
	
	public static void main(String... args) {
		Coulds noiseFunction = new Puff();

		noiseFunction.setAbsPartial(true);
		noiseFunction.setAmplitude(1.0);
		noiseFunction.setPersistence(0.7);
		noiseFunction.setFrequency(2.25);
		noiseFunction.setHarmonicScale(2.0);
		noiseFunction.setOctaves(8);
		
		ColorRamp colorRamp = new ColorRamp.RGBColorRamp( 2.5, 0.0, 2.0, -0.5, 2.0, -1.0);
		//ColorRamp colorRamp = ColorRamp.Predefinas.GreyScale;
		
		Image[] frames = new Image[16];
		
		for(int i = 0; i < frames.length; i ++){
//			z = ramp( 0.65, 0.8, i, frames.length-1 );
			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( -0.1, 1.0, i, frames.length-1 ) );
//			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( 3.90, 4.025, i, frames.length-1 ) );
//			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( 1.69, 2.18, i, frames.length-1 ) );
//			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( 4.05, 4.15, i, frames.length-1 ) );
//			noiseFunction.setZ( Ramp.Predefinas.ACELERADA_DECELERADA.compute( 9.2, 9.71, i, frames.length-1 ) );
//			frames[i] = new FractalImage( 256, 256, noiseFunction, ColorRamp.Predefinas.GreyScale, BufferedImage.TYPE_BYTE_GRAY );
			frames[i] = new FractalImage( 256, 256, noiseFunction, colorRamp, BufferedImage.TYPE_INT_ARGB );
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
				panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		frame.setContentPane(scrollPane);
		frame.pack();
		frame.setVisible(true);
	}
}
