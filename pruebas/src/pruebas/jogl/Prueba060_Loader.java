package pruebas.jogl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import org.sam.colisiones.Poligono;
import org.sam.jogl.Apariencia;
import org.sam.jogl.Objeto3D;
import org.sam.jogl.MatrixSingleton;
import org.sam.jogl.OglList;
import org.sam.jspacewars.serialization.ElementosConverters;
import org.sam.jspacewars.servidor.elementos.Elemento;
import org.sam.util.ModificableBoolean;
import org.sam.util.Reflexion;

import com.sun.opengl.util.Animator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Prueba060_Loader {
	
	private static Poligono getPoligono(Elemento e){
		java.lang.reflect.Field f = Reflexion.findField(Elemento.class, "forma");
		if (f == null)
			return null;
		try {
			boolean accesible = f.isAccessible();
			f.setAccessible(true);
			Poligono p = (Poligono)f.get(e);
			f.setAccessible(accesible);
			return p;
		} catch (IllegalAccessException ignorada) {
			return null;
		}
	}
	
	private static float[] getArray(Poligono p, String name){
		java.lang.reflect.Field f = Reflexion.findField(Poligono.class, name);
		try {
			boolean accesible = f.isAccessible();
			f.setAccessible(true);
			float[] array = (float[])f.get(p);
			f.setAccessible(accesible);
			return array;
		} catch (IllegalAccessException ignorada) {
			return null;
		}
	}
	
	private static Objeto3D from(GL gl, Elemento e){
		Poligono p = getPoligono(e);
		if(p == null)
			return null;
		
		int nLados = p.getNLados();
		float coordX[] = getArray(p, "coordX");
		float coordY[] = getArray(p, "coordY");
		
		OglList oglList = new OglList(gl);
		gl.glBegin(GL.GL_LINES);
			for(int i=0; i< nLados-1;){
				gl.glVertex3f(coordX[i],coordY[i],0);
				i++;
				gl.glVertex3f(coordX[i],coordY[i],0);
			}
			gl.glVertex3f(coordX[nLados-1],coordY[nLados-1],0);
			gl.glVertex3f(coordX[0],coordY[0],0);
		gl.glEnd();
		OglList.endList(gl);
		
		return new Objeto3D( oglList, new Apariencia());
	}

	static class Renderer implements GLEventListener, KeyListener {

		private final Loader.Data data;
		private final List<Elemento> elementos;
		private Objeto3D[] siluetas;
		private final OrbitBehavior orbitBehavior;
		private transient int index = 0;
		private boolean mostrarForma;
		private boolean mostrarSilueta;

		Renderer(Loader.Data data, List<Elemento> elementos, OrbitBehavior orbitBehavior) {
			this.data = data;
			this.elementos = elementos;
			this.orbitBehavior = orbitBehavior;
			this.mostrarForma = true;
			this.mostrarSilueta = false;
		}

		private transient float proporcionesPantalla;
		private transient long tAnterior, tActual;
		private GLU glu;

		private transient boolean iniciado = false;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable
		 * )
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
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] { 0.0f,
					0.0f, 10.0f, 1.0f }, 0);
			gl.glEnable(GL.GL_CULL_FACE);
			
			if(elementos != null){
				siluetas = new Objeto3D[elementos.size()];
				for(int i = 0; i < elementos.size(); i++)
					siluetas[i] = from(gl, elementos.get(i));
				elementos.clear();
			}

			data.instancias[index].reset();

			tActual = System.nanoTime();
			iniciado = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.media.opengl.GLEventListener#display(javax.media.opengl.
		 * GLAutoDrawable)
		 */
		@Override
		public void display(GLAutoDrawable drawable) {
			if (!iniciado)
				init(drawable);
			tAnterior = tActual;
			tActual = System.nanoTime();
			float incT = (tActual - tAnterior) / 1000000000.0f;

			GL gl = drawable.getGL();

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);

			/*
			 * AtributosTransparencia.desactivar(gl); gl.glClear(
			 * GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT ); /
			 */
			data.apFondo.usar(gl);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

			float s1 = 0.75f;
			float s2 = proporcionesPantalla
					* data.apFondo.getTextura().getProporciones() + s1;

			gl.glDepthMask(false);
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(s1, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glTexCoord2f(s2, 0);
			gl.glVertex3f(proporcionesPantalla, 0, 0);
			gl.glTexCoord2f(s2, 1);
			gl.glVertex3f(proporcionesPantalla, 1, 0);
			gl.glTexCoord2f(s1, 1);
			gl.glVertex3f(0, 1, 0);
			gl.glEnd();
			gl.glDepthMask(true);
			// */

			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			orbitBehavior.setLookAt(glu);
			MatrixSingleton.loadModelViewMatrix();

			if(mostrarForma){
				if (data.instancias[index].getModificador() != null)
					data.instancias[index].getModificador().modificar(incT);
				data.instancias[index].draw(gl);
			}
			if(mostrarSilueta && siluetas!= null && index < siluetas.length && siluetas[index] != null)
				siluetas[index].draw(gl);
			gl.glFlush();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.media.opengl.GLEventListener#reshape(javax.media.opengl.
		 * GLAutoDrawable, int, int, int, int)
		 */
		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width,
				int height) {
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, width, height);
			proporcionesPantalla = (float) width / height;
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.01;
			double far = 100.0;
			double a1 = 45.0; // angulo en grados
			double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
			double d = near / Math.sqrt((1 / Math.pow(Math.sin(a2), 2)) - 1);

			/*
			 * // formato zoom/recorte double ratio = (double) w / h; if( ratio
			 * > 1 ) gl.glFrustum(-d, d, -d / ratio, d / ratio, near, far); else
			 * gl.glFrustum(-d * ratio, d * ratio, -d, d, near, far); //
			 */
			// *
			// formato panoramico Horizontal/Vertical centrado
			double ratio = (double) width / height;
			if (ratio < 1)
				gl.glFrustum(-d, d, -d / ratio, d / ratio, near, far);
			else
				gl.glFrustum(-d * ratio, d * ratio, -d, d, near, far);
			// */

			/*
			 * // formato recorte/panoramico derecha gl.glFrustum(-d,((2.0*w)/h
			 * -1.0)*d, -d, d, near, far); //
			 */

			MatrixSingleton.loadProjectionMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl
		 * .GLAutoDrawable, boolean, boolean)
		 */
		@Override
		public void displayChanged(GLAutoDrawable drawable,
				boolean modeChanged, boolean deviceChanged) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent keyEvent) {
			int keyCode = keyEvent.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_RIGHT:
				if (++index == data.instancias.length)
					index = 0;
				data.instancias[index].reset();
				break;
			case KeyEvent.VK_LEFT:
				if (--index < 0)
					index = data.instancias.length - 1;
				data.instancias[index].reset();
				break;
			case KeyEvent.VK_F:
				mostrarForma = !mostrarForma;
				if(!mostrarForma)
					mostrarSilueta = true;
				break;
			case KeyEvent.VK_S:
				mostrarSilueta = !mostrarSilueta;
				if(!mostrarSilueta)
					mostrarForma = true;
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
	
	private static List<Elemento> loadElementos(String path){
		XStream xStream = new XStream(new DomDriver());
		ElementosConverters.register(xStream);
		
		List<Elemento> l = new ArrayList<Elemento>();
		try{
			FileReader fr = new FileReader(path);
			ObjectInputStream in = xStream.createObjectInputStream(fr);
			try {
				while (true) {
					Object o = in.readObject();
					if (o != null)
						l.add((Elemento) o);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (EOFException eof) {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l;
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame("Prueba Loader");
		List<Elemento> elementos = loadElementos("resources/elementos-instancias3D-stream-sh.xml");

		frame.setSize(500, 500);
		frame.setBackground(Color.BLACK);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GLCanvas canvasLoader = new GLCanvas(new GLCapabilities());
		Loader.Data data = new  Loader.Data();
		ModificableBoolean loading = new ModificableBoolean(true);
		canvasLoader.addGLEventListener(new Loader(data, loading));

		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().add(canvasLoader);

		Animator animator = new Animator();
		// animator.setRunAsFastAsPossible(true);
		animator.add(canvasLoader);

		frame.setVisible(true);
		canvasLoader.setBounds((frame.getContentPane().getWidth() - 400) / 2,
				frame.getContentPane().getHeight() - 40, 400, 20);

		animator.start();

		if (loading.isTrue()) {
			synchronized (loading) {
				try {
					loading.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		GLCanvas canvas = new GLCanvas(null, null, data.context, null);

		OrbitBehavior orbitBehavior = new OrbitBehavior();
		orbitBehavior.setEyePos(0.0f, 0.0f, 4.0f);
		orbitBehavior.addMouseListeners(canvas);

		Renderer renderer = new Renderer(data, elementos, orbitBehavior);
		canvas.addGLEventListener(renderer);
		canvas.addKeyListener(renderer);

		animator.add(canvas);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.validate();
	
		animator.remove(canvasLoader);
		// Se muestra por lo menos una vez el canvas antes de quitar el canvasLoader para que no
		// se liberen las texturas de memoria, al eliminarlo.
		canvas.display();
		frame.getContentPane().remove(canvasLoader);
		// Se añade despues el canvas al animator, para evitar dos llamadas simultaneas al
		// metodo display()
		animator.add(canvas);
		canvas.requestFocusInWindow();
	}
}