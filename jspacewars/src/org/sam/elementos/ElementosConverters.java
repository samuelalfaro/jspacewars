/**
 * 
 */
package org.sam.elementos;

import org.sam.interpoladores.InterpoladoresConverters;

import com.thoughtworks.xstream.XStream;

/**
 * @author samuel
 *
 */
public class ElementosConverters {

	private ElementosConverters(){
	}
	
	public static void register(XStream xStream) {
		
		InterpoladoresConverters.register(xStream);
		
		xStream.alias("NaveUsuario", NaveUsuario.class);
		xStream.alias("Canion", Canion.class);
		xStream.alias("CanionLineal", CanionLineal.class);
		xStream.alias("DisparoLineal", DisparoLineal.class);
		xStream.alias("CanionInterpolado", CanionInterpolado.class);
		xStream.alias("DisparoInterpolado", DisparoInterpolado.class);
		xStream.alias("LanzaMisiles", LanzaMisiles.class);
		xStream.alias("Misil", Misil.class);
		xStream.alias("NaveEnemiga", NaveEnemiga.class);
	}
}
