/* 
 * Generator.java
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

import javax.media.opengl.GL2;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Interface que proporciona los métodos, a las clases que lo implementan,
 * para generar primitivas.<br/>
 * De este modo, se pueden gererar estas primitivas de distintas formas,
 * a partir de los mismos datos, simplemente usando un {@code Generator}
 * u otro.
 */
public abstract class Generator {
	
	public static final int ONLY_VERTICES          = 0x00;
	public static final int GENERATE_NORMALS       = 0x01;
	public static final int GENERATE_TEXT_COORDS   = 0x02;
	
	public static final int WIREFRAME_MASK         = 0x04;
	public static final int GENERATE_TANGENTS_MASK = 0x08;
	public static final int GENERATE_NTB_MASK      = 0x10;
	
	public static final int WIREFRAME              = GENERATE_NORMALS  | WIREFRAME_MASK;
	public static final int GENERATE_TANGENTS      = GENERATE_NORMALS  | GENERATE_TEXT_COORDS | GENERATE_TANGENTS_MASK;
	public static final int GENERATE_NTB           = GENERATE_TANGENTS | GENERATE_NTB_MASK;
	
	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que sólo contienen sus vértices. 
	 */
	private static class VerticesGenerator extends Generator{
		
		VerticesGenerator(){}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl, vert1, vert2, vert3 );
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un triángulo.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del triángulo.
		 * @param vert2 Vertice del segundo punto del triángulo.
		 * @param vert3 Vertice del tercer  punto del triángulo.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3
		){
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl, vert1, vert2, vert3, vert4 );
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un cuadrilátero.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del cuadrilatero.
		 * @param vert2 Vertice del segundo punto del cuadrilatero.
		 * @param vert3 Vertice del tercer  punto del cuadrilatero.
		 * @param vert4 Vertice del cuarto punto del cuadrilatero.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4
		){
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
		}
	}
	
	/**
	 * Instancia estática de un {@code VerticesGenerator} que evita la necesidad
	 * de crear más instancias.
	 */
	public static final Generator Vertices = new VerticesGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que contienen sus vértices y sus normales. 
	 */
	private static class VerticesNormalsGenerator extends Generator{
		
		VerticesNormalsGenerator(){}
	
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl,
					vert1, vert2, vert3,
					norm1, norm2, norm3
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un triángulo.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del triángulo.
		 * @param vert2 Vertice del segundo punto del triángulo.
		 * @param vert3 Vertice del tercer  punto del triángulo.
		 * 
		 * @param norm1 Normal del primer  punto del triángulo.
		 * @param norm2 Normal del segundo punto del triángulo.
		 * @param norm3 Normal del tercer  punto del triángulo.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3
		){
			gl.glNormal3f( norm1.x, norm1.y, norm1.z );
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f( norm2.x, norm2.y, norm2.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f( norm3.x, norm3.y, norm3.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl,
					vert1, vert2, vert3, vert4,
					norm1, norm2, norm3, norm4
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un cuadrilátero.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del cuadrilatero.
		 * @param vert2 Vertice del segundo punto del cuadrilatero.
		 * @param vert3 Vertice del tercer  punto del cuadrilatero.
		 * @param vert4 Vertice del cuarto punto del cuadrilatero.
		 * 
		 * @param norm1 Normal del primer  punto del cuadrilatero.
		 * @param norm2 Normal del segundo punto del cuadrilatero.
		 * @param norm3 Normal del tercer  punto del cuadrilatero.
		 * @param norm4 Normal del cuarto punto del cuadrilatero.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4
		){
			gl.glNormal3f( norm1.x, norm1.y, norm1.z );
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f( norm2.x, norm2.y, norm2.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f( norm3.x, norm3.y, norm3.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f( norm4.x, norm4.y, norm4.z );
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
		}
	}
	
	/**
	 * Instancia estática de un {@code VerticesNormalsGenerator} que
	 * evita la necesidad de crear más instancias.
	 */
	public static final Generator VerticesNormals = new VerticesNormalsGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera siempre cuadriláteros,
	 * en los que cada vértice recibe como coordenadas de textura, las coordenadas
	 * de los vértices que forman la primitiva a la que pertenece. Datos
	 * necesarios poder hacer uso del shader <i>SinglePassWireFrame</i>.
	 */
	private static class VerticesWireFrameGenerator extends Generator{
		
		final int posAtt1;
		final int posAtt2;
		final int posAtt3;
		final int posAtt4;
		
		VerticesWireFrameGenerator(int posAtt1, int posAtt2, int posAtt3, int posAtt4){
			this.posAtt1 = posAtt1;
			this.posAtt2 = posAtt2;
			this.posAtt3 = posAtt3;
			this.posAtt4 = posAtt4;
		}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl,
					vert1, vert2, vert2, vert3,
					norm1, norm2, norm2, norm3
			);
		}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl,
					vert1, vert2, vert3, vert4,
					norm1, norm2, norm3, norm4
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un cuadrilátero.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del cuadrilatero.
		 * @param vert2 Vertice del segundo punto del cuadrilatero.
		 * @param vert3 Vertice del tercer  punto del cuadrilatero.
		 * @param vert4 Vertice del cuarto punto del cuadrilatero.
		 * 
		 * @param norm1 Normal del primer  punto del cuadrilatero.
		 * @param norm2 Normal del segundo punto del cuadrilatero.
		 * @param norm3 Normal del tercer  punto del cuadrilatero.
		 * @param norm4 Normal del cuarto punto del cuadrilatero.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4
		){
			
			gl.glNormal3f( norm1.x, norm1.y, norm1.z );
			gl.glVertexAttrib3f( posAtt1, vert1.x, vert1.y, vert1.z );
			gl.glVertexAttrib3f( posAtt2, vert2.x, vert2.y, vert2.z );
			gl.glVertexAttrib3f( posAtt3, vert3.x, vert3.y, vert3.z );
			gl.glVertexAttrib3f( posAtt4, vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f( norm2.x, norm2.y, norm2.z );
			gl.glVertexAttrib3f( posAtt1, vert1.x, vert1.y, vert1.z );
			gl.glVertexAttrib3f( posAtt2, vert2.x, vert2.y, vert2.z );
			gl.glVertexAttrib3f( posAtt3, vert3.x, vert3.y, vert3.z );
			gl.glVertexAttrib3f( posAtt4, vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f( norm3.x, norm3.y, norm3.z );
			gl.glVertexAttrib3f( posAtt1, vert1.x, vert1.y, vert1.z );
			gl.glVertexAttrib3f( posAtt2, vert2.x, vert2.y, vert2.z );
			gl.glVertexAttrib3f( posAtt3, vert3.x, vert3.y, vert3.z );
			gl.glVertexAttrib3f( posAtt4, vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f( norm4.x, norm4.y, norm4.z );
			gl.glVertexAttrib3f( posAtt1, vert1.x, vert1.y, vert1.z );
			gl.glVertexAttrib3f( posAtt2, vert2.x, vert2.y, vert2.z );
			gl.glVertexAttrib3f( posAtt3, vert3.x, vert3.y, vert3.z );
			gl.glVertexAttrib3f( posAtt3, vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
		}
	}
	
	/**
	 * Instancia estática de un {@code VerticesWireFrameGenerator} 
	 * que evita la necesidad de crear más instancias.
	 */
	public static final Generator VerticesWireFrame = new VerticesWireFrameGenerator( 1, 2, 3, 4 );

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que contienen sus vértices y sus coordenadas de textura.
	 */
	private static class VerticesTexCoordsGenerator extends Generator{
		
		VerticesTexCoordsGenerator(){}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl,
					vert1, vert2, vert3,
					text1, text2, text3
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un triángulo.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del triángulo.
		 * @param vert2 Vertice del segundo punto del triángulo.
		 * @param vert3 Vertice del tercer  punto del triángulo.
		 * 
		 * @param text1 Coordenadas de textura del primer  punto del triángulo.
		 * @param text2 Coordenadas de textura del segundo punto del triángulo.
		 * @param text3 Coordenadas de textura del tercer  punto del triángulo.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3
		){	
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl,
					vert1, vert2, vert3, vert4,
					text1, text2, text3, text4
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un cuadrilátero.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del cuadrilatero.
		 * @param vert2 Vertice del segundo punto del cuadrilatero.
		 * @param vert3 Vertice del tercer  punto del cuadrilatero.
		 * @param vert4 Vertice del cuarto punto del cuadrilatero.
		 * 
		 * @param text1 Coordenadas de textura del primer  punto del cuadrilatero.
		 * @param text2 Coordenadas de textura del segundo punto del cuadrilatero.
		 * @param text3 Coordenadas de textura del tercer  punto del cuadrilatero.
		 * @param text4 Coordenadas de textura del cauarto punto del cuadrilatero.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4
		){	
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
			
			gl.glTexCoord2f( text4.x, text4.y );
			gl.glVertex3f(   vert4.x, vert4.y, vert4.z );
		}
	}
	
	/**
	 * Instancia estática de un {@code VerticesTexCoordsGenerator} que evita la necesidad
	 * de crear más instancias.
	 */
	public static final Generator VerticesTexCoords = new VerticesTexCoordsGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que contienen tanto sus vértices, sus normales, como sus
	 * coordenadas de textura.
	 */
	private static class VerticesNormalsTexCoordsGenerator extends Generator{
		
		VerticesNormalsTexCoordsGenerator(){}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl,
					vert1, vert2, vert3,
					text1, text2, text3,
					norm1, norm2, norm3
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un triángulo.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del triángulo.
		 * @param vert2 Vertice del segundo punto del triángulo.
		 * @param vert3 Vertice del tercer  punto del triángulo.
		 * 
		 * @param text1 Coordenadas de textura del primer  punto del triángulo.
		 * @param text2 Coordenadas de textura del segundo punto del triángulo.
		 * @param text3 Coordenadas de textura del tercer  punto del triángulo.
		 * 
		 * @param norm1 Normal del primer  punto del triángulo.
		 * @param norm2 Normal del segundo punto del triángulo.
		 * @param norm3 Normal del tercer  punto del triángulo.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3
		){
			gl.glNormal3f(   norm1.x, norm1.y, norm1.z );
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(   norm2.x, norm2.y, norm2.z );
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(   norm3.x, norm3.y, norm3.z );
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl,
					vert1, vert2, vert3, vert4,
					text1, text2, text3, text4,
					norm1, norm2, norm3, norm4
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un cuadrilátero.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del cuadrilatero.
		 * @param vert2 Vertice del segundo punto del cuadrilatero.
		 * @param vert3 Vertice del tercer  punto del cuadrilatero.
		 * @param vert4 Vertice del cuarto punto del cuadrilatero.
		 * 
		 * @param text1 Coordenadas de textura del primer  punto del cuadrilatero.
		 * @param text2 Coordenadas de textura del segundo punto del cuadrilatero.
		 * @param text3 Coordenadas de textura del tercer  punto del cuadrilatero.
		 * @param text4 Coordenadas de textura del cauarto punto del cuadrilatero.
		 * 
		 * @param norm1 Normal del primer  punto del cuadrilatero.
		 * @param norm2 Normal del segundo punto del cuadrilatero.
		 * @param norm3 Normal del tercer  punto del cuadrilatero.
		 * @param norm4 Normal del cuarto punto del cuadrilatero.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4
		){
			gl.glNormal3f(   norm1.x, norm1.y, norm1.z );
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(   norm2.x, norm2.y, norm2.z );
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(   norm3.x, norm3.y, norm3.z );
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f(   norm4.x, norm4.y, norm4.z );
			gl.glTexCoord2f( text4.x, text4.y );
			gl.glVertex3f(   vert4.x, vert4.y, vert4.z );
		}
	}
	
	/**
	 * Instancia estática de un {@code VerticesNormalsTexCoordsGenerator}
	 * que evita la necesidad de crear más instancias.
	 */
	public static final Generator VerticesNormalsTexCoords = new VerticesNormalsTexCoordsGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que contienen: tanto sus vértices, sus coordenadas de textura, 
	 * sus normales, como sus tangentes.
	 */
	private static class VerticesTangentsGenerator extends Generator{
		
		VerticesTangentsGenerator(){}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			gl.glNormal3f(          norm1.x, norm1.y, norm1.z );
			gl.glVertexAttrib4f( 1, tang1.x, tang1.y, tang1.z, tang1.w );
			gl.glTexCoord2f(        text1.x, text1.y );
			gl.glVertex3f(          vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(          norm2.x, norm2.y, norm2.z );
			gl.glVertexAttrib4f( 1, tang2.x, tang2.y, tang2.z, tang2.w );
			gl.glTexCoord2f(        text2.x, text2.y );
			gl.glVertex3f(          vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(          norm3.x, norm3.y, norm3.z );
			gl.glVertexAttrib4f( 1, tang3.x, tang3.y, tang3.z, tang3.w );
			gl.glTexCoord2f(        text3.x, text3.y );
			gl.glVertex3f(          vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			gl.glNormal3f(          norm1.x, norm1.y, norm1.z );
			gl.glVertexAttrib4f( 1, tang1.x, tang1.y, tang1.z, tang1.w );
			gl.glTexCoord2f(        text1.x, text1.y );
			gl.glVertex3f(          vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(          norm2.x, norm2.y, norm2.z );
			gl.glVertexAttrib4f( 1, tang2.x, tang2.y, tang2.z, tang2.w );
			gl.glTexCoord2f(        text2.x, text2.y );
			gl.glVertex3f(          vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(          norm3.x, norm3.y, norm3.z );
			gl.glVertexAttrib4f( 1, tang3.x, tang3.y, tang3.z, tang3.w );
			gl.glTexCoord2f(        text3.x, text3.y );
			gl.glVertex3f(          vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f(          norm4.x, norm4.y, norm4.z );
			gl.glVertexAttrib4f( 1, tang4.x, tang4.y, tang4.z, tang4.w );
			gl.glTexCoord2f(        text4.x, text4.y );
			gl.glVertex3f(          vert4.x, vert4.y, vert4.z );
		}
	}
	
	/**
	 * Instancia estática de un {@code VerticesTangentsGenerator} que evita la necesidad
	 * de crear más instancias.
	 */
	public static final Generator VerticesTangents = new VerticesTangentsGenerator();

	/**
	 * Implementación de un {@code Generator}, que  partir de los valores
	 * de las primitivas, genera las líneas que representan: tanto la normal, 
	 * la tangente, como la bitangente, correspondientes a cada vértice.<br/>
	 * Útil para poder visualizar valores al realizar test.
	 */
	public static final class NTBGenerator extends Generator{
	
		/**
		 * Longintud de las líneas generadas que representan la normal, la tangente y la bitangente.
		 */
		float length;
		
		/**
		 * Constructor por defecto con la una longitud de línea = 1.
		 */
		NTBGenerator(){
			this(1.0f);
		}
		
		/**
		 * Constructor que asiga la longitud de las líneas que recibe como parámetro.
		 * @param length Valor de escalado asignado.
		 */
		public NTBGenerator(float length) {
			this.length = length;
		}
		
		private transient final Vector3f bita1 = new Vector3f();
		private transient final Vector3f bita2 = new Vector3f();
		private transient final Vector3f bita3 = new Vector3f();
		private transient final Vector3f bita4 = new Vector3f();
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			bita1.set(
		        	(norm1.y*tang1.z - norm1.z*tang1.y)*Math.signum(tang1.w),
		        	(tang1.x*norm1.z - tang1.z*norm1.x)*Math.signum(tang1.w),
		        	(norm1.x*tang1.y - norm1.y*tang1.x)*Math.signum(tang1.w)
				);
			bita2.set(
		        	(norm2.y*tang2.z - norm2.z*tang2.y)*Math.signum(tang2.w),
		        	(tang2.x*norm2.z - tang2.z*norm2.x)*Math.signum(tang2.w),
		        	(norm2.x*tang2.y - norm2.y*tang2.x)*Math.signum(tang2.w)
				);
			bita3.set(
		        	(norm3.y*tang3.z - norm3.z*tang3.y)*Math.signum(tang3.w),
		        	(tang3.x*norm3.z - tang3.z*norm3.x)*Math.signum(tang3.w),
		        	(norm3.x*tang3.y - norm3.y*tang3.x)*Math.signum(tang3.w)
				);
			
			generate( gl,
					vert1, vert2, vert3,
					norm1, norm2, norm3,
					tang1, tang2, tang3,
					bita1, bita2, bita3
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un triángulo.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del triángulo.
		 * @param vert2 Vertice del segundo punto del triángulo.
		 * @param vert3 Vertice del tercer  punto del triángulo.
		 * 
		 * @param norm1 Normal del primer  punto del triángulo.
		 * @param norm2 Normal del segundo punto del triángulo.
		 * @param norm3 Normal del tercer  punto del triángulo.
		 * 
		 * @param tang1 Tangente del primer  punto del triángulo.
		 * @param tang2 Tangente del segundo punto del triángulo.
		 * @param tang3 Tangente del tercer  punto del triángulo.
		 * 
		 * @param bita1 Bitangente del primer  punto del triángulo.
		 * @param bita2 Bitangente del segundo punto del triángulo.
		 * @param bita3 Bitangente del tercer  punto del triángulo.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3,
				Vector3f   bita1, Vector3f   bita2, Vector3f   bita3
		){	
			gl.glColor3f(0.5f, 1.0f, 0.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + norm1.x * length, vert1.y + norm1.y * length, vert1.z + norm1.z * length );
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + norm2.x * length, vert2.y + norm2.y * length, vert2.z + norm2.z * length );
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + norm3.x * length, vert3.y + norm3.y * length, vert3.z + norm3.z * length );
	
			gl.glColor3f(1.0f, 0.0f, 0.5f);
	
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + tang1.x * length, vert1.y + tang1.y * length, vert1.z + tang1.z * length );
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + tang2.x * length, vert2.y + tang2.y * length, vert2.z + tang2.z * length );
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + tang3.x * length, vert3.y + tang3.y * length, vert3.z + tang3.z * length );
	
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + bita1.x * length, vert1.y + bita1.y * length, vert1.z + bita1.z * length);
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + bita2.x * length, vert2.y + bita2.y * length, vert2.z + bita2.z * length);
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + bita3.x * length, vert3.y + bita3.y * length, vert3.z + bita3.z * length);
		}
		
		@Override
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			bita1.set(
		        	(norm1.y*tang1.z - norm1.z*tang1.y)*Math.signum(tang1.w),
		        	(tang1.x*norm1.z - tang1.z*norm1.x)*Math.signum(tang1.w),
		        	(norm1.x*tang1.y - norm1.y*tang1.x)*Math.signum(tang1.w)
				);
			bita2.set(
		        	(norm2.y*tang2.z - norm2.z*tang2.y)*Math.signum(tang2.w),
		        	(tang2.x*norm2.z - tang2.z*norm2.x)*Math.signum(tang2.w),
		        	(norm2.x*tang2.y - norm2.y*tang2.x)*Math.signum(tang2.w)
				);
			bita3.set(
		        	(norm3.y*tang3.z - norm3.z*tang3.y)*Math.signum(tang3.w),
		        	(tang3.x*norm3.z - tang3.z*norm3.x)*Math.signum(tang3.w),
		        	(norm3.x*tang3.y - norm3.y*tang3.x)*Math.signum(tang3.w)
				);
			bita4.set(
		        	(norm4.y*tang4.z - norm4.z*tang4.y)*Math.signum(tang4.w),
		        	(tang4.x*norm4.z - tang4.z*norm4.x)*Math.signum(tang4.w),
		        	(norm4.x*tang4.y - norm4.y*tang4.x)*Math.signum(tang4.w)
				);
			
			generate( gl,
					vert1, vert2, vert3, vert4,
					norm1, norm2, norm3, norm4,
					tang1, tang2, tang3, tang4,
					bita1, bita2, bita3, bita4
			);
		}
		
		/**
		 * Método que genera primitivas a partir de los datos de un cuadrilátero.
		 * 
		 * @param gl Contexto gráfico en el que se realiza a acción.
		 * 
		 * @param vert1 Vertice del primer  punto del cuadrilatero.
		 * @param vert2 Vertice del segundo punto del cuadrilatero.
		 * @param vert3 Vertice del tercer  punto del cuadrilatero.
		 * @param vert4 Vertice del cuarto punto del cuadrilatero.
		 * 
		 * @param norm1 Normal del primer  punto del cuadrilatero.
		 * @param norm2 Normal del segundo punto del cuadrilatero.
		 * @param norm3 Normal del tercer  punto del cuadrilatero.
		 * @param norm4 Normal del cuarto punto del cuadrilatero.
		 * 
		 * @param tang1 Tangente del primer  punto del cuadrilatero.
		 * @param tang2 Tangente del segundo punto del cuadrilatero.
		 * @param tang3 Tangente del tercer  punto del cuadrilatero.
		 * @param tang4 Tangente del cuarto punto del cuadrilatero.
		 * 
		 * @param bita1 Bitangente del primer  punto del cuadrilatero.
		 * @param bita2 Bitangente del segundo punto del cuadrilatero.
		 * @param bita3 Bitangente del tercer  punto del cuadrilatero.
		 * @param bita4 Bitangente del cauarto punto del cuadrilatero.
		 */
		public void generate(GL2 gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4,
				Vector3f   bita1, Vector3f   bita2, Vector3f   bita3, Vector3f   bita4
		){	
			gl.glColor3f(0.5f, 1.0f, 0.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + norm1.x * length, vert1.y + norm1.y * length, vert1.z + norm1.z * length );
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + norm2.x * length, vert2.y + norm2.y * length, vert2.z + norm2.z * length );
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + norm3.x * length, vert3.y + norm3.y * length, vert3.z + norm3.z * length );
	
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + norm4.x * length, vert4.y + norm4.y * length, vert4.z + norm4.z * length );
	
			gl.glColor3f(1.0f, 0.0f, 0.5f);
	
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + tang1.x * length, vert1.y + tang1.y * length, vert1.z + tang1.z * length );
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + tang2.x * length, vert2.y + tang2.y * length, vert2.z + tang2.z * length );
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + tang3.x * length, vert3.y + tang3.y * length, vert3.z + tang3.z * length );
	
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + tang4.x * length, vert4.y + tang4.y * length, vert4.z + tang4.z * length );
	
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + bita1.x * length, vert1.y + bita1.y * length, vert1.z + bita1.z * length);
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + bita2.x * length, vert2.y + bita2.y * length, vert2.z + bita2.z * length);
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + bita3.x * length, vert3.y + bita3.y * length, vert3.z + bita3.z * length);
	
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + bita4.x * length, vert4.y + bita4.y * length, vert4.z + bita4.z * length );
		}
	}
	
	/**
	 * Instancia estática de un {@code NTBGenerator} que evita la necesidad
	 * de crear más instancias.
	 */
	public static final NTBGenerator NTB = new NTBGenerator();
	
	/**
	 * Método que genera primitivas a partir de los datos de un triángulo.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * 
	 * @param vert1 Vertice del primer  punto del triángulo.
	 * @param vert2 Vertice del segundo punto del triángulo.
	 * @param vert3 Vertice del tercer  punto del triángulo.
	 * 
	 * @param text1 Coordenadas de textura del primer  punto del triángulo.
	 * @param text2 Coordenadas de textura del segundo punto del triángulo.
	 * @param text3 Coordenadas de textura del tercer  punto del triángulo.
	 * 
	 * @param norm1 Normal del primer  punto del triángulo.
	 * @param norm2 Normal del segundo punto del triángulo.
	 * @param norm3 Normal del tercer  punto del triángulo.
	 * 
	 * @param tang1 Tangente del primer  punto del triángulo.
	 * @param tang2 Tangente del segundo punto del triángulo.
	 * @param tang3 Tangente del tercer  punto del triángulo.
	 */
	public abstract void generate(GL2 gl,
			Point3f    vert1, Point3f    vert2, Point3f    vert3,
			TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
			Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
			Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
	);
	
	/**
	 * Método que genera primitivas a partir de los datos de un cuadrilátero.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * 
	 * @param vert1 Vertice del primer  punto del cuadrilátero.
	 * @param vert2 Vertice del segundo punto del cuadrilátero.
	 * @param vert3 Vertice del tercer  punto del cuadrilátero.
	 * @param vert4 Vertice del cuarto punto del cuadrilátero.
	 * 
	 * @param text1 Coordenadas de textura del primer  punto del cuadrilátero.
	 * @param text2 Coordenadas de textura del segundo punto del cuadrilátero.
	 * @param text3 Coordenadas de textura del tercer  punto del cuadrilátero.
	 * @param text4 Coordenadas de textura del cauarto punto del cuadrilátero.
	 * 
	 * @param norm1 Normal del primer  punto del cuadrilátero.
	 * @param norm2 Normal del segundo punto del cuadrilátero.
	 * @param norm3 Normal del tercer  punto del cuadrilátero.
	 * @param norm4 Normal del cuarto punto del cuadrilátero.
	 * 
	 * @param tang1 Tangente del primer  punto del cuadrilátero.
	 * @param tang2 Tangente del segundo punto del cuadrilátero.
	 * @param tang3 Tangente del tercer  punto del cuadrilátero.
	 * @param tang4 Tangente del cuarto punto del cuadrilátero.
	 */
	public abstract void generate(GL2 gl,
			Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
			TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
			Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
			Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
	);
}
