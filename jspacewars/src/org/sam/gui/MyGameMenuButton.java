package org.sam.gui;

import java.awt.*;
import java.awt.Font;

import org.fenggui.Button;
import org.fenggui.appearance.LabelAppearance;
import org.fenggui.binding.render.*;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.text.DirectTextRenderer;
import org.fenggui.binding.render.text.ITextRenderer;
import org.fenggui.decorator.background.*;
import org.fenggui.decorator.border.*;
import org.fenggui.text.ITextContentManager;
import org.fenggui.util.*;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;
import org.fenggui.util.fonttoolkit.*;
import org.sam.util.Tipografias;

public class MyGameMenuButton extends Button {

	private static class FontDecorator{
		private final Paint borderPaint;
		private final Paint fontPaint;
		
		FontDecorator (Paint borderPaint, Paint fontPaint){
			this.borderPaint = borderPaint;
			this.fontPaint =   fontPaint;
		}
		
		public void decore( FontFactory ff ){
			AssemblyLine line = ff.getAssemblyLine();
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

	private static final Alphabet LATIN_ALPHABET = new Alphabet(latinAlphabetCharArray);
	
	private static ImageFont fontToImageFont(Font awtFont, int style, float size,  FontDecorator decorator){
		FontFactory fontFactory = new FontFactory(
				LATIN_ALPHABET,
				awtFont.deriveFont(style,size)
		);
		//		for(char c:alphabetCharArray){
		//			System.out.printf("%c: \'\\u%04x\'\n", c, (int)c);
		//		}
		if(decorator != null)
			decorator.decore( fontFactory );
		return fontFactory.createFont();
	}

//	private final static Font awtFont = Tipografias.load("resources/fonts/abduction.ttf");
//	private final static Font awtFont = Tipografias.load("resources/fonts/arbeka.ttf");
	private final static Font awtFont = Tipografias.load("resources/fonts/saved.ttf");
//	private final static Font awtFont = Tipografias.load("resources/fonts/smelo.ttf");
	
	private static final ITextRenderer renderer1 = new DirectTextRenderer(
			fontToImageFont(
					awtFont, Font.BOLD, 40,
					new FontDecorator(
							new GradientPaint(0, 0, new java.awt.Color(128,64,0,255), 15, 15,  new java.awt.Color(192,192,0,255), true),
							new GradientPaint(0, 4, java.awt.Color.BLACK, 0, 20, java.awt.Color.GREEN, true)
					)
			)
	);
	private static final ITextRenderer renderer2 = new DirectTextRenderer(
			fontToImageFont(
					awtFont, Font.BOLD, 40,
					new FontDecorator(
							new GradientPaint(0, 0, java.awt.Color.BLACK, 15, 15, java.awt.Color.GREEN, true),
							new GradientPaint(0, 5, java.awt.Color.RED.darker(), 0, 20, java.awt.Color.YELLOW.darker(), true)
					)
			)
	);
	private static final ITextRenderer renderer3 = new DirectTextRenderer(
			fontToImageFont(
					awtFont, Font.BOLD, 40,
					new FontDecorator(
							new GradientPaint(0, 0, java.awt.Color.BLACK, 15, 15, java.awt.Color.GREEN, true),
							new GradientPaint(0, 5, java.awt.Color.RED, 0, 20, java.awt.Color.YELLOW, true)
					)
			)
	);
	
	private static Border generatePixmapBorder(Pixmap pixmap){
		if(pixmap == null) return new PlainBorder();
		
		ITexture sixteenSlicesTex = pixmap.getTexture();
		Pixmap[] pixmaps = new Pixmap[16];
	
		pixmaps[ 0] = new Pixmap(sixteenSlicesTex,   0,   0,   9,  12); // upperLeftCorner
		pixmaps[ 1] = new Pixmap(sixteenSlicesTex,   9,   0,   9,  12); // topLeftJunction
		pixmaps[ 2] = new Pixmap(sixteenSlicesTex,  18,   0, 100,  12); // topEdge
		pixmaps[ 3] = new Pixmap(sixteenSlicesTex, 118,   0,   9,  12); // topRightJunction
		pixmaps[ 4] = new Pixmap(sixteenSlicesTex, 127,   0,   9,  12); // upperRightCorner
	
		pixmaps[ 5] = new Pixmap(sixteenSlicesTex,   0,  12,   9,   4); // upperLeftJunction
		pixmaps[ 6] = new Pixmap(sixteenSlicesTex, 127,  12,   9,   4); // upperRightJunction
	
		pixmaps[ 7] = new Pixmap(sixteenSlicesTex,   0,  16,   9,   8); // leftEdge
		pixmaps[ 8] = new Pixmap(sixteenSlicesTex, 127,  16,   9,   8); // rightEdge
	
		pixmaps[ 9] = new Pixmap(sixteenSlicesTex,   0,  24,   9,   4); // lowerLeftJunction
		pixmaps[10] = new Pixmap(sixteenSlicesTex, 127,  24,   9,   4); // lowerRightJunction
	
		pixmaps[11] = new Pixmap(sixteenSlicesTex,   0,  28,   9,  35); // lowerLeftCorner
		pixmaps[12] = new Pixmap(sixteenSlicesTex,   9,  28,   9,  35); // bottomLeftJunction
		pixmaps[13] = new Pixmap(sixteenSlicesTex,  18,  28, 100,  35); // bottomEdge
		pixmaps[14] = new Pixmap(sixteenSlicesTex, 118,  28,   9,  35); // bottomRightJunction
		pixmaps[15] = new Pixmap(sixteenSlicesTex, 127,  28,   9,  35); // lowerRightCorner
	
		return new PixmapBorder16(pixmaps);
	}

	private static Background generatePixmapBackground(Pixmap pixmap){
		if(pixmap == null) return new PlainBackground();
			
		return new PixmapBackground( 
				new Pixmap(pixmap.getTexture(),   9,  12, 118,  16), true
		);
	}

	/**
	 * Constructs a new MyGameMenuButton.
	 * 
	 * @param text
	 * @param defaultState
	 * @param hoverState
	 * @param pressedState
	 */
	public MyGameMenuButton(String text, Pixmap defaultState, Pixmap hoverState, Pixmap pressedState) {
			this( text, defaultState, hoverState, null, pressedState, null );
	}

	/**
	 * Constructs a new MyGameMenuButton.
	 * 
	 * @param text
	 * @param defaultState
	 * @param hoverState
	 * @param focusState
	 * @param pressedState
	 * @param disabledState
	 */
	public MyGameMenuButton(String text, Pixmap defaultState, Pixmap hoverState, Pixmap focusState, Pixmap pressedState, Pixmap disabledState) {
		setText(text);
		initButton(defaultState, hoverState, focusState, pressedState, disabledState);
		this.setMinSize( new Dimension(300,85));
	}
	
	private void initButton(Pixmap defaultState, Pixmap hoverState, Pixmap focusState, Pixmap pressedState, Pixmap disabledState) {
		LabelAppearance appearance = this.getAppearance();

		appearance.removeAll();
		appearance.clearSpacings();
		
		appearance.setMargin( new Spacing(3, 3) );
		// appearance.setBorder( new Spacing(10,10) );
		appearance.setPadding( new Spacing( 0, 10 ) );
		
		appearance.addRenderer(ITextRenderer.DEFAULTTEXTRENDERERKEY, renderer1);

		appearance.add( STATE_NONE,     generatePixmapBackground(defaultState) );
		appearance.add( STATE_NONE,     generatePixmapBorder(defaultState) );
		appearance.add( STATE_HOVERED,  generatePixmapBackground(hoverState) );
		appearance.add( STATE_HOVERED,  generatePixmapBorder(hoverState) );
		appearance.add( STATE_FOCUSED,  generatePixmapBackground(focusState) );
		appearance.add( STATE_FOCUSED,  generatePixmapBorder(focusState) );
		appearance.add( STATE_PRESSED,  generatePixmapBackground(pressedState) );
		appearance.add( STATE_PRESSED,  generatePixmapBorder(pressedState) );
		appearance.add( STATE_DISABLED, generatePixmapBackground(disabledState) );
		appearance.add( STATE_DISABLED, generatePixmapBorder(disabledState) );

		updateState();
		updateMinSize();
	}

	/* (non-Javadoc)
	 * @see org.fenggui.Button#updateState(java.lang.String)
	 */
	@Override
	protected void updateState(String newActiveState) {
		LabelAppearance appearance = this.getAppearance();
	
		// change update state to only activate one state at a time
		// only default state is active as usual
		// re-enable default state so switches get called
		appearance.setEnabled(STATE_ERROR,    false);
		appearance.setEnabled(STATE_DISABLED, false);
		appearance.setEnabled(STATE_PRESSED,  false);
		appearance.setEnabled(STATE_FOCUSED,  false);
		appearance.setEnabled(STATE_HOVERED,  false);
		appearance.setEnabled(STATE_NONE,     false);
		appearance.setEnabled(STATE_DEFAULT,  true);
	
		if( newActiveState != null ){
			appearance.setEnabled(newActiveState, true);
		}else if( hasError() ){
			appearance.setEnabled(STATE_ERROR,    true);
		}else if( ! isEnabled() ){
			appearance.setEnabled(STATE_DISABLED, true);
		}else if( isPressed() ){
			appearance.setEnabled(STATE_PRESSED,  true);
		}else if( isFocused() ){
			//appearance.setEnabled(STATE_FOCUSED,  true);
			if( isHovered() )
				appearance.setEnabled(STATE_HOVERED,  true);
			else
				appearance.setEnabled(STATE_NONE,     true);
		}else if( isHovered() ){
			appearance.setEnabled(STATE_HOVERED,  true);
		}else
			appearance.setEnabled(STATE_NONE,     true);
	}
	
	/* (non-Javadoc)
	 * @see org.fenggui.ObservableLabelWidget#paintContent(org.fenggui.binding.render.Graphics, org.fenggui.binding.render.IOpenGL)
	 */
	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;

		int contentWidth = getAppearance().getContentWidth();
		int contentHeight = getAppearance().getContentHeight();

		Pixmap pixmap = getPixmap();
		ITextContentManager textData = getTextRendererData();

		if( pixmap != null ){
			width = pixmap.getWidth();
			height = pixmap.getHeight();
			if( !textData.isEmpty() )
				width += getAppearance().getGap();
		}else if( textData.isEmpty() )
			return;

		if( !textData.isEmpty() ){
			width += textData.getSize().getWidth();
			height = Math.max(height, textData.getSize().getHeight());
		}

		x = x + getAppearance().getAlignment().alignX(contentWidth, width);

		if( pixmap != null ){
			g.setColor(Color.WHITE);
			y = getAppearance().getAlignment().alignY(contentHeight, pixmap.getHeight());
			g.drawImage(pixmap, x, y);
			x += pixmap.getWidth() + getAppearance().getGap();
		}

		if( !textData.isEmpty() ){
			y = getAppearance().getAlignment().alignY(contentHeight, textData.getSize().getHeight())
					+ textData.getSize().getHeight();
   
			ITextRenderer renderer = null;
			y+=13; // TODO Fixme Ñapa
			if(this.isPressed())
				renderer = renderer3;
			else if(this.isHovered())
				renderer = renderer2;
			else
				renderer = renderer1;
			
			if(renderer != null)
				renderer.render(x, y, textData.getContent(), Color.WHITE, g);
			else
				textData.render(x, y, g, getAppearance());
		}
	}
}
