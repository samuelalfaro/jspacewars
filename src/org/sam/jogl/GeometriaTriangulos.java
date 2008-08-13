package org.sam.jogl;

import javax.media.opengl.GL;

public class GeometriaTriangulos extends Geometria {

	public GeometriaTriangulos(int nVertex, int att_mask) {
		super(nVertex, att_mask);
	}

	@Override
	public void draw(GL gl) {
		// TODO Auto-generated method stub
		if((att_mask & Geometria.USAR_BUFFERS)!=0){
			if((att_mask & Geometria.COLOR_3)!=0){
				gl.glEnableClientState (GL.GL_COLOR_ARRAY);
				if((att_mask & Geometria.COLOR_4)==0)
					gl.glColorPointer(3,GL.GL_FLOAT, 0, colorBuff);
				else
					gl.glColorPointer(4,GL.GL_FLOAT, 0, colorBuff);
			}
			if((att_mask & Geometria.COORDENADAS_TEXTURA)!=0){
				gl.glEnableClientState (GL.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(2,GL.GL_FLOAT, 0, texCoordBuff[0]);
			}
			if((att_mask & Geometria.NORMALES)!=0){
				gl.glEnableClientState (GL.GL_NORMAL_ARRAY);
				gl.glNormalPointer(GL.GL_FLOAT, 0, normalBuff);
			}
			gl.glEnableClientState (GL.GL_VERTEX_ARRAY);
		   	gl.glVertexPointer(3,GL.GL_FLOAT, 0, coordBuff);	
		   	
			gl.glDrawArrays(GL.GL_TRIANGLES,0,nVertex);
		}else{
//			if((att_mask & Geometria.COORDENADAS_TEXTURA)!=0)
//				gl.glTexCoordPointer(2,GL.GL_FLOAT, 0, texCoords[0]);
//			if((att_mask & Geometria.NORMALES)!=0)
//				gl.gl
//		   	gl.glVertexPointer(3,GL.GL_FLOAT, 0, coords);	
//			// DRAW THE MODEL: ONLY VERTEXS APPEAR TO WORK CORRECTLY	
//			gl.glDrawArrays(GL.GL_TRIANGLES,0,size*3);
		}
	}
}
