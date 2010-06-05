package org.sam.jogl;

import java.io.*;
import java.net.URL;
import java.nio.*;
import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.*;

public class ObjLoader {
	
	public static final int NONE							=	0x00;
	public static final int RESIZE							=	0x01;
	public static final int TRIANGULATE						=	0x02;
	public static final int MUST_FLIP_VERTICALLY_TEXCOORDS	=	0x04;
	
	private static final int N_BYTES_FLOAT = 4;
	
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
	
	private final int flags;
	
	final List<Point3f> coordList;
	final Queue<Integer> coordIdxList;

	final List<TexCoord2f> texList;
	final Queue<Integer> texIdxList;

	final List<Vector3f> normList;
	final Queue<Integer> normIdxList;
	
	float minX, maxX;
	float minY, maxY;
	float minZ, maxZ;
	
	private ObjLoader(int flags){
		this.flags = flags;
		coordList = new ArrayList<Point3f>(512);
		coordIdxList = new LinkedList<Integer>();
		texList = new ArrayList<TexCoord2f>(512);
		texIdxList = new LinkedList<Integer>();
		normList = new ArrayList<Vector3f>(512);
		normIdxList = new LinkedList<Integer>();
		
		if((flags & ObjLoader.RESIZE) != 0){
			minX = Float.MAX_VALUE; maxX= -Float.MAX_VALUE;
			minY = Float.MAX_VALUE; maxY= -Float.MAX_VALUE;
			minZ = Float.MAX_VALUE; maxZ= -Float.MAX_VALUE;
		}
	}
	
