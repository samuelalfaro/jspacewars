package org.sam.pruebas.j3d;

import javax.media.j3d.Background;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.Getter;
import org.sam.interpoladores.MetodoDeInterpolacion;
import org.sam.j3d.ActualizadorBehavior;
import org.sam.j3d.Gestor;
import org.sam.j3d.Tools;
import org.sam.j3d.particulas.EmisorCache;
import org.sam.j3d.particulas.EmisorPuntual;
import org.sam.j3d.particulas.FactoriaDeParticulas;
import org.sam.j3d.particulas.Particulas;
import org.sam.util.Modificador;

import com.sun.j3d.loaders.Scene;

@SuppressWarnings("serial")
public class PruebaParticulas extends PantallaPruebas{
	private static final boolean useShaders = true;
	
	private static class Rotor implements Modificador{
		private final double PI2 = Math.PI*2;
		private final TransformGroup tg;
		private final Transform3D target;
		private double radSeg;
		private double angulo;
		
		public Rotor(TransformGroup tg, Transform3D target){
			this.tg = tg;
			tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			this.target = target;
		}
		
		public void setVelocidadRotacion(float radSeg) {
			this.radSeg = radSeg;
		}

		public boolean modificar(float steep){
			angulo += radSeg * steep;
			if(angulo > PI2)
				angulo -= PI2;
			target.rotY(angulo);
			tg.setTransform(target);
			return true;
		}
	}
	
	private Background crearFondo(String pathForma, String pathTextura){
		Background fondo =new Background();
		
		Shape3D forma = Tools.loadShape(pathForma);
		Tools.setMapaDeColores(forma,Tools.loadTexture(pathTextura));
		
		BranchGroup bg = new BranchGroup();
		bg.addChild(forma);
		
		fondo.setGeometry(bg);
		return fondo;
	}
	
