package org.sam.red;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Esta clase encapsula la comunicacion de un servidor mediante datagramas UDP.
 */
public class BufferSocket extends BufferCanal {
	
	private DatagramSocket dgSocket;
	private DatagramPacket pack;

	/**
	 * Crea un canal de comunicacion UDP que atenderá peticiones a través de un determinado puerto.
	 * 
	 * @param port Puerto por el que se atenderán las comunicaciones.
	 * 
	 * @throws SocketException No se ha podido crear el DatagramSocket.
	 */
	public BufferSocket(int port) throws SocketException{
	
		dgSocket = new DatagramSocket(port);
		byte[] datos = new byte[TAM_DATAGRAMA];
		
		pack = new DatagramPacket(datos, TAM_DATAGRAMA);
		buff = ByteBuffer.wrap(datos);
	}
	
	/**
	 * Crea un canal de comunicacion UDP que har� peticiones a una direccion de destino a través de un determinado 
	 * puerto.
	 * 
	 * @param destino Direccion donde establecer� la comunicaci�n.
	 * @param port Puerto por el que establecer� la comunicaci�n.
	 * 
	 * @throws SocketException No se ha podido crear el DatagramSocket.
	 * @throws UnknownHostException La direccion de destino no se conoce.
	 */
	public BufferSocket(String destino, int port) throws SocketException, UnknownHostException{
	
		dgSocket = new DatagramSocket();
		byte[] datos = new byte[TAM_DATAGRAMA];
		
		pack = new DatagramPacket(datos, TAM_DATAGRAMA);
		pack.setAddress(InetAddress.getByName(destino));
		pack.setPort(port);
		buff = ByteBuffer.wrap(datos);
	}
	
    /* (non-Javadoc)
     * @see org.sam.red.BufferCanal#enviar()
     */
	public void enviar() {
		try{
			pack.setData(buff.array(), buff.arrayOffset(), TAM_DATAGRAMA);
			dgSocket.send(pack);
			buff.rewind();
		}catch(IOException e){
		}
	}

    /* (non-Javadoc)
     * @see org.sam.red.BufferCanal#recibir()
     */
	public void recibir(){
		try{
			dgSocket.receive(pack);
		}catch(IOException e){
		}
	}
}
