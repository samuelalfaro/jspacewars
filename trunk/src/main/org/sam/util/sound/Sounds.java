/* 
 * Sounds.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.util.sound;

import java.io.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;

/**
 * 
 *
 */
public class Sounds{
	private static final long serialVersionUID = 1L;

	public static void playMidi(String fileName){
		try{
			//MidiFileFormat mff2=MidiSystem.getMidiFileFormat(f2);
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(MidiSystem.getSequence(new File(fileName)));
			sequencer.setLoopCount(Clip.LOOP_CONTINUOUSLY);
			sequencer.start();
		}catch(MidiUnavailableException e){
			e.printStackTrace();
		}catch(InvalidMidiDataException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	static void play(final InputStream data_input_stream, AudioFormat output_format) throws LineUnavailableException{
	//		final SourceDataLine output_line = AudioSystem.getSourceDataLine(output_format);
			final SourceDataLine output_line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, output_format));
			output_line.open();
	
			// Reproduccion
	//		new Thread(){
	//			public void run(){
					output_line.start();
					int tBuff = 4<<10; 
					byte[] output_buffer = new byte[ tBuff ];
					try {
						while( data_input_stream.available() > 0 ) {
							int read = data_input_stream.read(output_buffer, 0, tBuff);
							output_line.write( output_buffer, 0, read );
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					output_line.drain();
					output_line.close();
	//			}
	//		}.start();
		}

	public static void playWav(String fileName){
		try{
			File sf = new File(fileName);
			play(AudioSystem.getAudioInputStream(sf), AudioSystem.getAudioFileFormat(sf).getFormat());
			/*int bufferSize = (int)( ais.getFrameLength() * output_format.getFrameSize());
			DataLine.Info info = new DataLine.Info( Clip.class, ais.getFormat(), bufferSize);
			Clip ol = (Clip)AudioSystem.getLine(info);
			ol.open(ais);
			ol.loop(Clip.LOOP_CONTINUOUSLY);
			System.out.println("reprodución empezada, apretar CTRL-C para interrumpir");
			*/
		}catch(UnsupportedAudioFileException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(LineUnavailableException e){
			e.printStackTrace();
		}
	}
	
	public static void playTracker( String filename ){
		try {
			TrackerInputStream data_input_stream = new TrackerInputStream( new FileInputStream( filename ));
			play(data_input_stream, data_input_stream.getAudioFormat());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void playVorbisOgg( String filename ){
		try {
			OggInputStream data_input_stream = new OggInputStream( new FileInputStream( filename ));
			play(data_input_stream, data_input_stream.getAudioFormat());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void play(String filename){
		String filenameUpperCase = filename.toUpperCase();
		if(filenameUpperCase.endsWith(".MID") || filenameUpperCase.endsWith(".MIDI") )
			playMidi(filename);
		else if ( filenameUpperCase.endsWith(".WAV") )
			playWav(filename);
		else if ( filenameUpperCase.endsWith(".OGG") )
			playVorbisOgg(filename);
		else if ( filenameUpperCase.endsWith(".MOD") || filenameUpperCase.endsWith(".S3M") || filenameUpperCase.endsWith(".XM"))
			playTracker(filename);
	}
	
	public static void main(String arrgs[]){
//		play("resources/sounds/midis/AC-DC_-_Thunderstruck.mid");
//		play("resources/sounds/Riff.wav");
//		play("resources/sounds/delinquentes.ogg");
		play("resources/sounds/nstalgia.s3m");
		play("resources/sounds/snow.xm");
		play("resources/sounds/side_effects.mod");
	}
}
