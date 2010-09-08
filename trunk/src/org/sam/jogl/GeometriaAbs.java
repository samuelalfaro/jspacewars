package org.sam.jogl;

import java.nio.FloatBuffer;
import com.sun.opengl.util.BufferUtil;

/**
 * Clase abstracta, empleada como base, de la cual derivar distintas implementaciones,
 * que almacenan los datos de una geometría.
 */
public abstract class GeometriaAbs implements Geometria {
	
	/**
	 * Número de vértices que contiene esta {@code Geometria}.
	 */
	protected int nVertex;
	
	/**
	 * Entero que enmascara una combinación de los valores constantes
	 * que indican tanto: los datos que almacena esta {@code Geometria},
	 * como la forma de almacenarlos.
	 * <p>Las constantes que puenden emplearse son:<ul>
	 * <li>{@linkplain Geometria#POR_REFERENCIA POR_REFERENCIA}</li>
	 * <li>{@linkplain Geometria#USAR_BUFFERS USAR_BUFFERS}</li>
	 * <li>{@linkplain Geometria#COORDENADAS_TEXTURA COORDENADAS_TEXTURA}</li>
	 * <li>{@linkplain Geometria#COLOR_3 COLOR_3}</li>
	 * <li>{@linkplain Geometria#COLOR_4 COLOR_4}</li>
	 * <li>{@linkplain Geometria#NORMALES NORMALES}</li>
	 * <li>{@linkplain Geometria#ATRIBUTOS_VERTICES ATRIBUTOS_VERTICES}</li>
	 * </ul>
	 * O una combinacion mediante {@code OR} de los los valores anteriormente citados.</p>
	 */
	protected int att_mask;
	
	/**
	 * Vector de {@code Buffer} donde almacenar los conjuntos de coordenadas de textura.
	 */
	protected FloatBuffer	texCoordBuff[];
	/**
	 * Vector de vectores {@code float} donde almacenar los conjuntos de coordenadas de textura.
	 */
	protected float[]		texCoords[];
	/**
	 * {@code Buffer} donde almacenar los colores.
	 */
	protected FloatBuffer	colorBuff;
	/**
	 * Vector {@code float} donde almacenar los colores.
	 */
	protected float[]	 	colors;
	/**
	 * {@code Buffer} donde almacenar las normales.
	 */
	protected FloatBuffer	normalBuff;
	/**
	 * Vector {@code float} donde almacenar las normales.
	 */
	protected float[]	 	normals;
	/**
	 * {@code Buffer} donde almacenar las coordenadas de los vértices.
	 */
	protected FloatBuffer	coordBuff;
	/**
	 * Vector {@code float} donde almacenar las coordenadas de los vértices.
	 */
	protected float[]		coords;
	/**
	 * Vector de {@code Buffer} donde almacenar los conjuntos de atributos de vértice.
	 */
	protected FloatBuffer	vertexAttrBuff[];
	/**
	 * Vector de vectores {@code float} donde almacenar los conjuntos de atributos de vértice.
	 */
	protected float[]		vertexAttrs[];
	
	/**
	 * Constructor empleado por las clases derivadas que fuerza a indicar
	 * tanto: el {@link GeometriaAbs#nVertex número de vértices}, como la
	 * {@link GeometriaAbs#att_mask máscara de atributos}.
	 * @param nVertex  Número de vértices asignados.
	 * @param att_mask Máscara de atributos asignada.
	 */
	public GeometriaAbs(int nVertex, int att_mask){
		this.nVertex = nVertex;
		this.att_mask = att_mask;
		if((att_mask & USAR_BUFFERS) != 0){
			if((att_mask & COORDENADAS_TEXTURA) != 0)
				texCoordBuff = new FloatBuffer[1];
			if((att_mask & ATRIBUTOS_VERTICES) != 0)
				vertexAttrBuff = new FloatBuffer[1];
		}else{
			if((att_mask & COORDENADAS_TEXTURA) != 0)
				texCoords = new float[1][];
			if((att_mask & ATRIBUTOS_VERTICES) != 0)
				vertexAttrs = new float[1][];
		}
	}
	
	/**
	 * <i>Setter</i> que asigna las coordenadas de textura del conjunto 0 a esta {@code Geometria}.
	 * @param data Coordenadas de textura asignadas.
	 */
	public void setTexCoords(float[] data){
		setTexCoords(0, data);
	}
	
