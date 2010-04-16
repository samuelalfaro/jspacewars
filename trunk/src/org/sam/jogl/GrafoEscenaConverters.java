package org.sam.jogl;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.*;

import org.sam.interpoladores.InterpoladoresConverters;
import org.sam.jogl.particulas.Particulas;
import org.sam.jogl.particulas.ParticulasConverters;
import org.sam.util.Imagen;
import org.sam.util.Reflexion;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

@SuppressWarnings("deprecation")
public class GrafoEscenaConverters {

	private GrafoEscenaConverters(){}
	
	/**
	 * Clase privada para evitar repetir las cadenas y evitar errores al escribirlas.
	 */
	@SuppressWarnings("unused")
	private static class S{
		
		private static final String clazz = "class";
		private static final String length = "length";
		private static final String reference = "reference";
		
		private static final String TRUE = "true";

		private static final String x = "x";
		private static final String y = "y";
		private static final String z = "z";
		
		private static final String Instancia3D = "Instancia3D";
		private static final String tipo = "tipo";
		private static final String rotation = "rotation";
		private static final String axis = "axis";
		
		private static final String Particulas = "Particulas";
		
		private static final String Grupo = "Grupo";
		private static final String childs = "childs";
		private static final String NodoCompartido = "NodoCompartido";
		private static final String NodoTransformador = "NodoTransformador";
		private static final String transformMatrix = "transformMatrix";
		
		private static final String Objeto3D = "Objeto3D";
		
		private static final String Forma3DFromObjFile = "Forma3DFromObjFile";
		private static final String path = "path";
		
		private static final String TexturedQuad = "TexturedQuad";
		private static final String ancho = "ancho";
		private static final String alto = "alto";
		private static final String x1 = "x1";
		private static final String y1 = "y1";
		private static final String x2 = "x2";
		private static final String y2 = "y2";
		private static final String u1 = "u1";
		private static final String v1 = "v1";
		private static final String u2 = "u2";
		private static final String v2 = "v2";
		
		private static final String Apariencia ="Apariencia";
		private static final String AtributosColor = "AtributosColor";
		private static final String AtributosLinea = "AtributosLinea";
		private static final String AtributosPunto = "AtributosPunto";
		private static final String AtributosPoligono = "AtributosPoligono";
		private static final String AtributosRender = "AtributosRender";

		private static final String unidadesTextura = "unidadesTextura";
		
		private static final String Material = "Material";
		private static final String DEFAULT = "DEFAULT";
		private static final String r = "r";
		private static final String g = "g";
		private static final String b = "b";
		private static final String a = "a";
		private static final String ambient = "ambient";
		private static final String diffuse  = "diffuse";
		private static final String emission = "emission";
		private static final String shininess = "shininess";
		private static final String specular = "specular";
		
		private static final String AtributosTransparencia ="AtributosTransparencia";
		private static final String equation = "equation";
		private static final String srcFunc = "srcFunc";
		private static final String dstFunc = "dstFunc";
		
		private static final String UnidadTextura = "UnidadTextura";
		
		private static final String Textura = "Textura";
		private static final String minFilter = "minFilter";
		private static final String magFilter = "magFilter";
		private static final String format = "format";
		private static final String image = "image";
		private static final String flipY = "flipY";
		private static final String wrapS = "wrapS";
		private static final String wrapT = "wrapT";
		
		private static final String AtributosTextura = "AtributosTextura";
		private static final String mode = "mode";
		private static final String envColor = "envColor";

		private static final String combineRgbMode = "combineRgbMode";
		private static final String combineRgbSrcs = "combineRgbSrcs";
		private static final String combineRgbOperands = "combineRgbOperands";
		private static final String src = "src";
		private static final String operand = "operand";
		private static final String combineRgbSource0 = "combineRgbSource0";
		private static final String combineRgbSource1 = "combineRgbSource1";
		private static final String combineRgbSource2 = "combineRgbSource2";
		
		private static final String combineAlphaMode = "combineAlphaMode";
		private static final String combineAlphaSrcs = "combineAlphaSrcs";
		private static final String combineAlphaOperands = "combineAlphaOperands";
		private static final String combineAlphaSource0 = "combineAlphaSource0";
		private static final String combineAlphaSource1 = "combineAlphaSource1";
		private static final String combineAlphaSource2 = "combineAlphaSource2";
		private static final String perspectiveCorrection = "perspectiveCorrection";
		
		private static final String GenCoordTextura = "GenCoordTextura";
		private static final String coordinates = "coordinates";
		
		private static final String Shader = "Shader";
		private static final String vertex = "vertex";
		private static final String fragment = "fragment";
		private static final String Uniform = "Uniform";
		private static final String name = "name";
		
		private static final String unchecked = "unchecked";
	}

	private static Nodo forName( String name, HierarchicalStreamReader reader, UnmarshallingContext context ){

		if(name.equals(S.Grupo))
			return (Grupo)context.convertAnother(null, Grupo.class);
		if(name.equals(S.NodoCompartido))
			return (NodoCompartido)context.convertAnother(null, NodoCompartido.class);
		if(name.equals(S.NodoTransformador))
			return (NodoTransformador)context.convertAnother(null, NodoTransformador.class);
		if(name.equals(S.Objeto3D))
			return (Objeto3D)context.convertAnother(null, Objeto3D.class);
		if(name.equals(S.Particulas)){
			boolean clone = reader.getAttribute(S.reference) != null;
			Particulas particulas = (Particulas)context.convertAnother(null, Particulas.class);
			if(clone)
				particulas =  particulas.clone();
			return particulas;
		}
		throw new RuntimeException("Child no soportado: "+name);
	}
	
