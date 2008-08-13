package org.sam.jogl.particulas;

import javax.vecmath.*;

import org.sam.interpoladores.Getter;
import org.sam.jogl.Dibujable;
import org.sam.util.*;

public abstract class Particulas implements Prototipo<Particulas>, Modificable, Dibujable{

	public enum Emision{
		/**
		 * Establece el tipo de emision, con este parametro, el sistema finaliza cuando todas
		 * las particulas han muerto
		 */
		UNICA,
		/**
		 * Establece el tipo de emision, con este parametro, cuando todas las particulas han
		 * muerto, comienza la emision de nuevo
		 */
		PULSOS,
		/**
		 * Establece el tipo de emision, con este parametro, cuando muere una particula, vuelve
		 * a regenerarse
		 */
		CONTINUA
	}
	
	/**
	 * Numero de particulas. Debe ser un valor positivo.
	 */
	public final int nParticulas;

	/**
	 * Numero de particulas activas.
	 */
	protected transient int particulasActivas;

	protected Emisor emisor;
	protected Emision emision;
	/**
	 * Rango durante el cual se emiten las particulas.<br> 
	 * Debe ser un valor comprendido entre * 0.0 y 1.0
	 * <list>
	 * <li> Valor = 0.0 Todas las particulas se emiten a la vez.
	 * <li> ...
	 * <li> Valor = 0.5 Todas las particulas se emiten durante el la mitad del tiempo de vida de una particula
	 * <li> ...
	 * <li> Valor = 1.0 Todas las particulas se emiten durante el del tiempo de vida de una particula
	 * </list>
	 */
	protected float rangoDeEmision;
	protected transient Modificador modificador;
	/**
	 * Incremento de vida por segundo. Debe ser un valor positivo.
	 */
	protected transient float iVida;
	/**
	 * Velocidad lineal de la particula, en unidades por segundo.
	 */
	protected float velocidad;
	/**
	 * Pertubacion aleatoria de la velocidad en unidades por segundo.<br>
	 * Es un valor absoluto.<br>
	 * Este valor modifica la velocidad segun la formula:<br>
	 * velocidad final = velocidad + (aleatorioEntre(0,1) - 0.5) * pertubacion.
	 */
	protected float pVelocidad;
	/**
	 * Parametro que indica si el sentido de la velocicidad es aleatorio.
	 */
	protected boolean sentidoVelocidadAleatorio;
	/**
	 * Fuerza que sufren todas las particulas en unidades por segundo al cuadrado.
	 */
	protected Vector3f fuerza;
	/**
	 * Giro sobre el centro de la textura de la particula. En grados por segundo.
	 */
	protected float velGiro;
	/**
	 * Pertubacion aleatoria del giro de la particula
	 */
	protected float pVelGiro;
	/**
	 * Parametro que indica si el sentido del giro es aleatorio.
	 */
	protected boolean sentidoVelGiroAleatorio;
	
	protected Matrix4f tExterna;
	protected Matrix4f tLocal;
	
	private transient final Matrix4f tCompuesta; 
	
	protected Particulas(int nParticulas){
		this.nParticulas = nParticulas;

		emision = Emision.UNICA;
		particulasActivas = 0;
		
		iVida = 1.0f;
		rangoDeEmision = 0.0f;
		
		velocidad = 1.0f;
		pVelocidad = 0.0f;
		sentidoVelocidadAleatorio = false;

		fuerza = new Vector3f(0.0f,0.0f,0.0f);

		velGiro  = 0.0f;
		pVelGiro = 0.0f;
		sentidoVelGiroAleatorio = false;

		emisor = new Emisor.Puntual();
		tCompuesta = new Matrix4f();
		tCompuesta.setIdentity();
		
	}
	
	protected Particulas(Particulas me){
		
		nParticulas = me.nParticulas;
		emision = me.emision;
		particulasActivas = 0;
		
		iVida = me.iVida;
		rangoDeEmision = me.rangoDeEmision;
		
		velocidad = me.velocidad;
		pVelocidad = me.pVelocidad;
		sentidoVelocidadAleatorio = me.sentidoVelocidadAleatorio;

		fuerza = new Vector3f();
		fuerza.set(me.fuerza);

		velGiro  = me.velGiro;
		pVelGiro = me.pVelGiro;
		sentidoVelGiroAleatorio = me.sentidoVelGiroAleatorio;

		emisor = me.emisor;
		
		this.tExterna = me.tExterna;
		this.tLocal = me.tLocal;
		
		tCompuesta = new Matrix4f();
		tCompuesta.setIdentity();
	}
	
