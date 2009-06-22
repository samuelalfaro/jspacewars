package org.sam.red.cliente;

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

import org.sam.elementos.KeysState;
import org.sam.gui.Marco;
import org.sam.jogl.*;
import org.sam.jogl.ObjLoader.ParsingErrorException;
import org.sam.jogl.fondos.CieloEstrellado;
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
	private ModificableBoolean loading = new ModificableBoolean(true);
	
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
			synchronized(loading){
				try {
					loading.wait();
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
	
	private CieloEstrellado fondo;
	
	private class Loader implements GLEventListener{

		private final Sincronizador sincronizador;
//		private transient long tAnterior, tActual;
		
		private Loader(Sincronizador sincronizador){
			this.sincronizador = sincronizador;
//			tActual = System.nanoTime();
		}

		public void init(GLAutoDrawable drawable){
			System.out.println("iniciando");

			GL gl = drawable.getGL();
			
			fondo = new CieloEstrellado(gl, "resources/texturas/cielo1.jpg", "resources/texturas/spark.jpg");
			
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
		}
		
		private transient ObjectInputStream in;
		private transient boolean eof;
		
		public void display(GLAutoDrawable drawable){
			GL gl = drawable.getGL();
			try {
				if(in == null){
					XStream xStream = new XStream(new DomDriver());
					xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
					GrafoEscenaConverters.register(xStream);
					GrafoEscenaConverters.setReusingReferenceByXPathMarshallingStrategy(xStream);

					in = xStream.createObjectInputStream(new FileReader("instancias3D-stream.xml"));
				}
				
				if( !eof )
					try{
						cache.addPrototipo((Instancia3D) in.readObject());
					}catch( ClassNotFoundException e ){
						e.printStackTrace();
					}catch( EOFException eofEx ){
						eof = true;
						in.close();
					}
				
//				Instancia3D[] instancias =  (Instancia3D[])xStream.fromXML(new FileReader("instancias3D.xml"));
//				StringWriter writer = new StringWriter();
//				try{
//					ObjectOutputStream out = xStream.createObjectOutputStream(writer);
//					for(Instancia3D instancia: instancias)
//						out.writeObject(instancia);
//				}catch( IOException e ){
//					e.printStackTrace();
//				}
//				System.out.println(writer.toString());
//				for(Instancia3D instancia: instancias)
//					cache.addPrototipo( instancia );
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParsingErrorException e) {
				e.printStackTrace();
			} catch (GLException e) {
				e.printStackTrace();
			}
			
//			tAnterior = tActual;
//			tActual = System.nanoTime();
//			fondo.modificar((float)(tActual - tAnterior)/ 100000000);
			fondo.draw(gl);
			gl.glFlush();
			
			if(eof){
				synchronized(loading){
					loading.setFalse();
					loading.notifyAll();
				}
				canvas.removeGLEventListener(this);
				canvas.addGLEventListener(new Renderer(sincronizador));
			}
		}

		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){}

		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
			System.out.println("reescalando 1");
			Visor3D.this.reshape(drawable.getGL(), x, y, w, h);
		}
	}
	
	private class Renderer implements GLEventListener{

		private final Sincronizador sincronizador;
		private final transient GLU glu = new GLU();
		private transient long tAnterior, tActual;
		
		private Renderer(Sincronizador sincronizador){
			this.sincronizador = sincronizador;
			tActual = System.nanoTime();
		}
		
		public void init(GLAutoDrawable drawable){
		}
		
		public void display(GLAutoDrawable drawable){
			
			tAnterior = tActual;
			tActual = System.nanoTime();
			float incT = (float)(tActual - tAnterior)/ 1000000000;
			
			pantallaPintada = false;
			GL gl = drawable.getGL();
			
			fondo.modificar(incT);
			fondo.draw(gl);
			
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
			System.out.println("reescalando 2");
			Visor3D.this.reshape(drawable.getGL(), x, y, w, h);
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
		canvas.addGLEventListener(new Loader(sincronizador));
//		canvas.addGLEventListener(new Renderer(sincronizador));
		
		marco = Marco.getNewMarco(0);
		marco.add(canvas,BorderLayout.CENTER);
		
//		pantalla.setFocusable(true);
//		pantalla.setFocusTraversalKeysEnabled(false);
//		pantalla.requestFocusInWindow();
//		pantalla.getContext().getGL();
	}
	
	public void start(){
		lector.start();
		new Thread(){
			@Override
			public void run(){
				while(loading.isTrue()){
					canvas.display();
					try{
						Thread.sleep(5);
					}catch( InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		}.start();
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
	
	private void reshape(GL gl, int x, int y, int w, int h){
//		if(fondo != null)
		fondo.setProporcionesPantalla((float)w/h);
		gl.glViewport(0, 0, w, h);
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