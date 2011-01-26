/* 
 * ODT_Generator.java
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
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 
 */
public class ODT_Generator {
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
			TransformerFactory tFactory = TransformerFactory.newInstance();
			out.putNextEntry(new ZipEntry("content.xml"));
			aplicarPlantilla(tFactory, sourceContent, sourceStylesheet, out);
			out.closeEntry();
			if(manifestStylesheet != null){
				out.putNextEntry(new ZipEntry("META-INF/manifest.xml"));
				aplicarPlantilla(tFactory, null, manifestStylesheet, out);
				out.closeEntry();
			}
			out.close();
			return tempFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File generarODT(InputStream sourceContent, InputStream sourceStylesheet, File plantillaODT){
		return generarODT(sourceContent, sourceStylesheet, plantillaODT, null, null);
	}

	private static void aplicarPlantilla(TransformerFactory tFactory, InputStream source, InputStream stylesheet, OutputStream result){
		try {
			Transformer transformer = tFactory.newTransformer(new StreamSource(stylesheet));
			try {
				transformer.transform ( new StreamSource(source), new StreamResult (result));
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
	}
}
