package org.sam.j3d;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.Light;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.Getter;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.j3d.particulas.EmisorCache;
import org.sam.j3d.particulas.EmisorConico;
import org.sam.j3d.particulas.FactoriaDeParticulas;
import org.sam.j3d.particulas.Particulas;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Escena{
	private static final int BOUNDSIZE = 40;
	
	public static Component getCanvas3D(){
		Bounds bounds = new BoundingSphere(new Point3d(0,0,0), BOUNDSIZE); 
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
		
		BranchGroup scene = createSceneGraph(bounds);
		SimpleUniverse u = new SimpleUniverse(canvas3D);
		
		View view = u.getViewer().getView();
		view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
		view.setBackClipDistance(100d);
		view.setFrontClipDistance(0.010d);
		view.setFieldOfView(65*Math.PI/180);
		
		ViewingPlatform viewingPlatform = u.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();
		
		OrbitBehavior orbit = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);
		
		TransformGroup steerTG = viewingPlatform.getViewPlatformTransform();
		Transform3D t3d = new Transform3D();
		steerTG.getTransform(t3d);
		
		Point3d  CAMERA_EYE = new Point3d(0,0,20);
		Point3d  CAMERA_CEN = new Point3d(0,0,0);
		Vector3d CAMERA_UP = new Vector3d(0,1,0);
		
		t3d.lookAt( CAMERA_EYE, CAMERA_CEN, CAMERA_UP );
		t3d.invert();
		
		steerTG.setTransform(t3d);
		
		u.addBranchGraph(scene);
		
		canvas3D.setFocusable(true);
		canvas3D.requestFocus();
		canvas3D.addKeyListener( new KeyAdapter() {
			public void keyPressed(KeyEvent e){
				int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE) ||
					(keyCode == KeyEvent.VK_Q) ||
					(keyCode == KeyEvent.VK_END) ||
					((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
					System.exit(0);
				}
			}
		});
		
		return canvas3D;
	}
	
	private static BranchGroup createSceneGraph(Bounds bounds) {
		
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);

		/*
		 ###################################################
		 #     GESTORES                         #
		 ###################################################
		 */
		
		FPSBehavior fps = new FPSBehavior();
		fps.setSchedulingBounds(bounds);
		objRoot.addChild(fps);

		ActualizadorBehavior actualizador;

		Gestor gestorPrincipal = new Gestor();
		actualizador = new ActualizadorBehavior(gestorPrincipal, 0, false);
		actualizador.setSchedulingBounds(bounds);
		objRoot.addChild(actualizador);
		
		Gestor gestorDeParticulas = new Gestor();
		actualizador = new ActualizadorBehavior(gestorDeParticulas, 0, true);
		actualizador.setSchedulingBounds(bounds);
		objRoot.addChild(actualizador);

		/*
		###################################################
		#                     FONDO                       #
		###################################################
		*/
		
		System.out.println("Creando fondo...");
		/*
		Background bg =new Background();
		bg.setApplicationBounds(bounds);
		bg.setColor(new Color3f(0.3f,0.3f,0.3f));
		/*/
		Fondo fondo = new Fondo("resources/obj3d/texturas/cielo1.jpg");
		Background bg = fondo.getFondo(); 
		bg.setApplicationBounds(bounds);
		gestorPrincipal.add(fondo.getModificador());
		//*/
		
		objRoot.addChild(bg);
		System.out.println("ok");	
		
		/*
		###################################################
		#                     LUCES                       #
		###################################################
		*/
		Light light;
		boolean enable; 
		Color3f color;
		Point3f position;
		Point3f attenuation;
		Vector3f direction;

		enable = true;
		color = new Color3f(0.3f,0.3f,0.3f);
		position = new Point3f(0.0f, 0.0f, 39.0f);
		attenuation = new Point3f(0.0f, 0.02f, 0.0f);
		light = new PointLight(enable, color, position, attenuation);
		light.setInfluencingBounds(bounds);
		objRoot.addChild(light);
		
		enable = true;
		color = new Color3f(1.0f,1.0f,1.0f);
		direction = new Vector3f(-1.0f,-1.0f,-0.3f);
		direction.normalize();
		light = new DirectionalLight(enable, color, direction);
		light.setInfluencingBounds(bounds);
		objRoot.addChild(light);
		
		Getter.Float<Float> iScale1;
		Getter.Float<Float> iScale2;
		Getter.Float<Float> iAlfa;
		Texture smoke = Tools.loadTexture("resources/obj3d/texturas/smoke_particle.jpg","ALPHA");
		smoke.setMagFilter(Texture.LINEAR_SHARPEN);
		smoke.setBoundaryModeS(Texture.CLAMP);
		smoke.setBoundaryModeT(Texture.CLAMP);
		
		/*
		 ###################################################
		 #             Gestor de Enemigos                  #
		 ###################################################
		 */
		System.out.println("cargando naves...");
		Shape3D shape = null;
		Transform3D t3d;
		TransformGroup tg;
		
		ElementoMovil nave;
		t3d = new Transform3D();
		t3d.rotY(-Math.PI/2);
		t3d.setScale(3.0);
		shape = Tools.loadShape("resources/obj3d/formas/nave05/forma.obj",0,t3d,64.0f);
		Tools.setMapaDeColores(shape,Tools.loadTexture("resources/obj3d/formas/nave05/t01.jpg","RGB"));
		nave = new ElementoMovil(0x10001,Tools.toSharedGroup(shape));
		nave.setVelocidad(new Vector3f(-1.0f,0.0f,0.0f), 15.0f);
		
		Particulas humoNave1 = FactoriaDeParticulas.createParticulas(10);
		humoNave1.setEmisor(new EmisorCache(new EmisorConico(0.05f,5.0f),16));
		humoNave1.setModoDeEmision(Particulas.EMISION_CONTINUA);
		humoNave1.setRangoDeEmision(1.0f);
		humoNave1.setTiempoVida(0.05f);
		humoNave1.setVelocidad(7.0f,1.0f,false);
		
		iScale1 = GettersFactory.Float.create(
				new float[]{0.0f,0.25f,1.0f},
				new float[]{0.3f,1.0f,0.1f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		iScale2 = GettersFactory.Float.create(
				new float[]{0.0f,0.25f,1.0f},
				new float[]{0.15f,0.4f,0.05f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		humoNave1.setEscala(iScale1,iScale2);
		iAlfa   = GettersFactory.Float.create(
				new float[]{0.7f,1.0f},
				new float[]{1.0f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		humoNave1.setColor(0.25f,0.3f,0.35f, iAlfa);
		
		humoNave1.setTexture(smoke);
		Tools.setBlendMode(humoNave1,Tools.MODO_ACLARAR,0.0f);
		
		t3d = new Transform3D();
		t3d.setTranslation(new Vector3f(1.5f,-0.2f, 0.0f));
		humoNave1.setTLocal(t3d);

		nave.addParticulas(humoNave1);
		nave.compile();
		
		CacheDeElementos.addPrototipo(nave);

		t3d = new Transform3D();
		t3d.rotY(-Math.PI/2);
		t3d.setScale(2.0); 
		shape = Tools.loadShape("resources/obj3d/formas/nave01/forma.obj",0,t3d,64.0f);
		Tools.setMapaDeColores(shape,Tools.loadTexture("resources/obj3d/formas/nave01/t01.jpg","RGB"));
		nave = new ElementoMovil(0x10002,Tools.toSharedGroup(shape));
		nave.setVelocidad(new Vector3f(-1.0f,0.0f,0.0f), 20.0f);
		
		humoNave1 = humoNave1.clone();
		humoNave1.setColor(0.35f,0.25f,0.3f, iAlfa);
		t3d = new Transform3D();
		t3d.setTranslation(new Vector3f(1.0f,-0.1f, 0.0f));
		humoNave1.setTLocal(t3d);
		
		nave.addParticulas(humoNave1);
		nave.compile();
	
		CacheDeElementos.addPrototipo(nave);

		System.out.println("cargando bombardero01...");
		t3d = new Transform3D();
		t3d.rotY(Math.PI/2);
		t3d.setScale(6.0);
		shape = Tools.loadShape("resources/obj3d/formas/bomber01/forma.obj",0,t3d,64.0f);
		Tools.setMapaDeColores(shape,Tools.loadTexture("resources/obj3d/formas/bomber01/t01.jpg","RGB"));

		nave = new ElementoMovil(0x20001,Tools.toSharedGroup(shape));
		nave.setVelocidad(new Vector3f(-1f,0.0f,0.0f), 1.0f);
		nave.compile();
		CacheDeElementos.addPrototipo(nave);
		
		System.out.println("cargando bombardero02...");
		t3d = new Transform3D();
		t3d.rotY(Math.PI/2);
		t3d.setScale(10.0);
		shape = Tools.loadShape("resources/obj3d/formas/bomber02/forma.obj",0,t3d,64.0f);
		Tools.setMapaDeColores(shape,Tools.loadTexture("resources/obj3d/formas/bomber02/t01.jpg","RGB"));

		nave = new ElementoMovil(0x20002,Tools.toSharedGroup(shape));
		nave.setVelocidad(new Vector3f(-1f,0.0f,0.0f), 1.0f);
		nave.compile();
		CacheDeElementos.addPrototipo(nave);
		
		System.out.println("cargando fragata...");
		t3d = new Transform3D();
		t3d.rotY(Math.PI/2);
		t3d.setScale(25.0);
		shape = Tools.loadShape("resources/obj3d/formas/fragata/forma.obj",0,t3d,64.0f);
		Tools.setMapaDeColores(shape,Tools.loadTexture("resources/obj3d/formas/fragata/t01.jpg","RGB"));

		nave = new ElementoMovil(0x30001,Tools.toSharedGroup(shape));
		nave.setVelocidad(new Vector3f(-1f,0.0f,0.0f), 1.0f);
		nave.compile();
		CacheDeElementos.addPrototipo(nave);
	
		GestorDeMision gestorDeMision = new GestorDeMision();
		objRoot.addChild(gestorDeMision.getEnemigos().getElementos());
		gestorPrincipal.add(gestorDeMision.getEnemigos());
		gestorDeMision.iniciar();
		actualizador = new ActualizadorBehavior(gestorDeMision, 0, false);
		actualizador.setSchedulingBounds(bounds);
		objRoot.addChild(actualizador);
		
		/*
		 ###################################################
		 #                     NAVE                        #
		 ###################################################
		 */
		System.out.println("Creando nave...");
		
		Shape3D formaDisparo;
		Appearance appDisparo;
		//ElementoMovil prototipoDisparo;
		formaDisparo = new Cartel(1.0f,0.25f);
		Tools.setMapaDeColores(formaDisparo, Tools.loadTexture("resources/obj3d/texturas/disparo-tn.png"));
		Tools.setBlendMode(formaDisparo, Tools.MODO_NORMAL,0.0f);
		appDisparo = formaDisparo.getAppearance();

		CacheDeElementos.addPrototipo(new ElementoMovil(1,Tools.toSharedGroup(formaDisparo)));

		formaDisparo = new Cartel(1.5f,0.375f);
		formaDisparo.setAppearance(appDisparo);
		CacheDeElementos.addPrototipo(new ElementoMovil(2,Tools.toSharedGroup(formaDisparo)));
		
		formaDisparo = new Cartel(1.5f,0.75f);
		formaDisparo.setAppearance(appDisparo);
		CacheDeElementos.addPrototipo(new ElementoMovil(3,Tools.toSharedGroup(formaDisparo)));
		
		GestorDeElementos gestorDeDisparos = new GestorDeElementos();
		objRoot.addChild(gestorDeDisparos.getElementos());
		gestorPrincipal.add(gestorDeDisparos);
		
		Group gNave = new Group();
		
		tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		t3d = new Transform3D();
		t3d.rotY(Math.PI/2);
		t3d.setScale(2.0);
		shape = Tools.loadShape("resources/obj3d/formas/nave02/forma.obj",0,t3d,64.0f);
		Tools.setMapaDeColores(shape,Tools.loadTexture("resources/obj3d/formas/nave02/t01.jpg","RGB"));
		tg.addChild(shape);
		
		t3d = new Transform3D();
		ControlNave controlNave= new ControlNave(tg, t3d,gestorDeDisparos);
		controlNave.setSchedulingBounds(bounds);
		
		gNave.addChild(tg);
		gNave.addChild(controlNave);
		
		humoNave1 = FactoriaDeParticulas.createParticulas(10);
		humoNave1.setEmisor(new EmisorCache(new EmisorConico(0.025f,5.0f),16));
		humoNave1.setModoDeEmision(Particulas.EMISION_CONTINUA);
		humoNave1.setRangoDeEmision(1.0f);
		humoNave1.setTiempoVida(0.05f);
		humoNave1.setVelocidad(30.0f,5.0f,false);
		
		iScale1= GettersFactory.Float.create(
				new float[]{0.0f,0.35f,1.0f},
				new float[]{0.3f,0.75f,0.1f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		iScale2= GettersFactory.Float.create(
				new float[]{0.0f,0.35f,1.0f},
				new float[]{0.15f,0.5f,0.05f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		humoNave1.setEscala(iScale1,iScale2);
		iAlfa = GettersFactory.Float.create(
				new float[]{0.7f,1.0f},
				new float[]{1.0f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		humoNave1.setColor(0.45f,0.35f,0.25f, iAlfa);
		
		humoNave1.setTexture(smoke);
		Tools.setBlendMode(humoNave1,Tools.MODO_ACLARAR,0.0f);
		
		humoNave1.setTExterna(t3d);
		Particulas humoNave2 = humoNave1.clone();
		humoNave2.setTExterna(t3d);
		Particulas humoNave3 = humoNave1.clone();
		humoNave3.setTExterna(t3d);
		
		t3d = new Transform3D();
		t3d.setScale(new Vector3d(-1.0,1.0,1.0));
		t3d.setTranslation(new Vector3f(-0.65f, -0.3f, 0.3f));
		humoNave1.setTLocal(t3d);
		
		t3d = new Transform3D();
		t3d.setScale(new Vector3d(-1.0,1.0,1.0));
		t3d.setTranslation(new Vector3f(-0.65f, -0.3f, -0.3f));
		humoNave2.setTLocal(t3d);

		t3d = new Transform3D();
		t3d.setScale(new Vector3d(-1.0,1.0,1.0));
		t3d.setTranslation(new Vector3f(-0.65f, 0.15f, 0.0f ));
		humoNave3.setTLocal(t3d);
		
		gestorDeParticulas.add(humoNave1.getModificador());
		gestorDeParticulas.add(humoNave2.getModificador());
		gestorDeParticulas.add(humoNave3.getModificador());
		humoNave1.iniciar();
		humoNave2.iniciar();
		humoNave3.iniciar();
		gNave.addChild(humoNave1);
		gNave.addChild(humoNave2);
		gNave.addChild(humoNave3);
		
		objRoot.addChild(gNave);
		
		/*
		 ###################################################
		 #                    Bombardero                   #
		 ###################################################
		 */
		t3d = new Transform3D();
		t3d.setTranslation(new Vector3f(4.0f,-3.0f,0.0f));
		tg = new TransformGroup(t3d);
		tg.addChild(CacheDeElementos.newObject(0x20001));
		objRoot.addChild(tg);
		/*
		 ###################################################
		 #                    Humo                         #
		 ###################################################
		 */
		System.out.println("Creando humo...");
		
		Particulas llamas =	FactoriaDeParticulas.createParticulas(10);
		llamas.setEmisor(new EmisorCache(new EmisorConico(0.01f,5.0f),16));
		//llamas.setEmisor(new EmisorConico(0.1f,5.0f));
		llamas.setModoDeEmision(Particulas.EMISION_CONTINUA);
		llamas.setRangoDeEmision(1.0f);
		llamas.setTiempoVida(1.0f);
		llamas.setVelocidad(5.0f,1.0f,false);
		llamas.setGiro(720.0f,180.0f,true);
		iScale1= GettersFactory.Float.create(
				new float[]{0.0f,0.6f,1.0f},
				new float[]{0.5f,2.0f,1.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		llamas.setEscala(iScale1);
		iAlfa = GettersFactory.Float.create(
				new float[]{0.7f,1.0f},
				new float[]{1.0f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		llamas.setColor(0.6f,0.4f,0.2f, iAlfa);
		llamas.setTexture(smoke);
		Tools.setBlendMode(llamas, Tools.MODO_ACLARAR, 0.0f);
		
		Particulas humo = FactoriaDeParticulas.createParticulas(10);
		humo.setModoDeEmision(Particulas.EMISION_CONTINUA);
		humo.setEmisor(new EmisorCache(new EmisorConico(0.04f,3.0f),16));
		humo.setRangoDeEmision(1.0f);
		humo.setTiempoVida(2.0f);
		humo.setVelocidad(4.0f);
		humo.setGiro(120.0f,60.0f,true);
		iScale1 = GettersFactory.Float.create(
				new float[]{0.3f,1.0f},
				new float[]{2.5f,5.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		iScale2 = GettersFactory.Float.create(
				new float[]{0.3f,1.0f},
				new float[]{1.5f,3.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		humo.setEscala(iScale1,iScale2);
		iAlfa   = GettersFactory.Float.create(
				new float[]{0.0f,0.2f,0.8f,1.0f},
				new float[]{0.0f,0.5f,1.0f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		humo.setColor(0.6f,0.5f,0.45f, iAlfa);
		humo.setTexture(smoke);
		humo.setColorModulado(true);
		
		Tools.setBlendMode(humo, Tools.MODO_MODULAR, 0.0f);
		
		llamas.setTExterna(t3d);
		humo.setTExterna(t3d);
		
		t3d = new Transform3D();
		t3d.rotY(Math.PI);
		t3d.setTranslation(new Vector3f(-3.0f, 0.0f, 0.0f));
		llamas.setTLocal(t3d);
		
		t3d = new Transform3D();
		t3d.rotY(Math.PI);
		t3d.setTranslation(new Vector3f(-4.5f, 0.0f, 0.0f));
		humo.setTLocal(t3d);
		
		llamas.iniciar();
		objRoot.addChild(llamas);
		gestorDeParticulas.add(llamas.getModificador());
		humo.iniciar();
		objRoot.addChild(humo);
		gestorDeParticulas.add(humo.getModificador());
		System.out.println("ok");
		
		return objRoot;
	}
}