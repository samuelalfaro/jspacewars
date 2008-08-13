package org.sam.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Clase que proporciona métodos estaticos, para trabajar de forma más cómoda con tipografías.
 */
public class Tipografias{
	
	private static boolean loadFontsV15(GraphicsEnvironment ge, String dir){
		try{
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
	
	private static boolean loadFontsV14(GraphicsEnvironment ge, String dir){
		// TODO emular el metodo con la version 1.4 
		return false;
	}
	
	/**
	 * Método que carga en el sistema las tipografías que se encuentran en un directorio.
	 * <p><u>Nota</u>:</p>
	 * <p>Actualmente funciona invocando mediante reflexión al método <code>registerFontsInDir</code> del 
	 * <i>SunGraphicsEnvironment</i> de la  version 1.5 o superiores. Queda pendiente, dar soporte a versiones más antiguas, y a otras máquinas virtuales.
	 * @param dir Directorio donde se encuentran las fuentes.
	 */
	public static void load(String dir){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		if (!loadFontsV15(ge, dir))
			loadFontsV14(ge, dir);
	}

	/**
	 * Carga una tipografía de texto a partir de un fichero <b>True Type Font</b>, <i>.ttf</i>
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
		Font font = null;
		try{
			FileInputStream f = new FileInputStream(file);
			font = Font.createFont(Font.TRUETYPE_FONT,f).deriveFont(style,size);
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
}