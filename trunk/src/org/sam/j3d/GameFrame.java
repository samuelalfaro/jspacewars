package org.sam.j3d;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;

/**
 * This class is the main game frame for the windowed or full screen application.
 *
 * @author  Norbert Nopper
 */
public class GameFrame extends JFrame implements Runnable, WindowListener, MouseInputListener, MouseWheelListener, KeyListener {
	private static final long serialVersionUID = 1L;
	final private static int MOUSEBUTTONS = 3;
	final private static int KEYS = 256;
	
	/**
	 * Thread for updating the scene.
	 */
	private Thread runThread;
	
	/**
	 * Flag, if the application is running in window or full screen mode.
	 */
	private boolean windowed;
	
	/**
	 * Original display mode. Needed for setting back after leaving the application.
	 */
	private DisplayMode originalDisplayMode;
	
	/**
	 * Graphics device for the full screen mode.
	 */
	private GraphicsDevice graphicsDevice;
	
	/**
	 * The canvas for rendering 3D scenes.
	 */
	protected Canvas3D canvas3d;
	
	/**
	 * Graphics context for rendering 3D scenes.
	 */
	protected GraphicsContext3D gc3D;
	
	/**aaa
	 * Empty universe for immediate mode rendering.
	 */
	protected SimpleUniverse simpleUniverse;
	
	/**
	 * Flag, if the game mouse is enabled.
	 */
	protected boolean gameMouse;
	
	/**
	 * The x position of the absolute mouse.
	 */
	protected int mouseX;
	
	/**
	 * The y position of the absolute mouse.
	 */
	protected int mouseY;
	
	/**
	 * Flag, if the mouse was moved or not.
	 */
	private boolean mouseMoved;
	
	/**
	 * Internal storage for mouse movement
	 */
	private Point bufferedMouseMovement = new Point(0, 0);
	
	/**
	 * Internal storage for correcting mouse movement data 
	 */
	private Point mousePositionCorrectionData = new Point(0, 0);
	
	/**
	 * Horizontial delta movement of the mouse in.
	 * 
	 * The value deltaX is negative for moving the mouse to the left,
	 * possitive for moving it to the right or zero if it wasn't moved along the x-axis.
	 */
	protected int deltaX;
	
	/**
	 * Vertical delta movement of the mouse.
	 * 
	 * The value deltaY is negative for moving the mouse to the bottom,
	 * possitive for moving it to the top or zero if it wasn't moved along the y-axis.
	 */
	protected int deltaY;
	
	/**
	 * Array for the three mouse buttons.
	 */
	public boolean[] mouseButton = new boolean[MOUSEBUTTONS];
	
	/**
	 * Integer value of the mouse wheel position.
	 */
	protected int mouseWheel;
	
	/**
	 * Flag, if the mouse wheel was moved or not.
	 */
	private boolean mouseWheelMoved;
	
	/**
	 * AWT Toolkit to control the mouse cursor.
	 */
	private Robot mouseRobot;
	
	/**
	 * Array for the game keys.
	 */
	protected boolean[] gameKeys = new boolean[KEYS];
	
