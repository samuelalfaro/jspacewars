package org.sam.jogl;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public interface Generator {

	final class VerticesGenerator implements Generator{
		
		private VerticesGenerator(){
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl, vert1, vert2, vert3 );
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3
		){
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl, vert1, vert2, vert3, vert4 );
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4
		){
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
		}
	}
	final Generator Vertices = new VerticesGenerator();

	final class VerticesNormalsGenerator implements Generator{
		
		private VerticesNormalsGenerator(){
		}
	
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl,
					vert1, vert2, vert3,
					norm1, norm2, norm3
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3
		){
			gl.glNormal3f( norm1.x, norm1.y, norm1.z );
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f( norm2.x, norm2.y, norm2.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f( norm3.x, norm3.y, norm3.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl,
					vert1, vert2, vert3, vert4,
					norm1, norm2, norm3, norm4
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4
		){
			gl.glNormal3f( norm1.x, norm1.y, norm1.z );
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f( norm2.x, norm2.y, norm2.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f( norm3.x, norm3.y, norm3.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f( norm4.x, norm4.y, norm4.z );
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
		}
	}
	final Generator VerticesNormals = new VerticesNormalsGenerator();

	final class VerticesWireFrameGenerator implements Generator{
		
		private VerticesWireFrameGenerator(){
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl,
					vert1, vert2, vert2, vert3,
					norm1, norm2, norm2, norm3
			);
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl,
					vert1, vert2, vert3, vert4,
					norm1, norm2, norm3, norm4
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4
		){
			gl.glMultiTexCoord4f( 0, vert1.x, vert1.y, vert1.z, 1.0f );
			gl.glMultiTexCoord4f( 1, vert2.x, vert2.y, vert2.z, 1.0f );
			gl.glMultiTexCoord4f( 2, vert3.x, vert3.y, vert3.z, 1.0f );
			gl.glMultiTexCoord4f( 3, vert4.x, vert4.y, vert4.z, 1.0f );
			
			gl.glNormal3f( norm1.x, norm1.y, norm1.z );
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f( norm2.x, norm2.y, norm2.z );
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f( norm3.x, norm3.y, norm3.z );
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f( norm4.x, norm4.y, norm4.z );
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
		}
	}
	final Generator VerticesWireFrame = new VerticesWireFrameGenerator();

	final class VerticesTexCoordsGenerator implements Generator{
		
		private VerticesTexCoordsGenerator(){
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl,
					vert1, vert2, vert3,
					text1, text2, text3
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3
		){	
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl,
					vert1, vert2, vert3, vert4,
					text1, text2, text3, text4
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4
		){	
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
			
			gl.glTexCoord2f( text4.x, text4.y );
			gl.glVertex3f(   vert4.x, vert4.y, vert4.z );
		}
	}
	final Generator VerticesTexCoords = new VerticesTexCoordsGenerator();

	final class VerticesNormalsTexCoordsGenerator implements Generator{
		
		private VerticesNormalsTexCoordsGenerator(){
		}	
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			generate( gl,
					vert1, vert2, vert3,
					text1, text2, text3,
					norm1, norm2, norm3
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3
		){
			gl.glNormal3f(   norm1.x, norm1.y, norm1.z );
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(   norm2.x, norm2.y, norm2.z );
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(   norm3.x, norm3.y, norm3.z );
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			generate( gl,
					vert1, vert2, vert3, vert4,
					text1, text2, text3, text4,
					norm1, norm2, norm3, norm4
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4
		){
			gl.glNormal3f(   norm1.x, norm1.y, norm1.z );
			gl.glTexCoord2f( text1.x, text1.y );
			gl.glVertex3f(   vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(   norm2.x, norm2.y, norm2.z );
			gl.glTexCoord2f( text2.x, text2.y );
			gl.glVertex3f(   vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(   norm3.x, norm3.y, norm3.z );
			gl.glTexCoord2f( text3.x, text3.y );
			gl.glVertex3f(   vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f(   norm4.x, norm4.y, norm4.z );
			gl.glTexCoord2f( text4.x, text4.y );
			gl.glVertex3f(   vert4.x, vert4.y, vert4.z );
		}
	}
	final Generator VerticesNormalsTexCoords = new VerticesNormalsTexCoordsGenerator();

	final class VerticesTangentsGenerator implements Generator{
		
		private VerticesTangentsGenerator(){
		}	
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			gl.glNormal3f(          norm1.x, norm1.y, norm1.z );
			gl.glVertexAttrib4f( 1, tang1.x, tang1.y, tang1.z, tang1.w );
			gl.glTexCoord2f(        text1.x, text1.y );
			gl.glVertex3f(          vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(          norm2.x, norm2.y, norm2.z );
			gl.glVertexAttrib4f( 1, tang2.x, tang2.y, tang2.z, tang2.w );
			gl.glTexCoord2f(        text2.x, text2.y );
			gl.glVertex3f(          vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(          norm3.x, norm3.y, norm3.z );
			gl.glVertexAttrib4f( 1, tang3.x, tang3.y, tang3.z, tang3.w );
			gl.glTexCoord2f(        text3.x, text3.y );
			gl.glVertex3f(          vert3.x, vert3.y, vert3.z );
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			gl.glNormal3f(          norm1.x, norm1.y, norm1.z );
			gl.glVertexAttrib4f( 1, tang1.x, tang1.y, tang1.z, tang1.w );
			gl.glTexCoord2f(        text1.x, text1.y );
			gl.glVertex3f(          vert1.x, vert1.y, vert1.z );
			
			gl.glNormal3f(          norm2.x, norm2.y, norm2.z );
			gl.glVertexAttrib4f( 1, tang2.x, tang2.y, tang2.z, tang2.w );
			gl.glTexCoord2f(        text2.x, text2.y );
			gl.glVertex3f(          vert2.x, vert2.y, vert2.z );
			
			gl.glNormal3f(          norm3.x, norm3.y, norm3.z );
			gl.glVertexAttrib4f( 1, tang3.x, tang3.y, tang3.z, tang3.w );
			gl.glTexCoord2f(        text3.x, text3.y );
			gl.glVertex3f(          vert3.x, vert3.y, vert3.z );
			
			gl.glNormal3f(          norm4.x, norm4.y, norm4.z );
			gl.glVertexAttrib4f( 1, tang4.x, tang4.y, tang4.z, tang4.w );
			gl.glTexCoord2f(        text4.x, text4.y );
			gl.glVertex3f(          vert4.x, vert4.y, vert4.z );
		}
	}
	final Generator VerticesTangents = new VerticesTangentsGenerator();

	final class NTBGenerator implements Generator{
	
		float scale;
		
		public NTBGenerator(){
			this(1.0f);
		}
		
		public NTBGenerator(float scale) {
			this.scale = scale;
		}

		public float getScale() {
			return scale;
		}

		public void setScale(float scale) {
			this.scale = scale;
		}

		final Vector3f bita1 = new Vector3f();
		final Vector3f bita2 = new Vector3f();
		final Vector3f bita3 = new Vector3f();
		final Vector3f bita4 = new Vector3f();
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
		){
			bita1.set(
		        	(norm1.y*tang1.z - norm1.z*tang1.y)*Math.signum(tang1.w),
		        	(tang1.x*norm1.z - tang1.z*norm1.x)*Math.signum(tang1.w),
		        	(norm1.x*tang1.y - norm1.y*tang1.x)*Math.signum(tang1.w)
				);
			bita2.set(
		        	(norm2.y*tang2.z - norm2.z*tang2.y)*Math.signum(tang2.w),
		        	(tang2.x*norm2.z - tang2.z*norm2.x)*Math.signum(tang2.w),
		        	(norm2.x*tang2.y - norm2.y*tang2.x)*Math.signum(tang2.w)
				);
			bita3.set(
		        	(norm3.y*tang3.z - norm3.z*tang3.y)*Math.signum(tang3.w),
		        	(tang3.x*norm3.z - tang3.z*norm3.x)*Math.signum(tang3.w),
		        	(norm3.x*tang3.y - norm3.y*tang3.x)*Math.signum(tang3.w)
				);
			
			generate( gl,
					vert1, vert2, vert3,
					norm1, norm2, norm3,
					tang1, tang2, tang3,
					bita1, bita2, bita3
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3,
				Vector3f   bita1, Vector3f   bita2, Vector3f   bita3
		){	
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
	
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + bita1.x * scale, vert1.y + bita1.y * scale, vert1.z + bita1.z * scale);
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + bita2.x * scale, vert2.y + bita2.y * scale, vert2.z + bita2.z * scale);
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + bita3.x * scale, vert3.y + bita3.y * scale, vert3.z + bita3.z * scale);
		}
		
		@Override
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
		){
			bita1.set(
		        	(norm1.y*tang1.z - norm1.z*tang1.y)*Math.signum(tang1.w),
		        	(tang1.x*norm1.z - tang1.z*norm1.x)*Math.signum(tang1.w),
		        	(norm1.x*tang1.y - norm1.y*tang1.x)*Math.signum(tang1.w)
				);
			bita2.set(
		        	(norm2.y*tang2.z - norm2.z*tang2.y)*Math.signum(tang2.w),
		        	(tang2.x*norm2.z - tang2.z*norm2.x)*Math.signum(tang2.w),
		        	(norm2.x*tang2.y - norm2.y*tang2.x)*Math.signum(tang2.w)
				);
			bita3.set(
		        	(norm3.y*tang3.z - norm3.z*tang3.y)*Math.signum(tang3.w),
		        	(tang3.x*norm3.z - tang3.z*norm3.x)*Math.signum(tang3.w),
		        	(norm3.x*tang3.y - norm3.y*tang3.x)*Math.signum(tang3.w)
				);
			bita4.set(
		        	(norm4.y*tang4.z - norm4.z*tang4.y)*Math.signum(tang4.w),
		        	(tang4.x*norm4.z - tang4.z*norm4.x)*Math.signum(tang4.w),
		        	(norm4.x*tang4.y - norm4.y*tang4.x)*Math.signum(tang4.w)
				);
			
			generate( gl,
					vert1, vert2, vert3, vert4,
					norm1, norm2, norm3, norm4,
					tang1, tang2, tang3, tang4,
					bita1, bita2, bita3, bita4
			);
		}
		
		public void generate(GL gl,
				Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
				Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
				Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4,
				Vector3f   bita1, Vector3f   bita2, Vector3f   bita3, Vector3f   bita4
		){	
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
	
			gl.glColor3f(0.0f, 0.5f, 1.0f);
			
			gl.glVertex3f( vert1.x, vert1.y, vert1.z );
			gl.glVertex3f( vert1.x + bita1.x * scale, vert1.y + bita1.y * scale, vert1.z + bita1.z * scale);
	
			gl.glVertex3f( vert2.x, vert2.y, vert2.z );
			gl.glVertex3f( vert2.x + bita2.x * scale, vert2.y + bita2.y * scale, vert2.z + bita2.z * scale);
	
			gl.glVertex3f( vert3.x, vert3.y, vert3.z );
			gl.glVertex3f( vert3.x + bita3.x * scale, vert3.y + bita3.y * scale, vert3.z + bita3.z * scale);
	
			gl.glVertex3f( vert4.x, vert4.y, vert4.z );
			gl.glVertex3f( vert4.x + bita4.x * scale, vert4.y + bita4.y * scale, vert4.z + bita4.z * scale );
		}
	}
	
	public void generate(GL gl,
			Point3f    vert1, Point3f    vert2, Point3f    vert3,
			TexCoord2f text1, TexCoord2f text2, TexCoord2f text3,
			Vector3f   norm1, Vector3f   norm2, Vector3f   norm3,
			Vector4f   tang1, Vector4f   tang2, Vector4f   tang3
	);
	
	public void generate(GL gl,
			Point3f    vert1, Point3f    vert2, Point3f    vert3, Point3f    vert4,
			TexCoord2f text1, TexCoord2f text2, TexCoord2f text3, TexCoord2f text4,
			Vector3f   norm1, Vector3f   norm2, Vector3f   norm3, Vector3f   norm4,
			Vector4f   tang1, Vector4f   tang2, Vector4f   tang3, Vector4f   tang4
	);
}
