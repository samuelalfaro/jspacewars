package org.sam.jogl;

import javax.media.opengl.GL;

public class AtributosTransparencia {
	
	public enum SrcFunc{
		ZERO						(GL.GL_ZERO),
		ONE							(GL.GL_ONE),
		SRC_COLOR					(GL.GL_SRC_COLOR),
		ONE_MINUS_SRC_COLOR			(GL.GL_ONE_MINUS_SRC_COLOR),
		DST_COLOR					(GL.GL_DST_COLOR),
		ONE_MINUS_DST_COLOR			(GL.GL_ONE_MINUS_DST_COLOR),
		SRC_ALPHA					(GL.GL_SRC_ALPHA),
		ONE_MINUS_SRC_ALPHA			(GL.GL_ONE_MINUS_SRC_ALPHA),
		DST_ALPHA					(GL.GL_DST_ALPHA),
		ONE_MINUS_DST_ALPHA			(GL.GL_ONE_MINUS_DST_ALPHA),
		CONSTANT_COLOR				(GL.GL_CONSTANT_COLOR),
		ONE_MINUS_CONSTANT_COLOR	(GL.GL_ONE_MINUS_CONSTANT_COLOR),
		CONSTANT_ALPHA				(GL.GL_CONSTANT_ALPHA),
		ONE_MINUS_CONSTANT_ALPHA	(GL.GL_ONE_MINUS_CONSTANT_ALPHA),
		ALPHA_SATURATE				(GL.GL_SRC_ALPHA_SATURATE);
		
		private final int value;
		
		SrcFunc(int value){
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	public enum DstFunc{
		ZERO						(GL.GL_ZERO),
		ONE							(GL.GL_ONE),
		SRC_COLOR					(GL.GL_SRC_COLOR),
		ONE_MINUS_SRC_COLOR			(GL.GL_ONE_MINUS_SRC_COLOR),
		DST_COLOR					(GL.GL_DST_COLOR),
		ONE_MINUS_DST_COLOR			(GL.GL_ONE_MINUS_DST_COLOR),
		SRC_ALPHA					(GL.GL_SRC_ALPHA),
		ONE_MINUS_SRC_ALPHA			(GL.GL_ONE_MINUS_SRC_ALPHA),
		DST_ALPHA					(GL.GL_DST_ALPHA),
		ONE_MINUS_DST_ALPHA			(GL.GL_ONE_MINUS_DST_ALPHA),
		CONSTANT_COLOR				(GL.GL_CONSTANT_COLOR),
		ONE_MINUS_CONSTANT_COLOR	(GL.GL_ONE_MINUS_CONSTANT_COLOR),
		CONSTANT_ALPHA				(GL.GL_CONSTANT_ALPHA),
		ONE_MINUS_CONSTANT_ALPHA	(GL.GL_ONE_MINUS_CONSTANT_ALPHA);
		
		private final int value;
		
		DstFunc(int value){
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}

	private static AtributosTransparencia anterior;
	
	public void activar(GL gl){
		if( anterior == this )
			return;
		anterior = this;
	}
	
	public static void desactivar(GL gl){
		if( anterior == null )
			return;
		anterior = null;
	}
}
