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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.sam.odf_doclet.bindings.ClassBindingFactory;
import org.sam.odf_doclet.bindings.Recorders;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

public class ODFDoclet {
	
	public static int optionLength(String option) {
		if (option.equalsIgnoreCase("-projectRootpath")) 
			return 2;
		if (option.equalsIgnoreCase("-projectClasspath"))
			return 2;
		return 0;
	}

	public static boolean validOptions(String options[][], DocErrorReporter reporter) {
		
		String projectRootpath  = null;
		String projectClasspath = null;
		
		for(String[] option: options){
			if(option[0].equalsIgnoreCase("-projectRootpath")){
				if(projectRootpath != null){
					reporter.printError("Only one -projectRootpath option allowed.");
					return false;
				}
				projectRootpath = option[1];
			}else if(option[0].equalsIgnoreCase("-projectClasspath")){
				if(projectClasspath != null){
					reporter.printError("Only one -projectClasspath option allowed.");
					return false;
				}
				projectClasspath = option[1];
			}
		}
		ClassBindingFactory.setClassLoader(ClassLoaderTools.getLoader( projectRootpath, projectClasspath ) );
		return true;
	}
	
	public static File generarODT(
			InputStream sourceContent, InputStream sourceStylesheet,
			File platillaODT, String images[], InputStream manifestStylesheet
	){

		try {
			File tempFile = File.createTempFile("result", ".odt", null);
			byte[] buf = new byte[4096];

			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(platillaODT)));
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));

			ZipEntry entry = zin.getNextEntry();
			while (entry != null) {
				String name = entry.getName();
				boolean conservar = 
					!name.equalsIgnoreCase("content.xml") &&
					(!name.equalsIgnoreCase("META-INF/manifest.xml") || manifestStylesheet == null);
				if (conservar) {
					// System.out.println(name);
					out.putNextEntry(new ZipEntry(name));
					int len;
					while ((len = zin.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
				}
				entry = zin.getNextEntry();
			}
			zin.close();
//			TransformerFactory tFactory = TransformerFactory.newInstance();
//			out.putNextEntry(new ZipEntry("content.xml"));
//			aplicarPlantilla(tFactory, sourceContent, sourceStylesheet, out);
//			out.closeEntry();
//			if(manifestStylesheet != null){
//				out.putNextEntry(new ZipEntry("META-INF/manifest.xml"));
//				aplicarPlantilla(tFactory, null, manifestStylesheet, out);
//				out.closeEntry();
//			}
//			out.close();
			return tempFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean start(RootDoc root) throws ClassNotFoundException {

		ClassDoc[] classes = root.classes();
		
		XMLConverter converter = new XMLConverter();
		Recorders.register( converter );
		converter.setWriter( new XMLWriter(System.out, true) );

		for(ClassDoc classDoc: classes)
			converter.write( ClassBindingFactory.createBinding(classDoc) );
		return true;
	}
}