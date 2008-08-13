package org.sam.elementos;


public class Misil extends Disparo{
	private transient float angulo, velocidad;
	private transient Elemento objetivo;

	Misil(short code){
		super(code);
	}
	
	protected Misil(Misil prototipo){
		super(prototipo);
	}
	
	public Misil clone() {
		return new Misil(this);
	}
	
	public void setValues(float posX, float posY, float angulo, float velocidad){
		super.setPosicion(posX, posY);
		this.angulo = angulo;
		this.velocidad = velocidad/1000.0f;
	}
	
	public void setObjetivo(Elemento objetivo){
		this.objetivo = objetivo;
	}
	
	public boolean finalizado(){
		if(objetivo == null)
			return false;
		float dx = objetivo.posX - posX;
		float dy = objetivo.posY - posY;
		return (Math.sqrt(dx*dx+dy*dy) < 10);
	}
	
	private static int signo(double d){
		return d<0?-1:1;
	}	

	private static final float PI2 = (float)(2*Math.PI);  
	
	private static float diferenciaAngulos(float a1, float a2){
		float r = a2 -a1;
		float absR = Math.abs(r);
		if(absR > Math.PI)
			r = signo(r)*(absR - PI2);
		return r;
	}	
	
	public void actua(long milis){
		//TODO ajustar valores angulo
		try{
			float angOb = (float)Math.atan2( (objetivo.posY - posY),(objetivo.posX - posX));
			float dif = diferenciaAngulos(angulo,angOb);
			if(Math.abs(dif) < 0.1 )
				angulo = angOb;
			else
				angulo += 0.0075*milis*signo(dif);
		}catch(NullPointerException noHayObjetivo){
		}
		
		float vAct = velocidad * milis;

		posX += vAct * (float)Math.cos(angulo);
		posY += vAct * (float)Math.sin(angulo);
	}
	
	public double getAngulo(){
		return angulo;
	}
}