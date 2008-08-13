package org.sam.pruebas.j3d;

import java.util.Random;

import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import org.sam.j3d.*;
import org.sam.j3d.particulas.FactoriaDeParticulas;
import org.sam.util.Modificador;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

@SuppressWarnings("serial")
public class PruebaImmediateMode extends JFrame implements Runnable {
	private static float PI2 = (float)(Math.PI*2.0);
	
	private boolean pintado = true;
	
	private Modificador modificador;
	private SimpleUniverse univ = null;
	
	private Canvas3D canvas;
	private Background background= null;
	private GraphicsContext3D gc3d = null;
	private Shape3D[] shapesFondo;
	private Appearance estrellasAppearance;
	private Appearance naveAppearance;
	private PointLight pointLight;
	private Shape3D nave = null;
	private Geometry geometriaNave = null;
	private Transform3D cmt = new Transform3D();
	
	// One rotation (2*PI radians) every 6 second
	private float incAng = (float)(Math.PI/3.0);
	private float angle = 0;
	private long  tAnterior;
	
	/**
	 * Creates new form PureImmediate
	 */
	public PruebaImmediateMode() {
		loadObjects();
		createUniverse();
		
		setTitle(Messages.getString("PruebaImmediateMode.1"));
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		canvas.setPreferredSize(new java.awt.Dimension(250, 250));
		getContentPane().add(canvas, java.awt.BorderLayout.CENTER);
		pack();
		
		this.tAnterior = System.nanoTime();
		new Thread(this).start();
	}
	
	private void loadObjects() {
		// Set up geometry
		background = new Background();
		background.setColor(new Color3f(0.25f,0.25f,0.25f));
		
		Fondo fondo = new Fondo(Messages.getString("PruebaImmediateMode.2"));
		shapesFondo = fondo.getShapes();
		modificador = fondo.getModificador();
		
		estrellasAppearance = new Appearance();
		
		Texture texture_particle = Tools.loadTexture(Messages.getString("PruebaImmediateMode.3"),"ALPHA");
		texture_particle.setMinFilter(Texture.FASTEST);
		texture_particle.setMagFilter(Texture.FASTEST);
		texture_particle.setBoundaryModeS(Texture.CLAMP);
		texture_particle.setBoundaryModeT(Texture.CLAMP);
		
		TextureAttributes tAtt;
		tAtt = new TextureAttributes();
		tAtt.setTextureMode(TextureAttributes.MODULATE);
		estrellasAppearance.setTexture(texture_particle);
		estrellasAppearance.setTextureAttributes(tAtt);
		
//		TransparencyAttributes traA = new TransparencyAttributes(TransparencyAttributes.BLENDED, 1.0f);
//		traA.setSrcBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA );
//		traA.setDstBlendFunction(TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA);
//		estrellasAppearance.setTransparencyAttributes(traA);
		
		nave= Tools.loadShape(Messages.getString("PruebaImmediateMode.5"));
		Tools.setIluminacionActiva(nave, 64.0f);
		Tools.setMapaDeColores(nave,Tools.loadTexture(Messages.getString("PruebaImmediateMode.6"),"RGB"));
		Tools.setBlendMode(nave, Tools.MODO_NORMAL, 0.0f);
		geometriaNave = nave.getGeometry(); 
		naveAppearance = nave.getAppearance();
		PolygonAttributes pAtt = new PolygonAttributes();
		pAtt.setCullFace(PolygonAttributes.CULL_NONE);
		
		pointLight = new PointLight();
		pointLight.setEnable(true); 
		pointLight.setColor(new Color3f(0.3f,0.3f,0.3f)); 
		pointLight.setPosition(0.0f,0.0f,8.0f);
		pointLight.setAttenuation(0,0.05f,0);
	}
	
