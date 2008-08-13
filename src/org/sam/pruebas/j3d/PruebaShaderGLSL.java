package org.sam.pruebas.j3d;

import java.io.IOException;
import java.util.Enumeration;

import javax.media.j3d.*;
import javax.vecmath.*;

import org.sam.j3d.Tools;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.shader.StringIO;

public class PruebaShaderGLSL extends PantallaPruebas{
	
	private static final long serialVersionUID = 1L;
	
	public BranchGroup createSceneGraph(Bounds bounds) {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();
		
		// Set up the background
		Background bgNode = new Background(new Color3f(0.05f, 0.05f, 0.5f));
		bgNode.setApplicationBounds(bounds);
		objRoot.addChild(bgNode);
		
		Color3f color;
		Point3f posicion;
		Point3f atenuacion;
		Vector3f direccion;
		float anguloApertura;
		float concentracion; 
		
		// Set up the ambient light
		Light light;
		
//		color = new Color3f(0.5f, 0.5f, 0.5f);
//		light = new AmbientLight(color);
//		light.setInfluencingBounds(bounds);
//		objRoot.addChild(light);
	
		/*
		color = new Color3f(0.7f, 0.7f, 0.7f);
		direccion = new Vector3f(-1.0f, -1.0f, -1.0f);
		direccion.normalize();
		light = new DirectionalLight(
				color,
				direccion);
		/*/
		color = new Color3f(0.5f, 0.5f, 0.7f);
		posicion = new Point3f(0.0f, 5.0f, 5.0f);
		atenuacion = new Point3f(0.0f, 0.1f, 0.0f);
		direccion = new Vector3f(0.0f,-1.0f,-1.0f);
		direccion.normalize();
		anguloApertura = (float)(Math.PI/3);
		concentracion = 128.0f; 
		light = new PointLight(
				color,
				posicion,
				atenuacion);
		//*/
		light.setEnable(true);
		light.setInfluencingBounds(bounds);
		objRoot.addChild(light);

		color = new Color3f(0.6f, 0.5f, 0.4f);
		posicion = new Point3f(0.0f,-5.0f,-5.0f);
		atenuacion = new Point3f(0.1f, 0.1f, 0.0f);
		direccion = new Vector3f(0.0f,1.0f, 1.0f);
		direccion.normalize();
		anguloApertura = (float)(Math.PI/3);
		concentracion = 128.0f; 

		light = new SpotLight(
				color,
				posicion,
				atenuacion,
				direccion,
				anguloApertura,
				concentracion);
		light.setEnable(true);
		light.setInfluencingBounds(bounds);
		objRoot.addChild(light);
		
		// Variables para las transformaciones.
		Transform3D t3d;
		TransformGroup rGroup, tGroup;
		
		// Se crea el grupo de rotacion y el interpolador.
		rGroup = new TransformGroup();
		rGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		
		Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
				0, 0,
				4000, 0, 0,
				0, 0, 0);
		
		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, rGroup, new Transform3D(),
				0.0f, (float) Math.PI*2.0f);
		rotator.setSchedulingBounds(bounds);

		objRoot.addChild(rGroup);
		objRoot.addChild(rotator);

		// Se a�aden los distintos objetos
		// Objeto con el shader.
		Scene s = Tools.loadScene("resources/obj3d/formas/nave04/forma.obj",20.0f);
		//Shape3D shape = Tools.loadShape("resources/obj3d/formas/nave04/forma.obj",20.0f);
		try {
			Shader[] shaders = new Shader[2];
			shaders[0] = new SourceCodeShader(
					Shader.SHADING_LANGUAGE_GLSL,
					Shader.SHADER_TYPE_VERTEX,
					StringIO.readFully("resources/obj3d/shaders/phong.vert"));


			shaders[1] = new SourceCodeShader(
					Shader.SHADING_LANGUAGE_GLSL,
					Shader.SHADER_TYPE_FRAGMENT,
					StringIO.readFully("resources/obj3d/shaders/phong.frag"));


			ShaderProgram shaderProgram = new GLSLShaderProgram();
			
			shaderProgram.setShaders(shaders);

			setShaderProgram(s.getSceneGroup(), shaderProgram, null);
			//setShaderProgram( shape, shaderProgram);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		rGroup.addChild(s.getSceneGroup());
		//rGroup.addChild(shape);
		
		// Escena sin nada
		t3d = new Transform3D();
		t3d.setTranslation(new Vector3f(0,1,0));
		tGroup = new TransformGroup(t3d);
		tGroup.addChild(Tools.loadScene("resources/obj3d/formas/nave04/forma.obj",20.0f).getSceneGroup());
		rGroup.addChild(tGroup);		
		
		// Shape sin nada
		t3d = new Transform3D();
		t3d.setTranslation(new Vector3f(0,-1,0));
		tGroup = new TransformGroup(t3d);
		tGroup.addChild(Tools.loadShape("resources/obj3d/formas/nave04/forma.obj",20.0f));
		rGroup.addChild(tGroup);
		
		objRoot.compile();
		return objRoot;
	}
	
	// Recursively set shader program for all children of specified group
	private void setShaderProgram(Group g, ShaderProgram shaderProgram, ShaderAttributeSet shaderAttributeSet) {
		Enumeration<?> e = g.getAllChildren();
		while (e.hasMoreElements()) {
			Node n = (Node)(e.nextElement());
			if (n instanceof Group) {
				setShaderProgram((Group)n, shaderProgram, shaderAttributeSet);
			}
			else if (n instanceof Shape3D) {
				setShaderProgram((Shape3D)n,shaderProgram, shaderAttributeSet);
			}
		}
	}
	
	private void setShaderProgram(Shape3D s, ShaderProgram shaderProgram, ShaderAttributeSet shaderAttributeSet) {
		ShaderAppearance shaderApp = new ShaderAppearance();
		shaderApp.setShaderProgram(shaderProgram);
		shaderApp.setShaderAttributeSet(shaderAttributeSet);

		Appearance oldApp = s.getAppearance();
		if(oldApp != null){
			shaderApp.setRenderingAttributes(oldApp.getRenderingAttributes());
			shaderApp.setPointAttributes(oldApp.getPointAttributes());
			shaderApp.setLineAttributes(oldApp.getLineAttributes());
			shaderApp.setPolygonAttributes(oldApp.getPolygonAttributes());
			shaderApp.setTexture(oldApp.getTexture());
			shaderApp.setTexCoordGeneration(oldApp.getTexCoordGeneration());
			shaderApp.setTextureAttributes(oldApp.getTextureAttributes());
			shaderApp.setTextureUnitState(oldApp.getTextureUnitState());
			shaderApp.setTransparencyAttributes(oldApp.getTransparencyAttributes());
		}
		if(shaderApp.getMaterial() == null){
			Material mat = new Material();
			mat.setAmbientColor(new Color3f(0.0f, 0.0f, 0.0f));
			mat.setDiffuseColor(new Color3f(1.0f, 1.0f, 1.0f));
			mat.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
			mat.setEmissiveColor(new Color3f(0.0f, 0.0f, 0.0f));
			mat.setShininess(20.0f);				
			shaderApp.setMaterial(mat);
		}
		s.setAppearance(shaderApp);
	}
}