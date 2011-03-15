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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
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
import org.sam.odf_doclet.Loader;
import org.sam.odf_doclet.bindings.ClassBinding;
import org.sam.odf_doclet.bindings.Recorders;
import org.sam.pipeline.Filter;
import org.sam.pipeline.FilterAbs;
import org.sam.pipeline.FilterException;
import org.sam.pipeline.Pump;
import org.sam.pipeline.SinkAbs;
import org.sam.xml.XMLConverter;
import org.sam.xml.XMLWriter;

/**
 */
class ToXML implements Pump{

	private final XMLConverter converter;
	private ClassBinding source;

	ToXML(){
		converter = new XMLConverter();
		Recorders.register( converter );
	}
	
	/**
	 * Method setSource.
	 * @param source ClassBinding
	 */
	void setSource( ClassBinding source ){
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Pump#process(java.io.OutputStream)
	 */
	@Override
	public void process( OutputStream out ) throws IOException{
		converter.setWriter( new XMLWriter( out, false ) );
		converter.write( source );
	}
}

/**
 */
class ToSVG extends FilterAbs{

	private final Transformer transformer;

	/**
	 * Constructor for ToSVG.
	 * 
	 * @param pump Pump
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 */
	ToSVG( Pump pump ) throws IOException, FileNotFoundException, TransformerFactoryConfigurationError,
			TransformerConfigurationException{
		super( pump );

		transformer = TransformerFactory.newInstance().newTransformer(
					new StreamSource( 
							Loader.getResourceAsStream("resources/shared/toSVG.xsl"),
							Loader.getResourceAsURI("resources/shared/toSVG.xsl").toString() )
		);
		transformer.setParameter( "scale", 1.0 );
		transformer.setParameter( "background", "#FFFFFF" );
		transformer.setParameter( "widthChar1", 6.6 );
		transformer.setParameter( "widthChar2", 9.0 );
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Filter#process(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void process( InputStream in, OutputStream out ) throws IOException, FilterException{
		try{
			transformer.transform( new StreamSource( in ), new StreamResult( out ) );
		}catch( TransformerException e ){
			throw new FilterException( "ToSVG Error!!!", e );
		}
	}
}

/**
 * 
 */
final class PNGSizeGrabber extends OutputStream{

	private final OutputStream out;
	
	private final byte[] widthBytes;
	private final byte[] heightBytes;
	
	private int writtenBytes;
	
	PNGSizeGrabber( OutputStream out ){
		this.out = out;
		widthBytes  = new byte[4];
		heightBytes = new byte[4];
		writtenBytes = 0;
	}

	private static int toInt(byte[] bytes){
		return 
			( ( bytes[0] << 24 ) & 0xFF000000 ) |
			( ( bytes[1] << 16 ) & 0x00FF0000 ) |
			( ( bytes[2] << 8  ) & 0x0000FF00 ) |
			( ( bytes[3]       ) & 0x000000FF );
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write( int b ) throws IOException{
		out.write( b );
		// cabecera PNG: 0:[ ... ]16:[ whidth ]20:[ height ]24:[ ...
		if( writtenBytes < 24 ){
			if( writtenBytes >= 16 ){
				if( writtenBytes < 20 )
					widthBytes[writtenBytes - 16] = (byte)b;
				else
					heightBytes[writtenBytes - 20] = (byte)b;
			}
			writtenBytes++;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write( byte bytes[], int off, int len ) throws IOException{
		out.write( bytes, off, len );
		// cabecera PNG: 0:[ ... ]16:[ whidth ]20:[ height ]24:[ ... 
		int i = 0;
		while( writtenBytes < 24 && i < len ){
			if( writtenBytes >= 16 ){
				if( writtenBytes < 20 )
					widthBytes[writtenBytes - 16] = bytes[i + off];
				else
					heightBytes[writtenBytes - 20] = bytes[i + off];
			}
			writtenBytes++; i++;
		}
	}

	int getWidth(){
		return toInt( widthBytes );
	}

	int getHeight(){
		return toInt( heightBytes );
	}
}

/**
 */
class ToPNG extends FilterAbs{

	private final ImageTranscoder transcoder;
	private final String uri;

	/**
	 * Constructor for ToPNG.
	 * 
	 * @param pump Pump
	 * @throws IOException
	 */
	ToPNG( Pump pump ) throws IOException{
		super( pump );
		transcoder = new PNGTranscoder();
		transcoder.addTranscodingHint( SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD, Boolean.TRUE );
		uri = new File( Loader.getRunPath() + "resources").toURI().toString();
		System.err.println( uri );
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Filter#process(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void process( InputStream in, OutputStream out ) throws IOException, FilterException{
		TranscoderInput input = new TranscoderInput( in );
		input.setURI( uri );
		TranscoderOutput output = new TranscoderOutput( out );
		try{
			transcoder.transcode( input, output );
		}catch( TranscoderException e ){
			throw new FilterException( "ToPNG Error!!!", e );
		}
	}
}

/**
 */
class ToIMG extends SinkAbs{
	
	private BufferedImage destination;
	
	/**
	 * Constructor for ToIMG.
	 * @param pump Pump
	 * @throws IOException
	 */
	ToIMG( Pump pump ) throws IOException{
		super( pump );
	}

	/* (non-Javadoc)
	 * @see org.sam.pipeline.Sink#process(java.io.InputStream)
	 */
	@Override
	public void process( InputStream in ) throws IOException, FilterException{
		destination = ImageIO.read( in );
	}
	
	/**
	 * Method getDestination.
	 * @return BufferedImage
	 */
	BufferedImage getDestination(){
		return destination;
	}
}

/**
 */
public final class PipeLine {
	
	private static ToXML  toXML;
	private static Filter toSVG;
	private static Filter toPNG;
	private static ToIMG  toIMG;
	
	static{
		try{
			toXML = new ToXML();
			toSVG = new ToSVG(toXML);
			toPNG = new ToPNG(toSVG);
			toIMG = new ToIMG(toPNG);
		}catch( Throwable e ){
			e.printStackTrace();
		}
	}
	
	private PipeLine(){}
	
	/**
	 * @param clazz
	 * @param out
	 * @throws IOException
	 */
	public static void toXML( ClassBinding clazz, OutputStream out ) throws IOException{
		toXML.setSource( clazz );
		toXML.process( out );
	}
	
	/**
	 * @param clazz
	 * @param out
	 * @throws IOException
	 */
	public static void toSVG( ClassBinding clazz, OutputStream out ) throws IOException{
		toXML.setSource( clazz );
		toSVG.process( out );
	}
	
	/**
	 * @param clazz
	 * @param out
	 * @return Dimension
	 * @throws IOException
	 */
	public static Dimension toPNG( ClassBinding clazz, OutputStream out ) throws IOException{
		toXML.setSource( clazz );
		PNGSizeGrabber grabber = new PNGSizeGrabber( out );
		toPNG.process( grabber );
		return new Dimension( grabber.getWidth(), grabber.getHeight() );
	}
	
	/**
	 * @param clazz
	 * @return BufferedImage
	 * @throws IOException
	 */
	public static BufferedImage toIMG( ClassBinding clazz ) throws IOException{
		toXML.setSource( clazz );
		toIMG.process();
		return toIMG.getDestination();
	}
}
