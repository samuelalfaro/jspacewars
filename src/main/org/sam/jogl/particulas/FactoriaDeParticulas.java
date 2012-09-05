/* 
 * FactoriaDeParticulas.java
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

public class FactoriaDeParticulas{

	private static boolean pointSpritesEnabled = false;
	private static boolean shadersEnabled = false;
	private static boolean optimizedFor2D = false;

	public static Particulas createParticulas( int nParticulas ){
		return optimizedFor2D	?
				new ParticulasLibres2D(nParticulas) :
				//new Particulas2D(nParticulas) :
				new ParticulasQuads(nParticulas);
	}
	
	public static boolean isPointSpritesEnabled(){
		return pointSpritesEnabled;
	}

	/**
	 * @param pointSpritesEnabled ignorado operacion no soportada.
	 */
	public static void setPointSpritesEnabled( boolean pointSpritesEnabled ){
		throw new UnsupportedOperationException();
		//TODO implementar o eliminar completamente.
		//FactoriaDeParticulas.pointSpritesEnabled = pointSpritesEnabled;
	}

	public static boolean isShadersEnabled(){
		return shadersEnabled;
	}

	/**
	 * @param shadersEnabled ignorado operacion no soportada.
	 */
	public static void setShadersEnabled( boolean shadersEnabled ){
		throw new UnsupportedOperationException();
		//TODO implementar o eliminar completamente
		//FactoriaDeParticulas.shadersEnabled = shadersEnabled;
	}

	public static boolean isOptimizedFor2D(){
		return optimizedFor2D;
	}

	public static void setOptimizedFor2D( boolean optimizedFor2D ){
		FactoriaDeParticulas.optimizedFor2D = optimizedFor2D;
	}
}
