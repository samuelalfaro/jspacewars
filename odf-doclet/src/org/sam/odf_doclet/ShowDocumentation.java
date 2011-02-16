/* 
 * ShowDocumentation.java
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

import org.sam.odf_doclet.bindings.ClassBinding;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

public class ShowDocumentation {
	
	public static boolean start(RootDoc root) throws ClassNotFoundException {

		ClassDoc[] classes = root.classes();

		for(ClassDoc classDoc: classes)
			ClassBinding.from(classDoc).toXML(System.out);
		return true;
	}
	
	public static int optionLength(String option) {
		if (option.equalsIgnoreCase("-projectRootpath")) 
			return 2;
		if (option.equalsIgnoreCase("-projectClasspath"))
			return 2;
		return 0;
	}

	public static boolean validOptions(String options[][], DocErrorReporter reporter) {
		
		String projectRootpath  = ""; boolean foundProjectRootpath = false;
		String projectClasspath = ""; boolean foundProjectClasspath = false;
		
		for(String[] option: options){
			if(option[0].equalsIgnoreCase("-projectRootpath")){
				if(foundProjectRootpath){
					reporter.printError("Only one -projectRootpath option allowed.");
					return false;
				}
				foundProjectRootpath = true;
				projectRootpath = option[1];
			}else if(option[0].equalsIgnoreCase("-projectClasspath")){
				if(foundProjectClasspath){
					reporter.printError("Only one -projectClasspath option allowed.");
					return false;
				}
				foundProjectClasspath = true;
				projectClasspath = option[1];
			}
		}
		ClassBinding.setClassLoader(ClassLoaderTools.getLoader( projectRootpath, projectClasspath ) );
		return true;
	}
}