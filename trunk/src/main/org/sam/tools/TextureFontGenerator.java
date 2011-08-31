/* 
 * TextureFontGenerator.java
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
package org.sam.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.sam.util.Tipografias;

/**
 * @author Samuel Alfaro <samuelalfaro at gmail dot com>
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
		new String( basicAlphabetCharArray ) +
		new String( latinExtraCharArray )
	).toCharArray();
	
	static{
		Arrays.sort( latinAlphabetCharArray );
	}

	private char[] alfabeto;
	private char[] ignorados;

	private Font font;

	public TextureFontGenerator(){
		this( basicAlphabetCharArray, null );
	}

	public TextureFontGenerator( char[] alfabeto ){
		this( alfabeto, null );
	}

	public TextureFontGenerator( char[] alfabeto, char[] ignorados ){
		this.alfabeto = alfabeto;
		this.ignorados = ignorados;
		if( this.ignorados != null ){
			Arrays.sort( this.ignorados );
		}
	}

	public BufferedImage generate( PrintStream out, int width, int height ){
		return generate( out, width, height, 1, 0, 0, 1.0 );
	}

	public BufferedImage generate( PrintStream out, int width, int height, int nFilas, int bordeH, int bordeV, double ajustScaleX ){
		BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_BYTE_GRAY);
		//BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g2d = img.createGraphics();
		g2d.setBackground( new Color(0,0,0,0) );
		g2d.setBackground( Color.BLACK );
		g2d.clearRect( 0, 0, width, height );
		g2d.setColor( Color.WHITE );

		g2d.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

		int nChars = alfabeto.length;
		if( ignorados != null )
			nChars -= ignorados.length;

		if( font == null )
			font = g2d.getFont();
		FontMetrics fontMetrics = g2d.getFontMetrics( font );

		double defaultWidth = 0;
		for( int i = 0; i < alfabeto.length; i++ ){
			char c = alfabeto[i];
			if( ignorados == null || Arrays.binarySearch( ignorados, c ) < 0 ){
				defaultWidth += fontMetrics.charWidth( c );
			}
		}

		double defaultHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();

		AffineTransform trasnform = new AffineTransform();
		double scaleX = ( img.getWidth() * nFilas - 2 * bordeH * nChars ) / defaultWidth;
		double scaleY = ( img.getHeight() - 2 * nFilas * bordeV ) / ( defaultHeight * nFilas );

		trasnform.scale( scaleX * ajustScaleX, scaleY );
		Font font1 = font.deriveFont( trasnform );
		fontMetrics = g2d.getFontMetrics( font1 );
		g2d.setFont( font1 );

		int offX = bordeH;
		int offY = bordeV + fontMetrics.getMaxAscent();
		int fila = 0;
		int y    = 0;
		
		out.printf("<?xml version='1.0' encoding='utf-8'?>\n");
		out.printf("<Font scaleX='%s' scaleY='%s' textureWidth='%d' textureHeight='%d'>\n", 
				Double.toString( scaleX * ajustScaleX ), Double.toString( scaleY ),
				img.getWidth(), img.getHeight()
		);
		
		for( int i = 0; i < alfabeto.length; i++ ){
			char c = alfabeto[i];
			if( ignorados == null || Arrays.binarySearch( ignorados, c ) < 0 ){
				int nextX = offX + fontMetrics.charWidth( c ) + 2 * bordeH;
				if( nextX > width ){
					offX = bordeH;
					fila++;
					y = fila * ( 2* bordeV + fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() );
				}
//				g2d.setColor(Color.YELLOW);
//				g2d.drawRect( offX-bordeH, y, fontMetrics.charWidth(c)+2*bordeH, fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + 2*bordeV );	
//				g2d.setColor(Color.DARK_GRAY);
//				g2d.drawRect( offX, y + bordeV, fontMetrics.charWidth(c), fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() );
//				g2d.setColor( Color.WHITE );
				
				out.printf("\t<CharacterPixmap x='%d' y='%d' width='%d' height='%d' charWidth='%d' charHeight='%d'>\n", 
						offX-bordeH, y, 
						fontMetrics.charWidth(c)+2*bordeH, fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + 2*bordeV,
						fontMetrics.charWidth(c), fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() );
				out.printf("\t\t<character><![CDATA[%c]]></character>\n",c);
				out.printf("\t</CharacterPixmap>\n");
				
				g2d.drawChars( alfabeto, i, 1, offX , y + offY );
				offX += fontMetrics.charWidth( c ) + 2 * bordeH;
			}
		}
		
		out.printf("</Font>\n");
		return img;
	}

	private static String getType( String filename ){
		String aux = filename;
		int index = 0;
		int partial_index = aux.indexOf( '.' ) + 1;
		while( partial_index > 0 ){
			index += partial_index;
			aux = aux.substring( partial_index );
			partial_index = aux.indexOf( '.' ) + 1;
		}
		return ( index > 0 ) ? filename.substring( index ): "";
	}

	public static void export( BufferedImage img, String filename ) throws IOException{
		String type = getType( filename );
		if( type.length() == 0 )
			type = "png";
		ImageIO.write( img, type, new File( filename ) );
	}

	public static void main( String[] args ) throws IOException{
		//String fontName = "arbeka"; double scaleXAjust = 0.94;
		String fontName = "saved"; double scaleXAjust = 0.958;
		
		String fontFile = "resources/fonts/" + fontName + ".ttf";
		String fontImg = "resources/" + fontName + ".png";
		String fontXML = "resources/" + fontName + ".xml";
		
//		System.out.println( latinAlphabetCharArray.length );
//		for( int i = 0; i < latinAlphabetCharArray.length; ){
//			for( int j = 0; j < 16 && i < latinAlphabetCharArray.length; j++, i++ )
//				System.out.print( " " + latinAlphabetCharArray[i] );
//			System.out.println();
//		}
		
		TextureFontGenerator g = new TextureFontGenerator( latinAlphabetCharArray );
		g.ignorados = new char[] { 'Æ', 'Ð', '×', 'Ý', 'Þ', 'ß', 'æ', 'ð', 'ý', 'þ', 'ÿ' };
		g.font = Tipografias.load( fontFile, Font.PLAIN, 64.0f );
		PrintStream outXML = new PrintStream( new FileOutputStream( new File( fontXML ) ) );
		export( g.generate( outXML, 1024, 512, 8, 7, 7, scaleXAjust ), fontImg );
	}
}
