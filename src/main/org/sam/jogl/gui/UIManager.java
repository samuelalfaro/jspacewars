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
import java.util.ArrayDeque;
import java.util.Hashtable;
import java.util.Queue;

import javax.media.opengl.GL2;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.elementos.Initializable;
import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.Nodo;
import org.sam.jogl.Textura;
import org.sam.jogl.Textura.MagFilter;
import org.sam.jogl.Textura.MinFilter;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

/**
 * Clase encargada de cargar y proporcionar los datos necerarios a los distintos
 * compnentes del GUI.
 * TODO Cargar estos datos a partir de un fichero de recursos. Quitar todo el hard code.
 */
public final class UIManager{
	
	private static final String fontDef          = "resources/texturas/fonts/saved.xml";
	private static final String font1Texture     = "resources/texturas/fonts/saved.png";
	private static final String font2Texture     = "resources/texturas/fonts/saved-blur.png";
	private static final String font3Texture     = "resources/texturas/fonts/saved-neon.png";
	private static final String componentTexture = "resources/bordes_prueba.png";
	
	private static final AtributosTransparencia BLEND_ATT = new AtributosTransparencia( 
		AtributosTransparencia.Equation.ADD,
		AtributosTransparencia.SrcFunc.SRC_ALPHA,
		AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA
	);
	
	private static final AtributosTransparencia ADD_ATT = new AtributosTransparencia( 
		AtributosTransparencia.Equation.ADD,
		AtributosTransparencia.SrcFunc.SRC_ALPHA,
		AtributosTransparencia.DstFunc.ONE
	);
	
	private static final AtributosTransparencia MOD_ATT = new AtributosTransparencia( 
		AtributosTransparencia.Equation.ADD,
		AtributosTransparencia.SrcFunc.ZERO,
		AtributosTransparencia.DstFunc.SRC_COLOR
	);
	
	private static final AtributosTextura ALPHA_MODULATE_ATT = new AtributosTextura();
	
	private static final AtributosTextura ALPHA_INTERPOLATE_ATT = new AtributosTextura();
	
	private static final Apariencia BLEND = new Apariencia();
	
	static{
		
		ALPHA_MODULATE_ATT.setMode( AtributosTextura.Mode.COMBINE );
		ALPHA_MODULATE_ATT.setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
		ALPHA_MODULATE_ATT.setCombineRgbSource0(
				AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
		);
		ALPHA_MODULATE_ATT.setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
		ALPHA_MODULATE_ATT.setCombineAlphaSource0(
				AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_ALPHA
		);
		ALPHA_MODULATE_ATT.setCombineAlphaSource1(
				AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
		);
		
		ALPHA_INTERPOLATE_ATT.setEnvColor( 1.0f, 1.0f, 1.0f, 1.0f );
		ALPHA_INTERPOLATE_ATT.setMode( AtributosTextura.Mode.COMBINE );
		ALPHA_INTERPOLATE_ATT.setCombineRgbMode( AtributosTextura.CombineMode.INTERPOLATE );
		// C' = S0 * S2 + S1*(1-S2)
		ALPHA_INTERPOLATE_ATT.setCombineRgbSource0( AtributosTextura.CombineSrc.OBJECT,
				AtributosTextura.CombineOperand.SRC_COLOR );
		ALPHA_INTERPOLATE_ATT.setCombineRgbSource1( AtributosTextura.CombineSrc.CONSTANT,
				AtributosTextura.CombineOperand.SRC_COLOR );
		ALPHA_INTERPOLATE_ATT.setCombineRgbSource2( AtributosTextura.CombineSrc.TEXTURE,
				AtributosTextura.CombineOperand.SRC_ALPHA );
		
		BLEND.setAtributosTransparencia( BLEND_ATT );
	}
	
	private UIManager(){}
	
	private static final Queue<Initializable> initializables = new ArrayDeque<Initializable>();
	
	private static Hashtable<Object,Object> hashtable = null;
	
