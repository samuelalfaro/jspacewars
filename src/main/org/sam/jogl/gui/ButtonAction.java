/* 
 * ButtonAction.java
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
package org.sam.jogl.gui;

import org.sam.jogl.gui.event.ActionEvent;
import org.sam.jogl.gui.event.ActionListener;

/**
 * Clase abstracta que define una acción que será invocada cuando se pulsa un botón.
 */
public abstract class ButtonAction implements ActionListener, Runnable{

	private final boolean multiThread;
	private final String  name;

	public ButtonAction( boolean multiThread, String name ){
		this.multiThread = multiThread;
		this.name = name;
	}
	
	public ButtonAction( String name ){
		this( false, name );
	}
	
	public final boolean isMultiThread(){
		return multiThread;
	}

	public final String getName(){
		return this.name;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.gui.event.ActionListener#actionPerformed(org.sam.jogl.gui.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( ActionEvent e ){
		if( multiThread )
			new Thread( this ).start();
		else
			this.run();
	}
}
