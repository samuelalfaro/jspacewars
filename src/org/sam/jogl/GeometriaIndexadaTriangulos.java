package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Implementación de una {@code Geometria} diseñada para almacenar triángulos indexados.
 */
public class GeometriaIndexadaTriangulos extends GeometriaIndexada {

	/**
	 * Constructor que genera una {@code GeometriaIndexadaTriangulos} con los parámetros correspondientes.
	 * @param nVertex  {@link GeometriaAbs#nVertex Número de vértices} de la {@code Geometria} creada.
	 * @param att_mask {@link GeometriaAbs#att_mask Máscara de atributos} de la {@code Geometria} creada.
	 */
	public GeometriaIndexadaTriangulos(int nVertex, int att_mask) {
		super(nVertex, att_mask);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(GL gl) {
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
		   	gl.glVertexPointer(3, GL.GL_FLOAT, 0, coordBuff);	
		   	
			gl.glDrawElements(GL.GL_TRIANGLES,nVertex,GL.GL_UNSIGNED_INT,coordIndicesBuff);
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
