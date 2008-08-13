package org.sam.pruebas.j3d;

import javax.media.j3d.*;
import javax.vecmath.Color3f;

import org.sam.j3d.Tools;

public class PruebaMultitexturas extends PantallaPruebas{
	private static final long serialVersionUID = 1L;
	
	private final String urlNave = "resources/obj3d/formas/nave05/forma.obj";
	private final String pathColorMap = "resources/obj3d/formas/nave05/t01.jpg";
	private final String pathReflexionCube = "resources/obj3d/texturas/skycube/reflex_sala";

	
	protected BranchGroup createSceneGraph(Bounds bounds) {
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		TextureCubeMap tcm = Tools.buildTextureCubeMap(pathReflexionCube,".png","RGBA",128);
		
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
	   	pointLight.setColor(new Color3f(0.6f,0.6f,0.6f));
	   	pointLight.setPosition(0.0f,3.0f,5.0f);
	   	pointLight.setAttenuation(0,0.05f,0);
	   	pointLight.setInfluencingBounds(bounds);
     
	   	objRoot.addChild(pointLight);
	   	
		/*
		 ###################################################
		 #                        Material                 #
		 ###################################################
		 */
		Appearance ap = new Appearance();

		float shininess = 30f;
		Material mt = new Material();
		mt.setAmbientColor(0.0f,0.0f,0.0f);
		mt.setDiffuseColor(0.5f,0.5f,0.5f);
		mt.setSpecularColor(0.5f,0.5f,0.5f);
		mt.setEmissiveColor(0.0f,0.0f,0.0f);
		mt.setShininess(shininess);
		mt.setLightingEnable(true);
		mt.setCapability(Material.ALLOW_COMPONENT_WRITE);
		
		ap.setMaterial(mt);

		Shape3D shape = Tools.loadShape(urlNave);
		shape.setAppearance(ap);
		
		Tools.setMapaDeColores(shape, Tools.loadTexture(pathColorMap));
		Tools.setMapaDeEntorno(shape, tcm);
	   	
	   	
//	   	Texture cTex = new TextureLoader(pathColorMap,TextureLoader.GENERATE_MIPMAP, null).getTexture();		
//	    Texture eTex = Tools.buildTextureCubeMap(pathReflexionCube,".png","RGBA");
//	   	Texture nTex = new TextureLoader(pathNormalMap, TextureLoader.GENERATE_MIPMAP,  null).getTexture();
//	   	Texture lTex = new TextureLoader(pathLightMap, TextureLoader.GENERATE_MIPMAP, null).getTexture();
//	   	
//	   	Node obj = Tools.createMultiTextureObject(nave, cTex, eTex, nTex, lTex);
	   	trans.addChild(shape);

        objRoot.compile();
        return objRoot;
	}
}