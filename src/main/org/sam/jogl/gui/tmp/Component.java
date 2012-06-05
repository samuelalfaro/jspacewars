/*
 * Component.java
 *
 * Created on November 6, 2007, 3:38 PM
 */

package org.sam.jogl.gui.tmp;

import javax.swing.event.EventListenerList;

import org.sam.jogl.gui.event.ControllerEvent;
import org.sam.jogl.gui.event.ControllerListener;
import org.sam.jogl.gui.event.KeyEvent;
import org.sam.jogl.gui.event.KeyListener;
import org.sam.jogl.gui.event.MouseEvent;
import org.sam.jogl.gui.event.MouseListener;
import org.sam.jogl.gui.event.MouseWheelEvent;
import org.sam.jogl.gui.event.MouseWheelListener;

/**
 * An abstract base component which all other components must
 * derive from.
 * 
 * @author davedes
 * @since b.2.0
 */
public abstract class Component{

	boolean debugRender = false;

	/** The parent of this container, used internally. */
	Container parent = null;
	boolean displayDirty = true;
	boolean sizeChanged = false;

	//static ClipBounds clip = new ClipBounds();
	//protected boolean hasClip = true;
	//protected SuiSkin.ContainerUI ui = null;

	private String tooltipText = null;
	private String name = null;
	private boolean appearanceEnabled = true;
	boolean hasFocus = false;
	private Object skinData = null;

	private boolean borderVisible = true;

	/** Whether this component is focusable, initially true. */
	private boolean focusable = true;

	/** Whether this component is visible. */
	private boolean visible = true;

	/** A type-safe list which holds the different listeners. */
	protected EventListenerList listenerList = new EventListenerList();

	/** The x position of this container. */
	private float x = 0f;

	/** The y position of this container. */
	private float y = 0f;

	/** The width this container. */
	private float width = 0;

	/** The height this container. */
	private float height = 0;

	/** Whether this label is opaque; drawing all pixels. */
	private boolean opaque = false;

	/** Whether this label is enabled. */
	private boolean enabled = true;

	//protected boolean isInit = false;
	//protected boolean isEnsuredAppearance = false;
	private boolean requestFocusEnabled = true;

	/**
	 * Whether this component is ignoring events
	 * and letting them pass through to underlying
	 * components.
	 */
	private boolean glassPane = false;

	//TODO: fix static default font implementation
	//(incase font is changed after construction)

	/**
	 * Creates a new instance of Component
	 */
	protected Component(){
	}

	/**
	 * Adds the specified listener to the list.
	 * 
	 * @param s the listener to receive events
	 */
	public synchronized void addKeyListener( KeyListener s ){
		listenerList.add( KeyListener.class, s );
	}

	/**
	 * Removes the specified listener from the list.
	 * 
	 * @param s the listener to remove
	 */
	public synchronized void removeKeyListener( KeyListener s ){
		listenerList.remove( KeyListener.class, s );
	}

	/**
	 * Adds the specified listener to the list.
	 * 
	 * @param s the listener to receive events
	 */
	public synchronized void addMouseListener( MouseListener s ){
		listenerList.add( MouseListener.class, s );
	}

	/**
	 * Removes the specified listener from the list.
	 * 
	 * @param s the listener to remove
	 */
	public synchronized void removeMouseListener( MouseListener s ){
		listenerList.remove( MouseListener.class, s );
	}

	/**
	 * Adds the specified listener to the list.
	 * 
	 * @param s the listener to receive events
	 */
	public synchronized void addMouseWheelListener( MouseWheelListener s ){
		listenerList.add( MouseWheelListener.class, s );
	}

	/**
	 * Removes the specified listener from the list.
	 * 
	 * @param s the listener to remove
	 */
	public synchronized void removeMouseWheelListener( MouseWheelListener s ){
		listenerList.remove( MouseWheelListener.class, s );
	}

	/**
	 * Adds the specified listener to the list.
	 * 
	 * @param s the listener to receive events
	 */
	public synchronized void addControllerListener( ControllerListener s ){
		listenerList.add( ControllerListener.class, s );
	}

