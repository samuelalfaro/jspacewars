package tips;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

public class PruebaFullScreen{
	@SuppressWarnings( "serial" )
	static class MiPanel extends JPanel{
		public MiPanel(){
			this.addKeyListener( new KeyAdapter(){
				public void keyPressed( KeyEvent e ){
					int keyCode = e.getKeyCode();
					if( ( keyCode == KeyEvent.VK_ESCAPE ) || ( keyCode == KeyEvent.VK_Q )
							|| ( keyCode == KeyEvent.VK_END ) || ( ( keyCode == KeyEvent.VK_C ) && e.isControlDown() ) )
						System.exit( 0 );
				}
			} );
			this.setFocusable( true );
		}

		public void paintComponent( Graphics g ){
			g.drawLine( 0, 0, this.getWidth(), this.getHeight() );
		}
	}

	private static String DisplayModeToString( DisplayMode mode ){
		return String.format(
				"%10s [%s bits] [%s Hz]",
				String.format( "%d x %d", mode.getWidth(), mode.getHeight() ),
				mode.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI ? "BIT_DEPTH_MULTI": Integer.toString( mode
						.getBitDepth() ),
				mode.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN ? "REFRESH_RATE_UNKNOWN": Integer
						.toString( mode.getRefreshRate() ) );
	}

	static public void main( String args[] ){

		GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		Frame frame = new Frame( myDevice.getDefaultConfiguration() );
		frame.setTitle("FullScreen");  
		frame.setUndecorated( true );
		frame.setResizable( false );
		if( frame.isAlwaysOnTopSupported() ){
			System.out.println("yes");
			frame.setAlwaysOnTop( true );
		}

		if( myDevice.isFullScreenSupported() ){
			System.out.println( "Full-Screen mode" );

			myDevice.setFullScreenWindow( frame );

			if( myDevice.isDisplayChangeSupported() ){

				System.out.println( "Cambiando resolucion de pantalla" );

				System.out.println( "Current Display Mode:" );
				DisplayMode currentDisplayMode = myDevice.getDisplayMode();
				System.out.println( DisplayModeToString( currentDisplayMode ) );

				System.out.println( "\nSuported Display Modes:" );
				for( DisplayMode mode: myDevice.getDisplayModes() )
					System.out.println( DisplayModeToString( mode ) );
				//*
				DisplayMode newDisplayMode = new DisplayMode( 640, 480, currentDisplayMode.getBitDepth(),
						currentDisplayMode.getRefreshRate() );
				/*/
				DisplayMode newDisplayMode = currentDisplayMode;
				//*/
				try{
					myDevice.setDisplayMode( newDisplayMode );
					frame.setSize( newDisplayMode.getWidth(), newDisplayMode.getHeight() );
				}catch( IllegalArgumentException e ){
					System.err.println( "Display Mode: " + DisplayModeToString( newDisplayMode ) + " not supported!!" );
				}
			}
			frame.add( new MiPanel() );
			frame.validate();
		}else{
			System.out.println( "Windowed mode" );
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds( 0, 0, dim.width, dim.height );
			frame.add( new MiPanel() );
			frame.setVisible( true );
		}
		frame.getComponent( 0 ).requestFocus();
	}
}
