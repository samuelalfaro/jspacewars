package pruebas.jogl;

import javax.media.nativewindow.NativeSurface;
import javax.media.opengl.GLCapabilities;
import javax.vecmath.Matrix4f;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;

public class PruebaCursorNativeWindow implements WindowListener{
	
	static{
		System.setProperty( "java.awt.headless", "true" );
	}

	boolean running = true;
	
	public void windowRepaint( WindowUpdateEvent e ){
//		System.err.println( "windowRepaint " + e );
	}

	public void windowResized( WindowEvent e ){
//		System.err.println( "windowResized " + e );
	}

	public void windowMoved( WindowEvent e ){
//		System.err.println( "windowMoved " + e );
	}

	public void windowGainedFocus( WindowEvent e ){
//		System.err.println( "windowGainedFocus " + e );
	}

	public void windowLostFocus( WindowEvent e ){
//		System.err.println( "windowLostFocus " + e );
	}

	public void windowDestroyNotify( WindowEvent e ){
//		System.err.println( "windowDestroyNotify " + e );
		// stop running ..
		running = false;
	}

	public void windowDestroyed( WindowEvent e ){
//		System.err.println( "windowDestroyed " + e );
	}

	private static class MyKeyListener implements KeyListener{

		MyKeyListener(){
		}

		public void keyPressed( KeyEvent e ){
			System.err.println( "keyPressed " + e );
		}

		public void keyReleased( KeyEvent e ){
			System.err.println( "keyReleased " + e );
		}

		public void keyTyped( KeyEvent e ){
			System.err.println( "keyTyped " + e );
		}
	}

	private static class CursorListener implements MouseListener{
		
		private transient final Matrix4f transform;
		
		CursorListener( Matrix4f transform ){
			this.transform = transform;
		}
		
		private void setPosition( int x, int y ){
			transform.m03 = x;
			transform.m13 = y;
		}
		
		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseEntered(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered( MouseEvent e ){
			System.err.println( "mouseEntered " + e );
			setPosition( e.getX(), e.getY() );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseExited(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseExited( MouseEvent e ){
			System.err.println( "mouseExited " + e );
			transform.m03 =	( e.getX() - transform.m03 ) * 80 + e.getX();
			transform.m13 =	( e.getY() - transform.m13 ) * 80 + e.getY();
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseClicked(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked( MouseEvent e ){
			System.err.println( "mouseClicked " + e );
		}
		
		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mousePressed(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mousePressed( MouseEvent e ){
			//System.err.println( "mousePressed " + e );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseReleased(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased( MouseEvent e ){
			//System.err.println( "mouseReleased " + e );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseMoved(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved( MouseEvent e ){
			//System.err.println( "mouseMoved " + e );
			setPosition( e.getX(), e.getY() );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseDragged(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged( MouseEvent e ){
			//System.err.println( "mouseDragged " + e );
			setPosition( e.getX(), e.getY() );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseWheelMoved(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseWheelMoved( MouseEvent e ){
			//System.err.println( "mouseWheelMoved " + e );
		}
	}
	
	void run(){
		
		GLCapabilities caps = new GLCapabilities(null);
		caps.setRedBits( 8 );
		caps.setGreenBits( 8 );
		caps.setBlueBits( 8 );
		// caps.setBackgroundOpaque(true);

		Display display = NewtFactory.createDisplay( null );
		Screen screen = NewtFactory.createScreen( display, 0 );
		GLWindow window = GLWindow.create( screen, caps );
		
		window.setTitle( "Prueba Cursor" );
		window.setUndecorated( false );
		window.setSize( 256, 256 );
		window.addKeyListener( new MyKeyListener() );
		
		Matrix4f tCursor = new  Matrix4f();
		window.addMouseListener( new CursorListener( tCursor ) );
		
		window.addGLEventListener( new PruebaCursor.Renderer(tCursor) );
		
		window.setVisible( true );
		window.addWindowListener( this );
		while( running ){
			display.dispatchMessages();

			if( window.lockSurface() == NativeSurface.LOCK_SUCCESS ){
				try{
					window.display();
				}finally{
					window.unlockSurface();
				}
			}
			Thread.yield();
			// not necessary Thread.sleep(40);
		}
		window.destroy();
	}
	
	public static void main( String[] args ){
		new PruebaCursorNativeWindow().run();
	}
}
