package org.sam.jogl;

import java.awt.Image;
import java.nio.Buffer;

import javax.media.opengl.GL;

import org.sam.util.Imagen;

public class Textura{
	
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
		INTENSITY	(GL.GL_INTENSITY),
		
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
	
	public Textura(GL gl, Format format, Image image, boolean flipY){
		this(gl, MinFilter.LINEAR, MagFilter.LINEAR, format, image, flipY);
	}

	public Textura(GL gl, MinFilter minFilter, MagFilter magFilter, Format format, Image image, boolean flipY){
		this(gl, minFilter, magFilter, format, image.getWidth(null), image.getHeight(null),
				(format == Format.LUMINANCE || format == Format.ALPHA || format == Format.INTENSITY)?
						Imagen.toByteBuffer(image, flipY): 
						Imagen.toBuffer(image, flipY)
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
		
		if(format == Format.LUMINANCE || format == Format.ALPHA || format == Format.INTENSITY)
			gl.glTexImage2D( GL.GL_TEXTURE_2D, 0,
					format.value,
					width,
					height,
					0,
					format.value, GL.GL_UNSIGNED_BYTE,
					pixels);
		else if(format == Format.RGB || format == Format.RGBA)
			gl.glTexImage2D( GL.GL_TEXTURE_2D, 0,
					format.value,
					width,
					height,
					0,
					GL.GL_BGRA, GL.GL_UNSIGNED_INT_8_8_8_8_REV,
					pixels);
		else
			throw new IllegalArgumentException("Formato: "+format+" no soportado");
		
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
