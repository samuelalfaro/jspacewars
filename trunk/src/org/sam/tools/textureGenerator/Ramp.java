/* 
 * Ramp.java
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
public interface Ramp {
	
	public enum Predefinas implements Ramp{
		
		LINEAL{
			/**
			 * @inheritDoc
			 */
			@Override
			public double compute(double alpha){
				return alpha;
			}
		},
		
		ACELERADA{
			/**
			 * @inheritDoc
			 */
			@Override
			public double compute(double alpha){
				return 1.0 + Math.sin( Math.PI/2 * ( alpha - 1.0) );
			}
		},
		
		DECELERADA{
			/**
			 * @inheritDoc
			 */
			@Override
			public double compute(double alpha){
				return Math.sin( Math.PI/2 * alpha );
			}
		},
		
		ACELERADA_DECELERADA{
			/**
			 * @inheritDoc
			 */
			@Override
			public double compute(double alpha){
				return Math.sin( Math.PI * ( alpha - 0.5) )/2.0 + 0.5;
			}
		},
		
		DECELERADA_ACELERADA{
			/**
			 * @inheritDoc
			 */
			@Override
			public double compute(double alpha){
				return Math.tan( Math.PI/2 * ( alpha - 0.5))/2.0 + 0.5;
			}
		};

		public double compute(int i, int frames) {
			return compute( (double)i/frames );
		}
		
		public double compute(double min, double max, double alpha){
			return ( max - min ) * compute( alpha ) + min;
		}
		
		public double compute(double min, double max, int i, int frames){
			return ( max - min ) * compute( (double)i/frames ) + min;
		}
	}
	
	public double compute(double alpha);
	
	public double compute(int i, int frames);
	
	public double compute(double min, double max, double alpha);
	
	public double compute(double min, double max, int i, int frames);
}