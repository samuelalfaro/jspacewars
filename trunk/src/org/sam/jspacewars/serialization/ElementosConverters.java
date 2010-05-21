package org.sam.jspacewars.serialization;

import org.sam.jspacewars.elementos.Canion;
import org.sam.jspacewars.elementos.CanionInterpolado;
import org.sam.jspacewars.elementos.CanionLineal;
import org.sam.jspacewars.elementos.DisparoInterpolado;
import org.sam.jspacewars.elementos.DisparoLineal;
import org.sam.jspacewars.elementos.Elemento;
import org.sam.jspacewars.elementos.LanzaMisiles;
import org.sam.jspacewars.elementos.Misil;
import org.sam.jspacewars.elementos.NaveEnemiga;
import org.sam.jspacewars.elementos.NaveUsuario;

import com.thoughtworks.xstream.XStream;

/**
 * @author samuel
 * 
 */
public class ElementosConverters {

	private ElementosConverters() {
	}

	public static void register(XStream xStream) {

		InterpoladoresConverters.register(xStream);

		xStream.useAttributeFor(Elemento.class, "type");
//		xStream.aliasField("codigo", Elemento.class, "type");
		xStream.alias("NaveUsuario", NaveUsuario.class);
		xStream.alias("Canion", Canion.class);
		xStream.alias("CanionLineal", CanionLineal.class);
		xStream.alias("DisparoLineal", DisparoLineal.class);
		xStream.alias("CanionInterpolado", CanionInterpolado.class);
		xStream.alias("DisparoInterpolado", DisparoInterpolado.class);
		xStream.alias("LanzaMisiles", LanzaMisiles.class);
		xStream.alias("Misil", Misil.class);
		xStream.alias("NaveEnemiga", NaveEnemiga.class);
		
		xStream.registerConverter(new Ignorado());
		xStream.alias("Instancia3D", Ignorado.class);
	}
}
