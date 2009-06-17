package org.sam.gui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JPanel;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.jogl.*;
import org.sam.jogl.ObjLoader.ParsingErrorException;
import org.sam.jogl.particulas.*;
import org.sam.red.KeysState;
import org.sam.red.Sincronizador;
import org.sam.util.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Visor3D implements KeyListener{
	
	private final static int K_SUBE		= KeyEvent.VK_UP;
	private final static int K_BAJA		= KeyEvent.VK_DOWN;
	private final static int K_ACELERA	= KeyEvent.VK_RIGHT;
	private final static int K_FRENA	= KeyEvent.VK_LEFT;
	private final static int K_DISPARO	= KeyEvent.VK_SHIFT;
	private final static int K_BOMBA	= KeyEvent.VK_SPACE;
	private final static int K_UPGRADE	= KeyEvent.VK_CONTROL;
	
	private Marco marco;
	private final GLCanvas canvas;
	private final ReadableByteChannel channelIn;
	private final WritableByteChannel channelOut;
	private ByteBuffer buff;
	
	private Cache<Instancia3D> cache;
	private List<Instancia3D> elementos;
	private final Collection<Modificador> modificadores;
	
	private	int	key_state = 0;
	
	private boolean pantallaPintada;
	private Object iniciado = new Object();
	
	private class Lector extends Thread{
		
		private final Sincronizador sincronizador;
		
		public Lector(Sincronizador sincronizador){
			this.sincronizador = sincronizador;
		}
		
		private void recibir(ByteBuffer productor, List<Instancia3D> consumidor){
			int i = 0, nElementos = productor.getInt();
			ListIterator<Instancia3D> iConsumidor = consumidor.listIterator();
		
			if(iConsumidor.hasNext() && i < nElementos){
				short tipo = productor.getShort();
				short id = productor.getShort();
				i++;
				Instancia3D almacenado = iConsumidor.next();
				
				do{
					if(almacenado.getTipo() < tipo || ( almacenado.getTipo() == tipo && almacenado.getId() < id )){
						if(almacenado.getModificador()!= null)
							modificadores.remove(almacenado.getModificador());
						cache.cached(almacenado);
						iConsumidor.remove();
						if(!iConsumidor.hasNext()){
							Instancia3D nuevo = cache.newObject(tipo);
							if(nuevo.getModificador()!= null)
								modificadores.add(nuevo.getModificador());
							nuevo.setId(id);
							nuevo.recibir(productor);
							consumidor.add(nuevo);
							break;
						}
						almacenado = iConsumidor.next();
					}else if(almacenado.getTipo() > tipo || ( almacenado.getTipo() == tipo && almacenado.getId() > id )){
						Instancia3D nuevo = cache.newObject(tipo);
						if(nuevo.getModificador()!= null)
							modificadores.add(nuevo.getModificador());
						nuevo.setId(id);
						nuevo.recibir(productor);
						iConsumidor.add(nuevo);
						if(i == nElementos){
							if(almacenado.getModificador()!= null)
								modificadores.remove(almacenado.getModificador());
							cache.cached(almacenado);
							iConsumidor.remove();
							break;
						}
						tipo = productor.getShort();
						id = productor.getShort();
						i++;
					}else{
						almacenado.recibir(productor);
						if(i == nElementos || !iConsumidor.hasNext()) 
							break;
						tipo = productor.getShort();
						id = productor.getShort();
						i++;
						almacenado = iConsumidor.next();
					}
				}while(true);
			}
			// Si se reciben mas elementos de los que hay, se crean.
			while(i < nElementos){
				i++;
				Instancia3D nuevo = cache.newObject(productor.getShort());
				if(nuevo.getModificador()!= null)
					modificadores.add(nuevo.getModificador());
				nuevo.setId(productor.getShort());
				nuevo.recibir(productor);
				consumidor.add(nuevo);
			}
			// Si queda mas elementos de los que se reciben se eliminan
			while(iConsumidor.hasNext()){
				Instancia3D viejo = iConsumidor.next();
				cache.cached(viejo);
				if(viejo.getModificador()!= null)
					modificadores.remove(viejo.getModificador());
				iConsumidor.remove();
			}
			if(consumidor.size()!= nElementos)
				System.out.println("Error: "+nElementos+"\t"+consumidor.size());
		}

		public void run(){
			synchronized(iniciado){
				try {
					iniciado.wait();
				} catch (InterruptedException e) {
				}
			}
			while(true){
//				datosRecibidos = false;
				buff.clear();
				buff.putInt(key_state);
				buff.flip();
				try {
					channelOut.write(buff);
				} catch (IOException e) {
					e.printStackTrace();
				}

				buff.clear();
				try {
					channelIn.read(buff);
				} catch (IOException e) {
					e.printStackTrace();
				}
				buff.flip();

				recibir(buff, elementos);
			
//				datosRecibidos = true;
				canvas.display();
				while(!pantallaPintada)
					sincronizador.esperar();
			}
		}
	}
	
	private class Renderer implements GLEventListener{

		private final Sincronizador sincronizador;
		private boolean datos_cargados = false;
		
		Renderer(Sincronizador sincronizador){
			this.sincronizador = sincronizador;
		}

		private GLU glu;
		private Apariencia fondo;
		private Particulas[] estrellas = new Particulas[20];
		
		private float s1, s2, proporcionesPantalla;
		private transient long tAnterior, tActual;
		
		public void init(GLAutoDrawable drawable){
			System.out.println("iniciando");

			GL gl = drawable.getGL();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			glu = new GLU();

			try {
				fondo = new Apariencia(); 
				fondo.setTextura(
						new Textura(gl, Textura.Format.RGB, Imagen.cargarImagen("resources/texturas/cielo1.jpg"), true)
				);
				fondo.setAtributosTextura(new AtributosTextura());
				fondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);

				Apariencia apEstrellas = new Apariencia();
				apEstrellas.setTextura(
						new Textura(gl, Textura.Format.ALPHA, Imagen.cargarImagen("resources/texturas/spark.jpg"), true)
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

				// TODO check
				FactoriaDeParticulas.setPointSpritesEnabled(true);
				for(int i = 0, len = estrellas.length; i< len; i++){
					estrellas[i] = FactoriaDeParticulas.createParticulas((int)( Math.pow(8*(len-i)/len, 2) + 1 ));
					estrellas[i].setEmisor(emisor);
					estrellas[i].setEmision(Particulas.Emision.CONTINUA);
					estrellas[i].setRangoDeEmision(1.0f);
//					estrellas[i].setRadio(4.0f + (12.0f * (i+1) )/len);
					estrellas[i].setRadio(0.004f + (0.012f * (i+1) )/len);
					float vel = 0.1f*i +  0.05f;
					float tVida = 2.05f/(vel*0.95f);
					estrellas[i].setTiempoVida(tVida);
					estrellas[i].setVelocidad(vel, vel*0.05f, false);
					estrellas[i].setGiroInicial(0, 180, true);
					estrellas[i].setColor(
							0.65f + (0.35f * (i+1))/len,
							0.35f + (0.65f * (i+1))/len,
							0.85f + (0.15f * (i+1))/len,
							1.0f
					);
					estrellas[i].setPertubacionColor(0.25f, false, false);
					estrellas[i].setApariencia(apEstrellas);
					estrellas[i].reset();
					estrellas[i].getModificador().modificar(tVida);
				}
				FactoriaDeParticulas.setPointSpritesEnabled(false);

				XStream xStream = new XStream(new DomDriver());
				xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
				GrafoEscenaConverters.register(xStream);
				Instancia3D[] instancias =  (Instancia3D[])xStream.fromXML(new FileReader("instancias3D.xml"));
				
				for(Instancia3D instancia: instancias)
					cache.addPrototipo( instancia );
				
				System.out.println("ok");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ParsingErrorException e) {
				e.printStackTrace();
			} catch (GLException e) {
				e.printStackTrace();
			}

			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			gl.glShadeModel(GL.GL_SMOOTH);

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LESS);

			gl.glEnable(GL.GL_LIGHT0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,
					new float[]{ 0.5f, 1.0f, 1.0f, 0.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,
					new float[]{ 0.4f, 0.6f, 0.6f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,
					new float[]{ 0.0f, 0.0f, 0.0f, 1.0f }, 0);
			gl.glEnable(GL.GL_LIGHT1);
			gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION,
					new float[]{ -0.5f, -1.0f, 1.0f, 0.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE,
					new float[]{ 0.6f, 0.4f, 0.4f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR,
					new float[]{ 0.0f, 0.0f, 0.0f, 1.0f }, 0);
			gl.glEnable(GL.GL_LIGHT2);
			gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION,
					new float[]{ 0.0f, 0.0f, 1.0f, 0.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE,
					new float[]{ 0.0f, 0.0f, 0.0f, 1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPECULAR,
					new float[]{ 0.95f, 0.95f, 0.95f, 1.0f }, 0);
			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_CULL_FACE);

			synchronized(iniciado){
				iniciado.notifyAll();
			}
			tActual = System.nanoTime();
			datos_cargados = true;
		}
		
		public void display(GLAutoDrawable drawable){
			if(!datos_cargados){
				System.out.println("Cargando datos");
				return;
			}
			
			tAnterior = tActual;
			tActual = System.nanoTime();
			
			float incT = (float)(tActual - tAnterior)/ 1000000000;
			
			pantallaPintada = false;
			
			GL gl = drawable.getGL();

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			ObjetosOrientables.loadModelViewMatrix();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0.0, 1.0);
			
			s1 += 0.02f * incT;
			if(s1 > 1.0f)
				s1 -= 1.0f;

			fondo.usar(gl);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
			gl.glDepthMask(false);
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(s1,0.0f);
				gl.glVertex3f(0.0f,0.0f,0.0f);
				gl.glTexCoord2f(s2 + s1,0.0f);
				gl.glVertex3f(proporcionesPantalla,0.0f,0.0f);
				gl.glTexCoord2f(s2 + s1,1.0f);
				gl.glVertex3f(proporcionesPantalla,1.0f,0.0f);
				gl.glTexCoord2f(s1,1.0f);
				gl.glVertex3f(0.0f,1.0f,0.0f);
			gl.glEnd();

			for(Particulas p:estrellas ){
				p.getModificador().modificar(incT);
				p.draw(gl);
			}
			
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPopMatrix();
			ObjetosOrientables.loadProjectionMatrix();
			
			gl.glMatrixMode(GL.GL_MODELVIEW);
			glu.gluLookAt(0.0, 0.0, 11, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
			ObjetosOrientables.loadModelViewMatrix();
			
			for(Modificador modificador: modificadores)
				modificador.modificar(incT);
			
			gl.glDepthMask(true);
			for(Instancia3D elemento: elementos)
				elemento.draw( gl );

			gl.glFlush();
			
			pantallaPintada = true;
			sincronizador.notificar();
		}

		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){}

		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
			System.out.println("reescalando");
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, w, h);
			proporcionesPantalla = (float)w/h;
			s2 = proporcionesPantalla / fondo.getTextura().getProporciones();
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.5;
			double far  = 240.0;
			double a1 = 35.0;			// angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);
			//gl.glFrustum(-d,((2.0*w)/h -1.0)*d, -d, d, near, far);
			double anchovisible = 4.5/3.0;
			gl.glFrustum(-anchovisible*d,((2.0*w)/h -anchovisible)*d, -d, d, near, far);
			ObjetosOrientables.loadProjectionMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}
	}
	
	private Thread lector;
	
	public Visor3D(ReadableByteChannel channelIn,  WritableByteChannel channelOut){
		//ComparadorDeElementos comparador = new ComparadorDeElementos();
		cache = new Cache<Instancia3D>(500);

		elementos = new Lista<Instancia3D>();
		modificadores = new LinkedList<Modificador>();
		
		this.channelIn = channelIn;
		this.channelOut = channelOut;
		buff = ByteBuffer.allocateDirect(8192); 
		
		key_state = 0;
		
//		datosRecibidos = false;
//		pantallaPintada = false;
		
		Sincronizador sincronizador = new Sincronizador();
		lector = new Lector(sincronizador);
		
		canvas = new GLCanvas(new GLCapabilities());

		canvas.setIgnoreRepaint(true);
		canvas.addKeyListener(this);
		canvas.addGLEventListener(new Renderer(sincronizador));
		
		marco = Marco.getNewMarco(0);
		marco.add(canvas,BorderLayout.CENTER);
		
//		pantalla.setFocusable(true);
//		pantalla.setFocusTraversalKeysEnabled(false);
//		pantalla.requestFocusInWindow();
//		pantalla.getContext().getGL();
	}
	
	public void start(){
		lector.start();
		canvas.display();
		canvas.requestFocus();
		/*
		new Thread(){
			public void run(){
				Sounds.play("side_effects.mod");
			}
		}.start();
//		new Thread(){
//			public void run(){
//				Sounds.playVorbisOgg("delinquentes.ogg");
//			}
//		}.start();
//		new Thread(){
//			public void run(){
//				while(true){
//					try{
//						Thread.sleep(5000);
//					}catch( InterruptedException ignorada ){
//					}
//					Sounds.playWav("Riff.wav");
//				}
//			}
//		}.start();
		//*/
	}
	
	public JPanel getPanel(){
		return marco;
	}
	
	public void keyPressed(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		switch (keyCode) {
			case K_SUBE:	key_state |= KeysState.SUBE;	break;
			case K_BAJA:	key_state |= KeysState.BAJA;	break;
			case K_ACELERA:	key_state |= KeysState.ACELERA;	break;
			case K_FRENA:	key_state |= KeysState.FRENA;	break;
			case K_DISPARO:	key_state |= KeysState.DISPARO;	break;
			case K_BOMBA:	key_state |= KeysState.BOMBA;	break;
			case K_UPGRADE:	key_state |= KeysState.UPGRADE;	break;
			default:
		}
	}
	
	public void keyReleased(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		switch (keyCode) {
			case K_SUBE:	key_state &= ~KeysState.SUBE;		break;
			case K_BAJA:	key_state &= ~KeysState.BAJA;		break;
			case K_ACELERA:	key_state &= ~KeysState.ACELERA;	break;
			case K_FRENA:	key_state &= ~KeysState.FRENA;		break;
			case K_DISPARO:	key_state &= ~KeysState.DISPARO;	break;
			case K_BOMBA:	key_state &= ~KeysState.BOMBA;		break;
			case K_UPGRADE:	key_state &= ~KeysState.UPGRADE;	break;
			case KeyEvent.VK_C: System.out.println(cache);
			default:
		}
	}
	
	public void keyTyped(KeyEvent keyEvent) {}
}