	/**
	 * Removes the specified listener from the list.
	 * 
	 * @param s the listener to remove
	 */
	public synchronized void removeControllerListener( ControllerListener s ){
		listenerList.remove( ControllerListener.class, s );
	}

	/**
	 * Sets the visibility for this component and all of its
	 * children. Invisible components are not rendered. If a
	 * component with the focus is hidden, the focus is released.
	 * 
	 * @param b <tt>true</tt> if it should be renderable
	 */
	public void setVisible( boolean b ){
		boolean old = this.visible;
		this.visible = b;
		if( this.visible != old && hasFocus() ) //changed
			releaseFocus();
	}

	/**
	 * Whether this component is currently visible.
	 * 
	 * @return <tt>true</tt> if this component is visible
	 */
	public boolean isVisible(){
		return visible;
	}

	/**
	 * Determines whether this component is showing on screen. This means
	 * that the component must be visible, and it must be in a container
	 * that is visible and showing.
	 * @return <code>true</code> if the component is showing, <code>false</code> otherwise
	 * @see #setVisible
	 */
	public boolean isShowing(){
		if( visible ){
			Container parent = this.parent;
			return ( parent == null ) || parent.isShowing();
		}
		return false;
	}

	Component getFirstNonGlassPane(){
		if( !isGlassPane() )
			return this;
		if( parent == null )
			return null;
		return parent.getFirstNonGlassPane();
	}

	/**
	 * Gets the parent of this container.
	 * 
	 * @return the parent container, or <tt>null</tt> if this is a top-level component
	 */
	public Container getParent(){
		return parent;
	}

	/**
	 * Called to render this component. By default, this method will
	 * render the current appearance if it exists and is enabled.
	 */
	protected void renderComponent(){
	}

	/**
	 * Whether this label is enabled
	 * 
	 * @return <tt>true</tt> if this label is enabled
	 */
	public boolean isEnabled(){
		return enabled;
	}

	/**
	 * Set whether to enable or disable this label.
	 * 
	 * @param b <tt>true</tt> if this label should be enabled
	 */
	public void setEnabled( boolean b ){
		this.enabled = b;
	}

	/**
	 * Sets whether this label is opaque.
	 * Opaque components will draw any
	 * transparent pixels with the
	 * background color.
	 * 
	 * @param b <tt>true</tt> if background color
	 * should be drawn
	 */
	public void setOpaque( boolean b ){
		this.opaque = b;
	}

	/**
	 * Returns whether this label is opaque.
	 * Opaque labels will fill a rectangle
	 * of the background color before drawing the
	 * text or image.
	 * 
	 * @return <tt>true</tt> if background color
	 * should be drawn
	 */
	public boolean isOpaque(){
		return opaque;
	}

	/**
	 * Called to update this component. By default, this method will
	 * render the current appearance if it exists and is enabled.
	 * 
	 * @param container the GUIContext we are rendering to
	 * @param delta the delta time (in ms)
	 */
	//protected void updateComponent( GUIContext container, int delta ){
	protected void updateComponent( int delta ){
	}

	/**
	 * Called to render this component as a whole. For subclasses looking
	 * for custom rendering (outside of skins), it is suggested that you
	 * override renderComponent and disable the appearance.
	 * 
	 * @param container The container displaying this component
	 * @param g The graphics context used to render to the display
	 * @param topLevel <tt>true</tt> if this is a top-level component
	 */
	public void render(){

	}

	/**
	 * Updates this container and its children to the screen.
	 * <p>
	 * The order of updating is as follows:
	 * <ol>
	 * <li>Update this component through updateComponent</li>
	 * <li>Update this component's border through updateBorder</li>
	 * <li>Update this component's children through updateChildren</li>
	 * </ol>
	 * <p>
	 * For custom updating of the component, override renderComponent.<br>
	 * For cusotm updating of the border, override renderBorder.<br>
	 * For custom updating of the children, override renderChildren.<br>
	 * 
	 * @param container The container displaying this component
	 * @param delta The delta to update by
	 */
	//public void update( GUIContext container, int delta ){
	public void update( int delta ){
		//TODO: update only while visible?
		//updateComponent( container, delta );
	}

