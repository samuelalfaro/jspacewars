package org.sam.jogl;

import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import com.sun.opengl.util.BufferUtil;

public abstract class GeometriaAbs implements Geometria {
	
	protected int nVertex;
	protected int att_mask;
	
	protected FloatBuffer	texCoordBuff[];
	protected float[]		texCoords[];
	protected FloatBuffer	colorBuff;
	protected float[]	 	colors;
	protected FloatBuffer	normalBuff;
	protected float[]	 	normals;
	protected FloatBuffer	coordBuff;
	protected float[]		coords;
	protected FloatBuffer	vertexAttrBuff[];
	protected float[]		vertexAttrs[];
	
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
	
	public void setTexCoords(float[] data){
		setTexCoords(0, data);
	}
	
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
	
	public void setTexCoords(FloatBuffer buffer){
		setTexCoords(0, buffer);
	}

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
	
	public abstract void draw(GL gl);
}
