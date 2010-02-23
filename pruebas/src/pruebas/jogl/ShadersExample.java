package pruebas.jogl;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.LinkedList;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.*;

import org.sam.elementos.Modificador;
import org.sam.jogl.*;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class ShadersExample
		extends JFrame 
		implements GLEventListener{
	
	private GLU glu;
	private final GLCanvas canvas;
	private final OrbitBehavior orbitBehavior;
	
	private Apariencia apFondo;
	private Instancia3D nave1;
	
	private final Collection<Modificador> gestorDeParticulas;
	
	public ShadersExample(){
		super("Example");
		gestorDeParticulas = new LinkedList<Modificador>();
		canvas = new GLCanvas(new GLCapabilities());
		canvas.addGLEventListener(this);
		orbitBehavior = new OrbitBehavior();
		orbitBehavior.eyePos.x = 0;
		orbitBehavior.eyePos.y = 0;
		orbitBehavior.eyePos.z = 2.5f;
		canvas.addMouseListener(orbitBehavior);
		canvas.addMouseMotionListener(orbitBehavior);

		getContentPane().add(canvas);
	}

	private transient float proporcionesFondo, proporcionesPantalla;
	private transient long tAnterior, tActual;

	public void init(GLAutoDrawable drawable){
		GL gl = drawable.getGL();
		glu = new GLU();
		gl.glClearColor(0.0f,0.25f,0.25f,0.0f);
		
		try {
			BufferedImage img = Imagen.cargarToBufferedImage("resources/texturas/cielo512.jpg");
			proporcionesFondo = img.getHeight(null)/img.getWidth(null);
			apFondo = new Apariencia();

			apFondo.setTextura(new Textura(gl, Textura.Format.RGB, img , true));
			apFondo.setAtributosTextura(new AtributosTextura());
			apFondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);
			
			Grupo childs;
			Objeto3D nave;
			Matrix4d mtd;
			Matrix4f mtf;
			
			XStream xStream = new XStream(new DomDriver());
			GrafoEscenaConverters.register(xStream);
			Particulas humoNave =  (Particulas)xStream.fromXML(new FileReader("resources/particulas.xml"));
			
			mtd = new Matrix4d();
			mtd.rotY(Math.PI/2);
			mtd.setScale(1.25);
			nave = ObjLoader.loadToList("resources/obj3d/nave05/forma.obj",mtd);
			Material material = new Material();
			material.setAmbient(0.0f, 0.0f, 0.0f, 1.0f);
			material.setDiffuse(0.25f, 0.25f, 0.25f, 1.0f);
			material.setSpecular(1.0f, 1.0f, 1.0f, 1.0f);
			material.setEmission(0.0f, 0.0f, 0.0f, 1.0f);
			material.setShininess(1.0f);
			nave.getApariencia().setMaterial(material);
			UnidadTextura unidadesTextura[] = new UnidadTextura[3];
			AtributosTextura atributosTextura;
			unidadesTextura[0] = new UnidadTextura();
			unidadesTextura[0].setTextura(
					new Textura(gl, Textura.Format.RGB, Imagen.cargarToBufferedImage("resources/obj3d/nave05/t01.jpg"), true)
			);
			unidadesTextura[1] = new UnidadTextura();
			unidadesTextura[1].setTextura(
					new Textura(gl, Textura.Format.LUMINANCE, Imagen.cargarToBufferedImage("resources/obj3d/nave05/t02.jpg"), true)
			);
			atributosTextura = new AtributosTextura();
			atributosTextura.setMode(AtributosTextura.Mode.COMBINE);
			atributosTextura.setCombineRgbMode(AtributosTextura.CombineMode.SUBTRACT);
			atributosTextura.setCombineRgbSource0(AtributosTextura.CombineSrc.PREVIOUS, AtributosTextura.CombineOperand.SRC_COLOR);
			atributosTextura.setCombineRgbSource1(AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_COLOR);
			unidadesTextura[1].setAtributosTextura(atributosTextura);
			
			unidadesTextura[2] = new UnidadTextura();
			unidadesTextura[2].setTextura(
					new Textura(gl, Textura.Format.RGB, Imagen.cargarToBufferedImage("resources/texturas/reflect.jpg"), true)
			);
			atributosTextura = new AtributosTextura();
			atributosTextura.setMode(AtributosTextura.Mode.COMBINE);
			atributosTextura.setCombineRgbMode(AtributosTextura.CombineMode.ADD_SIGNED);
			atributosTextura.setCombineRgbSource0(AtributosTextura.CombineSrc.PREVIOUS, AtributosTextura.CombineOperand.SRC_COLOR);
			atributosTextura.setCombineRgbSource1(AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_COLOR);
			unidadesTextura[2].setAtributosTextura(atributosTextura);
			unidadesTextura[2].setGenCoordTextura(
					new GenCoordTextura(GenCoordTextura.Mode.REFLECTION_MAP, GenCoordTextura.Coordinates.TEXTURE_COORDINATE_2)
			);
			
			nave.getApariencia().setUnidadesTextura(unidadesTextura);
			
	        String extensions = gl.glGetString(GL.GL_EXTENSIONS);
	        if( extensions.indexOf("GL_ARB_vertex_shader") != -1 ){
	        	Shader shader = new Shader(
	        			gl,
	        			"resources/shaders/reflexion.vert",
						"resources/shaders/reflexion.frag"
	        	);
	        	shader.addUniform(gl, "difuseMap", 0 );
	        	shader.addUniform(gl, "reflexionMap", 1 );
	        	shader.addUniform(gl, "reflexionEnv", 2 );
	        	
				nave.getApariencia().setShader(shader);
			}
			
			childs = new Grupo();
			childs.add( new NodoCompartido(nave) );
			
			mtf = new Matrix4f();
			mtf.rotY((float)Math.PI);
			mtf.setTranslation(new Vector3f(-0.60f, -0.075f, 0.0f));
			childs.add(new NodoTransformador(mtf, humoNave));
			
			nave1 = new Instancia3D(0,childs);
			nave1.reset();
			gestorDeParticulas.add(nave1.getModificador());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ObjLoader.ParsingErrorException e) {
			e.printStackTrace();
		}
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		
		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,
			new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,
				new float[]{ 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,
				new float[]{ 1.0f, 1.0f, 1.0f, 1.0f }, 0);
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

		apFondo.usar(gl);
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
		
		for( Modificador modificador: gestorDeParticulas )
			modificador.modificar(incT);

		nave1.draw(gl);
		
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
	
	public void run(){
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.requestFocusInWindow();
//		canvas.getContext().getGL();
	
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

	public static void main(String[] args){
		new ShadersExample().run();
	}
}