package org.sam.jogl;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;

import org.sam.util.Imagen;

public class Textura{
	
	public static class Util{
		
		private Util(){
		}

		public static ByteBuffer toByteBuffer(BufferedImage img, Format format, boolean flipY){
			
			int nBands = 1;
			if(format == Format.RGB)
				nBands = 3;
			else if(format == Format.RGBA)
				nBands = 4;
		
			ByteBuffer bb = ByteBuffer.allocateDirect( img.getWidth() * img.getHeight() * nBands );
			
			switch(nBands){
			case 1:
				if(!flipY){
					for(int y = 0; y < img.getHeight(); y++)
						for(int x = 0; x < img.getWidth(); x++)
							bb.put( Imagen.rgb2luminance(img.getRGB(x, y) ) );
				}else{
					for(int y = img.getHeight()-1; y >= 0; y--)
						for(int x = 0; x < img.getWidth(); x++)
							bb.put( Imagen.rgb2luminance(img.getRGB(x, y) ) );
				}
				break;
			case 3:
				if(!flipY){
					for(int y = 0; y < img.getHeight(); y++)
						for(int x = 0; x < img.getWidth(); x++){
							int pixel = img.getRGB(x, y);
							bb.put( (byte)( pixel>>16 & 0xFF) );
							bb.put( (byte)( pixel>>8 & 0xFF) );
							bb.put( (byte)( pixel & 0xFF) );
						}
				}else{
					for(int y = img.getHeight()-1; y >= 0; y--)
						for(int x = 0; x < img.getWidth(); x++){
							int pixel = img.getRGB(x, y);
							bb.put( (byte)( pixel>>16 & 0xFF) );
							bb.put( (byte)( pixel>>8 & 0xFF) );
							bb.put( (byte)( pixel & 0xFF) );
						}
				}
				break;
			case 4:
				if(!flipY){
					for(int y = 0; y < img.getHeight(); y++)
						for(int x = 0; x < img.getWidth(); x++){
							int pixel = img.getRGB(x, y);
							bb.put( (byte)( pixel>>16 & 0xFF) );
							bb.put( (byte)( pixel>>8 & 0xFF) );
							bb.put( (byte)( pixel & 0xFF) );
							bb.put( (byte)( pixel>>24 & 0xFF) );
						}
				}else{
					for(int y = img.getHeight()-1; y >= 0; y--)
						for(int x = 0; x < img.getWidth(); x++){
							int pixel = img.getRGB(x, y);
							bb.put( (byte)( pixel>>16 & 0xFF) );
							bb.put( (byte)( pixel>>8 & 0xFF) );
							bb.put( (byte)( pixel & 0xFF) );
							bb.put( (byte)( pixel>>24 & 0xFF) );
						}
				}
				break;
			}
			bb.rewind();
			return bb;
		}
	}
	
	public enum MinFilter{
		NEAREST		(GL.GL_NEAREST),
		LINEAR		(GL.GL_LINEAR),
		MIPMAP		(GL.GL_LINEAR_MIPMAP_NEAREST);

		private final int value;
		private MinFilter(int value){
			this.value = value;
		}
	}
	
	public enum MagFilter{
		NEAREST		(GL.GL_NEAREST),
		LINEAR		(GL.GL_LINEAR);

		private final int value;
		private MagFilter(int value){
			this.value = value;
		}
	}
	
	public enum Wrap{
		CLAMP				(GL.GL_CLAMP),
		REPEAT				(GL.GL_REPEAT),
		CLAMP_TO_EDGE		(GL.GL_CLAMP_TO_EDGE),
		CLAMP_TO_BORDER		(GL.GL_CLAMP_TO_BORDER);

		private final int value;
		private Wrap(int value){
			this.value = value;
		}
	}
	
	public enum Format{
		LUMINANCE	(GL.GL_LUMINANCE),
		ALPHA		(GL.GL_ALPHA),
		
		RGB			(GL.GL_RGB),
		RGBA		(GL.GL_RGBA);
		
		private final int value;
		private Format(int value){
			this.value = value;
		}
	}
	
	private transient final int texId;
	private transient final float proporciones;
	private Wrap wrapS;
	private Wrap wrapT;
	
	public Textura(GL gl, Format format, BufferedImage image, boolean flipY){
		this(gl, MinFilter.LINEAR, MagFilter.LINEAR, format, image, flipY);
	}

	public Textura(GL gl, MinFilter minFilter, MagFilter magFilter, Format format, BufferedImage image, boolean flipY){
		this(gl, minFilter, magFilter, format, image.getWidth(null), image.getHeight(null),
				Util.toByteBuffer( image, format, flipY)
		);
	}

	public Textura(GL gl, MinFilter minFilter, MagFilter magFilter, Format format, int width, int  height, Buffer pixels){

		proporciones = (float)width / height;

		int tmp[] = new int[1];
		gl.glGenTextures(1, tmp, 0);
		texId= tmp[0];
		gl.glBindTexture(GL.GL_TEXTURE_2D, texId); 
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, minFilter.value);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, magFilter.value);
		
		gl.glTexImage2D( GL.GL_TEXTURE_2D, 0,
				format.value,
				width,
				height,
				0,
				format.value, GL.GL_UNSIGNED_BYTE,
				pixels);
		wrapS = Wrap.REPEAT;
		wrapT = Wrap.REPEAT;
	}
	
	public void setWrap_s(Wrap wrapS) {
		this.wrapS = wrapS;
	}

	public void setWrap_t(Wrap wrapT) {
		this.wrapT = wrapT;
	}

	public float getProporciones(){
		return proporciones;
	}
	
	public void activar(GL gl, Textura anterior) {
		if(anterior == null)
			gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texId);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrapS.value);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrapT.value);
	}
	
	public void desactivar(GL gl) {
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
}
