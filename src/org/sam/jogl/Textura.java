package org.sam.jogl;

import java.awt.Image;
import java.nio.Buffer;

import javax.media.opengl.GL;

import org.sam.util.Imagen;

public class Textura{
	
	public static final int NEAREST = GL.GL_NEAREST;
	public static final int LINEAR = GL.GL_LINEAR;
	public static final int MIPMAP = GL.GL_LINEAR_MIPMAP_NEAREST;
	
    public static final int CLAMP  = GL.GL_CLAMP;
    public static final int REPEAT = GL.GL_REPEAT;
    public static final int CLAMP_TO_EDGE = GL.GL_CLAMP_TO_EDGE;
    public static final int CLAMP_TO_BORDER = GL.GL_CLAMP_TO_BORDER;
	
	public static final int LUMINANCE = GL.GL_LUMINANCE;
	public static final int ALPHA = GL.GL_ALPHA;
	public static final int INTENSITY = GL.GL_INTENSITY;
	
	public static final int RGB = GL.GL_RGB;
	public static final int RGBA = GL.GL_RGBA;
	
	private final int texId;
	private int wrap_s;
	private int wrap_t;
	private final float proporciones;
	
	public Textura(GL gl, int minFilter, int magFilter, int format, int width, int  height, Buffer pixels){

		switch(minFilter){
		case NEAREST:
		case LINEAR:
		case MIPMAP:
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		switch(magFilter){
		case NEAREST:
		case LINEAR:
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		proporciones = (float)width / height;

		int tmp[] = new int[1];
		gl.glGenTextures(1, tmp, 0);
		texId= tmp[0];
		gl.glBindTexture(GL.GL_TEXTURE_2D, texId); 
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, minFilter);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, magFilter);
		
		if(format == LUMINANCE || format == ALPHA || format == INTENSITY)
			gl.glTexImage2D( GL.GL_TEXTURE_2D, 0,
					format,
					width,
					height,
					0,
					format, GL.GL_UNSIGNED_BYTE,
					pixels);
		else if(format == RGB || format == RGBA)
			gl.glTexImage2D( GL.GL_TEXTURE_2D, 0,
					format,
					width,
					height,
					0,
					GL.GL_BGRA, GL.GL_UNSIGNED_INT_8_8_8_8_REV,
					pixels);
		else
			throw new IllegalArgumentException("Formato: "+format+" no soportado");
		
		wrap_s = REPEAT;
		wrap_t = REPEAT;
	}
	
	public Textura(GL gl, int format, Image image, boolean flipY){
		this(gl, LINEAR, LINEAR, format, image.getWidth(null), image.getHeight(null),
				(format == LUMINANCE || format == ALPHA || format == INTENSITY)?
						Imagen.toByteBuffer(image, flipY): 
						Imagen.toBuffer(image, flipY)
		);
	}
	
	public void setWrap_s(int wrap_s) {
		switch(wrap_s){
		case CLAMP:
		case REPEAT:
		case CLAMP_TO_EDGE:
		case CLAMP_TO_BORDER:
			break;
		default:
			throw new IllegalArgumentException();
		}
			
		this.wrap_s = wrap_s;
	}

	public void setWrap_t(int wrap_t) {
		switch(wrap_t){
		case CLAMP:
		case REPEAT:
		case CLAMP_TO_EDGE:
		case CLAMP_TO_BORDER:
			break;
		default:
			throw new IllegalArgumentException();
		}

		this.wrap_t = wrap_t;
	}

	public float getProporciones(){
		return proporciones;
	}
	
	public void activar(GL gl, Textura anterior) {
		if(anterior == null)
			gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texId);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrap_s);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrap_t);
	}
	
	public void desactivar(GL gl) {
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
}
