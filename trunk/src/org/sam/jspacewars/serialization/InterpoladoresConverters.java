package org.sam.jspacewars.serialization;

import org.sam.interpoladores.Extractor;
import org.sam.interpoladores.Getter;
import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.Introductor;
import org.sam.interpoladores.Keys;
import org.sam.interpoladores.MetodoDeInterpolacion;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *
 * @author Samuel
 */
public class InterpoladoresConverters {

	private InterpoladoresConverters(){}
	
	/**
	 * Clase privada para evitar repetir las cadenas.
	 * @author samuel
	 */
	private static class S{
		
		private static final String clazz = "class";
		private static final String length = "length";

		private static final String Double = "double";
		private static final String Float = "float";
		private static final String Integer = "int";
		private static final String Punto2D = "Punto2D";
		private static final String Punto2F = "Punto2F";
		private static final String Punto2I = "Punto2I";
		private static final String Punto3D = "Punto3D";
		private static final String Punto3F = "Punto3F";
		private static final String Punto3I = "Punto3I";

		private static final String x = "x";
		private static final String y = "y";
		private static final String z = "z";
		
		private static final String Color3F = "Color3F";
		private static final String r = "r";
		private static final String g = "g";
		private static final String b = "b";
		
		private static final String Getter_Double = "Getter.Double";
		private static final String Getter_Float = "Getter.Float";
		private static final String Getter_Integer = "Getter.Integer";
		
		private static final String Keys = "Keys";
		
		private static final String keyGen = "keyGen";
		private static final String PROPORCIONALES = "PROPORCIONALES";
		private static final String HOMOGENEAS = "HOMOGENEAS";
		private static final String scale = "scale";
		private static final String translation = "translation";
		
		private static final String Introductor = "Introductor";
		private static final String Values = "Values";
		private static final String Extractor = "Extractor";
		
		private static final String MetodoDeInterpolacion = "MetodoDeInterpolacion";
		private static final String predefinido = "predefinido";
		private static final String Params = "Params";
		
		private static final String unchecked = "unchecked";
	}

