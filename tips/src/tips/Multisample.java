package tips;

import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

public class Multisample{
	
	// Simple class to warn if results are not going to be as expected
	static class MultisampleChooser extends DefaultGLCapabilitiesChooser{
		
		public int chooseCapabilities( GLCapabilities desired, List<GLCapabilitiesImmutable> available, int windowSystemRecommendedChoice ){
			boolean anyHaveSampleBuffers = false;
			for( GLCapabilitiesImmutable caps: available ){
				if( caps != null && caps.getSampleBuffers() ){
					anyHaveSampleBuffers = true;
					break;
				}
			}
			int selection = super.chooseCapabilities( desired, available, windowSystemRecommendedChoice );
			if( !anyHaveSampleBuffers ){
				System.err.println( "WARNING: antialiasing will be disabled because none of the available pixel formats had it to offer" );
			}else if( selection >= 0 ){
				GLCapabilitiesImmutable caps = available.get( selection );
				if( !caps.getSampleBuffers() ){
					System.err.println( "WARNING: antialiasing will be disabled because the DefaultGLCapabilitiesChooser didn't supply it" );
				}
			}
			return selection;
		}
	}

	public static void main( String[] args ){
		// set argument 'NotFirstUIActionOnProcess' in the JNLP's application-desc tag for example
		// <application-desc main-class="demos.j2d.TextCube"/>
		//   <argument>NotFirstUIActionOnProcess</argument> 
		// </application-desc>
//		boolean firstUIActionOnProcess = 0 == args.length || !args[0].equals( "NotFirstUIActionOnProcess" );
//		GLProfile.initSingleton( firstUIActionOnProcess );

		new Multisample().run( args );
	}

	public void run( String[] args ){
		GLCapabilities caps = new GLCapabilities( GLProfile.getDefault() );
		GLCapabilitiesChooser chooser = new MultisampleChooser();

		caps.setSampleBuffers( true );
		caps.setNumSamples( 4 );
		
		GLCanvas canvas = new GLCanvas( caps, chooser, null, null );
		canvas.addGLEventListener( new Listener() );

		Frame frame = new Frame( "Full-scene antialiasing" );
		frame.setLayout( new BorderLayout() );
		canvas.setSize( 512, 512 );
		frame.add( canvas, BorderLayout.CENTER );
		frame.pack();
		frame.setVisible( true );
		frame.setLocation( 0, 0 );
		canvas.requestFocus();

		frame.addWindowListener( new WindowAdapter(){
			public void windowClosing( WindowEvent e ){
				runExit();
			}
		} );

		// No antialiasing (for comparison)
		caps.setSampleBuffers( false );
		canvas = new GLCanvas( caps );
		canvas.addGLEventListener( new Listener() );

		frame = new Frame( "No antialiasing" );
		frame.setLayout( new BorderLayout() );
		canvas.setSize( 512, 512 );
		frame.add( canvas, BorderLayout.CENTER );
		frame.pack();
		frame.setVisible( true );
		frame.setLocation( 512, 0 );
		canvas.requestFocus();

		frame.addWindowListener( new WindowAdapter(){
			public void windowClosing( WindowEvent e ){
				runExit();
			}
		} );
	}

	class Listener implements GLEventListener{
		public void init( GLAutoDrawable drawable ){
			System.err.println( "Info: " + drawable );
			GL2 gl = drawable.getGL().getGL2();

			gl.glClearColor( 0, 0, 0, 0 );
			//      gl.glEnable(GL.GL_DEPTH_TEST);
			//      gl.glDepthFunc(GL.GL_LESS);

			gl.glMatrixMode( GL2ES1.GL_MODELVIEW );
			gl.glLoadIdentity();
			gl.glMatrixMode( GL2ES1.GL_PROJECTION );
			gl.glLoadIdentity();
			gl.glOrtho( -1, 1, -1, 1, -1, 1 );
		}

		public void dispose( GLAutoDrawable drawable ){
		}

		public void display( GLAutoDrawable drawable ){
			GL2 gl = drawable.getGL().getGL2();

			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

			int numSteps = 20;
			double increment = Math.PI / numSteps;
			double radius = 1;

			gl.glBegin( GL.GL_LINES );
			for( int i = numSteps - 1; i >= 0; i-- ){
				gl.glVertex3d( radius * Math.cos( i * increment ), radius * Math.sin( i * increment ), 0 );
				gl.glVertex3d( -1.0 * radius * Math.cos( i * increment ), -1.0 * radius * Math.sin( i * increment ), 0 );
			}
			gl.glEnd();
		}

		// Unused routines
		public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ){
		}

		public void displayChanged( GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged ){
		}
	}

	private void runExit(){
		// Note: calling System.exit() synchronously inside the draw,
		// reshape or init callbacks can lead to deadlocks on certain
		// platforms (in particular, X11) because the JAWT's locking
		// routines cause a global AWT lock to be grabbed. Instead run
		// the exit routine in another thread.
		new Thread( new Runnable(){
			public void run(){
				System.exit( 0 );
			}
		} ).start();
	}
}