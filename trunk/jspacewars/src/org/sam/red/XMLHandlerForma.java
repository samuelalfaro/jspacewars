package org.sam.red;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import java.awt.Polygon;
import java.util.Queue;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandlerForma extends DefaultHandler {
	
	private Queue<Polygon> instancias;
	private XMLReader parser;
	private DefaultHandler handlerPadre;
	private XMLHandlerPunto2D handlerPunto2D;
	private int coordenadasX[], coordenadasY[];

	public XMLHandlerForma (XMLReader parser, DefaultHandler handlerPadre, Queue<Polygon> instancias){
		this.parser = parser;
		this.handlerPadre = handlerPadre;
		this.instancias = instancias;
		this.handlerPunto2D = new XMLHandlerPunto2D( parser, this);
	}

	public void startElement( String namespaceURI, String localName, String qName, Attributes attr )
	throws SAXException {
		if (localName.equals("Forma")){
			int nPuntos = Integer.parseInt(attr.getValue("nPuntos"));
			coordenadasX = new int[nPuntos];
			coordenadasY = new int[nPuntos];

			handlerPunto2D.setDst(coordenadasX,coordenadasY);
			parser.setContentHandler( handlerPunto2D );
		}else{
			try{
				handlerPadre.startElement ( namespaceURI, localName, qName, attr );
				parser.setContentHandler( handlerPadre );
			}catch(NullPointerException esRaiz){
			}
		}
	}

	public void endElement (String namespaceURI, String localName, String rawName)
	throws SAXException{
		if (localName.equals("Forma"))
			instancias.offer(new Polygon(coordenadasX, coordenadasY,coordenadasX.length));
		else{
			try{
				handlerPadre.endElement ( namespaceURI, localName, rawName);
				parser.setContentHandler( handlerPadre );
			}catch(NullPointerException esRaiz){
			}
		}
	}
}
