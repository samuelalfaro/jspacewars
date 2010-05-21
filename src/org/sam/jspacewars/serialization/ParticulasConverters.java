package org.sam.jspacewars.serialization;

import org.sam.interpoladores.Getter;
import org.sam.jogl.Apariencia;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Reflexion;

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
public class ParticulasConverters{

	private ParticulasConverters(){
	}

	/**
	 * Clase privada para evitar repetir las cadenas.
	 */
	private static class S{
		
		private static final String clazz = "class";

		private static final String Float = "float";
		private static final String TRUE = "true";

		private static final String x = "x";
		private static final String y = "y";
		private static final String z = "z";
		
		private static final String Particulas = "Particulas";
		private static final String nParticulas = "nParticulas";
		private static final String Emisor = "Emisor";
		private static final String EmisorConico = "Emisor.Conico";
		private static final String EmisorLineal = "Emisor.Lineal";
		private static final String EmisorPiramidal = "Emisor.Piramidal";
		private static final String EmisorPuntual = "Emisor.Puntual";
		
		private static final String Emision = "Emision";
		private static final String RangoDeEmision = "RangoDeEmision";
		private static final String TiempoDeVida = "TiempoDeVida";
		private static final String cache = "cache";
		private static final String Radio = "Radio";
		private static final String SemiEje = "SemiEje";
		private static final String Color = "Color";
		private static final String Velocidad = "Velocidad";
		private static final String v = "v";
		private static final String p = "p";
		private static final String s = "s";
		private static final String PertubacionColor = "PertubacionColor";
		private static final String homogenea = "homogenea";
		private static final String escalar = "escalar";
		private static final String Fuerza = "Fuerza";
		private static final String GiroIncial = "GiroIncial";
		private static final String VelocidadGiro = "VelocidadGiro";

		private static final String Apariencia = "Apariencia";
		
		private static final String unchecked = "unchecked";
	}
	
	private static class EmisorConverter implements Converter{

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Emisor.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			String att = reader.getAttribute(S.clazz);
			if( att == null)
				return null;
			try {
				Class<?> clazz = null;
				if(att.equals(S.EmisorConico)){
					clazz = Emisor.Conico.class;
				}else if(att.equals(S.EmisorLineal)){
					clazz = Emisor.Lineal.class;
				}else if(att.equals(S.EmisorPiramidal)){
					clazz = Emisor.Piramidal.class;
				}else if(att.equals(S.EmisorPuntual)){
					clazz = Emisor.Puntual.class;
				}else{
					clazz = Class.forName(reader.getAttribute(S.clazz));
				}
				Emisor emisor;
				if(reader.hasMoreChildren()){
					Object[] params = (Object[])context.convertAnother(null, Object[].class);
					emisor = (Emisor)Reflexion.findConstructor(clazz, params).newInstance(params);
				}else
					emisor = (Emisor)clazz.newInstance();
				if( (att = reader.getAttribute(S.cache)) != null )
					return new Emisor.Cache(emisor,Integer.parseInt(att));
				return emisor;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}
	}
	
