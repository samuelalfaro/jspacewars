package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase que contiene los atributos de combinación y correcion de perspectiva empleados
 * al aplicar una {@code Textura}.
 */
public class AtributosTextura {

	/**
	 * Enumeración que contiene los distintos modos de aplicar la textura.
	 */
	public enum Mode{
		/**
		 * Encapsula el valor GL_MODULATE.
		 */
		MODULATE	      (GL.GL_MODULATE),
		/**
		 * Encapsula el valor GL_DECAL.
		 */
		DECAL		      (GL.GL_DECAL),
		/**
		 * Encapsula el valor GL_BLEND.
		 */
		BLEND		      (GL.GL_BLEND),
		/**
		 * Encapsula el valor GL_REPLACE.
		 */
		REPLACE		      (GL.GL_REPLACE),
		/**
		 * Encapsula el valor GL_COMBINE.
		 */
		COMBINE		      (GL.GL_COMBINE);

		private final int value;
		private Mode(int value){
			this.value = value;
		}
	}
	
	/**
	 * Enumeración que contiene los distintos modos de combinación,
	 * de los componentes de la textura, tanto RGB como Alpha.<br/>
	 * Estos métodos de combinación son empleados cuando el modo de aplicación
	 * de textura elegido es {@link AtributosTextura.Mode#COMBINE}.
	 */
	public enum CombineMode{
		/**
		 * Encapsula el valor GL_REPLACE.
		 */
		REPLACE		      (GL.GL_REPLACE),
		/**
		 * Encapsula el valor GL_MODULATE.
		 */
		MODULATE	      (GL.GL_MODULATE),
		/**
		 * Encapsula el valor GL_ADD.
		 */
		ADD			      (GL.GL_ADD),
		/**
		 * Encapsula el valor GL_ADD_SIGNED.
		 */
		ADD_SIGNED	      (GL.GL_ADD_SIGNED),
		/**
		 * Encapsula el valor GL_SUBTRACT.
		 */
		SUBTRACT	      (GL.GL_SUBTRACT),
		/**
		 * Encapsula el valor GL_INTERPOLATE.
		 */
		INTERPOLATE	      (GL.GL_INTERPOLATE),
		/**
		 * Encapsula el valor GL_DOT3_RGB.
		 */
		DOT3		      (GL.GL_DOT3_RGB);

		private final int value;
		private CombineMode(int value){
			this.value = value;
		}
	}
	
	/**
	 * Enumeración que contiene los distintos orígenes, sobre los que pueden
	 * actuar los modos de combinación, de los componentes de la textura.
	 * Estos orígenes se usan tanto para los componentes RGB como Alpha<br/>
	 * Estos orígenes son empleados cuando el modo de aplicación
	 * elegido es {@link AtributosTextura.Mode#COMBINE}.
	 */
	public enum CombineSrc{
		/**
		 * Encapsula el valor GL_CONSTANT.
		 */
		CONSTANT	      (GL.GL_CONSTANT),
		/**
		 * Encapsula el valor GL_PRIMARY_COLOR.
		 */
		OBJECT		      (GL.GL_PRIMARY_COLOR),
		/**
		 * Encapsula el valor GL_PREVIOUS.
		 */
		PREVIOUS	      (GL.GL_PREVIOUS),
		/**
		 * Encapsula el valor GL_TEXTURE.
		 */
		TEXTURE		      (GL.GL_TEXTURE),
		/**
		 * Encapsula el valor GL_TEXTURE0.
		 */
		TEXTURE0	      (GL.GL_TEXTURE0),
		/**
		 * Encapsula el valor GL_TEXTURE1.
		 */
		TEXTURE1	      (GL.GL_TEXTURE1),
		/**
		 * Encapsula el valor GL_TEXTURE2.
		 */
		TEXTURE2	      (GL.GL_TEXTURE2),
		/**
		 * Encapsula el valor GL_TEXTURE3.
		 */
		TEXTURE3	      (GL.GL_TEXTURE3),
		/**
		 * Encapsula el valor GL_TEXTURE4.
		 */
		TEXTURE4	      (GL.GL_TEXTURE4),
		/**
		 * Encapsula el valor GL_TEXTURE5.
		 */
		TEXTURE5	      (GL.GL_TEXTURE5),
		/**
		 * Encapsula el valor GL_TEXTURE6.
		 */
		TEXTURE6	      (GL.GL_TEXTURE6),
		/**
		 * Encapsula el valor GL_TEXTURE7.
		 */
		TEXTURE7	      (GL.GL_TEXTURE7);
		
