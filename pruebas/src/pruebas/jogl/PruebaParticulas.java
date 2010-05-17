package pruebas.jogl;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.jogl.*;
import org.sam.jogl.Textura.MagFilter;
import org.sam.jogl.Textura.MinFilter;
import org.sam.jogl.particulas.*;
import org.sam.util.Imagen;

/**
 */
@SuppressWarnings("serial")
public class PruebaParticulas extends JFrame implements GLEventListener{
	private final GLCanvas canvas;
	private GLU glu;
	private final OrbitBehavior orbitBehavior;
	private Particulas explosion, humo, fuente;
	private UnidadTextura fondo, texture1, texture12, texture2;
	private NodoTransformador fuenteTransformada;

	public PruebaParticulas() {
		super("Prueba Particulas");
		canvas = new GLCanvas(new GLCapabilities());
		canvas.addGLEventListener(this);
		orbitBehavior = new OrbitBehavior();
		orbitBehavior.eyePos.x = 1;
		orbitBehavior.eyePos.y = 0;
		orbitBehavior.eyePos.z = 3;
		orbitBehavior.targetPos.x = 1;
		orbitBehavior.targetPos.y = 0;
		orbitBehavior.targetPos.z = 0;
		canvas.addMouseListener(orbitBehavior);
		canvas.addMouseMotionListener(orbitBehavior);

		getContentPane().add(canvas);
	}

	public void init(GLAutoDrawable drawable) {
		glu = new GLU();
		GL gl = drawable.getGL();
//		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		try{
			fondo = new UnidadTextura();
			fondo.setTextura(new Textura(gl, MinFilter.NEAREST, MagFilter.NEAREST, Textura.Format.RGB, Imagen.cargarToBufferedImage("resources/fondo.png"), true));
			fondo.setAtributosTextura(new AtributosTextura());
			fondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);

			texture1 = new UnidadTextura();
			texture1.setTextura(new Textura(gl, Textura.Format.ALPHA, Imagen
					.cargarToBufferedImage("resources/texturas/smok.png"), true));
			texture1.getTextura().setWrap_s(Textura.Wrap.CLAMP_TO_BORDER);
			texture1.getTextura().setWrap_t(Textura.Wrap.CLAMP_TO_BORDER);

			texture12 = new UnidadTextura();
			texture12.setTextura(texture1.getTextura());
			texture12.setAtributosTextura(new AtributosTextura());
			texture12.getAtributosTextura().setEnvColor(1.0f, 1.0f, 1.0f, 1.0f);
			texture12.getAtributosTextura().setMode(AtributosTextura.Mode.COMBINE);
			texture12.getAtributosTextura().setCombineRgbMode(AtributosTextura.CombineMode.INTERPOLATE);
			// C' = S0 * S2 + S1*(1-S2)
			texture12.getAtributosTextura().setCombineRgbSource0(AtributosTextura.CombineSrc.OBJECT,
					AtributosTextura.CombineOperand.SRC_COLOR);
			texture12.getAtributosTextura().setCombineRgbSource1(AtributosTextura.CombineSrc.CONSTANT,
					AtributosTextura.CombineOperand.SRC_COLOR);
			texture12.getAtributosTextura().setCombineRgbSource2(AtributosTextura.CombineSrc.PREVIOUS,
					AtributosTextura.CombineOperand.SRC_ALPHA);

			texture2 = new UnidadTextura();
			texture2.setTextura(new Textura(gl, Textura.Format.ALPHA, Imagen
					.cargarToBufferedImage("resources/texturas/flecha.jpg"), true));
			texture2.getTextura().setWrap_s(Textura.Wrap.CLAMP_TO_BORDER);
			texture2.getTextura().setWrap_t(Textura.Wrap.CLAMP_TO_BORDER);
		}catch( GLException e ){
			e.printStackTrace();
		}

		//TODO fixme
//		FactoriaDeParticulas.setPointSpritesEnabled(true);
		
		explosion = FactoriaDeParticulas.createParticulas(60);
		explosion.setEmision(Particulas.Emision.CONTINUA);
		explosion.setRangoDeEmision(0.0f);
		explosion.setEmisor(new Emisor.Puntual());
		explosion.setVelocidad(1.0f, 0.2f, false);
		explosion.setTiempoVida(0.5f);
		explosion.setGiroInicial(0, 180, true);
		explosion.setVelocidadGiro(180.0f, 90.0f, true);
		explosion.setColor(0.3f, 0.2f, 0.15f, 0.9f);
		
//		explosion.setRadio(0.15f);
		explosion.setRadio(FactoriaDeParticulas.isPointSpritesEnabled()?64f:0.15f); //TODO Fix
		explosion.reset();

		humo = FactoriaDeParticulas.createParticulas(30);
		humo.setEmision(Particulas.Emision.CONTINUA);
		humo.setRangoDeEmision(0.25f);
		humo.setEmisor(new Emisor.Cache(new Emisor.Puntual(), 512));
		humo.setVelocidad(0.2f, 0.04f, false);
		humo.setTiempoVida(2.5f);
		humo.setGiroInicial(0, 180, true);
		humo.setVelocidadGiro(30.0f, 15.0f, true);
		humo.setColor(
				0.5f, 0.45f, 0.35f,
				GettersFactory.Float.create(
						new float[]{0.0f,0.25f,0.75f,1.0f},
						new float[]{0.0f,1.0f,1.0f,0.0f},
						MetodoDeInterpolacion.Predefinido.COSENOIDAL
				)
		);
