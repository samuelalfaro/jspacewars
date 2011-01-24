/* 
 * GeometriaIndexada.java
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

import java.nio.IntBuffer;
import com.sun.opengl.util.BufferUtil;

/**
 * Clase abstracta, empleada como base, de la cual derivar distintas implementaciones,
 * que almacenan los datos de una geometría indexada.
 */
public abstract class GeometriaIndexada extends GeometriaAbs {

	/**
	 * Vector de vectores donde almacenar los índices corresponentes a cada conjunto de coordenadas de textura.<br/>
	 */
	protected int[]		texCoordIndices[];
	/**
	 * Vector donde almacenar los índices corresponentes a los colores.
	 */
	protected int[]	 	colorIndices;
	/**
	 * Vector donde almacenar los índices corresponentes a las normales.
	 */
	protected int[]	 	normalIndices;
	/**
	 * {@code Buffer} donde almacenar los índices corresponentes: tanto a las coordenadas
	 * de los vértices, como al resto de los atributos de la {@code Geometria}.<br>
	 * <u>Nota:</u> No se pueden usar índices independientes para cada atributo, si los valores
	 * son almacenanos en <i>buffers</i>
	 */
	protected IntBuffer	coordIndicesBuff;
	/**
	 * Vector donde almacenar los índices corresponentes a las coordenadas de los vértices.
	 */
	protected int[]		coordIndices;
	/**
	 * Vector de vectores donde almacenar los índices corresponentes a cada conjunto de los atributos de vértice.<br/>
	 */
	protected int[]		vertexAttrIndices[];
	
	/**
	 * Constructor empleado por las clases derivadas que fuerza a indicar
	 * tanto: el {@link GeometriaAbs#nVertex número de vértices}, como la
	 * {@link GeometriaAbs#att_mask máscara de atributos}.
	 * @param nVertex  Número de vértices asignados.
	 * @param att_mask Máscara de atributos asignada.
	 */
	public GeometriaIndexada(int nVertex, int att_mask) {
		super(nVertex, att_mask);
		if((att_mask & USAR_BUFFERS) == 0){
			if((att_mask & COORDENADAS_TEXTURA) != 0)
				texCoordIndices = new int[1][];
			if((att_mask & ATRIBUTOS_VERTICES) != 0)
				vertexAttrIndices = new int[1][];
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los índices de las coordenadas de textura del conjunto 0 a esta {@code Geometria}.
	 * @param indices Índices asignados.
	 */
	public void setTexCoordIndices(int[] indices){
		setTexCoordIndices(0, indices);
	}
	
	/**
	 * <i>Setter</i> que asigna los índices de las coordenadas de textura, del conjunto
	 * indicado por el parámetro  {@code texCoordSet}, a esta {@code Geometria}.
	 * @param texCoordSet Valor que indica el conjunto donde asignar las índices.
	 * @param indices Índices asignados.
	 */
	public void setTexCoordIndices(int texCoordSet, int[] indices){
		if( ((att_mask & USAR_BUFFERS) != 0) ||
			((att_mask & COORDENADAS_TEXTURA) == 0)	)
			throw new IllegalArgumentException();
		
		if((att_mask & POR_REFERENCIA )!= 0){
			texCoordIndices[texCoordSet] = indices;
		}else{
			texCoordIndices[texCoordSet] = new int[indices.length];
			System.arraycopy(indices, 0, texCoords[texCoordSet], 0, indices.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los índices de los colores a esta {@code Geometria}.
	 * @param indices Índices asignados.
	 */
	public void setColorIndices(int[] indices){
		if( ((att_mask & USAR_BUFFERS) != 0) ||
			((att_mask & COLOR_3) == 0) )
			throw new IllegalArgumentException();

		if((att_mask & POR_REFERENCIA )!= 0){
			colorIndices = indices;
		}else{
			colorIndices = new int[indices.length];
			System.arraycopy(indices, 0, colorIndices, 0, indices.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los índices de las normales a esta {@code Geometria}.
	 * @param indices Índices asignados.
	 */
	public void setNormalIndices(int[] indices){
		if( ((att_mask & USAR_BUFFERS) != 0) ||
			((att_mask & NORMALES) == 0) )
			throw new IllegalArgumentException();
		
		if((att_mask & POR_REFERENCIA )!= 0){
			normalIndices = indices;
		}else{
			normalIndices = new int[indices.length];
			System.arraycopy(indices, 0, normals, 0, indices.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los índices de las coordenadas de los vértices a esta {@code Geometria}.
	 * @param indices Índices asignados.
	 */
	public void setCoordIndices(int[] indices){
		if((att_mask & USAR_BUFFERS) != 0)
			throw new IllegalArgumentException();
		
		if((att_mask & POR_REFERENCIA )!= 0){
			coordIndices = indices;
		}else{
			coordIndices = new int[indices.length];
			System.arraycopy(indices, 0, coordIndices, 0, indices.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los índices de las coordenadas de los vértices a esta {@code Geometria}.
	 * @param bufferIndices Índices asignados.
	 */
	public void setCoordIndices(IntBuffer bufferIndices){
		if((att_mask & USAR_BUFFERS) == 0)
			throw new IllegalArgumentException();
		
		bufferIndices.rewind();
		if((att_mask & POR_REFERENCIA )!= 0){
			coordIndicesBuff = bufferIndices;
		}else{
			coordIndicesBuff = BufferUtil.copyIntBuffer(bufferIndices);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los índices de las atributos de vértice, del conjunto
	 * indicado por el parámetro  {@code vertexAttrNum}, a esta {@code Geometria}.
	 * @param vertexAttrNum Valor que indica el conjunto donde asignar las índices.
	 * @param indices Índices asignados.
	 */
	public void setVertexAttrIndices(int vertexAttrNum, int[] indices){
		if( ((att_mask & USAR_BUFFERS) != 0) ||
			((att_mask & ATRIBUTOS_VERTICES) == 0))
			throw new IllegalArgumentException();
		
		if((att_mask & POR_REFERENCIA )!= 0){
			vertexAttrIndices[vertexAttrNum] = indices;
		}else{
			vertexAttrIndices[vertexAttrNum] = new int[indices.length];
			System.arraycopy(indices, 0, vertexAttrs[vertexAttrNum], 0, indices.length);
		}
	}
}
