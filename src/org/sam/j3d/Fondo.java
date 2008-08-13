package org.sam.j3d;

import javax.media.j3d.*;
import javax.vecmath.Vector3d;

import org.sam.j3d.particulas.*;
import org.sam.util.Modificable;
import org.sam.util.Modificador;

import com.sun.j3d.utils.image.TextureLoader;

public class Fondo implements Modificador, Modificable{

	class ModificadorTexturaCoord implements GeometryUpdater{
		float incremento = 0.05f;
		float steep;
		public void updateData(Geometry geometry){
			float[] texturaCoord = ((GeometryArray)geometry).getTexCoordRefFloat(0);
			float inc = steep*incremento;
			texturaCoord[0] = texturaCoord[6]+= inc;
			texturaCoord[2] = texturaCoord[4]+= inc;
			if(texturaCoord[0]>1.0f){
				texturaCoord[0] = texturaCoord[6]-= 1.0f;
				texturaCoord[2] = texturaCoord[4]-= 1.0f;
			}

		}
	}

	private Shape3D shapes[];
	private QuadArray geometria;
	private ModificadorTexturaCoord  updater;
	private Modificador mEstrellas1;
	private Modificador mEstrellas2;
	private Modificador mEstrellas3;
	
	public Fondo(String filename){
		crearShapes(filename);
	}
	
	private void crearShapes(String pahtTextura){
		
		shapes = new Shape3D[4];
		
		Appearance app = new Appearance();
		Texture textura = new TextureLoader(pahtTextura, "RGB", null).getTexture();
		textura.setMinFilter(Texture.FASTEST);
		textura.setMagFilter(Texture.FASTEST);
		app.setTexture(textura);

		geometria = new QuadArray(4, 
				GeometryArray.COORDINATES | 
				GeometryArray.TEXTURE_COORDINATE_2 |
				GeometryArray.BY_REFERENCE );

		float[] posicionCoord = {
				-0.8f, -0.6f, -1.0f,
				 0.8f, -0.6f, -1.0f,
				 0.8f,  0.6f, -1.0f,
				-0.8f,  0.6f, -1.0f
//				-1.6f, -1.2f, 0.0f,
//				 1.6f, -1.2f, 0.0f,
//				 1.6f,  1.2f, 0.0f,
//				-1.6f,  1.2f, 0.0f
		};
		geometria.setCoordRefFloat(posicionCoord);
		float[] texturaCoord = {
				0.0f, 0.0f,
				0.5f, 0.0f,
				0.5f, 1.0f,
				0.0f, 1.0f
			};
		geometria.setTexCoordRefFloat(0,texturaCoord);
		
		geometria.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
		shapes[0] = new Shape3D(geometria,app);
		
		Particulas estrellas1 = FactoriaDeParticulas.createParticulas(20);
		estrellas1.setModoDeEmision(Particulas.EMISION_CONTINUA);
		estrellas1.setRangoDeEmision(1.0f);
		
		Transform3D tEmisor = new Transform3D();
		tEmisor.rotX(Math.PI/2);
		tEmisor.setScale(new Vector3d(-1.0,1.0,1.0));
		tEmisor.setTranslation(new Vector3d(0.8,0.0,-0.999999999999));
		
		estrellas1.setEmisor(new EmisorCache(new EmisorLineal(1.2f,0.0f),tEmisor,256));
		estrellas1.setTiempoVida(2.0f);
		estrellas1.setVelocidad(0.7f,0.1f,false);

		estrellas1.setEscala(0.01f);
		estrellas1.setColor(1.0f,1.0f,1.0f,1.0f);
		Texture texture_particle = Tools.loadTexture("resources/obj3d/texturas/spark.jpg","ALPHA");
		texture_particle.setMinFilter(Texture.FASTEST);
		texture_particle.setMagFilter(Texture.FASTEST);
		texture_particle.setBoundaryModeS(Texture.CLAMP);
		texture_particle.setBoundaryModeT(Texture.CLAMP);
		estrellas1.setTexture(texture_particle);
		Tools.setBlendMode(estrellas1, Tools.MODO_ACLARAR, 0.5f);
		
		Particulas estrellas2 = estrellas1.clone();
		estrellas2.setEscala(0.0075f);
		estrellas2.setTiempoVida(3.0f);
		estrellas2.setVelocidad(0.5f,0.03f,false);

		Particulas estrellas3 = estrellas1.clone();
		estrellas3.setEscala(0.005f);
		estrellas3.setTiempoVida(4.0f);
		estrellas3.setVelocidad(0.38f,0.02f,false);

		estrellas1.iniciar();
		shapes[1] = estrellas1;
		estrellas2.iniciar();
		shapes[2] = estrellas2;
		estrellas3.iniciar();
		shapes[3] = estrellas3;
		
		updater = new ModificadorTexturaCoord();
		mEstrellas1 = estrellas1.getModificador();
		mEstrellas2 = estrellas2.getModificador();
		mEstrellas3 = estrellas3.getModificador();
	}
	
	public  Background getFondo(){
		Background fondo = new Background();
		BranchGroup bg = new BranchGroup();
		for(int i=0, len = shapes.length; i<len; i++)
			bg.addChild(shapes[i]);
		fondo.setGeometry(bg);
		return fondo;
	}
	
	public Shape3D[] getShapes(){
		return shapes;
	}
	
	public boolean modificar(float steep) {
		updater.steep = steep;
		geometria.updateData(updater);
		mEstrellas1.modificar(steep);
		mEstrellas2.modificar(steep);
		mEstrellas3.modificar(steep);
		return true;
	}

	public Modificador getModificador() {
		return this;
	}
}