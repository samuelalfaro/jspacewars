import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.*;
import org.sam.jogl.*;
import org.sam.jogl.particulas.*;
import org.sam.util.Imagen;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 */
@SuppressWarnings("serial")
public class Alpha3D
extends JFrame
implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	private GLU glu;
	private final GLCanvas canvas;
	private UnidadTextura utFondo;
	private Objeto3D forma1;
	private Objeto3D forma2;
	private Matrix4f mt;
	private Particulas explosion, humoNave1, humoNave2, humoNave3;
	private UnidadTextura utParticulas;
	
	public Alpha3D(){
		super("alpha3D");
		canvas = new GLCanvas(new GLCapabilities());
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);

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

	public static void main(String[] args){
		new Alpha3D().run();
	}

	float proporcionesFondo;

	public void init(GLAutoDrawable drawable){
		GL gl = drawable.getGL();
		glu = new GLU();
		gl.glClearColor(0.0f,0.25f,0.25f,0.0f);
		
		try {
			Image img = Imagen.cargarImagen("resources/obj3d/texturas/cielo512.jpg");
			proporcionesFondo = img.getHeight(null)/img.getWidth(null);
			utFondo = new UnidadTextura(); 
			utFondo.setTextura(new Textura(gl, Textura.RGB, img , true));
			utFondo.setAtributosTextura(new AtributosTextura());
			utFondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);
			
			forma1 = ObjLoader.loadToList("resources/obj3d/formas/nave03/forma.obj", ObjLoader.RESIZE);
			forma1.getApariencia().setTextura(
					new Textura(gl, Textura.RGB, Imagen.cargarImagen("resources/obj3d/formas/nave03/t01.jpg"), true)
			);
			forma1.getApariencia().setAtributosTextura(new AtributosTextura());
			forma1.getApariencia().getAtributosTextura().setPerspectiveCorrection(AtributosTextura.PerspectiveCorrection.NICEST);
			forma1.getApariencia().setGenCoordTextura(
					new GenCoordTextura(GenCoordTextura.Mode.REFLECTION_MAP,
							GenCoordTextura.Coordinates.TEXTURE_COORDINATE_3));

			forma2 = ObjLoader.loadToList("resources/obj3d/formas/nave02/forma.obj", ObjLoader.RESIZE);
			Material material = new Material();
			material.setAmbient(0.0f, 0.0f, 0.0f, 1.0f);
			material.setDifuse(1.0f, 1.0f, 1.0f, 1.0f);
			material.setSpecular(1.0f, 1.0f, 1.0f,1.0f);
			material.setEmission(0.5f, 0.25f, 0.25f, 1.0f);
			material.setShininess(16.0f);
			forma2.getApariencia().setMaterial(material);
			forma2.getApariencia().setTextura(
					new Textura(gl, Textura.RGB, Imagen.cargarImagen("resources/obj3d/formas/nave02/t01.jpg"), true)
			);
			
			utParticulas = new UnidadTextura();  
//			img = Imagen.cargarImagen("resources/obj3d/texturas/smoke_particle.jpg");
			img =  Imagen.cargarImagen("resources/obj3d/texturas/flecha.jpg");
			utParticulas.setTextura(new Textura(gl, Textura.ALPHA, img, true));
			utParticulas.getTextura().setWrap_s(Textura.CLAMP_TO_BORDER);
			utParticulas.getTextura().setWrap_t(Textura.CLAMP_TO_BORDER);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ObjLoader.ParsingErrorException e) {
			e.printStackTrace();
		}
		
		explosion = FactoriaDeParticulas.createParticulas(30);
		explosion.setEmision(Particulas.Emision.PULSOS);
		explosion.setRangoDeEmision(0.2f);
		explosion.setEmisor(new Emisor.Puntual());
		explosion.setRadio(0.5f);
		explosion.setVelocidad(0.25f,0.05f,false);
		//explosion.setFuerza(0.0f,-0.098f,0.0f);
		explosion.setTiempoVida(3.0f);
		explosion.setVelocidadGiro(360.0f,90.0f,true);
		explosion.setColor(0.2f, 0.15f, 0.1f, 0.99f);
		explosion.iniciar();
		
		mt = new Matrix4f();
		
		XStream xStream = new XStream(new DomDriver());
		XStreamConventers.registerConverters(xStream);
		try {
			humoNave1 = (Particulas)xStream.fromXML(new FileReader("Particulas.xml"));
			humoNave2 = humoNave1.clone();
			humoNave3 = humoNave1.clone();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Matrix4f t3d = new Matrix4f();
		t3d.rotY((float)Math.PI/2);
		t3d.setTranslation(new Vector3f( 0.12f, -0.15f, -0.5f));
		humoNave1.setTLocal(t3d);
		humoNave1.setTExterna(mt);
		
		
		t3d = new Matrix4f();
		t3d.rotY((float)Math.PI/2);
		t3d.setTranslation(new Vector3f(-0.12f, -0.15f, -0.5f));
		humoNave2.setTLocal(t3d);
		humoNave2.setTExterna(mt);
		

		t3d = new Matrix4f();
		t3d.rotY((float)Math.PI/2);
		t3d.setTranslation(new Vector3f( 0.0f, 0.07f, -0.5f ));
		humoNave3.setTLocal(t3d);
		humoNave3.setTExterna(mt);
		
		humoNave1.iniciar();
		humoNave2.iniciar();
		humoNave3.iniciar();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,
			new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
	}

	public void display(GLAutoDrawable drawable){
		GL gl = drawable.getGL();

		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);

		utFondo.activar(gl,0);

		float s1 = 0.75f;
		float s2 = proporcionesPantalla*proporcionesFondo + s1;
		gl.glDepthMask(false);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(s1,0);
			gl.glVertex3f(0,0,-0.9f);
			gl.glTexCoord2f(s1,1);
			gl.glVertex3f(0,1,-0.9f);
			gl.glTexCoord2f(s2,1);
			gl.glVertex3f(proporcionesPantalla,1,-0.9f);
			gl.glTexCoord2f(s2,0);
			gl.glVertex3f(proporcionesPantalla,0,-0.9f);
		gl.glEnd();
		gl.glDepthMask(true);
		
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		glu.gluLookAt(
				3 * Math.sin(-g_fSpinX), 3.0 * Math.sin(-g_fSpinY), 3.0	* Math.cos(-g_fSpinY) * Math.cos(-g_fSpinX),
				0.0, 0.0, 0.0,
				Math.sin(-g_fSpinX), Math.cos(-g_fSpinY), -Math.sin(-g_fSpinY) * Math.cos(-g_fSpinX));
