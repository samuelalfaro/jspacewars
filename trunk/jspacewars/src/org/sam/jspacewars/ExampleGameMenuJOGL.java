package org.sam.jspacewars;

import java.awt.BorderLayout;

import javax.media.opengl.*;
import javax.swing.JFrame;

import org.fenggui.Display;
import org.fenggui.binding.render.jogl.*;
import org.sam.jogl.fondos.Fondo;

import com.sun.opengl.util.Animator;

public class ExampleGameMenuJOGL {

	private static class GLEventListenerBackgroundRenderer implements GLEventListener {
		
		private final transient Fondo fondo;
		private transient long tAnterior, tActual;
		
		GLEventListenerBackgroundRenderer( Fondo fondo ){
			this.fondo = fondo;
			tActual = System.nanoTime();
		}
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
		 */
		public void init(GLAutoDrawable drawable) {
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
		 */
		public void display(GLAutoDrawable drawable) {
			tAnterior = tActual;
			tActual = System.nanoTime();
			float incT = (float)(tActual - tAnterior)/ 1000000000;
			GL gl = drawable.getGL();
			
			fondo.getModificador().modificar(incT);
			fondo.draw(gl);
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
		 */
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			GL gl = drawable.getGL();
			fondo.setProporcionesPantalla((float)width/height);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.5;
			double far  = 240.0;
			double a1 = 35.0;			// angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);
			//gl.glFrustum(-d,((2.0*w)/h -1.0)*d, -d, d, near, far);
			double anchovisible = 4.5/3.0;
			gl.glFrustum(-anchovisible*d,((2.0*width)/height -anchovisible)*d, -d, d, near, far);
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
		 */
		@Override
		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		}
	}
	
	private static class GLEventListenerDisplayGUI implements GLEventListener {
		
		private Display display;
		private final transient MyGameMenuButton botonPrototipo;
		
		GLEventListenerDisplayGUI(MyGameMenuButton botonPrototipo){
			this.botonPrototipo = botonPrototipo;
		}
		
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
		 */
		public void init(GLAutoDrawable drawable) {
			display = new Display(  new JOGLBinding((GLCanvas)drawable, drawable.getGL(), new JOGLOpenGL(drawable.getGL())) );
//			try{
//				FengGUI.setTheme( new XMLTheme("data/themes/QtCurve/QtCurve.xml") );
//			}catch( IOException ignorada ){
//			}catch( IXMLStreamableException ignorada ){
//			}
			new EventBinding((GLCanvas)drawable, display);
			display.addWidget( new GameMenu(botonPrototipo, display) );
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
		 */
		public void display(GLAutoDrawable drawable) {
			display.display();
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
		 */
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
		 */
		@Override
		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		}
	}

	public static void main(String[] args) {

		DataGame dataGame = new DataGame();

		SplashWindow splashFrame = new SplashWindow("splash.png", dataGame);
		splashFrame.setVisible(true);

		JFrame frame = new JFrame("FengGUI - Test Game Menu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setSize(640, 480);
		
		Animator animator = new Animator();
		animator.setRunAsFastAsPossible(true);
		animator.setPrintExceptions(true);
		
		splashFrame.setVisible(false);
		splashFrame = null;
		System.gc();
		
		GLCanvas canvas = new GLCanvas( new GLCapabilities(), null, dataGame.getGLContext(), null );
		canvas.addGLEventListener( new GLEventListenerBackgroundRenderer( dataGame.getFondo()) );
		canvas.addGLEventListener( new GLEventListenerDisplayGUI(dataGame.getBotonPrototipo()) );
		
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		
		frame.setVisible(true);
		animator.add(canvas);
		animator.start();
	}
}
