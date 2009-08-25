package org.sam.jogl;

import javax.media.opengl.GL;

public class AtributosTextura {

	public enum Mode{
		MODULATE	(GL.GL_MODULATE),
		DECAL		(GL.GL_DECAL),
		BLEND		(GL.GL_BLEND),
		REPLACE		(GL.GL_REPLACE),
		COMBINE		(GL.GL_COMBINE);

		private final int value;
		private Mode(int value){
			this.value = value;
		}
	}
	
	public enum CombineMode{
		REPLACE		(GL.GL_REPLACE),
		MODULATE	(GL.GL_MODULATE),
		ADD			(GL.GL_ADD),
		ADD_SIGNED	(GL.GL_ADD_SIGNED),
		SUBTRACT	(GL.GL_SUBTRACT),
		INTERPOLATE	(GL.GL_INTERPOLATE),
		DOT3		(GL.GL_DOT3_RGB);

		private final int value;
		private CombineMode(int value){
			this.value = value;
		}
	}
	
	public enum CombineSrc{
		CONSTANT	(GL.GL_CONSTANT),
		OBJECT		(GL.GL_PRIMARY_COLOR),
		PREVIOUS	(GL.GL_PREVIOUS),
		TEXTURE		(GL.GL_TEXTURE),
		TEXTURE0	(GL.GL_TEXTURE0),
		TEXTURE1	(GL.GL_TEXTURE1),
		TEXTURE2	(GL.GL_TEXTURE2),
		TEXTURE3	(GL.GL_TEXTURE3),
		TEXTURE4	(GL.GL_TEXTURE4),
		TEXTURE5	(GL.GL_TEXTURE5),
		TEXTURE6	(GL.GL_TEXTURE6),
		TEXTURE7	(GL.GL_TEXTURE7);
		
		private final int value;
		private CombineSrc(int value){
			this.value = value;
		}
	}

	public enum CombineOperand{
		SRC_COLOR			(GL.GL_SRC_COLOR),
		ONE_MINUS_SRC_COLOR	(GL.GL_ONE_MINUS_SRC_COLOR),
		SRC_ALPHA			(GL.GL_SRC_ALPHA),
		MINUS_SRC_ALPHA		(GL.GL_ONE_MINUS_SRC_ALPHA);

		private final int value;
		private CombineOperand(int value){
			this.value = value;
		}
	}

	public enum PerspectiveCorrection{
		FASTEST (GL.GL_FASTEST),
		NICEST 	(GL.GL_NICEST);

		private final int value;
		private PerspectiveCorrection(int value){
			this.value = value;
		}
	}

	public static final AtributosTextura DEFAULT = new AtributosTextura() {

		public void setEnvColor(float r, float g, float b, float a) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setMode(Mode mode) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setCombineRgbMode(CombineMode combineRgbMode) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setCombineRgbSource0(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setCombineRgbSource1(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setCombineRgbSource2(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setCombineAlphaMode(CombineMode combineAlphaMode) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setCombineAlphaSource0(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setCombineAlphaSource1(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

		public void setCombineAlphaSource2(CombineSrc src, CombineOperand operand) {
			throw new UnsupportedOperationException("DEFAULT can't be changed");
		}

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

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setCombineRgbMode(CombineMode combineRgbMode) {
		this.combineRgbMode = combineRgbMode;
	}

	private void setCombineRgbSource(int index, CombineSrc src, CombineOperand operand) {
		combineRgbSrcs[index] = src;
		combineRgbOperands[index] = operand;
	}

	public void setCombineRgbSource0(CombineSrc src, CombineOperand operand) {
		setCombineRgbSource(0, src, operand);
	}
	
	public void setCombineRgbSource1(CombineSrc src, CombineOperand operand) {
		setCombineRgbSource(1, src, operand);
	}
	
	public void setCombineRgbSource2(CombineSrc src, CombineOperand operand) {
		setCombineRgbSource(2, src, operand);
	}

	public void setCombineAlphaMode(CombineMode combineAlphaMode) {
		this.combineAlphaMode = combineAlphaMode;
	}
	
	private void setCombineAlphaSource(int index, CombineSrc src, CombineOperand operand) {
		combineAlphaSrcs[index] = src;
		combineAlphaOperands[index] = operand;
	}

	public void setCombineAlphaSource0(CombineSrc src, CombineOperand operand) {
		setCombineAlphaSource(0, src, operand);
	}
	
	public void setCombineAlphaSource1(CombineSrc src, CombineOperand operand) {
		setCombineAlphaSource(1, src, operand);
	}
	
	public void setCombineAlphaSource2(CombineSrc src, CombineOperand operand) {
		setCombineAlphaSource(2, src, operand);
	}

	public void setPerspectiveCorrection(PerspectiveCorrection perspCorrection) {
		this.perspectiveCorrection = perspCorrection;
	}
	
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