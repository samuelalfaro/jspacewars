/* 
 * MyGameMenuButton.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
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
package org.sam.jspacewars;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.fenggui.Button;
import org.fenggui.appearance.LabelAppearance;
import org.fenggui.binding.render.*;
import org.fenggui.binding.render.text.DirectTextRenderer;
import org.fenggui.binding.render.text.ITextRenderer;
import org.fenggui.decorator.background.*;
import org.fenggui.decorator.border.*;
import org.fenggui.text.ITextContentManager;
import org.fenggui.util.*;

public class MyGameMenuButton extends Button {

	private static ImageFont font = null;
	static{
		try{
			font = new ImageFont("font1.png", "font.xml");
		}catch( FileNotFoundException e ){
		}catch( IOException e ){
		}
	}
	private static final ITextRenderer renderer1 = new DirectTextRenderer(font);

	static{
		try{
			font = new ImageFont("font2.png", "font.xml");
		}catch( FileNotFoundException e ){
		}catch( IOException e ){
		}
	}
	private static final ITextRenderer renderer2 = new DirectTextRenderer(font);

	static{
		try{
			font = new ImageFont("font3.png", "font.xml");
		}catch( FileNotFoundException e ){
		}catch( IOException e ){
		}
	}
	private static final ITextRenderer renderer3 = new DirectTextRenderer(font);

//	private static Border generatePixmapBorder(Pixmap pixmap) {
//		if( pixmap == null )
//			return new PlainBorder();
//
//		ITexture sixteenSlicesTex = pixmap.getTexture();
//		Pixmap[] pixmaps = new Pixmap[16];
//
//		pixmaps[0] = new Pixmap(sixteenSlicesTex, 0, 0, 9, 12); // upperLeftCorner
//		pixmaps[1] = new Pixmap(sixteenSlicesTex, 9, 0, 9, 12); // topLeftJunction
//		pixmaps[2] = new Pixmap(sixteenSlicesTex, 18, 0, 100, 12); // topEdge
//		pixmaps[3] = new Pixmap(sixteenSlicesTex, 118, 0, 9, 12); // topRightJunction
//		pixmaps[4] = new Pixmap(sixteenSlicesTex, 127, 0, 9, 12); // upperRightCorner
//
//		pixmaps[5] = new Pixmap(sixteenSlicesTex, 0, 12, 9, 4); // upperLeftJunction
//		pixmaps[6] = new Pixmap(sixteenSlicesTex, 127, 12, 9, 4); // upperRightJunction
//
//		pixmaps[7] = new Pixmap(sixteenSlicesTex, 0, 16, 9, 8); // leftEdge
//		pixmaps[8] = new Pixmap(sixteenSlicesTex, 127, 16, 9, 8); // rightEdge
//
//		pixmaps[9] = new Pixmap(sixteenSlicesTex, 0, 24, 9, 4); // lowerLeftJunction
//		pixmaps[10] = new Pixmap(sixteenSlicesTex, 127, 24, 9, 4); // lowerRightJunction
//
//		pixmaps[11] = new Pixmap(sixteenSlicesTex, 0, 28, 9, 35); // lowerLeftCorner
//		pixmaps[12] = new Pixmap(sixteenSlicesTex, 9, 28, 9, 35); // bottomLeftJunction
//		pixmaps[13] = new Pixmap(sixteenSlicesTex, 18, 28, 100, 35); // bottomEdge
//		pixmaps[14] = new Pixmap(sixteenSlicesTex, 118, 28, 9, 35); // bottomRightJunction
//		pixmaps[15] = new Pixmap(sixteenSlicesTex, 127, 28, 9, 35); // lowerRightCorner
//
//		return new PixmapBorder16(pixmaps);
//	}
	
	private static Border generatePixmapBorder(Pixmap pixmap) {
		if( pixmap == null )
			return new PlainBorder();

		ITexture texture = pixmap.getTexture();
		Pixmap[] pixmaps = new Pixmap[8];

		pixmaps[PixmapBorder.TOP_LEFT]     = new Pixmap(texture,   0,  0,  18, 12);
		pixmaps[PixmapBorder.TOP]          = new Pixmap(texture,  18,  0, 100, 12);
		pixmaps[PixmapBorder.TOP_RIGHT]    = new Pixmap(texture, 118,  0,  18, 12);

		pixmaps[PixmapBorder.LEFT]         = new Pixmap(texture,   0, 12,  18, 16);
		pixmaps[PixmapBorder.RIGHT]        = new Pixmap(texture, 118, 12,  18, 16);
		
		pixmaps[PixmapBorder.BOTTOM_LEFT]  = new Pixmap(texture,   0, 28,  18, 35);
		pixmaps[PixmapBorder.BOTTOM]       = new Pixmap(texture,  18, 28, 100, 35);
		pixmaps[PixmapBorder.BOTTOM_RIGHT] = new Pixmap(texture, 118, 28,  18, 35);

		return new PixmapBorder(pixmaps);
	}


	private static Background generatePixmapBackground(Pixmap pixmap) {
		if( pixmap == null )
			return new PlainBackground();
		return new PixmapBackground(new Pixmap(pixmap.getTexture(), 18, 12, 100, 16), true);
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
		this(text, defaultState, hoverState, null, pressedState, null);
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
	public MyGameMenuButton(String text, Pixmap defaultState, Pixmap hoverState, Pixmap focusState,
			Pixmap pressedState, Pixmap disabledState) {
		setText(text);
		initButton(defaultState, hoverState, focusState, pressedState, disabledState);
	}

	private Dimension minSize;

	public Dimension getMinSize() {
		if( minSize == null )
			;
		minSize = new Dimension(312, 86);
		return minSize;
	}

	private void initButton(Pixmap defaultState, Pixmap hoverState, Pixmap focusState, Pixmap pressedState,
			Pixmap disabledState) {
		LabelAppearance appearance = this.getAppearance();

		appearance.removeAll();
		appearance.clearSpacings();

		appearance.setMargin(new Spacing(3, 3));
		appearance.setBorder(new Spacing(0, 0));
		// appearance.setBorder( new Spacing(12, 9, 9, 35) );
		// Spacing spacing = generatePixmapBorder(defaultState) ;
		// System.out.println("Top:   " + spacing.getTop());
		// System.out.println("Left:  " + spacing.getLeft());
		// System.out.println("Right: " + spacing.getRight());
		// System.out.println("Bottom:" + spacing.getBottom());
		appearance.setPadding(new Spacing(12, 15, 15, 37));

		appearance.addRenderer(ITextRenderer.DEFAULTTEXTRENDERERKEY, renderer1);

		appearance.add(STATE_NONE, generatePixmapBackground(defaultState));
		appearance.add(STATE_NONE, generatePixmapBorder(defaultState));
		appearance.add(STATE_HOVERED, generatePixmapBackground(hoverState));
		appearance.add(STATE_HOVERED, generatePixmapBorder(hoverState));
		appearance.add(STATE_FOCUSED, generatePixmapBackground(focusState));
		appearance.add(STATE_FOCUSED, generatePixmapBorder(focusState));
		appearance.add(STATE_PRESSED, generatePixmapBackground(pressedState));
		appearance.add(STATE_PRESSED, generatePixmapBorder(pressedState));
		appearance.add(STATE_DISABLED, generatePixmapBackground(disabledState));
		appearance.add(STATE_DISABLED, generatePixmapBorder(disabledState));

		updateState();
		// updateMinSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fenggui.Button#updateState(java.lang.String)
	 */
	@Override
	protected void updateState(String newActiveState) {
		LabelAppearance appearance = this.getAppearance();

		// change update state to only activate one state at a time
		// only default state is active as usual
		// re-enable default state so switches get called
		appearance.setEnabled(STATE_ERROR, false);
		appearance.setEnabled(STATE_DISABLED, false);
		appearance.setEnabled(STATE_PRESSED, false);
		appearance.setEnabled(STATE_FOCUSED, false);
		appearance.setEnabled(STATE_HOVERED, false);
		appearance.setEnabled(STATE_NONE, false);
		appearance.setEnabled(STATE_DEFAULT, true);

		if( newActiveState != null ){
			appearance.setEnabled(newActiveState, true);
		}else if( hasError() ){
			appearance.setEnabled(STATE_ERROR, true);
		}else if( !isEnabled() ){
			appearance.setEnabled(STATE_DISABLED, true);
		}else if( isPressed() ){
			appearance.setEnabled(STATE_PRESSED, true);
		}else if( isFocused() ){
			// appearance.setEnabled(STATE_FOCUSED, true);
			if( isHovered() )
				appearance.setEnabled(STATE_HOVERED, true);
			else
				appearance.setEnabled(STATE_NONE, true);
		}else if( isHovered() ){
			appearance.setEnabled(STATE_HOVERED, true);
		}else
			appearance.setEnabled(STATE_NONE, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fenggui.ObservableLabelWidget#paintContent(org.fenggui.binding.render
	 * .Graphics, org.fenggui.binding.render.IOpenGL)
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
			y = getAppearance().getBottomMargins() + (contentHeight - textData.getSize().getHeight()) / 2;

			ITextRenderer renderer = null;
			if( this.isPressed() )
				renderer = renderer3;
			else if( this.isHovered() )
				renderer = renderer2;
			else
				renderer = renderer1;

			if( renderer != null )
				renderer.render(x, y, textData.getContent(), Color.WHITE, g);
			else
				textData.render(x, y, g, getAppearance());
		}
	}

	private static MyGameMenuButton prototipo;

	public static boolean hasPrototipo() {
		return MyGameMenuButton.prototipo != null;
	}

	public static void setPrototipo(MyGameMenuButton prototipo) {
		MyGameMenuButton.prototipo = prototipo;
	}

	public static MyGameMenuButton derive(String text) {
		MyGameMenuButton result = (MyGameMenuButton) prototipo.clone();
		result.setText(text);
		return result;
	}
}
