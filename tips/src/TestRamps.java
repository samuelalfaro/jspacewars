/* 
 * TestRamps.java
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
public class TestRamps {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i = 0; i <= 20; i++){
			System.out.printf("%2d\t%5.3f %5.3f %5.3f %5.3f %5.3f\n", 
					i,
					Ramp.Lineal.compute(0, 1, i, 20),
					Ramp.Acelerada.compute(0, 1, i, 20),
					Ramp.Decelerada.compute(0, 1, i, 20),
					Ramp.AceleradaDecelerada.compute(0, 1, i, 20),
					Ramp.DeceleradaAcelerada.compute(0, 1, i, 20)
			);
		}

	}

}
