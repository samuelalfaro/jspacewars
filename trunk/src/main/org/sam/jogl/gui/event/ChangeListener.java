/*
 * ChangeListener.java
 *
 * Created on November 25, 2007, 6:28 PM
 */

package org.sam.jogl.gui.event;

/**
 *
 * @author davedes
 */
public interface ChangeListener extends Listener {
    
	/**
	 * Notification that the state changed has occurred.
	 * 
	 * @param e the event associated with this listener
	 */
	public void stateChanged( ChangeEvent e );
}
