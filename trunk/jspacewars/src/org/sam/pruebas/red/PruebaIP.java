package org.sam.pruebas.red;

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
