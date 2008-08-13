package org.sam.j3d;

import java.nio.*;
import java.util.Enumeration;

import javax.media.j3d.*;
import javax.vecmath.Vector3f;

import org.sam.j3d.ObjLoader.ParsingErrorException;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.image.TextureLoader;

/**
 * Esta clase es una coleccion de m�todos est�ticos de utilidad para el resto de las clases.
 * 
 * @author Samuel
 */
public class Tools {
	
	/**
	 * Establece la forma de dibujar la particula, este modo, suma al color anterior el color de
	 * la particula teniendo en cuenta la transparencia de la textura y de la particula. 
	 */
	public static final int MODO_ACLARAR = 0;
	/**
	 * Establece la forma de dibujar la particula, este modo, interpola el color anterior, con el color de
	 * la particula teniendo en cuenta la transparencia de la textura y de la particula. 
	 */
	public static final int MODO_NORMAL = 1;
	/**
	 * Establece la forma de dibujar la particula, este modo, resta al color anterior el color de
	 * la particula teniendo en cuenta la transparencia de la textura y de la particula. 
	 */
	public static final int MODO_OSCURECER = 2;
	
	/**
	 * Establece la forma de dibujar la particula, este modo, resta al color anterior el color de
	 * la particula teniendo en cuenta la transparencia de la textura y de la particula. 
	 */
	public static final int MODO_MODULAR = 3;

	/**
	 * Carga una textura de una determidada ruta.
	 * 
	 * @param texturePath Ruta donde se encuentra la textura a cargar.
	 * @param textureFormat Cadena que indica el formato de la textura que se cargar�.
	 * <p>Los valores admitidos son:
	 * <ul><li>null</li>
	 * <li>"RGBA"</li>
	 * <li>"RGBA4"</li>
	 * <li>"RGB5_A1"</li>
	 * <li>"RGB"</li>
	 * <li>"RGB4"</li>
	 * <li>"RGB5"</li>
	 * <li>"R3_G3_B2"</li>
	 * <li>"LUM8_ALPHA8"</li>
	 * <li>"LUM4_ALPHA4"</li>
	 * <li>"LUMINANCE"</li>
	 * <li>"ALPHA"</li></ul></p>
	 * @return La textura cargada.
	 */
	public static Texture loadTexture(String texturePath, String textureFormat){
		if(texturePath == null)
			return null;
		TextureLoader tLoader;
		if(textureFormat != null)
			tLoader = new TextureLoader(texturePath,textureFormat,TextureLoader.GENERATE_MIPMAP, null);
		else
			tLoader = new TextureLoader(texturePath,TextureLoader.GENERATE_MIPMAP, null);
		return tLoader.getTexture();
	}
	
	/**
	 * Carga una textura de una determidada ruta.
	 * 
	 * @param texturePath Ruta donde se encuentra la textura a cargar.
	 * @return La textura cargada.
	 */
	public static Texture loadTexture(String texturePath){
		if(texturePath == null)
			return null;
		TextureLoader tLoader = new TextureLoader(texturePath,TextureLoader.GENERATE_MIPMAP, null);
		return tLoader.getTexture();
	}
	
	public static ImageComponent2D loadImage(String texturePath, String textureFormat){
		if(texturePath == null)
			return null;
		TextureLoader tLoader;
		if(textureFormat != null)
			tLoader = new TextureLoader(texturePath,textureFormat, null);
		else
			tLoader = new TextureLoader(texturePath, null);
		return tLoader.getImage();
	}
	
	/**
	 * Carga una escena a partir de un fichero <b>Wavefront <i>.OBJ</i></b>
	 * @param url Url donde se encuentra el fichero.
	 * @param flag Flag que se pasa al crear el ObjectFile, los valores aceptados son:
	 * <ul>
	 * <li>Loader.LOAD_LIGHT_NODES</li>
	 * <li>Loader.LOAD_FOG_NODES</li>
	 * <li>Loader.LOAD_BACKGROUND_NODES</li>
	 * <li>Loader.LOAD_BEHAVIOR_NODES</li>
	 * <li>Loader.LOAD_VIEW_GROUPS</li>
	 * <li>Loader.LOAD_SOUND_NODES</li>
	 * <li>Loader.LOAD_ALL</li>
	 * <li>ObjectFile.DEBUG</li>
	 * <li>ObjectFile.RESIZE</li>
	 * <li>ObjectFile.TRIANGULATE</li>
	 * <li>ObjectFile.REVERSE</li>
	 * <li>ObjectFile.STRIPIFY</li>
	 * <li>O una combinacion de ellos mediante OR</li>
	 * </ul>
	 * @return La escena cargada
	 * @see Loader, ObjectFile
	 */
	public static Scene loadScene(String url, int flag){
		ObjectFile f = new ObjectFile(flag);
		Scene s = null;
		
		java.net.URL filename = null;
		try {
			if ((url.indexOf("file:") == 0) ||
					(url.indexOf("http") == 0)) {
				filename = new java.net.URL(url);
			}
			else if (url.charAt(0) != '/') {
				filename = new java.net.URL("file:./" + url);
			}
			else {
				filename = new java.net.URL("file:" + url);
			}

			try {
				s = f.load(filename);
			}catch (java.io.FileNotFoundException e) {
				System.err.println(e);
				System.exit(1);
			}catch (ParsingErrorException e) {
				System.err.println(e);
				System.exit(1);
			}catch (IncorrectFormatException e) {
				System.err.println(e);
				System.exit(1);
			}
		}catch (java.net.MalformedURLException e) {
			System.err.println(e);
			System.exit(1);
		}
		return s;
	}
	
