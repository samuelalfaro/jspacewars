/* 
 * Tipografias.java
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
package org.sam.util;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Clase que proporciona métodos estáticos, para trabajar de forma más cómoda con tipografías.
 */
public class Tipografias{
	
	private static boolean loadFontsInDirV15(String dir){
		try{
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Class<?>[] args = new Class[]{dir.getClass()};
			Method mLoadFonts = ge.getClass().getMethod("registerFontsInDir", args);
			mLoadFonts.invoke(ge, dir);
		} catch (SecurityException e) {
			return false;
		} catch (NoSuchMethodException e) {
			return false;
		} catch(IllegalAccessException e){
			return false;
		} catch(InvocationTargetException e){
			return false;
		} 
		return true;
	}
	
	/**
	 * Método que carga en el sistema las tipografías que se encuentran en un directorio.
	 * <p><u>Nota</u>:</p>
	 * <p>Actualmente funciona invocando mediante reflexión al método <code>registerFontsInDir</code> del 
	 * <i>SunGraphicsEnvironment</i> de la  version 1.5 o superiores. Queda pendiente, dar soporte a versiones más antiguas, y a otras máquinas virtuales.
	 * @param dir Directorio donde se encuentran las fuentes.
	 */
	public static void loadFontsInDir(String dir){
		if (!loadFontsInDirV15(dir))
			throw new UnsupportedOperationException("No disponilbe");
	}

	/**
	 * Carga una tipografía a partir de un fichero <b>True Type Font</b>, <i>.ttf</i>
	 * 
	 * @param file Fichero donde se encuentra la fuente.
	 * @return La fuente cargada.
	 */
	public static Font load( String file ){
		Font font = null;
		try{
			FileInputStream f = new FileInputStream(file);
			font = Font.createFont(Font.TRUETYPE_FONT,f);
			f.close();
		}catch(FileNotFoundException e){
			System.err.println("Fichero no encontrado" + e);
		}catch(FontFormatException e){
			System.err.println("Formato de fuente no válido" + e);
		}catch(IOException e){
			System.err.println("Error de IO" + e);
		}
		return font;
	}
	
	/**
	 * Carga una tipografía a partir de un fichero <b>True Type Font</b>, <i>.ttf</i>
	 * 
	 * @param file Fichero donde se encuentra la fuente.
	 * @param style Estilo de la fuente. Los valores aceptados son:<ul>
	 * <li>Font.PLAIN
	 * <li>Font.BOLD
	 * <li>Font.ITALIC</ul>
	 * @param size Tamaño de la fuente.
	 * @return La fuente cargada.
	 */
	public static Font load(String file, int style, float size ){
		return load(file).deriveFont(style,size);
	}
}
