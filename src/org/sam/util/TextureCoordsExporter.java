package org.sam.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.vecmath.TexCoord2f;

public class TextureCoordsExporter {
	
	@SuppressWarnings("serial")
	static public class ParsingErrorException extends RuntimeException {
		public ParsingErrorException() {
			super();
		}
		public ParsingErrorException(String s) {
			super(s);
		}
	}
	
	static class ObjParser extends StreamTokenizer {
		
		private static final char BACKSLASH = '\\';
		
		ObjParser(Reader r) {
			super(r);
			setup();
		} 
		
		void setup() {
			resetSyntax();
			eolIsSignificant(true);
			lowerCaseMode(true);
			// Todos los caracteres ascii imprimibles
			wordChars('!', '~');
			// Comentario desde ! hasta el fin de linea
			commentChar('!');
			commentChar('#');
			whitespaceChars(' ', ' ');
			whitespaceChars('\n', '\n');
			whitespaceChars('\r', '\r');
			whitespaceChars('\t', '\t');
			// Tokens
			ordinaryChar('/');
			ordinaryChar(BACKSLASH);
		}
		
		void getToken() throws ParsingErrorException {
			try {
				while (true){
					nextToken();
					if (ttype != ObjParser.BACKSLASH)
						break;
					nextToken();
					if (ttype != StreamTokenizer.TT_EOL)
						break;
				}
			}
			catch (IOException e) {
				throw new ParsingErrorException(
						"IO error on line " + lineno() + ": " + e.getMessage());
			}
		}
		
		void skipToNextLine() throws ParsingErrorException {
			while (ttype != StreamTokenizer.TT_EOF && ttype != StreamTokenizer.TT_EOL) {
				getToken();
			}
		}
		
		float getFloat() throws ParsingErrorException {
			do{
				getToken();
			}while(ttype == StreamTokenizer.TT_EOL);
			return getLastValueAsFloat();
		}
		
		float getLastValueAsFloat() throws ParsingErrorException {
			try{
//				if(ttype == StreamTokenizer.TT_NUMBER)
//					return (float)this.nval;
				if(ttype == StreamTokenizer.TT_WORD)
					return (Double.valueOf(sval)).floatValue();
				throw new ParsingErrorException("Expected number on line " + lineno());
			}
			catch (NumberFormatException e) {
				throw new ParsingErrorException(e.getMessage());
			}
		}

		int getInteger() throws ParsingErrorException {
			do{
				getToken();
			}while(ttype == StreamTokenizer.TT_EOL);
			return getLastValueAsInteger();
		}

		int getLastValueAsInteger() throws ParsingErrorException {
			try {
//				if(ttype == StreamTokenizer.TT_NUMBER)
//					return (int)this.nval;
				if(ttype == StreamTokenizer.TT_WORD)
					return (Integer.valueOf(sval)).intValue();
				throw new ParsingErrorException("Expected number on line " + lineno());
			}
			catch (NumberFormatException e) {
				throw new ParsingErrorException(e.getMessage());
			}
		}
	}
	
	final int w, h;
	final BufferedImage img;
	final Graphics2D g2d;
	final List<TexCoord2f> texList;

	private TextureCoordsExporter(int w, int h){
		this.w = w;
		this.h = h;
		img = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
		g2d = img.createGraphics();
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, w, h);
		g2d.setColor(Color.WHITE);
		texList = new ArrayList<TexCoord2f>(500);
	}
	
	void read(ObjParser st) throws ParsingErrorException {
		do{
			st.getToken();
			if (st.ttype == StreamTokenizer.TT_WORD) {
				if (st.sval.equals("v"))
					st.skipToNextLine(); // vertex
				else if (st.sval.equals("vn"))
					st.skipToNextLine(); // normal
				else if (st.sval.equals("vt"))
					readTexture(st);
				else if (st.sval.equals("f")){
					drawFace(st);
				}
				else if (st.sval.equals("fo")){
					drawFace(st);
				}
				else if (st.sval.equals("g"))
					st.skipToNextLine();
				else if (st.sval.equals("s"))
					st.skipToNextLine();
				else if (st.sval.equals("p"))
					st.skipToNextLine();
				else if (st.sval.equals("l"))
					st.skipToNextLine();
				else if (st.sval.equals("mtllib"))
					st.skipToNextLine();
				else if (st.sval.equals("usemtl"))
					st.skipToNextLine();
				else if (st.sval.equals("maplib"))
					st.skipToNextLine();
				else if (st.sval.equals("usemap"))
					st.skipToNextLine();
				else
					throw new ParsingErrorException(
							"Unrecognized token, line " + st.lineno());
			}
			st.skipToNextLine();
		}while (st.ttype != StreamTokenizer.TT_EOF);
	}

	void readTexture(ObjParser st) throws ParsingErrorException {
		TexCoord2f t = new TexCoord2f();
		t.x = st.getFloat();
		t.y = 1.0f - st.getFloat();
		texList.add(t);
	}
	
	void drawFace(ObjParser st) throws ParsingErrorException {
		int[] texIndex  = new int[4];
		int count = 0;
		
		st.getToken();
		do{
			// Se le el vertice, se continua con las coordenadas de textura
			st.getToken();
			if (st.ttype == '/') {
				st.getToken();
				if (st.ttype == StreamTokenizer.TT_WORD) {
					texIndex[count] = st.getLastValueAsInteger() - 1;
					if (texIndex[count] < 0)
						texIndex[count] += texList.size() + 1;
					st.getToken();
				}
				if (st.ttype == '/') {
					st.getInteger(); // se lee la normal
					st.getToken(); // se lee el siguiente valor para continuar el bucle
				}
			}
			count++;
		}while (st.ttype != StreamTokenizer.TT_EOF && st.ttype != StreamTokenizer.TT_EOL);
		
		TexCoord2f t1 = texList.get(texIndex[0]), t2;
		g2d.drawRect((int)(t1.x * w) - 1, (int)(t1.y * h) - 1, 2, 2);
		for(int i=1; i < count; i++){
			t2 = texList.get(texIndex[i]);
			g2d.drawRect((int)(t2.x * w) -1, (int)(t2.y * h) -1, 2, 2);
			g2d.drawLine((int)(t1.x * w), (int)(t1.y * h), (int)(t2.x * w), (int)(t2.y * h));
			t1 = t2;
		}
		t2 = texList.get(texIndex[0]); 
		g2d.drawLine((int)(t1.x * w), (int)(t1.y * h), (int)(t2.x * w), (int)(t2.y * h));
	}

	private static void export(final Reader reader, final int w, final int h, final String formato, final String rutaFichero) throws ParsingErrorException, IOException{
		TextureCoordsExporter exporter = new TextureCoordsExporter(w,h);
		exporter.read(new ObjParser(new BufferedReader(reader)));
		ImageIO.write( exporter.img, formato, new File( rutaFichero ));
	}

	public static void export(String filename, final int w, final int h, final String formato, final String rutaFichero) throws ParsingErrorException, IOException{
		export(new FileReader(filename), w, h, formato, rutaFichero);
	}
	
	public static void main(String args[]){
		try{
			export("resources/obj3d/formas/bomber02/forma.obj",1024,1024,"png","textureCoords.png");
		}catch( ParsingErrorException e ){
			e.printStackTrace();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}
}