	/**
	 * Initialize the game frame without deciding to start in full or window mode.
	 */
	public GameFrame() {
		// pass the graphic configuration to the super class
		super(SimpleUniverse.getPreferredConfiguration());
		
		// at this point of time the application is in window mode
		windowed = true;
		
		// save the original display mode
		this.originalDisplayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		
		// get the graphics device
		this.graphicsDevice = SimpleUniverse.getPreferredConfiguration().getDevice();
		
		// prepare the layout for one container
		getContentPane().setLayout(new BorderLayout());
		
		// create the canvas3d to render on
		canvas3d = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		
		// stop the renderer for immediate mode
		canvas3d.stopRenderer();   
		
		// get the graphics context 3D
		gc3D = canvas3d.getGraphicsContext3D();
		
		// add the cancas3d to the frame
		getContentPane().add(canvas3d, BorderLayout.CENTER);
		
		// create the simple universe
		simpleUniverse = new SimpleUniverse(canvas3d);
		
		//enable sound
		simpleUniverse.getViewer().createAudioDevice();    
		
		//stops view from being updated automaticaly
		canvas3d.getView().stopView();
		
		canvas3d.getView().stopBehaviorScheduler();
		
		// listen to window events
		addWindowListener(this);
		
		// listen to the mouse events on the canvas 3d
		canvas3d.addMouseListener(this);
		canvas3d.addMouseMotionListener(this);
		canvas3d.addMouseWheelListener(this);
		
		// game mouse to off
		gameMouse = false;
		
		// create a robot to control the mouse cursor
		try {
			mouseRobot = new Robot(graphicsDevice);
		} catch (AWTException awte) {
			System.err.println(awte);
			System.exit(1);
		}
		
		// listen to the key events
		canvas3d.addKeyListener(this);
	}
	
	/**
	 * Method for creating the window screen.
	 *
	 * @param width the width of the screen
	 * @param height the width of the screen
	 *
	 * @return true, if the creation was successful
	 */
	final public boolean makeWindowScreen(int width, int height) {
		windowed = true;
		
		// resize the frame
		setSize(width, height);
		
		// validate the frame
		validate();
		
		// make the frame visible
		setVisible(true);
		
		init();
		
		return true;
	}
	
	/**
	 * Method for creating the full screen. The method checks, if the graphic
	 * card supports the given resolution.
	 *
	 * @param width the width of the screen
	 * @param height the width of the screen
	 * @param bitDepth the bit depth of the color (8, 16, or 32)
	 * @param refreshRate refresh rate
	 *
	 * @return true, if the creation was successful
	 */
	final public boolean makeFullScreen(int width, int height, int bitDepth, int refreshRate) {
		windowed = false;
		
		// check, if full screen mode is supported: return
		if (!graphicsDevice.isFullScreenSupported())
			return false;
		
		// check, if the given mode is supported
		boolean available = false;
		int index = 0;
		
		DisplayMode allModes[] = graphicsDevice.getDisplayModes();
		
		// check, if display mode is available
		while (!available && index < allModes.length) {
			if (allModes[index].getWidth() == width && allModes[index].getHeight() == height && allModes[index].getBitDepth() == bitDepth && allModes[index].getRefreshRate() == refreshRate) {
				
				// display mode is available
				available = true;
			}
			
			index++;
		}
		
		// display mode is not available: return
		if (!available)
			return false;
		
		// no decorations for the frame
		setUndecorated(true);
		
		//setExtendedState(MAXIMIZED_BOTH);
		
		// do not allow to resize the frame
		setResizable(false);
		
		//set the full screen mode to this frame
		graphicsDevice.setFullScreenWindow(this);
		
		// create the new display mode
		DisplayMode newDisplayMode = new DisplayMode(width, height, bitDepth, refreshRate);
		
		// sets the new display mode
		graphicsDevice.setDisplayMode(newDisplayMode);
		
		// resize the frame
		//setSize(new Dimension(newDisplayMode.getWidth(), newDisplayMode.getHeight()));
		
		// validate the frame
		validate();
		
		init();
		
		return true;
	}
	
	/**
	 * Returns the current graphics device.
	 *
	 * @return the graphics device
	 */
	final public GraphicsDevice getGraphicsDevice() {
		return graphicsDevice;
	}
	
	/**
	 * Start the update and render thread.
	 */
	final private void init() {
		if (runThread == null)
			runThread = new Thread(this);
		
		runThread.start();
	}
	
	/**
	 * Exit the game frame and the whole application.
	 */
	final protected void exit() {
		// stop the thread and theapplication exits
		runThread = null;
	}
	
	final public void windowActivated(WindowEvent windowEvent) {
	}
	
