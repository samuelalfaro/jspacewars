package tips;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

public class AntialiasingExample extends JFrame implements GLEventListener, KeyListener{

	private GLCanvas canvas;
	private float rotAngle = 0f;
	private boolean rotate = false;

	public AntialiasingExample(){
		super( "aargb" );
		GLCapabilities tGLCapabilities = new GLCapabilities( GLProfile.getDefault() );
		//enable/configure multisampling support ...
		tGLCapabilities.setSampleBuffers( true );
		tGLCapabilities.setNumSamples( 4 );
		tGLCapabilities.setAccumAlphaBits( 16 );
		tGLCapabilities.setAccumBlueBits( 16 );
		tGLCapabilities.setAccumGreenBits( 16 );
		tGLCapabilities.setAccumRedBits( 16 );
		
		canvas = new GLCanvas( tGLCapabilities );
		canvas.addGLEventListener( this );
		canvas.addKeyListener( this );

		getContentPane().add( canvas );
	}

	public void run(){
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 512, 512 );
		setLocationRelativeTo( null );
		setVisible( true );
		canvas.requestFocusInWindow();
	}

	public static void main( String[] args ){
		new AntialiasingExample().run();
	}

	public void init( GLAutoDrawable drawable ){
		GL gl = drawable.getGL();
		//
		float values[] = new float[2];
		gl.glGetFloatv( GL2.GL_LINE_WIDTH_GRANULARITY, values, 0 );
		System.out.println( "GL.GL_LINE_WIDTH_GRANULARITY value is " + values[0] );
		gl.glGetFloatv( GL2.GL_LINE_WIDTH_RANGE, values, 0 );
		System.out.println( "GL.GL_LINE_WIDTH_RANGE values are " + values[0] + ", " + values[1] );
		gl.glEnable( GL.GL_LINE_SMOOTH );
		gl.glHint( GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST );
		gl.glEnable( GL.GL_BLEND );
		//gl.glBlendEquation( GL.GL_FUNC_ADD );
		gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glLineWidth( 1.5f );
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
	}

	public void display( GLAutoDrawable drawable ){
		GL2 gl = drawable.getGL().getGL2();
		
//		gl.glAccum( GL2.GL_LOAD, 0.0f );
//		for( int i = 0; i < 4; ++i ){
//			// jitter scene to sample location i
//			// render the entire scene
//			gl.glAccum( GL2.GL_ACCUM, sampleweight[i] );
//		}
//		gl.glAccum( GL2.GL_RETURN, 1.0f );
		//
		gl.glClear( GL.GL_COLOR_BUFFER_BIT );
		gl.glColor3f( 0.0f, 1.0f, 0.0f );
		gl.glPushMatrix();
		gl.glRotatef( -rotAngle, 0.0f, 0.0f, 0.1f );
		gl.glBegin( GL.GL_LINES );
		gl.glVertex2f( -0.5f, 0.5f );
		gl.glVertex2f( 0.5f, -0.5f );
		gl.glEnd();
		gl.glPopMatrix();
		gl.glColor3f( 0.0f, 0.0f, 1.0f );
		gl.glPushMatrix();
		gl.glRotatef( rotAngle, 0.0f, 0.0f, 0.1f );
		gl.glBegin( GL.GL_LINES );
		gl.glVertex2f( 0.5f, 0.5f );
		gl.glVertex2f( -0.5f, -0.5f );
		gl.glEnd();
		gl.glPopMatrix();
		gl.glFlush();
		if( rotate )
			rotAngle += 1f;
		if( rotAngle >= 360f )
			rotAngle = 0f;
	}

	public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ){
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		gl.glViewport( 0, 0, w, h );
		gl.glMatrixMode( GL2.GL_PROJECTION );
		gl.glLoadIdentity();
		if( w <= h ) //
			glu.gluOrtho2D( -1.0, 1.0, -1.0 * (float)h / (float)w, //
					1.0 * (float)h / (float)w );
		else
			glu.gluOrtho2D( -1.0 * (float)w / (float)h, //
					1.0 * (float)w / (float)h, -1.0, 1.0 );
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glLoadIdentity();
	}

	public void displayChanged( GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged ){
	}

	public void keyTyped( KeyEvent key ){
	}

	public void keyPressed( KeyEvent key ){
		switch( key.getKeyCode() ){
		case KeyEvent.VK_ESCAPE:
			System.exit( 0 );
		case KeyEvent.VK_R:
			rotate = !rotate;
			canvas.display();
		default:
			break;
		}
	}

	public void keyReleased( KeyEvent key ){
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose( GLAutoDrawable glautodrawable ){
		// TODO Auto-generated method stub

	}
}