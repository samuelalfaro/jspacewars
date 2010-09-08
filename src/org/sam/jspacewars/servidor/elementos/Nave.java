/* 
 * Nave.java
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

import java.nio.ByteBuffer;
import java.util.Collection;

import org.sam.colisiones.Poligono;

public abstract class Nave extends Elemento {

	protected transient float angulo;
	protected transient Collection<Disparo> dstDisparos;

	public Nave(short code, Poligono forma) {
		super(code, forma);
		dstDisparos = null;
	}

	protected Nave(Nave prototipo) {
		super(prototipo);
		dstDisparos = prototipo.dstDisparos;
	}

	public final Collection<Disparo> getDstDisparos() {
		return dstDisparos;
	}

	public final void setDstDisparos(Collection<Disparo> dstDisparos) {
		this.dstDisparos = dstDisparos;
	}

	public abstract void iniciar();

	public void enviar(ByteBuffer buff) {
		super.enviar(buff);
		buff.putFloat(angulo);
	}
	
	int resistencia = 1000;
	public void inicializar(){
		resistencia = 1000;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jspacewars.servidor.elementos.Destruible#recibirImpacto(int)
	 */
	@Override
	public void recibirImpacto(int fuerzaDeImpacto) {
		resistencia -= fuerzaDeImpacto;
	}

	@Override
	public boolean isDestruido() {
		return resistencia <= 0;
	}
}
