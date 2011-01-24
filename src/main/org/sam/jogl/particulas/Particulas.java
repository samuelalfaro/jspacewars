/* 
 * Particulas.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jogl.particulas;

import java.util.Deque;
import java.util.LinkedList;

import javax.vecmath.*;

import org.sam.elementos.*;
import org.sam.interpoladores.Getter;
import org.sam.jogl.*;

public abstract class Particulas extends Objeto3DAbs implements Modificable, PrototipoCacheable<Particulas>{

	/**
	 * Enumeración que contiene los distintos modos de emisión aceptados.
	 */
	public enum Emision{
		/**
		 * Modo de emisión que indica que: el sistema finaliza cuando todas
		 * las partículas han muerto.
		 */
		UNICA,
		/**
		 * Modo de emisión que indica que: cuando todas las partículas han
		 * muerto, comienza la emisión de nuevo.
		 */
		PULSOS,
		/**
		 * Modo de emisión que indica que: cuando muere una partícula, vuelve
		 * a regenerarse.
		 */
		CONTINUA
	}

	/**
	 * Número de particulas. Debe ser un valor positivo.
	 */
	public final int nParticulas;

	/**
	 * Número de particulas activas.
	 */
	protected transient int particulasActivas;
	
	/**
	 * Objeto encargado de emitir las partículas.
	 */
	protected Emisor emisor;
	/**
	 * Modo de emisión de las particulas
	 * @see Particulas.Emision
	 */
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
	/**
	 * Modificador encargado de animar las particulas.
	 */
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
	 * Pertubación aleatoria de la velocidad en unidades por segundo.<br>
	 * Es un valor absoluto.<br>
	 * Este valor modifica la velocidad segun la formula:<br>
	 * velocidad_final = velocidad + (aleatorioEntre(0,1) - 0.5) * pertubacion.
	 */
	protected float pVelocidad;
	/**
	 * Parámetro que indica si el sentido de la velocicidad es aleatorio.
	 */
	protected boolean sentidoVelocidadAleatorio;
	/**
	 * Fuerza que sufren todas las particulas en unidades por segundo al cuadrado.
	 */
	protected Vector3f fuerza;
	/**
	 * Giro inicial sobre el centro de la textura de la particula. En grados.
	 */
	protected float giroInicial;
	/**
	 * Pertubación aleatoria del giro de la particula
	 */
	protected float pGiroInicial;
	/**
	 * Parámetro que indica si el sentido del giro es aleatorio.
	 */
	protected boolean sentidoGiroInicialAleatorio;
	/**
	 * Giro sobre el centro de la textura de la particula. En grados por segundo.
	 */
	protected float velGiro;
	/**
	 * Pertubación aleatoria del giro de la particula
	 */
	protected float pVelGiro;
	/**
	 * Parámetro que indica si el sentido del giro es aleatorio.
	 */
	protected boolean sentidoVelGiroAleatorio;
	
	protected float r;
	protected float g;
	protected float b;
	protected Getter.Float<float[]> iColores;
	
	protected float a;
	protected Getter.Float<Float> iAlfa;
	
	protected float pColor;
	protected boolean pColorHomogenea;
	protected boolean pColorEscalar;
	
	private transient final Matrix4f tCompuesta; 
	
	protected Particulas(int nParticulas){
		this.nParticulas = nParticulas;

		this.emision = Emision.UNICA;
		this.particulasActivas = 0;
		
		this.iVida = 1.0f;
		this.rangoDeEmision = 0.0f;
		
		this.velocidad = 1.0f;
		this.pVelocidad = 0.0f;
		this.sentidoVelocidadAleatorio = false;

		this.fuerza = new Vector3f(0.0f,0.0f,0.0f);

		this.velGiro  = 0.0f;
		this.pVelGiro = 0.0f;
		this.sentidoVelGiroAleatorio = false;

		this.pColor = 0.0f;
		this.pColorHomogenea = true;
		this.pColorEscalar = true;
		
		this.emisor = new Emisor.Puntual();
		this.tCompuesta = new Matrix4f();
		this.tCompuesta.setIdentity();
	}
	
	protected Particulas(Particulas me){
		super(me);
		
		this.nParticulas = me.nParticulas;
		this.emision = me.emision;
		this.particulasActivas = 0;
		
		this.iVida = me.iVida;
		this.rangoDeEmision = me.rangoDeEmision;
		
		this.velocidad = me.velocidad;
		this.pVelocidad = me.pVelocidad;
		this.sentidoVelocidadAleatorio = me.sentidoVelocidadAleatorio;

		this.fuerza = new Vector3f();
		this.fuerza.set(me.fuerza);

		this.giroInicial  = me.giroInicial;
		this.pGiroInicial = me.pGiroInicial;
		this.sentidoGiroInicialAleatorio = me.sentidoGiroInicialAleatorio;
		
		this.velGiro  = me.velGiro;
		this.pVelGiro = me.pVelGiro;
		this.sentidoVelGiroAleatorio = me.sentidoVelGiroAleatorio;

		this.r = me.r;
		this.g = me.g;
		this.b = me.b;
		this.iColores = me.iColores;
		this.a = me.a;
		this.iAlfa = me.iAlfa;
		
		this.pColor = me.pColor;
		this.pColorHomogenea = me.pColorHomogenea;
		this.pColorEscalar = me.pColorEscalar;
		
		this.emisor = me.emisor;
		
		this.tCompuesta = new Matrix4f();
		this.tCompuesta.setIdentity();
	}
	
	public final void setEmisor(Emisor emisor) {
		if(emisor == null)
			throw new NullPointerException();
		this.emisor = emisor;
	}
	
	public void setEmision(Emision emision) {
		this.emision = emision;
	}
	
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
		del tiempo y los volvemos a poner en funcion del nuevo tiempo de vida.
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
		// premultiplicamos la perturbacion por 2, para optimizar el calculo
		// ( aleatorio(0,1) - 0.5 ) * pertubacion -> rango [ -pertubacion/2 , pertubacion/2]
		this.pVelocidad = pVelocidad*2 / iVida;
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
	
	
	public final void setGiroInicial(float giroInicial){
		this.giroInicial = (float)(giroInicial*Math.PI/180);
	}

	public final void setGiroInicial(float giroInicial, float pGiroInicial, boolean sentidoGiroInicialAleatorio){
		this.giroInicial = (float)(giroInicial*Math.PI/180);
		// premultiplicamos la perturbacion por 2, para optimizar el calculo
		// ( aleatorio(0,1) - 0.5 ) * pertubacion -> rango [ -pertubacion/2 , pertubacion/2]
		this.pGiroInicial = (float)(pGiroInicial*Math.PI/90);
		this.sentidoGiroInicialAleatorio = sentidoGiroInicialAleatorio;
	}
	
	public final void setVelocidadGiro(float velGiro){
		// Pasamos del dominino del tiempo al dominio de la vida y de grados a radianes.	
		this.velGiro = (float)(velGiro*Math.PI/(180 * iVida));
	}

	public final void setVelocidadGiro(float velGiro, float pVelGiro, boolean sentidoVelGiroAleatorio){
		// Pasamos del dominino del tiempo al dominio de la vida y de grados a radianes.
		this.velGiro = (float)(velGiro*Math.PI/(180 * iVida));
		// premultiplicamos la perturbacion por 2, para optimizar el calculo
		// ( aleatorio(0,1) - 0.5 ) * pertubacion -> rango [ -pertubacion/2 , pertubacion/2]
		this.pVelGiro = (float)(pVelGiro*Math.PI/(90 * iVida));
		this.sentidoVelGiroAleatorio = sentidoVelGiroAleatorio;
	}

	// TODO cambiar ñapa
	private final Deque<Matrix4f> transforms = new LinkedList<Matrix4f>();
	
	protected Matrix4f getTransformMatrix(){
		Nodo n = this.getParent();
		while(n != null){
			if(n instanceof NodoTransformador)
				transforms.push( ((NodoTransformador)n).getTransform() );
			n = n.getParent();
		}
		tCompuesta.setIdentity();
		while( !transforms.isEmpty() )
			tCompuesta.mul(transforms.pop());
		return tCompuesta;
	}
	
	public abstract void setRadio(float radio);

	public abstract void setRadio(Getter.Float<Float> radio);
	
	public abstract void setSemiEje(float semiEje);

	public abstract void setSemiEje(Getter.Float<Float> semiEje);
	
	public final void setColor( Color4f color){
		setColor(color.x, color.y, color.z, color.w);
	}
	
	public final void setColor( float r, float g, float b, float a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.iColores = null;
		this.a = a;
		this.iAlfa = null;
	}
	
	public final void setColor( Color3f color, Getter.Float<Float> iAlfa){
		setColor(color.x, color.y, color.z, iAlfa);
	}
	
	public final void setColor( float r, float g, float b, Getter.Float<Float> iAlfa){
		this.r = r;
		this.g = g;
		this.b = b;
		this.iColores = null;
		this.iAlfa = iAlfa;
	}
	
	public final void setColor( Getter.Float<float[]> iColores, float alfa){
		this.iColores = iColores;
		this.a = alfa;
		this.iAlfa = null;
	}
	
	public final void setColor( Getter.Float<float[]> iColores, Getter.Float<Float> iAlfa){
		this.iColores = iColores;
		this.iAlfa = iAlfa;
	}
	
	public final void setPertubacionColor(float pColor, boolean pColorHomogenea, boolean pColorEscalar){
		this.pColor = pColor;
		this.pColorHomogenea = pColorHomogenea;
		this.pColorEscalar = pColorEscalar;
	}
	
	public final Modificador getModificador(){
		return modificador;
	}
	
	public abstract Particulas clone();
}
