/* 
 * ODFDoclet.java
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

import java.awt.Dimension;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.sam.odf_doclet.bindings.ClassBindingFactory;
import org.sam.odf_doclet.pipeline.PipeLine;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

/**
 */
public class ODFDoclet {
	
	static final Charset UTF8 = Charset.forName( "UTF-8" );
	static final double scaleFactor = 1.0 / 120;
	
	private static class ManifestGenerator{
		
		private BufferedReader reader;
		private boolean havePicturesDir;
		private boolean addPicturesDir;
		private String breakLine;
		private final StringWriter newManifest;
		private final PrintWriter writer;
		
		ManifestGenerator( InputStream in ) throws IOException {
			reader = new BufferedReader( new StringReader( readOldManifest( in ) ) );
			newManifest = new StringWriter();
			writer = new PrintWriter( new BufferedWriter( newManifest ) );
			havePicturesDir = addPicturesDir = false;
			wirteBeginOldManifest();
		}
		
		private String readOldManifest( InputStream in ) throws IOException {
			StringBuffer oldManifest = new StringBuffer();
			byte[] buf = new byte[4096];
			int len;
			while( ( len = in.read( buf ) ) > 0 ){
				oldManifest.append( new String( buf, 0, len, UTF8 ) );
			}
			return oldManifest.toString();
		}
		
		private void wirteBeginOldManifest() throws IOException {
			while(true){
				breakLine = reader.readLine();
				if( breakLine == null ||
					breakLine.contains( "manifest:full-path=\"Pictures/\"" ) ||
					breakLine.contains( "manifest:full-path=\"content.xml\"" ) ||
					breakLine.contains( "</manifest:manifest>" )
				){
					if( breakLine.contains( "manifest:full-path=\"Pictures/\"" ) )
						havePicturesDir = true;
					break;
				}
				writer.println( breakLine );
			}
		}
		
		private void wirteEndOldManifest() throws IOException {
			if( addPicturesDir )
				addEntry("", "Pictures/");
			do{
				writer.println( breakLine );
				breakLine = reader.readLine();
			}while( breakLine != null );
			writer.flush();
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
			wirteEndOldManifest();
			return newManifest.toString().getBytes( UTF8 );
		}
	}
	
	private static class ContentGenerator{
		
		private static String readTemplate( InputStream in ) throws IOException {
			StringBuffer template = new StringBuffer();
			byte[] buf = new byte[4096];
			int len;
			while( ( len = in.read( buf ) ) > 0 ){
				template.append( new String( buf, 0, len, UTF8 ) );
			}
			return template.toString();
		}
		
		private final String template;
		private final VelocityContext context;
		private final Collection<Graphic> graphics;
		
		ContentGenerator() throws IOException {
			Velocity.init();
			template = readTemplate( Loader.getResourceAsStream( "/resources/toODT.vm" ) );
			context = new VelocityContext();
			graphics = new LinkedList<Graphic>();
			context.put( "graphics", graphics );
		}
		
		void addGraphic( String name, String path, Dimension dim, int dpi ){
			graphics.add( new Graphic( name, path, dim, dpi ) );
		}
		
		byte[] getBytes() {
			StringWriter writer = new StringWriter();
			Velocity.evaluate( context, writer, "toODT.vm", template );
			return writer.getBuffer().toString().getBytes( UTF8 );
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
		
		final int dpi = 120;

		byte[] buf = new byte[4096];

		ZipInputStream  zin = new ZipInputStream( new BufferedInputStream( plantilla ) );
		ZipOutputStream out = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( resultFile ) ) );

		ZipEntry entry = zin.getNextEntry();

		ManifestGenerator manifest = null;
		while( entry != null ){
			String name = entry.getName();
			if( name.equalsIgnoreCase( "META-INF/manifest.xml" ) ){
				manifest = new ManifestGenerator( zin );
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
		
		ContentGenerator content = new ContentGenerator();
		
		ClassDoc[] classes = root.classes();
		for( ClassDoc classDoc: classes ){
			String pictName = classDoc.qualifiedName();
			String pictPath = "Pictures/" + pictName + ".png";
			manifest.addImage( pictPath );
			
			out.putNextEntry( new ZipEntry( pictPath ) );
			try{
				content.addGraphic( 
						pictName, pictPath,
						PipeLine.toPNG( ClassBindingFactory.createBinding( classDoc ), scaleFactor * dpi, out ),
						dpi
				);
			}catch( ClassNotFoundException e ){
				e.printStackTrace();
			}
			out.closeEntry();
		}
		
		out.putNextEntry( new ZipEntry( "content.xml" ) );
		manifest.addEntry( "text/xml", "content.xml");
		out.write( content.getBytes() );
		
		out.putNextEntry( new ZipEntry( "META-INF/manifest.xml" ) );
		out.write( manifest.getBytes() );
		out.close();

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