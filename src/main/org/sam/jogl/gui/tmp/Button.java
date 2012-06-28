package org.sam.jogl.gui.tmp;

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
	
	/**
	 * A SuiMouseListener for handling the button clicks.
	 */
	private class ButtonListener extends MouseAdapter{

		ButtonListener(){
		}

		public void mouseEntered( MouseEvent e ){
			if( !isEnabled() )
				return;
			if( state != DOWN )
				state = ROLLOVER;
		}

		public void mouseExited( MouseEvent e ){
			if( !isEnabled() )
				return;
			state = UP;
		}

		public void mousePressed( MouseEvent e ){
			if( !isEnabled() )
				return;
			if( e.button == MouseEvent.BUTTON1 ){
				state = DOWN;
			}
		}

		//TODO: don't call these methods if a container is disabled??
		public void mouseReleased( MouseEvent e ){
			if( !isEnabled() )
				return;
			if( state == DOWN && e.button == MouseEvent.BUTTON1 ){
				state = ROLLOVER;
				fireActionPerformed( actionCommand );
			}
		}

		public void mouseDragged( MouseEvent e ){
			if( !isEnabled() )
				return;
			state = Button.this.contains( e.x, e.y ) ? DOWN: UP;
		}
	}

	/** The current state. */
	int state = UP;

	/** The action command, initially null. */
	String actionCommand = null;

	protected Button( boolean updateAppearance ){
		super( false );
		setRequestFocusEnabled( true );
		setFocusable( true );
		addMouseListener( new ButtonListener() );
		if( updateAppearance )
			updateAppearance();
	}
	
	/**
	 * Creates a button with the specified text, which
	 * also acts as the action command String.
	 * 
	 * @param text the text to display on the button
	 */
	public Button( String text ){
		this( true );
		setText( text );
		actionCommand = text;
		pack();
	}
	
	/** Creates an empty button. */
	public Button(){
		this( true );
	}

	public void updateAppearance(){

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
	
	/**
	 * Fires an action event with the specified command
	 * to all action listeners registered with this component.
	 * 
	 * @param command the action command for the event
	 */
	protected void fireActionPerformed( String command ){
		ActionListener[] listeners = listenerList.getListeners( ActionListener.class );
		if( listeners.length > 0 ){
			ActionEvent evt = new ActionEvent( this, command );
			for( ActionListener listener: listeners )
				listener.actionPerformed( evt );
		}
	}
	
	/**
	 * Sets the action command to be passed to <tt>ActionEvent</tt>s when this button
	 * is clicked.
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
	 * Overridden to set the state of the button to UP if
	 * it's being invisible.
	 * 
	 * @param visible <tt>true</tt> if the button should be displayed
	 */
	public void setVisible( boolean visible ){
		if( !visible && isVisible() )
			state = UP;
		super.setVisible( visible );
	}

	/**
	 * Overridden to set the state of the button to UP if
	 * it's being disabled.
	 * 
	 * @param enabled <tt>true</tt> if the button should accept clicks
	 */
	public void setEnabled( boolean enabled ){
		//a disabled button is always in the UP state
		if( !enabled && isEnabled() ){
			state = UP;
		}
		super.setEnabled( enabled );
	}
}