	/**
	 * M�todo que carga una escena llamando al metodo <code>loadScene(String url, int flag)</code> encapsulando el flag <code>ObjectFile.RESIZE</code>
	 *
	 * @see #loadScene(String, int)
	 */
	public static Scene loadScene(String url){
		return loadScene(url,0);
	}
	
	/**
	 * M�todo que carga una escena llamando al metodo <code>loadScene(String url, int flag)</code> y activa la iluminacion
	 * de todos los shapes que contiene mediante el m�todo <code>setIluminacionActiva(Shape3D, float)</code>
	 *
	 * @see #loadScene(String, int)
	 * @see #setIluminacionActiva(Shape3D, float)
	 */
	public static Scene loadScene(String url, int flag, float shininess){
		Scene s = loadScene(url,flag);
		
		Group g = s.getSceneGroup();
		for(int i= 0, numChildren = g.numChildren(); i < numChildren ; i++)
			try{
				setIluminacionActiva((Shape3D) g.getChild(i),shininess);
			}catch(ClassCastException elElementoNoEsUnShape){
			}
		return s;
	}
	
	/**
	 * M�todo que carga una escena iluminable llamando al metodo <code>loadScene(String url, int flag, float shininess)</code> encapsulando el flag <code>ObjectFile.RESIZE</code>
	 *
	 * @see #loadScene(String, int)
	 * @see #loadScene(String, int, float)
	 * @see #setIluminacionActiva(Shape3D, float)
	 */
	public static Scene loadScene(String url, float shininess){
		return loadScene( url, 0, shininess);
	}
	
	/**
	 * <p>M�todo que carga una escena utilizando <code>loadScene(String url, int flag)</code> y fusiona todas las 
	 * geometrias de todos los shapes que lo forman en un �nico shape.
	 * </p><p><u>Notas</u>:</p><p>
	 * <ul>
	 * <li>El shape creado no tiene apariencia. Por tanto habr� que asignarsela despu�s.</li>
	 * <li>Este m�todo es recomendable para cuando todos los objetos que forman la escena que contiene el fichero 
	 * van a tener la misma apariencia. Ya que el arbol de escena ser� mas corto con un �nico saphe donde todas las
	 * geometrias tienen la misma apariencia, que con una escena donde cada shape tiene su propia apariencia igual
	 * a las dem�s.</li>  
	 * </ul>
	 * 
	 * @return Shape creado.
	 * 
	 * @see #loadScene(String, int)
	 */
	public static Shape3D loadShape(String url, int flag, Transform3D transform){
		Shape3D s = null;

		java.net.URL filename = null;
		try {
			if ((url.indexOf("file:") == 0) ||
					(url.indexOf("http") == 0)) {
				filename = new java.net.URL(url);
			}
			else if (url.charAt(0) != '/') {
				filename = new java.net.URL("file:./" + url);
			}
			else {
				filename = new java.net.URL("file:" + url);
			}

			try {
				s = ObjLoader.load(filename,flag,transform);
			}catch (java.io.IOException e) {
				System.err.println(e);
				System.exit(1);
			}catch (ObjLoader.ParsingErrorException e) {
				System.err.println(e);
				System.exit(1);
			}
		}catch (java.net.MalformedURLException e) {
			System.err.println(e);
			System.exit(1);
		}
		return s;
	}
	
	/**
	 * M�todo que carga un shape llamando al metodo <code>loadShape(String url, int flag)</code> encapsulando el flag <code>ObjectFile.RESIZE</code>
	 * 
	 * @return Shape creado.
	 * 
	 * @see #loadScene(String, int)
	 * @see #loadShape(String, int)
	 */
	public static Shape3D loadShape(String url){
		return loadShape(url,0,null);
	}
	
