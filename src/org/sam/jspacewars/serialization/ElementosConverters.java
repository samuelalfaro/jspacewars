package org.sam.jspacewars.serialization;

import org.sam.colisiones.Poligono;
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
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author samuel
 * 
 */
public class ElementosConverters {

	private static class PoligonoConverter implements Converter {

		@SuppressWarnings("unchecked")
		public boolean canConvert(Class clazz) {
			return Poligono.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			int nLados = Integer.parseInt(reader.getAttribute("nLados"));
			float coordX[] = new float[nLados];
			float coordY[] = new float[nLados];
			int i = 0;
			while( i < nLados && reader.hasMoreChildren() ){
				reader.moveDown();
				coordX[i] = Float.parseFloat(reader.getAttribute("x"));
				coordY[i] = Float.parseFloat(reader.getAttribute("y"));
				reader.moveUp();
				i++;
			}
			return new Poligono( coordX, coordY );
		}
	}
	
	private ElementosConverters() {
	}

	public static void register(XStream xStream) {

		InterpoladoresConverters.register(xStream);

		xStream.useAttributeFor(Elemento.class, "type");
		xStream.alias("Poligono", Poligono.class);
		xStream.registerConverter(new PoligonoConverter());
		
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
