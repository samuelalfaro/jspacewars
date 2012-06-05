package pruebas.jogl;

import javax.media.nativewindow.NativeSurface;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.MatrixSingleton;
import org.sam.jogl.NodoTransformador;
import org.sam.jogl.Textura;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

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

public class PruebaCursor2 implements WindowListener, KeyListener{
	
	static{
		System.setProperty( "java.awt.headless", "true" );
	}

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

	boolean running = true;

	public void keyPressed( KeyEvent e ){
//		System.err.println( "keyPressed " + e );
	}

	public void keyReleased( KeyEvent e ){
//		System.err.println( "keyReleased " + e );
	}

	public void keyTyped( KeyEvent e ){
//		System.err.println( "keyTyped " + e );
	}

	private static class GUIListener implements MouseListener{
		
		final NodoTransformador cursor;
		final Vector3f posOld;
		final Vector3f posCur;
		
		GUIListener( NodoTransformador cursor ){
			this.cursor = cursor;
			this.posOld = new Vector3f();
			this.posCur = new Vector3f();
		}
	
		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseClicked(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked( MouseEvent e ){
			//System.err.println( "mouseClicked " + e );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseEntered(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered( MouseEvent e ){
			//System.err.println( "mouseEntered " + e );
			posOld.set( posCur );
			posCur.x = e.getX();
			posCur.y = e.getY();
			cursor.getTransform().setTranslation( posCur );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseExited(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseExited( MouseEvent e ){
			//System.err.println( "mouseExited " + e );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mousePressed(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mousePressed( MouseEvent e ){
			//System.err.println( "mousePressed " + e );
			posCur.x = ( e.getX() - posOld.x ) * 40 + e.getX();
			posCur.y = ( e.getY() - posOld.y ) * 40 + e.getY();
			cursor.getTransform().setTranslation( posCur );
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
			posOld.set( posCur );
			posCur.x = e.getX();
			posCur.y = e.getY();
			cursor.getTransform().setTranslation( posCur );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseDragged(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged( MouseEvent e ){
			//System.err.println( "mouseDragged " + e );
			posOld.set( posCur );
			posCur.x = e.getX();
			posCur.y = e.getY();
			cursor.getTransform().setTranslation( posCur );
		}

		/* (non-Javadoc)
		 * @see com.jogamp.newt.event.MouseListener#mouseWheelMoved(com.jogamp.newt.event.MouseEvent)
		 */
		@Override
		public void mouseWheelMoved( MouseEvent e ){
			//System.err.println( "mouseWheelMoved " + e );
		}
	}
	
	private static class Renderer implements GLEventListener{
		
		private Particulas estela;
		private NodoTransformador cursor;
		
		public Renderer(){
		}

		public void init( GLAutoDrawable glDrawable ){
			GL2 gl = glDrawable.getGL().getGL2();
			
			Apariencia ap = new Apariencia();
			
			ap.setTextura( new Textura( gl, Textura.Format.ALPHA, Imagen.cargarToBufferedImage( "resources/texturas/spark.jpg" ), true ) );
			ap.getTextura().setWrap_s( Textura.Wrap.CLAMP_TO_BORDER);
			ap.getTextura().setWrap_t( Textura.Wrap.CLAMP_TO_BORDER );
			
			ap.setAtributosTextura( new AtributosTextura() );
			
			ap.getAtributosTextura().setMode( AtributosTextura.Mode.COMBINE );
			ap.getAtributosTextura().setCombineRgbMode( AtributosTextura.CombineMode.REPLACE );
			ap.getAtributosTextura().setCombineRgbSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_COLOR
			);
			ap.getAtributosTextura().setCombineAlphaMode( AtributosTextura.CombineMode.MODULATE );
			ap.getAtributosTextura().setCombineAlphaSource0(
					AtributosTextura.CombineSrc.OBJECT, AtributosTextura.CombineOperand.SRC_ALPHA
			);
			ap.getAtributosTextura().setCombineAlphaSource1(
					AtributosTextura.CombineSrc.TEXTURE, AtributosTextura.CombineOperand.SRC_ALPHA
			);			
			
			ap.setAtributosTransparencia( 
					new AtributosTransparencia( 
							AtributosTransparencia.Equation.ADD,
							AtributosTransparencia.SrcFunc.SRC_ALPHA,
							AtributosTransparencia.DstFunc.ONE
					) 
			);
			
			FactoriaDeParticulas.setOptimizedFor2D(true);
			
			estela = FactoriaDeParticulas.createParticulas( 50 );
			estela.setEmision( Particulas.Emision.CONTINUA );
			estela.setRangoDeEmision( 1.0f );
			estela.setEmisor( new Emisor.Puntual() );
			estela.setVelocidad( 100.0f, 20.0f, false );
			estela.setTiempoVida( 0.25f );
			estela.setGiroInicial( 0, 180, true );
			estela.setVelocidadGiro( 30.0f, 15.0f, true );
			estela.setColor(
					0.15f,
					0.3f,
					0.2f,
					GettersFactory.Float.create( 
							new float[] { 0.25f, 1.0f },
							new float[] { 1.0f, 0.0f },
							MetodoDeInterpolacion.Predefinido.COSENOIDAL
					)
			);
			estela.setRadio( 	
					GettersFactory.Float.create( 
						new float[] { 0.0f, 0.25f },
						new float[] { 0.0f, 15.0f },
						MetodoDeInterpolacion.Predefinido.COSENOIDAL
					)
			);
			estela.reset();
			estela.setApariencia( ap );
			
			Matrix4f tLocal = new Matrix4f();
			tLocal.setIdentity();
			
			cursor = new NodoTransformador( tLocal, estela );
			
			gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
			gl.glClearColor( 0.2f, 0.2f, 0.3f, 0.0f );
			
			GLWindow component = ( (GLWindow)glDrawable );
			component.addMouseListener( new GUIListener( cursor ) );
		}

		private transient long tAnterior, tActual;
		
		public void display( GLAutoDrawable glDrawable ){
			tAnterior = tActual;
			tActual = System.nanoTime();
			// @SuppressWarnings("unused")
			float incT = (float)( tActual - tAnterior ) / 1000000000;
		
			GL2 gl = glDrawable.getGL().getGL2();

			gl.glClear( GL.GL_COLOR_BUFFER_BIT );
			
			gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
			gl.glLoadIdentity();
			MatrixSingleton.loadModelViewMatrix();
			
			estela.getModificador().modificar( incT );
			cursor.draw( gl );
			gl.glFlush();
		}

		public void reshape( GLAutoDrawable glDrawable, int x, int y, int w, int h ){
			if( h == 0 )
				h = 1;
			GL2 gl = glDrawable.getGL().getGL2();
			
			gl.glViewport( 0, 0, w, h );
			
			gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
			gl.glLoadIdentity();
			gl.glOrtho( 0, w, h, 0, -1, 1 );
			
			MatrixSingleton.loadProjectionMatrix();
		}

		/* (non-Javadoc)
		 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
		 */
		@Override
		public void dispose( GLAutoDrawable glDrawable ){
		}
	}
	
	void run(){
		try{
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
			window.addKeyListener( this );
			//window.addMouseListener( this );
			

			// let's get notified if window is closed
			
			Renderer renderer = new Renderer();
			window.addGLEventListener( renderer );
			
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
			
		}catch( Throwable t ){
			t.printStackTrace();
		}
	}
	
	public static void main( String[] args ){
		new PruebaCursor2().run();
	}
}