//		gl.glRotatef( -g_fSpinY, 1.0f, 0.0f, 0.0f );
//		gl.glRotatef( -g_fSpinX, 0.0f, 1.0f, 0.0f );
		
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);

		//gl.glEnable(GL.GL_NORMALIZE);
		//gl.glScalef(3.5f, 3.5f, 3.5f);
		gl.glPushMatrix();
		gl.glTranslatef(-0.25f, 0.0f, -1.0f);
		forma1.draw(gl);
		gl.glPopMatrix();

//		gl.glEnable(GL.GL_BLEND);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
//		gl.glDepthMask(false);
		
		//gl.glScalef(2.5f, 2.5f, 2.5f);
		gl.glPushMatrix();
		
		mt.setIdentity();
		mt.rotZ(1);
		mt.setTranslation(new Vector3f(0.25f, 0.5f, -1.0f));
		float mt_array[] = new float[]{
			mt.m00, mt.m10, mt.m20, mt.m30,
			mt.m01, mt.m11, mt.m21, mt.m31,
			mt.m02, mt.m12, mt.m22, mt.m32,
			mt.m03, mt.m13, mt.m23, mt.m33
		};
		gl.glMultMatrixf(mt_array, 0);
		forma2.draw(gl);
		gl.glPopMatrix();

//		gl.glDisable(GL.GL_BLEND);
//		gl.glDepthMask(true);
		//gl.glDisable(GL.GL_NORMALIZE);
		
		gl.glDisable(GL.GL_LIGHT0);
		gl.glDisable(GL.GL_LIGHTING);

		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
//		gl.glBlendEquation(GL.GL_FUNC_ADD);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		humoNave1.getModificador().modificar(0.1f);
		humoNave2.getModificador().modificar(0.1f);
		humoNave3.getModificador().modificar(0.1f);
		explosion.getModificador().modificar(0.1f);

		utParticulas.activar(gl,0);

		humoNave1.draw(gl);
		humoNave2.draw(gl);
		humoNave3.draw(gl);
		explosion.draw(gl);

		gl.glDepthMask(true);
		gl.glDisable(GL.GL_BLEND);
		
		gl.glFlush();
	}

	float proporcionesPantalla;
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
		gl.glFrustum(-d,((2.0*w)/h -1.0)*d, -d, d, near, far);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged){
	}

	public void keyTyped(KeyEvent key){
	}

	public void keyPressed(KeyEvent key){
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		default:
			break;
		}
	}

	public void keyReleased(KeyEvent key){
	}

	Point ptCurrentMousePosit, ptLastMousePosit;
	float g_fSpinX=0.0f;
	float g_fSpinY=0.0f;

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		ptLastMousePosit= e.getPoint();
	}

	public void mouseReleased(MouseEvent e) {}
	
	public void mouseDragged(MouseEvent e) {
		ptCurrentMousePosit = e.getPoint();
		
		g_fSpinX -= (ptCurrentMousePosit.x - ptLastMousePosit.x)/100.f;
		g_fSpinY -= (ptCurrentMousePosit.y - ptLastMousePosit.y)/100.f;

		ptLastMousePosit = ptCurrentMousePosit;
	}

	public void mouseMoved(MouseEvent e) {}
}