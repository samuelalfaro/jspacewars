package org.sam.pruebas.j3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JComponent;
import javax.vecmath.Point3d;

import org.sam.j3d.Tools;

import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class PruebaCubo extends JComponent{
	private static final long serialVersionUID = 1L;
	
	private static final int BOUNDSIZE = 10;
	private static final String rutaFondo = "resources/obj3d/formas/cubo/fondo.jpg";
	private static final String urlForma = "resources/obj3d/formas/cubo/forma.obj";
	private static final String rutaTextura = "resources/obj3d/formas/cubo/luz.png";
	
	Canvas3D canvas3D;
	
	public PruebaCubo(){
		super();
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config){
			private static final long serialVersionUID = 1L;
			int r = 255, g = 128, b = 0, a = 128;
			int ir = -1, ig = 1, ib = 1, ia = 1;
			public void postRender(){
				J3DGraphics2D g2 = getGraphics2D();
				g2.setPaint(new GradientPaint(0,100,new Color(r/2,g,b,a),0,200,new Color(r,255,0,a/4),false));
				g2.fillRect(100,100,200,200);
				
				r+=ir;
				if(r== 0||r==255)
					ir=-ir;
				g+=ig;
				if(g== 0||g==255)
					ig=-ig;
				b+=ib;
				if(b== 0||b==255)
					ib=-ib;
				a+=ia;
				if(a== 128||a==255)
					ia=-ia;
				
				g2.flush(true);
			}
		};
		SimpleUniverse su = new SimpleUniverse(canvas3D);
		
		su.getViewingPlatform().setNominalViewingTransform();
		su.addBranchGraph(createSceneGraph());
		
		this.setLayout(new BorderLayout());
		this.add(canvas3D, BorderLayout.CENTER);
	}
	
	public void setVisible(boolean visible){
		super.setVisible(visible);
		canvas3D.setVisible(visible);
	}
	
	private Appearance creaApariencia(Texture t){
		Appearance ap = new Appearance();
		
		ap.setTexture(t);
		
		TextureAttributes texA = new TextureAttributes();
		texA.setTextureMode(TextureAttributes.MODULATE);
		ap.setTextureAttributes(texA);

		TransparencyAttributes traA = new TransparencyAttributes(TransparencyAttributes.BLENDED,0.0f);
		traA.setSrcBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA );
		traA.setDstBlendFunction(TransparencyAttributes.BLEND_ONE);

		ap.setTransparencyAttributes(traA);

		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		
		ap.setPolygonAttributes(pa);
		
		return ap;
	}
	
	
	private BranchGroup createSceneGraph() {
		Bounds bounds = new BoundingBox(new Point3d(-BOUNDSIZE,-BOUNDSIZE,-BOUNDSIZE), new Point3d(BOUNDSIZE,BOUNDSIZE,BOUNDSIZE));
		
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		
		Background bg = new Background(new TextureLoader(rutaFondo, this).getImage());
		bg.setApplicationBounds(bounds);
		bg.setImageScaleMode(Background.SCALE_FIT_MAX);
		
		objRoot.addChild(bg);

		TransformGroup trans = new TransformGroup();
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		trans.setCapability(Node.ENABLE_PICK_REPORTING);
		objRoot.addChild(trans);
		
		Shape3D cubo = Tools.loadShape(urlForma);
		Appearance ap = creaApariencia(new TextureLoader(rutaTextura, this).getTexture());

		cubo.setAppearance(ap);
		
		trans.addChild(cubo);

        Alpha rotAlpha = new Alpha();
        rotAlpha.setMode(Alpha.INCREASING_ENABLE);
        rotAlpha.setLoopCount(-1);
        rotAlpha.setIncreasingAlphaDuration(50000);
	
        Transform3D transf3D = new Transform3D();
        transf3D.rotX( (Math.PI / 6.0) );

        RotationInterpolator rotInterp =
        	new RotationInterpolator( rotAlpha, trans, transf3D,
        		0.0f, (float)Math.PI * 2.0f );
   
        rotInterp.setSchedulingBounds(bounds);
        objRoot.addChild(rotInterp);
    
        objRoot.compile();
        return objRoot;
	}
}