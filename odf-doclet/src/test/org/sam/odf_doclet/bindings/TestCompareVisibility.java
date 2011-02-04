/* 
 * TestCompareVisibility.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of odf-doclet.
 * 
 * odf-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * odf-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with odf-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odf_doclet.bindings;

public class TestCompareVisibility {

	private static <T> void printComparation(Comparable<T> c1, T c2){
		if( c1.compareTo(c2) > 0)
			System.out.println(c1 +" es mayor que "+c2);
		else if( c1.compareTo(c2) == 0 )
			System.out.println(c1 +" es igual a "+c2);
		else
			System.out.println(c1 +" es menor que "+c2);
	}
	
	public static void main(String... args) {

		Visibility v1 = Visibility.PUBLIC;
		Visibility v2 = Visibility.PROTECTED;
		Visibility v3 = Visibility.PACKAGE;
		Visibility v4 = Visibility.PRIVATE;
		
		printComparation(v1, Visibility.PUBLIC);
		printComparation(v1, Visibility.PROTECTED);
		printComparation(v1, Visibility.PACKAGE);
		printComparation(v1, Visibility.PRIVATE);
		
		System.out.println();
		printComparation(v2, Visibility.PUBLIC);
		printComparation(v2, Visibility.PROTECTED);
		printComparation(v2, Visibility.PACKAGE);
		printComparation(v2, Visibility.PRIVATE);
		
		System.out.println();
		printComparation(v3, Visibility.PUBLIC);
		printComparation(v3, Visibility.PROTECTED);
		printComparation(v3, Visibility.PACKAGE);
		printComparation(v3, Visibility.PRIVATE);
		
		System.out.println();
		printComparation(v4, Visibility.PUBLIC);
		printComparation(v4, Visibility.PROTECTED);
		printComparation(v4, Visibility.PACKAGE);
		printComparation(v4, Visibility.PRIVATE);

	}
}