		private final int value;
		private CombineSrc(int value){
			this.value = value;
		}
	}

	/**
	 * Enumeración que contiene los distintos operadores, que se pueden aplicar
	 * al orígenes, sobre los que actúan los modos de combinación de componentes.
	 * Estos operadores se usan tanto para los componentes RGB como Alpha<br/>
	 * Estos operadores son empleados cuando el modo de aplicación
	 * elegido es {@link AtributosTextura.Mode#COMBINE}.
	 */
	public enum CombineOperand{
		/**
		 * Encapsula el valor   GL_SRC_COLOR.
		 */
		SRC_COLOR			(GL.GL_SRC_COLOR),
		/**
		 * Encapsula el valor   GL_ONE_MINUS_SRC_COLOR.
		 */
		ONE_MINUS_SRC_COLOR	(GL.GL_ONE_MINUS_SRC_COLOR),
		/**
		 * Encapsula el valor   GL_SRC_ALPHA.
		 */
		SRC_ALPHA			(GL.GL_SRC_ALPHA),
		/**
		 * Encapsula el valor   GL_ONE_MINUS_SRC_ALPHA.
		 */
		ONE_MINUS_SRC_ALPHA	(GL.GL_ONE_MINUS_SRC_ALPHA);

		private final int value;
		private CombineOperand(int value){
			this.value = value;
		}
	}

	/**
	 * Enumeración que contiene los distintos modos de correcion de
	 * perspectiva de la textura.
	 */
	public enum PerspectiveCorrection{
		/**
		 * Encapsula el valor GL_FASTEST.
		 */
		FASTEST           (GL.GL_FASTEST),
		/**
		 * Encapsula el valor GL_NICEST.
		 */
		NICEST 	          (GL.GL_NICEST);

		private final int value;
		private PerspectiveCorrection(int value){
			this.value = value;
		}
	}

