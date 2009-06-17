package org.sam.red;

public class Semaforo {
	
	private int s;
	@SuppressWarnings("unused")
	private int esp;
	
	public Semaforo(int init_value){
		s = init_value;
		esp = 0;
	}
	/*
	public synchronized void esperar(){
		if(s==0){ 
			esp++;
			try{
				wait();
			}catch(InterruptedException e){
			}
		}else
			s--;
	}
	
	public synchronized void notificar(){
		if(esp>0){
			notify();
			esp--;
		}else
			s++;
	}
	
	public synchronized int valor(){
		return s;
	}
	/*/
	
	public void esperar(){
		while(s==0) 
			Thread.yield();
		s--;
	}
	
	public void notificar(){
		s++;
		Thread.yield();
	}
	
	public int valor(){
		return s;
	}
	//*/
}