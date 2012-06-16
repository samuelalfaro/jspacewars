/* 
 * Textura.java
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
package org.sam.jogl;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

/**
 * Clase que encapsula los datos necesarios en el manejo de texturas y proporciona los métodos para facilitar su empleo.
 */
public class Textura{

	/**
	 * Clase proporciona métodos estáticos útiles para el manejo de texturas.
	 */
	public static class Util{

		private Util(){
		}

		private static byte rgb2luminance( int pixel ){
			int r = pixel >> 16 & 0xFF;
			int g = pixel >> 8 & 0xFF;
			int b = pixel & 0xFF;
			return (byte)( 0.299f * r + 0.587f * g + 0.114f * b );
		}

		private static transient final byte[] RGB = new byte[3];

		private static byte[] rgb2ByteArray( int pixel ){
			RGB[0] = (byte)( pixel >> 16 & 0xFF );
			RGB[1] = (byte)( pixel >> 8 & 0xFF );
			RGB[2] = (byte)( pixel & 0xFF );
			return RGB;
		}

		private static transient final byte[] RGBA = new byte[4];

		private static byte[] rgba2ByteArray( int pixel ){
			RGBA[0] = (byte)( pixel >> 16 & 0xFF );
			RGBA[1] = (byte)( pixel >> 8 & 0xFF );
			RGBA[2] = (byte)( pixel & 0xFF );
			RGBA[3] = (byte)( pixel >> 24 & 0xFF );
			return RGBA;
		}

		/**
		 * Método que convierte una {@code BufferedImage} en un {@code ByteBuffer} que puede emplearse para crear una
		 * {@code Textura}.
		 * 
		 * @param img
		 *            {@code BufferedImage} que será convertida.
		 * @param format
		 *            {@code Format} que tendrá la textura para la que se genera el {@code ByteBuffer}.
		 * @param flipY
		 *            Booleano que indica si deben reflejarse verticalmente los pixels de la imagen.
		 * @return El {@code ByteBuffer} generado.
		 */
		public static ByteBuffer toByteBuffer( BufferedImage img, Format format, boolean flipY ){

			int nBands = 1;
			if( format == Format.RGB )
				nBands = 3;
			else if( format == Format.RGBA )
				nBands = 4;

			ByteBuffer bb = ByteBuffer.allocateDirect( img.getWidth() * img.getHeight() * nBands );

			switch( nBands ){
			case 1:
				if( flipY ){
					for( int y = img.getHeight() - 1; y >= 0; y-- )
						for( int x = 0; x < img.getWidth(); x++ )
							bb.put( rgb2luminance( img.getRGB( x, y ) ) );
				}else{
					for( int y = 0; y < img.getHeight(); y++ )
						for( int x = 0; x < img.getWidth(); x++ )
							bb.put( rgb2luminance( img.getRGB( x, y ) ) );
				}
				break;
			case 3:
				if( flipY ){
					for( int y = img.getHeight() - 1; y >= 0; y-- )
						for( int x = 0; x < img.getWidth(); x++ )
							bb.put( rgb2ByteArray( img.getRGB( x, y ) ) );
				}else{
					for( int y = 0; y < img.getHeight(); y++ )
						for( int x = 0; x < img.getWidth(); x++ )
							bb.put( rgb2ByteArray( img.getRGB( x, y ) ) );
				}
				break;
			case 4:
				if( flipY ){
					for( int y = img.getHeight() - 1; y >= 0; y-- )
						for( int x = 0; x < img.getWidth(); x++ )
							bb.put( rgba2ByteArray( img.getRGB( x, y ) ) );
				}else{
					for( int y = 0; y < img.getHeight(); y++ )
						for( int x = 0; x < img.getWidth(); x++ )
							bb.put( rgba2ByteArray( img.getRGB( x, y ) ) );
				}
				break;
			}
			bb.rewind();
			return bb;
		}
	}

