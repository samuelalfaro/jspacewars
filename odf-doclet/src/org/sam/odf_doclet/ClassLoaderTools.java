/* 
 * ClassLoaderTools.java
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * 
 */
public class ClassLoaderTools {
	
	private ClassLoaderTools(){}

	/**
	 * Method getLoader.
	 * @param rootpath String
	 * @param classpath String
	 * @return ClassLoader
	 */
	public static ClassLoader getLoader(String rootpath, String classpath){

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		if(classpath == null || classpath.length() == 0)
			return classLoader;
		
		if(rootpath == null)
			rootpath = "";
		
		StringTokenizer paths = new StringTokenizer(classpath, ":;");

		Collection<File> filePaths = new ArrayDeque<File>();
		while(paths.hasMoreTokens()){
			File file = new File(rootpath+paths.nextToken());
			if( file.exists() && !filePaths.contains(file)){
				filePaths.add(file);
			}
		}

		if( filePaths.size() > 0 ){
			URL[] urls = new URL[filePaths.size()];
			int i = 0; Iterator<File> it =  filePaths.iterator();
			while(it.hasNext()){
				try {
					urls[i] = it.next().toURI().toURL();
					i++;
				} catch (MalformedURLException ignorada) {
				}
			}
			classLoader = new URLClassLoader( urls, classLoader );
		}

		return classLoader;
	}
}
