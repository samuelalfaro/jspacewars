/* 
 * OggInputStream.java
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;

import com.jcraft.jogg.*;
import com.jcraft.jorbis.*;

/**
 * 
 * Decompresses an Ogg file.
 * <p>
 * How to use:<br>
 * 1. Create OggInputStream passing in the input stream of the packed ogg file<br>
 * 2. Fetch format and sampling rate using getFormat() and getRate(). Use it to
 *    initalize the sound player.<br>
 * 3. Read the PCM data using one of the read functions, and feed it to your player.
 * <p>
 * OggInputStream provides a read(ByteBuffer, int, int) that can be used to read
 * data directly into a native buffer.
 */
public class OggInputStream extends FilterInputStream {
	public static final String INVALID_OGG_MESSAGE="Input does not appear to be an Ogg bitstream.";
	private static final boolean BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
	
	/** The mono 16 bit format */
	public static final int FORMAT_MONO16 = 1;

	/** The stereo 16 bit format */
	public static final int FORMAT_STEREO16 = 2;

	// temp vars
	private float[][][] _pcm = new float[1][][];
	private int[] _index;

	// end of stream
	private boolean eos = false;

	// sync and verify incoming physical bitstream
	private SyncState syncState = new SyncState(); 

	// take physical pages, weld into a logical stream of packets
	private StreamState streamState = new StreamState(); 

	// one Ogg bitstream page.  Vorbis packets are inside
	private Page page = new Page(); 

	// one raw packet of data for decode
	private Packet packet = new Packet(); 

	// struct that stores all the static vorbis bitstream settings
	private Info info = new Info(); 

	// struct that stores all the bitstream user comments
	private Comment comment = new Comment(); 

	// central working state for the packet->PCM decoder
	private DspState dspState = new DspState(); 

	// local working space for packet->PCM decode
	private Block block = new Block(dspState); 

	// input buffer size
	private static int bufsize = 4096 * 2;

	/// Conversion buffer size
	private static int convsize = bufsize * 2;

	// Conversion buffer
	private static byte[] convbuffer = new byte[convsize];

	// where we are in the convbuffer
	private int convbufferOff = 0;

	// bytes ready in convbuffer.
	private int convbufferSize = 0;

	// a dummy used by read() to read 1 byte.
	private byte readDummy[] = new byte[1];

	/**
	 * Creates an OggInputStream that decompressed the specified ogg file.
	 */
	public OggInputStream(InputStream input) throws IOException{
		super(input);
		try {
			initVorbis();
			_index = new int[info.channels];
		} catch (IOException e) {
			eos = true;
			throw e;
		}
	}

	public AudioFormat getAudioFormat(){
		return new AudioFormat(getRate(), 16, getAudioChannels(), true, OggInputStream.BIG_ENDIAN );
	}
	
	/**
	 * Gets the format of the ogg file. Is either FORMAT_MONO16 or FORMAT_STEREO16
	 */
	public int getFormat() {
		if (info.channels == 1) {
			return FORMAT_MONO16;
		} 
		return FORMAT_STEREO16;        
	}

