package pruebas.jogl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.ObjetosOrientables;
import org.sam.jogl.Shader;
import org.sam.jogl.Textura;
import org.sam.util.Imagen;

import com.sun.opengl.util.Animator;

public class Prueba013_NormalMap{
	
	private static class Renderer implements GLEventListener{
		
		private GLU glu;
		private OrbitBehavior orbitBehavior;

		private Apariencia apFondo;
		private Objeto3D helix;
		
		private transient float proporcionesFondo, proporcionesPantalla;

		public void init(GLAutoDrawable drawable){
			GL gl = drawable.getGL();
			glu = new GLU();
			orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos(9.0f, 0.0f, 22.0f);
			orbitBehavior.setTargetPos(9.0f, 0.0f, 0.0f);
			orbitBehavior.addMouseListeners((GLCanvas)drawable);

			gl.glClearColor(0.0f,0.25f,0.25f,0.0f);

			BufferedImage img = Imagen.cargarToBufferedImage("resources/texturas/cielo512.jpg");
			proporcionesFondo = img.getHeight(null)/img.getWidth(null);
			apFondo = new Apariencia();

			apFondo.setTextura(new Textura(gl, Textura.Format.RGB, img , true));
			apFondo.setAtributosTextura(new AtributosTextura());
			apFondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);

			helix = HelixGenerator.generateHelix(gl, 1.2f, 3.0f, 6);
			Textura bump= new Textura(
					gl,
					Textura.Format.RGB,
					Imagen.cargarToBufferedImage("resources/texturas/bump2.png"),
					true
			);
			bump.setWrap_s(Textura.Wrap.CLAMP_TO_EDGE);
			bump.setWrap_t(Textura.Wrap.CLAMP_TO_EDGE);
			helix.getApariencia().setTextura(bump);
        	Shader shader = new Shader(
        			gl,
        			"shaders/normal.vert",
					"shaders/normal.frag"
        	);
        	shader.addUniform(gl, "normalMap", 0 );
        	System.out.println( gl.glGetAttribLocation(shader.programObject, "vTangent") );
   	
        	helix.getApariencia().setShader(shader);

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LESS);

			gl.glEnable(GL.GL_LIGHT0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, 	new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,	new float[]{ 0.9f, 1.0f, 1.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,	new float[]{ 1.0f, 1.0f, 0.9f, 1.0f }, 0);
			//gl.glEnable(GL.GL_CULL_FACE);
		}

		public void display(GLAutoDrawable drawable){

			GL gl = drawable.getGL();

			gl.glMatrixMode( GL.GL_MODELVIEW );
			gl.glLoadIdentity();
			ObjetosOrientables.loadModelViewMatrix();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);
			ObjetosOrientables.loadProjectionMatrix();

			apFondo.usar(gl);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

			float s1 = 0.75f;
			float s2 = proporcionesPantalla*proporcionesFondo + s1;

			gl.glDepthMask(false);
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
			
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPopMatrix();

			gl.glMatrixMode(GL.GL_MODELVIEW);
			
			orbitBehavior.setLookAt(glu);
			
			helix.draw(gl);
			
			gl.glFlush();
		}
		
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, w, h);
			proporcionesPantalla = (float)w/h;
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.01;
			double far  = 100.0;
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
		
		JFrame frame = new JFrame("Prueba Normal Mapping");
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().setPreferredSize(new Dimension(640, 480));
		frame.pack();
		frame.setLocationRelativeTo(null); // center
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GLCanvas canvas = new GLCanvas(new GLCapabilities());
		canvas.addGLEventListener(new Renderer());

		frame.getContentPane().add(canvas);
		
		Animator animator = new Animator();
		// animator.setRunAsFastAsPossible(true);
		animator.add(canvas);
		
		frame.setVisible(true);

		animator.add(canvas);
		canvas.requestFocusInWindow();
		animator.start();
	}
}