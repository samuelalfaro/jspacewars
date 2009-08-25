package pruebas.red.nuevas;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class ClienteNIO {
	
	private static final int TAM_DATAGRAMA = 8192;
	private static final int PUERTO=9000;
	private static final String MAQUINA="localhost";
	
	public static void main(String args[]) throws IOException{
		DatagramChannel canalCliente = DatagramChannel.open();
		canalCliente.connect(new InetSocketAddress(MAQUINA,PUERTO));
		ByteBuffer buff = ByteBuffer.allocateDirect(TAM_DATAGRAMA);
		
		buff.clear();
		buff.put("¿Que hora es capullo?\n".getBytes());
		buff.flip();
		canalCliente.write(buff);
		
		buff.clear();
		canalCliente.read(buff);
		buff.flip();
		WritableByteChannel salida = Channels.newChannel(System.out);
		salida.write(buff);
		
		salida.close();
		canalCliente.socket().close();
		canalCliente.close();
	}
}