	private void createUniverse() {
		// Create a Canvas3D using the preferred configuration
		canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		gc3d = canvas.getGraphicsContext3D();
		gc3d.setBackground(background);
		canvas.stopRenderer();
		// Create simple universe with view branch
		univ = new SimpleUniverse(canvas);
		
		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		ViewingPlatform viewingPlatform = univ.getViewingPlatform();
		Transform3D t3d = new Transform3D();
		//t3d.ortho(-2.0,2.0,-2.0,2.0,-2.0,2.0);
		double fieldOfView = Math.PI/4.0;
		double viewDistance = 1.0/Math.tan(fieldOfView/2.0);
		t3d.setTranslation(new Vector3d(0.0, 0.0, viewDistance));
		viewingPlatform.getViewPlatformTransform().setTransform(t3d);
		
		View view = univ.getViewer().getView();
		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		view.setMinimumFrameCycleTime(5);
		view.setBackClipDistance(100d);
		view.setFrontClipDistance(0.010d);
		
		//univ.getViewer().getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
//		view.setWindowResizePolicy(View.VIRTUAL_WORLD);
//		view.setWindowEyepointPolicy(View.RELATIVE_TO_WINDOW);
//		view.setScreenScalePolicy(View.SCALE_EXPLICIT);
//		view.setScreenScale(1.0);
	}
	
	static Random random = new Random();
	
	public void render() {
		// Render the geometry for this frame
		gc3d.clear();
		
		cmt.setIdentity();
		gc3d.setModelTransform(cmt);
		
		gc3d.setAppearance(shapesFondo[0].getAppearance());
		gc3d.draw(shapesFondo[0].getGeometry());
		gc3d.setAppearance(estrellasAppearance);
		for(int i= 1,len =shapesFondo.length; i<len; i++)
			gc3d.draw(shapesFondo[i].getGeometry());
		
		cmt.setIdentity();
		gc3d.setModelTransform(cmt);
		gc3d.addLight(pointLight);

		gc3d.setAppearance(naveAppearance);
		
		//Perfil
		cmt.setIdentity();
		cmt.rotY(-Math.PI/2);
		cmt.setScale(0.2);
		for(int i= 0; i<10; i++)
			for(int j= 0; j<10; j++){
				cmt.setTranslation(new Vector3d(0.2*i-1.0,0.2*j-1.0,0.0));
				gc3d.setModelTransform(cmt);
				if(random.nextBoolean()&&random.nextBoolean())
					continue;
				gc3d.draw(geometriaNave);
			}
		
//		cmt.rotY(angle);
//		gc.setModelTransform(cmt);
//		for(int i=0, len= geometriasNave.length; i< len; i++)
//			gc.draw(geometriasNave[i]);
		gc3d.removeLight(0);
		canvas.swap();
	}
	
	//
	// Run method for our immediate mode rendering thread.
	//
	public void run() {
		System.out.println(Messages.getString("PruebaImmediateMode.8"));
		while (true) {
			while(pintado)
				Thread.yield();
			render();
			pintado = true;
			Thread.yield();
		}
	}
	
	int fps = 0;
	long tAcumulado = 0;
	
	public void actua(){
		while (true) {
			while(!pintado)
				Thread.yield();
			long tActual = System.nanoTime();
			float steep = (float)((tActual - tAnterior)/1.0e9);
			fps++;
			tAcumulado += (tActual - tAnterior);
			if(tAcumulado > 2e9){
				System.out.println(fps/2);
				fps = 0;
				tAcumulado -= 2e9;
			}
				
			modificador.modificar(steep);
			angle += incAng*steep;
			if(angle > PI2)
				angle -= PI2;
			tAnterior = tActual;
			pintado = false;
			Thread.yield();
		}
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		FactoriaDeParticulas.setShadersActivos(false);
		//FactoriaDeParticulas.setShadersActivos(true);
		final PruebaImmediateMode frame =new PruebaImmediateMode();
		new Thread(){
			public void run(){
				frame.actua();
			}
		}.start();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}