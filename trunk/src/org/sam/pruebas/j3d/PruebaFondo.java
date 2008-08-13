package org.sam.pruebas.j3d;

import javax.media.j3d.*;

import org.sam.j3d.Tools;

import com.sun.j3d.utils.image.TextureLoader;

public class PruebaFondo extends PantallaPruebas{
	private static final long serialVersionUID = 1L;
	
	private TransformGroup crearFondo(String urlForma, String pathTextura){
		TransformGroup fondo = new TransformGroup();
		fondo.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Appearance app = new Appearance();
		TextureLoader pTexture = new TextureLoader(pathTextura,Texture.RGB, null);
		app.setTexture(pTexture.getTexture());

		Shape3D shape = Tools.loadShape(urlForma);
		shape.setAppearance(app);
		
		fondo.addChild(shape);
		return fondo;
	}
	
	protected BranchGroup createSceneGraph(Bounds bounds) {
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
    
		TransformGroup fondo = crearFondo(
			"file:./resources/obj3d/formas/cielo.obj",
			"resources/obj3d/texturas/cielo1.jpg");
		
		BranchGroup bg = new BranchGroup();
		bg.addChild(fondo);
		Background fondogiratorio =new Background();
		fondogiratorio.setGeometry(bg);
		fondogiratorio.setApplicationBounds(bounds);
		
        objRoot.addChild(fondogiratorio);

        Alpha rotAlpha = new Alpha();
        rotAlpha.setMode(Alpha.INCREASING_ENABLE);
        rotAlpha.setLoopCount(-1);
        rotAlpha.setIncreasingAlphaDuration(100000);

        RotationInterpolator rotInterp = new RotationInterpolator( rotAlpha, fondo);   
        rotInterp.setSchedulingBounds(bounds);
        objRoot.addChild(rotInterp);
        
		TransformGroup trans = new TransformGroup();
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		trans.setCapability(Node.ENABLE_PICK_REPORTING);
		objRoot.addChild(trans);
		
		trans.addChild(Tools.loadScene("file:./resources/obj3d/formas/nave01/forma.obj").getSceneGroup());
    
        objRoot.compile();
        return objRoot;
	}
}