	public static Shape3D loadShape(String url, int flag, Transform3D transform, float shininess){
		Shape3D s = loadShape(url,flag,transform);
		setIluminacionActiva(s, shininess);
		return s;
	}
	
	/**
	 * M�todo que carga un shape iluminable llamando al metodo <code>loadShape(String url, int flag)</code>
	 * y activa la iluminacion de dicho shape mediante el m�todo 
	 * <code>setIluminacionActiva(Shape3D aShape, float shininess)</code>
	 * </p><p><u>Nota</u>:</p><p>
	 * <ul>
	 * <li>Como el shape cargado no tiene apariencia. Tendr� la apariencia por defecto creada por el m�todo
	 * <code>setIluminacionActiva</code>
	 * </ul>
	 *
	 * @return Shape iluminable creado.
	 * 
	 * @see #loadScene(String, int)
	 * @see #loadShape(String, int)
	 * @see #setIluminacionActiva(Shape3D, float)
	 */
	public static Shape3D loadShape(String url, int flag, float shininess){
		return loadShape(url,flag,null, shininess);
	}
	
	/**
	 * M�todo que carga un shape iluminable llamando al metodo <code>loadShape(String url, int flag, float shininess)</code> encapsulando el flag <code>ObjectFile.RESIZE</code>
	 *
	 * @return Shape iluminable creado.
	 * 
	 * @see #loadScene(String, int)
	 * @see #loadShape(String, int)
	 * @see #loadShape(String, int, float)
	 * @see #setIluminacionActiva(Shape3D, float)
	 */
	public static Shape3D loadShape(String url, float shininess){
		return loadShape( url, 0, null, shininess);
	}

	/**
	 * M�todo que mueve todos los Shapes3D de una escena en un SharedGroup.
	 * @param scene Scene de donde origen los Shapes3D
	 * @return Un nuevo SharedGroup que contine todos los Shapes3D de la escena.
	 */
	public static SharedGroup toSharedGroup(Scene scene){
		Group g  = scene.getSceneGroup();
		SharedGroup sg= new SharedGroup();
		
		Enumeration<?> en = g.getAllChildren();
		while(en.hasMoreElements()){
			try{
				Shape3D s = (Shape3D)en.nextElement();
				g.removeChild(s);
				sg.addChild(s);
			}catch(ClassCastException elNodoNoEsUnShape){
			}
		}
		return sg;
	}
	
	/**
	 * M�todo crea un SharedGroup que contiene el Shape3D que se quiere compartir.
	 * @param shape Shape3D que se quiere compartir
	 * @return Un nuevo SharedGroup que contine todos el Shapes3D.
	 */
	public static SharedGroup toSharedGroup(Shape3D shape){
		SharedGroup sg= new SharedGroup();
		sg.addChild(shape);
		return sg;
	}
	
