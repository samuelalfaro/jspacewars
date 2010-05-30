package pruebas.jogl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import org.sam.jogl.Shader;

import com.sun.opengl.util.Animator;

/**
 * Prueba del efecto Bloom.<br/>
 * <br/>
 * Basado en port a jogl de <a href="http://www.java-tips.org/other-api-tips/jogl/radial-blur-and-rendering-to-a-texture-nehe-tutorial-jogl.html">Pepijn Van Eeckhoudt</a>
 * del la <a href="http://nehe.gamedev.net/data/lessons/lesson.asp?lesson=36">leccion 36</a> de los tutoriales <a href="http://nehe.gamedev.net/">NeHe</a>.<br/>
 * Y en el código de la demo <a href="http://pouet.scene.org/prod.php?which=54043">coffee</a> de <a href="http://pouet.scene.org/groups.php?which=10834">rustbloom</a>.
 */
public class PruebaBloom {

	private static int initTexture(GL gl, int w, int h) {
		int[] textuId = new int[1];
		gl.glGenTextures(1, textuId, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textuId[0]);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, w, h, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

		return textuId[0];
	}
	
	private static int initFBO(GL gl, int textuId, int w, int h) {
		int FBO[] = new int[1];
		gl.glGenFramebuffersEXT(1, FBO, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textuId);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, FBO[0]);
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, textuId, 0);

		int[] depthId = new int[1];
		gl.glGenRenderbuffersEXT(1, depthId, 0);
		gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthId[0]);
		gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT, GL.GL_DEPTH_COMPONENT24, w, h);
		gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT, depthId[0]);

		/*
		int fbostate = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		switch (fbostate) {
		case GL.GL_FRAMEBUFFER_COMPLETE_EXT:
			System.out.println("framebuffer complete");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
			System.out.println("incomplete: GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
			System.out.println("incomplete: GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
			System.out.println("incomplete: GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			break;
		case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
			System.out.println("incomplete: GL_FRAMEBUFFER_INCOMPLETE_UNSUPPORTED");
			break;
		}*/
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
		
		return FBO[0];
	}

	private static float DEG_TO_RAD = (float)(Math.PI / 180.0);
	private static float TOW_TO_PI  = (float)( 2 * Math.PI);
	
	private static int generateHelix(GL gl, float r1I, float r1F, float r2I, float r2F, float l, int twists) {
		float incTheta = 15.0f * DEG_TO_RAD;
		float incPhi   = 30.0f * DEG_TO_RAD;
		
		int lid = gl.glGenLists(1);
		
		gl.glNewList(lid, GL.GL_COMPILE);
		gl.glBegin(GL.GL_QUADS);
		
		float cosU0 = 1.0f;
		float sinU0 = 0.0f;
		float cosU1 = (float)Math.cos( incTheta );
		float sinU1 = (float)Math.sin( incTheta );
		float l0 = 0, l1 = l * incTheta / TOW_TO_PI;
		float r10 = r1I, r11 = (incTheta /(TOW_TO_PI * twists)) * (r1F - r1I) + r1I;
		float r20 = r2I, r21 = (incTheta /(TOW_TO_PI * twists)) * (r2F - r2I) + r2I;
		
		for (float theta = 0.0f; theta <= TOW_TO_PI * twists; ) {
				
			float cosV0 = 1.0f;
			float sinV0 = 0.0f;
			float cosV1 = (float) Math.cos( incPhi );
			float sinV1 = (float) Math.sin( incPhi );

			float posX00 = r10 + l0;
			float posY00 = r20;
			float posX10 = r10 * cosV1 + l0;
			float posY10 = r10 * sinV1 + r20;
			
			float posX01 = r11 + l1;
			float posY01 = r21;
			float posX11 = r11 * cosV1 + l1;
			float posY11 = r11 * sinV1 + r21;
		
			for (float phi = 0.0f; phi <= TOW_TO_PI; ) {
				
				gl.glNormal3f(  cosV0,  sinV0 * cosU0,  sinV0 * sinU0 );
				gl.glVertex3f( posX00, posY00 * cosU0, posY00 * sinU0 );
				
				gl.glNormal3f(  cosV1,  sinV1 * cosU0,  sinV1 * sinU0 );
				gl.glVertex3f( posX10, posY10 * cosU0, posY10 * sinU0 );
				
				gl.glNormal3f(  cosV1,  sinV1 * cosU1,  sinV1 * sinU1 );
				gl.glVertex3f( posX11, posY11 * cosU1, posY11 * sinU1 );
				
				gl.glNormal3f(  cosV0,  sinV0 * cosU1,  sinV0 * sinU1 );
				gl.glVertex3f( posX01, posY01 * cosU1, posY01 * sinU1 );
				
				cosV0 = cosV1;
				sinV0 = sinV1;
				posX00 = posX10;
				posY00 = posY10;
				posX01 = posX11;
				posY01 = posY11;
				
				phi += incPhi;
				cosV1 = (float) Math.cos( phi );
				sinV1 = (float) Math.sin( phi );
				posX10 = r10 * cosV1 + l0;
				posY10 = r10 * sinV1 + r20;
				posX11 = r11 * cosV1 + l1;
				posY11 = r11 * sinV1 + r21;
			}
			cosU0 = cosU1;
			sinU0 = sinU1;
			theta += incTheta;
			cosU1 = (float)Math.cos( theta );
			sinU1 = (float)Math.sin( theta );
			l0 = l1;
			float alpha = theta / TOW_TO_PI;
			l1 = alpha * l;
			r10 = r11;
			r11 = (theta /(TOW_TO_PI * twists)) * (r1F - r1I) + r1I;
			r20 = r21;
			r21 = (theta /(TOW_TO_PI * twists)) * (r2F - r2I) + r2I;
		}
		gl.glEnd();
		gl.glEndList();
		
		return lid;
	}
	
	private static int generateHelix(GL gl, float r1, float r2, float l, int twists) {
		return generateHelix(gl, r1, r1, r2, r2, l, twists);
	}
	
	private static void viewOrtho(GL gl, int w, int h) {
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, w, 0, h, -1, 1);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
	}

	private static void viewPerspective(GL gl) {
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glPopMatrix();
	}
	
	private static class Renderer implements GLEventListener {
		
		// User Defined Variables
		private GLU glu = new GLU();
		private int width, height;
		
		private int idList;
		private int textuId[];
		private int FBO[];
		private Shader corte, hBlur, vBlur;
		
		private float angle;
		private float bloom;
		private long previousTime = System.currentTimeMillis();

		public void init(GLAutoDrawable drawable) {
			GL gl = drawable.getGL();
			angle = 0.0f;
			bloom = 8f;

			textuId = new int[2];
			textuId[0] = initTexture(gl,256,256);
			textuId[1] = initTexture(gl,256,256);
			FBO = new int[2];
			FBO[0] = initFBO(gl, textuId[0],256,256);
			FBO[1] = initFBO(gl, textuId[1],256,256);
			idList = generateHelix(gl, 2.5f, 5.0f, 7.5f, 20);
			
			corte = new Shader(gl,"shaders/filter.vert","shaders/corte.frag");
			corte.addUniform(gl, "textureIn", 0);
			corte.addUniform(gl, "pixelSize", 1.0f/256);
			corte.addUniform(gl, "corte", 0.2f);
			corte.addUniform(gl, "bloom", bloom);
			
			hBlur = new Shader(gl,"shaders/filter.vert","shaders/blurhorizontal.frag");
			hBlur.addUniform(gl, "textureIn", 0);
			hBlur.addUniform(gl, "pixelSize", 1.0f/256);
			
			vBlur = new Shader(gl,"shaders/filter.vert","shaders/blurvertical.frag");
			vBlur.addUniform(gl, "textureIn", 0);
			vBlur.addUniform(gl, "pixelSize", 1.0f/256);
			
			gl.glEnable(GL.GL_DEPTH_TEST);
		
			float[] global_ambient = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
			float[] light0pos = new float[] { 0.0f, 5.0f, 10.0f, 1.0f };
			float[] light0ambient = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
			float[] light0diffuse = new float[] { 0.3f, 0.3f, 0.3f, 1.0f };
			float[] light0specular = new float[] { 0.8f, 0.8f, 0.8f, 1.0f };
		
			float[] lmodel_ambient = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
			gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
		
			gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, global_ambient, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light0pos, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light0ambient, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light0diffuse, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, light0specular, 0);
			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_LIGHT0);
		
			gl.glShadeModel(GL.GL_SMOOTH);
		
			gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, 128);
			gl.glDisable(GL.GL_LIGHTING);
		}

		private void drawHelix(GL gl) {

			float[] glfMaterialColor = new float[] { 0.4f, 0.2f, 0.8f, 1.0f };
			float[] specular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

			gl.glPushMatrix();

			gl.glTranslatef(0, 0, -50);
			gl.glRotatef(angle / 2.0f, 1, 0, 0);
			gl.glRotatef(angle / 3.0f, 0, 1, 0);

			gl.glEnable(GL.GL_LIGHTING);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE, glfMaterialColor, 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, specular, 0);

			gl.glCallList(idList);
			gl.glDisable(GL.GL_LIGHTING);
			
			gl.glPopMatrix();
		}
		
		private void renderToTexture(GL gl, int w, int h) {

			gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, FBO[0]);

			gl.glViewport(0, 0, w, h);
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			drawHelix(gl);

			viewOrtho(gl, w, h);
			
			gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, FBO[1]);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, textuId[0]);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,	GL.GL_REPLACE);
			corte.setUniform("bloom", bloom);
			corte.activar(gl);
			
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(0, 0); gl.glVertex2f(0, 0);
				gl.glTexCoord2f(0, 1); gl.glVertex2f(0, h);
				gl.glTexCoord2f(1, 1); gl.glVertex2f(w, h);
				gl.glTexCoord2f(1, 0); gl.glVertex2f(w, 0);
			gl.glEnd();

			gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, FBO[0]);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, textuId[1]);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,	GL.GL_REPLACE);
			hBlur.activar(gl);

			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(0, 0); gl.glVertex2f(0, 0);
				gl.glTexCoord2f(0, 1); gl.glVertex2f(0, h);
				gl.glTexCoord2f(1, 1); gl.glVertex2f(w, h);
				gl.glTexCoord2f(1, 0); gl.glVertex2f(w, 0);
			gl.glEnd();
			
			gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, FBO[1]);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, textuId[0]);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,	GL.GL_REPLACE);
			vBlur.activar(gl);

			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(0, 0); gl.glVertex2f(0, 0);
				gl.glTexCoord2f(0, 1); gl.glVertex2f(0, h);
				gl.glTexCoord2f(1, 1); gl.glVertex2f(w, h);
				gl.glTexCoord2f(1, 0); gl.glVertex2f(w, 0);
			gl.glEnd();

			Shader.desactivar(gl);
			viewPerspective(gl);
			
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
			gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

			gl.glViewport(0, 0, width, height);
		}

		private void drawBlur(GL gl, int times, float inc) {
			float spost = 0.0f;
			float alpha = 0.1f;

			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, textuId[1]);
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,	GL.GL_MODULATE);

			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendEquation(GL.GL_FUNC_ADD);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
