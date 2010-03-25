/* 
 * TextureFontGenerator.java
 * 
 * Copyright (c) 2010 Samuel Alfaro <samuelalfaro at gmail dot com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * @author Samuel Alfaro <samuelalfaro at gmail dot com>
 *
 */
public class TextureFontGenerator {

	private static final char[] basicAlphabetCharArray = (
		" !\"#$%&'()*+,-./" +
		"0123456789:;<=>?" +
		"@ABCDEFGHIJKLMNO" +
		"PQRSTUVWXYZ[\\]^_" +
		"`abcdefghijklmno" +
		"pqrstuvwxyz{|}¡¿"
	).toCharArray();
	
	private static final char[] latinExtraCharArray = new char[]{
		'\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7',
		'\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf',
		'\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7',
		'\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df',
		'\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7',
		'\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef',
		'\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7',
		'\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'
	};
	
	private static final char[] latinAlphabetCharArray = (
		new String(basicAlphabetCharArray) +
		new String(latinExtraCharArray)
	).toCharArray();
	
	static{
		Arrays.sort(latinAlphabetCharArray);
	}
	
	private char[] alfabeto;
	private char[] ignorados;
	
	private Font   font;
	private double correctorX, correctorY;
	
	public TextureFontGenerator(){
		this(basicAlphabetCharArray, null);
	}
	
	public TextureFontGenerator(char[] alfabeto){
		this(alfabeto, null);
	}
	
	public TextureFontGenerator(char[] alfabeto, char[] ignorados){
		this.alfabeto = alfabeto;
		this.ignorados = ignorados;
		if (this.ignorados != null){
			Arrays.sort(this.ignorados);
		}
		correctorX = 1.0;
		correctorY = 1.0;
	}
	
	public BufferedImage generate( int width, int height ){
		return generate( width, height, (int)(Math.sqrt(alfabeto.length) + 0.5) );
	}
	
	public BufferedImage generate( int width, int height, int columnas ){
		return generate( width, height, columnas, 0, 0 );
	}
	
	public BufferedImage generate( int width, int height, int columnas, int bordeH, int bordeV ){
		
		BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = img.createGraphics();
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, width, height);
		g2d.setColor(Color.WHITE);

		g2d.setRenderingHint(
				RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON
		);
		g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
		);
		
		int filas    = alfabeto.length / columnas;
		if( alfabeto.length % columnas != 0)
			filas++;
		
		if(font == null)
			font = g2d.getFont();		
		FontMetrics fontMetrics = g2d.getFontMetrics(font);
		
		double defaultWidth = fontMetrics.charWidth('W') * columnas;
		System.out.println(defaultWidth);
		
		double defaultHeight = (fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent()) * filas;
		System.out.println(defaultHeight);
		
		AffineTransform trasnform = new AffineTransform();
		double scaleX = correctorX * ( img.getWidth() - bordeH * ( columnas + 1) )/defaultWidth;
		System.out.println(scaleX);
		double scaleY = correctorY * ( img.getHeight() - bordeV * ( filas + 1) )/defaultHeight;
		System.out.println(scaleY);
		trasnform.scale( scaleX, scaleY );
		System.out.println(trasnform);
		
		Font font1 = font.deriveFont( trasnform );
		fontMetrics = g2d.getFontMetrics(font1);
		g2d.setFont(font1);
		
		defaultWidth = fontMetrics.charWidth('M') * columnas;
		System.out.println(defaultWidth);
		defaultHeight = (fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent()) * filas;
		System.out.println(defaultHeight);
		
		double offX = (double)( img.getWidth() - bordeH ) / columnas;
		double offY = (double)( img.getHeight() - bordeV ) / filas;
		int charHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
		
		for( int i = 0, j = 0; j < filas; j++ ){
			for( int k=0; k < columnas && i < alfabeto.length; k++, i++){
				char c = alfabeto[i];
				if(ignorados == null || Arrays.binarySearch(ignorados, c) < 0 ){
					int offCharX = (int)( k * offX + bordeH + ( offX - bordeH - fontMetrics.charWidth(c))/2);
					int offCharY = (int)( j * offY + bordeV + fontMetrics.getMaxAscent() + (offY - bordeV - charHeight )/2 );
					g2d.setColor(Color.DARK_GRAY);
					g2d.drawRect(offCharX, (int)(j * offY + bordeV + (offY - bordeV - charHeight )/2), fontMetrics.charWidth(c), charHeight);
					g2d.setColor(Color.WHITE);
					g2d.drawChars( alfabeto, i, 1, offCharX, offCharY );
				}
			}
		}
		return img;
	}
	
	public static String getType(String filename){
		String aux = filename;
		int index = 0;
		int partial_index = aux.indexOf('.') + 1;
		while(partial_index > 0){
			index += partial_index;
			aux = aux.substring(partial_index);
			System.out.println(aux);
			partial_index = aux.indexOf('.') + 1;
		}
		return (index > 0) ? filename.substring(index) : "";
	}
	
	public static void export( BufferedImage img, String filename ) throws IOException{
		String type = getType(filename);
		if(type.length() == 0)
			type = "png";
		ImageIO.write( img, type, new File( filename ));
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(	latinAlphabetCharArray.length);
		for(int i = 0; i < latinAlphabetCharArray.length; ){
			for(int j=0; j < 16 && i < latinAlphabetCharArray.length; j++, i++)
				System.out.print(" "+ latinAlphabetCharArray[i]);
			System.out.println();
		}
		TextureFontGenerator g = new TextureFontGenerator( latinAlphabetCharArray );
//		g.ignorados = new char[]{'Æ','Ð','×','Ý','Þ','ß','æ','ð','ý', 'þ', 'ÿ'};
		g.font = Tipografias.load("resources/fonts/arbeka.ttf", Font.PLAIN, 64.0f);
//		g.font = Tipografias.load("resources/fonts/smelo.ttf", Font.PLAIN, 64.0f);
//		g.font = Tipografias.load("resources/fonts/saved.ttf", Font.PLAIN, 64.0f);
		g.correctorX = 1.3;
		g.correctorY = 1.3;
		
		export( g.generate( 512, 512, 16, 6, 6 ), "arbeka.png");
	}
}
