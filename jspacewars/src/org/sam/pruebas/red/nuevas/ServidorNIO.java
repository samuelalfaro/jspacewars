package org.sam.pruebas.red.nuevas;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;

public class ServidorNIO {
	private static final int TAM_DATAGRAMA = 8192;
	private static final int PUERTO=9000;

	public static void main(String args[]) throws IOException{
		final DatagramChannel canalServer = DatagramChannel.open();
		canalServer.configureBlocking(false);
		canalServer.socket().bind(new InetSocketAddress(PUERTO));
		
		final Pipe pipeServer = Pipe.open();
		final Pipe pipeClient = Pipe.open();
		//Pipe.SinkChannel pipeOut = pipe.sink();
		Pipe.SourceChannel pipeIn = pipeServer.source();
		Pipe.SinkChannel pipeOut = pipeClient.sink();
		pipeIn.configureBlocking(false);
		
		Selector selector = Selector.open();
		canalServer.register(selector, SelectionKey.OP_READ);
		pipeIn.register(selector, SelectionKey.OP_READ);
		System.out.println("Escuchando");
		ByteBuffer buffIn = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		ByteBuffer buffOut = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
//		byte byteArray[] = new byte[TAM_DATAGRAMA];
		
		new Thread(){
			public void run(){
				Pipe.SourceChannel pipeIn = pipeClient.source();
				Pipe.SinkChannel pipeOut = pipeServer.sink();
				ByteBuffer buff = ByteBuffer.allocateDirect(100);
				for(int i = 0; i < 10000; i ++){
					buff.clear();
					buff.putInt(i);
					buff.flip();
					try{
						pipeOut.write(buff);
						
						buff.clear();
						pipeIn.read(buff);
						buff.flip();
						WritableByteChannel salida = Channels.newChannel(System.out);
						salida.write(buff);
						
					}catch( IOException e ){
						e.printStackTrace();
					}
					try{
						Thread.sleep(5);
					}catch( InterruptedException e ){
						e.printStackTrace();
					}
				}
			}
		}.start();

		WritableByteChannel salida = Channels.newChannel(System.out);
		while (true) {
			selector.select();
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext()){
				SelectionKey key = it.next();
//				System.out.println(++i + " " + key);
//				System.out.println("\t isAcceptable:\t" + key.isAcceptable());
//				System.out.println("\t isConnectable:\t" + key.isConnectable());
//				System.out.println("\t isReadable:\t" + key.isReadable());
//				System.out.println("\t isValid:\t" + key.isValid());
//				System.out.println("\t isWritable:\t" + key.isWritable());
				if(key.channel() == pipeIn){
					buffIn.clear();
					pipeIn.read(buffIn);
					buffIn.flip();
					int nPipe = buffIn.getInt();
					
					buffOut.clear();
					buffOut.put(("Repondiendo a pipe: "+nPipe+"\n").getBytes());
					buffOut.flip();
					pipeOut.write(buffOut);
				}
				else if( key.channel() == canalServer){
					buffIn.clear();
					SocketAddress sa = canalServer.receive(buffIn);
					if (sa != null) {
						buffIn.flip();
						salida.write(buffIn);

						buffOut.clear();
						String fecha = new Date().toString()+ System.getProperty("line.separator");
						buffOut.put(fecha.getBytes());
						buffOut.flip();
						canalServer.send(buffOut,sa);
					}
				}
				it.remove();
			}
		}
	}
}
