package org.sam.red.cliente;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Matrix4d;

import org.sam.jogl.*;
import org.sam.jogl.ObjLoader.ParsingErrorException;
import org.sam.red.KeysState;
import org.sam.red.Sincronizador;
import org.sam.util.*;

@SuppressWarnings("serial")
public class VisorCliente3D implements KeyListener{
	
	private final static int K_SUBE		= KeyEvent.VK_UP;
	private final static int K_BAJA		= KeyEvent.VK_DOWN;
	private final static int K_ACELERA	= KeyEvent.VK_RIGHT;
	private final static int K_FRENA	= KeyEvent.VK_LEFT;
	private final static int K_DISPARO	= KeyEvent.VK_SHIFT;
	private final static int K_BOMBA	= KeyEvent.VK_SPACE;
	private final static int K_UPGRADE	= KeyEvent.VK_CONTROL;
	
	private final GLCanvas pantalla;
	private final ReadableByteChannel channelIn;
	private final WritableByteChannel channelOut;
	private ByteBuffer buff;
	
	private Cache<Instancia3D> cache;
	private List<Instancia3D> naves;
	private List<Instancia3D> disparos;
	
	private	int	key_state = 0;
	
	private boolean datosRecibidos, pantallaPintada;
	private boolean iniciado = false;
	
	private class Lector extends Thread{
		
		private final Sincronizador sincronizador;
		//private final InterruptedException terminado = new InterruptedException();
		
		public Lector(Sincronizador sincronizador){
			this.sincronizador = sincronizador;
		}
		
		public void run(){
			while(!iniciado)
				Thread.yield();
			while(true){
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

				recibir(buff, naves);
				recibir(buff, disparos);
			
				datosRecibidos = true;
				pantallaPintada = false;
				Thread.yield();
				sincronizador.notificar();
				while(!pantallaPintada)
					sincronizador.esperar();
			}
		}
	}
	
	private class Animador extends Thread{
		
		private final Sincronizador sincronizador;
		
		public Animador(Sincronizador sincronizador){
			this.sincronizador = sincronizador;
		}
		
		public void run(){
			while(true){
				while(!datosRecibidos)
					sincronizador.esperar();
				datosRecibidos = false;
				pantalla.display();
				try{
					Thread.sleep(1);
				}catch(InterruptedException e){
				}
				pantallaPintada = true;
				Thread.yield();
				sincronizador.notificar();
			}
		}
	}
	
	private class Renderer implements GLEventListener{
		private GLU glu;
		Apariencia fondo;
		
		public void init(GLAutoDrawable drawable){
			System.out.println("iniciando");
			GL gl = drawable.getGL();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			glu = new GLU();

			try {
				fondo = new Apariencia(); 
				fondo.setTextura(
						new Textura(gl, Textura.RGB, Imagen.cargarImagen("resources/obj3d/texturas/cielo512.jpg"), true)
				);
				fondo.setAtributosTextura(new AtributosTextura());
				fondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);
				
				Matrix4d mt = new Matrix4d();
				mt.rotY(Math.PI/2);
				Objeto3D forma = ObjLoader.loadToList("resources/obj3d/formas/nave02/forma.obj", ObjLoader.RESIZE, mt);
				forma.getApariencia().setTextura(
						new Textura(gl, Textura.RGB, Imagen.cargarImagen("resources/obj3d/formas/nave02/t01.jpg"), true)
				);

				cache.addPrototipo( new Instancia3D(0x100, forma) );
				
				int lid = gl.glGenLists(1);
				gl.glNewList(lid, GL.GL_COMPILE);
				gl.glBegin(GL.GL_QUADS);
				float ancho_med = 0.95f /2;
				float alto_med  = ancho_med /4;
				gl.glTexCoord2f(0,0);
				gl.glVertex3f(-ancho_med,-alto_med,0);
				gl.glTexCoord2f(0,1);
				gl.glVertex3f(-ancho_med,alto_med,0);
				gl.glTexCoord2f(1,1);
				gl.glVertex3f(ancho_med,alto_med,0);
				gl.glTexCoord2f(1,0);
				gl.glVertex3f(ancho_med,-alto_med,0);
				gl.glEnd();
				gl.glEndList();
				
				forma = new Objeto3D( new OglList(lid), new Apariencia() );
				forma.getApariencia().setTextura(
						new Textura(gl, Textura.RGBA, Imagen.cargarImagen("resources/obj3d/texturas/disparo-tn.png"), true)
				);
				
				cache.addPrototipo( new Instancia3D(1, forma) );
				cache.addPrototipo( new Instancia3D(2, forma) );
				cache.addPrototipo( new Instancia3D(3, forma) );
				cache.addPrototipo( new Instancia3D(4, forma) );
				cache.addPrototipo( new Instancia3D(5, forma) );
				cache.addPrototipo( new Instancia3D(6, forma) );
				cache.addPrototipo( new Instancia3D(7, forma) );
				cache.addPrototipo( new Instancia3D(10, forma) );
				cache.addPrototipo( new Instancia3D(20, forma) );
				
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
			
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,
				new float[]{ 0.0f, 0.0f, 10.0f, 1.0f }, 0);
			iniciado = true;
		}
		
