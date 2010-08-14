package textureGenerator;
/* 
 * TricubicInterpolator.java
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
public class TricubicInterpolator {
	
	@SuppressWarnings("unused")
	private static final double[][] M = {             // Catmull-Rom basis matrix
		{-0.5,  1.5, -1.5, 0.5},
		{ 1  , -2.5,  2  ,-0.5},
		{-0.5,  0  ,  0.5, 0  },
		{ 0  ,  1  ,  0  , 0  }
	};

	double 
		c000 ,c001, c002, c003,
		c010, c011, c012, c013,
		c020, c021, c022, c023,
		c030, c031, c032, c033,
		
		c100 ,c101, c102, c103,
		c110, c111, c112, c113,
		c120, c121, c122, c123,
		c130, c131, c132, c133,
	
		c200 ,c201, c202, c203,
		c210, c211, c212, c213,
		c220, c221, c222, c223,
		c230, c231, c232, c233,
		
		c300 ,c301, c302, c303,
		c310, c311, c312, c313,
		c320, c321, c322, c323,
		c330, c331, c332, c333;
	
	/**
	 * Generates a two dimensional surface using bicubic interpolation.
	 * The value is calculated using the position of the point and the values of the 16 surrounding points.
	 * The coordinate system works this way: The point G[x][y] has position (x-1, y-1).
	 * For example, if you call this function with x=0 and y=1, G[1][2] will be returned.
	 * 
	 * @param G A 4x4x4 array containing the values of the 64 surrounding points
	 */
	TricubicInterpolator(double[][][] G){

//		double[][] T = new double[4][4];
//		for (int i = 0 ; i < 4 ; i++)    	// T = G * MT
//			for (int j = 0 ; j < 4 ; j++)
//				for (int k = 0 ; k < 4 ; k++)
//					T[i][j] += G[i][k] * M[j][k];
//
//		double[][] C = new double[4][4];    // coefficients matrix
//		for (int i = 0 ; i < 4 ; i++)    	// C = M * T
//			for (int j = 0 ; j < 4 ; j++)
//				for (int k = 0 ; k < 4 ; k++)
//					C[i][j] += M[i][k] * T[k][j];
//		
//		c00 = C[0][0]; c01 = C[0][1]; c02 = C[0][2]; c03 = C[0][3];
//		c10 = C[1][0]; c11 = C[1][1]; c12 = C[1][2]; c13 = C[1][3];
//		c20 = C[2][0]; c21 = C[2][1]; c22 = C[2][2]; c23 = C[2][3];
//		c30 = C[3][0]; c31 = C[3][1]; c32 = C[3][2]; c33 = C[3][3];
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
	public double interpolate (double x, double y, double z) {
		return
			x * ( x * ( x * 
			( y * ( y * ( y * (z * (z * (z * c000 + c001) + c002) + c003)
			                + (z * (z * (z * c010 + c011) + c012) + c013)
			            )   + (z * (z * (z * c020 + c021) + c022) + c023)
			      )         + (z * (z * (z * c030 + c031) + c032) + c033))
			+    
			( y * ( y * ( y * (z * (z * (z * c100 + c101) + c102) + c103)
			                + (z * (z * (z * c110 + c111) + c112) + c113)
			            )   + (z * (z * (z * c120 + c121) + c122) + c123)
			      )         + (z * (z * (z * c130 + c131) + c132) + c133))
			) +    
			( y * ( y * ( y * (z * (z * (z * c200 + c201) + c202) + c203)
			                + (z * (z * (z * c210 + c211) + c212) + c213)
			            )   + (z * (z * (z * c220 + c221) + c222) + c223)
			      )         + (z * (z * (z * c230 + c231) + c232) + c233))
			) +   
			  y * ( y * ( y * (z * (z * (z * c300 + c301) + c302) + c303)
			                + (z * (z * (z * c310 + c311) + c312) + c313)
			            )   + (z * (z * (z * c320 + c321) + c322) + c323)
			      )         + (z * (z * (z * c330 + c331) + c332) + c333);
	}
}