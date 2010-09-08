/* 
 * ClienteNIO.java
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
