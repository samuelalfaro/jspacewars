package org.sam.tips;

/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/* JOrbisPlayer -- pure Java Ogg Vorbis player
 *
 * Copyright (C) 2000 ymnk, JCraft,Inc.
 *
 * Written by: 2000 ymnk<ymnk@jcraft.com>
 *
 * Many thanks to 
 *   Monty <monty@xiph.org> and 
 *   The XIPHOPHORUS Company http://www.xiph.org/ .
 * JOrbis has been based on their awesome works, Vorbis codec and
 * JOrbisPlayer depends on JOrbis.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

import java.io.FileInputStream;
import java.io.InputStream;

import javax.sound.sampled.*;

import com.jcraft.jogg.*;
import com.jcraft.jorbis.*;

public class JOrbisPlayer implements Runnable{

	static final int BUFSIZE=4096*2;
	private static int convsize=BUFSIZE*2;
	private static byte[] convbuffer = new byte[convsize];

	private Thread player=null;
	private InputStream bitStream=null;

	private SyncState syncState;
	private StreamState streamState;
	private Page page;
	private Packet packet;
	private Info info;
	private Comment comment;
	private DspState dspState;
	private Block block;

	private void init_jorbis(){
		syncState = new SyncState();
		streamState = new StreamState();
		page = new Page();
		packet = new Packet();

		info = new Info();
		comment = new Comment();
		dspState = new DspState();
		block = new Block(dspState);

		syncState.init();
	}

	private int rate=0;
	private int channels=0;
	private SourceDataLine outputLine=null;
	
	private void init_audio(int channels, int rate){
		try {
			AudioFormat audioFormat = new AudioFormat(
					rate, 
					16,
					channels,
					true,  // PCM_Signed
					false  // littleEndian
			);
			DataLine.Info info = new DataLine.Info(
					SourceDataLine.class,
					audioFormat, 
					AudioSystem.NOT_SPECIFIED
			);
			if (!AudioSystem.isLineSupported(info)) {
				return;
			}
			try{
				outputLine = (SourceDataLine) AudioSystem.getLine(info);
				outputLine.open(audioFormat);
			} catch (LineUnavailableException ex) { 
				System.out.println("Unable to open the sourceDataLine: " + ex);
				return;
			} catch (IllegalArgumentException ex) { 
				System.out.println("Illegal Argument: " + ex);
				return;
			}
			this.rate=rate;
			this.channels=channels;
		} catch(Exception ee){
			System.out.println(ee);
		}
	}

	private SourceDataLine getOutputLine(int channels, int rate){
		if(outputLine==null || this.rate!=rate || this.channels!=channels){
			if(outputLine!=null){
				outputLine.drain();
				outputLine.stop();
				outputLine.close();
			}
			init_audio(channels, rate);
			outputLine.start();
		}
		return outputLine;
	}
	
	private InputStream getStream(String fileName){
		try{
			InputStream is= new FileInputStream(System.getProperty("user.dir")+
					System.getProperty("file.separator")+fileName);
			System.out.println("Select: "+fileName);
			return is;
		}catch(Exception ee){ 
			System.err.println(ee);
			return null;
		}
	}
	
	private void play_stream(Thread me) {
		byte[] buffer = null;
		int bytes = 0;
		boolean chained = false;
		init_jorbis();
		loop:
			while(true){
				boolean eos= false;
				int index = syncState.buffer(BUFSIZE);
				buffer = syncState.data;
				try{
					bytes = bitStream.read(buffer, index, BUFSIZE);
				} catch(Exception e) {
					System.err.println(e);
					return;
				}
				syncState.wrote(bytes);

				if(chained){
					chained=false;   
				} else if(syncState.pageout(page)!=1){
					if(bytes<BUFSIZE)
						break;
					System.err.println("Input does not appear to be an Ogg bitstream.");
					return;
				}
				streamState.init(page.serialno());
				streamState.reset();

				info.init();
				comment.init();

				if(streamState.pagein(page)<0){ 
					// error; stream version mismatch perhaps
					System.err.println("Error reading first page of Ogg bitstream data.");
					return;
				}
				if(streamState.packetout(packet)!=1){ 
					// no page? must not be vorbis
					System.err.println("Error reading initial header packet.");
					break;
				}
				if(info.synthesis_headerin(comment, packet)<0){ 
					// error case; not a vorbis header
					System.err.println("This Ogg bitstream does not contain Vorbis audio data.");
					return;
				}

				{
					int i=0;
					while(i<2){
						while(i<2){
							int result=syncState.pageout(page);
							if(result==0)
								break; // Need more data
							if(result==1){
								streamState.pagein(page);
								while(i<2){
									result=streamState.packetout(packet);
									if(result==0)
										break;
									if(result==-1){
										System.err.println("Corrupt secondary header.  Exiting.");
										break loop;
									}
									info.synthesis_headerin(comment, packet);
									i++;
								}
							}
						}

						index = syncState.buffer(BUFSIZE);
						buffer = syncState.data; 
						try{ 
							bytes = bitStream.read(buffer, index, BUFSIZE);
						}catch(Exception e){
							System.err.println(e);
							return;
						}
						if(bytes==0 && i<2){
							System.err.println("End of file before finding all Vorbis headers!");
							return;
						}
						syncState.wrote(bytes);
					}
				}

				// Muestra los metadatos del archivo
				/*
				{
					byte[][] ptr=vc.user_comments;
					StringBuffer sb= new StringBuffer();

					for(int j=0; j<ptr.length;j++){
						if(ptr[j]==null) break;
						System.err.println("Comment: "+new String(ptr[j], 0, ptr[j].length-1));
						if(sb!=null)sb.append(" "+new String(ptr[j], 0, ptr[j].length-1));
					} 
					System.err.println("Bitstream is "+vi.channels+" channel, "+vi.rate+"Hz");
					System.err.println("Encoded by: "+new String(vc.vendor, 0, vc.vendor.length-1)+"\n");
				}
				 */
				
				//Aqui comienza la lectura y reproduccion
				convsize = BUFSIZE/info.channels;
				dspState.synthesis_init(info);
				block.init(dspState);
				getOutputLine(info.channels, info.rate);
				
				while(!eos) {
					while(!eos) {
						if(player!=me){
							try{
								bitStream.close();
							} catch(Exception ee){
							}
							return;
						}

						int result=syncState.pageout(page);
						if(result==0)
							break; // need more data
						if(result!=-1){
							streamState.pagein(page);
							if(page.granulepos()==0){
								chained = eos = true;
								break;
							}
							while(true){
								result=streamState.packetout(packet);
								if(result==0)
									break; // need more data
								if(result!=-1){ 
									// we have a packet.  Decode it
									if(block.synthesis(packet)==0){ // test for success!
										dspState.synthesis_blockin(block);
									}
									
									float[][][] _pcmf = new float[1][][];
									int[] _index=new int[info.channels];
									int samples = dspState.synthesis_pcmout(_pcmf, _index);
									int bout= samples < convsize ? samples: convsize;
									while(samples > 0){
										float[][] pcmf=_pcmf[0];
										// convert doubles to 16 bit signed ints (host order) and
										// interleave
										int ptr=0;
										for(int i=0; i<bout; i++){
											for(int j=0; j < info.channels; j++){
												int val=(int)(pcmf[j][_index[j]+i]*32767.0f);
												if(val>32767)
													val = 32767;
												else if(val<-32768)
													val = -32768;
												if(val<0)
													val |= 0x8000;
												convbuffer[ptr++]=(byte)(val);
												convbuffer[ptr++]=(byte)(val>>>8);
											}
										}
										outputLine.write(convbuffer, 0, ptr);
										dspState.synthesis_read(bout);
										samples = dspState.synthesis_pcmout(_pcmf, _index);
										if(samples < convsize)
											bout= samples;
									}	  
								}
							}
							if(page.eos()!=0)
								eos=true;
						}
					}

					if(!eos){
						index  = syncState.buffer(BUFSIZE);
						buffer = syncState.data;
						try{ 
							bytes = bitStream.read(buffer,index,BUFSIZE);
						}catch(Exception e){
							System.err.println(e);
							return;
						}
						if(bytes==-1){
							break;
						}
						syncState.wrote(bytes);
						if(bytes==0)
							eos = true;
					}
				}
				streamState.clear();
				block.clear();
				dspState.clear();
				info.clear();
			}
		syncState.clear();
		try {
			if(bitStream!=null)
				bitStream.close();
		} catch(Exception e) { 
		}
	}

	public void run() {
		Thread me = Thread.currentThread();
		while(true){
			bitStream=getStream(item);
			if(bitStream!=null){
				play_stream(me);
			}
			if(player!=me){
				break;
			}
			bitStream=null;
		}
		player=null; 
	}

	public void play_sound(){
		if(player!=null)
			return;
		player=new Thread(this);
		player.start();
	}

	public void stop_sound(){
		if(player==null)
			return;
		player=null;
	}

	public void stop(){
		if(player==null){
			try{
				outputLine.drain();
				outputLine.stop();
				outputLine.close();
				if(bitStream!=null)bitStream.close();
			}
			catch(Exception e){}
		}
		player=null;
	}
	
	final String item="delinquentes.ogg";
	
	public static void main(String[] arg){
		JOrbisPlayer player= new JOrbisPlayer();
		player.play_sound();
	}
}

