/* 
 * PruebaTexto.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package pruebas.jogl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.swing.JFrame;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.Textura;
import org.sam.util.Imagen;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.Animator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class PruebaTexto{
	
	static float readAttribute( HierarchicalStreamReader reader, String name, float defaultValue ){
		String att = reader.getAttribute( name );
		if( att != null && att.length() > 0 )
			try{
				return (float)Double.parseDouble( att );
			}catch( NumberFormatException e ){
			}
		return defaultValue;
	}
	
	static int readAttribute( HierarchicalStreamReader reader, String name, int defaultValue ){
		String att = reader.getAttribute( name );
		if( att != null && att.length() > 0 )
			try{
				return Integer.parseInt( att );
			}catch( NumberFormatException e ){
			}
		return defaultValue;
	}
	
	private static class CharacterPixmapData{
		
		static class Comparator implements java.util.Comparator<CharacterPixmapData>{
		    public int compare(CharacterPixmapData o1, CharacterPixmapData o2){
		    	return o1.c - o2.c;
		    }
		}
		
		final Character c;
		final float x;
		final float y;
		final float width;
		final float height;
		final float charWidth;
		final float charHeight;
		
		CharacterPixmapData( Character c, float x, float y, float width, float height, float charWidth, float charHeight ){
			this.c = c;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.charWidth = charWidth;
			this.charHeight = charHeight;
		}
		
		void buidCharacterPixmap( GL2 gl, int listId, int textureWidth, int textureHeight, float gap, float scaleX, float scaleY ){
			float u1 = x / textureWidth;
			float v1 = y / textureHeight;
			float u2 = ( x + width ) / textureWidth;
			float v2 = ( y + height ) / textureHeight;
			
			float x1 = ( charWidth - width )/2 * scaleX;
			float x2 = x1 + width * scaleX;
			
			float y1 = ( charHeight - height )/2 * scaleY;
			float y2 = y1 + height * scaleY;

			gl.glNewList( listId, GL2.GL_COMPILE );
				gl.glBegin( GL2.GL_QUADS );
					gl.glTexCoord2f( u1, v1 );
					gl.glVertex2f( x1, y1 );
					gl.glTexCoord2f( u2, v1 );
					gl.glVertex2f( x2, y1 );
					gl.glTexCoord2f( u2, v2 );
					gl.glVertex2f( x2, y2 );
					gl.glTexCoord2f( u1, v2 );
					gl.glVertex2f( x1, y2 );
				gl.glEnd();
				gl.glTranslated( ( charWidth + gap ) * scaleX, 0, 0 );
			gl.glEndList();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals( Object otro ){
			return
				( otro != null ) &&
				( otro instanceof CharacterPixmapData) &&
				( (CharacterPixmapData)otro ).c.equals( this.c );
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode(){
			return c.hashCode();
		}
	}
	
	private static class CharacterPixmapConverter implements Converter{

		CharacterPixmapConverter(){
		}

		@SuppressWarnings( "rawtypes" )
		public boolean canConvert( Class clazz ){
			return CharacterPixmapData.class == clazz;
		}

		public void marshal( Object value, HierarchicalStreamWriter writer, MarshallingContext context ){
		}
		
		public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ){
			char c = 0;
			float x = readAttribute( reader, "x", 0.0f );
			float y = readAttribute( reader, "y", 0.0f );
			float width  = readAttribute( reader, "width", 0.0f );
			float height = readAttribute( reader, "height", 0.0f );
			float charWidth  = readAttribute( reader, "charWidth", width );
			float charHeight = readAttribute( reader, "charHeight", height );
			if( reader.hasMoreChildren() ){
				reader.moveDown();
				c = reader.getValue().charAt( 0 );
				reader.moveUp();
			}
			return new CharacterPixmapData( c, x, y, width, height, charWidth, charHeight );
		}
	}
	
	private static class CharactersPixmapsData{
		final float scaleX;
		final float scaleY;
		final int textureWidth;
		final int textureHeight;
		final SortedSet<CharacterPixmapData> charactersData;
		
		CharactersPixmapsData( float scaleX, float scaleY, int textureWidth, int textureHeight ){
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
			this.charactersData = new TreeSet<CharacterPixmapData>( new CharacterPixmapData.Comparator() );
		}
	}
	
	private static class CharactersPixmapsConverter implements Converter {
		
		CharactersPixmapsConverter(){
		}
		
		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return CharactersPixmapsData.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			CharactersPixmapsData fontData = new CharactersPixmapsData(
				readAttribute( reader, "scaleX", 1.0f ),
				readAttribute( reader, "scaleY", 1.0f ),
				readAttribute( reader, "textureWidth", 256 ),
				readAttribute( reader, "textureHeight", 256 )
			);
			CharacterPixmapData charData;
			while (reader.hasMoreChildren()){
				reader.moveDown();
					charData = (CharacterPixmapData)context.convertAnother( fontData, CharacterPixmapData.class );
					fontData.charactersData.add( charData );
				reader.moveUp();
			}
			return fontData;
		}
	}
	
	private static XStream xStream = null;
	
	static final XStream getXStream(){
		if( xStream == null ){
			xStream = new XStream(new DomDriver());
			xStream.alias( "CharacterPixmap", CharacterPixmapData.class );
			xStream.registerConverter( new CharacterPixmapConverter() );
			xStream.alias( "Font", CharactersPixmapsData.class );
			xStream.registerConverter( new CharactersPixmapsConverter() );
		}
		return xStream;
	}
	
	private static class CharactersPixmaps{
		
		final SortedMap<Character, Integer> characters; 
		final int unknown;

		CharactersPixmaps( GL2 gl, CharactersPixmapsData data ){
			this.characters = new TreeMap<Character, Integer>();
			int spaceId = 0;
			
			int base = gl.glGenLists( data.charactersData.size() );
			while( !data.charactersData.isEmpty() ){
				CharacterPixmapData cData = data.charactersData.first();
				data.charactersData.remove( cData );
				cData.buidCharacterPixmap( gl, base, data.textureWidth, data.textureHeight, 10.0f, 1.0f/data.scaleX, 1.0f/data.scaleY );
				characters.put( cData.c, base );
				if( cData.c == ' ' )
					spaceId = base;
				base++;
			}
			unknown = spaceId;
		}
	}
	
	private static class Font{

		final CharactersPixmaps pixmaps;
		final Apariencia apariencia;

		Font( CharactersPixmaps pixmaps, Apariencia apariencia ){
			this.pixmaps = pixmaps;
			this.apariencia = apariencia;
		}
	}
	
	@SuppressWarnings( "static-access" )
	private static class Renderer implements GLEventListener{
		
		/*
		private final static String fontDef = "resources/arbeka.xml";
		private final static String font1Texture = "resources/arbeka.png";
		private final static String font2Texture = "resources/arbeka-blur.png";
		/*/
		private final static String fontDef = "resources/saved.xml";
		private final static String font1Texture = "resources/saved.png";
		private final static String font2Texture = "resources/saved-blur.png";
		//*/
		
		private IntBuffer stringBuffer;

		private CharactersPixmapsData charactersPixmapsData;
		
		private Font font1;
		private Font font2;
		
		public Renderer(){
			stringBuffer = Buffers.newDirectIntBuffer( 256 );
			try{
				charactersPixmapsData = (CharactersPixmapsData)getXStream().fromXML( new FileInputStream( fontDef ) );
			}catch( FileNotFoundException e ){
				e.printStackTrace();
			}
		}
		
		private void glPrint( GL2 gl, float x, float y, String string, Font font ){
			glPrint( gl, x, y, 1.0f, 1.0f, string, 1.0f, 1.0f,  1.0f, 1.0f, font );
		}
		
		private void glPrint( GL2 gl, float x, float y, String string, float r, float g, float b, float a, Font font ){
			glPrint( gl, x, y, 1.0f, 1.0f, string, r, g, b, a, font );
		}
		
		private void glPrint( GL2 gl, float x, float y, float scaleX, float scaleY, String string, Font font ){
			glPrint( gl, x, y, scaleX, scaleY, string, 1.0f, 1.0f,  1.0f, 1.0f, font );
		}
		
		private void glPrint( GL2 gl, float x, float y, float scaleX, float scaleY, String string, float r, float g, float b, float a, Font font ){

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glScalef( scaleX, scaleY, 1.0f );
			gl.glTranslatef( x / scaleX, y / scaleY, 0 );

			if( stringBuffer.capacity() < string.length() ){
				stringBuffer = Buffers.newDirectIntBuffer( string.length() );
			}

			stringBuffer.clear();
			for(int i= 0; i< string.length(); i++ ){
				Integer listId = font.pixmaps.characters.get( string.charAt( i ) );
				stringBuffer.put( listId != null ? listId.intValue() : font.pixmaps.unknown );
			}
			stringBuffer.flip();

			font.apariencia.usar( gl );
			gl.glColor4f( r, g, b, a );
			gl.glCallLists( string.length(), GL2.GL_INT, stringBuffer );

			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glPopMatrix();
		}

		public void init( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();
			
			CharactersPixmaps charactersPixmaps = new CharactersPixmaps( gl, charactersPixmapsData );
		
			BufferedImage img = Imagen.cargarToBufferedImage( font1Texture );
			
			Apariencia apFont = new Apariencia();
			
			//*
			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_EDGE );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_EDGE );
			
			apFont.setAtributosTextura( new AtributosTextura() );
			
			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			/*/
			apFont.setTextura( new Textura( gl, Textura.Format.RGBA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_EDGE );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_EDGE );
			
			apFont.setAtributosTextura( new AtributosTextura() );
			
			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.ADD_SIGNED );
			apFont.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineRgbSource1(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			//*/
			
			apFont.setAtributosTransparencia( 
					new AtributosTransparencia( 
							AtributosTransparencia.Equation.ADD,
							AtributosTransparencia.SrcFunc.SRC_ALPHA,
							AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA
					) 
			);
			font1 = new Font( charactersPixmaps, apFont );
			
			img = Imagen.cargarToBufferedImage( font2Texture );
			apFont = new Apariencia();
			
			apFont.setTextura( new Textura( gl, Textura.Format.ALPHA, img, false ) );
			apFont.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_EDGE );
			apFont.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_EDGE );
			
			apFont.setAtributosTextura( new AtributosTextura() );
			
			apFont.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			apFont.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			apFont.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.REPLACE );
			apFont.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			
			apFont.setAtributosTransparencia( 
					new AtributosTransparencia( 
							AtributosTransparencia.Equation.ADD,
							AtributosTransparencia.SrcFunc.SRC_ALPHA,
							AtributosTransparencia.DstFunc.ONE
					) 
			);
			font2 = new Font( charactersPixmaps, apFont );
			
			gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
			gl.glClearColor( 0.2f, 0.2f, 0.3f, 0.0f );
		}

		public void display( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();

			gl.glClear( GL.GL_COLOR_BUFFER_BIT );
			
			glPrint( gl, 5, 0, "Prueba Texto áéíóú ÁÉÍÓÚ cigüeña", 0.5f, 0.5f, 0.5f, 1.0f, font1 );
			glPrint( gl, 5, 0, "Prueba Texto áéíóú ÁÉÍÓÚ cigüeña", 0.2f, 0.2f, 0.2f, 1.0f, font2 );
			
			glPrint( gl, 5, 51, "Bla: bla@bla ¿bla? ¡bla!", 0.5f, 0.5f, 0.5f, 1.0f, font1 );
			glPrint( gl, 5, 51, "Bla: bla@bla ¿bla? ¡bla!", 0.5f, 0.5f, 0.5f, 1.0f, font2 );
			
			glPrint( gl, 5, 102, "12345,67890.12345;67890", 0.5f, 0.5f, 0.5f, 1.0f, font1 );
			glPrint( gl, 5, 102, "12345,67890.12345;67890", 0.15f, 0.15f, 0.15f, 1.0f, font2 );
			
			glPrint( gl, 5, 300, "ABCDEFGHIJKLMNOPQRSTUVXYZ", 0.5f, 0.25f, 0.5f, 1.0f, font1 );
			glPrint( gl, 5, 300, "ABCDEFGHIJKLMNOPQRSTUVXYZ", 0.5f, 0.5f, 0.25f, 1.0f, font2 );
			
			glPrint( gl, 5, 400, "abcdefghijklmnopqrstuvxyz", 0.0f, 0.0f, 0.5f, 1.0f, font1 );
			glPrint( gl, 5, 400, "abcdefghijklmnopqrstuvxyz", 0.5f, 0.25f, 0.5f, 1.0f, font2 );
		}

		public void reshape( GLAutoDrawable glDrawable, int x, int y, int w, int h ){
			if( h == 0 )
				h = 1;
			GL2 gl = glDrawable.getGL().getGL2();
			
			gl.glViewport( 0, 0, w, h );
			
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho( 0, w, h, 0, -1, 1 );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable glDrawable ){
		}
	}

	public static void main( String[] args ){

		JFrame frame = new JFrame( "Prueba Texto" );
		frame.getContentPane().setLayout( new BorderLayout() );
		frame.getContentPane().setBackground( Color.BLACK );

		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener( new Renderer() );
		frame.getContentPane().add( canvas, BorderLayout.CENTER );

		frame.getContentPane().setPreferredSize( new Dimension( 640, 480 ) );
		frame.pack();
		frame.setLocationRelativeTo( null ); // center
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
		
		Animator animator = new Animator();
		animator.setRunAsFastAsPossible( true );
		animator.add( canvas );
		
		canvas.requestFocusInWindow();
	}
}
