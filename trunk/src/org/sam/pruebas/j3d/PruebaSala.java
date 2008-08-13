package org.sam.pruebas.j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.IOException;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GLSLShaderProgram;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shader;
import javax.media.j3d.ShaderAppearance;
import javax.media.j3d.ShaderAttribute;
import javax.media.j3d.ShaderAttributeSet;
import javax.media.j3d.ShaderAttributeValue;
import javax.media.j3d.ShaderProgram;
import javax.media.j3d.Shape3D;
import javax.media.j3d.SourceCodeShader;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JComponent;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.sam.j3d.Tools;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.shader.StringIO;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class PruebaSala extends JComponent{
	
	private static final long serialVersionUID = 1L;
	private final String urlSala = "file:./resources/obj3d/formas/sala/sala.obj";
	private static final int BOUNDSIZE = 1000;
	Canvas3D canvas3D;
	
	public PruebaSala(){
		Bounds bounds = new BoundingBox(new Point3d(-BOUNDSIZE,-BOUNDSIZE,-BOUNDSIZE), new Point3d(BOUNDSIZE,BOUNDSIZE,BOUNDSIZE));  
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);

		BranchGroup scene = createSceneGraph(bounds);
		SimpleUniverse u = new SimpleUniverse(canvas3D);

		ViewingPlatform viewingPlatform = u.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();

		OrbitBehavior orbit = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);
		
		View view = u.getViewer().getView();
		view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
		view.setBackClipDistance(100d);
		//view.setFrontClipDistance(0.010d);
		view.setFieldOfView(70*Math.PI/180);

		TransformGroup steerTG = viewingPlatform.getViewPlatformTransform();
		Transform3D t3d = new Transform3D();
		steerTG.getTransform(t3d);

		Point3d  CAMERA_EYE = new Point3d(0,30,150);
		Point3d  CAMERA_CEN = new Point3d(0,30,100);
		Vector3d CAMERA_UP = new Vector3d(0,1,0);
		
		t3d.lookAt( CAMERA_EYE, CAMERA_CEN, CAMERA_UP );
		t3d.invert();

		steerTG.setTransform(t3d);
		
		u.addBranchGraph(scene);
		
		this.setLayout(new BorderLayout());
		this.add(canvas3D, BorderLayout.CENTER);
	}
	
	public void setVisible(boolean visible){
		super.setVisible(visible);
		canvas3D.setVisible(visible);
	}
	
	private BranchGroup createSceneGraph(Bounds bounds) {
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		
 	   	PointLight pointLight;
 	   	
 	    pointLight = new PointLight();
	   	pointLight.setEnable(true);
	   	pointLight.setColor(new Color3f(0.6f,0.6f,0.6f));
	   	pointLight.setPosition(0.0f,35.0f,100.0f);
	   	pointLight.setAttenuation(0,0.01f,0);
	   	pointLight.setInfluencingBounds(bounds);
 	   	objRoot.addChild(pointLight);
  	   	objRoot.addChild(getSphere(0.0f,15.0f,130.0f,1.0f));
 	   	
 	    pointLight = new PointLight();
	   	pointLight.setEnable(true);
	   	pointLight.setColor(new Color3f(0.6f,0.6f,0.6f));
	   	pointLight.setPosition(-64.0f,15.0f,100.0f);
	   	pointLight.setAttenuation(0,0.02f,0);
	   	pointLight.setInfluencingBounds(bounds);
 	   	objRoot.addChild(pointLight);
 	   	objRoot.addChild(getSphere(-64.0f,15.0f,100.0f,1.0f));
 	   	
 	    pointLight = new PointLight();
	   	pointLight.setEnable(true);
	   	pointLight.setColor(new Color3f(0.6f,0.6f,0.6f));
	   	pointLight.setPosition(64.0f,15.0f,100.0f);
	   	pointLight.setAttenuation(0,0.02f,0);
	   	pointLight.setInfluencingBounds(bounds);
 	   	objRoot.addChild(pointLight);
 	   	objRoot.addChild(getSphere(64.0f,15.0f,100.0f,1.0f));

 		Scene sala = Tools.loadScene(urlSala,0,50f);
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
			
			final String[] shaderAttrNames = {
					"textura"
			};
			shaderProgram.setShaderAttrNames(shaderAttrNames);
			
			final Object[] shaderAttrValues = {
					new Integer(0)
			};
			
	        ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();
	        for (int i = 0; i < shaderAttrNames.length; i++) {
	            ShaderAttribute shaderAttribute =
	                    new ShaderAttributeValue(shaderAttrNames[i],  shaderAttrValues[i]);
	            shaderAttributeSet.put(shaderAttribute);
	        }

			setShaderProgram(sala.getSceneGroup(), shaderProgram, shaderAttributeSet);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
 	   
		objRoot.addChild(sala.getSceneGroup());

        return objRoot;
	}
	
	private Node getSphere(float x, float y, float z, float radio){
		Transform3D t3D = new Transform3D();
		t3D.setTranslation(new Vector3f(x,y,z));
		
		TransformGroup objTrans = new TransformGroup(t3D);
				
		Appearance app = new Appearance();
		
		Material mat =  new Material();
		float brillo = 0.2f;
		mat.setAmbientColor(0.0f,0.0f,0.0f);
		mat.setDiffuseColor(1.0f,1.0f,1.0f);
		mat.setSpecularColor(brillo,brillo,brillo);
		mat.setEmissiveColor(0.0f,0.0f,0.0f);
		mat.setShininess(brillo*10);
		mat.setLightingEnable(true);
		app.setMaterial(mat);

		Sphere obj3D = new Sphere(radio, Primitive.GENERATE_NORMALS, 40, app);

		objTrans.addChild(obj3D);
		
		return objTrans;
	}
	
	// Set shader program for all nodes in specified branch graph
	private void setShaderProgram(Group g, ShaderProgram shaderProgram,ShaderAttributeSet shaderAttributeSet) {
		ShaderAppearance myApp = new ShaderAppearance();
		myApp.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		Material mat = new Material();
		mat.setAmbientColor(new Color3f(0.0f, 0.0f, 0.0f));
		mat.setDiffuseColor(new Color3f(1.0f, 1.0f, 1.0f));
		mat.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
		mat.setEmissiveColor(new Color3f(0.0f, 0.0f, 0.0f));
		mat.setShininess(20.0f);
		myApp.setMaterial(mat);
		TextureUnitState tus[] = new TextureUnitState[3];
		tus[0] = new TextureUnitState(Tools.loadTexture("resources/obj3d/formas/sala/pasillo.jpg"),null,null);
		//tus[1] = new TextureUnitState(Tools.loadTexture("resources/obj3d/formas/nave05/bump.png"),null,null);
		//tus[2] = new TextureUnitState(Tools.loadTexture("resources/obj3d/formas/nave05/light.png"),null,null);
		myApp.setTextureUnitState(tus);
		myApp.setShaderProgram(shaderProgram);
		myApp.setShaderAttributeSet(shaderAttributeSet);
		
		setShaderProgram(g, myApp);
	}
	
	// Recursively set shader program for all children of specified group
	private void setShaderProgram(Group g, ShaderAppearance myApp) {
		Enumeration<?> e = g.getAllChildren();
		while (e.hasMoreElements()) {
			Node n = (Node)(e.nextElement());
			if (n instanceof Group) {
				setShaderProgram((Group)n, myApp);
			}
			else if (n instanceof Shape3D) {
				Shape3D s = (Shape3D)n;
				s.setAppearance(myApp);
			}
		}
	}
}