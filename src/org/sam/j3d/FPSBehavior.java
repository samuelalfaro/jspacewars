package org.sam.j3d;

import java.util.Enumeration;

import javax.media.j3d.*;

public class FPSBehavior extends Behavior{
	private final WakeupCriterion elapsedFrames = new WakeupOnElapsedFrames(0);
	private long  tAnterior;
	private int   fps;
	
	public FPSBehavior(){
	}
	
	public void initialize(){
		this.tAnterior = System.nanoTime();
		wakeupOn(elapsedFrames);
		fps = 0;
	}

	@SuppressWarnings("unchecked")
	public void processStimulus(Enumeration criteria) {
		fps++;
		long tActual = System.nanoTime();
		if(tActual - tAnterior > 2.0e9){
			System.out.println("FPS: "+ (fps/2));
			fps = 0;
			tAnterior = tActual + (tActual - tAnterior - (long)2.0e9);
		}
		wakeupOn(elapsedFrames);
	}
}