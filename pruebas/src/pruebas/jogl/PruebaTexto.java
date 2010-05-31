package pruebas.jogl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.*;

import org.sam.jogl.Textura;
import org.sam.util.Imagen;

import com.sun.opengl.util.BufferUtil;

// TODO terminar
public class PruebaTexto {

	private static class Renderer implements GLEventListener {
		private int base;  // Base Display List For The Font
		private int[] textures = new int[2];  // Storage For Our Font Texture

		private float cnt1;    // 1st Counter Used To Move Text & For Coloring
		private float cnt2;    // 2nd Counter Used To Move Text & For Coloring

		private GLU glu = new GLU();
		private ByteBuffer stringBuffer = BufferUtil.newByteBuffer(256);

		public Renderer() {
		}

		public void loadGLTextures(GL gl) {

			String tileNames [] = 
			{"demos/data/images/font.png", "demos/data/images/bumps.png"};

			gl.glGenTextures(2, textures, 0);

			for (int i = 0; i < 2; i++) {
				BufferedImage texture =  Imagen.cargarToBufferedImage(tileNames[i]);
				//Create Nearest Filtered Texture
				gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

				gl.glTexImage2D(GL.GL_TEXTURE_2D,
						0,
						3,
						texture.getWidth(),
						texture.getHeight(),
						0,
						GL.GL_RGB,
						GL.GL_UNSIGNED_BYTE,
						Textura.Util.toByteBuffer(texture, Textura.Format.RGB, true));
			}
		}

		private void buildFont(GL gl){
			float cx;      // Holds Our X Character Coord
			float cy;      // Holds Our Y Character Coord

			base = gl.glGenLists(256);  // Creating 256 Display Lists
			gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
			
			for (int loop = 0; loop < 256; loop++){
				cx = (float)( loop % 16 ) / 16;  // X Position Of Current Character
				cy = (float)( loop / 16 ) / 16;  // Y Position Of Current Character

				gl.glNewList(base + loop, GL.GL_COMPILE);
					gl.glBegin(GL.GL_QUADS);      // Use A Quad For Each Character
						gl.glTexCoord2f(cx, 1 - cy - 0.0625f);
						gl.glVertex2i(0, 0);
						gl.glTexCoord2f(cx + 0.0625f, 1 - cy - 0.0625f);
						gl.glVertex2i(16, 0);
						gl.glTexCoord2f(cx + 0.0625f, 1 - cy);
						gl.glVertex2i(16, 16);
						gl.glTexCoord2f(cx, 1 - cy);
						gl.glVertex2i(0, 16);
					gl.glEnd();
					gl.glTranslated(10, 0, 0);      // Move To The Right Of The Character
				gl.glEndList();
			}           
		}

		// Where The Printing Happens
		private void glPrint(GL gl, int x, int y, String string, int set)  
		{
			if (set > 1) {
				set = 1;
			}
			gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]); // Select Our Font Texture
			gl.glDisable(GL.GL_DEPTH_TEST);       // Disables Depth Testing
			gl.glMatrixMode(GL.GL_PROJECTION);     // Select The Projection Matrix
			gl.glPushMatrix();         // Store The Projection Matrix
			gl.glLoadIdentity();         // Reset The Projection Matrix
			gl.glOrtho(0, 640, 0, 480, -1, 1);     // Set Up An Ortho Screen
			gl.glMatrixMode(GL.GL_MODELVIEW);     // Select The Modelview Matrix
			gl.glPushMatrix();         // Store The Modelview Matrix
			gl.glLoadIdentity();         // Reset The Modelview Matrix
			gl.glTranslated(x, y, 0);       // Position The Text (0,0 - Bottom Left)
			gl.glListBase(base - 32 + (128 * set));     // Choose The Font Set (0 or 1)

			if (stringBuffer.capacity() < string.length()) {
				stringBuffer = BufferUtil.newByteBuffer(string.length());
			}

			stringBuffer.clear();
			stringBuffer.put(string.getBytes());
			stringBuffer.flip();

			// Write The Text To The Screen
			gl.glCallLists(string.length(), GL.GL_BYTE, stringBuffer);      
			
