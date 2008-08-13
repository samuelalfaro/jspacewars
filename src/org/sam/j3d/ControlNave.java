package org.sam.j3d;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.media.j3d.*;
import javax.vecmath.Vector3f;

/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

public class ControlNave extends Behavior implements KeyListener{
	
	private static final float ALTO		= 7.5f;
	private static final float ANCHO	= 11.0f;
	
	private static final int SUBE		= (1<<0);
	private static final int BAJA		= (1<<1);
	private static final int ACELERA	= (1<<2);
	private static final int FRENA		= (1<<3);
	private static final int DISPARO	= (1<<4);
	private static final int BOMBA		= (1<<5);
	private static final int UPGRADE	= (1<<6);

	private	int	key_state = 0;
	
	private final static int K_SUBE		= KeyEvent.VK_UP;
	private final static int K_BAJA		= KeyEvent.VK_DOWN;
	private final static int K_ACELERA	= KeyEvent.VK_RIGHT;
	private final static int K_FRENA	= KeyEvent.VK_LEFT;
	private final static int K_DISPARO	= KeyEvent.VK_SHIFT;
	private final static int K_BOMBA	= KeyEvent.VK_SPACE;
	private final static int K_UPGRADE	= KeyEvent.VK_CONTROL;
	
	private WakeupCriterion		w1 = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
	private WakeupCriterion 	w2 = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
	private WakeupCriterion 	w3 = new WakeupOnElapsedFrames(0);
	private WakeupCriterion[]	warray = { w1, w2, w3 };
	private WakeupCondition 	w = new WakeupOr(warray);
	private KeyEvent 			eventKey;
	
	private boolean listener = false;
	private LinkedList<KeyEvent> eventq;
	
	private TransformGroup  targetTG;
	private GestorDeElementos gestorDeDisparos;
	private float			rotX;
	private Transform3D     posNave;
	private Vector3f 	    posCoor;
	private float			velocidad;
	private float			ultimoDisparo;
	private long  tAnterior;
	
	public ControlNave(TransformGroup targetTG, Transform3D posNave, GestorDeElementos gestorDeDisparos){
		this.targetTG = targetTG;
		this.gestorDeDisparos = gestorDeDisparos;
		this.posNave = posNave;
		rotX = 0.0f;
		posCoor = new Vector3f();
		velocidad = 10.0f;
		ultimoDisparo = 0.0f;
	}
	
	public ControlNave(Component c, TransformGroup targetTG, Transform3D posNave, GestorDeElementos gestorDeDisparos) {
		this(targetTG, posNave, gestorDeDisparos);
		if (c != null) {
			c.addKeyListener(this);
		}
		listener = true;	
	}
	
	public void initialize(){
		if (listener) {
			w1 = new WakeupOnBehaviorPost(this, KeyEvent.KEY_PRESSED);
			w2 = new WakeupOnBehaviorPost(this, KeyEvent.KEY_RELEASED);
			warray[0] = w1;
			warray[1] = w2;
			w = new WakeupOr(warray);
			eventq = new LinkedList<KeyEvent>();
		}
		this.tAnterior = System.nanoTime();
		wakeupOn(w);
	}

	@SuppressWarnings("unchecked")
	public void processStimulus(Enumeration criteria) {
		long tActual = System.nanoTime();
		float steep = (float)((tActual - tAnterior)/1.0e9);
		
		WakeupOnAWTEvent ev;
		WakeupCriterion genericEvt;
		AWTEvent[] events;
		boolean sawFrame = false;
		
		while (criteria.hasMoreElements()) {
			genericEvt = (WakeupCriterion) criteria.nextElement();
			if (genericEvt instanceof WakeupOnAWTEvent) {
				ev = (WakeupOnAWTEvent) genericEvt;
				events = ev.getAWTEvent();
				processAWTEvent(events);
			} else if (genericEvt instanceof WakeupOnElapsedFrames && eventKey != null){
				sawFrame = true;
			} else if ((genericEvt instanceof WakeupOnBehaviorPost)) {
				while(true) {
					synchronized (eventq) {
						if (eventq.isEmpty()) break;
						eventKey  = eventq.remove(0);
						if (eventKey.getID() == KeyEvent.KEY_PRESSED ||
								eventKey.getID() == KeyEvent.KEY_RELEASED) {
							processKeyEvent(eventKey);
						}
					}
				}
			}
		}
		if (sawFrame)
			integrateTransformChanges(steep);
		tAnterior = tActual;
		wakeupOn(w);
	}

