package org.sam.jogl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class ObjLoader {
	
	public static final int NONE							=	0x00;
	public static final int TO_GEOMETRY						=	0x01;
	public static final int RESIZE							=	0x02;
	public static final int TRIANGULATE						=	0x04;
	public static final int MUST_FLIP_VERTICALLY_TEXCOORDS	=	0x08;
	public static final int GENERATE_TANGENTS				=	0x10;
	public static final int GENERATE_NTB					=	0x20;
	
	@SuppressWarnings("serial")
	public static class ParsingErrorException extends RuntimeException {
		public ParsingErrorException() {
			super();
		}
		public ParsingErrorException(String s) {
			super(s);
		}
	}
	
	private static final int N_BYTES_FLOAT = Float.SIZE / Byte.SIZE;
	
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
		abstract void generateTangents(final List<Point3f> coordList, Vector3f[][] tangents);
		abstract void generate(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final List<TexCoord2f> texList
		);
		abstract void generate(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final Vector3f[][] tangents,
				final List<TexCoord2f> texList
		);
		abstract void generateNTB(
				GL gl,
				List<Point3f> coordList,
				List<Vector3f> normList,
				Vector3f[][] tangents,
				List<TexCoord2f> texList,
				float scale
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
		
		private static transient final Vector3f tangent = new Vector3f();
		private static transient final Vector3f bitangent = new Vector3f();
		
		void generateTangents(final List<Point3f> coordList, Vector3f[][] tangents){
			final Point3f p0 = coordList.get(indexP1);
			final Point3f p1 = coordList.get(indexP2);
			final Point3f p2 = coordList.get(indexP3);

			tangent.set(
					p1.x - p0.x,
					p1.y - p0.y,
					p1.z - p0.z
			);
			
			bitangent.set(
					p2.x - p0.x,
					p2.y - p0.y,
		            p2.z - p0.z
			);
			
			tangents[0][indexN1].add(tangent);
			tangents[0][indexN2].add(tangent);
			tangents[0][indexN3].add(tangent);
			
			tangents[1][indexN1].add(bitangent);
			tangents[1][indexN2].add(bitangent);
			tangents[1][indexN3].add(bitangent);
		}
		
		void generate(
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
		
		void generate(
				GL gl,
				final List<Point3f> coordList,
				final List<Vector3f> normList,
				final Vector3f[][] tangents,
				final List<TexCoord2f> texList
		){
			final Point3f p00 = coordList.get(indexP1);
			final Point3f p10 = coordList.get(indexP2);
			final Point3f p11 = coordList.get(indexP3);
			
			final Vector3f n00 = normList.get(indexN1);
			final Vector3f n10 = normList.get(indexN2);
			final Vector3f n11 = normList.get(indexN3);
			
			final Vector4f t00 = new Vector4f();
			final Vector4f t10 = new Vector4f();
			final Vector4f t11 = new Vector4f();

			final TexCoord2f c00 = texList.get(indexT1);
			final TexCoord2f c10 = texList.get(indexT2);
			final TexCoord2f c11 = texList.get(indexT3);
			
			float s1 = c11.x - c00.x;
			float s2 = c10.x - c00.x;
			float t1 = c11.y - c00.y;
			float t2 = c10.y - c00.y;
			float r = 1.0f / (s1 * t2 - s2 * t1);
			
			final Vector2f s = new Vector2f(s1, s2); s.scale(r);
			final Vector2f t = new Vector2f(t1, t2); t.scale(r);
			
			calculateTangent(n00, s, t, tangents[0][indexN1], tangents[1][indexN1], t00);
			calculateTangent(n10, s, t, tangents[0][indexN2], tangents[1][indexN2], t10);
			calculateTangent(n11, s, t, tangents[0][indexN3], tangents[1][indexN3], t11);
			
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
		}
		
		void generateNTB(
				GL gl,
				List<Point3f> coordList,
				List<Vector3f> normList,
				Vector3f[][] tangents,
				List<TexCoord2f> texList,
				float scale
		){
			final Point3f vert1 = coordList.get(indexP1);
			final Point3f vert2 = coordList.get(indexP2);
			final Point3f vert3 = coordList.get(indexP3);
			
			final Vector3f norm1 = normList.get(indexN1);
			final Vector3f norm2 = normList.get(indexN2);
			final Vector3f norm3 = normList.get(indexN3);
			
			final Vector4f tang1 = new Vector4f();
			final Vector4f tang2 = new Vector4f();
			final Vector4f tang3 = new Vector4f();

			final TexCoord2f c00 = texList.get(indexT1);
			final TexCoord2f c10 = texList.get(indexT2);
			final TexCoord2f c11 = texList.get(indexT3);
			
			float s1 = c11.x - c00.x;
			float s2 = c10.x - c00.x;
			float t1 = c11.y - c00.y;
			float t2 = c10.y - c00.y;
			float r = 1.0f / (s1 * t2 - s2 * t1);
			
			final Vector2f s = new Vector2f(s1, s2); s.scale(r);
			final Vector2f t = new Vector2f(t1, t2); t.scale(r);
			
			calculateTangent(norm1, s, t, tangents[0][indexN1], tangents[1][indexN1], tang1);
			calculateTangent(norm2, s, t, tangents[0][indexN2], tangents[1][indexN2], tang2);
			calculateTangent(norm3, s, t, tangents[0][indexN3], tangents[1][indexN3], tang3);
			
			gl.glColor3f(0.5f, 1.0f, 0.0f);

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + norm1.x * scale, vert1.y + norm1.y * scale, vert1.z + norm1.z * scale );

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + norm2.x * scale, vert2.y + norm2.y * scale, vert2.z + norm2.z * scale );

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + norm3.x * scale, vert3.y + norm3.y * scale, vert3.z + norm3.z * scale );

			gl.glColor3f(1.0f, 0.0f, 0.5f);

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + tang1.x * scale, vert1.y + tang1.y * scale, vert1.z + tang1.z * scale );

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + tang2.x * scale, vert2.y + tang2.y * scale, vert2.z + tang2.z * scale );

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + tang3.x * scale, vert3.y + tang3.y * scale, vert3.z + tang3.z * scale );

			final Vector3f bitang = new Vector3f();
			gl.glColor3f(0.0f, 0.5f, 1.0f);

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			bitang.set(
					(norm1.y*tang1.z - norm1.z*tang1.y)*Math.signum(tang1.w),
					(tang1.x*norm1.z - tang1.z*norm1.x)*Math.signum(tang1.w),
					(norm1.x*tang1.y - norm1.y*tang1.x)*Math.signum(tang1.w)
			);
			gl.glVertex3f( vert1.x + bitang.x * scale, vert1.y + bitang.y * scale, vert1.z + bitang.z * scale);

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			bitang.set(
					(norm2.y*tang2.z - norm2.z*tang2.y)*Math.signum(tang2.w),
					(tang2.x*norm2.z - tang2.z*norm2.x)*Math.signum(tang2.w),
					(norm2.x*tang2.y - norm2.y*tang2.x)*Math.signum(tang2.w)
			);
			gl.glVertex3f( vert2.x + bitang.x * scale, vert2.y + bitang.y * scale, vert2.z + bitang.z * scale);

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			bitang.set(
					(norm3.y*tang3.z - norm3.z*tang3.y)*Math.signum(tang3.w),
					(tang3.x*norm3.z - tang3.z*norm3.x)*Math.signum(tang3.w),
					(norm3.x*tang3.y - norm3.y*tang3.x)*Math.signum(tang3.w)
			);

			gl.glVertex3f( vert3.x + bitang.x * scale, vert3.y + bitang.y * scale, vert3.z + bitang.z * scale);
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
		
		private static transient final Vector3f tangent = new Vector3f();
		private static transient final Vector3f bitangent = new Vector3f();
		
		void generateTangents(final List<Point3f> coordList, Vector3f[][] tangents){
			final Point3f p00 = coordList.get(indexP1);
			final Point3f p10 = coordList.get(indexP2);
			final Point3f p11 = coordList.get(indexP3);
			final Point3f p01 = coordList.get(indexP4);

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
			
			tangents[0][indexN1].add(tangent);
			tangents[0][indexN2].add(tangent);
			tangents[0][indexN3].add(tangent);
			tangents[0][indexN4].add(tangent);
			
			tangents[1][indexN1].add(bitangent);
			tangents[1][indexN2].add(bitangent);
			tangents[1][indexN3].add(bitangent);
			tangents[1][indexN4].add(bitangent);
		}
		
		void generate(
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
		
		void generate(
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
		
		void generateNTB(
				GL gl,
				List<Point3f> coordList,
				List<Vector3f> normList,
				Vector3f[][] tangents,
				List<TexCoord2f> texList,
				float scale
		){
			final Point3f vert1 = coordList.get(indexP1);
			final Point3f vert2 = coordList.get(indexP2);
			final Point3f vert3 = coordList.get(indexP3);
			final Point3f vert4 = coordList.get(indexP4);
			
			final Vector3f norm1 = normList.get(indexN1);
			final Vector3f norm2 = normList.get(indexN2);
			final Vector3f norm3 = normList.get(indexN3);
			final Vector3f norm4 = normList.get(indexN4);
			
			final Vector4f tang1 = new Vector4f();
			final Vector4f tang2 = new Vector4f();
			final Vector4f tang3 = new Vector4f();
			final Vector4f tang4 = new Vector4f();

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
			
			calculateTangent(norm1, s, t, tangents[0][indexN1], tangents[1][indexN1], tang1);
			calculateTangent(norm2, s, t, tangents[0][indexN2], tangents[1][indexN2], tang2);
			calculateTangent(norm3, s, t, tangents[0][indexN3], tangents[1][indexN3], tang3);
			calculateTangent(norm4, s, t, tangents[0][indexN4], tangents[1][indexN4], tang4);
			
			gl.glColor3f(0.5f, 1.0f, 0.0f);

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + norm1.x * scale, vert1.y + norm1.y * scale, vert1.z + norm1.z * scale );

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + norm2.x * scale, vert2.y + norm2.y * scale, vert2.z + norm2.z * scale );

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + norm3.x * scale, vert3.y + norm3.y * scale, vert3.z + norm3.z * scale );

			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + norm4.x * scale, vert4.y + norm4.y * scale, vert4.z + norm4.z * scale );

			gl.glColor3f(1.0f, 0.0f, 0.5f);

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + tang1.x * scale, vert1.y + tang1.y * scale, vert1.z + tang1.z * scale );

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + tang2.x * scale, vert2.y + tang2.y * scale, vert2.z + tang2.z * scale );

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + tang3.x * scale, vert3.y + tang3.y * scale, vert3.z + tang3.z * scale );

			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + tang4.x * scale, vert4.y + tang4.y * scale, vert4.z + tang4.z * scale );

			final Vector3f bitang = new Vector3f();
			gl.glColor3f(0.0f, 0.5f, 1.0f);

			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			bitang.set(
					(norm1.y*tang1.z - norm1.z*tang1.y)*Math.signum(tang1.w),
					(tang1.x*norm1.z - tang1.z*norm1.x)*Math.signum(tang1.w),
					(norm1.x*tang1.y - norm1.y*tang1.x)*Math.signum(tang1.w)
			);
			gl.glVertex3f( vert1.x + bitang.x * scale, vert1.y + bitang.y * scale, vert1.z + bitang.z * scale);

			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			bitang.set(
					(norm2.y*tang2.z - norm2.z*tang2.y)*Math.signum(tang2.w),
					(tang2.x*norm2.z - tang2.z*norm2.x)*Math.signum(tang2.w),
					(norm2.x*tang2.y - norm2.y*tang2.x)*Math.signum(tang2.w)
			);
			gl.glVertex3f( vert2.x + bitang.x * scale, vert2.y + bitang.y * scale, vert2.z + bitang.z * scale);

			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			bitang.set(
					(norm3.y*tang3.z - norm3.z*tang3.y)*Math.signum(tang3.w),
					(tang3.x*norm3.z - tang3.z*norm3.x)*Math.signum(tang3.w),
					(norm3.x*tang3.y - norm3.y*tang3.x)*Math.signum(tang3.w)
			);

			gl.glVertex3f( vert3.x + bitang.x * scale, vert3.y + bitang.y * scale, vert3.z + bitang.z * scale);

			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			bitang.set(
					(norm4.y*tang4.z - norm4.z*tang4.y)*Math.signum(tang4.w),
					(tang4.x*norm4.z - tang4.z*norm4.x)*Math.signum(tang4.w),
					(norm4.x*tang4.y - norm4.y*tang4.x)*Math.signum(tang4.w)
			);
			gl.glVertex3f( vert4.x + bitang.x * scale, vert4.y + bitang.y * scale, vert4.z + bitang.z * scale );
		}
	}
	
	private final List<Point3f> coordList;
	private final List<Vector3f> normList;
	private final List<TexCoord2f> texList;
	
	
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
		
		minX = Float.MAX_VALUE; maxX= -Float.MAX_VALUE;
		minY = Float.MAX_VALUE; maxY= -Float.MAX_VALUE;
		minZ = Float.MAX_VALUE; maxZ= -Float.MAX_VALUE;
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

	private static float distance(Tuple3f t1, Tuple3f t2){
		float dx = t1.x-t2.x;
		float dy = t1.y-t2.y;
		float dz = t1.z-t2.z;
		return (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
	}
	
	private static float clearDist;
	
	private void readVertex(ObjParser st) throws ParsingErrorException {
		Point3f v = new Point3f(){
			public boolean equals(Object t){
		        try {
		            return ObjLoader.distance(this, (Tuple3f)t) <= clearDist;
		         }
		         catch (NullPointerException e2) {return false;}
		         catch (ClassCastException   e1) {return false;}
			}
		};
		v.x = st.getFloat();
		v.y = st.getFloat();
		v.z = st.getFloat();
		coordList.add(v);

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

	private void readTexture(ObjParser st) throws ParsingErrorException {
		TexCoord2f t = new TexCoord2f();
		t.x = st.getFloat();
		t.y = (flags & MUST_FLIP_VERTICALLY_TEXCOORDS) != 0 ? 1.0f - st.getFloat() : st.getFloat();
		texList.add(t);
	}
	
	private void readNormal(ObjParser st) throws ParsingErrorException {
		Vector3f n = new Vector3f(){
			public boolean equals(Object t){
		        try {
		            return dot((Vector3f)t) > 0.000995f;
		         }
		         catch (NullPointerException e2) {return false;}
		         catch (ClassCastException   e1) {return false;}
			}
		};
		n.x = st.getFloat();
		n.y = st.getFloat();
		n.z = st.getFloat();
		n.normalize();
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
	
	private double getMaxDistance(){
		double absX = maxX - minX;
		double absY = maxY - minY;
		double absZ = maxZ - minZ;
		
		return Math.max(absX, Math.max(absY,absZ));
	}
	
	private Matrix4d escalarYCentrar(){
		double centerX = (minX + maxX)/2;
		double centerY = (minY + maxY)/2;
		double centerZ = (minZ + maxZ)/2;

		double scale = 1.0 / Math.max(1.0, getMaxDistance() );
		//System.err.println("Centro: ("+centerX+", "+centerY+", "+centerZ+")\nEscala: "+scale);
	
		Matrix4d mt = new Matrix4d();
		mt.set(scale, new Vector3d(-centerX*scale,-centerY*scale,-centerZ*scale));

		return mt;
	}
	
	/**
	 * @param mt  
	 */
	private Geometria generarGeometria(Matrix4d mt){
		throw new UnsupportedOperationException();
		/* TODO Ajustar
	
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
		return gt;
		*/
	}

	private static void purgeVertices(final Queue<Primitive> primitives, final List<Point3f> coordList){
		int[] newIndices = new int[coordList.size()];
		ArrayList<Point3f> newCoordList = new ArrayList<Point3f>(coordList.size());
		int i = 0, j = 0;
		for(Point3f p: coordList){
			int index = newCoordList.indexOf(p);
			if(index < 0){
				newCoordList.add(p);
				newIndices[i]= j;
				j++;
			}else{
				newIndices[i]= index;
			}
			i++;
		}
		if( coordList.size() != newCoordList.size() ){
			System.out.println("Vertices: " + coordList.size() + " --> " +newCoordList.size() );
			for( Primitive p: primitives ){
				if(p instanceof Triangle){
					Triangle t = (Triangle)p;
					t.indexP1 = newIndices[t.indexP1];
					t.indexP2 = newIndices[t.indexP2];
					t.indexP3 = newIndices[t.indexP3];
				}else{
					Quad q = (Quad)p;
					q.indexP1 = newIndices[q.indexP1];
					q.indexP2 = newIndices[q.indexP2];
					q.indexP3 = newIndices[q.indexP3];
					q.indexP4 = newIndices[q.indexP4];
				}
			}
			coordList.clear();
			coordList.addAll(newCoordList);
		}else{
			System.out.println("Vertices: " + coordList.size());
		}
		LinkedList<Primitive> newPrimitives = new LinkedList<Primitive>();
		while( !primitives.isEmpty() ){
			Primitive p = primitives.poll();
			if(p instanceof Triangle){
				Triangle t = (Triangle)p;
				if( t.indexP1 == t.indexP2 ||  t.indexP2 == t.indexP3 || t.indexP3 == t.indexP1 )
					 continue;
				newPrimitives.add(p);
			}else{
				Quad q = (Quad)p;
				
				if( q.indexP1 == q.indexP3 ||  q.indexP2 == q.indexP4 )
					continue;
				
				if( q.indexP1 ==  q.indexP2 || q.indexP1 == q.indexP4 ){
					newPrimitives.add(
							new Triangle(
									q.indexP2, q.indexP3, q.indexP4,
									q.indexN2, q.indexN3, q.indexN4,
									q.indexT2, q.indexT3, q.indexT4
							)
					);
				}else if( q.indexP3 ==  q.indexP2 || q.indexP3 ==  q.indexP4 ){
					newPrimitives.add(
							new Triangle(
									q.indexP1, q.indexP2, q.indexP4,
									q.indexN1, q.indexN2, q.indexN4,
									q.indexT1, q.indexT2, q.indexT4
							)
					);
				}else
					newPrimitives.add(q);
			}				
		}
		primitives.addAll(newPrimitives);
	}
	
	private static final Vector3f _d1 = new Vector3f();
	private static final Vector3f _d2 = new Vector3f();
	private static final Vector3f _normal = new Vector3f();
	
	
	/**
	 * @return ( p1 - p2) x ( p3 - p1 )
	 */
	private static Vector3f calculateNormal( Point3f p1, Point3f p2, Point3f p3 ){
		_d1.set(p1);
		_d1.sub(p2);
		
		_d2.set(p3);
		_d2.sub(p1);
		
		_normal.cross( _d1, _d2 );
		
		return _normal;
	}
	
	
	@SuppressWarnings("unchecked")
	private static void regenerateNormals(final Queue<Primitive> primitives, final List<Point3f> coordList, final List<Vector3f> normList){
		
		purgeVertices(primitives, coordList);
		
		Map<Integer,Integer>[] newIndices = new Map[coordList.size()];
		for(int i = 0; i < newIndices.length; i++)
			newIndices[i] = new HashMap<Integer,Integer>();
		
		for( Primitive p: primitives ){
			if(p instanceof Triangle){
				Triangle t = (Triangle)p;
				newIndices[t.indexP1].put(t.indexN1, t.indexN1);
				newIndices[t.indexP2].put(t.indexN2, t.indexN2);
				newIndices[t.indexP3].put(t.indexN3, t.indexN3);
			}else{
				Quad q = (Quad)p;
				newIndices[q.indexP1].put(q.indexN1, q.indexN1);
				newIndices[q.indexP2].put(q.indexN2, q.indexN2);
				newIndices[q.indexP3].put(q.indexN3, q.indexN3);
				newIndices[q.indexP4].put(q.indexN4, q.indexN4);
			}
		}
		
		ArrayList<Vector3f> newNormList = new ArrayList<Vector3f>(normList.size());
		for( Map<Integer,Integer> m: newIndices ){
			int indexI = newNormList.size();
			for( int oldIndex:  m.keySet() ){
				Vector3f v = normList.get(oldIndex);
				int index = newNormList.lastIndexOf(v);
				if( index < 0 ){
					m.put(oldIndex, newNormList.size());
					newNormList.add(v);
				}else if(index < indexI){
					m.put(oldIndex, newNormList.size());
					newNormList.add(v);
				}else{
					m.put( oldIndex, index );
				}
			}
		}
		
		System.out.println("Normals: " + normList.size() + " --> " +newNormList.size() );
		for( Primitive p: primitives ){
			if(p instanceof Triangle){
				Triangle t = (Triangle)p;
				t.indexN1 = newIndices[t.indexP1].get(t.indexN1);
				t.indexN2 = newIndices[t.indexP2].get(t.indexN2);
				t.indexN3 = newIndices[t.indexP3].get(t.indexN3);
			}else{
				Quad q = (Quad)p;
				q.indexN1 = newIndices[q.indexP1].get(q.indexN1);
				q.indexN2 = newIndices[q.indexP2].get(q.indexN2);
				q.indexN3 = newIndices[q.indexP3].get(q.indexN3);
				q.indexN4 = newIndices[q.indexP4].get(q.indexN4);
			}
		}
		normList.clear();
		for(int i = 0; i < newNormList.size(); i++)
			normList.add(new Vector3f());
		
		final Point3f p1 = new Point3f();
		final Point3f p2 = new Point3f();
		final Point3f p3 = new Point3f();
		final Point3f p4 = new Point3f();
		
		final Vector3f normal = new Vector3f();
		
		for( Primitive p: primitives ){
			if(p instanceof Triangle){
				
				Triangle t = (Triangle)p;
				
				p1.set(coordList.get(t.indexP1));
				p2.set(coordList.get(t.indexP2));
				p3.set(coordList.get(t.indexP3));
				
				normal.set( calculateNormal( p1, p3, p2 ) );
				normal.add( calculateNormal( p2, p1, p3 ) );
				normal.add( calculateNormal( p3, p2, p1 ) );
				normal.scale(1.0f/3);
				
				normList.get(t.indexN1).add( normal );
				normList.get(t.indexN2).add( normal );
				normList.get(t.indexN3).add( normal );
			}else{
				Quad q = (Quad)p;
				
				p1.set(coordList.get(q.indexP1));
				p2.set(coordList.get(q.indexP2));
				p3.set(coordList.get(q.indexP3));
				p4.set(coordList.get(q.indexP4));
				
//				if( p1.equals(p3) || p2.equals(p4) )
//					continue;
//				
//				if( p1.equals(p2) ){
//					normal = calculateNormal(p1,p4,p3);					
//					normList.get(q.indexN1).add( normal );
//					normList.get(q.indexN2).add( normal );				
//					normList.get(q.indexN3).add( calculateNormal(p3,p2,p4) );
//					normList.get(q.indexN4).add( calculateNormal(p4,p3,p1) );
//				}else if( p1.equals(p4) ){
//					normal = calculateNormal(p1,p3,p2);
//					normList.get(q.indexN1).add( normal );
//					normList.get(q.indexN2).add( calculateNormal(p2,p1,p3) );
//					normList.get(q.indexN3).add( calculateNormal(p3,p2,p4) );
//					normList.get(q.indexN4).add( normal );
//				}else if( p2.equals(p3) ){
//					normal = calculateNormal(p2,p1,p4);
//					normList.get(q.indexN1).add( calculateNormal(p1,p4,p2) );
//					normList.get(q.indexN2).add( normal );
//					normList.get(q.indexN3).add( normal );
//					normList.get(q.indexN4).add( calculateNormal(p4,p3,p1) );
//				}else if( p3.equals(p4) ){
//					normal = calculateNormal(p3,p2,p1);
//					normList.get(q.indexN1).add( calculateNormal(p1,p4,p2) );
//					normList.get(q.indexN2).add( calculateNormal(p2,p1,p3) );
//					normList.get(q.indexN3).add( normal );
//					normList.get(q.indexN4).add( normal );
//				}else{
				normal.set( calculateNormal(p1,p4,p2) );
				normal.add( calculateNormal(p2,p1,p3) );
				normal.add( calculateNormal(p3,p2,p4) );
				normal.add( calculateNormal(p4,p3,p1) );
				normal.scale(1.0f/4);
					
				normList.get(q.indexN1).add( normal );
				normList.get(q.indexN2).add( normal );
				normList.get(q.indexN3).add( normal );
				normList.get(q.indexN4).add( normal );
//				}
			}
		}
		for(Vector3f n: normList)
			n.normalize();
		
		for( Primitive p: primitives )
			if(p instanceof Quad){
				
				Quad q = (Quad)p;

				float l1 = normList.get(q.indexN1).length();
				float l2 = normList.get(q.indexN2).length();
				float l3 = normList.get(q.indexN3).length();
				float l4 = normList.get(q.indexN4).length();
				
				if( 
						valorNoValido(l1) ||
						valorNoValido(l2) ||
						valorNoValido(l3) ||
						valorNoValido(l4)
						//( d  < 0 && d  > 0 ) || ( d  == Float.POSITIVE_INFINITY ) || ( d  == Float.NEGATIVE_INFINITY ) 
				){
					System.out.println(l1 + " " + l2 + " " + l3 + " " + l4);
					
////					System.out.println(coordList.get(q.indexP1));
////					System.out.println(coordList.get(q.indexP2));
////					System.out.println(coordList.get(q.indexP3));
////					System.out.println(coordList.get(q.indexP4));
//					
//					d1.set(coordList.get(q.indexP2));
//					d1.sub(coordList.get(q.indexP1));
//					
//					d2.set(coordList.get(q.indexP3));
//					d2.sub(coordList.get(q.indexP2));
//					
//					d3.set(coordList.get(q.indexP4));
//					d3.sub(coordList.get(q.indexP3));
//					
//					d4.set(coordList.get(q.indexP1));
//					d4.sub(coordList.get(q.indexP4));
//					
//					d5.set(coordList.get(q.indexP3));
//					d5.sub(coordList.get(q.indexP1));
//					
//					normal.cross(d4, d1);
//					normal.normalize();
////					System.out.println( d4 + " " + d1 + " --> " + normal );
//					normList.get(q.indexN1).set(normal);
//					
//					normal.cross(d1, d2);
//					normal.normalize();
////					System.out.println( d1 + " " + d2 + " --> " + normal );
//					normList.get(q.indexN2).set(normal);
//					
//					normal.cross(d2, d3);
//					normal.normalize();
////					System.out.println( d2 + " " + d3 + " --> " + normal );
//					normList.get(q.indexN3).set(normal);
//					
//					normal.cross(d3, d4);
//					normal.normalize();
////					System.out.println( d3 + " " + d4 + " --> " + normal );
////					normList.get(q.indexN4).set(normal);
					
//					int aux = q.indexP2;
//					q.indexP2 = q.indexP4;
//					q.indexP4 = aux;
//					
//					aux = q.indexN2;
//					q.indexN2 = q.indexN4;
//					q.indexN4 = aux;
//					
//					aux = q.indexT2;
//					q.indexT2 = q.indexT4;
//					q.indexT4 = aux;
					
				}
			}
	}
	
	private static boolean valorNoValido(float f){
		return
			Float.compare(f, 0.0f) == 0 ||
			Float.compare(f, -0.0f) == 0 ||
			Float.compare(f, Float.NaN) == 0 ||
			Float.compare(f, Float.POSITIVE_INFINITY) == 0 ||
			Float.compare(f, Float.NEGATIVE_INFINITY) == 0;
	}
	
	private static void generateTangents(final Queue<Primitive> primitives, final List<Point3f> coordList, Vector3f[][] tangents){
		
		for ( int i= 0; i < tangents[0].length; i++ ){
			tangents[0][i] = new Vector3f();
			tangents[1][i] = new Vector3f();
		}
		
		for (Primitive p: primitives)
			p.generateTangents( coordList, tangents);
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
		//t4f.set( uVec.x, uVec.y, uVec.z, dotcross( normal, uVec, vVec ) < 0.0f ? -1.0f : 1.0f );
	}
	
	private OglList almacenarGeometria(GL gl, Matrix4d mt){
		
		boolean textCoord = texList.size() > 0;
		boolean normal = normList.size() > 0;
		
		
		//TODO mirar
		clearDist = (float)(getMaxDistance() * 0.0001);
		
		regenerateNormals( primitives, coordList, normList );
		
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
		
		int lid = gl.glGenLists(1);
		gl.glNewList(lid, GL.GL_COMPILE);
		
		if( (flags & ObjLoader.GENERATE_TANGENTS) != 0){
			
			Vector3f[][] tb  = new Vector3f[2][normList.size()];
			
			generateTangents( primitives, coordList, tb );
			for (int i = 0; i < normList.size(); i++) {
				orthogonalizeGramSchmidt(normList.get(i),tb[0][i]);
				orthogonalizeGramSchmidt(normList.get(i),tb[1][i]);
			}
			
			if( (flags & ObjLoader.TRIANGULATE) != 0){
				if((flags & ObjLoader.GENERATE_NTB) != 0){
					gl.glBegin(	GL.GL_LINES );
					for(Primitive p: primitives)
						for(Triangle t: p.toTriangles())
							t.generateNTB( gl, coordList, normList, tb, texList, 0.25f );
				}else{
					gl.glBegin(	GL.GL_TRIANGLES );
					for(Primitive p: primitives)
						for(Triangle t: p.toTriangles())
							t.generate( gl, coordList, normList, tb, texList );
				}
			}else{
				if((flags & ObjLoader.GENERATE_NTB) != 0){
					gl.glBegin(	GL.GL_LINES );
					for(Primitive p: primitives)
						p.generateNTB( gl, coordList, normList, tb, texList, 0.25f );
				}else{
					Primitive pa = primitives.element();
					gl.glBegin(	pa instanceof Quad ? GL.GL_QUADS: GL.GL_TRIANGLES );
					for(Primitive p: primitives){
						if(p.getClass() != pa.getClass()){
							gl.glEnd();
							gl.glBegin(	p instanceof Quad ? GL.GL_QUADS: GL.GL_TRIANGLES );
						}
						p.generate( gl, coordList, normList, tb, texList );
						pa = p;
					}
				}
			}
		}else{
			if( (flags & ObjLoader.TRIANGULATE) != 0){
				gl.glBegin(	GL.GL_TRIANGLES );
				for(Primitive p: primitives)
					for(Triangle t: p.toTriangles())
						t.generate(gl, coordList, normList, texList);
			}else{
				Primitive pa = primitives.element();
				gl.glBegin(	pa instanceof Quad ? GL.GL_QUADS: GL.GL_TRIANGLES );
				for(Primitive p: primitives){
					if(p.getClass() != pa.getClass()){
						gl.glEnd();
						gl.glBegin(	p instanceof Quad ? GL.GL_QUADS: GL.GL_TRIANGLES );
					}
					p.generate( gl, coordList, normList, texList );
					pa = p;
				}
			}
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
		if( (flags & ObjLoader.GENERATE_NTB) == 0 )
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