	public abstract void iniciar();
	
	public final void setEmisor(Emisor emisor) {
		this.emisor = emisor;
	}
	
	public abstract void setEmision(Emision emision);
	
	public final void setRangoDeEmision(float rangoDeEmision) {
		if(rangoDeEmision < 0.0f || rangoDeEmision > 1.0f)
			throw new IllegalArgumentException("El rango de emision debe estar comprendido entre 0.0 y 1.0");
		this.rangoDeEmision = rangoDeEmision;
	}
	
	public void setTiempoVida(float tVida){
		if(tVida <= 0.0f)
			throw new IllegalArgumentException("El tiempo de vida debe de ser mayor de 0");
		/*
		Pasamos todos los valores que estaban en funcion de la vida otra vez a funcion
		del tiempo y los volvemos a poner en funcion del nuevo indice de vida.
		*/
		velocidad  = velocidad  * iVida * tVida;
		pVelocidad = pVelocidad * iVida * tVida;
		velGiro = velGiro * iVida * tVida;
		pVelGiro = pVelGiro * iVida * tVida;
		fuerza.scale((iVida*iVida)*(tVida*tVida));
		iVida = 1.0f/tVida;
	}
	
	public final void setVelocidad(float velocidad){
		// Pasamos del dominino del tiempo al dominio de la vida.
		this.velocidad = velocidad / iVida;
	}

	public final void setVelocidad(float velocidad, float pVelocidad, boolean sentidoVelocidadAleatorio){
		// Pasamos del dominino del tiempo al dominio de la vida.
		this.velocidad  = velocidad  / iVida;
		this.pVelocidad = pVelocidad / iVida;
		this.sentidoVelocidadAleatorio = sentidoVelocidadAleatorio;
	}
	
	public void setFuerza(Tuple3f fuerza){
		this.fuerza.set(fuerza);
		// Pasamos del dominino del tiempo al dominio de la vida.
		this.fuerza.scale(1.0f/(iVida*iVida));
	}
	
	public void setFuerza(float x, float y, float z){
		this.fuerza.set(x,y,z);
		// Pasamos del dominino del tiempo al dominio de la vida.
		this.fuerza.scale(1.0f/(iVida*iVida));
	}
	
	public final void setVelocidadGiro(float velGiro){
		// Pasamos del dominino del tiempo al dominio de la vida y de grados a radianes.	
		this.velGiro = (float)(velGiro*Math.PI/(180 * iVida));
	}

	public final void setVelocidadGiro(float velGiro, float pVelGiro, boolean sentidoVelGiroAleatorio){
		// Pasamos del dominino del tiempo al dominio de la vida y de grados a radianes.
		this.velGiro = (float)(velGiro*Math.PI/(180 * iVida));
		this.pVelGiro = (float)(pVelGiro*Math.PI/(180 * iVida));
		this.sentidoVelGiroAleatorio = sentidoVelGiroAleatorio;
	}

	public final void setTExterna(Matrix4f externa) {
		tExterna = externa;
	}

	public final void setTLocal(Matrix4f local) {
		tLocal = local;
	}
	
	protected final Matrix4f getTransform(){
		if(tExterna != null){
			if(tLocal != null){
				tCompuesta.mul(tExterna,tLocal);
				return tCompuesta;
			}
			return tExterna;
		}
		if(tLocal != null)
			return tLocal;
		tCompuesta.setIdentity();
		return tCompuesta;
	}
	
	public abstract void setRadio(float radio);

	public abstract void setRadio(Getter.Float<Float> radio);
	
	public abstract void setSemiEje(float semiEje);

	public abstract void setSemiEje(Getter.Float<Float> semiEje);
	
	public final void setColor( Color4f color){
		setColor(color.x, color.y, color.z, color.w);
	}
	
	public abstract void setColor( float r, float g, float b, float a);
	
	public final void setColor( Color3f color, Getter.Float<Float> iAlfa){
		setColor(color.x, color.y, color.z, iAlfa);
	}
	
	public abstract void setColor( float r, float g, float b, Getter.Float<Float> iAlfa);
	
	public abstract void setColor( Getter.Float<float[]> iColores, float alfa);
	
	public abstract void setColor( Getter.Float<float[]> iColores, Getter.Float<Float> iAlfa);
	
	public final Modificador getModificador(){
		return modificador;
	}
	
	public abstract Particulas clone();
}