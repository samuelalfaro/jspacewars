/* 
 * UIManager.java
 * 
 * Copyright (c) 2012 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
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
package org.sam.jogl.gui;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import javax.media.opengl.GL2;
import javax.vecmath.Color4f;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.Textura;
import org.sam.util.Imagen;

/**
 * 
 */
public class UIManager{
	
	private static final Apariencia BLEND = new Apariencia();
	
	static{
		BLEND.setAtributosTransparencia( 
				new AtributosTransparencia( 
						AtributosTransparencia.Equation.ADD,
						AtributosTransparencia.SrcFunc.SRC_ALPHA,
						AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA
				)
		);
	}
	
	private UIManager(){}
	
	private static Hashtable<Object,Object> hashtable = null;
	
	private static void loadFonts( GL2 gl, Hashtable<Object, Object> hashtable ){
		
		final String fontDef      = "resources/texturas/fonts/arbeka.xml";
		final String font1Texture = "resources/texturas/fonts/arbeka.png";
		final String font2Texture = "resources/texturas/fonts/arbeka-blur.png";
		final String font3Texture = "resources/texturas/fonts/arbeka-neon.png";
		
		
		try{
			TextureFont font = new TextureFont( gl, new FileInputStream( fontDef ) ).deriveFont( 0.3f );

			BufferedImage img = Imagen.cargarToBufferedImage( font1Texture );

			Apariencia apFont = new Apariencia();

			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );

			apFont.setAtributosTextura( new AtributosTextura() );

			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.getAtributosTextura().setCombineAlphaSource1(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			
			apFont.setAtributosTransparencia( new AtributosTransparencia(
					AtributosTransparencia.Equation.ADD, 
					AtributosTransparencia.SrcFunc.SRC_ALPHA,
					AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA
			) );
			
			font.setApariencia( apFont );
			
			hashtable.put( "Font.default", font );
			
			img = Imagen.cargarToBufferedImage( font2Texture );
			apFont = new Apariencia();

			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );

			apFont.setAtributosTextura( new AtributosTextura() );
			

			apFont.getAtributosTextura().setEnvColor( 1.0f, 1.0f, 1.0f, 1.0f );
			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.INTERPOLATE );
			// C' = S0 * S2 + S1*(1-S2)
			apFont.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineRgbSource1(
					AtributosTextura.CombineSrc.CONSTANT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineRgbSource2(
					AtributosTextura.CombineSrc.PREVIOUS, AtributosTextura.CombineOperand.SRC_ALPHA
			);

			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0(
				AtributosTextura.CombineSrc.OBJECT, 
				AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
				AtributosTextura.CombineSrc.OBJECT,
				AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.getAtributosTextura().setCombineAlphaSource1(
				AtributosTextura.CombineSrc.TEXTURE,
				AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.setAtributosTransparencia(
				new AtributosTransparencia(
					AtributosTransparencia.Equation.ADD,
					AtributosTransparencia.SrcFunc.ZERO,
					AtributosTransparencia.DstFunc.SRC_COLOR
				)
			);
			
			hashtable.put( "Font.default.shadow", font.deriveFont( apFont ) );
			
			img = Imagen.cargarToBufferedImage( font3Texture );
			apFont = new Apariencia();

			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );

			apFont.setAtributosTextura( new AtributosTextura() );

			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0(
				AtributosTextura.CombineSrc.OBJECT, 
				AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
				AtributosTextura.CombineSrc.OBJECT,
				AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.getAtributosTextura().setCombineAlphaSource1(
				AtributosTextura.CombineSrc.TEXTURE,
				AtributosTextura.CombineOperand.SRC_ALPHA
			);
			apFont.setAtributosTransparencia(
				new AtributosTransparencia(
					AtributosTransparencia.Equation.ADD,
					AtributosTransparencia.SrcFunc.SRC_ALPHA,
					AtributosTransparencia.DstFunc.ONE
				)
			);
			
			hashtable.put( "Font.default.fx", font.deriveFont( apFont ) );
			
			
		}catch( FileNotFoundException e ){
			e.printStackTrace();
		}
	}
	
	private static void loadTextures( GL2 gl, Hashtable<Object, Object> hashtable ){
		
		Border border;
		Background background;
		
		//String texture1 = "resources/bordes_prueba.png";
		String texture1 = "resources/fondo.png";
		
		BufferedImage img = Imagen.cargarToBufferedImage( texture1 );

		Apariencia ap1 = new Apariencia();

		ap1.setTextura( new Textura( gl, Textura.Format.RGBA, img, false ) );
		ap1.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
		ap1.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );

		ap1.setAtributosTextura( new AtributosTextura() );

		ap1.getAtributosTextura().setMode( AtributosTextura.Mode.REPLACE );
		
		ap1.setAtributosTransparencia( new AtributosTransparencia( AtributosTransparencia.Equation.ADD,
				AtributosTransparencia.SrcFunc.SRC_ALPHA, AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA ) );
		
		background = new Background.Textured( 16,16, 48, 48, 64, 64 );
		background.setApariencia( ap1 );
		hashtable.put( "Background.default", background );
		
		
		border = new Border.Textured( new Insets( 16 ), 64, 64 );
		border.setApariencia( ap1 );
		
		hashtable.put( "Border.default", border );
		
		border = new Border.Gradient(
			new Insets( 5 ), 
			new Color4f( 1.0f, 1.0f, 1.0f, 0.5f ),
			new Color4f( 0.25f, 0.0f, 0.25f, 1.0f ),
			new Color4f( 0.0f, 0.0f, 0.0f, 0.5f )
		);
		border.setApariencia( BLEND );
		hashtable.put( "Container.Border", border );
		
		background = new Background.Gradient( 0.25f, 0.0f, 0.25f, 1.0f, 0.5f, 0.0f, 0.5f, 0.5f, false );
		background.setApariencia( BLEND );
		hashtable.put( "Container.Background", background );
	}
	
	public static void Init( GL2 gl ){
		if( hashtable != null )
			return;
		hashtable = new Hashtable<Object, Object>();
		loadFonts( gl, hashtable );
		loadTextures( gl, hashtable );
	}
	
	public static Object get( Object key ){
		return hashtable.get( key );
	}

	public static Background getBackground( Object key ){
        Object value = get(key);
        return (value instanceof Background) ? (Background)value : null;
	}
	
	public static Border getBorder( Object key ){
        Object value = get(key);
        return (value instanceof Border) ? (Border)value : null;
	}

	public static Color4f getColor( Object key ){
        Object value = get(key);
        return (value instanceof Color4f) ? (Color4f)value : null;
	}

	public static TextureFont getFont( Object key ){
        Object value = get(key);
        return (value instanceof TextureFont) ? (TextureFont)value : null;
	}

	public static Insets getInsets( Object key ){
        Object value = get(key);
		return (value instanceof Insets) ? (Insets)value : null;
	}

}
