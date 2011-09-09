/* 
 * Emisor.java
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
package org.sam.jogl.particulas;

import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;


@SuppressWarnings("serial")
public interface Emisor {

	static final Random ALEATORIO = new Random(){
		@Override
		public int nextInt(int nBits) {
			return next(nBits);
		}
	};
	
	public static class Cache implements Emisor{

		private static int log2(int val){
			assert (val >= 0);
			val--;
			int n=0;
			while(val>0){
				val >>=1;
				n++;
			}
			return n;
		}

		private transient final int nBits;
		private transient final float[] pos;
		private transient final float[] dir;
		private transient final VectorLibre v;

		public Cache(Emisor e, int size){
			this.nBits = log2(size);
			size = (int)Math.pow(2,nBits);
			pos = new float[size*3];
			dir = new float[size*3];
			for(int i = 0, j=0; i<size; i++){
				VectorLibre aux = e.emite();
				pos[j]= aux.posicion.x; dir[j++]= aux.direccion.x;
				pos[j]= aux.posicion.y; dir[j++]= aux.direccion.y;
				pos[j]= aux.posicion.z; dir[j++]= aux.direccion.z;
			}
			v = new VectorLibre();
		}

		public Cache(Emisor e, Matrix4f t, int size){
			this.nBits = log2(size);
			size = (int)Math.pow(2,nBits);
			pos = new float[size*3];
			dir = new float[size*3];
			for(int i = 0, j=0; i<size; i++){
				VectorLibre aux = e.emite();
				t.transform(aux.posicion);
				t.transform(aux.direccion);
				pos[j]= aux.posicion.x; dir[j++]= aux.direccion.x;
				pos[j]= aux.posicion.y; dir[j++]= aux.direccion.y;
				pos[j]= aux.posicion.z; dir[j++]= aux.direccion.z;
			}
			v = new VectorLibre();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public VectorLibre emite(){
			int i = ALEATORIO.nextInt(nBits)*3;
			v.posicion.x = pos[i]; v.direccion.x = dir[i++];
			v.posicion.y = pos[i]; v.direccion.y = dir[i++];
			v.posicion.z = pos[i]; v.direccion.z = dir[i++];
			return v;
		}
	}

	public static class Conico implements Emisor{
		private static final float dosPI = (float)( 2 * Math.PI);
		
		private final float radio;
		private final float altura;
		private final transient VectorLibre v;

		public Conico(){
			this( 1.0f, 1.0f );
		}

		public Conico(float radio, float altura){
			this.radio  = radio;
			this.altura = altura;
			v = new VectorLibre();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public VectorLibre emite(){
			// distribucion homogenea de los puntos en el circulo.
			// float r  = (float)Math.sqrt(ALEATORIO.nextFloat()) * radio;
			float r  = ALEATORIO.nextFloat() * radio;
			float a = ALEATORIO.nextFloat() * dosPI;
			v.posicion.set(0.0f, r * (float)Math.sin(a), r * (float)Math.cos(a));

			v.direccion.x = altura;
			v.direccion.y = v.posicion.y;
			v.direccion.z = v.posicion.z;
			v.direccion.normalize();

			return v;
		}
	}

	public static class Lineal implements Emisor{

		private final float longitud;
		private final float rangoGiro;
		private final transient VectorLibre v;

		public Lineal(){
			this( 1.0f, 360.0f );
		}

		public Lineal(float longitud, float rangoGiro){
			this.longitud = longitud;
			this.rangoGiro = (float)(rangoGiro * Math.PI /180.0);
			v = new VectorLibre();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public VectorLibre emite(){
			v.posicion.set(0.0f,(ALEATORIO.nextFloat()-0.5f)*longitud, 0.0f);
			float a = (ALEATORIO.nextFloat()-0.5f)*rangoGiro;
			v.direccion.set((float)Math.cos(a), 0.0f, (float)Math.sin(a));
			return v;
		}
	}

	public class Piramidal implements Emisor{

		private final float lado;
		private final float altura;
		private final transient VectorLibre v;

		public Piramidal(){
			this(1.0f,1.0f);
		}

		public Piramidal(float lado, float altura){
			this.lado   = lado;
			this.altura = altura;
			v = new VectorLibre();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public VectorLibre emite(){
			v.posicion.set(0.0f,(ALEATORIO.nextFloat()-0.5f)*lado,(ALEATORIO.nextFloat()-0.5f)*lado);

			v.direccion.x = altura;
			v.direccion.y = v.posicion.y;
			v.direccion.z = v.posicion.z;
			v.direccion.normalize();

			return v;
		}
	}

	public static class Puntual implements Emisor{

		private final float rangoEje;
		private final float rangoInf;
		private final float rangoGiro;
		private final transient VectorLibre v;

		public Puntual(){
			this( 1.0f, -1.0f, 360.0f );
		}

		public Puntual( float rangoSup, float rangoInf, float rangoGiro ){
			this.rangoEje = rangoSup - rangoInf;
			this.rangoInf = rangoInf;
			this.rangoGiro = (float)( rangoGiro * Math.PI / 180.0 );
			v = new VectorLibre();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public VectorLibre emite(){
			v.posicion.set( 0.0f, 0.0f, 0.0f );

			Vector3f direccion = v.direccion;
			direccion.y = rangoEje * ALEATORIO.nextFloat() + rangoInf;
			float r = (float)Math.sqrt( 1 - direccion.y * direccion.y );
			float a = ( ALEATORIO.nextFloat() - 0.5f ) * rangoGiro;
			direccion.x = r * (float)Math.cos( a );
			direccion.z = r * (float)Math.sin( a );
			return v;
		}
	}

	public VectorLibre emite();
}


