package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase que conteniene todos los atributos relaccionados con el formato de fusión de elementos
 * semitransparentes, para poder gestinarlos a través de la {@code Apariencia}
 * que los contiene.<br/>
 */
public class AtributosTransparencia {
	
	/**
	 * Enumeración que contiene las distintas funciones, que pueden
	 * emplearse para la fusión de elementos semitransparentes.
	 */
	public enum Equation{
		/**
		 * Encapsula el valor           GL_FUNC_ADD.
		 */
		ADD							(GL.GL_FUNC_ADD),
		/**
		 * Encapsula el valor           GL_FUNC_REVERSE_SUBTRACT.
		 */
		REVERSE_SUBTRACT			(GL.GL_FUNC_REVERSE_SUBTRACT),
		/**
		 * Encapsula el valor           GL_FUNC_SUBTRACT.
		 */
		SUBTRACT					(GL.GL_FUNC_SUBTRACT);
		
		private final int value;
		
		private Equation(int value){
			this.value = value;
		}
	}
	
	/**
	 * Enumeración que contiene los distintas operaciones que se pueden
	 * aplicar al orígen que emplea la funcion de fusión.
	 */
	public enum SrcFunc{
		/**
		 * Encapsula el valor           GL_ZERO.
		 */
		ZERO						(GL.GL_ZERO),
		/**
		 * Encapsula el valor           GL_ONE.
		 */
		ONE							(GL.GL_ONE),
		/**
		 * Encapsula el valor           GL_SRC_COLOR.
		 */
		SRC_COLOR					(GL.GL_SRC_COLOR),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_SRC_COLOR.
		 */
		ONE_MINUS_SRC_COLOR			(GL.GL_ONE_MINUS_SRC_COLOR),
		/**
		 * Encapsula el valor           GL_DST_COLOR.
		 */
		DST_COLOR					(GL.GL_DST_COLOR),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_DST_COLOR.
		 */
		ONE_MINUS_DST_COLOR			(GL.GL_ONE_MINUS_DST_COLOR),
		/**
		 * Encapsula el valor           GL_SRC_ALPHA.
		 */
		SRC_ALPHA					(GL.GL_SRC_ALPHA),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_SRC_ALPHA.
		 */
		ONE_MINUS_SRC_ALPHA			(GL.GL_ONE_MINUS_SRC_ALPHA),
		/**
		 * Encapsula el valor           GL_DST_ALPHA.
		 */
		DST_ALPHA					(GL.GL_DST_ALPHA),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_DST_ALPHA.
		 */
		ONE_MINUS_DST_ALPHA			(GL.GL_ONE_MINUS_DST_ALPHA),
		/**
		 * Encapsula el valor           GL_CONSTANT_COLOR.
		 */
		CONSTANT_COLOR				(GL.GL_CONSTANT_COLOR),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_CONSTANT_COLOR.
		 */
		ONE_MINUS_CONSTANT_COLOR	(GL.GL_ONE_MINUS_CONSTANT_COLOR),
		/**
		 * Encapsula el valor           GL_CONSTANT_ALPHA.
		 */
		CONSTANT_ALPHA				(GL.GL_CONSTANT_ALPHA),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_CONSTANT_ALPHA.
		 */
		ONE_MINUS_CONSTANT_ALPHA	(GL.GL_ONE_MINUS_CONSTANT_ALPHA),
		/**
		 * Encapsula el valor           GL_SRC_ALPHA_SATURATE.
		 */
		ALPHA_SATURATE				(GL.GL_SRC_ALPHA_SATURATE);
		
		private final int value;
		
		private SrcFunc(int value){
			this.value = value;
		}
	}
	
	/**
	 * Enumeración que contiene los distintas operaciones que se pueden
	 * aplicar al destino que emplea la funcion de fusión.
	 */
	public enum DstFunc{
		/**
		 * Encapsula el valor           GL_ZERO.
		 */
		ZERO						(GL.GL_ZERO),
		/**
		 * Encapsula el valor           GL_ONE.
		 */
		ONE							(GL.GL_ONE),
		/**
		 * Encapsula el valor           GL_SRC_COLOR.
		 */
		SRC_COLOR					(GL.GL_SRC_COLOR),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_SRC_COLOR.
		 */
		ONE_MINUS_SRC_COLOR			(GL.GL_ONE_MINUS_SRC_COLOR),
		/**
		 * Encapsula el valor           GL_DST_COLOR.
		 */
		DST_COLOR					(GL.GL_DST_COLOR),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_DST_COLOR.
		 */
		ONE_MINUS_DST_COLOR			(GL.GL_ONE_MINUS_DST_COLOR),
		/**
		 * Encapsula el valor           GL_SRC_ALPHA.
		 */
		SRC_ALPHA					(GL.GL_SRC_ALPHA),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_SRC_ALPHA.
		 */
		ONE_MINUS_SRC_ALPHA			(GL.GL_ONE_MINUS_SRC_ALPHA),
		/**
		 * Encapsula el valor           GL_DST_ALPHA.
		 */
		DST_ALPHA					(GL.GL_DST_ALPHA),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_DST_ALPHA.
		 */
		ONE_MINUS_DST_ALPHA			(GL.GL_ONE_MINUS_DST_ALPHA),
		/**
		 * Encapsula el valor           GL_CONSTANT_COLOR.
		 */
		CONSTANT_COLOR				(GL.GL_CONSTANT_COLOR),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_CONSTANT_COLOR.
		 */
		ONE_MINUS_CONSTANT_COLOR	(GL.GL_ONE_MINUS_CONSTANT_COLOR),
		/**
		 * Encapsula el valor           GL_CONSTANT_ALPHA.
		 */
		CONSTANT_ALPHA				(GL.GL_CONSTANT_ALPHA),
		/**
		 * Encapsula el valor           GL_ONE_MINUS_CONSTANT_ALPHA.
		 */
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
	
	/**
	 *  Constructor que crea los {@code AtributosTransparencia} con los parámetros que recibe.
	 *  
	 * @param equation {@code Equation} de estos {@code AtributosTransparencia}.
	 * @param srcFunc  {@code SrcFunc}  de estos {@code AtributosTransparencia}.
	 * @param dstFunc  {@code DstFunc}  de estos {@code AtributosTransparencia}.
	 */
	public AtributosTransparencia(Equation	equation, SrcFunc srcFunc, DstFunc dstFunc){
		this.equation = equation;
		this.srcFunc  = srcFunc;
		this.dstFunc  = dstFunc;
	}
	
	/**
	 * Método que activa la fusión de elementos semitransparentes con los valores de
	 * estos {@code AtributosTransparencia}.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
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
	
	/**
	 * Método estático que desactiva la fusión de elementos semitransparentes.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public static void desactivar(GL gl){
		if( anterior == null )
			return;
		gl.glDepthMask(true);
//		gl.glEnable(GL.GL_CULL_FACE);//TODO cambiar a AtributosRender
		gl.glDisable(GL.GL_BLEND);
		anterior = null;
	}
}
