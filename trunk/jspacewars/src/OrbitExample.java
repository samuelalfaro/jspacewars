import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.*;

import org.sam.jogl.*;
import org.sam.jogl.particulas.*;
import org.sam.util.Imagen;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class OrbitExample
extends JFrame 
implements GLEventListener{

	private GLU glu;
	private final GLCanvas canvas;
	private final OrbitBehavior orbitBehavior = new OrbitBehavior();

	private Apariencia apFondo;
	private Instancia3D nave;

	public OrbitExample(){
		super("Orbit Example");
		canvas = new GLCanvas(new GLCapabilities());
		canvas.addGLEventListener(this);
		canvas.addMouseListener(orbitBehavior);
		canvas.addMouseMotionListener(orbitBehavior);

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
	
	private transient Particulas estrellas[] = new Particulas[20];
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
			
			Apariencia apEstrellas = new Apariencia();
			apEstrellas.setTextura(
					new Textura(gl, Textura.Format.ALPHA, Imagen.cargarToBufferedImage("resources/texturas/spark.jpg"), true)
			);
			apEstrellas.setAtributosTransparencia(
					new AtributosTransparencia(
							AtributosTransparencia.Equation.ADD,
							AtributosTransparencia.SrcFunc.SRC_ALPHA,
							AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA)
			);
			
			Matrix4f tEmisor = new Matrix4f();
			tEmisor.setIdentity();
			tEmisor.rotZ((float)Math.PI);
			tEmisor.setTranslation(new Vector3f(2.05f,0.5f,0.0f));
			Emisor emisor = new Emisor.Cache(new Emisor.Lineal(1.0f,0.0f),tEmisor,1024);
			
//			FactoriaDeParticulas.setPointSpritesEnabled(true);
			for(int i = 0, len = estrellas.length; i< len; i++){
				estrellas[i] = FactoriaDeParticulas.createParticulas((int)( Math.pow(8*(len-i)/len, 2) + 1 ));
				estrellas[i].setEmisor(emisor);
				estrellas[i].setEmision(Particulas.Emision.CONTINUA);
				estrellas[i].setRangoDeEmision(1.0f);
//				estrellas[i].setRadio(4.0f + (12.0f * (i+1) )/len);
				estrellas[i].setRadio(0.004f + (0.012f * (i+1) )/len);
				float vel = 0.1f*i +  0.05f;
				float tVida = 2.05f/(vel*0.95f);
				estrellas[i].setTiempoVida(tVida);
				estrellas[i].setVelocidad(vel, vel*0.05f, false);
//				estrellas[i].setVelocidad(vel);
//				estrellas[i].setVelocidadGiro(360.0f,90.0f,true);
				estrellas[i].setGiroInicial(0, 180, true);
				estrellas[i].setColor(
						0.65f + (0.35f * (i+1))/len,
						0.35f + (0.65f * (i+1))/len,
						0.85f + (0.15f * (i+1))/len,
						1.0f
				);
				//					estrellas[i].setPertubacionColor(0.25f, false, true);
				estrellas[i].setApariencia(apEstrellas);
				estrellas[i].reset();
				estrellas[i].getModificador().modificar(tVida);
			}
//			FactoriaDeParticulas.setPointSpritesEnabled(false);

			Grupo childs = new Grupo();

			Matrix4d mtd = new Matrix4d();
			mtd.rotY(Math.PI/2);
			mtd.setScale(2.5);

			Objeto3D objetoNave = ObjLoader.loadToList("resources/obj3d/bomber01/forma.obj", mtd);
			objetoNave.getApariencia().setMaterial(Material.DEFAULT);
			
			//*
			if( gl.glGetString(GL.GL_EXTENSIONS).indexOf("GL_ARB_vertex_shader") != -1 ){
			/*/
			if( false ){
			//*/
				UnidadTextura[] unidadesTextura = new UnidadTextura[3];
				unidadesTextura[0] = new UnidadTextura();
				unidadesTextura[0].setTextura(
						new Textura(gl, Textura.Format.RGB, Imagen.cargarToBufferedImage("resources/obj3d/bomber01/t01.jpg"), true)
				);
				unidadesTextura[1] = new UnidadTextura();
				unidadesTextura[1].setTextura(
						new Textura(gl, Textura.Format.LUMINANCE, Imagen.cargarToBufferedImage("resources/obj3d/bomber01/t02.jpg"), true)
				);
				unidadesTextura[2] = new UnidadTextura();
				unidadesTextura[2].setTextura(
						new Textura(gl, Textura.Format.RGB, Imagen.cargarToBufferedImage("resources/texturas/reflect.jpg"), true)
				);
				objetoNave.getApariencia().setUnidadesTextura(unidadesTextura);
				
				Shader shader = new Shader(
						gl,
						"resources/shaders/reflexion.vert",
						"resources/shaders/reflexion.frag"
				);
				shader.addUniform(gl, "difuseMap", 0 );
				shader.addUniform(gl, "reflexionMap", 1 );
				shader.addUniform(gl, "reflexionEnv", 2 );
				objetoNave.getApariencia().setShader(shader);
			}else{
				objetoNave.getApariencia().setTextura(
						new Textura(gl, Textura.Format.RGB, Imagen.cargarToBufferedImage("resources/obj3d/bomber01/t01.jpg"), true)
				);
			}
			
			childs.add(objetoNave);

			XStream xStream = new XStream(new DomDriver());
			GrafoEscenaConverters.register(xStream);
			Particulas humoNave =  (Particulas)xStream.fromXML(new FileReader("particulas.xml"));

			Matrix4f mtf = new Matrix4f();
			mtf.rotY((float)Math.PI);
			mtf.setTranslation(new Vector3f(-1.25f,-0.12f,-0.82f));
			childs.add( new NodoTransformador(mtf, humoNave.clone() ) );

			mtf = new Matrix4f();
			mtf.rotY((float)Math.PI);
			mtf.setTranslation(new Vector3f(-1.25f,-0.12f,-0.62f));
			childs.add( new NodoTransformador(mtf, humoNave.clone() ) );

			mtf = new Matrix4f();
			mtf.rotY((float)Math.PI);
			mtf.setTranslation(new Vector3f(-1.25f,-0.12f, 0.62f));
			childs.add( new NodoTransformador(mtf, humoNave.clone() ) );

			mtf = new Matrix4f();
			mtf.rotY((float)Math.PI);
			mtf.setTranslation(new Vector3f(-1.25f,-0.12f, 0.82f));
			childs.add( new NodoTransformador(mtf, humoNave.clone() ) );

			nave = new Instancia3D(0,childs);
			nave.reset();

			// TODO check
			//			xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
			//			System.out.println(xStream.toXML(nave));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ObjLoader.ParsingErrorException e) {
			e.printStackTrace();
		}

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);

		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, 	new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,	new float[]{ 0.9f, 1.0f, 1.0f, 1.0f }, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,	new float[]{ 1.0f, 1.0f, 0.9f, 1.0f }, 0);
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
		ObjetosOrientables.loadModelViewMatrix();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);
		ObjetosOrientables.loadProjectionMatrix();

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
		
		for(Particulas p:estrellas ){
			p.getModificador().modificar(incT);
			p.draw(gl);
		}
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		ObjetosOrientables.loadProjectionMatrix();
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		glu.gluLookAt(
				orbitBehavior.eyePos.x, orbitBehavior.eyePos.y, orbitBehavior.eyePos.z,
				orbitBehavior.targetPos.x, orbitBehavior.targetPos.y, orbitBehavior.targetPos.z,
				orbitBehavior.upDir.x, orbitBehavior.upDir.y, orbitBehavior.upDir.z
		);
		ObjetosOrientables.loadModelViewMatrix();

		nave.getModificador().modificar(incT);
		nave.draw(gl);

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
		new OrbitExample().run();
	}
}