	/**
	 * Centers this component based on the size of the given context.
	 * If <tt>null</tt> is passed, no change is made.
	 * 
	 * @param context the context to center to
	 */
	//public void setLocationRelativeToContext( GUIContext context ){
	public void setLocationRelativeToContext(){
//		if( context == null )
//			return;
//		setLocation( context.getWidth() / 2f - getWidth() / 2f, context.getHeight() / 2f - getHeight() / 2f );
	}

	/**
	 * Sets the x and y positions of this Container, relative
	 * to its parent. If no parent exists, the location is absolute
	 * to the GUIContext.
	 * 
	 * 
	 * @param x the x position of this component
	 * @param y the y position of this component
	 * @see mdes.slick.sui.Container#setBounds(float, float, int, int)
	 */
	public void setLocation( float x, float y ){
		setX( x );
		setY( y );
	}

	/**
	 * Sets the x position of this Container, relative
	 * to its parent.
	 * 
	 * @param x the x position of this component
	 * @see mdes.slick.sui.Container#setLocation(float, float)
	 */
	public void setX( float x ){
		this.x = x;
	}

	/**
	 * Sets the y position of this Container, relative
	 * to its parent.
	 * 
	 * 
	 * @param y the y position of this component
	 * @see mdes.slick.sui.Container#setLocation(float, float)
	 */
	public void setY( float y ){
		this.y = y;
	}

	/**
	 * Gets the x position of this Container, relative
	 * to its parent.
	 * 
	 * 
	 * @return the x position of this component
	 * @see mdes.slick.sui.Container#setLocation(float, float)
	 */
	public float getX(){
		return x;
	}

	/**
	 * Gets the y position of this Container, relative
	 * to its parent.
	 * 
	 * 
	 * @return the y position of this component
	 * @see mdes.slick.sui.Container#setLocation(float, float)
	 */
	public float getY(){
		return y;
	}

	/**
	 * Translates the location of this container.
	 * 
	 * @param x the x amount to translate by
	 * @param y the y amount to translate by
	 */
	public void translate( float x, float y ){
		float dx = getX() + x;
		float dy = getY() + y;
		setLocation( dx, dy );
	}

	/**
	 * Gets the absolute x position of this
	 * component. This is <i>not</i> relative to the
	 * parent's position.
	 * 
	 * @return the x position in the GUIContext
	 */
	public float getAbsoluteX(){
		return ( parent == null ) ? x: x + parent.getAbsoluteX();
	}

	/**
	 * Gets the absolute y position of this
	 * component. This is <i>not</i> relative to the
	 * parent's position.
	 * 
	 * @return the y position in the GUIContext
	 */
	public float getAbsoluteY(){
		return ( parent == null ) ? y: y + parent.getAbsoluteY();
	}

	/**
	 * Sets the bounds of this Container.
	 * <p>
	 * The x and y positions are relative to this component's parent. However, if no parent exists, the x and y
	 * positions are equivalent to the absolute x and y positions.
	 * 
	 * 
	 * @param x the x position of this component
	 * @param y the y position of this component
	 * @param width the width of this component
	 * @param height the height of this component
	 */
	public void setBounds( float x, float y, float width, float height ){
		setLocation( x, y );
		setSize( width, height );
	}

	/**
	 * Sets the size of this Container.
	 * 
	 * 
	 * @param width the width of this component
	 * @param height the height of this component
	 */
	public void setSize( float width, float height ){
		setWidth( width );
		setHeight( height );
	}

//	public void setSize( Dimension d ){
//		this.setSize( d.width, d.height );
//	}

	/**
	 * Sets the height of this Container.
	 * 
	 * 
	 * @param height the height of this component
	 */
	public void setHeight( float height ){
		float old = this.height;
		this.height = height;
		if( old != this.width ){
			sizeChanged = true;
			onResize();
		}
	}

