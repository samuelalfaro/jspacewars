/* 
 * Coulds.java
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
package org.sam.tools.textureGenerator;

/**
 *
 * @author Samuel Alfaro
 */
public class Coulds{

	int width;
	int height;

	/**
	 * @param width
	 * @param height
	 */
	public void setDimensions( int width, int height ){
		this.width = width;
		this.height = height;
	}

	int octaves;

	public void setOctaves( int octaves ){
		this.octaves = octaves;
	}

	double frequency;

	public void setFrequency( double frequency ){
		this.frequency = frequency;
	}

	double harmonicScale;

	public void setHarmonicScale( double harmonicScale ){
		this.harmonicScale = harmonicScale;
	}

	double amplitude;

	public void setAmplitude( double amplitude ){
		this.amplitude = amplitude;
	}

	double persistence;

	public void setPersistence( double persistence ){
		this.persistence = persistence;
	}

	boolean absPartial;

	public void setAbsPartial( boolean absPartial ){
		this.absPartial = absPartial;
	}

	double z;

	public void setZ( double z ){
		this.z = z;
	}

	public Coulds(){
		width = height = 256;
		octaves = 6;
		frequency = 4.0;
		//		harmonicScale = Math.pow(1.0/frequency, 1.0/(octaves-1));
		harmonicScale = 2.0;
		amplitude = 1.0;
		persistence = Math.pow( .02, 1.0 / ( octaves - 1 ) );
		//		persistence = .5;
		absPartial = false;
		z = 0;
	}
	
	public double calculate( int x, int y ){
		double freq = frequency / width;
		double ampl = amplitude;
		double total = 0.0;
		for( int lcv = 0; lcv < octaves; lcv++ ){
			double partial = SimplexNoise.noise( x * freq, y * freq, z ) * ampl;
			total += ( partial < 0.0 && absPartial ) ? -partial: partial;
			freq *= harmonicScale;
			ampl *= persistence;
		}
		return total;
	}
}
