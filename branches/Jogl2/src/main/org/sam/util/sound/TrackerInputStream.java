/* 
 * TrackerInputStream.java
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

import ibxm.*;

import java.io.*;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

public class TrackerInputStream extends FilterInputStream {
	
	public static final String INVALID_TRACKER_MESSAGE="La entrada no contiene un formato reconocido.";
	
	int song_duration;
	IBXM ibxm;
	
	private final boolean bigEndian;
	
	public TrackerInputStream(InputStream in) throws IOException {
		this(in, true);
	}
	
	public TrackerInputStream(InputStream in, boolean bigEndian) throws IOException {
		super((in instanceof DataInputStream) ? in: new DataInputStream(in));
		this.bigEndian = bigEndian;
		InitTracker();
	}
	
	private void InitTracker() throws IOException{
		DataInputStream dis = (DataInputStream)in;
		Module module = null;
		byte[] xm_header, s3m_header, mod_header;

		// Se lee la cabecera para determinar el archivo y se crea el modulo
		xm_header = new byte[ 60 ];
		dis.readFully( xm_header );
		if( FastTracker2.is_xm( xm_header ) ) {
			module = FastTracker2.load_xm( xm_header, dis );
		} else {
			s3m_header = new byte[ 96 ];
			System.arraycopy( xm_header, 0, s3m_header, 0, 60 );
			dis.readFully( s3m_header, 60, 36 );
			if( ScreamTracker3.is_s3m( s3m_header ) ) {
				module = ScreamTracker3.load_s3m( s3m_header, dis );
			} else {
				mod_header = new byte[ 1084 ];
				System.arraycopy( s3m_header, 0, mod_header, 0, 96 );
				dis.readFully( mod_header, 96, 988 );
				if(ProTracker.is_mod(mod_header))
					module = ProTracker.load_mod( mod_header, dis );
			}
		}
		dis.close();
		// Si el archivo no se ha reconocido lanza una exception.
		if(module == null)
			throw new IOException(INVALID_TRACKER_MESSAGE);

		// Se carga el modulo
		ibxm = new IBXM( 48000 );
		ibxm.set_module( module );
		song_duration = ibxm.calculate_song_duration();
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	public int read() throws IOException {
//		int retVal = read(readDummy, 0, 1);
//		return (retVal == -1 ? -1 : readDummy[0]);
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	public int read(byte b[], int off, int len) throws IOException {
		int frames = len>>2;
		if(frames > song_duration)
			frames = song_duration;
		ibxm.get_audio( b, frames );
		int size = frames<<2;
		if(!bigEndian){
			for(int i= 0; i < size ; i+=2 ){
				b[i] ^= b[i+1];
				b[i+1] ^= b[i];
				b[i] ^= b[i+1];
			}
		}
		song_duration -= frames;
		return size;
	}

	/**
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public int read(ByteBuffer b, int off, int len) throws IOException {
		return 0;
	}
	
	
	public int available() throws IOException {
		//return (eos ? 0 : 1);
		return song_duration<<2;
	}

	/**
	 * TrackerInputStream does not support mark and reset. This function does nothing.
	 */
	public void reset() throws IOException {
	}

	/**
	 * TrackerInputStream does not support mark and reset.
	 * @return false.
	 */
	public boolean markSupported() {
		return false;
	}
	
	public AudioFormat getAudioFormat(){
		return new AudioFormat ( 48000, 16, 2, true, bigEndian );
	}
}
