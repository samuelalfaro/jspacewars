package org.sam.jogl.gui.event;

/**
 * The base class for SuiEvents.
 * 
 * @author davedes
 * @since b.0.2
 */
@SuppressWarnings( "serial" )
public class EventObject implements java.io.Serializable {
    
	/** If the no ID is specified, an ID_UNDEFINED id will be used. */
	public static final int ID_UNDEFINED = 0;
	
	/** The source object that created this event. */
	public final Object source;

	/** The unique ID for this event. */
	public final int id;

	/**
	 * Creates a new event with the specified params. IDs passed
	 * should not be equal to 0 unless an ID_UNDEFINED is intended.
	 * 
	 * @param source the object that created this event
	 * @param id the id for this event
	 */
	public EventObject( Object source, int id ){
//		System.out.println( this.getClass().getSimpleName() + ":\t" + source.getClass().getSimpleName() + " ->\t" + id );
		this.source = source;
		this.id = id;
	}

	/**
	 * Creates a new event with the specified source.
	 * All events created using this constructor will have
	 * an ID of 0, ID_UNDEFINED.
	 * 
	 * @param source the container which created this event
	 */
	public EventObject( Object source ){
		this( source, ID_UNDEFINED );
	}
}