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
	public static final int TO_GEOMETRY						=	0x01;
	public static final int RESIZE							=	0x02;
	public static final int TRIANGULATE						=	0x04;
	public static final int MUST_FLIP_VERTICALLY_TEXCOORDS	=	0x08;
	
	@SuppressWarnings("serial")
	public static class ParsingErrorException extends RuntimeException {
		public ParsingErrorException() {
			super();
		}
		public ParsingErrorException(String s) {
			super(s);
		}
	}
	
	private static final int N_BYTES_FLOAT = 4;
	
	private static class ObjParser extends StreamTokenizer {
		
		private static final char BACKSLASH = '\\';
		
		private ObjParser(Reader r) {
			super(r);
			setup();
		} 
		
		private void setup() {
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
		
		private void getToken() throws ParsingErrorException {
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
		
		private void skipToNextLine() throws ParsingErrorException {
			while (ttype != StreamTokenizer.TT_EOF && ttype != StreamTokenizer.TT_EOL) {
				getToken();
			}
		}
		
		private float getFloat() throws ParsingErrorException {
			do{
				getToken();
			}while(ttype == StreamTokenizer.TT_EOL);
			return getLastValueAsFloat();
		}
		
		private float getLastValueAsFloat() throws ParsingErrorException {
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

		private int getInteger() throws ParsingErrorException {
			do{
				getToken();
			}while(ttype == StreamTokenizer.TT_EOL);
			return getLastValueAsInteger();
		}

		private int getLastValueAsInteger() throws ParsingErrorException {
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
	
	private static abstract class Primitive{
		abstract Triangle[] toTriangles();
		abstract Quad toQuad();
		abstract void draw(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final List<TexCoord2f> texList
		);
		abstract void draw(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final Vector3f[][] tangents,
				final List<TexCoord2f> texList
		);
	}
	
	private static class Triangle extends Primitive{
		int indexP1, indexP2, indexP3;
		int indexN1, indexN2, indexN3;
		int indexT1, indexT2, indexT3;
		
		Triangle(
				int indexP1, int indexP2, int indexP3,
				int indexN1, int indexN2, int indexN3,
				int indexT1, int indexT2, int indexT3
		){
			this.indexP1 = indexP1;
			this.indexP2 = indexP2;
			this.indexP3 = indexP3;
			this.indexN1 = indexN1;
			this.indexN2 = indexN2;
			this.indexN3 = indexN3;
			this.indexT1 = indexT1;
			this.indexT2 = indexT2;
			this.indexT3 = indexT3;
		}
		
		Triangle[] toTriangles(){
			return new Triangle[]{ this };
		}
		
		Quad toQuad(){
			return new Quad(
				indexP1, indexP2, indexP2, indexP3,
				indexN1, indexN2, indexN2, indexN3,
				indexT1, indexT2, indexT2, indexT3
			);
		}
		
		void draw(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final List<TexCoord2f> texList
		){
			if(indexN1 >= 0){
				Vector3f n = normList.get(indexN1);
				gl.glNormal3f(n.x, n.y, n.z);
			}
			if(indexT1 >= 0){
				TexCoord2f t = texList.get(indexT1);
				gl.glTexCoord2f(t.x,t.y);
			}
			Point3f v = coordList.get(indexP1);
			gl.glVertex3f(v.x,v.y,v.z);
			
			if(indexN2 >= 0){
				Vector3f n = normList.get(indexN2);
				gl.glNormal3f(n.x, n.y, n.z);
			}
			if(indexT2 >= 0){
				TexCoord2f t = texList.get(indexT2);
				gl.glTexCoord2f(t.x,t.y);
			}
			v = coordList.get(indexP2);
			gl.glVertex3f(v.x,v.y,v.z);
			
			if(indexN3 >= 0){
				Vector3f n = normList.get(indexN3);
				gl.glNormal3f(n.x, n.y, n.z);
			}
			if(indexT3 >= 0){
				TexCoord2f t = texList.get(indexT3);
				gl.glTexCoord2f(t.x,t.y);
			}
			v = coordList.get(indexP3);
			gl.glVertex3f(v.x,v.y,v.z);
		}
		
		void draw(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final Vector3f[][] tangList,
				final List<TexCoord2f> texList
		){
		}
	}
	
	private static class Quad extends Primitive{
		int indexP1, indexP2, indexP3, indexP4;
		int indexN1, indexN2, indexN3, indexN4;
		int indexT1, indexT2, indexT3, indexT4;
		
		Quad(
				int indexP1, int indexP2, int indexP3, int indexP4,
				int indexN1, int indexN2, int indexN3, int indexN4,
				int indexT1, int indexT2, int indexT3, int indexT4
		){
			this.indexP1 = indexP1;
			this.indexP2 = indexP2;
			this.indexP3 = indexP3;
			this.indexP4 = indexP4;
			
			this.indexN1 = indexN1;
			this.indexN2 = indexN2;
			this.indexN3 = indexN3;
			this.indexN4 = indexN4;
			
			this.indexT1 = indexT1;
			this.indexT2 = indexT2;
			this.indexT3 = indexT3;
			this.indexT4 = indexT4;
		}
		
		Triangle[] toTriangles(){
			return new Triangle[]{
					new Triangle(
							indexP1, indexP2, indexP3,
							indexN1, indexN2, indexN3,
							indexT1, indexT2, indexT3
					),
					new Triangle(
							indexP3, indexP4, indexP1,
							indexN3, indexN4, indexN1,
							indexT3, indexT4, indexT1
					)
			};
		}
		
		Quad toQuad(){
			return this;
		}
		
		void draw(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final List<TexCoord2f> texList
		){
			if(indexN1 >= 0){
				Vector3f n = normList.get(indexN1);
				gl.glNormal3f(n.x, n.y, n.z);
			}
			if(indexT1 >= 0){
				TexCoord2f t = texList.get(indexT1);
				gl.glTexCoord2f(t.x,t.y);
			}
			Point3f v = coordList.get(indexP1);
			gl.glVertex3f(v.x,v.y,v.z);
			
			if(indexN2 >= 0){
				Vector3f n = normList.get(indexN2);
				gl.glNormal3f(n.x, n.y, n.z);
			}
			if(indexT2 >= 0){
				TexCoord2f t = texList.get(indexT2);
				gl.glTexCoord2f(t.x,t.y);
			}
			v = coordList.get(indexP2);
			gl.glVertex3f(v.x,v.y,v.z);
			
			if(indexN3 >= 0){
				Vector3f n = normList.get(indexN3);
				gl.glNormal3f(n.x, n.y, n.z);
			}
			if(indexT3 >= 0){
				TexCoord2f t = texList.get(indexT3);
				gl.glTexCoord2f(t.x,t.y);
			}
			v = coordList.get(indexP3);
			gl.glVertex3f(v.x,v.y,v.z);
			
			if(indexN4 >= 0){
				Vector3f n = normList.get(indexN4);
				gl.glNormal3f(n.x, n.y, n.z);
			}
			if(indexT4 >= 0){
				TexCoord2f t = texList.get(indexT4);
				gl.glTexCoord2f(t.x,t.y);
			}
			v = coordList.get(indexP4);
			gl.glVertex3f(v.x,v.y,v.z);
		}
		
		void draw(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final Vector3f[][] tangents,
				final List<TexCoord2f> texList
		){
			final Point3f p00 = coordList.get(indexP1);
			final Point3f p10 = coordList.get(indexP2);
			final Point3f p11 = coordList.get(indexP3);
			final Point3f p01 = coordList.get(indexP4);
			
			final Vector3f n00 = normList.get(indexN1);
			final Vector3f n10 = normList.get(indexN2);
			final Vector3f n11 = normList.get(indexN3);
			final Vector3f n01 = normList.get(indexN4);
			
			final Vector4f t00 = new Vector4f();
			final Vector4f t10 = new Vector4f();
			final Vector4f t11 = new Vector4f();
			final Vector4f t01 = new Vector4f();

			final TexCoord2f c00 = texList.get(indexT1);
			final TexCoord2f c10 = texList.get(indexT2);
			final TexCoord2f c11 = texList.get(indexT3);
			final TexCoord2f c01 = texList.get(indexT4);
			
			float s1 = c11.x + c01.x - c10.x - c00.x;
			float s2 = c11.x + c10.x - c01.x - c00.x;
			float t1 = c11.y + c01.y - c10.y - c00.y;
			float t2 = c11.y + c10.y - c01.y - c00.y;
			float r = 1.0f / (s1 * t2 - s2 * t1);
			
			final Vector2f s = new Vector2f(s1, s2); s.scale(r);
			final Vector2f t = new Vector2f(t1, t2); t.scale(r);
			
			calculateTangent(n00, s, t, tangents[0][indexN1], tangents[1][indexN1], t00);
			calculateTangent(n10, s, t, tangents[0][indexN2], tangents[1][indexN2], t10);
			calculateTangent(n11, s, t, tangents[0][indexN3], tangents[1][indexN3], t11);
			calculateTangent(n01, s, t, tangents[0][indexN4], tangents[1][indexN4], t01);
			
			gl.glNormal3f(          n00.x, n00.y, n00.z );
			gl.glVertexAttrib4f( 1, t00.x, t00.y, t00.z, t00.w );
			gl.glTexCoord2f(        c00.x, c00.y );
			gl.glVertex3f(          p00.x, p00.y, p00.z );
			
			gl.glNormal3f(          n10.x, n10.y, n10.z );
			gl.glVertexAttrib4f( 1, t10.x, t10.y, t10.z, t10.w );
			gl.glTexCoord2f(        c10.x, c10.y );
			gl.glVertex3f(          p10.x, p10.y, p10.z );
			
			gl.glNormal3f(          n11.x, n11.y, n11.z );
			gl.glVertexAttrib4f( 1, t11.x, t11.y, t11.z, t11.w );
			gl.glTexCoord2f(        c11.x, c11.y );
			gl.glVertex3f(          p11.x, p11.y, p11.z );
			
			gl.glNormal3f(          n01.x, n01.y, n01.z );
			gl.glVertexAttrib4f( 1, t01.x, t01.y, t01.z, t01.w );
			gl.glTexCoord2f(        c01.x, c01.y );
			gl.glVertex3f(          p01.x, p01.y, p01.z );
		}
	}
	
	private final List<Point3f> coordList;
	private final List<TexCoord2f> texList;
	private final List<Vector3f> normList;
	
	int nVertex = 0;
	private final Queue<Primitive> primitives;
	
	private float minX, maxX;
	private float minY, maxY;
	private float minZ, maxZ;
	
	private ObjLoader(int flags){
		this.flags = flags;
		coordList = new ArrayList<Point3f>(512);
		texList = new ArrayList<TexCoord2f>(512);
		normList = new ArrayList<Vector3f>(512);
		
		primitives = new LinkedList<Primitive>();
		
		if((flags & ObjLoader.RESIZE) != 0){
			minX = Float.MAX_VALUE; maxX= -Float.MAX_VALUE;
			minY = Float.MAX_VALUE; maxY= -Float.MAX_VALUE;
			minZ = Float.MAX_VALUE; maxZ= -Float.MAX_VALUE;
		}
	}
	
	private void read(ObjParser st) throws ParsingErrorException {
		do{
			st.getToken();
			if (st.ttype == StreamTokenizer.TT_WORD) {
				if (st.sval.equals("v"))
					readVertex(st);
				else if (st.sval.equals("vn"))
					readNormal(st);
				else if (st.sval.equals("vt"))
					readTexture(st);
				else if (st.sval.equals("f") || st.sval.equals("fo"))
					primitives.offer( readPrimitive(st) );
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

	private void readVertex(ObjParser st) throws ParsingErrorException {
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

	private void readTexture(ObjParser st) throws ParsingErrorException {
		TexCoord2f t = new TexCoord2f();
		t.x = st.getFloat();
		t.y = (flags & MUST_FLIP_VERTICALLY_TEXCOORDS) != 0 ? 1.0f - st.getFloat() : st.getFloat();
		texList.add(t);
	}
	
	private void readNormal(ObjParser st) throws ParsingErrorException {
		Vector3f n = new Vector3f();
		n.x = st.getFloat();
		n.y = st.getFloat();
		n.z = st.getFloat();
		normList.add(n);
	}

	private Primitive readPrimitive(ObjParser st) throws ParsingErrorException {
		int[] vertIndex = new int[]{ -1, -1, -1, -1 };
		int[] texIndex  = new int[]{ -1, -1, -1, -1 };
		int[] normIndex = new int[]{ -1, -1, -1, -1 };
		int count = 0;
		
		st.getToken();
		do{
			if (st.ttype == StreamTokenizer.TT_WORD){
				vertIndex[count] = st.getLastValueAsInteger() - 1;
				if (vertIndex[count] < 0)
					vertIndex[count] += coordList.size() + 1;
			}
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
					normIndex[count] = st.getInteger() - 1;
					if (normIndex[count] < 0)
						normIndex[count] += normList.size() + 1;
					st.getToken();
				}
			}
			count++;
		}while (st.ttype != StreamTokenizer.TT_EOF && st.ttype != StreamTokenizer.TT_EOL);

		if(count == 3){
			return new Triangle(
				vertIndex[0], vertIndex[1], vertIndex[2],
				normIndex[0], normIndex[1], normIndex[2],
				texIndex [0], texIndex [1], texIndex [2]
			);
		}
		return new Quad(
			vertIndex[0], vertIndex[1], vertIndex[2], vertIndex[3],
			normIndex[0], normIndex[1], normIndex[2], normIndex[3],
			texIndex [0], texIndex [1], texIndex [2], texIndex [3]
		);
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
	
	private Geometria generarGeometria(Matrix4d mt){
				
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
				
		// TODO Arrgeglar
		/*
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
			if(mt != null)
				mt.transform(v);
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
				if(mt != null)
					mt.transform(n);
				n.normalize();
				floatBuffer.put(n.x);
				floatBuffer.put(n.y);
				floatBuffer.put(n.z);
			}
			normList.clear();
			gt.setNormals(floatBuffer);
		}
		*/
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
	
	private static void generateTangents(final Queue<Primitive> primitives, final List<Point3f> coordList, Vector3f[][] tangents){
		
		for ( int i= 0; i < tangents[0].length; i++ ){
			tangents[0][i] = new Vector3f();
			tangents[1][i] = new Vector3f();
		}
		final Vector3f tangent = new Vector3f();
		final Vector3f bitangent = new Vector3f();
		
		for (Primitive p: primitives) {
			Quad q = p.toQuad();
				
				final Point3f p00 = coordList.get(q.indexP1);
				final Point3f p10 = coordList.get(q.indexP2);
				final Point3f p11 = coordList.get(q.indexP3);
				final Point3f p01 = coordList.get(q.indexP4);

				tangent.set(
						p11.x + p01.x - p10.x - p00.x,
						p11.y + p01.y - p10.y - p00.y,
						p11.z + p01.z - p10.z - p00.z
				);
				
				bitangent.set(
						p11.x + p10.x - p01.x - p00.x,
						p11.y + p10.y - p01.y - p00.y,
			            p11.z + p10.z - p01.z - p00.z
				);
				
				tangents[0][q.indexN1].add(tangent);
				tangents[0][q.indexN2].add(tangent);
				tangents[0][q.indexN3].add(tangent);
				tangents[0][q.indexN4].add(tangent);
				
				tangents[1][q.indexN1].add(bitangent);
				tangents[1][q.indexN2].add(bitangent);
				tangents[1][q.indexN3].add(bitangent);
				tangents[1][q.indexN4].add(bitangent);
		}
	}
	
	static float dot(Vector3f v1, Vector3f v2){
		return (v1.x*v2.x + v1.y*v2.y + v1.z*v2.z);
	}
	
	private static void orthogonalizeGramSchmidt(Vector3f n, Vector3f t){
	    // tOrtho = normalize( t - n·dot(n, t) )
		float d = dot( n, t );
		t.set(
			t.x - n.x*d,
			t.y - n.y*d,
			t.z - n.z*d
		);
		t.normalize();
	}
	
	/**
	 * @return dot( cross(v1, v2), v3 )
	 */
	static float dotcross(Vector3f v1, Vector3f v2, Vector3f v3){
		return ((v1.y*v2.z - v1.z*v2.y)*v3.x + (v2.x*v1.z - v2.z*v1.x)*v3.y + (v1.x*v2.y - v1.y*v2.x)*v3.z);
	}
	
	private static void calculateTangent(Vector3f normal, Vector2f s, Vector2f t, Vector3f uVec, Vector3f vVec, Vector4f t4f){
		Vector3f sDir = new Vector3f(
				(t.y * uVec.x - t.x * vVec.x),
				(t.y * uVec.y - t.x * vVec.y),
	            (t.y * uVec.z - t.x * vVec.z)
		);
		sDir.normalize();
		Vector3f tDir = new Vector3f(
				(s.y * uVec.x + s.x * vVec.x),
				(s.y * uVec.y + s.x * vVec.y),
	            (s.y * uVec.z + s.x * vVec.z)
		);
		t4f.set( sDir.x, sDir.y, sDir.z, dotcross( normal, sDir, tDir ) < 0.0f ? -1.0f : 1.0f );
	}
	
	private OglList almacenarGeometria(GL gl, Matrix4d mt){
		boolean textCoord = texList.size() > 0;
		boolean normal = normList.size() > 0;
		
		if ( (flags & RESIZE) != 0 ){
			if(mt != null)
				mt.mul(escalarYCentrar());
			else
				mt = escalarYCentrar();
		}
		if(mt != null){
			for(Point3f  v: coordList)
				mt.transform(v);
			for(Vector3f n: normList){
				mt.transform(n);
				n.normalize();
			}
		}
		
		Vector3f[][] tb  = new Vector3f[2][normList.size()];
		
		generateTangents( primitives, coordList, tb );
		for (int i = 0; i < normList.size(); i++) {
			orthogonalizeGramSchmidt(normList.get(i),tb[0][i]);
			orthogonalizeGramSchmidt(normList.get(i),tb[1][i]);
		}
		
		int lid = gl.glGenLists(1);
		gl.glNewList(lid, GL.GL_COMPILE);
		if( (flags & ObjLoader.TRIANGULATE) != 0){
			gl.glBegin(	GL.GL_TRIANGLES );
			for(Primitive p: primitives)
				for(Triangle t: p.toTriangles())
					t.draw(gl, coordList, normList, texList);
		}else{
			gl.glBegin(	GL.GL_QUADS );
			for(Primitive p: primitives)
				p.toQuad().draw(gl, coordList, normList, tb, texList);
		}
		gl.glEnd();
		gl.glEndList();

		if(textCoord)
			texList.clear();
		if(normal)
			normList.clear();
		coordList.clear();
		primitives.clear();
		return new OglList(lid);
	}
	
	private static Objeto3D load(Reader reader, int flags, Matrix4d mt) throws ParsingErrorException{
		ObjLoader loader = new ObjLoader(flags);
		loader.read(new ObjParser(new BufferedReader(reader)));
		Apariencia ap = new Apariencia();
		ap.setMaterial(Material.DEFAULT);
		if( (flags & TO_GEOMETRY) != 0)
			return new Objeto3D( loader.generarGeometria(mt), ap );
		return new Objeto3D( loader.almacenarGeometria( GLU.getCurrentGL(), mt), ap );
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
}
