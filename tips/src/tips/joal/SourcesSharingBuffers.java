/**
 * 
 */
package tips.joal;

import java.io.*;
import java.nio.*;
import java.util.*;

// For the gui
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

import net.java.games.joal.*;
import net.java.games.joal.util.*;

/**
 * Adapted from <a href="http://www.devmaster.net/">DevMaster</a> <a
 * href="http://www.devmaster.net/articles/openal-tutorials/lesson3.php"
 * >MultipleSources Tutorial</a> by Jesse Maurais.
 * 
 * @author Athomas Goldberg
 * @author Kenneth Russell
 */

public class SourcesSharingBuffers {

	static ALC alc;
    static AL al = null;

    public static final int NUM_BUFFERS = 6;
    
	// These index the buffers.
	public static final int THUNDER 	= 0;
	public static final int WATERDROP	= 1;
	public static final int STREAM		= 2;
	public static final int RAIN		= 3;
	public static final int CHIMES		= 4;
	public static final int OCEAN		= 5;
	
	// Buffers hold sound data.
	static int[] buffers = new int[NUM_BUFFERS];

	// A list of sources for multiple emissions.
	static List<Integer> sources = new ArrayList<Integer>();

	// Position of the source sounds.
	static float[] sourcePos = { 0.0f, 0.0f, 0.0f };

	// Velocity of the source sounds.
	static float[] sourceVel = { 0.0f, 0.0f, 0.0f };

	static void initOpenAL() throws ALException {

		alc = ALFactory.getALC();
		al = ALFactory.getAL();
		
		// Get handle to default device.
		ALCdevice device = alc.alcOpenDevice(null);
		if( device == null ){
			throw new ALException("Error opening default OpenAL device");
		}

		// Get the device specifier.
		String deviceSpecifier = alc.alcGetString(device, ALCConstants.ALC_DEVICE_SPECIFIER);
		if( deviceSpecifier == null ){
			throw new ALException("Error getting specifier for default OpenAL device");
		}

		System.out.println("Using device: \"" + deviceSpecifier + "\"");

		// Create audio context.
		ALCcontext context = alc.alcCreateContext(device, null);
		if( context == null ){
			throw new ALException("Error creating OpenAL context");
		}

		// Set active context.
		alc.alcMakeContextCurrent(context);

		// Check for an error.
		if( alc.alcGetError(device) != ALCConstants.ALC_NO_ERROR ){
			throw new ALException("Error making OpenAL context current");
		}
		
		al.alGetError();
	}

	static void exitOpenAL() {
		// Get the current context.
		ALCcontext curContext = alc.alcGetCurrentContext();
		// Get the device used by that context.
		ALCdevice curDevice = alc.alcGetContextsDevice(curContext);

		// Reset the current context to NULL.
		alc.alcMakeContextCurrent(null);

		// Release the context and the device.
		alc.alcDestroyContext(curContext);
		alc.alcCloseDevice(curDevice);
	}

	static int loadALData() {
		// Variables to load into.
		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] freq = new int[1];
		int[] loop = new int[1];

		System.out.println("0");
		// Load wav data into buffers.
		al.alGenBuffers(NUM_BUFFERS, buffers, 0);
		if( al.alGetError() != ALConstants.AL_NO_ERROR ){
			return ALConstants.AL_FALSE;
		}

