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

import ibxm.IBXM;
import ibxm.Module;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;

public class TrackerInputStream extends FilterInputStream {
	
	private static final int SAMPLE_RATE = 48000;
	private static final boolean BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

	
	private final IBXM   ibxm;
	private final int[]  mixBuf;
	private final byte[] outBuf;
	
	private int outIdx, outLen, remain;
	
	public TrackerInputStream( InputStream in ) throws IOException{
		super(in);
		byte[] buf = new byte[ in.available() ];
		int idx = 0;
		while( idx < buf.length ) {
			int len =  in.read( buf, idx, buf.length - idx );
			if( len < 0 )
				throw new java.io.IOException( "Unexpected end of file." );
			idx += len;
		}
		in.close();
		ibxm = new IBXM( new Module( buf ), SAMPLE_RATE );
		mixBuf = new int[ ibxm.getMixBufferLength() ];
		outBuf = new byte[ mixBuf.length * 4 ];
		
		remain = ibxm.calculateSongDuration() * 4;
	}
	
	@Override
	public int read() {
		int out = -1;
		if( remain > 0 ) {
			out = outBuf[ outIdx++ ];
			if( outIdx >= outLen ) {
				getAudio();
			}
			remain--;
		}
		return out;
	}

	@Override
	public int read( byte[] buf, int off, int len ) {
		int bytesRead = 0;
		while( remain > 0 && len > 0 ){
			if( outIdx >= outLen ) {
				getAudio();
			}
			int bytesToCopy = Math.min( len, outLen - outIdx );
			System.arraycopy( outBuf, outIdx, buf, off, bytesToCopy );
			outIdx += bytesToCopy;
			remain -= bytesToCopy;
			len -= bytesToCopy;
			off += bytesToCopy;
			bytesRead += bytesToCopy;
		}
		return bytesRead;
	}
	
	private void getAudio() {
		int mEnd = ibxm.getAudio( mixBuf ) * 2;
		for( int mIdx = 0, oIdx = 0; mIdx < mEnd; mIdx++ ) {
			int val = mixBuf[mIdx];
			if( val > 32767 )
				val = 32767;
			else if( val < -32768 )
				val = -32768;
			if( val < 0 )
				val |= 0x8000;
			if( BIG_ENDIAN ){
				outBuf[oIdx++] = (byte)( val >> 8 );
				outBuf[oIdx++] = (byte)  val;
			}else{
				outBuf[oIdx++] = (byte)  val;
				outBuf[oIdx++] = (byte)( val >> 8 );
			}
		}
		outIdx = 0;
		outLen = mEnd * 2;
	}
	
	@Override
	public int available() throws IOException {
		return remain;
	}

	/**
	 * TrackerInputStream does not support mark and reset. This function does nothing.
	 */
	@Override
	public void reset() throws IOException {
	}

	/**
	 * TrackerInputStream does not support mark and reset.
	 * @return false.
	 */
	@Override
	public boolean markSupported() {
		return false;
	}
	
	public AudioFormat getAudioFormat(){
		return new AudioFormat ( SAMPLE_RATE, 16, 2, true, BIG_ENDIAN );
	}
}
