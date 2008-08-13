package org.sam.tips;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class PruebaPhp {

	public static void main(String[] arg){

		InputStream i = null;
		try{
			URL myURL= new URL("http://www.eupla.org/index.php?dcha=foro/buscador.php&pagina=&orden=comment_id&direccion=asc&btnBuscar=Buscar&cboForo=todos&chkResaltar=&chkTxtContenido=&chkTxtTitulo=&chkTxtUsuario=checked&txtContenido=&txtTitulo=&txtUsuario=");
			Object obj = myURL.getContent();
			try{
				i = (InputStream)obj;
			}catch(ClassCastException e){
				System.err.println("Error de Datos");
			}
		}catch(IOException e){
			System.err.println("URL no valida");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(i));
		try{
			String st = br.readLine();
			while(st != null){
				System.out.println(st);
				st = br.readLine();
			}
		}catch(IOException e){
		}
	}
}
