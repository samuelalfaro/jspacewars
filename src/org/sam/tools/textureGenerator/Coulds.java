/* 
 * Coulds.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sam.tools.textureGenerator;

/**
 *
 * @author Samuel Alfaro
 */
public class Coulds {

	int width;
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	int octaves;
	/**
	 * @param octaves the octaves to set
	 */
	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

	double frequency;
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	double harmonicScale;
	/**
	 * @param harmonicScale the harmonicScale to set
	 */
	public void setHarmonicScale(double harmonicScale) {
		this.harmonicScale = harmonicScale;
	}

	double amplitude;
	/**
	 * @param amplitude the amplitude to set
	 */
	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}

	double persistence;
	/**
	 * @param persistence the persistence to set
	 */
	public void setPersistence(double persistence) {
		this.persistence = persistence;
	}

	boolean absPartial;
	/**
	 * @param absPartial the absPartial to set
	 */
	public void setAbsPartial(boolean absPartial) {
		this.absPartial = absPartial;
	}

	double z;
	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	public Coulds(){
		width = 256;
		octaves = 6;
		frequency = 4.0;
//		harmonicScale = Math.pow(1.0/frequency, 1.0/(octaves-1));
		harmonicScale = 2.0;
		amplitude = 1.0;
		persistence = Math.pow(.02, 1.0/(octaves-1));
//		persistence = .5;
		absPartial = false;
		z = 0;
	}
	
	public double calculate(int x, int y) {
		double freq = frequency/width;
		double ampl = amplitude;
		double total = 0.0;
		for( int lcv = 0; lcv < octaves; lcv++ ){
			double partial = SimplexNoise.noise( x * freq, y * freq, z)  * ampl;
			total += (partial < 0.0 && absPartial) ? -partial: partial;
			freq *= harmonicScale;
			ampl *= persistence;
		}
		return total;
	}
}