	void read(ObjParser st) throws ParsingErrorException {
		boolean triangular = (flags & ObjLoader.TRIANGULATE) != 0;
		do{
			st.getToken();
			if (st.ttype == StreamTokenizer.TT_WORD) {
				if (st.sval.equals("v"))
					readVertex(st);
				else if (st.sval.equals("vn"))
					readNormal(st);
				else if (st.sval.equals("vt"))
					readTexture(st);
				else if (st.sval.equals("f")){
					if(triangular)
						readTriangle(st);
					else
						readQuad(st);
				}
				else if (st.sval.equals("fo")){
					if(triangular)
						readTriangle(st);
					else
						readQuad(st);
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

	void readVertex(ObjParser st) throws ParsingErrorException {
		Point3f v = new Point3f();
		v.x = st.getFloat();
		v.y = st.getFloat();
		v.z = st.getFloat();
		coordList.add(v);
		if((flags & ObjLoader.RESIZE) != 0){
			if(v.x < minX)
				minX = v.x;
			if(v.x > maxX)
				maxX = v.x;
			if(v.y < minY)
				minY = v.y;
			if(v.y > maxY)
				maxY = v.y;
			if(v.z < minZ)
				minZ = v.z;
			if(v.z > maxZ)
				maxZ = v.z;
		}
	}

	void readTexture(ObjParser st) throws ParsingErrorException {
		TexCoord2f t = new TexCoord2f();
		t.x = st.getFloat();
		t.y = (flags & MUST_FLIP_VERTICALLY_TEXCOORDS) != 0 ? 1.0f - st.getFloat() : st.getFloat();
		texList.add(t);
	}
	
	void readNormal(ObjParser st) throws ParsingErrorException {
		Vector3f n = new Vector3f();
		n.x = st.getFloat();
		n.y = st.getFloat();
		n.z = st.getFloat();
		normList.add(n);
	}

	void readTriangle(ObjParser st) throws ParsingErrorException {
		int[] vertIndex = new int[4];
		int[] texIndex  = new int[4];
		int[] normIndex = new int[4];
		int count = 0;
		
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
	
	void readQuad(ObjParser st) throws ParsingErrorException {
		int[] vertIndex = new int[4];
		int[] texIndex  = new int[4];
		int[] normIndex = new int[4];
		int count = 0;
		
		st.getToken();
		do{
			if (st.ttype == StreamTokenizer.TT_WORD)
				vertIndex[count] = st.getLastValueAsInteger() - 1;
			if (vertIndex[count] < 0)
				vertIndex[count] += coordList.size() + 1;
				coordIdxList.offer(new Integer(vertIndex[count]));
			st.getToken();
			if (st.ttype == '/') {
				st.getToken();
				if (st.ttype == StreamTokenizer.TT_WORD) {
					texIndex[count] = st.getLastValueAsInteger() - 1;
					if (texIndex[count] < 0)
						texIndex[count] += texList.size() + 1;
					texIdxList.offer(new Integer(texIndex[count]));
					st.getToken();
				}
				if (st.ttype == '/') {
					normIndex[count] = st.getInteger() - 1;
					if (normIndex[count] < 0)
						normIndex[count] += normList.size() + 1;
					normIdxList.offer(new Integer(normIndex[count]));
					st.getToken();
				}
			}
			count++;
		}while (st.ttype != StreamTokenizer.TT_EOF && st.ttype != StreamTokenizer.TT_EOL);
		// Si la cara tiene solo tres vertices creamos el quad repitiendo el ultimo.
		if(count == 3){
			coordIdxList.offer(new Integer(vertIndex[2]));
			if(texIdxList.size()>0)
				texIdxList.offer(new Integer(texIndex[2]));
			if(normIdxList.size()>0)
				normIdxList.offer(new Integer(normIndex[2]));	
		}
	}
	
	private Matrix4d escalarYCentrar(){
		double centerX = (minX + maxX)/2;
		double centerY = (minY + maxY)/2;
		double centerZ = (minZ + maxZ)/2;

		double absX = maxX - minX;
		double absY = maxY - minY;
		double absZ = maxZ - minZ;
		
		double scale = 1.0 / Math.max(1.0, Math.max(absX, Math.max(absY,absZ)));
		//System.err.println("Centro: ("+centerX+", "+centerY+", "+centerZ+")\nEscala: "+scale);
	
		Matrix4d mt = new Matrix4d();
		mt.set(scale, new Vector3d(-centerX*scale,-centerY*scale,-centerZ*scale));

		return mt;
	}
	
	Geometria generarGeometria(Matrix4d mt){
		int nVertex = coordIdxList.size();
		
		boolean textCoord = texList.size() > 0;
		boolean normal = normList.size() > 0;
		int att = Geometria.COORDENADAS
			| Geometria.USAR_BUFFERS
			| Geometria.POR_REFERENCIA;
		if (textCoord)
			att |= Geometria.COORDENADAS_TEXTURA;
		if (normal)
			att |= Geometria.NORMALES; 
		Geometria gt = (flags & ObjLoader.TRIANGULATE)!=0 ?
				new GeometriaTriangulos(nVertex, att):
				new GeometriaQuads(nVertex, att);
		
		final ByteOrder order =  ByteOrder.nativeOrder();
		FloatBuffer floatBuffer;
		floatBuffer = ByteBuffer.allocateDirect(N_BYTES_FLOAT * nVertex * 3).order(order).asFloatBuffer();

		Point3f  v = new Point3f(); 
		Vector3f n = new Vector3f();
		
		if ( (flags & RESIZE) != 0 ){
			if(mt != null)
				mt.mul(escalarYCentrar());
			else
				mt = escalarYCentrar();
		}
		
		while(!coordIdxList.isEmpty()){
			v.set(coordList.get(coordIdxList.poll()));
			try{
				mt.transform(v);
			}catch(NullPointerException noHayTransfrom){
			}
			floatBuffer.put(v.x);
			floatBuffer.put(v.y);
			floatBuffer.put(v.z);
		}
		coordList.clear();
		gt.setCoords(floatBuffer);

		if(textCoord){
			floatBuffer = ByteBuffer.allocateDirect(N_BYTES_FLOAT * nVertex * 2).order(order).asFloatBuffer();
			while(!texIdxList.isEmpty()){
				TexCoord2f t = texList.get(texIdxList.poll());
				floatBuffer.put(t.x);
				floatBuffer.put(t.y);
			}
			texList.clear();
			gt.setTexCoords(0,floatBuffer);
		}
		
		if(normal){
			floatBuffer = ByteBuffer.allocateDirect(N_BYTES_FLOAT * nVertex * 3).order(order).asFloatBuffer();
			while(!normIdxList.isEmpty()){
				n.set(normList.get(normIdxList.poll()));
				try{
					mt.transform(n);
				}catch(NullPointerException noHayTransfrom){
				}
				n.normalize();
				floatBuffer.put(n.x);
				floatBuffer.put(n.y);
				floatBuffer.put(n.z);
			}
			normList.clear();
			gt.setNormals(floatBuffer);
		}
		return gt;
	}

	/*/

	Prueba Indexado.
	
	private static final int N_BYTES_INT = 4;
	
	Geometria generarGeometria(int flags, Transform3D transform){
		int nVertex = coordIdxList.size();
		
		boolean textCoord = texList.size() > 0;
		boolean normal = normList.size() > 0;
		int att = Geometria.COORDENADAS
			| Geometria.USAR_BUFFERS
			| Geometria.POR_REFERENCIA;
		if (textCoord)
			att |= Geometria.COORDENADAS_TEXTURA;
		if (normal)
			att |= Geometria.NORMALES; 
		GeometriaIndexada gt = new GeometriaIndexadaTriangulos(nVertex, att);
		
		final ByteOrder order =  ByteOrder.nativeOrder();
		FloatBuffer floatBuffer;
		IntBuffer   intBuffer;
		Point3f  v = new Point3f(); 
		Vector3f n = new Vector3f();

		floatBuffer = ByteBuffer.allocateDirect(N_BYTES_FLOAT * nVertex * 3).order(order).asFloatBuffer();
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
		gt.setCoords(floatBuffer);
		
		intBuffer = ByteBuffer.allocateDirect(N_BYTES_INT * nVertex).order(order).asIntBuffer();
		for(int i=0;i<nVertex;i++){
			intBuffer.put(i);
		}
		gt.setCoordIndices(intBuffer);
		
		if(textCoord){
			floatBuffer = ByteBuffer.allocateDirect(N_BYTES_FLOAT * nVertex * 2).order(order).asFloatBuffer();
			while(!texIdxList.isEmpty()){
				TexCoord2f t = texList.get(texIdxList.poll());
				floatBuffer.put(t.x);
				floatBuffer.put(t.y);
			}
			texList.clear();
			gt.setTexCoords(0,floatBuffer);
		}
		
		if(normal){
			floatBuffer = ByteBuffer.allocateDirect(N_BYTES_FLOAT * nVertex * 3).order(order).asFloatBuffer();
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
			gt.setNormals(floatBuffer);
		}
		return gt;
	}
	//*/
	
	OglList almacenarGeometria(GL gl, Matrix4d mt){
		boolean textCoord = texList.size() > 0;
		boolean normal = normList.size() > 0;
		Point3f  v = new Point3f(); 
		Vector3f n = new Vector3f();
		
		if ( (flags & RESIZE) != 0 ){
			if(mt != null)
				mt.mul(escalarYCentrar());
			else
				mt = escalarYCentrar();
		}
		
		int lid = gl.glGenLists(1);
		
		gl.glNewList(lid, GL.GL_COMPILE);
		gl.glBegin(
				(flags & ObjLoader.TRIANGULATE) != 0 ?
						GL.GL_TRIANGLES:
						GL.GL_QUADS
		);
		while(!coordIdxList.isEmpty()){
			if(textCoord){
				TexCoord2f t = texList.get(texIdxList.poll());
				gl.glTexCoord2f(t.x,t.y);
			}
			if (normal){
				n.set(normList.get(normIdxList.poll()));
				try{
					mt.transform(n);
				}catch(NullPointerException noHayTransfrom){
				}
				n.normalize();
				gl.glNormal3f(n.x, n.y, n.z);
			}
			
			v.set(coordList.get(coordIdxList.poll()));
			try{
				mt.transform(v);
			}catch(NullPointerException noHayTransfrom){
			}
			gl.glVertex3f(v.x,v.y,v.z);
			
		}
		gl.glEnd();
		gl.glEndList();

		if(textCoord)
			texList.clear();
		if(normal)
			normList.clear();
		coordList.clear();
		return new OglList(lid);
	}
	
	private static Objeto3D load(Reader reader, int flags, Matrix4d mt) throws ParsingErrorException{
		ObjLoader loader = new ObjLoader(flags);
		loader.read(new ObjParser(new BufferedReader(reader)));
		return new Objeto3D(loader.generarGeometria(mt), new Apariencia());
	}

	public static Objeto3D load(String filename) throws FileNotFoundException, ParsingErrorException{
		return load(filename, NONE, null);
	}
	
	public static Objeto3D load(String filename, int flags) throws FileNotFoundException, ParsingErrorException{
		return load(filename, flags, null);
	}
	
	public static Objeto3D load(String filename, Matrix4d mt) throws FileNotFoundException, ParsingErrorException{
		return load(filename, NONE, mt);
	}
	
	public static Objeto3D load(String filename, int flags, Matrix4d mt) throws FileNotFoundException, ParsingErrorException{
		return load(new FileReader(filename),flags, mt);
	}

	public static Objeto3D load(URL url) throws IOException, ParsingErrorException{
		return load( url, NONE, null);
	}
	
	public static Objeto3D load(URL url, int flags) throws IOException, ParsingErrorException{
		return load( url, flags, null );
	}
	
	public static Objeto3D load(URL url, Matrix4d mt) throws IOException, ParsingErrorException{
		return load( url, NONE, mt );
	}
	
	public static Objeto3D load(URL url, int flags, Matrix4d mt) throws IOException, ParsingErrorException{
		return load(new InputStreamReader(url.openStream()),flags, mt);
	}
	
	private static Objeto3D loadToList(Reader reader, int flags, Matrix4d mt) throws ParsingErrorException{
		ObjLoader loader = new ObjLoader(flags);
		loader.read(new ObjParser(new BufferedReader(reader)));
		return new Objeto3D(loader.almacenarGeometria( GLU.getCurrentGL(), mt), new Apariencia());
	}
	
	public static Objeto3D loadToList(String filename) throws FileNotFoundException, ParsingErrorException{
		return loadToList( filename, NONE, null);
	}
	
	public static Objeto3D loadToList(String filename, int flags) throws FileNotFoundException, ParsingErrorException{
		return loadToList( filename, flags, null);
	}
	
	public static Objeto3D loadToList(String filename, Matrix4d mt) throws FileNotFoundException, ParsingErrorException{
		return loadToList( filename, NONE, mt);
	}
	
	public static Objeto3D loadToList(String filename, int flags, Matrix4d mt) throws FileNotFoundException, ParsingErrorException{
		return loadToList(new FileReader(filename),flags, mt);
	}

	public static Objeto3D loadToList(URL url) throws IOException, ParsingErrorException{
		return loadToList( url, NONE, null);
	}
	
	public static Objeto3D loadToList(URL url, int flags) throws IOException, ParsingErrorException{
		return loadToList( url, flags, null);
	}
	
	public static Objeto3D loadToList(URL url, Matrix4d mt) throws IOException, ParsingErrorException{
		return loadToList( url, NONE, mt);
	}
	
	public static Objeto3D loadToList(URL url, int flags, Matrix4d mt) throws IOException, ParsingErrorException{
		return loadToList(new InputStreamReader(url.openStream()),flags, mt);
	}
}
