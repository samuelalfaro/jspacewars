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
import org.sam.jogl.Textura;
import org.sam.jspacewars.cliente.MarcoDeIndicadores;
import org.sam.util.Imagen;

import pruebas.jogl.generators.HelixGenerator;

import com.sun.opengl.util.Animator;

public class Prueba040_Interface{
	
	private static class Renderer implements GLEventListener{

		private GLU glu;
		private OrbitBehavior orbitBehavior;

		private Apariencia apFondo;
		private Objeto3D helix;

		private final MarcoDeIndicadores marco = MarcoDeIndicadores.getMarco(0);

		private transient float proporcionesPantalla;
		private transient float proporcionesArea;

		public void init(GLAutoDrawable drawable){
			GL gl = drawable.getGL();
			glu = new GLU();
			orbitBehavior = new OrbitBehavior();
			orbitBehavior.setEyePos(0.0f, 0.0f, -22.0f);
			orbitBehavior.setTargetPos(0.0f, 0.0f, 0.0f);
			orbitBehavior.addMouseListeners((GLCanvas)drawable);

			gl.glClearColor(0.0f,0.25f,0.25f,0.0f);

			BufferedImage img = Imagen.cargarToBufferedImage("resources/texturas/cielo512.jpg");
			apFondo = new Apariencia();

			apFondo.setTextura(new Textura(gl, Textura.Format.RGB, img , true));
			apFondo.setAtributosTextura(new AtributosTextura());
			apFondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);

			while(!marco.isLoadComplete())
				marco.loadTexturas(gl);

			helix = HelixGenerator.generate(gl, 1.2f, 3.0f, 6);

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LESS);

			gl.glEnable(GL.GL_LIGHT0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, 	new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,	new float[]{ 0.9f, 1.0f, 1.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,	new float[]{ 1.0f, 1.0f, 0.9f, 1.0f }, 0);
			gl.glEnable(GL.GL_CULL_FACE);
		}

		public void display(GLAutoDrawable drawable){
			
			GL gl = drawable.getGL();

			gl.glMatrixMode( GL.GL_MODELVIEW );
			gl.glLoadIdentity();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);

			apFondo.usar(gl);
			gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
			
			float s1 = 0.75f;
			float s2 = proporcionesArea * apFondo.getTextura().getProporciones() + s1;
			
			float p64 = proporcionesPantalla/64;
			
			gl.glDepthMask(false);
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(s1, 0);
				gl.glVertex2f(      p64,         p64);
				gl.glTexCoord2f(s2, 0);
				gl.glVertex2f( 63 * p64,         p64);
				gl.glTexCoord2f(s2, 1);
				gl.glVertex2f( 63 * p64, 1 - 5 * p64);
				gl.glTexCoord2f(s1, 1);
				gl.glVertex2f(      p64, 1 - 5 * p64);
			gl.glEnd();
			gl.glDepthMask(true);
			
			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			orbitBehavior.setLookAt(glu);

			helix.draw(gl);

			marco.draw(gl);
			gl.glFlush();
		}

		private static final double W = 4.0;
		private static final double H = 3.0;
		private static final double H_A = H - (3.0 * W / 32);
		private static final double OFF = 2*(H - H_A)/H_A;
		private static final double wA43 = (2.0 + OFF)*W/H;

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
		 */
		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, width, height);
			proporcionesPantalla = (float)width/height;
			marco.setBounds( 0, 0, width, height);
			double aWidth = 31.0 * width / 32;
			double aHeight = height - (3.0 * width / 32);
			proporcionesArea = (float)(aWidth / aHeight);
			
//			gl.glViewport((width -(int)aWidth)/2, (height - (int)aHeight)/6, (int)aWidth, (int)aHeight);
			
			// Formato, offset GUI, 4/3 mínimo, panorámico a la derecha en caso contrario.
			
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.01;
			double far  = 100.0;
			double a1 = 45.0;			// angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);

			// aHeight -> 2, [-d, d] => offset -> 2·(height - aHeight)/aHeight ;
			double offH = 2*(height - aHeight)/aHeight;
			// height -> 2.0 + offH => width -> (2.0 + offH)*width/height
			double wA = (2.0 + offH)*width/height;
			
			double offBottom = offH/6;
			double offTop = 5*offH/6;
			
			// Si las proporciones de la pantalla son menores de 4/3 se centra el origen en la pantlla
			if(wA <= wA43)
				gl.glFrustum(-d * ( wA/2 ), d * ( wA/2 ), -d * ( 1 + offBottom ), d*( 1 + offTop ), near, far);
			// Si no, se considera el centro igual que el de 4/3 desde la izqda, y se hace panoramica la dcha.
			else
				gl.glFrustum(-d * ( wA43/2 ), d * ( wA - wA43/2 ), -d * ( 1 + offBottom ), d*( 1 + offTop ), near, far);
			
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
				boolean deviceChanged){
		}
	}

	public static void main(String[] args){
		
		JFrame frame = new JFrame("Prueba Interface");
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