	/**
	 * Enumeración que contiene los valores que describen los distintos de modos <i>MinFilter</i> soportados.
	 */
	public enum MinFilter{
		/** Encapsula el valor GL_NEAREST. */
		NEAREST( GL.GL_NEAREST ),
		/** Encapsula el valor GL_LINEAR.*/
		LINEAR( GL.GL_LINEAR ),
		/** Encapsula el valor GL_LINEAR_MIPMAP_NEAREST. */
		MIPMAP( GL.GL_LINEAR_MIPMAP_NEAREST );

		/** Entero que almacena el valor encapsulado.*/
		final int value;

		private MinFilter( int value ){
			this.value = value;
		}
	}

	/**
	 * Enumeración que contiene los valores que describen los distintos de modos <i>MagFilter</i> soportados.
	 */
	public enum MagFilter{
		/**
		 * Encapsula el valor GL_NEAREST.
		 */
		NEAREST( GL.GL_NEAREST ),
		/**
		 * Encapsula el valor GL_LINEAR.
		 */
		LINEAR( GL.GL_LINEAR );

		/** Entero que almacena el valor encapsulado.*/
		final int value;

		private MagFilter( int value ){
			this.value = value;
		}
	}

	/**
	 * Enumeración que contiene los valores que describen los distintos de modos <i>Wrap</i> soportados.
	 */
	public enum Wrap{

		/** Encapsula el valor GL_REPEAT. */
		REPEAT( GL.GL_REPEAT ),
		/** Encapsula el valor GL_MIRRORED_REPEAT. */
		MIRRORED_REPEAT( GL.GL_MIRRORED_REPEAT),
		/** Encapsula el valor GL_CLAMP_TO_EDGE. */
		CLAMP_TO_EDGE( GL.GL_CLAMP_TO_EDGE ),
		/** Encapsula el valor GL_CLAMP. */
		CLAMP( GL2.GL_CLAMP ),
		/** Encapsula el valor GL_CLAMP_TO_BORDER. */
		CLAMP_TO_BORDER( GL2GL3.GL_CLAMP_TO_BORDER );
		
		/** Entero que almacena el valor encapsulado.*/
		final int value;

		private Wrap( int value ){
			this.value = value;
		}
	}

	/**
	 * Enumeración que contiene los valores que describen los distintos de formatos de textura soportados.
	 */
	public enum Format{
		/**
		 * Encapsula el valor GL_LUMINANCE.
		 */
		LUMINANCE( GL.GL_LUMINANCE ),
		/**
		 * Encapsula el valor GL_ALPHA.
		 */
		ALPHA( GL.GL_ALPHA ),
		/**
		 * Encapsula el valor GL_RGB.
		 */
		RGB( GL.GL_RGB ),
		/**
		 * Encapsula el valor GL_RGBA.
		 */
		RGBA( GL.GL_RGBA );

		/** Entero que almacena el valor encapsulado.*/
		final int value;

		private Format( int value ){
			this.value = value;
		}
	}

	private transient final int texId;
	private transient final float proporciones;
	private Wrap wrapS;
	private Wrap wrapT;

	/**
	 * Constructor que genera una {@code Textura} a partir de los datos indicados.
	 * 
	 * @param gl
	 *            Contexto gráfico empleado para almacenar los pixels en memoria de vídeo.
	 * @param format
	 *            {@code Format} de la {@code Textura} generada.
	 * @param image
	 *            {@code BufferedImage} que contiene los pixels de la {@code Textura} generada.
	 * @param flipY
	 *            Booleano que indica si deben reflejarse verticalmente los pixels de la imagen.
	 */
	public Textura( GL2 gl, Format format, BufferedImage image, boolean flipY ){
		this( gl, MinFilter.LINEAR, MagFilter.LINEAR, format, image, flipY );
	}