	/**
	 * Sets the width of this Container.
	 * 
	 * 
	 * @param width the width of this component
	 */
	public void setWidth( float width ){
		float old = this.width;
		this.width = width;
		if( old != this.width ){
			sizeChanged = true;
			onResize();
		}
	}

	/**
	 * Gets the width of this Container.
	 * 
	 * 
	 * @return the width of this component
	 */
	public float getWidth(){
		return width;
	}

	/**
	 * Gets the height of this Container.
	 * 
	 * 
	 * @return the height of this component
	 */
	public float getHeight(){
		return height;
	}

	/**
	 * Grow the rectangle at all edges by the given amounts. This will
	 * result in the rectangle getting larger around it's centre.
	 * 
	 * @param h the horizontal amount to adjust
	 * @param v the vertical amount to adjust
	 */
	public void grow( float h, float v ){
		setX( getX() - h );
		setY( getY() - v );
		setWidth( getWidth() + ( h * 2 ) );
		setHeight( getHeight() + ( v * 2 ) );
	}

	/**
	 * Grow the rectangle based on scaling it's size
	 * 
	 * @param h The scale to apply to the horizontal
	 * @param v The scale to appy to the vertical
	 */
	public void scaleGrow( float h, float v ){
		grow( getWidth() * ( h - 1 ), getHeight() * ( v - 1 ) );
	}

	public void setAppearanceEnabled( boolean b ){
		this.appearanceEnabled = b;
	}

	public boolean isAppearanceEnabled(){
		return appearanceEnabled;
	}

	/**
	 * Sets the focus ability of this component. Focusable components can
	 * receive key, controller or mouse wheel events if they have the focus.
	 * Non-focusable components will never receive key, controller or mouse
	 * wheel events.
	 * 
	 * @param focusable <tt>true</tt> if this component should receive key, controller
	 * or mouse wheel events when it has the focus
	 */
	public void setFocusable( boolean focusable ){
		if( this.focusable && !focusable )
			releaseFocus();
		this.focusable = focusable;
	}

	public boolean isFocusable(){
		return focusable;
	}

	public void grabFocus(){
//		if( !focusable )
//			return;
//		Display d = getDisplay();
//		if( d == null )
//			return;
//		d.setFocusOwner( this );
//		setWindowsActive( true, d );
	}

	public boolean hasFocus(){
		return focusable && hasFocus;
		/*
		 * Display d = getDisplay();
		 * return d!=null ? d.getFocusOwner()==this : false;
		 */
	}

	/**
	 * Releases the focus on this container and all of its
	 * Window parents. If this component did not have the
	 * focus, no change is made.
	 * <p>
	 * If the top-level parent of this component is an instance of Window, it will be deactivated.
	 */
	public void releaseFocus(){
//		Display display = getDisplay();
//		if( display == null )
//			return;
//
//		if( display.getFocusOwner() == this ){
//			display.setFocusOwner( null );
//			setWindowsActive( false, display );
//		}
	}

	//void setWindowsActive( boolean b, Display display ){
	void setWindowsActive( boolean b ){
//		display.clearActiveWindows();
//		Component top = this;
//		while( top != null ){
//			if( top instanceof Window ){
//				Window win = (Window)top;
//				boolean old = win.isActive();
//				win.setActive( b );
//
//				if( b )
//					display.activeWindows.add( win );
//			}
//			top = top.parent;
//		}
	}

	//TODO: skip input for overlapping buttons
	//EG: click button on a content pane
	//	  but content pane receives event

	/**
	 * Whether the absolute (relative to the GUIContext) x
	 * and y positions are contained within this component.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return <tt>true</tt> if this component contains the specified
	 * point
	 */
	public boolean contains( float x, float y ){
//		ComponentAppearance appearance = getAppearance();
//		if( appearance != null )
//			return appearance.contains( this, x, y );
//		else
			return inside( x, y );
	}

	/**
	 * Checks whether the specified point is within the bounds
	 * of this component.
	 */
	public boolean inside( float x, float y ){
		float ax = getAbsoluteX(), ay = getAbsoluteY();
		return x >= ax && y >= ay && x <= ax + getWidth() && y <= ay + getHeight();
	}

