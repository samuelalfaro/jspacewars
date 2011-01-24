/* 
 * Misil.java
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
package org.sam.jspacewars.servidor.elementos;

import org.sam.colisiones.Poligono;

/**
 * Implementación de un {@link Disparo} que sigue una trayectoria variable,
 * en función de la posición del {@link Elemento objetivo} asignado.
 */
public class Misil extends Disparo {
	
	private transient float vTangencial;
	// TODO ajustar valores angulo
	private transient float vAngular = 0.000000005f;
	private transient float aceleracion;

	private transient Elemento objetivo;

	Misil(short code, Poligono forma) {
		super(code, forma);
	}

	/**
	 * Construtor que crea un {@code Misil} copiando los
	 * datos de otro {@code Misil} que sirve como prototipo.
	 * @param prototipo {@code Misil} prototipo.
	 */
	protected Misil(Misil prototipo) {
		super(prototipo);
	}

	public Misil clone() {
		return new Misil(this);
	}

	private static float anguloAcotado(float angulo){
		float a = angulo;
		while( a < 0 )
			a += PI2;
		while(a > PI2 )
			a -= PI2;
		return a;
	}
	
	public void setValues(float posX, float posY, float angulo, float velocidad) {
		super.setPosicion(posX, posY);
		super.setAngulo(anguloAcotado(angulo));
		this.vTangencial = velocidad;
		this.aceleracion = velocidad / 500000000;
	}

	public void setObjetivo(Elemento objetivo) {
		this.objetivo = objetivo;
	}

	private static transient final float PI2 = (float) (2 * Math.PI);
	private static transient final float PI = (float) Math.PI;

	private void rotarPos(float alfa, float x, float y, float pX, float pY) {
		float cosAlfa = (float) Math.cos(alfa);
		float senAlfa = (float) Math.sin(alfa);
		setPosicion( x * cosAlfa - y * senAlfa + pX, x * senAlfa + y * cosAlfa + pY );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actua(long nanos) {

		float vel = vTangencial * nanos;
		float x = getX(), y = getY(), a = getAngulo();

		if( objetivo != null && !objetivo.isDestruido()){

			float oX =  objetivo.getX(), oY =  objetivo.getY();
			
			// w = velocidad Angular
			// v = velocidad Tangencial
			// v = w · radio
			// radio = v / w

			float radio = vTangencial / vAngular;
			float dx, dy;

			float perpendicularX = (float) Math.sin(a) * radio;
			float perpendicularY = (float) Math.cos(a) * radio;

			// Centro de giro 1
			float c1X = x - perpendicularX;
			float c1Y = y + perpendicularY;
			dx = oX - c1X;
			dy = oY - c1Y;
			float dist1 = dx * dx + dy * dy;

			// Centro de giro 2
			float c2X = x + perpendicularX;
			float c2Y = y - perpendicularY;
			dx = oX - c2X;
			dy = oY - c2Y;
			float dist2 = dx * dx + dy * dy;

			float iAng = vAngular * nanos;

			// Si el objetivo esta dentro del radio de uno de los centros de
			// giro, es inalcanzable por tanto se gira en el contrario.
			float r2 = radio * radio;
			if( dist1 < r2 ){
				a -= iAng;
				while( a < 0 )
					a += PI2;
				setAngulo(a);
				rotarPos(-iAng, x - c2X, y - c2Y, c2X, c2Y);
				return;
			}else if( dist2 < r2 ){
				a += iAng;
				while( a > PI2 )
					a -= PI2;
				setAngulo(a);
				rotarPos(iAng, x - c1X, y - c1Y, c1X, c1Y);
				return;
			}

			{ // TODO cambiar por giro y movimiento lineal tangencial a las
				// circunferencias de giro
				// El angulo al objetivo es alcanzable en este intervalo se
				// iguala y se avanza en dicha dirección
				float angOb = (float) Math.atan2( oY - y, oX - x );
				if( angOb < 0 )
					angOb = PI2 + angOb;

				float absDif = Math.abs(angOb - a);
				if( absDif > PI )
					absDif = PI2 - absDif;

				if( absDif < iAng ){
					setAngulo(angOb);
					setPosicion(x + vel * (float) Math.cos(a), y + vel * (float) Math.sin(a) );
					vTangencial += aceleracion * nanos;
					return;
				}
			}
			// Se gira en el centro de giro más próximo al objetivo
			if( dist1 < dist2 ){
				a += iAng;
				while( a > PI2 )
					a -= PI2;
				setAngulo(a);
				rotarPos(iAng, x - c1X, y - c1Y, c1X, c1Y);
			}else{
				a -= iAng;
				while( a < 0 )
					a += PI2;
				setAngulo(a);
				rotarPos(-iAng, x - c2X, y - c2Y, c2X, c2Y);
			}
		}else
			setPosicion(x + vel * (float) Math.cos(a), y + vel * (float) Math.sin(a) );
	}
}
