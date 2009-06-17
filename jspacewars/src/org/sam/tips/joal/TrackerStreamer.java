/**
 * 
 */
package org.sam.tips.joal;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;

import net.java.games.joal.*;
import net.java.games.joal.util.ALut;

import org.sam.util.TrackerInputStream;

public class TrackerStreamer {

	static AL al = null;

	static{
		// Initialize OpenAL and clear the error bit.
		try{
			ALut.alutInit();
			al = ALFactory.getAL();
			al.alGetError();
		}catch( ALException e ){
			System.err.println("Error initializing OpenAL");
			e.printStackTrace();
		}
	}

	private static boolean debug = false;
	private static int totalBytes = 0;

	private static void debugMsg(String str) {
		if( debug )
			System.err.println(str);
	}

	private TrackerInputStream data_input_stream;

	// The size of a chunk from the stream that we want to read for each update.
	private static int BUFFER_SIZE = 4096 * 16;

	// The number of buffers used in the audio pipeline
	private static int NUM_BUFFERS = 2;

	// Buffers hold sound data. There are two of them by default (front/back)
	private int[] buffers = new int[NUM_BUFFERS];

	// Sources are points emitting sound.
	private int[] source = new int[1];

	private int format; // OpenAL data format
	private int rate; // sample rate

	// Position, Velocity, Direction of the source sound.
	private float[] sourcePos = { 0.0f, 0.0f, 0.0f };
	private float[] sourceVel = { 0.0f, 0.0f, 0.0f };
	private float[] sourceDir = { 0.0f, 0.0f, 0.0f };

	private long sleepTime = 200;

