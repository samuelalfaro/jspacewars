package org.sam.red;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandlerPunto2D extends DefaultHandler {
	
	private XMLReader parser;
	private DefaultHandler handlerPadre;
	private int coordenadasX[], coordenadasY[];
	private int i;

	public XMLHandlerPunto2D (XMLReader parser, DefaultHandler handlerPadre){
		this.parser = parser;
		this.handlerPadre = handlerPadre;
	}

	public void setDst(int coordenadasX[], int coordenadasY[]){
		this.i = 0;
		this.coordenadasX = coordenadasX;
		this.coordenadasY = coordenadasY;
	}
	
	public void startElement( String namespaceURI, String localName, String qName, Attributes attr )
	throws SAXException {
		if(localName.equals("Punto2D")){
			this.coordenadasX[i] = Integer.parseInt(attr.getValue("x"));
			this.coordenadasY[i] = Integer.parseInt(attr.getValue("y"));
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
		if(localName.equals("Punto2D"))
			i++;
		else{
			try{
				handlerPadre.endElement ( namespaceURI, localName, rawName);
				parser.setContentHandler( handlerPadre );
			}catch(NullPointerException esRaiz){
			}
		}
	}
}