	protected BranchGroup createSceneGraph(Bounds bounds) {
		FactoriaDeParticulas.setShadersActivos(useShaders);
		
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		
		Background fondo = crearFondo("resources/obj3d/formas/cielo2.obj","resources/obj3d/texturas/cielo1024.jpg");
		fondo.setApplicationBounds(bounds);
		
		objRoot.addChild(fondo);
		
		Scene sala = Tools.loadScene("resources/obj3d/formas/sala/sala.obj",0,50f);
		Transform3D tSala = new Transform3D();
		tSala.setScale(1.0/10);
		tSala.setTranslation(new Vector3f(0,-2,-10));
		TransformGroup tgSala = new TransformGroup(tSala);
		tgSala.addChild(sala.getSceneGroup());
		objRoot.addChild(tgSala);
		
		Gestor gestor = new Gestor();
		ActualizadorBehavior actualizador = new ActualizadorBehavior(gestor,0,true);
		actualizador.setSchedulingBounds(bounds);
		objRoot.addChild(actualizador);

		Transform3D tExterna = new Transform3D();
		TransformGroup tg1 = new TransformGroup(tExterna);
		objRoot.addChild(tg1);
		Rotor rotor = new Rotor(tg1,tExterna);
		rotor.setVelocidadRotacion(1.0f);
		gestor.add(rotor);
		
		Transform3D tLocal = new Transform3D();
		//tLocal.rotZ(90*Math.PI/180);
		tLocal.setTranslation(new Vector3d(-5.0,0.0,0.0));


		TransformGroup tg2 = new TransformGroup(tLocal);
		tg1.addChild(tg2);
		
		Getter.Float<Float> iScale1; 
		Getter.Float<Float> iScale2; 
		Getter.Float<Float> iAlfa;
		Texture texture_particle;

		//FactoriaDeParticulas.setShadersActivos(true);
		
		Particulas explosion = FactoriaDeParticulas.createParticulas(100);
		explosion.setModoDeEmision(Particulas.EMISION_CONTINUA);
		explosion.setRangoDeEmision(1.0f);
		explosion.setEmisor(new EmisorCache(new EmisorPuntual(),256));
		explosion.setTiempoVida(0.25f);
		explosion.setVelocidad(5.0f,2.0f,false);
		//explosion.setFuerza(0.0f,-9.8f,0.0f);
		explosion.setGiro(360.0f,90.0f,true);
		explosion.setTExterna(tExterna);
		explosion.setTLocal(tLocal);
		
		//iScale1 = FactoriaDeInterpoladores.createInterpoladorDeValores("MODO_LINEAL");
		iScale1 = GettersFactory.Float.create(
				new float[]{0.0f,0.37f,1.0f},
				new float[]{0.5f,1.0f,4.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		iScale2 = GettersFactory.Float.create(
				new float[]{0.0f,0.37f,1.0f},
				new float[]{1.0f,1.5f,2.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		explosion.setEscala(iScale1, iScale2);
		
		iAlfa= GettersFactory.Float.create(
				new float[]{0.0f,0.8f,1.0f},
				new float[]{0.5f,1.0f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		explosion.setColor(0.4f,0.3f,0.6f, iAlfa);
		//explosion.setColor(1.0f,1.0f,1.0f,1.0f);
		//explosion.setColorModulado(false);

		texture_particle = Tools.loadTexture("resources/obj3d/texturas/spark.jpg","ALPHA");
		texture_particle.setMagFilter(Texture.LINEAR_SHARPEN);
		texture_particle.setBoundaryModeS(Texture.CLAMP);
		texture_particle.setBoundaryModeT(Texture.CLAMP);
		explosion.setTexture(texture_particle);
		Tools.setBlendMode(explosion, Tools.MODO_ACLARAR, 0.0f);
		
		explosion.iniciar();
		objRoot.addChild(explosion);
		gestor.add(explosion.getModificador());
		
		Particulas humo = FactoriaDeParticulas.createParticulas(30);
		humo.setTExterna(tExterna);
		humo.setTLocal(tLocal);
		humo.setTiempoVida(1.0f);
		humo.setRangoDeEmision(0.75f);
		
		humo.setModoDeEmision(Particulas.EMISION_CONTINUA);
		humo.setRangoDeEmision(1.0f);
		humo.setEmisor(new EmisorCache(new EmisorPuntual(),16));
		humo.setVelocidad(1.0f,0.5f,false);
		//humo.setFuerza(0.0f,-9.8f,0.0f);
		humo.setGiro(360.0f,90.0f,true);

		iScale1 = GettersFactory.Float.create(
				new float[]{0.0f,0.3f,1.0f},
				new float[]{0.1f,1.5f,5.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		humo.setEscala(iScale1);
		iAlfa= GettersFactory.Float.create(
				new float[]{0.0f,0.2f,0.8f,1.0f},
				new float[]{0.0f,0.5f,1.0f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		humo.setColor(0.8f,0.7f,0.6f, iAlfa);
		
		texture_particle = Tools.loadTexture("resources/obj3d/texturas/smoke_particle.jpg","ALPHA");
		texture_particle.setBoundaryModeS(Texture.CLAMP);
		texture_particle.setBoundaryModeT(Texture.CLAMP);
		humo.setTexture(texture_particle);
		humo.setColorModulado(true);
		Tools.setBlendMode(humo, Tools.MODO_MODULAR,0.0f);
		
		humo.iniciar();
		objRoot.addChild(humo);
		gestor.add(humo.getModificador());
		
 	   	PointLight pointLight;
 	   	
 	    pointLight = new PointLight();
	   	pointLight.setEnable(true);
	   	pointLight.setColor(new Color3f(0.6f,0.6f,0.6f));
	   	pointLight.setAttenuation(0,0.15f,0);
	   	pointLight.setInfluencingBounds(bounds);
 	   	tg2.addChild(pointLight);
		
		tLocal = new Transform3D();
		//tLocal.rotZ(90*Math.PI/180);
		tLocal.setTranslation(new Vector3d(5.0,0.0,0.0));
	
		explosion = explosion.clone();
		explosion.setModoDeEmision(Particulas.EMISION_PULSOS);
		explosion.setTExterna(tExterna);
		explosion.setTLocal(tLocal);
		explosion.iniciar();
		objRoot.addChild(explosion);
		gestor.add(explosion.getModificador());
		
		humo = humo.clone();
		humo.setModoDeEmision(Particulas.EMISION_PULSOS);
		humo.setTiempoVida(2.5f);
		humo.setGiro(90.0f,30.0f,true);
		humo.setTExterna(tExterna);
		humo.setTLocal(tLocal);
		humo.iniciar();
		objRoot.addChild(humo);
		gestor.add(humo.getModificador());
		

		pointLight = new PointLight();
	   	pointLight.setEnable(true);
	   	pointLight.setColor(new Color3f(0.6f,0.6f,0.6f));
	   	pointLight.setAttenuation(0,0.15f,0);
	   	pointLight.setInfluencingBounds(bounds);
		tg2 = new TransformGroup(tLocal);
		tg1.addChild(tg2);
	   	tg2.addChild(pointLight);
 	   	
		/*
		Particulas estrellas1 = FactoriaDeParticulas.createParticulas(100);
		estrellas1.setModoDeEmision(Particulas.EMISION_CONTINUA);
		estrellas1.setRangoDeEmision(1.0f);
		Emisor e = new EmisorLineal(1.2f,0.0f);
		estrellas1.setEmisor(new EmisorCache(e,256));
		estrellas1.setTiempoVida(2.0f);
		estrellas1.setVelocidad(5.0f,0.1f,false);
		//estrellas1.setGiro(360.0f,90.0f,true);
		tLocal = new Transform3D();
		tLocal.rotX(Math.PI/2);
		tLocal.setScale(new Vector3d(-1.0,1.0,1.0));
		//tLocal.setTranslation(new Vector3d(0.8,0.0,0.0));
		estrellas1.setTLocal(tLocal);
		estrellas1.setEscala(0.1f);
		estrellas1.setColor(1.0f,1.0f,1.0f,1.0f);
		texture_particle = Tools.loadTexture("resources/obj3d/texturas/spark.jpg","ALPHA");
		texture_particle.setMinFilter(Texture.FASTEST);
		texture_particle.setMagFilter(Texture.FASTEST);
		texture_particle.setBoundaryModeS(Texture.CLAMP);
		texture_particle.setBoundaryModeT(Texture.CLAMP);
		estrellas1.setTexture(texture_particle);
		Tools.setBlendMode(estrellas1, Tools.MODO_ACLARAR, 0.0f);
		estrellas1.iniciar();
		
		objRoot.addChild(estrellas1);
		gestor.add(estrellas1.getModificador());*/
		
		objRoot.compile();
		return objRoot;
	}
}