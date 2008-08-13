package org.sam.pruebas.j3d;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import org.sam.j3d.Tools;
import org.sam.util.Imagen;

public class PruebaTexturasPNG extends PantallaPruebas{
	private static final long serialVersionUID = 1L;
	
	protected BranchGroup createSceneGraph(Bounds bounds) {
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);

		Background bg = new Background(new ImageComponent2D(ImageComponent.FORMAT_RGBA,Imagen.toBufferedImage(Imagen.cargarImagen("resources/obj3d/texturas/cielo1.jpg"))));
		bg.setApplicationBounds(new BoundingSphere(new Point3d(),100.0));
		bg.setImageScaleMode(Background.SCALE_FIT_MAX);
		
		objRoot.addChild(bg);

		ImageComponent2D image = Tools.loadImage("resources/obj3d/texturas/explosion.png","RGBA");
		
		Raster raster = new Raster( );
		raster.setPosition( new Point3f( 2.0f, 0.0f, 0.0f ) );
		raster.setType( Raster.RASTER_COLOR );
		raster.setSrcOffset( 0, 0 );
		raster.setDstOffset( 128, 128 );
		raster.setSize( 256, 256 );
		raster.setImage( image );
		Shape3D objeto = new Shape3D( raster, new Appearance( ) );
		//objRoot.addChild( sh );
		
		//Shape3D objeto = new Cartel(1.0f,1.0f);

		Appearance ap = objeto.getAppearance();
		ColoringAttributes colA = new ColoringAttributes(0.5f,0.5f,0.5f,ColoringAttributes.SHADE_GOURAUD);
		ap.setColoringAttributes(colA);
		PolygonAttributes polA = new PolygonAttributes(PolygonAttributes.POLYGON_FILL,PolygonAttributes.CULL_NONE, 0.0f);
		ap.setPolygonAttributes(polA);
		
//		TextureAttributes ta = ap.getTextureAttributes();
//		ta.setTextureMode(TextureAttributes.COMBINE);
//		
//		ta.setCombineAlphaMode(TextureAttributes.COMBINE_REPLACE);
//			ta.setCombineAlphaSource(0,TextureAttributes.COMBINE_TEXTURE_COLOR);
//			ta.setCombineAlphaFunction(0,TextureAttributes.COMBINE_SRC_ALPHA);
//			
//		ta.setCombineRgbMode(TextureAttributes.COMBINE_ADD_SIGNED);
//			ta.setCombineRgbSource(0,TextureAttributes.COMBINE_PREVIOUS_TEXTURE_UNIT_STATE);
//			ta.setCombineRgbSource(1,TextureAttributes.COMBINE_TEXTURE_COLOR);
//			ta.setCombineRgbFunction(0,TextureAttributes.COMBINE_SRC_COLOR);
//			ta.setCombineRgbFunction(1,TextureAttributes.COMBINE_SRC_COLOR);
	
		Tools.setBlendMode(objeto, Tools.MODO_NORMAL,0.5f);
		
		TransformGroup trans = new TransformGroup();
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		trans.addChild(objeto);
		objRoot.addChild(trans);
		
        Alpha rotAlpha = new Alpha();
        rotAlpha.setMode(Alpha.INCREASING_ENABLE);
        rotAlpha.setLoopCount(-1);
        rotAlpha.setIncreasingAlphaDuration(1000);
	
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