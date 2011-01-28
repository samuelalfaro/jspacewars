/* 
 * Ramp.java
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
public interface Ramp {
	
	public enum Predefinidas implements Ramp{
		
		LINEAL{
			@Override
			public double compute(double alpha){
				return alpha;
			}
		},
		
		ACELERADA{
			@Override
			public double compute(double alpha){
				return 1.0 + Math.sin( Math.PI/2 * ( alpha - 1.0) );
			}
		},
		
		DECELERADA{
			@Override
			public double compute(double alpha){
				return Math.sin( Math.PI/2 * alpha );
			}
		},
		
		ACELERADA_DECELERADA{
			@Override
			public double compute(double alpha){
				return Math.sin( Math.PI * ( alpha - 0.5) )/2.0 + 0.5;
			}
		},
		
		DECELERADA_ACELERADA{
			@Override
			public double compute(double alpha){
				return Math.tan( Math.PI/2 * ( alpha - 0.5))/2.0 + 0.5;
			}
		};

		/* (non-Javadoc)
		 * @see org.sam.tools.textureGenerator.Ramp#compute(int, int)
		 */
		@Override
		public final double compute(int i, int frames) {
			return compute(  frames < 2 ? 0 : (double)i/(frames-1) );
		}
		
		/* (non-Javadoc)
		 * @see org.sam.tools.textureGenerator.Ramp#compute(double, double, double)
		 */
		@Override
		public final double compute(double min, double max, double alpha){
			return ( max - min ) * compute( alpha ) + min;
		}
		
		/* (non-Javadoc)
		 * @see org.sam.tools.textureGenerator.Ramp#compute(double, double, int, int)
		 */
		@Override
		public final double compute(double min, double max, int i, int frames){
			return ( max - min ) * compute( frames < 2 ? 0 : (double)i/(frames-1) ) + min;
		}
	}
	
	public double compute(double alpha);
	
	public double compute(int i, int frames);
	
	public double compute(double min, double max, double alpha);
	
	public double compute(double min, double max, int i, int frames);
}
