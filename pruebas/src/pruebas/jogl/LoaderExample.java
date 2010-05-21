package pruebas.jogl;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.ObjLoader;
import org.sam.jogl.ObjetosOrientables;
import org.sam.jogl.Textura;
import org.sam.jspacewars.serialization.GrafoEscenaConverters;
import org.sam.util.Imagen;
import org.sam.util.ModificableBoolean;

import com.sun.opengl.util.Animator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class LoaderExample{
	
	private static class Data{
		GLContext context;
		Apariencia apFondo;
		Instancia3D[] instancias;
	}
	
	private static class Loader implements GLEventListener{
		
		// Indices rgb
		@SuppressWarnings("unused")
		private static final transient int r = 0, g = 1, b = 2;
		
		private final transient Data data;
		private final transient ModificableBoolean loading;
		private final transient float[] color1, color2;

		Loader( Data data, ModificableBoolean loading ){
			this.data = data;
			this.loading = loading;
			color1 = new float[] { 1.0f, 0.0f, 0.0f };
			color2 = new float[] { 1.0f, 0.0f, 0.0f };
		}

		private transient int ciclo;
		
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void init(GLAutoDrawable drawable) {
			data.context = drawable.getContext();

			GL gl = drawable.getGL();
			gl.glDepthMask(false);
			gl.glShadeModel(GL.GL_SMOOTH);
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			ciclo = 0;
		}

		private static final transient int MAX_CICLOS = 42;
		private static final transient float inc1 = 1.0f / (MAX_CICLOS / 2);
		private static final transient float inc2 = 1.0f / MAX_CICLOS;

		private transient float proporcionesPantalla;
		
		private transient ObjectInputStream in;
		private transient Collection<Instancia3D> intanciasCollection;
		private transient boolean eof;
		
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void display(GLAutoDrawable drawable) {
			GL gl = drawable.getGL();
			if( ciclo == 0 ){

			}else if(data.apFondo == null ){
				BufferedImage img = Imagen.cargarToBufferedImage("resources/texturas/cielo512.jpg");
				data.apFondo = new Apariencia();

				data.apFondo.setTextura(new Textura(gl, Textura.Format.RGB, img , true));
				data.apFondo.setAtributosTextura(new AtributosTextura());
				data.apFondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);
			}else{
				try{
					if( in == null ){
						XStream xStream = new XStream(new DomDriver());
						xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
						GrafoEscenaConverters.register(xStream);
						GrafoEscenaConverters.setReusingReferenceByXPathMarshallingStrategy(xStream);

						intanciasCollection = new LinkedList<Instancia3D>();
						in = xStream.createObjectInputStream(new FileReader("resources/instancias3D-stream-sh.xml"));
					}
					if( !eof )
						try{
							intanciasCollection.add((Instancia3D) in.readObject());
						}catch( ClassNotFoundException e ){
							e.printStackTrace();
						}catch( EOFException eofEx ){
							eof = true;
							in.close();
						}
				}catch( FileNotFoundException e ){
					e.printStackTrace();
				}catch( IOException e ){
					e.printStackTrace();
				}catch( ObjLoader.ParsingErrorException e ){
					e.printStackTrace();
				}catch( GLException e ){
					e.printStackTrace();
				}
			}

			color1[g] += inc1;
			if( color1[g] > 1.0f ){
				color1[g] = 1.0f;
				color1[r] -= inc1;
				if( color1[r] < 0 )
					color1[r] = 0.0f;
			}
			color2[g] += inc2;
			if( color2[g] > 1.0f )
				color2[g] = 1.0f;

			gl.glClear(GL.GL_COLOR_BUFFER_BIT);

			ciclo++;

			gl.glBegin(GL.GL_QUADS);
			gl.glColor3fv(color2, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glColor3fv(color1, 0);
			gl.glVertex3f(proporcionesPantalla * ciclo / MAX_CICLOS, 0, 0);
			gl.glColor3fv(color1, 0);
			gl.glVertex3f(proporcionesPantalla * ciclo / MAX_CICLOS, 1, 0);
			gl.glColor3fv(color2, 0);
			gl.glVertex3f(0, 1, 0);
			gl.glEnd();
			gl.glFlush();

			if( eof && intanciasCollection != null){
				synchronized( loading ){
					data.instancias = intanciasCollection.toArray(new Instancia3D[intanciasCollection.size()]);
					intanciasCollection = null;
					loading.setFalse();
					loading.notifyAll();
				}
//				drawable.removeGLEventListener(this);
			}
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
		 */
		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, width, height);
			proporcionesPantalla = (float) width / height;

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
		 */
		@Override
		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		}
	}
	
	private static class Renderer implements GLEventListener, KeyListener{
		
		private final Data data;
		private final OrbitBehavior orbitBehavior;
		private transient int index = 0;
		
		Renderer( Data data , OrbitBehavior orbitBehavior){
			this.data = data;
			this.orbitBehavior = orbitBehavior;
		}
		
		private transient float proporcionesPantalla;
		private transient long tAnterior, tActual;
		private GLU glu;

		private transient boolean iniciado = false;
		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void init(GLAutoDrawable drawable) {
			GL gl = drawable.getGL();
			glu = new GLU();
			
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LESS);
			
			gl.glDisable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_LIGHT0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,
				new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
			gl.glEnable(GL.GL_CULL_FACE);
			
			data.instancias[index].reset();
			
			tActual = System.nanoTime();
			iniciado = true;
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void display(GLAutoDrawable drawable) {
			if(!iniciado)
				init(drawable);
			tAnterior = tActual;
			tActual = System.nanoTime();
			float incT = (tActual - tAnterior) / 1000000000.0f;

			GL gl = drawable.getGL();

			gl.glMatrixMode( GL.GL_MODELVIEW );
			gl.glLoadIdentity();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);

			/*
			AtributosTransparencia.desactivar(gl);
			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
			/*/
			data.apFondo.usar(gl);
			gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
			
			float s1 = 0.75f;
			float s2 = proporcionesPantalla * data.apFondo.getTextura().getProporciones() + s1;
			
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
			//*/
			
			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			glu.gluLookAt(
					orbitBehavior.eyePos.x, orbitBehavior.eyePos.y, orbitBehavior.eyePos.z,
					orbitBehavior.targetPos.x, orbitBehavior.targetPos.y, orbitBehavior.targetPos.z,
					orbitBehavior.upDir.x, orbitBehavior.upDir.y, orbitBehavior.upDir.z
			);
			ObjetosOrientables.loadModelViewMatrix();
			
			if(data.instancias[index].getModificador() != null)
				data.instancias[index].getModificador().modificar(incT);
			data.instancias[index].draw(gl);

			gl.glFlush();
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
		 */
		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, width, height);
			proporcionesPantalla = (float)width/height;
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.01;
			double far  = 100.0;
			double a1 = 45.0;			// angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);

			/*
			// formato zoom/recorte
			double ratio = (double) w / h;
			if( ratio > 1 )
				gl.glFrustum(-d, d, -d / ratio, d / ratio, near, far);
			else
				gl.glFrustum(-d * ratio, d * ratio, -d, d, near, far);
			//*/
			//*
			// formato panoramico Horizontal/Vertical centrado
			double ratio = (double) width / height;
			if( ratio < 1 )
				gl.glFrustum(-d, d, -d / ratio, d / ratio, near, far);
			else
				gl.glFrustum(-d * ratio, d * ratio, -d, d, near, far);
			//*/
			
			/*
			// formato recorte/panoramico derecha
			gl.glFrustum(-d,((2.0*w)/h -1.0)*d, -d, d, near, far);
			//*/
			
			ObjetosOrientables.loadProjectionMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
		 */
		@Override
		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent keyEvent) {
			int keyCode = keyEvent.getKeyCode();
			switch (keyCode) {
				case KeyEvent.VK_RIGHT:
					if(++index == data.instancias.length )
						index = 0;
					data.instancias[index].reset();
				break;
				case KeyEvent.VK_LEFT:
					if(--index < 0 )
						index = data.instancias.length -1;
					data.instancias[index].reset();
				break;
			}
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
	
	public static void main(String[] args){
	
		JFrame frame = new JFrame("Loader Example");
		
		Data data = new Data();
		ModificableBoolean loading = new ModificableBoolean(true);
		
		Animator animator = new Animator();
//		animator.setRunAsFastAsPossible(true);

		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GLCanvas canvasLoader = new GLCanvas(new GLCapabilities());
		canvasLoader.addGLEventListener(new Loader(data, loading));
		
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(canvasLoader);
		
		animator.add(canvasLoader);
		
		frame.setVisible(true);
		canvasLoader.setBounds( (frame.getContentPane().getWidth() - 400) / 2, frame.getContentPane().getHeight() - 40, 400, 20 );
		
		animator.start();
		
		if( loading.isTrue() ){
			synchronized( loading ){
				try{
					loading.wait();
				}catch( InterruptedException e ){
					e.printStackTrace();
				}
			}
		}
		
		GLCanvas canvas = new GLCanvas(null, null, data.context, null);

		OrbitBehavior orbitBehavior = new OrbitBehavior();
		orbitBehavior.eyePos.x = 0.0f;
		orbitBehavior.eyePos.y = 0.0f;
		orbitBehavior.eyePos.z = 4.0f;
		canvas.addMouseListener(orbitBehavior);
		canvas.addMouseMotionListener(orbitBehavior);

		Renderer renderer = new Renderer(data, orbitBehavior);
		canvas.addGLEventListener(renderer);
		canvas.addKeyListener(renderer);


		animator.add(canvas);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add( canvas, BorderLayout.CENTER );
		frame.validate();
		canvas.requestFocusInWindow();
		
		try {
			animator.remove(canvasLoader);
			// Ñapa para no quitar el canvas loader antes de que se muestren el resto :S
			Thread.sleep(100);
			frame.getContentPane().remove(canvasLoader);
		} catch (InterruptedException ignorada) {
		}
	}
}