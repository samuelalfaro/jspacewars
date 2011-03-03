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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
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
import org.sam.odf_doclet.bindings.ClassBindingFactory;
import org.sam.odf_doclet.bindings.Recorders;
import org.sam.pipeline.Filter;
import org.sam.pipeline.FilterAbs;
import org.sam.pipeline.FilterException;
import org.sam.pipeline.Pump;
import org.sam.pipeline.Sink;
import org.sam.pipeline.SinkAbs;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

class ToXML implements Pump{

	private final XMLConverter converter;
	
	ToXML(){
		converter = new XMLConverter();
		Recorders.register(converter);
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Pump#process(java.io.OutputStream)
	 */
	@Override
	public void process(OutputStream out) throws IOException {
		converter.setWriter( new XMLWriter( out, false ) );
		converter.write(PipeLine.getSource());
	}
}

class ToSVG extends FilterAbs{
	
	private final Transformer transformer;

	ToSVG(Pump source) throws IOException, FileNotFoundException, TransformerFactoryConfigurationError, TransformerConfigurationException{
		File template = new File("resources/shared/toSVG.xsl");
		transformer = TransformerFactory.newInstance().newTransformer(
				new StreamSource(new FileInputStream(template), template.toString() )
		);
		transformer.setParameter("scale", 2.0);
		transformer.setParameter("background", "#FFFFFF");
		transformer.setParameter("widthChar1", 6.6);
		transformer.setParameter("widthChar2", 9.0);
		setPump(source);
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
	
	ToPNG(Pump source) throws IOException{
		transcoder = new PNGTranscoder();
		transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD, Boolean.TRUE);
		setPump(source);
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
			throw new FilterException("ToPNG Error!!!", e);
		}
	}
}

class ToIMG extends SinkAbs{
	
	ToIMG(Pump source) throws IOException{
		setPump(source);
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Sink#process(java.io.InputStream)
	 */
	@Override
	public void process(InputStream in) throws IOException, FilterException {
		PipeLine.setDestination( ImageIO.read(in) );
	}
}

public final class PipeLine {
	
	private PipeLine(){}
	
	private static Pump   toXML;
	private static Filter toSVG;
	private static Filter toPNG;
	private static Sink   toIMG;
	
	static{
		try {
			toXML = new ToXML();
			toSVG = new ToSVG(toXML);
			toPNG = new ToPNG(toSVG);
			toIMG = new ToIMG(toPNG);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private static ClassBinding source;

	static ClassBinding getSource(){
		return source;
	}
	
	public static void setSource(Class<?> source){
		PipeLine.source = ClassBindingFactory.createBinding(source);
	}
	
	private static BufferedImage destination;
	
	static void setDestination(BufferedImage destination){
		PipeLine.destination = destination;
	}
	
	public static BufferedImage getDestination(){
		return destination;
	}
	
	public static void toXML(OutputStream out) throws IOException{
		toXML.process(out);
	}
	
	public static void toSVG(OutputStream out) throws IOException{
		toSVG.process(out);
	}
	
	public static void toPNG(OutputStream out) throws IOException{
		toPNG.process(out);
	}
	
	public static void toIMG() throws IOException{
		toIMG.process();
	}
}
