package org.sam.red.cliente;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import javax.swing.JPanel;

import org.sam.red.KeysState;
import org.sam.red.Sincronizador;
import org.sam.red.XMLHandlerForma;
import org.sam.util.Cache;
import org.sam.util.Lista;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class VisorCliente implements KeyListener{

	private final static int K_SUBE		= KeyEvent.VK_UP;
	private final static int K_BAJA		= KeyEvent.VK_DOWN;
	private final static int K_ACELERA	= KeyEvent.VK_RIGHT;
	private final static int K_FRENA	= KeyEvent.VK_LEFT;
	private final static int K_DISPARO	= KeyEvent.VK_SHIFT;
	private final static int K_BOMBA	= KeyEvent.VK_SPACE;
	private final static int K_UPGRADE	= KeyEvent.VK_CONTROL;
	
	private final JPanel pantalla;
	private final ReadableByteChannel channelIn;
	private final WritableByteChannel channelOut;
	private ByteBuffer buff;
	
	private Cache<Elemento> cache;
	private List<Elemento> naves;
	private List<Elemento> disparos;
	
	private	int	key_state = 0;

	private boolean datosRecibidos, pantallaPintada;
//	private boolean iniciado = false;
	
	private class Lector extends Thread{
		
		private final Sincronizador sincronizador;
		//private final InterruptedException terminado = new InterruptedException();
		
		public Lector(Sincronizador sincronizador){
			this.sincronizador = sincronizador;
		}
		
		public void run(){
//			while(!iniciado)
//				Thread.yield();
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
				pantalla.repaint();
				try{
					Thread.sleep(35);
				}catch(InterruptedException e){
				}
				pantallaPintada = true;
				Thread.yield();
				sincronizador.notificar();
			}
		}
	}

	private Thread lector, animador;

	@SuppressWarnings("serial")
	public VisorCliente(ReadableByteChannel channelIn,  WritableByteChannel channelOut){

		Queue<Polygon> formas = new LinkedList<Polygon>();
		try{
			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(new XMLHandlerForma(parser,null,formas));
			parser.parse("resources/xml/elementos.xml");
		}catch (Exception e){
			System.err.println ("Error al procesar el fichero de elementos: "+ e.getMessage());
			e.printStackTrace();
		}
		Polygon formaNave = formas.poll();
		Polygon formaDisparo = formas.poll();

		//ComparadorDeElementos comparador = new ComparadorDeElementos();
		cache = new Cache<Elemento>(500);
		cache.addPrototipo(new Nave(0x100,formaNave));
		cache.addPrototipo(new Disparo(1,formaDisparo));
		cache.addPrototipo(new Disparo(2,formaDisparo));
		cache.addPrototipo(new Disparo(3,formaDisparo));
		cache.addPrototipo(new Disparo(4,formaDisparo));
		cache.addPrototipo(new Disparo(5,formaDisparo));
		cache.addPrototipo(new Disparo(6,formaDisparo));
		cache.addPrototipo(new Disparo(7,formaDisparo));
		cache.addPrototipo(new Disparo(10,formaDisparo));
		cache.addPrototipo(new Disparo(20,formaDisparo));

		naves = new Lista<Elemento>();
		disparos = new Lista<Elemento>();

		this.channelIn = channelIn;
		this.channelOut = channelOut;
		buff = ByteBuffer.allocateDirect(8192); 
		
		key_state = 0;
		
		datosRecibidos = false;
		pantallaPintada = false;
		
		Sincronizador sincronizador = new Sincronizador();
		lector = new Lector(sincronizador);
		animador = new Animador(sincronizador);
		
		pantalla = new JPanel(){
			public void paintComponent(Graphics g){
				g.setColor(Color.BLACK);
				g.fillRect(0,0,getWidth(),getHeight());

				g.setColor(Color.YELLOW);
				Iterator <Elemento> i = disparos.iterator();
				while(i.hasNext())
					g.fillPolygon(i.next().getForma());

				g.setColor(Color.CYAN);
				Iterator <Elemento> in = naves.iterator();
				while(in.hasNext())
					g.fillPolygon(in.next().getForma());
			}
		};

		pantalla.addKeyListener(this);
		pantalla.setFocusable(true);
		pantalla.setFocusTraversalKeysEnabled(false);
	}

	public void start(){
		pantalla.requestFocus();
		animador.start();
		lector.start();
	}
	
	protected void recibir(ByteBuffer productor, List<Elemento> consumidor){
		int i = 0, nElementos = productor.getInt();
		ListIterator<Elemento> iConsumidor = consumidor.listIterator();

		if(iConsumidor.hasNext() && i < nElementos){
			short tipo = productor.getShort();
			short id = productor.getShort();
			i++;
			Elemento almacenado = iConsumidor.next();
			
			do{
				if(almacenado.tipo < tipo || ( almacenado.tipo == tipo && almacenado.id < id )){
					cache.cached(almacenado);
					iConsumidor.remove();
					if(!iConsumidor.hasNext()){
						Elemento nuevo = cache.newObject(tipo);
						nuevo.setId(id);
						nuevo.recibir(productor);
						consumidor.add(nuevo);
						break;
					}
					almacenado = iConsumidor.next();
				}else if(almacenado.tipo > tipo || ( almacenado.tipo == tipo && almacenado.id > id )){
					Elemento nuevo = cache.newObject(tipo);
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
			Elemento nuevo = cache.newObject(productor.getShort());
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

