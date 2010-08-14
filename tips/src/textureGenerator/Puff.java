package textureGenerator;
/* 
 * Puff.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of tips.
 * 
 * tips is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * tips is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tips.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Samuel Alfaro
 */
public class Puff extends Coulds {
	
	int x0 , y0;
	int radius1, radius2;
	
	public Puff(){
		caluculateRadiuses();
		calculateIncRadius();
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		caluculateRadiuses();
		calculateIncRadius();
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void setOctaves(int octaves) {
		super.setOctaves(octaves);
		calculateIncRadius();
	}
	
	private void caluculateRadiuses(){
		radius2 = x0 = y0 = width/2;
		radius1 = 3*radius2/4;
	}
	
	private double incRadius;
	private void calculateIncRadius(){
		if( this.octaves < 2){
			incRadius = 0;
			radius1 = radius2;
		}else
			incRadius = ((double)(radius2 - radius1))/(this.octaves -1);
	}
	
	/* (non-Javadoc)
	 * @see PerlinNoise#generate(int, int)
	 */
	@Override
	public double calculate(int x, int y) {
		
		double total = 0.0;
		double freq = this.frequency/width;
		double ampl = this.amplitude;
		
		double len = Math.sqrt( Math.pow( x - x0, 2 ) + Math.pow( y - y0, 2 ) );
		
		for( int i = 0; i < this.octaves; i++ ){
			double radius =  incRadius * i + radius1;
			double partial = (len > radius) ? 
					0 :
					(radius - len )/radius *
					( SimplexNoise.noise( x * freq, y * freq, this.z) ) *
					ampl;
			total +=  ( partial < 0.0 && this.absPartial ) ? -partial: partial;
			freq *= this.harmonicScale;
			ampl *= this.persistence;
		}
		return Math.abs(total);
	}
}
