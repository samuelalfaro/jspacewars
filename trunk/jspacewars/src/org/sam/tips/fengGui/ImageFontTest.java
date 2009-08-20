package org.sam.tips.fengGui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.fenggui.binding.render.ImageFont;
import org.fenggui.util.Alphabet;
import org.fenggui.util.fonttoolkit.*;
import org.sam.util.Tipografias;

/**
 * @author samuel
 *
 */
public class ImageFontTest {

	private static class FontDecorator{
		private final Paint borderPaint;
		private final Paint fontPaint;
		
		FontDecorator (Paint borderPaint, Paint fontPaint){
			this.borderPaint = borderPaint;
			this.fontPaint =   fontPaint;
		}
		
		public void decore( AssemblyLine line ){
			line.addStage(new Clear());
			
			line.addStage(new DrawCharacter(java.awt.Color.WHITE, false));
			line.addStage(new BinaryDilation(java.awt.Color.WHITE, 3));
			line.addStage(new PixelReplacer(borderPaint, java.awt.Color.WHITE));
		
			line.addStage(new DrawCharacter(java.awt.Color.CYAN, false));
			line.addStage(new PixelReplacer(fontPaint, java.awt.Color.CYAN));
		}
	}

	private static final char[] latinAlphabetCharArray = new char[]{
		'\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7',
		'\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf',
		'\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7',
		'\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df',
		'\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7',
		'\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef',
		'\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7',
		'\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'};
	
//	static{
//		for(char c: latinAlphabetCharArray){
//			System.out.printf("%c: \'\\u%04x\'\n", c, (int)c);
//		}
//	}

	private static final Alphabet LATIN_ALPHABET = new Alphabet(latinAlphabetCharArray);
	
	private static ImageFont fontToImageFont(Font awtFont, int style, float size,  FontDecorator decorator){
		MyFontFactory fontFactory = new MyFontFactory(
				LATIN_ALPHABET,
				awtFont.deriveFont(style,size)
		);
		if(decorator != null){
			decorator.decore( fontFactory.getAssemblyLine() );
		}else{
		    AssemblyLine line = fontFactory.getAssemblyLine();
		    line.addStage(new Clear());
		    line.addStage(new DrawCharacter(java.awt.Color.WHITE, true));
		}
		return fontFactory.createFont();
	}
	
	private final static Font awtFont = Tipografias.load("resources/fonts/saved.ttf");
	
	@SuppressWarnings("unused")
	private static final ImageFont imageFont1 =  fontToImageFont( awtFont, Font.BOLD, 40, null );
	
	private static final ImageFont imageFont =  fontToImageFont(
			awtFont, Font.BOLD, 40,
			new FontDecorator(
					new GradientPaint(0, 0, new java.awt.Color(128,64,0,255), 15, 15,  new java.awt.Color(192,192,0,255), true),
					new GradientPaint(0, 4, java.awt.Color.BLACK, 0, 20, java.awt.Color.GREEN, true)
			)
	);
	
	@SuppressWarnings("unused")
	private static final ImageFont imageFont2 = fontToImageFont(
			awtFont, Font.BOLD, 40,
			new FontDecorator(
					new GradientPaint(0, 0, java.awt.Color.BLACK, 15, 15, java.awt.Color.GREEN, true),
					new GradientPaint(0, 5, java.awt.Color.RED.darker(), 0, 20, java.awt.Color.YELLOW.darker(), true)
			)
	);
	@SuppressWarnings("unused")
	private static final ImageFont imageFont3 = fontToImageFont(
			awtFont, Font.BOLD, 40,
			new FontDecorator(
					new GradientPaint(0, 0, java.awt.Color.BLACK, 15, 15, java.awt.Color.GREEN, true),
					new GradientPaint(0, 5, java.awt.Color.RED, 0, 20, java.awt.Color.YELLOW, true)
			)
	);
		
	@SuppressWarnings("serial")
	public static void main(String[] args) {

		JFrame frame = new JFrame("Image Font Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setSize(512, 512);
		frame.setBackground(Color.BLACK);
		
		frame.getContentPane().add(new JPanel(){
			public void paintComponent(Graphics g){
				BufferedImage img = imageFont.getImage();
				g.setColor(Color.WHITE);
				g.drawRect(
						(this.getWidth() - img.getWidth())/2 -1 ,
						(this.getHeight() - img.getHeight())/2 -1,
						img.getWidth()+2,
						img.getHeight()+2
				);
				g.drawImage(img, (this.getWidth() - img.getWidth())/2, (this.getHeight() - img.getHeight())/2, null);
			}
		},
		BorderLayout.CENTER);
		try{
			imageFont.writeFontData("font1.png", "font.xml");
		}catch( IOException e ){
			e.printStackTrace();
		}
		frame.setVisible(true);
	}
}