	private static void loadFonts( GL2 gl, Hashtable<Object, Object> hashtable ){
		try{
			BufferedImage img;
			Textura textura;
			TextureFont font = new TextureFont( gl, new FileInputStream( fontDef ) );
			
			Apariencia apFont = new Apariencia();
			
			img = Imagen.cargarToBufferedImage( font1Texture );
			textura = new Textura( gl, MinFilter.NEAREST, MagFilter.LINEAR, Textura.Format.ALPHA, img, false );
			textura.setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			textura.setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
			
			apFont.setTextura( textura );
			apFont.setAtributosTextura( ALPHA_MODULATE_ATT );
			apFont.setAtributosTransparencia( BLEND_ATT );
			font.setApariencia( apFont );
			
			hashtable.put( "Font.default", font.deriveFont( .25f ) );
			hashtable.put( "Font.Component.default", font.deriveFont( .5f ) );
			
			img = Imagen.cargarToBufferedImage( font2Texture );
			
			apFont = new Apariencia();
			
			img = Imagen.cargarToBufferedImage( font2Texture );
			textura = new Textura( gl, MinFilter.LINEAR, MagFilter.LINEAR, Textura.Format.ALPHA, img, false );
			textura.setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			textura.setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );

			apFont.setTextura( textura );
			apFont.setAtributosTextura( ALPHA_INTERPOLATE_ATT );
			apFont.setAtributosTransparencia( MOD_ATT );
			
			hashtable.put( "Font.Component.shadow", font.deriveFont( .5f ).deriveFont( apFont ) );
			
			apFont = new Apariencia();
			
			apFont.setTextura( textura );
			apFont.setAtributosTextura( ALPHA_MODULATE_ATT );
			apFont.setAtributosTransparencia( ADD_ATT );
			
			hashtable.put( "Font.Component.fx1", font.deriveFont( .5f ).deriveFont( apFont ) );

			apFont = new Apariencia();
			
			img = Imagen.cargarToBufferedImage( font3Texture );
			textura = new Textura( gl, MinFilter.LINEAR, MagFilter.LINEAR, Textura.Format.ALPHA, img, false );
			textura.setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			textura.setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
			
			apFont.setTextura( textura );
			apFont.setAtributosTextura( ALPHA_MODULATE_ATT );
			apFont.setAtributosTransparencia( ADD_ATT );
			
			hashtable.put( "Font.Component.fx2", font.deriveFont( .5f ).deriveFont( apFont ) );

		}catch( FileNotFoundException e ){
			e.printStackTrace();
		}
	}
	
	private static Nodo generateCursorDecorator( GL2 gl ){
		
		Apariencia ap = new Apariencia();
		
		ap.setTextura( new Textura( gl, Textura.Format.ALPHA, Imagen.cargarToBufferedImage( "resources/texturas/smok.png" ), true ) );
		ap.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER);
		ap.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
		
		ap.setAtributosTextura( new AtributosTextura() );
		
		ap.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
		ap.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
		ap.getAtributosTextura().setCombineRgbSource0(
				AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
		);
		ap.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
		ap.getAtributosTextura().setCombineAlphaSource0(
				AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_ALPHA
		);
		ap.getAtributosTextura().setCombineAlphaSource1(
				AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
		);			
		
		ap.setAtributosTransparencia( 
				new AtributosTransparencia( 
						AtributosTransparencia.Equation.ADD,
						AtributosTransparencia.SrcFunc.SRC_ALPHA,
						AtributosTransparencia.DstFunc.ONE
				) 
		);
		
		FactoriaDeParticulas.setOptimizedFor2D( true );
		
		Particulas estela = FactoriaDeParticulas.createParticulas( 50 );
		
		Matrix4f t1 = new Matrix4f();
		t1.setIdentity();
		t1.rotX( (float)(Math.PI /2) );
		
		Matrix4f t2 = new Matrix4f();
		t2.setIdentity();
		t2.rotZ( (float)(Math.PI /4) );
		t2.mul( t1 );
		
		t1 = new Matrix4f();
		t1.setIdentity();
		t1.setTranslation( new Vector3f( 8, 8, 0 ) );
		t1.mul( t2 );
		
		estela.setEmisor( new Emisor.Cache( new Emisor.Transformador( new Emisor.Puntual( 0.25f, -0.25f, 45 ), t1 ), 256 ) );
		
		estela.setEmision( Particulas.Emision.CONTINUA );
		estela.setRangoDeEmision( 1.0f );
		estela.setVelocidad( 300.0f, 20.0f, false );
		estela.setTiempoVida( 0.25f );
		estela.setGiroInicial( 0, 180, true );
		estela.setVelocidadGiro( 30.0f, 15.0f, true );
		estela.setColor(
				0.16f,
				0.24f,
				0.08f,
				GettersFactory.Float.create( 
					new float[] { 0.25f, 1.0f },
					new float[] { 1.0f, 0.0f },
					MetodoDeInterpolacion.Predefinido.COSENOIDAL
				)
		);
		estela.setRadio( 	
				GettersFactory.Float.create( 
					new float[] { 0.0f, 0.25f },
					new float[] { 0.0f, 8.0f },
					MetodoDeInterpolacion.Predefinido.COSENOIDAL
				)
		);
		estela.init();
		estela.setApariencia( ap );
		
		return estela;
	}
	
	private static void generateBorderAndBackgrounds( GL2 gl, Hashtable<Object, Object> hashtable ){
		
		Border border;
		Background background;
		
		BufferedImage img = Imagen.cargarToBufferedImage( componentTexture );

		Apariencia ap1 = new Apariencia();

		ap1.setTextura( new Textura( gl, Textura.Format.RGBA, img, false ) );
		ap1.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
		ap1.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );

		ap1.setAtributosTextura( new AtributosTextura() );
		ap1.getAtributosTextura().setMode( AtributosTextura.Mode.REPLACE );
		ap1.setAtributosTransparencia( BLEND_ATT );
		
		background = new Background.Textured( 16, 16, 48, 48, 64, 64 );
		background.setApariencia( ap1 );
		hashtable.put( "Container.background.default", background );
		
		border = new Border.Textured( new Insets( 16 ), 64, 64 );
		border.setOuterInsets( new Insets( 4 ) );
		border.setInnerInsets( new Insets( 4 ) );
		border.setApariencia( ap1 );
		
		hashtable.put( "Container.border.default", border );
		
		border = new Border.Gradient(
			new Insets( 5 ), 
			new Color4f( 1.0f, 1.0f, 1.0f, 0.5f ),
			new Color4f( 0.25f, 0.0f, 0.25f, 1.0f ),
			new Color4f( 0.0f, 0.0f, 0.0f, 0.5f )
		);
		border.setApariencia( BLEND );
		hashtable.put( "Border.default", border );
		
		background = new Background.Gradient( 0.25f, 0.0f, 0.25f, 1.0f, 0.5f, 0.0f, 0.5f, 0.5f, false );
		background.setApariencia( BLEND );
		hashtable.put( "Background.default", background );
	}
	
	private static void notifyInitializables(){
		while( !initializables.isEmpty() )
			initializables.remove().init();
	}
	
	public static boolean isInitialized(){
		return hashtable != null;
	}
	
	public static void Init( GL2 gl ){
		if( isInitialized() )
			return;
		hashtable = new Hashtable<Object, Object>();
		
		loadFonts( gl, hashtable );
		generateBorderAndBackgrounds( gl, hashtable );
		hashtable.put( "Cursor.decorator", generateCursorDecorator( gl ) );
		
		hashtable.put( "Color.Text.default", new Color4f( 0.5f, 0.5f, 0.5f, 1.0f ) );
		hashtable.put( "Color.Text.shadow",  new Color4f( 0.1f, 0.0f, 0.2f, 1.0f ) );
		hashtable.put( "Color.Text.fx",      new Color4f( 0.5f, 0.1f, 0.1f, 1.0f ) );
		
		TextRendererProperties properties;
		
		Object nullObject = new Object();
		
		hashtable.put( "Label.background.disabled",  nullObject );
		hashtable.put( "Label.border.disabled",  nullObject );
		properties = new TextRendererProperties();
		properties.shadowFont   = UIManager.getFont(  "Font.Component.shadow" );
		properties.shadowColor  = UIManager.getColor( "Color.Text.shadow" );
		properties.shadowOfsetX = 2.5f;
		properties.shadowOfsetY = 2.5f;
		properties.font         = UIManager.getFont(  "Font.Component.default" );
		properties.color        = UIManager.getColor( "Color.Text.default" );
		hashtable.put( "Label.properties.disabled", properties );
		
		hashtable.put( "Label.background.default",  nullObject );
		hashtable.put( "Label.border.default",  nullObject );
		
		properties = new TextRendererProperties();
		properties.shadowFont   = UIManager.getFont(  "Font.Component.shadow" );
		properties.shadowColor  = UIManager.getColor( "Color.Text.shadow" );
		properties.shadowOfsetX = 2.5f;
		properties.shadowOfsetY = 2.5f;
		properties.font         = UIManager.getFont(  "Font.Component.default" );
		properties.color        = UIManager.getColor( "Color.Text.default" );
		properties.fxFont       = UIManager.getFont(  "Font.Component.fx2" );
		properties.fxColor      = UIManager.getColor( "Color.Text.fx" );
		properties.fxOfsetX     = 0.0f;
		properties.fxOfsetY     = 0.0f;
		hashtable.put( "Label.properties.default", properties );
		
		hashtable.put( "Button.background.disabled",  UIManager.getBackground( "Background.default" ) );
		hashtable.put( "Button.border.disabled",  UIManager.getBorder( "Border.default" ) );
		properties = new TextRendererProperties();
		properties.shadowFont   = UIManager.getFont(  "Font.Component.shadow" );
		properties.shadowColor  = UIManager.getColor( "Color.Text.shadow" );
		properties.shadowOfsetX = 2.5f;
		properties.shadowOfsetY = 2.5f;
		properties.font         = UIManager.getFont(  "Font.Component.default" );
		properties.color        = UIManager.getColor( "Color.Text.default" );
		hashtable.put( "Button.properties.disabled", properties );
		
		hashtable.put( "Button.background.hovered",  UIManager.getBackground( "Background.default" ) );
		hashtable.put( "Button.border.hovered",  UIManager.getBorder( "Border.default" ) );
		properties = new TextRendererProperties();
		properties.shadowFont   = UIManager.getFont(  "Font.Component.fx1" ).deriveFont( 1.25f, 1.20f );
		properties.shadowColor  = new Color4f( 0.5f, 0.5f, 0.1f, 0.5f );
		properties.shadowOfsetX = 0.0f;
		properties.shadowOfsetY = 0.0f;
		properties.font         = UIManager.getFont(  "Font.Component.fx1" ).deriveFont( 1.20f, 1.175f );
		properties.color        = new Color4f( 0.5f, 0.5f, 0.1f, 0.75f );
		properties.fxFont       = UIManager.getFont(  "Font.Component.default" ).deriveFont( 1.15f );
		properties.fxColor      = UIManager.getColor( "Color.Text.default" );
		properties.fxOfsetX     = 0.0f;
		properties.fxOfsetY     = 0.0f;
		hashtable.put( "Button.properties.hovered",  properties );
		
		hashtable.put( "Button.background.focused",  UIManager.getBackground( "Background.default" ) );
		hashtable.put( "Button.border.focused",  UIManager.getBorder( "Border.default" ) );
		properties = new TextRendererProperties();
		properties.shadowFont   = UIManager.getFont(  "Font.Component.shadow" );
		properties.shadowColor  = UIManager.getColor( "Color.Text.shadow" );
		properties.shadowOfsetX = 2.5f;
		properties.shadowOfsetY = 2.5f;
		properties.font         = UIManager.getFont(  "Font.Component.default" );
		properties.color        = UIManager.getColor( "Color.Text.default" );
		properties.fxFont       = UIManager.getFont(  "Font.Component.fx2" );
		properties.fxColor      = UIManager.getColor( "Color.Text.fx" );
		properties.fxOfsetX     = 0.0f;
		properties.fxOfsetY     = 0.0f;
		hashtable.put( "Button.properties.focused",  properties );
		
		hashtable.put( "Button.background.pressed",  UIManager.getBackground( "Background.default" ) );
		hashtable.put( "Button.border.pressed",  UIManager.getBorder( "Border.default" ) );
		properties = new TextRendererProperties();
		properties.shadowFont   = UIManager.getFont(  "Font.Component.shadow" ).deriveFont( 1.05f );
		properties.shadowColor  = UIManager.getColor( "Color.Text.shadow" );
		properties.shadowOfsetX = 2.5f;
		properties.shadowOfsetY = 2.5f;
		properties.font         = UIManager.getFont(  "Font.Component.default" ).deriveFont( 1.05f );
		properties.color        = UIManager.getColor( "Color.Text.default" );
		properties.fxFont       = UIManager.getFont(  "Font.Component.fx2" ).deriveFont( 1.05f );
		properties.fxColor      = new Color4f( 1.0f, 0.5f, 0.1f, 1.0f );
		properties.fxOfsetX     = 0.0f;
		properties.fxOfsetY     = 0.0f;
		hashtable.put( "Button.properties.pressed",  properties );
		
		hashtable.put( "Button.background.default",  UIManager.getBackground( "Background.default" ) );
		hashtable.put( "Button.border.default",  UIManager.getBorder( "Border.default" ) );
		properties = new TextRendererProperties();
		properties.shadowFont   = UIManager.getFont(  "Font.Component.shadow" );
		properties.shadowColor  = UIManager.getColor( "Color.Text.shadow" );
		properties.shadowOfsetX = 2.5f;
		properties.shadowOfsetY = 2.5f;
		properties.font         = UIManager.getFont(  "Font.Component.default" );
		properties.color        = UIManager.getColor( "Color.Text.default" );
		hashtable.put( "Button.properties.default",  properties );
		
		notifyInitializables();
	}
	
	public static void registerInitializable( Initializable i ){
		if( isInitialized() )
			i.init();
		else
			initializables.add( i );
	}
	
	public static Object get( Object key ){
		if( !isInitialized() )
			return null;
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
	
	public static TextRendererProperties getTextRendererProperties( Object key ){
        Object value = get(key);
        return (value instanceof TextRendererProperties) ? (TextRendererProperties)value : null;
	}

	public static Insets getInsets( Object key ){
        Object value = get(key);
		return (value instanceof Insets) ? (Insets)value : null;
	}
	
	public static Nodo getNodo( Object key ){
        Object value = get(key);
		return (value instanceof Nodo) ? (Nodo)value : null;
	}
}
