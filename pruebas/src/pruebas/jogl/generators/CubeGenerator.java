/* 
 * CubeGenerator.java
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
package pruebas.jogl.generators;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.sam.jogl.Apariencia;
import org.sam.jogl.Material;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.OglList;

public class CubeGenerator {
	
	private CubeGenerator(){}

	private interface Generator{
		int getMode();
		void generarPlanoX(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float y0, float y1, 
				float z0, float z1, 
				float x);
		void generarPlanoY(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float x0, float x1, 
				float z0, float z1, 
				float y);
		void generarPlanoZ(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float x0, float x1, 
				float y0, float y1, 
				float z);
	}
	
	private static Generator QuadsGenerator = new Generator(){
		@Override
		public int getMode() {
			return GL2.GL_QUADS;
		}
	
		public void generarPlanoX(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float y0, float y1, 
				float z0, float z1, 
				float x){
			
			float n = x < 0 ? -1: 1;
			float t = (u0 < u1 ? 1: -1);
			float h = t * (v0 < v1 ? 1: -1);
			
			gl.glNormal3f(           n,  0,  0);
			gl.glVertexAttrib4f( 1,  0,  0,  t, h);
			
			gl.glTexCoord2f(        u0, v0);
			gl.glVertex3f(           x, y0, z0);
	
			gl.glTexCoord2f(        u1, v0);
			gl.glVertex3f(           x, y0, z1);
			
			gl.glTexCoord2f(        u1, v1);
			gl.glVertex3f(           x, y1, z1);
			
			gl.glTexCoord2f(        u0, v1);
			gl.glVertex3f(           x, y1, z0);
		}
		
		public void generarPlanoY(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float x0, float x1, 
				float z0, float z1, 
				float y){
			
			float n = y < 0 ? -1: 1;
			float t = (u0 < u1 ? -1: 1)* n;
	   		float h = t * (v0 < v1 ? -1: 1) * n;
	   		
	   		gl.glNormal3f(           0,  n,  0);
			gl.glVertexAttrib4f( 1,  t,  0,  0, h);
			
			gl.glTexCoord2f(        u0, v0);
			gl.glVertex3f(          x0,  y, z0);

			gl.glTexCoord2f(        u1, v0);
			gl.glVertex3f(          x1,  y, z0);
			
			gl.glTexCoord2f(        u1, v1);
			gl.glVertex3f(          x1,  y, z1);
			
			gl.glTexCoord2f(        u0, v1);
			gl.glVertex3f(          x0,  y, z1);
		}
		
		public void generarPlanoZ(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float x0, float x1, 
				float y0, float y1, 
				float z){
			
			float n = z < 0 ? -1: 1;
			float t = (u0 < u1 ? 1: -1)* n;
	   		float h = t * (v0 < v1 ? 1: -1) * n;
			
	   		gl.glNormal3f(           0,  0,  n);
			gl.glVertexAttrib4f( 1,  t,  0,  0, h);
			
			gl.glTexCoord2f(        u0, v0);
			gl.glVertex3f(          x0, y0,  z);
				
			gl.glTexCoord2f(        u1, v0);
			gl.glVertex3f(          x1, y0,  z);
			
			gl.glTexCoord2f(        u1, v1);
			gl.glVertex3f(          x1, y1,  z);
			
			gl.glTexCoord2f(        u0, v1);
			gl.glVertex3f(          x0, y1,  z);
		}
	};
	
	private static class NTBGenerator implements Generator{
		float scale;
		
		NTBGenerator(){
			scale = 1.0f;
		}
		
		@Override
		public int getMode() {
			return GL.GL_LINES;
		}
	
		public void generarPlanoX(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float y0, float y1, 
				float z0, float z1, 
				float x){
			
			float n = x < 0 ? -1: 1;
			float t = (u0 < u1 ? 1: -1);
	        float b = (v0 < v1 ? -1: 1)*n;
	
	        gl.glColor3f(0.5f, 1.0f, 0.0f);
			gl.glVertex3f( x, (y0 + y1)/2, (z0 + z1)/2);
			gl.glVertex3f( x + n*scale, (y0 + y1)/2, (z0 + z1)/2);
			
			gl.glColor3f(1.0f, 0.0f, 0.5f);
			gl.glVertex3f( x, (y0 + y1)/2, (z0 + z1)/2);
			gl.glVertex3f( x, (y0 + y1)/2, (z0 + z1)/2 + t*scale);
			
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			gl.glVertex3f( x, (y0 + y1)/2, (z0 + z1)/2);
			gl.glVertex3f( x, (y0 + y1)/2 + b*scale, (z0 + z1)/2);
		}
		
		public void generarPlanoY(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float x0, float x1, 
				float z0, float z1, 
				float y){
			
			float n = y < 0 ? -1: 1;
			float t = (u0 < u1 ? -1: 1)* n;
	        float b = (v0 < v1 ? 1: -1);
			
	        gl.glColor3f(0.5f, 1.0f, 0.0f);
			gl.glVertex3f((x0 + x1)/2, y, (z0 + z1)/2);
			gl.glVertex3f((x0 + x1)/2, y+ n*scale, (z0 + z1)/2);
			
			gl.glColor3f(1.0f, 0.0f, 0.5f);
			gl.glVertex3f((x0 + x1)/2, y, (z0 + z1)/2);
			gl.glVertex3f((x0 + x1)/2 + t*scale, y, (z0 + z1)/2);
			
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			gl.glVertex3f((x0 + x1)/2, y, (z0 + z1)/2);
			gl.glVertex3f((x0 + x1)/2, y, (z0 + z1)/2 + b*scale);
		}
		
		public void generarPlanoZ(GL2 gl,
				float u0, float u1, 
				float v0, float v1,
				float x0, float x1, 
				float y0, float y1, 
				float z){
			
			float n = z < 0 ? -1: 1;
			float t = (u0 < u1 ? 1: -1)* n;
	        float b = (v0 < v1 ? 1: -1);
			
	        gl.glColor3f(0.5f, 1.0f, 0.0f);
			gl.glVertex3f((x0 + x1)/2, (y0 + y1)/2, z );
			gl.glVertex3f((x0 + x1)/2, (y0 + y1)/2, z + n*scale);
			
			gl.glColor3f(1.0f, 0.0f, 0.5f);
			gl.glVertex3f((x0 + x1)/2, (y0 + y1)/2, z );
			gl.glVertex3f((x0 + x1)/2+ t*scale, (y0 + y1)/2, z );
			
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			gl.glVertex3f((x0 + x1)/2, (y0 + y1)/2, z );
			gl.glVertex3f((x0 + x1)/2, (y0 + y1)/2 + b*scale, z );
		}
	}
	
	private static NTBGenerator MyNTBGenerator = new NTBGenerator();
	
	private static OglList generate(GL2 gl, float lado, Generator generator) {
		float l = lado / 2;
		
		OglList oglList = new OglList(gl);
		
		gl.glBegin(generator.getMode());
		generator.generarPlanoZ( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				 l );
		generator.generarPlanoZ( gl,
				 1,  0,  0, 1,
				 0,  l, -l, 0,
				 l );
		generator.generarPlanoZ( gl,
				 0,  1,  1, 0,
				-l,  0,  0, l,
				 l );
		generator.generarPlanoZ( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				 l );
		
		generator.generarPlanoY( gl, 
				 1,  0,  0, 1,
				 0, -l, -l, 0,
				 l );
		generator.generarPlanoY( gl,
				 1,  0,  1, 0,
				 0, -l,  0, l,
				 l );
		generator.generarPlanoY( gl,
				 0,  1,  0, 1,
				 l,  0, -l, 0,
				 l );
		generator.generarPlanoY( gl,
				 0,  1,  1, 0,
				 l,  0,  0, l,
				 l );
		
		generator.generarPlanoZ( gl, 
				 1,  0,  0, 1,
				 0, -l, -l, 0,
				-l );
		generator.generarPlanoZ( gl,
				 1,  0,  1, 0,
				 0, -l,  0, l,
				-l );
		generator.generarPlanoZ( gl,
				 0,  1,  0, 1,
				 l,  0, -l, 0,
				-l );
		generator.generarPlanoZ( gl,
				 0,  1,  1, 0,
				 l,  0,  0, l,
				-l );
		
		generator.generarPlanoY( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				-l );
		generator.generarPlanoY( gl,
				 1,  0,  0, 1,
				 0,  l, -l, 0,
				-l );
		generator.generarPlanoY( gl,
				 0,  1,  1, 0,
				-l,  0,  0, l,
				-l );
		generator.generarPlanoY( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				-l );
		
		generator.generarPlanoX( gl, 
				 0,  1,  1, 0,
				 0, -l, -l, 0,
				 l );
		generator.generarPlanoX( gl,
				 1,  0,  1, 0,
				 0, -l,  0, l,
				 l );
		generator.generarPlanoX( gl,
				 0,  1,  0, 1,
				 l,  0, -l, 0,
				 l );
		generator.generarPlanoX( gl,
				 1,  0,  0, 1,
				 l,  0,  0, l,
				 l );
		
		generator.generarPlanoX( gl, 
				 0,  1,  0, 1,
				-l,  0, -l, 0,
				-l );
		generator.generarPlanoX( gl,
				 1,  0,  0, 1,
				-l,  0,  0, l,
				-l );
		generator.generarPlanoX( gl,
				 0,  1,  1, 0,
				 0,  l, -l, 0,
				-l );
		generator.generarPlanoX( gl,
				 1,  0,  1, 0,
				 0,  l,  0, l,
				-l );
		gl.glEnd();
		OglList.endList(gl);
		
		return oglList;
	}
	
	public static Objeto3D generate( GL2 gl, float lado ){
		Apariencia ap = new Apariencia();
		ap.setMaterial( Material.DEFAULT );
		return new Objeto3D( generate( gl, lado, QuadsGenerator ), ap );
	}

	public static Objeto3D generateNTB( GL2 gl, float lado, float scale ){
		MyNTBGenerator.scale = scale;
		return new Objeto3D( generate( gl, lado, MyNTBGenerator ), new Apariencia() );
	}
}