//		humo.setRadio(0.25f);
		humo.setRadio(FactoriaDeParticulas.isPointSpritesEnabled()?64f:0.25f);// TODO fix
		humo.reset();

//		FactoriaDeParticulas.setPointSpritesEnabled(false);
		fuente = FactoriaDeParticulas.createParticulas(1000);
		fuente.setEmision(Particulas.Emision.CONTINUA);

		fuente.setRangoDeEmision(1.0f);
		fuente.setEmisor(new Emisor.Conico(0.02f, 0.1f));
//		fuente.setRadio(0.0125f);
		fuente.setRadio(FactoriaDeParticulas.isPointSpritesEnabled()?64f:0.0125f);// TODO fix
		if(!FactoriaDeParticulas.isPointSpritesEnabled())
			fuente.setSemiEje(0.025f);
//		fuente.setColor(0.1f, 0.3f, 0.5f, 1.0f);
		fuente.setColor(
				GettersFactory.Float.create(
						new float[]{0.0f,0.25f,0.75f,1.0f},
						new float[][]{
								{0.1f, 0.3f, 0.5f},
								{0.5f, 0.1f, 0.3f},
								{0.3f, 0.5f, 0.1f},
								{0.1f, 0.3f, 0.5f}
						},
						MetodoDeInterpolacion.Predefinido.COSENOIDAL
				),
				1.0f
		);
		fuente.setVelocidad(0.6f);
		fuente.setFuerza( 0.0f, -0.392f, 0.0f);
		fuente.setTiempoVida(3.5f);
		fuente.reset();
		
		Matrix4f tLocal = new Matrix4f();
		tLocal.rotZ((float) Math.PI / 2);
		tLocal.setTranslation(new Vector3f(1.0f, 0.0f, 0.0f));
		fuenteTransformada = new NodoTransformador(tLocal, fuente);
		
//		FactoriaDeParticulas.setPointSpritesEnabled(true);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
//		gl.glEnable(GL.GL_CULL_FACE);
	}

	private transient long tAnterior, tActual;
	
	public void display(GLAutoDrawable drawable) {
		tAnterior = tActual;
		tActual = System.nanoTime();
//		@SuppressWarnings("unused")
		float incT = (float)(tActual - tAnterior)/ 1000000000;
		
		GL gl = drawable.getGL();

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		float x = w/2;
		float y = h/2;
		float s = x/256;
		float t = y/256;
		gl.glOrtho(-x, x, -y, y, 0, 1);

		fondo.activar(gl, 0);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
//		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glDepthMask(false);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f (-s,-t);
			gl.glVertex3f   (-x,-y, 0);
			gl.glTexCoord2f ( s,-t);
			gl.glVertex3f   ( x,-y, 0);
			gl.glTexCoord2f ( s, t);
			gl.glVertex3f   ( x, y, 0);
			gl.glTexCoord2f (-s, t);
			gl.glVertex3f   (-x, y, 0);
		gl.glEnd();
		gl.glDepthMask(true);

		gl.glPopMatrix();

		gl.glMatrixMode(GL.GL_MODELVIEW);
//		gl.glLoadIdentity();
		glu.gluLookAt(
				orbitBehavior.eyePos.x, orbitBehavior.eyePos.y, orbitBehavior.eyePos.z,
				orbitBehavior.targetPos.x, orbitBehavior.targetPos.y, orbitBehavior.targetPos.z,
				orbitBehavior.upDir.x, orbitBehavior.upDir.y, orbitBehavior.upDir.z
		);
		ObjetosOrientables.loadModelViewMatrix();
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);

		texture1.activar(gl, 0);
		texture12.activar(gl, 1);

//		gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
		gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_COLOR);

		humo.getModificador().modificar(incT);
		humo.draw(gl);

		UnidadTextura.desactivar(gl, 1);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//		gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_COLOR);
//		gl.glBlendFunc(GL.GL_ZERO,GL.GL_ONE_MINUS_SRC_ALPHA);
//		gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		explosion.getModificador().modificar(incT);
		explosion.draw(gl);

		texture2.activar(gl, 0);
//		gl.glBlendEquation(GL.GL_FUNC_ADD);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		fuente.getModificador().modificar(incT);
		fuenteTransformada.draw(gl);

		gl.glDepthMask(true);
		gl.glDisable(GL.GL_BLEND);
		
		gl.glFlush();
	}

	float w;
	float h;
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		this.w = w;
		this.h = h;
		if( w != 0 && h != 0 ){
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, w, h);

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.001;
			double far = 30.0;
			double a1 = 30.0; // angulo en grados
			double a2 = a1 / 360 * Math.PI; // mitad del angulo en radianes
			double d = near / Math.sqrt((1 / Math.pow(Math.sin(a2), 2)) - 1);

			double ratio = (double) w / h;
			if( ratio < 1 )
				gl.glFrustum(-d, d, -d / ratio, d / ratio, near, far);
			else
				gl.glFrustum(-d * ratio, d * ratio, -d, d, near, far);

			ObjetosOrientables.loadProjectionMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	public void run() {
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.requestFocusInWindow();
		canvas.getContext().getGL();
	
		setVisible(true);
		new Thread() {
			public void run() {
				while( true ){
					canvas.display();
					try{
						Thread.sleep(5);
					}catch( InterruptedException e ){
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public static void main(String[] args) {
		new PruebaParticulas().run();
	}
}