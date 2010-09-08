package pruebas.jogl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Matrix4d;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.ObjLoader;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.MatrixSingleton;
import org.sam.jogl.Textura;
import org.sam.jogl.ObjLoader.ParsingErrorException;
import org.sam.util.Imagen;

import com.sun.opengl.util.Animator;

public class Prueba011_StencilBufferWireframe{
	
	private static class Renderer implements GLEventListener{
		
		private GLU glu;
		private OrbitBehavior orbitBehavior;

		private Apariencia apFondo;
		private Objeto3D forma;
		
		private transient float proporcionesFondo, proporcionesPantalla;

		public void init(GLAutoDrawable drawable){
			GL gl = drawable.getGL();
			glu = new GLU();
			orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos(0.0f, 0.0f, -22.0f);
			orbitBehavior.setTargetPos(0.0f, 0.0f, 0.0f);
			orbitBehavior.addMouseListeners((GLCanvas)drawable);

			gl.glClearColor(0.0f,0.25f,0.25f,0.0f);

			BufferedImage img = Imagen.cargarToBufferedImage("resources/texturas/cielo512.jpg");
			proporcionesFondo = img.getHeight(null)/img.getWidth(null);
			apFondo = new Apariencia();

			apFondo.setTextura(new Textura(gl, Textura.Format.RGB, img , true));
			apFondo.setAtributosTextura(new AtributosTextura());
			apFondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);

			/*
			forma = HelixGenerator.generate(gl, 1.2f, 3.0f, 6);
			/*/
			Matrix4d mtd = new Matrix4d();
			mtd.rotY(Math.PI/2);
			mtd.setScale(18.0);
			try {
				String path = "resources/obj3d/nave01/forma.obj";
				forma = ObjLoader.load(
						path,
						ObjLoader.RESIZE|ObjLoader.GENERATE_TANGENTS,
						mtd
				);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ParsingErrorException e) {
				e.printStackTrace();
			}
			//*/

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LESS);

			gl.glEnable(GL.GL_LIGHT0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, 	new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,	new float[]{ 0.9f, 1.0f, 1.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,	new float[]{ 1.0f, 1.0f, 0.9f, 1.0f }, 0);
			//gl.glEnable(GL.GL_CULL_FACE);
			gl.glClearStencil(0);
			gl.glStencilMask(~0);
		}

		public void display(GLAutoDrawable drawable){

			GL gl = drawable.getGL();

			gl.glMatrixMode( GL.GL_MODELVIEW );
			gl.glLoadIdentity();
			MatrixSingleton.loadModelViewMatrix();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);
			MatrixSingleton.loadProjectionMatrix();

			apFondo.usar(gl);
			gl.glStencilMask(~0);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT|GL.GL_STENCIL_BUFFER_BIT);

			gl.glDepthMask(false);
			float s1 = 0.75f;
			float s2 = proporcionesPantalla*proporcionesFondo + s1;
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(s1,0);
				gl.glVertex3f(0,0,0);
				gl.glTexCoord2f(s2,0);
				gl.glVertex3f(proporcionesPantalla,0,0);
				gl.glTexCoord2f(s2,1);
				gl.glVertex3f(proporcionesPantalla,1,0);
				gl.glTexCoord2f(s1,1);
				gl.glVertex3f(0,1,0);
			gl.glEnd();
			gl.glDepthMask(true);
			
			gl.glPopMatrix();

			gl.glMatrixMode(GL.GL_MODELVIEW);
			
			orbitBehavior.setLookAt(glu);

			gl.glColorMask(false, false, false, false);
			gl.glEnable( GL.GL_POLYGON_OFFSET_FILL );
			gl.glPolygonOffset( 1.1f, 0.1f);
			forma.getGeometria().draw(gl);
			gl.glDisable( GL.GL_POLYGON_OFFSET_FILL );
			gl.glPolygonOffset( 0.f, 0.f);
			
			gl.glDepthMask(false);
			gl.glDepthFunc(GL.GL_LEQUAL);
			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glStencilFunc(GL.GL_ALWAYS, 1, ~0);
			gl.glStencilOp( GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE );
			gl.glFlush();
			
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glLineWidth(1);
			forma.getGeometria().draw(gl);

			gl.glColorMask(true, true, true, true);
			gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );
			gl.glStencilFunc(GL.GL_EQUAL, 1, ~0);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_ZERO);
			//gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
			
			/*
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
			gl.glDepthMask(true);
			gl.glDepthFunc(GL.GL_LESS);
			//gl.glDisable(GL.GL_STENCIL_TEST);
			forma.draw(gl);
			//gl.glEnable(GL.GL_STENCIL_TEST);

			/*/
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glDepthMask(false);
			
			gl.glMatrixMode( GL.GL_MODELVIEW );
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);
			gl.glDisable(GL.GL_LIGHTING);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glBegin(GL.GL_QUADS);
				gl.glColor3f(1.0f, 0.0f, 1.0f);
				gl.glVertex3f(0,0,0);
				gl.glVertex3f(proporcionesPantalla,0,0);
				gl.glVertex3f(proporcionesPantalla,1,0);
				gl.glVertex3f(0,1,0);
			gl.glEnd();
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL.GL_LIGHTING);
			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPopMatrix();
			
			gl.glDepthMask(true);
			gl.glEnable(GL.GL_DEPTH_TEST);
			
			// clean up state */
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glDisable(GL.GL_STENCIL_TEST);
			gl.glDepthFunc(GL.GL_LESS);
			
			gl.glFlush();
		}
		
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, w, h);
			proporcionesPantalla = (float)w/h;
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.5;
			double far  = 50.0;
			double a1 = 45.0;			// angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);

			// formato panoramico Horizontal/Vertical centrado
			double ratio = (double) w / h;
			if( ratio < 1 )
				gl.glFrustum(-d, d, -d / ratio, d / ratio, near, far);
			else
				gl.glFrustum(-d * ratio, d * ratio, -d, d, near, far);

			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
				boolean deviceChanged){
		}
	}
	
	public static void main(String[] args){
		
		JFrame frame = new JFrame("Prueba Stencil Buffer Wireframe");
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().setPreferredSize(new Dimension(640, 480));
		frame.pack();
		frame.setLocationRelativeTo(null); // center
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GLCapabilities caps = new GLCapabilities();
		caps.setStencilBits(8);
		GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(new Renderer());

		frame.getContentPane().add(canvas);
		
		Animator animator = new Animator();
		animator.add(canvas);
		
		frame.setVisible(true);

		animator.add(canvas);
		canvas.requestFocusInWindow();
		animator.start();
	}
}