		System.out.println("1");
		ALut.alutLoadWAVFile("data/thunder.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[THUNDER], format[0], data[0], size[0], freq[0]);

		ALut.alutLoadWAVFile("data/waterdrop.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[WATERDROP], format[0], data[0], size[0], freq[0]);

		ALut.alutLoadWAVFile("data/stream.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[STREAM], format[0], data[0], size[0], freq[0]);

		ALut.alutLoadWAVFile("data/rain.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[RAIN], format[0], data[0], size[0], freq[0]);

		ALut.alutLoadWAVFile("data/ocean.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[OCEAN], format[0], data[0], size[0], freq[0]);

		ALut.alutLoadWAVFile("data/chimes.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[CHIMES], format[0], data[0], size[0], freq[0]);
		
		System.out.println("2");

		// Do another error check and return.
		if( al.alGetError() != ALConstants.AL_NO_ERROR )
			return ALConstants.AL_FALSE;

		System.out.println("3");
		return ALConstants.AL_TRUE;
	}

	static void addSource(int type) {
		int[] source = new int[1];

		al.alGenSources(1, source, 0);

		if( al.alGetError() != ALConstants.AL_NO_ERROR ){
			System.err.println("Error generating audio source.");
			System.exit(1);
		}

		al.alSourcei(source[0], ALConstants.AL_BUFFER, buffers[type]);
		al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
		al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
		al.alSourcefv(source[0], ALConstants.AL_POSITION, sourcePos, 0);
		al.alSourcefv(source[0], ALConstants.AL_VELOCITY, sourceVel, 0);
		al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);

		al.alSourcePlay(source[0]);
		sources.add(source[0]);
	}

	static void setListenerValues() {
		al.alListenerfv(ALConstants.AL_POSITION, new float[]{ 0.0f, 0.0f, 0.0f }, 0);
		al.alListenerfv(ALConstants.AL_VELOCITY, new float[]{ 0.0f, 0.0f, 0.0f }, 0);
		al.alListenerfv(ALConstants.AL_ORIENTATION, new float[]{ 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f }, 0);
	}

	static void killALData() {
		for( Integer source : sources )
			al.alDeleteSources(1, new int[]{ source }, 0);
		sources.clear();
		al.alDeleteBuffers(NUM_BUFFERS, buffers, 0);
		exitOpenAL();
	}

	static boolean initialized = false;

	static void initialize() {
		if( initialized )
			return;
		initialized = true;
		try{
			initOpenAL();
		}catch( ALException e ){
			e.printStackTrace();
			System.exit(1);
		}
		if( loadALData() == ALConstants.AL_FALSE )
			System.exit(1);
		setListenerValues();
	}

	private static void addButton(JFrame frame, String text, final int whichSound) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addSource(whichSound);
			}
		});
		frame.getContentPane().add(button);
	}

	public static void main(String[] args) {
		boolean gui = true;

		for( int i = 0; i < args.length; i++ ){
			if( args[i].equals("-gui") )
				gui = true;
		}

		initialize();
		if( gui ){
			JFrame frame = new JFrame("Sources Sharing Buffers - DevMaster OpenAL Lesson 5");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(new GridLayout(7, 1));
			addButton(frame, "Add Water Drop", WATERDROP);
			addButton(frame, "Add Thunder", THUNDER);
			addButton(frame, "Add Stream", STREAM);
			addButton(frame, "Add Rain", RAIN);
			addButton(frame, "Add Ocean", OCEAN);
			addButton(frame, "Add Chimes", CHIMES);

			JButton button = new JButton("Quit");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			frame.getContentPane().add(button);

			frame.pack();
			frame.setVisible(true);
		}else{
			char[] c = new char[1];
			while( c[0] != 'q' ){
				try{
					BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
					System.out.println("Press a key and hit ENTER: \n" + "\t'w' for Water Drop\n"
							+ "\t't' for Thunder\n" + "\t's' for Stream\n" + "\t'r' for Rain\n" + "\t'o' for Ocean\n"
							+ "\t'c' for Chimes\n" + "\n'q' to Quit\n");

					buf.read(c);
					switch( c[0] ){
					case 'w':
						addSource(WATERDROP);
						break;
					case 't':
						addSource(THUNDER);
						break;
					case 's':
						addSource(STREAM);
						break;
					case 'r':
						addSource(RAIN);
						break;
					case 'o':
						addSource(OCEAN);
						break;
					case 'c':
						addSource(CHIMES);
						break;
					}
				}catch( IOException e ){
					killALData();
					System.exit(1);
				}
			}
			killALData();
		}
	}
}