	private static class ParticulasConverter implements Converter {
		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Particulas.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Particulas particulas = (Particulas)value;
			if(particulas.getApariencia() != null){
				writer.startNode("Apariencia");
				context.convertAnother(particulas.getApariencia());
				writer.endNode();
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		@SuppressWarnings("unchecked")
		public Particulas unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context){
			String att = reader.getAttribute(S.nParticulas);
			Particulas particulas = FactoriaDeParticulas.createParticulas((att!= null) ? Integer.parseInt(att) : 0);

			while(reader.hasMoreChildren()){
				reader.moveDown();
				try {
					String nodeName = reader.getNodeName();
					if(nodeName.equals(S.Emisor)){
						particulas.setEmisor((Emisor)context.convertAnother(null, Emisor.class));
					}else if(nodeName.equals(S.Emision)){
						particulas.setEmision( Enum.valueOf(Particulas.Emision.class, reader.getValue()) );
					}else if(nodeName.equals(S.RangoDeEmision)){
						particulas.setRangoDeEmision((Float)context.convertAnother(null, Float.class));
					}else if(nodeName.equals(S.TiempoDeVida)){
						particulas.setTiempoVida((Float)context.convertAnother(null, Float.class));
					}else if(nodeName.equals(S.Radio)){
						if(reader.getAttribute(S.clazz).equals(S.Float))
							particulas.setRadio((Float)context.convertAnother(null, Float.class));
						else
							particulas.setRadio((Getter.Float<Float>)context.convertAnother(null, Getter.Float.class));
					}else if(nodeName.equals(S.SemiEje)){
						if(reader.getAttribute(S.clazz).equals(S.Float))
							particulas.setSemiEje((Float)context.convertAnother(null, Float.class));
						else
							particulas.setSemiEje((Getter.Float<Float>)context.convertAnother(null, Getter.Float.class));
					}else if(nodeName.equals(S.Color)){
						if(reader.hasMoreChildren()){
							Object[] params = (Object[])context.convertAnother(null, Object[].class);
							Reflexion.findSetter(Particulas.class, S.Color, params).invoke(particulas, params);
						}
					}else if(nodeName.equals(S.PertubacionColor)){
						particulas.setPertubacionColor(
								Float.valueOf(reader.getAttribute(S.p)),
								S.TRUE.equalsIgnoreCase(reader.getAttribute(S.homogenea)),
								S.TRUE.equalsIgnoreCase(reader.getAttribute(S.escalar))
						);
					}else if(nodeName.equals(S.Velocidad)){
						float v = Float.valueOf(reader.getAttribute(S.v));
						if( (att = reader.getAttribute(S.p)) != null){
							float p = Float.valueOf(att);
							boolean s = S.TRUE.equalsIgnoreCase(reader.getAttribute(S.s));
							particulas.setVelocidad(v,p,s);
						}else
							particulas.setVelocidad(v);
					}else if(nodeName.equals(S.Fuerza)){
						particulas.setFuerza(
								Float.valueOf(reader.getAttribute(S.x)),
								Float.valueOf(reader.getAttribute(S.y)),
								Float.valueOf(reader.getAttribute(S.z))
						);					
					}else if(nodeName.equals(S.GiroIncial)){
						float v = Float.valueOf(reader.getAttribute(S.v));
						if( (att = reader.getAttribute(S.p)) != null){
							float p = Float.valueOf(att);
							boolean s = S.TRUE.equalsIgnoreCase(reader.getAttribute(S.s));
							particulas.setGiroInicial(v,p,s);
						}else
							particulas.setGiroInicial(v);
					}else if(nodeName.equals(S.VelocidadGiro)){
						float v = Float.valueOf(reader.getAttribute(S.v));
						if( (att = reader.getAttribute(S.p)) != null){
							float p = Float.valueOf(att);
							boolean s = S.TRUE.equalsIgnoreCase(reader.getAttribute(S.s));
							particulas.setVelocidadGiro(v,p,s);
						}else
							particulas.setVelocidadGiro(v);
					}else if(nodeName.equals(S.Apariencia)){
						particulas.setApariencia((Apariencia)context.convertAnother(null, Apariencia.class));
					}
				} catch (Exception e) {
					e.printStackTrace();
					reader.moveUp();
					return null;
				} 
				reader.moveUp();
			}
			return particulas;
		}
	}
	
	public static void register(XStream xStream) {
		xStream.alias(S.Emisor, Emisor.class);
		xStream.alias(S.EmisorConico, Emisor.Conico.class);
		xStream.alias(S.EmisorLineal, Emisor.Lineal.class);
		xStream.alias(S.EmisorPiramidal, Emisor.Piramidal.class);
		xStream.alias(S.EmisorPuntual, Emisor.Puntual.class);
		xStream.registerConverter(new EmisorConverter());
		xStream.alias(S.Particulas, Particulas.class);
//		xStream.alias(S.Particulas, ParticulasPointSprites.class);
//		xStream.alias(S.Particulas, ParticulasQuads.class);
		xStream.registerConverter(new ParticulasConverter());
	}
}