	/**
	 * Instancia de una clase anónima {@code AtributosTextura}, con sus
	 * valores por defecto, que puede ser compartida en múltiples
	 * {@code UnidadTextura}.<br/>
	 * Esta clase anónima sobreescribe todos los <i>setters</i> impidiendo
	 * que sus valores puedan ser modificados.
	 */
	public static final AtributosTextura DEFAULT = new AtributosTextura() {

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setEnvColor(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setMode(Mode mode) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setCombineRgbMode(CombineMode combineRgbMode) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setCombineRgbSource0(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setCombineRgbSource1(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setCombineRgbSource2(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setCombineAlphaMode(CombineMode combineAlphaMode) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setCombineAlphaSource0(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setCombineAlphaSource1(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setCombineAlphaSource2(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		/**
		 * {@inheritDoc}
		 * @throws
		 * UnsupportedOperationException Puesto que no se pueden modificar los valores por defecto.
		 */
		@Override
		public void setPerspectiveCorrection(PerspectiveCorrection perspCorrection) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}
	};

	private float[] envColor;
	private Mode mode;
	private CombineMode combineRgbMode;
	private final CombineSrc[] combineRgbSrcs;
	private final CombineOperand[] combineRgbOperands;
	private CombineMode combineAlphaMode;
	private final CombineSrc[] combineAlphaSrcs;
	private final CombineOperand[] combineAlphaOperands;
	private PerspectiveCorrection perspectiveCorrection;
	
	/**
	 *  Constructor que crea unos {@code AtributosTextura} con su valores por defecto.
	 */
	public AtributosTextura() {
		mode = Mode.MODULATE;
		combineRgbMode = CombineMode.MODULATE;
		combineRgbSrcs = new CombineSrc[] { CombineSrc.OBJECT, CombineSrc.TEXTURE, CombineSrc.CONSTANT };
		combineRgbOperands = new CombineOperand[] { CombineOperand.SRC_COLOR, CombineOperand.SRC_COLOR, CombineOperand.SRC_COLOR };
		combineAlphaMode = CombineMode.MODULATE;
		combineAlphaSrcs = new CombineSrc[] { CombineSrc.OBJECT, CombineSrc.TEXTURE, CombineSrc.CONSTANT };
		combineAlphaOperands = new CombineOperand[] { CombineOperand.SRC_ALPHA, CombineOperand.SRC_ALPHA, CombineOperand.SRC_ALPHA };
		perspectiveCorrection = PerspectiveCorrection.FASTEST;
	}

	/**
	 * <i>Setter</i> que asigna el color del entorno.<br/>
	 * Este color será empleado: si el modo de aplicación de textura elegido es
	 * {@link AtributosTextura.Mode#BLEND}, o es solicitado como origen 
	 * al emplear {@link AtributosTextura.Mode#COMBINE}.
	 * @param r Componente rojo del color asignado.
	 * @param g Componente verde del color asignado.
	 * @param b Componente azul del color asignado.
	 * @param a Componente alpha del color asignado.
	 */
	public void setEnvColor(float r, float g, float b, float a){
		if ((r < 0.0f  || r > 1.0f) ||
			(g < 0.0f  || g > 1.0f) ||
			(b < 0.0f  || b > 1.0f) ||
			(a < 0.0f  || a > 1.0f))
			throw new IllegalArgumentException();
		if(envColor  == null)
			this.envColor = new float[]{r,g,b,a};
		else{
			this.envColor[0] = r;
			this.envColor[1] = g;
			this.envColor[2] = b;
			this.envColor[3] = a;
		}
	}

	/**
	 * <i>Setter</i> que asigna el {@code Mode} a estos {@code AtributosTextura}.
	 * @param mode El {@code Mode} asignado.
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * <i>Setter</i> que asigna el {@code CombineMode} para el componenet RGB a estos {@code AtributosTextura}.
	 * @param combineRgbMode El {@code CombineMode} asignado.
	 */
	public void setCombineRgbMode(CombineMode combineRgbMode) {
		this.combineRgbMode = combineRgbMode;
	}

	private void setCombineRgbSource(int index, CombineSrc src, CombineOperand operand) {
		combineRgbSrcs[index] = src;
		combineRgbOperands[index] = operand;
	}

	/**
	 * <i>Setter</i> que asigna el <b>primer</b> origen y operador, que empleará el {@code CombineMode},
	 * para los componenetes RGB.<br/>
	 * <u>Nota:</u> Si el {@code CombineMode} usado es {@linkplain AtributosTextura.CombineMode#REPLACE REPLACE},
	 * no es necesario indicar ni el segundo, ni el tercer origen y operador.
	 * @param src     Primer origen que emplará el {@code CombineMode} para los componenetes RGB.
	 * @param operand Primer operador que emplará el {@code CombineMode} para los componenetes RGB.
	 */
	public void setCombineRgbSource0(CombineSrc src, CombineOperand operand) {
		setCombineRgbSource(0, src, operand);
	}
	
	/**
	 * <i>Setter</i> que asigna el <b>segundo</b> origen y operador, que empleará el {@code CombineMode},
	 * para los componenetes RGB.<br/>
	 * @param src     Segundo origen que emplará el {@code CombineMode} para los componenetes RGB.
	 * @param operand Segundo operador que emplará el {@code CombineMode} para los componenetes RGB.
	 */
	public void setCombineRgbSource1(CombineSrc src, CombineOperand operand) {
		setCombineRgbSource(1, src, operand);
	}
	
	/**
	 * <i>Setter</i> que asigna el <b>tercer</b> origen y operador, que empleará el {@code CombineMode},
	 * para los componenetes RGB.<br/>
	 * <u>Nota:</u> Sólo es necesario indicar el tercer origen y operador cuando el {@code CombineMode}
	 * usado es {@linkplain AtributosTextura.CombineMode#INTERPOLATE INTERPOLATE}.
	 * @param src     Tercer origen que emplará el {@code CombineMode} para los componenetes RGB.
	 * @param operand Tercer operador que emplará el {@code CombineMode} para los componenetes RGB.
	 */
	public void setCombineRgbSource2(CombineSrc src, CombineOperand operand) {
		setCombineRgbSource(2, src, operand);
	}

	/**
	 * <i>Setter</i> que asigna el {@code CombineMode} para el componenete Alpha a estos {@code AtributosTextura}.
	 * @param combineAlphaMode El {@code CombineMode} asignado.
	 */
	public void setCombineAlphaMode(CombineMode combineAlphaMode) {
		this.combineAlphaMode = combineAlphaMode;
	}
	
	private void setCombineAlphaSource(int index, CombineSrc src, CombineOperand operand) {
		combineAlphaSrcs[index] = src;
		combineAlphaOperands[index] = operand;
	}

	/**
	 * <i>Setter</i> que asigna el <b>primer</b> origen y operador, que empleará el {@code CombineMode}
	 * para el componenete Alpha.<br/>
	 * <u>Nota:</u> Si el {@code CombineMode} usado es {@linkplain AtributosTextura.CombineMode#REPLACE REPLACE},
	 * no es necesario indicar ni el segundo, ni el tercer origen y operador.
	 * @param src     Primer origen que emplará el {@code CombineMode} para el componenete Alpha.
	 * @param operand Primer operador que emplará el {@code CombineMode} para el componenete Alpha.
	 */
	public void setCombineAlphaSource0(CombineSrc src, CombineOperand operand) {
		setCombineAlphaSource(0, src, operand);
	}
	
	/**
	 * <i>Setter</i> que asigna el <b>segundo</b> origen y operador, que empleará el {@code CombineMode}
	 * para el componenete Alpha.<br/>
	 * @param src     Segundo origen que emplará el {@code CombineMode} para el componenete Alpha.
	 * @param operand Segundo operador que emplará el {@code CombineMode} para el componenete Alpha.
	 */
	public void setCombineAlphaSource1(CombineSrc src, CombineOperand operand) {
		setCombineAlphaSource(1, src, operand);
	}
	
	/**
	 * <i>Setter</i> que asigna el <b>tercer</b> origen y operador, que empleará el {@code CombineMode}
	 * para el componenete Alpha.<br/>
	 * <u>Nota:</u> Sólo es necesario indicar el tercer origen y operador cuando el {@code CombineMode}
	 * usado es {@linkplain AtributosTextura.CombineMode#INTERPOLATE INTERPOLATE}.
	 * @param src     Tercer origen que emplará el {@code CombineMode} para el componenete Alpha.
	 * @param operand Tercer operador que emplará el {@code CombineMode} para el componenete Alpha.
	 */
	public void setCombineAlphaSource2(CombineSrc src, CombineOperand operand) {
		setCombineAlphaSource(2, src, operand);
	}

	/**
	 * <i>Setter</i> que asigna el {@code PerspectiveCorrection} a estos {@code AtributosTextura}.
	 * @param perspCorrection El {@code PerspectiveCorrection} asignado.
	 */
	public void setPerspectiveCorrection(PerspectiveCorrection perspCorrection) {
		this.perspectiveCorrection = perspCorrection;
	}
	
	/**
	 * Método que se encarga de usar estos {@code AtributosTextura}.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public void usar(GL gl) {

		if (envColor != null)
			gl.glTexEnvfv(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_COLOR, envColor, 0);

		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, mode.value);
		if (mode == Mode.COMBINE) {
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, combineRgbMode.value);
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, combineRgbSrcs[0].value);
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND0_RGB,	combineRgbOperands[0].value);
			if (combineRgbMode != CombineMode.REPLACE) {
				gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SRC1_RGB, combineRgbSrcs[1].value);
				gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND1_RGB, combineRgbOperands[1].value);
				if (combineRgbMode == CombineMode.INTERPOLATE) {
					gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SRC2_RGB, combineRgbSrcs[2].value);
					gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND2_RGB, combineRgbOperands[2].value);
				}
			}
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_ALPHA, combineAlphaMode.value);
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SRC0_ALPHA, combineAlphaSrcs[0].value);
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND0_ALPHA, combineAlphaOperands[0].value);
			if (combineAlphaMode != CombineMode.REPLACE) {
				gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SRC1_ALPHA, combineAlphaSrcs[1].value);
				gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND1_ALPHA, combineAlphaOperands[1].value);
				if (combineAlphaMode == CombineMode.INTERPOLATE) {
					gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SRC2_ALPHA, combineAlphaSrcs[2].value);
					gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND2_ALPHA, combineAlphaOperands[2].value);
				}
			}
		}
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, perspectiveCorrection.value);
	}
}