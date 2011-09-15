/* 
 * TestRamps.java
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
public class TestRamps {

	/**
	 * @param args ignorados.
	 */
	public static void main(String[] args) {
		for (int i = 0; i <= 20; i++) {
			System.out.printf("%2d\t%5.3f %5.3f %5.3f %5.3f %5.3f\n", i,
					Ramp.Predefinidas.LINEAL.compute(0, 1, i, 20),
					Ramp.Predefinidas.ACELERADA.compute(0, 1, i, 20),
					Ramp.Predefinidas.DECELERADA.compute(0, 1, i, 20),
					Ramp.Predefinidas.ACELERADA_DECELERADA.compute(0, 1, i, 20),
					Ramp.Predefinidas.DECELERADA_ACELERADA.compute(0, 1, i, 20)
			);
		}
	}
}