	/**
	 * Open the Ogg/Vorbis stream and initialize OpenAL based on the stream
	 * properties
	 */
	public boolean open() {

		try{
			data_input_stream = new TrackerInputStream(new FileInputStream("side_effects.mod"), ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
		}catch( FileNotFoundException e ){
			e.printStackTrace();
		}catch( IOException e ){
			e.printStackTrace();
		}

		AudioFormat audioFormat = data_input_stream.getAudioFormat();

		if( audioFormat.getChannels() == 1 ){
			if( audioFormat.getFrameSize() / audioFormat.getChannels() == 2 )
				format = ALConstants.AL_FORMAT_MONO16;
			else
				format = ALConstants.AL_FORMAT_MONO8;
		}else{
			if( audioFormat.getFrameSize() / audioFormat.getChannels() == 2 ){
				format = ALConstants.AL_FORMAT_STEREO16;
			}else
				format = ALConstants.AL_FORMAT_STEREO8;
		}

		rate = (int) audioFormat.getSampleRate();

		al.alGenBuffers(NUM_BUFFERS, buffers, 0);
		check();
		al.alGenSources(1, source, 0);
		check();

		al.alSourcefv(source[0], ALConstants.AL_POSITION, sourcePos, 0);
		al.alSourcefv(source[0], ALConstants.AL_VELOCITY, sourceVel, 0);
		al.alSourcefv(source[0], ALConstants.AL_DIRECTION, sourceDir, 0);

		al.alSourcef(source[0], ALConstants.AL_ROLLOFF_FACTOR, 0.0f);
		al.alSourcei(source[0], ALConstants.AL_SOURCE_RELATIVE, ALConstants.AL_TRUE);

		return true;
	}

	/**
	 * OpenAL cleanup
	 */
	public void release() {
		al.alSourceStop(source[0]);
		empty();

		for( int i = 0; i < NUM_BUFFERS; i++ ){
			al.alDeleteSources(i, source, 0);
			check();
		}
	}

	/**
	 * Play the Ogg stream
	 */
	public boolean playback() {
		if( playing() )
			return true;

		debugMsg("playback(): stream all buffers");
		for( int i = 0; i < NUM_BUFFERS; i++ ){
			if( !stream(buffers[i]) )
				return false;
		}

		debugMsg("playback(): queue all buffers & play source");
		al.alSourceQueueBuffers(source[0], NUM_BUFFERS, buffers, 0);
		al.alSourcePlay(source[0]);

		return true;
	}

	/**
	 * Check if the source is playing
	 */
	public boolean playing() {
		int[] state = new int[1];

		al.alGetSourcei(source[0], ALConstants.AL_SOURCE_STATE, state, 0);

		return (state[0] == ALConstants.AL_PLAYING);
	}

	/**
	 * Update the stream if necessary
	 */
	public boolean update() {
		int[] processed = new int[1];
		boolean active = true;

		debugMsg("update()");
		al.alGetSourcei(source[0], ALConstants.AL_BUFFERS_PROCESSED, processed, 0);

		while( processed[0] > 0 ){
			int[] buffer = new int[1];

			al.alSourceUnqueueBuffers(source[0], 1, buffer, 0);
			check();
			debugMsg("update(): buffer unqueued => " + buffer[0]);

			active = stream(buffer[0]);

			debugMsg("update(): buffer queued => " + buffer[0]);
			al.alSourceQueueBuffers(source[0], 1, buffer, 0);
			check();

			processed[0]--;
		}

		return active;
	}

	/**
	 * Reloads a buffer (reads in the next chunk)
	 */
	public boolean stream(int buffer) {
		byte[] pcm = new byte[BUFFER_SIZE];
		int size = 0;

		try{
			if( (size = data_input_stream.read(pcm)) <= 0 )
				return false;
		}catch( Exception e ){
			e.printStackTrace();
			return false;
		}

		totalBytes += size;
		debugMsg("stream(): buffer data => " + buffer + " totalBytes:" + totalBytes);

		ByteBuffer data = ByteBuffer.wrap(pcm, 0, size);
		// al.alBufferi(ALConstants., arg1, arg2)
		al.alBufferData(buffer, format, data, size, rate);
		check();

		return true;
	}

	/**
	 * Empties the queue
	 */
	protected void empty() {
		int[] queued = new int[1];
		al.alGetSourcei(source[0], ALConstants.AL_BUFFERS_QUEUED, queued, 0);
		while( queued[0] > 0 ){
			int[] buffer = new int[1];
			al.alSourceUnqueueBuffers(source[0], 1, buffer, 0);
			check();
			queued[0]--;
		}
		data_input_stream = null;
	}

	/**
	 * Check for OpenAL errors...
	 */
	protected void check() {
		if( al.alGetError() != ALConstants.AL_NO_ERROR )
			throw new ALException("OpenAL error raised...");
	}

	/**
	 * The main loop to initialize and play the entire stream
	 */
	public boolean playstream() {
		if( !open() )
			return false;
		// data_input_stream.dump();
		if( !playback() )
			return false;

		while( update() ){
			// We will try sleeping for sometime so that we dont
			// peg the CPU...
			try{
				Thread.sleep(sleepTime);
			}catch( Exception e ){
				e.printStackTrace();
			}
			if( playing() )
				continue;
			if( !playback() )
				return false;
		}
		return true;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		if( al == null )
			return;

		try{
			boolean played = false;
			for( int i = 0; i < args.length; i++ ){
				if( "-bs".equals(args[i]) ){
					BUFFER_SIZE = Integer.valueOf(args[++i]).intValue();
					continue;
				}
				if( "-nb".equals(args[i]) ){
					NUM_BUFFERS = Integer.valueOf(args[++i]).intValue();
					continue;
				}
				if( "-d".equals(args[i]) ){
					debug = true;
					continue;
				}
				System.err.println("Playing Ogg stream : " + args[i]);

				if( (new TrackerStreamer()).playstream() )
					continue;

				played = true;
				System.err.println("ERROR!!");
			}
			if( !played ){
				(new TrackerStreamer()).playstream();
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		System.exit(0);
	}
}