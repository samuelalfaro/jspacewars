package tips.joal;

/**
* Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* -Redistribution of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
*
* -Redistribution in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Sun Microsystems, Inc. or the names of contributors may 
* be used to endorse or promote products derived from this software without 
* specific prior written permission.
* 
* This software is provided "AS IS," without a warranty of any kind.
* ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
* ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
* NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS
* LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
* RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
* IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT
* OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
* PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
* ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
* BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
*
* You acknowledge that this software is not designed or intended for use in the
* design, construction, operation or maintenance of any nuclear facility.
*
* Created on Jun 24, 2003
* Adapted from DevMaster SingleStaticSource Tutorial by Jesse Maurais
*/

import net.java.games.joal.*;
import net.java.games.joal.util.*;
import java.io.*;
import java.nio.ByteBuffer;

public class SingleStaticSource {

    static AL al = ALFactory.getAL();

    // Buffers hold sound data.
    static int[] buffer = new int[1];

    // Sources are points emitting sound.
    static int[] source = new int[1];

    static int loadALData(String filename) {

        // variables to load into

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
//        ALut.alutUnloadWAV(format[0], data[0], size[0], freq[0]);

        // Bind buffer with a source.
        al.alGenSources(1, source, 0);

        if (al.alGetError() != ALConstants.AL_NO_ERROR)
            return ALConstants.AL_FALSE;

        al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
        al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
        al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
        al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
        al.alSourcefv(source[0], ALConstants.AL_POSITION, new float[] { 0.0f, 0.0f, 0.0f }, 0);
        al.alSourcefv(source[0], ALConstants.AL_VELOCITY, new float[] { 0.0f, 0.0f, 0.0f }, 0);
        

        // Do another error check and return.
        if (al.alGetError() == ALConstants.AL_NO_ERROR)
            return ALConstants.AL_TRUE;

        return ALConstants.AL_FALSE;
    }
    
    static void killAllData() {
        al.alDeleteBuffers(1, buffer, 0);
        al.alDeleteSources(1, source, 0);
        ALut.alutExit();
    }

    static void setListenerValues() {
        al.alListenerfv(ALConstants.AL_POSITION, new float[]{ 0.0f, 0.0f, 0.0f }, 0);
        al.alListenerfv(ALConstants.AL_VELOCITY,  new float[]{ 0.0f, 0.0f, 0.0f }, 0);
        al.alListenerfv(ALConstants.AL_ORIENTATION, new float[]{ 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f }, 0);
    }

    public static void main(String[] args) {
        // Initialize OpenAL and clear the error bit.

        ALut.alutInit();
        al.alGetError();

        // Load the wav data.
        if (loadALData("Riff.wav") == ALConstants.AL_FALSE)
            System.exit(1);

        setListenerValues();

        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        char[] c = new char[1];
        while (c[0] != 'q') {
            try {
               
                System.out.println(
                    "Press a key and hit ENTER: \n"
                        + "'p' to play, 's' to stop, " +
                          "'h' to pause and 'q' to quit");
                buf.read(c);
                switch (c[0]) {
                    case 'p' :
                        // Pressing 'p' will begin playing the sample.
                        al.alSourcePlay(source[0]);
                        break;
                    case 's' :
                        // Pressing 's' will stop the sample from playing.
                        al.alSourceStop(source[0]);
                        break;
                    case 'h' :
                        // Pressing 'n' will pause (hold) the sample.
                        al.alSourcePause(source[0]);
                        break;
                    case 'q' :
                        killAllData();
                        break;
                }
            } catch (IOException e) {
                System.exit(1);
            }
        }
    }
}

