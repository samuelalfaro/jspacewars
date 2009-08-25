package org.sam.jspacewars;

import java.awt.*;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

import com.sun.opengl.util.Animator;

public class ExampleGameMenuJOGL {

	private static void mostrar(Component component) {

		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame(myDevice.getDefaultConfiguration());
		frame.setUndecorated(true);
		frame.setResizable(false);

		if( myDevice.isFullScreenSupported() ){
			myDevice.setFullScreenWindow(frame);
			if( myDevice.isDisplayChangeSupported() ){
				DisplayMode currentDisplayMode = myDevice.getDisplayMode();
//				currentDisplayMode = new DisplayMode(640, 400, currentDisplayMode.getBitDepth(), currentDisplayMode.getRefreshRate());
				try{
					myDevice.setDisplayMode(currentDisplayMode);
					frame.setSize(currentDisplayMode.getWidth(), currentDisplayMode.getHeight());
				}catch( IllegalArgumentException e ){
					System.err.println("Display Mode: not supported!!");
				}
			}
			if( Container.class.isAssignableFrom( component.getClass() ) )
				frame.setContentPane( (Container)component );
			else{
				frame.getContentPane().setLayout( new BorderLayout() );
				frame.getContentPane().add(component, BorderLayout.CENTER);
			}
			frame.validate();
		}else{
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(0, 0, dim.width, dim.height);
			if( Container.class.isAssignableFrom( component.getClass() ) )
				frame.setContentPane( (Container)component );
			else{
				frame.getContentPane().setLayout( new BorderLayout() );
				frame.getContentPane().add(component, BorderLayout.CENTER);
			}
			frame.setVisible(true);
		}
		component.requestFocus();
	}	
	
	public static void main(String[] args) {

		DataGame dataGame = new DataGame();

		SplashWindow splashFrame = new SplashWindow("splash.png", dataGame);
		splashFrame.setVisible(true);

		Animator animator = new Animator();
		animator.setRunAsFastAsPossible(true);
		animator.setPrintExceptions(true);
		
		splashFrame.setVisible(false);
		splashFrame = null;
		System.gc();
		
		GLCanvas canvas = new GLCanvas( new GLCapabilities(), null, dataGame.getGLContext(), null );
		canvas.addGLEventListener( new GLEventListenerBackgroundRenderer( dataGame.getFondo()) );
		GameMenu gameMenu = new GameMenu();
		canvas.addGLEventListener( new GLEventListenerDisplayGUI( gameMenu ) );
		animator.add(canvas);
		
		mostrar(canvas);
		
		animator.start();
	}
}