	private static class Punto2D implements Converter {

		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Punto2D.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new double[] {
					Double.parseDouble(reader.getAttribute(S.x)),
					Double.parseDouble(reader.getAttribute(S.y)) };
		}
	}

	private static class Punto2F implements Converter {

		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Punto2F.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new float[] {
					Float.parseFloat(reader.getAttribute(S.x)),
					Float.parseFloat(reader.getAttribute(S.y)) };
		}
	}

	private static class Punto2I implements Converter {

		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Punto2I.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new int[] {
					Integer.parseInt(reader.getAttribute(S.x)),
					Integer.parseInt(reader.getAttribute(S.y)) };
		}
	}

	private static class Punto3D implements Converter {

		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Punto3D.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new double[] {
					Double.parseDouble(reader.getAttribute(S.x)),
					Double.parseDouble(reader.getAttribute(S.y)),
					Double.parseDouble(reader.getAttribute(S.z)) };
		}
	}

	private static class Punto3F implements Converter {

		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Punto3F.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			return new float[] {
					Float.parseFloat(reader.getAttribute(S.x)),
					Float.parseFloat(reader.getAttribute(S.y)),
					Float.parseFloat(reader.getAttribute(S.z)) };
		}
	}

	private static class Punto3I implements Converter {

		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Punto3I.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return new int[] {
					Integer.parseInt(reader.getAttribute(S.x)),
					Integer.parseInt(reader.getAttribute(S.y)),
					Integer.parseInt(reader.getAttribute(S.z)) };
		}
	}
	
	private static class Color3F implements Converter {

		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Color3F.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			return new float[] {
					Float.parseFloat(reader.getAttribute(S.r)),
					Float.parseFloat(reader.getAttribute(S.g)),
					Float.parseFloat(reader.getAttribute(S.b)) };
		}
	}

	private static void writeValuesOfGetter(Object values, HierarchicalStreamWriter writer, MarshallingContext context){
		if ( values != null && values.getClass().isArray() ) {
			writer.startNode(S.Values);
			writer.addAttribute(S.clazz, values.getClass().getName());
			Class<?> clazz = values.getClass().getComponentType();

			boolean guardado = false;
			if ( clazz.equals(double.class)){
				double[] valuesArray = (double[])values;
				writer.addAttribute(S.length, ((Integer) valuesArray.length).toString());
				for (double v: valuesArray) {
					writer.startNode(S.Double);
					writer.setValue(((Double)v).toString());
					writer.endNode();
				}
				guardado = true;
			}else if ( clazz.equals(float.class)){
				float[] valuesArray = (float[])values;
				writer.addAttribute(S.length, ((Integer) valuesArray.length).toString());
				for (float v: valuesArray) {
					writer.startNode(S.Float);
					writer.setValue(((Float)v).toString());
					writer.endNode();
				}
				guardado = true;
			}else if ( clazz.equals(int.class)){
				int[] valuesArray = (int[])values;
				writer.addAttribute(S.length, ((Integer) valuesArray.length).toString());
				for (int v: valuesArray) {
					writer.startNode(S.Integer);
					writer.setValue(((Integer)v).toString());
					writer.endNode();
				}
				guardado = true;
			}else if ( clazz.equals(double[].class) ){
				double[][] valuesArray = (double[][])values;
				writer.addAttribute(S.length, ((Integer) valuesArray.length).toString());
				if(valuesArray[0].length == 2){
					for (double[] p2d : valuesArray) {
						writer.startNode(S.Punto2D);
						writer.addAttribute(S.x, ((Double) p2d[0]).toString());
						writer.addAttribute(S.y, ((Double) p2d[1]).toString());
						writer.endNode();
					}
					guardado = true;
				}else if(valuesArray[0].length == 3){
					for (double[] p3d : valuesArray) {
						writer.startNode(S.Punto3D);
						writer.addAttribute(S.x, ((Double) p3d[0]).toString());
						writer.addAttribute(S.y, ((Double) p3d[1]).toString());
						writer.addAttribute(S.z, ((Double) p3d[2]).toString());
						writer.endNode();
					}
					guardado = true;
				}
			}else if ( clazz.equals(float[].class) ){
				float[][] valuesArray = (float[][])values;
				writer.addAttribute(S.length, ((Integer) valuesArray.length).toString());
				if(valuesArray[0].length == 2){
					for (float[] p2f : valuesArray) {
						writer.startNode(S.Punto2F);
						writer.addAttribute(S.x, ((Float) p2f[0]).toString());
						writer.addAttribute(S.y, ((Float) p2f[1]).toString());
						writer.endNode();
					}
					guardado = true;
				}else if(valuesArray[0].length == 3){
					for (float[] p3f : valuesArray) {
						writer.startNode(S.Punto3F);
						writer.addAttribute(S.x, ((Float) p3f[0]).toString());
						writer.addAttribute(S.y, ((Float) p3f[1]).toString());
						writer.addAttribute(S.z, ((Float) p3f[2]).toString());
						writer.endNode();
					}
					guardado = true;
				}
			}else if ( clazz.equals(int[].class) ){
				int[][] valuesArray = (int[][])values;
				writer.addAttribute(S.length, ((Integer) valuesArray.length).toString());
				if(valuesArray[0].length == 2){
					for (int[] p2i : valuesArray) {
						writer.startNode(S.Punto2I);
						writer.addAttribute(S.x, ((Integer) p2i[0]).toString());
						writer.addAttribute(S.y, ((Integer) p2i[1]).toString());
						writer.endNode();
					}
					guardado = true;
				}else if(valuesArray[0].length == 2){
					for (int[] p3i : valuesArray) {
						writer.startNode(S.Punto3I);
						writer.addAttribute(S.x, ((Integer) p3i[0]).toString());
						writer.addAttribute(S.y, ((Integer) p3i[1]).toString());
						writer.addAttribute(S.z, ((Integer) p3i[2]).toString());
						writer.endNode();
					}
					guardado = true;
				}
			}else{
				Object[] valuesArray = (Object[])values;
				writer.addAttribute(S.length, ((Integer) valuesArray.length).toString());
			}
			if(!guardado)
				context.convertAnother(values);
			writer.endNode();
		}
	}

	private static class GetterDoubleConverter implements Converter {

		private static class DoubleParams{
			private double keys[];
			private int keysGen;
			private double keysScale;
			private double keysTranslation;
			private Object values;
			private Introductor.Double<?> introductor;
			private Extractor.Double<?> extractor;
			private MetodoDeInterpolacion metodoDeInterpolacion;
			private Object mdiParams[];
		}
		
		@SuppressWarnings(S.unchecked)
		private static <T> Getter.Double<?> create(DoubleParams params){
			if(params.keys != null){
				if(params.values instanceof Double[])
					return GettersFactory.Double.create(params.keys, (Double[])params.values, params.metodoDeInterpolacion, params.mdiParams );
				if(params.values instanceof double[])
					return GettersFactory.Double.create(params.keys, (double[])params.values, params.metodoDeInterpolacion, params.mdiParams );
				if(params.values instanceof double[][])
					return GettersFactory.Double.create(params.keys, (double[][])params.values, params.metodoDeInterpolacion, params.mdiParams );
				return GettersFactory.Double.create(params.keys, (Introductor.Double<T>) params.introductor, (T[])params.values, (Extractor.Double<? super T>)params.extractor, params.metodoDeInterpolacion, params.mdiParams );
			}
			if(params.values instanceof Double[])
				return GettersFactory.Double.create(params.keysGen, params.keysScale, params.keysTranslation, (Double[])params.values, params.metodoDeInterpolacion, params.mdiParams );
			if(params.values instanceof double[])
				return GettersFactory.Double.create(params.keysGen, params.keysScale, params.keysTranslation, (double[])params.values, params.metodoDeInterpolacion, params.mdiParams );
			if(params.values instanceof double[][])
				return GettersFactory.Double.create(params.keysGen, params.keysScale, params.keysTranslation, (double[][])params.values, params.metodoDeInterpolacion, params.mdiParams );
			return GettersFactory.Double.create(params.keysGen, params.keysScale, params.keysTranslation, (Introductor.Double<T>) params.introductor, (T[])params.values, (Extractor.Double<? super T>)params.extractor, params.metodoDeInterpolacion, params.mdiParams );
		}
		
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Getter.Double.class.isAssignableFrom(clazz)
					|| DoubleParams.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			DoubleParams params = (DoubleParams)value;

			writer.startNode(S.Keys);
			if (params.keys != null) {
				writer.addAttribute(S.length,((Integer) params.keys.length).toString());
				context.convertAnother(params.keys);
			} else {
				if(params.keysGen == Keys.PROPORCIONALES)
					writer.addAttribute(S.keyGen, S.PROPORCIONALES);
				else
					writer.addAttribute(S.keyGen, S.HOMOGENEAS);
				writer.addAttribute(S.scale, ((Double) params.keysScale).toString());
				writer.addAttribute(S.translation, ((Double) params.keysTranslation).toString());
			}
			writer.endNode();

			if(params.introductor != null){
				writer.startNode(S.Introductor);
				writer.addAttribute(S.clazz, params.introductor.getClass().getName());
				context.convertAnother(params.introductor);
				writer.endNode();
			}

			writeValuesOfGetter(params.values, writer, context);

			if(params.extractor != null){
				writer.startNode(S.Extractor);
				writer.addAttribute(S.clazz, params.extractor.getClass().getName());
				context.convertAnother(params.extractor);
				writer.endNode();
			}

			if (params.metodoDeInterpolacion != null) {
				writer.startNode(S.MetodoDeInterpolacion);
				if (params.metodoDeInterpolacion instanceof MetodoDeInterpolacion.Predefinido)
					writer.addAttribute(S.predefinido, ((MetodoDeInterpolacion.Predefinido)params.metodoDeInterpolacion).name());
				else
					writer.addAttribute(S.clazz, params.metodoDeInterpolacion.getClass().getName());
				if (params.mdiParams != null && params.mdiParams.length != 0) {
					writer.startNode(S.Params);
					writer.addAttribute(S.length,
							((Integer) params.mdiParams.length).toString());
					context.convertAnother(params.mdiParams);
					writer.endNode();
				}
				writer.endNode();
			}
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			try{
				DoubleParams params = new DoubleParams();
				String att;

				while (reader.hasMoreChildren()) {
					reader.moveDown();

					String nodeName = reader.getNodeName();
					if (reader.getNodeName().equals(S.Keys))
						if (reader.getAttribute(S.length) != null)
							params.keys = (double[]) context.convertAnother(null, double[].class);
						else {
							att = reader.getAttribute(S.keyGen);
							if( att != null && att.equals(S.PROPORCIONALES))
								params.keysGen = Keys.PROPORCIONALES;
							else
								params.keysGen = Keys.HOMOGENEAS;
							att = reader.getAttribute(S.scale);
							params.keysScale = (att!= null) ? Double.parseDouble(att) : 1.0;
							att = reader.getAttribute(S.translation);
							params.keysTranslation = (att!= null) ? Double.parseDouble(att) : 0.0;
						}

					else if(nodeName.equals(S.Introductor))
						params.introductor = (Introductor.Double<?>)context.convertAnother(null, Class.forName(reader.getAttribute(S.clazz)));

					else if (reader.getNodeName().equals(S.Values))
						params.values =	context.convertAnother(null, Class.forName(reader.getAttribute(S.clazz)));

					else if(nodeName.equals(S.Extractor))
						params.extractor =	(Extractor.Double<?>)context.convertAnother(null, Class.forName(reader.getAttribute(S.clazz)));

					else if (nodeName.equals(S.MetodoDeInterpolacion)) {
						att = reader.getAttribute(S.predefinido);
						if (att != null)
							params.metodoDeInterpolacion = MetodoDeInterpolacion.Predefinido.valueOf(att);
						else
							params.metodoDeInterpolacion = (MetodoDeInterpolacion)
									(Class.forName(reader.getAttribute(S.clazz)).newInstance());
						if (reader.hasMoreChildren()) {
							reader.moveDown();
							if (reader.getNodeName().equals(S.Params))
								params.mdiParams = (Object[]) context.convertAnother(null, Object[].class);
							reader.moveUp();
						}
					}
					reader.moveUp();
				}
				return create(params);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}

	private static class GetterFloatConverter implements Converter {

		private static class FloatParams{
			private float keys[];
			private int keysGen;
			private float keysScale;
			private float keysTranslation;
			private Object values;
			private Introductor.Float<?> introductor;
			private Extractor.Float<?> extractor;
			private MetodoDeInterpolacion metodoDeInterpolacion;
			private Object mdiParams[];
		}

		@SuppressWarnings(S.unchecked)
		private static <T> Getter.Float<?> create(FloatParams params){
			if(params.keys != null){
				if(params.values instanceof Float[])
					return GettersFactory.Float.create(params.keys, (Float[])params.values, params.metodoDeInterpolacion, params.mdiParams );
				if(params.values instanceof float[])
					return GettersFactory.Float.create(params.keys, (float[])params.values, params.metodoDeInterpolacion, params.mdiParams );
				if(params.values instanceof float[][])
					return GettersFactory.Float.create(params.keys, (float[][])params.values, params.metodoDeInterpolacion, params.mdiParams );
				return GettersFactory.Float.create(params.keys, null, (Introductor.Float<T>) params.introductor, (T[])params.values, (Extractor.Float<? super T>)params.extractor, params.metodoDeInterpolacion, params.mdiParams );
			}
			if(params.values instanceof Float[])
				return GettersFactory.Float.create(params.keysGen, params.keysScale, params.keysTranslation, (Float[])params.values, params.metodoDeInterpolacion, params.mdiParams );
			if(params.values instanceof float[])
				return GettersFactory.Float.create(params.keysGen, params.keysScale, params.keysTranslation, (float[])params.values, params.metodoDeInterpolacion, params.mdiParams );
			if(params.values instanceof float[][])
				return GettersFactory.Float.create(params.keysGen, params.keysScale, params.keysTranslation, (float[][])params.values, params.metodoDeInterpolacion, params.mdiParams );
			
			return GettersFactory.Float.create(params.keysGen, params.keysScale, params.keysTranslation, (Introductor.Float<T>) params.introductor, (T[])params.values, (Extractor.Float<? super T>)params.extractor, params.metodoDeInterpolacion, params.mdiParams );
		}
		
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Getter.Float.class.isAssignableFrom(clazz)
					|| FloatParams.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			FloatParams params = (FloatParams)value;

			writer.startNode(S.Keys);
			if (params.keys != null) {
				writer.addAttribute(S.length,((Integer) params.keys.length).toString());
				context.convertAnother(params.keys);
			} else {
				if(params.keysGen == Keys.PROPORCIONALES)
					writer.addAttribute(S.keyGen, S.PROPORCIONALES);
				else
					writer.addAttribute(S.keyGen, S.HOMOGENEAS);
				writer.addAttribute(S.scale, ((Float) params.keysScale).toString());
				writer.addAttribute(S.translation, ((Float) params.keysTranslation).toString());
			}
			writer.endNode();

			if(params.introductor != null){
				writer.startNode(S.Introductor);
				writer.addAttribute(S.clazz, params.introductor.getClass().getName());
				context.convertAnother(params.introductor);
				writer.endNode();
			}

			writeValuesOfGetter(params.values, writer, context);

			if(params.extractor != null){
				writer.startNode(S.Extractor);
				writer.addAttribute(S.clazz, params.extractor.getClass().getName());
				context.convertAnother(params.extractor);
				writer.endNode();
			}

			if (params.metodoDeInterpolacion != null) {
				writer.startNode(S.MetodoDeInterpolacion);
				if (params.metodoDeInterpolacion instanceof MetodoDeInterpolacion.Predefinido)
					writer.addAttribute(S.predefinido, ((MetodoDeInterpolacion.Predefinido)params.metodoDeInterpolacion).name());
				else
					writer.addAttribute(S.clazz, params.metodoDeInterpolacion.getClass().getName());
				if (params.mdiParams != null && params.mdiParams.length != 0) {
					writer.startNode(S.Params);
					writer.addAttribute(S.length,
							((Integer) params.mdiParams.length).toString());
					context.convertAnother(params.mdiParams);
					writer.endNode();
				}
				writer.endNode();
			}
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			try{
				FloatParams params = new FloatParams();
				String att;

				while (reader.hasMoreChildren()) {
					reader.moveDown();

					String nodeName = reader.getNodeName();
					if (reader.getNodeName().equals(S.Keys))
						if (reader.getAttribute(S.length) != null)
							params.keys = (float[]) context.convertAnother(null, float[].class);
						else {
							att = reader.getAttribute(S.keyGen);
							if( att != null && att.equals(S.PROPORCIONALES))
								params.keysGen = Keys.PROPORCIONALES;
							else
								params.keysGen = Keys.HOMOGENEAS;
							att = reader.getAttribute(S.scale);
							params.keysScale = (att!= null) ? Float.parseFloat(att) : 1.0f;
							att = reader.getAttribute(S.translation);
							params.keysTranslation = (att!= null) ? Float.parseFloat(att) : 0.0f;
						}

					else if(nodeName.equals(S.Introductor))
						params.introductor = (Introductor.Float<?>)context.convertAnother(null, Class.forName(reader.getAttribute(S.clazz)));

					else if (reader.getNodeName().equals(S.Values))
						params.values =	context.convertAnother(null, Class.forName(reader.getAttribute(S.clazz)));

					else if(nodeName.equals(S.Extractor))
						params.extractor =	(Extractor.Float<?>)context.convertAnother(null, Class.forName(reader.getAttribute(S.clazz)));

					else if (nodeName.equals(S.MetodoDeInterpolacion)) {
						att = reader.getAttribute(S.predefinido);
						if (att != null)
							params.metodoDeInterpolacion = MetodoDeInterpolacion.Predefinido.valueOf(att);
						else
							params.metodoDeInterpolacion = (MetodoDeInterpolacion)
									(Class.forName(reader.getAttribute(S.clazz)).newInstance());
						if (reader.hasMoreChildren()) {
							reader.moveDown();
							if (reader.getNodeName().equals(S.Params))
								params.mdiParams = (Object[]) context.convertAnother(null, Object[].class);
							reader.moveUp();
						}
					}
					reader.moveUp();
				}
				return create(params);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 *
	 * @param xStream
	 */
	public static void register(XStream xStream) {

		xStream.alias(S.Getter_Double, Getter.Double.class);
		xStream.alias(S.Getter_Double, GetterDoubleConverter.DoubleParams.class);
		xStream.registerConverter(new GetterDoubleConverter());

		xStream.alias(S.Getter_Float, Getter.Float.class);
		xStream.alias(S.Getter_Float, GetterFloatConverter.FloatParams.class);
		xStream.registerConverter(new GetterFloatConverter());

		xStream.alias(S.Getter_Integer, Getter.Integer.class);
		
		xStream.alias(S.Punto2D, Punto2D.class);
		xStream.registerConverter(new Punto2D());
		xStream.alias(S.Punto2F, Punto2F.class);
		xStream.registerConverter(new Punto2F());
		xStream.alias(S.Punto2I, Punto2I.class);
		xStream.registerConverter(new Punto2I());
		xStream.alias(S.Punto3D, Punto3D.class);
		xStream.registerConverter(new Punto3D());
		xStream.alias(S.Punto3F, Punto3F.class);
		xStream.registerConverter(new Punto3F());
		xStream.alias(S.Punto3I, Punto3I.class);
		xStream.registerConverter(new Punto3I());
		xStream.alias(S.Color3F, Color3F.class);
		xStream.registerConverter(new Color3F());
	}
	
    /**
     * 
     * @param args
     */
    @SuppressWarnings(S.unchecked)
    // TODO Borrar es para pruebas
	public static void main(String... args){
		XStream xStream = new XStream(new DomDriver());
		InterpoladoresConverters.register(xStream);

		InterpoladoresConverters.GetterDoubleConverter.DoubleParams params = new InterpoladoresConverters.GetterDoubleConverter.DoubleParams();
		params.keys = new double[]{0.0,0.5,1.0};
//		params.values = new double[][]{{1,2,3},{4,5,6},{7,8,9}};
		params.values = new double[]{0.0,0.5,1.0};
		params.metodoDeInterpolacion = MetodoDeInterpolacion.Predefinido.CARDINAL_SPLINE;
		params.mdiParams = new Object[]{5.0};
		System.out.println(xStream.toXML(params));

		Getter.Double<Double> interpolador = (Getter.Double<Double>)xStream.fromXML(
				"<Getter.Double>"+
				"  <Keys keyGen=\"PROPORCIONALES\" scale=\"1.0\" translation=\"0.0\"/>"+
				"  <Values class=\"[D\" length=\"3\">"+
				"    <double>0.0</double>"+
			    "    <double>0.5</double>"+
			    "    <double>1.0</double>"+
//			    "    <Punto3D x=\"1.0\" y=\"2.0\" z=\"3.0\"/>"+
//			    "    <Punto3D x=\"4.0\" y=\"5.0\" z=\"6.0\"/>"+
//			    "    <Punto3D x=\"7.0\" y=\"8.0\" z=\"9.0\"/>"+
				"  </Values>"+
				"  <MetodoDeInterpolacion predefinido=\"CARDINAL_SPLINE\">"+
				"    <Params length=\"1\">"+
				"      <double>0.5</double>"+
				"    </Params>"+
				"  </MetodoDeInterpolacion>"+
				"</Getter.Double>"
		);
		System.out.println(interpolador);
		Double d;
		d = interpolador.get(0.1);
		System.out.println(d);
		d = interpolador.get(0.9);
		System.out.println(d);
	}
}