	/**
	 * Gets the rate of the pcm audio.
	 */
	public int getRate() {
		return info.rate;
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	public int read() throws IOException {
		int retVal = read(readDummy, 0, 1);
		return (retVal == -1 ? -1 : readDummy[0]);
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	public int read(byte b[], int off, int len) throws IOException {
		if (eos) {
			return -1;
		}
		int bytesRead = 0;
		while (!eos && (len > 0)) {
			fillConvbuffer();
			if (!eos) {
				int bytesToCopy = Math.min(len, convbufferSize-convbufferOff);
				System.arraycopy(convbuffer, convbufferOff, b, off, bytesToCopy);
				convbufferOff += bytesToCopy;
				bytesRead += bytesToCopy;
				len -= bytesToCopy;
				off += bytesToCopy;
			}
		}
		return bytesRead;
	}

	/**
	 * Reads up to len bytes of data from the input stream into a ByteBuffer.
	 * @param b the buffer into which the data is read.
	 * @param off the start offset of the data.
	 * @param len the maximum number of bytes read.
	 * @return the total number of bytes read into the buffer, or -1 if there is
	 *         no more data because the end of the stream has been reached. 
	 */
	public int read(ByteBuffer b, int off, int len) throws IOException {
		if (eos) {
			return -1;
		}
		b.position(off);
		int bytesRead = 0;
		while (!eos && (len > 0)) {
			fillConvbuffer();
			if (!eos) {
				int bytesToCopy = Math.min(len, convbufferSize-convbufferOff);
				b.put(convbuffer, convbufferOff, bytesToCopy);
				convbufferOff += bytesToCopy;
				bytesRead += bytesToCopy;
				len -= bytesToCopy;
			}
		}
		return bytesRead;
	}

	/**
	 * Helper function. Decodes a packet to the convbuffer if it is empty. 
	 * Updates convbufferSize, convbufferOff, and eos.
	 */
	private void fillConvbuffer() throws IOException {
		if (convbufferOff >= convbufferSize) {
			convbufferSize = lazyDecodePacket();
			convbufferOff = 0;
			if (convbufferSize == -1) {
				eos = true;
			}
		}
	}

	/**
	 * Returns 0 after EOF is reached, otherwise always return 1.
	 * <p>
	 * Programs should not count on this method to return the actual number of
	 * bytes that could be read without blocking.
	 * @return 1 before EOF and 0 after EOF is reached. 
	 */
	public int available() throws IOException {
		return (eos ? 0 : 1);
	}

	/**
	 * OggInputStream does not support mark and reset. This function does nothing.
	 */
	public void reset() throws IOException {
	}

	/**
	 * OggInputStream does not support mark and reset.
	 * @return false.
	 */
	public boolean markSupported() {
		return false;
	}

	/**
	 * Skips over and discards n bytes of data from the input stream. The skip
	 * method may, for a variety of reasons, end up skipping over some smaller
	 * number of bytes, possibly 0. The actual number of bytes skipped is returned. 
	 * @param n the number of bytes to be skipped. 
	 * @return the actual number of bytes skipped.
	 */
	public long skip(long n) throws IOException {
		int bytesRead = 0;
		while (bytesRead < n) {
			//int res = 
			read();
			if (read() == -1) {
				break;
			}
			bytesRead++;
		}
		return bytesRead;
	}

	/**
	 * Initalizes the vorbis stream. Reads the stream until info and comment are read.
	 */
	private void initVorbis() throws IOException {
		// Now we can read pages
		syncState.init(); 

		// grab some data at the head of the stream.  We want the first page
		// (which is guaranteed to be small and only contain the Vorbis
		// stream initial header) We need the first page to get the stream
		// serialno.

		// submit a 4k block to libvorbis' Ogg layer
		int index = syncState.buffer(bufsize);
		byte buffer[] = syncState.data;
		int bytes = in.read(buffer, index, bufsize);
		syncState.wrote(bytes);

		// Get the first page.
		if (syncState.pageout(page) != 1) {
			// have we simply run out of data?  If so, we're done.
			if (bytes < bufsize)
				return;//break;
			// error case.  Must not be Vorbis data
			throw new IOException(INVALID_OGG_MESSAGE);
		}
		// Get the serial number and set up the rest of decode.
		// serialno first; use it to set up a logical stream
		streamState.init(page.serialno());

		// extract the initial header from the first page and verify that the
		// Ogg bitstream is in fact Vorbis data

		// I handle the initial header first instead of just having the code
		// read all three Vorbis headers at once because reading the initial
		// header is an easy way to identify a Vorbis bitstream and it's
		// useful to see that functionality seperated out.

		info.init();
		comment.init();
		if (streamState.pagein(page) < 0) {
			// error; stream version mismatch perhaps
			throw new IOException("Error reading first page of Ogg bitstream data.");
		}
		if (streamState.packetout(packet) != 1) {
			// no page? must not be vorbis
			throw new IOException("Error reading initial header packet.");
		}
		if (info.synthesis_headerin(comment, packet) < 0) {
			// error case; not a vorbis header
			throw new IOException("This Ogg bitstream does not contain Vorbis audio data.");
		}
		// At this point, we're sure we're Vorbis.  We've set up the logical
		// (Ogg) bitstream decoder.  Get the comment and codebook headers and
		// set up the Vorbis decoder

		// The next two packets in order are the comment and codebook headers.
		// They're likely large and may span multiple pages.  Thus we read
		// and submit data until we get our two packets, watching that no
		// pages are missing.  If a page is missing, error out; losing a
		// header page is the only place where missing data is fatal. 
		int i = 0;
		while (i < 2) {
			while (i < 2) {
				int result = syncState.pageout(page);
				if (result == 0)
					break; // Need more data
					// Don't complain about missing or corrupt data yet.  We'll
				// catch it at the packet output phase
				if (result == 1) {
					streamState.pagein(page); // we can ignore any errors here
					// as they'll also become apparent
					// at packetout
					while (i < 2) {
						result = streamState.packetout(packet);
						if (result == 0) {
							break;
						}
						if (result == -1) {
							// Uh oh; data at some point was corrupted or missing!
							// We can't tolerate that in a header.  Die.
							throw new IOException("Corrupt secondary header. Exiting.");
						}
						info.synthesis_headerin(comment, packet);
						i++;
					}
				}
			}
			// no harm in not checking before adding more
			index = syncState.buffer(bufsize);
			buffer = syncState.data;
			bytes = in.read(buffer, index, bufsize);
			// NOTE: This is a bugfix. read will return -1 which will mess up syncState.
			if (bytes < 0 ) {
				bytes = 0;
			}
			if (bytes == 0 && i < 2) {
				throw new IOException("End of file before finding all Vorbis headers!");
			}
			syncState.wrote(bytes);
		}
		convsize = bufsize / info.channels;
		// OK, got and parsed all three headers. Initialize the Vorbis
		//  packet->PCM decoder.
		dspState.synthesis_init(info); // central decode state
		block.init(dspState); // local state for most of the decode
		// so multiple block decodes can
		// proceed in parallel.  We could init
		// multiple vorbis_block structures
		// for vd here
	}

	/**
	 * Decodes a packet.
	 */
	private int decodePacket(Packet packet) {
		// check the endianes of the computer.
		if (block.synthesis(packet) == 0) { 
			// test for success!
			dspState.synthesis_blockin(block);
		}

		// **pcm is a multichannel float vector.  In stereo, for
		// example, pcm[0] is left, and pcm[1] is right.  samples is
		// the size of each channel.  Convert the float values
		// (-1.<=range<=1.) to whatever PCM format and write it out
		
		int ptr=0;
		int samples;
		switch(info.channels){
		case 1:
			while((samples = dspState.synthesis_pcmout(_pcm, _index)) > 0){
				int bout= samples < convsize ? samples: convsize;
				float[] pcm=_pcm[0][0];
				int j = _index[0];
				// convert doubles to 16 bit signed ints (host order)
				for(int i=0; i<bout; i++){
					int val=(int)(pcm[j++]*32767.0f);
					if(val>32767)
						val = 32767;
					else if(val<-32768)
						val = -32768;
					if(val<0)
						val |= 0x8000;
					if(OggInputStream.BIG_ENDIAN){
						convbuffer[ptr++]=(byte)(val>>>8);
						convbuffer[ptr++]=(byte)(val);
					}else{
						convbuffer[ptr++]=(byte)(val);
						convbuffer[ptr++]=(byte)(val>>>8);
					}
				}
				dspState.synthesis_read(bout);
			}
		case 2:
			while((samples = dspState.synthesis_pcmout(_pcm, _index)) > 0){
				int bout= samples < convsize ? samples: convsize;
				float[] pcm1=_pcm[0][0];
				float[] pcm2=_pcm[0][1];
				int j1= _index[0], j2 = _index[1];
				for(int i=0; i<bout; i++){
					int val=(int)(pcm1[j1++]*32767.0f);
					if(val>32767)
						val = 32767;
					else if(val<-32768)
						val = -32768;
					if(val<0)
						val |= 0x8000;
					if(OggInputStream.BIG_ENDIAN){
						convbuffer[ptr++]=(byte)(val>>>8);
						convbuffer[ptr++]=(byte)(val);
					}else{
						convbuffer[ptr++]=(byte)(val);
						convbuffer[ptr++]=(byte)(val>>>8);
					}
					
					val=(int)(pcm2[j2++]*32767.0f);
					if(val>32767)
						val = 32767;
					else if(val<-32768)
						val = -32768;
					if(val<0)
						val |= 0x8000;
					if(OggInputStream.BIG_ENDIAN){
						convbuffer[ptr++]=(byte)(val>>>8);
						convbuffer[ptr++]=(byte)(val);
					}else{
						convbuffer[ptr++]=(byte)(val);
						convbuffer[ptr++]=(byte)(val>>>8);
					}
				}
				dspState.synthesis_read(bout);
			}
		default:
			while((samples = dspState.synthesis_pcmout(_pcm, _index)) > 0){
				int bout= samples < convsize ? samples: convsize;
				float[][] pcm=_pcm[0];
				// convert doubles to 16 bit signed ints (host order)
				for(int i=0; i<bout; i++){
					for(int j=0; j < info.channels; j++){
						int val=(int)(pcm[j][_index[j]++]*32767.0f);
						if(val>32767)
							val = 32767;
						else if(val<-32768)
							val = -32768;
						if(val<0)
							val |= 0x8000;
						if(OggInputStream.BIG_ENDIAN){
							convbuffer[ptr++]=(byte)(val>>>8);
							convbuffer[ptr++]=(byte)(val);
						}else{
							convbuffer[ptr++]=(byte)(val);
							convbuffer[ptr++]=(byte)(val>>>8);
						}
					}
				}
				dspState.synthesis_read(bout);
			}
		}
		return ptr;
	}

	/**
	 * Decodes the next packet.
	 * @return bytes read into convbuffer of -1 if end of file
	 */
	private int lazyDecodePacket() throws IOException {
		int result = getNextPacket(packet);
		if (result == -1) {
			return -1;
		}
		// we have a packet.  Decode it
		return decodePacket(packet);
	}

	/**
	 * @param packet where to put the packet.
	 */
	private int getNextPacket(Packet packet) throws IOException {
		// get next packet.
		boolean fetchedPacket = false;
		while (!eos && !fetchedPacket) {
			int result1 = streamState.packetout(packet);
			if (result1 == 0) {
				// no more packets in page. Fetch new page.
				int result2 = syncState.pageout(page);
				// return if we have reaced end of file.
				if ((result2 == 0) && (page.eos() != 0)) {
					return -1;
				}
				if (result2 == 0) {
					// need more data fetching page..
					fetchData();
				} else if (result2 == -1) {
					//throw new Exception("syncState.pageout(page) result == -1");
					return -1;
				} else {
					//int result3 = 
					streamState.pagein(page);
				}
			} else if (result1 == -1) {
				//throw new Exception("streamState.packetout(packet) result == -1");
				return -1;
			} else {
				fetchedPacket = true;
			}
		}
		return 0;
	}

	/**
	 * Copys data from input stream to syncState.
	 */
	private void fetchData() throws IOException {
		if (!eos) {
			// copy (bufsize) bytes from compressed stream to syncState.
			int index = syncState.buffer(bufsize);
			int bytes = in.read(syncState.data, index, bufsize);
			syncState.wrote(bytes); 
			if (bytes == 0) {
				eos = true;
			}
		}
	}

	/**
	 * Gets information on the ogg.
	 */
	public String toString() {
		String s = "";
		s = s + "version         " + info.version         + "\n";
		s = s + "channels        " + info.channels        + "\n";
		s = s + "rate (hz)       " + info.rate            ;
		return s;
	}

	public Info getInfo() {
		return info;
	}

	public int rate(){
		return info.rate;
	}

	public int getAudioChannels(){
		return info.channels;
	}
}
