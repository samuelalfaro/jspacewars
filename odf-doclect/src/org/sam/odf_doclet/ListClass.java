/* 
 * ListClass.java
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
package org.sam.odf_doclet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

public class ListClass {
	
	private static final Comparator<PackageDoc> COMPARADOR_DE_PACKAGES = new Comparator<PackageDoc>() {
		/** {@inheritDoc} */
		public int compare(PackageDoc e1, PackageDoc e2) {
			return e1.name().compareTo(e2.name());
		}
	};
	
	private static final Comparator<ClassDoc> COMPARADOR_POR_NOMBRE = new Comparator<ClassDoc>() {
		/** {@inheritDoc} */
		public int compare(ClassDoc e1, ClassDoc e2) {
			return e1.name().compareTo(e2.name());
		}
	};
	
	private static final Comparator<ClassDoc> COMPARADOR_DE_CLASES = new Comparator<ClassDoc>() {
		/** {@inheritDoc} */
		public int compare(ClassDoc e1, ClassDoc e2) {
			return getHierarchicalName(e1).compareTo(getHierarchicalName(e2));
		}
	};
	
	private static String getHierarchicalName(ClassDoc classDoc){
		String name = classDoc.simpleTypeName();
		ClassDoc containingClass = classDoc.containingClass();
		while(containingClass != null){
			if(containingClass.containingClass() != null)
				name = containingClass.simpleTypeName() + "." + classDoc.simpleTypeName();
			else
				name = getHierarchicalName(containingClass) + "." + name;
			containingClass = containingClass.containingClass();
		}
		ClassDoc superClassDoc = classDoc.superclass();
		if( superClassDoc != null && 
				classDoc.containingPackage().name().equals(superClassDoc.containingPackage().name())){
			name = getHierarchicalName(superClassDoc) + "/" + name;
		}
		return name;
	}
	
	private static void printCollection(Collection<ClassDoc> collection, String title){
		if(collection.size() > 0){
			System.err.println(title);
			for(ClassDoc classDoc: collection)
				System.err.println("\t"+classDoc.name());
		}
	}
	
	public static boolean start(RootDoc root) {
		ClassDoc[] classes = root.classes();
		for(ClassDoc classDoc: classes){
			if(classDoc.containingPackage().name().equals(""))
				System.err.println("\t"+classDoc.name());
		}

		SortedSet<PackageDoc> sortedPackages = new TreeSet<PackageDoc>(COMPARADOR_DE_PACKAGES);
		sortedPackages.addAll(Arrays.asList(root.specifiedPackages()));

		for(PackageDoc packageDoc: sortedPackages){
			System.err.println(packageDoc+"\n");
			SortedSet<ClassDoc> sortedInterfaces = new TreeSet<ClassDoc>(COMPARADOR_POR_NOMBRE);
			SortedSet<ClassDoc> sortedClasses = new TreeSet<ClassDoc>(COMPARADOR_DE_CLASES);
			SortedSet<ClassDoc> sortedException = new TreeSet<ClassDoc>(COMPARADOR_POR_NOMBRE);
			SortedSet<ClassDoc> sortedEnums = new TreeSet<ClassDoc>(COMPARADOR_POR_NOMBRE);

			sortedInterfaces.addAll(Arrays.asList(packageDoc.interfaces()));
			sortedException.addAll(Arrays.asList(packageDoc.exceptions()));
			sortedClasses.addAll(Arrays.asList(packageDoc.ordinaryClasses()));
			sortedEnums.addAll(Arrays.asList(packageDoc.enums()));
			
			printCollection(sortedInterfaces, "Interfaces:");
			printCollection(sortedException, "Exceptions:");
			
			if(sortedClasses.size() > 0){
				System.err.println("Classes:");
				for(ClassDoc classDoc: sortedClasses){
					
					if(classDoc.superclass().qualifiedName().equals("java.lang.Enum")){
						ClassDoc containingClass = classDoc.containingClass();
						while(containingClass  != null){
							System.err.print("\t");
							containingClass = containingClass.containingClass();
						}
						System.err.println("\t"+classDoc.name());
//						System.err.println(classDoc.isEnum());
//						System.err.println(classDoc.isEnumConstant());
//						System.err.println(classDoc.getRawCommentText());
//						System.err.println(classDoc.enumConstants().length);
						
						for(FieldDoc field: classDoc.fields(true))
							if(!field.type().isPrimitive() && field.type().asClassDoc().superclass() != null && field.type().asClassDoc().superclass().qualifiedName().equals("java.lang.Enum")){
								System.err.println("\t<<Constant>>" +field.name());
							}else
								System.err.println("\t"+field.type()+ " " +field.name());
					}
//					System.err.println("\t"+classDoc.name());
//					for(FieldDoc field: classDoc.fields(true))
//						System.err.println("\t"+field.name());
//					for(ConstructorDoc constructor: classDoc.constructors(true))
//						System.err.println("\t"+constructor.name());
//					for(MethodDoc method: classDoc.methods(true))
//						System.err.println("\t"+method.name());
					
				}
			}
			printCollection(sortedEnums, "Enumerations:");
			System.err.println();
		}

		return true;
	}
    
}