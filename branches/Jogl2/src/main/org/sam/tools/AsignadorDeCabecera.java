/* 
 * AsignadorDeCabecera.java
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
package org.sam.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Comando que icluye una cabecera en los archivos fuente de un determinado
 * directorio, la cabecera de este código fuente, ha sido incluida por este
 * programa.
 */
public class AsignadorDeCabecera {

	private static String template =
	"/* \n"+
	" * %1$s\n"+
	" * \n"+
	" * Copyright (c) %2$s\n"+
	" * %3$s.\n"+
	" * All rights reserved.\n"+
	" * \n"+
	" * This file is part of %4$s.\n"+
	" * \n"+
	" * %4$s is free software: you can redistribute it and/or modify\n"+
	" * it under the terms of the GNU General Public License as published by\n"+
	" * the Free Software Foundation, either version 3 of the License, or\n"+
	" * (at your option) any later version.\n"+
	" * \n"+
	" * %4$s is distributed in the hope that it will be useful,\n"+
	" * but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
	" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
	" * GNU General Public License for more details.\n"+
	" * \n"+
	" * You should have received a copy of the GNU General Public License\n"+
	" * along with %4$s. If not, see <http://www.gnu.org/licenses/>.\n"+
	" */\n";
	
	private static void includeCabecera(File archivo, String anio, String autor, String proyecto) throws FileNotFoundException, IOException{
		BufferedReader fileReader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(archivo)
				)
		);
		File tmp = new  File(archivo.getAbsolutePath()+".tmp");
		PrintStream fileWriter = new PrintStream(
				new FileOutputStream( tmp ),
				true,
				"utf-8"
		);
		fileWriter.format( template, archivo.getName(), anio, autor, proyecto );
		
		String line;
		boolean saltar = true;
		while( ( line = fileReader.readLine()) != null ){
			if(saltar)
				saltar = !(line.contains("package") || line.contains("import") || line.contains("/**"));
			if(!saltar)
				fileWriter.println(line);
		}
		fileReader.close();
		fileWriter.close();
		
		String nombre = archivo.getAbsolutePath();
		if( archivo.delete() ){
			System.out.println(nombre +": "+ (tmp.renameTo(new File(nombre)) ? "OK":"Error!") );
		}
	}
	
	/**
	 * @param args ignorados
	 */
	public static void main(String[] args){

		File f = new File("ruta del codigo fuente");

		Queue<File> directorios = new LinkedList<File>();
		directorios.add(f);
		
		while(!directorios.isEmpty()){
			File directorioActual = directorios.poll();
			System.out.println(directorioActual);
			for(File archivo: directorioActual.listFiles()){
				if( !archivo.isHidden() ){
					if( archivo.isDirectory())
						directorios.add(archivo);
					else if(archivo.getName().endsWith(".java"))
						try {
							includeCabecera(
								archivo,
								"2008-2010",
								"autor",
								"nombre del proyecto"
							);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
		}
		
	}
}
