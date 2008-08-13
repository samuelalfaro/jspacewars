package org.sam.pruebas.j3d;

import java.io.IOException;

import javax.media.j3d.*;
import javax.vecmath.Color3f;

import org.sam.j3d.Tools;

import com.sun.j3d.utils.shader.StringIO;

public class PruebaMultitexturasGLSL extends PantallaPruebas{
	private static final long serialVersionUID = 1L;
	
	private final String urlNave = "resources/obj3d/formas/nave05/forma.obj";
	private final String pathColorMap = "resources/obj3d/formas/nave05/t01.jpg";
	private final String pathReflexionCube = "resources/obj3d/texturas/skycube/planeta";
	private final String pathReflexionMap = "resources/obj3d/formas/nave05/t02.jpg";
	
	protected BranchGroup createSceneGraph(Bounds bounds) {
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		TextureCubeMap tcm = Tools.buildTextureCubeMap(pathReflexionCube,".jpg","RGB",512);
		
		Background bg = new Background(Tools.buildSkyBox(16,tcm));
		bg.setApplicationBounds(bounds);

		objRoot.addChild(bg);
		/*
		 ###################################################
		 #             Rotation Interpolator               #
		 ###################################################
		 */
		TransformGroup trans = new TransformGroup();
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(trans);
		   
		Alpha rotAlpha = new Alpha();
		rotAlpha.setMode(Alpha.INCREASING_ENABLE);
		rotAlpha.setLoopCount(-1);
		rotAlpha.setIncreasingAlphaDuration(10000);
		   
		Transform3D transf3D = new Transform3D();

		RotationInterpolator rotInterp =
		new RotationInterpolator( rotAlpha, trans, transf3D,
				0.0f, (float)Math.PI * 2.0f );
		   
		rotInterp.setSchedulingBounds(bounds);
	   	objRoot.addChild(rotInterp);
       
		/*
		 ###################################################
		 #                  Punto de Luz                   #
		 ###################################################
		 */
 	   	PointLight pointLight = new PointLight();
	   	pointLight.setEnable(true);
	   	pointLight.setColor(new Color3f(1.0f,1.0f,1.0f));
	   	pointLight.setPosition(0.0f,3.0f,5.0f);
	   	pointLight.setAttenuation(0,0.05f,0);
	   	pointLight.setInfluencingBounds(bounds);
     
	   	objRoot.addChild(pointLight);
	   	
		/*
		 ###################################################
		 #                        Apariencia               #
		 ###################################################
		 */
		Shape3D shape = Tools.loadShape(urlNave);
	   	
	   	ShaderAppearance shaderApp = new ShaderAppearance();
		float shininess = 30f;
		Material mt = new Material();
		mt.setAmbientColor(0.0f,0.0f,0.0f);
		mt.setDiffuseColor(0.5f,0.5f,0.5f);
		mt.setSpecularColor(0.5f,0.5f,0.5f);
		mt.setEmissiveColor(0.0f,0.0f,0.0f);
		mt.setShininess(shininess);
		mt.setLightingEnable(true);
		mt.setCapability(Material.ALLOW_COMPONENT_WRITE);
		
		shaderApp.setMaterial(mt);
		
		TextureUnitState tus[] = new TextureUnitState[3];
		tus[0] = new TextureUnitState(Tools.loadTexture(pathColorMap), null, null);
		tus[1] = new TextureUnitState(Tools.loadTexture(pathReflexionMap,"ALPHA"), null, null);
		tus[2] = new TextureUnitState(tcm, null, null);
		
		shaderApp.setTextureUnitState(tus);
		
		try {
			Shader[] shaders = new Shader[2];
			shaders[0] = new SourceCodeShader(
					Shader.SHADING_LANGUAGE_GLSL,
					Shader.SHADER_TYPE_VERTEX,
					StringIO.readFully("resources/obj3d/shaders/multi_texture.vert"));
			shaders[1] = new SourceCodeShader(
					Shader.SHADING_LANGUAGE_GLSL,
					Shader.SHADER_TYPE_FRAGMENT,
					StringIO.readFully("resources/obj3d/shaders/multi_texture.frag"));
			ShaderProgram shaderProgram = new GLSLShaderProgram();
			shaderProgram.setShaders(shaders);

			String[] shaderAttrNames = {
					"difuseMap",
					"reflexionMap",
					"reflexionEnv",
				};

			shaderProgram.setShaderAttrNames(shaderAttrNames);
			
			shaderApp.setShaderProgram(shaderProgram);
		
			ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();

			shaderAttributeSet.put(new ShaderAttributeValue(shaderAttrNames[0], new Integer(0)));
			shaderAttributeSet.put(new ShaderAttributeValue(shaderAttrNames[1], new Integer(1)));
			shaderAttributeSet.put(new ShaderAttributeValue(shaderAttrNames[2], new Integer(2)));
			
			shaderApp.setShaderAttributeSet(shaderAttributeSet);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		shape.setAppearance(shaderApp);
	   	trans.addChild(shape);

        objRoot.compile();
        return objRoot;
	}
}