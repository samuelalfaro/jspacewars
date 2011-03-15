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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.sam.odf_doclet.bindings.ClassBindingFactory;
import org.sam.odf_doclet.pipeline.PipeLine;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

/**
 */
public class ODFDoclet {
	
	private static class ManifestGenerator{
		
		private static final Charset UTF8 = Charset.forName( "UTF-8" );

		private BufferedReader reader;
		private String breakLine;
		private boolean havePicturesDir;
		private boolean addPicturesDir;
		private final StringWriter newManifest;
		private final PrintWriter writer;
		
		ManifestGenerator(){
			newManifest = new StringWriter();
			writer = new PrintWriter( new BufferedWriter( newManifest ) );
			addPicturesDir = false;
		}
		
		void readOldManifest( InputStream in ) throws IOException {
			StringBuffer oldManifest = new StringBuffer();
			byte[] buf = new byte[4096];
			int len;
			while( ( len = in.read( buf ) ) > 0 ){
				oldManifest.append( new String( buf, 0, len, UTF8 ) );
			}
			reader = new BufferedReader( new StringReader( oldManifest.toString() ) );
			havePicturesDir = false;
			while(true){
				breakLine = reader.readLine();
				if( breakLine == null ||
					breakLine.contains( "manifest:full-path=\"Pictures/\"" ) ||
					breakLine.contains( "manifest:full-path=\"content.xml\"" ) ||
					breakLine.contains( "</manifest:manifest>")
				){
					if( breakLine.contains( "manifest:full-path=\"Pictures/\"" ) )
						havePicturesDir = true;
					break;
				}
				writer.println( breakLine );
			}
		}
		
		void addEntry(String type, String path){
			writer.format(" <manifest:file-entry manifest:media-type=\"%s\" manifest:full-path=\"%s\"/>\n", type, path );
		}
		
		void addImage(String name){
			if( !havePicturesDir && !addPicturesDir )
				addPicturesDir = true;
			addEntry("image/png", name);
		}
		
		byte[] getBytes() throws IOException{
			if( addPicturesDir )
				addEntry("", "Pictures/");
			do{
				writer.println( breakLine );
				breakLine = reader.readLine();
			}while( breakLine != null );
			writer.flush();
			return newManifest.toString().getBytes( UTF8 );
		}
	}
	
	/**
	 * Method optionLength.
	 * @param option String
	 * @return int
	 */
	public static int optionLength(String option) {
		if (option.equalsIgnoreCase("-projectRootpath")) 
			return 2;
		if (option.equalsIgnoreCase("-projectClasspath"))
			return 2;
		return 0;
	}
	
	/**
	 * Method validOptions.
	 * @param options String[][]
	 * @param reporter DocErrorReporter
	 * @return boolean
	 */
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
	
	/**
	 * Method generarODT.
	 * @param platillaODT File
	 * @param root RootDoc
	 * @return File
	 */
	public static void generarODT( InputStream plantilla, File resultFile, RootDoc root ) throws IOException{

		byte[] buf = new byte[4096];

		ZipInputStream  zin = new ZipInputStream( new BufferedInputStream( plantilla ) );
		ZipOutputStream out = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( resultFile ) ) );

		ZipEntry entry = zin.getNextEntry();


		ManifestGenerator manifest = new ManifestGenerator();
		while( entry != null ){
			String name = entry.getName();
			if( name.equalsIgnoreCase( "META-INF/manifest.xml" ) ){
				manifest.readOldManifest( zin );
			}else if( !name.equalsIgnoreCase( "content.xml" ) ){
				out.putNextEntry( new ZipEntry( name ) );
				int len;
				while( ( len = zin.read( buf ) ) > 0 ){
					out.write( buf, 0, len );
				}
			}
			entry = zin.getNextEntry();
		}
		zin.close();
		
		ClassDoc[] classes = root.classes();
		for( ClassDoc classDoc: classes ){
			String pictName = "Pictures/" + classDoc.qualifiedName() + ".png";
			manifest.addImage( pictName );
			out.putNextEntry( new ZipEntry( pictName ) );
			try{
				PipeLine.toPNG( ClassBindingFactory.createBinding( classDoc ), out );
			}catch( ClassNotFoundException e ){
				e.printStackTrace();
			}
			out.closeEntry();
		}
		
		out.putNextEntry( new ZipEntry( "META-INF/manifest.xml" ) );
		out.write( manifest.getBytes() );
		out.close();
		
//			TransformerFactory tFactory = TransformerFactory.newInstance();
//			out.putNextEntry(new ZipEntry("content.xml"));
//			aplicarPlantilla(tFactory, sourceContent, sourceStylesheet, out);
//			out.closeEntry();
//			if(manifestStylesheet != null){
//				out.putNextEntry(new ZipEntry("META-INF/manifest.xml"));
//				aplicarPlantilla(tFactory, null, manifestStylesheet, out);
//				out.closeEntry();
//			}
		
	}
	
	/**
	 * Method start.
	 * @param root RootDoc
	 * @return boolean
	 */
	public static boolean start( RootDoc root ){

		try{
			File fileOutput = new File( "output/result.odt" );
			generarODT(	
					Loader.getResourceAsStream( "resources/plantilla.odt" ),
					fileOutput,
					root
			);
			return true;
		}catch( IOException e ){
			e.printStackTrace();
			return false;
		}
	}
}