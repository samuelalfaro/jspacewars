/* 
 * ClassToUML.java
 * 
 * Copyright (c) 2010 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
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
 * along with tips.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.odf_doclet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.sam.odf_doclet.bindings.ClassBinding;

@Deprecated
public class ClassToUML {
	
	private static Transformer toSVGSingleton = null;

	private static final Transformer toSVGTransformer() {
		if (toSVGSingleton == null)
			try {
				File template = new File("resources/shared/toSVG.xsl");
				toSVGSingleton = TransformerFactory.newInstance().newTransformer(
						new StreamSource(new FileInputStream(template), template.toString() )
				);
				toSVGSingleton.setParameter("scale", 2.0);
				toSVGSingleton.setParameter("background", "#FFFFFF");
				toSVGSingleton.setParameter("widthChar1", 6.6);
				toSVGSingleton.setParameter("widthChar2", 9.0);
			} catch (TransformerConfigurationException ignorada) {
				ignorada.printStackTrace();
			} catch (FileNotFoundException ignorada) {
				ignorada.printStackTrace();
			} catch (TransformerFactoryConfigurationError ignorada) {
				ignorada.printStackTrace();
			}
		return toSVGSingleton;
	}
	
	public static void toSVG(final Class<?> clazz, OutputStream out) throws TransformerException, IOException{

		final PipedOutputStream pipeOut = new PipedOutputStream();

		Thread thread = new Thread() {
			public void run() {
				try {
					ClassBinding.from(clazz).toXML(pipeOut);
					pipeOut.flush();
					pipeOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		PipedInputStream pipeIn = new PipedInputStream();
		pipeIn.connect(pipeOut);
		
		thread.start();
		toSVGTransformer().transform( new StreamSource(pipeIn), new StreamResult(out) );
		
        try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        out.flush();
        out.close();
	}
	
	public static void toPNG(final Class<?> clazz, OutputStream out) throws TranscoderException, IOException{

		final PipedOutputStream pipeOut = new PipedOutputStream();
		
		Thread thread = new Thread() {
			public void run() {
				try {
					toSVG( clazz, pipeOut );
					pipeOut.flush();
					pipeOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}
			}
		};
		PipedInputStream pipeIn = new PipedInputStream();
		pipeIn.connect(pipeOut);

		ImageTranscoder t = new PNGTranscoder();
		t.addTranscodingHint(PNGTranscoder.KEY_INDEXED, new Integer(8));
        
		TranscoderInput input = new TranscoderInput(pipeIn);
        input.setURI( new File("resources").toURI().toString() );
        TranscoderOutput output = new TranscoderOutput(out);
        
        thread.start();
        t.transcode( input, output );
        try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        out.flush();
        out.close();
	}
	

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws TranscoderException 
	 * @throws IOException 
	 */
	public static void main(String... args) throws ClassNotFoundException, TranscoderException, IOException {
		/*
		Class<?> clazz =  Class.forName(
				"org.sam.colisiones.ComprobadorDeColisones",
				false,
				ClassLoaderTools.getLoader(
						"/media/DATA/Samuel/Proyectos/jspacewars/",
						"lib/ext/vecmath.jar:lib/gluegen-rt.jar:lib/jogl.jar:" +
						"lib/FengGUI.jar:lib/xstream-1.3.jar:lib/ibxm-alpha51.jar:" +
						"lib/jogg-0.0.7.jar:lib/jorbis-0.0.15.jar:bin"
				)
		);
		/*/
		Class<?> clazz = pruebas.ClaseDePrueba.class;
		//*/
		ClassBinding.from(clazz).toXML(System.out);
//		toPNG( clazz, new FileOutputStream("output/out.png") );
//		toPNG( java.util.concurrent.ConcurrentHashMap.class, new FileOutputStream("output/out.png") );
//		toPNG( pruebas.ClaseDePrueba.class, new FileOutputStream("output/out.png") );
	}
}
