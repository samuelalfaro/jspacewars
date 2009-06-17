/**
 * 
 */
package org.sam.tips.joal;

import java.nio.ByteBuffer;
import java.util.Random;

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

public class MultipleSources {

	static AL al;
	static final int NUM_BUFFERS = 3;
	static final int NUM_SOURCES = 3;

	static final int BATTLE = 0;
	static final int GUN1 = 1;
	static final int GUN2 = 2;

	static int[] buffers = new int[NUM_BUFFERS];
	static int[] sources = new int[NUM_SOURCES];

	static float[][] sourcePos = new float[NUM_SOURCES][3];
	static float[][] sourceVel = new float[NUM_SOURCES][3];
	static float[] listenerPos = { 0.0f, 0.0f, 0.0f };
	static float[] listenerVel = { 0.0f, 0.0f, 0.0f };
	static float[] listenerOri = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };

	static int loadALData() {
		// variables to load into
		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] freq = new int[1];
		int[] loop = new int[1];

		// load wav data into buffers
		al.alGenBuffers(NUM_BUFFERS, buffers, 0);
		if( al.alGetError() != ALConstants.AL_NO_ERROR ){
			return ALConstants.AL_FALSE;
		}

		ALut.alutLoadWAVFile("data/Battle.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[BATTLE], format[0], data[0], size[0], freq[0]);

		ALut.alutLoadWAVFile("data/Gun1.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[GUN1], format[0], data[0], size[0], freq[0]);

		ALut.alutLoadWAVFile("data/Gun2.wav", format, data, size, freq, loop);
		al.alBufferData(buffers[GUN2], format[0], data[0], size[0], freq[0]);

		// bind buffers into audio sources
		al.alGenSources(NUM_SOURCES, sources, 0);

		al.alSourcei(sources[BATTLE], ALConstants.AL_BUFFER, buffers[BATTLE]);
		al.alSourcef(sources[BATTLE], ALConstants.AL_PITCH, 1.0f);
		al.alSourcef(sources[BATTLE], ALConstants.AL_GAIN, 1.0f);
		al.alSourcefv(sources[BATTLE], ALConstants.AL_POSITION, sourcePos[BATTLE], 0);
		al.alSourcefv(sources[BATTLE], ALConstants.AL_POSITION, sourceVel[BATTLE], 0);
		al.alSourcei(sources[BATTLE], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);

		al.alSourcei(sources[GUN1], ALConstants.AL_BUFFER, buffers[GUN1]);
		al.alSourcef(sources[GUN1], ALConstants.AL_PITCH, 1.0f);
		al.alSourcef(sources[GUN1], ALConstants.AL_GAIN, 1.0f);
		al.alSourcefv(sources[GUN1], ALConstants.AL_POSITION, sourcePos[GUN1], 0);
		al.alSourcefv(sources[GUN1], ALConstants.AL_POSITION, sourceVel[GUN1], 0);
		al.alSourcei(sources[GUN1], ALConstants.AL_LOOPING, ALConstants.AL_FALSE);

		al.alSourcei(sources[GUN2], ALConstants.AL_BUFFER, buffers[GUN2]);
		al.alSourcef(sources[GUN2], ALConstants.AL_PITCH, 1.0f);
		al.alSourcef(sources[GUN2], ALConstants.AL_GAIN, 1.0f);
		al.alSourcefv(sources[GUN2], ALConstants.AL_POSITION, sourcePos[GUN2], 0);
		al.alSourcefv(sources[GUN2], ALConstants.AL_POSITION, sourceVel[GUN2], 0);
		al.alSourcei(sources[GUN2], ALConstants.AL_LOOPING, ALConstants.AL_FALSE);

		// do another error check and return
		if( al.alGetError() != ALConstants.AL_NO_ERROR ){
			return ALConstants.AL_FALSE;
		}

		return ALConstants.AL_TRUE;
	}

	static void setListenerValues() {
		al.alListenerfv(ALConstants.AL_POSITION, listenerPos, 0);
		al.alListenerfv(ALConstants.AL_VELOCITY, listenerVel, 0);
		al.alListenerfv(ALConstants.AL_ORIENTATION, listenerOri, 0);
	}

	static void killAllData() {
		al.alDeleteBuffers(NUM_BUFFERS, buffers, 0);
		al.alDeleteSources(NUM_SOURCES, sources, 0);
	}

	public static void main(String[] args) {
		try{
			ALut.alutInit();
			al = ALFactory.getAL();
		}catch( ALException e ){
			e.printStackTrace();
			System.exit(1);
		}
		al.alGetError();

		if( loadALData() == ALConstants.AL_FALSE ){
			System.exit(1);
		}
		setListenerValues();
		al.alSourcePlay(sources[BATTLE]);
		long startTime = System.currentTimeMillis();
		long elapsed = 0;
		long totalElapsed = 0;
		Random rand = new Random();
		int[] state = new int[1];
		
		int nextGun = rand.nextInt(800);
		while( totalElapsed < 1000000 ){
			elapsed = System.currentTimeMillis() - startTime;
			
			if( elapsed > nextGun ){
				totalElapsed += elapsed;
				startTime = System.currentTimeMillis();
				// pick one of the two guns
				int pick = Math.abs((rand.nextInt()) % 2) + 1;
				al.alGetSourcei(sources[pick], ALConstants.AL_SOURCE_STATE, state, 0);
//				if( state[0] != ALConstants.AL_PLAYING ){
					double theta = rand.nextDouble() * 2.0 * Math.PI;
					sourcePos[pick][0] = -((float) Math.cos(theta));
					sourcePos[pick][1] = -((float) (rand.nextInt() % 2));
					sourcePos[pick][2] = -((float) Math.sin(theta));

					al.alSourcefv(sources[pick], ALConstants.AL_POSITION, sourcePos[pick], 0);

					al.alSourcePlay(sources[pick]);
//				}
				nextGun = rand.nextInt(800);
			}
		}
		killAllData();
		System.exit(0);
	}
}