	/**
	 * <i>Setter</i> que asigna las coordenadas de textura del conjunto, indicado por el parámetro
	 * {@code texCoordSet}, a esta {@code Geometria}.
	 * @param texCoordSet Valor que indica el conjunto donde asignar las coordenadas.
	 * @param data Coordenadas de textura asignadas.
	 */
	public void setTexCoords(int texCoordSet, float[] data){
		if( ((att_mask & USAR_BUFFERS) != 0) ||
			((att_mask & COORDENADAS_TEXTURA) == 0)	)
			throw new IllegalArgumentException();
		
		if((att_mask & POR_REFERENCIA )!= 0){
			texCoords[texCoordSet] = data;
		}else{
			texCoords[texCoordSet] = new float[data.length];
			System.arraycopy(data, 0, texCoords[texCoordSet], 0, data.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna las coordenadas de textura del conjunto 0 a esta {@code Geometria}.
	 * @param buffer Coordenadas de textura asignadas.
	 */
	public void setTexCoords(FloatBuffer buffer){
		setTexCoords(0, buffer);
	}

	/**
	 * <i>Setter</i> que asigna las coordenadas de textura del conjunto, indicado por el parámetro
	 * {@code texCoordSet}, a esta {@code Geometria}.
	 * @param texCoordSet Valor que indica el conjunto donde asignar las coordenadas.
	 * @param buffer Coordenadas de textura asignadas.
	 */
	public void setTexCoords(int texCoordSet,  FloatBuffer buffer){
		if( ((att_mask & USAR_BUFFERS) == 0) ||
			((att_mask & COORDENADAS_TEXTURA) == 0) )
			throw new IllegalArgumentException();
		
		buffer.rewind();
		if((att_mask & POR_REFERENCIA )!= 0){
			texCoordBuff[texCoordSet] = buffer;
		}else{
			texCoordBuff[texCoordSet] = BufferUtil.copyFloatBuffer(buffer);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los colores a esta {@code Geometria}.
	 * @param data Colores asignados.
	 */
	public void setColors(float[] data){
		if( ((att_mask & USAR_BUFFERS) != 0) ||
			((att_mask & COLOR_3) == 0) )
			throw new IllegalArgumentException();

		if((att_mask & POR_REFERENCIA )!= 0){
			colors = data;
		}else{
			colors = new float[data.length];
			System.arraycopy(data, 0, colors, 0, data.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los colores a esta {@code Geometria}.
	 * @param buffer Colores asignados.
	 */
	public void setColors(FloatBuffer buffer){
		if( ((att_mask & USAR_BUFFERS) == 0) ||
			((att_mask & COLOR_3) == 0) )
			throw new IllegalArgumentException();

		buffer.rewind();
		if((att_mask & POR_REFERENCIA )!= 0){
			colorBuff = buffer;
		}else{
			colorBuff = BufferUtil.copyFloatBuffer(buffer);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna las normales a esta {@code Geometria}.
	 * @param data Normales asignadas.
	 */
	public void setNormals(float[] data){
		if( ((att_mask & USAR_BUFFERS) != 0) ||
			((att_mask & NORMALES) == 0) )
			throw new IllegalArgumentException();
		
		if((att_mask & POR_REFERENCIA )!= 0){
			normals = data;
		}else{
			normals = new float[data.length];
			System.arraycopy(data, 0, normals, 0, data.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna las normales a esta {@code Geometria}.
	 * @param buffer Normales asignadas.
	 */
	public void setNormals(FloatBuffer buffer){
		if( ((att_mask & USAR_BUFFERS) == 0) ||
			((att_mask & NORMALES) == 0) )
			throw new IllegalArgumentException();
		
		buffer.rewind();
		if((att_mask & POR_REFERENCIA )!= 0){
			normalBuff = buffer;
		}else{
			normalBuff = BufferUtil.copyFloatBuffer(buffer);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna las coordenadas de los vértices de esta {@code Geometria}.
	 * @param data Coordenadas de los vértices asignadas.
	 */
	public void setCoords(float[] data){
		if((att_mask & USAR_BUFFERS) != 0)
			throw new IllegalArgumentException();
		
		if((att_mask & POR_REFERENCIA )!= 0){
			coords = data;
		}else{
			coords = new float[data.length];
			System.arraycopy(data, 0, coords, 0, data.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna las coordenadas de los vértices de esta {@code Geometria}.
	 * @param buffer Coordenadas de los vértices asignadas.
	 */
	public void setCoords(FloatBuffer buffer){
		if((att_mask & USAR_BUFFERS) == 0)
			throw new IllegalArgumentException();
		
		buffer.rewind();
		if((att_mask & POR_REFERENCIA )!= 0){
			coordBuff = buffer;
		}else{
			coordBuff = BufferUtil.copyFloatBuffer(buffer);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los atributos de vértice en el conjunto, indicado por el parámetro
	 * {@code vertexAttrNum}, a esta {@code Geometria}.
	 * @param vertexAttrNum Valor que indica el conjunto donde asignar las coordenadas.
	 * @param data Atributos de vértice asignados.
	 */
	public void setVertexAttrs(int vertexAttrNum, float[] data){
		if( ((att_mask & USAR_BUFFERS) != 0) ||
			((att_mask & ATRIBUTOS_VERTICES) == 0))
			throw new IllegalArgumentException();
		
		if((att_mask & POR_REFERENCIA )!= 0){
			vertexAttrs[vertexAttrNum] = data;
		}else{
			vertexAttrs[vertexAttrNum] = new float[data.length];
			System.arraycopy(data, 0, vertexAttrs[vertexAttrNum], 0, data.length);
		}
	}
	
	/**
	 * <i>Setter</i> que asigna los atributos de vértice en el conjunto, indicado por el parámetro
	 * {@code vertexAttrNum}, a esta {@code Geometria}.
	 * @param vertexAttrNum Valor que indica el conjunto donde asignar las coordenadas.
	 * @param buffer Atributos de vértice asignados.
	 */
	public void setVertexAttrs(int vertexAttrNum, FloatBuffer buffer){
		if( ((att_mask & USAR_BUFFERS) == 0) ||
			((att_mask & ATRIBUTOS_VERTICES) == 0))
			throw new IllegalArgumentException();
		
		buffer.rewind();
		if((att_mask & POR_REFERENCIA )!= 0){
			vertexAttrBuff[vertexAttrNum] = buffer;
		}else{
			vertexAttrBuff[vertexAttrNum] = BufferUtil.copyFloatBuffer(buffer);
		}
	}
}
