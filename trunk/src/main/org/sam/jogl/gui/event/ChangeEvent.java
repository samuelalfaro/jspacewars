/*
 * ChangeEvent.java
 *
 * Created on November 25, 2007, 6:34 PM
 */

package org.sam.jogl.gui.event;

/**
 * Indicates that a component/object state has changed.
 *
 * @author davedes
 */
@SuppressWarnings( "serial" )
public class ChangeEvent extends EventObject{

	/**
	 * Creates a new event using the specified params.
	 * 
	 * @param source the source that created the event
	 */
	public ChangeEvent( Object source ){
		super( source );
	}

}
