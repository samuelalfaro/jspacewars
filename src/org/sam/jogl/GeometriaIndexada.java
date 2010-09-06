package org.sam.jogl;

import java.nio.IntBuffer;
import com.sun.opengl.util.BufferUtil;

public abstract class GeometriaIndexada extends GeometriaAbs {

	protected int[]		texCoordIndices[];
	protected int[]	 	colorIndices;
	protected int[]	 	normalIndices;
	protected IntBuffer	coordIndicesBuff;
	protected int[]		coordIndices;
	protected int[]		vertexAttrIndices[];
	
	public GeometriaIndexada(int nVertex, int att_mask) {
		super(nVertex, att_mask);
		if((att_mask & USAR_BUFFERS) == 0){
			if((att_mask & COORDENADAS_TEXTURA) != 0)
				texCoordIndices = new int[1][];
			if((att_mask & ATRIBUTOS_VERTICES) != 0)
				vertexAttrIndices = new int[1][];
		}
	}
	
	public void setTexCoordIndices(int[] indices){
		setTexCoordIndices(0, indices);
	}
	
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