		public void display(GLAutoDrawable drawable){
			GL gl = drawable.getGL();

			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0.0, 1.0);
			
			float s1 = 0.0f;
			float s2 = proporcionesPantalla * fondo.getTextura().getProporciones() + s1;
			gl.glDepthMask(false);

			fondo.activar(gl);

			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(s1,0.0f);
				gl.glVertex3f(0.0f,0.0f,0.0f);
				gl.glTexCoord2f(s1,1.0f);
				gl.glVertex3f(0.0f,1.0f,0.0f);
				gl.glTexCoord2f(s2,1.0f);
				gl.glVertex3f(proporcionesPantalla,1.0f,0.0f);
				gl.glTexCoord2f(s2,0.0f);
				gl.glVertex3f(proporcionesPantalla,0.0f,0.0f);
			gl.glEnd();
			gl.glDepthMask(true);
			
			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			glu.gluLookAt(0.0, 0.0, 9, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_LIGHT0);
			
			for(Instancia3D nave:naves)
				nave.draw( gl );
			
			gl.glDisable(GL.GL_LIGHT0);
			gl.glDisable(GL.GL_LIGHTING);
			
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDepthMask(false);
			for(Instancia3D disparo:disparos)
				disparo.draw(gl);
			gl.glDepthMask(true);
			gl.glDisable(GL.GL_BLEND);
			
			gl.glFlush();
		}

		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){}

		float proporcionesPantalla;
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
			System.out.println("reescalando");
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, w, h);
			proporcionesPantalla = (float)w/h;
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.5;
			double far  = 240.0;
			double a1 = 40.0;			// angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);
			//gl.glFrustum(-d,((2.0*w)/h -1.0)*d, -d, d, near, far);
			double anchovisible = 4.5/3.0;
			gl.glFrustum(-anchovisible*d,((2.0*w)/h -anchovisible)*d, -d, d, near, far);
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}
	}
	
	private Thread lector, animador;
	
	public VisorCliente3D(ReadableByteChannel channelIn,  WritableByteChannel channelOut){
		//ComparadorDeElementos comparador = new ComparadorDeElementos();
		cache = new Cache<Instancia3D>(500);

		naves = new Lista<Instancia3D>();
		disparos = new Lista<Instancia3D>();
		
		this.channelIn = channelIn;
		this.channelOut = channelOut;
		buff = ByteBuffer.allocateDirect(8192); 
		
		key_state = 0;
		
		datosRecibidos = false;
		pantallaPintada = false;
		
		Sincronizador sincronizador = new Sincronizador();
		lector = new Lector(sincronizador);
		animador = new Animador(sincronizador);
		
		pantalla = new GLCanvas(new GLCapabilities());

		pantalla.setIgnoreRepaint( true );
		pantalla.addKeyListener(this);
//		pantalla.setFocusable(true);
//		pantalla.setFocusTraversalKeysEnabled(false);
//		pantalla.requestFocusInWindow();
//		pantalla.getContext().getGL();
	}
	
	public void start(){
		pantalla.requestFocus();
		final Renderer renderer = new Renderer();
		pantalla.addGLEventListener(renderer);
		pantalla.display();
		animador.start();
		lector.start();
	}
	
	protected void recibir(ByteBuffer productor, List<Instancia3D> consumidor){
		int i = 0, nElementos = productor.getInt();
		ListIterator<Instancia3D> iConsumidor = consumidor.listIterator();

		if(iConsumidor.hasNext() && i < nElementos){
			short tipo = productor.getShort();
			short id = productor.getShort();
			i++;
			Instancia3D almacenado = iConsumidor.next();
			
			do{
				if(almacenado.tipo < tipo || ( almacenado.tipo == tipo && almacenado.id < id )){
					cache.cached(almacenado);
					iConsumidor.remove();
					if(!iConsumidor.hasNext()){
						Instancia3D nuevo = cache.newObject(tipo);
						nuevo.setId(id);
						nuevo.recibir(productor);
						consumidor.add(nuevo);
						break;
					}
					almacenado = iConsumidor.next();
				}else if(almacenado.tipo > tipo || ( almacenado.tipo == tipo && almacenado.id > id )){
					Instancia3D nuevo = cache.newObject(tipo);
					nuevo.setId(id);
					nuevo.recibir(productor);
					iConsumidor.add(nuevo);
					if(i == nElementos){
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
		// Si se reciben mas elementos de los q hay, se crean.
		while(i < nElementos){
			i++;
			Instancia3D nuevo = cache.newObject(productor.getShort());
			nuevo.setId(productor.getShort());
			nuevo.recibir(productor);
			consumidor.add(nuevo);
		}
		while(iConsumidor.hasNext()){
			cache.cached(iConsumidor.next());
			iConsumidor.remove();
		}
		if(consumidor.size()!= nElementos)
			System.out.println("Error: "+nElementos+"\t"+consumidor.size());
	}
	
	public Component getPantalla(){
		return pantalla;
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

