package org.sam.jogl.gui.event;

/**
 * A mouse event, including motion and press/release. 
 * Mouse events will hold the absolute and local x and y
 * positions and often the button that is pressed.
 *
 * @author davedes
 * @since b.0.2
 */
@SuppressWarnings( "serial" )
public class MouseEvent extends EventObject {
    
    /** An event ID for the mouse moved event. */
    public static final int MOUSE_MOVED = java.awt.event.MouseEvent.MOUSE_MOVED;
    
    /** An event ID for the mouse dragged event. */
    public static final int MOUSE_DRAGGED = java.awt.event.MouseEvent.MOUSE_DRAGGED;
    
    /** An event ID for the mouse pressed event. */
    public static final int MOUSE_PRESSED = java.awt.event.MouseEvent.MOUSE_PRESSED;
    
    /** An event ID for the mouse entered event. */
    public static final int MOUSE_ENTERED = java.awt.event.MouseEvent.MOUSE_ENTERED;
    
    /** An event ID for the mouse exited event. */
    public static final int MOUSE_EXITED = java.awt.event.MouseEvent.MOUSE_EXITED;
    
    /** An event ID for the mouse released event. */
    public static final int MOUSE_RELEASED = java.awt.event.MouseEvent.MOUSE_RELEASED;
    
    /** A constant for mouse button 1, index 0. */
    public static final int BUTTON1 = java.awt.event.MouseEvent.BUTTON1;
    
    /** A constant for mouse button 2, index 1. */
    public static final int BUTTON2 = java.awt.event.MouseEvent.BUTTON2;
    
    /** A constant for mouse button 3, index 2. */
    public static final int BUTTON3 = java.awt.event.MouseEvent.BUTTON3;
    
    /** A constant for no mouse button or unknown mouse button, index -1. */
    public static final int NOBUTTON = java.awt.event.MouseEvent.NOBUTTON;
    
    /** The button for this event. */
    private int button;
    
    /** The local x position for this event. */
    private float x;
    
    /** The local y position for this event. */
    private float y;
    
    /**
     * Creates a new mouse event with the specified params.
     *
     * @param source the source
     * @param id the event type id
     * @param button the button index or -1 if it's unknown/undefined
     * @param x the x position of the mouse when the event occurred
     * @param y the y position of the mouse when the event occurred
     */
	public MouseEvent( Object source, int id, int button, float x, float y ){
		super( source, id );
		this.button = button;
		this.x = x;
		this.y = y;
	}
    
    /**
     * Creates a new mouse event with the specified params. The
     * old x and y values will be equal to the x and y values using
     * this constructor. Also, the button index will be -1, equal to
     * NOBUTTON.
     *
     * @param source the source
     * @param id the event type id
     * @param x the x position of the mouse when the event occurred
     * @param y the y position of the mouse when the event occurred
     */
    public MouseEvent(Object source, int id, float x, float y ) {
        this(source, id, NOBUTTON, x, y);
    }
    
    /**
     * Gets the x position relative to the parent container.
     *
     * @return the x position in the container's local space
     */
    public float getX() {
        return x;
    }
    
    /**
     * Gets the y position relative to the parent container.
     *
     * @return the y position in the container's local space
     */
    public float getY() {
        return y;
    }
    
    /**
     * Gets the button that initiated the event, or NOBUTTON if no
     * mouse button was used.
     *
     * @return the button index (starting at 0, 
     *              conforming to the button 
     *              constants BUTTON1, BUTTON2 and BUTTON3)
     *          or NOBUTTON (-1) if it's unknown
     */
    public int getButton() {
        return button;
    }
}