/* 
 * TextUtils.java
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
package pruebas.jogl;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.media.opengl.GL2;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.Textura;
import org.sam.jogl.gui.GLTextRenderer;
import org.sam.jogl.gui.GLTextRenderer.HorizontalAlignment;
import org.sam.jogl.gui.GLTextRenderer.VerticalAlignment;
import org.sam.jogl.gui.TextureFont;
import org.sam.util.Imagen;

public class TextUtils{

	private final static String fontDef      = "resources/texturas/fonts/arbeka.xml";
	private final static String font1Texture = "resources/texturas/fonts/arbeka.png";

	public static GLTextRenderer getDefaultTextRenderer( GL2 gl ){

		GLTextRenderer renderer = new GLTextRenderer();
		
		renderer.setHorizontalAlignment( HorizontalAlignment.LEFT );
		renderer.setVerticalAlignment( VerticalAlignment.TOP );
		renderer.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
		
		try{
			TextureFont font = new TextureFont( gl, new FileInputStream( fontDef ) );
			font = font.deriveFont( 0.3f );

			BufferedImage img = Imagen.cargarToBufferedImage( font1Texture );

			Apariencia apFont = new Apariencia();

			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );

			apFont.setAtributosTextura( new AtributosTextura() );

			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0( AtributosTextura.CombineSrc.OBJECT,
					AtributosTextura.CombineOperand.SRC_COLOR );
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			apFont.getAtributosTextura().setCombineAlphaSource0( AtributosTextura.CombineSrc.OBJECT,
					AtributosTextura.CombineOperand.SRC_ALPHA );
			apFont.getAtributosTextura().setCombineAlphaSource1( AtributosTextura.CombineSrc.TEXTURE,
					AtributosTextura.CombineOperand.SRC_ALPHA );
			
			apFont.setAtributosTransparencia( new AtributosTransparencia( AtributosTransparencia.Equation.ADD,
					AtributosTransparencia.SrcFunc.SRC_ALPHA, AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA ) );
			
			font.setApariencia( apFont );

			renderer.setFont( font );

		}catch( FileNotFoundException e ){
			e.printStackTrace();
		}
		
		return renderer;
	}
}
