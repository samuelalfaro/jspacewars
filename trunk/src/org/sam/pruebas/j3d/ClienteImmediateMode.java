package org.sam.pruebas.j3d;

import java.util.Random;

import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import org.sam.j3d.*;
import org.sam.j3d.particulas.*;
import org.sam.util.Modificador;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

@SuppressWarnings("serial")
public class ClienteImmediateMode extends JFrame implements Runnable {

	private boolean pintado = true;
	
	private Canvas3D canvas;
	private GraphicsContext3D gc3d = null;
	
	/**
	 * Creates new form PureImmediate
	 */
	public ClienteImmediateMode() {
		createUniverse();
		loadObjects();
		
		setTitle(Messages.getString("PruebaImmediateMode.1"));
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		canvas.setPreferredSize(new java.awt.Dimension(250, 250));
		getContentPane().add(canvas, java.awt.BorderLayout.CENTER);
		pack();
		
		new Thread(this).start();
	}
	
	private void createUniverse() {
		// Create a Canvas3D using the preferred configuration
		canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		gc3d = canvas.getGraphicsContext3D();
		canvas.stopRenderer();
		SimpleUniverse univ = new SimpleUniverse(canvas);
		
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
	}
	
	private PointLight pointLight;
	private Shape3D nave = null;
	private Geometry geometriaNave = null;
	private Appearance naveAppearance= null;
	private Particulas estrellas;
	private Modificador modificador;
	
	private void loadObjects() {
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
		
		FactoriaDeParticulas.setShadersActivos(false);
		estrellas = FactoriaDeParticulas.createParticulas(20);
		estrellas.setModoDeEmision(Particulas.EMISION_CONTINUA);
		estrellas.setRangoDeEmision(1.0f);
		estrellas.iniciar();
		
		Transform3D tEmisor = new Transform3D();
		tEmisor.rotX(Math.PI/2);
		tEmisor.setScale(new Vector3d(-1.0,1.0,1.0));
		tEmisor.setTranslation(new Vector3d(0.8,0.0,-0.999999999999));
		
		estrellas.setEmisor(new EmisorCache(new EmisorLineal(1.2f,0.0f),tEmisor,256));
		estrellas.setTiempoVida(2.0f);
		estrellas.setVelocidad(0.7f,0.1f,false);

		estrellas.setEscala(0.01f);
		estrellas.setColor(1.0f,1.0f,1.0f,1.0f);
		Texture texture_particle = Tools.loadTexture("resources/obj3d/texturas/spark.jpg","ALPHA");
		texture_particle.setMinFilter(Texture.FASTEST);
		texture_particle.setMagFilter(Texture.FASTEST);
		texture_particle.setBoundaryModeS(Texture.CLAMP);
		texture_particle.setBoundaryModeT(Texture.CLAMP);
		estrellas.setTexture(texture_particle);
		Tools.setBlendMode(estrellas, Tools.MODO_ACLARAR, 0.5f);
		modificador = estrellas.getModificador();
	}
	
	static Random random = new Random();
	private Transform3D cmt = new Transform3D();

	public void render() {
		// Render the geometry for this frame
		gc3d.clear();
		
		cmt.setIdentity();
		gc3d.setModelTransform(cmt);
		Appearance app = estrellas.getAppearance();
		if (app == null)
			System.out.println("No tiene apariencia");
		else
			gc3d.setAppearance(app);
		gc3d.draw(estrellas.getGeometry());
		
		cmt.setIdentity();
		gc3d.setModelTransform(cmt);
		gc3d.addLight(pointLight);

		gc3d.setAppearance(naveAppearance);
		
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
	
	private long tAnterior;
	
	public void actua(){
		while (true) {
			while(!pintado)
				Thread.yield();
			long tActual = System.nanoTime();
			float steep = (float)((tActual - tAnterior)/1.0e9);
			modificador.modificar(steep);
			tAnterior = tActual;
			pintado = false;
			Thread.yield();
		}
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		final ClienteImmediateMode frame =new ClienteImmediateMode();
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