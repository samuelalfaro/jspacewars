/* 
 * PipeLine.java
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
package org.sam.odf_doclet.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.sam.odf_doclet.bindings.ClassBinding;
import org.sam.pipeline.Filter;
import org.sam.pipeline.FilterAbs;
import org.sam.pipeline.FilterException;
import org.sam.pipeline.Filterable;
import org.sam.xml.XMLPrinter;

class ToXML implements Filterable{

	private ClassBinding clazz;

	/**
	 * @param clazz valor del clazz asignado.
	 */
	public void setClass(ClassBinding clazz) {
		this.clazz = clazz;
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Filter#process(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void process(OutputStream out) throws IOException {
		clazz.toXML( new XMLPrinter(out, false) );
	}
}

class ToSVG extends FilterAbs{
	
	private final Transformer transformer;

	ToSVG() throws FileNotFoundException, TransformerFactoryConfigurationError, TransformerConfigurationException{
		File template = new File("resources/shared/toSVG.xsl");
		transformer = TransformerFactory.newInstance().newTransformer(
				new StreamSource(new FileInputStream(template), template.toString() )
		);
		transformer.setParameter("scale", 2.0);
		transformer.setParameter("background", "#FFFFFF");
		transformer.setParameter("widthChar1", 6.6);
		transformer.setParameter("widthChar2", 9.0);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.pipeline.Filter#process(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void process(InputStream in, OutputStream out) throws IOException, FilterException{
		try {
			transformer.transform(new StreamSource(in), new StreamResult(out));
		} catch (TransformerException e) {
			throw new FilterException("ToSVG Error!!!", e);
		}
	}
}

class ToPNG extends FilterAbs{
	
	final ImageTranscoder transcoder;
	
	ToPNG(){
		transcoder = new PNGTranscoder();
		transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD, Boolean.TRUE);	
	}
	
	/* (non-Javadoc)
	 * @see org.sam.pipeline.Filter#process(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void process(InputStream in, OutputStream out) throws IOException, FilterException{
		TranscoderInput input = new TranscoderInput(in);
        input.setURI( new File("resources").toURI().toString() );
        TranscoderOutput output = new TranscoderOutput(out);
        
        try {
			transcoder.transcode( input, output );
		} catch (TranscoderException e) {
			throw new FilterException("ToSVG Error!!!", e);
		}
	}
}

/**
 * 
 */
public final class PipeLine {
	
	private PipeLine(){}
	
	private static ToXML  source;
	private static Filter toSVG;
	private static Filter toPNG;
	
	static{
		try {
			source = new ToXML();
			toSVG = new ToSVG();
			toSVG.setSource(source);
			toPNG = new ToPNG();
			toPNG.setSource(toSVG);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void setClass(Class<?> clazz){
		source.setClass( ClassBinding.from(clazz) );
	}
	
	public static void toXML(OutputStream out) throws IOException{
		source.process(out);
	}
	
	public static void toSVG(OutputStream out) throws IOException{
		toSVG.process(out);
	}
	
	public static void toPNG(OutputStream out) throws IOException{
		toPNG.process(out);
	}
}