			gl.glMatrixMode(GL.GL_PROJECTION);  // Select The Projection Matrix
			gl.glPopMatrix();      // Restore The Old Projection Matrix
			gl.glMatrixMode(GL.GL_MODELVIEW);  // Select The Modelview Matrix
			gl.glPopMatrix();      // Restore The Old Projection Matrix
			gl.glEnable(GL.GL_DEPTH_TEST);    // Enables Depth Testing
		}

		public void init(GLAutoDrawable glDrawable) {
			GL gl = glDrawable.getGL();

			loadGLTextures(gl);

			buildFont(gl);

			gl.glShadeModel(GL.GL_SMOOTH);                 // Enables Smooth Color Shading

			// This Will Clear The Background Color To Black
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);       

			// Enables Clearing Of The Depth Buffer
			gl.glClearDepth(1.0);                  

			gl.glEnable(GL.GL_DEPTH_TEST);                 // Enables Depth Testing
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);    // Select The Type Of Blending
			gl.glDepthFunc(GL.GL_LEQUAL);                  // The Type Of Depth Test To Do

			// Really Nice Perspective Calculations
			gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
			gl.glEnable(GL.GL_TEXTURE_2D);      // Enable 2D Texture Mapping
		}

		public void display(GLAutoDrawable glDrawable) {
			GL gl = glDrawable.getGL();

			// Clear The Screen And The Depth Buffer
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);       
			gl.glLoadIdentity();  // Reset The View

			// Select Our Second Texture
			gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1]);  
			gl.glTranslatef(0.0f, 0.0f, -5.0f);  // Move Into The Screen 5 Units

			// Rotate On The Z Axis 45 Degrees (Clockwise)
			gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);  

			// Rotate On The X & Y Axis By cnt1 (Left To Right)
			gl.glRotatef(cnt1 * 30.0f, 1.0f, 1.0f, 0.0f);  
			gl.glDisable(GL.GL_BLEND);          // Disable Blending Before We Draw In 3D
			gl.glColor3f(1.0f, 1.0f, 1.0f);     // Bright White
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(0.0f, 0.0f); gl.glVertex2f(-1.0f,  1.0f);
				gl.glTexCoord2d(1.0f, 0.0f); gl.glVertex2f( 1.0f,  1.0f);
				gl.glTexCoord2d(1.0f, 1.0f); gl.glVertex2f( 1.0f, -1.0f);
				gl.glTexCoord2d(0.0f, 1.0f); gl.glVertex2f(-1.0f, -1.0f);
			gl.glEnd();
			
			// Rotate On The X & Y Axis By 90 Degrees (Left To Right)
			gl.glRotatef(90.0f, 1.0f, 1.0f, 0.0f);  
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(0.0f, 0.0f); gl.glVertex2f(-1.0f,  1.0f);
				gl.glTexCoord2d(1.0f, 0.0f); gl.glVertex2f( 1.0f,  1.0f);
				gl.glTexCoord2d(1.0f, 1.0f); gl.glVertex2f( 1.0f, -1.0f);
				gl.glTexCoord2d(0.0f, 1.0f); gl.glVertex2f(-1.0f, -1.0f);
			gl.glEnd();
			gl.glEnable(GL.GL_BLEND);           // Enable Blending

			gl.glLoadIdentity();                // Reset The View

			// Pulsing Colors Based On Text Position
			gl.glColor3f((float) (Math.cos(cnt1)), (float) 
					(Math.sin(cnt2)), 1.0f - 0.5f * (float) (Math.cos(cnt1 + cnt2)));

			// Print GL Text To The Screen
			glPrint(gl, (int) ((280 + 250 * Math.cos(cnt1))), 
					(int) (235 + 200 * Math.sin(cnt2)), "NeHe", 0);    

			gl.glColor3f((float) (Math.sin(cnt2)), 1.0f - 0.5f * 
					(float) (Math.cos(cnt1 + cnt2)), (float) (Math.cos(cnt1)));

			// Print GL Text To The Screen
			glPrint(gl, (int) ((280 + 230 * Math.cos(cnt2))), 
					(int) (235 + 200 * Math.sin(cnt1)), "OpenGL", 1);  

			gl.glColor3f(0.0f, 0.0f, 1.0f);
			glPrint(gl, (int) (240 + 200 * Math.cos((cnt2 + cnt1) / 5)), 
					2, "Giuseppe D'Agata", 0);

			gl.glColor3f(1.0f, 1.0f, 1.0f);
			glPrint(gl, (int) (242 + 200 * Math.cos((cnt2 + cnt1) / 5)), 
					2, "Giuseppe D'Agata", 0);

			cnt1 += 0.01f;      // Increase The First Counter
			cnt2 += 0.0081f;    // Increase The Second Counter
		}

		public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {
			if (h == 0) h = 1;
			GL gl = glDrawable.getGL();

			// Reset The Current Viewport And Perspective Transformation
			gl.glViewport(0, 0, w, h);                       
			gl.glMatrixMode(GL.GL_PROJECTION);    // Select The Projection Matrix
			gl.glLoadIdentity();                  // Reset The Projection Matrix

			// Calculate The Aspect Ratio Of The Window
			glu.gluPerspective(45.0f, (float) w / (float) h, 0.1f, 100.0f);  
			gl.glMatrixMode(GL.GL_MODELVIEW);    // Select The Modelview Matrix
			gl.glLoadIdentity();                 // Reset The ModalView Matrix
		}

		public void displayChanged(GLAutoDrawable glDrawable, boolean b, boolean b1) {
		}
	}
	
	public static void main(String[] args){

		JFrame frame = new JFrame("Prueba Texto");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().setBackground(Color.BLACK);

		GLCapabilities caps = new GLCapabilities();

		GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(new Renderer());
		frame.getContentPane().add(canvas, BorderLayout.CENTER);

		frame.getContentPane().setPreferredSize(new Dimension(500, 250));
		frame.pack();
		frame.setLocationRelativeTo(null); // center
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		canvas.requestFocusInWindow();
	}
}