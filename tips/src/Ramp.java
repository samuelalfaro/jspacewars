
/* 
 * Ramp.java
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
public interface Ramp {
	
	abstract class  AbsRamp implements Ramp{
		public double compute(double min, double max, int i, int frames){
			return ( max - min ) * compute( i, frames) + min;
		}
	}
	
	public final Ramp Lineal = new AbsRamp(){

		/* (non-Javadoc)
		 * @see Ramp#compute(int, int)
		 */
		@Override
		public double compute(int i, int frames) {
			return (double)i/frames;
		}
	};
	
	public final Ramp Acelerada = new AbsRamp(){
		/* (non-Javadoc)
		 * @see Ramp#compute(int, int)
		 */
		@Override
		public double compute(int i, int frames) {
			return 1.0 + Math.sin( Math.PI/2 * ( ((double)i/frames) - 1.0) );
		}
	};
	
	public final Ramp Decelerada = new AbsRamp(){
		/* (non-Javadoc)
		 * @see Ramp#compute(int, int)
		 */
		@Override
		public double compute(int i, int frames) {
			return Math.sin( Math.PI/2 * ((double)i/frames) );
		}
	};
	
	public final Ramp AceleradaDecelerada = new AbsRamp(){
		/* (non-Javadoc)
		 * @see Ramp#compute(int, int)
		 */
		@Override
		public double compute(int i, int frames) {
			return Math.sin( Math.PI * (((double)i/frames) - 0.5) )/2.0 + 0.5;
		}
	};
	
	public final Ramp DeceleradaAcelerada = new AbsRamp(){
		/* (non-Javadoc)
		 * @see Ramp#compute(int, int)
		 */
		@Override
		public double compute(int i, int frames) {
			return Math.tan( Math.PI/2 * (((double)i/frames) - 0.5))/2.0 + 0.5;
		}
	};
	
	public double compute(int i, int frames);
	
	public double compute(double min, double max, int i, int frames);
}