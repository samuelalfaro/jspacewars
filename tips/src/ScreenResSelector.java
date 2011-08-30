/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

public class ScreenResSelector{

	@SuppressWarnings( "serial" )
	static class SelectionDialog extends JFrame{

		private static void center( Component component, Dimension containerDimension ){
			Dimension sz = component.getSize();
			int x = ( ( containerDimension.width - sz.width ) / 2 );
			int y = ( ( containerDimension.height - sz.height ) / 2 );
			component.setLocation( x, y );
		}

		private Object monitor = new Object();
		private List<DisplayMode> modes;
		private volatile boolean done = false;
		private volatile int selectedIndex;
		private final JList modeList;

		public SelectionDialog(){
			super();
			setTitle( "Display Modes" );
			modes = Util.getAvailableDisplayModes();
			modeList = new JList( Util.modesToString( modes ) );
			modeList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			modeList.setSelectedIndex( 0 );
			Font defaultFont = UIManager.getFont( "TextField.font" );
			modeList.setFont( new Font( "Monospaced", defaultFont.getStyle(), defaultFont.getSize() ) );
			JScrollPane scroller = new JScrollPane( modeList );
			getContentPane().setLayout( new BorderLayout() );
			JPanel panel = new JPanel();
			panel.setLayout( new BorderLayout() );
			panel.add( new JLabel( "Select full-screen display mode," ), BorderLayout.NORTH );
			panel.add( new JLabel( "or Cancel for windowed mode:" ), BorderLayout.CENTER );
			getContentPane().add( BorderLayout.NORTH, panel );
			getContentPane().add( BorderLayout.CENTER, scroller );
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
			buttonPanel.add( Box.createGlue() );
			JButton button = new JButton( "OK" );
			button.addActionListener( new ActionListener(){
				public void actionPerformed( ActionEvent e ){
					okAction();
				}
			} );
			buttonPanel.add( button );
			buttonPanel.add( Box.createHorizontalStrut( 10 ) );
			button = new JButton( "Cancel" );
			button.addActionListener( new ActionListener(){
				public void actionPerformed( ActionEvent e ){
					cancelAction();
				}
			} );
			buttonPanel.add( button );
			buttonPanel.add( Box.createGlue() );
			getContentPane().add( BorderLayout.SOUTH, buttonPanel );
			setSize( 300, 200 );
			center( this, Toolkit.getDefaultToolkit().getScreenSize() );
		}

		void okAction(){
			selectedIndex = modeList.getSelectedIndex();
			done = true;
			synchronized( monitor ){
				monitor.notify();
			}
			setVisible( false );
			dispose();
		}

		void cancelAction(){
			selectedIndex = -1;
			done = true;
			synchronized( monitor ){
				monitor.notify();
			}
			setVisible( false );
			dispose();
		}

		public void waitFor(){
			synchronized( monitor ){
				while( !done ){
					try{
						monitor.wait();
					}catch( InterruptedException e ){
					}
				}
			}
		}

		public DisplayMode selected(){
			if( selectedIndex < 0 )
				return null;
			return modes.get( selectedIndex );
		}
	}

	private static class Util{

		static interface Filter <T>{
			public boolean filter( T data );
		}

		private static <T>List<T> filterList( List<T> list, Filter<T> filter ){
			List<T> res = new ArrayList<T>();
			for( T data: list ){
				if( filter.filter( data ) )
					res.add( data );
			}
			return res;
		}

		private static GraphicsDevice getDefaultScreenDevice(){
			return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		}

		static List<DisplayMode> getAvailableDisplayModes(){
			GraphicsDevice dev = getDefaultScreenDevice();
			final DisplayMode curMode = dev.getDisplayMode();
			List<DisplayMode> modes = Arrays.asList( dev.getDisplayModes() );

			// Filter everything which is higher frequency than the current
			// display mode
			modes = filterList( modes, new Filter<DisplayMode>(){
				public boolean filter( DisplayMode mode ){
					return( mode.getRefreshRate() <= curMode.getRefreshRate() );
				}
			} );
			// Filter everything that is not at least 24-bit
			modes = filterList( modes, new Filter<DisplayMode>(){
				public boolean filter( DisplayMode mode ){
					// Bit depth < 0 means "multi-depth" -- can't reason about it
					return( mode.getBitDepth() < 0 || mode.getBitDepth() >= 24 );
				}
			} );
			// Filter everything less than 640x480
			modes = filterList( modes, new Filter<DisplayMode>(){
				public boolean filter( DisplayMode mode ){
					return( mode.getWidth() >= 640 && mode.getHeight() >= 480 );
				}
			} );
			if( modes.size() == 0 ){
				throw new RuntimeException( "Couldn't find any valid display modes" );
			}
			return modes;
		}

		static String displayModeToString( DisplayMode mode ){
			return String.format(
					"%10s [%s Hz]", 
					String.format( "%d x %d", mode.getWidth(), mode.getHeight() ),
//					mode.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI ? 
//							"BIT_DEPTH_MULTI":
//							Integer.toString( mode.getBitDepth() ),
					mode.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN ?
							"REFRESH_RATE_UNKNOWN": 
							Integer.toString( mode.getRefreshRate() )
			);
		}

		static String[] modesToString( List<DisplayMode> modes ){
			String[] res = new String[modes.size()];
			int i = 0;
			for( DisplayMode mode: modes )
				res[i++] = displayModeToString( mode );
			return res;
		}
	}

	/**
	 * Shows a modal dialog containing the available screen resolutions and requests selection of one of them.
	 * 
	 * @return The selected one.
	 */
	public static DisplayMode showSelectionDialog(){
		SelectionDialog dialog = new SelectionDialog();
		dialog.setVisible( true );
		dialog.waitFor();
		return dialog.selected();
	}

	public static void main( String[] args ){
		try{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}catch( Throwable ignorada ){
			ignorada.printStackTrace();
		}
		DisplayMode mode = showSelectionDialog();
		if( mode != null ){
			System.err.println( "Selected display mode:" );
			System.err.println( Util.displayModeToString( mode ) );
		}else{
			System.err.println( "No display mode selected." );
		}
		System.exit( 0 );
	}
}
