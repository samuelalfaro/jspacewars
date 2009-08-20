package org.sam.jspacewars.cliente;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import org.sam.elementos.Cache;
import org.sam.elementos.Modificador;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.ObjetosOrientables;
import org.sam.jogl.fondos.Fondo;
import org.sam.jspacewars.DataGame;
import org.sam.jspacewars.Marco;
import org.sam.jspacewars.elementos.KeysState;
import org.sam.util.Lista;

public class Visor3D implements KeyListener{
	
	private final static int K_SUBE		= KeyEvent.VK_UP;
	private final static int K_BAJA		= KeyEvent.VK_DOWN;
	private final static int K_ACELERA	= KeyEvent.VK_RIGHT;
	private final static int K_FRENA	= KeyEvent.VK_LEFT;
	private final static int K_DISPARO	= KeyEvent.VK_SHIFT;
	private final static int K_BOMBA	= KeyEvent.VK_SPACE;
	private final static int K_UPGRADE	= KeyEvent.VK_CONTROL;
	
	private Marco marco;
//	private final GLCanvas canvas;
	private final ReadableByteChannel channelIn;
	private final WritableByteChannel channelOut;
	private ByteBuffer buff;
	
	private DataGame dataGame;
	private List<Instancia3D> elementos;
	private final Collection<Modificador> modificadores;
	
	private	int	key_state = 0;
	
//	private boolean pantallaPintada;
	
	private class Lector extends Thread{
		
		private final Cache<Instancia3D> cache = dataGame.getCache();
//		private final Sincronizador sincronizador;
		
		private final transient int[] nivelesFijos = new int[5];
		private final transient int[] nivelesActuales = new int[5];
		private final transient int[] nivelesDisponibles = new int[5];
		
//		public Lector(Sincronizador sincronizador){
//			this.sincronizador = sincronizador;
//		}
		
		private void recibir(ByteBuffer productor, List<Instancia3D> consumidor){
			marco.setNumBombas(productor.getInt());
			marco.setNumVidas(productor.getInt());
			marco.setPuntos(productor.getInt());
			
			for(int i= 0; i< nivelesFijos.length; i++)
				nivelesFijos[i] = productor.getInt();
			marco.setNivelesFijos(nivelesFijos);
			
			for(int i= 0; i< nivelesActuales.length; i++)
				nivelesActuales[i] = productor.getInt();
			marco.setNivelesActuales(nivelesActuales);
			
			for(int i= 0; i< nivelesDisponibles.length; i++)
				nivelesDisponibles[i] = productor.getInt();
			marco.setNivelesDisponibles(nivelesDisponibles);
			
			marco.setIndicador( productor.getInt() );
			marco.setGrado( productor.getInt() );
			
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

				recibir(buff, elementos);
//				pantallaPintada = false;
				
				
				marco.actualizar();
//				while(!pantallaPintada)
//					sincronizador.esperar();
			}
		}
	}
	
	private class Renderer implements GLEventListener{

//		private final Sincronizador sincronizador;
		private final transient GLU glu = new GLU();
		private transient long tAnterior, tActual;
		
		private final transient Fondo fondo;
		
//		private Renderer(Sincronizador sincronizador){
//			this.sincronizador = sincronizador;
//			this.fondo = dataGame.getFondo();
//			tActual = System.nanoTime();
//		}
		
		private Renderer(){
			this.fondo = dataGame.getFondo();
			tActual = System.nanoTime();
		}
		
		public void init(GLAutoDrawable drawable){
			GL gl = drawable.getGL();
			
			gl.glShadeModel(GL.GL_SMOOTH);

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LESS);

			gl.glEnable(GL.GL_LIGHT0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{  0.5f,  1.0f,  1.0f,  0.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,  new float[]{  0.4f,  0.6f,  0.6f,  1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, new float[]{  0.0f,  0.0f,  0.0f,  1.0f }, 0);

			gl.glEnable(GL.GL_LIGHT1);
			gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, new float[]{ -0.5f, -1.0f,  1.0f,  0.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE,  new float[]{  0.6f,  0.4f,  0.4f,  1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, new float[]{  0.0f,  0.0f,  0.0f,  1.0f }, 0);

			gl.glEnable(GL.GL_LIGHT2);
			gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, new float[]{  0.0f,  0.0f,  1.0f,  0.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE,  new float[]{  0.0f,  0.0f,  0.0f,  1.0f }, 0);
			gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPECULAR, new float[]{  0.95f, 0.95f, 0.95f, 1.0f }, 0);

			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_CULL_FACE);
		}
		
		public void display(GLAutoDrawable drawable){
			
			tAnterior = tActual;
			tActual = System.nanoTime();
			float incT = (float)(tActual - tAnterior)/ 1000000000;
			
			GL gl = drawable.getGL();
			
			fondo.getModificador().modificar(incT);
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
			
//			pantallaPintada = true;
//			sincronizador.notificar();
		}

		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){}

		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
			GL gl = drawable.getGL();
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
	}
	
	public Visor3D(DataGame dataGame, ReadableByteChannel channelIn,  WritableByteChannel channelOut){
		//ComparadorDeElementos comparador = new ComparadorDeElementos();
		this.dataGame = dataGame;

		elementos = new Lista<Instancia3D>();
		modificadores = new LinkedList<Modificador>();
		
		this.channelIn = channelIn;
		this.channelOut = channelOut;
		buff = ByteBuffer.allocateDirect(8192); 
		
		key_state = 0;
	}
	
	public void start(){
		
//		Sincronizador sincronizador = new Sincronizador();
		
		new Lector().start();
		marco = Marco.getNewMarco(0, dataGame.getGLContext(), new Renderer());
		marco.addKeyListener(this);
//		marco.requestFocus();
		
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
	
	public Container getPanel(){
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
			case KeyEvent.VK_C: System.out.println(dataGame.getCache());		break;
			default:
				if ((keyCode == KeyEvent.VK_ESCAPE)
						|| (keyCode == KeyEvent.VK_Q)
						|| (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && keyEvent.isControlDown()) )
						System.exit(0);
		}
	}
	
	public void keyTyped(KeyEvent keyEvent) {}
}