	/**
	 * Glass pane components will ignore events and
	 * the underlying components (ie: the parent panel) will
	 * pick them up instead.
	 * <p>
	 * Still testing this.
	 * 
	 * @param b whether this component should be glass pane
	 */
	public void setGlassPane( boolean b ){
		glassPane = b;
	}

	//TODO: package-protected setParent() which sets parent x/y
	//TODO: local space ints
	//TODO: check for isVisible()

	/**
	 * Whether this component is a glass pane component,
	 * ignoring events and letting them pass through
	 * to underlying components.
	 * 
	 * @return whether this is a glass pane component
	 */
	public boolean isGlassPane(){
		return glassPane;
	}

	public String getToolTipText(){
		return tooltipText;
	}

	public void setToolTipText( String tooltipText ){
		this.tooltipText = tooltipText;
	}

	public String getName(){
		return name;
	}

	public void setName( String name ){
		this.name = name;
	}

	/**
	 * Returns a String representation of this container.
	 * 
	 * @return a String representation of this container
	 */
	public String toString(){
		String str = super.toString();
		if( name != null )
			str += " [" + name + "]";
		return str;
	}

	/**
	 * Can be overridden if you wish this component's mouse
	 * dragged, pressed and released events to pass down to slick.
	 * <p>
	 * For example, the Display does not (by default) consume events.
	 * 
	 * 
	 * @return <tt>true</tt> if we should consume an even on a
	 * mouse press/release of this component
	 */
	protected boolean isConsumingEvents(){
		return true;
	}

	/**
	 * Fires the specified key event to all key listeners
	 * in this component.
	 * 
	 * 
	 * @param id the KeyEvent id constant
	 * @param key the Input constant
	 * @param chr the character of the key
	 * @see mdes.slick.sui.event.KeyEvent
	 */
	protected void processKeyEvent( int id, int key, char chr ){
		KeyListener[] listeners = listenerList.getListeners( KeyListener.class );
		if( listeners.length > 0 ){
			KeyEvent evt = new KeyEvent( this, id, key, chr );
			switch( id ){
			case KeyEvent.KEY_PRESSED:
				for( KeyListener listener: listeners )
					listener.keyPressed( evt );
				break;
			case KeyEvent.KEY_RELEASED:
				for( KeyListener listener: listeners )
					listener.keyReleased( evt );
				break;
			}
		}
	}

	/**
	 * Fires the specified mouse event to all mouse listeners
	 * in this component.
	 * 
	 * 
	 * @param id the MouseEvent id constant
	 * @param button the index of the button (starting at 0)
	 * @param x the local new x position
	 * @param y the local new y position
	 * @param ox the local old x position
	 * @param oy the local old y position
	 * @param absx the absolute x position
	 * @param absy the absolute y position
	 */
	protected void processMouseEvent( int id, int button, int x, int y, int ox, int oy, int absx, int absy ){
		MouseListener[] listeners = listenerList.getListeners( MouseListener.class );
		if( listeners.length > 0 ){
			MouseEvent evt = new MouseEvent( this, id, button, x, y, ox, oy, absx, absy );
			switch( id ){
			case MouseEvent.MOUSE_MOVED:
				for( MouseListener listener: listeners )
					listener.mouseMoved( evt );
				break;
			case MouseEvent.MOUSE_PRESSED:
				for( MouseListener listener: listeners )
					listener.mousePressed( evt );
				break;
			case MouseEvent.MOUSE_RELEASED:
				for( MouseListener listener: listeners )
					listener.mouseReleased( evt );
				break;
			case MouseEvent.MOUSE_DRAGGED:
				for( MouseListener listener: listeners )
					listener.mouseDragged( evt );
				break;
			case MouseEvent.MOUSE_ENTERED:
				for( MouseListener listener: listeners )
					listener.mouseEntered( evt );
				break;
			case MouseEvent.MOUSE_EXITED:
				for( MouseListener listener: listeners )
					listener.mouseExited( evt );
				break;
			}
		}
	}

