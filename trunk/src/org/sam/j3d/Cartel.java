package org.sam.j3d;

import javax.media.j3d.*;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Cartel extends Shape3D{
	
	public Cartel(float ancho, float alto){
		super(crearGeometria(ancho,alto));
	}
	
	private static Geometry crearGeometria(float ancho, float alto){
		QuadArray quadParts = new QuadArray(4, 
				GeometryArray.COORDINATES | 
				GeometryArray.TEXTURE_COORDINATE_2 |
				GeometryArray.NORMALS  |
				GeometryArray.BY_REFERENCE );

		ancho /= 2;
		alto /= 2;
		float[] posicionCoord = {
				-ancho, -alto, 0.0f,
				ancho, -alto, 0.0f,
				ancho,  alto, 0.0f,
				-ancho,  alto, 0.0f
		};
		quadParts.setCoordRefFloat(posicionCoord);
		float[] normalCoord = {
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,  
				0.0f, 0.0f, 1.0f,  
				0.0f, 0.0f, 1.0f  
		};
		quadParts.setNormalRefFloat(normalCoord);
		float[] texturaCoord = {
				0.0f, 0.0f,
				1.0f, 0.0f,
				1.0f, 1.0f,
				0.0f, 1.0f
		};
		quadParts.setTexCoordRefFloat(0,texturaCoord);
		return quadParts;
	}
}