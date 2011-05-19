/* 
 * GeometriaIndexadaTriangulos.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLPointerFunc;

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

	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw(GL2 gl) {
		if( ( att_mask & Geometria.USAR_BUFFERS ) != 0 ){
			if( ( att_mask & Geometria.COLOR_3 ) != 0 ){
				gl.glEnableClientState( GLPointerFunc.GL_COLOR_ARRAY );
				if( ( att_mask & Geometria.COLOR_4 ) == 0 )
					gl.glColorPointer( 3, GL.GL_FLOAT, 0, colorBuff );
				else
					gl.glColorPointer( 4, GL.GL_FLOAT, 0, colorBuff );
			}
			if( ( att_mask & Geometria.COORDENADAS_TEXTURA ) != 0 ){
				gl.glEnableClientState( GLPointerFunc.GL_TEXTURE_COORD_ARRAY );
				gl.glTexCoordPointer( 2, GL.GL_FLOAT, 0, texCoordBuff[0] );
			}
			if( ( att_mask & Geometria.NORMALES ) != 0 ){
				gl.glEnableClientState( GLPointerFunc.GL_NORMAL_ARRAY );
				gl.glNormalPointer( GL.GL_FLOAT, 0, normalBuff );
			}
			gl.glEnableClientState( GLPointerFunc.GL_VERTEX_ARRAY );
			gl.glVertexPointer( 3, GL.GL_FLOAT, 0, coordBuff );

			gl.glDrawElements( GL.GL_TRIANGLES, nVertex, GL.GL_UNSIGNED_INT, coordIndicesBuff );
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
