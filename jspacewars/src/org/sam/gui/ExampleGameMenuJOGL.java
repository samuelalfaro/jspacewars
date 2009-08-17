package org.sam.gui;

import java.awt.BorderLayout;

import javax.media.opengl.*;
import javax.swing.JFrame;

import org.fenggui.Display;
import org.fenggui.binding.render.jogl.EventBinding;
import org.fenggui.binding.render.jogl.JOGLBinding;
import org.sam.jogl.DataGame;
import org.sam.jogl.fondos.Fondo;
import org.sam.util.ModificableBoolean;

import com.sun.opengl.util.Animator;

public class ExampleGameMenuJOGL {

	private static class GLEventListenerImplementation implements GLEventListener {
		
		private Display display;
		private final transient Fondo fondo;
		private transient long tAnterior, tActual;
		
		GLEventListenerImplementation(Fondo fondo){
			this.fondo = fondo;
			tActual = System.nanoTime();
		}
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
		 */
		public void init(GLAutoDrawable drawable) {
			
			display = new Display( new JOGLBinding((GLCanvas)drawable,  drawable.getGL()));
//			try{
//				FengGUI.setTheme( new XMLTheme("data/themes/QtCurve/QtCurve.xml") );
//			}catch( IOException ignorada ){
//			}catch( IXMLStreamableException ignorada ){
//			}
			
//			gl.glClearColor(146f / 255f, 164f / 255f, 1, 0.0f);
//			gl.glEnable(GL.GL_BLEND);
//
//			gl.glDisable(GL.GL_TEXTURE_2D);
//			gl.glEnable(GL_DEPTH_TEST);
//			gl.glDepthFunc(GL_LEQUAL);
//			gl.glShadeModel(GL.GL_SMOOTH);
//			gl.glEnable(GL_LIGHTING);
//			gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, new float[] { 146f / 255f, 164f / 255f, 1f, 1.0f }, 0);
//			gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, new float[] { 1f, 1f, 1f, 1.0f }, 0);
//			gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, new float[] { -100, -100, 400 }, 0);
//			gl.glEnable(GL.GL_LIGHT1);

			new EventBinding((GLCanvas)drawable, display);
			display.addWidget( new GameMenu(display) );
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
			display.display();
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

	public static void main(String[] args) {

		DataGame dataGame = new DataGame();
		ModificableBoolean loading = new ModificableBoolean(true);

		SplashWindow splashFrame = new SplashWindow("splash.png", dataGame, loading);
		splashFrame.setVisible(true);

		JFrame frame = new JFrame("FengGUI - Test Game Menu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setSize(640, 480);
		
		synchronized(loading){
			try{
				loading.wait();
			}catch( InterruptedException e ){
				e.printStackTrace();
			}
		}
		
		GLCanvas canvas = new GLCanvas(new GLCapabilities(), null,dataGame.getGLContext(),null );
		canvas.addGLEventListener( new GLEventListenerImplementation(dataGame.getFondo()) );
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		
		Animator animator = new Animator(canvas);
		animator.setRunAsFastAsPossible(true);
		animator.setPrintExceptions(true);

		splashFrame.setVisible(false);
		splashFrame = null;
		System.gc();

		frame.setVisible(true);
		animator.start();
	}
}
