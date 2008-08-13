package org.sam.j3d.particulas;

import java.io.IOException;
import java.nio.*;
import java.util.Random;

import javax.media.j3d.*;
import javax.vecmath.*;

import org.sam.interpoladores.Getter;
import org.sam.util.Modificador;

import com.sun.j3d.utils.shader.StringIO;

class ParticulasConShaders extends Particulas{

	private final static Integer FALSE = new Integer(0);
	private final static Integer TRUE = new Integer(1);
	
	private static final int[] taSets = { 0 };
	private static final int[] vertexAttrSizes = { 3, 1, 1 };
	private static final String[] vertexAttrNames = { 
		"velocidad",
		"giro", 
		"vida",
	}; 
	private static final String[] shaderAttrNames = {
		"fuerza",
		"escalaInterpolada",
		"escalas",
		"colorInterpolado",
		"colores",
		"textura"
	};
	
	private static ShaderProgram shaderProgramOverlay = null;
	private static ShaderProgram shaderProgramModulate = null;
	
	static{
		try {
			Shader[] shadersOverlay  = new Shader[2];
			Shader[] shadersModulate = new Shader[2];
			shadersOverlay[0] = shadersModulate[0] = new SourceCodeShader(
					Shader.SHADING_LANGUAGE_GLSL,
					Shader.SHADER_TYPE_VERTEX,
					StringIO.readFully("resources/obj3d/shaders/particle.vert"));
			
			shadersOverlay[1] = new SourceCodeShader(
					Shader.SHADING_LANGUAGE_GLSL,
					Shader.SHADER_TYPE_FRAGMENT,
					StringIO.readFully("resources/obj3d/shaders/particle.frag"));
			
			shadersModulate[1] = new SourceCodeShader(
					Shader.SHADING_LANGUAGE_GLSL,
					Shader.SHADER_TYPE_FRAGMENT,
					StringIO.readFully("resources/obj3d/shaders/modulateParticle.frag"));
			
			shaderProgramOverlay = new GLSLShaderProgram();
			shaderProgramOverlay.setShaders(shadersOverlay);
			
			shaderProgramOverlay.setVertexAttrNames(vertexAttrNames);
			shaderProgramOverlay.setShaderAttrNames(shaderAttrNames);
			
			shaderProgramModulate = new GLSLShaderProgram();
			shaderProgramModulate.setShaders(shadersModulate);
			
			shaderProgramModulate.setVertexAttrNames(vertexAttrNames);
			shaderProgramModulate.setShaderAttrNames(shaderAttrNames);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private class Iniciador implements GeometryUpdater{
		public void updateData(Geometry geometry){
			FloatBuffer vida  = (FloatBuffer)((GeometryArray)geometry).getVertexAttrRefBuffer(2).getBuffer();
			for (int i = 0, len = vida.limit(); i < len; i++)
			    vida.put(i, -((float)(nParticulas - i)/nParticulas)*rangoDeEmision);
		}
	}
	
	private class GeneradorDeGiros implements GeometryUpdater{
		public void updateData(Geometry geometry){
			FloatBuffer giros = (FloatBuffer)((GeometryArray)geometry).getVertexAttrRefBuffer(1).getBuffer();
			for (int i = 0, len = giros.limit(); i < len; i++)
				giros.put(i,
						((pGiro != 0.0f)?
							giro + (aleatorio.nextFloat()-0.5f) * pGiro :
							giro) *
						(sentidoGiroAleatorio?
							(aleatorio.nextBoolean()?1:-1):
							1)
						);
		}
	}
	
	private static final Random aleatorio = new Random();
	
	private class ModificadorEmisionDiscreta implements GeometryUpdater, Modificador{
		private final GeometryArray geometria;
		private float iVidaT;
		
		ModificadorEmisionDiscreta(GeometryArray geometria){
			this.geometria = geometria;
			iVidaT = 0.0f;
		}
		
		public void updateData(Geometry geometry){
			GeometryArray ga = (GeometryArray)geometry;
			
			FloatBuffer pos   = (FloatBuffer)ga.getCoordRefBuffer().getBuffer();
			FloatBuffer vels  = (FloatBuffer)ga.getVertexAttrRefBuffer(0).getBuffer();
			
			FloatBuffer vidas = (FloatBuffer)ga.getVertexAttrRefBuffer(2).getBuffer();
			
			Transform3D t = getTransform();
			for (int i = 0, len = vidas.limit(); i < len; i++){
				float vida = vidas.get(i);
				if(vida < 1.0f){
					vida += iVidaT;
					vidas.put(i,vida);
					// La particula ha muerto en este intervalo
					if (vida >= 1.0f){
						particulasActivas--;
						continue;
					}
					// La particula no ha nacido en este intervalo
					if (vida<=0 || vida>iVidaT )
						continue;
					// La particula ha nacido en este intervalo					
					Vector  v = emisor.getVector();			
					t.transform(v.posicion);
					v.direccion.scale(
							((pVelocidad != 0.0f)?
								velocidad + (aleatorio.nextFloat()-0.5f) * pVelocidad :
								velocidad) *
							(sentidoVelocidadAleatorio?
								(aleatorio.nextBoolean()?1:-1):
								1)
						);
					t.transform(v.direccion);
						
					int j= i*3;
					pos.position(j); vels.position(j);
					pos.put(v.posicion.x); vels.put(v.direccion.x);
					pos.put(v.posicion.y); vels.put(v.direccion.y);
					pos.put(v.posicion.z); vels.put(v.direccion.z);
				}
			}
		}

		public boolean modificar(float steep){
			if(particulasActivas == 0){
				if(modoDeEmision == Particulas.EMISION_UNICA)
					return false;
				particulasActivas = nParticulas;
				geometria.updateData(iniciador);
			}
			iVidaT = iVida * steep;
			geometria.updateData(this);
			return true;
		}
	}
	
	private class ModificadorEmisionContinua implements GeometryUpdater, Modificador{
		private final GeometryArray geometria;
		private float iVidaT;
		
		ModificadorEmisionContinua(GeometryArray geometria){
			this.geometria = geometria;
			iVidaT = 0.0f;
		}
		
		public void updateData(Geometry geometry){
			GeometryArray ga = (GeometryArray)geometry;
			
			FloatBuffer pos   = (FloatBuffer)ga.getCoordRefBuffer().getBuffer();
			FloatBuffer vels  = (FloatBuffer)ga.getVertexAttrRefBuffer(0).getBuffer();
			FloatBuffer vidas = (FloatBuffer)ga.getVertexAttrRefBuffer(2).getBuffer();

			Transform3D t = getTransform();
			for (int i = 0, len = vidas.limit(); i < len; i++){
				float vida = vidas.get(i)+ iVidaT;
				vidas.put(i,vida);
				// La particula no ha nacido ni se ha regenerado en este intervalo
				if (vida<=0 || (vida>iVidaT && vida<1))
					continue;
				// La particula ha nacido o se ha regenerado en este intervalo
				Vector  v = emisor.getVector();
				
				if(vida>1.0f)
					vidas.put(i, vida - (float)Math.floor(vida));
				
				t.transform(v.posicion);
				v.direccion.scale(
					((pVelocidad != 0.0f)?
						velocidad + (aleatorio.nextFloat()-0.5f) * pVelocidad :
						velocidad) *
					(sentidoVelocidadAleatorio?
						(aleatorio.nextBoolean()?1:-1):
						1)
				);
				t.transform(v.direccion);
					
				int j= i*3;
				pos.position(j); vels.position(j);
				pos.put(v.posicion.x); vels.put(v.direccion.x);
				pos.put(v.posicion.y); vels.put(v.direccion.y);
				pos.put(v.posicion.z); vels.put(v.direccion.z);
			}
		}
		
		public boolean modificar(float steep){
			iVidaT = iVida * steep;
			geometria.updateData(this);
			return true;
		}
	}
	
	private final GeometryUpdater iniciador;
	
	protected ParticulasConShaders(int nParticulas){
		super(nParticulas);
		iniciador = new Iniciador();
		modificador = new ModificadorEmisionDiscreta((GeometryArray)this.getGeometry());
	
		ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();

		// Fuerza
		shaderAttributeSet.put(new ShaderAttributeValue(shaderAttrNames[0], fuerza));
		// Escala interpolada
		shaderAttributeSet.put(new ShaderAttributeValue(shaderAttrNames[1], FALSE));
		// Valores escala
		shaderAttributeSet.put(new ShaderAttributeArray(shaderAttrNames[2], escalaToArray(1.0f)));
		// Color interpolado
		shaderAttributeSet.put(new ShaderAttributeValue(shaderAttrNames[3], FALSE));
		// Valores color
		shaderAttributeSet.put(new ShaderAttributeArray(shaderAttrNames[4], colorToArray(1.0f,1.0f,1.0f,1.0f)));
		// Textura posicion
		shaderAttributeSet.put(new ShaderAttributeValue(shaderAttrNames[5], new Integer(0)));
		
		ShaderAppearance myApp = new ShaderAppearance();
		myApp.setShaderProgram(shaderProgramOverlay);
		myApp.setShaderAttributeSet(shaderAttributeSet);
		
		TextureUnitState tus[] = new TextureUnitState[1];
		tus[0] = new TextureUnitState();
		myApp.setTextureUnitState(tus);	
				
		this.setAppearance(myApp);
		
		this.setBoundsAutoCompute(false);
		this.setBounds(new BoundingSphere(new Point3d(),10.0));
	}
	
	protected ParticulasConShaders(ParticulasConShaders me){
		super(me);
		iniciador = new Iniciador();
		modificador = (me.modoDeEmision == Particulas.EMISION_CONTINUA) ?
				new ModificadorEmisionContinua( (GeometryArray)this.getGeometry() ):
				new ModificadorEmisionDiscreta( (GeometryArray)this.getGeometry() );
		this.setBoundsAutoCompute(false);
		this.setBounds(new BoundingSphere(new Point3d(),10.0));
	}
	
	protected GeometryArray crearGeometria(int nParticulas){
		
		IndexedQuadArray quadArray = new IndexedQuadArray(
				nParticulas, 
				GeometryArray.COORDINATES | 
				GeometryArray.TEXTURE_COORDINATE_2 |
				GeometryArray.VERTEX_ATTRIBUTES|
				GeometryArray.BY_REFERENCE|
				GeometryArray.USE_NIO_BUFFER,
				taSets.length, taSets,
				vertexAttrSizes.length, vertexAttrSizes,
				nParticulas*4);
		
		final ByteOrder order =  ByteOrder.nativeOrder();
		final int nBytesFloat = 4;
		FloatBuffer floatBuffer;
		
		int[] coordIndices = new int[nParticulas*4];
		for(int i=0, j=0, len = coordIndices.length; i< len; ) 
			coordIndices[i++]= coordIndices[i++] = coordIndices[i++] = coordIndices[i++] = j++;
		
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nParticulas * 3).order(order).asFloatBuffer();
		J3DBuffer posicionCoordBuff = new J3DBuffer(floatBuffer);
		quadArray.setCoordinateIndices(0,coordIndices);
		quadArray.setCoordRefBuffer(posicionCoordBuff);
		
		float[] texturaCoord = {
				-0.5f, -0.5f,
				 0.5f, -0.5f,
				 0.5f,  0.5f,
				-0.5f,  0.5f
		};
		int[] textIndices = new int[nParticulas*4];
		for(int i=0, len = textIndices.length; i< len; i++) 
			textIndices[i]= i%4;
		
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * 8).order(order).asFloatBuffer();
		floatBuffer.put(texturaCoord, 0, 8);
		J3DBuffer texturaCoordBuff = new J3DBuffer(floatBuffer);
		quadArray.setTexCoordRefBuffer(0,texturaCoordBuff);
		quadArray.setTextureCoordinateIndices(0,0,textIndices);
		
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nParticulas * 3).order(order).asFloatBuffer();
		J3DBuffer velocidadesBuff = new J3DBuffer(floatBuffer);
		quadArray.setVertexAttrRefBuffer(0, velocidadesBuff);
		quadArray.setVertexAttrIndices(0,0,coordIndices);
		
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nParticulas ).order(order).asFloatBuffer();
		J3DBuffer girosBuff = new J3DBuffer(floatBuffer);
		quadArray.setVertexAttrRefBuffer(1, girosBuff);
		quadArray.setVertexAttrIndices(1,0,coordIndices);
		
		floatBuffer = ByteBuffer.allocateDirect(nBytesFloat * nParticulas ).order(order).asFloatBuffer();
		J3DBuffer vidasBuff = new J3DBuffer(floatBuffer);
		quadArray.setVertexAttrRefBuffer(2, vidasBuff);
		quadArray.setVertexAttrIndices(2,0,coordIndices);
		
		quadArray.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
		quadArray.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
		
		return quadArray;
	}
	
	public void iniciar(){
		particulasActivas = nParticulas;
		GeometryArray ga = ((GeometryArray)this.getGeometry());
		ga.updateData(new GeneradorDeGiros());
		ga.updateData(iniciador);
	}
	
	public void setModoDeEmision(int modoDeEmision) {
		if(modoDeEmision < Particulas.EMISION_UNICA || modoDeEmision > Particulas.EMISION_CONTINUA)
			throw new IllegalArgumentException("El rango de emision debe estar comprendido entre EMISION_UNICA y EMISION_CONTINUA");
		
		if(this.modoDeEmision != modoDeEmision){
			if(modoDeEmision == Particulas.EMISION_CONTINUA)
				modificador = new ModificadorEmisionContinua( (GeometryArray)this.getGeometry() );
			else if(this.modoDeEmision == Particulas.EMISION_CONTINUA)
				modificador = new ModificadorEmisionDiscreta( (GeometryArray)this.getGeometry() );
		}
		this.modoDeEmision = modoDeEmision;
	}
	
	public void setTiempoVida(float tVida){
		super.setTiempoVida(tVida);
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[0])).setValue(fuerza);
	}
	
	public void setFuerza(Tuple3f fuerza){
		this.fuerza.set(fuerza);
		// Pasamos del dominino del tiempo al dominio de la vida.
		this.fuerza.scale(1.0f/(iVida*iVida));
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[0])).setValue(this.fuerza);
	}
	
	public void setFuerza(float x, float y, float z){
		super.setFuerza(x,y,z);
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[0])).setValue(this.fuerza);
	}
	
	private static final int TAM = 32; 
	
	public void setEscala(float escala){
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[1])).setValue(FALSE);
		((ShaderAttributeArray)shaderAttributeSet.get(shaderAttrNames[2])).setValue(escalaToArray(escala));
	}
	private static Tuple2f[] escalaToArray(float escala){
		return new Tuple2f[]{new Point2f(escala, escala)};
	}
	
	public void setEscala(float escalaX, float escalaY){
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[1])).setValue(FALSE);
		((ShaderAttributeArray)shaderAttributeSet.get(shaderAttrNames[2])).setValue(escalaToArray(escalaX, escalaY));
	}
	private static Tuple2f[] escalaToArray(float escalaX, float escalaY){
		return new Tuple2f[]{new Point2f(escalaX, escalaY)};
	}

	public void setEscala(Getter.Float<Float> iEscalas){
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[1])).setValue(TRUE);
		((ShaderAttributeArray)shaderAttributeSet.get(shaderAttrNames[2])).setValue(escalaToArray(iEscalas));
	}
	private static Tuple2f[] escalaToArray( Getter.Float<Float> iEscalas){
		Tuple2f[] esclas = new Tuple2f[TAM];
		
		for(int i= 0; i< TAM; i++){
			float val = iEscalas.get(((float)i)/(TAM-1));
			esclas[i] = new Point2f(val, val);
		}
		return esclas;
	}
	
	public void setEscala(Getter.Float<Float> iEscalaX, Getter.Float<Float> iEscalaY){
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[1])).setValue(TRUE);
		((ShaderAttributeArray)shaderAttributeSet.get(shaderAttrNames[2])).setValue(escalaToArray(iEscalaX,iEscalaY));
	}
	private static Tuple2f[] escalaToArray( Getter.Float<Float> iEscalaX, Getter.Float<Float> iEscalaY){
		Tuple2f[] esclas = new Tuple2f[TAM];
		for(int i= 0; i< TAM; i++){
			float key = ((float)i)/(TAM-1);
			esclas[i] = new Point2f(iEscalaX.get(key), iEscalaY.get(key));
		}
		return esclas;
	}

	public void setColor( Color3f color, float alfa){
		setColor(color.x, color.y, color.z, alfa);
	}
	public void setColor( float r, float g, float b, float a){
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[3])).setValue(FALSE);
		((ShaderAttributeArray)shaderAttributeSet.get(shaderAttrNames[4])).setValue(colorToArray(r,g,b,a));
	}
	private static Color4f[] colorToArray( float r, float g, float b, float a){
		return new Color4f[]{new Color4f(r, g, b, a)};
	}
	
	public void setColor( Color3f color, Getter.Float<Float> iAlfa){
		setColor(color.x, color.y, color.z, iAlfa);
	}
	public void setColor( float r, float g, float b, Getter.Float<Float> iAlfa){
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[3])).setValue(TRUE);
		((ShaderAttributeArray)shaderAttributeSet.get(shaderAttrNames[4])).setValue(colorToArray(r,g,b,iAlfa));
	}
	private static Color4f[] colorToArray( float r, float g, float b, Getter.Float<Float> iAlfa){
		Color4f[] colores = new Color4f[TAM];
		for(int i= 0; i< TAM; i++){
			float key = ((float)i)/(TAM-1);
			colores[i] = new Color4f(r,g,b,iAlfa.get(key));
		}
		return colores;
	}

	public void setColor( Getter.Float<float[]> iColores, float alfa){
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[3])).setValue(TRUE);
		((ShaderAttributeArray)shaderAttributeSet.get(shaderAttrNames[4])).setValue(colorToArray(iColores,alfa));
	}
	private static Color4f[] colorToArray( Getter.Float<float[]> iColores, float alfa){
		Color4f[] colores = new Color4f[TAM];
		for(int i= 0; i< TAM; i++){
			float key = ((float)i)/(TAM-1);
			float[] color = iColores.get(key);
			colores[i] = new Color4f(color[0],color[1],color[2],alfa);
		}
		return colores;
	}

	public void setColor( Getter.Float<float[]> iColores, Getter.Float<Float> iAlfa){
		ShaderAttributeSet shaderAttributeSet = ((ShaderAppearance)this.getAppearance()).getShaderAttributeSet();
		((ShaderAttributeValue)shaderAttributeSet.get(shaderAttrNames[3])).setValue(TRUE);
		((ShaderAttributeArray)shaderAttributeSet.get(shaderAttrNames[4])).setValue(colorToArray(iColores,iAlfa));
	}
	private static Color4f[] colorToArray( Getter.Float<float[]> iColores, Getter.Float<Float> iAlfa){
		Color4f[] colores = new Color4f[TAM];
		for(int i= 0; i< TAM; i++){
			float key = ((float)i)/(TAM-1);
			float[] color = iColores.get(key);
			colores[i] = new Color4f(color[0],color[1],color[2],iAlfa.get(key));
		}
		return colores;
	}
	
	public void setColorModulado( boolean colorModulado){
		((ShaderAppearance)this.getAppearance()).setShaderProgram(colorModulado?shaderProgramModulate:shaderProgramOverlay);
	}

	public Particulas clone(){
		return new ParticulasConShaders(this);
	}
	
	public Bounds getBounds(){
		Bounds b = super.getBounds();
		b.transform(b,getTransform());
		return b;
	}
}