package org.sam.jspacewars.servidor.elementos;

import java.util.Collection;

import org.sam.elementos.Cache;
import org.sam.elementos.Prototipo;

/**
 * Clase abstracta que representa a un elemento capaz lanzar {@link Disparo disparos}.
 */
public abstract class Canion implements Prototipo<Canion> {

	/**
	 * {@link Cache} estática empleada por todos los cañones
	 * para obtener las instancias de los disparos que lanzan.
	 */
	protected static Cache<Elemento> cache;

	/**
	 * Método estático que asigna la {@link #cache caché} empleada por todos los cañones.
	 * @param unaCache La caché asignada.
	 */
	public final static void setCache(Cache<Elemento> unaCache) {
		if( cache == null )
			cache = unaCache;
	}

	/**
	 * {@link CanionData Datos} para los distintos grados de este {@code Canion}.
	 */
	protected final CanionData data;

	/**
	 * Coordenada X de la posición relativa de este cañon respecto al elemento donde esta armado.
	 */
	protected float posX;
	/**
	 * Coordenada Y de la posición relativa de este cañon respecto al elemento donde esta armado.
	 */
	protected float posY;
	
	/**
	 * Desfase de disparo, en forma fracional [0..1] que sirve para calcular {@link #tDesfase}.
	 */
	protected float desfase;
	
	/**
	 * Id del {@code Disparo} que lanzará este {@code Canion}.
	 */
	protected transient int idDisparo;
	
	/**
	 * Tiempo de recarga, en nanosegundos, de este {@code Canion}.
	 */
	protected transient long tRecarga;
	
	/**
	 * Desfase de disparo, en nanosegundos, del {@code Canion}.<br/>
	 * Este valor de obtiene multiplicado el {@link #tRecarga} por {@link #desfase  el desfase fracional}.<br/>
	 * Este desfase, se emplea, para que una {@code Nave} con varios {@code Canion}, de las mismas caracteristicas,
	 * en vez de dispararlos a la vez, pueda hacerlo en secuencialmente.
	 */
	protected transient long tDesfase;
	
	/**
	 * Velocidad, en unidades por nanosegundo, a la que sale el {@code Disparo} del {@code Canion}.
	 */
	protected transient float velocidad;

	/**
	 * Contador para acumular el tiempo transcurrido, necesario para poder calcular la
	 * cantidad de disparos lanzados en un determinado periodo.
	 */
	protected transient long tTranscurrido;

	/**
	 * Constructor que crea un {@code Canion} y asigna los valores correspondientes.
	 * @param data {@link #data Datos del cañón} asignados.
	 */
	protected Canion(CanionData data) {
		this.data = data;
		this.posX = 0;
		this.posY = 0;
		this.desfase = 0;
		
		this.idDisparo = data.getIdDisparo(0);
		this.tRecarga = data.getTRecarga(0);
		this.tDesfase = 0;
		this.velocidad = data.getVelocidad(0);
	}

	/**
	 * Construtor que crea un {@code Canion} copiando los
	 * datos de otro {@code Canion} que sirve como prototipo.
	 * @param prototipo {@code Canion} prototipo.
	 */
	protected Canion(Canion prototipo) {
		this.data = prototipo.data;
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
		this.desfase = prototipo.desfase;
		
		this.idDisparo = prototipo.idDisparo;
		this.tRecarga = prototipo.tRecarga;
		this.tDesfase = prototipo.tDesfase;
		this.velocidad = prototipo.velocidad;

		tTranscurrido = tRecarga - tDesfase;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Canion clone();

	/**
	 * <i>Setter</i> que asigna la posición relativa de este {@code Canion} respecto
	 * al elemento donde esta armado.
	 * @param posX Coordenada X de la posición asignada.
	 * @param posY Coordenada Y de la posición asignada.
	 */
	public final void setPosicion(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	/**
	 * <i>Setter</i> que asigna el {@link #desfase desfase fracional} de este {@code Canion}.
	 * @param desfase Desfase de disparo asignada.
	 */
	public final void setDesfase(float desfase) {
		this.desfase = desfase;
		this.tDesfase = (long)(desfase * tRecarga);
	}

	/**
	 * <i>Setter</i> que asigna: el {@link #idDisparo}, el {@link #tRecarga}, el {@link #tDesfase}
	 * y la {@link #velocidad} de este {@code Canion}, a partir del {@code grado} pasado como parámetro.
	 * @param grado Valor empleado para obtener los valores asignados.
	 */
	public final void setGrado(int grado) {
		this.idDisparo = data.getIdDisparo(grado);
		this.tRecarga = data.getTRecarga(grado);
		this.tDesfase = (long)(desfase * tRecarga);
		this.velocidad = data.getVelocidad(grado);
		
		tTranscurrido = tRecarga - tDesfase;
	}

	/**
	 * Método que reinicia el {@link #tTranscurrido tiempo transcurrido}.
	 */
	public final void cargar() {
		tTranscurrido = tRecarga - tDesfase;
	}

	/**
	 * @param mX
	 * @param nX
	 * @param mY
	 * @param nY
	 * @param nanos
	 * @param stopTime
	 * @param dst
	 */
	public abstract void dispara(float mX, float nX, float mY, float nY, long nanos, long stopTime, Collection<? super Disparo> dst);
}