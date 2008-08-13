package org.sam.j3d;

import java.util.Enumeration;

import javax.media.j3d.*;

import org.sam.util.Modificador;

public class ActualizadorBehavior extends Behavior{
	private final WakeupCriterion elapsedFrames;
	private long  tAnterior;
	private final Modificador modificador;
	
	public ActualizadorBehavior(Modificador modificador,int frames, boolean pasive){
		this.modificador = modificador;
		elapsedFrames = new WakeupOnElapsedFrames(frames, pasive);
	}
	
	public void initialize(){
		this.tAnterior = System.nanoTime();
		wakeupOn(elapsedFrames);
	}

	@SuppressWarnings("unchecked")
	public void processStimulus(Enumeration criteria) {
		long tActual = System.nanoTime();
		float steep = (float)((tActual - tAnterior)/1.0e9);
		modificador.modificar(steep);
		tAnterior = tActual;
		wakeupOn(elapsedFrames);
	}
}