	public void addListener(Component c) {
		if (!listener) {
			throw new IllegalStateException();
		}
		c.addKeyListener(this);
	}
	
	public void keyPressed(KeyEvent evt) {
		// add new event to the queue
		// must be MT safe
		synchronized (eventq) {
			eventq.add(evt);
			// only need to post if this is the only event in the queue
			if (eventq.size() == 1) postId(KeyEvent.KEY_PRESSED);
		}
	}

	public void keyReleased(KeyEvent evt) {
		// add new event to the queue
		// must be MT safe
		synchronized (eventq) {
			eventq.add(evt);
			// only need to post if this is the only event in the queue
			if (eventq.size() == 1) postId(KeyEvent.KEY_RELEASED);
		}
	}

	public void keyTyped(KeyEvent evt) {}
	
	
	private void processAWTEvent(AWTEvent[] events) {
		for (int loop = 0; loop < events.length; loop++) {
			if (events[loop] instanceof KeyEvent) {
				eventKey = (KeyEvent) events[loop];
				if (eventKey.getID() == KeyEvent.KEY_PRESSED ||
						eventKey.getID() == KeyEvent.KEY_RELEASED) {
					processKeyEvent(eventKey);
				}
			}
		}
	}
	
	public void processKeyEvent(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();

		if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
			switch (keyCode) {
			case K_SUBE:	key_state &= ~SUBE;		break;
			case K_BAJA:	key_state &= ~BAJA;		break;
			case K_ACELERA:	key_state &= ~ACELERA;	break;
			case K_FRENA:	key_state &= ~FRENA;	break;
			case K_DISPARO:	key_state &= ~DISPARO;	break;
			case K_BOMBA:	key_state &= ~BOMBA;	break;
			case K_UPGRADE:	key_state &= ~UPGRADE;	break;
			default:
			}
		} else if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
			switch (keyCode) {
			case K_SUBE:	key_state |= SUBE;		break;
			case K_BAJA:	key_state |= BAJA;		break;
			case K_ACELERA:	key_state |= ACELERA;	break;
			case K_FRENA:	key_state |= FRENA;		break;
			case K_DISPARO:	key_state |= DISPARO;	break;
			case K_BOMBA:	key_state |= BOMBA;		break;
			case K_UPGRADE:	key_state |= UPGRADE;	break;
			default:
			}
		}
	}

	private Vector3f posDisparo = new Vector3f();
	private Vector3f posDisparo1 = new Vector3f(1.0f, 0.0f, 0.0f);
	private Vector3f posDisparo2 = new Vector3f(0.75f, 0.25f, 0.0f);
	private Vector3f posDisparo3 = new Vector3f(0.75f, -0.25f, 0.0f);
	private Vector3f posDisparo4 = new Vector3f(0.5f, 0.5f, 0.0f);
	private Vector3f posDisparo5 = new Vector3f(0.5f, -0.5f, 0.0f);
	private Vector3f dirDisparo1 = new Vector3f(1.0f,  0.0f, 0.0f);
	private Vector3f dirDisparo2 = new Vector3f(0.999f,  0.039f, 0.0f);
	private Vector3f dirDisparo3 = new Vector3f(0.999f, -0.039f, 0.0f);
	private Vector3f dirDisparo4 = new Vector3f(0.996f,  0.078f, 0.0f);
	private Vector3f dirDisparo5 = new Vector3f(0.996f, -0.078f, 0.0f);

	
	
	public void integrateTransformChanges(float steep) {
		boolean rotacionModificada = false; 
		if((key_state & SUBE) != 0){
			if (posCoor.y < ALTO) posCoor.y += velocidad*steep;
			if (rotX > -0.5f){
				rotX -= 1.5f*steep;
				if(rotX < -0.5f)
					rotX = -0.5f;
			}
			rotacionModificada = true;
		}else if ((key_state & BAJA) != 0){
			if (posCoor.y > -ALTO) posCoor.y -= velocidad*steep;
			if (rotX < 0.5f){
				rotX += 1.5f*steep;
				if(rotX > 0.5f)
					rotX = 0.5f;
			}
			rotacionModificada = true;
		}
		if((key_state & ACELERA) != 0){
			if (posCoor.x < ANCHO) posCoor.x += velocidad*steep;
		}else if ((key_state & FRENA) != 0){
			if (posCoor.x > -ANCHO) posCoor.x -= velocidad*steep;
		}
		if(!rotacionModificada && rotX != 0){
			if (rotX > 0.0f){
				rotX -= 3.0f*steep;
				if (rotX < 0.0f)
					rotX = 0.0f;
			}
			else{
				rotX += 3.0f*steep;
				if (rotX > 0.0f)
					rotX = 0.0f;
			}
			rotacionModificada = true;
		}
		if (rotacionModificada)
			posNave.rotX(rotX);

		ultimoDisparo += steep;
		if((key_state & DISPARO) != 0 && ultimoDisparo > 0.05f){
			ElementoMovil nuevoDisparo;
			nuevoDisparo = (ElementoMovil)CacheDeElementos.newObject(3); 
			posDisparo.set(posCoor); posDisparo.add(posDisparo1);
			nuevoDisparo.setPosicion(posDisparo);
			nuevoDisparo.setVelocidad(dirDisparo1,30.0f);
			nuevoDisparo.aplicarTransformacion();
			gestorDeDisparos.add(nuevoDisparo);
			
			nuevoDisparo = (ElementoMovil)CacheDeElementos.newObject(2); 
			posDisparo.set(posCoor); posDisparo.add(posDisparo2);
			nuevoDisparo.setPosicion(posDisparo);
			nuevoDisparo.setVelocidad(dirDisparo2,30.0f);
			nuevoDisparo.aplicarTransformacion();
			gestorDeDisparos.add(nuevoDisparo);

			nuevoDisparo = (ElementoMovil)CacheDeElementos.newObject(2); 
			posDisparo.set(posCoor); posDisparo.add(posDisparo3);
			nuevoDisparo.setPosicion(posDisparo);
			nuevoDisparo.setVelocidad(dirDisparo3,30.0f);
			nuevoDisparo.aplicarTransformacion();
			gestorDeDisparos.add(nuevoDisparo);

			nuevoDisparo = (ElementoMovil)CacheDeElementos.newObject(1); 
			posDisparo.set(posCoor); posDisparo.add(posDisparo4);
			nuevoDisparo.setPosicion(posDisparo);
			nuevoDisparo.setVelocidad(dirDisparo4,30.0f);
			nuevoDisparo.aplicarTransformacion();
			gestorDeDisparos.add(nuevoDisparo);

			nuevoDisparo = (ElementoMovil)CacheDeElementos.newObject(1); 
			posDisparo.set(posCoor); posDisparo.add(posDisparo5);
			nuevoDisparo.setPosicion(posDisparo);
			nuevoDisparo.setVelocidad(dirDisparo5,30.0f);
			nuevoDisparo.aplicarTransformacion();
			gestorDeDisparos.add(nuevoDisparo);

			
			ultimoDisparo = 0.0f;
		}
		posNave.setTranslation(posCoor);
		targetTG.setTransform(posNave);
	}
}