import java.awt.Point;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.jogl.*;
import org.sam.jogl.particulas.*;
import org.sam.util.Imagen;

/**
 */
@SuppressWarnings("serial")
public class PruebaParticulas
extends JFrame
implements GLEventListener, KeyListener, MouseListener, MouseMotionListener{
	private final GLCanvas canvas;
	private GLU glu;
	private Particulas explosion, humo, fuente;
	private UnidadTextura fondo, texture1, texture12, texture2;

	public PruebaParticulas(){
		super("Prueba Particulas");
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
		new PruebaParticulas().run();
	}

	public void init(GLAutoDrawable drawable){
		glu = new GLU();
		GL gl = drawable.getGL();
		gl.glClearColor(1.0f,1.0f,1.0f,0.0f);
		
		try {
			fondo = new UnidadTextura(); 
			fondo.setTextura(
					new Textura(gl, Textura.RGB,Imagen.cargarImagen("resources/fondo.png"), true)
			);
			fondo.setAtributosTextura(new AtributosTextura());
			fondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);
			
			texture1 = new UnidadTextura(); 
			texture1.setTextura(
					new Textura(gl, Textura.ALPHA,
							Imagen.cargarImagen("resources/obj3d/texturas/smoke_particle.jpg"), true)
			);
			texture1.getTextura().setWrap_s(Textura.CLAMP_TO_BORDER);
			texture1.getTextura().setWrap_t(Textura.CLAMP_TO_BORDER);
			texture1.setAtributosTextura(new AtributosTextura());
			
			texture12 = new UnidadTextura();
			texture12.setTextura(texture1.getTextura());
			texture12.setAtributosTextura(new AtributosTextura());
			texture12.getAtributosTextura().setEnvColor(1.0f, 1.0f, 1.0f, 1.0f);
			texture12.getAtributosTextura().setMode(AtributosTextura.Mode.COMBINE);
			texture12.getAtributosTextura().setCombineRgbMode(AtributosTextura.CombineMode.INTERPOLATE);
			// C' = S0 * S2 + S1*(1-S2)
			texture12.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR);
			texture12.getAtributosTextura().setCombineRgbSource1(
					AtributosTextura.CombineSrc.CONSTANT, AtributosTextura.CombineOperand.SRC_COLOR);
			texture12.getAtributosTextura().setCombineRgbSource2(
					AtributosTextura.CombineSrc.PREVIOUS, AtributosTextura.CombineOperand.SRC_ALPHA);
			
			texture2 = new UnidadTextura(); 
			texture2.setTextura(
					new Textura(gl, Textura.ALPHA,
							Imagen.cargarImagen("resources/obj3d/texturas/flecha.jpg"), true)
			);
			texture2.getTextura().setWrap_s(Textura.CLAMP_TO_BORDER);
			texture2.getTextura().setWrap_t(Textura.CLAMP_TO_BORDER);
			texture2.setAtributosTextura(new AtributosTextura());
		} catch (GLException e) {
			e.printStackTrace();
		}
		
		explosion = FactoriaDeParticulas.createParticulas(60);
		explosion.setEmision(Particulas.Emision.CONTINUA);
		explosion.setRangoDeEmision(0.0f);
		explosion.setEmisor(new Emisor.Puntual());
		explosion.setVelocidad(0.25f,0.05f,false);
		explosion.setTiempoVida(2.0f);
		explosion.setVelocidadGiro(36.0f,9.0f,true);
		explosion.setColor(0.3f, 0.2f, 0.15f, 0.9f);
		explosion.setRadio(0.15f);
		explosion.iniciar();
		
		humo = FactoriaDeParticulas.createParticulas(30);
		humo.setEmision(Particulas.Emision.CONTINUA);
		humo.setRangoDeEmision(0.25f);
		humo.setEmisor(new Emisor.Cache(new Emisor.Puntual(),512));
		humo.setVelocidad(0.05f,0.01f,false);
		humo.setTiempoVida(10.0f);
		humo.setVelocidadGiro(36.0f,9.0f,true);
//		humo.setColor(0.15f, 0.25f, 0.3f, 0.5f);
		humo.setColor(0.85f, 0.75f, 0.7f, 0.9f);
		humo.setRadio(0.25f);
		humo.iniciar();
		
		fuente = FactoriaDeParticulas.createParticulas(1000);
		fuente.setEmision(Particulas.Emision.CONTINUA);
		Matrix4f tLocal = new Matrix4f();
		tLocal.rotZ((float)Math.PI/2);
		tLocal.setTranslation(new Vector3f(0.5f,-0.15f,0.0f));
		fuente.setTLocal(tLocal);
		fuente.setRangoDeEmision(1.0f);
		fuente.setEmisor(new Emisor.Conico(0.02f,0.1f));
		fuente.setRadio(0.0125f);
		fuente.setSemiEje(0.025f);
		fuente.setColor(0.1f,0.3f,0.5f,1.0f);
		fuente.setVelocidad(0.3f);
		fuente.setFuerza(0.0f, -0.098f, 0.0f);
		fuente.setTiempoVida(7.0f);
		fuente.iniciar();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
	}

	public void display(GLAutoDrawable drawable){

		GL gl = drawable.getGL();
		
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0.0, 1.0, 0.0, 1.0, 0, 1);

		fondo.activar(gl,0);
		gl.glDepthMask(false);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0,0);
			gl.glVertex3f(0,0,0);
			gl.glTexCoord2f(0,1);
			gl.glVertex3f(0,1,0);
			gl.glTexCoord2f(1,1);
			gl.glVertex3f(1,1,0);
			gl.glTexCoord2f(1,0);
			gl.glVertex3f(1,0,0);
		gl.glEnd();
		gl.glDepthMask(true);
		
		gl.glPopMatrix();
		
		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		glu.gluLookAt(3*Math.sin(-g_fSpinX), 3*Math.sin(-g_fSpinY), 3*Math.cos(-g_fSpinY)*Math.cos(-g_fSpinX), 0.0, 0.0, 0.0, Math.sin(-g_fSpinX), Math.cos(-g_fSpinY), -Math.sin(-g_fSpinY)*Math.cos(-g_fSpinX));

		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		
		texture1.activar(gl,0);
		texture12.activar(gl,1);
		
//		gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
		gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_COLOR);
		
		humo.getModificador().modificar(0.1f);
		humo.draw(gl);
		
		UnidadTextura.desactivar(gl,1);
		//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		//gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_COLOR);		
		//gl.glBlendFunc(GL.GL_ZERO,GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		explosion.getModificador().modificar(0.1f);
		explosion.draw(gl);
		
        texture2.activar(gl,0);
//		gl.glBlendEquation(GL.GL_FUNC_ADD);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		fuente.getModificador().modificar(0.1f);
		fuente.draw(gl);
		
		gl.glDepthMask(true);
		gl.glDisable(GL.GL_BLEND);
		gl.glFlush();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
		if(w!= 0 && h!= 0 ){
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, w, h);

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.001;
			double far  = 30.0;
			double a1 = 30.0;			// angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt( (1 / Math.pow(Math.sin(a2), 2) )-1);

			double ratio = (double)w/h;
			if(ratio>1)
				gl.glFrustum(-d, d, -d/ratio, d/ratio, near, far);
			else
				gl.glFrustum(-d*ratio, d*ratio, -d, d, near, far);

			gl.glMatrixMode(GL.GL_MODELVIEW);
		}
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