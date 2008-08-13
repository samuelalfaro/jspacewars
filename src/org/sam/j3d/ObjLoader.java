package org.sam.j3d;

import java.io.*;
import java.net.URL;
import java.nio.*;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

public class ObjLoader {
	
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
	
	final List<Point3f> coordList;
	final Queue<Integer> coordIdxList;

	final List<TexCoord2f> texList;
	final Queue<Integer> texIdxList;

	final List<Vector3f> normList;
	final Queue<Integer> normIdxList;
	
	float minX, maxX;
	float minY, maxY;
	float minZ, maxZ;
	
	private ObjLoader(){
		coordList = new ArrayList<Point3f>(500);
		coordIdxList = new LinkedList<Integer>();
		texList = new ArrayList<TexCoord2f>(500);
		texIdxList = new LinkedList<Integer>();
		normList = new ArrayList<Vector3f>(500);
		normIdxList = new LinkedList<Integer>();
		
		minX = Float.MAX_VALUE; maxX= -Float.MAX_VALUE;
		minY = Float.MAX_VALUE; maxY= -Float.MAX_VALUE;
		minZ = Float.MAX_VALUE; maxZ= -Float.MAX_VALUE;
	}
	
	void read(ObjParser st) throws ParsingErrorException {
		do{
			st.getToken();
			if (st.ttype == StreamTokenizer.TT_WORD) {
				if (st.sval.equals("v"))
					readVertex(st);
				else if (st.sval.equals("vn"))
					readNormal(st);
				else if (st.sval.equals("vt"))
					readTexture(st);
				else if (st.sval.equals("f"))
					readFace(st);
				else if (st.sval.equals("fo"))
					readFace(st);
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

	void readVertex(ObjParser st) throws ParsingErrorException {
		Point3f v = new Point3f();
		v.x = st.getFloat();
		if(v.x < minX)
			minX = v.x;
		if(v.x > maxX)
			maxX = v.x;
		v.y = st.getFloat();
		if(v.y < minY)
			minY = v.y;
		if(v.y > maxY)
			maxY = v.y;
		v.z = st.getFloat();
		if(v.z < minZ)
			minX = v.z;
		if(v.z > maxZ)
			maxZ = v.z;
		coordList.add(v);
	}

	void readTexture(ObjParser st) throws ParsingErrorException {
		TexCoord2f t = new TexCoord2f();
		t.x = st.getFloat();
		t.y = st.getFloat();
		texList.add(t);
	}
	
	void readNormal(ObjParser st) throws ParsingErrorException {
		Vector3f n = new Vector3f();
		n.x = st.getFloat();
		n.y = st.getFloat();
		n.z = st.getFloat();
		normList.add(n);
	}

	void readFace(ObjParser st) throws ParsingErrorException {
		int[] vertIndex = new int[4];
		int[] texIndex  = new int[4];
		int[] normIndex = new int[4];
		int count = 0;
		
		//   There are n vertices on each line.  Each vertex is comprised
		//   of 1-3 numbers separated by slashes ('/').  The slashes may
		//   be omitted if there's only one number.
		st.getToken();
		do{
			if (st.ttype == StreamTokenizer.TT_WORD)
				vertIndex[count] = st.getLastValueAsInteger() - 1;
			if (vertIndex[count] < 0)
				vertIndex[count] += coordList.size() + 1;
			
			// Triangulamos
			if(count==3){
				coordIdxList.offer(new Integer(vertIndex[2]));
				coordIdxList.offer(new Integer(vertIndex[3]));
				coordIdxList.offer(new Integer(vertIndex[0]));
			}else
				coordIdxList.offer(new Integer(vertIndex[count]));
			st.getToken();
			if (st.ttype == '/') {
				st.getToken();
				if (st.ttype == StreamTokenizer.TT_WORD) {
					texIndex[count] = st.getLastValueAsInteger() - 1;
					if (texIndex[count] < 0)
						texIndex[count] += texList.size() + 1;
					// Triangulamos
					if(count==3){
						texIdxList.offer(new Integer(texIndex[2]));
						texIdxList.offer(new Integer(texIndex[3]));
						texIdxList.offer(new Integer(texIndex[0]));
					}else
						texIdxList.offer(new Integer(texIndex[count]));
					st.getToken();
				}
				if (st.ttype == '/') {
					// There has to be a number after the 2nd slash
					normIndex[count] = st.getInteger() - 1;
					if (normIndex[count] < 0)
						normIndex[count] += normList.size() + 1;
					
					if(count==3){
						normIdxList.offer(new Integer(normIndex[2]));
						normIdxList.offer(new Integer(normIndex[3]));
						normIdxList.offer(new Integer(normIndex[0]));
					}else
						normIdxList.offer(new Integer(normIndex[count]));
					st.getToken();
				}
			}
			count++;
		}while (st.ttype != StreamTokenizer.TT_EOF && st.ttype != StreamTokenizer.TT_EOL);
	}
	
	GeometryArray generateGeometry(int flags, Transform3D transform){
		int size = coordIdxList.size();
		boolean textCoord = texList.size() > 0;
		boolean normal = normList.size() > 0;
		int att = GeometryArray.COORDINATES 
			| GeometryArray.BY_REFERENCE
			| GeometryArray.USE_NIO_BUFFER;
		if (textCoord)
			att |= GeometryArray.TEXTURE_COORDINATE_2;
		if (normal)
			att |= GeometryArray.NORMALS; 
		TriangleArray ga = new TriangleArray(size, att);
		
		final ByteOrder order =  ByteOrder.nativeOrder();
		final int nBytesFloat = 4;
		FloatBuffer floatBuffer;
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * size * 3).order(order).asFloatBuffer();

		Point3f  v = new Point3f(); 
		Vector3f n = new Vector3f();

		while(!coordIdxList.isEmpty()){
			v.set(coordList.get(coordIdxList.poll()));
			try{
				transform.transform(v);
			}catch(NullPointerException noHayTransfrom){
			}
			floatBuffer.put(v.x);
			floatBuffer.put(v.y);
			floatBuffer.put(v.z);
		}
		coordList.clear();
		ga.setCoordRefBuffer(new J3DBuffer(floatBuffer));

		if(textCoord){
			floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * size * 2).order(order).asFloatBuffer();
			while(!texIdxList.isEmpty()){
				TexCoord2f t = texList.get(texIdxList.poll());
				floatBuffer.put(t.x);
				floatBuffer.put(t.y);
			}
			texList.clear();
			ga.setTexCoordRefBuffer(0,new J3DBuffer(floatBuffer));
		}
		
		if(normal){
			floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * size * 3).order(order).asFloatBuffer();
			while(!normIdxList.isEmpty()){
				n.set(normList.get(normIdxList.poll()));
				try{
					transform.transform(n);
					n.normalize();
				}catch(NullPointerException noHayTransfrom){
				}
				floatBuffer.put(n.x);
				floatBuffer.put(n.y);
				floatBuffer.put(n.z);
			}
			normList.clear();
			//floatBuffer.rewind();
			ga.setNormalRefBuffer(new J3DBuffer(floatBuffer));
		}
		return ga;
	}
	
	public static Shape3D load(String filename, int flags, Transform3D transform) throws FileNotFoundException, ParsingErrorException{
		ObjLoader loader = new ObjLoader();
		loader.read(new ObjParser(new BufferedReader(new FileReader(filename))));
		return new Shape3D(loader.generateGeometry(flags, transform));
	}
	
	public static Shape3D load(URL url, int flags, Transform3D transform) throws IOException, ParsingErrorException{
		ObjLoader loader = new ObjLoader();
		loader.read(new ObjParser(new BufferedReader(new InputStreamReader(url.openStream()))));
		return new Shape3D(loader.generateGeometry(flags, transform));
	}
}
