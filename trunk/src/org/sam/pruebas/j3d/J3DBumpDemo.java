package org.sam.pruebas.j3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import org.sam.j3d.Tools;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

public class J3DBumpDemo extends PantallaPruebas{
	private static final long serialVersionUID = 1L;

	private final String imageDecalPath  = "resources/earth.jpg";
	private final String imageLightPath  = "resources/light2.png";
	private final String imageNormalPath = "resources/earthDot3.jpg";
	
	public BranchGroup createSceneGraph(Bounds bounds){

		BranchGroup objRoot = new BranchGroup();
		TransformGroup objScale = new TransformGroup();
		objRoot.addChild(objScale);

		Color3f bgColor = new Color3f(0.0f, 0.05f, 0.2f);
		Background bgNode = new Background(bgColor);
		bgNode.setApplicationBounds(bounds);
		objRoot.addChild(bgNode);
		
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objScale.addChild(objTrans);

		Appearance app = new Appearance();

		app.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
		app.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
		app.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
		app.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_WRITE);

		Material mat = new Material();
		mat.setAmbientColor(0.0f,0.0f,0.0f);
		mat.setDiffuseColor(1.0f,1.0f,1.0f);
		mat.setSpecularColor(0.0f,0.0f,0.0f);
		mat.setEmissiveColor(0.0f,0.0f,0.0f);
		mat.setShininess(0.0f);
		mat.setLightingEnable(true);
		app.setMaterial(mat);

		PolygonAttributes polAtt =  new PolygonAttributes();
		polAtt.setCullFace(PolygonAttributes.CULL_BACK);
		app.setPolygonAttributes(polAtt);
		
		Texture texLight = Tools.loadTexture(imageLightPath);
		Texture texNormal = Tools.loadTexture(imageNormalPath);
		
		TextureUnitState[] textureUnitState = buildBumpTextures(texLight,texNormal);
		app.setTextureUnitState(textureUnitState);
		
		Appearance appDecal = new Appearance();
		
		appDecal.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
		appDecal.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
		appDecal.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
		appDecal.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_WRITE);

		Material matBase =  new Material();
		float brillo = 0.0f;
		
		matBase.setAmbientColor(0.0f,0.0f,0.0f);
		matBase.setDiffuseColor(1.0f,1.0f,1.0f);
		matBase.setSpecularColor(brillo,brillo,brillo);
		matBase.setEmissiveColor(0.0f,0.0f,0.0f);
		matBase.setShininess(brillo*10);
		matBase.setLightingEnable(true);
		appDecal.setMaterial(matBase);

		Texture texBase = Tools.loadTexture(imageDecalPath); 
		appDecal.setTexture(texBase);

		TextureAttributes texAtt =  new TextureAttributes();
		texAtt.setTextureMode(TextureAttributes.MODULATE);
		appDecal.setTextureAttributes(texAtt);

		TransparencyAttributes transAttD = new TransparencyAttributes();
		transAttD.setTransparencyMode(TransparencyAttributes.BLENDED);
		transAttD.setTransparency(0.70f);
		appDecal.setTransparencyAttributes(transAttD);

		Sphere obj3D = new Sphere(0.7f,
				Primitive.GENERATE_NORMALS|
				Primitive.GENERATE_TEXTURE_COORDS,
				40,
				app);

		Tools.setTexCoordSetMap(obj3D.getShape(),new int[] {0,0});

		Sphere objDecal = new Sphere(0.7f,
				Primitive.GENERATE_NORMALS|
				Primitive.GENERATE_TEXTURE_COORDS,
				40,
				appDecal);

		objTrans.addChild(obj3D);
		objTrans.addChild(objDecal);
		
		PointLight pointLight = new PointLight();
		pointLight.setEnable(true); 
		pointLight.setColor(new Color3f(0.6f,0.6f,0.6f)); 
		pointLight.setPosition(0.0f,3.0f,5.0f);
		pointLight.setAttenuation(0,0.05f,0);
		pointLight.setInfluencingBounds(bounds);
		
		objRoot.addChild(pointLight);

		return objRoot;
	}

	private TextureUnitState[] buildBumpTextures(Texture texLight, Texture texNormal){
		TextureUnitState tus[] ;
		
		TextureAttributes ta0 = new TextureAttributes();
		ta0.setTextureMode(TextureAttributes.MODULATE);
		TexCoordGeneration tcg;
		tcg = new TexCoordGeneration(TexCoordGeneration.SPHERE_MAP, TexCoordGeneration.TEXTURE_COORDINATE_2);
		
		TextureAttributes ta1 = new TextureAttributes();
		
		ta1.setTextureMode(TextureAttributes.COMBINE);
		ta1.setCombineRgbMode(TextureAttributes.COMBINE_DOT3);

		
		ta1.setCombineRgbSource(0,TextureAttributes.COMBINE_PREVIOUS_TEXTURE_UNIT_STATE);
		ta1.setCombineRgbSource(1,TextureAttributes.COMBINE_TEXTURE_COLOR);
		
		ta1.setCombineRgbFunction(0,TextureAttributes.COMBINE_SRC_COLOR);
		ta1.setCombineRgbFunction(1,TextureAttributes.COMBINE_SRC_COLOR);
		
		tus = new TextureUnitState[2];

		tus[0] = new TextureUnitState(texLight, ta0, tcg );
		tus[0].setCapability(TextureUnitState.ALLOW_STATE_WRITE);
		tus[0].setCapability(TextureUnitState.ALLOW_STATE_READ);
		
		tus[1] = new TextureUnitState(texNormal,  ta1, null);
		tus[1].setCapability(TextureUnitState.ALLOW_STATE_WRITE);
		tus[1].setCapability(TextureUnitState.ALLOW_STATE_READ);

		return tus;
   }
}

