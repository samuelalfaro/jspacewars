package org.sam.j3d.particulas;

import javax.media.j3d.*;
import javax.vecmath.*;

import org.sam.interpoladores.Getter;
import org.sam.util.*;

public abstract class Particulas extends Shape3D implements Prototipo<Particulas>, Modificable{

	/**
	 * Establece el tipo de emision, con este parametro, el sistema finaliza cuando todas
	 * las particulas han muerto
	 */
	public static final int EMISION_UNICA = 0;
	/**
	 * Establece el tipo de emision, con este parametro, cuando todas las particulas han
	 * muerto, comienza la emision de nuevo
	 */
	public static final int EMISION_PULSOS = 1;
	/**
	 * Establece el tipo de emision, con este parametro, cuando muere una particula, vuelve
	 * a regenerarse
	 */
	public static final int EMISION_CONTINUA = 2;
	
	/**
	 * Numero de particulas. Debe ser un valor positivo.
	 */
	public final int nParticulas;
	//private final GeometryUpdater iniciador;

	/**
	 * Numero de particulas activas.
	 */
	protected int particulasActivas;
	protected int modoDeEmision;
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
	protected Emisor emisor;
	protected Modificador modificador;
	/**
	 * Incremento de vida por segundo. Debe ser un valor positivo.
	 */
	protected float iVida;
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
	protected float giro;
	/**
	 * Pertubacion aleatoria del spin de la particula
	 */
	protected float pGiro;
	/**
	 * Parametro que indica si el sentido del giro es aleatorio.
	 */
	protected boolean sentidoGiroAleatorio;
	
	protected Transform3D tExterna;
	protected Transform3D tLocal;
	
	protected Particulas(int nParticulas){
		this.nParticulas = nParticulas;

		modoDeEmision = Particulas.EMISION_UNICA;
		particulasActivas = 0;
		
		iVida = 1.0f;
		rangoDeEmision = 0.0f;
		
		velocidad = 1.0f;
		pVelocidad = 0.0f;
		sentidoVelocidadAleatorio = false;

		fuerza = new Vector3f(0.0f,0.0f,0.0f);

		giro  = 0.0f;
		pGiro = 0.0f;
		sentidoGiroAleatorio = false;

		emisor = new EmisorPuntual();
		
		GeometryArray geometria = crearGeometria(nParticulas);
		this.setGeometry(geometria);
	}
	
	protected abstract GeometryArray crearGeometria(int nParticulas);
	
	protected Particulas(Particulas me){
		GeometryArray geometria = crearGeometria(me.nParticulas);
		this.setGeometry(geometria);
		this.setAppearance(me.getAppearance());
		
		nParticulas = me.nParticulas;
		modoDeEmision = me.modoDeEmision;
		particulasActivas = 0;
		
		iVida = me.iVida;
		rangoDeEmision = me.rangoDeEmision;
		
		velocidad = me.velocidad;
		pVelocidad = me.pVelocidad;
		sentidoVelocidadAleatorio = me.sentidoVelocidadAleatorio;

		fuerza = new Vector3f();
		fuerza.set(me.fuerza);

		giro  = me.giro;
		pGiro = me.pGiro;
		sentidoGiroAleatorio = me.sentidoGiroAleatorio;

		emisor = me.emisor;
		
		this.tExterna = me.tExterna;
		this.tLocal = me.tLocal;
	}
	
	public abstract void iniciar();
	
	public final void setEmisor(Emisor emisor) {
		this.emisor = emisor;
	}
	
	public abstract void setModoDeEmision(int modoDeEmision);
	
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
		giro = giro * iVida * tVida;
		pGiro = pGiro * iVida * tVida;
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
	
	public final void setGiro(float giro){
		// Pasamos del dominino del tiempo al dominio de la vida y de grados a radianes.	
		this.giro = (float)(giro*Math.PI/(180 * iVida));
	}

	public final void setGiro(float giro, float pGiro, boolean sentidoGiroAleatorio){
		// Pasamos del dominino del tiempo al dominio de la vida y de grados a radianes.
		this.giro = (float)(giro*Math.PI/(180 * iVida));
		this.pGiro = (float)(pGiro*Math.PI/(180 * iVida));
		this.sentidoGiroAleatorio = sentidoGiroAleatorio;
	}

	public final void setTExterna(Transform3D externa) {
		tExterna = externa;
	}

	public final void setTLocal(Transform3D local) {
		tLocal = local;
	}
	
	private final Transform3D tCompuesta = new Transform3D();
	
	protected final Transform3D getTransform(){
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
	
	public abstract void setEscala(float escala);
	
	public abstract void setEscala(float escalaX, float escalaY);

	public abstract void setEscala(Getter.Float<Float> iEscalas);
	
	public abstract void setEscala(Getter.Float<Float> iEscalaX, Getter.Float<Float> iEscalaY);
	
	public abstract void setColor( Color3f color, float alfa);
	
	public abstract void setColor( float r, float g, float b, float a);
	
	public abstract void setColor( Color3f color, Getter.Float<Float> iAlfa);
	
	public abstract void setColor( float r, float g, float b, Getter.Float<Float> iAlfa);
	
	public abstract void setColor( Getter.Float<float[]> iColores, float alfa);
	
	public abstract void setColor( Getter.Float<float[]> iColores, Getter.Float<Float> iAlfa);
	
	public abstract void setColorModulado( boolean colorModulado);

	public final void setTexture(Texture texture){
		TextureUnitState tus[] = this.getAppearance().getTextureUnitState();
		tus[0].setTexture(texture);
	}
	
	public final Modificador getModificador(){
		return modificador;
	}
	
	public abstract Particulas clone();
}