	private static void writeNodo( Nodo nodo, HierarchicalStreamWriter writer, MarshallingContext context ){
		writer.startNode( nodo instanceof Particulas ? S.Particulas : nodo.getClass().getSimpleName() );
			context.convertAnother(nodo);
		writer.endNode();
	}
	
	private static class Instancia3DConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Instancia3D.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Instancia3D instancia3D = (Instancia3D)value;
			writer.startNode(S.tipo);
				context.convertAnother(instancia3D.getTipo());
			writer.endNode();

			writeNodo( instancia3D.getChilds()[0], writer, context );
			
			try{
				Field field = Reflexion.findField(Instancia3D.class, S.rotation);
				field.setAccessible(true);
				AxisAngle4f rotation = ( AxisAngle4f )field.get(instancia3D);
				if( rotation.x != 0.0f || rotation.y != 0.0f || rotation.z != 1.0f){
					writer.startNode(S.axis);
						writer.addAttribute(S.x, ((Float)rotation.x).toString());
						writer.addAttribute(S.y, ((Float)rotation.y).toString());
						writer.addAttribute(S.z, ((Float)rotation.z).toString());
					writer.endNode();
				}
			}catch( IllegalArgumentException ignorada ){
			}catch( IllegalAccessException ignorada ){
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			int tipo = 0;
			Nodo child = null;
			Vector3f axis = null;
			while (reader.hasMoreChildren()) {
				reader.moveDown();

				String nodeName = reader.getNodeName();
				if (nodeName.equals(S.tipo))
					tipo = (Integer)context.convertAnother(null, Integer.class);
				else if (nodeName.equals(S.axis))
					axis = new Vector3f(
							Float.parseFloat(reader.getAttribute(S.x)),
							Float.parseFloat(reader.getAttribute(S.y)),
							Float.parseFloat(reader.getAttribute(S.z))
					);
				else
					child = forName( nodeName, reader, context );
				reader.moveUp();
			}
			Instancia3D instancia3D = new Instancia3D(tipo, child);
			if (axis != null)
				instancia3D.setAxis(axis);
			return instancia3D;
		}
	}
	
	
	private static class GrupoConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Grupo.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			for( Nodo nodo: ((Grupo)value).getChilds() )
				writeNodo( nodo, writer, context );
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			Grupo grupo = new Grupo();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				grupo.add( forName( reader.getNodeName(), reader, context ) );
				reader.moveUp();
			}
			return grupo;
		}
	}
	
	private static class NodoCompartidoConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return NodoCompartido.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			writeNodo( ((NodoCompartido)value).getChilds()[0], writer, context );
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			Nodo child = null;
			if (reader.hasMoreChildren()) {
				reader.moveDown();
				child = forName( reader.getNodeName(), reader, context );
				reader.moveUp();
			}
			return child != null ? new NodoCompartido(child) : null;
		}
	}
	
	private static class NodoTransformadorConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return NodoTransformador.class.equals(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			NodoTransformador nodoTransformador = (NodoTransformador)value;
			writer.startNode( S.transformMatrix );
				context.convertAnother(nodoTransformador.getTransform());
			writer.endNode();
			writeNodo( nodoTransformador.getChilds()[0], writer, context );
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		
			Matrix4f transform = null;
			Nodo child = null;
			
			while (reader.hasMoreChildren()) {
				reader.moveDown();

				String nodeName = reader.getNodeName();
				if (nodeName.equals(S.transformMatrix))
					transform = (Matrix4f)context.convertAnother(null, Matrix4f.class);
				else
					child = forName( nodeName, reader, context );
				reader.moveUp();
			}
			if (child == null)
				return null;
			return new NodoTransformador(transform, child);
		}
	}
	
	private static class ObjLoaderData{
		private String path;
		private Matrix4d transformMatrix;
	}
	
	
	private static class ObjLoaderDataConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class type) {
			return type.equals(ObjLoaderData.class);
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
			ObjLoaderData loaderData = new ObjLoaderData();
			loaderData.path = reader.getAttribute(S.path);
			if( reader.hasMoreChildren() ){
				reader.moveDown();
				if( reader.getNodeName().equals(S.transformMatrix) )
					loaderData.transformMatrix = (Matrix4d) context.convertAnother(null, Matrix4d.class);
				reader.moveUp();
			}
			try{
				return ObjLoader.loadToList(loaderData.path, loaderData.transformMatrix).getForma3D();
			}catch( Exception e ){
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private static class TexturedQuadData{
		float x1;
		float y1;
		float x2;
		float y2;
		
		float u1;
		float v1;
		float u2;
		float v2;
	}
	
	private static OglList createTexturedQuad(GL gl, TexturedQuadData data){
		
		int lid = gl.glGenLists(1);
		gl.glNewList(lid, GL.GL_COMPILE);
		gl.glBegin(GL.GL_QUADS);

		gl.glTexCoord2f(data.u1,data.v1);
		gl.glVertex3f(data.x1,data.y1,0);
		gl.glTexCoord2f(data.u2,data.v1);
		gl.glVertex3f(data.x2,data.y1,0);
		gl.glTexCoord2f(data.u2,data.v2);
		gl.glVertex3f(data.x2,data.y2,0);
		gl.glTexCoord2f(data.u1,data.v2);
		gl.glVertex3f(data.x1,data.y2,0);
		gl.glEnd();
		gl.glEndList();	
		
		return new OglList(lid);
	}
	
	private static class TexturedQuadDataConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class type) {
			return type.equals(TexturedQuadData.class);
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
			TexturedQuadData data = new TexturedQuadData();

			if( reader.getAttribute(S.x1) == null ){
				float ancho_med = Float.parseFloat(reader.getAttribute(S.ancho)) / 2;
				float alto_med = Float.parseFloat(reader.getAttribute(S.alto)) / 2;
				data.x1 = -ancho_med;
				data.y1 = -alto_med;
				data.x2 = ancho_med;
				data.y2 = alto_med;
			}else{
				data.x1 = Float.parseFloat(reader.getAttribute(S.x1));
				data.y1 = Float.parseFloat(reader.getAttribute(S.y1));
				data.x2 = Float.parseFloat(reader.getAttribute(S.x2));
				data.y2 = Float.parseFloat(reader.getAttribute(S.y2));
			}
			data.u1 = Float.parseFloat(reader.getAttribute(S.u1));
			data.v1 = Float.parseFloat(reader.getAttribute(S.v1));
			data.u2 = Float.parseFloat(reader.getAttribute(S.u2));
			data.v2 = Float.parseFloat(reader.getAttribute(S.v2));
			return createTexturedQuad(GLU.getCurrentGL(), data);
		}
	}
	
	
	private static class Objeto3DConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Objeto3D.class.isAssignableFrom(clazz);
		}


		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Objeto3D objeto3D = (Objeto3D)value;
			if(objeto3D.getApariencia() != null){
				writer.startNode("Apariencia");
					context.convertAnother(objeto3D.getApariencia());
				writer.endNode();
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			Objeto3D objeto3D = new Objeto3D();
			while(reader.hasMoreChildren()){
				reader.moveDown();
				String nodeName = reader.getNodeName();
				if(nodeName.equals(S.Apariencia)){
					objeto3D.setApariencia((Apariencia)context.convertAnother(null, Apariencia.class));
				}else if( nodeName.equals(S.Forma3DFromObjFile) ){
					objeto3D.setForma3D((Forma3D)context.convertAnother(null, ObjLoaderData.class));
				}else if( nodeName.equals(S.TexturedQuad) ){
					objeto3D.setForma3D((Forma3D)context.convertAnother(null, TexturedQuadData.class));
				}
				reader.moveUp();
			}
			return objeto3D;
		}
	}
	
	private static class AparienciaConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Apariencia.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Apariencia apariencia = (Apariencia)value;
			if(apariencia.getAtributosColor() != null){
				writer.startNode(S.AtributosColor);
				context.convertAnother(apariencia.getAtributosColor());
				writer.endNode();
			}
			if(apariencia.getAtributosLinea() != null){
				writer.startNode(S.AtributosLinea);
				context.convertAnother(apariencia.getAtributosLinea());
				writer.endNode();
			}
			if(apariencia.getAtributosPunto() != null){
				writer.startNode(S.AtributosPunto);
				context.convertAnother(apariencia.getAtributosPunto());
				writer.endNode();
			}
			if(apariencia.getAtributosPoligono() != null){
				writer.startNode(S.AtributosPoligono);
				context.convertAnother(apariencia.getAtributosPoligono());
				writer.endNode();
			}
			if(apariencia.getAtributosRender() != null){
				writer.startNode(S.AtributosRender);
				context.convertAnother(apariencia.getAtributosRender());
				writer.endNode();
			}
			if(apariencia.getMaterial() != null){
				writer.startNode(S.Material);
				context.convertAnother(apariencia.getMaterial());
				writer.endNode();
			}
			if(apariencia.getAtributosTransparencia() != null){
				writer.startNode(S.AtributosTransparencia);
				context.convertAnother(apariencia.getAtributosTransparencia());
				writer.endNode();
			}
			UnidadTextura[] ut = apariencia.getUnidadesTextura();
			if(ut != null && ut.length > 0){
				if (ut.length == 1){
					context.convertAnother(ut[0]);
				}else{
					writer.startNode(S.unidadesTextura);
					context.convertAnother(ut);
					writer.endNode();
				}
			}
			if(apariencia.getShader() != null){
				writer.startNode(S.Shader);
				context.convertAnother(apariencia.getShader());
				writer.endNode();
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

			Apariencia apariencia = new Apariencia();
			while (reader.hasMoreChildren()) {
				reader.moveDown();

				String nodeName = reader.getNodeName();
				if (nodeName.equals(S.AtributosColor)){
					apariencia.setAtributosColor((AtributosColor)context.convertAnother(null, AtributosColor.class));
				}else if (nodeName.equals(S.AtributosLinea)){
					apariencia.setAtributosLinea((AtributosLinea)context.convertAnother(null, AtributosLinea.class));
				}else if (nodeName.equals(S.AtributosPunto)){
					apariencia.setAtributosPunto((AtributosPunto)context.convertAnother(null, AtributosPunto.class));
				}else if (nodeName.equals(S.AtributosPoligono)){
					apariencia.setAtributosPoligono((AtributosPoligono)context.convertAnother(null, AtributosPoligono.class));
				}else if (nodeName.equals(S.AtributosRender)){
					apariencia.setAtributosRender((AtributosRender)context.convertAnother(null, AtributosRender.class));
				}else if (nodeName.equals(S.Material)){
					apariencia.setMaterial((Material)context.convertAnother(null, Material.class));
				}else if (nodeName.equals(S.AtributosTransparencia)){
					apariencia.setAtributosTransparencia((AtributosTransparencia)context.convertAnother(null, AtributosTransparencia.class));
				}else if (nodeName.equals(S.unidadesTextura)){
					apariencia.setUnidadesTextura((UnidadTextura[])context.convertAnother(null, UnidadTextura[].class));
				}else if (nodeName.equals(S.Textura)){
					apariencia.setTextura((Textura)context.convertAnother(null, Textura.class));
				}else if (nodeName.equals(S.AtributosTextura)){
					apariencia.setAtributosTextura((AtributosTextura)context.convertAnother(null, AtributosTextura.class));
				}else if (nodeName.equals(S.GenCoordTextura)){
					apariencia.setGenCoordTextura((GenCoordTextura)context.convertAnother(null, GenCoordTextura.class));
				}else if (nodeName.equals(S.Shader)){
					apariencia.setShader((Shader)context.convertAnother(null, Shader.class));
				}
				reader.moveUp();
			}
			return apariencia;
		}
	}
	
	private static class MaterialConverter implements Converter {

		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Material.class.isAssignableFrom(clazz);
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Material material = (Material)value;
			if(material == Material.DEFAULT)
				writer.setValue(S.DEFAULT);
			else{
				try{
					Field ambientField = Reflexion.findField(Material.class,S.ambient);
					ambientField.setAccessible(true);
					float[] ambient = (float[])ambientField.get(material);
					writer.startNode(S.ambient);
						writer.addAttribute(S.r,((Float)ambient[0]).toString());
						writer.addAttribute(S.g,((Float)ambient[1]).toString());
						writer.addAttribute(S.b,((Float)ambient[2]).toString());
						writer.addAttribute(S.a,((Float)ambient[3]).toString());
					writer.endNode();

					Field difuseField = Reflexion.findField(Material.class,S.diffuse);
					difuseField.setAccessible(true);
					float[] difuse = (float[])difuseField.get(material);
					writer.startNode(S.diffuse);
						writer.addAttribute(S.r,((Float)difuse[0]).toString());
						writer.addAttribute(S.g,((Float)difuse[1]).toString());
						writer.addAttribute(S.b,((Float)difuse[2]).toString());
						writer.addAttribute(S.a,((Float)difuse[3]).toString());
					writer.endNode();
					
					Field emissionField = Reflexion.findField(Material.class,S.emission);
					emissionField.setAccessible(true);
					float[] emission = (float[])emissionField.get(material);
					writer.startNode(S.emission);
						writer.addAttribute(S.r,((Float)emission[0]).toString());
						writer.addAttribute(S.g,((Float)emission[1]).toString());
						writer.addAttribute(S.b,((Float)emission[2]).toString());
						writer.addAttribute(S.a,((Float)emission[3]).toString());
					writer.endNode();

					Field shininessField = Reflexion.findField(Material.class,S.shininess);
					shininessField.setAccessible(true);
					writer.startNode(S.shininess);
						context.convertAnother( ((float[])shininessField.get(material))[0] );
					writer.endNode();
					
					Field specularField = Reflexion.findField(Material.class,S.specular);
					specularField.setAccessible(true);
					float[] specular = (float[])specularField.get(material);
					writer.startNode(S.specular);
						writer.addAttribute(S.r,((Float)specular[0]).toString());
						writer.addAttribute(S.g,((Float)specular[1]).toString());
						writer.addAttribute(S.b,((Float)specular[2]).toString());
						writer.addAttribute(S.a,((Float)specular[3]).toString());
					writer.endNode();
				}catch( IllegalArgumentException ignorada ){
				}catch( IllegalAccessException ignorada ){
				}
			}
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			if(!reader.hasMoreChildren() && reader.getValue().equals(S.DEFAULT))
				return Material.DEFAULT;
			Material material = new Material();
			while (reader.hasMoreChildren()) {
				reader.moveDown();

				String nodeName = reader.getNodeName();
				
				if (nodeName.equals(S.ambient)){
					material.setAmbient(
							Float.parseFloat(reader.getAttribute(S.r)),
							Float.parseFloat(reader.getAttribute(S.g)),
							Float.parseFloat(reader.getAttribute(S.b)),
							Float.parseFloat(reader.getAttribute(S.a))
					);
				}else if (nodeName.equals(S.diffuse)){
					material.setDiffuse(
							Float.parseFloat(reader.getAttribute(S.r)),
							Float.parseFloat(reader.getAttribute(S.g)),
							Float.parseFloat(reader.getAttribute(S.b)),
							Float.parseFloat(reader.getAttribute(S.a))
					);
				}else if (nodeName.equals(S.emission)){
					material.setEmission(
							Float.parseFloat(reader.getAttribute(S.r)),
							Float.parseFloat(reader.getAttribute(S.g)),
							Float.parseFloat(reader.getAttribute(S.b)),
							Float.parseFloat(reader.getAttribute(S.a))
					);
				}else if (nodeName.equals(S.shininess)){
					material.setShininess(Float.parseFloat(reader.getValue()));
				}else if (nodeName.equals(S.specular)){
					material.setSpecular(
							Float.parseFloat(reader.getAttribute(S.r)),
							Float.parseFloat(reader.getAttribute(S.g)),
							Float.parseFloat(reader.getAttribute(S.b)),
							Float.parseFloat(reader.getAttribute(S.a))
					);
				}
				reader.moveUp();
			}
			return material;
		}
	}
	
	private static class AtributosTransparenciaConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return AtributosTransparencia.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			AtributosTransparencia atributosTransparencia = (AtributosTransparencia)value;
			try{
				Field field;
				field = Reflexion.findField(AtributosTransparencia.class, S.equation);
				field.setAccessible(true);
				writer.addAttribute(
						S.equation,
						((AtributosTransparencia.Equation)field.get(atributosTransparencia)).toString()
				);
				field = Reflexion.findField(AtributosTransparencia.class, S.srcFunc);
				field.setAccessible(true);
				writer.addAttribute(
						S.srcFunc,
						((AtributosTransparencia.SrcFunc)field.get(atributosTransparencia)).toString()
				);
				field = Reflexion.findField(AtributosTransparencia.class, S.dstFunc);
				field.setAccessible(true);
				writer.addAttribute(
						S.dstFunc,
						((AtributosTransparencia.DstFunc)field.get(atributosTransparencia)).toString()
				);
			}catch( IllegalArgumentException ignorada ){
			}catch( IllegalAccessException ignorada ){
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			AtributosTransparencia.Equation equation = 
				Enum.valueOf(AtributosTransparencia.Equation.class, reader.getAttribute(S.equation));
			AtributosTransparencia.SrcFunc srcFunc = 
				Enum.valueOf(AtributosTransparencia.SrcFunc.class, reader.getAttribute(S.srcFunc));
			AtributosTransparencia.DstFunc dstFunc = 
				Enum.valueOf(AtributosTransparencia.DstFunc.class, reader.getAttribute(S.dstFunc));
			return new AtributosTransparencia(equation, srcFunc, dstFunc);
		}
	}
	
	private static class UnidadTexturaConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return UnidadTextura.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			UnidadTextura unidadTextura = (UnidadTextura) value;
			if( unidadTextura.getTextura() != null ){
				writer.startNode(S.Textura);
				context.convertAnother(unidadTextura.getTextura());
				writer.endNode();
			}
			if( unidadTextura.getAtributosTextura() != null
					&& unidadTextura.getAtributosTextura() != AtributosTextura.DEFAULT ){
				writer.startNode(S.AtributosTextura);
				context.convertAnother(unidadTextura.getAtributosTextura());
				writer.endNode();
			}
			if( unidadTextura.getGenCoordTextura() != null ){
				writer.startNode(S.GenCoordTextura);
				context.convertAnother(unidadTextura.getGenCoordTextura());
				writer.endNode();
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			UnidadTextura unidadTextura = new UnidadTextura();
			while (reader.hasMoreChildren()) {
				reader.moveDown();

				String nodeName = reader.getNodeName();
				if (nodeName.equals(S.Textura)){
					unidadTextura.setTextura((Textura)context.convertAnother(null, Textura.class));
				}else if (nodeName.equals(S.AtributosTextura)){
					unidadTextura.setAtributosTextura((AtributosTextura)context.convertAnother(null, AtributosTextura.class));
				}else if (nodeName.equals(S.GenCoordTextura)){
					unidadTextura.setGenCoordTextura((GenCoordTextura)context.convertAnother(null, GenCoordTextura.class));
				}
				reader.moveUp();
			}
			return unidadTextura;
		}
	}
	
	private static class TexturaConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Textura.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Textura textura = (Textura)value;
			// TODO almacenar estos datos en la textura para su serialización. (Prescindible)
			writer.addAttribute(S.minFilter, Textura.MinFilter.LINEAR.toString());
			writer.addAttribute(S.magFilter, Textura.MagFilter.LINEAR.toString());
			writer.addAttribute(S.format, Textura.Format.RGB.toString());
			writer.addAttribute(S.image, "texture path");
			writer.addAttribute(S.flipY, ((Boolean)true).toString());
			try{
				Field field;
				field = Reflexion.findField(Textura.class,S.wrapS);
				field.setAccessible(true);
				writer.addAttribute(S.wrapS, ((Textura.Wrap)field.get(textura)).toString());
				
				field = Reflexion.findField(Textura.class,S.wrapT);
				field.setAccessible(true);
				writer.addAttribute(S.wrapT, ((Textura.Wrap)field.get(textura)).toString());
			}catch( IllegalArgumentException ignorada ){
			}catch( IllegalAccessException ignorada ){
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
//			System.err.println(reader.getAttribute(S.format) + " " +reader.getAttribute(S.image));
			Textura.MinFilter minFilter = Enum.valueOf(Textura.MinFilter.class, reader.getAttribute(S.minFilter));
			Textura.MagFilter magFilter = Enum.valueOf(Textura.MagFilter.class, reader.getAttribute(S.magFilter));
			Textura.Format format = Enum.valueOf(Textura.Format.class, reader.getAttribute(S.format));
			BufferedImage image = Imagen.cargarToBufferedImage(reader.getAttribute(S.image));
			boolean flipY = reader.getAttribute(S.flipY) != null && S.TRUE.equalsIgnoreCase(reader.getAttribute(S.flipY));
			Textura textura = new Textura(GLU.getCurrentGL(), minFilter, magFilter, format, image, flipY);
			textura.setWrap_s(Enum.valueOf(Textura.Wrap.class, reader.getAttribute(S.wrapS)));
			textura.setWrap_t(Enum.valueOf(Textura.Wrap.class, reader.getAttribute(S.wrapT)));
			return textura;
		}
	}
	
	private static class AtributosTexturaConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return AtributosTextura.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			AtributosTextura atributosTextura = (AtributosTextura)value;
			try{
				Field field;
				
				field = Reflexion.findField(AtributosTextura.class,S.envColor);
				field.setAccessible(true);
				float[] envColor = (float[])field.get(atributosTextura);
				if(envColor != null){
					writer.startNode(S.envColor);
						writer.addAttribute(S.r,((Float)envColor[0]).toString());
						writer.addAttribute(S.g,((Float)envColor[1]).toString());
						writer.addAttribute(S.b,((Float)envColor[2]).toString());
						writer.addAttribute(S.a,((Float)envColor[3]).toString());
					writer.endNode();
				}
				
				field = Reflexion.findField(AtributosTextura.class,S.mode);
				field.setAccessible(true);
				AtributosTextura.Mode mode = ( AtributosTextura.Mode )field.get(atributosTextura);
				writer.startNode(S.mode);
					context.convertAnother(mode);
				writer.endNode();
				
				if(mode == AtributosTextura.Mode.COMBINE){
					
					field = Reflexion.findField(AtributosTextura.class,S.combineRgbMode);
					field.setAccessible(true);
					AtributosTextura.CombineMode combineRgbMode = ( AtributosTextura.CombineMode )field.get(atributosTextura);
					writer.startNode(S.combineRgbMode);
						context.convertAnother(combineRgbMode);
					writer.endNode();
					field = Reflexion.findField(AtributosTextura.class,S.combineRgbSrcs);
					field.setAccessible(true);
					AtributosTextura.CombineSrc[] combineRgbSrcs = (AtributosTextura.CombineSrc[])field.get(atributosTextura);
					field = Reflexion.findField(AtributosTextura.class,S.combineRgbOperands);
					field.setAccessible(true);
					AtributosTextura.CombineOperand[] combineRgbOperands = (AtributosTextura.CombineOperand[])field.get(atributosTextura);
					
					writer.startNode(S.combineRgbSource0);
						writer.addAttribute(S.src, combineRgbSrcs[0].toString());
						writer.addAttribute(S.operand, combineRgbOperands[0].toString());
					writer.endNode();
					if (combineRgbMode != AtributosTextura.CombineMode.REPLACE) {
						writer.startNode(S.combineRgbSource1);
							writer.addAttribute(S.src, combineRgbSrcs[1].toString());
							writer.addAttribute(S.operand, combineRgbOperands[1].toString());
						writer.endNode();
						if (combineRgbMode == AtributosTextura.CombineMode.INTERPOLATE) {
							writer.startNode(S.combineRgbSource2);
								writer.addAttribute(S.src, combineRgbSrcs[2].toString());
								writer.addAttribute(S.operand, combineRgbOperands[2].toString());
							writer.endNode();
						}
					}
					
					field = Reflexion.findField(AtributosTextura.class,S.combineAlphaMode);
					field.setAccessible(true);
					AtributosTextura.CombineMode combineAlphaMode = ( AtributosTextura.CombineMode )field.get(atributosTextura);
					writer.startNode(S.combineAlphaMode);
						context.convertAnother(combineAlphaMode);
					writer.endNode();
					field = Reflexion.findField(AtributosTextura.class,S.combineAlphaSrcs);
					field.setAccessible(true);
					AtributosTextura.CombineSrc[] combineAlphaSrcs = (AtributosTextura.CombineSrc[])field.get(atributosTextura);
					field = Reflexion.findField(AtributosTextura.class,S.combineAlphaOperands);
					field.setAccessible(true);
					AtributosTextura.CombineOperand[] combineAlphaOperands = (AtributosTextura.CombineOperand[])field.get(atributosTextura);
					
					writer.startNode(S.combineAlphaSource0);
						writer.addAttribute(S.src, combineAlphaSrcs[0].toString());
						writer.addAttribute(S.operand, combineAlphaOperands[0].toString());
					writer.endNode();
					if (combineAlphaMode != AtributosTextura.CombineMode.REPLACE) {
						writer.startNode(S.combineAlphaSource1);
							writer.addAttribute(S.src, combineAlphaSrcs[1].toString());
							writer.addAttribute(S.operand, combineAlphaOperands[1].toString());
						writer.endNode();
						if (combineAlphaMode == AtributosTextura.CombineMode.INTERPOLATE) {
							writer.startNode(S.combineAlphaSource2);
								writer.addAttribute(S.src, combineAlphaSrcs[2].toString());
								writer.addAttribute(S.operand, combineAlphaOperands[2].toString());
							writer.endNode();
						}
					}
				}
				
				field = Reflexion.findField(AtributosTextura.class,S.perspectiveCorrection);
				field.setAccessible(true);
				writer.startNode(S.perspectiveCorrection);
					context.convertAnother( field.get(atributosTextura) );
				writer.endNode();
				
			}catch( IllegalArgumentException ignorada ){
			}catch( IllegalAccessException ignorada ){
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			AtributosTextura atributosTextura = new AtributosTextura();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
			
				String nodeName = reader.getNodeName();
				if (nodeName.equals(S.envColor)){
					atributosTextura.setEnvColor(
							Float.parseFloat(reader.getAttribute(S.r)),
							Float.parseFloat(reader.getAttribute(S.g)),
							Float.parseFloat(reader.getAttribute(S.b)),
							Float.parseFloat(reader.getAttribute(S.a))
					);
				}else if (nodeName.equals(S.mode)){
					atributosTextura.setMode(Enum.valueOf(AtributosTextura.Mode.class, reader.getValue()));
				}else if (nodeName.equals(S.combineRgbMode)){
					atributosTextura.setCombineRgbMode(Enum.valueOf(AtributosTextura.CombineMode.class, reader.getValue()));
				}else if (nodeName.equals(S.combineRgbSource0)){
					atributosTextura.setCombineRgbSource0(
							Enum.valueOf(AtributosTextura.CombineSrc.class, reader.getAttribute(S.src)),
							Enum.valueOf(AtributosTextura.CombineOperand.class, reader.getAttribute(S.operand))
					);
				}else if (nodeName.equals(S.combineRgbSource1)){
					atributosTextura.setCombineRgbSource1(
							Enum.valueOf(AtributosTextura.CombineSrc.class, reader.getAttribute(S.src)),
							Enum.valueOf(AtributosTextura.CombineOperand.class, reader.getAttribute(S.operand))
					);
				}else if (nodeName.equals(S.combineRgbSource2)){
					atributosTextura.setCombineRgbSource2(
							Enum.valueOf(AtributosTextura.CombineSrc.class, reader.getAttribute(S.src)),
							Enum.valueOf(AtributosTextura.CombineOperand.class, reader.getAttribute(S.operand))
					);
				}else if (nodeName.equals(S.combineAlphaMode)){
					atributosTextura.setCombineAlphaMode(Enum.valueOf(AtributosTextura.CombineMode.class, reader.getValue()));
				}else if (nodeName.equals(S.combineAlphaSource0)){
					atributosTextura.setCombineAlphaSource0(
							Enum.valueOf(AtributosTextura.CombineSrc.class, reader.getAttribute(S.src)),
							Enum.valueOf(AtributosTextura.CombineOperand.class, reader.getAttribute(S.operand))
					);
				}else if (nodeName.equals(S.combineAlphaSource1)){
					atributosTextura.setCombineAlphaSource1(
							Enum.valueOf(AtributosTextura.CombineSrc.class, reader.getAttribute(S.src)),
							Enum.valueOf(AtributosTextura.CombineOperand.class, reader.getAttribute(S.operand))
					);
				}else if (nodeName.equals(S.combineAlphaSource2)){
					atributosTextura.setCombineAlphaSource2(
							Enum.valueOf(AtributosTextura.CombineSrc.class, reader.getAttribute(S.src)),
							Enum.valueOf(AtributosTextura.CombineOperand.class, reader.getAttribute(S.operand))
					);
				}else if (nodeName.equals(S.perspectiveCorrection)){
					atributosTextura.setPerspectiveCorrection(Enum.valueOf(AtributosTextura.PerspectiveCorrection.class, reader.getValue()));
				}
				reader.moveUp();
			}
			return atributosTextura;
		}
	}
	
	private static class GenCoordTexturaConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return GenCoordTextura.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			GenCoordTextura genCoordTextura = (GenCoordTextura)value;
			try{
				Field field;
				field = Reflexion.findField(GenCoordTextura.class,S.mode);
				field.setAccessible(true);
				writer.addAttribute(S.mode, ((GenCoordTextura.Mode)field.get(genCoordTextura)).toString());
				
				field = Reflexion.findField(GenCoordTextura.class,S.coordinates);
				field.setAccessible(true);
				writer.addAttribute(S.coordinates,
						((GenCoordTextura.Coordinates)field.get(genCoordTextura)).toString());
			}catch( IllegalArgumentException ignorada ){
			}catch( IllegalAccessException ignorada ){
			}
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			GenCoordTextura.Mode mode = 
				Enum.valueOf(GenCoordTextura.Mode.class, reader.getAttribute(S.mode));
			GenCoordTextura.Coordinates coordinates = 
				Enum.valueOf(GenCoordTextura.Coordinates.class, reader.getAttribute(S.coordinates));
			return new GenCoordTextura(mode, coordinates);
		}
	}
	
	private static class ShaderConverter implements Converter {

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
		 */
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Shader.class.isAssignableFrom(clazz);
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
		 */
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			// TODO guardar en la clase los datos de origen para su serializacion (prescindible)
			writer.addAttribute(S.vertex, "vertex shader path");
			writer.addAttribute(S.fragment, "fragment shader path");
		}

		/* (non-Javadoc)
		 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			String vertexFile = reader.getAttribute(S.vertex);
			String fragmentFile = reader.getAttribute(S.fragment);
			GL gl = GLU.getCurrentGL();
			Shader shader = new Shader(gl, vertexFile, fragmentFile);
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				String nodeName = reader.getNodeName();
				
				if (nodeName.equals(S.Uniform)){
					shader.addUniform(
							gl,
							reader.getAttribute(S.name),
							((Object[])context.convertAnother(null, Object[].class))[0]
					);
				}
				reader.moveUp();
			}
			return shader;
		}
	}
	
	private static class ReusingReferenceByXPathMarshallingStrategy implements MarshallingStrategy {
	
		private TreeMarshaller marshaller;
		private TreeUnmarshaller unmarshaller;
		
		private int mode;
		
		@SuppressWarnings("unused")
		public ReusingReferenceByXPathMarshallingStrategy() {
	        this(ReferenceByXPathMarshallingStrategy.RELATIVE);
	    }

	    public ReusingReferenceByXPathMarshallingStrategy(int mode) {
	        this.mode = mode;
	    }
    	
	    public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, ConverterLookup converterLookup, Mapper mapper) {
	    	
	    	if(unmarshaller == null)
	    		unmarshaller = new ReferenceByXPathUnmarshaller(root, reader, converterLookup, mapper){
		    		// TODO Mirar en futuras vesiones para quitar esta ñapa que adapta las referencias
		    	    protected Object getReferenceKey(String reference) {
		    	    	if(reference.startsWith("/Instancia3D[")){
		    	    		//System.out.println(reference);
		    	    		String number = reference.substring("/Instancia3D[".length(),reference.indexOf(']'));
		    	    		String resto = reference.substring(reference.indexOf("]/")+2);
		    	    		StringBuffer newReference = new StringBuffer("/Instancia3D/");
		    	    		newReference.append(resto.substring(0,resto.indexOf('/')));
		    	    		newReference.append('[');
		    	    		newReference.append(number);
		    	    		newReference.append(']');
		    	    		newReference.append(resto.substring(resto.indexOf('/')));
		    	    		//System.out.println(newReference);
			    	        return super.getReferenceKey( newReference.toString() );
		    	    	}
		    	        return super.getReferenceKey( reference );
		    	    }
	    		};
	    	return unmarshaller.start(dataHolder);
	    }

        public void marshal(HierarchicalStreamWriter writer, Object obj, ConverterLookup converterLookup, Mapper mapper, DataHolder dataHolder) {
	    	if( marshaller == null )
	    		marshaller = new ReferenceByXPathMarshaller(writer, converterLookup, mapper, mode);
	    	marshaller.start(obj, dataHolder);
        }
        
        /**
         * @deprecated As of 1.2, use {@link #unmarshal(Object, HierarchicalStreamReader, DataHolder, ConverterLookup, Mapper)}
         */
        public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
            return unmarshal(root, reader, dataHolder, (ConverterLookup)converterLookup, (Mapper)classMapper);
        }

        /**
         * @deprecated As of 1.2, use {@link #marshal(HierarchicalStreamWriter, Object, ConverterLookup, Mapper, DataHolder)}
         */
        public void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper, DataHolder dataHolder) {
            marshal(writer, obj, converterLookup, (Mapper)classMapper, dataHolder);
        }
    }

	/**
	 *
	 * @param xStream
	 */
	public static void register(XStream xStream) {

		xStream.alias(S.Instancia3D, Instancia3D.class);
		xStream.registerConverter(new Instancia3DConverter());
		
		xStream.alias(S.Grupo, Grupo.class);
		xStream.registerConverter(new GrupoConverter());
		xStream.alias(S.NodoCompartido, NodoCompartido.class);
		xStream.registerConverter(new NodoCompartidoConverter());
		xStream.alias(S.NodoTransformador, NodoTransformador.class);
		xStream.registerConverter(new NodoTransformadorConverter());
		xStream.alias(S.Objeto3D, Objeto3D.class);
		xStream.registerConverter(new Objeto3DConverter());
		xStream.registerConverter(new ObjLoaderDataConverter());
		xStream.registerConverter(new TexturedQuadDataConverter());

		xStream.alias(S.Apariencia, Apariencia.class);
		xStream.registerConverter(new AparienciaConverter());

		xStream.alias(S.Material, Material.class);
		xStream.alias(S.Material, Material.DEFAULT.getClass());
		xStream.addImmutableType(Material.DEFAULT.getClass());
		xStream.registerConverter(new MaterialConverter());

		xStream.alias(S.AtributosTransparencia, AtributosTransparencia.class);
		xStream.registerConverter(new AtributosTransparenciaConverter());

		xStream.alias(S.UnidadTextura, UnidadTextura.class);
		xStream.registerConverter(new UnidadTexturaConverter());
		xStream.alias(S.Textura, Textura.class);
		xStream.registerConverter(new TexturaConverter());
		xStream.alias(S.AtributosTextura, AtributosTextura.class);
		xStream.registerConverter(new AtributosTexturaConverter());
		xStream.alias(S.GenCoordTextura, GenCoordTextura.class);
		xStream.registerConverter(new GenCoordTexturaConverter());
		
		xStream.alias(S.Shader, Shader.class);
		xStream.registerConverter(new ShaderConverter());
		
		InterpoladoresConverters.register(xStream);
		ParticulasConverters.register(xStream);
	}
	
	public static void setReusingReferenceByXPathMarshallingStrategy(XStream xStream) {
		MarshallingStrategy strategy = new ReusingReferenceByXPathMarshallingStrategy(ReferenceByXPathMarshallingStrategy.ABSOLUTE);
		xStream.setMarshallingStrategy(strategy);
	}
}