	final public void windowClosed(WindowEvent windowEvent) {
	}
	
	/**
	 * The method cleans up the application and is directly called by the window listener.
	 *
	 * @param windowEvent the passed event
	 */
	final public void windowClosing(WindowEvent windowEvent) {
		// clean up the system
		exit();
	}
	
	final public void windowDeactivated(WindowEvent windowEvent) {
	}
	
	final public void windowDeiconified(WindowEvent windowEvent) {
	}
	
	final public void windowIconified(WindowEvent windowEvent) {
	}
	
	/**
	 * The method starts the render thread and is directly called by the window listener.
	 *
	 * @param windowEvent the passed event
	 */
	final public void windowOpened(WindowEvent windowEvent) {
		// start the render thread
		//init();
	}
	
	/**
	 * The thread for updating the scene.
	 */
	final public void run() {
		
		// center the mouse
		mouseRobot.mouseMove(canvas3d.getWidth() / 2, canvas3d.getHeight() / 2);
		
		// mouse was not moved
		mouseMoved = false;
		
		// mouse wheel was not moved
		mouseWheelMoved = false;
		
		// allow the game to initalize itself
		initGame();
		
		// clean up what is possible
		System.gc();
		
		canvas3d.stopRenderer();
		
		canvas3d.getView().stopView();
		
		long gameTime = 0;
		long currentTime = System.currentTimeMillis();
		long lastTime = currentTime;
		long lastMouseMove = currentTime;
		
		while (runThread == Thread.currentThread()) {
			
			//		copy mouse movement values
			if (gameMouse) {
				synchronized (bufferedMouseMovement) {
					if (mouseMoved) {
						deltaX = bufferedMouseMovement.x;
						deltaY = bufferedMouseMovement.y;
						bufferedMouseMovement.x = bufferedMouseMovement.y = 0;
						lastMouseMove = currentTime;
						mouseMoved = false;
					} else {
						deltaX = deltaY = 0;
						if (currentTime - lastMouseMove > 250)
							mousePositionCorrectionData.x = mousePositionCorrectionData.y = 0;
					}
				}
			}
			
			//canvas3d.getView().stopView();
			
			updateGame(gameTime);
			
			canvas3d.getView().renderOnce();
			
			renderGame();
			
			//canvas3d.getView().startView();
			
			// swap the scene
			canvas3d.swap();
			
			// allow other threads to execute
			Thread.yield();
			
			// reset the mouse wheel movement value
			if (!mouseWheelMoved)
				mouseWheel = 0;
			mouseWheelMoved = false;
			
			// adjust the game time
			currentTime = System.currentTimeMillis();
			gameTime += currentTime - lastTime;
			lastTime = currentTime;
		}
		
		// allow the game to clean up itself ...
		exitGame();
		
		// ... and now the engine
		
		// clear the universe
		if (simpleUniverse != null)
			simpleUniverse.removeAllLocales();
		
		// reset the display to the original configuration
		if (graphicsDevice != null && windowed == false) {
			graphicsDevice.setDisplayMode(originalDisplayMode);
			graphicsDevice.setFullScreenWindow(null);
		}
		
		// finally exit
		System.exit(0);
		
	}
	
	/**
	 * Overwrite this method for making your own initialization method!
	 */
	protected void initGame() {
	}
	
	/**
	 * Overwrite this method for making your own update method!
	 *
	 *@param currentTime the current game time in miliseconds, starting with 0
	 */
	protected void updateGame(long currentTime) {
		// exit the application by pressing the ESC key.
		if (gameKeys[27])
			exit();
	}
	
	/**
	 * Overwritete this method for making your own render method!
	 */
	protected void renderGame() {
		gc3D.clear();
		
	}
	
	/**
	 * Overwrite this method for making your own exit method!
	 */
	protected void exitGame() {
	}
	
