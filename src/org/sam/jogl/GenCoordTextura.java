package org.sam.jogl;

import javax.media.opengl.GL;

public class GenCoordTextura {
	
	public enum Mode{
		OBJECT_LINEAR	(GL.GL_OBJECT_LINEAR),
		EYE_LINEAR    	(GL.GL_EYE_LINEAR),
		SPHERE_MAP		(GL.GL_SPHERE_MAP),
		NORMAL_MAP    	(GL.GL_NORMAL_MAP),
		REFLECTION_MAP	(GL.GL_REFLECTION_MAP);

		private final int value;

		Mode(int value){
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public enum Coordinates{
		TEXTURE_COORDINATE_2(2),
    	TEXTURE_COORDINATE_3(3),
    	TEXTURE_COORDINATE_4(4);
		
		private final int value;

		Coordinates(int value){
			this.value = value;
		}
		public int getValue() {
			return this.value;
		}
	}
	
	private Mode mode;
    private Coordinates numCoordinates;
    
    public GenCoordTextura(Mode mode, Coordinates numCoordinates){
    	this.mode = mode;
    	this.numCoordinates = numCoordinates;
    }
    
    public void activar(GL gl, GenCoordTextura anterior ){
    	switch(numCoordinates){
    	case TEXTURE_COORDINATE_2:
            gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, mode.value);
            gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, mode.value);
            if(anterior == null){
            	gl.glEnable(GL.GL_TEXTURE_GEN_S);
            	gl.glEnable(GL.GL_TEXTURE_GEN_T);
            }else{
            	switch(anterior.numCoordinates){
            	case TEXTURE_COORDINATE_2: break;
            	case TEXTURE_COORDINATE_3:
            		gl.glDisable(GL.GL_TEXTURE_GEN_R);
            		break;
            	case TEXTURE_COORDINATE_4:
            		gl.glDisable(GL.GL_TEXTURE_GEN_Q);
            		gl.glDisable(GL.GL_TEXTURE_GEN_R);
            		break;
            	}
            }
            break;
    	case TEXTURE_COORDINATE_3:
    		gl.glTexGeni(GL.GL_R, GL.GL_TEXTURE_GEN_MODE, mode.value);
    		gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, mode.value);
            gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, mode.value);
            if(anterior == null){
            	gl.glEnable(GL.GL_TEXTURE_GEN_R);
            	gl.glEnable(GL.GL_TEXTURE_GEN_S);
            	gl.glEnable(GL.GL_TEXTURE_GEN_T);
            }else{
            	switch(anterior.numCoordinates){
            	case TEXTURE_COORDINATE_2:
            		gl.glEnable(GL.GL_TEXTURE_GEN_R);
            		break;
            	case TEXTURE_COORDINATE_3: break;
            	case TEXTURE_COORDINATE_4:
            		gl.glDisable(GL.GL_TEXTURE_GEN_Q);
            		break;
            	}
            }
            break;
    	case TEXTURE_COORDINATE_4:
    		gl.glTexGeni(GL.GL_Q, GL.GL_TEXTURE_GEN_MODE, mode.value);
    		gl.glTexGeni(GL.GL_R, GL.GL_TEXTURE_GEN_MODE, mode.value);
    		gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, mode.value);
            gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, mode.value);
            if(anterior == null){
            	gl.glEnable(GL.GL_TEXTURE_GEN_Q);
            	gl.glEnable(GL.GL_TEXTURE_GEN_R);
            	gl.glEnable(GL.GL_TEXTURE_GEN_S);
            	gl.glEnable(GL.GL_TEXTURE_GEN_T);
            }else{
            	switch(anterior.numCoordinates){
            	case TEXTURE_COORDINATE_2:
            		gl.glEnable(GL.GL_TEXTURE_GEN_Q);
            		gl.glEnable(GL.GL_TEXTURE_GEN_R);
            		break;
            	case TEXTURE_COORDINATE_3:
            		gl.glEnable(GL.GL_TEXTURE_GEN_Q);
            		break;
            	case TEXTURE_COORDINATE_4:
            		break;
            	}
            }
    	}
	}
	
	public void desactivar(GL gl){
    	switch(numCoordinates){
    	case TEXTURE_COORDINATE_2:
    		gl.glDisable(GL.GL_TEXTURE_GEN_S);
            gl.glDisable(GL.GL_TEXTURE_GEN_T);
    	case TEXTURE_COORDINATE_3:
    		gl.glDisable(GL.GL_TEXTURE_GEN_R);
    		gl.glDisable(GL.GL_TEXTURE_GEN_S);
            gl.glDisable(GL.GL_TEXTURE_GEN_T);
    	case TEXTURE_COORDINATE_4:
    		gl.glDisable(GL.GL_TEXTURE_GEN_Q);
    		gl.glDisable(GL.GL_TEXTURE_GEN_R);
            gl.glDisable(GL.GL_TEXTURE_GEN_S);
            gl.glDisable(GL.GL_TEXTURE_GEN_T);
    	}
	}
}
