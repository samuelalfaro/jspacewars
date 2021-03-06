/* 
 * NaveUsuario.java
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
 * Clase que contiene los datos de las naves que manejan los usuarios del juego.
 */
public class NaveUsuario extends Nave {

	private static final int I_VELOCIDAD = 0;
	private static final int I_POW_CANIONES_PRI = 1;
	private static final int I_NUM_CANIONES_PRI = 2;
	private static final int I_POW_CANIONES_SEC = 3;
	private static final int I_NUM_CANIONES_SEC = 4;

	private final int[][] limitesDeNiveles;
	private transient int gradoNave;

	private static final int I_FIJO = 0;
	private static final int I_ACTUAL = 1;

	private transient int niveles[][];

	private final float[] velocidadesDisponibles;
	private transient float velocidad;

	private static final int I_PRIMARIO = 0;
	private static final int I_SECUNDARIO = 1;
	private final Canion[][][] canionesDisponibles;
	private transient Canion[][] caniones;

	private transient int aumentadoresDeNivel = 0;

	/**
	 *  Constructor que crea una {@code NaveUsuario} y le asigna los valores correspondientes.
	 * 
	 * @param code Código que identifica el tipo de elemento que es la nave.
	 * @param forma Polígono que define la forma de la nave con que comprueban las colisiones.
	 * @param limitesDeNiveles Vector bidimensional, que contiene los niveles iniciales, y los niveles máximos para cada grado de la nave.
	 * @param velocidadesDisponibles Vector que contiene las velocidades para los distintos niveles de la nave.
	 * @param canionesDisponibles Vector tridimensional, con los cañones primarios y secundarios, para cada nivel de la nave.
	 * <tt><pre>
	 * +-PRIMARIO
	 * |   +--Nivel [   0   ][   1   ][   2   ][   3   ][   4   ][   5   ]
	 * |            [ Cañón ][ Cañón ][ Cañón ][ Cañón ][ Cañón ][ Cañón ]
	 * |                              [ Cañón ][ Cañón ][ Cañón ][ Cañón ]
	 * |                                       [ Cañón ][ Cañón ][ Cañón ]
	 * |                                                [ Cañón ][ Cañón ]
	 * |                                                         [ Cañón ]
	 * +-SECUNDARIO
	 *     +--Nivel [   0   ][   1   ][   2   ][   3   ]
	 *                       [ Cañón ][ Cañón ][ Cañón ]
	 *                                [ Cañón ][ Cañón ]
	 *                                         [ Cañón ]</pre></tt>
	 */
	public NaveUsuario( short code, Poligono forma, int[][] limitesDeNiveles, float[] velocidadesDisponibles,
			Canion[][][] canionesDisponibles ){
		super( code, forma );
		this.limitesDeNiveles = limitesDeNiveles;
		this.velocidadesDisponibles = velocidadesDisponibles;
		this.canionesDisponibles = canionesDisponibles;
		this.gradoNave = 1;
		init();
	}
	
	/**
	 * Constructor que crea una {@code NaveUsuario} copiando los
	 * datos de otra {@code NaveUsuario} que sirve como prototipo.
	 * @param prototipo {@code NaveUsuario} prototipo.
	 */
	protected NaveUsuario(NaveUsuario prototipo) {
		super(prototipo);
		this.limitesDeNiveles = prototipo.limitesDeNiveles;
		this.velocidadesDisponibles = prototipo.velocidadesDisponibles;
		this.canionesDisponibles = prototipo.canionesDisponibles;
		// this.gradoNave = prototipo.gradoNave;
		this.gradoNave = 1;
		init();
	}

	/* (non-Javadoc)
	 * @see org.sam.jspacewars.servidor.elementos.Elemento#clone()
	 */
	@Override
	public NaveUsuario clone() {
		return new NaveUsuario(this);
	}