	/**
	 * <p>
	 * Activa la iluminacion del shape, del siguiente modo:
	 * </p><ul>
	 * <li>Si tiene apariencia, la cambiar�, y si no la crea.</li>
	 * <li><p>Si la apariencia tiene definido un material, activar� la iluminacion, y establecer� el valor shininess.
	 * </p><p>Si no tiene material, crear� uno nuevo con:</li> <ul>
	 * <li>un ambient color {0,0,0}</li>
	 * <li> un diffuse color {0.5,0.5,0.5}</li>
	 * <li> un specular color {0.5,0.5,0.5}</li>
	 * <li> un emisive color {0,0,0}</li>
	 * </ul>
	 * y como anteriormente activar� la iluminacion, y establecer� el valor shininess.</p>
	 * <li><p>Si la apariencia tiene definidos los TextureAttributes cambiar� el modo a <i>TextureAttributes.MODULATE</i>
	 * </p><p>Si no tiene TextureAttributes, crear� uno nuevo y establecer� el modo a <i>TextureAttributes.MODULATE</i></p>
	 * </li></ul>
	 * 
	 * @param aShape Shape del que se va a activar la iluminacion.
	 * @param shininess Valor shininess que se le va a dar.
	 */
	public static void setIluminacionActiva(Shape3D aShape, float shininess){
		Appearance ap = aShape.getAppearance();
		if (ap == null){
			ap = new Appearance();
			aShape.setAppearance(ap);
		} 
		try{
			Material ma = ap.getMaterial();
			ma.setShininess(shininess);
			ma.setLightingEnable(true);
		}catch(NullPointerException laFormaNoTieneMaterial){
			Material ma = new Material();
			ma.setAmbientColor(0.0f,0.0f,0.0f);
			ma.setDiffuseColor(1.0f,1.0f,1.0f);
			ma.setSpecularColor(0.5f,0.5f,0.5f);
			ma.setEmissiveColor(0.0f,0.0f,0.0f);
			ma.setShininess(shininess);
			ma.setLightingEnable(true);
			ap.setMaterial(ma);
		}
		try{
			ap.getTextureAttributes().setTextureMode(TextureAttributes.MODULATE);
		}catch(NullPointerException laFormaNoTieneTextureAttributes){
			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.MODULATE);
			ap.setTextureAttributes(ta);
		}
	}
	
	/**
	 * <p>
	 * Cambia el textCoordSetMap de todas las geometrias de un shape.
	 * </p><p>
	 * El textCoordSetMap, es un conjunto donde se establecen, las coordenas de textura que se deben emplear cuando
	 * hay varios niveles de texturas, por defecto es {0}, esto quiere decir que las texturas del primer nivel utilizaran
	 * las coordenadas de textura 0, y las demas no usan texture coordinate set.
	 * </p><p>
	 * Este metodo es necesario para el bump maping, ya que la textura de luz, aunque tiene sus propias coordenasdas de
	 * textura, necesita utilizar las coordenadas de textura del bump-map, para calcular el Dot3D.
	 * 
	 * @param sh Shape del que se va a cambiar el TextureCoordSetMap
	 * @param coordSetMap nuevo Conjunto de las coordenadas de textura de una geometria.
	 */
	public static void setTexCoordSetMap(Shape3D sh, int [] coordSetMap){
		for(int i = 0, numGeometries = sh.numGeometries(); i < numGeometries; i++){
			Geometry g = sh.getGeometry(i);
			try{
				GeometryInfo gi = new GeometryInfo((GeometryArray)g);
				gi.setTexCoordSetMap(coordSetMap);
				sh.setGeometry(gi.getGeometryArray(),i);
			}catch(ClassCastException ignorada){
				// La geometria no es GeometryArray
			}catch(IllegalArgumentException ignorada){
				// No se ha podido obtener el nuevo GeometryArray
			}
		}
	}
	
	/**
	 * <p>
	 * Crea un TextureCubeMap, a partir de las seis imagenes que forman el cubo.
	 * </p><p>
	 * Estas imagenes tendran un nombre que acabara en los sufijos, _posx, _negx, _posy, _negy, _posz o _negz.
	 * </p><p><u>Por ejemplo</u>:</p><p>
	 * Si en el directorio <i>./recursos/texturas/skycube</i> estan las texturas
	 * <ul>
	 * <li>sky_posx.jpg</li>
	 * <li>sky_negx.jpg</li>
	 * <li>sky_posy.jpg</li>
	 * <li>sky_negy.jpg</li>
	 * <li>sky_posz.jpg</li>
	 * <li>sky_negz.jpg</li>
	 * </ul>
	 * El parametro prefijo deber� ser <i>"/recursos/texturas/skycube/sky"</i> y el sufijo ser� <i>".jpg"</i>
	 * </p>
	 * @param prefijo Ruta y el nombre inicial de las imagenes
	 * @param sufijo Extension de las imagenes
	 * @param formato Fomato de la textura genereda y de la carga de las imagenes el modo por defecto es RGBA. 
	 * Otros valores legales son: RGBA, RGBA4, RGB5_A1, RGB, RGB4, RGB5, R3_G3_B2, LUM8_ALPHA8, LUM4_ALPHA4,
	 * LUMINANCE y ALPHA 
	 * @param ancho Ancho en pixels de las imagenes.
	 * @return TextureCubeMap creado
	 * @see Texture
	 * @see TextureCubeMap
	 * @see TextureLoader
	 */
	public static TextureCubeMap buildTextureCubeMap(String prefijo, String sufijo, String formato, int ancho){

        int textureFormat= Texture.RGBA;
        
        if (formato.equals("RGBA4")) {                                  
            textureFormat = Texture.RGBA;
        } else if (formato.equals("RGB5_A1")) {                                 
            textureFormat = Texture.RGBA;
        } else if (formato.equals("RGB")) { 
            textureFormat = Texture.RGB;
        } else if (formato.equals("RGB4")) {
            textureFormat = Texture.RGB;
        } else if (formato.equals("RGB5")) {                                  
            textureFormat = Texture.RGB;
        } else if (formato.equals("R3_G3_B2")) {                              
            textureFormat = Texture.RGB;
         } else if (formato.equals("LUM8_ALPHA8")) {
            textureFormat = Texture.LUMINANCE_ALPHA;
        } else if (formato.equals("LUM4_ALPHA4")) {
            textureFormat = Texture.LUMINANCE_ALPHA;
         } else if (formato.equals("LUMINANCE")) {
            textureFormat = Texture.LUMINANCE;
         } else if (formato.equals("ALPHA")) {
            textureFormat = Texture.ALPHA;
        }
		
		TextureCubeMap tcm = new TextureCubeMap(Texture.BASE_LEVEL,textureFormat,ancho);
		tcm.setMinFilter(Texture.BASE_LEVEL_LINEAR);
		tcm.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		tcm.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
		tcm.setBoundaryModeT(Texture.CLAMP_TO_EDGE);

		try{
			tcm.setImage(0,TextureCubeMap.POSITIVE_X, new TextureLoader(prefijo + "_posx"+sufijo, null).getImage());
			tcm.setImage(0,TextureCubeMap.NEGATIVE_X, new TextureLoader(prefijo + "_negx"+sufijo, null).getImage());
			tcm.setImage(0,TextureCubeMap.POSITIVE_Y, new TextureLoader(prefijo + "_posy"+sufijo, null).getImage());
			tcm.setImage(0,TextureCubeMap.NEGATIVE_Y, new TextureLoader(prefijo + "_negy"+sufijo, null).getImage());
			tcm.setImage(0,TextureCubeMap.POSITIVE_Z, new TextureLoader(prefijo + "_posz"+sufijo, null).getImage());
			tcm.setImage(0,TextureCubeMap.NEGATIVE_Z, new TextureLoader(prefijo + "_negz"+sufijo, null).getImage());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	    return tcm;
	}
	
	/**
	 * Aplica una textura unica a la apariencia del shape en modo </i>MODULATE</i>. En caso de que no tenga apariencia, crea una nueva. 
	 * @param aShape Shape al que se le aplica la textura
	 * @param cTexture Textura de color a aplicar
	 */
	public static void setMapaDeColores(Shape3D aShape, Texture cTexture){
		
		Appearance ap = aShape.getAppearance();
		if(ap == null){
			ap = new Appearance();
			aShape.setAppearance(ap);
		}
		
		ap.setTexture(cTexture);
		
		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);

		ap.setTextureAttributes(ta);
	}

	/**
	 * Aplica una textura de entorno, mediante multitexturas, para ello recupera la textura original,
	 * y la textura de entorno la aplica en modo <i>DECAL</i> con unas coordenedas de textura generadas
	 * en con los parametros <i>TexCoordGeneration.REFLECTION_MAP, TexCoordGeneration.TEXTURE_COORDINATE_2</i>
	 * @param aShape Shape al que se le aplica la textura
	 * @param eTexture Textura de entorno a aplicar
	 */
	public static void setMapaDeEntorno(Shape3D aShape,Texture eTexture){
		if(eTexture == null)
			return;
		
		Appearance ap = aShape.getAppearance();
		if(ap == null){
			ap = new Appearance();
			aShape.setAppearance(ap);
		}
		
		TextureAttributes ta;
		TextureUnitState tus[] = new TextureUnitState[2];
		
		// Se recuperan las caracteriscas de la textura para la multitextura
		tus[0] = new TextureUnitState(ap.getTexture(), ap.getTextureAttributes(), ap.getTexCoordGeneration());
		
		// Mapa de entorno
		ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.DECAL);
		
		TexCoordGeneration tcg = new TexCoordGeneration( 
				TexCoordGeneration.REFLECTION_MAP, 
				TexCoordGeneration.TEXTURE_COORDINATE_2);

		tus[1] = new TextureUnitState( eTexture, ta, tcg);

		ap.setTextureUnitState(tus);
	}

	/**
	 * Aplica una textura de entorno, mediante multitexturas, para ello recupera la textura original,
	 * y la textura de entorno la aplica en modo <i>DECAL</i> con unas coordenedas de textura generadas
	 * en con los parametros <i>TexCoordGeneration.REFLECTION_MAP, TexCoordGeneration.TEXTURE_COORDINATE_3</i>
	 * @param aShape Shape al que se le aplica la textura
	 * @param eTexture Textura de entorno a aplicar
	 */
	public static void setMapaDeEntorno(Shape3D aShape, TextureCubeMap eTexture){
		if(eTexture == null)
			return;
		
		Appearance ap = aShape.getAppearance();
		if(ap == null){
			ap = new Appearance();
			aShape.setAppearance(ap);
		}
		
		TextureAttributes ta;
		TextureUnitState tus[] = new TextureUnitState[2];
		
		// Se recuperan las caracteriscas de la textura para la multitextura
		tus[0] = new TextureUnitState(ap.getTexture(), ap.getTextureAttributes(), ap.getTexCoordGeneration());
		
		// Mapa de entorno
		ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.DECAL);
		
		TexCoordGeneration tcg = new TexCoordGeneration(
				TexCoordGeneration.REFLECTION_MAP,
				TexCoordGeneration.TEXTURE_COORDINATE_3);

		tus[1] = new TextureUnitState(eTexture,ta,tcg);

		ap.setTextureUnitState(tus);
	}
	
	/**
	 * Crea y establece el modo de Blend de un Shape3D:
	 * 
	 * @param aShape Shape al que se le aplica el modo Blend.
	 * @param blendMode Modo de dibuajdo del objeto.
	 * <ul>
	 * <li><A HREF=#MODO_ACLARAR>MODO_ACLARAR</A></li>
	 * <li><A HREF=#MODO_NORMAL>MODO_NORMAL</A></li>
	 * <li><A HREF=#MODO_OSCURECER>MODO_OSCURECER</A></li>
	 * </ul>
	 * @param transparencia Grado de transparencia del objeto comprendido entre:
	 * <ul>
	 * <li>0.0: Totalmente opaco</li>
	 * <li>1.0: Totalmente transparente</li>
	 * </ul>
	 */
	public static void setBlendMode(Shape3D aShape, int blendMode, float transparencia){
		Appearance ap = aShape.getAppearance();
		if(ap == null){
			ap = new Appearance();
			aShape.setAppearance(ap);
		}

		TransparencyAttributes traA = new TransparencyAttributes(TransparencyAttributes.BLENDED, transparencia);
		
   		switch(blendMode){
			case MODO_ACLARAR:
				traA.setSrcBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA );
				traA.setDstBlendFunction(TransparencyAttributes.BLEND_ONE);
				break;
			case MODO_NORMAL:
				traA.setSrcBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA );
				traA.setDstBlendFunction(TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA);
				break;
			case MODO_OSCURECER:
				traA.setSrcBlendFunction(TransparencyAttributes.BLEND_ZERO);
				traA.setDstBlendFunction(TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA);
				break;
			case MODO_MODULAR:
				traA.setSrcBlendFunction(TransparencyAttributes.BLEND_ZERO);
				traA.setDstBlendFunction(TransparencyAttributes.BLEND_SRC_COLOR);
