package org.sam.jogl;

import javax.media.opengl.GL;

public class AtributosTransparencia {
	
	public enum Equation{
		ADD							(GL.GL_FUNC_ADD),
		REVERSE_SUBTRACT			(GL.GL_FUNC_REVERSE_SUBTRACT),
		SUBTRACT					(GL.GL_FUNC_SUBTRACT);
		
		private final int value;
		
		private Equation(int value){
			this.value = value;
		}
	}
	
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
		
		private SrcFunc(int value){
			this.value = value;
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
		
		private DstFunc(int value){
			this.value = value;
		}
	}

	private static AtributosTransparencia anterior;
	
	private final Equation	equation;
	private final SrcFunc	srcFunc;
	private final DstFunc	dstFunc;
	
	public AtributosTransparencia(Equation	equation, SrcFunc srcFunc, DstFunc dstFunc){
		this.equation = equation;
		this.srcFunc  = srcFunc;
		this.dstFunc  = dstFunc;
	}
	
	public void activar(GL gl){
		if( anterior == this )
			return;
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendEquation(equation.value);
		gl.glBlendFunc(srcFunc.value, dstFunc.value);
//		gl.glDisable(GL.GL_CULL_FACE);//TODO cambiar a AtributosRender
		gl.glDepthMask(false);
		anterior = this;
	}
	
	public static void desactivar(GL gl){
		if( anterior == null )
			return;
		gl.glDepthMask(true);
//		gl.glEnable(GL.GL_CULL_FACE);//TODO cambiar a AtributosRender
		gl.glDisable(GL.GL_BLEND);
		anterior = null;
	}
}