	/* (non-Javadoc)
	 * @see org.sam.jspacewars.servidor.elementos.Nave#init()
	 */
	@Override
	public void init() {
		if( this.niveles == null ){
			this.niveles = new int[2][5];
			// En la posicion 0 estan almacenados los valores inciales
			System.arraycopy(this.limitesDeNiveles[0], 0, this.niveles[I_ACTUAL], 0, 5);
			System.arraycopy(this.limitesDeNiveles[0], 0, this.niveles[I_FIJO], 0, 5);
		}else
			// En este caso reiniciamos la nave porque la han matado,
			// y copiamos los niveles guardados a los actuales.
			System.arraycopy(niveles[I_FIJO], 0, niveles[I_ACTUAL], 0, 5);

		velocidad = velocidadesDisponibles[niveles[I_ACTUAL][I_VELOCIDAD]];

		if( this.caniones == null )
			this.caniones = new Canion[2][];
		setCaniones( I_PRIMARIO, niveles[I_ACTUAL][I_NUM_CANIONES_PRI] );
		setGradoCaniones( I_PRIMARIO, niveles[I_ACTUAL][I_POW_CANIONES_PRI] );
		setCaniones( I_SECUNDARIO, niveles[I_ACTUAL][I_NUM_CANIONES_SEC] );
		setGradoCaniones( I_SECUNDARIO, niveles[I_ACTUAL][I_POW_CANIONES_SEC] );
	}

	/**
	 * 
	 * @param iCanion Indice de origen/destino de los cañones(primarios/secundarios)
	 * @param nivel   Nivel de los cañones en la tabla de cañones disponibles
	 */
	private void setCaniones( int iCanion, int nivel ){
		int len = canionesDisponibles[iCanion][nivel].length;
		caniones[iCanion] = new Canion[len];
		for( int i = 0; i < len; i++ )
			caniones[iCanion][i] = canionesDisponibles[iCanion][nivel][i].clone();
	}

	private void setGradoCaniones( int iCanion, int grado ){
		if( caniones[iCanion] != null )
			for( Canion canion: caniones[iCanion] )
				canion.setGrado( grado );
	}

	private transient float ancho, alto;

	/**
	 * <i>Setter</i> que asigna los límites por donde puede moverse la nave.
	 * @param ancho anchura del área de movimiento.
	 * @param alto altura del área de movimiento.
	 */
	public void setLimites( float ancho, float alto ){
		this.ancho = ancho;
		this.alto = alto;
	}

	private transient int key_state;

	/**
	 * <i>Setter</i> que asigna el entero donde están enmascaradas las distintas teclas de control que están
	 * siendo pulsadas.
	 * @param key_state El Valor asignado.
	 */
	public void setKeyState( int key_state ){
		this.key_state = key_state;
	}

	/**
	 * <i>Getter</i> que devuelve el vector con los niveles fijos de la nave.
	 * @return el vector solicitado.
	 */
	public int[] getNivelesFijos(){
		return niveles[I_FIJO];
	}

	/**
	 * <i>Getter</i> que devuelve el vector con los niveles actuales de la nave.
	 * @return el vector solicitado.
	 */
	public int[] getNivelesActuales(){
		return niveles[I_ACTUAL];
	}

	/**
	 * <i>Getter</i> que devuelve el vector con los niveles disponibles de la nave.
	 * @return el vector solicitado.
	 */
	public int[] getNivelesDisponibles(){
		return limitesDeNiveles[gradoNave];
	}

	/**
	 * <i>Getter</i> que devuelve el número de aumentadores. Éstos se obtienen cuando
	 * se recoge una cápsula y permiten aumentar los niveles de la nave.
	 * @return el valor solicitado.
	 */
	public int getAumentadoresDeNivel(){
		return aumentadoresDeNivel;
	}

	/**
	 * <i>Getter</i> que devuelve el grado actual de la nave. Dependiendo del grado, las nave podrá
	 * mejorar más sus niveles.
	 * @return el valor solicitado.
	 */
	public int getGradoNave(){
		return gradoNave;
	}

	/* (non-Javadoc)
	 * @see org.sam.elementos.Dinamico#actua(long)
	 */
	@Override
	public void actua(long nanos) {
		mover(nanos);
		if( (key_state & KeysState.DISPARO) != 0 )
			dispara(nanos);
		if( (key_state & KeysState.BOMBA) != 0 )
			lanzarBomba(nanos);
		if( (key_state & KeysState.UPGRADE) != 0 && aumentadoresDeNivel > 0 )
			upgrade();
	}

	private transient float posXant, posYant;
	
