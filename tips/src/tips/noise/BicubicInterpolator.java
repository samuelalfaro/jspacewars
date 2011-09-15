/* 
 * BicubicInterpolator.java
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
package tips.noise;

/**
 *
 * @author Samuel Alfaro
 */
public class BicubicInterpolator {
	
	/** Catmull-Rom basis matrix */
	private static final double[][] M = {
		{-0.5,  1.5, -1.5, 0.5},
		{ 1  , -2.5,  2  ,-0.5},
		{-0.5,  0  ,  0.5, 0  },
		{ 0  ,  1  ,  0  , 0  }
	};

	private double 
		c00 ,c01, c02, c03,
		c10, c11, c12, c13,
		c20, c21, c22, c23,
		c30, c31, c32, c33;
	
	/**
	 * Generates a two dimensional surface using bicubic interpolation.
	 * The value is calculated using the position of the point and the values of the 16 surrounding points.
	 * The coordinate system works this way: The point G[x][y] has position (x-1, y-1).
	 * For example, if you call this function with x=0 and y=1, G[1][2] will be returned.
	 * 
	 * @param G A 4x4 array containing the values of the 16 surrounding points
	 */
	BicubicInterpolator( double[][] G ){

		double[][] T = new double[4][4];
		for (int i = 0 ; i < 4 ; i++)    	// T = G * MT
			for (int j = 0 ; j < 4 ; j++)
				for (int k = 0 ; k < 4 ; k++)
					T[i][j] += G[i][k] * M[j][k];

		double[][] C = new double[4][4];    // coefficients matrix
		for (int i = 0 ; i < 4 ; i++)    	// C = M * T
			for (int j = 0 ; j < 4 ; j++)
				for (int k = 0 ; k < 4 ; k++)
					C[i][j] += M[i][k] * T[k][j];
		
		c00 = C[0][0]; c01 = C[0][1]; c02 = C[0][2]; c03 = C[0][3];
		c10 = C[1][0]; c11 = C[1][1]; c12 = C[1][2]; c13 = C[1][3];
		c20 = C[2][0]; c21 = C[2][1]; c22 = C[2][2]; c23 = C[2][3];
		c30 = C[3][0]; c31 = C[3][1]; c32 = C[3][2]; c33 = C[3][3];
	}

	/**
	 * Interpolates the value of a point in a two dimensional surface using bicubic interpolation.
	 * You can pass any value for x and y, but you usually want them to be between 0 and 1.
	 * Note that the returned value can be more or less than any of the values of the surrounding points. 
	 * 
	 * @param x The horizontal position, between 0 and 1
	 * @param y The vertical position, between 0 and 1
	 * @return the interpolated value
	 */
	public double interpolate( double x, double y ){
		return 
			x * ( x * ( x * (y * (y * (y * c00 + c01) + c02) + c03)
			              + (y * (y * (y * c10 + c11) + c12) + c13)
			          )   + (y * (y * (y * c20 + c21) + c22) + c23)
			    )         + (y * (y * (y * c30 + c31) + c32) + c33);
	}
}
