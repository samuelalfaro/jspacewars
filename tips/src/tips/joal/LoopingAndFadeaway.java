/**
 * 
 */
package tips.joal;

import java.nio.ByteBuffer;

import net.java.games.joal.*;
import net.java.games.joal.util.ALut;

/**
 * Adapted from <a href="http://www.devmaster.net/">DevMaster</a>
 * <a href="http://www.devmaster.net/articles/openal-tutorials/lesson2.php">LoopingAndFadeaway Tutorial</a>
 * by Jesse Maurais.
 *
 * @author Athomas Goldberg
 * @author Kenneth Russell
 */

public class LoopingAndFadeaway {

	static int[] buffer = new int[1];
	static int[] source = new int[1];
	static float[] sourcePos = { 0.0f, 0.0f, 0.0f };
	static float[] sourceVel = { 0.0f, 0.0f, 0.1f };
	static float[] listenerPos = { 0.0f, 0.0f, 0.0f };
	static float[] listenerVel = { 0.0f, 0.0f, 0.0f };
	static float[] listenerOri = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };
	static AL al = ALFactory.getAL();
	static ALC alc;

	static int loadALData(String filename) throws ALException{
		
        int[] format = new int[1];
        int[] size = new int[1];
        ByteBuffer[] data = new ByteBuffer[1];
        int[] freq = new int[1];
        int[] loop = new int[1];

        // Load wav data into a buffer.
        
        al.alGenBuffers(1, buffer, 0);
        if (al.alGetError() != ALConstants.AL_NO_ERROR)
            return ALConstants.AL_FALSE;

        ALut.alutLoadWAVFile(
        	filename,
            format,
            data,
            size,
            freq,
            loop);
        al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

		al.alGenSources(1, source, 0);
		al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
		al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
		al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
		al.alSourcefv(source[0], ALConstants.AL_POSITION, sourcePos, 0);
		al.alSourcefv(source[0], ALConstants.AL_POSITION, sourceVel, 0);
		al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);

		if (al.alGetError() != ALConstants.AL_NO_ERROR) {
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
		al.alDeleteBuffers(1, buffer, 0);
		al.alDeleteSources(1, source, 0);
	}

	public static void main(String[] args) {
        ALut.alutInit();
        al.alGetError();

        // Load the wav data.
        if (loadALData("resources/sounds/Riff.wav") == ALConstants.AL_FALSE)
            System.exit(1);
 
		setListenerValues();
		al.alSourcePlay(source[0]);
		long startTime = System.currentTimeMillis();
		long elapsed = 0;
		long ticker = 0;
		long lastTime = 0;
		while (elapsed < 1000000) {
			elapsed = System.currentTimeMillis() - startTime;            
			if (ticker > 100) {
				ticker = 0;
				sourcePos[0] += sourceVel[0];
				sourcePos[1] += sourceVel[1];
				sourcePos[2] += sourceVel[2];
				al.alSourcefv(source[0],
						ALConstants.AL_POSITION,
						sourcePos, 0);
			}
			ticker += System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis(); 
		}
		System.exit(0);
	}
}
