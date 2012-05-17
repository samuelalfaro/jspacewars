package org.sam.jogl.gui;

import org.sam.jogl.gui.event.ActionEvent;
import org.sam.jogl.gui.event.ActionListener;
import org.sam.jogl.gui.event.MouseAdapter;
import org.sam.jogl.gui.event.MouseEvent;

/**
 * A basic, clickable button component. <tt>SuiButtons</tt> are
 * rendered based on their state: UP, DOWN, or ROLLOVER.
 * Images can be set for each state.
 * <p>
 * A button will be "hit" when the first mouse button is clicked and released over it (MouseEvent.BUTTON1).
 * <p>
 * All SuiButtons are created with a padding of 5, and with text and images initially centered.
 * 
 * 
 * @author davedes
 * @since b.0.1
 */
public class Button extends Label{

	/** A constant for the UP state. */
	public static final int UP = -10;

	/** A constant for the DOWN state. */
	public static final int DOWN = -9;

	/** A constant for the ROLLOVER state. */
	public static final int ROLLOVER = -8;

	/** The current state. */
	int state = UP;

	/** The action command, initially null. */
	private String actionCommand = null;

	boolean pressedOutside = false;

	/**
	 * Creates a button with the specified text, which
	 * also acts as the action command String.
	 * 
	 * @param text the text to display on the button
	 */
	public Button( String text ){
		this();
		setText( text );
		actionCommand = text;
		pack();
	}

	/** Creates an empty button. */
	public Button(){
		this( true );
	}

	protected Button( boolean updateAppearance ){
		super( false );
		setRequestFocusEnabled( true );
		setFocusable( true );
		addMouseListener( new ButtonListener() );
		if( updateAppearance )
			updateAppearance();
	}

	public void updateAppearance(){

	}

	/**
	 * Sets the action command to be passed to <tt>ActionEvent</tt>s when this button
	 * is clicked.
	 * 
	 * 
	 * @param t the command
	 */
	public void setActionCommand( String t ){
		this.actionCommand = t;
	}

	/**
	 * Gets the action command.
	 * 
	 * @return the action command
	 */
	public String getActionCommand(){
		return actionCommand;
	}

	/**
	 * Adds the specified listener to the list.
	 * 
	 * @param s the listener to receive events
	 */
	public synchronized void addActionListener( ActionListener s ){
		listenerList.add( ActionListener.class, s );
	}

	/**
	 * Removes the specified listener from the list.
	 * 
	 * @param s the listener to remove
	 */
	public synchronized void removeActionListener( ActionListener s ){
		listenerList.remove( ActionListener.class, s );
	}

	public void setVisible( boolean b ){
		if( !isVisible() && b )
			state = UP;
		super.setVisible( b );
	}

	/**
	 * Gets the current state of the button.
	 * 
	 * @return the state id: either UP, DOWN, or ROLLOVER.
	 */
	public int getState(){
		return state;
	}

	/**
	 * Overridden to set the state of the button to UP if
	 * it's being disabled.
	 * 
	 * @param b <tt>true</tt> if the button should accept clicks
	 */
	public void setEnabled( boolean b ){
		//a disabled button is always in the UP state
		if( !b && isEnabled() ){
			state = UP;
		}
		super.setEnabled( b );
	}

	/**
	 * Fires a virtual press. Subclasses may override to react
	 * to button press <i>before</i> actions are sent.
	 */
	public void press(){
		fireActionPerformed( actionCommand );
	}

	/**
	 * @return 
	 */
	public boolean isPressedOutside(){
		return pressedOutside;
	}

	/**
	 * Fires an action event with the specified command
	 * to all action listeners registered with this component.
	 * 
	 * @param command the action command for the event
	 */
	protected void fireActionPerformed( String command ){
		ActionEvent evt = null;

		final ActionListener[] listeners = listenerList.getListeners( ActionListener.class );
		for( int i = 0; i < listeners.length; i++ ){
			//lazily create it
			if( evt == null ){
				evt = new ActionEvent( this, command );
			}
			listeners[i].actionPerformed( evt );
		}
	}

	/**
	 * A SuiMouseListener for handling the button clicks.
	 */
	protected class ButtonListener extends MouseAdapter{

		private boolean inside = false;

		public void mousePressed( MouseEvent e ){
			if( !isEnabled() ){
				state = UP;
				return;
			}
			inside = true;

			if( e.getButton() == MouseEvent.BUTTON1 ){
				state = DOWN;
			}
			pressedOutside = false;
		}

		public void mouseDragged( MouseEvent e ){
			//if we are dragging outside
			inside = Button.this.contains( e.getAbsoluteX(), e.getAbsoluteY() );
			state = inside ? DOWN: UP;
			pressedOutside = !inside;
		}

		public void mouseEntered( MouseEvent e ){
			if( !isEnabled() ){
				state = UP;
				return;
			}

			if( state != DOWN )
				state = ROLLOVER;

			inside = true;
		}

		//TODO: don't call these methods if a container is disabled??
		public void mouseReleased( MouseEvent e ){
			if( !isEnabled() ){
				state = UP;
				return;
			}

			pressedOutside = false;

			//only fire action if we are releasing button 1
			if( inside && e.getButton() == MouseEvent.BUTTON1 ){
				state = ROLLOVER;
				press();
			}
		}

		public void mouseExited( MouseEvent e ){
			if( !isEnabled() ){
				state = UP;
				return;
			}
			inside = false;
			state = UP;
		}
	}
}
