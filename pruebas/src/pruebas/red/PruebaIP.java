/* 
 * PruebaIP.java
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
package pruebas.red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PruebaIP {

	public static void main(String[] args) {
		InetAddress direccion;
		String nombreOrdenador;
		InputStreamReader flujo = new InputStreamReader(System.in);
		BufferedReader teclado = new BufferedReader(flujo);
		try{
			System.out.println("Introduce el nombre del ordenador:");
			nombreOrdenador = teclado.readLine();
			direccion = InetAddress.getByName(nombreOrdenador);
			System.out.println("Direccion IP: "+direccion.getHostAddress());
			System.out.println("Nombre: "+direccion.getHostName());
		}catch(UnknownHostException e){
			System.err.println("Nombre del ordenador desconocido");
		}catch (IOException e){
			System.err.println("No se ha podido leer el nombre");
		}
	}
}