//				traA.setSrcBlendFunction(TransparencyAttributes.BLEND_DST_COLOR);
//				traA.setDstBlendFunction(TransparencyAttributes.BLEND_ZERO);
				break;
		}
		ap.setTransparencyAttributes(traA);
	}
	
	/**
	 * Crea un nuevo Shape a partir del Shape aShape, donde se aplicar� el bump mapping
	 * @param aShape Shape que se copiar�.
	 * @param nTexture Mapa de normales.
	 * @param lTexture Mapa de luz.
	 * @return El Shape creado
	 */
	public static Shape3D createBumpMappingShape(Shape3D aShape, Texture nTexture, Texture lTexture){
		
		Shape3D sBump = (Shape3D)aShape.cloneNode(false);
		
		setTexCoordSetMap(sBump,new int[]{0,0});
		
		Appearance ap = new Appearance();
		
		TextureAttributes ta;
		TextureUnitState tus[] = new TextureUnitState[2];
		
		// Mapa de normales
		ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.REPLACE);
		
		tus[0] = new TextureUnitState( nTexture, ta, null);

		// Mapa de luz
		ta = new TextureAttributes();
		
		ta.setTextureMode(TextureAttributes.COMBINE);
		ta.setCombineRgbMode(TextureAttributes.COMBINE_DOT3);
		
		ta.setCombineRgbSource(0,TextureAttributes.COMBINE_PREVIOUS_TEXTURE_UNIT_STATE);
		ta.setCombineRgbSource(1,TextureAttributes.COMBINE_TEXTURE_COLOR);
		ta.setCombineRgbFunction(0,TextureAttributes.COMBINE_SRC_COLOR);
		ta.setCombineRgbFunction(1,TextureAttributes.COMBINE_SRC_COLOR);
		
		tus[1] = new TextureUnitState(lTexture, ta, new TexCoordGeneration(TexCoordGeneration.SPHERE_MAP, TexCoordGeneration.TEXTURE_COORDINATE_2) );
		
		ap.setTextureUnitState(tus);
		sBump.setAppearance(ap);
		return sBump;
	}

	/**
	 * Crea un objeto con multitexturas.
	 * 
	 * @param aShape Shape que contiene las formas a las que aplicarle las texturas
	 * @param cTexture Mapa de colores
	 * @param eTexture Mapa de entorno
	 * @param nTexture Mapa de normales
	 * @param lTexture Mapa de luces
	 * @return 
	 * <ul><li> Si el mapa de normales y el mapa de luces no son nulos, devuelve un BranchGroup
	 * con dos shapes, uno con la textura y el mapa de entorno, y otro con el bump mapping.
	 * <li> Si el mapa de normales o el mapa de luces es nulo, devuelve el shape que se ha recibido, con el
	 * mapa de colores y el mapa de entorno aplicados.
	 * </ul>
	 */
	public static Node createMultiTextureObject(Shape3D aShape, Texture cTexture, Texture eTexture, Texture nTexture, Texture lTexture){
		setMapaDeColores(aShape, cTexture);
		try{
			setMapaDeEntorno(aShape, (TextureCubeMap)eTexture);
		}catch(ClassCastException e){
			setMapaDeEntorno(aShape, eTexture);
		}
		if (nTexture == null || lTexture == null)
			return aShape;
		BranchGroup obj = new BranchGroup();
		//*
		obj.addChild(createBumpMappingShape(aShape, nTexture, lTexture));
		setBlendMode(aShape, Tools.MODO_NORMAL, 0.1f);
		obj.addChild(aShape);
		/*/
		obj.addChild(aShape);
		Shape3D bShape = createBumpMappingShape(aShape, nTexture, lTexture);
		setBlendMode(bShape, Tools.MODO_MODULAR, 0.1f);
		obj.addChild(bShape);
		//*/

		return obj;
	}
	
	private static GeometryArray generateSkyFace(int subdivisiones){
		assert(subdivisiones >= 1);
		int nVertex = subdivisiones * subdivisiones * 4;
		int att = GeometryArray.COORDINATES 
			| GeometryArray.BY_REFERENCE
			| GeometryArray.USE_NIO_BUFFER
			| GeometryArray.TEXTURE_COORDINATE_2;
		QuadArray ga = new QuadArray(nVertex, att);
		final ByteOrder order =  ByteOrder.nativeOrder();
		final int nBytesFloat = 4;
		FloatBuffer floatBuffer;

		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nVertex * 3).order(order).asFloatBuffer();
		float x=-1.0f, y, z=-1.0f, iv = 2.0f/subdivisiones;
		Vector3f v3f = new Vector3f();
		for(int i = 0 ; i < subdivisiones; i++){
			y = -1.0f;
			for(int j = 0 ; j < subdivisiones; j++){
				v3f.x = x;    v3f.y = y;    v3f.z = z;
				v3f.normalize();
				floatBuffer.put(v3f.x);floatBuffer.put(v3f.y);floatBuffer.put(v3f.z);

				v3f.x = x+iv; v3f.y = y;    v3f.z = z;
				v3f.normalize();
				floatBuffer.put(v3f.x);floatBuffer.put(v3f.y);floatBuffer.put(v3f.z);

				v3f.x = x+iv; v3f.y = y+iv; v3f.z = z;
				v3f.normalize();
				floatBuffer.put(v3f.x);floatBuffer.put(v3f.y);floatBuffer.put(v3f.z);

				v3f.x = x;    v3f.y = y+iv; v3f.z = z;
				v3f.normalize();
				floatBuffer.put(v3f.x);floatBuffer.put(v3f.y);floatBuffer.put(v3f.z);
				y += iv;
			}
			x += iv;
		}
		ga.setCoordRefBuffer(new J3DBuffer(floatBuffer));

		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nVertex * 2).order(order).asFloatBuffer();
		float s = 1.0f, is = -1.0f/subdivisiones; 
		float t, it = -1.0f/subdivisiones;
		for(int i = 0 ; i < subdivisiones; i++){
			t = 1.0f;
			for(int j = 0 ; j < subdivisiones; j++){
				floatBuffer.put(s);    floatBuffer.put(t);
				floatBuffer.put(s+is);    floatBuffer.put(t);
				floatBuffer.put(s+is); floatBuffer.put(t+it);
				floatBuffer.put(s); floatBuffer.put(t+it);
				t += it;
			}
			s += is;
		}
		ga.setTexCoordRefBuffer(0,new J3DBuffer(floatBuffer));
		return ga;
	}
	
	public static BranchGroup buildSkyBox(int subdivisiones, TextureCubeMap tcm){
		BranchGroup bg = new BranchGroup();
		Transform3D t3d, t3d2;
		TransformGroup tg;
		Shape3D s;
		Geometry g = generateSkyFace(subdivisiones);
		Appearance ap;
		Texture t;
		
//		PolygonAttributes pa = new PolygonAttributes();
//		pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		
		t = new Texture2D( tcm.getMipMapMode(), tcm.getFormat(), tcm.getWidth(), tcm.getHeight());
		t.setImages(tcm.getImages(TextureCubeMap.NEGATIVE_Z));
		t.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
		t.setBoundaryModeT(Texture.CLAMP_TO_EDGE);
//		t.setMinFilter(Texture.MULTI_LEVEL_LINEAR);
		t.setMagFilter(Texture.BASE_LEVEL_LINEAR);

		ap = new Appearance();
		ap.setTexture(t);
//		ap.setPolygonAttributes(pa);
		s = new Shape3D(g,ap);
		bg.addChild(s);

		t = new Texture2D( tcm.getMipMapMode(), tcm.getFormat(), tcm.getWidth(), tcm.getHeight());
		t.setImages(tcm.getImages(TextureCubeMap.POSITIVE_X));
		t.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
		t.setBoundaryModeT(Texture.CLAMP_TO_EDGE);
//		t.setMinFilter(Texture.MULTI_LEVEL_LINEAR);
		t.setMagFilter(Texture.BASE_LEVEL_LINEAR);

		ap = new Appearance();
		ap.setTexture(t);
//		ap.setPolygonAttributes(pa);
		s = new Shape3D(g,ap);
		t3d = new Transform3D();
		t3d.rotY(-Math.PI/2);
		tg = new TransformGroup(t3d);
		tg.addChild(s);
		bg.addChild(tg);
		
		t = new Texture2D( tcm.getMipMapMode(), tcm.getFormat(), tcm.getWidth(), tcm.getHeight());
		t.setImages(tcm.getImages(TextureCubeMap.POSITIVE_Z));
		t.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
		t.setBoundaryModeT(Texture.CLAMP_TO_EDGE);
//		t.setMinFilter(Texture.MULTI_LEVEL_LINEAR);
		t.setMagFilter(Texture.BASE_LEVEL_LINEAR);

		ap = new Appearance();
		ap.setTexture(t);
//		ap.setPolygonAttributes(pa);
		s = new Shape3D(g,ap);
		t3d = new Transform3D();
		t3d.rotY(Math.PI);
		tg = new TransformGroup(t3d);
		tg.addChild(s);
		bg.addChild(tg);

		t = new Texture2D( tcm.getMipMapMode(), tcm.getFormat(), tcm.getWidth(), tcm.getHeight());
		t.setImages(tcm.getImages(TextureCubeMap.NEGATIVE_X));
		t.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
		t.setBoundaryModeT(Texture.CLAMP_TO_EDGE);
//		t.setMinFilter(Texture.MULTI_LEVEL_LINEAR);
		t.setMagFilter(Texture.BASE_LEVEL_LINEAR);

		ap = new Appearance();
		ap.setTexture(t);
//		ap.setPolygonAttributes(pa);
		s = new Shape3D(g,ap);
		t3d = new Transform3D();
		t3d.rotY(Math.PI/2);
		tg = new TransformGroup(t3d);
		tg.addChild(s);
		bg.addChild(tg);

		t3d2 = new Transform3D();
		t3d2.rotY(-Math.PI);
		
		t = new Texture2D( tcm.getMipMapMode(), tcm.getFormat(), tcm.getWidth(), tcm.getHeight());
		t.setImages(tcm.getImages(TextureCubeMap.NEGATIVE_Y));
		t.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
		t.setBoundaryModeT(Texture.CLAMP_TO_EDGE);
//		t.setMinFilter(Texture.MULTI_LEVEL_LINEAR);
		t.setMagFilter(Texture.BASE_LEVEL_LINEAR);

		ap = new Appearance();
		ap.setTexture(t);
//		ap.setPolygonAttributes(pa);
		s = new Shape3D(g,ap);
		t3d = new Transform3D();
		t3d.rotX(Math.PI/2);
		t3d.mul(t3d2);
		
		tg = new TransformGroup(t3d);
		tg.addChild(s);
		bg.addChild(tg);

		t = new Texture2D( tcm.getMipMapMode(), tcm.getFormat(), tcm.getWidth(), tcm.getHeight());
		t.setImages(tcm.getImages(TextureCubeMap.POSITIVE_Y));
		t.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
		t.setBoundaryModeT(Texture.CLAMP_TO_EDGE);
//		t.setMinFilter(Texture.MULTI_LEVEL_LINEAR);
		t.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		ap = new Appearance();
		ap.setTexture(t);
//		ap.setPolygonAttributes(pa);
		s = new Shape3D(g,ap);
		t3d = new Transform3D();
		t3d.rotX(-Math.PI/2);
		t3d.mul(t3d2);
		tg = new TransformGroup(t3d);
		tg.addChild(s);
		bg.addChild(tg);
		
		return bg;
	}
}