	/**
	 * Fires the specified mouse event to all mouse listeners
	 * in this component.
	 * 
	 * 
	 * @param id the MouseEvent id constant
	 * @param button the index of the button (starting at 0)
	 * @param x the local x position
	 * @param y the local y position
	 * @param absx the absolute x position
	 * @param absy the absolute y position
	 * @see mdes.slick.sui.event.MouseEvent
	 */
	protected void processMouseEvent( int id, int button, int x, int y, int absx, int absy ){
		processMouseEvent( id, button, x, y, x, y, absx, absy );
	}

	/**
	 * Fires the specified mouse event to all mouse listeners
	 * in this component.
	 * 
	 * 
	 * @param id the MouseEvent id constant
	 * @param x the local x position
	 * @param y the local y position
	 * @param ox the local old x position
	 * @param oy the local old y position
	 * @param absx the absolute x position
	 * @param absy the absolute y position
	 * @see mdes.slick.sui.event.MouseEvent
	 */
	protected void processMouseEvent( int id, int x, int y, int ox, int oy, int absx, int absy ){
		processMouseEvent( id, MouseEvent.NOBUTTON, x, y, ox, oy, absx, absy );
	}

	/**
	 * Fires the specified mouse wheel event to all mouse wheel
	 * listeners in this component.
	 * 
	 * 
	 * @param change the amount the mouse wheel has changed
	 * @see mdes.slick.sui.event.MouseWheelEvent
	 */
	protected void processMouseWheelEvent( int change ){
		MouseWheelListener[] listeners = listenerList.getListeners( MouseWheelListener.class );
		if( listeners.length > 0 ){
			MouseWheelEvent evt = new MouseWheelEvent( this, change );
			for( MouseWheelListener listener: listeners )
				listener.mouseWheelMoved( evt );
		}
	}

	/**
	 * Fires the specified controller event to all controller
	 * listeners in this component.
	 * 
	 * 
	 * @param id the ControllerEvent id
	 * @param controller the controller being used
	 * @param button the button that was pressed/released
	 * @see mdes.slick.sui.event.ControllerEvent
	 */
	protected void fireControllerEvent( int id, int controller, int button ){
		ControllerListener[] listeners = listenerList.getListeners( ControllerListener.class );
		if( listeners.length > 0 ){
			ControllerEvent evt = new ControllerEvent( this, id, controller, button );
			switch( id ){
			case ControllerEvent.BUTTON_PRESSED:
				for( ControllerListener listener: listeners )
					listener.controllerButtonPressed( evt );
				break;
			case ControllerEvent.BUTTON_RELEASED:
				for( ControllerListener listener: listeners )
					listener.controllerButtonReleased( evt );
				break;
			}
		}
	}

	public void onResize(){

	}

	public Object getSkinData(){
		return skinData;
	}

	public void setSkinData( Object skinData ){
		this.skinData = skinData;
	}

	public boolean isRequestFocusEnabled(){
		return requestFocusEnabled;
	}

	public void setRequestFocusEnabled( boolean requestFocusEnabled ){
		this.requestFocusEnabled = requestFocusEnabled;
	}

	public boolean isBorderRendered(){
		return borderVisible;
	}

	/**
	 * Provides a hint for skins as to whether or not this component should
	 * display its border. The default value is <tt>true</tt>, even if the
	 * component has no border. It is up to the skin developer to choose whether
	 * or not it is worth their while to implement support for visible borders
	 * on components.
	 * 
	 * @param borderVisible <tt>true</tt> if a border should be rendered on this
	 * component, assuming it has one defined by the current skin
	 */
	public void setBorderRendered( boolean borderVisible ){
		this.borderVisible = borderVisible;
	}

	public boolean isSizeChanged(){
		return sizeChanged;
	}

	public void setSizeChanged( boolean sizeChanged ){
		this.sizeChanged = sizeChanged;
	}

	//TODO: fix crappy polling stuff
	void pollEnded(){
		this.sizeChanged = false;
	}
}