	/**
	 * The method updates the mouse position.
	 *
	 * @param mouseEvent the passed event
	 */
	final private void updateMousePosition(MouseEvent mouseEvent) {
		synchronized (bufferedMouseMovement) {
			Point mouseDelta = new Point(mouseEvent.getX() - mouseX, mouseY-mouseEvent.getY());
			bufferedMouseMovement.x += mouseDelta.x + mousePositionCorrectionData.x;
			bufferedMouseMovement.y += mouseDelta.y + mousePositionCorrectionData.y;
			mousePositionCorrectionData.x = mouseDelta.x + mousePositionCorrectionData.x;
			mousePositionCorrectionData.y = mouseDelta.y + mousePositionCorrectionData.y;
			mouseX = mouseEvent.getX();
			mouseY = mouseEvent.getY();
			if (gameMouse) {
				mouseRobot.mouseMove(canvas3d.getWidth() / 2, canvas3d.getHeight() / 2);
			}
			
			mouseMoved = true;
			//mouseEvent.consume();
		}
		
	}
	
	/**
	 * The method updates the mouse buttons.
	 *
	 * @param mouseEvent the passed event
	 */
	final private void updateMouseButton(MouseEvent mouseEvent) {
		
		// pass the button to the engine
		if (mouseEvent.getButton() > 0 && mouseEvent.getButton() <= MOUSEBUTTONS) {
			if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED)
				mouseButton[mouseEvent.getButton() - 1] = true;
			else
				mouseButton[mouseEvent.getButton() - 1] = false;
		}
		
		mouseEvent.consume();
		
	}
	
	/**
	 * The method updates the mouse position.
	 *
	 * @param mouseEvent the passed event
	 */
	final public void mouseMoved(MouseEvent mouseEvent) {
		updateMousePosition(mouseEvent);
	}
	
	/**
	 * The method updates the mouse position.
	 *
	 * @param mouseEvent the passed event
	 */
	final public void mouseDragged(MouseEvent mouseEvent) {
		updateMousePosition(mouseEvent);
	}
	
	/**
	 * The method updates the mouse buttons.
	 *
	 * @param mouseEvent the passed event
	 */
	final public void mousePressed(MouseEvent mouseEvent) {
		updateMouseButton(mouseEvent);
	}
	
	/**
	 * The method updates the mouse buttons.
	 *
	 * @param mouseEvent the passed event
	 */
	final public void mouseReleased(MouseEvent mouseEvent) {
		updateMouseButton(mouseEvent);
	}
	
	final public void mouseClicked(MouseEvent mouseEvent) {
	}
	
	final public void mouseEntered(MouseEvent mouseEvent) {
	}
	
	final public void mouseExited(MouseEvent mouseEvent) {
	}
	
	/**
	 * The method updates the mouse wheel.
	 *
	 * @param mouseWheelEvent the passed event
	 */
	final public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
		// change the mouse wheel
		mouseWheel += mouseWheelEvent.getWheelRotation();
		
		mouseWheelMoved = true;
		mouseWheelEvent.consume();
	}
	
	final public void keyPressed(KeyEvent keyEvent) {
		updateGameKeys(keyEvent);
	}
	
	final public void keyReleased(KeyEvent keyEvent) {
		updateGameKeys(keyEvent);
	}
	
	final public void keyTyped(KeyEvent keyEvent) {
	}
	
	final private void updateGameKeys(KeyEvent keyEvent) {
		// System.out.println(keyEvent.getKeyCode());
		// pass the key status to the engine
		if (keyEvent.getKeyCode() >= 0 && keyEvent.getKeyCode() < KEYS) {
			if (keyEvent.getID() == KeyEvent.KEY_PRESSED)
				gameKeys[keyEvent.getKeyCode()] = true;
			else
				gameKeys[keyEvent.getKeyCode()] = false;
		}
		keyEvent.consume();
	}
	
	public TransformGroup getWorldTransformGroup(){
		return simpleUniverse.getViewingPlatform().getViewPlatformTransform();
	}
}
