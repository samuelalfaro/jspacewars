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

import javax.media.opengl.GL;
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
public interface Generator {

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que sólo contienen sus vértices. 
	 */
	final class VerticesGenerator implements Generator{
		
		private VerticesGenerator(){
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3
		){
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
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
	final Generator Vertices = new VerticesGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que contienen sus vértices y sus normales. 
	 */
	final class VerticesNormalsGenerator implements Generator{
		
		private VerticesNormalsGenerator(){
		}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
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
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
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
	final Generator VerticesNormals = new VerticesNormalsGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera siempre cuadriláteros,
	 * en los que cada vértice recibe como coordenadas de textura, las coordenadas
	 * de los vértices que forman la primitiva a la que pertenece. Datos
	 * necesarios poder hacer uso del shader <i>SinglePassWireFrame</i>.
	 */
	final class VerticesWireFrameGenerator implements Generator{
		
		private VerticesWireFrameGenerator(){
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4
		){
			gl.glMultiTexCoord4f( 0, vert1.x, vert1.y, vert1.z, 1.0f );
			gl.glMultiTexCoord4f( 1, vert2.x, vert2.y, vert2.z, 1.0f );
			gl.glMultiTexCoord4f( 2, vert3.x, vert3.y, vert3.z, 1.0f );
			gl.glMultiTexCoord4f( 3, vert4.x, vert4.y, vert4.z, 1.0f );
			
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
	 * Instancia estática de un {@code VerticesWireFrameGenerator} 
	 * que evita la necesidad de crear más instancias.
	 */
	final Generator VerticesWireFrame = new VerticesWireFrameGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que contienen sus vértices y sus coordenadas de textura.
	 */
	final class VerticesTexCoordsGenerator implements Generator{
		
		private VerticesTexCoordsGenerator(){
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
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
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
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
	final Generator VerticesTexCoords = new VerticesTexCoordsGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que contienen tanto sus vértices, sus normales, como sus
	 * coordenadas de textura.
	 */
	final class VerticesNormalsTexCoordsGenerator implements Generator{
		
		private VerticesNormalsTexCoordsGenerator(){
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
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
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
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
	final Generator VerticesNormalsTexCoords = new VerticesNormalsTexCoordsGenerator();

	/**
	 * Implementación de un {@code Generator}, que genera primitivas
	 * que contienen: tanto sus vértices, sus coordenadas de textura, 
	 * sus normales, como sus tangentes.
	 */
	final class VerticesTangentsGenerator implements Generator{
		
		private VerticesTangentsGenerator(){
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
	final Generator VerticesTangents = new VerticesTangentsGenerator();

	/**
	 * Implementación de un {@code Generator}, que  partir de los valores
	 * de las primitivas, genera las líneas que representan: tanto la normal, 
	 * la tangente, como la bitangente, correspondientes a cada vértice.<br/>
	 * Útil para poder visualizar valores al realizar test.
	 */
	final class NTBGenerator implements Generator{
	
		/**
		 * Escalado que se aplicará, a las líneas que representan la normal, la tangente y la bitangente.
		 */
		float scale;
		
		/**
		 * Constructor por defecto con la escala = 1.
		 */
		public NTBGenerator(){
			this(1.0f);
		}
		
		/**
		 * Constructor que asiga el valor de escalado que recibe como parámetro.
		 * @param scale Valor de escalado asignado.
		 */
		public NTBGenerator(float scale) {
			this.scale = scale;
		}

		/**
		 * <i>Getter</i> que devuelve valor de escalado de este {@code Generator}.
		 * @return Valor de escalado solicitado.
		 */
		public float getScale() {
			return scale;
		}

		/**
		 * <i>Setter</i> que asigna un valor de escalado a este {@code Generator}.
		 * @param scale Valor de escalado asignado.
		 */
		public void setScale(float scale) {
			this.scale = scale;
		}

		private transient final Vector3f bita1 = new Vector3f();
		private transient final Vector3f bita2 = new Vector3f();
		private transient final Vector3f bita3 = new Vector3f();
		private transient final Vector3f bita4 = new Vector3f();
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3,
				Vector3f   bita1, Vector3f   bita2, Vector3f   bita3
		){	
			gl.glColor3f(0.5f, 1.0f, 0.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + norm1.x * scale, vert1.y + norm1.y * scale, vert1.z + norm1.z * scale );
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + norm2.x * scale, vert2.y + norm2.y * scale, vert2.z + norm2.z * scale );
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + norm3.x * scale, vert3.y + norm3.y * scale, vert3.z + norm3.z * scale );
	
			gl.glColor3f(1.0f, 0.0f, 0.5f);
	
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + tang1.x * scale, vert1.y + tang1.y * scale, vert1.z + tang1.z * scale );
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + tang2.x * scale, vert2.y + tang2.y * scale, vert2.z + tang2.z * scale );
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + tang3.x * scale, vert3.y + tang3.y * scale, vert3.z + tang3.z * scale );
	
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + bita1.x * scale, vert1.y + bita1.y * scale, vert1.z + bita1.z * scale);
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + bita2.x * scale, vert2.y + bita2.y * scale, vert2.z + bita2.z * scale);
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + bita3.x * scale, vert3.y + bita3.y * scale, vert3.z + bita3.z * scale);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void generate(GL gl,
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
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4,
				Vector3f   bita1, Vector3f   bita2, Vector3f   bita3, Vector3f   bita4
		){	
			gl.glColor3f(0.5f, 1.0f, 0.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + norm1.x * scale, vert1.y + norm1.y * scale, vert1.z + norm1.z * scale );
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + norm2.x * scale, vert2.y + norm2.y * scale, vert2.z + norm2.z * scale );
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + norm3.x * scale, vert3.y + norm3.y * scale, vert3.z + norm3.z * scale );
	
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + norm4.x * scale, vert4.y + norm4.y * scale, vert4.z + norm4.z * scale );
	
			gl.glColor3f(1.0f, 0.0f, 0.5f);
	
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + tang1.x * scale, vert1.y + tang1.y * scale, vert1.z + tang1.z * scale );
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + tang2.x * scale, vert2.y + tang2.y * scale, vert2.z + tang2.z * scale );
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + tang3.x * scale, vert3.y + tang3.y * scale, vert3.z + tang3.z * scale );
	
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + tang4.x * scale, vert4.y + tang4.y * scale, vert4.z + tang4.z * scale );
	
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + bita1.x * scale, vert1.y + bita1.y * scale, vert1.z + bita1.z * scale);
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + bita2.x * scale, vert2.y + bita2.y * scale, vert2.z + bita2.z * scale);
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + bita3.x * scale, vert3.y + bita3.y * scale, vert3.z + bita3.z * scale);
	
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + bita4.x * scale, vert4.y + bita4.y * scale, vert4.z + bita4.z * scale );
		}
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
	 * 
	 * @param tang1 Tangente del primer  punto del triángulo.
	 * @param tang2 Tangente del segundo punto del triángulo.
	 * @param tang3 Tangente del tercer  punto del triángulo.
	 */
	public void generate(GL gl,
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
	public void generate(GL gl,
			Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
			TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
			Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
			Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
	);
}
