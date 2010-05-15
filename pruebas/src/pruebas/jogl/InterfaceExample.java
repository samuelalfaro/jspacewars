package pruebas.jogl;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import org.sam.jogl.GrafoEscenaConverters;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.ObjLoader;
import org.sam.jogl.ObjetosOrientables;
import org.sam.jogl.Textura;
import org.sam.jspacewars.cliente.MarcoDeIndicadores;
import org.sam.util.Imagen;
import org.sam.util.ModificableBoolean;

import com.sun.opengl.util.Animator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class InterfaceExample{
	
	private static class Data{
		GLContext context;
		Apariencia apFondo;
		MarcoDeIndicadores marco;
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
			}else if(data.marco == null ){
				data.marco = MarcoDeIndicadores.getMarco(0);
				data.marco.loadTexturas(gl);
			}else if( !data.marco.isLoadComplete() ){
				data.marco.loadTexturas(gl);
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
		private transient float proporcionesArea;
		
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

			data.apFondo.usar(gl);
			gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
			
			float s1 = 0.75f;
			float s2 = proporcionesArea * data.apFondo.getTextura().getProporciones() + s1;
			
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
			glu.gluLookAt(
					orbitBehavior.eyePos.x, orbitBehavior.eyePos.y, orbitBehavior.eyePos.z,
					orbitBehavior.targetPos.x, orbitBehavior.targetPos.y, orbitBehavior.targetPos.z,
					orbitBehavior.upDir.x, orbitBehavior.upDir.y, orbitBehavior.upDir.z
			);
			ObjetosOrientables.loadModelViewMatrix();
			
			if(data.instancias[index].getModificador() != null)
				data.instancias[index].getModificador().modificar(incT);
			data.instancias[index].draw(gl);

			data.marco.draw(gl);
			
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
			data.marco.setBounds( 0, 0, width, height);
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
	
		JFrame frame = new JFrame("Interface Example");
		
		Data data = new Data();
		ModificableBoolean loading = new ModificableBoolean(true);
		
		Animator animator = new Animator();
//		animator.setRunAsFastAsPossible(true);

		//*
		frame.getContentPane().setPreferredSize( new Dimension (800, 600));
		frame.pack();
		/*/
		frame.setSize(800, 600);
		//*/
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

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add( canvas, BorderLayout.CENTER );
		frame.validate();
		canvas.requestFocusInWindow();
		
		animator.remove(canvasLoader);
		// Se muestra por lo menos una vez el canvas antes de quitar el canvasLoader para que no
		// se liberen las texturas de memoria, al eliminarlo.
		canvas.display();
		frame.getContentPane().remove(canvasLoader);
		// Se añade despues el canvas al animator, para evitar dos llamadas simultaneas al
		// metodo display()
		animator.add(canvas);
	}
}