	private void mover( long nanos ){
		float x = getX();
		float y = getY();
		posXant = x;
		posYant = y;
		boolean anguloModificado = false;

		float v = velocidad * nanos;

		if( (key_state & KeysState.SUBE) != 0 ){
			y += v;
			if( y > alto )
				y = alto;
			if( angulo > -0.5f )
				angulo -= 1.5e-9f * nanos;
			if( angulo < -0.5f )
				angulo = -0.5f;
			anguloModificado = true;
		}else if( (key_state & KeysState.BAJA) != 0 ){
			y -= v;
			if( y < -alto )
				y = -alto;
			if( angulo < 0.5f )
				angulo += 1.5e-9f * nanos;
			if( angulo > 0.5f )
				angulo = 0.5f;
			anguloModificado = true;
		}
		if( !anguloModificado && angulo != 0.0f ){
			if( angulo > 0.0f ){
				angulo -= 3.0e-9f * nanos;
				if( angulo < 0.0f )
					angulo = 0.0f;
			}else{
				angulo += 3.0e-9f * nanos;
				if( angulo > 0.0f )
					angulo = 0.0f;
			}
		}
		if( (key_state & KeysState.ACELERA) != 0 ){
			x += v;
			if( x > ancho )
				x = ancho;
		}else if( (key_state & KeysState.FRENA) != 0 ){
			x -= v;
			if( x < -ancho )
				x = -ancho;
		}
		this.setPosicion(x, y);
	}

	private void dispara( long nanos ){
		float nX = posXant;
		float nY = posYant;
		float mX = ( getX() - nX ) / nanos;
		float mY = ( getY() - nY ) / nanos;

		for( Canion canion: caniones[I_PRIMARIO] )
			canion.dispara( mX, nX, mY, nY, nanos, getDstDisparos() );

		if( caniones[I_SECUNDARIO] != null )
			for( Canion canion: caniones[I_SECUNDARIO] )
				canion.dispara( mX, nX, mY, nY, nanos, getDstDisparos() );
	}

	private void lanzarBomba(long nanos) {
		// TODO Cambiar
		incAumentadoresDeNivel(nanos);
	}

	private transient long tUltimoAumento = 0;

	private void incAumentadoresDeNivel(long nanos) {
		tUltimoAumento += nanos;
		if( tUltimoAumento >= 250000000 ){
			tUltimoAumento = 0;
			aumentadoresDeNivel++;
			if( aumentadoresDeNivel == 12 ){
				aumentadoresDeNivel = 0;
				//vidas ++;
			}
			/*
			 * if((gradoNave < 3 && aumentadoresDeNivel == 12)|| (gradoNave == 3
			 * && aumentadoresDeNivel == 11)){ aumentadoresDeNivel = 0; // vidas
			 * ++; }
			 */
		}
	}

	private boolean nivelAumentable(int index) {
		return niveles[I_ACTUAL][index] < limitesDeNiveles[gradoNave][index];
	}

	private void aumentarNivel(int index) {
		niveles[I_ACTUAL][index]++;
		switch( index ){
		case I_VELOCIDAD:
			velocidad = velocidadesDisponibles[niveles[I_ACTUAL][I_VELOCIDAD]];
			break;
		case I_POW_CANIONES_PRI:
			setGradoCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_POW_CANIONES_PRI]);
			break;
		case I_NUM_CANIONES_PRI:
			setCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_NUM_CANIONES_PRI]);
			setGradoCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_POW_CANIONES_PRI]);
			break;
		case I_POW_CANIONES_SEC:
			setGradoCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_POW_CANIONES_SEC]);
			break;
		case I_NUM_CANIONES_SEC:
			setCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_NUM_CANIONES_SEC]);
			setGradoCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_POW_CANIONES_SEC]);
			break;
		}
	}

	private boolean nivelGuardable(int index) {
		return niveles[I_FIJO][index] < niveles[I_ACTUAL][index];
	}

	private void guardarNivel(int index) {
		niveles[I_FIJO][index]++;
	}

	private void aumentarGradoNave() {
		gradoNave++;
	}

	private void upgrade() {

		if( aumentadoresDeNivel == 11 ){
			if( gradoNave < 3 ){
				aumentarGradoNave();
				aumentadoresDeNivel = 0;
			}
		}else{
			int nivel = (aumentadoresDeNivel - 1) / 2;

			if( aumentadoresDeNivel % 2 == 0 ){
				if( nivelAumentable(nivel) ){
					aumentarNivel(nivel);
					aumentadoresDeNivel = 0;
				}
			}else{
				if( nivelGuardable(nivel) ){
					guardarNivel(nivel);
					aumentadoresDeNivel = 0;
				}
			}
		}
	}
}