	/**
	 * Constructor que genera una {@code Textura} a partir de los datos indicados.
	 * 
	 * @param gl
	 *            Contexto gráfico empleado para almacenar los pixels en memoria de vídeo.
	 * @param minFilter
	 *            {@code MinFilter} de la {@code Textura} generada.
	 * @param magFilter
	 *            {@code MagFilter} de la {@code Textura} generada.
	 * @param format
	 *            {@code Format} de la {@code Textura} generada.
	 * @param image
	 *            {@code BufferedImage} que contiene los pixels de la {@code Textura} generada.
	 * @param flipY
	 *            Booleano que indica si deben reflejarse verticalmente los pixels de la imagen.
	 */
	public Textura( GL2 gl, MinFilter minFilter, MagFilter magFilter, Format format, BufferedImage image, boolean flipY ){
		this( gl, minFilter, magFilter, format, image.getWidth( null ), image.getHeight( null ), Util.toByteBuffer(
				image, format, flipY ) );
	}

	/**
	 * Constructor que genera una {@code Textura} a partir de los datos indicados.
	 * 
	 * @param gl
	 *            Contexto gráfico empleado para almacenar los pixels en memoria de vídeo.
	 * @param minFilter
	 *            {@code MinFilter} de la {@code Textura} generada.
	 * @param magFilter
	 *            {@code MagFilter} de la {@code Textura} generada.
	 * @param format
	 *            {@code Format} de la {@code Textura} generada.
	 * @param width
	 *            Anchura de la matriz de pixels.
	 * @param height
	 *            Altura la matriz de pixels.
	 * @param pixels
	 *            {@code Buffer} que contiene los pixels de la {@code Textura} generada.
	 */
	public Textura( GL2 gl, MinFilter minFilter, MagFilter magFilter, Format format, int width, int height, Buffer pixels ){

		proporciones = (float)width / height;

		int tmp[] = new int[1];
		gl.glGenTextures( 1, tmp, 0 );
		texId = tmp[0];
		gl.glBindTexture( GL.GL_TEXTURE_2D, texId );
		gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, minFilter.value );
		gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, magFilter.value );

		gl.glTexImage2D( GL.GL_TEXTURE_2D, 0, format.value, width, height, 0, format.value, GL.GL_UNSIGNED_BYTE, pixels );
		wrapS = Wrap.REPEAT;
		wrapT = Wrap.REPEAT;
	}

	/**
	 * <i>Setter</i> que asigna el valor {@code Wrap} en direccion {@code S} a esta {@code Textura}.
	 * 
	 * @param wrapS
	 *            El valor {@code Wrap} asignado.
	 */
	public void setWrap_s( Wrap wrapS ){
		this.wrapS = wrapS;
	}

	/**
	 * <i>Setter</i> que asigna el valor {@code Wrap} en direccion {@code T} a esta {@code Textura}.
	 * 
	 * @param wrapT
	 *            El valor {@code Wrap} asignado.
	 */
	public void setWrap_t( Wrap wrapT ){
		this.wrapT = wrapT;
	}

	/**
	 * <i>Getter</i> que devuelve las proporciones de la imagen contenida en esta {@code Textura}.
	 * 
	 * @return Las proporciones de la imagen contenida.
	 */
	public float getProporciones(){
		return proporciones;
	}

	/**
	 * Método que activa la aplicación de texturas con los valores de esta {@code Textura}.<br/>
	 * Este método compara la {@code Textura} anteriormente usada para, activar o desactivar los atributos
	 * correspondientes.
	 * 
	 * @param gl
	 *            Contexto gráfico en el que se realiza a acción.
	 * @param anterior
	 *            {@code Textura} anteriormente usada.
	 */
	public void activar( GL2 gl, Textura anterior ){
		if( anterior == null )
			gl.glEnable( GL.GL_TEXTURE_2D );
		gl.glBindTexture( GL.GL_TEXTURE_2D, texId );
		gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrapS.value );
		gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrapT.value );
	}

	/**
	 * Método que desactiva la aplicación de textura.
	 * 
	 * @param gl
	 *            Contexto gráfico en el que se realiza a acción.
	 */
	public static void desactivar( GL2 gl ){
		gl.glDisable( GL.GL_TEXTURE_2D );
	}
}
