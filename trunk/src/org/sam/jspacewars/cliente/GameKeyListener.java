/* 
 * GameKeyListener.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars.cliente;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.sam.jspacewars.elementos.KeysState;

class GameKeyListener implements KeyListener {

	private final static int K_SUBE = KeyEvent.VK_UP;
	private final static int K_BAJA = KeyEvent.VK_DOWN;
	private final static int K_ACELERA = KeyEvent.VK_RIGHT;
	private final static int K_FRENA = KeyEvent.VK_LEFT;
	private final static int K_DISPARO = KeyEvent.VK_SHIFT;
	private final static int K_BOMBA = KeyEvent.VK_SPACE;
	private final static int K_UPGRADE = KeyEvent.VK_CONTROL;

	private final ClientData data;

	GameKeyListener(ClientData data) {
		this.data = data;
	}

	public void keyPressed(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		switch( keyCode ){
		case K_SUBE:
			data.key_state |= KeysState.SUBE;
			break;
		case K_BAJA:
			data.key_state |= KeysState.BAJA;
			break;
		case K_ACELERA:
			data.key_state |= KeysState.ACELERA;
			break;
		case K_FRENA:
			data.key_state |= KeysState.FRENA;
			break;
		case K_DISPARO:
			data.key_state |= KeysState.DISPARO;
			break;
		case K_BOMBA:
			data.key_state |= KeysState.BOMBA;
			break;
		case K_UPGRADE:
			data.key_state |= KeysState.UPGRADE;
			break;
		default:
		}
	}

	public void keyReleased(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		switch( keyCode ){
		case K_SUBE:
			data.key_state &= ~KeysState.SUBE;
			break;
		case K_BAJA:
			data.key_state &= ~KeysState.BAJA;
			break;
		case K_ACELERA:
			data.key_state &= ~KeysState.ACELERA;
			break;
		case K_FRENA:
			data.key_state &= ~KeysState.FRENA;
			break;
		case K_DISPARO:
			data.key_state &= ~KeysState.DISPARO;
			break;
		case K_BOMBA:
			data.key_state &= ~KeysState.BOMBA;
			break;
		case K_UPGRADE:
			data.key_state &= ~KeysState.UPGRADE;
			break;
		default:
			if( (keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END)
					|| ((keyCode == KeyEvent.VK_C) && keyEvent.isControlDown()) )
				System.exit(0);
		}
	}

	public void keyTyped(KeyEvent keyEvent) {
	}
}