package pruebas.jogl;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.LinkedList;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import org.sam.jogl.*;
import org.sam.util.Imagen;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class LoaderExample
		extends JFrame 
		implements GLEventListener{
	
	private GLU glu;
	private final GLCanvas canvas;
	private final OrbitBehavior orbitBehavior;
	
	private Apariencia utFondo;
	private transient int index = 0;
	private Instancia3D[] instancias;
	
    public LoaderExample(){
		super("Loader Example");
		canvas = new GLCanvas(new GLCapabilities());
		canvas.addGLEventListener(this);
		orbitBehavior = new OrbitBehavior();
		orbitBehavior.eyePos.x = 0.0f;
		orbitBehavior.eyePos.y = 0.0f;
		orbitBehavior.eyePos.z = 4.0f;
		canvas.addMouseListener(orbitBehavior);
		canvas.addMouseMotionListener(orbitBehavior);;

		getContentPane().add(canvas);
	}

	public void run(){
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.requestFocusInWindow();
		canvas.getContext().getGL();

		setVisible(true);
		new Thread(){
            @Override
			public void run(){
				while(true){
					canvas.display();
					try{
						Thread.sleep(5);
					}catch( InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private transient float proporcionesFondo, proporcionesPantalla;
	private transient long tAnterior, tActual;
	
	public void init(GLAutoDrawable drawable){
		GL gl = drawable.getGL();
		glu = new GLU();
		gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
		
		try {
			BufferedImage img = Imagen.cargarToBufferedImage("resources/texturas/cielo512.jpg");
			proporcionesFondo = img.getHeight(null)/img.getWidth(null);
			utFondo = new Apariencia();
			
			utFondo.setTextura(new Textura(gl, Textura.Format.RGB, img , true));
			utFondo.setAtributosTextura(new AtributosTextura());
			utFondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);
			
			XStream xStream = new XStream(new DomDriver());
			xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
			GrafoEscenaConverters.register(xStream);
			GrafoEscenaConverters.setReusingReferenceByXPathMarshallingStrategy(xStream);
			
			Collection<Instancia3D> intanciasCollection = new LinkedList<Instancia3D>();
			ObjectInputStream in = xStream.createObjectInputStream(new FileReader("instancias3D-stream.xml"));
			try{
				while( true )
					intanciasCollection.add((Instancia3D) in.readObject());
			}catch( ClassNotFoundException e ){
				e.printStackTrace();
			}catch( EOFException eof ){
				in.close();
			}
			instancias = intanciasCollection.toArray(new Instancia3D[intanciasCollection.size()]);
			
			StringWriter output = new StringWriter();
			try{
				ObjectOutputStream out = xStream.createObjectOutputStream(output);
				for(Instancia3D instancia: instancias)
					out.writeObject(instancia);
				out.close();
			}catch( IOException e ){
				e.printStackTrace();
			}
			System.out.println(output);
			
			canvas.addKeyListener(new KeyListener(){
				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent keyEvent) {
					int keyCode = keyEvent.getKeyCode();
					switch (keyCode) {
						case KeyEvent.VK_RIGHT:
							if(++index == instancias.length )
								index = 0;
							instancias[index].reset();
						break;
						case KeyEvent.VK_LEFT:
							if(--index < 0 )
								index = instancias.length -1;
							instancias[index].reset();
						break;
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {
				}
			});
			
			instancias[index].reset();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch( IOException e ){
			e.printStackTrace();
		} 
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		
		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,
			new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
		gl.glEnable(GL.GL_CULL_FACE);
		tActual = System.nanoTime();
	}

	public void display(GLAutoDrawable drawable){
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

		utFondo.usar(gl);
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
		
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		glu.gluLookAt(
				orbitBehavior.eyePos.x, orbitBehavior.eyePos.y, orbitBehavior.eyePos.z,
				orbitBehavior.targetPos.x, orbitBehavior.targetPos.y, orbitBehavior.targetPos.z,
				orbitBehavior.upDir.x, orbitBehavior.upDir.y, orbitBehavior.upDir.z
		);
		ObjetosOrientables.loadModelViewMatrix();
		
//		for(Modificador modificador: gestorDeParticulas)
//			modificador.modificar(incT);
//		
//		for(Instancia3D instancia: instancias)
//			instancia.draw(gl);
		if(instancias[index].getModificador() != null)
			instancias[index].getModificador().modificar(incT);
		instancias[index].draw(gl);

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
		double ratio = (double) w / h;
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

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged){
	}

	public static void main(String[] args){
		new LoaderExample().run();
	}
}