//			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

			viewOrtho(gl, width, height);

			float alphainc = alpha / times;

			gl.glBegin(GL.GL_QUADS);
			for (int num = 0; num < times; num++) {
				gl.glColor3f(alpha, alpha, alpha);
				gl.glTexCoord2f(0 + spost, 0 + spost); gl.glVertex2f(0, 0);
				gl.glTexCoord2f(0 + spost, 1 - spost); gl.glVertex2f(0, height);
				gl.glTexCoord2f(1 - spost, 1 - spost); gl.glVertex2f(width, height);
				gl.glTexCoord2f(1 - spost, 0 + spost); gl.glVertex2f(width, 0);

				spost += inc;
				alpha -= alphainc;
			}
			gl.glEnd();
			
			viewPerspective(gl);

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glDisable(GL.GL_BLEND);
		}

		private long t = 0;
		private void update(long milliseconds) {
			angle += milliseconds / 5.0f;
			t += milliseconds;
			if(t > 10000)
				t -= 10000;
			bloom = (float)( Math.sin(t*Math.PI/2000) +1.0 ) *8f;
		}

		public void display(GLAutoDrawable drawable) {
			long currentTime = System.currentTimeMillis();
			update(currentTime - previousTime);
			previousTime = currentTime;

			GL gl = drawable.getGL();

			gl.glLoadIdentity();
			glu.gluLookAt(0, 5, 50, 0, 0, 0, 0, 1, 0);
			
			renderToTexture(gl, 256, 256);
			gl.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			drawHelix(gl);
			
			drawBlur(gl, 25, 0.02f);
			gl.glFlush();
		}

		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			GL gl = drawable.getGL();

			height = (height == 0) ? 1 : height;
			this.width = width;
			this.height = height;

			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPerspective(50, (float) width / height, 5, 2000);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
		}

		public void displayChanged(GLAutoDrawable drawable,
				boolean modeChanged,
				boolean deviceChanged) {
		}
	}

	public static void main(String[] args){

		JFrame frame = new JFrame("Bloom");
		frame.getContentPane().setPreferredSize( new Dimension (800, 600));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GLCanvas canvas = new GLCanvas(new GLCapabilities());
		canvas.setBackground(Color.BLACK);

		Renderer renderer = new Renderer();
		canvas.addGLEventListener(renderer);

		frame.getContentPane().add( canvas, BorderLayout.CENTER );

		Animator animator = new Animator();
		animator.setRunAsFastAsPossible(true);
		animator.add(canvas);

		frame.setVisible(true);
		animator.start();
	}
}
