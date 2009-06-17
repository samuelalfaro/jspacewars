package org.sam.jogl;

import javax.media.opengl.GL;

public class OglList implements Forma3D {
	
	private final transient int idList;
	
	public OglList(int id){
		this.idList = id;
	}
	
	public void draw(GL gl) {
		gl.glCallList(idList);
	}
}
